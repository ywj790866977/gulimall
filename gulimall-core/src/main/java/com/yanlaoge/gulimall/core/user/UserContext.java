package com.yanlaoge.gulimall.core.user;

import com.yanlaoge.common.utils.CommonStatusCode;
import com.yanlaoge.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author rubyle
 */
//@Component
@Slf4j
public class UserContext {


	public static final String HEADER_X_AUTH_TOKEN = "TB-TOKEN";

//	public static final String SITE_ID_SUFFIX = ":siteId";
//	public static final String APP_URL_SUFFIX = ":URI";
//
//	public static final String REQ_ATTR_USER_NAME = "$kc:userId";
//	public static final String REQ_ATTR_SITEID_NAME = "$kc:siteId";
//
//
//
//	// 站点ID头名称
//	public static final String REQ_HEADER_SITE_ID_NAME = "TB-SITE-ID";


	@Resource
	private RedisUtil redisUtil;





	/**
	 * 当前登录用户
	 *
	 * @return user
	 */
	@SuppressWarnings("deprecation")
	public final SessionUser getUser() {
		HttpServletRequest request = getHttpRequest();
		if (request == null) {
			return null;
		}

		String token = request.getHeader(HEADER_X_AUTH_TOKEN);
		if (token == null) {
			return null;
		}
		SessionUser user = (SessionUser) redisUtil.get(CommonStatusCode.USER_LOGIN_TOKEN_KEY_PREFIX + token);
		return user;
	}

//	/**
//	 * 通过token 获取 key
//	 *
//	 * @param token
//	 * @return
//	 */
//	private String getDBSiteId(String token) {
//		String[] keyStrs = token.split("_");
//		if (keyStrs != null && keyStrs.length > 0) {
//			String str = keyStrs[0];
//			if (CommonStatusCode.SITE_TYPE_ZK.contentEquals(str) || CommonStatusCode.SITE_TYPE_ZD.contentEquals(str)
//					|| CommonStatusCode.SITE_TYPE_DL.contentEquals(str) || CommonStatusCode.SITE_TYPE_HY.contentEquals(str)) {
//				return str;
//			}
//		}
//		return null;
//	}

//	@SuppressWarnings("deprecation")
//	public final boolean hasPermission(String uri) {
//
//		SessionUser user = getUser();
//		if (user == null) {
//			return false;
//		}
//
//		if (user.isAdmin()) {
//			return true;
//		}
//
//		@SuppressWarnings("unchecked")
//		Set<String> userUriSet = (Set<String>) redisService
//				.getObjectFullKey(CommonStatusCode.USER_LOGIN_RESOURCE_URL + getUserIdAndSiteId());
//
//		if (userUriSet == null) {
//			return false;
//		}
//
//		return userUriSet.contains(uri);
//	}

//	public final Integer getSiteId() {
//		return getSiteId(false);
//	}

//	/**
//	 * 检查当前站点是否停用
//	 * @return
//	 */
//	public final boolean siteIsClosed(){
//		 //健康检查不做检查
//		 if(getHttpRequest().getRequestURI().equals("/actuator/health")){
//		 			return false;
//		 }
//		 String domain= HttpHeaderUtil.getHeader(getHttpRequest()).get("x-forwarded-host");
//		 Set set=redisTemplate.opsForSet().members(CommonStatusCode.SITE_CLOSE_OF_CONTROL_PREFX);
//		if(set == null){
//			return false;
//		}
//        return set.contains(domain);
//	}



//	public final Integer getSiteId(boolean headFlag) {
//		HttpServletRequest request = getHttpRequest();
//		if (request == null) {
//			return 0;
//		}
//		String token = request.getHeader(HEADER_X_AUTH_TOKEN);
//		//logger.info(" --- xxxx - UserContext.getSiteId.token: {}", token);
//		if (null == request || null == token) {
//			//logger.info(" --- xxxx - UserContext.getSiteId.request is null : ");
//			return 0;
//		}
//
//		@SuppressWarnings("deprecation")
//		Integer siteId = (Integer) redisService.getObjectFullKey(CommonStatusCode.REDIS_KEY_SITE_TYPE + getDBSiteId(token) + ":" + token);
//		//logger.info(" --- xxxx - UserContext.getSiteId.redis.siteId: {}", siteId);
//
//		if (siteId == null) {
//			try {
//				if(headFlag) {
//					Object siteIdObj = request.getHeader(REQ_HEADER_SITE_ID_NAME);
//					if(siteIdObj != null) {
//						siteId = Integer.parseInt(siteIdObj.toString());
//					}
//				}else {
//					siteId = Integer.parseInt(DynamicDataSource.getSite().toString());
//				}
//			} catch (Exception ex) {
//				logger.error(ex.getMessage());
//			}
//		}
//		return siteId==null?0:siteId;
//	}

