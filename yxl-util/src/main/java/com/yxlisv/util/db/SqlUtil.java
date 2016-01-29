package com.yxlisv.util.db;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxlisv.util.file.FileUtil;

/**
 *     ┏┓　　　┏┓
 *   ┏┛┻━━━┛┻┓
 *   ┃　　　　　　　┃ 　
 *   ┃　　　━　　　┃
 *   ┃　┳┛　┗┳　┃
 *   ┃　　　　　　　┃
 *   ┃　　　┻　　　┃
 *   ┃　　　　　　　┃
 *   ┗━┓　　　┏━┛
 *   　　┃　　　┃神兽保佑
 *   　　┃　　　┃永无BUG！
 *   　　┃　　　┗━━━┓
 *   　　┃　　　　　　　┣┓
 *   　　┃　　　　　　　┏┛
 *   　　┗┓┓┏━┳┓┏┛
 *   　　　┃┫┫　┃┫┫
 *   　　　┗┻┛　┗┻┛
 *
 * Sql工具类
 * @author yxl
 */
public class SqlUtil {
	
	/** 子查询 正则表达式 */
	private final static Pattern sqlChildPattern = Pattern.compile("\\(([\\s\\S]*)\\)");
	/** from 正则表达式 */
	private final static Pattern sqlFromPattern = Pattern.compile("(?i)FROM\\s+([\\s,a-zA-Z0-9_.]+)");
	/** 表名正则表达式 */
	private final static Pattern tableNamePattern = Pattern.compile("([a-zA-Z0-9_]+)[\\s,]*");
	/** sql关键字 */
	private final static String sqlKeywords = "FROM JOIN RIGHT LEFT ON AS AND OR WHERE";
	
	/**
	 * 解析sql语句获取表名
	 * @param sql sql语句
	 * @return	.table1.table2.table3....
	 */
	public final static String getTableNames(String sql){
		return getTableNames(sql, "");
	}
	
	/**
	 * 格式化子查询sql
	 * @param sql语句
	 * @date 2015年12月15日 下午5:03:47 
	 * @author yxl
	 */
	public static String formatChildSql(String sql){
		if(!sql.contains("(") || !sql.contains(")")) return sql;
		//匹配子查询
		Matcher sqlChildMatcher = sqlChildPattern.matcher(sql);
		if (sqlChildMatcher.find()){
			String childSql = sqlChildMatcher.group(1);
			sql = sql.replace(childSql, "");
			sql = sql.replaceAll("(?i)\\(\\s*\\)\\s+FROM\\s", " FROM ");
			sql = sql.replaceAll("\\(\\s*\\)\\s+[a-zA-Z0-9_]+", "");
			sql = sql.replaceAll("\\(\\s*\\)", "");
			sql = childSql + " | " + sql;
			if(sql.contains("(") && sql.contains(")")) return formatChildSql(sql);
		}
		return sql;
	}
	
	
	/**
	 * 解析sql语句获取表名
	 * @param sql sql语句
	 * @param prefix 表的前缀，返回表名添加前缀
	 * @return	.table1.table2.table3....
	 */
	public final static String getTableNames(String sql, String prefix){
		sql = sql.replaceAll("(?i)\\sAS\\s+[a-zA-Z0-9_]+", " ");
		sql = sql.replaceAll("`", "");
		sql = sql.replaceAll("(?i)\\sUNION\\s", " | ");
		sql = sql.replaceAll("\\([a-zA-Z0-9_.]+\\)+", " ");
		sql = formatChildSql(sql);
		StringBuffer tableNames = new StringBuffer(".");
		sql = sql.replaceAll("[a-zA-Z0-9_.]+\\s*=\\s*[a-zA-Z0-9_.]+", "").replaceAll("(?i)\\sFROM\\s", " |FROM ").replaceAll("(?i)\\sWHERE\\s", " | ").replaceAll("(?i)\\sAND\\s", " | ").replaceAll("(?i)\\sOR\\s", " | ").replaceAll("(?i)\\sGROUP\\s", " | ").replaceAll("(?i)\\sORDER\\s", " | ");
		Matcher sqlFromMatcher = sqlFromPattern.matcher(sql);
		while (sqlFromMatcher.find()){
			String fromStr = sqlFromMatcher.group(1);
			fromStr = fromStr.replaceAll("(?i)\\sON\\s", " ").replaceAll("(?i)\\sLEFT\\s+[OUT]{0,3}\\s*JOIN\\s", ",").replaceAll("(?i)\\sRIGHT\\s+[OUT]{0,3}\\s*JOIN\\s", ",").replaceAll("(?i)\\sINNER\\s+[OUT]{0,3}\\s*JOIN\\s", ",").replaceAll("(?i)\\sFULL\\s+[OUT]{0,3}\\s*JOIN\\s", ",").replaceAll("(?i)\\sJOIN\\s", ",").replaceAll("\\s*,\\s*", ",").trim();
			fromStr = fromStr.replaceAll("\\s[a-zA-Z0-9_]+", "");
			Matcher tableNameMatcher = tableNamePattern.matcher(fromStr);
			while (tableNameMatcher.find()){
				String tableName = tableNameMatcher.group(1);
				if(sqlKeywords.contains(tableName.toUpperCase())) continue;
				if(prefix.length()>0 && !tableName.toUpperCase().startsWith(prefix.toUpperCase())) tableName = prefix + tableName;//给表添加前缀
				if(!tableNames.toString().contains("." + tableName + ".")) tableNames.append(tableName).append(".");
			}
		}
		return tableNames.toString();
	}

	public static void main(String[] args) {
		String sql = "select *,(select id from J_t1_group jt1,T_t2 right out join j_t3 jt3 on jt2.id=jt3.id left join on j_t4 jt4 on jt4.id=jt3.id from tt1, J_t1  , tt2 as t2 , tt3 , tt4 left join tt5 on t2.ds=sd where 1=1 and f2.name=f3.name or f4.t=f5.t and id in (select id from w_t1,w_t2 right out join w_t3)";
		sql = "SELECT * FROM ( SELECT tsd.*, tv.VIDEO_NAME, tv.VIDEO_TIMES, tv.VIDEO_CONTENT, tv.CREATE_BY_NAME, tv.IMG_URL_X, tv.CREATE_TIME, uh.HISTORY_ID FROM ( SELECT t.SPECIALEDITION_ID, t.USER_ID, t.VIDEO_ID FROM t_specialedition_detail t  WHERE t.SPECIALEDITION_ID = '676680952608722944'  AND t.USER_ID = '672319908423667712' ) tsd   LEFT JOIN      t_video tv        ON tsd.VIDEO_ID = tv.VIDEO_ID    LEFT JOIN   t_user_history uh      ON tv.VIDEO_ID=uh.VEDIO_ID      AND uh.USER_ID = '672319908423667712'      ) tt  order by   tt.VIDEO_TIMES DESC";
		try {
			sql = FileUtil.read("f:/sql.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}
		long start = System.currentTimeMillis();
		String tableNames = SqlUtil.getTableNames(sql, "T_");
		System.out.println(tableNames);
		System.out.println(System.currentTimeMillis()-start);
	}
}