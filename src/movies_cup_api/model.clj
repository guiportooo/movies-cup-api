(ns movies-cup-api.model
  (:require [schema.core :as s]))


(def Movie {:id s/Str
            :title s/Str
            :year (s/constrained s/Int pos-int?)
            :rating (s/constrained s/Num pos?)})


(def Match {:left Movie
            :right Movie})


(def MatchResult {:winner Movie
                  :loser Movie})


(def CupResult {:id s/Str
                :first Movie
                :second Movie})
