package com.hksj.xzb.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.hksj.common.constants.SystemConstants;

/**
 * 根据用户登录session信息拦截请求
 * @createTime 2015年11月25日 下午1:03:42 
 * @author yxl
 */
public class CheckLoginInterceptor implements HandlerInterceptor {

	/**
	 * 请求前置拦截
	 * @createTime 2015年11月25日 下午1:03:42 
	 * @author yxl
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//如果session中没有用户的登录信息，则拦截
		if(request.getSession().getAttribute(SystemConstants.USER_SESSION_INFO)==null) {
			String header=request.getHeader("x-requested-with");
			if(header.equals("XMLHttpRequest")){//ajax方式
				response.getOutputStream().print("not_login");
			} else {
				response.sendRedirect(request.getContextPath() + "/login.html");
				return false;
			}
		}
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}
}