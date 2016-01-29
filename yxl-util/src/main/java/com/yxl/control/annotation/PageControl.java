package com.yxl.control.annotation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yxl.control.AbstractBaseControl;


/**
 * 处理页面的控制器
 * @author yxl
 */
@Controller("pageForwordControl")
public class PageControl extends AbstractBaseControl{
	
	/**
	 * 根据某个路径跳转到页面
	 * @autor yxl
	 */
	@RequestMapping(value="**")
	public String index (){
		String path = getRequest().getServletPath();
		if(path.indexOf(".") != -1) path = path.substring(0, path.indexOf("."));
		if(path.startsWith("/")) path = path.substring(1);
		while(path.endsWith("/")) path = path.substring(0, path.length()-1);
		
		String viewName = "index";
		if(path.trim().length()>0) viewName = path.trim();
		
		this.logger.debug("跳转到页面：" + viewName);
		AbstractBaseControl.transferArgsFromRequest(getRequest());
		return viewName;
	}
}