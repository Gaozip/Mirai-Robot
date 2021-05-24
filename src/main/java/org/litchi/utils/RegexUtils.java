package org.litchi.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: gaozp
 * @date: 2021-05-19 14:15
 * @desc: 正则工具类
 * @author lsj
 */
@Component
public class RegexUtils {

    public static String regex(String regex, String msg) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String regexGroup(String regex, String msg, int groupId) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(msg);
        if (matcher.find()) {
            return matcher.group(groupId);
        }
        return null;
    }

}
