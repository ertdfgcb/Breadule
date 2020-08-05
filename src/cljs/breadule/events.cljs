(ns breadule.events
  (:require
   [re-frame.core :as re-frame]
   [breadule.db]
   [breadule.util :as util]
   [ajax.core :as ajax]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   breadule.db/default-db))

(re-frame/reg-event-fx
 ::post-schedule
 (fn [_ [_ schedule]]
   {:http-xhrio {:method          :post
                 :uri             "/api/schedules"
                 :url-params      schedule
                 :timeout         5000
                 :format          (ajax/json-request-format)
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success      [::success-post-result]
                 :on-failure      [::failure-post-result]}}))

(re-frame/reg-event-db
 ::success-post-result
 (fn [db res]
   (print "posted schedules" res)
   db))

(re-frame/reg-event-db
 ::failure-post-result
 (fn [db err]
   (print "failed to post schedules:" err)
   db))

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
 ::update-db
 (fn [db [_ field value]]
   (assoc db field value)))