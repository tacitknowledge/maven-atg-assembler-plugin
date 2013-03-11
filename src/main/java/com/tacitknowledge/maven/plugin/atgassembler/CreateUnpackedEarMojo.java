package com.tacitknowledge.maven.plugin.atgassembler;

import com.tacitknowledge.maven.plugin.atgassembler.util.ConfigSettingUtils;

import atg.appassembly.ant.CreateUnpackedEarTask;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author marques
 * @since Sep 12, 2008, 7:02:09 PM
 */

/**
 * Runs CreateUnpackedEarTask.execute()
 *
 * @goal assemble-unpacked-ear
 */

public class CreateUnpackedEarMojo extends AbstractMojo
{
    /**
     * Specifies an existing EAR file whose contents will be added to the assembled EAR file
     *
     * @parameter
     */
    protected File addEarFile;

    /**
     * If true, the JAR files and directories in the CLASSPATH are collapsed into a single JAR
     * file in the assembled EAR file.
     * <p/>
     * default is false
     *
     * @parameter
     */
    protected boolean collapseClasspath;

    /**
     * Specifies a Java properties file to be used to override the <code>context-root</code> values
     * for any web applications included in the assembled EAR file. In this properties file, each
     * line has the format:
     * <p/>
     * <code>module-uri=context-root</code>
     * <p/>
     * This assigns the specified context root to the web application indicated by the module URI
     *
     * @parameter
     */
    protected File contextRootsFile;

    /**
     * Specifies the path of the EAR file to be created
     *
     * @parameter
     * @required
     */
    protected String destinationFile;

    /**
     * Specifies the value to be used for the &lt;display-name&gt; tag in the <code>application.xml</code>
     * file in the assembled EAR file.
     *
     * @parameter
     */
    protected String displayName;

    /**
     * Specifies the X Window System variable declaring where any X display should be sent.
     * <p/>
     * <code>(e.g., :0.0)</code>
     *
     * @parameter
     */
    protected String displayVariable;

    /**
     * Specifies a file that supplies Dynamo environment properties to be added to <code>dynamo.env</code> in
     * the assembled EAR file.
     *
     * @parameter
     */
    protected File dynamoEnvPropsFile;

    /**
     * Specifies the path to the ATG installation directory
     *
     * @parameter
     * @required
     */
    protected File dynamoRoot;

    /**
     * Specifies the Dynamo modules to include in the EAR file, as a whitespace-delimited string
     *
     * @parameter
     * @required
     */
    protected String dynamoModules;

    /**
     * This parameter isn't documented in the ATG docs (big surprise), but apparently,
     * this imports necessary resources for the ACC to run when building a standalone EAR.
     * <p/>
     * default is true (and there really shouldn't be a reason to ever have it false,
     * but leave it configurable anyway since the underlying assembler does as well)
     *
     * @parameter default-value="true"
     */
    protected boolean importAccResources;

    /**
     * If true, liveconfig mode is enabled in the assembled EAR file.
     * <p/>
     * default is false
     *
     * @parameter
     */
    protected boolean liveconfig;

    /**
     * If true, license files are omitted from the assembled EAR file. This is useful if you don't
     * want your development licenses included in a production EAR file.
     * <p/>
     * default is false
     *
     * @parameter
     */
    protected boolean omitLicenses;

    /**
     * If true, overwrites an existing EAR file; if false, stops processing if the EAR file already
     * exists.
     * <p/>
     * default is false (do not overwrite)
     *
     * @parameter
     */
    protected boolean overwrite;

    /**
     * prependJars
     *
     * @parameter
     */
    protected String prependJars;

    /**
     * If set, specifies the Dynamo server (for localconfig, etc.) to be used by the assembled EAR
     * file. If unset, the default server is used.
     *
     * @parameter
     */
    protected String servername;

    /**
     * If true, the EAR file is created in standalone mode, in which all necessary resources are
     * imported into the resulting EAR file, and the EAR file does not reference the ATG installation
     * directory. If false, a development-mode EAR file is created, in which Nucleus configuration
     * and other runtime resources are used directly from the ATG installation.
     * <p/>
     * default is false (development mode)
     *
     * @parameter
     */
    protected boolean standalone;

    /**
     * Configures and executes the CreateUnpackedEarTask
     *
     * @throws MojoExecutionException on exection error
     * @throws MojoFailureException   on failure
     */
    public void execute() throws MojoExecutionException, MojoFailureException
    {
        CreateUnpackedEarTask eartask = new CreateUnpackedEarTask();

        configureEarTask(eartask);

        eartask.execute();
    }

