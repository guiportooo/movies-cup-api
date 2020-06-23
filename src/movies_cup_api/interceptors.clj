(ns movies-cup-api.interceptors
  (:require [io.pedestal.interceptor.error :as error]
            [ring.util.response :as ring-resp]
            [movies-cup-api.adapters :as adapters]))


(defn- bad-request
  [ex]
  (ring-resp/bad-request
   (adapters/message->ErrorMessage (:cause (Throwable->map ex)))))


(def error-interceptor
  (error/error-dispatch
   [ctx ex]
   [{:exception-type :clojure.lang.ExceptionInfo}]
   (assoc ctx :response (bad-request ex))

   [{:exception-type :java.lang.AssertionError}]
   (assoc ctx :response (bad-request ex))

   :else
   (assoc ctx :io.pedestal.interceptor.chain/error ex)))
