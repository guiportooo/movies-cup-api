(ns movies-cup-api.components.pedestal
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [movies-cup-api.interceptors :as interceptors]))


(defrecord Pedestal [service-map service]
  component/Lifecycle
  (start
   [this]
   (if service
     this
     (cond-> service-map
       true                            http/default-interceptors
       true                            (update ::http/interceptors
                                               conj
                                               (interceptors/components-interceptor this))
       (= :dev (:env service-map))     http/dev-interceptors
       true                            http/create-server
       (not= :test (:env service-map)) http/start
       true                            ((partial assoc this :service)))))
  
  (stop
   [this]
   (when (and service (not= :test (:env service-map)))
     (http/stop service))
   (assoc this :service nil)))


(defn new-pedestal
  []
  (map->Pedestal {}))
