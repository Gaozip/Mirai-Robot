package org.litchi.plugins;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import net.lz1998.pbbot.bot.Bot;
import net.lz1998.pbbot.bot.BotPlugin;
import net.lz1998.pbbot.utils.Msg;
import onebot.OnebotEvent;
import org.jetbrains.annotations.NotNull;
import org.litchi.constant.CommandEnum;
import org.litchi.constant.CommonConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @author: gaozp
 * @date: 2021-05-26 9:26
 * @desc: 一言
 */

@Component
public class AWordPlugin extends BotPlugin {


    private RestTemplate restTemplate;
    @Autowired
    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 无类型
     */
    private final char NONE_TYPE = 'z';

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull OnebotEvent.GroupMessageEvent event) {

        long groupId = event.getGroupId();
        String message = event.getRawMessage();
        long userId = event.getUserId();

        if(message.contains(CommandEnum.A_WORD_PREFIX.getCommand())){

            if(message.trim().equals(CommandEnum.A_WORD_PREFIX.getCommand())){
                this.getData(NONE_TYPE);
                String type = CommonConstant.typesMap.get(getType);
                Msg msg = Msg.builder().at(userId).text("\n『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
                bot.sendGroupMsg(groupId,msg,false);
            }

            if(message.trim().length() > CommandEnum.A_WORD_PREFIX.getCommand().length() && message.trim().startsWith(CommandEnum.A_WORD_PREFIX.getCommand())){
                String command = message.substring(CommandEnum.A_WORD_PREFIX.getCommand().length()).trim();
                char requestType = command.trim().charAt(0);
                if(CommonConstant.typesMap.containsKey(requestType)){
                    this.getData(requestType);
                    String type = CommonConstant.typesMap.get(getType);
                    Msg msg = Msg.builder().at(userId).text("\n『" + hitokoto + "』\n" + "出自：" + from + "\n" + "类型：" + type);
                    bot.sendGroupMsg(groupId,msg,false);
                }
                if(!CommonConstant.typesMap.containsKey(requestType)){
                    Msg msg = Msg.builder().at(userId).text("指令格式异常！请重试！(a-k)");
                    bot.sendGroupMsg(groupId,msg,false);
                }
            }
        }
        return MESSAGE_IGNORE;
    }

    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private String hitokoto;
    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private String from;
    @JSONField(serialzeFeatures = {SerializerFeature.WriteMapNullValue})
    private char getType;


    public void getData(char type) {
        String url = CommonConstant.A_WORD_URL + type;
        if (type == NONE_TYPE) {
            url = CommonConstant.A_WORD_URL;
        }
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSONObject.parseObject(result);
        hitokoto = jsonObject.getString("hitokoto");
        from = jsonObject.getString("from");
        getType = jsonObject.getString("type").charAt(0);
    }
}
