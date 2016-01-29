package com.yxlisv.util.security.radom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.exception.SimpleMessageException;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.web.RequestUtil;

/**
 * 网站随机数工具
 * @author yxl
 */
public class WebRadomUtil {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected static Logger logger = LoggerFactory.getLogger(WebRadomUtil.class);
	/** 请求数 */
	public static double requestNum = 0;
	/**随机数参数key*/
	public static String RD_KEY = "_r";
	
	/**
	 * 校验随机数
	 * @author yxl
	 * @return 
	 * @throws MessageException 
	 */
	public static void validRd(HttpServletRequest request) throws SimpleMessageException {
		//随机数结构key_val
		String requestRdData = RequestUtil.getString(RD_KEY, request);//请求中的随机数数据
		if(requestRdData==null) throw new SimpleMessageException("随机数校验失败", "error.403");
		String key = requestRdData.substring(0, requestRdData.indexOf("_"));
		String val = requestRdData.substring(requestRdData.indexOf("_")+1);

		HttpSession session = request.getSession();
		//logger.info("requestRdData: " + requestRdData);
		//logger.info("validRd: " + key + " / " + session.getAttribute(key) + " / " + val);
		if(session.getAttribute(key)==null || !session.getAttribute(key).equals(val)) 
			throw new SimpleMessageException("随机数校验失败", "error.403");
		else
			session.setAttribute(key, NumberUtil.getRandomStr());//修改随机数
	}
	
	/**
	 * 更新随机数
	 * @autor yxl
	 */
	public static void add(HttpServletRequest request){
		
		String key = request.getRequestURL().toString();
		key = key.substring(key.indexOf((request.getRequestURI().toString())));
		//添加修改删除请求不需要生成随机数
		if(request.getMethod().toUpperCase().equals("POST") && (key.contains("/add") || key.contains("/update") || key.contains("/delete"))) {
			//System.out.println("************** 添加修改删除请求不需要生成随机数：" + key);
			return;
		}
		key = key.replaceAll("\\?[\\d\\D]*", "").replaceAll("_[0-9]+", "").replaceAll("[^a-zA-Z0-9]", "");
		key = "URL-RD-" + key + request.getMethod();
		HttpSession session = request.getSession();
		String rdVal = NumberUtil.getRandomStr();
		session.setAttribute(key, rdVal);//随机数缓存
		session.setAttribute(RD_KEY, key + "_" + rdVal);//随机数结构key_val
		//logger.info("add radom: " + key + " : " + rdVal);
	}
}