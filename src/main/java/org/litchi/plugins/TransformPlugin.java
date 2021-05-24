package org.litchi.plugins;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.utils.FontUtils;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-14 17:25
 * @desc:
 */

@Slf4j
@Component
public class TransformPlugin extends BotPlugin {

    private final String MSG_PREFIX = ".transform";
    private final String API_URL = "http://tts.baidu.com/text2audio?lan=zh&ie=UTF-8&spd=2&text=";

    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();

        if(message.startsWith(MSG_PREFIX)){

            String recordMsg = message.substring(MSG_PREFIX.length());
            String url = FontUtils.utfURL(API_URL + recordMsg);
            log.info(url);
            Msg msg = Msg.builder().record(url);
            bot.sendGroupMsg(groupId,msg,false);
        }
        return MESSAGE_IGNORE;
    }
}
