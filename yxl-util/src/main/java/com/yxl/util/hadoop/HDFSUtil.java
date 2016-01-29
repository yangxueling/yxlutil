/*package com.yxl.util.hadoop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxl.util.file.FileUtil;

*//**
 * hadoop HDFS工具类
 * 需要引入jar包hadoop-hdfs，不要引入hadoop-core，否则会冲突
 * 		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-common</artifactId>
			<version>2.7.1</version>
			<optional>true</optional>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-client</artifactId>
			<version>2.7.1</version>
			<optional>true</optional>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-minicluster</artifactId>
			<version>2.7.1</version>
			<optional>true</optional>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
		<dependency>
			<groupId>org.apache.hadoop</groupId>
			<artifactId>hadoop-hdfs</artifactId>
			<version>2.7.1</version>
			<optional>true</optional>
			<exclusions>
				<exclusion>
					<groupId>org.slf4j</groupId>
					<artifactId>slf4j-log4j12</artifactId>
				</exclusion>
				<exclusion>
					<groupId>log4j</groupId>
					<artifactId>log4j</artifactId>
				</exclusion>
			</exclusions>
		</dependency>
 * @createTime 2015年11月8日 上午8:22:07 
 * @author yxl
 *//*
public class HDFSUtil {

	private static Logger logger = LoggerFactory.getLogger(HDFSUtil.class);
	
	*//** hadoop配置 *//*
	public static Configuration conf = new Configuration();
	*//** hadoop文件系统 *//*
	public static FileSystem fs = null;
	static{
		conf.set("fs.defaultFS", HadoopConfig.hdfsAddr);
		try {
			fs = FileSystem.get(conf);
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	*//**
	 * 创建文件夹
	 * @param path 文件夹路径，如：/test/1
	 * @param filePath 文件路径
	 * @date 2015年11月8日 上午7:51:12 
	 * @author yxl
	 *//*
	public static void mkdirs(String path) throws IllegalArgumentException, IOException{
		fs.mkdirs(new Path(path));
	}

	*//**
	 * 上传文件
	 * @param path 文件存放路径，如：/test/1.png
	 * @param localPath 本地文件路径
	 * @date 2015年11月8日 上午7:51:12 
	 * @author yxl
	 *//*
	public static void put(String path, String localPath) throws IllegalArgumentException, IOException{
		fs.copyFromLocalFile(new Path(localPath), new Path(path));
	}
	
	*//**
	 * 文件是否存在
	 * @param path 文件路径，如：/test/1.png
	 * @date 2015年11月8日 上午7:51:12 
	 * @author yxl
	 *//*
	public static boolean exists(String path) throws IllegalArgumentException, IOException{
		return fs.exists(new Path(path));
	}
	
	*//**
	 * 文件是否存在
	 * @param path 文件路径，如：/test/1.png
	 * @date 2015年11月8日 上午7:51:12 
	 * @author yxl
	 *//*
	public static FSDataInputStream get(String path) throws IllegalArgumentException, IOException{
		Path filePath = new Path(path);
		if(fs.exists(filePath)) return fs.open(filePath);
		return null;
	}
	
	*//**
	 * 保存文件
	 * @param fsi 输入流
	 * @param path 要保存的路径
	 * @date 2015年11月8日 上午8:02:18 
	 * @author yxl
	 *//*
	public static boolean save(FSDataInputStream fsi, String path) throws IOException{
		if(fsi==null) {
			logger.error("Save file \""+ path +"\" faild, Because InputStream is null.");
			return false;
		}
		FileUtil.copy(fsi, path);
		return true;
	}
	
	*//**
	 * 获取一个路径下的文件列表
	 * @param path 路径
	 * @date 2015年11月8日 下午5:21:57 
	 * @author yxl
	 *//*
	public static List<String> list(String path) throws FileNotFoundException, IllegalArgumentException, IOException{
		List<String> fileList = new ArrayList();
		FileStatus[] fileStatusArray = fs.listStatus(new Path(path));
		for(FileStatus fileStatus : fileStatusArray){
			fileList.add(fileStatus.getPath().getName());
		}
		return fileList;
	}
	
	
	*//**
	 * 删除文件
	 * @param path 文件路径，如：/test/1.png
	 * @date 2015年11月8日 上午7:51:12 
	 * @author yxl
	 * @throws IOException 
	 * @throws IllegalArgumentException 
	 *//*
	public static boolean delete(String path) throws IllegalArgumentException, IOException {
		return fs.deleteOnExit(new Path(path));
	}

	public static void main(String[] args) throws IOException, URISyntaxException {
		System.setProperty("os.name", "Linux");
		long startTime = System.currentTimeMillis();
		//test 1
		//put("test/testMp41", "C:/Users/HX15011070/Desktop/testVideo/Let102.mp4");
		//put("test/testMp42", "C:/Users/HX15011070/Desktop/testVideo/Let102.mp4");
		//put("test/testMp45", "C:/Users/HX15011070/Desktop/testVideo/Let102.mp4");
		//System.out.println(delete("test/testMp4"));
		System.out.println(exists("test/testMp43"));
		//save(get("test/testMp45"), "d:/testHadoop/testMp451.mp4");
		
		//test 2
		//put("bigMp43", "C:/Users/HX15011070/Desktop/testVideo/bbb_sunflower_2160p_60fps_normal.mp4");
		//save(get("bigMp4"), "d:/testHadoop/big.mp4");
		
		//test 3
		for(String fileName : list("/")){
			System.out.println(fileName);
			delete(fileName);
		}
		
		//test4
		//put("/root/wordcount/input/1", "D:/小自播海外/日志.txt");
	
		System.out.println("use " + (System.currentTimeMillis()-startTime) + " ms...");
	}
}
*/