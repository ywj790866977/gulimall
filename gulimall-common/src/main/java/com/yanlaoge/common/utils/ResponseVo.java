package com.yanlaoge.common.utils;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回处理
 *
 * @author rubyle
 */
@Data
@NoArgsConstructor
public class ResponseVo<T> {

    /**
     * 返回状态
     */
    private int code;
    /**
     * 描述信息
     */
    private String msg;

    /**
     * 错误序号
     */
    private String errorSn;

    /**
     * 数据
     */
    private T data;


    public ResponseVo(ServiceStatusCode httpStatus) {
        this.code = httpStatus.getCode();
        this.msg = httpStatus.getMsg();
    }

    public ResponseVo(ServiceStatusCode httpStatus, T data) {
        this.code = httpStatus.getCode();
        this.msg = httpStatus.getMsg();
        this.data = data;
    }

    public ResponseVo(int status, String message) {
        this.code = status;
        this.msg = message;
    }

    public ResponseVo(int status, String message, T data) {
        this.code = status;
        this.msg = message;
        this.data = data;
    }

    public ResponseVo(int status, String message, T data, String errorSn) {
        this.code = status;
        this.msg = message;
        this.data = data;
        this.errorSn = errorSn;
    }

    public ResponseVo(T data) {
        this.code = CommonStatusCode.SUCCESS.getCode();
        this.msg = CommonStatusCode.SUCCESS.getMsg();
        this.data = data;
    }
}