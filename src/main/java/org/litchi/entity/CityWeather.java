package org.litchi.entity;

import lombok.Data;

/**
 * @Author: gzip
 * @Date: 2021/1/14  11:09
 * @Description:
 **/
@Data
public class CityWeather {

    private String date;
    private String week;
    private String dayweather;
    private String nightweather;
    private String daytemp;
    private String nighttemp;
    private String daywind;
    private String nightwind;
    private String daypower;
    private String nightpower;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        String weather = dayweather.equals(nightweather) ? nightweather : dayweather + "转" + nightweather;
        String wind = daywind.equals(nightwind) ? daywind : daywind + "转" + nightwind;
        String power = daypower.equals(nightpower) ? daypower + "级" : "白天" + daypower + "级,夜晚" + nightpower + "级";

        sb.append("天气：" + weather + "\n");
        sb.append("温度：" + nighttemp + "\u2103/" + daytemp + "\u2103\n");
        sb.append("风向：" + wind + "\n");
        sb.append("风力：" + power);
        return sb.toString();
    }
}
