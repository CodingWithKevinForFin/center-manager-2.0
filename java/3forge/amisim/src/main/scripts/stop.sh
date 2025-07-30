#!/bin/bash

cd `dirname $0`;

if [ -f ../.amiweb.prc ]; then mv ../.amiweb.prc ../.amiweb.prc.kill;fi


exit 1
