package com.yanlaoge.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * GulimallGatewayApplication
 *
 * @author js-rubyle
 * @date 2020/5/14 13:43
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class GulimallGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallGatewayApplication.class, args);
	}

}
