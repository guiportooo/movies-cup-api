(ns movies-cup-api.components.storage
  (:require [com.stuartsierra.component :as component]
            [movies-cup-api.protocols.storage-client :as storage-client]
            [movies-cup-api.dbs.movies :as movies]))


(defrecord InMemoryStorage [storage]
  component/Lifecycle
  (start [this] 
         (swap! storage assoc :movies (movies/seed))
         this)
  (stop [this]
        (reset! storage {})
        this)
  
  storage-client/StorageClient
  (read-all   
   [this entity] 
   (get @storage entity))

  (upsert!       
   [this entity key value] 
   (swap! storage assoc-in [entity key] value))

  (clear-all! 
   [this entity] 
   (swap! storage assoc entity {})))


(defn new-in-memory []
  (->InMemoryStorage (atom {})))
