(defproject clojure-gembine "0.1.0-SNAPSHOT"
  :description "Exercise that automates gembine, a minigame from the excellent 10tons' Jydge"
  :license {:name "GNU General Public License version 3"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [commons-io/commons-io "2.6"]
                 [opencv/opencv "3.4.1"]
                 [opencv/opencv-native "3.4.1"]]
  :main ^:skip-aot clojure-gembine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)])
