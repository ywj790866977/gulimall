package com.yanlaoge.gulimall.thirdparty.utils;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yanlaoge.gulimall.thirdparty.config.AliPayProperties;
import com.yanlaoge.gulimall.thirdparty.vo.PayVo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author rubyle
 */
@EnableConfigurationProperties(AliPayProperties.class)
@Component
@Data
@Slf4j
public class AlipayTemplate {

    @Resource
    private AliPayProperties aliPayProperties;

    public String pay(PayVo vo) throws AlipayApiException {

        //1、根据支付宝的配置生成一个支付客户端
        AlipayClient alipayClient = new DefaultAlipayClient(aliPayProperties.getGatewayUrl(),
                aliPayProperties.getAppId(), aliPayProperties.getMerchantPrivateKey(), "json",
                aliPayProperties.getCharset(), aliPayProperties.getAlipayPublicKey(), aliPayProperties.getSignType());

        //2、创建一个支付请求 //设置请求参数
        AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
        alipayRequest.setReturnUrl(aliPayProperties.getReturnUrl());
        alipayRequest.setNotifyUrl(aliPayProperties.getNotifyUrl());

//        alipayRequest.setBizContent("{\"out_trade_no\":\""+ outTradeNo +"\","
//                + "\"total_amount\":\""+ totalAmount +"\","
//                + "\"subject\":\""+ subject +"\","
//                + "\"body\":\""+ body +"\","
//                + "\"product_code\":\"FAST_INSTANT_TRADE_PAY\"}");
        alipayRequest.setBizContent(JSON.toJSONString(vo));
        //请求
        String result = alipayClient.pageExecute(alipayRequest).getBody();

        //会收到支付宝的响应，响应的是一个页面，只要浏览器显示这个页面，就会自动来到支付宝的收银台页面
        log.info("[pay] 支付宝的响应：", result);

        return result;

    }
}
