#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

if [ -f ${HOME}/f1license.txt ]; then
  F1_LICENSE_FILE=${HOME}/f1license.txt
  export F1_LICENSE_FILE
fi

cd "$APPDIR/.."

CP=./resources
for i in `find -L lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done
if [ "${CLASSPATH}" ]; then
  CP=$CP:$CLASSPATH;
fi

mkdir -p log resources

OPTIONS_FILE=AMI_One_linux.vmoptions


java -version 2>&1 | head -1 | egrep -q '\"1[1-9]\.|\"1[1-9]\"|\"2[0-9]\.|\"2[0-9]\"'
if [ $? -eq 0 ]; then
  ISJ17=1;
else
  ISJ17=
fi


if [ -f ${OPTIONS_FILE} ];then
  JAVA_OPTIONS="$JAVA_OPTIONS `envsubst < ${OPTIONS_FILE}`";
else
  if [ $ISJ17 ]; then
    DEF="-Dhttps.protocols=TLSv1.2\n-Xmx29g\n-Xlog:gc:log/gc.log:time,uptime,level,tags,tid"
  else
    DEF="-Dhttps.protocols=TLSv1.2\n-Xmx29g\n-XX:+UseConcMarkSweepGC\n-XX:+PrintGCDetails\n-XX:+PrintGCTimeStamps\n-Xloggc:log/gc.log"
  fi
  echo -e $DEF >> ${OPTIONS_FILE}
  JAVA_OPTIONS="$JAVA_OPTIONS `echo -e $DEF`";
fi;

JAVA_OPTIONS=" -Df1.license.mode=dev\
      -Df1.license.file=${HOME}/f1license.txt,f1license.txt,config/f1license.txt \
      -Df1.license.property.file=config/local.properties \
      -Dproperty.f1.conf.dir=config/ \
      -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
      -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager \
      -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class \
      -Dfile.encoding=UTF-8\
      -Dproperty.f1.logging.mode=\
	  -Djava.security.properties=config/3fsecurity.properties \
      -Djava.security.debug=properties \
      -XX:-OmitStackTraceInFastThrow \
      $JAVA_OPTIONS $*"


if [ -f $HOME/.bash_profile ];then
  . $HOME/.bash_profile
elif [ -f $HOME/.profile ];then
  . $HOME/.profile
fi

cd "$APPDIR/.."

APPCLASS=com.f1.ami.one.AmiOneMain

if [ "${JDEBUG_PORT}" ];then
  JAVA_OPTIONS="$JAVA_OPTIONS -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=${JDEBUG_PORT},server=y,suspend=y"
  echo "### JDBUG_PORT option set so waiting for java debugger to attach on `hostname`:$JDEBUG_PORT ###"
fi

if [ $ISJ17 ]; then
  JAVA_OPTIONS="$JAVA_OPTIONS --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/java.text=ALL-UNNAMED --add-opens java.base/sun.net=ALL-UNNAMED --add-opens java.management/sun.management=ALL-UNNAMED --add-opens java.base/sun.security.action=ALL-UNNAMED --add-opens java.desktop/com.sun.imageio.plugins.png=ALL-UNNAMED"
fi

if [ "${AMI_TERMINATE_FILE}" ];then
  JAVA_OPTIONS="$JAVA_OPTIONS -Dproperty.f1.terminate.file=${AMI_TERMINATE_FILE}"
fi


java -classpath $CP com.f1.speedlogger.impl.RoleFilesMain 10 log/stdout.log log/stderr.log log/gc.log


echo "TIMESTAMP: `date`" > log/stdout.log
echo "JVM_VERSION: `java -version 2>&1`" >> log/stdout.log
echo "JVM_OPTIONS: $JAVA_OPTIONS" >> log/stdout.log
echo "JVM_CLASSPATH: $CP" >> log/stdout.log
echo "JVM_MAINCLASS: $APPCLASS" >> log/stdout.log
cp log/stdout.log log/stderr.log

java  ${JAVA_OPTIONS} $* -classpath $CP $APPCLASS >> log/stdout.log 2> log/stderr.log &

echo -n "Starting 3Forge AMI ONE"

count=0
while [ 1 ]; do
  sleep 1
  echo -n '.'
  grep -q 'Startup complete in' log/stdout.log; if [ $? -eq 0 ];then echo;break;fi
  count=$(($count+1))
  if [ $count -eq 60 ]; then echo -n "[Startup is taking longer than usual]";fi
done

grep 'Startup complete in' log/stdout.log;
echo "Visit us at http://3forge.com"
echo
echo "Logging: `pwd`/log"
echo "Scripts: `pwd`/scripts"
echo "Configs: `pwd`/config"
echo

grep '^To access ' log/stdout.log
if [ $? -eq 0 ];then echo "Default Username: demo"; echo "Default Password: demo123"; fi
