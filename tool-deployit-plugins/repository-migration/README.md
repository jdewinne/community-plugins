# Repository Migration 

# Overview #

This script allows to migrate the XL Deploy or XL Release repository from one configuration to another. The typical use case is to migrate from the default configuration which uses Apache Derby and filesystem to another backend, for example fully external database storage.

# Requirements #

* **XL Deploy requirements version 3.9 or higher**
* **XL Release 4.5.0 or higher** (may be working on other versions but was not tested)
	
# Installation #

1. Unzip the content of the distribution zip file (deployit-repository-migration-${version}-distribution.zip) in your deployit server installation.
    * the `bin` directory should include now 2 new files migrate.sh & migrate.cmd
    * the `plugins` directory should include now the repository-migration-${version}.jar
    * a new `sample` directory
2. If necessary, copy the jdbc drivers to the lib/ directory if your repository is plugged on a RDBMS (eg: ojdbc6.jar if your database is Oracle)

# Execution

1. Configure the new target repository structure `jackrabbit-repository.xml`. Do *not* modify or override the existing file. See [the documentation](https://docs.xebialabs.com/xl-deploy/how-to/configure-the-xl-deploy-repository.html).
2. Run the migration script:

	bin/migrate.sh -deployitHome <Deployit-Server-Home> -jackrabbit-config-file <Path-to-new-configuration-file> -repository-name <Name> -updateDeployitConfiguration

For example:

	bin/migrate.sh -deployitHome /opt/deployit/deployit-3.9.4-server -jackrabbit-config-file ./bin/jackrabbit-mysql-repository.xml -repository-name migration-to-mysql -updateDeployitConfiguration

Once the script has been successfully executed, you should have a new folder in the `SERVER_HOME/<Name>` directory having the <Name>. The `-updateDeployitConfiguration` flag updates the `deployit.conf` or `xl-release-server.conf` configuration file and copies the new jackrabbit configuration file to the `conf/` directory. Previous Jackrabbit configuration file is backed up.