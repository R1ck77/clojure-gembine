(ns clojure-automate.core
  (:import [java.awt Robot]
          [java.awt.event KeyEvent])
  (:gen-class))

(def move-delay-ms 400)

(def arrows {
             :up KeyEvent/VK_UP
             :down KeyEvent/VK_DOWN
             :left KeyEvent/VK_LEFT
             :right KeyEvent/VK_RIGHT
             })

(defn new-robot []
  (Robot.))

(defn tap-key
  "Do a quick tap on the keyboard"
  ([code]
   (tap-key (new-robot) code))
  ([robot code]
   (doto robot
     (.keyPress code)
     (.keyRelease code))))

(defn tap-character
  ([char]
   (tap-character (new-robot) char))
  ([robot char]
   (tap-key robot
            (KeyEvent/getExtendedKeyCodeForChar (int char)))))

(defn tap-arrow
  ([direction]
   (tap-arrow (new-robot) direction))
  ([robot direction]
   (tap-key robot (get arrows direction))))

(defn move
  ([direction]
   (move (new-robot) direction))
  ([robot direction]
   (tap-arrow robot direction)
   (Thread/sleep move-delay-ms)))


(defn test-random-moves [delay n]
  (Thread/sleep delay)
  (let [robot (new-robot)
        moves (keys arrows)]
    (repeatedly n #(move robot
                         (rand-nth moves)))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
