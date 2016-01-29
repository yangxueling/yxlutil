package com.yxl.ynt;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxl.util.file.FileUtil;
import com.yxl.util.string.StringUtil;

public class ServiceXmlBuild {
	
	/**
	 * 打印service层的xml代码
	 * @param dirPath 文件夹
	 * @param packageStr 包
	 * @throws IOException 
	 * @autor yxl
	 * 2014-1-20
	 */
	public static void printServiceXmlCode(String dirPath, String packageStr) throws IOException{
		File fileDir = new File(dirPath);
		File[] listFiles = fileDir.listFiles();
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		sb.append("<!DOCTYPE beans PUBLIC \"-//SPRING//DTD BEAN//EN\" \"http://www.springframework.org/dtd/spring-beans.dtd\">\n");
		sb.append("<beans default-autowire=\"byName\">\n\n");
		for (File file : listFiles) {
			String fileName = file.getName().replace(".java", "");
			String id = StringUtil.toLower4FirstWord(fileName);
			
			sb.append("\t<bean id=\""+ id +"\" parent=\"baseTxService\">\n");
			sb.append("\t\t<property name=\"target\">\n");
			sb.append("\t\t\t<bean class=\""+ packageStr +"."+ fileName +"\"/>\n");
			sb.append("\t\t</property>\n");
			sb.append("\t</bean>\n");
		}
		sb.append("</beans>\n");
		System.out.println(sb.toString());
		FileUtil.write(dirPath + "/service.xml", sb.toString());
	}
	
	//@Resource
	//private BannerDao bannerDao;
	//service层变量匹配表达式
	private static Pattern serviceVarPattern = Pattern.compile("\\s*@Resource\\s+private\\s+([A-Z]{1}[a-z]+[a-zA-Z]+)\\s+[a-z]{1}[a-zA-Z]+;");
	private static Matcher serviceVarMatcher;
	
	/**
	 * 把annotation的service层改为xml方式
	 * @param dirPath 文件夹
	 * @throws IOException 
	 * @autor yxl
	 * 2014-1-20
	 */
	public static void serviceToXml(String dirPath) throws IOException{
		File fileDir = new File(dirPath);
		File[] listFiles = fileDir.listFiles();
		for (File file : listFiles) {
			String content = FileUtil.read(file.getAbsolutePath());
			serviceVarMatcher = serviceVarPattern.matcher(content);
			while(serviceVarMatcher.find()){
				String varClassName = serviceVarMatcher.group(1);
				String varName = StringUtil.toLower4FirstWord(varClassName);
				String varStr = serviceVarMatcher.group(0);
				String oldVarStr = varStr;
				varStr += "\n\tpublic void set"+ varClassName +"("+ varClassName +" "+ varName +") {\n";
				varStr += "\t\tthis."+ varName +" = "+ varName +";\n";
				varStr += "\t}\n";
				//System.out.println(varStr);
				content = content.replace(oldVarStr, varStr);
			}
			
			content = content.replaceAll("@Service", "//@Service");
			content = content.replaceAll("@Transactional", "//@Transactional");
			content = content.replaceAll("@Resource", "//@Resource");
			FileUtil.write(file.getAbsolutePath(), content);
		}
		
		System.out.println("Service 层文件修改成功...");
	}

	/**
	 * 测试
	 * @throws IOException 
	 * @autor yxl
	 * 2014-1-20
	 */
	public static void main(String[] args) throws IOException {
		String dirPath = "E:/workspace/spring3hibernate_google/test_demo/com/yxl/test/service/simple";
		String packageStr = "com.yxl.test.service.simple";
		ServiceXmlBuild.serviceToXml(dirPath);
		ServiceXmlBuild.printServiceXmlCode(dirPath, packageStr);
	}
}