package com.yxlisv.util.spring.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

import com.yxlisv.util.math.NumberUtil;

/**
 * <p>SpringMVC自动注入日期格式</p>
 * <p>各种常用日期格式转换为Date类型</p>
 * @author 杨雪令
 * @time 2016年4月7日上午11:09:11
 * @version 1.0
 */
public class CustomNumberEditor extends PropertyEditorSupport {

	// 传入的数据
	private String text;
	// 要转换的类别
	private String type;

	/**
	 * <p>构造方法</p>
	 * @param type 要转换的类别(int/long/float/double)
	 * @author 杨雪令
	 * @time 2016年4月14日下午3:01:48
	 * @version 1.0
	 */
	public CustomNumberEditor(String type) {
		this.type = type;
	}

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (!StringUtils.hasText(text)) {
			setValue(0);
		} else {
			text.trim();
			this.text = text;
			if(type.equals("int")) setValue(NumberUtil.parseInt(text));
			else if(type.equals("long")) setValue(NumberUtil.parseLong(text));
			else if(type.equals("float")) setValue(NumberUtil.parseFloat(text));
			else if(type.equals("double")) setValue(NumberUtil.parseDouble(text));
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 */
	@Override
	public String getAsText() {
		return text;
	}
}