package com.yxlisv.util.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.yxlisv.util.math.NumberUtil;

/**
 * <p>分页工具类</p>
 * @author 杨雪令
 * @time 2016年3月9日下午12:40:44
 * @version 1.0
 */
public class Page implements Serializable {

	private static final long serialVersionUID = 3333332222222555666L;

	// 默认每页显示几条记录
	public static int default_page_size = 10;
	// 初始化pageNumber
	public static int default_page_pn = -999;
	// 当前页码(pageNumber)
	private int pn = default_page_pn;
	// 每页显示几条记录
	private int pageSize = default_page_size;
	// 总页数
	private int totalPage;
	// 总共有多少条记录
	private long totalRows;
	// 查询结果
	private Object result;
	// 模糊查询（true/on为使用模糊查询）
	private String fuzzy = null;
	// 是否获取记录总数，如果不需要获取记录总数，请设置此参数为false，可以大幅提高查询效率
	private boolean getTotalRows = true;

	/**
	 * <p>是否为模糊查询</p>
	 * @return boolean true/false 
	 * @author 杨雪令
	 * @time 2016年3月15日上午10:03:25
	 * @version 1.0
	 */
	public boolean getIsFuzzy() {
		return isFuzzy();
	}

	/**
	 * <p>是否为模糊查询</p>
	 * @return boolean true/false 
	 * @author 杨雪令
	 * @time 2016年3月15日上午10:03:25
	 * @version 1.0
	 */
	public boolean isFuzzy() {
		if (fuzzy == null) return false;
		if (fuzzy.equals("true")) return true;
		if (fuzzy.equals("on")) return true;
		return false;
	}

	/**
	 * <p>获取模糊查询值</p>
	 * @param fuzzy （true/on为使用模糊查询） 
	 * @author 杨雪令
	 * @time 2016年3月15日上午10:03:56
	 * @version 1.0
	 */
	public String getFuzzy() {
		return fuzzy;
	}

	/**
	 * <p>设置模糊查询</p>
	 * @param fuzzy （true/on为使用模糊查询） 
	 * @author 杨雪令
	 * @time 2016年3月15日上午10:03:56
	 * @version 1.0
	 */
	public void setFuzzy(String fuzzy) {
		this.fuzzy = fuzzy;
	}

	/**
	 * 查询数据的第一行
	 * @author yxl
	 */
	public int getStartRow() {
		return pageSize * (pn - 1);
	}

	/**当前页码*/
	public int getPn() {

		return pn;
	}

	/**当前页码*/
	public void setPn(Object pn) {

		this.pn = NumberUtil.parseInt(pn);
	}

	/**每页显示几条记录*/
	public int getPageSize() {

		return pageSize;
	}

	/**每页显示几条记录*/
	public void setPageSize(int pageSize) {

		this.pageSize = pageSize;
	}

	/**总页数*/
	public int getTotalPage() {

		return totalPage;
	}

	/**总页数*/
	public void setTotalPage(int totalPage) {

		this.totalPage = totalPage;
	}

	/**总共有多少条记录*/
	public long getTotalRows() {

		return totalRows;
	}

	/**
	 * 设置总共有多少条记录
	 * 计算总页数，页码越界处理
	 */
	public void setTotalRows(long dataCounts) {

		this.totalRows = dataCounts;

		// 计算总页数
		totalPage = 0;
		if (dataCounts % pageSize == 0) totalPage = (int) (dataCounts / pageSize);
		else totalPage = (int) (dataCounts / pageSize + 1);

		// 验证当前也是否合法
		if (totalPage < 1) totalPage = 1;
		if (pn > totalPage) pn = totalPage;
		else if (pn < 1) pn = 1;
	}

	/**查询结果 */
	public Object getResult() {

		return result;
	}

	/**查询结果 */
	public void setResult(Object result) {

		this.result = result;
	}

