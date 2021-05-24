package org.litchi.constant;

import org.litchi.utils.ForEachUtils;

import java.util.Arrays;

/**
 * @author: gaozp
 * @date: 2021-05-22 16:11
 * @desc:
 */
public enum CommandEnum {

    FEATURES_COMMAND_PREFIX("#指令列表","获取指令列表"),
    SENSITIVE_ADD_PREFIX("#添加敏感词", "添加敏感词(admin)"),
    SENSITIVE_DEL_PREFIX("#移除敏感词", "移除敏感词(admin)"),
    COLOR_IMAGE_PREFIX("#涩图", "获取色图"),
    WORD_TO_IMAGE_PREFIX("#转图", "文字转图片(#转图 卧槽/nb)"),
    DEFAULT_WEATHER_PREFIX("#天气预报", "默认城市天气预报"),
    SPECIFY_WEATHER_PREFIX("#天气预报/", "指定城市天气预报(#天气预报/ + 城市)"),
    DEFAULT_WEI_BO_TOP_PREFIX("#微博头条","获取微博头条(默认10条)"),
    SPECIFY_WEI_BO_TOP_PREFIX("#微博头条/","获取指定微博头条条数(最多50条)");

    private String command;
    private String desc;

    CommandEnum(String command, String desc) {
        this.command = command;
        this.desc = desc;
    }

    public String getCommand() {
        return command;
    }

    public String getCommandDesc(){
        return command + "   " + desc;
    }

    /**
     * 获取此枚举常量列表
     *
     * @return
     */
    public static String getCommandList() {

        StringBuilder sb = new StringBuilder("#指令列表\n");
        sb.append("=================================\n");
        ForEachUtils.forEach(0, Arrays.asList(CommandEnum.values()),(index, command) ->{
            sb.append(index + 1 + "." + command.getCommandDesc() + "\n");
        } );
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(CommandEnum.getCommandList());
    }
}
