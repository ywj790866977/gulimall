package com.yanlaoge.gulimall.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class GulimallSsoServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallSsoServerApplication.class, args);
    }

}
