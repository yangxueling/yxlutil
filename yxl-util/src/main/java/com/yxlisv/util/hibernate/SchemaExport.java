package com.yxlisv.util.hibernate;

import java.io.IOException;
import java.util.Properties;

import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import com.yxlisv.util.datasource.DataSourceBean;
import com.yxlisv.util.datasource.DataSourceBean.DBTypes;
import com.yxlisv.util.datasource.DataSourceFactory;

/**
 * <p>导出到数据库实例</p>
 * @author 杨雪令
 * @time 2016年3月14日上午12:52:47
 * @version 1.0
 */
public class SchemaExport {
	
	/**
	 * <p>获取数据库方言</p>
	 * @param dataSourceBean 数据源对象 
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:07:33
	 * @version 1.0
	 */
	public static String getDialect(DataSourceBean dataSourceBean){
		if(dataSourceBean.getDbType().equals(DBTypes.MYSQL)) return "org.hibernate.dialect.MySQLDialect";
		if(dataSourceBean.getDbType().equals(DBTypes.ORACLE)) return "org.hibernate.dialect.OracleDialect";
		if(dataSourceBean.getDbType().equals(DBTypes.SQLSERVER)) return "org.hibernate.dialect.SQLServerDialect";
		if(dataSourceBean.getDbType().equals(DBTypes.DB2)) return "org.hibernate.dialect.DB2Dialect";
		
		throw new RuntimeException("getDialect error, unkonwn jdbcUrl: " + dataSourceBean.getJdbcUrl());
	}

	/**
	 * <p>根据DataSource信息自动在数据库建表</p>
	 * @param dataSourceBean 数据源对象 
	 * @author 杨雪令
	 * @time 2016年3月14日上午12:53:12
	 * @version 1.0
	 */
	public synchronized static void export(DataSourceBean dataSourceBean){
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", getDialect(dataSourceBean));
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", "false");
		hibernateProperties.setProperty("hibernate.cache.use_query_cache", "false");
		LocalSessionFactoryBean localSessionFactoryBean = new LocalSessionFactoryBean();
		localSessionFactoryBean.setDataSource(DataSourceFactory.createC3P0(dataSourceBean));
		localSessionFactoryBean.setPackagesToScan("*");
		localSessionFactoryBean.setHibernateProperties(hibernateProperties);
		try {
			localSessionFactoryBean.afterPropertiesSet();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if(localSessionFactoryBean.getObject()!=null) localSessionFactoryBean.getObject().close();
		}
	}
}