package org.litchi.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-22 16:53
 * @desc: 功能指令列表
 */

@Component
public class CommandPlugin extends BotPlugin {

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();

        if(message.equals(CommandEnum.FEATURES_COMMAND_PREFIX.getCommand())){
            Msg msg = Msg.builder().text(CommandEnum.getCommandList());
            bot.sendGroupMsg(groupId,msg,false);
        }
        return MESSAGE_IGNORE;
    }
}
