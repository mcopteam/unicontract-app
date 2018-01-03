package com.uniledger.contract.interceptor;

import com.uniledger.contract.exception.AuthorizationException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by wxcsdb88 on 2017/5/23 20:17.
 */
public class LoginInterceptor implements HandlerInterceptor {
    private static final String LOGIN_URL = "/login.htm";
    private List<String> excludedUrls;

    public void setExcludedUrls(List<String> excludedUrls) {
        this.excludedUrls = excludedUrls;
    }
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // excluded URLs:
        // see http://stackoverflow.com/questions/9908124/spring-mvc-3-interceptor-on-all-excluding-some-defined-paths
        String requestUri = request.getRequestURI();
        for (String url : excludedUrls) {
            if (requestUri.endsWith(url)) {
                return true;
            }
        }

        // intercept
        HttpSession session = request.getSession();
//        UserToken user =(UserToken) session.getAttribute("userToken");
        if (session.getAttribute("currentUser") == null) {
            // see http://stackoverflow.com/questions/12713873/spring-3-1-how-do-you-send-all-exception-to-one-page
            throw new AuthorizationException("user not login or login is outdated!");
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
