(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

(defn- compare-moves
  [[_ points]]
  (- points))

(defn minimax-moves-evaluator [board next-element]
  (get-in
   (vec
    (sort-by compare-moves
             (map (fn [[move boards]]
                    (vector move (apply min (map score/simple-score boards))))
                  (game/evolve-board board #{next-element}))))
   [0 0]))

(defn create-minimax-two-ahead-moves-evaluator
  "Create a minimax that looks 2 steps ahead. Not optimized, sketched code

Ignores the weight of dead paths…"
  []
  (let [allowed-next-elements (atom #{:rb :rB})]
    (fn [board next-element]
      (swap! allowed-next-elements #(conj % next-element))

      (first (first (sort-moves-ascending (map (fn [[move evolved-board]]
                                           (vector move (apply min (flatten
                                                                    (map (fn [l2-board]
                                                                           (map #(if (nil? %) 0 %) (map #(second (raw-minimax-moves-evaluator l2-board %))
                                                                                 @allowed-next-elements)))
                                                                         (game/all-insertions evolved-board move next-element))))))
                                               (evolved-boards board))))))))




