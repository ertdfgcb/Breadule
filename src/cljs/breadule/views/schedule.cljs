(ns breadule.views.schedule
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [input-text input-textarea button label v-box p]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.stage :refer [stages-edit-view]]
   [breadule.views.timer :refer [timer-view start-wait-timer]]))

(defn labeled [l t]
  [:div [label :label l] t])

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

(defn schedule-edit-button [schedule scheduleId]
  (let [update-event [::events/update-schedule scheduleId :editing (not (:editing schedule))]
        onclick (fn []
                  (re-frame/dispatch update-event)
                  (re-frame/dispatch [::events/post-schedule schedule]))]
  [button
   :label (if (:editing schedule) "Finish" "Edit")
   :on-click onclick]))

(defn schedule-view [scheduleId schedule]
  (let [running (re-frame/subscribe [::subs/db-field :running])
        stageNum (re-frame/subscribe [::subs/db-field :currentStage])]
    [:div
     (if (:editing schedule)
       (schedule-edit-view scheduleId)
       [:div
        [:h3 (:name schedule)]
        [:p (:notes schedule)]])
     (schedule-edit-button schedule scheduleId)
     [:h4 "Stages"]
     (when @running
       (timer-view scheduleId))
     (stages-edit-view scheduleId schedule)
     [button
      :label (if @running "Stop" "Start")
      :on-click (fn []
                  (when @running
                    (re-frame/dispatch-sync [::events/update-db :timer 0]))
                  (re-frame/dispatch-sync [::events/update-db :running (not @running)])
                  (start-wait-timer @stageNum (:stages schedule)))]]))
