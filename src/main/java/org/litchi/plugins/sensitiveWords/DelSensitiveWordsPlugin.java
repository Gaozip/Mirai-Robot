package org.litchi.plugins.sensitiveWords;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.mapper.SensitiveWordsMapper;
import org.litchi.utils.SensitiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author: gaozp
 * @date: 2021-05-21 16:07
 * @desc:
 */

@Component
public class DelSensitiveWordsPlugin extends BotPlugin {

    @Value("${adminId}")
    private long adminId;

    private SensitiveWordsMapper sensitiveWordsMapper;
    @Autowired
    public void setSensitiveWordRepository(SensitiveWordsMapper sensitiveWordsMapper) {
        this.sensitiveWordsMapper = sensitiveWordsMapper;
    }

    private SensitiveUtils sensitiveUtils;
    @Autowired
    public void setSensitiveUtils(SensitiveUtils sensitiveUtils){
        this.sensitiveUtils = sensitiveUtils;
    }


    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();
        long userId = event.getUserId();

        if(message.startsWith(CommandEnum.SENSITIVE_DEL_PREFIX.getCommand())){

            String word = Optional.ofNullable(message.substring(CommandEnum.SENSITIVE_DEL_PREFIX.getCommand().length())).orElse("").trim();

            if (adminId != userId) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_BLOCK;
            }
            if (word != null && word.isEmpty()) {
                return MESSAGE_BLOCK;
            }

            if (!sensitiveWordsMapper.findWord(word).isPresent()) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("该敏感词不存在!"), false);
                return MESSAGE_BLOCK;
            }

            sensitiveWordsMapper.deleteByWord(word);
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("敏感词移除成功"), false);
            sensitiveUtils.init();
        }
        return MESSAGE_IGNORE;
    }
}
