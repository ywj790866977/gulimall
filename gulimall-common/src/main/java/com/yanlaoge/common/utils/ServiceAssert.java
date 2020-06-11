package com.yanlaoge.common.utils;

import com.yanlaoge.common.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

import java.io.Serializable;

/**
 * 断言某些对象或值是否符合规定，否则抛出异常。经常用于做变量检查
 *
 * @author rubyle
 */
public class ServiceAssert implements Serializable {

    public static ServiceException serviceException(Integer code, String msg) {
        return new ServiceException(code, msg);
    }

    public static ServiceException serviceException(Integer code, String msg, Object data) {
        return new ServiceException(code, msg, data);
    }

    public static ServiceException serviceException(ServiceStatusCode status, Object data) {
        return new ServiceException(status, data);
    }

    public static ServiceException serviceException(ServiceStatusCode status) {
        return new ServiceException(status);
    }

    public static void isFalse(boolean expression, String msg) {
        if (!expression) {
            throw serviceException(CommonStatusCode.FAILURE.getCode(), msg);
        }
    }

    public static void isFalse(boolean expression, int code, String msg) {
        if (!expression) {
            throw serviceException(code, msg);
        }
    }

    public static void isFalse(boolean expression, Integer code, String msg) {
        if (!expression) {
            throw serviceException(code, msg);
        }
    }


    public static void isFalse(boolean expression, Integer code, String msg, Object data) {
        if (!expression) {
            throw serviceException(code, msg, data);
        }
    }

    public static void isFalse(boolean expression, ServiceStatusCode status) {
        if (!expression) {
            throw serviceException(status);
        }
    }

    public static void isFalse(boolean expression, ServiceStatusCode status, Object data) {
        if (!expression) {
            throw serviceException(status, data);
        }
    }

    public static void isFalse(Object object, Integer code, String msg) {
        if (object != null) {
            throw serviceException(code, msg);
        }
    }

    public static void isNull(Object object, int code, String msg) {
        if (object == null) {
            throw serviceException(code, msg);
        }
    }
    public static void isNull(Object object, Integer code, String msg) {
        if (object == null) {
            throw serviceException(code, msg);
        }
    }

    public static void isNull(Object object, Integer code, String msg, Object data) {
        if (object == null) {
            throw serviceException(code, msg, data);
        }
    }

    public static void isNull(Object object, ServiceStatusCode status) {
        if (object == null) {
            throw serviceException(status);
        }
    }

    public static void isNull(Object object, ServiceStatusCode status, Object data) {
        if (object == null) {
            throw serviceException(status, data);
        }
    }

    public static void isBlank(String str, Integer code, String msg) {
        if (StringUtils.isBlank(str)) {
            throw serviceException(code, msg);
        }
    }

    public static void isBlank(String str, Integer code, String msg, Object data) {
        if (StringUtils.isBlank(str)) {
            throw serviceException(code, msg, data);
        }
    }

    public static void isEmpty(Object object, Integer code, String msg) {
        if (ObjectUtils.isEmpty(object)) {
            throw serviceException(code, msg);
        }
    }

    public static void isEmpty(Object object, Integer code, String msg, Object data) {
        if (ObjectUtils.isEmpty(object)) {
            throw serviceException(code, msg, data);
        }
    }

    public static void isEmpty(Object object, ServiceStatusCode status) {
        if (ObjectUtils.isEmpty(object)) {
            throw serviceException(status);
        }
    }

    public static void isEmpty(Object object, ServiceStatusCode status, Object data) {
        if (ObjectUtils.isEmpty(object)) {
            throw serviceException(status, data);
        }
    }
}