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
  "One depth minimax solver. Can still make it to 60k, with a bit of patience"
  ([board next-element]
   (minimax-moves-evaluator board next-element score/simple-score))
  ([board next-element score-function]
   (get-best-minimax-move
    (keep-only-feasible board (minimax-score-moves board #{next-element} score-function)))))

(defn- deep-minimax-terminal-step
  [board next-elements score-function]
  (map second (minimax-score-moves board next-elements score-function)))

(defn- step-2-minimax-my-move
  "Return the minimax of the 2 depth move. The move selected is irrelevant, just the points matter"
  [board next-elements score-function]
  (if (= board :game-over)
    (score-function :game-over)
    (apply max (deep-minimax-terminal-step board next-elements score-function))))

(defn- step-2-minimax-computer-move
  [move boards next-elements score-function]
  (vector move (apply min (map #(step-2-minimax-my-move % next-elements score-function) boards))))

(defn create-minimax-level-2-solver
  "Create a minimax that looks 2 steps ahead"
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

(declare ^:private step-n-minimax-computer-move)

(defn- step-n-minimax-my-move
  "Return the minimax of the n depth move. The move selected is irrelevant, just the points matter"
  [board next-elements score-function depth]
  (if (= board :game-over)
    (score-function :game-over)
    (let [depth (dec depth)]
      (apply max (if (zero? depth)
                   (deep-minimax-terminal-step board next-elements score-function)
                   (map second (map #(step-n-minimax-computer-move % [board] next-elements score-function depth) moves)))))))

(defn- step-n-minimax-computer-move
  [move boards next-elements score-function depth]
  (vector move (apply min (map #(step-n-minimax-my-move % next-elements score-function depth) boards))))


(defn create-deep-minimax-solver
  "Level n solver"
  ([depth]
   (create-deep-minimax-solver depth score/simple-score))
  ([depth score-function]
   (assert (> depth 1))
   (let [allowed-next-elements (atom #{:rb :rB})]
     (fn [board next-element]
       (swap! allowed-next-elements #(conj % next-element))
       (get-best-minimax-move
        (keep-only-feasible board
                            (pmap (fn [[move boards]]
                                   (step-n-minimax-computer-move move boards @allowed-next-elements score-function (- depth 1)))
                                 (game/evolve-board board #{next-element})
                                 )))))))

(defn- vector-of-move-min
  [[move scores]]
  (vector move (apply min scores)))

(defn map-of-movesmin
  [m-moves-scores]
  (map vector-of-move-min m-moves-scores))

(defn- vector-of-move-scores
  [[move boards] score-function]
  (vector move (map score-function boards)))

(defn- f-vector-of-move-scores
  [score-function]
  #(vector-of-move-scores % score-function))

(defn map-of-movesscores
  [m-moves-boards score-function]
  (into {}
        (map (f-vector-of-move-scores score-function) m-moves-boards)))

(defn map-of-movesboards
  [board next-element]
  (game/evolve-board board #{next-element}))

(defn- f-is-legal-move?
  [board]
  (partial game/can-move? board))

(defn set-of-feasible-moves
  [board]
  (set (filter (f-is-legal-move? board) moves)))

(defn map-of-feasible-movesx
  [board m-moves-x]
  (let [feasible-moves (set-of-feasible-moves board)]
    (into {}
          (filter (fn [[move x]]
                    (contains? feasible-moves move))
                  m-moves-x))))

(defn- negated-min-value
  [[_ min-value]]
  (- min-value))

(defn vector-of-move-maxscore
  [m-movesmin]
  (first
   (sort-by negated-min-value m-movesmin)))

(defn minimax-moves-evaluator
  "One depth minimax solver. Can still make it to 60k, with a bit of patience"
  ([board next-element]
   (minimax-moves-evaluator board next-element score/simple-score))
  ([board next-element score-function]
   (first
    (vector-of-move-maxscore
     (map-of-movesmin
      (map-of-movesscores (map-of-feasible-movesx board
                                                  (map-of-movesboards board next-element))
                          score-function))))))
