package com.yxlisv.util.spring.annotation;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * Spring異常處理類
 * @author yxl
 */
@Component
public class SpringExceptionHandle implements HandlerExceptionResolver {

	//定义一个全局的记录器，通过LoggerFactory获取  
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
		
	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) {
		
        //根据异常类型的不同，提供不同的异常提示
        String errorMessage = e.getMessage();
        
        StringBuilder message = new StringBuilder();
        message.append(getStackTraceMsg(e));
        
        //自定义错误信息
        if(message.toString().contains("org.springframework.web.bind.annotation.support.HandlerMethodInvoker.doBind")){
        	message.setLength(0);
        	message.append("\tSpringMVC bind args faild!");
        } else if(message.toString().contains("org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter$ServletHandlerMethodResolver.resolveHandlerMethod")){
        	message.setLength(0);
        	message.append("\tSpringMVC Request method not supported!");
        } else if(message.toString().contains("org.springframework.beans.SimpleTypeConverter.convertIfNecessary")){
        	message.setLength(0);
        	message.append("\tSpringMVC PathVariable error!");
        } else if(message.toString().contains("org.apache.catalina.connector.OutputBuffer.realWriteBytes")){
        	message.setLength(0);
        	message.append("\tDownload file cancel!");
        	logger.warn(message.toString());
        	return new ModelAndView();
        }
        
        //記錄錯誤消息
        String msgStr = errorMessage + "{"+ "\n" +" URI: "+ request.getRequestURI() + "\n" + message +"\n}";
        logger.error(msgStr);
        request.setAttribute("errorMessage", msgStr);
        
		try {
			request.getSession().getServletContext().getRequestDispatcher("/WEB-INF/error/500.jsp").forward(request, response);
		} catch (Exception e1) {
			logger.error("forward 500 page error", e1);
		}
        return new ModelAndView();
	}
	
	/**
	 * 获取堆栈信息
	 * @date 2015年12月12日 下午7:15:29 
	 * @author yxl
	 */
	public static String getStackTraceMsg(Exception e){
		StringBuilder message = new StringBuilder();
        message.append("SYS_ERROR:");
        message.append(e.getMessage());
        for (StackTraceElement st: e.getStackTrace()){
        	message.append("\n\t     at ");
        	message.append(st.toString());
        }
        return message.toString();
	}
}