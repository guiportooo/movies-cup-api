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
  [{{:keys [storage]} :components}]
  (-> (controllers/get-movies storage)
      adapters/Movies->MovieModels
      ring-resp/response))


(defn create-cup
  [{ids :json-params
    {:keys [storage]} :components}]
  (let [participating-movies (adapters/ParticipatingMovies ids)
        cup                  (controllers/create-cup participating-movies storage)
        url                  (route/url-for ::get-cup :params {:cup-id (:id cup)})]
    (ring-resp/created url (adapters/CupResult->CupModel cup))))


(defn get-cups
  [{{:keys [storage]} :components}]
  (-> (controllers/get-cups storage)
      adapters/CupResults->CupModels
      ring-resp/response))


(defn get-cup
  [{{:keys [cup-id]} :path-params
    {:keys [storage]} :components}]
  (-> (controllers/get-cup cup-id storage)
      adapters/CupResult->CupModel
      ring-resp/response))


(def common-interceptors [(body-params/body-params) 
                          http/json-body
                          interceptors/error-interceptor])


(def routes #{["/" :get (conj common-interceptors `home-page)]
              ["/movies" :get (conj common-interceptors `get-movies)]
              ["/cups" :post (conj common-interceptors `create-cup)]
              ["/cups" :get (conj common-interceptors `get-cups)]
              ["/cups/:cup-id" :get (conj common-interceptors `get-cup)]})
