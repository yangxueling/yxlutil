package com.yxl.util.file;

import java.util.HashMap;
import java.util.Map;

/**
 * 文件类别工具
 * @author yxl
 */
public class FileTypeUitl {
	
	//文件类别映射
	private static Map<String, Integer> fileType = new HashMap<String, Integer>();
	
	/**
	 * 初始化
	 * @autor yxl
	 * 2013-11-4
	 */
	protected static void init(){
		//类别1，文档
		fileType.put("doc", 1);
		fileType.put("xls", 1);
		fileType.put("ppt", 1);
		fileType.put("pdf", 1);
		fileType.put("docx", 1);
		fileType.put("xlsx", 1);
		fileType.put("pptx", 1);
		fileType.put("txt", 1);
		//类别2，视频
		fileType.put("flv", 2);
		fileType.put("wmv", 2);
		fileType.put("avi", 2);
		fileType.put("mpeg", 2);
		fileType.put("rm", 2);
		fileType.put("rmvb", 2);
		//类别3，图片
		fileType.put("png", 3);
		fileType.put("jpg", 3);
		fileType.put("jpeg", 3);
		fileType.put("gif", 3);
		fileType.put("ico", 3);
		fileType.put("bmp", 3);
	}
	
	/**
	 * 获取文件类别
	 * @param fileName 文件名称，包含后缀
	 * @return 1，文档; 2，视频; 3，图片; 4，未知
	 * @autor yxl
	 * 2013-11-4
	 */
	public static int getFileType(String fileName){
		if(fileType.size()<1) init();
		String fileTypeStr = FilePathUtil.getSuffix(fileName).substring(1);
		if(fileType.get(fileTypeStr) == null) return 4;
		return fileType.get(fileTypeStr);
	}
	
	/**
	 * 获取文件类别
	 * @param fileName 文件名称，包含后缀
	 * @return doc,rmvb.....
	 * @autor yxl
	 * 2013-11-4
	 */
	public static String getFileTypeStr(String fileName){
		return FilePathUtil.getSuffix(fileName).substring(1);
	}
}
