(ns movies-cup-api.service-test
  (:require [clojure.test :refer :all]
            [io.pedestal.test :as test]
            [io.pedestal.http :as http]
            [io.pedestal.http.route :as http.routes]
            [com.stuartsierra.component :as component]
            [cheshire.core :as cheshire]
            [clojure.string :as str]
            [clojure.walk :as walk]
            [movies-cup-api.system :as system]
            [movies-cup-api.service :as service]
            [movies-cup-api.dbs.movies :as dbs.movies]
            [movies-cup-api.dbs.cups :as dbs.cups]))


(defn- parse-body
  [response]
  (cheshire/parse-string (:body response) true))


(defn- serialize-body
  [body]
  (cheshire/generate-string body))


(defn parse-headers
  [response]
  (walk/keywordize-keys (:headers response)))


(def url-for (http.routes/url-for-routes (http.routes/expand-routes service/routes)))


(defn service-fn
  [system]
  (get-in system [:pedestal :service ::http/service-fn]))


(defmacro with-system
  [[bound-var binding-expr] & body]
  `(let [~bound-var (component/start ~binding-expr)]
     (try
       ~@body
       (finally
         (component/stop ~bound-var)))))


(def all-movies (dbs.movies/seed))


(def participating-movies ["1", "2", "3", "4", "5", "6", "7", "8"])


(defn created-cup
  [id]
  {:id id
   :first {:id "6"
           :title "Title6"
           :year 2020
           :rating 9.7}
   :second {:id "8"
            :title "Title8"
            :year 2020
            :rating 8.9}})


(def cup-1 {:id "1"
            :first {:id "6"
                    :title "Title6"
                    :year 2020
                    :rating 9.7}
            :second {:id "8"
                     :title "Title8"
                     :year 2020
                     :rating 8.9}})


(def cup-2 {:id "2"
            :first {:id "1"
                    :title "Title1"
                    :year 2019
                    :rating 8.0}
            :second {:id "2"
                     :title "Title2"
                     :year 2020
                     :rating 9.1}})


(def all-cups [cup-1 cup-2])


(defn create-cups
  [system]
  (let [storage  (get-in system [:storage])]
    (dbs.cups/add-cup! cup-1 storage)
    (dbs.cups/add-cup! cup-2 storage)))


(deftest home-page-test
  (testing "Returns Movies Cup Api"
    (with-system [sut (system/new-system :test)]
      (let [service               (service-fn sut)
            {:keys [status body]} (test/response-for service :get (url-for ::service/home-page))]
        (is (= 200 status))
        (is (= "Movies Cup Api" body))))))


(deftest get-movies-test
  (testing "Returns all sixteen movies"
    (with-system [sut (system/new-system :test)]
      (let [service  (service-fn sut)
            response (test/response-for service :get (url-for ::service/get-movies))
            status   (:status response)
            body     (parse-body response)]
        (is (= 200 status))
        (is (= all-movies body))))))


(deftest create-cup-test
  (testing "Runs cup with participating movies and returns created cup"
    (with-system [sut (system/new-system :test)]
      (let [service  (service-fn sut)
            response (test/response-for service
                                        :post (url-for ::service/create-cup)
                                        :headers {"Content-Type" "application/json"}
                                        :body (serialize-body participating-movies))
            status   (:status response)
            headers  (parse-headers response)
            location (:Location headers)
            cup-id   (last (str/split location #"/"))
            body     (parse-body response)]
        (is (= 201 status))
        (is (= (created-cup cup-id) body))))))


(deftest get-cups-test
  (testing "Returns all cups"
    (with-system [sut (system/new-system :test)]
      (create-cups sut)
      (let [service  (service-fn sut)
            response (test/response-for service :get (url-for ::service/get-cups))
            status   (:status response)
            body     (parse-body response)]
        (is (= 200 status))
        (is (= all-cups body))))))


(deftest get-cup-test
  (testing "Returns cup with id"
    (with-system [sut (system/new-system :test)]
      (create-cups sut)
      (let [service  (service-fn sut)
            response (test/response-for service :get (url-for ::service/get-cup
                                                              :path-params {:cup-id (:id cup-1)}))
            status   (:status response)
            body     (parse-body response)]
        (is (= 200 status))
        (is (= cup-1 body))))))
