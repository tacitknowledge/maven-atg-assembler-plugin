package com.tacitknowledge.maven.plugin.atgassember;

import junit.framework.TestCase;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.jar.Manifest;

/**
 * @author mshort
 * @since Aug 20, 2008, 10:43:33 AM
 */
public class TestWriteManifest extends TestCase
{
    public void testWriteAndRead() throws Exception
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Manifest manifestOut = new Manifest();
        String cpValue = "aaaaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbbbbb ccccccccccccccccccccccc ddddddddddddddddddd eeeeeeeeeeeee fffffffffffffff";
        manifestOut.getMainAttributes().putValue("Class-Path", cpValue);
        manifestOut.getMainAttributes().putValue("Manifest-Version", "1.0");
        manifestOut.write(outputStream);
        ByteArrayInputStream bis = new ByteArrayInputStream(outputStream.toByteArray());
        Manifest manifestIn = new Manifest();
        manifestIn.read(bis);
        assertEquals(cpValue, manifestIn.getMainAttributes().getValue("Class-Path"));
        assertEquals(manifestIn, manifestOut);
    }
}
