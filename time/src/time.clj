(ns time
  (:require 
   [babashka.fs :as fs]
   [babashka.cli :as cli]
            [clojure.edn :as edn]
            [clojure.math :as math])
  (:import 
   java.io.File
   java.time.format.DateTimeFormatter 
   java.time.LocalDate 
   java.time.LocalDateTime 
   java.time.temporal.ChronoUnit))

(def cli-options {:event {:alias :e 
                          :coerce :keyword}
                  :help {:coerce :boolean}
                  :verbose {:alias :v
                            :coerce :boolean
                            :default false}})

(def db-file (File. (str (fs/path (fs/cwd) "log.edn"))))

(when (not (fs/exists? db-file))
  (fs/create-file db-file {:keys (fs/str->posix "rwxrwxrwx")})
  (spit db-file {:log [] :remain {:time 0 :expire "2024-05-23 19:00:00"}}))

(def db (edn/read-string (slurp db-file)))

(def formatter (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(defn date->str [date]
  (.format date formatter))

(defn str->date [s]
  (LocalDateTime/parse s formatter))

(comment
  (date->str (LocalDateTime/now))
  (str->date "2024-05-23 13:06:26")
  :rcf)

(defn replace-db [db]
  (spit db-file db))

(defn last-expire-time
  "上一个 big break 的时间点"
  []
  (let [today (LocalDate/now)
        yesterday (.minusDays today 1)
        mid (.atTime today 12 0 0)
        night (.atTime yesterday 19 0 0)
        now (LocalDateTime/now)]
    (if (.isAfter now mid)
      mid
      night)))

(comment
  (last-expire-time)
  :rcf)

(defn new-expire-time [now]
  (let [today (LocalDate/now)
        mid (.atTime today 12 0 0)
        night (.atTime today 19 0 0)]
    (cond 
      (.isAfter now night) (.atTime today 23 59 59)
      (.isAfter now mid) night
      :else mid)))

(comment
  (new-expire-time (LocalDateTime/now))
  (new-expire-time (.atTime (LocalDate/now) 11 10 0))
  (new-expire-time (.atTime (LocalDate/now) 13 10 0))
  (new-expire-time (.atTime (LocalDate/now) 19 1 0)) 
  :rcf)

(defn get-old-remain-time
  [db]
  (let [now (LocalDateTime/now)
        remain (:remain db)
        expire-time (str->date (:expire remain))]
    (if (.isBefore expire-time now)
      ;; 过期了
      {:expire (date->str (new-expire-time now))
       :time 0}
      ;; 没过期
      remain)))

(comment
  (get-old-remain-time {:remain {:time 5
                                 :expire "2024-05-23 19:00:00"}})
  :rcf)

(defn can-start-rest? [last-item]
  (and (not (nil? last-item))
       (= :work (:event last-item))
       (.isAfter (str->date (:time last-item))
                 (last-expire-time))))

(defn get-remain-time [db]
  (let [logs (:log db)
        old-remain (get-old-remain-time db)
        last-item (last logs)
        now (LocalDateTime/now)]
    (if (can-start-rest? last-item)
      (let [last-start (str->date (:time last-item)) 
            work-time (.until last-start
                              now
                              ChronoUnit/MINUTES)
            break-time (math/ceil (/ work-time 3))]
        (-> old-remain
            (assoc :expire (date->str (new-expire-time now)))
            (update :time #(+ % break-time))))
      (assoc old-remain
             :expire (date->str (new-expire-time now))))))

(comment
  (get-remain-time {:remain {:time   10.0
                             :expire "2024-05-23 19:00:00"}
                    :log    [{:time  "2024-05-23 14:06:01"
                              :event :work}
                             {:time  "2024-05-23 15:17:30"
                              :event :rest}
                             {:time  "2024-05-23 15:28:17"
                              :event :work}]})
  :rcf)

(defn start-resting [db]
  (let [log {:time (date->str (LocalDateTime/now))
             :event :rest}
        remain (get-remain-time db)
        _ (println remain)]
    (if (> (:time remain) 0)
      (-> db
          (update :log #(conj % log)) 
          (assoc :remain remain))
      (throw (RuntimeException. "不能休息")))))

(comment
  ;; 开始休息的时候, 就计算好时间, 然后把时间写到 remain 上
  ;; 开始工作的时候, 就扣减 remain 的时间
  (start-resting {:log [{:time "2024-05-25 20:07:39", :event :work}], :remain {:time 0, :expire "2024-05-23 19:00:00"}})
  :rcf)

(defn can-start-work? [last-item]
  (or (nil? last-item) 
      (= :rest (:event last-item))
      (.isBefore (str->date (:time last-item))
                 (last-expire-time))))

(comment
  (can-start-work? {:event :rest
                    :time "2024-05-23 14:06:01"})
  
  (can-start-work? {:event :work
                    :time "2024-05-23 11:06:01"})
  :rcf)

(defn start-working [db]
  (let [logs (:log db)
        last-item (last logs)
        rest-time (if (= :rest (:event last-item))
                    (let [rest-start-time (str->date (:time last-item))
                          now (LocalDateTime/now)]
                      (.until rest-start-time now ChronoUnit/MINUTES))
                    0)]
    (if (can-start-work? last-item)
      (-> db
          (update :log #(conj % {:time (date->str (LocalDateTime/now))
                                 :event :work}))
          (update-in [:remain :time]
                     (fn [old-time]
                       (max 0 (- old-time rest-time)))))
      db)))

(comment
  (start-working {:remain {:time   56.0
                           :expire "2024-05-23 19:00:00"}
                  :log    [{:time  "2024-05-23 16:39:19"
                            :event :work}
                           {:time  "2024-05-23 17:15:20"
                            :event :rest}
                           {:time  "2024-05-23 17:17:45"
                            :event :work}
                           {:time  "2024-05-23 18:39:28"
                            :event :rest}]})

  (-> db 
      (update-in [:remain :time]
                 (fn [old-time]
                   (println old-time)
                   (max 0 (- old-time 10)))))
  :rcf)

(defn start-working! []
  (replace-db 
   (start-working db)))

(defn start-resting! []
  (replace-db
   (start-resting db)))

(let [args (cli/parse-opts *command-line-args* {:spec cli-options})
      event (:event args)] 
  (when (or (:v args) (:verbose args))
    (println db-file))
  (cond
    (= event :work) (start-working!)
    (= event :rest) (start-resting!)))