(ns clojure-gembine.end-game-test
  (:require [clojure.test :refer :all]
            [clojure-gembine.end-game :as end-game]
            [clojure-gembine.test-utils :as test-utils]))

(deftest test-secret-level?
  (testing "positive mocks"
    (is (end-game/secret-level? (test-utils/load-mock-image 10))))
  (testing "negative mocks"
    (is (not (end-game/secret-level? (test-utils/load-mock-image 1))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 2))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 3))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 4))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 5))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 6))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 7))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 8))))
    (is (not (end-game/secret-level? (test-utils/load-mock-image 9))))))
