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

APPCLASS=com.f1.anvil.AnvilMain

if [ "${JDEBUG_PORT}" ];then 
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi

java  -Df1.license.mode=dev\
      -Dproperty.f1.conf.dir=config/ \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dproperty.f1.logging.mode=\
      -Xmx128g -XX:+UseConcMarkSweepGC \
      -XX:+HeapDumpOnOutOfMemoryError \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS > log/stdout.log 2> log/stderr.log &
       
echo -n "Starting 3Forge Anvil"

count=0
while [ 1 ]; do
  sleep 1
  echo -n '.'
  #grep -q 'To access this web server browse to' log/stdout.log; if [ $? -eq 0 ];then echo -n '.[SERVER STARTED].';sleep 1;echo -n '.';sleep 1;echo -n '.';sleep 1;echo '.';echo;count=60;fi
  grep -q 'Startup complete in' log/stdout.log; if [ $? -eq 0 ];then echo;break;fi
  egrep -i 'not found|Exception|Invalid|err|failed' log/stderr.log ./log/stdout.log; if [ $? -eq 0 ];then echo;echo "Startup failed. Check logs at `pwd`/log/ FOR DETAILS. FOR ASSISTENCE, Contact us at support@3forge.com"; echo;exit 1;fi
  count=$(($count+1))
  if [ $count -eq 60 ]; then echo;echo "Startup timed out. Check logs at `pwd`/log/ FOR DETAILS. Contact us at support@3forge.com"; echo;exit 1;fi
done

grep 'Startup complete in' log/stdout.log;
echo "Visit us at http://3forge.com"
echo
echo "Logging: `pwd`/log"
echo "Scripts: `pwd`/scripts"
echo "Configs: `pwd`/config"
echo

grep 'To access this web server browse to' log/stdout.log | sed 's/To access this web server browse to/Anvil Web Server/g';
if [ $? -eq 0 ];then echo "Default Username: demo"; echo "Default Password: demo123"; echo "";echo "Developer Username: dev"; echo "Developer Password: dev123"; fi
