package com.yxl.util;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 * @author yxl
 * @version 1.0
 * class createTime 2012-7-14
 */
public class Page {
	
	//默认每页显示几条记录
	public static int default_page_size = 20;

	//当前页码(pageNumber)
	private int pn;
	//每页显示几条记录
	private int pageSize = default_page_size;
	//总页数
	private int totalPage;
	//总共有多少条记录
	private long totalRows;
	
	//查询结果 
	private Object result;
	
	/**
	 * 查询数据的第一行
	 * @author yxl
	 */
	public int getStartRow(){
		return pageSize * (pn - 1);
	}
	
	
	/**当前页码*/
	public int getPn() {
	
		return pn;
	}

	/**当前页码*/
	public void setPn(int pn) {
	
		this.pn = pn;
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
		if (dataCounts % pageSize == 0)
			totalPage = (int) (dataCounts / pageSize);
		else
			totalPage = (int) (dataCounts / pageSize + 1);

		// 验证当前也是否合法
		if (totalPage < 1)
			totalPage = 1;
		if (pn > totalPage)
			pn = totalPage;
		else if (pn < 1)
			pn = 1;
	}

	/**查询结果 */
	public Object getResult() {
	
		return result;
	}

	/**查询结果 */
	public void setResult(Object result) {
	
		this.result = result;
	}
	
	
	/**重新设置结果，可以手动 */
	public void resetResult(Object result){
		
		List newList = new ArrayList();
		if(result instanceof List){
			List rList = (List) result;
			this.setTotalRows(rList.size());
			//如果记录数量大于每页显示数量，则要删选，针对手动设置结果的情况
			for (int i=this.getStartRow(); i<this.getPageSize()*this.pn; i++) {
				if(i >= rList.size()) break;
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
	public boolean getHasPrevPage(){
		if(pn<=1) return false;
		return true;
	} 
	
	/**
	 * 是否存在下一页
	 * @autor yxl
	 * 2015-3-2
	 */
	public boolean getHasNextPage(){
		if(pn>=totalPage) return false;
		return true;
	} 
}
