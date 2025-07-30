#!/bin/bash

cd `dirname $0`;

if [ -f ../config/.fix2amimain.prc ]; then mv ../config/.fix2amimain.prc ../config/.fix2amimain.prc.kill;fi


exit 1
