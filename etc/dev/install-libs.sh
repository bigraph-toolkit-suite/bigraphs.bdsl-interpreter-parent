#!/bin/bash

current=$(pwd)
cd /home/dominik/eclipse-projects/bdsl/de.tudresden.inf.st.bigraphs.dsl.parent/
#-PwithTests
./gradlew generateXtext
cp ./de.tudresden.inf.st.bigraphs.dsl/build/libs/de.tudresden.inf.st.bigraphs.dsl-1.0.0-SNAPSHOT.jar $current/../libs
#cp ./de.tudresden.inf.st.bigraphs.dsl.tests/build/libs/de.tudresden.inf.st.bigraphs.dsl.tests-1.0.0-SNAPSHOT.jar $current/libs
cd $current/../../
mvn package -DskipTests -PuseLocalLib
