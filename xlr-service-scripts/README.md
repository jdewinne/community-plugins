# xlr-servicewrapper
Scripts and libraries needed to support running XL Release as a service on Unix and Windows

**Versions**:  tested on XL Release server versions 4.7.x

**Installation**:

1.  Download the  xlr-service-wrappers.zip file
2.  copy the files in **/bin** directory to your **<XL_RELEASE>/bin** directory.
3.  copy the files in **/conf** directory to your **<XL_RELEASE>/conf** directory.
4.  copy the entire **/serviceWrapper** directory to your **<XL_RELEASE>** home directory.

You can then run the following commands:

**Install a service**:  <XL_RELEASE>/bin/install-service (.sh or .cmd)

**Uninstall a service**:  <XL_RELEASE>/bin/uninstall-service (.sh or .cmd)

**Start XL Release service**:  start service xl-release

**Stop XL Release service**:  stop service xl-release

Note:  This approach is similar to [Installing XL Deploy as service, using YAJSW libraries](https://docs.xebialabs.com/xl-deploy/how-to/install-xl-deploy-as-a-service-4.5.html)
