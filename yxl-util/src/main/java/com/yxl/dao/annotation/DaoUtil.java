package com.yxl.dao.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yxl.dao.AbstractBaseHibernateDAO;

/**
 * dao层工具类
 * @author yxl
 */
@Component
public class DaoUtil extends AbstractBaseHibernateDAO{
	
	
	private static DaoUtil daoUtil;
	/** 设置daoUtil */
	@Autowired(required = true)
	public void setDaoUtil(DaoUtil daoUtil) {
		DaoUtil.daoUtil = daoUtil;
	}


	/**
	 * 根据ID查询对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public static Object getObj(String id, Class objClass) {
		if(id==null || id.trim().length()<1) return null;
		return daoUtil.get(id, objClass);
	}


	/**
	 * 根据ID查询对象
	 * @param objClass 对象Class
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	public static Object getObj(int id, Class objClass) {
		return daoUtil.get(id, objClass);
	}
}