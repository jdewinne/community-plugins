#!/bin/sh
#
# Shell script to install XL Release Server as a daemon
#

DIR=$( cd "$( dirname "$0" )" && pwd )
. "$DIR"/.wrapper-env.sh

user=""
export user

# stop and uninstall daemon/ service
"$java_exe" $wrapper_java_options "$wrapper_java_sys_options" -jar "$wrapper_jar" -p -r "$conf_file"
