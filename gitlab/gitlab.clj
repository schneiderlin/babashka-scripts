(ns gitlab)

(require '[babashka.http-client :as http])
(require '[clojure.java.io :as io])
(require '[cheshire.core :as json])

(def projects (-> 
  (http/get "https://git.huamaobook.com/api/v4/projects?all=true"
          {:headers {"PRIVATE-TOKEN" "glpat-dxNYe3kG_NaJSQR6_t3J"}})
  (:body)
  (json/parse-string true)
 ))

(def werkzeug-projects (-> 
  (http/get "https://git.huamaobook.com/api/v4/projects?all=true&search=werkzeug"
          {:headers {"PRIVATE-TOKEN" "glpat-dxNYe3kG_NaJSQR6_t3J"}})
  (:body)
  (json/parse-string true)
 ))

(def example-project (first (filter #(= "werkzeug" (:name %)) werkzeug-projects)))

(def example-project-id (:id example-project))

(def branches (-> 
  (http/get (str "https://git.huamaobook.com/api/v4" 
                 "/projects/" example-project-id "/repository/branches")
          {:headers {"PRIVATE-TOKEN" "glpat-dxNYe3kG_NaJSQR6_t3J"}})
  (:body)
  (json/parse-string true)
 ))