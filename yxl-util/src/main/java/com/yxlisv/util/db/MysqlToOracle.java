package com.yxlisv.util.db;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yxlisv.util.date.DateUtil;
import com.yxlisv.util.file.FileUtil;

/**
 * <p>Mysql脚本转换Oracle脚本</p>
 * @author 杨雪令
 * @time 2016年7月14日上午9:11:58
 * @version 1.0
 */
public class MysqlToOracle {
	
	// 定义一个全局的记录器，通过LoggerFactory获取
	protected static Logger logger = LoggerFactory.getLogger(MysqlToOracle.class);
	
	// 创建表的正则表达式
	private static Pattern tbPt = Pattern.compile("(?i)(\\s*create\\s+table\\s+[`']([a-zA-Z0-9_`]+)[`']\\s*\\u0028" + // CREATE
	// TABLE
	// `aitem_sort`
	// (
	// "([a-z\\d\\u0028\\u0029^_']+,)+" + //筛选属性
	"(\\s*[`'][a-zA-Z0-9_`]+[`']\\s+[^;]*,)+" + // 筛选属性
			"[^;]*comment\\s*=\\s*'([^']*)';" + // 表注释
			")");
	// 表的属性（字段）正则表达式
	private static Pattern tbpPt = Pattern.compile(
			"(?i)(\\s*[`']([a-zA-Z0-9_`]+)[`']\\s+([a-zA-Z]+)\\u0028?([0-9]*)\\u0029?[a-zA-Z\\s_'0-9]*(comment\\s+'([^']*)')?\\s*,)" // 筛选属性
	);
	
	public static void toOracle(String targetPath, String newPath){
		// 读取文件内容
		String sqlStr = "";
		try {
			sqlStr = FileUtil.read(targetPath);
		} catch (IOException e) {
			logger.error("读取Sql文件出错", e);
		}
		logger.info("读取Mysql脚本成功：" + targetPath);
		
		//替换非法字符
		Matcher specCharMatcher = Pattern.compile("'[^']*'").matcher(sqlStr);
		while(specCharMatcher.find()){
			String str = specCharMatcher.group(0);
			String newStr = str.replaceAll(";", ",");
			sqlStr = sqlStr.replace(str, newStr);
		}
		
		//提取注释
		StringBuilder sbCmt = new StringBuilder();
		// table create 语句 正则表达式解析器
		Matcher tbMatcher = tbPt.matcher(sqlStr);
		int tableCount = 0, columnCount = 0;
		while (tbMatcher.find()) {

			// 表名
			String tableName = tbMatcher.group(2);
			// 表注释
			String tableCmt = tbMatcher.group(4);
			sbCmt.append("COMMENT ON TABLE \""+ tableName +"\" IS '"+ tableCmt +"';\n");

			// 从Sql语句中解析属性
			String pStr = tbMatcher.group(3);
			Matcher pMatcher = tbpPt.matcher(pStr);
			while (pMatcher.find()) {
				String pName = pMatcher.group(2);// 字段名称
				String pCmt = pMatcher.group(6);// 字段注释
				sbCmt.append("COMMENT ON COLUMN \""+ tableName +"\".\""+ pName +"\" IS '"+ pCmt +"';\n");
				columnCount++;
			}
			tableCount++;
		}
		logger.info("共"+ tableCount +"张表，共"+ columnCount +"个字段");
		
		
		sqlStr = sqlStr.replaceAll("\\s(?i)TINYINT\\s*\\([0-9]{1,3}\\)\\s", " NUMBER(3,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)TINYINT\\s", " NUMBER(3,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)SMALLINT\\s*\\([0-9]{1,3}\\)\\s", " NUMBER(3,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)SMALLINT\\s", " NUMBER(3,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)INT\\s*\\([0-9]{1,3}\\)\\s", " NUMBER(10,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)INT\\s", " NUMBER(10,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)BIGINT\\s*\\([0-9]{1,3}\\)\\s", " NUMBER(20,0) ");
		sqlStr = sqlStr.replaceAll("\\s(?i)BIGINT\\s", " NUMBER(20,0) ");
		sqlStr = sqlStr.replaceAll(" (?i)VARCHAR\\(", " VARCHAR2\\(");
		sqlStr = sqlStr.replaceAll(" (?i)VARCHAR ", " VARCHAR2 ");
		sqlStr = sqlStr.replaceAll(" (?i)CHAR\\(", " VARCHAR2\\(");
		sqlStr = sqlStr.replaceAll(" (?i)CHAR ", " VARCHAR2 ");
		sqlStr = sqlStr.replaceAll(" (?i)DATETIME\\(", " DATE\\(");
		sqlStr = sqlStr.replaceAll(" (?i)DATETIME ", " DATE ");
		sqlStr = sqlStr.replaceAll(" (?i)TEXT\\(", " CLOB\\(");
		sqlStr = sqlStr.replaceAll(" (?i)TEXT ", " CLOB ");
		sqlStr = sqlStr.replaceAll(" (?i)LONGTEXT\\(", " CLOB\\(");
		sqlStr = sqlStr.replaceAll(" (?i)LONGTEXT ", " CLOB ");
		sqlStr = sqlStr.replaceAll(" (?i)DOUBLE\\(", " DOUBLE PRECISION\\(");
		sqlStr = sqlStr.replaceAll(" (?i)DOUBLE ", " DOUBLE PRECISION ");
		sqlStr = sqlStr.replaceAll("\\s*(?i)COMMENT\\s+'([^']*)'", "");
		sqlStr = sqlStr.replaceAll("\\)\\s*(?i)ENGINE[\\sa-zA-Z0-9=]+COMMENT\\s*=\\s*'([^']*)';", "\\);");
		sqlStr = sqlStr.replaceAll("`", "\"");
		sqlStr = sqlStr.replaceAll("DROP [^;]+;", "");
		sqlStr = sqlStr.replaceAll("SET [^;]+;", "");
		sqlStr = sqlStr.replaceAll("(PRIMARY KEY \\(\"[0-9a-zA-Z_]+\"\\))[^;]*", "$1\n)");
		sqlStr = sqlStr.replaceAll("/\\*[\\sa-zA-Z0-9.:-]+\\*/", "");
		
		logger.info("转换成功，Oracle脚本地址：" + newPath);
		String header = "/*\nThe script for oracle converted by mysql\n\nAuthor: yangxueling\nDate: "+ DateUtil.getTime() +" \n*/\n";
		try {
			FileUtil.write(newPath, header + sqlStr + "\n\n" + sbCmt);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		
		MysqlToOracle.toOracle("C:/Users/3yxl/Desktop/CRM.sql", "C:/Users/3yxl/Desktop/CRM_ORACLE.sql");
	}

}
