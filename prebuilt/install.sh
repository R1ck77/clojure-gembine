#!/bin/bash

mvn install:install-file -Dfile=opencv-341.jar -DgroupId=opencv -DartifactId=opencv -Dversion=3.4.1 -Dpackaging=jar
mvn install:install-file -Dfile=opencv-native-341.jar -DgroupId=opencv -DartifactId=opencv-native -Dversion=3.4.1 -Dpackaging=jar
