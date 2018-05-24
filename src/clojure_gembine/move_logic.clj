(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

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
  [board next-elements]
  (game/evolve-board board next-elements))

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

(defn bestmove-maxscore
  [board next-elements score-function]
  (vector-of-move-maxscore
     (map-of-movesmin
      (map-of-movesscores (map-of-feasible-movesx board
                                                  (map-of-movesboards board next-elements))
                          score-function))))

(defn minimax-moves-evaluator
  "One depth minimax solver. Can still make it to 60k, with a bit of patience"
  ([board next-element]
   (minimax-moves-evaluator board next-element score/simple-score))
  ([board next-element score-function]
   (first (bestmove-maxscore board #{next-element} score-function))))


(defn two-steps-minimax-function
  [board next-element allowed-elements score-function]
  (first (vector-of-move-maxscore
          (map-of-movesmin
           (map-of-movesscores
            (map-of-feasible-movesx board
                                    (map-of-movesboards board #{next-element}))
            (fn [board] (let [score-or-nil (second (bestmove-maxscore board allowed-elements score-function))]
                          (if score-or-nil
                            score-or-nil
                            (score-function :game-over)))))))))

(defn invoke-minimax-function-with-updated-cpu-moves
  "Call the minimax function m(board next-element allowed-cpu-moves) keeping track of the possible cpu moves"
  [allowed-next-elements-atom minimax-function board next-element]
  (swap! allowed-next-elements-atom #(conj % next-element))
  (minimax-function board next-element @allowed-next-elements-atom))

(defn create-minimax-level-2-solver
  "Create a minimax that looks 2 steps ahead"
  ([]
   (create-minimax-level-2-solver score/simple-score))
  ([score-function]
   (partial invoke-minimax-function-with-updated-cpu-moves
            (atom #{:rb :rB})
            #(two-steps-minimax-function % %2 %3 score-function))))

