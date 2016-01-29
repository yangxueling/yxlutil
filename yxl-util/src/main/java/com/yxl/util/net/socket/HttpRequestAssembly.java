package com.yxl.util.net.socket;


/**
 * HTTP 请求组装器
 * @author yxl
 */
public class HttpRequestAssembly {

	/**
	 * 获取http发送文件时的头信息
	 * @param fileName 文件名称
	 * @param filePath 文件路径
	 * @param boundary 分隔符
	 * @autor yxl
	 * 2013-8-6
	 */
	public static String getHttpPostFileHead(String fileName, String filePath, String boundary) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n--" + boundary + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fileName + "\"; filename=\"" + filePath + "\"\r\n");
		sb.append("Content-Type: "+ getFileContentType(filePath) +"\r\n\r\n");
		return sb.toString();
	}
	
	/**
	 * 获取http文件类别
	 * @param filePath 文件路径
	 * @autor yxl
	 * 2013-8-6
	 */
	public static String getFileContentType(String filePath) {
		String fileType = null;
		//根据文件后缀去判断类别
		filePath = filePath.toLowerCase();
		if(filePath.endsWith(".txt")) fileType = "text/plain";
		else if(filePath.endsWith(".jpg") || filePath.endsWith(".jpeg")) fileType = "image/jpeg";
		else if(filePath.endsWith(".png")) fileType = "image/png";
		else if(filePath.endsWith(".gif")) fileType = "image/gif";
		else if(filePath.endsWith(".bmp")) fileType = "image/bmp";
		else fileType = "application/octet-stream";
		
		return fileType;
	}
	
	
	
	/**
	 * 获取http发送文件时的头信息
	 * @param fieldName 参数名
	 * @param fieldValue 参数值
	 * @param boundary 分隔符
	 * @autor yxl
	 * 2013-8-6
	 */
	public static String getHttpPostArgsHead(String fieldName, String fieldValue, String boundary) {
		StringBuilder sb = new StringBuilder();
		sb.append("\r\n--" + boundary + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fieldName + "\"\r\n");
		sb.append("Content-Type: text/plain\r\n\r\n");
		sb.append(fieldValue);
		return sb.toString();
	}
}