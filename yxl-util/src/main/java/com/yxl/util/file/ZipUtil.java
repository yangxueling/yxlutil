package com.yxl.util.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Zip;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxl.util.web.URLUtil;

/**
 * zip 工具类
 * @author yxl
 */
public class ZipUtil {

	private static Logger logger = LoggerFactory.getLogger(ZipUtil.class);

	/**
	 * 打包
	 * @param baseFolder 根目录
	 * @param fileList file list 集合
	 * @param zipFilePath 输出zip文件路径
	 * @param packagedResult 打包状态,为一个list对象：
	 * 										第0个元素是打包状态  0：正在打包，1：打包完成，2：打包出错， 9:准备数据  
	 * 										第1个元素是key
	 * 										第2个元素是打包进度百分比
	 * 										第3个元素是打包的实时信息
	 * @param level 压缩级别，数值越大压缩程度越高，但是消耗时间越大: 测试，压缩同一个文件夹（-1 307k，0  652k, 1  320k, 2  317k 3  315k, 4  310k, 5  307k, 9  307k）
	 * @autor yxl
	 */
	public static void toZip(String baseFolder, String zipFilePath, int level) {
		//zip输出目录，如果目录不存在，就创建目录
		String zipFileDir = URLUtil.getStandardUrl(zipFilePath);
		if(zipFileDir.indexOf("/") != -1){
			zipFileDir = zipFileDir.substring(0, zipFileDir.lastIndexOf("/"));
			File zipFileDirFile = new File(zipFileDir);
			if(!zipFileDirFile.exists()) zipFileDirFile.mkdirs();
		}
		
		Project proj = new Project();
		Zip zip = new Zip();
		zip.setBasedir(new File(baseFolder));
        zip.setProject(proj);
        zip.setDestFile(new File(zipFilePath));
        zip.setEncoding("utf8");
        zip.setLevel(level);
        zip.execute();
	}
	
	
	/**
	 * 写文件
	 * @param zOut zip 输出流
	 * @param file 文件
	 * @throws IOException
	 * @autor yxl
	 */
	public static void writeFile(ZipOutputStream zOut, File file) throws IOException {
		FileInputStream in = new FileInputStream(file);
		int len;
		while ((len = in.read()) != -1)
			zOut.write(len);
		in.close();
	}

	public static void main(String[] args) {

		long start = System.currentTimeMillis();
		String folder = "E:/workspace/netyntdp/webapp/netyntdp";
		ZipUtil.toZip(folder, "F:/zip/test.zip", 9);
		
		long end = System.currentTimeMillis();
		System.out.println("ant zip 耗时: " + (end - start) / 1000);
	}
}