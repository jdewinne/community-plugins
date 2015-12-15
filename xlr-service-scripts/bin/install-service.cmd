@echo off
REM
REM Batch script to install XL Release Server as a service
REM

setlocal ENABLEDELAYEDEXPANSION

REM Get XL Release server home dir
if "%xlrelease_home%"=="" (
    cd /d "%~dp0"
    cd ..
    set xlrelease_home=!CD!
)

call "%xldeploy_home%\bin\.wrapper-env.cmd"

if %errorLevel% neq 0 ( exit /b %errorLevel% )

If NOT exist "%xlrelease_home%\conf\xl-release-server.conf" (
    echo The XL Deploy server is not initialized. Please execute the run.cmd script!
    exit /b 1
)

If NOT exist "%xlrelease_home%\conf\xl-release-license.lic" (
    echo A license is required in order to be able to install the XL Release Server as a service.
    exit /b 1
)

call %java_exe% %wrapper_java_options% -jar %wrapper_jar% -i -t %conf_file%

endlocal
