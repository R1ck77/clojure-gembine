#!/bin/bash

# compile and install the code with
#    cmake -DCMAKE_INSTALL_PREFIX:PATH=#{HOME}/local/opencv cmake -DBUILD_SHARED_LIBS=OFF ..
#    make -j 8 install
# and execute:
#    execstack -c ${HOME}/local/share/OpenCV/java/libopencv_java341.so 

VERSION=3.4.1
COMPVERSION=`echo ${VERSION} | sed s/[.]//g`
BASE_PATH=${HOME}/local/opencv/share/OpenCV/java
JAR_LOCATION=${BASE_PATH}/opencv-${COMPVERSION}.jar
SO_LOCATION=${BASE_PATH}/libopencv_java${COMPVERSION}.so
EXPECTED_NATIVE_JAR=opencv-native-${COMPVERSION}.jar

DEST=native/linux/x86_64
mkdir -p ${DEST}
cp ${SO_LOCATION} ${DEST}
jar -cMf ${EXPECTED_NATIVE_JAR} native
rm -rf native

mvn install:install-file -Dfile=${JAR_LOCATION} -DgroupId=opencv -DartifactId=opencv -Dversion=${VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=${EXPECTED_NATIVE_JAR} -DgroupId=opencv -DartifactId=opencv-native -Dversion=${VERSION} -Dpackaging=jar
rm ${EXPECTED_NATIVE_JAR}
