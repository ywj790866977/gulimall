package com.yanlaoge.gulimall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author rubyle
 */

@MapperScan("com.yanlaoge.gulimall.order.dao")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages ={"com.yanlaoge.gulimall","com.yanlaoge.common"})
@EnableFeignClients(basePackages = "com.yanlaoge.gulimall")
@EnableRabbit
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
