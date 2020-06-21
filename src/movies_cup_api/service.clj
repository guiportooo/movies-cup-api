(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.error :as error]
            [ring.util.response :as ring-resp]
            [movies-cup-api.seed :as seed]
            [movies-cup-api.logic :as logic]
            [movies-cup-api.db :as db]))


(defn error-body 
  [message]
  {:message message})


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (ring-resp/response seed/movies))


(defn create-cup
  [request]
  (let [movies (:json-params request)
        cup (logic/movies-cup movies)
        url (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (db/add-cup! cup)
    (ring-resp/created url cup)))


(defn get-cups
  [request]
  (ring-resp/response (db/all-cups)))


(defn get-cup
  [request]
  (let [{{id :cup-id} :path-params} request
        cup (db/cup id)]
    (if cup
      (ring-resp/response cup)
      (ring-resp/not-found (error-body (format "Cup with id %s was not found" id))))))


(def error-interceptor
  (error/error-dispatch
   [ctx ex]
   [{:exception-type :java.lang.AssertionError}]
   (assoc ctx :response (ring-resp/bad-request (error-body (:cause (Throwable->map ex)))))

   :else
   (assoc ctx :io.pedestal.interceptor.chain/error ex)))


(def common-interceptors [(body-params/body-params) 
                          http/json-body
                          error-interceptor])


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
