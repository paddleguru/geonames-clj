(ns geonames.geocoder
  (:require [cheshire.core :refer [parse-string]]
            [clojure.core.async :as a]
            [clojure.string :refer [blank? lower-case join]]
            [org.httpkit.client :as client]
            [schema.core :as s])
  (:import [clojure.core.async.impl.protocols ReadPort]))

;; ## Schemas

(def Channel
  "Friendlier alias for a Clojure channel."
  ReadPort)

(def Endpoint
  "All valid endpoints, as described at http://www.geonames.org/export/ws-overview.html."
  s/Str)

(s/defschema Location
  {:latitude s/Num
   :longitude s/Num})

(def ^:dynamic *base-url* "http://ws.geonames.org")
(def ^:dynamic *key* "demo")

(s/defn request :- Channel
  ([endpoint :- Endpoint]
     (request endpoint {}))
  ([endpoint query-params]
     (let [c (a/chan)]
       (client/get
        (str *base-url* "/" endpoint)
        {:query-params (assoc query-params :username *key*)}
        (fn [{:keys [body]}]
          (a/put! c (parse-string body keyword))
          (a/close! c)))
       c)))

(defmacro with-key
  "Evaluate `body` with *key* bound to `key`."
  [key & body] `(binding [*key* ~key] ~@body))

(s/defn merge-options :-  {:lat s/Num
                           :lng s/Num
                           s/Any s/Any}
  [location :- Location opts]
  (-> (apply hash-map opts)
      (assoc :lat (:latitude location)
             :lng (:longitude location))))

(s/defn find-nearby :- Channel
  [location :- Location & opts]
  (->> (request "findNearbyJSON" (merge-options location opts))
       (a/map< :geonames)))

(s/defn find-nearby-place-name :- Channel
  [location :- Location & opts]
  (->> (request "findNearbyPlaceNameJSON" (merge-options location opts))
       (a/map< :geonames)))

(s/defn timezone :- Channel
  [location :- Location & opts]
  (request "timezoneJSON" (merge-options location opts)))

(defn formatted-address
  "Returns the formatted address of the result."
  [result]
  (let [address (join ", " (remove blank? [(:name result) (:adminName1 result) (:countryName result)]))]
    (if-not (blank? address)
      address)))
