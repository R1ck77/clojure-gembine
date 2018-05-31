(ns clojure-gembine.end-game
  (:require [clojure-gembine.utils :refer [match-template sleep]]
            [clojure-gembine.parameters :refer :all]
            [clojure-gembine.keyboard :refer :all]))

(def secret-level-threshold 0.8)

(defn secret-level?
  [screenshot]
  (let [secret-area-region (get-screen-section screenshot secret-level-area)]
    (> (nth (match-template secret-area-region entering-secret-area) 2) secret-level-threshold)))

(defn end-game-ritual
  "Guide JYDGE from the 'Entering Secret Level' screen to a non tutorial Gembine game

Delays arbitrary, and hopefully on the large side"
  [robot]
  (sleep 3000)

  ;;; enter secret level
  (tap-enter robot) (sleep 5000)

  ;;; exit to level menu
  (tap-esc robot) (sleep 2000)

  ;;; select the "exit to map"
  (tap-arrow robot :right) (sleep 300)
  (tap-arrow robot :right) (sleep 300)
  (tap-arrow robot :right) (sleep 300)
  (tap-enter robot) (sleep 5000)

  ;;; make sure you are on "Collectibles", then tap enter
  (tap-arrow robot :down) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-arrow robot :left) (sleep 300)
  (tap-arrow robot :left) (sleep 300)
  (tap-arrow robot :left) (sleep 300)
  (tap-enter robot) (sleep 1000)

  ;;; select the gem
  (tap-arrow robot :down) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-enter robot) (sleep 3000)

  ;;; select gembine
  (tap-arrow robot :up ) (sleep 300)
  (tap-enter robot) (sleep 5000)

  ;;; restart the gembine game to remove the tutorial
  (tap-esc robot) (sleep 300)
  (tap-arrow robot :down) (sleep 300)
  (tap-enter robot) (sleep 5000)
  (println "ritual performed!"))
