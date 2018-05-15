(ns clojure-gembine.score
  (:require [clojure-gembine.game :as game]))


(defn- score-empty-spaces
  "Score empty board cells using this mapping:

+-------+
|2 3 3 2|
|3 4 4 3|
|3 4 4 3|
|2 3 3 2|
+-------+"
  [board]
  (reduce + (map *
                 (map #(if (nil? %) 1 0)
                      (flatten board))
                 [2 3 3 2
                  3 4 4 3
                  3 4 4 3
                  2 3 3 2])))

(defn- score-adjacent-values [board]
  0)

(defn simple-score
  "Very simple board scoring:

- empty places are scored depending on the position:

+-------+
|2 3 3 2|
|3 4 4 3|
|3 4 4 3|
|2 3 3 2|
+-------+

- adjacent values of the same type are always scored 2, without regard for grouping or position.

worth 2 at the board corners, 3 in the borders, 4 in the middle "
  [board]
  (+ (score-empty-spaces board)
     (score-adjacent-values board)))
