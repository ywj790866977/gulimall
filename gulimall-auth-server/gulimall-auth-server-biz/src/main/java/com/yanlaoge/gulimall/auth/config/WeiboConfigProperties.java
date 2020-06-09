package com.yanlaoge.gulimall.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author 好人
 * @date 2020-06-09 08:50
 **/
@Configuration
@ConfigurationProperties(prefix = "gulimall.oauth2.weibo")
@EnableConfigurationProperties(WeiboConfigProperties.class)
@Data
public class WeiboConfigProperties {
    private String host;
    private String path;
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