    /**
     * configures the CreateUnpackedEarTask with our settings from the pom
     *
     * @param eartask the instance of CreateUnpackedEarTask
     * @throws MojoExecutionException if configuration fails
     */
    private void configureEarTask(CreateUnpackedEarTask eartask) throws MojoExecutionException
    {
        logInfo("Configuring the UnpackedEar assembler...");

        try
        {
            configure(eartask, "addEarFile");
            configure(eartask, "collapseClasspath");
            configure(eartask, "contextRootsFile");
            configure(eartask, "destinationFile");
            configure(eartask, "displayName");
            configure(eartask, "displayVariable");
            configure(eartask, "dynamoEnvPropsFile");
            configure(eartask, "dynamoRoot");
            configure(eartask, "dynamoModules", ",");
            configure(eartask, "importAccResources");
            configure(eartask, "liveconfig");
            configure(eartask, "omitLicenses");
            configure(eartask, "overwrite");
            configure(eartask, "prependJars");
            configure(eartask, "servername");
            configure(eartask, "standalone");
        }
        catch (NoSuchFieldException e)
        {
            throw new MojoExecutionException("can't resolve field", e);
        }
        catch (NoSuchMethodException e)
        {
            throw new MojoExecutionException("can't resolve method", e);
        }
        catch (IllegalAccessException e)
        {
            throw new MojoExecutionException("can't access field", e);
        }
        catch (InvocationTargetException e)
        {
            throw new MojoExecutionException("can't invoke method", e);
        }
    }

    /**
     * overloads configure() without a list delimiter
     *
     * @param eartask   the instance of the CreateUnpackedEarTask
     * @param fieldName the name of the field to set
     * @throws NoSuchFieldException      if the field doesn't exist
     * @throws NoSuchMethodException     if the setter doesn't exist on the CreateUnpackedEarTask
     * @throws IllegalAccessException    if we can't access the field value from this instance
     * @throws InvocationTargetException if we can't invoke the method on our instance
     *                                   of the CreateUnpackedEarTask
     */
    private void configure(CreateUnpackedEarTask eartask, String fieldName)
            throws IllegalAccessException, NoSuchMethodException, NoSuchFieldException,
            InvocationTargetException
    {
        configure(eartask, fieldName, null);
    }

    /**
     * reflectively invokes a setter on the CreateUnpackedEarTask that matches a field
     * on this class. this implies that any configurable field must have the same naming
     * convention as the setters on the CreateUnpackedEarTask.
     *
     * @param eartask   the instance of the CreateUnpackedEarTask
     * @param fieldName the name of the field to set
     * @param delimiter a delimiter to use if the value is a raw string list. if null,
     *                  value is treated as-is.
     * @throws NoSuchFieldException      if the field doesn't exist
     * @throws NoSuchMethodException     if the setter doesn't exist on the CreateUnpackedEarTask
     * @throws IllegalAccessException    if we can't access the field value from this instance
     * @throws InvocationTargetException if we can't invoke the method on our instance
     *                                   of the CreateUnpackedEarTask
     */
    private void configure(CreateUnpackedEarTask eartask, String fieldName, String delimiter)
            throws NoSuchFieldException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException
    {
        /* get the classes so that we can use reflection */
        Class<? extends CreateUnpackedEarMojo> self = this.getClass();
        Class<? extends CreateUnpackedEarTask> earTaskClass = eartask.getClass();

        /* try to resolve the field name from this class */
        Field field = self.getDeclaredField(fieldName);

        /* possibly required for protected access? */
        field.setAccessible(true);

        /* extract the field value */
        Object value = field.get(this);

        if (value == null)
        {
            /* don't try to invoke the setter if we haven't set this */
            logInfo(fieldName, ": not set");
            return;
        }

        if (delimiter != null)
        {
            if (value instanceof String)
            {
                value = ConfigSettingUtils.createDelimitedString((String) value, delimiter);
            }
            else
            {
                String msg = "Tried to create delimited string from a non-string. value is of type: "
                        + value.getClass().getName();
                getLog().error(msg);
                throw new IllegalArgumentException(msg);
            }
        }

        /* derive the setter method name from the field's name */
        StringBuffer sb = new StringBuffer();
        String setterMethodName = sb.append("set").append(fieldName.substring(0, 1).toUpperCase())
                .append(fieldName.substring(1)).toString();

        /* try to resolve the method from the CreateUnpackedEarTask */
        Method setterMethod = earTaskClass.getDeclaredMethod(setterMethodName, field.getType());

        /* finally, invoke the setter with our field value */
        setterMethod.invoke(eartask, value);

        logInfo(fieldName, ": ", String.valueOf(value));
    }

    /**
     * some convienient logging
     *
     * @param messages message varargs. these will be concatenated.
     */
    private void logInfo(String... messages)
    {
        if (messages == null)
        {
            return;
        }

        StringBuffer buf = new StringBuffer();
        for (String msg : messages)
        {
            buf.append(msg);
        }

        getLog().info(buf.toString());
    }
}
