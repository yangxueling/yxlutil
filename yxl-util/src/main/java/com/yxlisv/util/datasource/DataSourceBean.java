package com.yxlisv.util.datasource;

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>数据源对象</p>
 * @author 杨雪令
 * @time 2016年3月12日下午1:08:50
 * @version 1.0
 */
public class DataSourceBean {
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	public static Logger logger = LoggerFactory.getLogger(DataSourceBean.class);
	
	//数据库连接
	private String jdbcUrl;
	//数据库用户名
	private String username;
	//数据库密码
	private String password;
	//数据库类别
	private DBTypes dbType;
	/** 数据库类型 */
	public enum DBTypes{MYSQL, ORACLE, DB2, SQLSERVER}
	
	/**
	 * <p>创建DataSourceBean对象</p>
	 * @param jdbcUrl 数据库连接
	 * @param username 数据库用户名
	 * @param pasword 数据库密码
	 * @author 杨雪令
	 * @time 2016年3月17日下午6:04:19
	 * @version 1.0
	 */
	public DataSourceBean(String jdbcUrl, String username, String pasword) {
		setJdbcUrl(jdbcUrl);
		setUsername(username);
		setPassword(pasword);
	}

	/**
	 * <p>根据连接字符串创建</p>
	 * <p>参考toString方法</p>
	 * @param dataSourceStr
	 * @author 杨雪令
	 * @time 2016年3月17日下午5:59:44
	 * @version 1.0
	 */
	public DataSourceBean(String dataSourceStr) {
		String[] items = dataSourceStr.split("#");
		setJdbcUrl(items[0]);
		setUsername(items[1]);
		setPassword(items[2]);
	}

	/**
	 * <p>获取数据库连接</p>
	 * @return String 数据库连接
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:11:34
	 * @version 1.0
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}

	/**
	 * <p>设置数据库连接</p>
	 * @param jdbcUrl 数据库连接
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:11:45
	 * @version 1.0
	 */
	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
		if(jdbcUrl.toUpperCase().contains("MYSQL")) dbType = DBTypes.MYSQL;
		else if(jdbcUrl.toUpperCase().contains("ORACLE")) dbType = DBTypes.ORACLE;
		else if(jdbcUrl.toUpperCase().contains("DB2")) dbType = DBTypes.DB2;
		else if(jdbcUrl.toUpperCase().contains("SQLSERVER")) dbType = DBTypes.SQLSERVER;
		else throw new RuntimeException("unkonwn jdbcUrl: " + jdbcUrl);
	}

	/**
	 * <p>获取数据库用户名</p>
	 * @return String 数据库用户名
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:12:33
	 * @version 1.0
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * <p>设置数据库用户名</p>
	 * @param username 数据库用户名 
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:12:48
	 * @version 1.0
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * <p>获取数据库密码</p>
	 * @return String 数据库密码
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:13:06
	 * @version 1.0
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * <p>设置数据库密码</p>
	 * @param password 数据库密码 
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:13:24
	 * @version 1.0
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	
	/**
	 * <p>获取数据库类类别</p>
	 * @return String 数据库类别
	 * @author 杨雪令
	 * @time 2016年3月13日上午10:08:46
	 * @version 1.0
	 */
	public DBTypes getDbType() {
		return dbType;
	}

	/**
	 * <p>设置数据库类类别</p>
	 * @param dbType 数据库类别
	 * @author 杨雪令
	 * @time 2016年3月13日上午10:08:46
	 * @version 1.0
	 */
	public void setDbType(DBTypes dbType) {
		this.dbType = dbType;
	}

	/**
	 * <p>获取数据源的唯一标识</p>
	 * @return String 数据源的唯一标识
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:11:00
	 * @version 1.0
	 */
	public String getIdentifierKey() {
		return DigestUtils.md5Hex(jdbcUrl + "-" + username + "-" + password);
	}
	
	/**
	 * <p>获取数据库驱动类名</p>
	 * @return String 数据库驱动类名
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:07:33
	 * @version 1.0
	 */
	public String getDriverClass(){
		if(dbType.equals(DBTypes.MYSQL)) return "com.mysql.jdbc.Driver";
		if(dbType.equals(DBTypes.ORACLE)) return "oracle.jdbc.OracleDriver";
		if(dbType.equals(DBTypes.SQLSERVER)) return "com.microsoft.sqlserver.jdbc.SQLServerDriver";
		if(dbType.equals(DBTypes.DB2)) return "com.db2.jdbc.Driver";
		
		throw new RuntimeException("getDriverClass error, unkonwn jdbcUrl: " + jdbcUrl);
	}
	
	
	/**
	 * <p>toString方法</p>
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:11:00
	 * @version 1.0
	 */
	public String toString() {
		String securityPassword = "******";
		if(password.length() > 4) {
			securityPassword = password.substring(0, 2);
			securityPassword += "**";
			securityPassword += password.substring(password.length()-2, password.length());
		} else if(password.length() > 1) {
			securityPassword = password.substring(0, 1);
			securityPassword += "**";
			securityPassword += password.substring(password.length()-1, password.length());
		}
		return "dbType: "+ getDbType() +", jdbcUrl: "+ getJdbcUrl() +", username: "+ getUsername() +", password: "+ securityPassword;
	}
	
	
	/**
	 * <p>info方法</p>
	 * @author 杨雪令
	 * @time 2016年3月12日下午1:11:00
	 * @version 1.0
	 */
	public String info() {
		return jdbcUrl + "#" + username + "#" + password;
	}
}