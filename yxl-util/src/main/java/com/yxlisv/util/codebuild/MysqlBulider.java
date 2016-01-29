package com.yxlisv.util.codebuild;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxlisv.util.codebuild.control.ControlBuilder;
import com.yxlisv.util.codebuild.control.ControlPackage;
import com.yxlisv.util.codebuild.dao.DaoBuilder;
import com.yxlisv.util.codebuild.entry.Entry;
import com.yxlisv.util.codebuild.entry.EntryBuilder;
import com.yxlisv.util.codebuild.entry.EntryData;
import com.yxlisv.util.codebuild.entry.Property;
import com.yxlisv.util.codebuild.service.ServiceBuilder;
import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.file.FileUtil;

/**
 * <pre>
 * 分析sql文件，生成代码(mysqldump 5.0 生成的mysql文件)
 * 
 * sql文件格式：
 * 1、表和字段命名规则：不同单词用下划线隔开 如：“par_type”
 * 2、表名和字段用单引号或者是"`"包含起来
 * 3、每个字段结束后，必须在后面加逗号 ","
 * 4、参考下面的例子
 * 
	CREATE 	TABLE `aitem_sort` (
	  `id` int(11) NOT NULL auto_increment,
	  `details_name` varchar(20) NOT NULL COMMENT '明细信息',
	  `type` int(11) NOT NULL COMMENT '类型(0资产,1负债)',
	  `par_type` int(11) NOT NULL COMMENT '父类型(1流动资产,2长期投资,3固定资产,4无形资产及其他资产,5流动负债,6长期负债,7所有者（或股东）权益)',
	  PRIMARY KEY  (`id`)
	) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='资产负债信息';
	</pre>
 * @author john Local
 */
public class MysqlBulider {
	
	//包
	public static String entryPackage = "com.yxlisv.entry";
	public static String daoBasePackage = "com.yxlisv.dao";
	public static String daoPackage = "com.yxlisv.dao";
	public static String serviceBasePackage = "com.yxlisv.service";
	public static String servicePackage = "com.yxlisv.service";
	public static boolean hibernateLazy = true;
	
	//control 层的包
	public static List<ControlPackage> controlPackages;
	static{
		controlPackages = new ArrayList();
		ControlPackage controlPackage = new ControlPackage();
		controlPackage.packageName = "com.yxlisv.admin.control";
		controlPackage.pathQz = "admin";
		controlPackages.add(controlPackage);
	}
	
	/** 正则表达式 
	 * http://www.cnblogs.com/deerchao/archive/2006/08/24/zhengzhe30fengzhongjiaocheng.html
	 * 
	　　点的转义：. ==> u002E
	　　美元符号的转义：$ ==> u0024
	　　乘方符号的转义：^ ==> u005E
	　　左大括号的转义：{ ==> u007B
	　　左方括号的转义：[ ==> u005B
	　　左圆括号的转义：( ==> u0028
	　　竖线的转义：| ==> u007C
	　　右圆括号的转义：) ==> u0029
	　　星号的转义：* ==> u002A
	　　加号的转义：+ ==> u002B
	　　问号的转义：? ==> u003F
	　　反斜杠的转义： ==> u005C
		汉字：\u4e00-\u9fa5
	 */
	
	//创建表的正则表达式
	private static Pattern tbPt = Pattern.compile(
			
			"(\\s*create\\s+table\\s+[`']([a-zA-Z0-9_`]+)[`']\\s*\\u0028" +//CREATE 	TABLE `aitem_sort` (
				//"([a-z\\d\\u0028\\u0029^_']+,)+" + //筛选属性
				"(\\s*[`'][a-zA-Z0-9_`]+[`']\\s+[^;]*,)+" + //筛选属性
				"[^;]*comment\\s*=\\s*'([^']*)';" + //表注释
			")"
			);
	//表的属性（字段）正则表达式
	private static Pattern tbpPt = Pattern.compile(
			"(\\s*[`']([a-zA-Z0-9_`]+)[`']\\s+([a-zA-Z]+)\\u0028?([0-9]*)\\u0029?[a-zA-Z\\s_]*(comment\\s+'([^']*)')?\\s*,)" //筛选属性
			);
		
	private static Matcher tbMatcher;
	private static Matcher pMatcher;
	
	//表名
	private static String tableName = "";
	//表注释
	private static String tableCmt = "";
	
