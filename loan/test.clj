(require '[clojure.string :as str])

;; every 2 lines is a block
(defn parse [block]
  (let [[date block] (str/split block #"已还款")
        [total block] (str/split block #"\n本金")
        [principal interest] (str/split block #"利息")]
    {:date date
     :total total
     :principal principal
     :interest interest}))

(defn split-every-2-lines [s]
  (->> s
       clojure.string/split-lines
       (partition 2 2 '())
       (map #(clojure.string/join "\n" %))))

(def blocks (split-every-2-lines input))

(defn block->journal [block]
  (str (:date block) " mortgage payment\n"
       "    expenses:interest  $" (:interest block) "\n"
       "    liabilities:mortgate  $" (:principal block) "\n"
       "    assets:pass unknown"))

(comment 
  
  (-> (parse block)
      (block->journal)
      println)

  (->> (split-every-2-lines input)
       (map parse)
       (map block->journal)
       (map println)) 
  
  (count (str/split-lines input))
  :rcf)
