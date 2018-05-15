(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

(defn- compare-moves
  [[_ points]]
  (- points))

(defn score-move-results-without-addition [board move]
  (vector move (score/simple-score (game/move board move))))

;;; fails horribly if the move is not feasible
(defn greedy-moves-evaluator [board]
  (first
   (first
    (sort-by compare-moves (map (partial score-move-results-without-addition board)
                                (filter #(game/can-move? board %) moves))))))
