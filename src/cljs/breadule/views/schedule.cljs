(ns breadule.views.schedule
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [gap v-box h-box input-text title input-textarea button md-icon-button label]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.stage :refer [stages-edit-view]]
   [breadule.views.timer :refer [timer-view start-wait-timer]]))

(defn labeled [l t]
  [:div [label :label l] t])

(defn schedule-edit-button [schedule scheduleId]
  (let [update-event [::events/update-schedule scheduleId :editing (not (:editing schedule))]
        onclick (fn []
                  (re-frame/dispatch update-event)
                  (re-frame/dispatch [::events/post-schedule schedule]))]
  [md-icon-button
   :class (if (:editing schedule) "schedule-save-button" "schedule-edit-button")
   :md-icon-name (if (:editing schedule) "zmdi-check" "zmdi-edit")
   :on-click onclick]))


(defn schedule-edit-view [schedule scheduleId]
  (let [name (re-frame/subscribe [::subs/schedule-field scheduleId :name])
        notes (re-frame/subscribe [::subs/schedule-field scheduleId :notes])]
    [:div
     (labeled "Name"
              [h-box
               :children [[input-text
                           :class "form-input"
                           :change-on-blur? false
                           :model name
                           :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :name %])]
                          (schedule-edit-button schedule scheduleId)]])
     [labeled "Notes"
      [input-textarea
       :change-on-blur? false
       :model notes
       :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :notes %])]]
     ]))

(defn schedule-view [scheduleId schedule]
  (let [running (re-frame/subscribe [::subs/db-field :running])
        stageNum (re-frame/subscribe [::subs/db-field :currentStage])]
    [:div
     (if (:editing schedule)
       (schedule-edit-view schedule scheduleId)
       [:div
        [h-box
         :children [[title :level :level2 :class "subtitle" :label (:name schedule)]
                    (schedule-edit-button schedule scheduleId)]]
        (:notes schedule)])
     (when @running
       (timer-view scheduleId))
     (stages-edit-view scheduleId schedule)
     [button
      :class "btn-lg btn-success start-button"
      :label (if @running "Stop" "Start")
      :on-click (fn []
                  (when @running
                    (re-frame/dispatch-sync [::events/update-db :timer 0]))
                  (re-frame/dispatch-sync [::events/update-db :running (not @running)])
                  (start-wait-timer @stageNum (:stages schedule)))]]))
