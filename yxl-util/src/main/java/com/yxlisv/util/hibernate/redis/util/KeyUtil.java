package com.yxlisv.util.hibernate.redis.util;

import org.apache.commons.codec.digest.DigestUtils;

import com.yxlisv.util.datasource.DataSourceBean;
import com.yxlisv.util.datasource.DynamicDataSource;

/**
 * <p>Redis Key 工具类</p>
 * @author 杨雪令
 * @time 2016年3月22日上午10:57:19
 * @version 1.0
 */
public class KeyUtil {

	// key前缀
	public static final String keyPrefix = "Hibernate_";
		
	/**
	 * <p>格式化key</p>
	 * @param regionName
	 * @param keyObj
	 * @return String 
	 * @author 杨雪令
	 * @time 2016年3月22日上午11:00:22
	 * @version 1.0
	 */
	public static String formatKey(String regionName, Object keyObj) {
		if (keyObj == null) return "";
		String key = keyObj.toString();
		//key添加数据源
		DataSourceBean dataSourceBean = DynamicDataSource.getCurrentDataSourceBean();
		if (dataSourceBean != null) key += dataSourceBean.getIdentifierKey();
		return keyPrefix + regionName + DigestUtils.md5Hex(key);
	}
}