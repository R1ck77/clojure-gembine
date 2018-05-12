(ns clojure-gembine.utils-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :refer [file input-stream resource]]
            [clojure-gembine.utils :refer :all]
            [clojure-gembine.parameters :refer :all])
  (:import [org.apache.commons.io IOUtils]
           [java.io FileInputStream]
           [javax.imageio ImageIO]))

(deftest test-template-as-file
  (testing "return a valid readable file"
    (let [result (template-as-file "red-bead.png")]
      (is (.exists result))
      (is (.canRead result))
      (is (.delete result))))
  (testing "content of the copy and of the resource are identical"
    (let [temp-file (template-as-file "green-bead.png")]
      (with-open [expected (input-stream (resource "templates/green-bead.png"))
                  result (FileInputStream. temp-file)]
        (is (IOUtils/contentEquals expected result))
        (.delete temp-file)))))

(defn- load-mock-image []
  (ImageIO/read (resource "mock/modified-with-game-over-screen.png")))

(defn- in-range [[x y] [x1 y1 x2 y2]]
  (and
   (>= x x1) (>= y y1)
   (<= x x2) (<= y y2)))

(deftest test-match-template
  (testing "Matching the mock with a 'game over' template returns max point in the message area"
    (let [[x y _] (match-template (load-mock-image) game-over)]
      (is (in-range [x y] message-area))))
  (testing "The green triangle in the mock are is to the right below of the red one"
    (let [mock-image (load-mock-image)
          [xr yr _] (match-template mock-image red-triangle)
          [xg yg _] (match-template mock-image green-triangle)]
      (is (> xg xr))
      (is (> yg yr))))
  (testing "the cell [0 2] in the mock matches an empty cell better than a red bead or a green bead"
    (let [mock-cell (get-board-cell (load-mock-image 0 2))
          [_ _ evalue] (match-template mock-cell void-board)
          [_ _ rvalue] (match-template mock-cell red-bead)
          [_ _ gvalue] (match-template mock-cell green-bead)]
      (is (> evalue rvalue))
      (is (> evalue gvalue)))))
