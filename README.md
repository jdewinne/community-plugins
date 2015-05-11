# IMPORTANT INFORMATION #

The new community plugins are hosted on https://github.com/xebialabs-community. Not all plugins have been migrated yet, so if you want to make some changes to a plugin within this repo, please migrate the plugin first. Information on how to do this can be found at: http://xebialabs-community.github.io/

# XL Community Plugins - old structure #

Welcome to the XL community plugin repository on GitHub!

Here you will find plugins that can be used to add capabilities to your existing [XL product](http://www.xebialabs.com/tour) installation. The source code
for the plugins is also provided if you are curious to see how a plugin works or want to improve it.

_Note: Community plugins are provided on an as-is basis as a service to XL Deploy users. These plugins are not covered by the XebiaLabs support policy. If you need help with a community plugin, you can post on the XL Deploy user forum or contact XebiaLabs Support to request assistance from the Professional Services department._

# Finding a plugin

All plugins are located in directories in the main `community-plugins` repository. Browse the main directory to find the list of plugins and navigate to a plugin directory to find the plugin's documentation and source code.

## deployit-udm-plugins

This directory contains XL Deploy plugins that are based on the Unified Deployment Model (UDM).

## deployit-server-plugins

This directory contains XL Deploy server plugins that influence server behavior.

## deployit-cli-plugins

This directory contains XL Deploy command-line interface (CLI) plugins that influence CLI behavior.

## tool-deployit-plugins

This directory contains plugins for external tools that interact with XL Deploy.

## example-plugins

This directory contains example plugins that can be used for tutorials and as a starting-off point for new plugins.

# Downloading a plugin

You can find compiled, ready-to-install binaries in the [_Downloads_](https://dist.xebialabs.com/public/community/) location.
If you want, you can clone or fork the repository and compile the plugin from the source code.

# Installing a plugin

Installing a plugin is as simple as copying the plugin jar file into the _plugins_ directory in your XL Deploy server installation directory. See the XL Deploy *System Administration Manual* for more information.

# Building community plugins

To build the plugins in the community plugins repository:
 
1.  Check out the community repository at [https://github.com/xebialabs/community-plugins](https://github.com/xebialabs/community-plugins).
2.  Ensure that Java 1.7 and Maven are installed. 
3.  Ensure that Maven is using Java 1.7 (otherwise, there may be issues when building). 
4.  Export an environment variable called `DEPLOYIT_HOME` and point it to the XL Deploy home directory on your machine.
5.  Export an environment variable called `DEPLOYIT_CLI_HOME` and point it to the XL Deploy CLI home directory on your machine.
6.  Ensure that the WebLogic plugin is present in `DEPLOYIT_HOME/plugins` because a Liferay plugin requires it. Alternatively, you can skip building that plugin. 
7.  Run `mvn package` or `mvn clean install` to build the plugins. 

You will find the built plugins under each one's target folder.

**Note:** If you are using OS X, you can use the following commands for step #3. To change the Java version: 

    export JAVA_HOME=\`/usr/libexec/java_home -v 1.7\`
    
To change the Maven Java version:

    echo JAVA_HOME=\`/usr/libexec/java_home -v 1.7\` | sudo tee -a /etc/mavenrc

# Contributing to a plugin

If you want to add features to a plugin, fix a bug, or otherwise contribute, great! Fork the `community-plugins` repository, make your changes, test them, and submit a pull request to us so we can incorporate your changes and make them available
to other XL Deploy users. 

# Discussing plugins

The [XL Deploy user forum](http://support.xebialabs.com/forums/20273366-deployit-users) is a place where you can discuss these community plugins, as well as other XL Deploy installation and configuration topics, with fellow XL Deploy users.

# Enjoy!

We hope the plugins provided here will help you make the most of XL Deploy. Please let us know what you think by posting a message on our support site at [http://support.xebialabs.com](http://support.xebialabs.com).

Sincerely,

XebiaLabs
