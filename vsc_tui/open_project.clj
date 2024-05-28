(ns open-project
  (:require
   [clojure.string :as str]
   [clojure.edn :as edn]
   [com.rpl.specter :as sp]
   [bblgum.core :refer [gum]]
   [babashka.fs :as fs]
   [babashka.process :refer [shell]])
  (:import
   [java.io File]))

;; it need a database to keep track of what project user has
;; database can just be a edn file, keep things simple
(def bbin-location 
  (fs/unixify 
   (str/trim
    (:out (shell {:out :string} "bbin bin")))))

(def db-location (str (fs/path bbin-location "vsc_tui.edn")))
(def db-file (File. db-location))

(when (not (fs/exists? db-file))
  (fs/create-file db-file {:keys (fs/str->posix "rwxrwxrwx")})
  (spit db-file [{:name "configuration"
                  :location db-location}]))

;; spec: [{:name "name" :location "location"}]
(def db (edn/read-string (slurp db-file)))

(defn location-by-name [name db]
  (sp/select-one
   [sp/ALL #(= (:name %) name) :location] 
   db))

(defn open-location [location]
  (when (not (str/blank? location))
    (shell "code" location)))

(defn open-db []
  (open-location db-file))

(comment
 (location-by-name db "vscode tui")

 (-> "third time tracker" 
      (location-by-name db) 
      (open-location))
  
  (open-location "C:/Users/zihao/Desktop/workspace/babashka-scripts/logseq") 
  (open-location db-location)
  (open-db)
  :rcf)

(defn choose-one [options]
  (let [result (gum :choose options)]
    (first (:result result))))

(defn choose-one-f 
  "choose with filter"
  [options]
  (let [result (gum :filter :in (clojure.string/join "\n" options))]
    (first (:result result))))

(defn confirm []
  (:result (gum :confirm :as :bool)))

(def project-names (map :name db))

;; show list of projects
;; user select one
;; open that project
(when (= *file* (System/getProperty "babashka.file"))
  (-> (choose-one-f project-names)
       #_println 
      (location-by-name db) 
      (open-location)))