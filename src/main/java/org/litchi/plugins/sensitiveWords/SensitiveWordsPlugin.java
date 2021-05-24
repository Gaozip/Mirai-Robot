package org.litchi.plugins.sensitiveWords;

import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.utils.SensitiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-21 14:33
 * @desc:
 */
@Slf4j
@Component
public class SensitiveWordsPlugin extends BotPlugin {

    @Value("${adminId}")
    private long adminId;

    private SensitiveUtils sensitiveUtils;
    @Autowired
    public void setSensitiveUtils(SensitiveUtils sensitiveUtils) {
        this.sensitiveUtils = sensitiveUtils;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {
        String msg = event.getRawMessage().replaceAll("\\s*", "");
        long userId = event.getUserId();
        long groupId = event.getGroupId();
        int msgId = event.getMessageId();

        String hitSensitive = sensitiveUtils.hitSensitive(msg);
        // 检查是否为敏感词
        if (hitSensitive.length() > 0) {
            if (msgId <= 0) {
                return MESSAGE_IGNORE;
            }

            Boolean isEditSensitive = msg.startsWith(CommandEnum.SENSITIVE_ADD_PREFIX.getCommand()) || msg.startsWith(CommandEnum.SENSITIVE_DEL_PREFIX.getCommand());
            if(userId == adminId && isEditSensitive){
                return MESSAGE_IGNORE;
            }

            // 如果获取到了消息ID则撤回消息
            bot.deleteMsg(msgId);
            Msg sendMsg = Msg.builder()
                    .at(userId)
                    .text("检测到您发送的内容存在不适当的内容:\n\uD83D\uDC49 "+  hitSensitive +"\n请注意言行哟～");
            bot.sendGroupMsg(groupId, sendMsg.build(), false);
            log.info("检测到敏感词: [{}], 来自群: [{}], 发送者: [{}]", msg, groupId, userId);
        }
        return MESSAGE_IGNORE;
    }
}
