package com.yntsoft.nlplatform.utils.doc;

import java.io.File;
import java.io.IOException;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * 利用 open office(windows平台)
 * 需要下载 jodconverter，使用里面的jar包
 * office 文件轉 pdf 文件
 * @author john Local
 *
 */
public class Office2Pdf extends AbstractTask implements Runnable {

	/**
	 * 轉換文檔
	 * @param docPath 文件路徑
	 * @param outPath 輸出路徑
	 * @throws IOException 
	 * @throws InterruptedException 
	 * @autor yxl
	 */
	@Override
	public void convertDocument(String docPath, String outPath) throws Exception {

		logger.info("start convert doc to pdf: " + docPath);
		Long startTime = System.currentTimeMillis();
		OpenOfficeConnection connection = null;
		try{
			connection = new SocketOpenOfficeConnection("127.0.0.1", 13333);
			connection.connect();
		} catch(Exception e){
			
			// I18N.getString(key, fileName)
			final String command = "D:/Program Files/OpenOffice 4/program/soffice.exe -headless -accept=\"socket,host=127.0.0.1,port=13333;urp;\"";
			//启动服务
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Runtime.getRuntime().exec(command);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();
			Thread.sleep(5000);//等待服务启动
			
			//重新建立连接
			connection = new SocketOpenOfficeConnection("127.0.0.1", 13333);
			connection.connect();
		}

		// convert
		DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
		converter.convert(new File(docPath), new File(outPath));

		// close the connection
		connection.disconnect();

		// 回调
		this.success("pdf", docPath, outPath);
		logger.info("convert to pdf success ["+ (System.currentTimeMillis() - startTime) +"]: " + outPath);
	}

	/**
	 * 測試
	 * @autor yxl
	 */
	public static void main(String[] args) {

		String docPath = "d:/1.docx";
		String outPath = "d:/1.pdf";
		Office2Pdf o2p = new Office2Pdf();
		o2p.docPath = docPath;
		o2p.outPath = outPath;
		new Thread(o2p).start();
	}
}
