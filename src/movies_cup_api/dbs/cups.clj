(ns movies-cup-api.dbs.cups)


(def cups (atom {}))


(defn add-cup! [cup] (swap! cups assoc (:id cup) cup))


(defn all-cups [] (vals @cups))


(defn cup [id] (get @cups id))
