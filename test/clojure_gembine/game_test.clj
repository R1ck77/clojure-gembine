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


(deftest test-step
  (testing "translation only"
    (is (= [[:rb nil nil nil]
            [:rB :rB :rB nil]
            [:gb nil :rt nil]
            [:rs :gs :rs :gs]]
           (step [[nil :rb nil nil]
                  [:rB nil :rB :rB]
                  [:gb nil nil :rt]
                  [:rs :gs :rs :gs]]))))
  (testing "generic example 1"
    (is (= [[:rB :rB :rB nil]
            [:rt :rt :rt nil]
            [:rs :gt :rp nil]
            [:rs nil :rs nil]]
           (step [[:rb :rb :rB :rB]
                  [nil :rt :rt :rt]
                  [:rs :gB :gB :rp]
                  [nil :rs nil :rs]])))))

(deftest test-move
  (testing "move up"
    (is (= [[:rb :rb :rB :rB]
            [:rs :rt :rt :rt]
            [nil :gB :gB :rp]
            [nil :rs nil :rs]]
           (move [[:rb :rb :rB :rB]
                  [nil :rt :rt :rt]
                  [:rs :gB :gB :rp]
                  [nil :rs nil :rs]] :up))))
  (testing "move down"
    (is (= [[nil :rb nil :rB]
            [:rb :rt :rB :rt]
            [nil :gB :rt :rp]
            [:rs :rs :gB :rs]]
           (move [[:rb :rb :rB :rB]
                  [nil :rt :rt :rt]
                  [:rs :gB :gB :rp]
                  [nil :rs nil :rs]] :down))))
  (testing "move left"
    (is (= [[:rB :rB :rB nil]
            [:rt :rt :rt nil]
            [:rs :gt :rp nil]
            [:rs nil :rs nil]]
           (move [[:rb :rb :rB :rB]
                  [nil :rt :rt :rt]
                  [:rs :gB :gB :rp]
                  [nil :rs nil :rs]] :left))))
  (testing "move right"
    (is (= [[nil :rb :rb :rt]
            [nil nil :rt :rs]
            [nil :rs :gt :rp]
            [nil nil :rs :rs]]
           (move [[:rb :rb :rB :rB]
                  [nil :rt :rt :rt]
                  [:rs :gB :gB :rp]
                  [nil :rs nil :rs]] :right)))))

(deftest test-insertion-indices
  (testing "insertion indices in the natural orientation"
    (is (= [1 3]
           (insertion-indices [[:rb :rb :rb :rb]
                               [nil :rb :rb nil]
                               [nil nil :rb :rb]
                               [nil :rb :rb nil]] :left)))))
