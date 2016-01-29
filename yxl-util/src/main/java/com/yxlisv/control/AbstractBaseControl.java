package com.yxlisv.control;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import sun.misc.BASE64Encoder;

import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.file.FileUtil;
import com.yxlisv.util.map.MapUtil;
import com.yxlisv.util.message.SimpleMessage;
import com.yxlisv.util.security.SecurityHttpServletRequestWrapper;

/**
 * 最基础的控制器
 * @author yxl
 */
public abstract class AbstractBaseControl extends MultiActionController{
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	/**
	 * 获取 ParameterMap，不是原生的 ParameterMap，此MAP没有被锁定
	 * @author yxl
	 */
	protected Map getParameterMap(){
		return MapUtil.parse(getRequest().getParameterMap());
	}
	
	/**
	 * 重定向
	 * @param url 地址
	 * @autor yxl
	 */
	protected String redirect(String url){
		return "redirect:" + url;
	}
	
	/**
	 * 重定向
	 * @param url 地址
	 * @autor yxl
	 */
	protected String redirectAjax(String url){
		return "redirect:" + getRequest().getContextPath() + url;
	}
	
	/**
	 * 跳转
	 * @param url 地址
	 * @autor yxl
	 */
	protected void forward (String url, HttpServletResponse response){
		try {
			getServletContext().getRequestDispatcher(url).forward(getRequest(), response);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 获取 HttpServletRequest 对象
	 * @autor yxl
	 */
	public static HttpServletRequest getRequest(){
		return new SecurityHttpServletRequestWrapper(((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest());
	}
	
	
	/**
	 * 获取 HttpSession 对象
	 * @autor yxl
	 */
	protected HttpSession getSession(){
		return getRequest().getSession();
	}
	
	
	/**
	 * 获取缓存的查询条件
	 * 模糊查询时：如果通过一些条件查询到一些数据，修改这些数据成功后要重新查询，会造成条件丢失，所以先缓存
	 * @param key 关键词
	 * @param srMap 新的查询条件
	 * @autor yxl
	 */
	protected Map updateSrMap(String key){
		Map srMap = getParameterMap();//查询条件MAP
		try{
			//获取缓存的查询条件(模糊查询时：如果通过一些条件查询到一些数据，修改这些数据成功后要重新查询，会造成条件丢失，所以先缓存)
			if(getFromSession(key) != null){
				Map cacheSrMap = (Map) getFromSession(key);
				cacheSrMap.putAll(srMap);
				srMap.clear();
				srMap.putAll(cacheSrMap);
			}
		} catch(Exception e){}
		srMap = MapUtil.parse(srMap);//把ParameterMap中获取的数组转换成单个值
		getSession().setAttribute(key, srMap);
		return srMap;
	}
	
	/**
	 * 获取缓存的查询条件
	 * @param key 关键词
	 * @autor yxl
	 */
	protected Map getSrMap(String key){
		if(getFromSession(key) != null) return (Map) getFromSession(key);
		return null;
	}
	
	
	/**
	 * 清除缓存的查询条件
	 * @param key 关键词
	 * @autor yxl
	 */
	protected void removeSrMap(String key){
		getSession().removeAttribute(key);
	}
	
	
	/**
	 * 获取 简单消息[SimpleMessage] 对象
	 * @autor yxl
	 */
	protected SimpleMessage getSimpleMessage(){
		HttpSession session = getRequest().getSession();
		SimpleMessage sMessage = null;
		if(session.getAttribute("sMessage") != null) sMessage = (SimpleMessage) session.getAttribute("sMessage");
		else{
			sMessage = new SimpleMessage();
			session.setAttribute("sMessage", sMessage);
		}
		return sMessage;
	}
	
	
	/**
	 * 发送消息
	 * @autor yxl
	 */
	protected void sendMsg(String msg){
		if(msg.contains(MessageException.MSG_SEPARATOR)) msg = msg.split(MessageException.MSG_SEPARATOR)[0];
		getSimpleMessage().removeAllMsg();
		getSimpleMessage().addMsg(msg);
		logger.debug(msg);
	}
	
	/**
	 * 发送警告消息
	 * @autor yxl
	 */
	protected void sendWarning(String msg){
		if(msg.contains(MessageException.MSG_SEPARATOR)) msg = msg.split(MessageException.MSG_SEPARATOR)[0];
		getSimpleMessage().removeAllWarning();
		getSimpleMessage().addWarning(msg);
		logger.warn(msg);
	}
	
	/**
	 * 发送错误消息
	 * @autor yxl
	 */
	protected void sendError(String msg){
		if(msg.contains(MessageException.MSG_SEPARATOR)) msg = msg.split(MessageException.MSG_SEPARATOR)[0];
		getSimpleMessage().removeAllError();
		getSimpleMessage().addError(msg);
		logger.warn(msg);
	}
	
	/**
	 * 发送错误消息
	 * @autor yxl
	 */
	protected void sendError(BindingResult bindingResult){
		sendError(getStr(bindingResult));
	}
	
	/**
	 * 从BindingResult中获取消息
	 * @autor yxl
	 */
	protected String getStr(BindingResult bindingResult){
		for(ObjectError objectError : bindingResult.getAllErrors()){
			return objectError.getDefaultMessage();
		}
		return "";
	}
	
	/**
	 * 从BindingResult中获取消息
	 * @param args 要验证的参数
	 * @autor yxl
	 */
	protected String getStr(BindingResult result, String[] args){
		for(String arg : args){
			if(result.getFieldErrorCount(arg) > 0) {
				return result.getFieldError(arg).getDefaultMessage();
			}
		}
		return "";
	}
	
	/**
	 * 发送错误消息
	 * @param args 要验证的参数
	 * @autor yxl
	 */
	protected void sendError(BindingResult result, String[] args){
		for(String arg : args){
			if(result.getFieldErrorCount(arg) > 0) {
				sendError(result.getFieldError(arg).getDefaultMessage());
				break;
			}
		}
	}
	
	/**
	 * 是否存在错误
	 * @param result BindingResult对象
	 * @param args 要验证的参数
	 * @autor yxl
	 * 2013-11-26
	 */
	public boolean hasError(BindingResult result, String[] args){
		for(String arg : args){
			if(result.getFieldErrorCount(arg) > 0) return true;
		}
		return false;
	}
	
	/**
	 * 添加临时对象到SESSION中
	 * 主要用来防止 用户恶意伪造代码
	 * 如：一个用户登录之后，该用户拥有多个产品，此时他修改产品为1的信息：
	 * 	通过toUpdate方法查询产品信息，并把数据显示到页面，页面在隐藏域中保存了产品ID"1"，
	 * 	但提交数据的时候，用firebug把隐藏域中的ID改为2，如果产品ID为2的数据是其他用户的，
	 * 	该用户就可以随意修改别人的数据。
	 * 
	 * 为了防止这种情况：在toUpdate中调用 addTempObj 方法把ID存放到session中，
	 * 在update方法中调用 getTempObj 方法把ID取出来，页面就不用添加隐藏域了。
	 * @autor yxl
	 */
	protected void addTempObj(String key, Object obj){
		HttpSession session = getSession();
		
		//一个存放临时对象的Map
		Map<String, Object> tempObjMap = null;
		if(session.getAttribute("tempObjMap") != null)
			tempObjMap = (Map) session.getAttribute("tempObjMap");
		
		//初始化
		if(tempObjMap == null){
			tempObjMap = new HashMap();
			session.setAttribute("tempObjMap", tempObjMap);
		}
		
		tempObjMap.put(key, obj);
	}
	
	
	
	/**
	 * 获取临时对象到SESSION中
	 * 主要用来防止 用户恶意伪造代码
	 * 如：一个用户登录之后，该用户拥有多个产品，此时他修改产品为1的信息：
	 * 	通过toUpdate方法查询产品信息，并把数据显示到页面，页面在隐藏域中保存了产品ID"1"，
	 * 	但提交数据的时候，用firebug把隐藏域中的ID改为2，如果产品ID为2的数据是其他用户的，
	 * 	该用户就可以随意修改别人的数据。
	 * 
	 * 为了防止这种情况：在toUpdate中调用 addTempObj 方法把ID存放到session中，
	 * 在update方法中调用 getTempObj 方法把ID取出来，页面就不用添加隐藏域了。
	 * @autor yxl
	 */
	protected Object getTempObj(String key){
		HttpSession session = getSession();
		
		//一个存放临时对象的Map
		Map<String, Object> tempObjMap = null;
		if(session.getAttribute("tempObjMap") != null)
			tempObjMap = (Map) session.getAttribute("tempObjMap");
		
		//取出缓存对象
		if(tempObjMap == null) return null;
		Object obj = tempObjMap.get(key);
		
		//移除对象
		tempObjMap.remove(key);
		
		if(obj == null) return "";
		return obj;
	}

	
	/**
	 * 得到前一个请求的地址
	 * @autor yxl
	 */
	protected String getBackUrl(HttpServletRequest request){
		String backUrl = request.getHeader("REFERER");
		backUrl = backUrl.substring(backUrl.indexOf(request.getContextPath()) + request.getContextPath().length());
		
		return redirect(backUrl);
	}
	
	
	/**
	 * 从 request 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	protected String getStringFromRequest(String key){
		Object obj = getFromRequest(key);
		if(obj==null) return "";
		return obj.toString();
	}
	
	
	/**
	 * 从 request 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	protected Object getFromRequest(String key){
		HttpServletRequest request = getRequest();
		return AbstractBaseControl.getFromRequest(key, request);
	}
	
	
	/**
	 * 从 session 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	protected Object getFromSession(String key){
		HttpServletRequest request = getRequest();
		return AbstractBaseControl.getFromSession(key, request);
	}
	
	/**
	 * 从 session 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	protected Object getFromApplication(String key){
		HttpServletRequest request = getRequest();
		return AbstractBaseControl.getFromSession(key, request);
	}
	
	/**
	 * 从 缓存 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	protected Object getFromCache(String key){
		HttpServletRequest request = getRequest();
		Object obj = null;
		
		//从request中获取
		obj = AbstractBaseControl.getFromRequest(key, request);
		if(obj != null) return obj;
		
		//从session中获取
		obj = AbstractBaseControl.getFromSession(key, request);
		if(obj != null) return obj;
		
		//从application中获取
		obj = AbstractBaseControl.getFromApplication(key, request);
		if(obj != null) return obj;
		
		return null;
	}
	
	
	/**
	 * 从 request 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	public static Object getFromRequest(String key, HttpServletRequest request){
		if(request.getAttribute(key) != null) return request.getAttribute(key);
		if(request.getParameter(key) != null) return request.getParameter(key);
		return null;
	}
	
	
	/**
	 * 从 session 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	public static Object getFromSession(String key, HttpServletRequest request){
		if(request.getSession().getAttribute(key) != null) return request.getSession().getAttribute(key);
		return null;
	}
	
	/**
	 * 从 Application 中获取参数值
	 * @param key key
	 * @autor yxl
	 */
	public static Object getFromApplication(String key, HttpServletRequest request){
		if(request.getSession().getAttribute(key) != null) return request.getSession().getServletContext().getAttribute(key);
		return null;
	}
	
	
	/**
	 * 把字符串写到输出流，格式：文本
	 * @param str
	 * @param response
	 */
	public static void writeOfText(String str,HttpServletResponse response){
		setOfText(response);
		PrintWriter out = null;   
		try {   
			out = response.getWriter();   
			out.print(str);
		}   
		catch (IOException ex1) {   
			ex1.printStackTrace();   
		}finally{   
		     if(out!=null) out.close();   
		}
	}
	
	/**
	 * 把字符串写到输出流，格式：文本
	 * @param str
	 * @param response
	 */
	public static void setOfText(HttpServletResponse response){
		response.setContentType("text/html;charset=utf-8");
	}
	
	/**
	 * 得到字符串的base64编码
	 * @author yxl
	 */
	@SuppressWarnings("restriction")
	public static String getBase64(String str){
		return new BASE64Encoder().encode(str.getBytes());
	}
	
	/**
	 * 把request中的参数传递到页面
	 * @param sFilter 要过滤的参数，如果有多个就用逗号隔开
	 * @autor yxl
	 */
	public static void transferArgsFromRequest (String sFilter, HttpServletRequest request){
		Enumeration varNames = request.getParameterNames();
        while(varNames.hasMoreElements()){
        	String varName = varNames.nextElement().toString();
        	if(sFilter.contains(varName)) continue;
        	//System.out.println("传递参数：" + varName + "--->" + request.getParameter(varName));
        	request.setAttribute(varName, request.getParameter(varName));
        }
	}
	
	/**
	 * 把request中的参数传递到页面
	 * @autor yxl
	 */
	public static void transferArgsFromRequest (HttpServletRequest request){
		AbstractBaseControl.transferArgsFromRequest("", request);
	}
	
	
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest arg0, HttpServletResponse arg1) throws Exception {
		return null;
	}
	
	//项目名称标记
	public static String projectNameTag = "#THIS_IS_PROJECT_TAG#";
	
	/**
	 * 清理字符串中的项目名称
	 * @param str 要清理的字符串
	 * @autor yxl
	 */
	public static String clearProjectName(String str){
		String projectName = AbstractBaseControl.getRequest().getContextPath();
		return str.replaceAll(projectName, "");
	}
	/**
	 * 替换字符串中的项目名称
	 * @param str 要替换的字符串
	 * @autor yxl
	 */
	public static String replaceProjectName(String str){
		if(str == null) return null;
		//String projectName = FileUtil.getStandardUrl(AbstractBaseControl.getRequest().getContextPath()).replaceAll("/", "");
		String projectName = AbstractBaseControl.getRequest().getContextPath();
		return str.replaceAll(projectName, projectNameTag);
	}
	
	/**
	 * 还原字符串中的项目名称
	 * @param str 要还原的字符串
	 * @autor yxl
	 */
	public static String revertProjectName(String str){
		if(str == null) return null;
		String projectName = AbstractBaseControl.getRequest().getContextPath();
		return str.replaceAll(projectNameTag, projectName);
	}
	
	/**
	 * 获取项目的path
	 * @param request HttpServletRequest
	 * @return eg: http://127.0.0.1:333/testProject
	 */
	public static String getProjectPath(HttpServletRequest request){
		String ip = "";
		try {
			ip = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			ip = request.getLocalAddr();
		}
		return "http://" + ip + ":" + request.getLocalPort() + request.getContextPath();
	}
	
	/**
	 * 获取项目的path
	 * @return eg: http://127.0.0.1:333/testProject
	 */
	public static String getProjectPath(){
		return AbstractBaseControl.getProjectPath(AbstractBaseControl.getRequest());
	}
	
	/**
	 * 下载文件
	 * @param fileName 文件名称，不需要后缀
	 * @param filePath 文件路径，相对路径需要包含文件后缀，从WebRoot开始计算，如：/resources/1/2.rmvb
	 * @throws IOException 
	 */
	public static void download(String fileName, String filePath, HttpServletResponse response) throws IOException{
		
		fileName += FilePathUtil.getSuffix(filePath);//加后缀
		//构造文件存储路径
		String path = FilePathUtil.getClassUrl("WEB-INF").replace("WEB-INF", "");
		if(path.endsWith("//")) path = path.substring(0, path.length()-1);
		File downloadFile = new File(path + filePath);
		
		response.setBufferSize((int) downloadFile.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + new String(fileName.getBytes("gb2312"),"iso8859-1") + "\"");
		response.setContentType(getRequest().getServletContext().getMimeType(fileName));
		response.setContentLength((int) downloadFile.length());
		FileUtil.copy(downloadFile, response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
	}
}
