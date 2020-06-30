(ns movies-cup-api.server
  (:gen-class) 
  (:require [movies-cup-api.system :as system]))


(def system (atom nil))


(defn run-dev
  "The entry-point for 'lein run-dev'"
  [& args]
  (println "\nCreating your [DEV] server...")
  (system/start-system! :dev system))


(defn -main
  "The entry-point for 'lein run'"
  [& args]
  (println "\nCreating your server...")
  (system/start-system! :prod system))
