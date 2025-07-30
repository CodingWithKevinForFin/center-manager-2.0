#!/bin/bash

if [ -f ${HOME}/f1license.txt ]; then
  F1_LICENSE_FILE=${HOME}/f1license.txt
  export F1_LICENSE_FILE
fi

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

cd "$APPDIR/.."

CP=./resources
for i in `find -L lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log

APPCLASS=com.f1.ami.web.AmiWebMain

if [ "${JDEBUG_PORT}" ];then 
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi


if [ -f ./config/prestart.sh ];then
  source ./config/prestart.sh
fi


GC_LOG=log/gc.log
ST_LOG=log/stdout.log
ER_LOG=log/stderr.log
for f in $GC_LOG $ST_LOG $ER_LOG; do 
  if [ -f  ${f}.0005 ];then rm ${f}.0005;fi; if [ -f  ${f}.0004 ];then mv ${f}.0004 ${f}.0005;fi; if [ -f  ${f}.0003 ];then mv ${f}.0003 ${f}.0004;fi; if [ -f  ${f}.0002 ];then mv ${f}.0002 ${f}.0003;fi; if [ -f  ${f}.0001 ];then mv ${f}.0001 ${f}.0002;fi; if [ -f ${f} ];then mv ${f} ${f}.0001;fi;
done

java  -Df1.license.mode=dev\
      -Dproperty.f1.conf.dir=config/ \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dfile.encoding=UTF-8\
      -verbose:gc -Xloggc:${GC_LOG} -XX:+PrintGCDateStamps -XX:+PrintGCDetails\
      -Dproperty.f1.logging.mode=\
      -Xmx29g -XX:+UseConcMarkSweepGC \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS > $ST_LOG 2> $ER_LOG &
