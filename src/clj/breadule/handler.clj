(ns breadule.handler
  (:require
   [compojure.core :refer [GET POST defroutes]]
   [compojure.route :refer [resources]]
   [ring.util.response :refer [resource-response]]
   [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
   [ring.middleware.reload :refer [wrap-reload]]
   [ring.middleware.json :refer [json-response]]
   [shadow.http.push-state :as push-state]
   [breadule.mongo :refer [insert-schedule get-all-schedules]]))

(defroutes routes
  (GET "/" [] (resource-response "index.html" {:root "public"}))
  (GET "/api/schedules" [] (json-response (get-all-schedules) {:pretty false}))
  (POST "/api/schedules" req (let [res (:params req)]
                               (insert-schedule res)))
  (resources "/"))

(def dev-handler (-> #'routes wrap-reload push-state/handle))

(def handler (wrap-defaults routes (assoc site-defaults :security false)))
