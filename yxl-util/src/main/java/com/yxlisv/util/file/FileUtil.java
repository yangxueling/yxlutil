/**
 * 文件名： FileUtil.java 描述： 文件常用操作类 修改人： 杨雪令 修改时间： 2010-5-27 修改内容：创建类
 * 
 * @author: 杨雪令
 * @date: 2010-5-27
 * @version V1.0
 */

package com.yxlisv.util.file;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.web.URLUtil;

/**
 * 描述：文件常用操作类 类名：FileUtil
 * 
 * @author: 杨雪令
 * @version 1.0
 */
public class FileUtil {

	/** 
	 * 删除文件/目录 
	 * @param   filePath 文件路径 
	 */
	public static void delete(String filePath) {
		File baseFile = new File(filePath);
		if (baseFile.exists()) {
			if (baseFile.isDirectory()) {
				// 删除文件夹下的所有文件(包括子目录)
				File[] files = baseFile.listFiles();
				for (File file : files) {
					FileUtil.delete(file.getAbsolutePath());
				}
			}
			baseFile.delete();
		}
	}
	
	/** 
	 * 删除文件
	 * @param   filePath 文件路径 
	 */
	public static void deleteFile(String filePath) {
		File baseFile = new File(filePath);
		if (baseFile.exists()) {
			baseFile.delete();
		}
	}

