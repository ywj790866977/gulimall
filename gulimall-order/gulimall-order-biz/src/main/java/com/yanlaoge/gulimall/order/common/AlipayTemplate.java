package com.yanlaoge.gulimall.order.common;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.yanlaoge.gulimall.order.config.AliPayProperties;
import com.yanlaoge.gulimall.order.vo.PayVo;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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

    @SneakyThrows
    public String pay(PayVo vo)  {

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
        log.info("[pay] 支付宝的响应：{}", result);

        return result;

    }

    @SneakyThrows
    public boolean checkSign(Map<String, String[]> aliParams){
        return AlipaySignature.rsaCheckV1(conversionParam(aliParams), aliPayProperties.getAlipayPublicKey(),
                aliPayProperties.getCharset(), aliPayProperties.getSignType());

    }

    private Map<String, String> conversionParam(Map<String, String[]> aliParams){
        Map<String, String> conversionParams = new HashMap<String, String>(16);
        for (Iterator<String> iter = aliParams.keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            String[] values = aliParams.get(key);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "UTF-8");
            conversionParams.put(key, valueStr);
        }
        return conversionParams;
    }
}
