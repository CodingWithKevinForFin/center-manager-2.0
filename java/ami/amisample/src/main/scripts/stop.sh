#!/bin/bash

cd `dirname $0`;

if [ -f ../.amisample.prc ]; then mv ../.amisample.prc ../.amisample.prc.kill;fi


exit 1
