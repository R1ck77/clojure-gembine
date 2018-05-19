(ns clojure-gembine.simulator
  (:require [clojure-gembine.game :as game]))


(defn- simulate-move
  [board human-move next-value]
  (let [result (rand-nth (get (game/evolve-board board #{next-value}) human-move))]
    (if (empty? (filter #(game/can-move? result %) game/moves))
      :game-over
      result)))
