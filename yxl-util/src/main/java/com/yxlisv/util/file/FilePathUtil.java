/**
 * 文件名： FileUtil.java 描述： 文件常用操作类 修改人： 杨雪令 修改时间： 2010-5-27 修改内容：创建类
 * 
 * @author: 杨雪令
 * @date: 2010-5-27
 * @version V1.0
 */

package com.yxlisv.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 描述：文件路径工具类
 * 
 * @author: 杨雪令
 * @version 1.0
 */
public class FilePathUtil {

	/**
	 * <p>Description:
	 * <p>Copyright　深圳太极软件公司
	 * @param subStr 截取路径的字符串
	 * @author  杨雪令
	 */
	public static String getClassUrl(String subStr) {
		String path = FilePathUtil.class.getResource("/").toString();
		if (path.startsWith("file:")) path = path.replaceFirst("file:", "");
		path = path.lastIndexOf("FileUtil.class") != -1 ? path.substring(0, path.lastIndexOf("FileUtil.class")) : path;
		if (subStr != null)
			path = path.indexOf(subStr) != -1 ? path.substring(0, path.indexOf(subStr)) + subStr + "/" : path;
		path = path.replaceAll("%20", " ");

		if (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1) {// windows
			if(path.startsWith("/")) path = path.substring(1);
		}
		return path;
	}
	
	
	/**
	 * <p>Description:获取class的路径
	 * <p>Copyright　深圳太极软件公司
	 * @param dir 截取到什么目录
	 * @author  杨雪令
	 */
	public static String getClassUrl(Object o, String dir) {
		String className = o.getClass().getName();
		if (className.lastIndexOf(".") != -1)
			className = className.substring(className.lastIndexOf(".") + 1);
		System.out.println(className);
		String path = o.getClass().getResource(className + ".class").toString();
		if (path.indexOf("file:") != -1)
			path = path.substring(path.indexOf("file:") + 6);
		path = path.lastIndexOf(className + ".class") != -1 ? path.substring(0, path.lastIndexOf(className + ".class")) : path;
		if (dir != null)
			path = path.indexOf("WEB-INF") != -1 ? path.substring(0, path.indexOf("WEB-INF")) + "WEB-INF/" : path;
		path = path.replaceAll("%20", " ");

		return path;
	}
	
	/**
	 * 获取web项目root目录
	 * @autor yxl
	 * 2013-11-04
	 */
	public static String getWebRoot(){
		String path = FilePathUtil.getClassUrl("WEB-INF").replace("WEB-INF", "");
		while(path.endsWith("/")) path = path.substring(0, path.length()-1);
		
		return path;
	}
	
	
	/**
	 * 获取文件后缀
	 * @param URI 文件路径
	 * @autor yxl
	 * 2013-8-7
	 */
	public static String getSuffix(String URI){
		String type = "";
		if(URI.indexOf(".") != -1) type = URI.substring(URI.lastIndexOf(".")+1).toLowerCase();
		return type;
	}

	
	/**
	 * 得到文件目录
	 * @param filePath 文件路径
	 * @autor yxl
	 */
	public static String getFileDir(String filePath) {

		if (filePath.indexOf("/") != -1)
			filePath = filePath.substring(0, filePath.lastIndexOf("/"));
		return filePath;
	}
	
	/**
	 * 创建存放文件的文件夹
	 * @param filePath 文件路径
	 * @return
	 */
	public static void mkFileDirs(String filePath){
		File fileDir = new File(FilePathUtil.getFileDir(filePath));
		if(!fileDir.exists()) fileDir.mkdirs();
	}

	/**
	 * 得到文件名称
	 * @param filePath 文件路径
	 * @autor yxl
	 */
	public static String getFileName(String filePath) {

		if (filePath.indexOf("/") != -1)
			filePath = filePath.substring(filePath.lastIndexOf("/"));
		if (filePath.indexOf(".") != -1)
			filePath = filePath.substring(0, filePath.indexOf("."));
		return filePath;
	}

	/**
	 * 得到文件名称
	 * @param basePath 基本路径
	 * @param folder 文件夹
	 * @autor yxl
	 */
	public static List<FileInfo> getFileList(String basePath, String folder) {
		List<FileInfo> listFileInfo = new ArrayList();
		if (folder != null) {
			File fileFolder = new File(basePath + "/" + folder);
			if (fileFolder.exists() && fileFolder.isDirectory()) {// 进入文件夹
				File[] listFiles = fileFolder.listFiles();
				for (File file : listFiles) {
					FileInfo fileInfo = new FileInfo(file);
					fileInfo.setPath("/" + folder + "/" + fileInfo.getName());

					listFileInfo.add(fileInfo);
				}
			}
		}

		return listFileInfo;
	}

	// 表的属性（字段）正则表达式
	private static Pattern tbpPt = Pattern
			.compile("(\\s*[`']([a-zA-Z0-9_`]+)[`']\\s+([a-zA-Z]+)\\u0028?([0-9]*)\\u0029?[a-zA-Z\\s_]*(comment\\s+'([^']*)')?\\s*,)" // 筛选属性
			);

	/**
	 * 得到文件名称
	 * @param folder 文件夹
	 * @param patternStr 正则表达式，匹配文件名
	 * @param fileList file list 集合
	 * @autor yxl
	 */
	public static void findFile(String folder, String patternStr, List<File> fileList) {
		Pattern pattern = null;// 正则表达式
		if (patternStr != null)
			pattern = Pattern.compile(patternStr);
		Matcher matcher;
		if (folder != null) {
			File fileFolder = new File(folder);
			if (fileFolder.exists() && fileFolder.isDirectory()) {// 进入文件夹
				File[] listFiles = fileFolder.listFiles();
				for (File file : listFiles) {
					if (file.isFile()) {
						// 匹配文件名
						if (pattern != null) {
							matcher = pattern.matcher(file.getName());
							if (matcher.find()) {
								fileList.add(file);
							}
						} else
							fileList.add(file);
					} else if (file.isDirectory()) {
						FilePathUtil.findFile(file.getAbsolutePath(), patternStr, fileList);
					}
				}
			}
		}
	}

	/**
	 * 得到文件名称
	 * @param folder 文件夹
	 * @param patternStr 正则表达式，匹配文件名
	 * @param fileList file list 集合
	 * @autor yxl
	 */
	public static List<File> findFile(String folder, String patternStr) {
		List<File> fileList = new ArrayList();
		FilePathUtil.findFile(folder, patternStr, fileList);
		return fileList;
	}


	public static void main(String[] args) throws Exception {
	}
}
