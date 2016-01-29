package com.yntsoft.nlplatform.utils.doc;

import java.io.File;
import java.util.Map;

import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.system.SysCmdHandle;

/**
 * pdf 文件转swf文件，利用工具 pdf2swf.exe
 * windows 平台
 * @author john Local
 */
public class Pdf2Swf extends AbstractTask{
	
	
	
	/**
	 * 获取cmd命令
	 * @param filePath 文件路径
	 * @param outPath 输出路径
	 * @autor yxl
	 */
	private String getCmd(String filePath, String outPath){
		return this.getBasePath() + "pdf2swf.exe -o "+ FilePathUtil.getWebRoot() + outPath +" -t "+ FilePathUtil.getWebRoot() + filePath +" -s flashversion=9";
	}
	
	/**
	 * 获取cmd命令
	 * @param filePath 文件路径
	 * @param outPath 输出路径
	 * @autor yxl
	 */
	private String getCmdTest(String filePath, String outPath){
		return this.getBasePath() + "pdf2swf.exe -o "+ outPath +" -t "+ filePath +" -s flashversion=9";
	}
	
	
	/**
	 * 轉換文檔
	 * @param filePath 文件路徑
	 * @param outPath 輸出路徑
	 * @autor yxl
	 */
	@Override
	public void convertDocument(String filePath, String outPath){
		String cmd = this.getCmd(filePath, outPath);
		//cmd = this.getCmdTest(filePath, outPath);
		SysCmdHandle sch = new SysCmdHandle();
		sch.CMD_TIME_OUT = 200;
		System.out.println("convert swf cmd: " + cmd);
		sch.endKeyStr = "writing";
		Map result = sch.excute(cmd);
		//System.out.println("convert swf return: " + result);
		
		if(!result.get("status").toString().equals("200") || !new File(FilePathUtil.getWebRoot() + outPath).exists()) this.error("swf", docPath);
		else {
			this.success("swf", docPath, outPath);
			System.out.println("convert success: " + outPath);
		}
	}

	/**
	 * pdf 文件转swf文件，利用工具 pdf2swf.exe
	 * @param args
	 * @autor yxl
	 */
	public static void main(String[] args) {
		String filePath = "d:/1.pdf";
		String outPath = "";
		
		for(int i=0; i<6; i++){
			Pdf2Swf p2s = new Pdf2Swf();
			p2s.docPath = filePath;
			p2s.outPath = outPath;
			
			System.out.println("d:/test_pdf/"+ i +".swf");
			p2s.outPath = "d:/test_pdf/"+ i +".swf";
			new Thread(p2s).start();
		}
	}

}