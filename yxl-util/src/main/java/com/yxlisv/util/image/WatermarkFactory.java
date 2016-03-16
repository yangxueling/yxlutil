package com.yxlisv.util.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.string.StringUtil;

/**
 * 水印工厂
 * @author yxl
 */
public class WatermarkFactory {

	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param spacing 间距(默认为1，0-10)
	 * @param targetPath 目标图片路径，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createLoopWatermark(String text, int fontSize, int angle, float alpha, float spacing, String targetPath, String savePath, String sfont) throws Exception{
		createLoopWatermark(text, 0, 0, fontSize, angle, alpha, spacing, targetPath, savePath, sfont);
	}
	
	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param spacing 间距(默认为1，0-10)
	 * @param targetImage 目标图片
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createLoopWatermark(String text, int fontSize, int angle, float alpha, float spacing, BufferedImage targetImage, String savePath, String sfont) throws Exception{
		createLoopWatermark(text, 0, 0, fontSize, angle, alpha, spacing, targetImage, savePath, sfont);
	}
	
	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param spacing 间距(默认为1，0-10)
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createLoopWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float spacing, String savePath, String sfont) throws Exception{
		createLoopWatermark(text, width, height, fontSize, angle, alpha, spacing, "", savePath, sfont);
	}
	
	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param spacing 间距(默认为1，0-10)
	 * @param targetPath 目标图片路径，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws Exception 
	 */
	private static void createLoopWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float spacing, String targetPath, String savePath, String sfont) throws Exception{
		BufferedImage targetImage = null;
		if(targetPath!=null && targetPath.trim().length()>0) targetImage = ImageUtil.readImage(targetPath);
		createLoopWatermark(text, width, height, fontSize, angle, alpha, spacing, targetImage, savePath, sfont);
	}
	
	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param spacing 间距(默认为1，0-10)
	 * @param targetImage 目标图片
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @param imgType 图片类型
	 * @throws Exception 
	 */
	private static void createLoopWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float spacing, BufferedImage targetImage, String savePath, String sfont) throws Exception{
		
		if(text==null || text.trim().length()<1) return;
		if(targetImage!=null) {
			width = targetImage.getWidth(null);
			height = targetImage.getHeight(null);
		}
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		
		String imgType = FilePathUtil.getSuffix(savePath);
		//背景透明
		if(imgType.equals("png") || imgType.equals("gif")){
			newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = newImage.createGraphics();
		}
		
		if(targetImage!=null) g2d.drawImage(targetImage, 0, 0, width, height, null);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));//透明度
		g2d.rotate(-angle * Math.PI / 180, width/2, height/2);//旋转
		g2d.setColor(Color.WHITE);
		Font font = null;
		if(sfont.endsWith("ttf")){
			FileInputStream aixing = null;
			try{
				aixing = new FileInputStream(sfont);
				Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
				font = dynamicFont.deriveFont(Font.BOLD, fontSize);
			} catch(Exception e){
				throw e;
			} finally{
				if(aixing!=null) aixing.close();
			}
			
		} else font = new Font(sfont, Font.BOLD, fontSize);
		g2d.setFont(font);
		g2d.setStroke(new BasicStroke(4f));//画笔加粗
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//去除锯齿
		
		int marginTop = (int)(fontSize*3*spacing);//单行文字顶部间距
		int marginLeft = (int)(fontSize*3*spacing);//单行文字左边间距
		int textWidth = fontSize * StringUtil.getCharLength(text);//单行文字宽度
		int textHeight = fontSize;//单行文字高度
		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
		textWidth = fm.stringWidth(text);
		textHeight = fm.getHeight();
		
		int cols = width/(textWidth+marginLeft);//绘制多少列
		if(cols<1) cols = 1;
		int rows = height/(textHeight+marginTop);//绘制多少行
		if(rows<1) rows = 1;
		
		//水印居中，边距计算
		int textAreaMarginTop =  (height%(textHeight+marginTop)+marginTop)/2;
		int textAreaMarginLeft = (width%(textWidth+marginLeft)+marginLeft)/2;
		
		for(int c=-2; c<cols+2; c++){
			for(int r=-2; r<rows+2; r++){
				int x = (textWidth+marginLeft)*c + textAreaMarginLeft;
				int y = (textHeight+marginTop)*r + fontSize + textAreaMarginTop;
				if(c%2!=0) y += (marginTop+fontSize)/2;//第二列错开
				
				//绘制边框
				FontRenderContext frc = g2d.getFontRenderContext();
				TextLayout tl = new TextLayout(text, font, frc);
				Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(x,y));
				g2d.setStroke(new BasicStroke(1.0f));
				g2d.setColor(Color.LIGHT_GRAY);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));//透明度
				g2d.draw(sha);
				
				g2d.setColor(Color.WHITE);
				g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));//透明度
				g2d.fill(sha);
				//g2d.drawString(text, x, y);
				//g2d.drawString(text, x, y);
				//System.out.println(x + "/" + y);
			}
		}
		
		g2d.dispose();

		// 输出为文件
		FilePathUtil.mkFileDirs(savePath);
		File newImageFile = new File(savePath);
		//byte[] fileData = compressImage(newImage, 1f);
		try {
			//FileUtil.saveFile(fileData, savePath);
			ImageIO.write(newImage, imgType, newImageFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		newImage.flush();
	}
	
	
	
	
	/**
	 * 生成水印
	 * @param text 水印文字
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param targetPath 目标图片路径，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createWatermark(String text, int fontSize, int angle, float alpha, int position, String targetPath, String savePath, String sfont) throws Exception{
		createWatermark(text, 0, 0, fontSize, angle, alpha, position, targetPath, savePath, sfont);
	}
	
	/**
	 * 生成水印
	 * @param text 水印文字
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param targetImage 目标图片
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createWatermark(String text, int fontSize, int angle, float alpha, int position, BufferedImage targetImage, String savePath, String sfont) throws Exception{
		createWatermark(text, 0, 0, fontSize, angle, alpha, position, targetImage, savePath, sfont);
	}
	
	/**
	 * 生成循环水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws IOException 
	 */
	public static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, int position, String savePath, String sfont) throws Exception{
		createWatermark(text, width, height, fontSize, angle, alpha, position, "", savePath, sfont);
	}
	
	/**
	 * 生成水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param targetPath 目标图片路径，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws Exception 
	 */
	private static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, int position, String targetPath, String savePath, String sfont) throws Exception{
		BufferedImage targetImage = null;
		if(targetPath!=null && targetPath.trim().length()>0) targetImage = ImageUtil.readImage(targetPath);
		createWatermark(text, width, height, fontSize, angle, alpha, position, targetImage, savePath, sfont);
	}
	
	
	/**
	 * 生成水印
	 * @param text 水印文字
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param fontSize 文字大小
	 * @param angle 旋转角度（0-360逆时针旋转）
	 * @param alpha 透明度（0.1-1，建议0.1-0.3）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param targetPath 目标图片路径，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @param sfont 字体
	 * @throws Exception 
	 */
	private static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, int position, BufferedImage targetImage, String savePath, String sfont) throws Exception{
		
		if(text==null || text.trim().length()<1) return;
		Font font = null;
		if(sfont.endsWith("ttf")){
			FileInputStream aixing = null;
			try{
				aixing = new FileInputStream(sfont);
				Font dynamicFont = Font.createFont(Font.TRUETYPE_FONT, aixing);
				font = dynamicFont.deriveFont(Font.BOLD, fontSize);
			} catch(Exception e){
				throw e;
			} finally{
				if(aixing!=null) aixing.close();
			}
			
		} else font = new Font(sfont, Font.BOLD, fontSize);
		int textWidth = fontSize * StringUtil.getCharLength(text);//单行文字宽度
		int textHeight = fontSize;//单行文字高度
		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(font);
		textWidth = fm.stringWidth(text);
		textHeight = fm.getHeight();
		
		if(targetImage!=null) {
			width = targetImage.getWidth(null);
			height = targetImage.getHeight(null);
		} else if(width<=0 || height<=0){
			width = textWidth;
			height = textHeight;
		}
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		
		String imgType = FilePathUtil.getSuffix(savePath);
		//背景透明
		if(imgType.equals("png") || imgType.equals("gif")){
			newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = newImage.createGraphics();
		}
		
		if(targetImage!=null) g2d.drawImage(targetImage, 0, 0, width, height, null);
		if(position==0) g2d.rotate(-angle * Math.PI / 180, width/2, height/2);//旋转
		g2d.setColor(Color.WHITE);
		g2d.setFont(font);
		g2d.setStroke(new BasicStroke(4f));//画笔加粗
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//去除锯齿
		
		int marging = 5;//间距
		int x = marging + 5;
		int y = marging + textHeight;
		//position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
		if(position==0){
			x = width/2 - textWidth/2;
			y = height/2 - textHeight/2;
			y += textHeight*4/5;//不是从左上角绘制字体
		} else if(position==2){
			x = width - textWidth - marging*2;
		} else if(position==3){
			y = height - textHeight*1/3;
		} else if(position==4){
			x = width - textWidth - marging*2;
			y = height - textHeight*1/3;
		}
		
		//绘制边框
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout tl = new TextLayout(text, font, frc);
		Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(x,y));
		g2d.setStroke(new BasicStroke(1.0f));
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));//透明度
		g2d.draw(sha);
		
		g2d.setColor(Color.WHITE);
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));//透明度
		g2d.fill(sha);
		//g2d.drawString(text, x, y);
		
		g2d.dispose();

		// 输出为文件
		FilePathUtil.mkFileDirs(savePath);
		File newImageFile = new File(savePath);
		try {
			ImageIO.write(newImage, imgType, newImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		newImage.flush();
	}
	
	
	/**
	 * 生成图片水印
	 * @param watermarkPath 水印路径
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param zoom 缩放（宽度所占比例）
	 * @param alpha 透明度
	 * @param savePath 新生成的水印图片路径
	 * @throws Exception 
	 */
	public static void createImageWatermark(String watermarkPath, int width, int height, int position, float zoom, float alpha, String savePath) throws Exception{
		createImageWatermark(watermarkPath, width, height, position, zoom, alpha, null, savePath);
	}
	
	
	/**
	 * 生成图片水印
	 * @param watermarkPath 水印路径	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param zoom 缩放（宽度所占比例）
	 * @param alpha 透明度
	 * @param targetImage 目标图片，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @throws Exception 
	 */
	public static void createImageWatermark(String watermarkPath, int position, float zoom, float alpha, BufferedImage targetImage, String savePath) throws Exception{
		createImageWatermark(watermarkPath, 0, 0, position, zoom, alpha, targetImage, savePath);
	}
		
	
	/**
	 * 生成图片水印
	 * @param watermarkPath 水印路径
	 * @param width 生成的水印图片宽度（如果有targetPath，此参数无效）
	 * @param height 生成的水印图片高度（如果有targetPath，此参数无效）
	 * @param position 位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
	 * @param zoom 缩放（宽度所占比例）
	 * @param alpha 透明度
	 * @param targetImage 目标图片，要给哪张图片添加水印（可以不传，会生成一张空白的水印图）
	 * @param savePath 新生成的水印图片路径
	 * @throws Exception 
	 */
	private static void createImageWatermark(String watermarkPath, int width, int height, int position, float zoom, float alpha, BufferedImage targetImage, String savePath) throws Exception{
		
		if(watermarkPath==null || watermarkPath.trim().length()<1) return;
		
		if(targetImage!=null) {
			width = targetImage.getWidth(null);
			height = targetImage.getHeight(null);
		}
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		
		String imgType = FilePathUtil.getSuffix(savePath);
		//背景透明
		if(imgType.equals("png") || imgType.equals("gif")){
			newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = newImage.createGraphics();
		}
		
		if(targetImage!=null) g2d.drawImage(targetImage, 0, 0, width, height, null);
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//去除锯齿
		
		//绘制水印
		BufferedImage wmbf = ImageUtil.readImage(watermarkPath);
		int spacing = 10;//间距
		int wmbfx = 10;//绘制起点x坐标
		int wmbfy = 10;//绘制起点y坐标
		int wmbfwidth = wmbf.getWidth();//宽度
		int wmbfheight = wmbf.getHeight();//高度
		float wmbfBl = (float)wmbfwidth / wmbfheight;//宽高比例
		
		//水印图缩小到画板大小
		if(wmbfwidth > width) {
			wmbfwidth = width;
			wmbfheight = (int)(wmbfwidth / wmbfBl);
		}
		if(wmbfheight > height) {
			wmbfheight = height;
			wmbfwidth = (int)(wmbfheight * wmbfBl);
		}
		
		//缩放水印
		if(zoom>0 && zoom<1){
			wmbfwidth = (int) (wmbfwidth * zoom);
			wmbfheight = (int)(wmbfwidth / wmbfBl);
		}
		if(wmbfwidth > (width-spacing*2)) {
			wmbfwidth = width-spacing*2;
			wmbfheight = (int)(wmbfwidth / wmbfBl);
		}
		if(wmbfheight > (height-spacing*2)){
			wmbfheight = height-spacing*2;
			wmbfwidth = (int)(wmbfheight * wmbfBl);
		}
		
		//计算位置
		//位置(0：居中，1：左上角，2：右上角，3：左下角，4：右下角)
		if(position==0){
			wmbfx = width/2 - (wmbfwidth/2);
			wmbfy = height/2 - (wmbfheight/2);
		} else if(position==2){
			wmbfx = width - wmbfwidth - spacing;
		} else if(position==3){
			wmbfy = height - wmbfheight - spacing;
		} else if(position==4){
			wmbfx = width - wmbfwidth - spacing;
			wmbfy = height - wmbfheight - spacing;
		}
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));//透明度
		g2d.drawImage(wmbf, wmbfx, wmbfy, wmbfwidth, wmbfheight, null);
		g2d.dispose();

		// 输出为文件
		FilePathUtil.mkFileDirs(savePath);
		File newImageFile = new File(savePath);
		try {
			ImageIO.write(newImage, imgType, newImageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		newImage.flush();
	}
	
	public static void main(String[] args) throws Exception {
		//WatermarkFactory.zoomImage("d:/wlyx/block1 - 副本.png", 50, 50, true);
		//WatermarkFactory.createLoopWatermark("IBM深圳分公司开发部", 26, 45, 0.15f, 2f, "F:/shuiyin/12.png", "f:/shuiyin/11.png", "方正舒体");
		//WatermarkFactory.createLoopWatermark("IBM深圳分公司开发部", 26, 45, 0.15f, 2f, "F:/shuiyin/5.png", "f:/shuiyin/11.png", "黑体");
		//WatermarkFactory.createLoopWatermark("淘水印2", 400, 400, 26, 30, 1f, 2f, "f:/shuiyin/水印.png", "方正舒体");
		//WatermarkFactory.createLoopWatermark("淘水印3", 400, 400, 26, 30, 1f, 2f, "f:/shuiyin/水印.png", "f:/shuiyin/2.ttf");
		//WatermarkFactory.createWatermark("淘水印342哈", -400, 400, 80, 0, 0.3f, 0, "f:/shuiyin/水印.png", "华文行楷");
		//WatermarkFactory.createWatermark("淘水印", 80, 30, 0.2f, 0, "f:/shuiyin/12.png", "f:/shuiyin/11.png", "华文行楷");
		//WatermarkFactory.zoomImage("F:/shuiyin/shuiyin1.png", 2300, 4100, true);//缩放图片
		//WatermarkFactory.merger("F:/shuiyin/1.jpg", "F:/shuiyin/shuiyin1.png", "F:/shuiyin/new.png", 3, 5, 1);
		createImageWatermark("F:/shuiyin/icon2.png", 400, 400, 4, 0.8f, 0.9f, "F:/shuiyin/shuiyin1.png");
	}
}