	/**
	 * 根据sql文件生成
	 * @param sqlFilePath sql文件路径
	 * @autor yxl
	 */
	public static void buildFromSqlFile(String sqlFilePath){
		
		String baseDir = FilePathUtil.getFileDir(sqlFilePath);
		baseDir += FilePathUtil.getFileName(sqlFilePath);
		
		//读取文件内容到list中
		String sqlStr = "";
		try {
			sqlStr = FileUtil.read(sqlFilePath);
			sqlStr = sqlStr.toLowerCase();
			System.out.println(sqlStr);
		} catch (IOException e) {
			e.printStackTrace();
		}
		tbMatcher = tbPt.matcher(sqlStr);
		
		//表的数量
		int count = 0;
		while (tbMatcher.find()){
			count++;
			
			//表名
			tableName = tbMatcher.group(2);
			//表注释
			tableCmt = tbMatcher.group(4);
			
			Entry entry = new Entry(tableName, tableCmt, entryPackage);
			
			System.out.print("("+ tbMatcher.groupCount() +")");
			System.out.print(tableName);
			System.out.print("[" + tableCmt + "]");
			
			
			//所有属性的字符串
			String pStr = tbMatcher.group(3);
			pMatcher = tbpPt.matcher(pStr);
			while(pMatcher.find()){
				String pName = pMatcher.group(2);//字段名称
				String pType = pMatcher.group(3);//字段类型
				String pSize = pMatcher.group(4);//字段长度
				String pCmt = pMatcher.group(6);//字段注释
				boolean notnull = false;
				if(pMatcher.group(0).toLowerCase().contains("not null")) notnull = true;
				System.out.print("("+ pName + "#" + pType + "#" + pSize + "#" + pCmt +")");
				entry.addProperty(pName, pType, pSize, pCmt, notnull);
			}
			
			System.out.println();
			EntryData.entryMap.put(entry.name, entry);
		}
		
		//确定表关系
		for(Map.Entry mentry : EntryData.entryMap.entrySet()){
			Entry entry = (Entry) mentry.getValue();
			for(Property property : entry.getPropertyList()){
				if(property.isMainClass()){//该字段为主控字段，那对应的表为附属表
					Entry propEntry = EntryData.entryMap.get(property.type);
					if(propEntry==null) continue;
					propEntry.isAffiliated = true;
				}
			}
		}
		
		//检查大文本字段
		for(Map.Entry mentry : EntryData.entryMap.entrySet()){
			Entry entry = (Entry) mentry.getValue();
			for(Property property : entry.getPropertyList()){
				property.checkBigText();
			}
		}
		
		//生成数据
		for(Map.Entry mentry : EntryData.entryMap.entrySet()){
			Entry entry = (Entry) mentry.getValue();
			//生成实体类
			EntryBuilder entryBuilder = new EntryBuilder(baseDir, entry);
			entryBuilder.hibernateAnnotation = true;
			entryBuilder.hibernateLazy = hibernateLazy;
			entryBuilder.build();
			
			if(entry.name.equals("TextSmall")) continue;
			if(entry.name.equals("TextMiddle")) continue;
			if(entry.name.equals("TextBig")) continue;
			if(entry.isAffiliated) continue;//附属表只需要一个实体类就够了
			
			//生成dao层
			DaoBuilder daoBuilder = new DaoBuilder(daoBasePackage, daoPackage, baseDir, entry);
			daoBuilder.build();
			
			//生成service层
			ServiceBuilder serviceBuilder = new ServiceBuilder(serviceBasePackage ,servicePackage, daoPackage, baseDir, entry);
			serviceBuilder.build();
			
			//生成control层
			ControlBuilder controlBuilder = new ControlBuilder(controlPackages, servicePackage, baseDir, entry);
			controlBuilder.build();
		}
		
		//生成多对一关系
		EntryBuilder.oneToManyBuilder.build(CodeBuilder.getFileDir(entryPackage, baseDir));
		
		System.out.println("\n\n=========> 一共为 " + count + " 张表生成了代码。");
		
		//生成父类
		//生成dao层
		DaoBuilder.buildParentClass(daoBasePackage, baseDir);
		ServiceBuilder.buildParentClass(serviceBasePackage, baseDir);
		ControlBuilder.buildParentClass(controlPackages, baseDir);
		
		//生成索引页面
		ControlBuilder.buildIndexPage(controlPackages, baseDir);
		
		//生成I18N文件
		EntryBuilder.buildI18n(baseDir);
		System.err.println(Property.waring);
	}
	

	/**
	 * @param args
	 * @autor yxl
	 */
	public static void main(String[] args) {
		String sqlFilePath = "F://codebuild/d3.sql";
		
		List<ControlPackage> packages = new ArrayList();
		
		//测试，生成4份control代码
		ControlPackage controlPackage = new ControlPackage();
		controlPackage.packageName = "com.yxlisv.admin.control";
		controlPackage.pathQz = "admin";
		packages.add(controlPackage);
		
		controlPackage = new ControlPackage();
		controlPackage.packageName = "com.yxlisv.teacher.control";
		controlPackage.pathQz = "teacher";
		packages.add(controlPackage);
		
		controlPackage = new ControlPackage();
		controlPackage.packageName = "com.yxlisv.student.control";
		controlPackage.pathQz = "student";
		packages.add(controlPackage);
		
		controlPackage = new ControlPackage();
		controlPackage.packageName = "com.yxlisv.control";
		packages.add(controlPackage);
		
		MysqlBulider.controlPackages = packages;
		MysqlBulider.buildFromSqlFile(sqlFilePath);
	}

}
