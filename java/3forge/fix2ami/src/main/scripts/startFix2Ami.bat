@echo off

set APPCLASS=com.f1.fix2ami.Fix2AmiMain

SETLOCAL ENABLEDELAYEDEXPANSION
cd ..
set CP=./resources

if not exist log mkdir log

for /f %%i in ('dir /S /B "*.jar"') do (SET CP=!CP!;%%i)
javaw -Df1.license.mode=dev ^
     -Df1.license.file=C:/f1license.txt,f1license.txt,config/f1license.txt ^
     -Df1.license.property.file=config/local.properties ^
     -Dproperty.f1.ide.mode=false ^
     -Dproperty.f1.conf.dir=config/fix2ami ^
     -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager ^
     -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager ^
     -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class ^
     -Dproperty.f1.autocoded.disabled=true ^          -Dhttps.protocols=TLSv1.2 ^
     -Xmx29g -XX:+UseConcMarkSweepGC ^
     -classpath %CP% %APPCLASS% 1> log/stdout.log 2> log/stderr.log 

cd scripts
