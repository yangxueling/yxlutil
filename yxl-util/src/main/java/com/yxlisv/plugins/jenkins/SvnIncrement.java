package com.yxlisv.plugins.jenkins;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxlisv.util.file.FileUtil;
import com.yxlisv.util.math.NumberUtil;

/**
 * jenkins svn 增量包插件
 * @createTime 2015年12月6日 下午1:48:27 
 * @author yxl
 */
public class SvnIncrement {
	
	
	/**
	 * 打包
	 * @param workspace 项目空间
	 * @date 2015年12月6日 下午1:49:48 
	 * @author yxl
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean toPackage(String workspace) throws IOException{
		//最后一次成功打包的版本号
		String lashSuccessRevision = getLastSuccessReVersion(workspace);
		System.out.println("lashSuccessRevision : " + lashSuccessRevision);
		//读取变化的文件
		Map<String, String> changeFiles = getChangeFile(workspace, NumberUtil.parseLong(lashSuccessRevision));
		if(changeFiles.size()<1) {
			System.out.println("svn not have update, don't need delopy...");
			return false;
		}
		
		//删除就的增量包
		String incrementPath = workspace + "/increment";
		File incrementFoder = new File(incrementPath);
		if(incrementFoder.exists()) FileUtil.delete(incrementPath);
		incrementFoder.mkdirs();
		
		//制作增量包
		int fileCount = 0;
		for(Iterator it=changeFiles.entrySet().iterator(); it.hasNext();){
			Map.Entry<String, String> entry = (Entry) it.next();
			String filePath = workspace + "/" + entry.getKey();
			String type = entry.getValue().split("_")[1];
			if(type.equals("D")){
				System.out.println("warn：svn delete file " + filePath);
			} else {
				String buildFilePath = getBuildFilePath(filePath, true);
				File file = new File(buildFilePath);
				if(file.isFile()){
					String incrementFilePath = incrementPath + "/" + getBuildFilePath(entry.getKey(), false);
					System.out.println("Copy file " + buildFilePath + "\n       to "+ incrementFilePath + " ...");
					FileUtil.copy(file, incrementFilePath);
					fileCount++;
				} else if(!file.exists()){
					System.out.println("warn：File not exists : " + buildFilePath);
				}
			}
		}
		System.out.println("Increment success!, From SVN version " + lashSuccessRevision + ", file count : " + fileCount);
		return true;
	}
	
	/**
	 * 根据源文件路径，获取编译文件路径
	 * @param filePath 源文件路径
	 * @param addTargetPath 是否添加target路径
	 * @date 2015年12月6日 下午3:10:54 
	 * @author yxl
	 */
	public String getBuildFilePath(String filePath, boolean addTargetPath) {
		String packageName = null;
		System.out.println(filePath);
		if(filePath.indexOf("/src")<0) return "";
		for(String packageFileName : new File(filePath.substring(0, filePath.indexOf("/src"))+"/target").list()){
			if(packageFileName.toLowerCase().endsWith(".war")) {
				packageName = packageFileName.substring(0, packageFileName.toLowerCase().indexOf(".war"));
				break;
			} else if(packageFileName.toLowerCase().endsWith(".jar")) {
				packageName = "jar";
				break;
			}
		}
		if(packageName == null) {
			System.out.println("get build file path error : " + filePath);
			return "";
		}
		System.out.println("packageName : " + packageName);
		System.out.println("src path : " + filePath);
		filePath = filePath.replaceAll("\\.java", "\\.class");
		if(addTargetPath) {
			if(packageName.equals("jar")){//如果是打jar包，直接去target/classes目录获取文件
				filePath = filePath.replaceAll("src/main/resources", "target/classes");
				filePath = filePath.replaceAll("src/main/java", "target/classes");
				filePath = filePath.replaceAll("src/main", "target/"+ packageName);
			} else {
				filePath = filePath.replaceAll("src/main/webapp", "target/"+ packageName);
				filePath = filePath.replaceAll("src/main/resources", "target/"+ packageName +"/WEB-INF/classes");
				filePath = filePath.replaceAll("src/main/java", "target/"+ packageName +"/WEB-INF/classes");
				filePath = filePath.replaceAll("src/main", "target/"+ packageName);
			}
		} else {
			filePath = filePath.replaceAll("src/main/webapp/", "");
			filePath = filePath.replaceAll("src/main/resources", "WEB-INF/classes");
			filePath = filePath.replaceAll("src/main/resources", "WEB-INF/classes");
			filePath = filePath.replaceAll("src/main/java", "WEB-INF/classes");
			filePath = filePath.replaceAll("src/main/", "");
		}
		System.out.println("target path : " + filePath);
		return filePath;
	}

