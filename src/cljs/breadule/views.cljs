(ns breadule.views
  (:require
   [re-frame.core :as re-frame]
   [re-com.core   :refer [h-box title single-dropdown md-icon-button]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.schedule :refer [schedule-view]]))
   
(defn main-panel []
  (let [name (re-frame/subscribe [::subs/name])
        schedules (re-frame/subscribe [::subs/schedules])
        currentSchedule (re-frame/subscribe [::subs/currentSchedule])
        make-choice (fn [[scheduleId schedule]] {:id scheduleId :label (:name schedule)})]
    [:div
     [title :level :level1 :label @name]
     [h-box
      :children [[single-dropdown
                  :choices (map make-choice (seq @schedules))
                  :model currentSchedule
                  :placeholder "Select schedule"
                  :on-change #(re-frame/dispatch [::events/select-schedule %])]
                 [md-icon-button
                  :size :larger
                  :md-icon-name "zmdi-plus"
                  :class "dropdown-add-button"
                  :on-click #(re-frame/dispatch [::events/add-schedule (gensym)])]]]
     (when (not= @currentSchedule nil)
       (schedule-view @currentSchedule (get @schedules @currentSchedule)))]))
