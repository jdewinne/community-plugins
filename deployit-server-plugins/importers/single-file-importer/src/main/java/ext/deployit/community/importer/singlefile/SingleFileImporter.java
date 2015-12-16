/*
 * @(#)EarImporter.java     19 Oct 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package ext.deployit.community.importer.singlefile;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayList;
import static com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry.getDescriptor;
import static com.xebialabs.deployit.server.api.util.IdGenerator.generateId;
import static ext.deployit.community.importer.singlefile.io.Dirs.listRecursively;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;
import static java.util.Collections.sort;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Application;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.api.udm.DeploymentPackage;
import com.xebialabs.deployit.plugin.api.udm.base.BaseDeployableFileArtifact;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportedPackage;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.ListableImporter;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.overthere.local.LocalFile;

import ext.deployit.community.importer.singlefile.base.NameAndVersion;
import ext.deployit.community.importer.singlefile.base.NameAndVersion.NameVersionParser;
import ext.deployit.community.importer.singlefile.config.PrefixStripper;
import ext.deployit.community.importer.singlefile.util.Predicates;

public abstract class SingleFileImporter implements ListableImporter {
    private static final String DEFAULT_APP_VERSION = "1.0";
    private static final NameVersionParser NAME_VERSION_PARSER = new NameVersionParser();

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileImporter.class);
    
    private static final String CONFIG_FILE_NAME = "single-file-importer.properties";
    private static final String CONFIG_PROPERTY_PREFIX = "single-file-importer.";
    private static final String SCAN_SUBDIRECTORIES_PROPERTY = "scanSubdirectories";
    private static final Map<String, String> CONFIG;

    static {
        Properties configProperties = new Properties();
        try {
            configProperties.load(checkNotNull(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE_NAME), CONFIG_FILE_NAME));
        } catch (Exception exception) {
            LOGGER.error(format("Unable to load configuration file '%s' from classpath", 
                    CONFIG_FILE_NAME), exception);
        }
        CONFIG = new PrefixStripper(CONFIG_PROPERTY_PREFIX)
                 .apply(Maps.fromProperties(configProperties));
    }

    protected final Type type;
    private final boolean scanSubdirectories;
    
    protected SingleFileImporter(Type type) {
        this(type, parseBoolean(CONFIG.get(SCAN_SUBDIRECTORIES_PROPERTY))); 
    }

    @VisibleForTesting
    protected SingleFileImporter(Type type, boolean scanSubdirectories) {
        checkArgument(isBaseDeployableFileType(type), "'%s' must be a subtype of %s", 
                type, BaseDeployableFileArtifact.class);
        this.type = type;
        this.scanSubdirectories = scanSubdirectories;
    }

    private static boolean isBaseDeployableFileType(Type type) {
        return Predicates.subtypeOf(Type.valueOf(BaseDeployableFileArtifact.class)).apply(type);
    }
    
    @Override
    public List<String> list(File directory) {
        FilenameFilter supportedFileFilter = new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        File file = new File(dir, name);
                        return file.isFile() && isSupportedFile(file);
                    }
                };
        List<String> supportedFiles = 
            scanSubdirectories ? listRecursively(directory, supportedFileFilter)
                               : newArrayList(directory.list(supportedFileFilter));
        sort(supportedFiles);
        LOGGER.debug("Found supported files in package directory: {}", supportedFiles);
        return supportedFiles;
    }
    
    protected abstract boolean isSupportedFile(File file);

    @Override
    public boolean canHandle(ImportSource source) {
        return isSupportedFile(source.getFile());
    }
    
    @Override
    public PackageInfo preparePackage(ImportSource source, ImportingContext context) {
        PackageMetadata packageMetadata = getPackageMetadata(source.getFile());
        PackageInfo packageInfo = new PackageInfo(source);
        packageInfo.setApplicationName(packageMetadata.appName);
        packageInfo.setApplicationVersion(packageMetadata.appVersion);
        return packageInfo;
    }
    
    // override me!
    protected PackageMetadata getPackageMetadata(File file) {
        NameAndVersion nameAndVersion = 
            NAME_VERSION_PARSER.parse(file.getName(), DEFAULT_APP_VERSION);
        return new PackageMetadata(nameAndVersion.name, nameAndVersion.version);
    }
    
    public static class PackageMetadata {
        public final String appName;
        public final String appVersion;
        
        public PackageMetadata(String appName, String appVersion) {
            this.appName = appName;
            this.appVersion = appVersion;
        }
    }
    
    @Override
    public ImportedPackage importEntities(PackageInfo packageInfo, ImportingContext context) {
        Application app = Type.valueOf(Application.class).getDescriptor().newInstance(packageInfo.getApplicationId());
        DeploymentPackage pkg = Type.valueOf(DeploymentPackage.class).getDescriptor().newInstance(generateId(app.getId(), packageInfo.getApplicationVersion()));
        ImportedPackage importedPackage =
            new ImportedPackage(packageInfo, app, pkg);
        for (Deployable deployable : getDeployables(importedPackage)) {
            LOGGER.debug("Adding deployable '{}' to package '{}'", deployable, packageInfo);
            importedPackage.addDeployable(deployable);
        }
        return importedPackage;
    }
    
    // override me!
    protected Set<Deployable> getDeployables(ImportedPackage importedPackage) {
        File importedFile = importedPackage.getPackageInfo().getSource().getFile();
        BaseDeployableFileArtifact fileArtifact = 
            getDescriptor(getDeployableType(importedFile))
            .newInstance(format("%s/%s", importedPackage.getVersion().getId(),
                importedPackage.getApplication().getName()));
        fileArtifact.setFile(LocalFile.valueOf(importedFile));
        LOGGER.debug("Created file artifact with ID '{}'", fileArtifact.getId());
        return ImmutableSet.<Deployable>of(fileArtifact);
    }
    
    // override me!
    protected Type getDeployableType(File file) {
        return type;
    }
    
    @Override
    public void cleanUp(PackageInfo packageInfo, ImportingContext context) {
        // nothing to do
    }
}
