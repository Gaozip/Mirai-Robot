package org.litchi;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;


/**
 * @author Administrator
 * @desc QQ机器人
 */

@MapperScan("org.litchi.mapper")
@EnableScheduling
@EnableAspectJAutoProxy
@SpringBootApplication
public class MiraiRobotApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MiraiRobotApplication.class);
        application.addListeners(new ApplicationPidFileWriter("./app.pid"));
        application.run();
    }

    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
