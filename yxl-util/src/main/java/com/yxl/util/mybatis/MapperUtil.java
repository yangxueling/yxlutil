package com.yxl.util.mybatis;

import java.io.IOException;
import java.util.List;

import com.yxl.util.file.FileInfo;
import com.yxl.util.file.FileUtil;

/**
 * Mapper配置文件添加缓存
 * @createTime 2015年11月28日 下午5:38:49 
 * @author yxl
 */
public class MapperUtil {
	
	/** 要插入的内容 */
	public static String content = "\n\t<!-- 使用二级缓存 -->\n\t<cache eviction=\"LRU\" type=\"com.hksj.common.mybatis.MybatisRedisCache\"/>\n";
	
	/**
	 * 给mapper文件添加缓存标签
	 * @param dir 目录
	 * @param content 内容
	 * @date 2015年11月28日 下午5:41:44 
	 * @author yxl
	 */
	public static void addCacheTag(String dir, String content) throws IOException{
		List<FileInfo> fileList = FileUtil.getFileList(dir);
		int count = 0;
		for(FileInfo fileInfo : fileList){
			if(fileInfo.getName().endsWith("Mapper.xml")){
				String fileContent = FileUtil.read(fileInfo.getAbsPath());
				if(fileContent.contains("<cache")) continue;
				fileContent = fileContent.replaceAll("(<\\s*mapper\\s*namespace\\s*=\\s*\"[a-zA-Z.0-9]+\\s*\">)", "$0" + content);
				fileContent = fileContent.replaceAll("\\s*<\\s*mapper", "<mapper");
				FileUtil.saveFile(fileContent.getBytes("utf-8"), fileInfo.getAbsPath());
				System.out.println(fileInfo.getAbsPath() + " add cache tag...");
				count++;
			}
		}
		System.out.println("update file count " + count);
	}
	
	
	/**
	 * 给mapper文件添加缓存标签
	 * @param dir 目录
	 * @param content 内容
	 * @date 2015年11月28日 下午5:41:44 
	 * @author yxl
	 */
	public static void format(String dir, String content) throws IOException{
		List<FileInfo> fileList = FileUtil.getFileList(dir);
		int count = 0;
		for(FileInfo fileInfo : fileList){
			if(fileInfo.getName().endsWith("Mapper.xml")){
				String fileContent = FileUtil.read(fileInfo.getAbsPath());
				String oldContent = fileContent;
				fileContent = fileContent.replaceAll("\\s*<\\?xml", "<\\?xml");
				if(!fileContent.equals(oldContent)){
					FileUtil.saveFile(fileContent.getBytes("utf-8"), fileInfo.getAbsPath());
					System.out.println(fileInfo.getAbsPath() + " format...");
					count++;
				}
			}
		}
		System.out.println("update file count " + count);
	}

	public static void main(String[] args) {
		String dir = "D:/java/workspace/xiaozibo/xiaoziboMiddleware/src";
		try {
			//MapperUtil.format(dir, content);
			MapperUtil.addCacheTag(dir, content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
