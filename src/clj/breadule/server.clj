(ns breadule.server
  (:require [breadule.handler :refer [handler]]
            [config.core :refer [env]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

 (defn -main [& _args]
   (let [port (Integer/parseInt (or (env :port) "1337"))]
     (run-jetty handler {:port port :join? false})))
