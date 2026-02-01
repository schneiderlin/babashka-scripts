#!/usr/bin/env bb
;; Install: bbin install nrepl_port.clj --as nrepl-port
;; Run from a project dir that has .nrepl-port; prints the port to stdout.
(require '[clojure.string :as str])

(defn port-listening? [host port]
  (try
    (let [s (java.net.Socket. host port)]
      (.close s)
      true)
    (catch Exception _ false)))

(let [port-file "./.nrepl-port"
      port-file-exists (.exists (clojure.java.io/file port-file))
      port-str (when port-file-exists (str/trim (slurp port-file)))
      port (when port-str (parse-long port-str))
      listening? (and port (port-listening? "127.0.0.1" port))]
  (cond
    (and port listening?)
    (println "the current project nrepl port is" port)
    (and port-file-exists port)
    (println "nrepl started before, but now can't connect")
    :else
    (println "nrepl not started")))
