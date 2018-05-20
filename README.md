# clojure-gembine

Solve with automation the minigame "Gembine" from 10tons' excellent JYDGE.

## Installation

Clone the repository and make sure to have leiningen properly installed.

Compile and install OpenCV version 3.4.1 in a way leiningen will like. I have created a small script that sort of helps in automating the task: once you have compiled and installed opencv with something like:

    $ cmake -DCMAKE_INSTALL_PREFIX:PATH=${HOME}/local/opencv cmake -DBUILD_SHARED_LIBS=OFF ..
    $ make -j 8 install 
    
Then you should be able to run ``prebuilt/install.sh`` and be ready to ``lein repl``.

If you installed OpenCV somewhere else and/or the version is not just the same (that is, you have version 3.4.4: don't expect anything to work if you have, like, OpenCV 4.0.0…), you can still _probably_ make the software work by modifying the constants in ``install.sh`` a bit. 

If the installation is correct, running ``lein run`` in the main project directory should start the program.

## Usage

To make effective use of this code, you need to have a full-screen GEMBINE game already running in background, and it must *not be in tutorial mode*, which means you have to enter the GEMBINE minigame and restart it manually.

This program will also *not work* on multi-monitor setups!

Without closing JYDGE but just putting it in background, run

    $ java -jar clojure-automate-0.1.0-standalone.jar [args]

It will start a countdown of 10 seconds. *You must to switch to GEMBINE and make sure the game windows has focus* before said countdown expires.

### Bugs

This program has been developed with a setup with a single Full HD monitor in mind. I have added a semi-decent rescaling feature that should make the game work to an extent on other monitor ratios, but that part of the code has not been tested extensively, so use other ratios at your own risk, but it will still fail miserably on a multi-monitor rig.

The solvers are under-optimized, and will bend old hardware, although they should work (albeit slowly) pretty much everywhere.

## License

Copyright © 2018 Riccardo Di Meo

Distributed under the GNU General Public License v3.

This work is intended as an educational program, a funny hack, and a tribute to a very nice top down shooter.

I'm not affiliated in any way with 10tons or their associates: JYDGE and GEMBINE, along with any trademarks are the properties of their respective owners.

