(ns breadule.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [input-text input-textarea label input-time button]]
   [breadule.subs :as subs]
   [breadule.events :as events]))

(defn stage-field-sub [id num name]
  (re-frame/subscribe [::subs/stage-field id num name]))

(defn stage-form [scheduleId num]
  (let [fieldSub #(stage-field-sub scheduleId num %)
        fieldUpdate #(re-frame/dispatch [::events/update-stage scheduleId num %1 %2])
        name (fieldSub :name)
        waitTime (fieldSub :waitTime)
        workTime (fieldSub :workTime)
        instructions (fieldSub :instructions)]
    [:div
     [label :label "Name"]
     [input-text
      :model name
      :on-change #(fieldUpdate :name %)]
     [label :label "Wait Time"]
     [input-time
      :model waitTime
      :on-change #(fieldUpdate :waitTime %)]
     [label :label "Work Time"]
     [input-time
      :model workTime
      :on-change #(fieldUpdate :workTime %)]
     [label :label "Instructions"]
     [input-textarea
      :model instructions
      :change-on-blur? false
      :on-change #(fieldUpdate :instructions %)]]))

(defn stage-view [scheduleId num stage]
  (let [editing (stage-field-sub scheduleId num :editing)
        edit-toggle #(re-frame/dispatch
                      [::events/update-stage scheduleId num :editing (not @editing)])]
    ^{:key (gensym)}
    [:div
     (if  @editing
       (stage-form scheduleId num)
       [:div
        [label :label (stage :name)]
        [label :label (stage :waitTime)]
        [label :label (stage :workTime)]
        [label :label (stage :instructions)]])
     [:div
      [button
       :label (if @editing "Finish" "Edit")
       :on-click edit-toggle]
      [button
       :label "Remove"
       :on-click #(re-frame/dispatch [::events/remove-stage scheduleId num])]]]))


(defn schedule-view [[scheduleId schedule]]
  ^{:key (gensym)}
  [:div
   [:h3 (schedule :name)]
   [:h4 "Stages"]
   (doall (map-indexed #(stage-view scheduleId %1 %2) (:stages schedule)))
   [button
    :label "Add"
    :on-click #(re-frame/dispatch
                [::events/add-stage scheduleId])]])


(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        schedules (re-frame/subscribe [::subs/schedules])]
    [:div
     [:h1 @name]
     (doall (map schedule-view (seq @schedules)))]))
