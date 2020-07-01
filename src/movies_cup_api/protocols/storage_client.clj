(ns movies-cup-api.protocols.storage-client)


(defprotocol StorageClient
  (read-all   [storage entity])
  (upsert!    [storage entity key value])
  (clear-all! [storage entity]))
