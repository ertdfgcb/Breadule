(ns breadule.subs
  (:require
   [re-frame.core :as re-frame]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::schedule
 (fn [db [_ id]]
   (get (:schedules db) id)))

(re-frame/reg-sub
 ::stage-field 
 (fn [db [_ scheduleId stageNum field]]
   (let [schedule (get (:schedules db) scheduleId)
         stage (nth (schedule :stages) stageNum)]
     (get stage field))))