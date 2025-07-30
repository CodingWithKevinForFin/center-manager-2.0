#!/bin/bash

cd `dirname $0`;

if [ -f ../.anvilmain.prc ]; then mv ../.anvilmain.prc ../.anvilmain.prc.kill;fi


exit 1
