(ns clojure-gembine.core-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.test-utils :as utils]
            [clojure-gembine.core :refer :all]))

(deftest test-is-game-over?
  (testing "mocks with game over detected correctly"
    (is (is-game-over? (utils/load-mock-image 1)))
    (is (is-game-over? (utils/load-mock-image 6)))
    (is (is-game-over? (utils/load-mock-image 7))))
  (testing "mocks without game over are not detected erroneously"
    (is (not (is-game-over? (utils/load-mock-image 2))))
    (is (not (is-game-over? (utils/load-mock-image 3))))
    (is (not (is-game-over? (utils/load-mock-image 4))))
    (is (not (is-game-over? (utils/load-mock-image 5))))))

(deftest test-is-gembine?
  (testing "all mocks report positive"
    (is (is-gembine? (utils/load-mock-image 1)))
    (is (is-gembine? (utils/load-mock-image 2)))
    (is (is-gembine? (utils/load-mock-image 3)))
    (is (is-gembine? (utils/load-mock-image 4)))
    (is (is-gembine? (utils/load-mock-image 5)))
    (is (is-gembine? (utils/load-mock-image 6)))
    (is (is-gembine? (utils/load-mock-image 7))))
  (testing "outlier image reports negative"
    (is (not (is-gembine? (utils/load-outlier))))))