	/**
	 * 刷新用户信息
	 */
	public final void refreshUser() {
		//心跳地址不做token刷新
		String uri=getHttpRequest().getRequestURI();
		if(uri.equals("/user/v1/checkHeartBeat")){
			   return ;
		}
		Long loginTimeOut = (Long) redisUtil.get(CommonStatusCode.USER_LOGIN_TIME_OUT_KEY);
		//logger.info("获取的登录超时时间={}",loginTimeOut);
		if (null == loginTimeOut) {
			loginTimeOut = 7 * 24 * 60 * 60 * 1000L;
		}

		String token = getHttpRequest().getHeader(HEADER_X_AUTH_TOKEN);
		if(StringUtils.isNotBlank(token) && token.endsWith("_XJ")) {
			//如果是脚本 则不刷，由调度系统维护
			return;
		}

		// 刷新用户信息
		SessionUser user = getUser();
		redisUtil.set(CommonStatusCode.USER_LOGIN_TOKEN_KEY_PREFIX+getHttpRequest().getHeader(HEADER_X_AUTH_TOKEN), user,
				loginTimeOut);
//		 刷新token
//		redisUtil.set(CommonStatusCode.USER_LOGIN_TOKEN_VALUE_KEY_PREFIX + getUserIdAndSiteId(),
//				getHttpRequest().getHeader(HEADER_X_AUTH_TOKEN), loginTimeOut);
//		//刷新站点id
//		StringBuffer siteSb=new StringBuffer(120);
//		siteSb.append(CommonStatusCode.REDIS_KEY_SITE_TYPE)
//				.append("ZD:")
//				.append(getHttpRequest().getHeader(HEADER_X_AUTH_TOKEN));
//		redisUtil.set(siteSb.toString(),getSiteId(),Duration.ofMillis(loginTimeOut));
//		//刷新资源缓存列表
//		String key=CommonStatusCode.USER_LOGIN_RESOURCE_URL+getUser().getId()+"-"+getSiteId();
//		redisUtil.set(key, redisUtil.get(key), loginTimeOut);

	}

//	/**
//	 * 验证用户权限,删除，禁用，角色等是否发生变更
//	 *
//	 * @return
//	 */
//	public final int userInfoChangeVali() {
//		SessionUser user = getUser();
//		if (user == null) {
//			return 0;
//		}
//		// 当前请求token
//		String requestToken = getHttpRequest().getHeader(HEADER_X_AUTH_TOKEN);
//		// 当前登录token
//		Object loginToken = redisTemplate.opsForValue()
//				.get(CommonStatusCode.USER_LOGIN_TOKEN_VALUE_KEY_PREFIX + getUserIdAndSiteId());
//		// 强制用户退出编码
//		Object object = redisTemplate.opsForValue()
//				.get(CommonStatusCode.USER_FORCE_LOGOUT_KEY_PREFIX + getUserIdAndSiteId());
//		if (object != null && object instanceof Integer) {
//			int result = (int) object;
//			// 多端登录特殊处理
//			if (CommonStatusCode.NEW_CLIENT_LOGIN_CODE == result) {
//				// 不同token说明是不同端登录
//				if (!requestToken.equals(loginToken)) {
//					// 请求token与登录token不一致，清除请求token相关用户信息
//					redisTemplate.delete(CommonStatusCode.USER_LOGIN_TOKEN_VALUE_KEY_PREFIX + getUserIdAndSiteId());
//					redisTemplate.delete(CommonStatusCode.USER_FORCE_LOGOUT_KEY_PREFIX + getUserIdAndSiteId());
//					redisTemplate.delete(CommonStatusCode.USER_LOGIN_TOKEN_KEY_PREFIX + requestToken);
//					return result;
//				}
//				// 禁用用户，删除用户，角色修改，权限变更等强制用户退出
//			} else {
//			    if (object.equals(1000104))
//					return result;
//
//				// 当前请求用户已经被强制退出，直接清除用户信息
//				redisTemplate.delete(CommonStatusCode.USER_LOGIN_TOKEN_VALUE_KEY_PREFIX + getUserIdAndSiteId());
//				redisTemplate.delete(CommonStatusCode.USER_FORCE_LOGOUT_KEY_PREFIX + getUserIdAndSiteId());
//				redisTemplate.delete(CommonStatusCode.USER_LOGIN_TOKEN_KEY_PREFIX + requestToken);
//				return result;
//			}
//		}
//		return 0;
//	}


//	public final boolean checkIpForbid(){
//		try {
//			String ip = HttpIpUtil.getIpAddress(getHttpRequest());
//			Integer siteId = getSiteId();
//			if (!new Integer(0).equals(siteId) && StringUtils.isNotBlank(ip)) {
//				//被禁用的ip永久缓存
//				Set<String> set = (Set<String>) redisTemplate.opsForValue().get(CommonStatusCode.FORBID_IP_PREFIX + siteId);
//				if (set == null) {
//					return false;
//				}
//				if (set.contains(ip.split(",")[0])) {
//					return true;
//				}
//			}
//		}catch (Exception e){
//			logger.error("ip白名单检测错误",e);
//		}
//		return false;
//	}

//	public String getUserIdAndSiteId() {
//		return getUser().getId() + "-" + getSiteId();
//	}

