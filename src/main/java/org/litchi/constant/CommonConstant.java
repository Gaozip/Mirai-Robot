package org.litchi.constant;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:34
 * @desc:
 */
public interface CommonConstant {


    String REDIS_CITY_KEY = "REDIS_AD_CODE_CITY";
    String REDIS_COLOR_IMAGE_KEY = "REDIS_COLOR_IMAGE";
    String REDIS_YELLOW_IMAGE_KEY = "REDIS_YELLOW_IMAGE";
    String REDIS_YELLOW_NOVEL_KEY = "REDIS_YELLOW_NOVEL";

    /**
     * 折线图url
     */
    String TEMPERATURE_IMAGE_URL = "http://106.15.94.235:8086/images/temperature.png";
    String PRECIPITATION_IMAGE_URL = "http://106.15.94.235:8086/images/precipitation.png";

    /**
     * 提醒喝水图片url
     */
    String DRINK_IMAGE_URL = "http://106.15.94.235:8086/images/promptDrinkWater.jpg";

    /**
     * 文字转图片接口
     */
    String REQUEST_MAIN_URL = "http://5000choyen.app.cyberrex.ml/image?";

    /**
     * 微博头条
     */
    String WEI_BO_TOP_URL = "https://s.weibo.com/top/summary";

    /**
     * 一言
     */
    String A_WORD_URL = "https://v1.hitokoto.cn?c=";

    /**
     * 初始化typesMap
     */
    Map<Character, String> typesMap = new ConcurrentHashMap<Character,String>() {
        {
            put('a', "动画");
            put('b', "漫画");
            put('c', "游戏");
            put('d', "文学");
            put('e', "原创");
            put('f', "来自网络");
            put('g', "其他");
            put('h', "影视");
            put('i', "诗词");
            put('j', "网易云");
            put('k', "哲学");
        }
    };

    String FEATURES_JUST_FOR_ADMIN = "此功能仅开放给admin用户!";
}
