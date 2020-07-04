package com.yanlaoge.gulimall.thirdparty.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author 好人
 * @date 2020-07-04 12:56
 **/
@ConfigurationProperties(prefix = "alipay")
@Data
public class AliPayProperties {
    /**
     * 在支付宝创建的应用的id
     */
    private String appId;

    /**
     * 商户私钥，您的PKCS8格式RSA2私钥
     */
    private String merchantPrivateKey;
    /**
     * 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
     */
    private String alipayPublicKey;
    /**
     * 服务器[异步通知]页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 支付宝会悄悄的给我们发送一个请求，告诉我们支付成功的信息
     */
    private String notifyUrl;
    /**
     *  页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
     * 同步通知，支付成功，一般跳转到成功页
     */
    private String returnUrl;
    /**
     * 支付宝网关； https://openapi.alipaydev.com/gateway.do
     */
    private String gatewayUrl;
    /**
     * 签名方式
     */
    private String signType = "RSA2";
    /**
     * 字符编码格式
     */
    private String charset = "utf-8";
}
