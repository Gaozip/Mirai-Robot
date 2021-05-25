package org.litchi.plugins;

import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.litchi.entity.WeiBoTop;
import org.litchi.service.WeiBoTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: gaozp
 * @date: 2021-05-24 0:23
 * @desc:
 */

@Component
public class WeiBoTopPlugin extends BotPlugin {

    @Value("${weiboTopNum}")
    private long weiboTopNum;

    private final int MAX_TOP_SIZE = 50;

    private WeiBoTopService weiBoTopService;
    @Autowired
    public void setWeiBoTopService(WeiBoTopService weiBoTopService) {
        this.weiBoTopService = weiBoTopService;
    }

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        long userId = event.getUserId();
        String message = event.getRawMessage();

        Boolean defaultWeiBoTop = message.equals(CommandEnum.DEFAULT_WEI_BO_TOP_PREFIX.getCommand());
        Boolean specifyWeiBoTop = message.startsWith(CommandEnum.SPECIFY_WEI_BO_TOP_PREFIX.getCommand());

        if(defaultWeiBoTop || specifyWeiBoTop){
            if(specifyWeiBoTop){
                if(message.length() == CommandEnum.SPECIFY_WEI_BO_TOP_PREFIX.getCommand().length()){
                    Msg msg = Msg.builder().text("参数不足！").at(userId);
                    bot.sendGroupMsg(groupId,msg,false);
                    return MESSAGE_IGNORE;
                }

                long num = 0;
                try {
                    num = Long.valueOf(message.substring(CommandEnum.SPECIFY_WEI_BO_TOP_PREFIX.getCommand().length()).trim());
                } catch (NumberFormatException e) {
                    Msg msg = Msg.builder().text("参数错误！").at(userId);
                    bot.sendGroupMsg(groupId,msg,false);
                    return MESSAGE_IGNORE;
                }
                if(num > MAX_TOP_SIZE){
                    Msg msg = Msg.builder().text("超过最大数量(上限50)！").at(userId);
                    bot.sendGroupMsg(groupId,msg,false);
                    return MESSAGE_IGNORE;
                }
                weiboTopNum = num;
            }
            List<WeiBoTop> weiBoTopList = weiBoTopService.opsForWeiBoTopNews();
            weiBoTopList = weiBoTopList.stream().filter(item -> item.getNo() == null || !item.getNo().equals("No.•")).limit(weiboTopNum + 1).collect(Collectors.toList());

            String text = weiBoTopList.stream().map(item -> item.toString()).collect(Collectors.joining("\n"));
            Msg msg = Msg.builder().text(text);
            bot.sendGroupMsg(groupId,msg,false);

            Msg msgDump = Msg.builder().share(CommonConstant.WEI_BO_TOP_URL, "进入微博头条", "你拉我唱大绵羊~", "");
            bot.sendGroupMsg(groupId,msgDump,false);
        }
        return MESSAGE_IGNORE;
    }
}
