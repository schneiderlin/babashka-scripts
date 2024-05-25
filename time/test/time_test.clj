(ns time-test
  (:require [clojure.test :refer [deftest testing is]]
            ))

(deftest multiply-test
  (testing "multiplication works as expected"
    (is (= 4 (* 2 2)))))