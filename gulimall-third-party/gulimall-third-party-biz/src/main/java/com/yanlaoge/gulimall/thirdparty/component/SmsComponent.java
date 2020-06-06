package com.yanlaoge.gulimall.thirdparty.component;

import com.yanlaoge.gulimall.thirdparty.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rubyle
 * @date 2020-06-06
 */
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Component
@Data
public class SmsComponent {
    private String host;
    private String path;
    private String skin;
    private String sign;
    private String appcode;

    public void sendSmsCode(String phone,String code){

        String method = "GET";
        Map<String,String> headers = new HashMap<>();
        headers.put("Authorization","APPCODE "+appcode);
        Map<String,String> querys = new HashMap<>();
        querys.put("code",code);
        querys.put("phone",phone);
        querys.put("skin",skin);
        querys.put("sign",sign);

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
