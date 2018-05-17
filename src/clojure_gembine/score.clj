(ns clojure-gembine.score
  (:require [clojure-gembine.game :as game]))


(defn- score-empty-spaces
  "Score empty board cells using this mapping:

2.0 * +-------+
      |2 3 3 2|
      |3 4 4 3|
      |3 4 4 3|
      |2 3 3 2|
      +-------+"
  [board]
  (* 2.0 (reduce + (map *
                  (map #(if (nil? %) 1 0)
                       (flatten board))
                  [2 3 3 2
                   3 4 4 3
                   3 4 4 3
                   2 3 3 2]))))

(defn- score-tuple
  "Return 2 if the pair is comprised of non nil identical values, 0 otherwise"
  [[a b]]
  (if (and a b (= a b)) 2 0))

(defn- score-row [row]
  "Return the number of identical couples in a row"
  (apply + (map score-tuple (partition 2 1 row))))

(defn- score-side-adjacent-values [board]
  (apply + (map score-row board)))

(defn- score-adjacent-values [board]
  "Score all adjacent identical values as 2 points, in both horizontal and vertical direction"
  (+ (score-side-adjacent-values board)
     (-> board game/rotate score-side-adjacent-values)))

(defn simple-score
  "Very simple board scoring:

- :game-over is evaluated to -1
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
  (if (= board :game-over)
    -1
    (+ (score-empty-spaces board)
       (score-adjacent-values board))))
