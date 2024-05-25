(ns password-gen)

(require '[babashka.cli :as cli])

;; bb password_gen.clj --length 2 --help
(def cli-options {:length {:default 10 :coerce :long}
                  :help {:coerce :boolean}})

(defn parse-to-long [s]
  (try
    (Long/parseLong s)
    (catch Exception _e
      12)))

(defn generate-password [length]
  (let [chars "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()-=_+[]{}|;:,.<>?"]
    (apply str (repeatedly length #(nth chars (rand-int (count chars)))))))

(let [args (cli/parse-opts *command-line-args* {:spec cli-options})
      password (generate-password (:length args))]
  (println password))