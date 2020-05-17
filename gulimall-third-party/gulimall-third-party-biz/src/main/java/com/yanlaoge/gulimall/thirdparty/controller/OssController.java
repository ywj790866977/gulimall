package com.yanlaoge.gulimall.thirdparty.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.yanlaoge.common.utils.DateUtils;
import com.yanlaoge.common.utils.R;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * OssController
 *
 * @author js-rubyle
 * @date 2020/5/17 11:01
 */
@RestController
@RequestMapping("/oss")
public class OssController {

	@Value("${spring.cloud.alicloud.oss.endpoint}")
	private String endpoint;
	@Value("${spring.cloud.alicloud.oss.bucket}")
	private String bucket;
	@Value("${spring.cloud.alicloud.access-key}")
	private String accessId;

	@Resource
	private OSSClient ossClient;


	@GetMapping("/policy")
	public R oss() {
		//  https://yanmall.oss-cn-beijing.aliyuncs.com/
		// 请填写您的 bucketname 。
		// host的格式为 bucketname.endpoint
		String host = "https://" + bucket + "." + endpoint;
		// callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
		// String callbackUrl = "http://88.88.88.88:8888"
		// 用户上传文件时指定的前缀。
		String format = DateUtils.format(new Date(), "yyyy-MM-dd");
		String dir = format + "/";

		// 创建OSSClient实例。
		Map<String, String> respMap = null;
		try {
			long expireTime = 30;
			long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
			Date expiration = new Date(expireEndTime);
			// PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
			PolicyConditions policyConds = new PolicyConditions();
			policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
			policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

			String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
			byte[] binaryData = postPolicy.getBytes("utf-8");
			String encodedPolicy = BinaryUtil.toBase64String(binaryData);
			String postSignature = ossClient.calculatePostSignature(postPolicy);

			respMap = new LinkedHashMap<String, String>();
			respMap.put("accessid", accessId);
			respMap.put("policy", encodedPolicy);
			respMap.put("signature", postSignature);
			respMap.put("dir", dir);
			respMap.put("host", host);
			respMap.put("expire", String.valueOf(expireEndTime / 1000));
			// respMap.put("expire", formatISO8601Date(expiration));
		} catch (Exception e) {
			// Assert.fail(e.getMessage());
			System.out.println(e.getMessage());
		} finally {
			ossClient.shutdown();
		}
		return R.ok().put("data",respMap);
	}
}
