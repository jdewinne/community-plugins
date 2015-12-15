@echo off
REM
REM Batch script to uninstall XL Release Server service
REM

setlocal ENABLEDELAYEDEXPANSION

REM Get XL Release server home dir
if "%xlrelease_home%"=="" (
    cd /d "%~dp0"
    cd ..
    set xldeploy_home=!CD!
)

call "%xlrelease_home%\bin\.wrapper-env.cmd"

if %errorLevel% neq 0 ( exit /b %errorLevel% )

call %java_exe% %wrapper_java_options% -jar %wrapper_jar% -p -r %conf_file%

endlocal
