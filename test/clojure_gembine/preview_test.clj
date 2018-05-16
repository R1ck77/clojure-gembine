(ns clojure-gembine.preview-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.test-utils :as utils]
            [clojure-gembine.preview :refer :all]))

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

(deftest test-next-move
  (testing "next move for available mocks"
    (is (= :rB (next-move (utils/load-mock-image 2))))
    (is (= :rb (next-move (utils/load-mock-image 3))))))
