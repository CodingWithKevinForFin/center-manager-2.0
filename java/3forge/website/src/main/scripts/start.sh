#!/bin/bash

F1_LICENSE_FILE=${HOME}/f1license.txt
export F1_LICENSE_FILE

cd `dirname $0`;APPDIR=`pwd`;cd -

cd "$APPDIR/.."

for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

mkdir -p log

APPCLASS=com.f1.website.ThreeForgeWebSite

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
      -Dhttps.protocols=TLSv1.2,TLSv1.3\
      -Djava.security.properties=config/3fsecurity.properties \
      -Djava.security.debug=properties \
      -Xmx12g \
      -Xms12g \
       ${JAVA_OPTIONS}\
       $* -classpath $CP $APPCLASS > log/stdout.log 2> log/stderr.log &
       







