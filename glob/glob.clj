(ns glob
  (:require [babashka.fs :as fs]
            [babashka.cli :as cli]))

(def cli-options 
  {:spec 
   {:root {:default "." 
           :coerce :string 
           :desc "the root directory to search"} 
    :pattern {:default "*" 
              :coerce :string 
              :desc "the pattern to match"} 
    :help {:coerce :boolean 
           :alias :h}}
   :args->opt [:root :pattern]})

(defn show-help
  [spec]
  (cli/format-opts (merge spec {:order (vec (keys (:spec spec)))})))

(defn main [root pattern]
  (let [_ (println (str "root: " root " pattern: " pattern))
        result (fs/glob root pattern)]
    (println (map #(.toString %) result))))

(let [args (cli/parse-opts *command-line-args* cli-options)]
  (if (or (:h args) (:help args))
    (println (show-help cli-options))
    (main (:root args) (:pattern args))))