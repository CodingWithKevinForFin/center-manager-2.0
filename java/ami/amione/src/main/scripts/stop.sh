#!/bin/bash

cd `dirname $0`;

F=../.amionemain.prc

if [ "${AMI_TERMINATE_FILE}" ];then
  F=${AMI_TERMINATE_FILE}
fi

if [ -f "$F"  ]; then mv $F ${F}.kill;fi


exit 1
