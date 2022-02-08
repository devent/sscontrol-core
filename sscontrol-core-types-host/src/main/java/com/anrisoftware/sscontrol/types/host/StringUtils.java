package com.anrisoftware.sscontrol.types.host;

import java.util.Arrays;

/**
 *
 * @author Erwin Müller, {@code <erwin@muellerpublic.de>}
 */
public class StringUtils {

    /**
     * @see <a href=
     *      "https://www.logicbig.com/how-to/java-string/java-indent-string.html">Java
     *      - How to Indent multiline String?</a>
     */
    public static String indentString(String input, int n) {
        char[] chars = new char[n];
        Arrays.fill(chars, ' ');
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String line : input.split("\n")) {
            if (!first) {
                sb.append("\n");
            }
            sb.append(chars).append(line);
            first = false;
        }

        return sb.toString();
    }

}
