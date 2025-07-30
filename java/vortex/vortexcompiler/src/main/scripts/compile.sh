#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

cd "$APPDIR/.."

for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log

APPCLASS=com.f1.vortex.compiler.PomCompiler

java -classpath $CP $APPCLASS  $*
exit $?
