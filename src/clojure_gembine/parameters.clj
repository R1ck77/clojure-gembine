(ns clojure-gembine.parameters
  (require [clojure-gembine.utils :refer [read-template load-opencv-libraries]]))

(load-opencv-libraries)

(def gembine-banner-area [811 202 1105 276])
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
(defonce green-pentagon (read-template "green-pentagon.png"))

