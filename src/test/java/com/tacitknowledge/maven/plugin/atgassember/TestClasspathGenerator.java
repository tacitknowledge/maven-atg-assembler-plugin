package com.tacitknowledge.maven.plugin.atgassember;

import com.tacitknowledge.maven.plugin.atgassembler.ClasspathGenerator;

import org.apache.maven.artifact.Artifact;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author mshort
 * @since Aug 23, 2008, 11:34:04 AM
 */
public class TestClasspathGenerator extends MockObjectTestCase
{


    public void testGenerateTestOnly() throws Exception
    {
        Collection<Artifact> projectArtifacts = new ArrayList<Artifact>();
        Mock mock = mock(Artifact.class);
        mock.expects(once()).method("getScope").withNoArguments().will(returnValue("test"));
        Artifact art = (Artifact) mock.proxy();

        projectArtifacts.add(art);
        ClasspathGenerator generator = new ClasspathGenerator("lib", projectArtifacts);
        String result = generator.generate();
        assertEquals("", result);
    }

/*
    public void testGenerate() throws Exception {
        Collection<Artifact> projectArtifacts = new ArrayList<Artifact>();
        Mock mock = mock(Artifact.class);
        mock.expects(once()).method("getScope").withNoArguments().will(returnValue("test"));
        Artifact art = (Artifact) mock.proxy();
        projectArtifacts.add(art);

        mock = mock(Artifact.class);
        mock.expects(once()).method("getScope").withNoArguments().will(returnValue("provided"));
        mock.expects(once()).method("getFile").withNoArguments().will(returnValue(new File("one.jar")));
        art = (Artifact) mock.proxy();
        projectArtifacts.add(art);

        mock = mock(Artifact.class);
        mock.expects(once()).method("getScope").withNoArguments().will(returnValue("compile"));
        mock.expects(once()).method("getFile").withNoArguments().will(returnValue(new File("two.jar")));
        art = (Artifact) mock.proxy();
        projectArtifacts.add(art);

        ClasspathGenerator generator = new ClasspathGenerator("lib",projectArtifacts);
        String result = generator.generate();
        assertEquals("lib/one.jar lib/two.jar",result);
    }
*/

}
