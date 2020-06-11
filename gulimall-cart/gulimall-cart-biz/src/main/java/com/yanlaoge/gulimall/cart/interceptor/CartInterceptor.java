package com.yanlaoge.gulimall.cart.interceptor;

import com.yanlaoge.common.utils.StaticConstant;
import com.yanlaoge.common.vo.MemberRespVo;
import com.yanlaoge.gulimall.cart.constant.CartConstant;
import com.yanlaoge.gulimall.cart.to.UserInfoTo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * @author rubyle
 */
@Component
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal =new ThreadLocal<>();
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfoTo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo memberRespVo = (MemberRespVo) session.getAttribute(StaticConstant.LOGIN_USER);
        if(memberRespVo != null){
            userInfoTo.setUserId(memberRespVo.getId());
        }
        Cookie[] cookies = request.getCookies();
        if(!ArrayUtils.isEmpty(cookies)){
            for (Cookie cookie : cookies) {
                String cookieName = cookie.getName();
                if(CartConstant.TEMP_USER_COOKIE_NAME.equals(cookieName)){
                    userInfoTo.setUserkey(cookie.getValue());
                    userInfoTo.setTempUser(true);
                }
            }
        }
        if(StringUtils.isEmpty(userInfoTo.getUserkey())){
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserkey(uuid);
        }
        threadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        if(!userInfoTo.getTempUser()){
            Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME,userInfoTo.getUserkey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_AGE);
            response.addCookie(cookie);
        }

    }
}
