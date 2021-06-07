package org.litchi.plugins;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.ColorImage;
import org.litchi.entity.YellowImage;
import org.litchi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author: gaozp
 * @date: 2021-05-13 16:10
 * @desc:
 */
@Component
public class ColorImagePlugin extends BotPlugin {


    private final String UPPER_LIMIT = "涩图接口1达到请求上限！";

    private final String CALLBACK_TIP = "图片将在45s后撤回_";

    private final int MAX_IMAGE_NUM = 3;

    @Value("${adminId}")
    private long adminId;

    /**
     * 涩图接口1 url
     */
    private final String COLOR_IMAGE_URL_1 = "https://api.lolicon.app/setu/";

    /**
     * 涩图接口2 url
     */
    private final String COLOR_IMAGE_URL_2 = "https://api.dongmanxingkong.com/suijitupian/acg/1080p/index.php?return=json";

    private RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RedisService redisService;
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();
        long userId = event.getUserId();

        if (CommandEnum.COLOR_IMAGE_PREFIX.getCommand().equals(message)) {

            if(userId != adminId){
                Msg msg = Msg.builder().image(this.genColorImage3()).text("\n" + CALLBACK_TIP).at(event.getUserId());
                int messageId = bot.sendGroupMsg(groupId, msg, false).getMessageId();
                //单位是毫秒
                TimeUnit.MILLISECONDS.sleep(45000);
                bot.deleteMsg(messageId);
                return MESSAGE_IGNORE;
            }

            if(userId == adminId){
                YellowImage yellowImage = this.genColorImage4();
                Msg msg = Msg.builder().text(yellowImage.getTitle() + "\n");

                for(int i = 0; i < MAX_IMAGE_NUM; i ++){
                    Random random = new Random();
                    int nextInt = random.nextInt(yellowImage.getImageNum());
                    msg.image(yellowImage.getUrl() + nextInt + ".jpg");
                }

                int messageId = bot.sendGroupMsg(groupId, msg, false).getMessageId();
                //单位是毫秒
                TimeUnit.MILLISECONDS.sleep(30000);
                bot.deleteMsg(messageId);
                return MESSAGE_IGNORE;
            }
        }
        return MESSAGE_IGNORE;
    }

    private String genColorImage() {
        String queryResult = restTemplate.getForObject(COLOR_IMAGE_URL_1, String.class);
        String imageData = JSON.parseObject(queryResult).getString("data");
        if (imageData.length() == 2) {
            return UPPER_LIMIT;
        }
        ArrayList<ColorImage> colorImages = JSON.parseObject(imageData, new TypeReference<ArrayList<ColorImage>>() {
        });

        return "<image url='" + colorImages.get(0).getUrl() + "'/>\n" + CALLBACK_TIP;
    }

    private String genColorImage2() {
        String queryResult = restTemplate.getForObject(COLOR_IMAGE_URL_2, String.class);
        String imgurl = JSON.parseObject(queryResult).getString("imgurl");
        return "<image url='" + imgurl + "'/>\n" + CALLBACK_TIP;
    }


    private String genColorImage3() {

        Long colorImageCacheSize = redisService.lGetListSize(CommonConstant.REDIS_COLOR_IMAGE_KEY);

        Random random = new Random();
        int index = random.nextInt(colorImageCacheSize.intValue());
        return redisService.lGetIndex(CommonConstant.REDIS_COLOR_IMAGE_KEY, Long.valueOf(index)).toString();
    }

    private YellowImage genColorImage4(){

        Long imageSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_IMAGE_KEY);
        Random random = new Random();
        int index = random.nextInt(imageSize.intValue());
        String json = redisService.lGetIndex(CommonConstant.REDIS_YELLOW_IMAGE_KEY, Long.valueOf(index)).toString();
        YellowImage yellowImage = JSON.parseObject(json, YellowImage.class);
        return yellowImage;
    }

}

