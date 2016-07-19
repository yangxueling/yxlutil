package com.yxlisv.util.datasource;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.resource.PropertiesUtil;

/**
 * <p>动态数据源</p>
 * <p>切换数据源时，把数据源对象存放到用户http session中：@see DataSourceBean，http session-key：DynamicDataSourceFilter.DATA_SOURCE_HTTP_SESSION_KEY</p>
 * <p>通过配置过滤器自动切换数据源：@see DynamicDataSourceFilter</p>
 * @author 杨雪令
 * @time 2016年3月12日下午1:16:36
 * @version 1.0
 */
public class DynamicDataSource implements DataSource {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	public static Logger logger = LoggerFactory.getLogger(DynamicDataSource.class);

	/** 数据源 http session key */
	final public static String DATA_SOURCE_HTTP_SESSION_KEY = "JDBC_DATA_SOURCE_HTTP_SESSION_KEY";

	// 创建新数据源时并发锁
	protected static final Lock createlock = new ReentrantLock();

	// 默认数据源
	protected static DataSource defaultDataSource;

	// 默认数据源对象
	protected static DataSourceBean defaultDataSourceBean;

	// DataSource缓存
	protected static Map<String, DataSource> dataSourceCache = new HashMap<String, DataSource>();

	// 当前线程中的 DataSourceBean
	protected static final ThreadLocal<DataSourceBean> currentDataSourceBean = new ThreadLocal<DataSourceBean>();

	// 当前线程中的 DataSource
	protected static final ThreadLocal<DataSource> currentDataSource = new ThreadLocal<DataSource>();

	// jdbc默认配置
	protected static Properties jdbcConfig = PropertiesUtil.readProperties("database/jdbc.properties", DataSourceFactory.class);

	/**
	 * <p>创建默认数据源</p>
	 * @author 杨雪令
	 * @time 2016年3月13日上午8:37:19
	 * @version 1.0
	 */
	public static synchronized void createDefaultDataSource() {
		if (defaultDataSource != null) return;
		defaultDataSourceBean = new DataSourceBean(jdbcConfig.getProperty("jdbc.url"), jdbcConfig.getProperty("jdbc.username"), jdbcConfig.getProperty("jdbc.password"));
		defaultDataSource = DataSourceFactory.createC3P0(defaultDataSourceBean);// 创建默认数据源
		if(defaultDataSource == null) return;
		dataSourceCache.put(defaultDataSourceBean.getIdentifierKey(), defaultDataSource);// 缓存数据源
		logger.info("create default DataSource success");
	}

	/**
	 * <p>获取当前线程变量中的数据源对象</p>
	 * @return DataSource 数据源Bean
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:26:21
	 * @version 1.0
	 */
	public static DataSourceBean getCurrentDataSourceBean() {
		DataSourceBean dataSourceBean = currentDataSourceBean.get();
		if (dataSourceBean != null) return dataSourceBean;
		return defaultDataSourceBean;
	}

	/**
	 * <p>获取一个数据源</p>
	 * <p>尝试在线程变量中获取，如果获取不到返回默认数据源</p>
	 * @return DataSource 数据源对象
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:26:21
	 * @version 1.0
	 */
	public static DataSource getDataSource() {
		DataSource dataSource = currentDataSource.get();
		if (dataSource != null) return dataSource;
		if (defaultDataSource != null) return defaultDataSource;
		createDefaultDataSource();
		return defaultDataSource;
	}

	/**
	 * <p>尝试从数据源缓存中激活传入的数据源</p>
	 * @param dataSourceBean 要激活的数据源对象
	 * @return boolean true：成功，false：失败
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:27:38
	 * @version 1.0
	 */
	protected static boolean activeFromCache(DataSourceBean dataSourceBean) {
		String key = dataSourceBean.getIdentifierKey();// 数据源唯一标识
		if (dataSourceCache.containsKey(key)) {// 如果缓存中存在
			currentDataSource.set(dataSourceCache.get(key));// 读取数据源并设置到当前线程变量（激活）
			currentDataSourceBean.set(dataSourceBean);
			return true;
		}
		return false;
	}

	/**
	 * <p>激活传入的数据源</p>
	 * <p>先尝试从缓存获取数据源激活，如果没有，创建数据源放入缓存并激活</p>
	 * @param dataSourceBean 要激活的数据源对象
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:30:38
	 * @version 1.0
	 */
	public static void active(DataSourceBean dataSourceBean) {
		if (activeFromCache(dataSourceBean)) return;// 尝试从数据源缓存中激活传入的数据源
		createlock.lock();// 多线程并发控制
		try {
			if (activeFromCache(dataSourceBean)) return;// 尝试从数据源缓存中激活传入的数据源
			DataSource dataSource = DataSourceFactory.createC3P0(dataSourceBean);// 创建一个数据源
			dataSourceCache.put(dataSourceBean.getIdentifierKey(), dataSource);// 缓存数据源
			currentDataSource.set(dataSource);// 设置数据源到当前线程变量（激活）
			currentDataSourceBean.set(dataSourceBean);
		} finally {
			createlock.unlock();// 释放锁
		}
	}

	/**
	 * <p>释放数据源</p>
	 * <p>从当前线程变量中移除数据源</p>
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:36:25
	 * @version 1.0
	 */
	public static void release() {
		currentDataSource.remove();
		currentDataSourceBean.remove();
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return getDataSource().getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		getDataSource().setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		getDataSource().setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return getDataSource().getLoginTimeout();
	}

	@Override
	public java.util.logging.Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return getDataSource().getParentLogger();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return getDataSource().unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return getDataSource().isWrapperFor(iface);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return getDataSource().getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getDataSource().getConnection(username, password);
	}
}