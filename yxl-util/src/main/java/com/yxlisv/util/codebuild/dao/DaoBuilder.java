package com.yxlisv.util.codebuild.dao;

import java.io.IOException;

import com.yxlisv.util.codebuild.CodeBuilder;
import com.yxlisv.util.codebuild.Constant;
import com.yxlisv.util.codebuild.entry.Entry;
import com.yxlisv.util.file.FileUtil;

/**
 * dao层生成器
 * @author john Local
 */
public class DaoBuilder extends CodeBuilder{
	
	//实体
	private Entry entry;
	
	/**
	 * 构造实体生成器
	 * @param packageName 包名
	 * @param baseDir	根目录
	 * @param entry	实体
	 */
	public DaoBuilder(String parentPackageName, String packageName, String baseDir, Entry entry){
		this.parentPackageName = parentPackageName;
		this.packageName = packageName;
		this.setFileDir(baseDir);
		this.entry = entry;
	}
	
	/**
	 * 生成文件
	 * @param fileDir 文件目录
	 * 
	 * @autor yxl
	 */
	@Override
	public void build(){
		
		StringBuffer sb = new StringBuffer();
		
		//包
		sb.append("package "+ packageName +";\n\n");
		
		//import
		sb.append("import org.springframework.stereotype.Repository;\n\n");
		
		sb.append("import "+ parentPackageName +".BaseDao;\n");
		sb.append("import "+ entry.packageName +"."+ entry.name +";\n\n");
		
		//类注释
		sb.append("/**\n");
		sb.append(" * "+ entry.tableCmt +" 的dao层\n");
		sb.append(" * @author "+ Constant.author +"\n");
		sb.append(" * @version "+ Constant.version +"\n");
		sb.append(" */\n");
		sb.append("@Repository\n");
		sb.append("public class "+ entry.name +"Dao extends BaseDao<"+ entry.name +">{\n\n");
					
		
		//类结束
		sb.append("}");
		
		//System.out.println(sb.toString());
		try {
			FileUtil.write(fileDir, entry.name + "Dao.java", sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 生成父类
	 * @param fileDir 文件目录
	 * 
	 * @autor yxl
	 */
	public static void buildParentClass(String packageName, String baseDir){
		
		StringBuffer sb = new StringBuffer();
		
		//包
		sb.append("package "+ packageName +";\n\n");
		
		//import
		sb.append("import com.yxlisv.dao.AbstractBaseEntryHibernateDAO;\n");
		
		//类注释
		sb.append("/**\n");
		sb.append(" * 项目所有Dao层的父类\n");
		sb.append(" * @author "+ Constant.author +"\n");
		sb.append(" * @version "+ Constant.version +"\n");
		sb.append(" */\n");
		sb.append("public class BaseDao<Entry> extends AbstractBaseEntryHibernateDAO<Entry>{\n");
					
		
		//类结束
		sb.append("}");
		
		try {
			FileUtil.write(CodeBuilder.getFileDir(packageName, baseDir), "BaseDao.java", sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}