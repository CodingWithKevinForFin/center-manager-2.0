#!/bin/bash

cd `dirname $0`;

if [ -f ../.amicentermain.prc ]; then mv ../.amicentermain.prc ../.amicentermain.prc.kill;fi


exit 1
