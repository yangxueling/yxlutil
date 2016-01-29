package com.yxl.util.string;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.yxl.util.math.NumberUtil;

/**
 * string 工具类
 * @author john Local
 *
 */
public class StringUtil {

	/**
	 * 字符串部分字母大写
	 * @param str 字符串
	 * @param begin 开始下标
	 * @param end 结束下标
	 * @autor yxl
	 */
	public static String toUpper(String str, int begin, int end){
		if(begin<0 || begin>end || end>str.length()) return str;
		String targetStr = str.substring(begin, end);
		targetStr = targetStr.toUpperCase();
		
		return str.substring(0, begin) + targetStr + str.substring(end);
	}
	
	
	/**
	 * 字符串部分字母小写
	 * @param str 字符串
	 * @param begin 开始下标
	 * @param end 结束下标
	 * @autor yxl
	 */
	public static String toLower(String str, int begin, int end){
		if(begin<0 || begin>end || end>str.length()) return str;
		String targetStr = str.substring(begin, end);
		targetStr = targetStr.toLowerCase();
		
		return str.substring(0, begin) + targetStr + str.substring(end);
	}
	
	/**
	 * 字符串首部分字母大写
	 * @param str 字符串
	 * @param begin 开始下标
	 * @param end 结束下标
	 * @autor yxl
	 */
	public static String toUpper4FirstWord(String str){
		return StringUtil.toUpper(str, 0, 1);
	}
	
	/**
	 * 字符串首部分字母小写
	 * @param str 字符串
	 * @param begin 开始下标
	 * @param end 结束下标
	 * @autor yxl
	 */
	public static String toLower4FirstWord(String str){
		return StringUtil.toLower(str, 0, 1);
	}
	
	/**
	 * key 后面的 count 个字母大写,并删掉key
	 * @param str 字符串
	 * @param key 关键key
	 * @autor yxl
	 */
	public static String toUpperBh(String str, String key, int count){
		
		while(str.indexOf(key) > -1){
			str = StringUtil.toUpper(str, str.indexOf(key)+1, str.indexOf(key)+2);
			str = str.replaceFirst(key, "");
		}
		return str;
	}
	
	
	//java脚本标签和样式标签正则表达式
	private static Pattern jbStylePt = Pattern.compile("(<(s|S){1}(c|C){1}(r|R){1}(i|I){1}(p|P){1}(t|T){1}[^>]*>[\\s\\S]*</(s|S){1}(c|C){1}(r|R){1}(i|I){1}(p|P){1}(t|T){1}>)*(<style[^>]*>[\\s\\S]*</style>)*");
	//HTML标签正则表达式
	private static Pattern htmlCodePt = Pattern.compile("<[^>]*>");
	private static Matcher htmlCodeMatcher;
	/**
	 * 清理HTML标签
	 * @param htmlCode
	 * @autor yxl
	 */
	public static String clearHtml(String htmlCode){
		htmlCodeMatcher = jbStylePt.matcher(htmlCode);
		htmlCode = htmlCodeMatcher.replaceAll("");
		htmlCodeMatcher = htmlCodePt.matcher(htmlCode);
		htmlCode = htmlCodeMatcher.replaceAll("");
		return htmlCode;
	}
	
	
	//HTML标签正则表达式
	private static Pattern cssBgImagePt = Pattern.compile("(url\\s*\\(\\s*[\"'][^\"\\(\\)]*[\"']\\s*\\))");
	private static Matcher cssBgImageMatcher;
		
	/**
	 * 修复html代码
	 * @param htmlCode html代码
	 * @return 格式良好的html代码
	 * @autor yxl
	 */
	public static String repairHtml(String htmlCode){
		
		if(htmlCode != null){
			//1.style=""; 如果里面包含背景图片，容易出现双引号导致解析错误 style="url("**.png"); background-repeat: repeat;"
			cssBgImageMatcher = cssBgImagePt.matcher(htmlCode);
			while(cssBgImageMatcher.find()){
				//System.out.println(cssBgImageMatcher.group(1));
				String cssBgUrl = cssBgImageMatcher.group(1).replaceAll("\"", "").replaceAll("'", "");
				htmlCode = cssBgImageMatcher.replaceFirst(cssBgUrl);
				cssBgImageMatcher = cssBgImagePt.matcher(htmlCode);
			}
		}
		
		return htmlCode;
	}
	
	
	/**
	 * 根据一个key查找一行数据
	 * @param str 字符串
	 * @param key key
	 * @autor yxl
	 */
	public static String findLine(String str, String key) {
		
		if(str.indexOf(key) != -1){
			int keyIndexStart = str.indexOf(key);//key的开始位置
			int keyIndexEnd = str.indexOf(key) + key.length();//key的结束位置
			//key把字符串分成2部分
			String str1 = str.substring(0, keyIndexStart);
			String str2 = str.substring(keyIndexEnd, str.length());
			
			int lineStart = 0;//key所在行的开始位置
			int lineEnd = str.length();//key所在行的结束位置
			//计算key所在行的开始位置
			if(str1.indexOf("\n") != -1) lineStart = str1.lastIndexOf("\n") + 1;
			if(str2.indexOf("\n") != -1) lineEnd = str2.indexOf("\n") + str1.length() + key.length();
			return str.substring(lineStart, lineEnd);
		}
		
		return "";
	}
	
