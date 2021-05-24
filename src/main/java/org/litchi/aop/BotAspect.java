package org.litchi.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * @author: gaozp
 * @date: 2021-05-13 14:30
 * @desc:
 */

@Slf4j
@Aspect
@Component
public class BotAspect {

    @Pointcut("execution(public * net.lz1998.pbbot.bot.ApiSender.*(..))")
    private void pointcut() {
    }

    @Around("pointcut()")
    private Object logHandler(ProceedingJoinPoint pjp) throws Throwable {
        try {
            log.info(pjp.getSignature() + " 被调用");
            return pjp.proceed();
        } catch (Throwable e) {
            log.error("Handling error");
            throw e;
        }
    }
}
