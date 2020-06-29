(ns movies-cup-api.server
  (:gen-class) 
  (:require [com.stuartsierra.component :as component]
            [movies-cup-api.system :as system]))


(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (component/start (system/new-system :dev)))


(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (component/start (system/new-system :prod)))
