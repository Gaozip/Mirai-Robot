package org.litchi.entity.caiyun;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author: gaozp
 * @date: 2021-04-27 10:17
 * @desc:
 */

@Data
public class CaiYunWeather implements Serializable {

    List<Temperature> temperature;
    List<Precipitation> precipitation;

    @Override
    public String toString() {
        return "CaiYunWeather{" +
                "temperature=" + temperature +
                ", precipitation=" + precipitation +
                '}';
    }
}


//status: ok
//        status: ok                                         #返回状态
//        api_status: active                                 #API 服务状态
//        api_version: v2.5                                  #API 版本号
//        server_time: 1586247444                            #响应时刻服务器时间
//        timezone: Asia/Taipei                              #时区信息
//        tzshift: 28800                                     #时区偏移秒数
//        unit: metric                                       #单位制
//        lang: zh_CN                                        #语言
//        location:
//        - 25.1552                                          #纬度
//        - 121.6544                                         #经度
//        result:
//        hourly:
//        status: ok
//        description: 未来24小时阴                       #预报自然语言表述
//        skycon:                                       #天气状态
//        - datetime: 2020-04-07T16:00+08:00
//        value: CLOUDY
//        temperature:                                  #温度
//        - datetime: 2020-04-07T16:00+08:00
//        value: 17.2
//        precipitation:                                #降雨
//        - datetime: 2020-04-07T16:00+08:00
//        value: 0.0
//        cloudrate:                                    #云量
//        - datetime: 2020-04-07T16:00+08:00
//        value: 1.0
//        humidity:                                     #相对湿度
//        - datetime: 2020-04-07T16:00+08:00
//        value: 0.81
//        pressure:                                     #气压
//        - datetime: 2020-04-07T16:00+08:00
//        value: 99897.4914560001
//        wind:                                         #风力、风向
//        - datetime: 2020-04-07T16:00+08:00
//        direction: 26.03
//        speed: 20.93
//        visibility:                                   #能见度
//        - datetime: 2020-04-07T16:00+08:00
//        value: 23.77
//        dswrf:                                        #短波辐射下向通量
//        - datetime: 2020-04-07T16:00+08:00
//        value: 113.6283648
//        air_quality:
//        aqi:                                        # AQI
//        - datetime: 2020-04-07T16:00+08:00
//        value:
//        chn: 14                                 #国标
//        usa: 14                                 #美标
//        pm25:                                       #PM25
//        - datetime: 2020-04-07T16:00+08:00
//        value: 8