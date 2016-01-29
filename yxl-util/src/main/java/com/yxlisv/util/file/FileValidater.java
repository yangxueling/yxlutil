package com.yxlisv.util.file;

import org.springframework.web.multipart.MultipartFile;

import com.yxlisv.util.exception.MessageException;
import com.yxlisv.util.exception.SimpleMessageException;
import com.yxlisv.util.math.NumberUtil;


/**
 * 描述：文件验证器
 * 
 * @author: 杨雪令
 * @version 1.0
 */
public class FileValidater {
	
	/**
	 * 验证文件大小
	 * @param types 文件类型： jpg,gif,png......
	 * @throws MessageException 
	 * @autor yxl
	 * 2013-11-04
	 */
	public static void type(String types, MultipartFile multipartFile) throws MessageException{
		if(!notNull(multipartFile)) return;
		String fileName = multipartFile.getOriginalFilename().toLowerCase();
		String fileTypeStr = FilePathUtil.getSuffix(fileName).substring(1);
		types = types.toLowerCase();
		if(!types.contains(fileTypeStr)) {
			throw new SimpleMessageException("文件格式错误，只支持："+types, "file_type_error#" + types);
		}
	}

	/**
	 * 验证文件大小
	 * @param size 文件大小，单位M
	 * @throws MessageException 
	 * @autor yxl
	 * 2013-11-04
	 */
	public static void maxSize(double size, MultipartFile multipartFile) throws MessageException{
		if(!notNull(multipartFile)) return;
		double byteSize = size * 1024 * 1024;
		if(multipartFile.getSize() > byteSize) {
			String sizeStr = NumberUtil.getFomat2(size) + "M";
			if(size < 1) sizeStr = NumberUtil.getFomat(size*1024, "#") + "K";
			throw new SimpleMessageException("文件大小不能超过"+sizeStr, "file_to_big#" + size);
		}
	}
	
	
	/**
	 * 验证文件不能为空
	 * @autor yxl
	 * 2013-11-04
	 */
	public static boolean notNull(MultipartFile multipartFile) {
		if(multipartFile == null || multipartFile.isEmpty() || multipartFile.getSize()<1) return false;
		return true;
		
	}
}