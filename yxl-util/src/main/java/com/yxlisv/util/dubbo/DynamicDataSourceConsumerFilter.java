package com.yxlisv.util.dubbo;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.yxlisv.util.context.WebContext;
import com.yxlisv.util.datasource.DataSourceBean;
import com.yxlisv.util.datasource.DynamicDataSource;

/**
 * <p>动态数据源过滤器</p>
 * <p>Dubbo消费者端</p>
 * @author 杨雪令
 * @time 2016年3月17日下午3:47:01
 * @version 1.0
 */
@Activate
public class DynamicDataSourceConsumerFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	public static Logger logger = LoggerFactory.getLogger(DynamicDataSourceConsumerFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
		
		String DSB = null;
		if (WebContext.getServletRequest() != null) {
			HttpSession session = WebContext.getServletRequest().getSession();
			// 如果用户有自定义数据源，那么激活用户的数据源
			if (session.getAttribute(DynamicDataSource.DATA_SOURCE_HTTP_SESSION_KEY) != null) {
				DataSourceBean dataSourceBean = (DataSourceBean) session.getAttribute(DynamicDataSource.DATA_SOURCE_HTTP_SESSION_KEY);
				DSB = dataSourceBean.info();
			}
		} else if(DynamicDataSource.getCurrentDataSourceBean() != null){
			DataSourceBean dataSourceBean = DynamicDataSource.getCurrentDataSourceBean();
			DSB = dataSourceBean.info();
		}
		if(DSB != null) RpcContext.getContext().setAttachment("DSB", DSB);
		return invoker.invoke(invocation);// 执行业务逻辑
	}
}