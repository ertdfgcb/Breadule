(ns breadule.views.schedule
  (:require
   [goog.string :as gstring]
   [goog.string.format]
   [re-frame.core :as re-frame]
   [re-com.core   :refer [input-text input-textarea button label v-box p]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [breadule.views.stage :refer [stages-edit-view]]))

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

(defn format-time [time]
  (let [hours (Math/floor (/ time 3600))
        minutes (mod (Math/floor  (/ time 60)) 60)
        seconds (mod time 60)]
    (gstring/format "%02d:%02d:%02d" hours minutes seconds)))

(defn time-left [time timer]
  (- (* time 60) timer))

(defn schedule-run-view [scheduleId]
  (let [currentStage (re-frame/subscribe [::subs/db-field :currentStage])
        phase (re-frame/subscribe [::subs/db-field :phase])
        stageField #(re-frame/subscribe [::subs/stage-field scheduleId @currentStage %])
        paused (re-frame/subscribe [::subs/db-field :paused])
        name (stageField :name)
        instructions (stageField :instructions)
        timer (re-frame/subscribe [::subs/db-field :timer])]
  [v-box
   :children [[p @name]
              [p @instructions]
              [p @phase]
              (format-time @timer)
              [button
               :label (if @paused "Start" "Pause")
               :on-click #(re-frame/dispatch-sync [::events/update-db :paused (not @paused)])]]]))

(defn dec-timer
  "Decrement the global timer then do it again in a second, or callback if it's 0"
  [callback]
  (let [timer (re-frame/subscribe [::subs/db-field :timer])
        running (re-frame/subscribe [::subs/db-field :running])
        paused (re-frame/subscribe [::subs/db-field :paused])
        timer-update [::events/update-db :timer (- @timer 1)]]
    (if (= @timer 0)
      (callback)
      (when @running
        (when (not @paused)
          (re-frame/dispatch timer-update))
        (js/setTimeout #(dec-timer callback) 1000)))))

; These two functions (start-wait/work) are corecursive (I think)
; so wait will (eventually) call work, generating a stream of function calls
; this ends when the stageNum is the end of the stages vector in work-fn
; they don't call each other directly, there's a chain of inc-timers in between
(declare start-work-timer)
(defn start-wait-timer
  "Returns a function that will start the wait timer"
  [stageNum stages]
  (when (< stageNum (count stages))
    (let [timer (get (nth stages stageNum) :waitTime)
          callback #(start-work-timer stageNum stages)]
        (re-frame/dispatch-sync [::events/update-db :phase "Waiting"])
        (re-frame/dispatch-sync [::events/update-db :timer (* timer 60)])
        (dec-timer callback))))

(defn start-work-timer
  "Returns a function that will start the work timer"
  [stageNum stages]
  (let [timer (get (nth stages stageNum) :workTime)
        callback #(start-wait-timer (+ stageNum 1) stages)]
      (re-frame/dispatch-sync [::events/update-db :phase "Working"])
      (re-frame/dispatch-sync [::events/update-db :timer (* timer 60)])
      (dec-timer callback)))

(defn schedule-view [scheduleId schedule]
  (let [running (re-frame/subscribe [::subs/db-field :running])
        stageNum (re-frame/subscribe [::subs/db-field :currentStage])]
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
     (when @running
       (schedule-run-view scheduleId))
     (stages-edit-view scheduleId schedule)
     [button
      :label (if @running "Stop" "Start")
      :on-click (fn []
                  (when @running
                    (re-frame/dispatch-sync [::events/update-db :timer 0]))
                  (re-frame/dispatch-sync [::events/update-db :running (not @running)])
                  (start-wait-timer @stageNum (:stages schedule)))]]))
