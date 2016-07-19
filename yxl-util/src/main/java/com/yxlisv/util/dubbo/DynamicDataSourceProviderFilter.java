package com.yxlisv.util.dubbo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.Filter;
import com.alibaba.dubbo.rpc.Invocation;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Result;
import com.alibaba.dubbo.rpc.RpcContext;
import com.alibaba.dubbo.rpc.RpcException;
import com.yxlisv.util.datasource.DataSourceBean;
import com.yxlisv.util.datasource.DynamicDataSource;

/**
 * <p>动态数据源过滤器</p>
 * <p>Dubbo提供者端</p>
 * @author 杨雪令
 * @time 2016年3月17日下午3:47:01
 * @version 1.0
 */
@Activate
public class DynamicDataSourceProviderFilter implements Filter {

	/** 定义一个全局的记录器，通过LoggerFactory获取  */
	public static Logger logger = LoggerFactory.getLogger(DynamicDataSourceProviderFilter.class);

	@Override
	public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {

		String dataSource = RpcContext.getContext().getAttachment("DSB");
		if (dataSource != null) DynamicDataSource.active(new DataSourceBean(dataSource));
		return invoker.invoke(invocation);// 执行业务逻辑
	}
}