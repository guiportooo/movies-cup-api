(ns movies-cup-api.dbs.movies
  (:require [movies-cup-api.protocols.storage-client :as storage-client]))


(defn seed [] 
  [{:id "1"
    :title "Title1"
    :year 2018
    :rating 7.8}

   {:id "2"
    :title "Title2"
    :year 2019
    :rating 8.2}

   {:id "3"
    :title "Title3"
    :year 2018
    :rating 8.2}

   {:id "4"
    :title "Title4"
    :year 2017
    :rating 7.8}

   {:id "5"
    :title "Title5"
    :year 2019
    :rating 9.5}

   {:id "6"
    :title "Title6"
    :year 2020
    :rating 9.7}

   {:id "7"
    :title "Title7"
    :year 2017
    :rating 8.8}

   {:id "8"
    :title "Title8"
    :year 2020
    :rating 8.9}

   {:id "9"
    :title "Title9"
    :year 2018
    :rating 8.8}

   {:id "10"
    :title "Title10"
    :year 2019
    :rating 9.3}

   {:id "11"
    :title "Title11"
    :year 2018
    :rating 8.2}

   {:id "12"
    :title "Title14"
    :year 2017
    :rating 3.8}

   {:id "13"
    :title "Title13"
    :year 2019
    :rating 9.5}

   {:id "14"
    :title "Title14"
    :year 2020
    :rating 9.6}

   {:id "15"
    :title "Title15"
    :year 2017
    :rating 6.8}

   {:id "16"
    :title "Title16"
    :year 2020
    :rating 7.8}])


(defn all-movies [storage]
  (storage-client/read-all storage :movies))
