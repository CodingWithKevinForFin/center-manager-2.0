#!/bin/bash


TARGET=$1
shift

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

cd "$APPDIR/.."

for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done


APPCLASS=com.f1.vortex.tester.TesterMain

java -classpath $CP:$TARGET $APPCLASS $*
