#!/usr/bin/env bash
#
# set -x -v
export SCRIPTDIR=$(dirname $(readlink --canonicalize $0))

export OPENSEARCH=$(docker inspect opensearch-node|jq -r '.[].NetworkSettings.Networks.bridge.IPAddress')
export OSPASSWORD="$(docker inspect opensearch-node|jq -r '.[].Config.Env[]'|grep -E '^OPENSEARCH_INITIAL_ADMIN_PASSWORD='|cut -f2 -d=)"

export mntpoint="$(docker  2>/dev/null volume inspect groovy-grapes-cache|jq -r '.[0].Mountpoint')"


if [ \( -z "${mntpoint}" \) -o \( "${mntpoint}" == "null" \) ] ; then
  docker volume create --name groovy-grapes-cache
fi
if [ \( -n "${mntpoint}" \) -a \( "${mntpoint}" != "null" \) ] ; then
  echo "admin:${OSPASSWORD}"
  docker run --rm \
             -e OSURL="https://${OPENSEARCH}:9200" \
             -e OSCRD="admin:${OSPASSWORD}" \
	     -v "${SCRIPTDIR}/groovy/scripts":/home/groovy/scripts \
	     -v "${SCRIPTDIR}/groovy/configs":/home/groovy/configs \
	     -v grapes-cache:/home/groovy/.groovy/grapes \
	     -w /home/groovy/scripts \
	groovy:jdk21-alpine \
	groovy OSDEMOClass.groovy $*
fi
