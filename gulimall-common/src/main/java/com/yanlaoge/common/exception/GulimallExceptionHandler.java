package com.yanlaoge.common.exception;


import com.yanlaoge.common.utils.R;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 */
@RestControllerAdvice
public class GulimallExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public R handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
		BindingResult bindingResult = e.getBindingResult();
		Map<String, String> errMap = new HashMap<>(16);
		bindingResult.getFieldErrors().forEach(errItem -> {
			errMap.put(errItem.getField(), errItem.getDefaultMessage());
		});
		return R.error(ServiceStatusCodeEum.VALID_ERROR.getCode(), ServiceStatusCodeEum.VALID_ERROR.getMsg())
				.put("data", errMap);
	}

	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(GulimllServiceException.class)
	public R handleGulimallException(GulimllServiceException e) {
		R r = new R();
		r.put("code", e.getCode());
		r.put("msg", e.getMessage());
		return r;
	}

	@ExceptionHandler(NoHandlerFoundException.class)
	public R handlerNoFoundException(Exception e) {
		logger.error(e.getMessage(), e);
		return R.error(404, "路径不存在，请检查路径是否正确");
	}

	@ExceptionHandler(DuplicateKeyException.class)
	public R handleDuplicateKeyException(DuplicateKeyException e) {
		logger.error(e.getMessage(), e);
		return R.error("数据库中已存在该记录");
	}

//	@ExceptionHandler(AuthorizationException.class)
//	public R handleAuthorizationException(AuthorizationException e){
//		logger.error(e.getMessage(), e);
//		return R.error("没有权限，请联系管理员授权");
//	}

	@ExceptionHandler(Throwable.class)
	public R handleException(Throwable t) {
		logger.error(t.getMessage(), t);
		return R.error(ServiceStatusCodeEum.UNKNOW_EXCEPTION.getCode(), ServiceStatusCodeEum.UNKNOW_EXCEPTION.getMsg());
	}

	@ExceptionHandler(Exception.class)
	public R handleException(Exception e) {
		logger.error(e.getMessage(), e);
		return R.error();
	}
}
