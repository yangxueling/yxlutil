package com.yxlisv.util.doc;

import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

/**
 * word 导出工具类
 * @author john Local
 *
 */
public class WordExport {

	
	/**
	 * html 导出WORD
	 * @param fileName 文件名
	 * @param htmlCode html代码，body内部的部分
	 * @throws Exception 
	 * @autor yxl
	 * 2014-4-16
	 */
	public static void htmlToWord(String fileName, String htmlCode, HttpServletResponse response) throws Exception{
		//word文件头
		String wordFileHead = "<html xmlns:v=\"urn:schemas-microsoft-com:vml\"\n";
		wordFileHead += "xmlns:o=\"urn:schemas-microsoft-com:office:office\"\n";
		wordFileHead += "xmlns:w=\"urn:schemas-microsoft-com:office:word\"\n";
		wordFileHead += "xmlns:m=\"http://schemas.microsoft.com/office/2004/12/omml\"\n";
		wordFileHead += "xmlns=\"http://www.w3.org/TR/REC-html40\">\n";
		wordFileHead += "<head>\n";
		wordFileHead += "<!--[if gte mso 9]><xml><w:WordDocument><w:View>Print</w:View><w:TrackMoves>false</w:TrackMoves><w:TrackFormatting/><w:ValidateAgainstSchemas/><w:SaveIfXMLInvalid>false</w:SaveIfXMLInvalid><w:IgnoreMixedContent>false</w:IgnoreMixedContent><w:AlwaysShowPlaceholderText>false</w:AlwaysShowPlaceholderText><w:DoNotPromoteQF/><w:LidThemeOther>EN-US</w:LidThemeOther><w:LidThemeAsian>ZH-CN</w:LidThemeAsian><w:LidThemeComplexScript>X-NONE</w:LidThemeComplexScript><w:Compatibility><w:BreakWrappedTables/><w:SnapToGridInCell/><w:WrapTextWithPunct/><w:UseAsianBreakRules/><w:DontGrowAutofit/><w:SplitPgBreakAndParaMark/><w:DontVertAlignCellWithSp/><w:DontBreakConstrainedForcedTables/><w:DontVertAlignInTxbx/><w:Word11KerningPairs/><w:CachedColBalance/><w:UseFELayout/></w:Compatibility><w:BrowserLevel>MicrosoftInternetExplorer4</w:BrowserLevel><m:mathPr><m:mathFont m:val=\"Cambria Math\"/><m:brkBin m:val=\"before\"/><m:brkBinSub m:val=\"--\"/><m:smallFrac m:val=\"off\"/><m:dispDef/><m:lMargin m:val=\"0\"/> <m:rMargin m:val=\"0\"/><m:defJc m:val=\"centerGroup\"/><m:wrapIndent m:val=\"1440\"/><m:intLim m:val=\"subSup\"/><m:naryLim m:val=\"undOvr\"/></m:mathPr></w:WordDocument></xml><![endif]-->\n";
		wordFileHead += "</head>\n";
		wordFileHead += "<body>\n";
		htmlCode = wordFileHead + htmlCode + "\n</body>\n</html>";
		
		//文件名
		response.setContentType("application/octet-stream;charset=utf-8");
		fileName = new String(fileName.getBytes("gbk"), "ISO-8859-1");
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ".doc");
		
		//写文件
		OutputStream out = response.getOutputStream();
		out.write(htmlCode.getBytes());
        out.flush();
        out.close();  
	}
    
    
    public static void main(String[] args) throws Exception {
    	WordExport workExport = new WordExport();
	}
}