	/**
	 *	获取打包的SVN版本号
	 * @param workspace 项目空间
	 * @date 2015年12月6日 下午1:49:48 
	 * @author yxl
	 */
	public String getSVNRevision(String filePath) throws IOException{
		//System.out.println("read file : " + filePath);
		String revision = FileUtil.read(filePath);
		//System.out.println(revision);
		if(revision==null) return "-1";
		revision = revision.substring(revision.lastIndexOf("/")+1);
		return revision.trim();
	}
	
	/**
	 *	获取Builds目录路径
	 * @param workspace 项目空间
	 * @date 2015年12月6日 下午1:49:48 
	 * @author yxl
	 */
	public String getBuildsPath(String workspace) throws IOException{
		return workspace.substring(0, workspace.lastIndexOf("/")) + "/builds";
	}
	
	/**
	 *	获取最后一次成功打包的版本号
	 * @param workspace 项目空间
	 * @date 2015年12月6日 下午1:49:48 
	 * @author yxl
	 */
	public String getLastSuccessReVersion(String workspace) throws IOException{
		return getSVNRevision(getBuildsPath(workspace) + "/lastSuccessfulBuild/revision.txt");
	}
	
	
	/**
	 *	获取最后一次成功打包的版本号
	 * @param workspace 项目空间
	 * @param lashSuccessRevision 最后一次成功打包的版本号
	 * @date 2015年12月6日 下午1:49:48 
	 * @author yxl
	 */
	public Map<String, String> getChangeFile(String workspace, long lashSuccessRevision) throws IOException{
		Map<String, String> changeFiles = new HashMap<String, String>();//变化的文件<文件名，A/U/D>
		for(String buildFoder : new File(getBuildsPath(workspace)).list()){
			String revision =  getSVNRevision(getBuildsPath(workspace) + "/" + buildFoder + "/revision.txt");
			//根据builds的svn版本号筛选
			if(lashSuccessRevision<NumberUtil.parseLong(revision)){
				System.out.println("! Find svn change data : " + getBuildsPath(workspace) + "/" + buildFoder + "/revision.txt" + " : " + revision);
				addChangeFile(changeFiles, getBuildsPath(workspace) + "/" + buildFoder + "/log", NumberUtil.parseLong(revision));
			}
			
		}
		return changeFiles;
	}
	
	//日志文件svn更新内容正则
	private static Pattern logSvnChangPattern = Pattern.compile("([AUD])\\s{9}([a-zA-Z0-9-_/.]+)\\s");
	/**
	 * 添加改变的文件
	 * @param changeFiles 变化的文件<文件名，revision_A/U/D>
	 * @date 2015年12月6日 下午2:23:58 
	 * @author yxl
	 */
	public void addChangeFile(Map<String, String> changeFiles, String logPath, long revision) throws IOException {
		System.out.println("! read log : " + logPath);
		String logContent = FileUtil.read(logPath);
		Matcher logSvnChangMatcher = logSvnChangPattern.matcher(logContent);
		while (logSvnChangMatcher.find()){
			String changeRow = logSvnChangMatcher.group(0);
			if(changeRow.length()>0) {
				String type = logSvnChangMatcher.group(1);
				String filePath = logSvnChangMatcher.group(2);
				//如果已经添加了改变的文件，而且版本号比当前记录大，忽略此条记录
				if(changeFiles.get(filePath)!=null){
					if(NumberUtil.parseLong(changeFiles.get(filePath).split("_")[0])>revision) continue;
				}
				System.out.println(filePath + " / " + revision+"_"+type);
				changeFiles.put(filePath, revision+"_"+type);
			}
		}
	}

	/**
	 * 启动
	 * @date 2015年12月6日 下午1:50:15 
	 * @author yxl
	 */
	public static void main(String[] args) {
		
		String workspace = args[0];
		//String workspace = "C:/Users/HX15011070/Desktop/jenkins/workspace";
		try {
			new SvnIncrement().toPackage(workspace);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
