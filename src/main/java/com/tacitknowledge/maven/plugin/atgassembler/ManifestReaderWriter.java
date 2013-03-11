package com.tacitknowledge.maven.plugin.atgassembler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.Manifest;

/**
 * @author mshort
 * @since Aug 23, 2008, 10:54:23 AM
 */
public class ManifestReaderWriter
{
    private final File manifestFile;

    public ManifestReaderWriter(File manifestFile)
    {
        this.manifestFile = manifestFile;
    }

    public void writeManifest(Manifest manifest) throws IOException
    {

        FileOutputStream stream = new FileOutputStream(manifestFile);
        manifest.write(stream);
        stream.close();
    }

    public Manifest createOrFindManifest() throws IOException
    {

        Manifest manifest = new Manifest();

        //read existing manifest
//        if (manifestFile.exists())
//        {
//                FileInputStream is = new FileInputStream(manifestFile);
//                manifest = new Manifest(is);
//                is.close();
//        }
        return manifest;
    }

}
