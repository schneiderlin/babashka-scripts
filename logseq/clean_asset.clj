(ns clean-asset
    (:require 
     [clojure.string :as str]
     [babashka.fs :as fs]))

(def home-pc-location "C:/Users/zihao/Desktop/workspace/zettelkasten")
(def office-pc-location "C:/Users/SHXY-GZ/Desktop/workspace/zettelkasten")
(def root office-pc-location)

(def page-location
  (str root "/pages"))

(def assets-location
  (str root "/assets"))

(defn read-content [pattern]
  (->> (fs/glob page-location pattern)
       (map (fn [path]
              (let [lines (fs/read-all-lines path)
                    contents (apply str lines)]
               contents)))))

(comment
;;   [The book of why the new science of cause and effect.pdf](zotero://select/library/items/JN482YVW)
 (read-content "*cause and effect.md") 
  :rcf)



;; 找到所有 asset 的 name
(def all-asset-names 
  (->> (fs/glob assets-location 
               "*")
       (map fs/file-name)
       (into #{})))

;; 遍历所有的 page, 记录所有遇到过的 assets
(def used-assets 
  (->> (fs/glob page-location "*")
     (map (fn [path]
            (let [lines (fs/read-all-lines path)
                  contents (apply str lines)]
              ;; 如果 asset name 在里面, 把 asset 放到结果集里面
              (into #{}
                    (filter (fn [asset-name] 
                             (str/includes? contents asset-name)) 
                      all-asset-names)))))
     (reduce clojure.set/union)))

(def unused-assets (clojure.set/difference all-asset-names used-assets))

(comment
  (->> unused-assets
       (filter #(str/includes? % "png"))
       (map #(str assets-location %))
       #_(map fs/delete))
  :rcf)
