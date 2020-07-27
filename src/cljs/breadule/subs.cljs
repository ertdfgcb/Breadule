(ns breadule.subs
  (:require
   [re-frame.core :as re-frame]
   [breadule.util :as util]))

(re-frame/reg-sub
 ::name
 (fn [db]
   (:name db)))

(re-frame/reg-sub
 ::schedules
 (fn [db]
   (:schedules db)))

(re-frame/reg-sub
 ::schedule
 (fn [db [_ id]]
   (get (:schedules db) id)))

(re-frame/reg-sub
 ::stage-field 
 (fn [db [_ scheduleId stageNum field]]
   (let [stage (util/get-stage db scheduleId stageNum)]
     (get stage field))))