#!/bin/sh
#
# Commons for service wrapper related scripts
#

if [ "$(id -u)" != "0" ]; then
   echo "This script must be run as root" 1>&2
   exit 1
fi

absdirname () {
  _dir="`dirname \"$1\"`"
  cd "$_dir"
  echo "`pwd`"
}

resolvelink() {
  _dir=`dirname "$1"`
  _dest=`readlink "$1"`
  case "$_dest" in
  /* ) echo "$_dest" ;;
  *  ) echo "$_dir/$_dest" ;;
  esac
}

# Get XL Release server home dir
if [ -z "$xlrelease_home" ] ; then
  self="$0"
  if [ -h "$self" ]; then
    self=`resolvelink "$self"`
  fi
  BIN_DIR=`absdirname "$self"`
  xlrelease_home=`dirname "$BIN_DIR"`
elif [ ! -d "$xldeploy_home" ] ; then
  echo "Directory $xldeploy_home does not exist"
  exit 1
fi
export xlrelease_home

cd "$xlrelease_home"

# path to wrapper home
wrapper_home="$xlrelease_home"/serviceWrapper
export wrapper_home

wrapper_jar="$wrapper_home"/wrapper.jar
export wrapper_jar

wrapper_java_sys_options=-Djna_tmpdir="$wrapper_home"/tmp
export wrapper_java_sys_options

wrapper_java_options="-Xmx30m"
export wrapper_java_options

wrapper_logging_file_options="-Djava.util.logging.config.file=$xlrelease_home/conf/logging.properties"
export wrapper_logging_file_options

# Get Java executable
if [ -z "$JAVA_HOME" ] ; then
  java_exe=java
else
  java_exe="${JAVA_HOME}/bin/java"
fi
export java_exe

conf_file="$xlrelease_home"/conf/xlr-wrapper-linux.conf
export conf_file