	/**
	 * 根据一个key查找一行数据,然后替换
	 * @param str 字符串
	 * @param key key
	 * @autor yxl
	 */
	public static String replaceLine(String key, String targetStr, String str) {
		
		if(str.indexOf(key) != -1){
			int keyIndexStart = str.indexOf(key);//key的开始位置
			int keyIndexEnd = str.indexOf(key) + key.length();//key的结束位置
			//key把字符串分成2部分
			String str1 = str.substring(0, keyIndexStart);
			String str2 = str.substring(keyIndexEnd, str.length());
			
			int lineStart = 0;//key所在行的开始位置
			int lineEnd = str.length();//key所在行的结束位置
			//计算key所在行的开始位置
			if(str1.indexOf("\n") != -1) lineStart = str1.lastIndexOf("\n") + 1;
			if(str2.indexOf("\n") != -1) lineEnd = str2.indexOf("\n") + str1.length() + key.length();
			return str.substring(0, lineStart) + targetStr + str.substring(lineEnd);
		}
		
		return "";
	}
	
	
	/**
	 * 描述： 强制 替换
	 * 
	 * @author: 杨雪令
	 * @param str
	 *            目标字符串
	 * @param oldStr
	 *            要替换的字符
	 * @param newStr
	 *            目标字符
	 * @return 替换后的字符串
	 * @version: 2010-8-28 下午06:05:24
	 */
	public static String forceReplace(String str, String oldStr, String newStr) {
		while (str.indexOf(oldStr) != -1) {
			str = str.substring(0, str.indexOf(oldStr))
					+ newStr
					+ str.substring(str.indexOf(oldStr) + oldStr.length(), str
							.length());
		}
		return str;
	}
	
	/**
	 * 格式化path
	 * @param path 源path路径
	 * @autor yxl
	 */
	public static String formatPath(String path){
		if(path.startsWith("file:/")) path = path.substring(6);
		else if(path.startsWith("/")) path = path.substring(1);
		return path;
	}
	
