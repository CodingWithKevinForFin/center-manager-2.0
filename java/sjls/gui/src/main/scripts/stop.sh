#!/bin/bash


APPCLASS=app.controlpanel.F1Main

cd `dirname $0`;

declare -a UPPIDS ; UPPIDS=$(grep UP ../.f1proc.txt | sort -n )

OIFS=$IFS

for row in $UPPIDS; do
        IFS='|'
        set $row
        pid=$3
        ctime=$1
        IFS=$OIFS
        if [[  -d /proc/${pid}  ]];  then
                 cmd=`cat /proc/${pid}/cmdline `;
                 echo $cmd | grep -q ${APPCLASS}
                 if [ $? == 0  ]; then
                        ftime=$( ls -ld --time-style=+%s000 /proc/${pid}  )
                        set $ftime
                        ((ctime= $6-$ctime))
                        btime=$( echo "$ctime" | awk ' { if($1>=0) { print $1} else {print $1*-1 }}')
                        if [[ "$btime" -lt "60000" ]]; then
                                echo Killing Process: $pid, $cmd >> ../log/stderr.log
                                echo Killing Process: $pid, $cmd
                                kill $pid
                                exit 0;
                        fi
                fi
        fi
done
exit 1
