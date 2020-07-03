(ns movies-cup-api.controllers
  (:require [movies-cup-api.dbs.movies :as dbs.movies]
            [movies-cup-api.dbs.cups :as dbs.cups]
            [movies-cup-api.logic :as logic]))


(defn get-movies [storage] (dbs.movies/all-movies storage))


(defn create-cup
  [participating-movies 
   storage]
  (let [all-movies      (dbs.movies/all-movies storage)
        filtered-movies (logic/filtered-movies all-movies participating-movies)
        cup             (logic/movies-cup filtered-movies)]
    (dbs.cups/add-cup! cup storage)
    cup))


(defn get-cups [storage] (dbs.cups/all-cups storage))


(defn get-cup
  [id
   storage]
  (if-let [cup (dbs.cups/cup id storage)]
    cup
    (throw (ex-info (format "Cup with id %s was not found" id) {:received id}))))
