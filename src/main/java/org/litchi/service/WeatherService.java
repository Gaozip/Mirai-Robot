package org.litchi.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.AdCityInfo;
import org.litchi.entity.CityWeather;
import org.litchi.entity.Serie;
import org.litchi.entity.caiyun.CaiYunWeather;
import org.litchi.entity.caiyun.Precipitation;
import org.litchi.entity.caiyun.Temperature;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author: gaozp
 * @date: 2021-05-13 16:33
 * @desc:
 */

@Service
public class WeatherService {

    @Value("${mapKey}")
    private String mapKey;

    @Value("${caiYunWeatherKey}")
    private String caiYunWeatherKey;

    /**
     * 温度折线图
     */
    private final String TEMPERATURE = "temperature";
    private final String TEMPERATURE_TITLE = "24h-temperature";
    private final String TEMPERATURE_Y_UNIT = "\u2103";
    private final String TEMPERATURE_FILE_PATH = "/usr/environment/images/temperature.png";


    /**
     * 降雨量折线图
     */
    private final String PRECIPITATIONS = "precipitation";
    private final String PRECIPITATIONS_TITLE = "24h-precipitation";
    private final String PRECIPITATIONS_Y_UNIT = "mm／h";
    private final String PRECIPITATIONS_FILE_PATH = "/usr/environment/images/precipitation.png";

    /**
     * 折线图属性
     */
    private final String X_UNIT = "";
    private final Integer PIC_HEIGHT = 300;
    private final Integer PIC_WIDTH = 1600;

    /**
     * 彩云api url
     */
    private final String CAI_YUN_WEATHER_API_URL = "https://api.caiyunapp.com/v2.5/";

    /**
     * 根据城市名称获取城市经纬度api url
     */
    private final String GAO_DE_POSITION_API_URL = "https://api.map.baidu.com/geocoder?address=%E5%9F%8E%E5%B8%82%E5%90%8D%E7%A7%B0&output=json&key=";
    private final String GAO_DE_WEATHER_API_URL = "https://restapi.amap.com/v3/weather/weatherInfo?key=";


    @Resource
    private RestTemplate restTemplate;

    @Resource
    private RedisService redisService;

    /**
     * 根据高德api城市代码获取天气预报
     * @param adCityCode
     * @return
     */
    public String genGaoDeWeathers(AdCityInfo adCityCode){

        String url = GAO_DE_WEATHER_API_URL + mapKey + "&city=" + adCityCode.getAdCode() +"&extensions=all&output=JSON";

        String queryResult = restTemplate.getForObject(url, String.class);
        String jsonString = JSON.parseObject(queryResult).getString("forecasts");
        String forecasts = jsonString.substring(1,jsonString.length() - 1);
        String casts = JSON.parseObject(forecasts).getString("casts");
        List<CityWeather> cityWeathers = JSONObject.parseArray(casts, CityWeather.class);

        return this.genWeatherMessage(cityWeathers,adCityCode.getCityName());
    }

    /**
     * 构造3日天气预报消息
     *
     * @param cityWeathers
     * @param cityName
     * @return
     */
    private String genWeatherMessage(List<CityWeather> cityWeathers, String cityName) {

        StringBuilder sb = new StringBuilder();

        Calendar calendar = Calendar.getInstance();

        sb.append("【" + cityName + "】天气预报\n");
        sb.append("今天--" + DateUtil.format(calendar.getTime(),"yyyy-MM-dd") + "\n");
        sb.append(cityWeathers.get(0) + "\n");
        sb.append("------------------------------\n");

        calendar.add(Calendar.DAY_OF_MONTH,1);

        sb.append("明天--" + DateUtil.format(calendar.getTime(),"yyyy-MM-dd") + "\n");
        sb.append(cityWeathers.get(1) + "\n");
        sb.append("------------------------------\n");

        calendar.add(Calendar.DAY_OF_MONTH,1);

        sb.append("后天--" + DateUtil.format(calendar.getTime(),"yyyy-MM-dd") + "\n");
        sb.append(cityWeathers.get(2));

        return sb.toString();
    }

