package com.xebialabs.deployit.tools;

import java.io.PrintStream;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

public class RepositoryMigrationOptions {

    @Option(name = "-deployitHome", required = true, usage = "The folder where XL Deploy or XL Release is installed.")
    private String deployitHome;

    @Option(name = "-jackrabbit-config-file", required = true, usage = "The new jackrabbit configuration.")
    private String jackrabbitConfigFile;

    @Option(name = "-repository-name", required = true, usage = "The name of the folder holding the new repository")
    private String repositoryName;

    @Option(name = "-updateDeployitConfiguration", usage = "Update the XL Deploy or XL Release configuration", required = false)
    private boolean updateDeployitConfiguration = false;

    public static RepositoryMigrationOptions parseCommandLine(String[] args) {
        RepositoryMigrationOptions options = new RepositoryMigrationOptions();
        final CmdLineParser parser = new CmdLineParser(options);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            printUsage(parser, System.err);
            return null;
        }
        return options;
    }


    public String getDeployitHome() {
        return deployitHome;
    }

    public String getJackrabbitConfigFile() {
        return jackrabbitConfigFile;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public boolean isUpdateDeployitConfiguration() {
        return updateDeployitConfiguration;
    }

    private static void printUsage(CmdLineParser parser, PrintStream stream) {
        stream.println("java -cp repository-migration->version>.jar com.xebialabs.deployit.tools.RepositoryMigration arguments...");
        parser.printUsage(stream);
    }
}
