#!/bin/bash

APPCLASS=com.f1.oms3f.start.oms.StartOms3fMain

cd `dirname $0`;

./stop.sh
sleep 1
./start.sh 
