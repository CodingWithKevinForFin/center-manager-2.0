#!/bin/bash


APPCLASS=app.controlpanel.F1Main

cd `dirname $0`;

./stop.sh
sleep 1
./start.sh 
