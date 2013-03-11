package com.tacitknowledge.maven.plugin.atgassembler;

import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.*;
import com.tacitknowledge.pluginsupport.util.ArtifactFilter;

import org.apache.commons.codec.binary.Hex;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.model.Dependency;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


/**
 * @author mshort
 * @author marques
 * @author mottinger
 * @since Aug 23, 2008, 11:05:23 AM
 */

//todo - mws - this needs the libraries for the config path
public class ClientLibraryGenerator
{
    private String libraryDirectory = DEFAULT_LIBRARY_PATH_PREFIX;
    private Collection<Artifact> projectArtifacts;
    private Collection<Dependency> clientLibraries;

    public ClientLibraryGenerator(Collection<Artifact> projectArtifacts, Collection<Dependency> clientLibraries)
    {
        this(null, projectArtifacts, clientLibraries);
    }

    public ClientLibraryGenerator(String libraryDirectory, Collection<Artifact> projectArtifacts, Collection<Dependency> clientLibraries)
    {
        if (libraryDirectory != null)
        {
            if (!libraryDirectory.endsWith("/"))
            {
                libraryDirectory += "/";
            }
            this.libraryDirectory = libraryDirectory;
        }
        this.projectArtifacts = projectArtifacts;
        this.clientLibraries = clientLibraries;
    }

    public String generateLibraryList() throws Exception
    {
        Collection<Artifact> arts = getArtifactsForDependencies();
        StringBuffer buf = new StringBuffer();
        for (Artifact artifact : arts)
        {
            buf.append(libraryDirectory);
            buf.append(artifact.getFile().getName());
            buf.append(" ");
        }

        return buf.toString().trim();
    }

    public void addAtgClientClasspath(Manifest manifest) throws Exception
    {
        Collection<Artifact> artifacts = getArtifactsForDependencies();

        if (artifacts != null && !artifacts.isEmpty())
        {
            manifest.getMainAttributes().putValue(ATG_CLIENT_CLASS_PATH, generateLibraryList());
        }
    }

    public void addAtgClientUpdateAttributes(Manifest manifest) throws Exception
    {
        Collection<Artifact> artifacts = getArtifactsForDependencies();
        if (artifacts == null || artifacts.isEmpty()) return;

        for (Artifact artifact : artifacts)
        {
            Attributes artifactEntries = getAttributesForArtifact(manifest, artifact);
            artifactEntries.putValue(ATG_CLIENT_UPDATE_FILE, "true");
            manifest.getEntries().put(getLibraryNameForArtifact(artifact), artifactEntries);
        }
    }

    /**
     * Loop through the artifacts and generate a SHA and MD5 checksum against each one. Apply each
     * of these to the passed in manifest as entries.
     *
     * @param manifest build on the passed in manifest the checksum entries
     *
     * @throws Exception let the calling mojo handle this, most likely resulting in a build error
     */
    public void addCheckSumAttributes(Manifest manifest) throws Exception
    {
        System.out.println("Apply checksum entries to artifacts");

        Collection<Artifact> artifacts = getArtifactsForDependencies();
        if (artifacts == null || artifacts.isEmpty()) return;

        MessageDigest sha = MessageDigest.getInstance(SHA_DIGEST_TYPE);
        MessageDigest md5 = MessageDigest.getInstance(MD5_DIGEST_TYPE);

        for (Artifact artifact : artifacts)
        {
            sha.reset();
            md5.reset();

            String shaCheckSum = generateCheckSum(artifact, sha);
            String md5CheckSum = generateCheckSum(artifact, md5);

            Attributes artifactEntries = getAttributesForArtifact(manifest, artifact);

            artifactEntries.putValue(DIGEST_ALGORITHMS, SUPPORTED_ALGORITHMS);
            artifactEntries.putValue(SHA_DIGEST_KEY, shaCheckSum);
            artifactEntries.putValue(MD5_DIGEST_KEY, md5CheckSum);

            manifest.getEntries().put(getLibraryNameForArtifact(artifact), artifactEntries);
        }
    }

    private Collection<Artifact> getArtifactsForDependencies() throws Exception
    {
        //return all libraries in scope if client libraries are not specified
        if (clientLibraries == null || clientLibraries.isEmpty())
            return ArtifactFilter.filterByType(ArtifactFilter.filterByScope(projectArtifacts));

        System.out.println("\n*****Found dependencies*****\n");
        //otherwise find only libraries which match
        List<Artifact> artifacts = new ArrayList<Artifact>();
        for (Dependency dependency : clientLibraries)
        {
            Artifact artifact = getArtifact(dependency);
            artifacts.add(artifact);
        }
        return artifacts;
    }

    private Artifact getArtifact(Dependency dependency) throws Exception
    {
        String groupId = dependency.getGroupId();
        String artifactId = dependency.getArtifactId();
        String version = dependency.getVersion();
        if (version == null || "".equals(version))
        {
            version = DEFAULT_DEPENDENCY_VERSION;
        }

        for (Artifact artifact : ArtifactFilter.filterByScope(projectArtifacts))
        {
            if (artifact.getArtifactId().equals(artifactId)
                    && artifact.getGroupId().equals(groupId)
                    && artifact.getVersionRange().containsVersion(new DefaultArtifactVersion(version))
                    ) return artifact;
        }
        throw new Exception("No project artifact matched the defined client library dependency ["
                + artifactId + ", " + groupId + ", " + version + "]");
    }

    /**
     * For each artifact generate a SHA and MD5 checksum to prevent unnecessary downloading of
     * unchanged jars
     *
     * @param artifact the artifact to create the checksums from
     * @param digest   either the SHA or MD5 digest
     *
     * @return a string value of the generated check sum
     *
     * @throws IOException let this bubble out and become a build fail
     */
    private String generateCheckSum(Artifact artifact, MessageDigest digest)
            throws IOException
    {
        byte[] buffer = new byte[5096];
        int numRead;
        InputStream artifactStream = null;
        String checkSum = "";

        try
        {
            artifactStream = new BufferedInputStream(new FileInputStream(artifact.getFile()));
            while ((numRead = artifactStream.read(buffer)) != -1)
            {
                digest.update(buffer, 0, numRead);
            }

            checkSum = new String(Hex.encodeHex(digest.digest()));

        }
        finally
        {
            if (artifactStream != null)
            {
                artifactStream.close();
            }
        }

        return checkSum;
    }

    /**
     * Simple little method to return the artifact's file name prepended with the library
     * directory.
     *
     * @param artifact retrieve the artifact's file name from this
     *
     * @return a string representing the artifact's file name with the libaray directory prepended
     *         to it.
     */
    private String getLibraryNameForArtifact(Artifact artifact)
    {
        return libraryDirectory + artifact.getFile().getName();
    }

    /**
     * For an artifact's file name as an attribute entry key, retrieve all of its existing entries
     * for the passed in manifest file. If none are found, create a new attributes object and return
     * it
     *
     * @param manifest from which to retrieve the artifact entries from
     * @param artifact use the artifact's library name as the key to look up previous manifest
     *                 entries from
     *
     * @return either the previous attribute entries for the passed in artifact, or a new one
     */
    private Attributes getAttributesForArtifact(Manifest manifest, Artifact artifact)
    {

        Attributes artifactEntries =
                manifest.getAttributes(getLibraryNameForArtifact(artifact));

        if (artifactEntries == null)
        {
            artifactEntries = new Attributes();
        }

        return artifactEntries;
    }

}