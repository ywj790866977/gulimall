package com.yanlaoge.common.utils;

/**
 * 框架定义状态码
 * 
 * @author xxx
 */
public class CommonStatusCode {
	public static final ServiceStatusCode SUCCESS = new ServiceStatusCode(0, "操作成功");
	public static final ServiceStatusCode FAILURE = new ServiceStatusCode(10001, "操作失败，请重试！");
	public static final ServiceStatusCode UNAUTHORIZED = new ServiceStatusCode(10401, "后台管理登录信息已失效");
	public static final ServiceStatusCode CONFLICT = new ServiceStatusCode(10409, "校验数据失败");
	public static final ServiceStatusCode PRODUCT_UP_EXCEPTION = new ServiceStatusCode(11000,"商品上架异常");
}