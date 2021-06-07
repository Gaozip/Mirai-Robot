package org.litchi.plugins;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.YellowImage;
import org.litchi.entity.YellowNovel;
import org.litchi.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Random;

/**
 * @author: gaozp
 * @date: 2021-06-06 14:23
 * @desc:
 */
@Component
public class YellowNovelPlugin extends BotPlugin {

    private RedisService redisService;
    @Autowired
    public void setRedisService(RedisService redisService) {
        this.redisService = redisService;
    }

    @Value("${adminId}")
    private long adminId;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();
        long userId = event.getUserId();

        if(message.trim().equals(CommandEnum.YELLOW_NOVEL_PREFIX.getCommand())){

            if(userId != adminId){
                Msg msg = Msg.builder().text(CommonConstant.FEATURES_JUST_FOR_ADMIN);
                bot.sendGroupMsg(groupId,msg,false);
                return MESSAGE_IGNORE;
            }

            YellowNovel yellowNovel = this.genYellowNovel();
            Msg msg1 = Msg.builder().at(userId).text("【" + yellowNovel.getTitle() + "】");
            bot.sendGroupMsg(groupId,msg1,false);

            String content = yellowNovel.getContent();
            String[] cut = StrUtil.cut(content, 1000);
            Arrays.asList(cut).forEach(text ->{
                String s = text.replaceAll("　　", "\n      ");
                Msg msg2 = Msg.builder().text(s);
                bot.sendGroupMsg(groupId,msg2,false);
            });
        }
        return MESSAGE_IGNORE;
    }

    private YellowNovel genYellowNovel(){
        Long novelSize = redisService.lGetListSize(CommonConstant.REDIS_YELLOW_NOVEL_KEY);
        Random random = new Random();
        int index = random.nextInt(novelSize.intValue());
        String json = redisService.lGetIndex(CommonConstant.REDIS_YELLOW_NOVEL_KEY, Long.valueOf(index)).toString();
        YellowNovel yellowNovel = JSON.parseObject(json, YellowNovel.class);
        return yellowNovel;
    }
}
