package com.yanlaoge.gulimall.search.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author rubyle
 */
@ConfigurationProperties(prefix = "gulimall.elasticsearch")
@Data
public class ElasticSearchConfigProperties {
    private String host;
    private Integer port;
}
