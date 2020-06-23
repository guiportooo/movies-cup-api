(ns movies-cup-api.service
  (:require [io.pedestal.http :as http]
            [io.pedestal.http.route :as route]
            [io.pedestal.http.body-params :as body-params]
            [io.pedestal.interceptor.error :as error]
            [ring.util.response :as ring-resp]
            [movies-cup-api.logic :as logic]
            [movies-cup-api.dbs.movies :as dbs.movies]
            [movies-cup-api.dbs.cups :as dbs.cups]
            [movies-cup-api.adapters :as adapters]))


(defn home-page
  [request]
  (ring-resp/response "Movies Cup Api"))


(defn get-movies
  [request]
  (let [movies    (dbs.movies/all-movies)
        movie-models (adapters/Movies->MovieModels movies)]
    (ring-resp/response movie-models)))


(defn create-cup
  [request]
  (let [ids                  (:json-params request)
        participating-movies (adapters/ParticipatingMovies ids)
        movies               (logic/filtered-movies (dbs.movies/all-movies) participating-movies)
        cup                  (logic/movies-cup movies)
        cup-model            (adapters/CupResult->CupModel cup)
        url                  (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (dbs.cups/add-cup! cup)
    (ring-resp/created url cup-model)))


(defn get-cups
  [request]
  (let [cups       (dbs.cups/all-cups)
        cup-models (adapters/CupResults->CupModels cups)]
    (ring-resp/response cup-models)))


(defn get-cup
  [request]
  (let [{{id :cup-id} :path-params} request
        cup                         (dbs.cups/cup id)]
    (if cup
      (ring-resp/response (adapters/CupResult->CupModel cup))
      (ring-resp/not-found 
       (adapters/message->ErrorMessage (format "Cup with id %s was not found" id))))))


(def error-interceptor
  (error/error-dispatch
   [ctx ex]
   [{:exception-type :clojure.lang.ExceptionInfo}]
   (assoc ctx :response (ring-resp/bad-request 
                         (adapters/message->ErrorMessage (:cause (Throwable->map ex)))))

   [{:exception-type :java.lang.AssertionError}]
   (assoc ctx :response (ring-resp/bad-request 
                         (adapters/message->ErrorMessage (:cause (Throwable->map ex)))))

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
