(ns movies-cup-api.logic
  (:import (java.util UUID))
  (:require [movies-cup-api.model :as m]
            [schema.core :as s]
            [clojure.string :as str]))


(s/defn phase-one-matches :- [m/Match]
  [movies :- [m/Movie]
   matches :- [m/Match]]
  (if (empty? movies)
    matches
    (recur (rest (butlast movies))
           (conj matches {:left (first movies)
                          :right (last movies)}))))


(s/defn phase-one :- [m/Match]
  [movies :- [m/Movie]]
  (let [by-title (sort-by :title movies)]
    (phase-one-matches by-title [])))


(s/defn match-result :- m/MatchResult
  [match :- m/Match]
  (let [movies [(:left match) (:right match)]
        result (sort-by (juxt (comp - :rating) :title) movies)]
    {:winner (first result)
     :loser (last result)}))


(s/defn phase-two-round-winners :- [m/Movie]
  [matches :- [m/Match]]
  (->> matches
       (map match-result)
       (map :winner)))


(s/defn phase-two-next-round :- [m/Match]
  [movies :- [m/Movie]
   round :- [m/Match]]
  (if (empty? movies)
    round
    (recur (rest (rest movies)) (conj round {:left (first movies)
                                             :right (second movies)}))))


(s/defn phase-two :- m/MatchResult
  [matches :- [m/Match]]
  (let [winners (phase-two-round-winners matches)]
    (if (== (count winners) 2)
      (match-result {:left (first winners)
                     :right (last winners)})
      (recur (phase-two-next-round winners [])))))


(s/defn finals :- m/CupResult
  [id :- s/Str
   last-match :- m/MatchResult]
  {:id id
   :first (:winner last-match)
   :second (:loser last-match)})


(s/defn titleless? :- s/Bool
  [movie :- m/Movie]
  (str/blank? (:title movie)))


(s/defn movies-cup :- m/CupResult
  ([movies :- [m/Movie]]
   (movies-cup (str (UUID/randomUUID)) movies))
  
  ([id :- s/Str
    movies :- [m/Movie]]
   {:pre [(= (count movies) 8)
          (empty? (filter titleless? movies))]}
   (let [phase-one-result (phase-one movies)
         phase-two-result (phase-two phase-one-result)]
     (finals id phase-two-result))))
