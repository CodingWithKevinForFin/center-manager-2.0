#!/bin/bash

APPCLASS=com.sjls.f1.start.ofradapter.StartOFRMain

cd `dirname $0`;

./stop.sh
sleep 1
./start.sh 
