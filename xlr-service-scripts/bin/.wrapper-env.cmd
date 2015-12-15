cd /d "%xldeploy_home%"

rem Checking for administrative permissions
net session >nul 2>&1
if %errorLevel% neq 0 (
    echo This script must be run as administrator to work properly!
    exit /b %errorLevel%
)

rem default java home
set wrapper_home=%xldeploy_home%\serviceWrapper

rem default java exe for running the wrapper
if "%JAVA_HOME%"=="" (
  set java_exe=java.exe
) else (
  set java_exe="%JAVA_HOME%\bin\java.exe"
)

rem location of the wrapper jar file. necessary lib files will be loaded by this jar. they must be at <wrapper_home>/lib/...
set wrapper_jar="%wrapper_home%/wrapper.jar"

rem setting java options for wrapper process. depending on the scripts used, the wrapper may require more memory.
set wrapper_java_options=-Xmx30m -Djna_tmpdir="%wrapper_home%/tmp" -Djava.net.preferIPv4Stack=true

rem configuration file used by all bat files
set conf_file="%xldeploy_home%/conf/xld-wrapper-win.conf"

set user=""
