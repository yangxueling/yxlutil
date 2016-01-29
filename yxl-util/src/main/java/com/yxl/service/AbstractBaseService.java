package com.yxl.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

/**
 * 所有service的父类
 * @author yxl
 * @version 1.0
 * class createTime 2012-6-16
 */
public abstract class AbstractBaseService {
	//定义一个全局的记录器，通过LoggerFactory获取  
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	
	/**
	 * 删除
	 * @return 删除了多少条记录
	 * @autor yxl
	 */
	@Transactional
    public int delete(String id){
    	return 0;
    };
    
    /**
	 * 删除
	 * @return 删除了多少条记录
	 * @autor yxl
	 */
    @Transactional
    public int delete(int id){
    	return 0;
    };
    
    /**
     * 判断是否有操作权限
     * @param id 对象id
     * @param obj 该对象用来计算用户是否有权限操作
     * @author yxl
     */
    protected boolean haveOperatingAuthority(String id, Object obj){
    	return true;
    }
	
    /**
	 * 批量删除
	 * @param ids 1,2,3,4,5
	 * @param obj 该对象用来计算用户是否 有权限操作
	 * @autor yxl
	 */
    @Transactional
	public int batchDelete(String ids, Object obj) {
		
		if(ids==null || ids.equals("")) return 0;
		int count = 0 ;
		//如果只有一条数据
		if(ids.indexOf(",") == -1) {
			if(this.haveOperatingAuthority(ids, obj)){
				count += this.delete(ids);
				try{
					int intId = Integer.parseInt(ids);
					count += this.delete(intId);
				} catch(Exception e){
				}
			}
		} else{
			//如果有多条数据
			String _ids[] = ids.split(",");
			for(int i=0; i<_ids.length; i++){
				String id = _ids[i];
				if(this.haveOperatingAuthority(id, obj)){
					count += this.delete(id);
					try{
						int intId = Integer.parseInt(id);
						count += this.delete(intId);
					} catch(Exception e){
					}
				}
			}
		}
		
		return count;
	}
	
	
	/**
	 * 从request中为实体类 [模块] 拼装模糊查询的条件
	 * @param fieldNames 授权的查询条件集合
	 * @param srMap 查询条件MAP
	 * @return 模糊查询工具
	 * @author yxl
	 */
	protected SearchRequirement getSearchRequirement(String[] fieldNames, Map srMap){

		Map backMap = new HashMap();
		if(srMap == null) srMap = new HashMap();
		backMap.putAll(srMap);//把查询条件map备份
		
		
		SearchRequirement sr = new SearchRequirement();
		
		//默认用like查询，如果request中传了 equals 参数就用 = 查询
		if(srMap != null && srMap.get("equals") != null) sr.addEqFromMap(fieldNames, srMap);
		else sr.addLikeFromMap(fieldNames, srMap);

		srMap.putAll(sr.addOrderFromMap(fieldNames, backMap));//添加排序条件
		
		return sr;
	}
}