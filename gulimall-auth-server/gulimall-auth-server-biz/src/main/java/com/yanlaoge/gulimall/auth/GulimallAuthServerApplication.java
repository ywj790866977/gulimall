package com.yanlaoge.gulimall.auth;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author rubyle
 */
@SpringBootApplication(scanBasePackages ={"com.yanlaoge.gulimall","com.yanlaoge.common"})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yanlaoge.gulimall")
public class GulimallAuthServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class,args);
    }
}
