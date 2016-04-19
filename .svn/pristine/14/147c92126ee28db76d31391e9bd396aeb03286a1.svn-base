#!/bin/bash
#
# This script is used to start the server from a supplied config file
#

# setup the server's home relative to this directory
export RAFT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
echo "** running client from ${RAFT_HOME} **"

echo Raft home = $RAFT_HOME
#exit

#cd ${POKE_HOME}

JAVA_MAIN='src.raft.comm.Client'
JAVA_ARGS1="$1"
JAVA_ARGS2="$2"
JAVA_ARGS3="$3"
JAVA_ARGS4="$4"
JAVA_ARGS5="$5"
#echo -e "\n** config: ${JAVA_ARGS} **\n"

# see http://java.sun.com/performance/reference/whitepapers/tuning.html
JAVA_TUNE='-Xms500m -Xmx1000m'


java ${JAVA_TUNE} -cp .:${RAFT_HOME}/libs/'*':${RAFT_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS1} ${JAVA_ARGS2} ${JAVA_ARGS3} ${JAVA_ARGS4} ${JAVA_ARGS5}
