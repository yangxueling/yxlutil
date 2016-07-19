package com.yxlisv.util.datasource;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.resource.PropertiesUtil;

/**
 * <p>数据源工厂</p>
 * @author 杨雪令
 * @time 2016年3月12日下午1:06:45
 * @version 1.0
 */
public class DataSourceFactory {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	public static Logger logger = LoggerFactory.getLogger(DataSourceFactory.class);

	// 连接池配置
	private static Properties poolConfig = PropertiesUtil.readProperties("database/pool.properties", DataSourceFactory.class);

	/**
	 * <p>创建C3P0数据源</p>
	 * @param dataSourceBean 数据源对象
	 * @return DataSource 数据源
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:08:23
	 * @version 1.0
	 */
	public static DataSource createC3P0(DataSourceBean dataSourceBean) {

		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass(dataSourceBean.getDriverClass());
		} catch (PropertyVetoException e) {
			logger.error("Create dataSource for c3p0 has error on set DriverClass: " + dataSourceBean.getDriverClass() + " jdbc url: " + dataSourceBean.getJdbcUrl(), e);
		}
		dataSource.setJdbcUrl(dataSourceBean.getJdbcUrl());
		dataSource.setUser(dataSourceBean.getUsername());
		dataSource.setPassword(dataSourceBean.getPassword());

		// 从配置文件读取设置连接池参数
		dataSource.setAutoCommitOnClose(Boolean.parseBoolean(poolConfig.getProperty("pool.autoCommitOnClose")));
		dataSource.setInitialPoolSize(NumberUtil.parseInt(poolConfig.getProperty("pool.initialPoolSize")));
		dataSource.setMinPoolSize(NumberUtil.parseInt(poolConfig.getProperty("pool.minPoolSize")));
		dataSource.setMaxPoolSize(NumberUtil.parseInt(poolConfig.getProperty("pool.maxPoolSize")));
		dataSource.setCheckoutTimeout(NumberUtil.parseInt(poolConfig.getProperty("pool.checkoutTimeout")));
		dataSource.setAcquireRetryDelay(NumberUtil.parseInt(poolConfig.getProperty("pool.acquireRetryDelay")));
		dataSource.setAcquireIncrement(NumberUtil.parseInt(poolConfig.getProperty("pool.acquireIncrement")));
		dataSource.setMaxIdleTime(NumberUtil.parseInt(poolConfig.getProperty("pool.maxIdleTime")));
		dataSource.setMaxIdleTimeExcessConnections(NumberUtil.parseInt(poolConfig.getProperty("pool.maxIdleTimeExcessConnections")));
		dataSource.setIdleConnectionTestPeriod(NumberUtil.parseInt(poolConfig.getProperty("pool.idleConnectionTestPeriod")));
		dataSource.setNumHelperThreads(NumberUtil.parseInt(poolConfig.getProperty("pool.numHelperThreads")));
		dataSource.setBreakAfterAcquireFailure(Boolean.parseBoolean(poolConfig.getProperty("pool.breakAfterAcquireFailure")));
		
		//尝试获取一个连接，如果超过5秒不能获取连接，此数据源无效
		try {
			int checkoutTimeout = dataSource.getCheckoutTimeout();
			dataSource.setCheckoutTimeout(5000);
			Connection conn = dataSource.getConnection();
			dataSource.setCheckoutTimeout(checkoutTimeout);
			conn.close();
		} catch (SQLException e) {
			String message = "!!!Failed create a c3p0 datasource ["+ dataSourceBean +"]";
			logger.error(message);
			dataSource.close();
			throw new RuntimeException(message);
		}
		logger.info("create a c3p0 datasource ["+ dataSourceBean +"]");
		return dataSource;
	}
}