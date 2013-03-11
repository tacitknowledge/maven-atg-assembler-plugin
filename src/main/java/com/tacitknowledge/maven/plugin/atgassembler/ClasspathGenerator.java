package com.tacitknowledge.maven.plugin.atgassembler;

import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.ATG_CLASS_PATH;
import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.DEFAULT_LIBRARY_PATH_PREFIX;
import com.tacitknowledge.pluginsupport.util.ArtifactFilter;

import org.apache.maven.artifact.Artifact;

import java.util.Collection;
import java.util.jar.Manifest;

/**
 * @author mshort
 * @since Aug 23, 2008, 11:05:23 AM
 */
public class ClasspathGenerator
{
    private String libraryDirectory = DEFAULT_LIBRARY_PATH_PREFIX;
    private Collection<Artifact> projectArtifacts;

    public ClasspathGenerator(Collection<Artifact> projectArtifacts)
    {
        this(null, projectArtifacts);
    }

    public ClasspathGenerator(String libraryDirectory, Collection projectArtifacts)
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

    }

    public String generate()
    {
        Collection<Artifact> arts = ArtifactFilter.filterByType(ArtifactFilter.filterByScope(projectArtifacts));

        StringBuffer buf = new StringBuffer();
        for (Artifact artifact : arts)
        {
            buf.append(libraryDirectory);
            buf.append(artifact.getFile().getName());
            buf.append(" ");
        }

        return buf.toString().trim();
    }

    public void addATGClasspath(Manifest manifest)
    {
        manifest.getMainAttributes().putValue(ATG_CLASS_PATH, generate());
    }

}