	public static HttpServletRequest getHttpRequest() {
		ServletRequestAttributes reqAttr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		return null == reqAttr ? null : reqAttr.getRequest();
	}

//	/**
//	 * 根据resourceId获取脱敏字段列表
//	 * @param resourceId
//	 * @return
//	 */
//	public List<String> getDesensitizationFieldList(Long resourceId){
//		Map<String,List<String>> desensitizationMap= (Map<String,List<String>>) redisTemplate.opsForValue().get(CommonStatusCode.DESENSITIZATION_FIELD_PREFIX+getUserIdAndSiteId());
//		if(desensitizationMap == null){
//			return  new ArrayList<>();
//		}
//		return desensitizationMap.get(resourceId+"");
//	}

//	/**
//	 * 获取当前用户角色及其下级角色
//	 * @return
//	 */
//	public List<Long> getChildRoleList(){
//		List<Long> list= (List<Long>) redisTemplate.opsForValue().get(CommonStatusCode.CHILD_ROLELIST_PREFIX+getUserIdAndSiteId());
//		if(list == null){
//			list=new ArrayList<>();
//		}
//		return list;
//	}

//	/**
//	 * 获取当前用户角色及其子角色对应的用户列表
//	 * @return
//	 */
//	public List<Map<String,Object>> getChildUserList(){
//		List<Map<String,Object>> list= (List<Map<String,Object>>) redisTemplate.opsForValue().get(CommonStatusCode.CHILD_USERLIST_PREFIX+getUserIdAndSiteId());
//		if(list == null){
//			list=new ArrayList<>();
//		}
//		return list;
//	}

//	/**
//	 * 获取编辑字段列表
//	 * @param resourceId
//	 * @return
//	 */
//	public List<Map<String,Object>> getEditFieldList(Long resourceId){
//		Map<String,List<Map<String,Object>>> editFieldMap=(Map<String,List<Map<String,Object>>> )redisTemplate.opsForValue().get(CommonStatusCode.EDIT_FIELD_LIST+getUserIdAndSiteId());
//		if(editFieldMap == null){
//			  return new ArrayList<>();
//		}
//		return editFieldMap.get(resourceId+"");
//	}
}