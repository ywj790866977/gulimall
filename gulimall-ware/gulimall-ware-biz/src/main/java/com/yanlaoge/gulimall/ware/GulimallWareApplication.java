package com.yanlaoge.gulimall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author rubyle
 */
@EnableRabbit
@EnableTransactionManagement
@SpringBootApplication(scanBasePackages = {"com.yanlaoge.gulimall", "com.yanlaoge.common"})
@MapperScan("com.yanlaoge.gulimall.ware.dao")
@EnableDiscoveryClient
@EnableFeignClients("com.yanlaoge.gulimall")
public class GulimallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallWareApplication.class, args);
	}

}
