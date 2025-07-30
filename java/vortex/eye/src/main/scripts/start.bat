@echo off

set LAYOUTS_PATH=vortex_demos/layouts

set APPCLASS=com.vortex.eye.VortexEyeMain

SETLOCAL ENABLEDELAYEDEXPANSION
cd ..
set CP=.

mkdir log

for /f %%i in ('dir /S /B "*.jar"') do (SET CP=!CP!;%%i)
javaw -Df1.license.mode=dev ^
     -Dproperty.f1.ide.mode=false ^
     -Dproperty.f1.conf.dir=config/ ^
     -Djava.util.logging.manager=com.f1.speedlogger.sun.SunSpeedLoggerLogManager ^
     -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager ^
     -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class ^
     -classpath %CP% %APPCLASS% 1> log/stdout.log 2> log/stderr.log 
