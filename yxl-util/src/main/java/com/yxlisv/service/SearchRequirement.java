package com.yxlisv.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.yxlisv.control.AbstractBaseControl;
import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.security.SecurityUtil;

/**
 * 模糊查询工具：组装查询条件 eg:<br/>
  		SearchRequirement searchRequirement = new SearchRequirement();<br/>
		<br/>
		//1、测试基本语法<br/>
		searchRequirement.addEq("name", "yxl");<br/>
		Object [] states = {1,2,3,"ok"};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		searchRequirement.addOrLess("state", states);<br/>
		searchRequirement.addOrLess("state", 2);<br/>
		searchRequirement.addGreater("p.money", 10000);<br/>
		searchRequirement.addLess("age", 28);<br/>
		
		
		//2、测试批量添加条件<br/>
		Map map = new HashMap();<br/>
		map.put("id", 60);<br/>
		map.put("name", "yxl");<br/>
		
		String []sex = {"男", "女"};<br/>
		map.put("sex", sex);<br/>
		<br/>
		String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addGreaterFromMap(names, map);<br/>
		<br/>
		System.out.println(searchRequirement.getSql());<br/>
 * @author john Local
 */
public class SearchRequirement {
	
	/** 排序 {String[](id, asc), ......  } */
	private List<String[]> orderList = new ArrayList();
	/** 查询条件MAP */
	public Map srMap = new HashMap();
	
	/**
	 * 获取排序的sql
	 * @return 如：id asc, name desc
	 * @autor yxl
	 */
	public String getOrderSql(){
		String sql = " ";
		
		int count=0;
		for (Iterator it=orderList.iterator(); it.hasNext();){
			if(count > 0) sql += ", ";
			
			String[] order = (String[]) it.next();
			sql += order[0] + " ";
			sql += order[1];
			
			count++;
		}
		
		return sql;
	}
	
	/**
	 * 添加排序条件
	 * @param order 排序条件 如：addOrder(new String[] {"id","desc"});
	 * @autor yxl
	 */
	public void addOrder(String[] order){
		orderList.add(order);
	}

	private String sql = "1=1";
	
	/** 逻辑字符串 and */
	protected static String LOGICSTR_AND = "and";
	/** 逻辑字符串 or */
	protected static String LOGICSTR_OR = "or";
	
	/** 运算符号 等于 */
	protected static String MARK_EQ = "=";
	/** 运算符号 不等于 */
	protected static String MARK_NOTEQ = "!=";
	/** 运算符号 小于 */
	protected static String MARK_LESS = "<";
	/** 运算符号 大于 */
	protected static String MARK_GREATER = ">";
	/** 运算符号 like */
	protected static String MARK_LIKE = " like ";
	
