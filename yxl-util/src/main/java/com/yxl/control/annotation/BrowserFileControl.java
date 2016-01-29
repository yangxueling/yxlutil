package com.yxl.control.annotation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.yxl.control.AbstractBaseControl;
import com.yxl.util.file.FileUtil;
import com.yxl.util.web.URLUtil;


/**
 * 文件流量控制器
 * @author john Local
 */
@Controller
@RequestMapping("/browser/file")
public class BrowserFileControl extends AbstractBaseControl{
	
	/**
	 * 跳转到"/"
	 * @autor yxl
	 */
	@RequestMapping(value = {""})
	public void index (String folder, HttpServletResponse response){
		
		transferArgsFromRequest(getRequest());//续传页面参数
		getRequest().setAttribute("checkSecurity", false);
		//检测文件浏览权限
		if(checkBrowserSecurity(folder)){
			if(checkUpSecurity(folder)) getRequest().setAttribute("checkSecurity", true);
			if(folder != null){
				try{
					List fileList = FileUtil.getFileList(getRequest().getRealPath(""), folder);
					getRequest().setAttribute("fileList", fileList);
				}catch(Exception e){
					System.out.println("文件浏览器获取文件失败");
				}
			}
		} else {
			getRequest().setAttribute("message", "ERROR 403! 你没有权限查看该目录!");
		}
		
		if(getRequest().getParameter("type")!=null && getRequest().getParameter("type").equals("ajax")) forward("/js/jquery/plugins/yxlAjax/plugins/filebrowser/browser.jsp", response);
		else forward("/ui/components/filebrowser/browser.jsp", response);
	}
	
	/**
	 * 检测文件浏览权限
	 * @autor yxl
	 */
	private boolean checkBrowserSecurity(String folder) {
		
		Map pathMap = new HashMap();
		if(getFromSession("browserFodle") != null) pathMap.putAll((Map) getFromSession("browserFodle"));
		if(getFromSession("upFodle") != null) pathMap.putAll((Map) getFromSession("upFodle"));//可以上传的目录也可以查看
		//MAP{[文件夹,说明]....}
		for(Iterator it=pathMap.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			String canBrowerFodle = URLUtil.getStandardUrl(entry.getKey().toString());//可操作的文件夹
			if(folder.contains(canBrowerFodle)){//如果要上传文件的文件夹属于可操作的文件夹
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 检测文件上传权限
	 * @autor yxl
	 */
	private boolean checkUpSecurity(String folder) {
		
		Map pathMap = new HashMap();
		if(getFromSession("upFodle") != null) pathMap.putAll((Map) getFromSession("upFodle"));//可以上传的目录也可以查看
		//MAP{[文件夹,说明]....}
		for(Iterator it=pathMap.entrySet().iterator(); it.hasNext();){
			Map.Entry entry = (Map.Entry) it.next();
			String canBrowerFodle = URLUtil.getStandardUrl(entry.getKey().toString());//可操作的文件夹
			if(folder.contains(canBrowerFodle)){//如果要上传文件的文件夹属于可操作的文件夹
				return true;
			}
		}
		return false;
	}
}