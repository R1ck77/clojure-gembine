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

(defn- available-moves [board]
  (filter #(game/can-move? board %) moves))

;;; doesn't account for "next move"
(defn greedy-moves-evaluator [board]
  (first
   (first
    (sort-by compare-moves (map (partial score-move-results-without-addition board)
                                (available-moves board))))))

(defn- evolved-boards
  "Return a list of tuples:

[:left <board evolved with left move>
 :right <board evolved with right move>
 …]"
  [board]
  (map (fn [move]
         (vector move (game/move board move)))
       (available-moves board)))

(defn- pure-minimax-score-move [move evolved-board next-element]
  (vector move (apply min (map score/simple-score
                               (game/all-insertions evolved-board move next-element)))))

(defn- sort-moves-ascending [moves]
  (sort-by compare-moves moves))

(defn raw-minimax-moves-evaluator [board next-element]
  (first (sort-moves-ascending
          (map (fn [[move evolved-board]]
                 (pure-minimax-score-move move evolved-board next-element))
               (evolved-boards board)))))


;;; doesn't account for the probabilistic nature of the game
;;; TODO very poor performances in respect to potential, not necessarily an issue
(defn minimax-moves-evaluator [board next-element]
  (first
   (raw-minimax-moves-evaluator board next-element)))

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




