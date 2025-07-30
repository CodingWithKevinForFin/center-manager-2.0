#!/bin/bash

cd `dirname $0`;

if [ -f ../.taploader.prc ]; then mv ../.taploader.prc ../.taploader.prc.kill;fi


exit 1
