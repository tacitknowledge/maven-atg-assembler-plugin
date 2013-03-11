package com.tacitknowledge.maven.plugin.atgassember;

import com.tacitknowledge.maven.plugin.atgassembler.UpdateApplicationXMLMojo;

import org.apache.maven.artifact.Artifact;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;

/**
 * Tests the xml parsing and updating of UpdateApplicationXMLMojo
 *
 * @author mshort
 * @author marques
 */
public class TestUpdateApplicationXMLMojo extends MockObjectTestCase
{
    private File appXML;
    private UpdateApplicationXMLMojo appXMLUpdater;
    private Document applicationDOM;
    private Node applicationRootNode;

    protected void setUp() throws Exception
    {
        super.setUp();

        String appXMLPath = this.getClass().getClassLoader().getResource("application.xml").getPath();
        appXML = new File(appXMLPath);
        appXMLUpdater = new UpdateApplicationXMLMojo();

        assertTrue(appXML.exists());

        applicationDOM = appXMLUpdater.parseXMLDOM(appXML);
        applicationRootNode = appXMLUpdater.getAppXMLRootNode(applicationDOM);
    }

    public void testParseXMLDOM() throws Exception
    {
        assertNotNull(applicationDOM);
    }

    public void testGetAppXMLRootNode() throws Exception
    {
        assertNotNull(applicationRootNode);
    }

    public void testAddJavaModules() throws Exception
    {
        Mock mock = mock(Artifact.class);
        mock.expects(once()).method("getFile").withNoArguments().will(returnValue(new File("mock-artifact.jar")));
        Artifact artifact = (Artifact) mock.proxy();

        appXMLUpdater.addJavaModule(applicationDOM, applicationRootNode, artifact);

        Node moduleNode = applicationRootNode.getLastChild();
        Node javaNode = moduleNode.getFirstChild();

        assertEquals("module", moduleNode.getNodeName());
        assertEquals("java", javaNode.getNodeName());
        assertEquals("lib/mock-artifact.jar", javaNode.getTextContent());
    }
}
