package com.yanlaoge.common.exception;

import com.alibaba.fastjson.JSON;
import com.yanlaoge.common.utils.ResponseVo;
import com.yanlaoge.common.utils.ServiceStatusCode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * ServiceException
 *
 * @author rubyle
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 2305201073664000499L;
	/**
	 * 异常状态码
	 */
    private int code;
	/**
	 * 异常文字信息（也可自定有）
	 */
	private String msg;
	/**
	 * 异常自定义信息
	 */
    private Object data;

	/**
	 * @param httpStatus 返回状态码对象
	 */
	public ServiceException(ServiceStatusCode httpStatus) {
        this.code = httpStatus.getCode();
        this.msg = httpStatus.getMsg();
    }

	/**
	 *
	 * @param httpStatus 状态类
	 * @param data 数据
	 * @param <T> 泛型
	 */
    public <T> ServiceException(ServiceStatusCode httpStatus, T data) {
        this.code = httpStatus.getCode();
        this.msg = httpStatus.getMsg();
        this.data = data;
    }

    public ServiceException(int status, String message) {
        this.code = status;
        this.msg = message;
    }

    public <T> ServiceException(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResponseVo<Object> toResponseVO() {
        return new ResponseVo<>(code, msg, data);
    }

    public ResponseVo<Object> toResponseVO(ServiceException se) {
        return new ResponseVo<>(se.getCode(), se.getMsg(), se.getData());
    }
}