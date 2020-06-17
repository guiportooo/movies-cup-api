(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [movies-cup-api.seed :as s]
            [movies-cup-api.logic :as l]))


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (http/json-response s/movies))


(defn create-cup
  [request]
  (let [movies (:json-params request)]
    (println movies)
    (http/json-response (l/movies-cup movies))))


(def common-interceptors [(body-params/body-params) http/html-body])


(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/movies" :get (conj common-interceptors `get-movies)]
              ["/cups" :post (conj common-interceptors `create-cup)]})


(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