	/**
	 * <p>重新设置结果</p>
	 * <p>根据当前页码和每页显示数量筛选结果集</p>
	 * @param result 结果集，List对象 
	 * @author 杨雪令
	 * @time 2016年3月14日下午2:18:11
	 * @version 1.0
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void resetResult(Object result) {

		List newList = new ArrayList();
		if (result instanceof List) {
			List rList = (List) result;
			this.setTotalRows(rList.size());
			// 如果记录数量大于每页显示数量，则要删选，针对手动设置结果的情况
			for (int i = this.getStartRow(); i < this.getPageSize() * this.pn; i++) {
				if (i >= rList.size()) break;
				newList.add(rList.get(i));
			}
		}
		this.setResult(newList);
	}

	/**
	 * 是否存在上一页
	 * @autor yxl
	 * 2015-3-2
	 */
	public boolean getHasPrevPage() {
		if (pn <= 1) return false;
		return true;
	}

	/**
	 * 是否存在下一页
	 * @autor yxl
	 * 2015-3-2
	 */
	public boolean getHasNextPage() {
		if (pn >= totalPage) return false;
		return true;
	}

	/**
	 * <p>是否获取记录总数</p>
	 * <p>如果不需要获取记录总数，请设置此参数为false，可以大幅提高查询效率</p>
	 * @return boolean 是否获取记录总数
	 * @author 杨雪令
	 * @time 2016年3月18日下午3:54:34
	 * @version 1.0
	 */
	public boolean isGetTotalRows() {
		return getTotalRows;
	}

	/**
	 * <p>设置是否获取记录总数</p>
	 * <p>如果不需要获取记录总数，请设置此参数为false，可以大幅提高查询效率</p>
	 * @param getTotalRows 是否获取记录总数
	 * @author 杨雪令
	 * @time 2016年3月18日下午3:54:50
	 * @version 1.0
	 */
	public void setGetTotalRows(boolean getTotalRows) {
		this.getTotalRows = getTotalRows;
	}

	/**
	 * <p>排序相关代码</p>
	 * @author 杨雪令
	 * @time 2016年4月21日下午3:02:33
	 * @version 1.0
	 */
	// 排序map（字段名:asc/desc）
	private Map<String, String> orderMap = new LinkedHashMap<String, String>();

	/**
	 * <p>获取排序Map</p>
	 * @return Map<String,String> 排序map（字段名:asc/desc）
	 * @author 杨雪令
	 * @time 2016年3月9日下午4:37:25
	 * @version 1.0
	 */
	public Map<String, String> getOrderMap() {
		return orderMap;
	}

	/**
	 * <p>添加排序字段（升序）</p>
	 * @param name 字段名称
	 * @author 杨雪令
	 * @time 2016年3月9日下午4:33:41
	 * @version 1.0
	 */
	public void addOrderByAsc(String name) {
		orderMap.put(name, "asc");
	}

	/**
	 * <p>添加排序字段（降序）</p>
	 * @param name 字段名称
	 * @author 杨雪令
	 * @time 2016年3月9日下午4:33:41
	 * @version 1.0
	 */
	public void addOrderByDesc(String name) {
		orderMap.put(name, "desc");
	}

	// 排序字符串 orderBy=字段名[-]，如：orderBy=id-，默认升序，降序在字段名称后面加：-
	private String orderBy = null;

	/**
	 * <p>设置排序条件</p>
	 * <p>自动注入排序条件</p>
	 * @param name 排序字段：orderBy=字段名[-/+]，如：orderBy=id-，默认升序，降序在字段名称后面加：-
	 * @author 杨雪令
	 * @time 2016年3月9日下午4:33:41
	 * @version 1.0
	 */
	public void setOrderBy(String name) {
		if (name == null || name.length() == 0) {
			orderBy = "";
			return;
		}
		name = name.trim();
		orderBy = name;
		if (name.endsWith("-")) {
			name = name.substring(0, name.length() - 1);
			addOrderByDesc(name);
		} else if (name.endsWith("+")) {
			name = name.substring(0, name.length() - 1);
			addOrderByAsc(name);
		} else {
			addOrderByAsc(name);
		}
	}

	/**
	 * <p>获取排序字符串</p>
	 * @return String 排序字符串
	 * @author 杨雪令
	 * @time 2016年4月21日下午3:27:15
	 * @version 1.0
	 */
	public String getOrderBy() {
		return orderBy;
	}
}