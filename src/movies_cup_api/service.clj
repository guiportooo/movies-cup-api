(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [movies-cup-api.interceptors :as interceptors]
            [movies-cup-api.controllers :as controllers]
            [movies-cup-api.adapters :as adapters]))


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (ring-resp/response (controllers/get-movies)))


(defn create-cup
  [{ids :json-params}]
  (let [participating-movies (adapters/ParticipatingMovies ids)
        cup                  (controllers/create-cup participating-movies)
        url                  (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (ring-resp/created url cup)))


(defn get-cups
  [request]
    (ring-resp/response (controllers/get-cups)))


(defn get-cup
  [{{:keys [cup-id]} :path-params}]
  (ring-resp/response (controllers/get-cup cup-id)))


(def common-interceptors [(body-params/body-params) 
                          http/json-body
                          interceptors/error-interceptor])


(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/movies" :get (conj common-interceptors `get-movies)]
              ["/cups" :post (conj common-interceptors `create-cup)]
              ["/cups" :get (conj common-interceptors `get-cups)]
              ["/cups/:cup-id" :get (conj common-interceptors `get-cup)]})


(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
