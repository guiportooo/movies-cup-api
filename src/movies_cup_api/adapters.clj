(ns movies-cup-api.adapters
  (:require [movies-cup-api.schemas :as schemas]
            [movies-cup-api.models :as models]
            [movies-cup-api.logic :as logic]
            [schema.core :as s]))


(s/defn ParticipatingMovies :- models/ParticipatingMovies
  [ids]
  (let [number-of-movies (count ids)]
    (if (logic/valid-number-of-movies? number-of-movies)
      (map str ids)
      (throw (ex-info "Required 8 movies to run cup" {:received number-of-movies})))))


(s/defn Movie->MovieModel :- models/MovieModel
  [movie :- schemas/Movie]
  (merge {}
         {:id (:id movie)
          :title (:title movie)
          :year (:year movie)
          :rating (:rating movie)}))


(s/defn Movies->MovieModels :- [models/MovieModel]
  [movies :- [schemas/Movie]]
  (map Movie->MovieModel movies))


(s/defn CupResult->CupModel :- models/CupModel
  [cup-result :- schemas/CupResult]
  (merge {}
         {:id (:id cup-result)
          :first (:first cup-result)
          :second (:second cup-result)}))


(s/defn CupResults->CupModels :- [models/CupModel]
  [cup-results :- [schemas/CupResult]]
  (map CupResult->CupModel cup-results))


(s/defn message->ErrorMessage :- models/ErrorMessage
  [message :- s/Str]
  {:message message})
