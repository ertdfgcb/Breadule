(ns breadule.util)

(defn get-stage [db scheduleId stageNum]
 (let [schedule (get (:schedules db) scheduleId)]
   (nth (schedule :stages) stageNum)))

(defn update-stage-field [db scheduleId stageNum field value]
  (assoc-in db [:schedules scheduleId :stages stageNum field] value))