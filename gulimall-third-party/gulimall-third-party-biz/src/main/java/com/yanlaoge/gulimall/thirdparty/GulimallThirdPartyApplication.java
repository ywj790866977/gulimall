package com.yanlaoge.gulimall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 第三方服务 启动类
 * @author  rubyle
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class GulimallThirdPartyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallThirdPartyApplication.class, args);
	}

}