    /**
     * 彩云天气温度、降雨量折线图数据填充
     * @param cityPosition
     * @throws Exception
     */
    public void genCaiYunWeatherImage(AdCityInfo cityPosition) throws Exception {

        String weatherObj = restTemplate.getForObject(CAI_YUN_WEATHER_API_URL + caiYunWeatherKey + "/" + cityPosition.getLng() + "," + cityPosition.getLat() + "/hourly.json", String.class);
        String result = JSON.parseObject(JSON.parseObject(weatherObj).get("result").toString()).get("hourly").toString();
        CaiYunWeather caiYunWeather = JSON.parseObject(result, CaiYunWeather.class);

        List<Temperature> temperature = caiYunWeather.getTemperature().subList(0,24);
        List<Precipitation> precipitation = caiYunWeather.getPrecipitation().subList(0,24);

        List<String> categories = temperature.stream().map(Temperature::getDatetime).collect(Collectors.toList());

        createCharts(categories, temperature.stream().map(Temperature::getValue), TEMPERATURE, TEMPERATURE_TITLE, TEMPERATURE_Y_UNIT, TEMPERATURE_FILE_PATH, temperature);
        createCharts(categories, precipitation.stream().map(Precipitation::getValue), PRECIPITATIONS, PRECIPITATIONS_TITLE, PRECIPITATIONS_Y_UNIT, PRECIPITATIONS_FILE_PATH, temperature);
    }

    /**
     * 绘制echarts
     * @param categories
     * @param doubleStream
     * @param temperature2
     * @param temperature_title
     * @param temperature_y_unit
     * @param temperature_file_path
     * @param temperature
     * @throws Exception
     */
    private void createCharts(List<String> categories, Stream<Double> doubleStream, String temperature2, String temperature_title, String temperature_y_unit, String temperature_file_path, List<Temperature> temperature) throws Exception {
        List<Double> temperatures = doubleStream.collect(Collectors.toList());
        List<Serie> temperatureSeries =  new Vector<>();
        temperatureSeries.add(new Serie(temperature2, temperatures.toArray()));
        CreateLineChart.CreateNewLineChartForPng(temperature_title,X_UNIT, temperature_y_unit, temperature_file_path,categories,temperatureSeries,PIC_WIDTH,PIC_HEIGHT);
    }

    /**
     * 根据高德地图api城市名称获取城市经纬度
     * @param cityName
     * @return
     */
    public AdCityInfo genCityPosition(String cityName) throws Exception {

        String requestUrl = GAO_DE_POSITION_API_URL + mapKey + "&city=" + cityName;
        String citySideJsonStr = restTemplate.getForObject(requestUrl, String.class);
        if(citySideJsonStr == null) {
            throw new Exception("查询异常!");
        }
        return JSON.parseObject(JSON.parseObject(JSON.parseObject(citySideJsonStr).get("result").toString()).get("location").toString(), AdCityInfo.class);
    }

    /**
     * 从redis获取城市地区编码
     * @return
     */
    private List<AdCityInfo> genRedisAdCityInfo(){

        String adCodeJson = redisService.get(CommonConstant.REDIS_CITY_KEY).toString();
        List<AdCityInfo> adCityInfoList = JSONArray.parseArray(adCodeJson, AdCityInfo.class);

        return CollectionUtils.isEmpty(adCityInfoList) ? Collections.EMPTY_LIST : adCityInfoList;
    }

    /**
     * 根据城市名称查询城市编码
     * @param cityName
     * @return
     */
    public AdCityInfo genAdCityInfo(String cityName){
        List<AdCityInfo> totalAdCityInfo = this.genRedisAdCityInfo();
        List<AdCityInfo> hitAdCityInfo = totalAdCityInfo.stream().filter(item -> item.getCityName().equals(cityName) || item.getCityName().substring(0, item.getCityName().length() - 1).equals(cityName)).collect(Collectors.toList());
        return CollectionUtils.isEmpty(hitAdCityInfo) ? null : hitAdCityInfo.get(0);
    }
}
