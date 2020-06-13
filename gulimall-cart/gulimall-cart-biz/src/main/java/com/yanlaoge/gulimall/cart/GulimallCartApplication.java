package com.yanlaoge.gulimall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author rubyle
 */
@SpringBootApplication(scanBasePackages ={"com.yanlaoge.gulimall","com.yanlaoge.common"})
@EnableDiscoveryClient
@MapperScan("com.yanlaoge.gulimall.cart.dao")
@EnableFeignClients(basePackages = "com.yanlaoge.gulimall")
public class GulimallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class, args);
    }

}
