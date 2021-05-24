package org.litchi.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.AdCityInfo;
import org.litchi.service.WeatherService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Administrator
 * @Desc 天气组件
 */

@Component
public class WeatherPlugin extends BotPlugin {


    @Value("${defaultCityName}")
    private String defaultCityName;

    @Resource
    private WeatherService weatherService;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String text = event.getRawMessage();

        Boolean defaultCityQuery = text.equals(CommandEnum.DEFAULT_WEATHER_PREFIX.getCommand());
        Boolean cityNameQuery = text.startsWith(CommandEnum.SPECIFY_WEATHER_PREFIX.getCommand());

        if (defaultCityQuery || cityNameQuery) {

            //要查询的城市名称
            String cityName = defaultCityName;

            if (cityNameQuery) {
                cityName = Optional.ofNullable(text.substring(CommandEnum.SPECIFY_WEATHER_PREFIX.getCommand().length())).orElse("");
            }

            //获取城市adCode - 高德api
            AdCityInfo hitAdCityInfo = weatherService.genAdCityInfo(cityName);

            if (hitAdCityInfo == null) {
                bot.sendGroupMsg(groupId, "高德地图api未查询到该城市的adCode！请重试！", false);
                return MESSAGE_IGNORE;
            }

            //获取近3天天气预报
            bot.sendGroupMsg(groupId, weatherService.genGaoDeWeathers(hitAdCityInfo), false);

            try {

                //获取城市经纬度 - 彩云api
                AdCityInfo cityPosition = weatherService.genCityPosition(cityName);

                //绘制彩云天气chart图片
                weatherService.genCaiYunWeatherImage(cityPosition);
                Msg msg1 = Msg.builder().image(CommonConstant.TEMPERATURE_IMAGE_URL).text("\n24h温度折线图");
                bot.sendGroupMsg(groupId, msg1, false);
                Msg msg2 = Msg.builder().image(CommonConstant.PRECIPITATION_IMAGE_URL).text("\n24h降雨量折线图");
                bot.sendGroupMsg(groupId, msg2, false);

            } catch (Exception e) {
                bot.sendGroupMsg(groupId, "高德地图api未查询到该城市的经纬度！请重试！", false);
            }
        }
        return MESSAGE_IGNORE;
    }
}
