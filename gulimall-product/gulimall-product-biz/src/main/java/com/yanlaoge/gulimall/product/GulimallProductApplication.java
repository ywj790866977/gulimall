package com.yanlaoge.gulimall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 启动类
 * @author rubyle
 */
@SpringBootApplication(scanBasePackages ={"com.yanlaoge.gulimall","com.yanlaoge.common"})
@MapperScan("com.yanlaoge.gulimall.product.dao")
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignClients("com.yanlaoge.gulimall")
public class GulimallProductApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallProductApplication.class, args);
	}

}
