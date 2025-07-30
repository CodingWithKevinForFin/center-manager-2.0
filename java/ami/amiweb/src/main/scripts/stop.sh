#!/bin/bash

cd `dirname $0`;

if [ -f ../.amiwebmain.prc ]; then mv ../.amiwebmain.prc ../.amiwebmain.prc.kill;fi


exit 1
