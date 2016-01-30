package com.yxlisv.util.redis.adapter;

import java.util.Set;

/**
 <pre>
      ┏┓　　　┏┓
    ┏┛┻━━━┛┻┓
    ┃　　　　　　　┃ 　
    ┃　　　━　　　┃
    ┃　┳┛　┗┳　┃
    ┃　　　　　　　┃
    ┃　　　┻　　　┃
    ┃　　　　　　　┃
    ┗━┓　　　┏━┛
    　　┃　　　┃神兽保佑
    　　┃　　　┃永无BUG！
    　　┃　　　┗━━━┓
    　　┃　　　　　　　┣┓
    　　┃　　　　　　　┏┛
    　　┗┓┓┏━┳┓┏┛
    　　　┃┫┫　┃┫┫
    　　　┗┻┛　┗┻┛
    </pre>
 *
 * Jedis适配器接口
 * @createTime 2015年10月26日 上午10:58:53 
 * @author 杨雪令
 */
public interface JedisAdapter {

	/**
	 * 设置缓存
	 * @param key 关键字
	 * @param value 可以把任何对象存放到缓存中
	 * @param timeout 超时时间，单位秒
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public String set(final String key, final Object value, int timeout) throws Exception;
	
	/**
	 * 设置缓存过期时间
	 * @param key 关键字
	 * @param timeout 超时时间，单位秒
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public void setTimeout(String string, int timeout) throws Exception;
	
	
	/**
	 * get缓存
	 * @param key 关键字
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public Object get(final String key) throws Exception;
	
	
	/**
	 * delete缓存
	 * @param key 关键字，可以使用*匹配
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public String delete(final String key) throws Exception;
	
	
	/**
	 * 根据条件查询key
	 * @param key 关键字，可以使用*匹配
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public Set<byte[]> getKeys(final String key) throws Exception;


	/**
	 * 清除redis所有缓存
	 * 
	 * @date 2015年10月23日 下午6:15:33
	 * @author yxl
	 */
	public void flushAll() throws Exception;
}