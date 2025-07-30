APPCLASS=com.f1.mktdatasim.MktDataSimMain

cd `dirname $0`;APPDIR=`pwd`;cd -

cd "$APPDIR/.."

for i in `find lib -type f -name '*.jar'`;do
  if [ "$CP" == "" ];then CP=$i;else CP=$CP:$i;fi
done

java -Df1.license.mode=dev \
     -Dproperty.f1.ide.mode=false \
     -Dproperty.f1.conf.dir=config/ \
     -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager \
     $* -classpath $CP $APPCLASS

