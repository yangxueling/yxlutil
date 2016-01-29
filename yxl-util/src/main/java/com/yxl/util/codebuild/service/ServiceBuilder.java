package com.yxl.util.codebuild.service;

import java.io.IOException;
import java.util.Iterator;

import com.yxl.util.codebuild.CodeBuilder;
import com.yxl.util.codebuild.Constant;
import com.yxl.util.codebuild.entry.Entry;
import com.yxl.util.codebuild.entry.EntryData;
import com.yxl.util.codebuild.entry.Property;
import com.yxl.util.date.DateUtil;
import com.yxl.util.file.FileUtil;

/**
 * service层生成器
 * @author john Local
 */
public class ServiceBuilder extends CodeBuilder{
	
	//实体
	private Entry entry;
	private String daoPackageName;
	
	/**
	 * 构造实体生成器
	 * @param packageName 包名
	 * @param daoPackageName dao层的包名
	 * @param baseDir	根目录
	 * @param entry	实体
	 */
	public ServiceBuilder(String parentPackageName, String packageName, String daoPackageName, String baseDir, Entry entry){
		this.parentPackageName = parentPackageName;
		this.packageName = packageName;
		this.daoPackageName = daoPackageName;
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
		sb.append("import java.util.List;\n");
		sb.append("import java.util.Map;\n\n");
		sb.append("import javax.annotation.Resource;\n");
		sb.append("import org.springframework.stereotype.Service;\n");
		sb.append("import org.springframework.transaction.annotation.Propagation;\n");
		sb.append("import org.springframework.transaction.annotation.Transactional;\n\n");
		sb.append("import "+ daoPackageName +"."+ entry.name +"Dao;\n");
		sb.append("import "+ entry.packageName +"."+ entry.name +";\n");
		sb.append("import "+ this.parentPackageName +".BaseService;\n");
		//sb.append("import "+ packageName +".support."+ entry.name +"ServiceSupport;\n");
		sb.append("import com.yxl.service.SearchRequirement;\n");
		sb.append("import com.yxl.dao.annotation.DaoUtil;\n");
		sb.append("import com.yxl.util.date.DateUtil;\n");
		sb.append("import com.yxl.util.Page;\n\n");
		
		//类注释
		sb.append("/**\n");
		sb.append(" * "+ entry.tableCmt +"Service\n");
		sb.append(" * @author "+ Constant.author +"\n");
		sb.append(" * @version "+ Constant.version +"\n");
		sb.append(" */\n");
		sb.append("@Service\n");
		sb.append("public class "+ entry.name +"Service extends BaseService{\n\n");
		
		//支援类
		//sb.append("\t/** “"+ entry.name +"Service”的支援类 (可以引入其他service) */\n");
		//sb.append("\t@Resource\n");
		//sb.append("\tprivate "+ entry.name +"ServiceSupport "+ entry.getLowerName() +"ServiceSupport;\n\n");
		
		//实体类字段
		sb.append("\t/** 模糊查询可匹配字段 */\n");
		sb.append("\tprivate String []srFields = {");
		int i=0;
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.isClass()){
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(i>0) sb.append(",");
					sb.append("\""+ property.name +"."+ pp.name +"\"");
				}
			} else {
				if(i>0) sb.append(",");
				sb.append("\""+ property.name +"\"");
			}
		}
		sb.append("};\n\n");
		
		//注入dao层
		sb.append("\t/** "+ entry.tableCmt +"Dao */\n");
		sb.append("\t@Resource\n");
		sb.append("\tprivate "+ entry.name +"Dao "+ entry.getLowerName() +"Dao;\n\n");
		
		//根据id查询 的静态方法
		sb.append("\t/**\n");
		sb.append("\t * 根据id查询"+ entry.tableCmt +"\n");
		sb.append("\t * @param id "+ entry.tableCmt +"ID\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic static "+ entry.name +" get("+ entry.getIdProperty().type +" id) {\n");
		sb.append("\t\treturn ("+ entry.name +") DaoUtil.getObj(id, "+ entry.name +".class);\n");
		sb.append("\t}\n\n");
		
		//根据Map中的条件查询集合
		sb.append("\t/**\n");
		sb.append("\t * 根据Map中的条件查询"+ entry.tableCmt +"集合\n");
		sb.append("\t * @param srMap 查询条件Map\n");
		sb.append("\t * @return "+ entry.tableCmt +"List\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic List<"+ entry.name +"> getList(Map srMap) {\n");
		//时间查询
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.isTime()) {
				sb.append("\t\tlong "+ property.name +"1 = DateUtil.toLong(srMap.get(\""+ property.name +"1\"));\n");
				sb.append("\t\tlong "+ property.name +"2 = DateUtil.toLong(srMap.get(\""+ property.name +"2\"));\n");
			}
			if(property.isMainClass()){
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.isTime()) {
						sb.append("\t\tlong "+ pp.name +"1 = DateUtil.toLong(srMap.get(\""+ property.name + "." + pp.name +"1\"));\n");
						sb.append("\t\tlong "+ pp.name +"2 = DateUtil.toLong(srMap.get(\""+ property.name + "." + pp.name +"2\"));\n");
					}
				}
			}
		}
		sb.append("\t\tSearchRequirement sr = this.getSearchRequirement(srFields, srMap);//模糊查询工具\n");
		//时间查询
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.isTime()) {
				sb.append("\t\tif("+ property.name +"1!=0) sr.addGreater(\""+ property.name +"\", "+ property.name +"1-1);\n");
				sb.append("\t\tif("+ property.name +"2!=0) sr.addLess(\""+ property.name +"\", "+ property.name +"2+1);\n");
			}
			if(property.isMainClass()){
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.isTime()) {
						sb.append("\t\tif("+ pp.name +"1!=0) sr.addGreater(\""+ property.name + "." + pp.name +"\", "+ pp.name +"1-1);\n");
						sb.append("\t\tif("+ pp.name +"2!=0) sr.addLess(\""+ property.name + "." + pp.name +"\", "+ pp.name +"2+1);\n");
					}
				}
			}
		}
		sb.append("\t\t//排序\n");
		sb.append("\t\tsr.addOrder(new String[] {\"id\",\"desc\"});\n");
		sb.append("\t\treturn "+ entry.getLowerName() +"Dao.getList(sr);\n");
		sb.append("\t}\n\n");
		
		//查询所有
		sb.append("\t/**\n");
		sb.append("\t * 查询所有"+ entry.tableCmt +"\n");
		sb.append("\t * @return "+ entry.tableCmt +"List\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic List<"+ entry.name +"> getAll() {\n");
		sb.append("\t\treturn "+ entry.getLowerName() +"Dao.getAll();\n");
		sb.append("\t}\n\n");
		
		//根据Map中的条件分页查询
		sb.append("\t/**\n");
		sb.append("\t * 根据Map中的条件分页查询"+ entry.tableCmt +"\n");
		sb.append("\t * @param page 分页对象\n");
		sb.append("\t * @param srMap 查询条件Map\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic void pageSearch(Page page, Map srMap) {\n");
		//时间查询
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.isTime()) {
				sb.append("\t\tlong "+ property.name +"1 = DateUtil.toLong(srMap.get(\""+ property.name +"1\"));\n");
				sb.append("\t\tlong "+ property.name +"2 = DateUtil.toLong(srMap.get(\""+ property.name +"2\"));\n");
			}
			if(property.isMainClass()){
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.isTime()) {
						sb.append("\t\tlong "+ pp.name +"1 = DateUtil.toLong(srMap.get(\""+ property.name + "." + pp.name +"1\"));\n");
						sb.append("\t\tlong "+ pp.name +"2 = DateUtil.toLong(srMap.get(\""+ property.name + "." + pp.name +"2\"));\n");
					}
				}
			}
		}
		sb.append("\t\tSearchRequirement sr = this.getSearchRequirement(srFields, srMap);//模糊查询工具\n");
		//时间查询
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.isTime()) {
				sb.append("\t\tif("+ property.name +"1!=0) sr.addGreater(\""+ property.name +"\", "+ property.name +"1-1);\n");
				sb.append("\t\tif("+ property.name +"2!=0) sr.addLess(\""+ property.name +"\", "+ property.name +"2+1);\n");
			}
			if(property.isMainClass()){
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.isTime()) {
						sb.append("\t\tif("+ pp.name +"1!=0) sr.addGreater(\""+ property.name + "." + pp.name +"\", "+ pp.name +"1-1);\n");
						sb.append("\t\tif("+ pp.name +"2!=0) sr.addLess(\""+ property.name + "." + pp.name +"\", "+ pp.name +"2+1);\n");
					}
				}
			}
		}
		sb.append("\t\t//排序\n");
		sb.append("\t\tsr.addOrder(new String[] {\"id\",\"desc\"});\n");
		sb.append("\t\t"+ entry.getLowerName() +"Dao.pageSearch(page, sr);\n");
		sb.append("\t}\n\n");
		
		
		//根据属性名和属性值查询数据
		sb.append("\t/**\n");
		sb.append("\t * 根据属性名和属性值查询数据\n");
		sb.append("\t * @param name 属性名称(key)\n");
		sb.append("\t * @param value 属性值\n");
		sb.append("\t * @return 部门List\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic List<"+ entry.name +"> findBy(String name, Object value) {\n");
		sb.append("\t\treturn "+ entry.getLowerName() +"Dao.findBy(name, value);\n");
		sb.append("\t}\n\n");
		
		
		//根据属性名和属性值查询一条数据
		sb.append("\t/**\n");
		sb.append("\t * 根据属性名和属性值查询一条数据\n");
		sb.append("\t * @param name 属性名称(key)\n");
		sb.append("\t * @param value 属性值\n");
		sb.append("\t * @return 部门List\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\tpublic "+ entry.name +" findOne(String name, String value) {\n");
		sb.append("\t\treturn "+ entry.getLowerName() +"Dao.findOne(name, value);\n");
		sb.append("\t}\n\n");
		
		
		//添加方法
		sb.append("\t/**\n");
		sb.append("\t * 添加"+ entry.tableCmt +"\n");
		sb.append("\t * @param "+ entry.getLowerName() +" "+ entry.tableCmt +"\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\t@Transactional\n");
		sb.append("\tpublic void add("+ entry.name +" "+ entry.getLowerName() +") {\n");
		if(entry.getIdProperty()!=null){
			if(entry.getIdProperty().type.equals("String"))
				sb.append("\t\t"+ entry.getLowerName() +".setId(\"\");\n");
			else sb.append("\t\t"+ entry.getLowerName() +".setId(0);\n");
		}
		//父类集合查询 
		for(Property property : entry.getClassProperty()){
			if(!property.isMainClass())
				sb.append("\t\tif("+ entry.getLowerName() +".get"+ property.getClassName() +"()!=null) "+ entry.getLowerName() +".set"+ property.getClassName() +"("+ property.getClassName() +"Service.get("+ entry.getLowerName() +".get"+ property.getClassName() +"().getId()));\n");
		}
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.name.equals("createTime")) sb.append("\t\t"+ entry.getLowerName() +".setCreateTime(System.currentTimeMillis());\n");
			if(property.name.equals("updateTime")) sb.append("\t\t"+ entry.getLowerName() +".setUpdateTime(System.currentTimeMillis());\n");
			if(property.isMainClass()){//给附属表添加创建时间
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.name.equals("createTime")) sb.append("\t\tif("+ entry.getLowerName() +".get"+ propEntry.name +"()!=null) "+ entry.getLowerName() +".get"+ propEntry.name +"().setCreateTime(System.currentTimeMillis());\n");
					if(pp.name.equals("updateTime")) sb.append("\t\tif("+ entry.getLowerName() +".get"+ propEntry.name +"()!=null) "+ entry.getLowerName() +".get"+ propEntry.name +"().setUpdateTime(System.currentTimeMillis());\n");
				}
			}
		}
		sb.append("\t\t"+ entry.getLowerName() +"Dao.save("+ entry.getLowerName() +");\n");
		sb.append("\t}\n\n");
		
		//修改方法
		sb.append("\t/**\n");
		sb.append("\t * 修改"+ entry.tableCmt +"\n");
		sb.append("\t * @param "+ entry.getLowerName() +" "+ entry.tableCmt +"\n");
		sb.append("\t * @param obj 该对象用来计算用户是否有权限操作\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\t@Transactional\n");
		sb.append("\tpublic void update("+ entry.name +" "+ entry.getLowerName() +", Object obj) {\n");
		sb.append("\t\tif(!this.haveOperatingAuthority("+ entry.getLowerName() +".getId()+\"\", obj)) return;\n");
		//父类集合查询
		for(Property property : entry.getClassProperty()){
			if(!property.isMainClass())
				sb.append("\t\tif("+ entry.getLowerName() +".get"+ property.getClassName() +"()!=null) "+ entry.getLowerName() +".set"+ property.getClassName() +"("+ property.getClassName() +"Service.get("+ entry.getLowerName() +".get"+ property.getClassName() +"().getId()));\n");
		}
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext(); i++){
			Property property = (Property) it.next();
			if(property.name.equals("updateTime")) sb.append("\t\t"+ entry.getLowerName() +".setUpdateTime(System.currentTimeMillis());\n");
			if(property.isMainClass()){//给附属表添加创建时间
				Entry propEntry = EntryData.entryMap.get(property.type);
				if(propEntry==null) continue;
				for(Property pp : propEntry.getPropertyList()){
					if(pp.name.equals("updateTime")) sb.append("\t\tif("+ entry.getLowerName() +".get"+ propEntry.name +"()!=null) "+ entry.getLowerName() +".get"+ propEntry.name +"().setUpdateTime(System.currentTimeMillis());\n");
				}
			}
		}
		sb.append("\t\t"+ entry.getLowerName() +"Dao.update("+ entry.getLowerName() +");\n");
		sb.append("\t}\n\n");
		
		//删除
		sb.append("\t/**\n");
		sb.append("\t * 删除"+ entry.tableCmt +"\n");
		sb.append("\t * @param id "+ entry.tableCmt +"ID\n");
		sb.append("\t * @return 删除数量\n");
		sb.append("\t * @author "+ Constant.author +"\n");
		sb.append("\t * @version "+ Constant.version +"\n");
		sb.append("\t */\n");
		sb.append("\t@Override\n");
		sb.append("\tpublic int delete("+ entry.getIdProperty().type +" id) {\n");
		sb.append("\t\treturn "+ entry.getLowerName() +"Dao.delete("+ entry.getLowerName() +"Dao.get(id));\n");
		sb.append("\t}\n");
		
		//类结束
		sb.append("}");
		
		//System.out.println(sb.toString());
		try {
			FileUtil.write(fileDir, entry.name + "Service.java", sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
		
		//生成支援类
		//sb.setLength(0);
		//包
		//sb.append("package "+ packageName +".support;\n\n");
		
		//import
		//sb.append("import org.springframework.stereotype.Component;\n\n");
		
		//类注释
		//sb.append("/**\n");
		//sb.append(" * "+ entry.name +"Service 的支援类 (可以引入其他service)\n");
		//sb.append(" * @author "+ Constant.author +"\n");
		//sb.append(" */\n");
		//sb.append("@Component\n");
		//sb.append("public class "+ entry.name +"ServiceSupport {\n");
		//sb.append("}");
		//System.out.println(sb.toString());
		//FileUtil.write(fileDir + "support/", entry.name + "ServiceSupport.java", sb.toString());
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
		sb.append("import com.yxl.service.AbstractBaseService;\n");
		
		//类注释
		sb.append("/**\n");
		sb.append(" * 项目所有service的父类\n");
		sb.append(" * @author "+ Constant.author +"\n");
		sb.append(" * @version "+ Constant.version +"\n");
		sb.append(" */\n");
		sb.append("public class BaseService extends AbstractBaseService{\n");
		
		//类结束
		sb.append("}");
		
		try {
			FileUtil.write(CodeBuilder.getFileDir(packageName, baseDir), "BaseService.java", sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}