package com.yanlaoge.gulimall.order.interceptor;

import com.yanlaoge.common.utils.StaticConstant;
import com.yanlaoge.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author 好人
 * @date 2020-06-14 12:05
 **/
@Component
public class LoginInterceptor implements HandlerInterceptor {
    public static ThreadLocal<MemberRespVo> threadLocal =new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        AntPathMatcher antPathMatcher = new AntPathMatcher();
        String uri = request.getRequestURI();
        boolean match = antPathMatcher.match("/order/order/status/**", uri);
        boolean match1 = antPathMatcher.match("/payed/notify", uri);
        if(match || match1){
            return true;
        }

        HttpSession session = request.getSession();
        MemberRespVo attribute = (MemberRespVo) session.getAttribute(StaticConstant.LOGIN_USER);
        if(attribute != null){
            threadLocal.set(attribute);
            return true;
        }
        session.setAttribute("msg","请先登录");
        response.sendRedirect("http://auth.gulimall.com/login.html");
        return false;
    }
}
