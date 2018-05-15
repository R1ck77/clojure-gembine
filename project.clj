(defproject clojure-gembine "0.1.0-SNAPSHOT"
  :description "Exercise to solve Jydge's Gembine with automation"
  :license {:name "GNU General public license v3.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [commons-io/commons-io "2.6"]
                 [opencv/opencv "3.4.1"]
                 [opencv/opencv-native "3.4.1"]]
  :main ^:skip-aot clojure-gembine.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}}
  :injections [(clojure.lang.RT/loadLibrary org.opencv.core.Core/NATIVE_LIBRARY_NAME)])
