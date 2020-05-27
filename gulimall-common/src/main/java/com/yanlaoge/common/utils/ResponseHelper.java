package com.yanlaoge.common.utils;

import com.yanlaoge.common.exception.ServiceException;

/**
 * ResponseHelper
 *
 * @author rubyle
 */
public class ResponseHelper {

    public static void execption(ServiceStatusCode httpStatus) {
        throw new ServiceException(httpStatus);
    }

    public static void execption(ServiceStatusCode httpStatus, Object data) {
        throw new ServiceException(httpStatus, data);
    }

    public static void execption(int status, String message) {
        throw new ServiceException(status, message);
    }

    public static Object execption(int status, String message, Object data) {
        throw new ServiceException(status, message, data);
    }

    /**
     * 服务端处理成功
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> success() {
        return new ResponseVo<T>(CommonStatusCode.SUCCESS);
    }

    /**
     * 服务端处理成功
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> success(T data) {
        return new ResponseVo<T>(data);
    }

    /**
     * 服务端数据校验失败（默认）
     *
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> badRequest() {
        return new ResponseVo<T>(CommonStatusCode.FAILURE);
    }

    /**
     * 服务端校验失败（请求数据语义有误）
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> badRequest(T data) {
        return new ResponseVo<T>(CommonStatusCode.FAILURE, data);
    }

    /**
     * 服务端校验失败（请求数据语义有误）
     *
     * @param message msg
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> badRequest(String message) {
        return new ResponseVo<T>(CommonStatusCode.FAILURE.getCode(), message);
    }

    /**
     * 服务端校验数据冲突（默认）
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> conflict() {
        return new ResponseVo<T>(CommonStatusCode.CONFLICT);
    }

    /**
     * 服务端校验数据冲突（数据重复...）
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> conflict(T data) {
        return new ResponseVo<T>(CommonStatusCode.CONFLICT, data);
    }

    /**
     * 服务端校验数据冲突（数据重复...）
     *
     * @param message msg
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> conflict(String message) {
        return new ResponseVo<T>(CommonStatusCode.CONFLICT.getCode(), message);
    }

    /**
     * 服务端内部错误
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> error() {
        return new ResponseVo<T>(CommonStatusCode.FAILURE);
    }

    /**
     * 服务端内部错误
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> error(T data) {
        return new ResponseVo<T>(CommonStatusCode.FAILURE, data);
    }

    /**
     * 服务端内部错误
     *
     * @param message 信息
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> error(String message) {
        return new ResponseVo<T>(CommonStatusCode.FAILURE.getCode(), message);
    }

    public static <T> ResponseVo<T> error(String message, String errorSn) {
        return new ResponseVo<T>(CommonStatusCode.FAILURE.getCode(), message, null, errorSn);
    }

    public static <T> ResponseVo<T> error(Integer code, String message) {
        return new ResponseVo<T>(code, message, null);
    }

    /**
     * 无权限
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> unauthorized() {
        return new ResponseVo<T>(CommonStatusCode.UNAUTHORIZED);
    }

    /**
     * 无权限
     *
     * @param <T> 泛型
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> unauthorized(T data) {
        return new ResponseVo<T>(CommonStatusCode.UNAUTHORIZED, data);
    }

    /**
     * 无权限
     *
     * @param message msg
     * @return ResponseVO
     */
    public static <T> ResponseVo<T> unauthorized(String message) {
        return new ResponseVo<T>(CommonStatusCode.UNAUTHORIZED.getCode(), message);
    }
}