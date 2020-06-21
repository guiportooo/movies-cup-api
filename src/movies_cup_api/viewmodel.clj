(ns movies-cup-api.viewmodel
  (:require [schema.core :as s]))


(def Movie {:id s/Str
            :title s/Str
            :year s/Int
            :rating s/Num})

(def Cup {:id s/Str
          :first Movie
          :second Movie})