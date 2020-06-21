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


(s/defn cup-model->cup-viewmodel :- viewmodel/Cup
  [cup-model :- model/CupResult]
  (merge {}
         {:id (:id cup-model)
          :first (:first cup-model)
          :second (:second cup-model)}))


(s/defn cups-model->cups-viewmodel :- [viewmodel/Cup]
  [cups-model :- [model/CupResult]]
  (map cup-model->cup-viewmodel cups-model))
