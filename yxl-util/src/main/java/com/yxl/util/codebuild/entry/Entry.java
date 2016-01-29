package com.yxl.util.codebuild.entry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.yxl.util.string.StringUtil;

/**
 * 实体
 * @author john Local
 */
public class Entry{
	
	//表名，表注释
	public String tableName, name, tableCmt, packageName;
	/**是否为附属表*/
	public boolean isAffiliated = false;
	//表字段集合
	private List<Property> propertyList = new ArrayList();
	
	/** 得到表字段集合 */
	public List<Property> getPropertyList(){
		return this.propertyList;
	}
	
	/**
	 * 获取ID属性
	 * @autor yxl
	 */
	public Property getIdProperty(){
		for(Property property : propertyList){
			if(property.name.equals("id")) return property;
		}
		return null;
	}
	
	/**
	 * 获取类型为class的所有属性
	 * @autor yxl
	 */
	public List<Property> getClassProperty(){
		List<Property> listClassProperty = new ArrayList();
		for(Property property : propertyList){
			if(property.isClass()) listClassProperty.add(property);
		}
		return listClassProperty;
	}
	
	/**
	 * 是否只有一个父类属性
	 * @autor yxl
	 */
	public boolean onlyOneParentClass(){
		List<Property> propList = getClassProperty();
		int count = 0;
		for(Property property : propList){
			if(property.isClass() && !property.isMainClass()) count++;
		}
		if(count==1) return true;
		return false;
	}
	
	
	/**
	 * 是否只有一个父类属性
	 * @autor yxl
	 */
	public Property getOneParentClassProperty(){
		List<Property> propList = getClassProperty();
		for(Property property : propList){
			if(property.isClass() && !property.isMainClass()) return property;
		}
		return null;
	}
	
	/**
	 * 获取展示的属性
	 */
	public String getViewProp(){
		String viewProp = "id";
		for(Property property : getPropertyList()){
			if(property.name.equals("name")) viewProp = property.name;
		}
		if(viewProp.equals("id")){
			for(Property property : getPropertyList()){
				if(property.name.equals("val")) viewProp = property.name;
			}
		}
		if(viewProp.equals("id")){
			for(Property property : getPropertyList()){
				if(property.name.toLowerCase().endsWith("name")) viewProp = property.name;
			}
		}
		if(viewProp.equals("id")){
			for(Property property : getPropertyList()){
				if(property.name.toLowerCase().contains("name")) viewProp = property.name;
			}
		}
		return viewProp;
	}
	
	/**
	 * 获取展示的属性（大写）
	 */
	public String getViewPropUp(){
		String viewProp = getViewProp();
		return StringUtil.toUpper4FirstWord(viewProp);
	}
	
	/**
	 * 获取简洁的表名
	 * @autor yxl
	 */
	public String getSimpleCmt(){
		String cmt = tableCmt;
		
		//不需要的字符串
		String []badStr = {"(", "（"};
		
		for(String bs : badStr){
			if(cmt.indexOf(bs) != -1) cmt = cmt.substring(0, cmt.indexOf(bs));
		}
		
		return cmt;
	}
	
	/**
	 * 表名，表注释
	 * @param tableName 表名
	 * @param tableCmt 表注释
	 * @param packageName 包名
	 */
	public Entry(String tableName, String tableCmt, String packageName){
		this.tableName = tableName;
		this.tableCmt = tableCmt;
		this.packageName = packageName;
		
		this.name = this.fmtTbName(this.tableName);
	}
	
	/**
	 * 添加属性
	 * @param name 属性名称
	 * @param type 属性类型
	 * @param comment 注释
	 * @param notnull 不允许为空
	 * @autor yxl
	 */
	public void addProperty(String name, String type, String pSize, String comment, boolean notnull){
		Property  property = new Property(name, type, pSize, comment, notnull, this);
		propertyList.add(property);
	}
	
	/**
	 * 格式化表名，如 customer_need --> CustomerNeed
	 * @param tableStr
	 * @autor yxl
	 */
	public String fmtTbName(String tableStr){
		tableStr = StringUtil.toUpperBh(tableStr, "_", 1);
		tableStr = StringUtil.toUpper4FirstWord(tableStr);
		
		return tableStr;
	}
	
	/**
	 * 得到小写的名称
	 * @autor yxl
	 */
	public String getLowerName(){
		return StringUtil.toLower4FirstWord(this.name);
	}
	
	/**
	 * 根据附属类查找属性
	 * @return
	 */
	public Property findPropByAffiliated(Entry propEntry){
		for(Property property : getPropertyList()){
			if(property.type.equals(propEntry.name)) return property;
		}
		return null;
	}
}
