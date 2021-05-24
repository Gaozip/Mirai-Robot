package org.litchi.constant;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:34
 * @desc:
 */
public interface CommonConstant {


    String REDIS_CITY_KEY = "REDIS_AD_CODE_CITY";
    String REDIS_COLOR_IMAGE_KEY = "REDIS_COLOR_IMAGE";

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

}
