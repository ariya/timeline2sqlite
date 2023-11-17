(ns timeline2sqlite
  (:require ["fs" :as fs]
            ["sql.js$default" :as sql-js]))

(defn read-file [filename] (-> filename fs/readFileSync str))

(defn parse-json [obj] (-> obj js/JSON.parse (js->clj :keywordize-keys true)))

(defn extract-timeline [content] (-> content parse-json :timelineObjects))

(defn timeline->placevisits [timeline] (->> timeline (filterv :placeVisit) (map :placeVisit)))

(defn normalize-coord [coord] (/ (js/parseInt coord) 1e7))

(defn location->place [location] {:id (:placeId location)
                                  :latitude (normalize-coord (:latitudeE7 location))
                                  :longitude (normalize-coord (:longitudeE7 location))
                                  :name (:name location)
                                  :address (:address location)})

(defn create-db [] (new (.-Database sql-js)))

(defn export-db [fname db] (fs/writeFileSync fname (.from js/Buffer (.export db))))

(defn insert-place-to-db [db place]
  (.run db "INSERT OR IGNORE INTO Places VALUES(?, ?, ?, ?, ?)"
        (clj->js (map place [:id :latitude :longitude :name :address]))))

(defn places->db [places]
  (let [db (create-db)]
    (.run db "CREATE TABLE IF NOT EXISTS Places (Id TEXT UNIQUE, Latitude REAL, Longitude REAL, Name TEXT, Address TEXT)")
    (doseq [place places] (insert-place-to-db db place))
    db))

(defn convert [filename]
  (->> (read-file filename)
       extract-timeline
       timeline->placevisits
       (map :location)
       (map location->place)
       (places->db)
       (export-db "places.sqlite")))

(def cli-args (not-empty (js->clj (.slice js/process.argv 3))))

(defn main [args]
  (if (pos? (count args))
    (convert (first args))
    (js/console.error "Usage: timeline2sqlite somefile.json")))

(main cli-args)