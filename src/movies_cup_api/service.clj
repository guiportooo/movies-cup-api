(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.body-params :as body-params]
            [ring.util.response :as ring-resp]
            [movies-cup-api.seed :as s]
            [movies-cup-api.logic :as l]
            [movies-cup-api.db :as db]))


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (http/json-response s/movies))


(defn create-cup
  [request]
  (let [movies (:json-params request)
        cup (l/movies-cup movies)]
    (db/add-cup! cup)
    (http/json-response cup)))


(defn get-cups
  [request]
  (http/json-response (db/all-cups)))


(defn get-cup
  [request]
  (let [{{id :id} :path-params} request]
    (http/json-response (db/cup id))))


(def common-interceptors [(body-params/body-params) http/html-body])


(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/movies" :get (conj common-interceptors `get-movies)]
              ["/cups" :post (conj common-interceptors `create-cup)]
              ["/cups" :get (conj common-interceptors `get-cups)]
              ["/cups/:id" :get (conj common-interceptors `get-cup)]})


(def service {:env :prod
              ::http/routes routes
              ::http/resource-path "/public"
              ::http/type :jetty
              ::http/port 8080
              ::http/container-options {:h2c? true
                                        :h2? false
                                        :ssl? false}})
