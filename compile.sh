#!/usr/bin/env bash

ARTIFACT=jaxws.cxf
MAINCLASS=sample.ws.client.SampleWsApplicationClient
VERSION=0.0.1-SNAPSHOT

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

rm -rf target
mkdir -p target/native-image

echo "Packaging $ARTIFACT with Maven"
mvn -ntp package -Pclient > target/native-image/output.txt

JAR="$ARTIFACT-$VERSION.war"
rm -f $ARTIFACT
echo "Unpacking $JAR"
cd target/native-image
jar -xvf ../$JAR >/dev/null 2>&1
cp -R META-INF WEB-INF/classes

LIBPATH=`find WEB-INF/lib | tr '\n' ':'`
CP=WEB-INF/classes:WEB-INF\lib\*:WEB-INF\lib-provided\*:$LIBPATH

GRAALVM_VERSION=`native-image --version`
echo "Compiling $ARTIFACT with $GRAALVM_VERSION"
 time native-image \
  -H:Name=$ARTIFACT \
  -Dspring.spel.ignore=true \
  -Dspring.native.remove-yaml-support=true \
   --initialize-at-build-time=org.apache.cxf.common.logging.Slf4jLogger \
  -cp $CP $MAINCLASS

if [[ -f $ARTIFACT ]]
then
  printf "${GREEN}SUCCESS${NC}\n"
  mv ./$ARTIFACT ..
  exit 0
else
  printf "${RED}FAILURE${NC}: an error occurred when compiling the native-image.\n"
  exit 1
fi
