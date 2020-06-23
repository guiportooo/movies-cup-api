(ns movies-cup-api.models
  (:require [schema.core :as s]))


(def ParticipatingMovies [s/Str])


(def MovieModel {:id s/Str
            :title s/Str
            :year s/Int
            :rating s/Num})


(def CupModel {:id s/Str
                 :first MovieModel
                 :second MovieModel})


(def ErrorMessage {:message s/Str})
