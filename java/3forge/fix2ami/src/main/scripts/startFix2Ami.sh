#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

if [ -f ${HOME}/f1license.txt ]; then
  F1_LICENSE_FILE=${HOME}/f1license.txt
  export F1_LICENSE_FILE
fi

cd "$APPDIR/.."

CP=./resources
for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log


if [ -f $HOME/.bash_profile ];then
  . $HOME/.bash_profile
elif [ -f $HOME/.profile ];then
  . $HOME/.profile
fi

APPCLASS=com.f1.fix2ami.Fix2AmiMain

if [ "${JDEBUG_PORT}" ];then 
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi


header=`date;java -version 2>&1`
java -classpath $CP com.f1.speedlogger.impl.RoleFilesMain 10 log/stdout.log log/stderr.log log/gc.log


echo $header > log/stdout.log
echo $header > log/stderr.log

java  -Df1.license.mode=dev\
      -Df1.license.file=${HOME}/f1license.txt,f1license.txt,config/f1license.txt \
      -Df1.license.property.file=config/local.properties \
      -Dproperty.f1.conf.dir=config/fix2ami \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dfile.encoding=UTF-8\
      -Dproperty.f1.logging.mode=\
      -Dproperty.f1.autocoded.disabled=true\
      -Dhttps.protocols=TLSv1.2\
      -Xmx29g -XX:+UseConcMarkSweepGC \
      -XX:+PrintGCDetails -XX:+PrintGCTimeStamps \
      -Xloggc:log/gc.log \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS >> log/stdout.log 2> log/stderr.log &

echo -n "Starting 3Forge AMI2FIX"

