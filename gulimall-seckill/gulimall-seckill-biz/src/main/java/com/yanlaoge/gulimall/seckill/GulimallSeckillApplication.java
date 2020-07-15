package com.yanlaoge.gulimall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author 好人
 * @date 2020-07-08 19:37
 **/
//@EnableTransactionManagement
//@MapperScan("com.yanlaoge.gulimall.product.dao")
@EnableRedisHttpSession
@SpringBootApplication(scanBasePackages = {"com.yanlaoge.gulimall", "com.yanlaoge.common"})
@EnableDiscoveryClient
@EnableFeignClients("com.yanlaoge.gulimall")
public class GulimallSeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(GulimallSeckillApplication.class,args);
    }
}
