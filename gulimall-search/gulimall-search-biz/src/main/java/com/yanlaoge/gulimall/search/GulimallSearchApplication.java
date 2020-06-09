package com.yanlaoge.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 启动类
 *
 * @author rubyle
 */
@SpringBootApplication(scanBasePackages = {"com.yanlaoge.gulimall", "com.yanlaoge.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yanlaoge.gulimall")
@EnableRedisHttpSession
public class GulimallSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSearchApplication.class, args);
    }

}
