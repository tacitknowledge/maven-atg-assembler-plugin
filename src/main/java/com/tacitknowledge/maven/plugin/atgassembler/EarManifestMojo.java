package com.tacitknowledge.maven.plugin.atgassembler;

import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.*;
import com.tacitknowledge.maven.plugin.atgassembler.util.ConfigSettingUtils;

import org.apache.maven.model.Dependency;

import java.util.List;
import java.util.jar.Manifest;

/**
 * @author marques
 * @since Aug 13, 2008, 1:39:27 PM
 */

/**
 * Generate ATG-specific MANIFEST.MF for ear's
 * <p/>
 * We support these attributes so far:
 * <ul>
 * <li>ATG-Class-Path</li>
 * <li>ATG-Client-Class-Path</li>
 * <li>ATG-Config-Path</li>
 * <li>ATG-Client-Update-File (per-resource entry, always set to true)</li>
 * <li>ATG-LiveConfig-Path</li>
 * <li>ATG-Product</li>
 * <li>ATG-Required</li>
 * <li>ATG-Web-Module</li>
 * <li>MD5-Digest (per-resource entry, always set)</li>
 * <li>SHA-Digest (per-resource entry, always set)</li>
 * </ul>
 * <p/>
 * These attributes have yet to be implemented:
 * <ul>
 * <li>ATG-Assembler-Class-Path</li>
 * <li>ATG-Assembler-Import-File (per-resource entry)</li>
 * <li>ATG-Assembler-Skip-File</li>
 * <li>ATG-Client-Help-Path</li>
 * <li>ATG-Client-Update-Version (per-resource entry)</li>
 * <li>ATG-EAR-Module</li>
 * <li>ATG-EJB-Module</li>
 * <li>ATG-Nucleus-Initializer</li>
 * </ul>
 *
 * @goal ear-manifest
 */
public class EarManifestMojo extends AbstractManifestAssembler
{
    /**
     * ATG-Product
     * <p/>
     * product name
     *
     * @parameter
     */
    protected String atgProduct;

    /**
     * ATG-Client-Class-Path
     * <p/>
     * A space-delimited set of paths to module resources that contain classes required
     * by client-side features of the module.
     * <p/>
     * <em>Maven plugin note: Used by ACC and BCC. By default, this will include all jars
     * on the app classpath</em>
     *
     * @parameter
     */
    List<Dependency> clientLibraries;

    /**
     * ATG-Config-Path
     * <p/>
     * A space-delimited set of paths to module resources that provide Nucleus configuration
     * files needed by the module's application components. These may be either .jar files
     * or directories.
     *
     * @parameter
     */
    String configJars;

    /**
     * ATG-LiveConfig-Path
     * <p/>
     * A space-delimited set of paths to module resources that provide Nucleus configuration
     * files. These configuration files will be appended to the CONFIGPATH when you enable
     * the liveconfig configuration layer. Paths are relative to the module's root directory,
     * <strong>not</strong> the ATG installation directory. For more information about the
     * liveconfig configuration layer, see the <em>ATG Installation and Configuration Guide</em>.
     *
     * @parameter
     */
    protected String liveConfigPath;

    /**
     * ATG-Required
     * <p/>
     * A space-delimited set of module names, specifying modules on which this module depends.
     * If you specify this module when you run the application assembler, any modules listed
     * here are included in the application as well. When the application is started up, the
     * module's manifest is processed after the manifests of the modules that the module depends
     * on. To determine the processing order, the ATG platform determines which modules depend
     * on other modules, then puts the most independent modules first, followed by modules that
     * depend on other modules. Note that in most cases, you should set this attribute to
     * include DSS.
     *
     * @parameter
     */
    protected String requiredModules;

    /**
     * ATG-Web-Module
     * <p/>
     * Indicates that the Dynamo module contains one or more web applications that should be
     * included in the assembled application.
     *
     * @parameter
     */
    protected String webModules;

    protected void populateManifestEntries(Manifest manifest) throws Exception
    {
        ClasspathGenerator classpathGenerator = new ClasspathGenerator(project.getArtifacts());
        ClientLibraryGenerator clientLibGenerator = new ClientLibraryGenerator(project.getArtifacts(), clientLibraries);

        // Add "ATG-Product"
        addATGProduct(manifest);

        // Add "ATG-Required"
        addRequiredModules(manifest);

        // Add "ATG-LiveConfig-Path"
        addLiveConfig(manifest);

        // Add "ATG-Config-Path"
        addConfigJars(manifest);

        // Add "ATG-Web-Module"
        addWebModules(manifest);

        // Add "ATG-Class-Path"
        classpathGenerator.addATGClasspath(manifest);

        // Add "ATG-Client-Class-Path"
        clientLibGenerator.addAtgClientClasspath(manifest);

        // Add client libs "Name", "ATG-Client-Update-File" pairs
        clientLibGenerator.addAtgClientUpdateAttributes(manifest);

        // Add MD5 and SHA checksums to the manifest
        clientLibGenerator.addCheckSumAttributes(manifest);
    }

    protected void addATGProduct(Manifest manifest)
    {
        if (atgProduct != null)
        {
            manifest.getMainAttributes().putValue(ATG_PRODUCT, atgProduct);
        }
    }

    protected void addWebModules(Manifest manifest)
    {
        if (webModules != null)
        {
            String modules = ConfigSettingUtils.createDelimitedString(webModules);
            manifest.getMainAttributes().putValue(ATG_WEB_MODULE, modules);
        }
    }

    protected void addRequiredModules(Manifest manifest)
    {
        if (requiredModules != null)
        {
            String dynamoModules = ConfigSettingUtils.createDelimitedString(requiredModules);
            manifest.getMainAttributes().putValue(ATG_REQUIRED, dynamoModules);
        }
    }

    protected void addLiveConfig(Manifest manifest)
    {
        if (liveConfigPath != null)
        {
            String liveConfig = ConfigSettingUtils.createDelimitedString(liveConfigPath);
            manifest.getMainAttributes().putValue(ATG_LIVECONFIG_PATH, liveConfig);
        }
    }

    protected void addConfigJars(Manifest manifest)
    {
        if (configJars != null)
        {
            String configs = ConfigSettingUtils.createDelimitedString(configJars);
            manifest.getMainAttributes().putValue(ATG_CONFIG_PATH, configs);
        }
    }
}
