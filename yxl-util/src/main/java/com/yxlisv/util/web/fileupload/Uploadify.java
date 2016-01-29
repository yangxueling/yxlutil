package com.yxlisv.util.web.fileupload;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.string.StringUtil;
import com.yxlisv.util.web.HttpSessionCache;
import com.yxlisv.util.web.URLUtil;

public class Uploadify extends HttpServlet {

	/**
	 * 
	 * 文件上传处理,页面上传操作请参照apache 的 fileupload,需导入apache的fileupload、IO两个jar包
	 * 
	 */

	private static final long serialVersionUID = 1L;
	
	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 
	 * 实现多文件的同时上传
	 * 
	 * 传文件可以用uploadify插件，很nb，而且很好看，进度条什么的都不错
	 * $(function() {
        	var $uploadify = $("#uploadify");//上传控件
     		try{
     			$uploadify.uploadify({
      	    	   'auto'           : true,
      			   'multi'          : true,
      			   'queueID'        : 'queueDiv',//进度条的位置
      	    	   'swf'       		: '${pageContext.request.contextPath }/js/jquery/plugins/uploadify3.2/uploadify.swf',//上传flash插件
      			   'uploader'       : '${pageContext.request.contextPath }/uploadify?args=folder:images/user/${user_id}',//上传地址
      			   'simUploadLimit' : 1, //同时上传的文件数目
      			　 'fileSizeLimit'	: '2MB', //设置单个文件大小限制
      			   'buttonText'		: '本地上传',
      			   'height'         : 25,
      			   'width'          : 162,
      			   'queueSizeLimit' : 10,
      			   'fileTypeDesc'	: '支持格式: png,jpg,gif',
      			　 'fileTypeExts'	: '*.png; *.jpg; *.gif',//允许的格式
      		    	
      			   'onUploadSuccess' : function(file, data, response) {
      		            var picAddr = "/images/user/${user_id}/" + data;
      		            var fileSize = file.size;
      		            $("#userPicUploadForm").find("[name=picPath]").val(picAddr);
      		            $("#userPicUploadForm").find("[name=fileSize]").val(fileSize);
      		          	$("#userPicUploadForm").submit();
      		       },
      		       'onSelectError':function(file, errorCode, errorMsg){
      		            switch(errorCode) {
      		                case -100: alert("您最多可以同时上传"+$uploadify.uploadify('settings','queueSizeLimit') + "个文件"); break;
      		                case -110: alert("文件 \""+file.name+"\" 大小超出系统限制"+$uploadify.uploadify('settings','fileSizeLimit')); break;
      		                case -120: alert("文件 \""+file.name+"\" 大小异常"); break;
      		                case -130: alert("文件 \""+file.name+"\" 类型不正确"); break;
      		            }
      		            return false;
      		        },
      		       'onFallback':function(){
      		            alert("您未安装FLASH控件，无法上传图片！请安装FLASH控件后再试。");
      		        },
      			   'onUploadError' : function(file, errorCode, errorMsg, errorString) {
      				   if(errorString!="Cancelled") alert("文件 " + file.name + " 没有上传: " + errorString);
      		       }
      	    	});
     		} catch(e){}
     	});
	 * 
	 */

	@SuppressWarnings("unchecked")
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		//保存文件名
		String saveFileName = request.getParameter("saveFileName")!=null ? request.getParameter("saveFileName") : "";
		String folder = request.getParameter("folder")!=null ? request.getParameter("folder") : "uploadfiles/";
		folder = URLUtil.getStandardUrl(folder);
		
		request.setCharacterEncoding("utf-8");
		//检测上传权限
		if(checkSecurity(request, folder)){
			String savePath = this.getServletConfig().getServletContext().getRealPath("/") + folder + "/";
			
			File dir = new File(savePath);
			if (!dir.exists()) dir.mkdirs();

			DiskFileUpload upload = new DiskFileUpload();
			// DiskFileItemFactory fac = new DiskFileItemFactory();

			// ServletFileUpload upload = new ServletFileUpload(fac);

			// 对于向上传文件大小控制等fac.setSizeThreshold(4096)最多允许在内存中存放4096个字节 这类请查apache 的
			// fileupload例子

			// 获取多个上传文件
			List fileList = null;
			try {

				// fileList = upload.parseRequest(request);
				fileList = upload.parseRequest(request);
			} catch (FileUploadException ex) {
				System.out.println("没有上传文件");
				return;
			}

			// 遍历上传文件写入磁盘
			Iterator<FileItem> it = fileList.iterator();
			for(int i=0; it.hasNext();){
				FileItem item = it.next();
				if (!item.isFormField()) {
					String name = item.getName();
					if (name == null || name.trim().equals("")
							|| item.getSize() == 0.0)
						continue;
					
					//把文件名替换掉，怕中文掉链子，但保留文件后缀
					String endTag = "";
					if (name.lastIndexOf(".") != -1) endTag = name.substring(name.lastIndexOf("."));
					//随便找个唯一的字符串代替
					if (saveFileName != null && !saveFileName.trim().equals("")) name = saveFileName + endTag;
					else name = it.hashCode() + "" + item.hashCode() + endTag;
					
					File saveFile = new File(savePath + name);
					try {
						item.write(saveFile);
						if(i>0) response.getWriter().write("#" + name);
						else response.getWriter().write(name);
						i++;
					} catch (Exception e) {
						response.getWriter().write("0");
						e.printStackTrace();
					}
				}
			}
		} else {
			logger.warn("用户没有权限上传文件到：" + folder);
			response.getWriter().write("你没有权限上传文件到：" + folder);
		}
	}

	/**
	 * 检测上传权限
	 * @autor yxl
	 */
	private boolean checkSecurity(HttpServletRequest request, String folder) {
		//获取可上传文件的文件夹
		HttpSession session = request.getSession();
		if(request.getParameter("sessionId") != null) session = HttpSessionCache.getSession(request.getParameter("sessionId"));
		if(session == null) return false;
		if(session.getAttribute("upFodle") == null) return false;
		//MAP{[文件夹,总大小单位M]....}
		Map canUpFodleMap = (Map) session.getAttribute("upFodle");
		for(Iterator it=canUpFodleMap.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			String canUpFodle = URLUtil.getStandardUrl(entry.getKey().toString());//可操作的文件夹
			if(folder.contains(canUpFodle)){//如果要上传文件的文件夹属于可操作的文件夹
				return true;
			}
		}
		return false;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

}