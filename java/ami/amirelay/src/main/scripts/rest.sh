#!/bin/bash

cd `dirname $0`;APPDIR=`pwd`;

if [ $# -eq "1" ]; then
        DAEMON="$APPDIR/restart.sh $1"
else
        DAEMON=$APPDIR/restart.sh
fi

    echo -n "Executing $DAEMON"
    RUN=`cd / && $DAEMON  2>&1 &`

    if [ "$?" -eq 0 ]; then
        echo "  ....Done."
    else
        echo "  ...FAILED."
    fi
