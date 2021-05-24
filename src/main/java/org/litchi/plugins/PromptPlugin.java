package org.litchi.plugins;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.AdCityInfo;
import org.litchi.entity.WeiBoTop;
import org.litchi.service.BotSendMsgService;
import org.litchi.service.WeatherService;
import org.litchi.service.WeiBoTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: gaozp
 * @date: 2021-05-12 15:22
 * @desc:
 */

@Component
@Slf4j
public class PromptPlugin extends BotPlugin {

    @Value("${groupId}")
    private Long groupId;

    @Value("${defaultCityName}")
    private String defaultCityName;

    @Value("${weiboTopNum}")
    private long weiboTopNum;

    @Resource
    private WeatherService weatherService;

    @Resource
    private BotSendMsgService botSendMsgService;

    private WeiBoTopService weiBoTopService;
    @Autowired
    public void setWeiBoTopService(WeiBoTopService weiBoTopService) {
        this.weiBoTopService = weiBoTopService;
    }

    /**
     * 提醒喝水小助手
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0 9,10,11,13,14,15,16,17,18 ? * 1,2,3,4,5",zone = "Asia/Shanghai")
    public void promptDrinkWater() throws InterruptedException {

        Msg msg = Msg.builder().image(CommonConstant.DRINK_IMAGE_URL);
        botSendMsgService.sendGroupMsg(groupId,msg);
    }


    /**
     * 天气预报
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0 9 * * ?",zone = "Asia/Shanghai")
    public void promptWeather() throws InterruptedException {

        AdCityInfo adCityInfo = weatherService.genAdCityInfo(defaultCityName);

        Msg msg = Msg.builder().text(weatherService.genGaoDeWeathers(adCityInfo));

        //获取近3天天气预报
        botSendMsgService.sendGroupMsg(groupId,msg);

        //获取城市经纬度 - 彩云api
        AdCityInfo cityPosition = null;
        try {
            cityPosition = weatherService.genCityPosition(defaultCityName);
            //绘制彩云天气chart图片
            weatherService.genCaiYunWeatherImage(cityPosition);
            Msg msg1 = Msg.builder().image(CommonConstant.TEMPERATURE_IMAGE_URL).text("\n24h温度折线图");
            Msg msg2 = Msg.builder().image(CommonConstant.PRECIPITATION_IMAGE_URL).text("\n24h降雨量折线图");

            botSendMsgService.sendGroupMsg(groupId,msg1);
            botSendMsgService.sendGroupMsg(groupId,msg2);
        } catch (Exception e) {
            Msg msge = Msg.builder().text("高德地图api未查询到该城市的经纬度！请重试！");
            botSendMsgService.sendGroupMsg(groupId,msge);
            log.error(e.getMessage());
        }
    }

    /**
     * 微博头条
     * @throws InterruptedException
     */
    @Scheduled(cron = "0 0 9 * * ?",zone = "Asia/Shanghai")
    public void promptWeiBoTop() throws InterruptedException {
        List<WeiBoTop> weiBoTopList = weiBoTopService.opsForWeiBoTopNews();
        weiBoTopList = weiBoTopList.stream().limit(weiboTopNum + 1).collect(Collectors.toList());

        String text = weiBoTopList.stream().map(item -> item.toString()).collect(Collectors.joining("\n"));
        Msg msg = Msg.builder().text(text);
        botSendMsgService.sendGroupMsg(groupId,msg);

        Msg msgDump = Msg.builder().share(CommonConstant.WEI_BO_TOP_URL, "进入微博头条", "你拉我唱大绵羊~", "");
        botSendMsgService.sendGroupMsg(groupId,msgDump);
    }
}
