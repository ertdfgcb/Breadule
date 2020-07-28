(ns breadule.views.stage
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [input-text input-textarea label input-time button]]
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
