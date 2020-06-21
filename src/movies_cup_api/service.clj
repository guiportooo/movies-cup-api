(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.error :as error]
            [ring.util.response :as ring-resp]
            [movies-cup-api.seed :as seed]
            [movies-cup-api.logic :as logic]
            [movies-cup-api.db :as db]
            [movies-cup-api.mapper :as mapper]))


(defn error-body 
  [message]
  {:message message})


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (let [movies    seed/movies
        viewmodel (mapper/movies-model->movies-viewmodel movies)]
    (ring-resp/response viewmodel)))


(defn create-cup
  [request]
  (let [ids       (:json-params request)
        movies    (logic/filtered-movies seed/movies ids)
        cup       (logic/movies-cup movies)
        viewmodel (mapper/cup-model->cup-viewmodel cup)
        url       (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (db/add-cup! cup)
    (ring-resp/created url viewmodel)))


(defn get-cups
  [request]
  (let [cups       (db/all-cups)
        viewmodels (mapper/cups-model->cups-viewmodel cups)]
    (ring-resp/response viewmodels)))


(defn get-cup
  [request]
  (let [{{id :cup-id} :path-params} request
        cup                         (db/cup id)]
    (if cup
      (ring-resp/response (mapper/cup-model->cup-viewmodel cup))
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
