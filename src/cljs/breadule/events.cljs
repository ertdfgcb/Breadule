(ns breadule.events
  (:require
   [re-frame.core :as re-frame]
   [breadule.db]
   [breadule.util :as util]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   breadule.db/default-db))

(re-frame/reg-event-db
 ::add-stage
 (fn [db [_ scheduleId]]
   (update-in db [:schedules scheduleId :stages] conj breadule.db/new-stage)))

(re-frame/reg-event-db
 ::update-stage
 (fn [db [_ scheduleId stageNum field value]]
   (util/update-stage-field db scheduleId stageNum field value)))

(re-frame/reg-event-db
 ::remove-stage
 (fn [db [_ scheduleId stageNum]]
   (let [drop-idx #(into [] (concat (subvec %1 0 %2) (subvec %1 (inc %2))))]
     (update-in db [:schedules scheduleId :stages] drop-idx stageNum))))

(re-frame/reg-event-db
 ::add-schedule
 (fn [db [_ scheduleId]]
   (let [selected (assoc db :currentSchedule scheduleId)]
   (update selected :schedules #(assoc % scheduleId breadule.db/new-schedule)))))

(re-frame/reg-event-db
 ::update-schedule
 (fn [db [_ scheduleId field value]]
   (assoc-in db [:schedules scheduleId field] value)))

(re-frame/reg-event-db
 ::select-schedule
 (fn [db [_ scheduleId]]
   (assoc db :currentSchedule scheduleId)))

(re-frame/reg-event-db
 ::toggle-running
 (fn [db [_]]
   (update db :running not)))