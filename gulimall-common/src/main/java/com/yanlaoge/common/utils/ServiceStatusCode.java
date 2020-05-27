package com.yanlaoge.common.utils;

import lombok.Data;
import lombok.ToString;

/**
 * 服务code
 *
 * @author rubyle
 */
@Data
@ToString
public class ServiceStatusCode {
	private final int code;
	private final String msg;

	public ServiceStatusCode(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
