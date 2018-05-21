(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

(defn- compare-moves
  [[_ points]]
  (- points))

(defn- minimax-score-moves
  [board next-elements score-function]
  (map (fn [[move boards]]
         (vector move (apply min (map score-function boards))))
       (game/evolve-board board next-elements)))

(defn- get-best-minimax-move
  "Get the best move in a [[:left x] [:right y] [:up z] [:down w]] array"
  [minimax-results]
  (-> compare-moves
      (sort-by minimax-results)
      vec
      (get-in [0 0])))

(defn- keep-only-feasible
  "Filter out impossible moves.

  Required because the 2-deep minimax doesn't distinguishes between immediate game-over and late one,
  and this will mess the end game when minimax lvl 2 foresee a defeat"
  [board results]
  (filter (fn [[move _]] (game/can-move? board move))
          results))

(defn minimax-moves-evaluator
  ([board next-element]
   (minimax-moves-evaluator board next-element score/simple-score))
  ([board next-element score-function]
   (get-best-minimax-move
    (keep-only-feasible board (minimax-score-moves board #{next-element} score-function)))))

(defn- step-2-minimax-scores
  [board next-elements score-function]
  (map second (minimax-score-moves board next-elements score-function)))

(defn- step-2-minimax-my-move
  "Return the minimax of the 2 depth move. The move selected is irrelevant, just the points matter"
  [board next-elements score-function]
  (if (= board :game-over)
    (score-function :game-over)
    (apply max (step-2-minimax-scores board next-elements score-function))))

(defn- step-2-minimax-computer-move
  [move boards next-elements score-function]
  (vector move (apply min (map #(step-2-minimax-my-move % next-elements score-function) boards))))

(defn create-minimax-level-2-solver
  "Create a minimax that looks 2 steps ahead. Not optimized, sketched code

  Ignores the weight of dead paths…"
  ([]
   (create-minimax-level-2-solver score/simple-score))
  ([score-function]
   (let [allowed-next-elements (atom #{:rb :rB})]
     (fn [board next-element]
       (swap! allowed-next-elements #(conj % next-element))
       (get-best-minimax-move
        (keep-only-feasible board
                            (map (fn [[move boards]]
                                   (step-2-minimax-computer-move move boards @allowed-next-elements score-function))
              (game/evolve-board board #{next-element}))))))))
