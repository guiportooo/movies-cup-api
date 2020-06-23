(ns movies-cup-api.adapters
  (:require [movies-cup-api.schemas :as schemas]
            [movies-cup-api.viewmodel :as viewmodel]
            [movies-cup-api.logic :as logic]
            [schema.core :as s]))


(s/defn participating-movies :- viewmodel/ParticipatingMovies
  [ids]
  (let [number-of-movies (count ids)]
    (if (logic/valid-number-of-movies? number-of-movies)
      (map str ids)
      (throw (ex-info "Required 8 movies to run cup" {:received-number number-of-movies})))))


(s/defn movie-model->movie-viewmodel :- viewmodel/Movie
  [movie-model :- schemas/Movie]
  (merge {}
         {:id (:id movie-model)
          :title (:title movie-model)
          :year (:year movie-model)
          :rating (:rating movie-model)}))


(s/defn movies-model->movies-viewmodel :- [viewmodel/Movie]
  [movies-model :- [schemas/Movie]]
  (map movie-model->movie-viewmodel movies-model))


(s/defn cup-model->cup-viewmodel :- viewmodel/Cup
  [cup-model :- schemas/CupResult]
  (merge {}
         {:id (:id cup-model)
          :first (:first cup-model)
          :second (:second cup-model)}))


(s/defn cups-model->cups-viewmodel :- [viewmodel/Cup]
  [cups-model :- [schemas/CupResult]]
  (map cup-model->cup-viewmodel cups-model))


(s/defn message->response-error :- viewmodel/ResponseError
  [message :- s/Str]
  {:message message})
