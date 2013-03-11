package com.tacitknowledge.maven.plugin.atgassembler.util;

import java.util.List;
import java.util.Arrays;

/**
 * @author marques
 * @since Oct 19, 2008, 7:04:59 PM
 */
public class ConfigSettingUtils
{
    private static final String DEFAULT_DELIMITER = "    ";
    private static final String REGEX_TO_SPLIT = "[\\s]+";

    public static String createDelimitedString(String rawListAsString)
    {
        return createDelimitedString(rawListAsString, null);
    }

    public static String createDelimitedString(String rawListAsString, String delimiter)
    {
        if (rawListAsString == null)
            return null;

        String[] argList = rawListAsString.trim().split(REGEX_TO_SPLIT);
        List<String> list = Arrays.asList(argList);

        return createDelimitedString(list, delimiter);
    }

    public static String createDelimitedString(List<String> list)
    {
        return createDelimitedString(list, null);
    }

    public static String createDelimitedString(List<String> list, String delimiter)
    {
        if (delimiter == null)
            delimiter = DEFAULT_DELIMITER;
        if (list == null)
            return null;

        /* todo: maybe replace with StringUtils later */
        StringBuffer buffer = new StringBuffer();
        for (String item : list)
        {
            buffer.append(item.trim());
            if (list.indexOf(item) < list.size() - 1)
            {
                buffer.append(delimiter);
            }
        }
        return buffer.toString().trim();
    }
}
