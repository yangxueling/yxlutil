package com.yxlisv.tag;
import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.yxlisv.util.security.SecurityUtil;
import com.yxlisv.util.string.StringUtil;

/**
 * 字符串工具
 */
public class StringUtilTag extends SimpleTagSupport {
	
	/** 字符串 */
	private String value;
	public void setValue(String value) {
		this.value = value;
	}
	
	/** 类别 */
	private String type = "";
	public void setType(String type) {
		this.type = type;
	}
	
	/** 字符长度 */
	private int length = -1;
	public void setLength(int length) {
		this.length = length;
	}
	
	
	@Override
	public void doTag() throws JspException, IOException {
		JspContext context=getJspContext();
		JspWriter out=context.getOut();
		
		String outStr = value;
		if(type.equals("clearBlank")) outStr = StringUtil.clearBlank(value);
		else if(type.equals("html")) outStr = SecurityUtil.reset(value);
		else if(type.equals("clearHtml")) outStr = StringUtil.clearHtml(value);
		else if(type.equals("clearHtmlBlank")) outStr = StringUtil.clearHtmlBlank(value);
		else if(type.equals("unicode")) outStr = StringUtil.toUnicode(value);
		else if(type.equals("clearXSS")) outStr = SecurityUtil.simpleClear(value);
		else if(type.equals("htmlEscape")) outStr = StringUtil.htmlEscape(SecurityUtil.simpleClear(value));
		else if(type.equals("lower")) outStr = value.toLowerCase();
		else if(type.equals("upper")) outStr = value.toUpperCase();
		else if(type.equals("desc")) outStr = StringUtil.formatDesc(value.toUpperCase());
		
		//截取长度
		if(length>0) outStr = StringUtil.substringByWidth(outStr, length, "...");
		
		out.println(outStr);
		super.doTag();
	}
 
}