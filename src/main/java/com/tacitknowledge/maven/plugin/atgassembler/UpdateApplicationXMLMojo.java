package com.tacitknowledge.maven.plugin.atgassembler;

import static com.tacitknowledge.maven.plugin.atgassembler.AssemblyConstants.DEFAULT_LIBRARY_PATH_PREFIX;
import com.tacitknowledge.pluginsupport.util.ArtifactFilter;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.xml.resolver.tools.CatalogResolver;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;

/**
 * Updates the application.xml descriptor to include artifact jars as java modules
 *
 * @author mshort
 * @author marques
 * @goal update-appxml
 */
public class UpdateApplicationXMLMojo extends AbstractMojo
{
    /** filename of the application descriptor */
    private static final String APPLICATION_XML_FILENAME = "application.xml";

    /** tag name for "module" DOM element */
    private static final String MODULE_DOM_ELEMENT = "module";

    /** tag name for "java" DOM element */
    private static final String JAVA_DOM_ELEMENT = "java";

    /**
     * The project build directory
     *
     * @parameter expression="${project.build.directory}"
     * @readonly
     */
    protected File buildDirectory;

    /**
     * The project
     *
     * @parameter expression="${project}"
     * @readonly
     */
    protected MavenProject project;

    /**
     * Performs the update to the application.xml
     *
     * @throws MojoExecutionException
     * @throws MojoFailureException
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        /* open the application.xml file */
        File applicationXML = new File(buildDirectory, APPLICATION_XML_FILENAME);

        /* parse the application.xml's DOM */
        Document applicationDOM = parseXMLDOM(applicationXML);

        /* retrieve the root node of the DOM, <application /> */
        Node applicationNode = getAppXMLRootNode(applicationDOM);

        /* loop through artifacts and add as Java Module elements to the DOM */
        for (Artifact artifact : getFilteredProjectArtifacts())
        {
            addJavaModule(applicationDOM, applicationNode, artifact);
        }

        /* finally, write the DOM back to the file */
        writeAppXMLFileFromDOM(applicationXML, applicationDOM);
    }

    /**
     * Convenience method to return a collection of project artifacts
     * filtered by scope and by type
     *
     * @return the scope and type filtered artifact list
     */
    private Collection<Artifact> getFilteredProjectArtifacts()
    {
        Collection<Artifact> rawArtifactList = project.getArtifacts();
        Collection<Artifact> scopeFilteredList = ArtifactFilter.filterByScope(rawArtifactList);
        return ArtifactFilter.filterByType(scopeFilteredList);
    }

    /**
     * Adds a project artifact as a library (Java Module) in the application.xml
     *
     * @param DOM                 the Document Object Module to which to append Java Modules
     * @param applicationRootNode the root node of the given DOM
     * @param artifact            the library to add
     */
    public void addJavaModule(Document DOM, Node applicationRootNode, Artifact artifact)
    {
        Element module = DOM.createElement(MODULE_DOM_ELEMENT);
        Element java = DOM.createElement(JAVA_DOM_ELEMENT);
        java.appendChild(DOM.createTextNode(DEFAULT_LIBRARY_PATH_PREFIX + artifact.getFile().getName()));
        module.appendChild(java);
        applicationRootNode.appendChild(module);
    }

    /**
     * Parses and returns the DOM from an XML file
     *
     * @param xmlFile the XML file to parse
     *
     * @return the xml file's DOM
     *
     * @throws MojoExecutionException on error
     */
    public Document parseXMLDOM(File xmlFile) throws MojoExecutionException
    {
        try
        {
            InputStream is = new FileInputStream(xmlFile);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            factory.setNamespaceAware(false);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setEntityResolver(new CatalogResolver());
            Document document = builder.parse(is);
            is.close();
            return document;
        }
        catch (ParserConfigurationException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        catch (SAXException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }

    /**
     * Retrieves the "application" node from the application.xml DOM
     *
     * @param applicationDOM the application.xml's DOM
     *
     * @return the "application" node
     *
     * @throws MojoExecutionException if node can't be retrieved
     */
    public Node getAppXMLRootNode(Document applicationDOM) throws MojoExecutionException
    {
        NodeList list = applicationDOM.getElementsByTagName("application");
        if (list.getLength() != 1)
        {
            throw new MojoExecutionException("Cannot retrieve <application /> node from application.xml! Number of application nodes: " + list.getLength());
        }
        return list.item(0);
    }

    private void writeAppXMLFileFromDOM(File applicationXMLFile, Document applicationDOM)
            throws MojoExecutionException
    {
        try
        {
            DOMSource source = new DOMSource(applicationDOM);
            OutputStream os = new FileOutputStream(applicationXMLFile);
            StreamResult result = new StreamResult(os);
            TransformerFactory transFactory = TransformerFactory.newInstance();
            Transformer transformer = transFactory.newTransformer();
            transformer.transform(source, result);
            os.close();
        }
        catch (TransformerException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
        catch (IOException e)
        {
            throw new MojoExecutionException(e.getMessage(), e);
        }
    }
}
