package com.yxlisv.util.codebuild.entry;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.persistence.FetchType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.yxlisv.util.file.FileUtil;

/**
 * OneToMany关系生成器
 * @author john Local
 */
public class OneToManyBuilder {

	/** 存储实体类的OneToMany关系---》{类名：oneToMany代码,...} */
	private Map entryMap = new HashMap();
	//是否开启hibernate annotaion
	public boolean hibernateLazy = false;
	
	/**
	 * 添加OneToMany关系
	 * @param parentName 父类名称
	 * @param entryName 本类的名称
	 * @param comment
	 * @autor yxl
	 */
	public void put(String parentName, String entryName, String comment){
		//构造简单的实体类
		Entry pEntry = new Entry(parentName, "", "");
		Entry entry = new Entry(entryName, comment, "");
		
		
		String oneToManyStr = "";
		if(entryMap.get(parentName) != null) oneToManyStr = entryMap.get(parentName).toString();
		
		if(oneToManyStr.equals("")) oneToManyStr = "\n\n\n\n\n\n\t//一对多级联关系\n";
		oneToManyStr += "\n\n\t/** "+ entry.getSimpleCmt() +"列表，级联删除 */\n";
		oneToManyStr += "\tprivate List<"+ entry.name +"> "+ entry.getLowerName() +"List;\n\n";
		oneToManyStr += "\t/** 获取"+ entry.getSimpleCmt() +"列表 */\n";
		String lazyProp = "";
		if(hibernateLazy) lazyProp = ", fetch=FetchType.LAZY";
		oneToManyStr += "\t@OneToMany(cascade={javax.persistence.CascadeType.REMOVE}, fetch=FetchType.LAZY, mappedBy=\""+ pEntry.getLowerName() +"\")\n";
		oneToManyStr += "\t@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)\n";
		oneToManyStr += "\tpublic List<"+ entry.name +"> get"+ entry.name +"List() {\n";
		oneToManyStr += "\t\treturn "+ entry.getLowerName() +"List;\n";
		oneToManyStr += "\t}\n\n";
		oneToManyStr += "\t/** 设置"+ entry.getSimpleCmt() +"列表 */\n";
		oneToManyStr += "\tpublic void set"+ entry.name +"List(List<"+ entry.name +"> "+ entry.getLowerName() +"List) {\n";
		oneToManyStr += "\t\tthis."+ entry.getLowerName() +"List = "+ entry.getLowerName() +"List;\n";
		oneToManyStr += "\t}\n";
		
		entryMap.put(parentName, oneToManyStr);
	}
	
	
	/**
	 * 生成代码
	 * @param path 实体类文件夹路径
	 */
	public void build(String path){
		for(Iterator it=entryMap.entrySet().iterator(); it.hasNext();){
			Map.Entry mapEntry = (Map.Entry) it.next();
			String fileName = mapEntry.getKey() + ".java";
			String oneToManyStr = mapEntry.getValue().toString();
			
			//读取文件内容
			String fileContent = "";
			try {
				fileContent = FileUtil.read(path + fileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(fileContent == null){ 
				System.err.println("文件" + (path + fileName) + "内容为空！  -->  ");
				System.out.println(oneToManyStr);
				continue;
			}
			//删除掉最后一个大括号"}"
			fileContent = fileContent.substring(0, fileContent.length()-2);
			fileContent += oneToManyStr;
			fileContent += "\n}";
			try {
				FileUtil.write(path, fileName, fileContent);
			} catch (IOException e) {
				e.printStackTrace();
			}
			//FileUtil.appendToTail(path + fileName, oneToManyStr);
		}
	}
}
