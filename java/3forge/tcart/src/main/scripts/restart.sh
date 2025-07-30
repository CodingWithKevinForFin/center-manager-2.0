#!/bin/bash

cd `dirname $0`;
if [ $# -eq "1" ]; then
./stop.sh $1
else
./stop.sh
fi


sleep 1
./start.sh 
