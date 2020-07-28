(ns breadule.views.timer
  (:require
   [goog.string :as gstring]
   [goog.string.format]
   [re-frame.core :as re-frame]
   [re-com.core   :refer [h-box md-circle-icon-button title v-box p]]
   [breadule.subs :as subs]
   [breadule.events :as events]
   [cljs-bach.synthesis :as b]))

(defn format-time [time]
  (let [hours (Math/floor (/ time 3600))
        minutes (mod (Math/floor  (/ time 60)) 60)
        seconds (mod time 60)]
    (gstring/format "%02d:%02d:%02d" hours minutes seconds)))

(defonce audio-context (b/audio-context))

(defn play-alarm [freq]
  (let [ping (b/connect->
              (b/square freq)
              (b/percussive 0.01 0.4)
              (b/gain 0.1))]
    (-> (ping freq)
        (b/connect-> b/destination)
        (b/run-with audio-context (b/current-time audio-context) 1.0))))

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
      (re-frame/dispatch-sync [::events/update-db :currentStage stageNum])
      (dec-timer callback))))

(defn start-work-timer
  "Returns a function that will start the work timer"
  [stageNum stages]
  (js/alert "Wait's over, do some work!")
  (let [timer (get (nth stages stageNum) :workTime)
        callback #(start-wait-timer (+ stageNum 1) stages)]
    (re-frame/dispatch-sync [::events/update-db :phase "Working"])
    (re-frame/dispatch-sync [::events/update-db :timer (* timer 60)])
    (dec-timer callback)))

(defn timer-view [scheduleId]
  (let [currentStage (re-frame/subscribe [::subs/db-field :currentStage])
        phase (re-frame/subscribe [::subs/db-field :phase])
        stageField #(re-frame/subscribe [::subs/stage-field scheduleId @currentStage %])
        paused (re-frame/subscribe [::subs/db-field :paused])
        name (stageField :name)
        instructions (stageField :instructions)
        timer (re-frame/subscribe [::subs/db-field :timer])]
    (print currentStage stageField)
    [v-box
     :children [[title :level :level3 :label (gstring/format "%s:  %s" @name @phase)]
                [h-box
                 :children [[title :level :level1 :margin-top "0.1em" :label (format-time @timer)]
                            [md-circle-icon-button
                             :size :larger
                             :class "timer-pause-button"
                             :md-icon-name (if @paused "zmdi-play" "zmdi-pause")
                             :on-click #(re-frame/dispatch-sync [::events/update-db :paused (not @paused)])]]]
                @instructions]]))

