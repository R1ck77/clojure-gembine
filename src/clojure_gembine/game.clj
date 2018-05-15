(ns clojure-gembine.game)

(def next-cell {
                :rb :rB
                :rB :rt
                :rt :rs
                :rs :rp
                :rp :gb
                :gb :gB
                :gB :gt
                :gt :gs
                :gs :gp
                :gp :gp}) ;;; this one is completely made up

(defn rotate
  "Rotate the board in a way that the right move corresponds to the previous up move"
  [board]
  (vec
   (map (fn [index] (vec (map #(nth % index) (reverse board))))
        [0 1 2 3])))


(defn- tuple-conversion [a b]
  (cond   ;;; TODO/FIXME these rule are overkill. They can be simplified…
    ;;; same symbol on both → transmutation
    (and (= a b) (not (nil? a))) [(get next-cell a) nil]
    ;;; second cell empty → no movement
    (nil? b) [a nil]
    ;;; first empty
    (nil? a) [b nil]
    ;;; different symbols → no movement
    (not= a b) [a b]
    ;;; fail early in case of unexpected results
    true (throw (RuntimeException. (format "Unexpected result [%s %s]" a b)))))

(defn- reduce-tuple [{result :result seed :seed} value]
  (let [[next-a next-b] (tuple-conversion seed value)]
    {:result (conj result next-a)
     :seed next-b}))

(defn- step-row [row]
  (let [{result :result, seed :seed} (reduce reduce-tuple
                                             {:result []
                                              :seed (first row)}
                                             (rest row))]
    (conj (vec result) seed)))

(defn step
  "Performs a left move on the board, stopping before the new element addition"
  [board]
  [(step-row (nth board 0))
   (step-row (nth board 1))
   (step-row (nth board 2))
   (step-row (nth board 3))])

(defn- apply-rotated [board direction f] 
  (case direction
    :down (-> board rotate f rotate rotate rotate)
    :up (-> board rotate rotate rotate f rotate)
    :left (f board)
    :right (-> board rotate rotate f rotate rotate)))

(defn move
  "Perform the specified move, which might have no result."
  [board direction]
  (apply-rotated board direction step))

(defn can-move?
  "Check whether the board can be moved in the specific direction.

Easiest (and dumbest) way is to try to perform the move and see if anything changed.

Good enough performance wise, at least for now."
  [board direction]
  (not (= board (move board direction))))

(defn- board-indexed-row-ends [board]
  (map #(vector % %2) [0 1 2 3 4] (map #(nth % 3) board)))

(defn left-insertion-indices
  "Available row indices that have a space for insertion at the end"
  [board]
  (map first (filter #(nil? (second %)) (board-indexed-row-ends board))))

(defn insertion-indices
  "Available indices for insertion in the specified direction.

The indices follow the clockwise convention"
  [board direction]
  (apply-rotated board direction left-insertion-indices))
