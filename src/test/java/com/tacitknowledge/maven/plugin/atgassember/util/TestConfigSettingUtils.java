package com.tacitknowledge.maven.plugin.atgassember.util;

import com.tacitknowledge.maven.plugin.atgassembler.util.ConfigSettingUtils;

import junit.framework.TestCase;

/**
 * @author marques
 * @since Oct 19, 2008, 8:26:07 PM
 */
public class TestConfigSettingUtils extends TestCase
{
    public static void testCreateDelimitedString()
    {
        String raw = "                                DafEar.Admin\n" +
                "                                DafEar\n" +
                "                                DCS\n" +
                "                                DCS.CustomCatalogs\n" +
                "                                DCS.CustomCatalogMigration\n" +
                "                                B2CCommerce\n" +
                "                                estore\n";
        String delim = ConfigSettingUtils.createDelimitedString(raw, ",");
        assertEquals("DafEar.Admin,DafEar,DCS,DCS.CustomCatalogs,DCS.CustomCatalogMigration,B2CCommerce,estore", delim);
    }
}
