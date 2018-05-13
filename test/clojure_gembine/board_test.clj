(ns clojure-gembine.board-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.board :refer :all]
            [clojure-gembine.test-utils :as utils]))

(deftest test-read-board
  (testing "result on mock 1"
    (is (= [[:rt :rB :rt :rs]
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
           (read-board (utils/load-mock-image 3)))))
  (testing "result on mock 4"
    (is (= [[nil :rb nil nil]
            [:rB nil :rb :rs]
            [nil nil :rt :rB]
            [nil :rt :rb :rp]]
           (read-board (utils/load-mock-image 4)))))
  (testing "result on mock 5"
    (is (= [[nil nil :rb :rB]
            [nil :rB :rB :rt]
            [:rb :rt :rs :gb]
            [:rb :rb :rB :rt]]
           (read-board (utils/load-mock-image 5)))))
  (testing "result on mock 6"
    (is (= [[:rB :rt :rB :rb]
            [:gb :rB :rs :rb]
            [:rb :gB :rp :gt]
            [:rB :rb :rs :rb]]
           (read-board (utils/load-mock-image 6)))))
  (testing "result on mock 7"
    (is (= [[:rB :rt :rB :rb]
            [:gb :rB :rs :rB]
            [nil :gB :rp :gt]
            [:rB :rb :rs :rb]]
         (read-board (utils/load-mock-image 7))))))
