(ns clojure-gembine.end-game
  (:require [clojure-gembine.utils :refer [match-template sleep]]
            [clojure-gembine.parameters :refer :all]
            [clojure-gembine.keyboard :refer :all]))

(defn secret-level? [screenshot]
  (let [secret-area-region (get-screen-section screenshot secret-level-area)]
    (> (nth (match-template secret-area-region entering-secret-area) 2) 0.8)))

(defn end-game-ritual [robot]
  (sleep 3000)
  
  (tap-enter robot) (sleep 5000)
  
  (tap-esc robot) (sleep 2000)

  (tap-arrow robot :right) (sleep 300)
  (tap-arrow robot :right) (sleep 300)
  (tap-arrow robot :right) (sleep 300)
  (tap-enter robot) (sleep 5000)

  (tap-enter robot) (sleep 1000)

  (tap-arrow robot :down) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-enter robot) (sleep 3000)

  (tap-arrow robot :up ) (sleep 300)
  (tap-enter robot) (sleep 5000)

  (tap-esc robot) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-enter robot) (sleep 5000)
  (println "ritual performed!"))