	/**
	 * 根据结束标签截取字符串
	 * @param str 要处理的字符串
	 * @param endTag 结束标签
	 * @autor yxl
	 */
	public static String subStrByEnd(String str, String endTag){
		if(str.indexOf(endTag) != -1) str = str.substring(0, str.indexOf(endTag) + endTag.length());
		return str;
	}
	
	
	/**
	 * 检查指定的字符串是否为空
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value 待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		if(value == null) return true;
		if(value.trim().length() < 1) return true;
		return false;
	}
	
	
	/**
	 * 清除空位符号
	 * @param str 字符串
	 * @autor yxl
	 * 2013-9-12
	 */
    public static String clearBlank(String str) {
        if(str!=null && !str.equals("")) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            String strNoBlank = m.replaceAll("");
            return strNoBlank;
        }else {
            return str;      
        }
    }
    
    /**
	 * 清除HTML空位符号
	 * @param htmlCode html字符串
	 * @autor yxl
	 * 2013-9-12
	 */
    public static String clearHtmlBlank(String htmlCode) {
    	//替换制表符、换行符等
    	Pattern p = Pattern.compile("\t|\r|\n");
        Matcher m = p.matcher(htmlCode);
        htmlCode = m.replaceAll("");
        
        //替换标签之间的空格
    	p = Pattern.compile(">\\s+<");
        m = p.matcher(htmlCode);
        htmlCode = m.replaceAll("><");
        
        //替换开始标签前的空格
    	p = Pattern.compile("\\s*<");
        m = p.matcher(htmlCode);
        htmlCode = m.replaceAll("<");
        
        //替换结束标签前后的空格
    	p = Pattern.compile("\\s*>\\s*");
        m = p.matcher(htmlCode);
        htmlCode = m.replaceAll(">");
        
        return htmlCode;
    }
    
    
    /**
	 * html编码
	 * @param htmlCode
	 * @return
	 * @autor yxl
	 * 2014-9-17
	 */
	public static String htmlEscape(String htmlCode){
		htmlCode = htmlCode.replaceAll("<", "&lt;");
		htmlCode = htmlCode.replaceAll(">", "&gt;");
		return htmlCode;
	}
    
    /**
     * 字符串转换为Unicode编码
     * @param str 要转换的字符串
     */
    public static String toUnicode(String str){
        char[]strChars=str.toCharArray();
        int iValue=0;
        String uStr="";
        for(char strChar : strChars){
            iValue=(int)strChar;          
            if(iValue<=256){
                uStr+=strChar;//"\\"+Integer.toHexString(iValue);
            }else{
                uStr+="\\u"+Integer.toHexString(iValue);
            }
        }
        return uStr;
    }
    
    /**
	 * 格式化描述
	 * @param desc 描述
	 */
	public static String formatDesc(String desc){
		StringBuffer sb = new StringBuffer();
		
		desc = desc.replaceAll("<br\\s*[/]?>", "\n");
		desc = desc.replaceAll("　", "");
		desc = desc.replaceAll(" ", "");
		desc = clearHtml(desc);
		
		String[] descArray = desc.split("\n");
		for(String descStr : descArray){
			if(descStr.trim().length()>0) {
				descStr = "<p>" + htmlEscape(descStr.trim()) + "</p>\n";
				descStr = descStr.replaceAll("<p>(&nbsp;)*", "<p>");
				sb.append(descStr);
			}
		}
		return sb.toString();
	}
	
	
	/** 索引正则表达式 */
	private static Pattern indexPt = Pattern.compile("[a-zA-Z0-9\u4e00-\u9fa5]{2,2}");
	private static Matcher indexMatcher;
	/**
	 * 获取索引
	 * @param str 字符串
	 * @autor yxl
	 * 2014-5-30
	 */
	public static Map getIndex(String str){
		Map map = new HashMap();
		
		for(int i=0; i<str.length()-1; i++){
			String indexKey = str.substring(i, i+2);
			indexMatcher = indexPt.matcher(indexKey);
			if(indexMatcher.find()) {
				//如果是重复关键词
				if(map.get(indexKey) != null){
					String[] vals = (String[]) map.get(indexKey);
					int count = Integer.parseInt(vals[1]);
					vals[1] = count+1 + "";
					map.put(indexKey, vals);
					continue;
				}
				map.put(indexKey, new String[]{i+"", 1+""});
			}
		}
		
		//索引密度
		int maxLength = str.length();
		for (Iterator it=map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			String[] vals = (String[]) entry.getValue();
			int count = Integer.parseInt(vals[1]);//出现次数
			double density = count*key.length()/(double)maxLength;
			String[] newVals = new String[] {vals[0], vals[1], NumberUtil.getFomat(density, "#.######")};//密度保留6位小数 
			map.put(key, newVals);
		}
		
		
		//输出
		/*for (Iterator it=map.entrySet().iterator(); it.hasNext();) {
			Entry entry = (Entry) it.next();
			String key = entry.getKey().toString();
			String[] vals = (String[]) entry.getValue();
			String valStr = "";
			for(int i=0; i<vals.length; i++){
				valStr += "\t" + vals[i];
			}
			System.out.println(key + valStr);
		}*/
		
		
		return map;
	}
	
	
	/**
      * 获取字符长度，一个汉字作为 1 个字符, 一个英文字母作为 0.5 个字符
      * @return 字符长度，如：text="中国",返回 2；text="test",返回 2；text="中国ABC",返回 4.
	 */
     public static int getCharLength(String text) {
         int textLength = text.length();
         int length = textLength;
         for (int i = 0; i < textLength; i++) {
             if (String.valueOf(text.charAt(i)).getBytes().length > 1) {
                 length++;
             }
         }
         return (length % 2 == 0) ? length / 2 : length / 2 + 1;
     }
     
     /** 超短字符正则表达式 */
     public static String regexSuperShortChar = "[ijl1:'\\.,\\{\\}\\(\\)\\[\\]*\\!]";
     /** 短字符正则表达式 */
     public static String regexShortChar = "[a-zA-Z2-90_@$%^&-=+\"\\s]";
     /** html 标签对正则表达式 */
     private static Pattern htmlTagPt = Pattern.compile("<[^>]*>([^><]*)</[^>]*>");
     /**
 	 * 截取字符串，一个汉字算一个位置，普通字母算半个位置，短字符算1/4个位置
 	 * @param str 要截取的字符串
 	 * @param width 宽度，一个汉字算一个位置，普通字母算1/2位置，短字符算1/4个位置
 	 * @param suffix 后缀
 	 * @date 2015年12月10日 下午9:21:36 
 	 * @author yxl
 	 */
 	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String substringByWidth(String str, int width, String suffix) {
 		if(str==null) return "";
 		int strLength = str.length();
 		
 		//筛选html标签，html标签不计算长度
 		Map htmlMap = new LinkedHashMap();
 		Matcher htmlTagMatcher = htmlTagPt.matcher(str);
 		while(htmlTagMatcher.find()){
 			String htmlTag = htmlTagMatcher.group(0);
 			String htmlContent = htmlTagMatcher.group(1);
 			htmlMap.put(str.indexOf(htmlTag), new String[]{htmlTag, htmlContent});//开始位置 : {htmlTag, htmlContent}
 		}
 		str = clearHtml(str);
 		
 		String subStr = "";//截取出的字符串
 		String firstChar = "";//字符串开头的字符
 		double currentWidth = 0;//当前宽度
 		while (str.length()>0){
 			firstChar = str.substring(0, 1);
 			str = str.substring(1);
 			if(firstChar.matches(regexSuperShortChar)) currentWidth += 1/4d;
 			else if(firstChar.matches(regexShortChar)) currentWidth += 11/20d;
 			else currentWidth += 1;
 			
 			if((currentWidth-0.5)<=width) subStr += firstChar;
 			else break;
 		}
 		
 		//html标签不计算长度，把前面去掉的html标签添加回来
 		for(Iterator it=htmlMap.entrySet().iterator(); it.hasNext();){
 			Entry entry = (Entry) it.next();
 			int index = NumberUtil.parseInt(entry.getKey());
 			String htmlTag = ((String[])entry.getValue())[0].toString();
 			String htmlContent = ((String[])entry.getValue())[1].toString();
 			try{
 				subStr = subStr.substring(0, index) + htmlTag + subStr.substring(index+htmlContent.length());
 			}catch(Exception e){}
 		}
 		
 		if(subStr.length()<strLength) subStr = subStr + suffix;
 		return subStr;
 	}
	
	
	public static void main(String[] args) throws IOException {
		/*String html = "<html>\n" 
				+ "\t<head>\n"
				+ "\t</head>\n"
				+ "\t<body style=\"color:red;\">                \n               "
				+ "\t</body>\n"
				+ "</html>";
		System.out.println(html);
		System.out.println(clearHtmlBlank(html));*/
		
		//String html = "话说啊好";
		//getIndex(html);
		/*String desc = "你好\n你好<br/><br><br/><div>你好</div><br><br/><br>";
		System.out.println(StringUtil.formatDesc(desc));*/
		
		String str="<em>琅琊</em><em>榜</em> <em>54</em>";
		System.out.println(substringByWidth(str, 4, "..."));
		
		/*str="aaaaaanbbbbbbbbb哈哈哈";
		System.out.println(substringByWidth(str, 4, "..."));
		
		str="iii哈哈哈iiiiiiiiiiiiiaaaaaaaa哈哈哈";
		System.out.println(substringByWidth(str, 4, "..."));
		
		str="iaibicidiiiiiiiiiiiiaaaaaaaa哈哈哈";
		System.out.println(substringByWidth(str, 4, "..."));
		
		
		str="哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈哈";
		System.out.println(substringByWidth(str, 10, "..."));
		
		str="String demo = \"abcdefsdfd\";aaaaaaaa哈哈哈";
		System.out.println(substringByWidth(str, 10, "..."));
		
		str="用java 正则表达式，规定字符必须以某些字母开头，如必须以\"abc\"为起始，请问怎么写";
		System.out.println(substringByWidth(str, 10, "..."));
		
		str="public static void main(String[] args) {";
		System.out.println(substringByWidth(str, 10, "..."));*/
	}
}
