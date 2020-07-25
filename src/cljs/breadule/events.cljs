(ns breadule.events
  (:require
   [re-frame.core :as re-frame]
   [breadule.db :as db]))

(re-frame/reg-event-db
 ::initialize-db
 (fn [_ _]
   db/default-db))

;TODO: add event for stage updating and event for stage adding