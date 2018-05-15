(ns clojure-gembine.score-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.score :refer :all]))

(deftest test-simple-score
  (testing "score contributed by empty places only"
    (is (= 17
           (simple-score [[nil :rb nil nil]
                          [nil :gb :rb :gb]
                          [:gb :rb nil :rb]
                          [:rb nil :rb :gb]])))))
