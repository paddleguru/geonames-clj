(defproject paddleguru/geonames "0.7.0"
  :description "Clojure API for GeoNames."
  :url "https://github.com/paddleguru/geonames-clj"
  :min-lein-version "2.0.0"
  :author "Roman Scherer"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :lein-release {:deploy-via :clojars}
  :dependencies [[cheshire "5.2.0"]
                 [http-kit "2.1.13"]
                 [org.clojure/core.async "0.1.267.0-0d7780-alpha"]
                 [org.clojure/clojure "1.5.1"]
                 [prismatic/schema "0.2.1"]])
