package com.yxl.dao;

import java.util.List;

import com.yxl.service.SearchRequirement;
import com.yxl.util.Page;
import com.yxl.util.reflect.GenericsUtils;

/**
 * 基础的dao类 (实体类专用)
 * Entry 实体类型
 * @author yxl
 */
public class AbstractBaseEntryHibernateDAO<Entry> extends AbstractBaseHibernateDAO{
	
	/** dao层管理的实体类型 */
    protected Class<Entry> entityClass = GenericsUtils.getGenericClass(getClass());
    
 
    /**
	 * 根据ID查询一条数据
	 * @author yxl
	 */
	public Entry get(String id) {
		return (Entry) get(id, entityClass);
	}
	
	/**
	 * 根据ID查询一条数据
	 * @author yxl
	 */
	public Entry get(int id) {
		return (Entry) get(id, entityClass);
	}
	
	/**
	 * 根据查询条件获取一条数据
	 * @author yxl
	 */
	public Entry get(SearchRequirement sr) {
		
		String hql = "from "+ entityClass.getName() +
				" where " + sr.getSql();
		
		//如果有排序条件
		if(!sr.getOrderSql().trim().equals("")) hql += " order by" + sr.getOrderSql();
		List list = getHibernateTemplate().find(hql);
		
		if(list.size() > 0) return (Entry) list.get(0);
		return null;
	}
	
	/**
     * 根据属性名和属性值查询数据
     * @param name 属性名称(key)
     * @param value 属性值
     * @return 符合条件的数据列表
     * @author yxl
     */
    public List<Entry> findBy(String name, Object value) {
        
    	String hql = "from " + entityClass.getName();
        SearchRequirement sr = new SearchRequirement();
        sr.addEq(name, value);
        
        hql += " where " + sr.getSql();
        
        return getHibernateTemplate().find(hql);
    }
    
    /**
	 * 根据HQL查询对象
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected Entry getOne(String hql){
		return (Entry) super.getOne(hql);
	}
	
	/**
	 * 根据HQL查询对象
	 * @autor yxl
	 * createtime Feb 15, 2012
	 */
	protected Entry findOne(String hql){
		return getOne(hql);
	}
    
    /**
     * 根据属性名和属性值查询数据
     * @param name 属性名称(key)
     * @param value 属性值
     * @return 符合条件的数据列表
     * @author yxl
     */
    public Entry findOne(String name, Object value) {
        
    	List<Entry> list = findBy(name, value);
    	if(list.size()>0) return list.get(0);
    	return null;
    }
	
	/**
	 * 查询所有数据
	 * @author yxl
	 */
	public List<Entry> getAll() {
		return getHibernateTemplate().find("from " + entityClass.getName());
	}
	
    /**
	 * 查询所有数据(模糊查询)
	 * @param sr 模糊查询的条件
	 * @author yxl
	 */
	public List<Entry> getList(SearchRequirement sr) {
		String hql = "from "+ entityClass.getName() +
				" where " + sr.getSql();
		
		//如果有排序条件
		if(!sr.getOrderSql().trim().equals("")) hql += " order by" + sr.getOrderSql();
		return getHibernateTemplate().find(hql);
	}
	
	/**
	 * 查询所有数据(模糊查询)
	 * @param sr 模糊查询的条件
	 * @param maxCount 返回最大记录条数
	 * @author yxl
	 */
	public List<Entry> getList(SearchRequirement sr, int maxCount) {
		String hql = "from "+ entityClass.getName() +
				" where " + sr.getSql();
		
		//如果有排序条件
		if(!sr.getOrderSql().trim().equals("")) hql += " order by" + sr.getOrderSql();
		return getList(hql, maxCount);
	}

	/**
	 * 分页查询(模糊查询)
	 * @param sr 模糊查询的条件
	 * @author yxl
	 */
	public void pageSearch(Page page, SearchRequirement sr) {
		String hql = "from "+ entityClass.getName();
		
		//如果有查询条件
		if(sr != null){ 
			hql += " where " + sr.getSql();
			//如果有排序条件
			if(!sr.getOrderSql().trim().equals("")) hql += " order by" + sr.getOrderSql();
		}
		pageQuery(hql, page);
	}
}