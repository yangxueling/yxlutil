package com.yntsoft.nlplatform.utils.doc;


/**
 * 转换接口
 * @author yxl
 */
public interface IConvert {
	
	/**
	 * 转换成功时调用
	 * @param tag 标识
	 * @param newFilePath 新文件路径
	 * @param cache 缓存，可以寄存东西在这里，回调时送回
	 * @autor yxl
	 */
	public void onSuccess(String tag, String oldFilePath, String newFilePath, Object cache);
	
	/**
	 * 转换失败时调用
	 * @param tag 标识
	 * @param cache 缓存，可以寄存东西在这里，回调时送回
	 * @autor yxl
	 */
	public void onError(String tag, String oldFilePath, Object cache);
}
