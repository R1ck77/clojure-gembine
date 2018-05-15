(ns clojure-gembine.game-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.game :refer :all]))

(def demo-board [[:a :b :c :d]
                 [:e :f :g :h]
                 [:i :l :m :n]
                 [:o :p :q :r]])

(def rotated-board [[:o :i :e :a]
                    [:p :l :f :b]
                    [:q :m :g :c]
                    [:r :n :h :d]])

(deftest test-rotate
  (testing "generic case"
    (is (= rotated-board (rotate demo-board))))
  (testing "4 rotations are an identity"
    (is (= demo-board (-> demo-board rotate rotate rotate rotate)))))
