package com.yxlisv.util.hibernate;

import org.hibernate.SessionFactory;

import com.yxlisv.util.datasource.DataSourceBean;
import com.yxlisv.util.datasource.DynamicDataSource;

/**
 * <p>hibernate 多数据库 SessionFactory</p>
 * <p>支持不同数据库</p>
 * @author 杨雪令
 * @time 2016年3月9日上午10:17:40
 * @version 1.0
 */
public class MulitDBSessionFactory {

	// 默认数据库SessionFactory
	private static SessionFactory defaultSessionFactory;
	
	// MySql数据库SessionFactory
	private static SessionFactory mySQLSessionFactory;
	
	// Oracle数据库SessionFactory
	private static SessionFactory oracleSessionFactory;
	
	// DB2数据库SessionFactory
	private static SessionFactory db2SessionFactory;
	
	// SQLServer数据库SessionFactory
	private static SessionFactory sqlServerSessionFactory;

	/**
	 * <p>设置默认的SessionFactory</p>
	 * @param sessionFactory 传入的 SessionFactory
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:35:36
	 * @version 1.0
	 */
	public static void setDefaultSessionFactory(SessionFactory sessionFactory) {
		MulitDBSessionFactory.defaultSessionFactory = sessionFactory;
	}

	/**
	 * <p>设置MySql的SessionFactory</p>
	 * @param sessionFactory 传入的 SessionFactory
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:35:36
	 * @version 1.0
	 */
	public static void setMySQLSessionFactory(SessionFactory mySQLSessionFactory) {
		MulitDBSessionFactory.mySQLSessionFactory = mySQLSessionFactory;
	}

	/**
	 * <p>设置Oracle的SessionFactory</p>
	 * @param sessionFactory 传入的 SessionFactory
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:35:36
	 * @version 1.0
	 */
	public static void setOracleSessionFactory(SessionFactory oracleSessionFactory) {
		MulitDBSessionFactory.oracleSessionFactory = oracleSessionFactory;
	}

	/**
	 * <p>设置DB2的SessionFactory</p>
	 * @param sessionFactory 传入的 SessionFactory
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:35:36
	 * @version 1.0
	 */
	public static void setDb2SessionFactory(SessionFactory db2SessionFactory) {
		MulitDBSessionFactory.db2SessionFactory = db2SessionFactory;
	}

	/**
	 * <p>设置SQLServer的SessionFactory</p>
	 * @param sessionFactory 传入的 SessionFactory
	 * @author 杨雪令
	 * @time 2016年3月9日上午10:35:36
	 * @version 1.0
	 */
	public static void setSqlServerSessionFactory(SessionFactory sqlServerSessionFactory) {
		MulitDBSessionFactory.sqlServerSessionFactory = sqlServerSessionFactory;
	}

	/**
	 * <p>获取SessionFactory</p>
	 * <p>根据动态数据源获取合适的SessionFactory</p>
	 * @return SessionFactory 
	 * @author 杨雪令
	 * @time 2016年3月13日上午10:00:22
	 * @version 1.0
	 */
	public static SessionFactory getSessionFactory() {

		DataSourceBean dataSourceBean = DynamicDataSource.getCurrentDataSourceBean();
		if (dataSourceBean != null) {
			if (dataSourceBean.getDbType().equals(DataSourceBean.DBTypes.MYSQL)) return mySQLSessionFactory;
			if (dataSourceBean.getDbType().equals(DataSourceBean.DBTypes.ORACLE)) return oracleSessionFactory;
			if (dataSourceBean.getDbType().equals(DataSourceBean.DBTypes.DB2)) return db2SessionFactory;
			if (dataSourceBean.getDbType().equals(DataSourceBean.DBTypes.SQLSERVER)) return sqlServerSessionFactory;
		}
		return defaultSessionFactory;
	}
}