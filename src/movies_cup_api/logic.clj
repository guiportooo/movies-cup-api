(ns movies-cup-api.logic
  (:import (java.util UUID))
  (:require [movies-cup-api.schemas :as schemas]
            [schema.core :as s]
            [clojure.string :as str]))


(s/defn ^:private phase-one-matches :- [schemas/Match]
  [movies :- [schemas/Movie]
   matches :- [schemas/Match]]
  (if (empty? movies)
    matches
    (recur (rest (butlast movies))
           (conj matches {:left (first movies)
                          :right (last movies)}))))


(s/defn phase-one :- [schemas/Match]
  [movies :- [schemas/Movie]]
  (let [by-title (sort-by :title movies)]
    (phase-one-matches by-title [])))


(s/defn match-result :- schemas/MatchResult
  [match :- schemas/Match]
  (let [movies [(:left match) (:right match)]
        result (sort-by (juxt (comp - :rating) :title) movies)]
    {:winner (first result)
     :loser (last result)}))


(s/defn ^:private phase-two-round-winners :- [schemas/Movie]
  [matches :- [schemas/Match]]
  (->> matches
       (map match-result)
       (map :winner)))


(s/defn ^:private phase-two-next-round :- [schemas/Match]
  [movies :- [schemas/Movie]
   round :- [schemas/Match]]
  (if (empty? movies)
    round
    (recur (rest (rest movies)) (conj round {:left (first movies)
                                             :right (second movies)}))))


(s/defn phase-two :- schemas/MatchResult
  [matches :- [schemas/Match]]
  (let [winners (phase-two-round-winners matches)]
    (if (== (count winners) 2)
      (match-result {:left (first winners)
                     :right (last winners)})
      (recur (phase-two-next-round winners [])))))


(s/defn ^:private finals :- schemas/CupResult
  [id :- s/Str
   last-match :- schemas/MatchResult]
  {:id id
   :first (:winner last-match)
   :second (:loser last-match)})


(s/defn valid-number-of-movies? :- s/Bool
  [number :- s/Int]
  (= number 8))


(s/defn ^:private all-movies-have-title? :- s/Bool
  [movies :- [schemas/Movie]]
  (empty? (filter #(str/blank? (:title %)) movies)))


(s/defn movies-cup :- schemas/CupResult
  ([movies :- [schemas/Movie]]
   (movies-cup (str (UUID/randomUUID)) movies))
  
  ([id :- s/Str
    movies :- [schemas/Movie]]
   {:pre [(valid-number-of-movies? (count movies))
          (all-movies-have-title? movies)]}
   (let [phase-one-result (phase-one movies)
         phase-two-result (phase-two phase-one-result)]
     (finals id phase-two-result))))


(s/defn filtered-movies :- [schemas/Movie]
  [movies :- [schemas/Movie]
   ids :- [s/Str]]
  (filter (fn [movie] (some #(= (:id movie) %) ids)) movies))
