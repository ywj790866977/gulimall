//package com.yanlaoge.gulimall.auth.config;
//
//import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.session.web.http.CookieSerializer;
//import org.springframework.session.web.http.DefaultCookieSerializer;
//
///**
// * @author 好人
// * @date 2020-06-08 23:42
// **/
//@Configuration
//public class SessionConfig {
//
//    @Bean
//    public CookieSerializer cookieSerializer(){
//        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
//        cookieSerializer.setDomainName("gulimall.com");
//        cookieSerializer.setCookieName("GULISESSION");
//        return cookieSerializer;
//    }
//
//    @Bean
//    public RedisSerializer<Object> springSessionDefaultRedisSerializer(){
//        return new GenericFastJsonRedisSerializer();
//    }
//}
//
