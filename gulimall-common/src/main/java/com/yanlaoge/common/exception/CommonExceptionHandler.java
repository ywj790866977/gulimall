package com.yanlaoge.common.exception;

import com.yanlaoge.common.utils.ResponseHelper;
import com.yanlaoge.common.utils.ResponseVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ValidationException;
import java.util.List;
import java.util.UUID;


/**
 * 全局异常处理
 *
 * @author rubyle
 */
@ResponseBody
@ControllerAdvice
public class CommonExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommonExceptionHandler.class);
    
    private static final String GLOBEL_ERROR_MSG = "服务端发生异常！";

    @ExceptionHandler(value = ServiceException.class)
    public ResponseVo<Object> serviceErrorHandler(HttpServletResponse resp, ServiceException ex) throws Exception {
    	logger.error("ServiceException: ", ex);
        return ex.toResponseVO(ex);
    }

    @ExceptionHandler(value = SecurityException.class)
    public ResponseVo<String> securityErrorHandler(HttpServletResponse resp, SecurityException ex) throws Exception {
        logger.warn("SecurityException: ", ex);
        return ResponseHelper.unauthorized(ex.getMessage());
    }

    @ExceptionHandler(value = ValidationException.class)
    public ResponseVo<String> validationErrorHandler(ValidationException ex) throws Exception {
    	logger.error("ValidationException: ", ex);
    	return ResponseHelper.badRequest(ex.getMessage());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseVo<String> defaultErrorHandler(HttpServletResponse resp, Exception ex) throws Exception {
    	String uuid = UUID.randomUUID().toString();
        logger.error("[{}]Internal Server Error: ",uuid, ex);
        return ResponseHelper.error(GLOBEL_ERROR_MSG,uuid);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseVo<?> bodyValidExceptionHandler(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        logger.warn(fieldErrors.get(0).getDefaultMessage());
        return ResponseHelper.error(fieldErrors.get(0).getDefaultMessage());
    }

}