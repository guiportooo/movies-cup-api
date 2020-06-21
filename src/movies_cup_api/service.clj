(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.error :as error]
            [ring.util.response :as ring-resp]
            [movies-cup-api.seed :as s]
            [movies-cup-api.logic :as l]
            [movies-cup-api.db :as db]))


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (ring-resp/response s/movies))


(defn create-cup
  [request]
  (let [movies (:json-params request)
        cup (l/movies-cup movies)
        url (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (db/add-cup! cup)
    (ring-resp/created url cup)))


(defn get-cups
  [request]
  (ring-resp/response (db/all-cups)))


(defn get-cup
  [request]
  (let [{{id :cup-id} :path-params} request]
    (ring-resp/response (db/cup id))))


(def error-interceptor
  (error/error-dispatch
   [ctx ex]
   [{:exception-type :java.lang.AssertionError}]
   (assoc ctx :response (ring-resp/bad-request (:cause (Throwable->map ex))))

   :else
   (assoc ctx :io.pedestal.interceptor.chain/error ex)))


(def common-interceptors [error-interceptor (body-params/body-params) http/json-body])


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
