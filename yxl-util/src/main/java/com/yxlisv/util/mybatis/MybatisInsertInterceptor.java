package com.yxlisv.util.mybatis;

import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;

/**
 * Mybatis insert语句拦截
 * 自动注入主键
 * @createTime 2015年11月19日 上午9:38:58 
 * @author yxl
 */
@Intercepts({@Signature(type=Executor.class, method="update", args={MappedStatement.class, Object.class})})
public class MybatisInsertInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
		Object param = null;
		if(invocation.getArgs().length>1) param = invocation.getArgs()[1];
		// sql语句
		String sql = mappedStatement.getBoundSql(param).getSql().toUpperCase().trim();
		if (sql.startsWith("INSERT")) {//insert 语句，自动注入主键
			String primaryKey = null;//SystemUtil.generationPrimaryId();
			if(param instanceof Map){
				Map paramMap = (Map) param;
				paramMap.put("PRIMARY_KEY", primaryKey);
				Object changeRows = invocation.proceed();
				paramMap.put("changeRows", changeRows);
				return changeRows;
			}
		}
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {}
}