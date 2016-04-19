#!/bin/bash
#
# This script is used to start the server from a supplied config file
#

# setup the server's home relative to this directory
export RAFT_HOME="$( cd "$( dirname "${BASH_SOURCE[0]}" )/.." && pwd )"
echo "** starting server from ${RAFT_HOME} **"

echo Raft home = $RAFT_HOME
#exit

#cd ${POKE_HOME}

JAVA_MAIN='src.raft.comm.Server'
JAVA_ARGS="$1"
#echo -e "\n** config: ${JAVA_ARGS} **\n"

# see http://java.sun.com/performance/reference/whitepapers/tuning.html
JAVA_TUNE='-Xms500m -Xmx1000m'


java ${JAVA_TUNE} -cp .:${RAFT_HOME}/libs/'*':${RAFT_HOME}/bin ${JAVA_MAIN} ${JAVA_ARGS}
