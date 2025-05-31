#!/usr/bin/env bash
#
# set -x -v
export SCRIPTDIR=$(dirname $(readlink --canonicalize $0))

export WEBADR=$(docker inspect alertmanager|jq -r '.[].NetworkSettings.Networks.bridge.IPAddress')

export mntpoint="$(docker  2>/dev/null volume inspect groovy-grapes-cache|jq -r '.[0].Mountpoint')"


if [ \( -z "${mntpoint}" \) -o \( "${mntpoint}" == "null" \) ] ; then
  docker volume create --name groovy-grapes-cache
fi
if [ \( -n "${mntpoint}" \) -a \( "${mntpoint}" != "null" \) ] ; then
  docker run --rm \
             -e WEBADR="http://${WEBADR}:9093" \
	     -v "${SCRIPTDIR}/groovy/scripts":/home/groovy/scripts \
	     -v "${SCRIPTDIR}/groovy/configs":/home/groovy/configs \
	     -v grapes-cache:/home/groovy/.groovy/grapes \
	     -w /home/groovy/scripts \
	groovy:jdk21-alpine \
	groovy MakeHttpRequest.groovy $*
fi
