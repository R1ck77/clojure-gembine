#!/bin/bash

UBUNTU_JAR_LOCATION=/usr/share/java/opencv-249.jar
UBUNTU_SO_LOCATION=/usr/lib/jni/libopencv_java249.so
UBUNTU_JAR_VERSION=2.4.9
EXPECTED_NATIVE_JAR=opencv-native-249.jar

DEST=native/linux/x86_64
mkdir -p ${DEST}
cp ${UBUNTU_SO_LOCATION} ${DEST}
jar -cMf ${EXPECTED_NATIVE_JAR} native
rm -rf native

mvn install:install-file -Dfile=${UBUNTU_JAR_LOCATION} -DgroupId=opencv -DartifactId=opencv -Dversion=${UBUNTU_JAR_VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=${EXPECTED_NATIVE_JAR} -DgroupId=opencv -DartifactId=opencv-native -Dversion=${UBUNTU_JAR_VERSION} -Dpackaging=jar
rm ${EXPECTED_NATIVE_JAR}