	/**
	 * 生成sql
	 * @param logicStr 逻辑字符串
	 * @param mark 运算符号
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	protected void generate(String logicStr, String mark, String name, Object value){
		if(value==null) return;
		if(value.toString().trim().length()<1) return;
		//like %
		String likeStr = "";
		if(mark.equals(MARK_LIKE)) likeStr = "%";
		if(value instanceof Integer || value instanceof Long || value instanceof Float || value instanceof Double) 
			sql += " " + logicStr + " " + name + mark + "'" + likeStr + value + likeStr + "'";
		else if(value instanceof String) sql += " " + logicStr + " "  + name + mark + "'" + likeStr + SecurityUtil.simpleClear(value.toString()) + likeStr + "'";
		else if(value == null) sql += " " + logicStr + " "  + name + mark + null;
		else if(value instanceof String[]){
			//如果是string数组要把查询条件拼装成如下格式：
			/*
			 * 1=1 and id='60' and (sex='男' or sex='女' or sex='不男不女')
			 */
			String[] sVal = (String[]) value;
			String tempSql = " (";
			boolean isFirst = true;
			for(String val : sVal){
				if(!isFirst) tempSql += "or ";
				tempSql += name + mark + "'" + likeStr + val + likeStr + "'";
				isFirst = false;
			}
			tempSql += ")";
			sql += " " + logicStr + tempSql;
		}
	}
	
	/**
	 * 添加相等的条件 eg:<br/>
	 * searchRequirement.addEq("name", "yxl");<br/>
	 * sql: 1=1 and name='yxl'<br/>
	 * 
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	public void addEq(String name, Object value){
		
		this.generate(SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_EQ, name, value);
	}
	
	/**
	 * 添加不相等的条件 eg:<br/>
	 * searchRequirement.addNotEq("name", "yxl");<br/>
	 * sql: 1=1 and name!='yxl'<br/>
	 * 
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	public void addNotEq(String name, Object value){
		
		this.generate(SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_NOTEQ, name, value);
	}
	
	
	/**
	 * 添加like条件 eg:<br/>
	 * searchRequirement.addLike("name", "yxl");<br/>
	 * sql: 1=1 and name like '%yxl%'<br/>
	 * 
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	public void addLike(String name, Object value){
		
		this.generate(SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_LIKE, name, value);
	}
	
	/**
	 * 添加小于的条件 eg:<br/>
	 * searchRequirement.addLess("age", 28);<br/>
	 * sql: 1=1 and age<28<br/>
	 * 
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	public void addLess(String name, Object value){
		
		this.generate(SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_LESS, name, value);
	}
	
	/**
	 * 添加大于的条件 eg:<br/>
	 * searchRequirement.addGreater("p.money", 10000);<br/>
	 * sql: 1=1 and p.money>10000<br/>
	 * 
	 * @param name 字段名称
	 * @param value 值
	 * @autor yxl
	 */
	public void addGreater(String name, Object value){
		
		this.generate(SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_GREATER, name, value);
	}
	
	/**
	 * 添加或者的条件
	 * @param name 字段名称
	 * @param values 值的字符串
	 * @param mark 运算符
	 * @autor yxl
	 */
	protected void addOr(String name, Object value, String mark){
		//如果是一个数组
		if(value instanceof Object[]){
			Object[] values = (Object[]) value;
			for(Object obj : values){
				this.generate(SearchRequirement.LOGICSTR_OR, mark, name, obj);
			}
		} else this.generate(SearchRequirement.LOGICSTR_OR, mark, name, value);
	}
	
	/**
	 * 添加条件或者(相等) eg：<br/>
	 	Object [] states = {1,2,3,"ok"};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		or state=1 or state=2 or state=3 or state='ok'<br/>
		
	 * @param name 字段名称
	 * @param values 值的字符串,可以直接传一个对象，或者一个对象数组
	 * @autor yxl
	 */
	public void addOrEq(String name, Object value){
		this.addOr(name, value, SearchRequirement.MARK_EQ);
	}
	
	/**
	 * 添加条件或者(不相等) eg：<br/>
	 	Object [] states = {1,2,3,"ok"};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		or state!=1 or state!=2 or state!=3 or state!='ok'<br/>
		
	 * @param name 字段名称
	 * @param values 值的字符串,可以直接传一个对象，或者一个对象数组
	 * @autor yxl
	 */
	public void addOrNotEq(String name, Object value){
		this.addOr(name, value, SearchRequirement.MARK_NOTEQ);
	}
	
	
	/**
	 * 添加条件或者(like) eg：<br/>
	 	Object [] states = {1,2};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		or state like %1% or state like %2%<br/>
		
	 * @param name 字段名称
	 * @param values 值的字符串,可以直接传一个对象，或者一个对象数组
	 * @autor yxl
	 */
	public void addOrLike(String name, Object value){
		this.addOr(name, value, SearchRequirement.MARK_LIKE);
	}
	
	/**
	 * 添加条件或者(小于) eg：<br/>
	 	Object [] states = {1,2,3,"ok"};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		or state<1 or state<2 or state<3 or state<'ok'<br/>
		
	 * @param name 字段名称
	 * @param values 值的字符串,可以直接传一个对象，或者一个对象数组
	 * @autor yxl
	 */
	public void addOrLess(String name, Object value){
		this.addOr(name, value, SearchRequirement.MARK_LESS);
	}
	
	/**
	 * 添加条件或者(大于) eg：<br/>
	 	Object [] states = {1,2,3,"ok"};<br/>
		searchRequirement.addOrEq("state", states);<br/>
		or state>1 or state>2 or state>3 or state>'ok'<br/>
		
	 * @param name 字段名称
	 * @param values 值的字符串,可以直接传一个对象，或者一个对象数组
	 * @autor yxl
	 */
	public void addOrGreater(String name, Object value){
		this.addOr(name, value, SearchRequirement.MARK_GREATER);
	}
	
	
	/**
	 * 批量添加相等的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addFromMap(names, map);<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @param mark 运算符
	 * @autor yxl
	 */
	protected void addFromMap(String []names, Map map, String logicStr, String mark){
		//排序处理
		boolean hasOrder = false; String orderName = "-1"; int orderType = 0;
		if(map.get("orderName")!=null && map.get("orderType")!=null){
			orderName = map.get("orderName").toString().trim();
			orderType = NumberUtil.parseInt(map.get("orderType").toString().trim());
		}
		
		//把处理后的查询条件返回
		Map searchMap = new HashMap();
		if(map != null){
			map.putAll(getDataIsolationMap());//添加数据隔离DATA
			for(String name : names){
				if(name.equals(orderName)) hasOrder = true;
				Object val = map.get(name);
				if(val !=null){
					if (val instanceof String[]){
						String[] sVal = (String[]) val;
						if(sVal.length == 1){//如果数组中只有一条数据
							if(name.endsWith(".id")) this.add(name, sVal[0].toString(), logicStr, SearchRequirement.MARK_EQ);
							else this.add(name, sVal[0].toString(), logicStr, mark);
							searchMap.put(name, sVal[0]);
						} else if(sVal.length > 1){
							//如果数组中有多条数据，要把查询条件拼装成如下格式：
							/*
							 * 1=1 and id='60' and (sex='男' or sex='女' or sex='不男不女')
							 */
							if(name.endsWith(".id")) this.add(name, val, logicStr, SearchRequirement.MARK_EQ);
							else this.add(name, val, logicStr, mark);
							searchMap.put(name, sVal[0]);
						}
					} else{
						if(name.endsWith(".id")) this.add(name, val.toString(), logicStr, SearchRequirement.MARK_EQ);
						else this.add(name, val.toString(), logicStr, mark);
						searchMap.put(name, val);
					}
				}
			}
			
			//排序处理
			if(hasOrder){
				if(orderType==0) searchMap.put("order_" + orderName, "");
				else searchMap.put("order_" + orderName, orderType);
				String orderStr = "-1";
				if(orderType==-1) orderStr = "DESC";
				else if(orderType==1) orderStr = "ASC";
				if(!orderStr.equals("-1")) {
					this.addOrder(new String[]{orderName, orderStr});
					searchMap.put("orderName", orderName);
					searchMap.put("orderType", orderType);
				}
			}
			
			this.srMap.putAll(searchMap);
			//map.clear();
			map.putAll(searchMap);
		}
	}
	
	/**
	 * 添加查询条件
	 * @param name 字段名称
	 * @param val 值
	 * @param logicStr 逻辑运算符
	 * @param mark 运算符
	 * @autor yxl
	 */
	private void add(String name, Object val, String logicStr, String mark){
		//不能为空字符串
		if(val==null || val.toString().length()<1) return;
		if(logicStr.equals(SearchRequirement.LOGICSTR_AND)){
			if(mark.equals(SearchRequirement.MARK_EQ)) this.addEq(name, val);
			else if(mark.equals(SearchRequirement.MARK_NOTEQ)) this.addNotEq(name, val);
			else if(mark.equals(SearchRequirement.MARK_LESS)) this.addLess(name, val);
			else if(mark.equals(SearchRequirement.MARK_GREATER)) this.addGreater(name, val);
			else if(mark.equals(SearchRequirement.MARK_LIKE)) this.addLike(name, val);
		} else{
			if(mark.equals(SearchRequirement.MARK_EQ)) this.addOrEq(name, val);
			else if(mark.equals(SearchRequirement.MARK_NOTEQ)) this.addOrNotEq(name, val);
			else if(mark.equals(SearchRequirement.MARK_LESS)) this.addOrLess(name, val);
			else if(mark.equals(SearchRequirement.MARK_GREATER)) this.addOrGreater(name, val);
			else if(mark.equals(SearchRequirement.MARK_LIKE)) this.addOrLike(name, val);
		}
	}
	
	
	/**
	 * 批量添加相等的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addEqFromMap(names, map);<br/>
		sql: 1=1 and id=60 and sex='男' and name='yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addEqFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_EQ);
		return map;
	}
	
	
	/**
	 * 批量添加不相等的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addEqFromMap(names, map);<br/>
		sql: 1=1 and id!=60 and sex!='男' and name!='yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addNotEqFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_NOTEQ);
		return map;
	}
	
	
	/**
	 * 批量添加like的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addLikeFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_LIKE);
		return map;
	}
	
	/**
	 * 批量添加like的条件(or): eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addOrLikeFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_OR, SearchRequirement.MARK_LIKE);
		return map;
	}
	
	
	/**
	 * 批量添加相等的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addEqFromMap(names, map);<br/>
		1=1 or id=60 or sex='男' or name='yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addOrEqFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_OR, SearchRequirement.MARK_EQ);
		return map;
	}
	
	/**
	 * 批量添加不相等的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addEqFromMap(names, map);<br/>
		1=1 or id!=60 or sex!='男' or name!='yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addOrNotEqFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_OR, SearchRequirement.MARK_NOTEQ);
		return map;
	}
	
	
	
	/**
	 * 批量添加小于的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addLessFromMap(names, map);<br/>
		sql: 1=1 and id<60 and sex<'男' and name<'yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addLessFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_LESS);
		return map;
	}
	
	
	/**
	 * 批量添加小于的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addOrLessFromMap(names, map);<br/>
		sql: 1=1 or id<60 or sex<'男' or name<'yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addOrLessFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_OR, SearchRequirement.MARK_LESS);
		return map;
	}
	
	
	
	/**
	 * 批量添加大于的条件: eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addGreaterFromMap(names, map);<br/>
		sql: 1=1 and id>60 and sex>'男' and name>'yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addGreaterFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_AND, SearchRequirement.MARK_GREATER);
		return map;
	}
	
	
	/**
	 * 批量添加大于的条件(or): eg:<br/>
	 	String []names = {"id", "sex", "name", "null"};<br/>
		searchRequirement.addOrGreaterFromMap(names, map);<br/>
		sql: 1=1 or id>60 or sex>'男' or name>'yxl'<br/>
		
	 * @param names 条件名称的数组
	 * @param map <String name, String value> 或者 <String name, String []value>(request.getParameterMap())
	 * @return 查询条件MAP，调用后可以不接收，传入的map会自动重新赋值
	 * @autor yxl
	 */
	public Map addOrGreaterFromMap(String []names, Map map){
		this.addFromMap(names, map, SearchRequirement.LOGICSTR_OR, SearchRequirement.MARK_GREATER);
		return map;
	}
	
	/**
	 * 批量添加排序 <br/>
	 * eg: order_by=name --> name desc, order_by=title,asc -- > title asc
	 * @autor yxl
	 */
	public Map addOrderFromMap(String []names, Map map){
		Map orderMap = new HashMap();
		if(map.get("order_by") != null) {
			if(!(map.get("order_by") instanceof String[])){//如果不是数组，先转换成数组再进行操作(统一格式，方便后面处理)
				String orderBy =  map.get("order_by").toString();
				map.put("order_by", new String[]{orderBy});
			}
			String[] orderByArray = (String[]) map.get("order_by");
			for(String orderBy : orderByArray){
				String fieldName = orderBy;
				String order = "desc";//默认降序
				if(orderBy.indexOf(",") != -1){//如果用户传了升序或者降序
					fieldName = orderBy.split(",")[0];
					order = orderBy.split(",")[1].toLowerCase();
					if(!order.equals("asc")) order = "desc";//安全考虑，不要相信用户的任何请求
				}
				
				//如果定义了此属性，安全考虑，不要相信用户的任何请求
				if(Arrays.asList(names).contains(fieldName)){
					this.addOrder(new String[] {fieldName, order});
					orderMap.put("order_by_" + fieldName, order);
				}
			}
		}
		return orderMap;
	}
	
	/**
	 * 得到sql语句
	 * @autor yxl
	 */
	public String getSql(){
		return sql;
	}
	
	/**
	 * 获取数据隔离数据
	 */
	public static Map getDataIsolationMap(){
		Map dataIsolationMap  = new HashMap();
		HttpSession session = null;
		try{
			session = AbstractBaseControl.getRequest().getSession();
		} catch(Exception e) {
			return dataIsolationMap;
		}
		if(session.getAttribute("dataIsolationMap")!=null) dataIsolationMap = (Map) session.getAttribute("dataIsolationMap");
		return dataIsolationMap;
	}
	
	/**
	 * 添加数据隔离(作用范围限制在httpSession中，pageSearch和getList方法有效)
	 */
	public static void addDataIsolation(String key, String value){
		Map dataIsolationMap  = getDataIsolationMap();
		dataIsolationMap.put(key, value);
		HttpSession session = AbstractBaseControl.getRequest().getSession();
		session.setAttribute("dataIsolationMap", dataIsolationMap);
	}
	
	public static void main(String[] args) {
		SearchRequirement searchRequirement = new SearchRequirement();
		
		//1、测试基本语法
		/*searchRequirement.addEq("name", "yxl");
		Object [] states = {1,2,3,"ok"};
		searchRequirement.addOrEq("state", states);
		searchRequirement.addOrLess("state", states);
		searchRequirement.addOrLess("state", 2);
		searchRequirement.addGreater("p.money", 10000);*/
		//searchRequirement.addLess("age", 28);
		//searchRequirement.addLike("value", "com'");
		//searchRequirement.addEq("type", null);
		
		
		//2、测试批量添加条件
		Map map = new HashMap();
		map.put("id", 60);
		map.put("name", null);
		
		String []sex = {"男", "女", "不男不女"};
		map.put("sex", sex);
		
		String []names = {"id", "sex", "name", "null"};
		searchRequirement.addOrLikeFromMap(names, map);
		
		System.out.println(searchRequirement.getSql());
	}
}
