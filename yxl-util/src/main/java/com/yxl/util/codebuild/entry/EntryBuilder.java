package com.yxl.util.codebuild.entry;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.yxl.util.codebuild.CodeBuilder;
import com.yxl.util.codebuild.Constant;
import com.yxl.util.file.FileUtil;
import com.yxl.util.string.StringUtil;

/**
 * 实体类生成器
 * @author john Local
 */
public class EntryBuilder extends CodeBuilder{
	
	//是否开启hibernate annotaion
	public boolean hibernateAnnotation = false;
	//是否开启hibernate annotaion
	public boolean hibernateLazy = false;
	//实体
	private Entry entry;
	//OneToMany关系生成器
	public static OneToManyBuilder oneToManyBuilder = new OneToManyBuilder();
	//i18n
	public static StringBuffer i18nStr = new StringBuffer();
	
	/**
	 * 构造实体生成器
	 * @param packageName 包名
	 * @param baseDir	根目录
	 * @param entry	实体
	 */
	public EntryBuilder(String baseDir, Entry entry){
		this.packageName = entry.packageName;
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
		sb.append("package "+ packageName +";");
		sb.append("\n\n");
		
		//import
		sb.append("import java.io.Serializable;\n");
		sb.append("import java.util.Date;\n");
		sb.append("import java.util.List;\n\n");
		if(hibernateAnnotation){
			sb.append("import javax.persistence.Column;\n");
			sb.append("import javax.persistence.Entity;\n");
			sb.append("import javax.persistence.FetchType;\n");
			sb.append("import javax.persistence.GeneratedValue;\n");
			sb.append("import javax.persistence.Id;\n");
			sb.append("import javax.persistence.OneToMany;\n");
			sb.append("import javax.persistence.Table;\n");
			sb.append("import javax.validation.constraints.NotNull;\n");
			sb.append("import javax.validation.constraints.Max;\n");
			sb.append("import javax.validation.constraints.Min;\n");
			sb.append("import javax.validation.constraints.Size;\n\n");
			sb.append("import org.hibernate.annotations.Cache;\n");
			sb.append("import org.hibernate.annotations.CacheConcurrencyStrategy;\n");
			sb.append("import org.hibernate.annotations.GenericGenerator;\n\n");
		}
		
		//类注释
		sb.append("/**\n");
		sb.append(" * "+ entry.tableCmt +"\n");
		sb.append(" * @author "+ Constant.author +"\n");
		sb.append(" * @version "+ Constant.version +"\n");
		sb.append(" */\n");
		if(hibernateAnnotation) {
			sb.append("@Entity\n");
			sb.append("@Table(name = \""+ entry.tableName +"\")\n");
			sb.append("@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)\n");
		}
		sb.append("public class "+ entry.name +" implements Serializable {\n");
		sb.append("\n");
			
		//生成属性
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext();){
			Property property = (Property) it.next();
			if(property.comment != null) sb.append("\t/** "+ property.comment +" */\n");
			if(property.notnull && !property.name.equals("id")) sb.append("\t@NotNull(message=\""+ property.getSimpleCmt() +"不能为空\")\n");
			if(property.type.equals("String") && !property.name.equals("id")) sb.append("\t@Size(max="+ property.size +", message=\""+ property.getSimpleCmt() +"不能超过"+ property.size +"个字符\")\n");
			if(property.tType.equals("tinyint")) sb.append("\t@Max(value=127, message=\""+ property.getSimpleCmt() +"不能超过127\")\n\t@Min(value=-128, message=\""+ property.getSimpleCmt() +"不能小于-128\")\n");
			sb.append("\tprivate "+ property.type +" "+ property.name +";\n\n");
			
			//in8n
			if(property.isI18n){
				i18nStr.append("\n#" + entry.name + "\n");
				for(Iterator itI18n = property.in8nMap.entrySet().iterator(); itI18n.hasNext();){
					Map.Entry entryMap = (Map.Entry) itI18n.next();
					String key = entry.name + "." + property.name + "_" + entryMap.getKey();
					String val = entryMap.getValue().toString();
					i18nStr.append(key + "=" + StringUtil.toUnicode(val) + "\n");
				}
			}
		}
		
		sb.append("\n\n");
		
		//是否引入了 ManyToOne
		boolean isImportManyToOne = false;
		//生成get,set方法
		for(Iterator it=entry.getPropertyList().iterator(); it.hasNext();){
			Property property = (Property) it.next();
			
			//get 方法
			if(property.comment != null) sb.append("\t/** 获取“"+ property.comment +"” */\n");
			if(hibernateAnnotation) {
				if(property.name.equals("id")) {
					if(property.type.equals("String")){
						sb.append("\t@Id\n\t@GenericGenerator(name=\"idGenerator\", strategy=\"uuid\")\n\t@GeneratedValue(generator=\"idGenerator\")\n");
					} else {
						sb.append("\t@Id\n\t@GeneratedValue\n");
					}
				}
				
				if(property.isClass()){
					String lazyProp = "";
					if(hibernateLazy) lazyProp = ", fetch=FetchType.LAZY";
					if(property.isMainClass()) sb.append("\t@ManyToOne(cascade={javax.persistence.CascadeType.ALL}"+ lazyProp +", optional=true)\n");
					else sb.append("\t@ManyToOne(cascade={javax.persistence.CascadeType.REFRESH}"+ lazyProp +", optional=true)\n");
					if(!hibernateLazy){
						sb.append("\t@Fetch(FetchMode.JOIN)\n");
					}
					sb.append("\t@JoinColumn(name=\""+ property.tName +"\")\n");
					
					if(isImportManyToOne == false){
						sb.insert(sb.indexOf("import javax.persistence.Table;"), "import javax.persistence.JoinColumn;\nimport javax.persistence.ManyToOne;\nimport org.hibernate.annotations.Fetch;\nimport org.hibernate.annotations.FetchMode;\n\n");
						isImportManyToOne = true;
					}
					//添加OneToMany关系
					if(!property.isBigString()) {
						oneToManyBuilder.hibernateLazy = hibernateLazy;
						oneToManyBuilder.put(property.type, entry.name, entry.getSimpleCmt());
					}
				} else if(!property.name.equals(property.tName)) 
					sb.append("\t@Column(name=\""+ property.tName +"\")\n");
			}
			sb.append("\tpublic "+ property.type +" get"+ StringUtil.toUpper4FirstWord(property.name) +"() {\n");
			sb.append("\t\treturn "+ property.name +";\n");
			sb.append("\t}\n\n");
			
			//set 方法
			if(property.comment != null) sb.append("\t/** 设置“"+ property.comment +"” */\n");
			sb.append("\tpublic void set"+ StringUtil.toUpper4FirstWord(property.name) +"("+ property.type +" "+ property.name +") {\n");
			sb.append("\t\tthis."+ property.name +" = "+ property.name +";\n");
			sb.append("\t}\n\n\n");
		}
		
		//类结束
		sb.append("}");
		
		//System.out.println(sb.toString());
		try {
			FileUtil.write(fileDir, entry.name + ".java", sb.toString());
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
	public static void buildI18n(String baseDir){
		
		try {
			FileUtil.write(baseDir, "dataDictionary_zh_CN.properties", i18nStr.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}