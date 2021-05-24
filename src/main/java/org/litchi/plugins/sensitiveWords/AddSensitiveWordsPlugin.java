package org.litchi.plugins.sensitiveWords;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.entity.SensitiveWord;
import org.litchi.mapper.SensitiveWordsMapper;
import org.litchi.utils.SensitiveUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * @author: gaozp
 * @date: 2021-05-21 14:47
 * @desc:
 */

@Component
public class AddSensitiveWordsPlugin extends BotPlugin {

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
        String msg = event.getRawMessage();
        long groupId = event.getGroupId();
        long userId = event.getUserId();
        if (msg.startsWith(CommandEnum.SENSITIVE_ADD_PREFIX.getCommand())) {
            String word = Optional.ofNullable(msg.substring(CommandEnum.SENSITIVE_ADD_PREFIX.getCommand().length())).orElse("").trim();

            if (adminId != userId) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("此操作仅管理员可执行").build(), false);
                return MESSAGE_BLOCK;
            }
            if (word != null && word.isEmpty()) {
                return MESSAGE_BLOCK;
            }
            if (sensitiveWordsMapper.findWord(word).isPresent()) {
                bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("敏感词已存在,请勿重复添加"), false);
                return MESSAGE_BLOCK;
            }
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(word);
            sensitiveWordsMapper.insert(sensitiveWord);
            bot.sendGroupMsg(groupId, Msg.builder().at(userId).text("敏感词添加成功"), false);

            sensitiveUtils.init();
        }
        return MESSAGE_IGNORE;
    }
}
