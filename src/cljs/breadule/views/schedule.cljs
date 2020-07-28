(ns breadule.views.schedule
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [h-box input-text title input-textarea md-icon-button]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.stage :refer [stages-edit-view]]
   [breadule.views.timer :refer [timer-view]]))

(defn labeled [l t]
  [:div [title :level :level3 l] t])

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
        levain (re-frame/subscribe [::subs/schedule-field scheduleId :levain])]
    [:div
     (labeled "Name"
              [h-box
               :children [[input-text
                           :class "form-input"
                           :width "314px"
                           :change-on-blur? false
                           :model name
                           :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :name %])]
                          (schedule-edit-button schedule scheduleId)]])
     [labeled "Levain"
      [input-textarea
       :width "314px"
       :change-on-blur? false
       :model levain
       :on-change #(re-frame/dispatch [::events/update-schedule scheduleId :levain %])]]]))

(defn schedule-view [scheduleId schedule]
  (let [running (re-frame/subscribe [::subs/db-field :running])]
    [:div
     (when @running
       (timer-view scheduleId))
     (if (:editing schedule)
       (schedule-edit-view schedule scheduleId)
       [:div
        [h-box
         :children [[title :level :level2 :class "subtitle" :label (:name schedule)]
                    (schedule-edit-button schedule scheduleId)]]
        [title :level :level3 :label "Levain"]
        (:levain schedule)
        [title :level :level3 :label "Dough"]
        (:dough schedule)])
     (stages-edit-view scheduleId schedule)]))
