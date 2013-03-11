package com.tacitknowledge.maven.plugin.atgassembler;

import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.*;

import java.util.jar.Manifest;

/**
 * @author marques
 * @since Aug 13, 2008, 1:39:27 PM
 */

/**
 * Generate ATG-specific MANIFEST.MF for war's
 *
 * @goal war-manifest
 */
public class WarManifestMojo extends AbstractManifestAssembler
{
    /**
     * ATG-Module-Uri - I think this is the relative path to the war?
     *
     * @parameter
     */
    protected String atgModuleUri;

    /**
     * ATG-Module-Version
     *
     * @parameter
     */
    protected String atgModuleVersion;

    /**
     * ATG-Context-Root - context root for war?
     *
     * @parameter
     */
    protected String atgContextRoot;

    protected void populateManifestEntries(Manifest manifest)
    {
        if (atgModuleUri != null)
        {
            manifest.getMainAttributes().putValue(ATG_MODULE_URI, atgModuleUri);
        }

        if (atgModuleVersion != null)
        {
            manifest.getMainAttributes().putValue(ATG_MODULE_VERSION, atgModuleVersion);
        }

        if (atgContextRoot != null)
        {
            manifest.getMainAttributes().putValue(ATG_CONTEXT_ROOT, atgContextRoot);
        }
    }
}
