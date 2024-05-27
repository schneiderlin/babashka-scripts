(ns open-project
  (:require
   [com.rpl.specter :as sp]
   [bblgum.core :refer [gum]]
   [babashka.fs :as fs]
   [babashka.process :refer [shell]]))

;; it need a database to keep track of what project user has
;; database can just be a edn file, keep things simple
(def db [{:name "babashka" 
          :location "D:/workspace/private/babashka-scripts"}
         {:name "time"
          :location "D:/workspace/private/babashka-scripts/time"}
         {:name "password_gen" 
          :location "D:/workspace/private/babashka-scripts/password_gen"}])

(defn location-by-name [name db]
  (sp/select-one
   [sp/ALL #(= (:name %) name) :location] 
   db))

(defn open-location [location]
  (shell "code" location))

(comment
 (location-by-name db "time")

 (-> "time" 
      (location-by-name db) 
      (open-location))
  
  (open-location "D:/workspace/private/babashka-scripts/time")
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
(-> (choose-one-f project-names)
    #_println
    (location-by-name db)
    (open-location))