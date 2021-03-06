(ns clojure-gembine.move-logic
  (:require [clojure-gembine.game :as game]
            [clojure-gembine.board :as board]
            [clojure-gembine.score :as score]))

(def moves [:up :down :left :right])

(defn- vector-of-move-min
  [[move scores]]
  (vector move (apply min scores)))

(defn map-of-movesmin
  [m-moves-scores parallel]
  ((if parallel pmap map) vector-of-move-min m-moves-scores))

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
  "Return a vector [move score] representing the best move for the arguments in a minimax step.

  The result can be nil if there is no possible move available"
  ([board next-elements score-function]
   (bestmove-maxscore board next-elements score-function false))
  ([board next-elements score-function parallel]
   (vector-of-move-maxscore
    (map-of-movesmin
     (map-of-movesscores (map-of-feasible-movesx board
                                                 (map-of-movesboards board next-elements))
                         score-function)
     parallel))))

(defn one-step-minimax-function
  "One depth minimax solver. Can still make it to 60k, with a bit of patience"
  ([board next-element]
   (one-step-minimax-function board next-element score/simple-score))
  ([board next-element score-function]
   (first
    (bestmove-maxscore board
                       #{next-element}
                       score-function))))

(def minimax-moves-evaluator one-step-minimax-function)

(defn score-board-with-minimax ;;; TODO/FIXME extract functions. Parallelize
  [allowed-elements score-function board]
  (if (or (nil? board) (= :game-over board))
    (score-function :game-over)
    (let [score-or-nil (second (bestmove-maxscore board allowed-elements score-function))]
      (if score-or-nil
        score-or-nil
        (score-function :game-over)))))

(defn recursive-score-board
  [allowed-elements score-function depth board]
  (if (= depth 1)
    (score-function board)
    (score-board-with-minimax
             allowed-elements
             (partial recursive-score-board
                      allowed-elements
                      score-function
                      (dec depth))
             board)))

(defn n-steps-minimax-function
  [board next-element allowed-elements score-function depth]
  (assert (> depth 0))
  (first
   (bestmove-maxscore board
                      #{next-element}
                      (partial recursive-score-board
                               allowed-elements
                               score-function
                               depth)
                      true)))


(defn invoke-minimax-function-with-updated-cpu-moves
  "Call the minimax function m(board next-element allowed-cpu-moves) keeping track of the possible cpu moves"
  [allowed-next-elements-atom minimax-function board next-element]
  (swap! allowed-next-elements-atom #(conj % next-element))
  (minimax-function board next-element @allowed-next-elements-atom))

(defn create-minimax-level-n-solver
  "Create a minimax that looks 3 steps ahead"
  ([depth]
   (create-minimax-level-n-solver score/simple-score depth))
  ([score-function depth]
   (partial invoke-minimax-function-with-updated-cpu-moves
            (atom #{:rb :rB})
            #(n-steps-minimax-function % %2 %3 score-function depth))))

