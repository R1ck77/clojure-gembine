(ns clojure-gembine.board
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils]))

(def symbol-to-image {:rb red-bead
                      :rB red-ball
                      :rt red-triangle
                      :rs red-square
                      :rp red-pentagon
                      :gb green-bead
                      :gB green-ball
                      :gt green-triangle
                      :gs green-square
                      :gp green-pentagon})

(def symbol-check-order [:rb :rB :rt :rs :rp :gb :gB :gt :gs :gp])

(def match-threshold 0.80)

(defn- score-for-symbol-in-area? [area symbol]
  (nth (utils/match-template area (get symbol-to-image symbol)) 2))

(defn- result-for-symbol-in-area [area symbol]
  [symbol (score-for-symbol-in-area? area symbol)])

(defn- remove-underscorers [match-results]
  (filter #(> (second %) match-threshold) match-results))

(defn- get-first-matching-result
  "evaluate lazily the argument and return the symbol corresponding of the  result that beats the threshold.

match-result should be like: [[:rb 0.3] [:gs 0.999] … ]"
  [match-results]
  (when-let [filtered (remove-underscorers match-results)]
        (-> (sort-by second filtered) first first)))

(defn- get-best-match                               
  "Return a code specifying the best match"
  [area]
  (get-first-matching-result (map (partial result-for-symbol-in-area area)
                                  symbol-check-order)))

(defn- read-cell [screenshot column row]
  (let [cell-region (get-board-cell screenshot column row)]
    (get-best-match cell-region)))

(defn- read-row [screenshot row]
  [(read-cell screenshot 0 row)
   (read-cell screenshot 1 row)
   (read-cell screenshot 2 row)
   (read-cell screenshot 3 row)])

(defn read-board [screenshot]
  [(read-row screenshot 0)
   (read-row screenshot 1)
   (read-row screenshot 2)
   (read-row screenshot 3)])

(defn rotate-board
  "Rotate the board in a way that the right move corresponds to the previos up move"
  [board]
  (vec
   (map (fn [index] (vec (map #(nth % index) (reverse board))))
        [0 1 2 3])))
