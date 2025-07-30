#!/bin/bash

APPCLASS=com.f1.oms3f.start.oms.StartOms3fMain
cd `dirname $0`;

./stop.sh
rm -rf ../data/qfix
exit 0
