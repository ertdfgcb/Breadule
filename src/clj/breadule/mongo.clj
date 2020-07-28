(ns breadule.mongo
  (:require [monger.core :as mg]
            [monger.collection :as mc])
  (:import [org.bson.types ObjectId]))

(defn insert-schedule [schedule]
  (let [connection (mg/connect)
        db (mg/get-db connection "breadule")
        obj (if (contains? schedule :_id)
              schedule
              (assoc schedule :_id (ObjectId.)))]
    (mc/insert db "schedules" obj)))

(defn get-all-schedules []
  (let [connection (mg/connect)
        db (mg/get-db connection "breadule")]
    (mc/find-maps db "schedules")))
