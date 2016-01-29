package com.yxl.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.yxl.util.date.DateUtil;
import com.yxl.util.math.NumberUtil;

/**
 * 文件信息
 * @author yxl
 */
public class FileInfo {

	/** 文件名 */
	private String name;
	
	/** 文件大小 */
	private String fileSize;
	
	/** 创建时间 */
	private String createTime;
	
	/** 文件路径 */
	private String path;
	
	/** 文件绝对路径 */
	private String absPath;
	
	/** 是不是普通文件 */
	private boolean isFile;
	
	/** 是不是文件夹 */
	private boolean isDir;
	

	/**
	 * 构造函数
	 */
	public FileInfo(File file) {
		this.setName(file.getName());
		this.setCreateTime(file.lastModified());
		this.setAbsPath(file.getAbsolutePath());
		this.setIsFile(file.isFile());
		this.setIsDir(file.isDirectory());
		
		//文件大小
		if (file.exists() && !file.isDirectory()) {
			 FileInputStream fis = null;
	         try {
				fis = new FileInputStream(file);
				this.setFileSize(fis.available());
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				try {
					if(fis != null) fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}


	/**  
	 * 获取isFile  FileInfo.java
	 * @return isFile isFile  
	 */
	public boolean getIsFile() {
		return isFile;
	}


	/**  
	 * 获取isDir  FileInfo.java
	 * @return isDir isDir  
	 */
	public boolean getIsDir() {
		return isDir;
	}


	/**  
	 * 设置isFile  
	 * @param isFile isFile  
	 */
	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
	}


	/**  
	 * 设置isDir  
	 * @param isDir isDir  
	 */
	public void setIsDir(boolean isDir) {
		this.isDir = isDir;
	}


	/**  
	 * 是不是图片
	 */
	public boolean getIsImage() {
		String imageTags = ".jpg,.jpeg,.png,.gif";
		String endTag = "";//后缀
		if (name.lastIndexOf(".") != -1) endTag = name.substring(name.lastIndexOf("."));
		endTag = endTag.toLowerCase();
		if(endTag!=null && !endTag.equals("") && imageTags.contains(endTag)) return true;
		return false;
	}


	/**  
	 * 获取path  FileInfo.java
	 * @return path path  
	 */
	public String getPath() {
		return path;
	}

	/**  
	 * 获取absPath  FileInfo.java
	 * @return absPath absPath  
	 */
	public String getAbsPath() {
		return absPath;
	}

	/**  
	 * 设置path  
	 * @param path path  
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**  
	 * 设置absPath  
	 * @param absPath absPath  
	 */
	public void setAbsPath(String absPath) {
		this.absPath = absPath;
	}

	/**  
	 * 获取 name  File.java
	 * @return name name  
	 */
	public String getName() {
		return name;
	}

	/**  
	 * 获取文件大小，byte
	 * @return fileSize fileSize  
	 */
	public String getFileSize() {
		return fileSize;
	}
	
	/**  
	 * 获取文件大小，K
	 * @return fileSize fileSize  
	 */
	public String getFileSizeOfK() {
		return NumberUtil.getFomat2((Double.parseDouble(fileSize) / 1024f)) + "";
	}
	
	/**  
	 * 获取文件大小，M
	 * @return fileSize fileSize  
	 */
	public String getFileSizeOfM() {
		return NumberUtil.getFomat2((Double.parseDouble(fileSize) / 1024f / 1024f)) + "";
	}

	/**  
	 * 获取createTime  File.java
	 * @return createTime createTime  
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**  
	 * 设置name  
	 * @param name name  
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**  
	 * 设置fileSize  
	 * @param fileSize fileSize  
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize + "";
	}

	/**  
	 * 设置createTime  
	 * @param createTime createTime  
	 */
	public void setCreateTime(long createTime) {
		this.createTime = DateUtil.toTime(createTime);
	}
	
	public static void main(String[] args) {
		//FileInfo fileInfo = new FileInfo();
		//fileInfo.setName("23434.jpg2");
		//System.out.println(fileInfo.getIsImage());
	}
}