(ns breadule.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [v-box input-text input-textarea label input-time button]]
   [breadule.subs :as subs]
   [breadule.events :as events]))

(defn stage-view [stage]
  [v-box
   :children [[label :label (stage :name)]
              [label :label (stage :waitTime)]
              [label :label (stage :workTime)]
              [label :label (stage :instructions)]]])

(defn schedule-view [id]
  (let [schedule (re-frame/subscribe [::subs/schedule id])]
    [v-box
     :children [[label :label (@schedule :name)]
                [label :label "Stages"]
                (map stage-view (@schedule :stages))]]))

(defn stage-field-sub [id num name]
  (re-frame/subscribe [::subs/stage-field id num name]))

(defn stage-form [id num]
  (let [fieldSub #(stage-field-sub id num %)
        fieldUpdate #(re-frame/dispatch-sync [::events/update-stage id num %1 %2])
        name (fieldSub :name)
        waitTime (fieldSub :waitTime)
        workTime (fieldSub :workTime)
        instructions (fieldSub :instructions)]
    [v-box
     :children [[label :label "Name"]
                [input-text
                 :model name
                 :change-on-blur? false
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
                 :on-change #(fieldUpdate :instructions %)]
                [button
                 :label "Remove"
                 :on-click #(re-frame/dispatch [::events/remove-stage id num])]]]))

(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])]
    [:div
     [:h1 @name]
     (schedule-view :testId)
     (stage-form :testId 0)]))
