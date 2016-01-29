package com.yxl.util.maven;

import java.io.IOException;
import java.util.List;

import com.yxl.util.file.FileInfo;
import com.yxl.util.file.FileUtil;

/**
 * maven pom 工具类
 * @author yxl    
 * @version 1.0  
 * @created 2015-8-13 上午12:22:25
 */
public class PomUtil {
	
	/**
	 * 生成本地jar的xml信息
	 * @param libDir jar的lib目录
	 * @param rootProjectName 根项目名称
	 * @throws IOException 
	 */
	public static void createLocalJarXml(String libDir, String rootProjectName) throws IOException{
		StringBuffer sb = new StringBuffer("\t<dependencies>\n");
		List<FileInfo> fileList = FileUtil.getFileList(libDir);
		for(FileInfo fInfo : fileList){
			if(!fInfo.getName().endsWith(".jar")) continue;
			String groupId = fInfo.getName();
			groupId = groupId.replaceAll(".jar", "").replaceAll("[-][a-zA-Z-0-9.]*", "");
			String artifactId = fInfo.getName();
			String version = fInfo.getName();
			version = version.replaceAll("[a-zA-Z-\\s]", "");
			while(version.endsWith(".")) version = version.substring(0, version.length()-1);
			if(version.length()<1) version="1.0";
			sb.append("\t\t<dependency>\n");
			sb.append("\t\t\t<groupId>"+ groupId +"</groupId>\n");
			sb.append("\t\t\t<artifactId>"+ artifactId +"</artifactId>\n");
			sb.append("\t\t\t<version>"+ version +"</version>\n");
			sb.append("\t\t\t<scope>system</scope>\n");
			sb.append("\t\t\t<systemPath>${basedir}/../"+ rootProjectName +"/lib/"+ fInfo.getName() +"</systemPath>\n");
			sb.append("\t\t</dependency>\n");
		}
		sb.append("\t</dependencies>\n");
		System.out.println(sb);
		FileUtil.writeToFile(libDir+"/localJarXml.txt", sb.toString());
	}

	/**  
	 * 描述
	 * @param args  
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		//PomUtil.createLocalJarXml("F:/myeclipe10workspace/yxlweb-root/lib", "yxlweb-root");
		PomUtil.createLocalJarXml("F:/myeclipe10workspace/yxlweb-util/lib", "yxlweb-util");
	}

}
