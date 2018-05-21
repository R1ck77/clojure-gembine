(ns clojure-gembine.board
  (:require [clojure-gembine.parameters :refer :all]
            [clojure-gembine.utils :as utils]))

(def red-condition #(> (nth % 2) 80))
(def blue-condition #(> (first %) 80))
(def green-condition #(> (second %) 80))

(def color-to-symbol-to-image {
                      :red {
                            :rb red-bead
                            :rB red-ball
                            :rt red-triangle
                            :rs red-square
                            :rp red-pentagon
                            }
                      :green {
                              :gb green-bead
                              :gB green-ball
                              :gt green-triangle
                              :gs green-square
                              :gp green-pentagon
                              }
                      :blue {
                             :bb green-bead
                             :bB green-ball
                             :bt green-triangle
                             :bs green-square
                             :bp green-pentagon}})

(def match-threshold 0.80)

(defn- color-of-symbol-in-area [area]
  (let [mat-area (utils/bufimage-to-mat area)
        center [(/ (.rows mat-area) 2) (/ (.cols mat-area) 2)]
        colors (seq (.get mat-area (first center) (second center)))]
    (cond
      (red-condition colors) :red
      (blue-condition colors) :blue
      (green-condition colors) :green
      :default :black)))

(defn- score-for-template-in-area [area template]
  (nth (utils/match-template area template) 2))

(defn- remove-underscorers [match-results]
  (filter #(> (second %) match-threshold) match-results))

(defn- get-first-matching-result
  "evaluate lazily the argument and return the symbol corresponding of the  result that beats the threshold.

match-result should be like: [[:rb 0.3] [:gs 0.999] … ]"
  [match-results]
  (when-let [filtered (remove-underscorers match-results)]
        (-> (sort-by second filtered) first first)))

(defn- get-best-matching-symbol
  [area symbols-to-image]
  (get-first-matching-result
   (map (fn [[symbol template]]
          (vector symbol (score-for-template-in-area area template)))
        symbols-to-image)))

(defn get-best-match                               
  "Return a code specifying the best match"
  [area]
  (let [matching-color (color-of-symbol-in-area area)]
    (if (= :black matching-color)
      nil
      (get-best-matching-symbol area (get color-to-symbol-to-image matching-color)))))

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

