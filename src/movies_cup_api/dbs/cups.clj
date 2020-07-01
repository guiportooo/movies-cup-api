(ns movies-cup-api.dbs.cups
  (:require [movies-cup-api.protocols.storage-client :as storage-client]))


(defn add-cup!
  [cup storage]
  (storage-client/upsert! storage :cups (:id cup) cup))


(defn all-cups 
  [storage] 
  (vals (storage-client/read-all storage :cups)))


(defn cup 
  [id storage]
  (get (storage-client/read-all storage :cups) id))
