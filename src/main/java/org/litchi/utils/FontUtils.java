package org.litchi.utils;

import org.litchi.constant.Strings;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author: gaozp
 * @date: 2021-05-12 17:52
 * @desc:
 */
public class FontUtils {

    public static String utfURL(String str) throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        int len = str.length();
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (c > 0x7F) {
                sb.append(URLEncoder.encode(String.valueOf(c), Strings.UTF_8));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}