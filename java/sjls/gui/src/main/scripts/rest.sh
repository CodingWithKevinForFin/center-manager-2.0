#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;

DAEMON=$APPDIR/restart.sh


    echo -n "starting up $DAEMON"
    RUN=`cd / && $DAEMON 2>&1 &`

    if [ "$?" -eq 0 ]; then
        echo "Done."
    else
        echo "FAILED."
    fi
