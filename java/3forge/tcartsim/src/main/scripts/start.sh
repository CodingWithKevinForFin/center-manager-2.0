#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

if [ -f ${HOME}/f1license.txt ]; then
  F1_LICENSE_FILE=${HOME}/f1license.txt
  export F1_LICENSE_FILE
fi

cd "$APPDIR/.."

for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log


if [ -f $HOME/.bash_profile ];then
  . $HOME/.bash_profile
elif [ -f $HOME/.profile ];then
  . $HOME/.profile
fi


APPCLASS=com.f1.tcartsim.TcartSimMain

if [ "${JDEBUG_PORT}" ];then 
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi

java  -Df1.license.mode=dev\
      -Dproperty.f1.conf.dir=config/ \
      -Dproperty.f1.terminate.file=./.tcartsim.prc \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dproperty.f1.logging.mode=\
      -Xmx29g -XX:+UseConcMarkSweepGC \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS > log/stdout.log 2> log/stderr.log &

echo "For details run:    cat log/stdout.log log/stderr.log"