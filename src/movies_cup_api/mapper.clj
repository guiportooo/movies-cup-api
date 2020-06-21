(ns movies-cup-api.mapper
  (:require [movies-cup-api.model :as model]
            [movies-cup-api.viewmodel :as viewmodel]
            [schema.core :as s]))

(s/defn movie-model->movie-viewmodel :- viewmodel/Movie
  [movie-model :- model/Movie]
  (merge {}
         {:id (:id movie-model)
          :title (:title movie-model)
          :year (:year movie-model)
          :rating (:rating movie-model)}))

(s/defn movies-model->movies-viewmodel :- [viewmodel/Movie]
  [movies-model :- [model/Movie]]
  (map movie-model->movie-viewmodel movies-model))
