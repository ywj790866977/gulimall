package com.yanlaoge.gulimall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.yanlaoge.gulimall.member.dao")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.yanlaoge.gulimall.member.feign")
@ComponentScan("com.yanlaoge.gulimall.coupon")
public class GulimallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallMemberApplication.class, args);
	}

}
