package com.yxlisv.util.web;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>文件上传Servlet</p>
 * <p>支持多文件同时上传</p>
 * @author 杨雪令
 * @time 2016年3月29日下午5:24:16
 * @version 1.0
 */
@SuppressWarnings({ "serial" })
public class UploadServlet extends HttpServlet {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/** 用户可上传的目录， */
	public static final String USER_UP_FODER_KEY = "USER_UP_FODER_KEY";

	/** 是否同步保存文件，默认为异步保存 */
	public static boolean saveFileSync = false;

	/** 是否检查上传权限 */
	public static boolean checkSecurity = false;

	// Apache FileItemFactory
	private static final FileItemFactory fileItemFactory = new DiskFileItemFactory();

	/**
	 * <p>文件上传请求处理</p>
	 * <p>支持多文件同时上传</p>
	 * @author 杨雪令
	 * @time 2016年3月29日下午5:24:16
	 * @version 1.0
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
		// 文件名
		String saveAs = request.getParameter("saveAs") != null ? request.getParameter("saveAs") : "";
		// 保存路径
		String path = request.getParameter("path") != null ? request.getParameter("path") : "uploadfiles/";
		path = URLUtil.getStandardUrl(path);
		//数据类型
		String type = request.getParameter("type") != null ? request.getParameter("type") : "text";

		// 检测上传权限
		if (!checkSecurity(request, path)) {
			logger.warn("用户没有权限上传文件到：" + path);
			HttpUtil.outputText("你没有权限上传文件到：" + path, response);
			return;
		}

		String savePath = this.getServletConfig().getServletContext().getRealPath("/") + path + "/";
		File dir = new File(savePath);
		if (!dir.exists()) dir.mkdirs();

		// ServletFileUpload 工具
		ServletFileUpload servletFileUpload = new ServletFileUpload(fileItemFactory);
		servletFileUpload.setSizeMax(5000 * 1024 * 1024);// 5000M

		// 获取多个上传文件
		List<FileItem> fileList = null;
		try {
			fileList = servletFileUpload.parseRequest(request);
		} catch (FileUploadException ex) {
			logger.error("没有检测到要上传的文件");
			HttpUtil.outputText("没有检测到要上传的文件", response);
			return;
		}

		// 文件名称
		StringBuffer fileNames = new StringBuffer();
		// 遍历上传文件写入磁盘
		for (FileItem fileItem : fileList) {
			if (fileItem.isFormField()) continue;
			String name = fileItem.getName();
			if (name == null || name.trim().equals("") || fileItem.getSize() == 0.0) continue;

			// 获取文件后缀
			String endTag = "";
			if (name.lastIndexOf(".") != -1) endTag = name.substring(name.lastIndexOf("."));
			// 生成文件名
			if (saveAs != null && !saveAs.trim().equals("")) name = saveAs;
			else name = fileItem.hashCode() + endTag;
			//if (saveAs != null && !saveAs.trim().equals("")) name = saveAs;

			//保存文件
			saveFile(fileItem, savePath + name);
			//拼接要返回的文件名
			if(fileNames.length()>0) fileNames.append("#");
			fileNames.append(name);
		}
		
		//要返回的字符
		String returnText = fileNames.toString();
		if(type.equals("json")){
			returnText = "{\"state\": \"SUCCESS\", \"url\": \""+ request.getContextPath() + "/" + path + fileNames +"\", \"title\": \""+ fileNames +"\", \"original\": \""+ fileNames +"\"}";
		}
		HttpUtil.outputText(returnText, response);
		return;
	}

	/**
	 * <p>保存文件到磁盘</p>
	 * @param fileItem apache fileItem
	 * @param savePath 保存路径
	 * @author 杨雪令
	 * @time 2016年3月29日下午5:33:29
	 * @version 1.0
	 */
	public void saveFile(final FileItem fileItem, final String savePath) {
		Thread saveThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					fileItem.write(new File(savePath));
				} catch (Exception e) {
					logger.error("save file error", e);
				}
			}
		});
		if (saveFileSync) saveThread.run();
		else saveThread.start();
	}

	/**
	 * 检测上传权限
	 * @autor yxl
	 */
	@SuppressWarnings("unchecked")
	private boolean checkSecurity(HttpServletRequest request, String path) {
		if (!checkSecurity) return true;
		// 获取可上传文件的文件夹
		HttpSession session = request.getSession();
		if (session == null) return false;
		if (session.getAttribute("upPath") == null) return false;
		// MAP{[文件夹,总大小单位M]....}
		Map<String, String> upPathMap = (Map<String, String>) session.getAttribute("upPath");
		for (Map.Entry<String, String> entry : upPathMap.entrySet()) {
			String configPath = URLUtil.getStandardUrl(entry.getKey().toString());// 可操作的文件夹
			// 如果要上传文件的文件夹属于可操作的文件夹
			if (path.contains(configPath)) return true;
		}
		return false;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

}