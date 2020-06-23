(ns movies-cup-api.controllers
  (:require [movies-cup-api.dbs.movies :as dbs.movies]
            [movies-cup-api.dbs.cups :as dbs.cups]
            [movies-cup-api.logic :as logic]
            [movies-cup-api.adapters :as adapters]))


(defn get-movies
  []
  (let [movies (dbs.movies/all-movies)]
    (adapters/Movies->MovieModels movies)))


(defn create-cup
  [participating-movies]
  (let [all-movies      (dbs.movies/all-movies)
        filtered-movies (logic/filtered-movies all-movies participating-movies)
        cup             (logic/movies-cup filtered-movies)]
    (dbs.cups/add-cup! cup)
    (adapters/CupResult->CupModel cup)))


(defn get-cups
  []
  (let [cups (dbs.cups/all-cups)]
    (adapters/CupResults->CupModels cups)))


(defn get-cup
  [id]
  (if-let [cup (dbs.cups/cup id)]
    (adapters/CupResult->CupModel cup)
    (throw (ex-info (format "Cup with id %s was not found" id) {:received id}))))
