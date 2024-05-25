(require '[clojure.test :as t])

(require 'time-test)

(def test-results
  (t/run-tests 'time-test))

(let [{:keys [fail error]} test-results]
  (when (pos? (+ fail error))
    (System/exit 1)))
