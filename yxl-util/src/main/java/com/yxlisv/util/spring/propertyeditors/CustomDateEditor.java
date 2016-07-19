package com.yxlisv.util.spring.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Date;

import org.springframework.util.StringUtils;

import com.yxlisv.util.date.DateUtil;

/**
 * <p>SpringMVC自动注入日期格式</p>
 * <p>各种常用日期格式转换为Date类型</p>
 * @author 杨雪令
 * @time 2016年4月7日上午11:09:11
 * @version 1.0
 */
public class CustomDateEditor extends PropertyEditorSupport {
	
	//传入的数据
	private String text;

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(null);
		} else {
			text.trim();
			this.text = text;
			setValue(new Date(DateUtil.toLong(text)));
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		//Date value = (Date) getValue();
		return text;
	}
}