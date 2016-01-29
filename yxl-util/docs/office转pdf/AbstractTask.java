package com.yntsoft.nlplatform.utils.doc;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.file.FilePathUtil;


/**
 * 转换线程的父类
 * @author yxl
 */
public abstract class AbstractTask implements Runnable{
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	// 接口
	public IConvert convert;
	// 缓存，可以寄存东西在这里，回调时送回
	public Object cache;
	
	/**
	 * 转换成功时调用
	 * @param tag 标识
	 * @param newFilePath 新文件路径
	 * @autor yxl
	 */
	protected void success(String tag, String oldFilePath, String newFilePath){
		if (convert != null) convert.onSuccess(tag, this.resetPath(oldFilePath), this.resetPath(newFilePath), cache);
	}
	
	/**
	 * 转换成功时调用
	 * @param tag 标识
	 * @autor yxl
	 */
	protected void error(String tag, String oldFilePath){
		if (convert != null) convert.onError(tag, this.resetPath(oldFilePath), cache);
	}
	
	/**
	 * 重置路徑，從絕對路徑轉換為相對路徑
	 * @autor yxl
	 */
	private String resetPath(String path){
		if(path.contains(FilePathUtil.getWebRoot())) path = path.replaceAll(FilePathUtil.getWebRoot(), "");
		if(path.contains(this.getBasePath())) path = path.replaceAll(this.getBasePath(), "");
		
		return path;
	}
	
	
	/**
	 * 轉換文檔
	 * @param docPath 文件路徑
	 * @param outPath 輸出路徑
	 * @autor yxl
	 */
	public abstract void convertDocument(String docPath, String outPath) throws Exception;
	
	/**文件路徑*/
	public String docPath = null;
	/**輸出路徑*/
	public String outPath = null;

	/** 允许同时执行多少个线程 */
	public int maxThread = 1; 
	
	//当前线程数量
	private static int currentThreadCount = 0;
	
	@Override
	public void run() {
		while (currentThreadCount >= maxThread){
			try {
				Thread.sleep(2000 * currentThreadCount);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//System.out.println("一个线程开始执行");
		currentThreadCount++;
		try {
			this.running();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println("一个线程执行完成");
		currentThreadCount--;
	}
	
	/**执行
	 * @throws IOException */
	protected void running() throws Exception{
		if (docPath !=null && outPath != null)
			this.convertDocument(docPath, outPath);
		else System.out.println("文件路径不能为空");
	}
	
	
	/**
	 * 得到路径
	 * @autor yxl
	 */
	protected String getBasePath() {
		try {
			return this.getClass().getResource("").toURI().getPath().substring(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
}
