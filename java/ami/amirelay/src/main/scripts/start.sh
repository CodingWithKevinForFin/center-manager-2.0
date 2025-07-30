#!/bin/bash


cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

if [ "$1" == "Unlocked" ]; then
  unset F1_LICENSE_FILE
  shift
else
  F1_LICENSE_FILE=${HOME}/f1license.txt
fi
export  F1_LICENSE_FILE

cd "$APPDIR/.."

for i in `find -L lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log

APPCLASS=com.f1.ami.relay.AmiRelayMain  


if [ "${JDEBUG_PORT}" ];then 
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi

java  -Dproperty.f1.conf.dir=config/ \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dfile.encoding=UTF-8\
      -Dproperty.f1.logging.mode=\
      -Xmx1g -XX:+UseConcMarkSweepGC \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS > log/stdout.log 2> log/stderr.log &
