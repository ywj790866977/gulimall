package com.yanlaoge.gulimall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rubyle
 * @date 2020-06-06
 */
@ConfigurationProperties(prefix = "gulimall.thread")
@Data
public class ThreadPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;

}
