package com.yxlisv.util.cookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * cookie工具类
 * @createTime 2015年12月12日 下午2:35:23 
 * @author yxl
 */
public class CookieUtil {

	/**
	 * 根据key在cookie中获取值
	 * @date 2015年12月12日 下午2:38:04 
	 * @author yxl
	 */
	public static String getString(String key, HttpServletRequest request){
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
		    for (Cookie cookie : cookies) {
		        String name = cookie.getName();
		        // 找到需要删除的Cookie  
		        if (name.equals(key)) {
		        	return cookie.getValue();
		        }
		    }
		}
		return null;
	}

	/**
	 * 设置cookie
	 * @date 2015年12月12日 下午2:43:33 
	 * @author yxl
	 */
	public static void setString(String key, String val, HttpServletRequest request, HttpServletResponse response) {
		Cookie cookies = new Cookie(key, val);
        cookies.setMaxAge(-1);
        String path = request.getContextPath();
        if(path.equals("")) path = "/";
        cookies.setPath(path);
        response.addCookie(cookies);
	}
	
	
}
