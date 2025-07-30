#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;cd - > /dev/null

cd "$APPDIR/.."

java  -classpath $APPDIR/../lib/out.jar com.f1.ami.amicommon.AmiTools "$@" 
