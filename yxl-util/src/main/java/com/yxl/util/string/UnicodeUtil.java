package com.yxl.util.string;

import java.lang.Character.UnicodeBlock;

/**
 * Unicode工具类
 * @author yxl
 */
public class UnicodeUtil {
	
	/**
	 * 判断是否需要转换
	 * @autor yxl
	 */
	protected static boolean isNeedConvert(char para) {
        return ((para & (0x00FF)) != para);
    }
     
    /**
     * gbk 转 Unicode
     * @param str 需要转换的字符串
     */
    public static String gbkToUnicode(String str) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char chr1 = (char) str.charAt(i);
 
            if (!isNeedConvert(chr1)) {
                result.append(chr1);
                continue;
            }
 
            result.append("\\u" + Integer.toHexString((int) chr1));
        }
 
        return result.toString();
    }
 
   
    /**
     * Unicode 转 gbk
     * @param str 需要转换的字符串
     */
    public static String unicodeToGbk(String dataStr) {
        int index = 0;
        StringBuffer buffer = new StringBuffer();
 
        int li_len = dataStr.length();
        while (index < li_len) {
            if (index >= li_len - 1
                    || !"\\u".equals(dataStr.substring(index, index + 2))) {
                buffer.append(dataStr.charAt(index));
 
                index++;
                continue;
            }
 
            String charStr = "";
            charStr = dataStr.substring(index + 2, index + 6);
 
            char letter = (char) Integer.parseInt(charStr, 16);
 
            buffer.append(letter);
            index += 6;
        }
 
        return buffer.toString();
    }
 
    
    /**
     * utf-8 转unicode
     * @param str 需要转换的字符串
     */
    public static String utf8ToUnicode(String str) {
        char[] myBuffer = str.toCharArray();
 
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            UnicodeBlock ub = UnicodeBlock.of(myBuffer[i]);
            if (ub == UnicodeBlock.BASIC_LATIN) {
                sb.append(myBuffer[i]);
            } else if (ub == UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                int j = (int) myBuffer[i] - 65248;
                sb.append((char) j);
            } else {
                short s = (short) myBuffer[i];
                String hexS = Integer.toHexString(s);
                String unicode = "\\u" + hexS;
                sb.append(unicode.toLowerCase());
            }
        }
        return sb.toString();
    }
 
    /**
     * unicode 转 utf-8
     * @param str 需要转换的字符串
     */
    public static String unicodeToUtf8(String theString) {
        char aChar;
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len;) {
            aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    // Read the xxxx
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            value = (value << 4) + aChar - '0';
                            break;
                        case 'a':
                        case 'b':
                        case 'c':
                        case 'd':
                        case 'e':
                        case 'f':
                            value = (value << 4) + 10 + aChar - 'a';
                            break;
                        case 'A':
                        case 'B':
                        case 'C':
                        case 'D':
                        case 'E':
                        case 'F':
                            value = (value << 4) + 10 + aChar - 'A';
                            break;
                        default:
                            throw new IllegalArgumentException(
                                    "Malformed   \\uxxxx   encoding.");
                        }
                    }
                    outBuffer.append((char) value);
                } else {
                    if (aChar == 't')
                        aChar = '\t';
                    else if (aChar == 'r')
                        aChar = '\r';
                    else if (aChar == 'n')
                        aChar = '\n';
                    else if (aChar == 'f')
                        aChar = '\f';
                    outBuffer.append(aChar);
                }
            } else
                outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
 
    
    public static void main(String[] args) {
		String str = "\\u513f";
		str = UnicodeUtil.unicodeToUtf8(str);
		System.out.println(str);
	}
}