package org.litchi.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-13 16:10
 * @desc:
 */

@Component
public class RecallPlugin extends BotPlugin {

    @Override
    public int onGroupRecallNotice(@NotNull Bot bot, @NotNull OnebotEvent.GroupRecallNoticeEvent event) {

        long groupId = event.getGroupId();
        long userId = event.getUserId();

        String card = bot.getGroupMemberInfo(groupId, userId, false).getCard();

        long loginUserId = bot.getLoginInfo().getUserId();

        if(userId != loginUserId){
            String message = "嘿嘿嘿，来看看" + card+ "撤回了什么：" + bot.getMsg(event.getMessageId()).getRawMessage();
            bot.sendGroupMsg(groupId, message,false);
        }
        return MESSAGE_IGNORE;
    }
}
