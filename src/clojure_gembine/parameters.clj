(ns clojure-gembine.parameters
  (require [clojure-gembine.utils :refer [read-template]]))

(def title-area [811 202 1105 276])
(defonce gembine-title (read-template "gembine-title.png"))

(def message-area [1330 550 1490 700])
(defonce game-over (read-template "game-over.png"))
(defonce no-message (read-template "no-message.png"))

(def board-area [758 420 1163 825])
(defonce void-board (read-template "void-board.png"))
(defonce red-bead (read-template "red-bead.png"))
(defonce red-ball (read-template "red-ball.png"))
(defonce red-triangle (read-template "red-triangle.png"))
(defonce red-square (read-template "red-square.png"))
(defonce red-pentagon (read-template "red-pentagon.png"))
(defonce green-bead (read-template "green-bead.png"))
(defonce green-ball (read-template "green-ball.png"))
(defonce green-triangle (read-template "green-triangle.png"))
(defonce green-square (read-template "green-square.png"))
(defonce green-pentagon (read-template "green-pentagon.png"))

(defn- board-slice [n a b]
  (let [cell-size (int (/ (- b a) 4))
        start (+ a (* n cell-size))]
    [start cell-size]))

(defn get-board-cell-coordinates [column row]
  (let [[x1 y1 x2 y2] board-area
        [xr1 width] (board-slice column x1 x2)
        [yr1 height] (board-slice row y1 y2)]
    [xr1 yr1 width height]))

(defn get-board-cell [screen-bufimage column row]
  (let [[x y w h] (get-board-cell-coordinates column row)]
    (.getSubimage screen-bufimage x y w h)))

(defn get-screen-section [screen-bufimage [x1 y1 x2 y2]]
  (.getSubimage screen-bufimage x1 y1 (- x2 x1) (- y2 y1)))
