package org.litchi.plugins;

import lombok.SneakyThrows;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.litchi.constant.Strings;
import org.litchi.utils.FontUtils;
import org.springframework.stereotype.Component;


/**
 * @author: gaozp
 * @date: 2021-05-12 17:04
 * @desc:
 */
@Component
public class World2ImagePlugin extends BotPlugin {

    @SneakyThrows
    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();
        if (message.startsWith(CommandEnum.WORD_TO_IMAGE_PREFIX.getCommand())) {
            String command = message.substring(CommandEnum.WORD_TO_IMAGE_PREFIX.getCommand().length()).trim();
            if (command.indexOf(Strings.SLASH) != -1) {

                String up = command.substring(0, command.indexOf(Strings.SLASH));
                String down = command.substring(command.indexOf(Strings.SLASH) + 1);

                if(StringUtils.isNotBlank(up) && StringUtils.isNotBlank(down)){
                    String url = CommonConstant.REQUEST_MAIN_URL + "top=" + up + "&bottom=" + down;
                    Msg msg = Msg.builder().image(FontUtils.utfURL(url));
                    bot.sendGroupMsg(groupId, msg, false);
                }else{
                    Msg msg = Msg.builder().text("命令格式不正确！");
                    bot.sendGroupMsg(groupId,msg,false);
                }
            }
        }
        return MESSAGE_IGNORE;
    }
}
