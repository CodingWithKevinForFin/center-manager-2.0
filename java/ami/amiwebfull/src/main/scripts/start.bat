@echo off

set LAYOUTS_PATH=ami_demos/layouts

set APPCLASS=com.f1.ami.web.AmiWebMain

SETLOCAL ENABLEDELAYEDEXPANSION
cd ..
set CP=./resources
if not exist log mkdir log
for /f %%i in ('dir /S /B "*.jar"') do (SET CP=!CP!;%%i)
javaw -Df1.license.mode=dev ^
     -Dproperty.users.access.file=../../%LAYOUTS_PATH%/access.txt -Dproperty.users.path=../../%LAYOUTS_PATH%/users ^
     -Dproperty.default.portletid=amidesktop ^
     -Dproperty.f1.ide.mode=false ^
     -Dproperty.f1.conf.dir=config/ ^
     -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager ^
     -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager ^
     -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class ^
     -Dhttps.protocols=TLSv1.2 ^
     -Xmx29g -XX:+UseConcMarkSweepGC ^
     -classpath %CP% %APPCLASS% 1> log/stdout.log 2> log/stderr.log 


cd scripts
