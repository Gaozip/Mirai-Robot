package org.litchi.interceptor;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.lz1998.pbbot.handler.BotSessionInterceptor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:47
 * @desc:
 */

@Slf4j
public class MyWsInterceptor extends BotSessionInterceptor {

    @SneakyThrows
    @Override
    public boolean checkSession(@NotNull WebSocketSession session) {
        HttpHeaders headers = session.getHandshakeHeaders();
        String botId = headers.getFirst("x-self-id");
        log.info("新的连接{}" , botId);
        if ("123".equals(botId)) {
            System.out.println("机器人账号是123，关闭连接");
            session.close();
            // 禁止连接
            return false;
        }
        // 正常连接
        return true;
    }
}