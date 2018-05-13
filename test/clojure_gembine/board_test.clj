(ns clojure-gembine.board-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.board :refer :all]
            [clojure-gembine.test-utils :as utils]))

(deftest test-read-board
  (testing "result on mock 1"
    (is (= [[:rt :rB :rt :ra]
            [:rB :rt :gb :rB]
            [:rb :gB :rb :rs]
            [:rs :rt :gB :rb]]
         (read-board (utils/load-mock-image 1)))))
  (testing "result on mock 2"
    (is (= [[nil nil :rb :rt]
            [:rb :rt :rs :rB]
            [:rb nil :rB :rb]
            [nil nil nil :rB]]
         (read-board (utils/load-mock-image 2)))))
  (testing "result on mock 3"
    (is (= [[nil nil :rB nil]
            [:rb :rB nil nil]
            [:rs nil nil :rb]
            [:rt :rB :rb nil]]
         (read-board (utils/load-mock-image 3))))))
