package com.yanlaoge.gulimall.thirdparty;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javax.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author 好人
 * @date 2020-05-17 09:03
 **/
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestDemo01 {
    @Resource
    OSSClient ossClient;

    @Test
    public void test02() throws FileNotFoundException {
        InputStream inputStream = new FileInputStream("C:\\Users\\Lenovo\\Pictures\\23ffasd.jpg");
        ossClient.putObject("yanmall","246x0w.png",inputStream);
        ossClient.shutdown();

    }

    @Test
    public void test01() throws FileNotFoundException {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 云账号AccessKey有所有API访问权限，建议遵循阿里云安全最佳实践，创建并使用RAM子账号进行API访问或日常运维，请登录 https://ram.console.aliyun.com 创建。
        String accessKeyId = "LTAI4GEEH4Asm83pNRUMd1S5";
        String accessKeySecret = "OCTLZA2FFuRDvUPRYQ6KoyUsMEORxy";

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 上传文件流。
        InputStream inputStream = new FileInputStream("/Users/yan/Pictures/images.jpeg");
        ossClient.putObject("yanmall", "images.jpeg", inputStream);

        // 关闭OSSClient。
        ossClient.shutdown();
        System.out.println("上传成功..");
    }
}
