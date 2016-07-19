package com.yxlisv.util.redis.session;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Map;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import javax.servlet.FilterRegistration.Dynamic;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;
import javax.servlet.SessionCookieConfig;
import javax.servlet.SessionTrackingMode;
import javax.servlet.descriptor.JspConfigDescriptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.redis.JedisUtil;

/**
 * <p>Redis ServletContext</p>
 * <p>实现跨服务器ServletContext共享</p>
 * @author 杨雪令
 * @time 2016年5月18日上午10:45:29
 * @version 1.0
 */
public class RedisServletContext implements ServletContext {

	private static Logger logger = LoggerFactory.getLogger(RedisServletContext.class);
	
	//原始(被代理的)ServletContext对象
	private ServletContext proxyServletContext = null;
	//单例模式
	private static ServletContext servletContext = null;
	
	/**
	 * <p>获取实例</p>
	 * @param servletContext 原始ServletContext对象
	 * @author 杨雪令
	 * @time 2016年5月18日上午10:52:03
	 * @version 1.0
	 */
	public static ServletContext getInstance(ServletContext servletContext) {
		if(RedisServletContext.servletContext != null) return RedisServletContext.servletContext;
		return newInstance(servletContext);
	}
	
	/**
	 * <p>创建实例</p>
	 * @param servletContext 原始ServletContext对象
	 * @author 杨雪令
	 * @time 2016年5月18日上午10:52:03
	 * @version 1.0
	 */
	public static synchronized ServletContext newInstance(ServletContext servletContext) {
		if(RedisServletContext.servletContext != null) return RedisServletContext.servletContext;
		RedisServletContext.servletContext = new RedisServletContext(servletContext);
		return RedisServletContext.servletContext;
	}
	
	/**
	 * <p>构造方法</p>
	 * @param servletContext 原始ServletContext对象
	 * @author 杨雪令
	 * @time 2016年5月18日上午11:10:49
	 * @version 1.0
	 */
	private RedisServletContext(ServletContext servletContext){
		this.proxyServletContext = servletContext;
	}

	@Override
	public Dynamic addFilter(String arg0, String arg1) {
		return proxyServletContext.addFilter(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Filter arg1) {
		return proxyServletContext.addFilter(arg0, arg1);
	}

	@Override
	public Dynamic addFilter(String arg0, Class<? extends Filter> arg1) {
		return proxyServletContext.addFilter(arg0, arg1);
	}

	@Override
	public void addListener(String arg0) {
		proxyServletContext.addListener(arg0);
	}

	@Override
	public <T extends EventListener> void addListener(T arg0) {
		proxyServletContext.addListener(arg0);
	}

	@Override
	public void addListener(Class<? extends EventListener> arg0) {
		proxyServletContext.addListener(arg0);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, String arg1) {
		return proxyServletContext.addServlet(arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Servlet arg1) {
		return proxyServletContext.addServlet(arg0, arg1);
	}

	@Override
	public javax.servlet.ServletRegistration.Dynamic addServlet(String arg0, Class<? extends Servlet> arg1) {
		return proxyServletContext.addServlet(arg0, arg1);
	}

	@Override
	public <T extends Filter> T createFilter(Class<T> arg0) throws ServletException {
		return proxyServletContext.createFilter(arg0);
	}

	@Override
	public <T extends EventListener> T createListener(Class<T> arg0) throws ServletException {
		return proxyServletContext.createListener(arg0);
	}

	@Override
	public <T extends Servlet> T createServlet(Class<T> arg0) throws ServletException {
		return proxyServletContext.createServlet(arg0);
	}

	@Override
	public void declareRoles(String... arg0) {
		proxyServletContext.declareRoles(arg0);
	}

	@Override
	public Object getAttribute(String key) {
		return JedisUtil.get(key);
	}

	@Override
	public Enumeration<String> getAttributeNames() {
		return null;
	}

	@Override
	public ClassLoader getClassLoader() {
		return proxyServletContext.getClassLoader();
	}

	@Override
	public ServletContext getContext(String arg0) {
		return this;
	}

	@Override
	public String getContextPath() {
		return proxyServletContext.getContextPath();
	}

	@Override
	public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
		return proxyServletContext.getDefaultSessionTrackingModes();
	}

	@Override
	public int getEffectiveMajorVersion() {
		return proxyServletContext.getEffectiveMajorVersion();
	}

	@Override
	public int getEffectiveMinorVersion() {
		return proxyServletContext.getEffectiveMinorVersion();
	}

	@Override
	public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
		return proxyServletContext.getEffectiveSessionTrackingModes();
	}

