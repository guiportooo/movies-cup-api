(ns movies-cup-api.logic-test
  (:import (java.util UUID))
  (:require [movies-cup-api.logic :as logic]
            [movies-cup-api.model :as model]
            [clojure.test :refer :all]
            [schema.core :as s]
            [schema-generators.generators :as g]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]))


(s/set-fn-validation! true)


(def movie1 {:id "1"
             :title "Title 1"
             :year 2012
             :rating 7.8})

(def movie2 {:id "2"
             :title "Title 2"
             :year 2019
             :rating 7.8})

(def movie3 {:id "3"
             :title "Title 3"
             :year 2010
             :rating 8.3})

(def movie4 {:id "4"
             :title "Title 4"
             :year 2004
             :rating 6.4})

(def movie5 {:id "5"
             :title "Title 5"
             :year 2005
             :rating 7.2})

(def movie6 {:id "6"
             :title "Title 6"
             :year 2016
             :rating 9.5})

(def movie7 {:id "7"
             :title "Title 7"
             :year 2020
             :rating 4.0})

(def movie8 {:id "8"
             :title "Title 8"
             :year 2001
             :rating 7.8})

(def titleless-movie {:id "9"
                      :title ""
                      :year 2012
                      :rating 8.0})


(def sorted-movies [movie1 movie2 movie3 movie4 movie5 movie6 movie7 movie8])
(def reversed-movies [movie8 movie7 movie6 movie5 movie4 movie3 movie2 movie1])
(def unsorted-movies [movie7 movie2 movie3 movie6 movie8 movie1 movie5 movie4])
(def movies-with-titleless [titleless-movie movie2 movie3 movie6 movie8 movie1 movie5 movie4])


(defn not-empty-str [string] (str string "s"))


(def movie-generator
  (g/generator model/Movie {s/Str (gen/fmap not-empty-str gen/string-alphanumeric)
                        s/Int gen/pos-int
                        s/Num (gen/double* {:min 0})}))


(defn movies-generator
  [num-elements]
  (gen/vector movie-generator num-elements))


(deftest movies-cup-test
  (testing "Throws error when movies count is different than 8"
    (are [movies] (thrown? AssertionError (logic/movies-cup movies))
      (gen/generate (movies-generator 7))
      (gen/generate (movies-generator 9))))


  (testing "Throws error when there is at least one titleless movie"
    (is (thrown? AssertionError
                 (logic/movies-cup movies-with-titleless))))


  (testing "Returns first and second placed movies"
(let [id (str (UUID/randomUUID))]
  (are [result movies] (= result (logic/movies-cup id movies))
    {:id id :first movie6 :second movie1} sorted-movies
    {:id id :first movie6 :second movie1} reversed-movies
    {:id id :first movie6 :second movie1} unsorted-movies))))


(defspec first-place-is-always-highest-rating 100
  (prop/for-all
   [movies (movies-generator 8)]
   (let [highest-rating (->> movies
                             (map :rating)
                             sort
                             last)]
     (= highest-rating (get-in (logic/movies-cup movies) [:first :rating])))))


(deftest phase-one-test
  (testing "Sorts by title and returns 4 matches between first x last, second x last but one..."
    (let [expected-matches [{:left movie1 :right movie8}
                            {:left movie2 :right movie7}
                            {:left movie3 :right movie6}
                            {:left movie4 :right movie5}]]
      (are [matches movies] (= matches (logic/phase-one movies))
        expected-matches sorted-movies
        expected-matches reversed-movies
        expected-matches unsorted-movies))))


(deftest match-result-test
  (testing "Returns winner as the best rated movie and loser as the worst rated one"
    (are [result match] (= result (logic/match-result match))
      {:winner movie3 :loser movie1} {:left movie1 :right movie3}
      {:winner movie4 :loser movie7} {:left movie4 :right movie7}
      {:winner movie6 :loser movie8} {:left movie8 :right movie6}))


  (testing "Applies alphabetical order as tiebreaker"
    (are [result match] (= result (logic/match-result match))
      {:winner movie1 :loser movie2} {:left movie1 :right movie2}
      {:winner movie2 :loser movie8} {:left movie8 :right movie2})))


(def phase-two-matches1 [{:left movie1 :right movie8}
                         {:left movie2 :right movie7}
                         {:left movie3 :right movie6}
                         {:left movie4 :right movie5}])

(def phase-two-matches2 [{:left movie1 :right movie2}
                         {:left movie3 :right movie4}
                         {:left movie5 :right movie6}
                         {:left movie7 :right movie8}])

(def phase-two-matches3 [{:left movie8 :right movie3}
                         {:left movie6 :right movie1}
                         {:left movie5 :right movie2}
                         {:left movie7 :right movie4}])


(deftest phase-two-test
  (testing "Runs rounds between first x second, third x fourth, etc. and returns final result"
    (are [result initial-matches] (= result (logic/phase-two initial-matches))
      {:winner movie6 :loser movie1} phase-two-matches1
      {:winner movie6 :loser movie3} phase-two-matches2
      {:winner movie6 :loser movie2} phase-two-matches3)))


(deftest filtered-movies-test
  (testing "Returns empty coll when no movies with the same ids exists"
    (is (= '() (logic/filtered-movies sorted-movies ["abc"]))))

  (testing "Returns only the movies with the same ids as the informed ones"
    (are [result movies ids] (= result (logic/filtered-movies movies ids))
      [movie1] sorted-movies ["1"]
      [movie4 movie6] sorted-movies ["6" "4"]
      [movie7 movie3 movie1] reversed-movies ["3" "1" "7"])))
