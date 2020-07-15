package com.yanlaoge.common.utils;

/**
 * 框架定义状态码
 * 
 * @author xxx
 */
public class CommonStatusCode {
	public static final ServiceStatusCode SUCCESS = new ServiceStatusCode(0, "操作成功");
	public static final ServiceStatusCode FAILURE = new ServiceStatusCode(10001, "操作失败，请重试！");
	public static final ServiceStatusCode TO_MANY_REQUEST = new ServiceStatusCode(10002, "请求流量过大");
	public static final ServiceStatusCode UNAUTHORIZED = new ServiceStatusCode(10401, "后台管理登录信息已失效");
	public static final ServiceStatusCode CONFLICT = new ServiceStatusCode(10409, "校验数据失败");
	public static final ServiceStatusCode PRODUCT_UP_EXCEPTION = new ServiceStatusCode(11000,"商品上架异常");

	public static final String USER_LOGIN_TIME_OUT_KEY = "kkcloud:sso:login:timeout:admin:";
	/**
	 * 用户登入token前缀
	 */
	public static final String USER_LOGIN_TOKEN_KEY_PREFIX = "kkcloud:sso:login:token:admin:";
	/**
	 * 存取用户token的key前缀
	 */
	public static final String USER_LOGIN_TOKEN_VALUE_KEY_PREFIX="kkcloud:sso:login:token:value:";
	//获取站点ID前缀   kc:site:ZK:ZK_alsfkjas08asdf0as8dfasd0f943h4k2jh42k3
	public static final String REDIS_KEY_SITE_TYPE = "kc:site:";
	/**
	 * 资源列表
	 */
	public static final String USER_LOGIN_RESOURCE_URL="kkcloud:sso:login:resource:url:";
}