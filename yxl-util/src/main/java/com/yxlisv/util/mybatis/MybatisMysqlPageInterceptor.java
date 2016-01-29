package com.yxlisv.util.mybatis;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.cache.TransactionalCacheManager;
import org.apache.ibatis.executor.CachingExecutor;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.statement.RoutingStatementHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.yxlisv.util.math.NumberUtil;
import com.yxlisv.util.reflect.ReflectionUtils;

/**
 * Mybatis mysql分页拦截器 如果mapper文件中的方法名包含page或者Page，则使用分页查询
 * 默认查询第一页，每页20条记录，可以在查询map中传入参数（pn/page：查询第几页，pageSize/rows：每页多少条数据）
 * 系统会改变查询条件map的值，map中包含：（查询传入的查询条件，rows：查询到的数据List<Map>，pn：查询第几页，pageSize：每页多少条数据，total：总行数，totalPage：总页数）
 * @createTime 2015年11月17日 下午4:10:39
 * @author 杨雪令
 */
@Intercepts({ @Signature(type = StatementHandler.class, method = "prepare", args = { Connection.class }), @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class MybatisMysqlPageInterceptor implements Interceptor {

	// 默认每页显示几条记录
	public static int default_page_size = 10;

	/**
	 * 是否是分页方法
	 * @param mappedId mybatis mappedId
	 */
	public boolean isPageMethod(String mappedId) {
		mappedId = mappedId.substring(mappedId.lastIndexOf("."));
		if (mappedId.contains("page") || mappedId.contains("Page")) return true;
		return false;
	}
	
	/**
	 * 是否是分页方法
	 * @param mappedId mybatis mappedId
	 */
	@SuppressWarnings("rawtypes")
	public boolean isPageMethod(Map paramMap) {
		if(paramMap.containsKey("not_page")) return false;
		return true;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object intercept(Invocation invocation) throws Throwable {
		//分页缓存处理
		//分页拦截器改变了分页传入的参数（map）中的内容，缓存也应该缓存分页传入的参数（map），并且缓存的key应该加上分页动态添加到sql的页码和每页大小参数
		if (invocation.getTarget() instanceof CachingExecutor) {
			MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
			String mappedId = ms.getId();
			if (!isPageMethod(mappedId)) return invocation.proceed();//如果不是分页，直接执行
			//查询参数是map才做处理
			Object parameterObject = invocation.getArgs()[1];
			if(parameterObject instanceof Map) {
				Map paramMap = (Map) parameterObject;
				if (!isPageMethod(paramMap)) return invocation.proceed();//如果不是分页，直接执行
				//获取CachingExecutor的参数
				Executor delegate = (Executor) ReflectionUtils.getFieldValue(invocation.getTarget(), "delegate");
				RowBounds rowBounds = (RowBounds) invocation.getArgs()[2];
				ResultHandler resultHandler = (ResultHandler) invocation.getArgs()[3];
				BoundSql boundSql = ms.getBoundSql(parameterObject);
				Cache cache = ms.getCache();
				CacheKey key = delegate.createCacheKey(ms, parameterObject, rowBounds, boundSql);
				if (cache != null) {
					if (ms.isUseCache() && resultHandler == null) {
						//给缓存key添加分页参数
						CacheKey cacheKey = key;
						// 当前页码
						int pn = 1;
						if (paramMap.containsKey("pn")) pn = NumberUtil.parseInt((Object) paramMap.get("pn"));
						else if (paramMap.containsKey("page")) pn = NumberUtil.parseInt((Object) paramMap.get("page"));
						if (pn < 1) pn = 1;
						cacheKey.update("pn-"+pn);
						// 每页显示几条记录
						int pageSize = default_page_size;
						if (paramMap.containsKey("pageSize")) pageSize = NumberUtil.parseInt((Object) paramMap.get("pageSize"));
						else if (paramMap.containsKey("rows")) pageSize = NumberUtil.parseInt((Object) paramMap.get("rows"));
						cacheKey.update("pageSize-"+pageSize);
						//缓存管理器
						TransactionalCacheManager tcm = (TransactionalCacheManager) ReflectionUtils.getFieldValue(invocation.getTarget(), "tcm");
						Object cacheObj = tcm.getObject(cache, cacheKey);
						if (cacheObj == null) {
							cacheObj = delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql);
							paramMap.put("rows", cacheObj);
							tcm.putObject(cache, cacheKey, paramMap);
						} else{
							paramMap.putAll((Map) cacheObj);
						}
						return new ArrayList();
					}
				}
				//如果没有配置使用缓存，直接查数据，设置到查询参数
				paramMap.put("rows", delegate.query(ms, parameterObject, rowBounds, resultHandler, key, boundSql));
				return new ArrayList();
			}
			return invocation.proceed();
		}
		
		//没有开启缓存时执行
		if (invocation.getTarget() instanceof Executor) {
			if (invocation.getArgs()[0] == null || !(invocation.getArgs()[0] instanceof MappedStatement)) return invocation.proceed();
			MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];
			String mappedId = mappedStatement.getId();
			if (isPageMethod(mappedId)) {
				Object param = null;
				if (invocation.getArgs().length > 1) param = invocation.getArgs()[1];
				if (param != null && param instanceof Map) {
					Map paramMap = (Map) param;
					if (!isPageMethod(paramMap)) return invocation.proceed();//如果不是分页，直接执行
					paramMap.put("rows", invocation.proceed());
					return new ArrayList();
				}
			}
			return invocation.proceed();
		}

		//处理分页数据 拦截StatementHandler类
		if (invocation.getTarget() instanceof StatementHandler) {
			RoutingStatementHandler handler = (RoutingStatementHandler) invocation.getTarget();
			StatementHandler statementHandler = (StatementHandler) ReflectionUtils.getFieldValue(handler, "delegate");
			MappedStatement mappedStatement = (MappedStatement) ReflectionUtils.getFieldValue(statementHandler, "mappedStatement");
			String mappedId = mappedStatement.getId();
			// 如果mapper文件中的方法名包含page或者Page，则使用分页查询
			if (isPageMethod(mappedId)) {

				// 查询参数
				Object param = statementHandler.getParameterHandler().getParameterObject();
				if (param != null && param instanceof Map) {
					Map paramMap = (Map) param;
					if (!isPageMethod(paramMap)) return invocation.proceed();//如果不是分页，直接执行
					// 查询总行数
					Connection connection = (Connection) invocation.getArgs()[0];
					int total = searchTotalRow(mappedStatement, connection, paramMap);
					paramMap.put("total", total);

					// 每页显示几条记录
					int pageSize = default_page_size;
					if (paramMap.containsKey("pageSize")) pageSize = NumberUtil.parseInt((Object) paramMap.get("pageSize"));
					else if (paramMap.containsKey("rows")) pageSize = NumberUtil.parseInt((Object) paramMap.get("rows"));
					if(pageSize<1) pageSize = default_page_size;
					paramMap.put("pageSize", pageSize);

					// 总页数
					int totalPage = 1;
					if (total % pageSize == 0) totalPage = (int) (total / pageSize);
					else totalPage = (int) (total / pageSize + 1);
					paramMap.put("totalPage", totalPage);

					// 当前页码
					int pn = 1;
					if (paramMap.containsKey("pn")) pn = NumberUtil.parseInt((Object) paramMap.get("pn"));
					else if (paramMap.containsKey("page")) pn = NumberUtil.parseInt((Object) paramMap.get("page"));
					if (pn < 1) pn = 1;
					else if (pn > totalPage) pn = totalPage;
					paramMap.put("pn", pn);

					// 查询数据
					if (total > 0) {
						int startRow = (pn - 1) * pageSize;
						if (startRow < 0) startRow = 0;
						MetaObject metaStatementHandler = mappedStatement.getConfiguration().newMetaObject(statementHandler);
						metaStatementHandler.setValue("boundSql.sql", mappedStatement.getBoundSql(paramMap).getSql() + " limit " + startRow + "," + pageSize);
						//下面的方式有坑，会影响非分页sql
						//metaStatementHandler.setValue("rowBounds.offset", startRow);
						//metaStatementHandler.setValue("rowBounds.limit", pageSize);
					}
				}
			}
			return invocation.proceed();
		}
		
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {}

	/**
	 * 获取查询数量的sql
	 * 
	 * @param sql SQL语句
	 * @autor yxl
	 */
	protected static String getCountSql(String sql) {
		sql = sql.replaceAll("\\s+", " ");
		int fromIndex = -1;//FROM字符的位置
		int bracketStartIndex = -1;//括号开始位置(
		int bracketEndIndex = -1;//括号结束位置)
		while(true){
			if((fromIndex=sql.toUpperCase().indexOf(" FROM "))!=-1) sql = sql.substring(fromIndex+5);
			bracketStartIndex = sql.indexOf("(");
			bracketEndIndex = sql.indexOf(")");
			if(bracketEndIndex>-1 && bracketStartIndex==-1) continue;//没有开始括号
			if(bracketEndIndex<bracketStartIndex) continue;//结束括号在前面
			break;
		}
		return "SELECT count(*) count FROM" + sql;
	}

	/**
	 * 查询总记录行数
	 * 
	 * @param mappedStatement  mappedStatement对象
	 * @param connection  数据库连接
	 * @param paramMap  查询参数
	 * @param sql  sql语句
	 * @date 2015年11月17日 下午4:22:50
	 * @author yxl
	 * @throws SQLException
	 */
	private int searchTotalRow(MappedStatement mappedStatement, Connection connection, Map paramMap) throws SQLException {
		BoundSql boundSql = mappedStatement.getBoundSql(paramMap);
		String countSql = getCountSql(boundSql.getSql());
		PreparedStatement pstmt = null;
		BoundSql countBoundSql = new BoundSql(mappedStatement.getConfiguration(), countSql, boundSql.getParameterMappings(), paramMap);
		ParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, paramMap, countBoundSql);
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(countSql);
			parameterHandler.setParameters(pstmt);
			rs = pstmt.executeQuery();
			if (rs.next()) return rs.getInt("count");
		} finally {
			if (rs != null) rs.close();
			if (pstmt != null) pstmt.close();
		}
		return 0;
	}
}