(ns clojure-gembine.parameters-test
  (require [clojure.test :refer :all]
           [clojure-gembine.parameters :as parameters]))

(deftest test-resize-area
  (testing "is a null operation if the screenshot is full HD"
    (is (= [12 56 34 78] (parameters/resize-area [1920 1080] [12 56 34 78]))))
  (testing "resizes the image proportianally if the screen size is different"
    (is (= [6 56/3 17 26]
           (parameters/resize-area [960 360] [12 56 34 78])))))
