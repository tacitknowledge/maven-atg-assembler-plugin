package com.tacitknowledge.maven.plugin.atgassembler;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/**
 * @author marques
 * @since Aug 13, 2008, 1:55:34 PM
 */
public abstract class AbstractManifestAssembler extends AbstractMojo
{
    /** the filename of the manifest */
    private static final String MANIFEST_MF = "MANIFEST.MF";

    /** the manifest version to put in the headers */
    private static final String MANIFEST_VERSION = "1.0";

    /**
     * The path to META-INF
     *
     * @parameter expression="${basedir}/src/main/application/META-INF"
     */
    protected String manifestFileDirectory;

    /**
     * The project
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Subclasses will implement this method to create entries in the manifest.
     * Called by the execute() method.
     *
     * @param manifest the Manifest Object to which to write
     *
     * @throws Exception on error
     */
    protected abstract void populateManifestEntries(Manifest manifest) throws Exception;

    /**
     * Creates or updates the Manifest file with entries defined in populateManifestEntries()
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        try
        {
            File manifestFile = new File(manifestFileDirectory, MANIFEST_MF);
            ManifestReaderWriter readerWriter = new ManifestReaderWriter(manifestFile);

            Manifest manifest = readerWriter.createOrFindManifest();

            //populate main required attributes
            Attributes mainAttrs = manifest.getMainAttributes();
            mainAttrs.putValue(Attributes.Name.MANIFEST_VERSION.toString(), MANIFEST_VERSION);

            //populate other attributes
            populateManifestEntries(manifest);

            readerWriter.writeManifest(manifest);

        }
        catch (Exception e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }

    }

}
