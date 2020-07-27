(ns breadule.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [single-dropdown input-text input-textarea label input-time button]]
   [breadule.subs :as subs]
   [breadule.events :as events]))

(defn stage-field-sub [id num name]
  (re-frame/subscribe [::subs/stage-field id num name]))

(defn labeled [l t]
  [:div [label :label l] t])

(defn stage-form [scheduleId num]
  (let [fieldSub #(stage-field-sub scheduleId num %)
        fieldUpdate #(re-frame/dispatch [::events/update-stage scheduleId num %1 %2])
        name (fieldSub :name)
        waitTime (fieldSub :waitTime)
        workTime (fieldSub :workTime)
        instructions (fieldSub :instructions)]
    [:div
     (labeled "Name"
              [input-text
               :change-on-blur? false
               :model name
               :on-change #(fieldUpdate :name %)])
     (labeled "Wait Time"
              [input-time
               :model waitTime
               :on-change #(fieldUpdate :waitTime %)])
     (labeled "Work Time"
              [input-time
               :model workTime
               :on-change #(fieldUpdate :workTime %)])
     (labeled "Instructions"
              [input-textarea
               :model instructions
               :change-on-blur? false
               :on-change #(fieldUpdate :instructions %)])]))

(defn stage-view [scheduleId num stage]
  (let [editing (stage-field-sub scheduleId num :editing)
        edit-toggle #(re-frame/dispatch
                      [::events/update-stage scheduleId num :editing (not @editing)])
        row (fn [l t] [:tr [:td l] [:td t]])]
    ^{:key (gensym)}
    [:div
     (if @editing
       (stage-form scheduleId num)
       [:table
        [:tbody
         (row "Name" (:name stage))
         (row "Wait Time" (:waitTime stage))
         (row "Work Time" (:workTime stage))
         (row "Instructions" (:instructions stage))]])
     [:div
      [button
       :label (if @editing "Finish" "Edit")
       :on-click edit-toggle]
      [button
       :label "Remove"
       :on-click #(re-frame/dispatch [::events/remove-stage scheduleId num])]]]))

(defn stages-edit-view [scheduleId schedule]
  [:div
   (doall (map-indexed #(stage-view scheduleId %1 %2) (:stages schedule)))
   [button
    :label "Add"
    :on-click #(re-frame/dispatch
                [::events/add-stage scheduleId])]])

(defn schedule-edit-view [scheduleId]
  (let [name (re-frame/subscribe [::subs/schedule-field scheduleId :name])
        notes (re-frame/subscribe [::subs/schedule-field scheduleId :notes])]
    [:div
     [labeled "Name"
      [input-text
       :change-on-blur? false
       :model name
       :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :name %])]]
     [labeled "Notes"
      [input-textarea
       :change-on-blur? false
       :model notes
       :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :notes %])]]]))

(defn schedule-view [scheduleId schedule]
  (let [running (re-frame/subscribe [::subs/running])]
    [:div
     (if (:editing schedule)
       (schedule-edit-view scheduleId)
       [:div [:h3 (:name schedule)]
        [:p (:notes schedule)]])
     [button
      :label (if (:editing schedule) "Finish" "Edit")
      :on-click #(re-frame/dispatch
                  [::events/update-schedule scheduleId :editing (not (:editing schedule))])]
     [:h4 "Stages"]
     (if @running
       [:h2 "running"]
       (stages-edit-view scheduleId schedule))
     [button
      :label (if @running "Stop" "Start")
      :on-click #(re-frame/dispatch
                  [::events/toggle-running])]]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        schedules (re-frame/subscribe [::subs/schedules])
        currentSchedule (re-frame/subscribe [::subs/currentSchedule])
        make-choice (fn [[scheduleId schedule]] {:id scheduleId :label (:name schedule)})]
    [:div
     [:h1 @name]
     [single-dropdown
      :choices (map make-choice (seq @schedules))
      :model currentSchedule
      :placeholder "Select schedule"
      :on-change #(re-frame/dispatch [::events/select-schedule %])]
     [button
      :label "Add"
      :on-click #(re-frame/dispatch [::events/add-schedule (gensym)])]
     (when (not= @currentSchedule nil)
       (schedule-view @currentSchedule (get @schedules @currentSchedule)))]))
