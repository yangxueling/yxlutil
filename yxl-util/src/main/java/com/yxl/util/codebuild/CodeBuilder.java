package com.yxl.util.codebuild;

/**
 * 代码生成器
 * @author john Local
 */
public abstract class CodeBuilder {
	
	/** 包名 */
	protected String packageName = "com.yxl";
	/** 父类包名 */
	protected String parentPackageName = "com.yxl";
	/** 文件目录 */
	protected String fileDir = null;
	/** 根目录 */
	protected String baseDir = null;
	
	/**
	 * 设置文件目录，根目录+包目录
	 * @param baseDir 根目录
	 * @autor yxl
	 */
	public void setFileDir(String baseDir){
		this.baseDir = baseDir;
		this.fileDir = CodeBuilder.getFileDir(packageName, baseDir);
	}
	
	/**
	 * 得到文件路径
	 * @param packageName 包名
	 * @param baseDir 根目录
	 * @autor yxl
	 */
	public static String getFileDir(String packageName, String baseDir) {
		return baseDir += "/" + packageName.replaceAll("\\.", "/") + "/";
	}

	/**
	 * 生成
	 * 
	 * @autor yxl
	 */
	public abstract void build();
}