	/**  
	 * 追加文件：使用FileWriter  
	 *   
	 * @param fileName  
	 * @param content  
	 * @throws IOException 
	 */
	public static void appendToTail(String fileName, String content) throws IOException {

		FileWriter writer = null;
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName, true);
			writer.write(content);
			writer.close();
		} finally {
			if(writer != null) writer.close();
		}
	}

	/**  
	 * 追加文件：使用FileWriter  
	 *   
	 * @param fileName  
	 * @param content  
	 * @throws IOException 
	 */
	public static void writeToFile(String fileName, String content) throws IOException {

		FileWriter writer = null;
		try {
			// 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
			writer = new FileWriter(fileName);
			writer.write(content);
			writer.close();
		} finally {
			if(writer != null) writer.close();
		}
	}

	/**  
	 * 写文件
	 *   
	 * @param filePath  文件路径
	 * @param content  文件内容
	 * @throws IOException 
	 */
	public static void write(String filePath, String content) throws IOException {

		filePath = URLUtil.getStandardUrl(filePath);

		// 构建目录
		if (filePath.lastIndexOf("/") != -1) {
			File _fileDir = new File(filePath.substring(0, filePath.lastIndexOf("/")));
			if (!_fileDir.exists())
				_fileDir.mkdirs();
		}

		OutputStreamWriter ops = null;
		try {
			ops = new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8");
			ops.write(content);
		} finally {
			if (ops != null) ops.close();
		}
	}

	/**  
	 * 写文件
	 *   
	 * @param fileDir  文件目录
	 * @param fileName 文件名称
	 * @param content  文件内容
	 * @throws IOException 
	 */
	public static void write(String fileDir, String fileName, String content) throws IOException {

		FileUtil.write(fileDir + "/" + fileName, content);
	}

	/**
	 * 描述：批量修改文件(文件名或者内容)
	 * 
	 * @author: 杨雪令
	 * @param dir
	 *            要被替换的目录
	 * @param oldStr
	 *            要被替换的字符串
	 * @param targetStr
	 *            要替换成的字符串
	 * @param area
	 *            替换范围，1为文件名，2为文件内容
	 * @version: 2010-5-27 上午09:32:54
	 * @throws IOException
	 */
	public String replaceAll(File file, String oldStr, String targetStr, int area) throws IOException {

		if (file.isDirectory()) {
			/** 得到文件夹下的所有文件 */
			File[] fileList = file.listFiles();
			/** 遍历所有文件 */
			for (int i = 0; i < fileList.length; i++) {
				File childFlie = fileList[i];
				if (childFlie.isDirectory()) {
					replaceAll(childFlie, oldStr, targetStr, area);
				} else if (area == 1) {
					rename(childFlie, oldStr, targetStr);
				} else if (area == 2) {
					replaceContent(childFlie, oldStr, targetStr).renameTo(childFlie);
				}
			}
			return "恭喜你，修改成功!";
		} else {
			return "目录路径不正确，请确认您输入的是一个目录！";
		}
	}

	/**
	 * 描述： 更改文件名
	 * 
	 * @author: 杨雪令
	 * @param file
	 *            要被更改的文件
	 * @param oldStr
	 *            文件名中要被替换的字符串
	 * @param targetStr
	 *            要替换成的字符串
	 * @version: 2010-5-27 上午10:37:09
	 */
	public void rename(File file, String oldStr, String targetStr) {

		String fileName = file.getName();
		fileName = fileName.replaceAll(oldStr, targetStr);
		File tempFile = new File(file.getParentFile().getPath() + File.separator + fileName);
		file.renameTo(tempFile);
	}

	/**
	 * 描述：得到文件内容
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public static String read(String path) throws IOException {

		File file = new File(path);
		if (!file.exists())
			return null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStreamReader ips = null;
		
		try{
			ips = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(ips);

			/** 读取文件内容 */
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} finally{
			if(br != null) br.close();
			if(ips != null) ips.close();
		}

		return sb.toString();
	}

	/**
	 * 描述：得到文件内容
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public static String read(String path, String charset) throws IOException {

		File file = new File(path);
		if (!file.exists())
			return null;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = null;
		InputStreamReader ips = null;

		try{
			ips = new InputStreamReader(new FileInputStream(file), charset);
			br = new BufferedReader(ips);

			/** 读取文件内容 */
			String line = "";
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}

		} finally{
			if(br != null) br.close();
			if(ips != null) ips.close();
		}
		
		return sb.toString();
	}
	
	/**
	 * 读取文件为字节流
	 * @author: 杨雪令
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public static byte[] readByte(String path) throws Exception {

		File file = new File(path);
		if (!file.exists()) return null;
		InputStream input = null;
		byte[] byt = null;
		try{
			input = new FileInputStream(file);  
		    byt = new byte[input.available()];  
		    input.read(byt);  
		} finally{
			if(input != null) input.close();
		}
		return byt;
	}

	/**
	 * 描述：得到文件内容
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public static List readOfList(String path) throws IOException {

		File file = new File(path);
		if (!file.exists())
			return null;
		List contentList = new ArrayList();
		BufferedReader br = null;
		InputStreamReader ips = null;

		try{
			ips = new InputStreamReader(new FileInputStream(file));
			br = new BufferedReader(ips);

			/** 读取文件内容 */
			String line = "";
			while ((line = br.readLine()) != null) {
				contentList.add(line);
			}
		} finally{
			if(br != null) br.close();
			if(ips != null) ips.close();
		}
		
		return contentList;
	}

	/**
	 * 描述：替换文件内容
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @param oldStr
	 * @param targetStr
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public File replaceContent(File file, String oldStr, String targetStr) throws IOException {

		File targetFile = null;
		OutputStreamWriter ops = null;
		InputStreamReader ips = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		
		try{
			targetFile = new File(file.getPath() + "2");
			ops = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
			ips = new InputStreamReader(new FileInputStream(file), "UTF-8");
			br = new BufferedReader(ips);
			bw = new BufferedWriter(ops);

			/** 读取文件内容并替换 */
			String line = "";
			while ((line = br.readLine()) != null) {
				while (line.indexOf(oldStr) != -1) {
					int addr = line.indexOf(oldStr);
					line = line.substring(0, addr) + targetStr + line.substring(addr + oldStr.length(), line.length());
				}
				bw.append(line);
				bw.newLine();
			}
			
			targetFile.renameTo(file);
			file.delete();
		} finally {
			if(br != null) br.close();
			if(bw != null) bw.close();
			if(ips != null) ips.close();
			if(ops != null) ops.close();
		}

		return targetFile;
	}

	/**
	 * 描述： html文件转换成jsp
	 * 
	 * @author: 杨雪令
	 * @param dir
	 *            要被替换的目录
	 * @param oldStr
	 *            要被替换的字符串
	 * @param targetStr
	 *            要替换成的字符串
	 * @version: 2010-5-27 上午09:32:54
	 * @throws IOException
	 */
	public String htmlToJsp(File file) throws IOException {

		if (file.isDirectory()) {
			/** 得到文件夹下的所有文件 */
			File[] fileList = file.listFiles();
			/** 遍历所有文件 */
			for (int i = 0; i < fileList.length; i++) {
				File childFlie = fileList[i];
				if (childFlie.isDirectory()) {
					htmlToJsp(childFlie);
				} else {

					/** 更改内容 */
					File tempF = htmlToJspOfContent(childFlie);
					/** 更改文件名 */
					if (tempF != null) {
						childFlie.delete();
						rename(tempF, ".html2", ".jsp");
					}
				}
			}
		} else {
			return "目标目录路径不正确，请确认您输入的是一个目录！";
		}
		return "恭喜您，所有HTML页面都转换成JSP页面了哦！";
	}

	/**
	 * 描述： 把html内容转换成jsp内容
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @version: 2010-5-27 上午10:41:53
	 * @throws IOException
	 */
	protected File htmlToJspOfContent(File file) throws IOException {

		File targetFile = null;
		BufferedReader br = null;
		BufferedWriter bw = null;
		OutputStreamWriter ops = null;
		
		try{
			/** 判断文件是否为html文件 */
			if (file.getName().indexOf(".html") > 0) {
				targetFile = new File(file.getPath() + "2", "");
				ops = new OutputStreamWriter(new FileOutputStream(targetFile), "UTF-8");
				br = new BufferedReader(new FileReader(file));
				bw = new BufferedWriter(ops);

				/** 写jsp文件头 */
				bw.append("<%@ page language=\"java\" import=\"java.util.*\" pageEncoding=\"UTF-8\"%>");
				bw.newLine();
				bw.append("<%");
				bw.newLine();
				bw.append("String path = request.getContextPath();");
				bw.newLine();
				bw.append("String basePath = request.getScheme()+\"://\"+request.getServerName()+\":\"+request.getServerPort()+path+\"/\";");
				bw.newLine();
				bw.append("%>");
				bw.newLine();

				/** 读取文件内容并替换 */
				String line = "";
				while ((line = br.readLine()) != null) {
					/** 转小写 */
					line = line.toLowerCase();
					/** 替换字符串../ */
					while (line.indexOf("../") != -1) {
						int addr = line.indexOf("../");
						line = line.substring(0, addr) + line.substring(addr + 3, line.length());
					}
					while (line.indexOf(".html") != -1) {
						int addr = line.indexOf(".html");
						line = line.substring(0, addr) + ".jsp" + line.substring(addr + 5, line.length());
					}

					// line = line.replaceAll("\u002E\u002E/", "");
					if (!(line.indexOf("<meta") != -1 && line.indexOf(">") != -1)) {

						/** 为JAVASCRIPT引用加字符编码 */
						if (line.indexOf("<script") != -1 && line.indexOf("src") != -1 && line.indexOf("charset") == -1) {
							line = "<script charset=\"gb2312\" " + line.substring(7);
						}
						bw.append(line);
						bw.newLine();
					}

					/** 查找<head>位置 */
					if (line.indexOf("<head>") != -1) {
						bw.append("<base href=\"<%=basePath%>\">");
						bw.newLine();
					}

					/** 查找</title>位置添加包含文件 */
					if (line.indexOf("</title>") != -1) {
						bw.append("<jsp:include page=\"/model/include/header.jsp\"></jsp:include>");
						bw.newLine();
					}
				}
				bw.flush();
			}
		} finally{
			if(br != null) br.close();
			if(bw != null) bw.close();
			if(ops != null) ops.close();
		}
		return targetFile;
	}

	/**
	 * 描述： 批量对文件重命名
	 * 
	 * @author: 杨雪令
	 * @param dir
	 *            要被替换的目录
	 * @param nameFront
	 *            新文件前缀
	 * @param namesEnd
	 *            要替换文件的后缀
	 * @param start
	 *            开始数字
	 * @version: 2010-5-27 上午09:32:54
	 */
	public String renameAll(File file, String nameFront, String namesEnd, int start) {

		if (file.isDirectory()) {
			/** 得到文件夹下的所有文件 */
			File[] fileList = file.listFiles();
			/** 遍历所有文件 */
			for (int i = 0; i < fileList.length; i++) {
				File childFlie = fileList[i];
				if (childFlie.isDirectory()) {
					renameAll(childFlie, nameFront, namesEnd, start);
				} else {
					String fileName = childFlie.getName();
					/** 判断后缀是否符合条件 */
					if (namesEnd.trim().equals("") || fileName.substring((fileName.length() - namesEnd.length()), fileName.length()).equals(namesEnd)) {
						/** 如果用户没有输入文件后缀，默认修改全部文件，文件后缀为文件原后缀 */
						String targetFileName = "";

						if (namesEnd.trim().equals("")) {
							targetFileName = nameFront + start
									+ (fileName.indexOf(".") != -1 ? fileName.substring(fileName.indexOf("."), fileName.length()) : "");
						} else
							targetFileName = nameFront + start + "." + namesEnd;

						File tempFile = new File(childFlie.getParentFile().getPath() + File.separator + targetFileName);
						childFlie.renameTo(tempFile);
						start++;
					}
				}
			}
			return "恭喜你，文件重命名成功!";
		} else {
			return "目录路径不正确，请确认您输入的是一个目录！";
		}
	}

	/**
	 * 描述：改变文件编码
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @param oldStr
	 * @param targetStr
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public File changeCode(File file, String thisCode, String targetCode) throws IOException {

		BufferedReader br = null;
		BufferedWriter bw = null;
		OutputStreamWriter ops = null;
		InputStreamReader ips = null;
		File targetFile = null;
		try{
			targetFile = new File(file.getPath() + "2");
			ops = new OutputStreamWriter(new FileOutputStream(targetFile), targetCode);
			ips = new InputStreamReader(new FileInputStream(file), thisCode);
			br = new BufferedReader(ips);
			bw = new BufferedWriter(ops);

			/** 重写文件内容 */
			String line = "";
			while ((line = br.readLine()) != null) {
				bw.append(line);
				bw.newLine();
			}
			bw.flush();
		} finally{
			if(br != null) br.close();
			if(bw != null) bw.close();
			if(ips != null) ips.close();
			if(ops != null) ops.close();
		}
		return targetFile;
	}

	/**
	 * 描述：改变文件编码方法
	 * 
	 * @author: 杨雪令
	 * @param file
	 * @param oldStr
	 * @param targetStr
	 * @return
	 * @version: 2010-6-1 下午05:55:39
	 * @throws IOException
	 */
	public String changeCodeStart(File file, String thisCode, String targetCode) throws IOException {

		if (file.isDirectory()) {
			/** 得到文件夹下的所有文件 */
			File[] fileList = file.listFiles();
			/** 遍历所有文件 */
			for (int i = 0; i < fileList.length; i++) {
				File childFlie = fileList[i];
				if (childFlie.isDirectory()) {
					changeCodeStart(childFlie, thisCode, targetCode);
				} else {
					File targetFile = changeCode(childFlie, thisCode, targetCode);
					childFlie.delete();
					File tempFile = new File(targetFile.getParentFile().getPath() + File.separator
							+ targetFile.getName().substring(0, targetFile.getName().length() - 1));
					targetFile.renameTo(tempFile);
				}
			}
			return "恭喜你，文件编码更改成功!";
		} else {
			return "目录路径不正确，请确认您输入的是一个目录！";
		}
	}

	/**
	 * Description:拷贝文件
	 * 
	 * @param filePath
	 *            原文件路径
	 * @param targetFilePath
	 *            目标文件路径 Copyright　深圳太极软件公司
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static void copy(String filePath, String targetFilePath) throws IOException {

		FileUtil.copy(new File(filePath), targetFilePath);
	}

	/**
	 * Description:拷贝文件
	 * 
	 * @param file
	 *            原文件
	 * @param targetFilePath
	 *            目标文件路径 Copyright　深圳太极软件公司
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static void copy(File file, String targetFilePath) throws IOException {

		FileInputStream fis = null;
		FileOutputStream fos = null;
		try{
			// 给文件创建相关的目录
			String targetDir = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));
			new File(targetDir).mkdirs();
			// System.out.println(targetDir);

			fis = new FileInputStream(file);
			fos = new FileOutputStream(new File(targetFilePath));
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = fis.read(temp)) != -1) {
				fos.write(temp, 0, i);
			}

			fos.flush();
		} finally {
			if(fos != null) fos.close();
			if(fis != null) fis.close();
		}
	}

	/**
	 * Description:拷贝文件
	 * 
	 * @param fileFis
	 *            原文件输入流
	 * @param targetFilePath
	 *            目标文件路径 Copyright　深圳太极软件公司
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static void copy(InputStream fileFis, String targetFilePath) throws IOException {

		InputStream fis = null;
		FileOutputStream fos = null;
		try{
			// 给文件创建相关的目录
			String targetDir = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));
			new File(targetDir).mkdirs();
			// System.out.println(targetDir);

			fis = fileFis;
			fos = new FileOutputStream(new File(targetFilePath));
			byte[] temp = new byte[1024];
			int i = 0;
			while ((i = fis.read(temp)) != -1) {
				fos.write(temp, 0, i);
			}
			fos.flush();
		} finally{
			if(fos != null) fos.close();
			if(fis != null) fis.close();
		}
	}
	
	
	/**
	 * 写文件到输出流
	 * @param filePath 原文件路径
	 * @param outputStream 文件输出流
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static void copy(File file, OutputStream outputStream) throws IOException {

		FileInputStream fis = null;
		try{
			fis = new FileInputStream(file);
			byte[] temp = new byte[1024 * 5];
			int i = 0;
			while ((i = fis.read(temp)) != -1) {
				outputStream.write(temp, 0, i);
			}
		} finally{
			if(fis != null) fis.close();
		}
	}

	/**
	 * <p>Description:拷贝文件
	 * @param fileFis 原文件输入流
	 * @param targetFilePath 目标文件路径 Copyright　深圳太极软件公司
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static void copyAsBuffer(InputStream fileFis, String targetFilePath) throws IOException {

		// 给文件创建相关的目录
		String targetDir = targetFilePath.substring(0, targetFilePath.lastIndexOf("/"));
		new File(targetDir).mkdirs();
		// System.out.println(targetDir);
		BufferedReader br = new BufferedReader(new InputStreamReader(fileFis));
		BufferedWriter bw = new BufferedWriter(new FileWriter(targetFilePath));
		String line = "";
		while ((line = br.readLine()) != null) {
			// 去除自动生成的字符
			if (line.indexOf("------------------------") == -1 && line.indexOf("Content") == -1 && line.length() > 0) {
				bw.write(line);
			}
		}
		br.close();
		bw.close();
	}
	
	
	/**
	 * 保存文件
	 * @param filePath 原文件路径
	 * @param outputStream 文件输出流
	 * @author 杨雪令
	 * @throws IOException
	 */
	public static String saveFile(String dir, MultipartFile multipartFile) throws IOException {
		//保存文件
		String path = FilePathUtil.getWebRoot();
		String fileName = NumberUtil.getRandomStr() + "." + FilePathUtil.getSuffix(multipartFile.getOriginalFilename().toLowerCase());
		FileUtil.copy(multipartFile.getInputStream(), path + dir + fileName);
		
		return fileName;
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
	
	
	/**
	 * 得到文件名称
	 * @param dirPath 路径
	 * @autor yxl
	 */
	public static List<FileInfo> getFileList(String dirPath) {
		List<FileInfo> listFileInfo = new ArrayList();
		if (dirPath != null) {
			File fileFolder = new File(dirPath);
			if (fileFolder.exists() && fileFolder.isDirectory()) {// 进入文件夹
				File[] listFiles = fileFolder.listFiles();
				for (File file : listFiles) {
					FileInfo fileInfo = new FileInfo(file);
					listFileInfo.add(fileInfo);
					if(fileInfo.getIsDir()) listFileInfo.addAll(getFileList(fileInfo.getAbsPath()));
				}
			}
		}

		return listFileInfo;
	}
	

	/**
	 * 文本文件去除重复
	 * @param oldFilePath 旧的文件路径
	 * @param newFilePath 新的文件路径
	 * @param textBlockEndTag 文本块结束标记
	 * @throws IOException 
	 * @autor yxl
	 */
	public static void textFilterDuplicate(String oldFilePath, String newFilePath, String textBlockEndTag) throws IOException {
		List<String> strList = readOfList(oldFilePath);

		StringBuffer newText = new StringBuffer();// 新的文本
		StringBuffer textBlock = new StringBuffer();// 一段文本块
		int count = 0;// 重复数量
		for (String str : strList) {
			str = str.trim();
			if (textBlockEndTag.equals(str)) {
				if (newText.indexOf(textBlock.toString()) == -1) {// 去重复
					newText.append(textBlock);
					newText.append(textBlockEndTag + "\n");
				} else
					count++;
				textBlock.setLength(0);
			} else {
				textBlock.append(str + "\n");
			}
		}
		System.out.println("一共去除" + count + "个重复项");
		FileUtil.writeToFile(newFilePath, newText.toString());
		// System.out.println(newText);
	}

	
	/**
	 * 下载文件
	 * @param urlStr 地址
	 * @throws Exception
	 * @autor yxl
	 * 2013-8-6
	 */
	public static void download(String urlStr, String savePath) throws Exception {
		// 构造URL
		URL url = new URL(urlStr);
		InputStream is = null;
		FilePathUtil.mkFileDirs(savePath);
		File saveFile = new File(savePath);
		FileOutputStream fos = null;
		try{
			URLConnection conn = url.openConnection();
			// 设置请求超时为5s
			conn.setConnectTimeout(5 * 1000);
			// 输入流
			is = conn.getInputStream();

			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;

			fos = new FileOutputStream(saveFile);

			// 开始读取
			while ((len = is.read(bs)) != -1) {
				fos.write(bs, 0, len);
			}
		} finally{
			if(is != null) is.close();
			if(fos != null) fos.close();
		}
	}
	
	
	/**
	 * 下载文件
	 * @param urlStr 地址
	 * @throws Exception
	 * @autor yxl
	 * 2013-8-6
	 */
	public static byte[] download(String urlStr) throws Exception {
		// 构造URL
		URL url = new URL(urlStr);
		InputStream is = null;
		ByteArrayOutputStream bos = null;
		try{
			bos = new ByteArrayOutputStream();
			URLConnection conn = url.openConnection();
			// 设置请求超时为5s
			conn.setConnectTimeout(5 * 1000);
			// 输入流
			is = conn.getInputStream();

			// 1K的数据缓冲
			byte[] bs = new byte[1024];
			// 读取到的数据长度
			int len;

			// 开始读取
			while ((len = is.read(bs)) != -1) {
				bos.write(bs, 0, len);
			}
			return bos.toByteArray();
		} finally{
			if(is != null) is.close();
			if(bos != null) bos.close();
		}
	}
	
	/** 
     * 把字节数组保存为一个文件 
     * @param fileData 文件数据
     * @param path 路径
     */  
    public static void saveFile(byte[] fileData, String path) {  
        BufferedOutputStream stream = null;  
        try {  
            FileOutputStream fstream = new FileOutputStream(path);  
            stream = new BufferedOutputStream(fstream);  
            stream.write(fileData);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (stream != null) {  
                try {  
                    stream.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }

	public static void main(String[] args) throws Exception {
		download("http://f.topisv.com/Rm/500018.png", "d:/down/2.png");
	}
}
