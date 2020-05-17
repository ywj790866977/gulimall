package com.yanlaoge.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * ServiceStatusCode
 *
 * @author js-rubyle
 * @date 2020/5/17 17:41
 */
@Getter
@AllArgsConstructor
public enum ServiceStatusCodeEum {
	/**
	 * 系统未知错误
	 */
	UNKNOW_EXCEPTION(10000,"系统未知错误"),
	/**
	 * 参数错误
	 */
	VALID_ERROR(10001,"参数校验失败");
	private Integer code;
	private String msg;
}
