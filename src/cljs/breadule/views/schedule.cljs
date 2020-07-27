(ns breadule.views.schedule
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [input-text input-textarea button label]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.stages :refer [stages-edit-view]]))

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

