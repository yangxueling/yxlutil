package com.yxl.util.codebuild.control;

import com.yxl.util.string.StringUtil;
import com.yxl.util.web.URLUtil;

/**
 * control 层的包
 * @author john Local
 */
public class ControlPackage {

	/** 包名 */
	public String packageName;
	/** path 前缀 */
	public String pathQz = "";
	
	/**
	 * 获取class名称（admim/website  -->  Website）
	 * @param entryName 实体名称
	 * @autor yxl
	 */
	public String getClassName(String entryName){
		return entryName +"Control";
	}
	
	/**
	 * 获取Control的id（admim/website  -->  adminWebsite）
	 * @param  entryName 实体名称
	 * @autor yxl
	 */
	public String getControlId(String entryName){
		String pq = URLUtil.getStandardUrl(pathQz);
		if(pq.length()>0 && pq.endsWith("/")) pq = pq.substring(0, pq.length()-1);
		pq = StringUtil.toUpperBh(pq, "/", 1);
		return pq + entryName +"Control";
	}
	
	public static void main(String[] args) {
		ControlPackage controlPackage = new ControlPackage();
		controlPackage.pathQz = "admin";
		System.out.println(controlPackage.getClassName("Website"));
		System.out.println(controlPackage.getControlId("Website"));
	}
}
