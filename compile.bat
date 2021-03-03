@echo off

SET ARTIFACT=jaxws.cxf
SET MAINCLASS=sample.ws.client.SampleWsApplicationClient
SET VERSION=0.0.1-SNAPSHOT

RMDIR /Q/S target
mkdir target\native-image

echo "Packaging $ARTIFACT with Maven"
call mvnw clean package -Pclient

echo "***************************** Setting JAR ************************************************"
SET JAR="%ARTIFACT%-%VERSION%.war"
rem RMDIR /Q/S %ARTIFACT%
echo "Unpacking %JAR%"
cd target/native-image
jar -xvf ../%JAR%

echo "***************************** Copy jars & classes *****************************************"
xcopy "META-INF" "WEB-INF/classes" /s /e

rem echo "***************************** Copy dump class laoders *****************************************"
rem xcopy "../../dump" "BOOT-INF/classes" /s /e

SET CP=WEB-INF\classes;WEB-INF\lib\*;WEB-INF\lib-provided\*

echo "***************************** CLASSPATH ***********************************************"
echo %CP%
echo "**************************************************************************************"


echo "MAINCLASS" %MAINCLASS%

call native-image ^
        --no-server ^
        --no-fallback ^
        --enable-all-security-services ^
        -H:Name=%ARTIFACT%-agent ^
        -H:TraceClassInitialization=true ^
        -Dspring.native.remove-yaml-support=true ^
        -Dspring.xml.ignore=false ^
        -Dspring.spel.ignore=true ^
        -Dspring.native.remove-jmx-support=true ^
        -Dspring.native.verify=true ^
        -cp %CP% %MAINCLASS%
