package com.yxlisv.tag;
import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import com.yxlisv.util.date.DateUtil;

/**
 * 格式化日期
 * 默认使用浏览器地区的时区
 * @createTime 2015年11月18日 下午1:02:21 
 * @author yxl
 */
public class FormatDateTag extends SimpleTagSupport {
	
	/** 时间 */
	private Long value;
	public void setValue(Long value) {
		this.value = value;
	}
	
	/** 类别：time/date */
	private String type = "time";
	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public void doTag() throws JspException, IOException {
		
		JspContext context=getJspContext();
		JspWriter out=context.getOut();
		if(value==0) {
			out.print("");
			return;
		}
		if(type.equals("date")) out.print(DateUtil.toDate(value));
		else if(type.equals("year")) out.print(DateUtil.toYear(value));
		else if(type.equals("month")) out.print(DateUtil.toMonth(value));
		else if(type.equals("day")) out.print(DateUtil.toDay(value));
		else if(type.equals("hour")) out.print(DateUtil.toHour(value));
		else if(type.equals("minute")) out.print(DateUtil.toMinute(value));
		else if(type.equals("second")) out.print(DateUtil.toSecond(value));
		else if(type.equals("hourMinute")) out.print(DateUtil.toHourMinute(value));
		else if(type.equals("hourMinuteSecond")) out.print(DateUtil.toHourMinuteSecond(value));
		else if(type.equals("duration")) out.print(DateUtil.formatDuration(value));
		else if(type.equals("ai")) out.print(DateUtil.toAi(value));
		else if(type.equals("ai1")) out.print(DateUtil.toAi1(value));
		else out.print(DateUtil.toTime(value));
		super.doTag();
	}
}