(ns clojure-gembine.game)

(defn rotate
  "Rotate the board in a way that the right move corresponds to the previos up move"
  [board]
  (vec
   (map (fn [index] (vec (map #(nth % index) (reverse board))))
        [0 1 2 3])))

