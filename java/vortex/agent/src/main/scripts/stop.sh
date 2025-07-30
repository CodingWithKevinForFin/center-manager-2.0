#!/bin/bash


APPCLASS=com.vortex.agent.VortexAgentMain
cd `dirname $0`;




kill_process()
{
        echo Killing Process: $1, $2>> ../log/process.log
        echo Killing Process: $1, $2
        kill $1
}

OIFS=$IFS

if [ $# -eq "1" ]; then
        PROCESSPID=${1}
        kill_process $PROCESSPID  "Pid passed in as an argument"
        exit 0
else

declare -a UPPIDS ; UPPIDS=$(grep '|UP' ../.f1proc.txt | sort -nr  | awk -F'|' '{ print $3 }')
declare -a DOWNPIDS ; DOWNPIDS=$(grep '|DN' ../.f1proc.txt | sort -nr |   awk -F'|' '{ print $3 }' )
declare -a UNMATCHEDPIDS;
for row in $UPPIDS; do
        match=0;
        for dnrow in $DOWNPIDS;do
                if [ $row -eq $dnrow ]; then
                        match=1;
                break;
                fi
        done
        if [ $match == 0 ]; then
                UNMATCHEDPIDS=(${UNMATCHEDPIDS[@]} $row);
        fi

done

for pid in $UNMATCHEDPIDS; do
        echo trying $pid
        if [[  -d /proc/${pid}  ]];  then
        	if [[ -e /proc/${pid}/cmdline ]]; then
                	cmd=`cat /proc/${pid}/cmdline `;
                	echo $cmd | grep -q ${APPCLASS}
                	echo $cmd
                	if [ $? == 0  ]; then
                	        kill_process $pid $cmd
                	        exit 0;
                	fi
                else
                	 cmd=`pargs ${pid}`;
		         echo $cmd | grep -q ${APPCLASS}
		         if [ $? == 0  ]; then
		         	kill_process $pid $cmd
		         	exit 0;
		         fi
		fi         


        elif [[ ! -d "/proc" ]]; then
                cmd=`ps ${pid} | grep ${APPCLASS}`;
                if [[ ! -z $cmd ]]; then
                         kill_process $pid $cmd
                         exit 0;
                fi
        fi
done
fi

exit 1