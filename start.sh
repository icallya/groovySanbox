
#!/usr/bin/env bash
export mntpoint="$(docker  2>/dev/null volume inspect groovy-grapes-cache|jq -r '.[0].Mountpoint')"
if [ \( -z "${mntpoint}" \) -o \( "${mntpoint}" == "null" \) ] ; then
  docker volume create --name groovy-grapes-cache
fi
if [ \( -n "${mntpoint}" \) -a \( "${mntpoint}" != "null" \) ] ; then
  docker run --rm \
	     -v "${HOME}/groovy/scripts":/home/groovy/scripts \
	     -v grapes-cache:/home/groovy/.groovy/grapes \
	     -w /home/groovy/scripts \
	groovy:jdk21-alpine \
	groovy DemoClass.groovy $*
fi
