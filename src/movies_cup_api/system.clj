(ns movies-cup-api.system
  (:require [com.stuartsierra.component :as component]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [movies-cup-api.components.pedestal :as pedestal]
            [movies-cup-api.service :as service]))


(defn service-map
  [env]
  (let [default-map {:env env
                     ::http/routes #(route/expand-routes (deref #'service/routes))
                     ::http/resource-path "/public"
                     ::http/type :jetty
                     ::http/port 8080
                     ::http/container-options {:h2c? true
                                               :h2? false
                                               :ssl? false}}]
    (if (not (pedestal/dev? env))
      default-map
      (merge default-map {::http/join? false
                          ::http/allowed-origins {:creds true :allowed-origins (constantly true)}
                          ::http/secure-headers {:content-security-policy-settings {:object-src "'none'"}}}))))


(defn new-system
  [env]
  (component/system-map
   :service-map (service-map env)
   :pedestal (component/using
              (pedestal/new-pedestal)
              [:service-map])))


(defn start-system! 
  [system env]
  (->> (new-system env)
       component/start
       (reset! system)))


(defn stop-system!
  [system]
  (swap! system #(component/stop %)))
