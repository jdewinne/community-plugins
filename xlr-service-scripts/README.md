# xlr-servicewrapper
Some wrapper script to support running XL Release as a service on Unix and Windows

Installation:

1.  copy the files in /bin directory to your <XL_RELEASE>/bin directory.
2.  copy the files in /conf directory to your <XL_RELEASE>/conf directory.
3.  copy the files in /serviceWrapper directory to your <XL_RELEASE> directory.

You can then run the following commands:

Install a service:  <XL_RELEASE>/bin/install-service (.sh or .cmd)
Uninstall a service:  <XL_RELEASE>/bin/uninstall-service (.sh or .cmd)
Start XL Release service:  start service xl-release
Stop XL Release service:  stop service xl-release
