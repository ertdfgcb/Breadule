(ns breadule.events
  (:require
   [re-frame.core :as re-frame]
   [breadule.db]
   [breadule.util :as util]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   breadule.db/default-db))

;TODO: add event for stage updating and event for stage adding
(re-frame/reg-event-db
 ::update-stage
 (fn [db [_ scheduleId stageNum field value]]
   (util/update-stage-field db scheduleId stageNum field value)))

(re-frame/reg-event-db
 ::remove-stage
 (fn [db [_ scheduleId stageNum]]
   (let [drop-idx #(concat (subvec %1 0 %2) (subvec %1 (inc %2)))]
     (print (update-in db [:schedules scheduleId :stages] drop-idx stageNum)))))