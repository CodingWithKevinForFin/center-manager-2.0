#!/bin/bash

cd `dirname $0`;

if [ -f ../.tcartsim.prc ]; then mv ../.tcartsim.prc ../.tcartsim.prc.kill;fi


exit 1
