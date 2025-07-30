#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null
cd "$APPDIR/.."

if [ "$ANVIL_BASE" = "" ]; then ANVIL_BASE=.;fi

CP=$CP:.
for i in `find ${ANVIL_BASE}/lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

APPCLASS=com.f1.anvil.Fix2Anvil

java -classpath $CP $APPCLASS $*