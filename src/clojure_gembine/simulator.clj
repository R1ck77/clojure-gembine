(ns clojure-gembine.simulator
  (:require [clojure-gembine.game :as game]
            [clojure.set :refer [union]]))

(def empty-board [[nil nil nil nil]
                  [nil nil nil nil]
                  [nil nil nil nil]
                  [nil nil nil nil]])

(def initial-next-available-elements #{:rb :rB})

(def next-element-expansion-rule {:gB :rt, :gs :rs})

(defn- simulate-move
  [board next-value human-move]
  (let [result (rand-nth (get (game/evolve-board board #{next-value}) human-move))]
    (if (or (= :game-over result)
            (empty? (filter #(game/can-move? result %) game/moves)))
      :game-over
      result)))

(defn- next-move
  "Given a board, a set of potential next values and a solver, advance the board"
  [board potential-next-values solver]
  (let [next (rand-nth (seq potential-next-values))]
    (simulate-move board next (solver board next))))

(defn- add-random-element [board available-elements]
  (assoc-in board [(rand-int 4) (rand-int 4)] (rand-nth (seq available-elements))))

(defn starting-board []
  (let [add-to-board #(add-random-element % #{:rb :rB})]
    (-> empty-board add-to-board add-to-board add-to-board)))

(defn- elements-to-add [board-elements]
  (filter keyword?
          (map next-element-expansion-rule
               board-elements)))

(defn- update-next-moves [board current-available-moves]
  (let [all-board-elements (set (flatten board))]
    (union current-available-moves (set (elements-to-add all-board-elements)))))

(defn- moves-before-game-over
  [xboards]
  (count
   (take-while #(not= % :game-over)
               xboards)))

(defn start-game [solver]
  (let [available-next (atom initial-next-available-elements)]
    (moves-before-game-over
     (iterate (fn [board]
                (swap! available-next
                       #(update-next-moves board %))
                (next-move board @available-next solver))
              (starting-board)))))

(defn test-drive-solver [solver iterations]
  (int
   (/ (reduce #(+ % %2) (pmap (fn [_] (start-game solver)) (range iterations))) iterations)))