	@Override
	public FilterRegistration getFilterRegistration(String arg0) {
		return proxyServletContext.getFilterRegistration(arg0);
	}

	@Override
	public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
		return proxyServletContext.getFilterRegistrations();
	}

	@Override
	public String getInitParameter(String arg0) {
		return proxyServletContext.getInitParameter(arg0);
	}

	@Override
	public Enumeration<String> getInitParameterNames() {
		return proxyServletContext.getInitParameterNames();
	}

	@Override
	public JspConfigDescriptor getJspConfigDescriptor() {
		return proxyServletContext.getJspConfigDescriptor();
	}

	@Override
	public int getMajorVersion() {
		return proxyServletContext.getMajorVersion();
	}

	@Override
	public String getMimeType(String arg0) {
		return proxyServletContext.getMimeType(arg0);
	}

	@Override
	public int getMinorVersion() {
		return proxyServletContext.getMajorVersion();
	}

	@Override
	public RequestDispatcher getNamedDispatcher(String arg0) {
		return proxyServletContext.getNamedDispatcher(arg0);
	}

	@Override
	public String getRealPath(String arg0) {
		return proxyServletContext.getRealPath(arg0);
	}

	@Override
	public RequestDispatcher getRequestDispatcher(String arg0) {
		return proxyServletContext.getRequestDispatcher(arg0);
	}

	@Override
	public URL getResource(String arg0) throws MalformedURLException {
		return proxyServletContext.getResource(arg0);
	}

	@Override
	public InputStream getResourceAsStream(String arg0) {
		return proxyServletContext.getResourceAsStream(arg0);
	}

	@Override
	public Set<String> getResourcePaths(String arg0) {
		return proxyServletContext.getResourcePaths(arg0);
	}

	@Override
	public String getServerInfo() {
		return proxyServletContext.getServerInfo();
	}

	@Override
	public Servlet getServlet(String arg0) throws ServletException {
		return proxyServletContext.getServlet(arg0);
	}

	@Override
	public String getServletContextName() {
		return proxyServletContext.getServletContextName();
	}

	@Override
	public Enumeration<String> getServletNames() {
		return proxyServletContext.getServletNames();
	}

	@Override
	public ServletRegistration getServletRegistration(String arg0) {
		return proxyServletContext.getServletRegistration(arg0);
	}

	@Override
	public Map<String, ? extends ServletRegistration> getServletRegistrations() {
		return proxyServletContext.getServletRegistrations();
	}

	@Override
	public Enumeration<Servlet> getServlets() {
		return proxyServletContext.getServlets();
	}

	@Override
	public SessionCookieConfig getSessionCookieConfig() {
		return proxyServletContext.getSessionCookieConfig();
	}

	@Override
	public void log(String arg0) {
		logger.info(arg0);
	}

	@Override
	public void log(Exception arg0, String arg1) {
		logger.error(arg1, arg0);
	}

	@Override
	public void log(String arg0, Throwable arg1) {
		logger.error(arg0, arg1);
	}

	@Override
	public void removeAttribute(String key) {
		JedisUtil.delete(key);
	}

	@Override
	public void setAttribute(String key, Object value) {
		JedisUtil.set(key, value);
	}

	@Override
	public boolean setInitParameter(String arg0, String arg1) {
		return proxyServletContext.setInitParameter(arg0, arg1);
	}

	@Override
	public void setSessionTrackingModes(Set<SessionTrackingMode> arg0) {
		proxyServletContext.setSessionTrackingModes(arg0);
	}
}