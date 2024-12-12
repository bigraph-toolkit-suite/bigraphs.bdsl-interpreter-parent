#!/bin/bash

VERSION="1.0.0-SNAPSHOT"
DIR="/opt/bdsl"
BDSLTOOL="bdsl-$VERSION.jar"
BDSLTOOLPATH="../bdsl-interpreter-cli/target/$BDSLTOOL"

if [ $(id -u) != "0" ]; then
  echo -e "\e[31mPlease run the installer script with sudo.\e[39m"
  exit 0
fi

echo -e "\e[94mInstalling Launcher Script for BDSL Interpreter\e[39m"
echo ""

if [ -f $DIR ]; then
  if ! [ -d "$DIR" ]; then
    echo -e "\e[31mThe path '$DIR' is not available as the install directory. Please fix first.\e[39m"
    exit -1
  fi
fi

if ! [ -f $BDSLTOOLPATH ]; then
  echo -e "\e[31mThe BDSL interpreter could not be found at: $BDSLTOOLPATH\e[39m"
  echo -e "\e[31mMaybe the interpreter was not built yet. Please run the build as specified in the README of this project first.\e[39m"
  exit -1
fi

echo ""
echo -e "\e[94mInstalling to $DIR\e[39m"
echo ""

if mkdir -p ${DIR}; then
  cp $BDSLTOOLPATH $DIR
fi

chmod -R +rwx ${DIR}
if [ -f ${DIR}/bdsl.sh ]; then
  rm -f ${DIR}/bdsl.sh
fi
touch ${DIR}/bdsl.sh
echo "#!/bin/bash" >>$DIR/bdsl.sh
echo "java -jar ${DIR}/$BDSLTOOL" >>$DIR/bdsl.sh

if [ -f /usr/local/bin/bdsl ]; then
  rm -f /usr/local/bin/bdsl
fi
ln -s ${DIR}/bdsl.sh /usr/local/bin/bdsl
chmod -R +rwx /usr/local/bin/bdsl

echo -e "\e[32mDone.\e[39m"
