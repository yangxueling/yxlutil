package com.yxlisv.util.image;

import gui.ava.html.image.generator.HtmlImageGenerator;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import com.yxlisv.util.file.FilePathUtil;
import com.yxlisv.util.file.FileUtil;
import com.yxlisv.util.string.StringUtil;
import com.yxlisv.util.system.SysCmdHandle;

/**
 * 图片工具类
 * 
 * @author whocare
 */
public class ImageUtil {

	// 定义一个全局的记录器，通过LoggerFactory获取
	public static Logger logger = LoggerFactory.getLogger(ImageUtil.class);
	
	/**
	 * 读取图片
	 * @param imagePath 图片路径
	 */
	public static BufferedImage readImage(String imagePath) {
		Image image = Toolkit.getDefaultToolkit().getImage(imagePath);
		if (image instanceof BufferedImage) {
			return (BufferedImage) image;
		}
		image = new ImageIcon(image).getImage();
		BufferedImage bimage = null;
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			int transparency = Transparency.TRANSLUCENT;
			GraphicsDevice gs = ge.getDefaultScreenDevice();
			GraphicsConfiguration gc = gs.getDefaultConfiguration();
			bimage = gc.createCompatibleImage(image.getWidth(null), image.getHeight(null), transparency);
		} catch (HeadlessException e) {
		}
		if (bimage == null) {
			int type = BufferedImage.TYPE_INT_RGB;
			bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
		}
		Graphics g = bimage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return bimage;
	}

	/**
	 * 对图片进行缩放
	 * 
	 * @param filePath 图片文件路径
	 * @param width 宽度
	 * @param height 高度
	 * @param keepProportion 保持比例
	 * @throws IOException
	 */
	public static void zoomImage(String filePath, int width, int height, boolean keepProportion) throws IOException {

		// 构造Image对象
		BufferedImage srcBuffer = readImage(filePath);
		if (width < 0 && height < 0) {
			width = srcBuffer.getWidth();
			height = srcBuffer.getHeight();
		} else if (width < 0)
			width = (int) (srcBuffer.getWidth() * ((srcBuffer.getHeight() + 0f) / height));
		else if (height < 0)
			height = (int) (srcBuffer.getHeight() * (width / (srcBuffer.getWidth() + 0f)));

		// 如果要保持比例
		if (keepProportion) {
			double srcProportion = (double) srcBuffer.getWidth() / srcBuffer.getHeight();// 原图的宽高比
			double newProportion = (double) width / height;// 原图的宽高比
			if (newProportion - srcProportion > 0) {// 新图片被拉宽，需要减少宽度
				width = (int) (height * srcProportion);
			} else {
				height = (int) (width / srcProportion);
			}
		}
		
		BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = newImage.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//去除锯齿
		g2d.drawImage(srcBuffer.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null); // 绘制小图
		String imgType = FilePathUtil.getSuffix(filePath);
		//背景透明
		if(imgType.equals("png") || imgType.equals("gif")){
			newImage = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
			g2d.dispose();
			g2d = newImage.createGraphics();
		}
		
		// 输出为文件
		FilePathUtil.mkFileDirs(filePath);
		File newImageFile = new File(filePath);
		try {
			ImageIO.write(newImage, imgType, newImageFile);
		} catch (Exception e) {
			logger.error("缩放图片出错！", e);
		}
		newImage.flush();
	}

	/**
	 * 对图片裁剪，并把裁剪完的新图片保存
	 * @param x 起始坐标 X
	 * @param y 起始坐标 Y
	 * @param width 宽度
	 * @param height 高度
	 * @param srcpath 源文件路径
	 * @param targetpath 目标文件路径
	 */
	public static void cut(int x, int y, int width, int height, String srcpath, String targetpath) throws IOException {

		// 读取源图像
		BufferedImage src = readImage(srcpath);
		// 四个参数分别为图像起点坐标和宽高
		// CropImageFilter cropFilter = new CropImageFilter(x, y, width,
		// height);
		// Image img = Toolkit.getDefaultToolkit().createImage(new
		// FilteredImageSource(src.getSource(),
		// cropFilter));
		// 模式改为 SCALE_SMOOTH 图片会清晰一点
		// img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		// img = img.getScaledInstance(width, height,
		// Image.SCALE_AREA_AVERAGING);
		// img = src.getScaledInstance(src.getWidth(), src.getHeight(),
		// Image.SCALE_SMOOTH);
		BufferedImage tagetImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = tagetImage.getGraphics();
		g.drawImage(src.getScaledInstance(src.getWidth(), src.getHeight(), Image.SCALE_SMOOTH), -x, -y, null); // 绘制小图
		g.dispose();
		// 输出为文件
		File newImage = new File(targetpath);
		FilePathUtil.mkFileDirs(targetpath);
		String imgType = FilePathUtil.getSuffix(targetpath);
		ImageIO.write(tagetImage, imgType, newImage);
		tagetImage.flush();
	}

	/**
	 * 根据html代码生成png图片（依赖：html2image.jar）
	 * @param htmlCode html代码
	 * @param pngPath png图片路径
	 * @autor yxl
	 */
	public static void createPng(String htmlCode, String pngPath) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadHtml(htmlCode);
		imageGenerator.getBufferedImage();
		FilePathUtil.mkFileDirs(pngPath);
		imageGenerator.saveAsImage(pngPath);
	}

	/**
	 * 根据html代码生成png图片（依赖：html2image.jar）
	 * @param url url链接
	 * @param pngPath png图片路径
	 * @autor yxl
	 */
	public static void createPngFromUrl(String url, String pngPath) {
		HtmlImageGenerator imageGenerator = new HtmlImageGenerator();
		imageGenerator.loadUrl(url);
		imageGenerator.getBufferedImage();
		imageGenerator.saveAsImage(pngPath);
	}
	
	/**
	 * 使用CutyCapt url生成png图片(把CutyCapt.exe文件放置到: WEB-INF/tools/, linux: /usr/local/cutycapt/)
	 * CutyCapt下载地址：http://sourceforge.net/projects/cutycapt/files/cutycapt/
	 * @param url url链接
	 * @param pngPath png图片路径
	 * @param waitTimeOnOver 当执行结束后，等待多少毫秒销毁
	 * @param linuxCmd linux 命令
	 * @autor yxl
	 */
	public static void createPngByCutyCapt(String url, String picUrl, int waitTimeOnOver, String linuxCmd) {
		// 构建路径
		String path = ImageUtil.class.getResource("/").getPath();
		path = StringUtil.formatPath(path);
		path = StringUtil.subStrByEnd(path, "WEB-INF");

		// 默认Linux命令
		String cmdStr = "/usr/local/cutycapt/xvfb-run.sh --server-args=\"-screen 0, 1024x768x24\" /usr/local/cutycapt/CutyCapt";
		if(linuxCmd != null) cmdStr = linuxCmd;
		cmdStr += " --url=" + url + " --out=" + picUrl;
		if (System.getProperty("os.name").toUpperCase().indexOf("WIN") != -1) {// windows
																				// 系统
			cmdStr = path + "/tools/CutyCapt.exe --min-height=1 --min-width=1 --url=" + url + " --out=" + picUrl;
		}
		logger.info("createPngByCutyCapt: " + cmdStr);

		SysCmdHandle sysCmdHandle = new SysCmdHandle();
		sysCmdHandle.CMD_TIME_OUT = 999999;//超时时间，CutyCapt大量截图速度较慢，把超时时间加长
		sysCmdHandle.WAIT_TIME_ON_OVER = waitTimeOnOver;
		sysCmdHandle.excute(cmdStr);
		//linux 中的CutyCapt容易卡死，杀掉进程
		if (System.getProperty("os.name").toUpperCase().indexOf("WIN") == -1) // 不是windows，默认linux
			sysCmdHandle.excute("killall CutyCapt");
	}

	/**
	 * 使用CutyCapt url生成png图片(把CutyCapt.exe文件放置到: WEB-INF/tools/, linux: /usr/local/cutycapt/)
	 * CutyCapt下载地址：http://sourceforge.net/projects/cutycapt/files/cutycapt/
	 * @param url url链接
	 * @param pngPath png图片路径
	 * @param waitTimeOnOver 当执行结束后，等待多少毫秒销毁
	 * @autor yxl
	 */
	public static void createPngByCutyCapt(String url, String picUrl, int waitTimeOnOver) {
		createPngByCutyCapt(url, picUrl, waitTimeOnOver, null);
	}

	/**
	 * 使用CutyCapt url生成png图片(把CutyCapt.exe文件放置到: WEB-INF/tools/, linux: /usr/local/cutycapt/)
	 * CutyCapt下载地址：http://sourceforge.net/projects/cutycapt/files/cutycapt/
	 * @param url url链接
	 * @param pngPath png图片路径
	 * @autor yxl
	 */
	public static void createPngByCutyCapt(String url, String picUrl) {
		ImageUtil.createPngByCutyCapt(url, picUrl, 200);
	}

	/**
	 * 创建一个印章(png图片)
	 * @param name 名称
	 * @param radius 半径（正常填150）
	 * @param savePath 保存路径
	 * @throws Exception 
	 * @autor yxl
	 */
	public static void createSeal(String name, int radius, String savePath) throws Exception {
		BufferedImage sealImage = new BufferedImage(radius * 2, radius * 2, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = sealImage.createGraphics();
		
		//背景透明
		sealImage = g2d.getDeviceConfiguration().createCompatibleImage(radius * 2, radius * 2, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = sealImage.createGraphics();

		g2d.setColor(Color.RED);//印章字为红色
		g2d.setStroke(new BasicStroke(4f));//画笔加粗
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);//去除锯齿

		// 绘制圆
		int centerX = radius;// 中心点坐标x
		int centerY = radius;// 中心点坐标y
		Ellipse2D circle = new Ellipse2D.Double();
		circle.setFrameFromCenter(centerX, centerY, radius*2-3, radius*2-3);//从中心点绘制圆，直径需要减去画笔
		g2d.draw(circle);

		// 绘制中间的五角星
		Font starFont = new Font("Serif", Font.BOLD, 120);
		g2d.setFont(starFont);
		g2d.setStroke(new BasicStroke(1f));//画笔加粗
		g2d.drawString("★", centerX - 60, centerY + 40);

		// 根据输入字符串得到字符数组
		char [] nameArray = name.toCharArray();

		// 输入的字数
		int ilength = nameArray.length;

		// 设置字体属性
		int fontsize = 36;
		Font f = new Font("宋体", Font.PLAIN, fontsize);

		FontRenderContext context = g2d.getFontRenderContext();
		Rectangle2D bounds = f.getStringBounds(name, context);

		// 字符宽度＝字符串长度/字符数
		double char_interval = (bounds.getWidth() / ilength);
		char_interval *= 1.0;//增加字符间距
		// 上坡度
		double ascent = -bounds.getY() + 5;

		//计算位置
		int first = 0, second = 0;
		boolean odd = false;
		if (ilength % 2 == 1) {
			first = (ilength - 1) / 2;
			odd = true;
		} else {
			first = (ilength) / 2 - 1;
			second = (ilength) / 2;
			odd = false;
		}

		double radius2 = radius - ascent;
		double x0 = centerX;
		double y0 = centerY - radius + ascent;
		// 旋转角度
		double a = 2 * Math.asin(char_interval / (2 * radius2));

		if (odd) {
			g2d.setFont(f);
			g2d.drawString(String.valueOf(nameArray[first]), (float) (x0 - char_interval / 2), (float) y0);

			// 中心点的右边
			for (int i = first + 1; i < ilength; i++) {
				double aa = (i - first) * a;
				double ax = radius2 * Math.sin(aa);
				double ay = radius2 - radius2 * Math.cos(aa);
				AffineTransform transform = AffineTransform.getRotateInstance(aa);
				Font f2 = f.deriveFont(transform);
				g2d.setFont(f2);
				g2d.drawString(String.valueOf(nameArray[i]), (float) (x0 + ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay - char_interval / 2 * Math.sin(aa)));
			}
			// 中心点的左边
			for (int i = first - 1; i > -1; i--) {
				double aa = (first - i) * a;
				double ax = radius2 * Math.sin(aa);
				double ay = radius2 - radius2 * Math.cos(aa);
				AffineTransform transform = AffineTransform.getRotateInstance(-aa);
				Font f2 = f.deriveFont(transform);
				g2d.setFont(f2);
				g2d.drawString(String.valueOf(nameArray[i]), (float) (x0 - ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay + char_interval / 2 * Math.sin(aa)));
			}

		} else {
			// 中心点的右边
			for (int i = second; i < ilength; i++) {
				double aa = (i - second + 0.5) * a;
				double ax = radius2 * Math.sin(aa);
				double ay = radius2 - radius2 * Math.cos(aa);
				AffineTransform transform = AffineTransform.getRotateInstance(aa);
				Font f2 = f.deriveFont(transform);
				g2d.setFont(f2);
				g2d.drawString(String.valueOf(nameArray[i]), (float) (x0 + ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay - char_interval / 2 * Math.sin(aa)));
			}

			// 中心点的左边
			for (int i = first; i > -1; i--) {
				double aa = (first - i + 0.5) * a;
				double ax = radius2 * Math.sin(aa);
				double ay = radius2 - radius2 * Math.cos(aa);
				AffineTransform transform = AffineTransform.getRotateInstance(-aa);
				Font f2 = f.deriveFont(transform);
				g2d.setFont(f2);
				g2d.drawString(String.valueOf(nameArray[i]), (float) (x0 - ax - char_interval / 2 * Math.cos(aa)), (float) (y0 + ay + char_interval / 2 * Math.sin(aa)));
			}
		}
		
		g2d.dispose();

		// 输出为文件
		File newImage = new File(savePath);
		String imgType = FilePathUtil.getSuffix(savePath);
		ImageIO.write(sealImage, imgType, newImage);
		sealImage.flush();
	}

	/**
	 * 合并图片
	 * @param mainImagePath 主图片路径
	 * @param appendImagePath 附加图片路径
	 * @param newImagePath 新图片路径
	 * @param postion 目标图片添加到原图片的位置(1:左上角，2：右上角，3：中间，4：左下角，5：右下角)
	 * @param padding 间距
	 * @param alpha 附加图片透明度(0 - 1, 0为完全透明，1为完全不透明)
	 * @throws Exception 
	 * @autor yxl
	 */
	public static void merger(String mainImagePath, String appendImagePath, String newImagePath, int postion, int padding, float alpha) throws Exception{
		//读取主图
		BufferedImage mainImage = readImage(mainImagePath);
		int width = mainImage.getWidth(null);
		int height = mainImage.getHeight(null);
		//绘制一个和主图同样大小的缓冲区
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bufferedImage.createGraphics();
		//绘制主图到缓冲区
		g.drawImage(mainImage, 0, 0, width, height, null);
		
		//读取附加图片
		BufferedImage appendImage = readImage(appendImagePath);
		int widthAppend = appendImage.getWidth(null);
		int heightAppend = appendImage.getHeight(null);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));//设置附加图片透明度
		
		//附加图片绘制的起始坐标
		int x = 0;
		int y = 0;
		
		//1:左上角，2：右上角，3：中间，4：左下角，5：右下角
		switch (postion) {
			case 1:{
				x = padding;
				y = padding;
				break;
			}
			case 2:{
				x = width - widthAppend - padding;
				y = padding;
				break;
			}
			case 3:{
				x = (width - widthAppend) / 2;
				y = (height - heightAppend) / 2;
				break;
			}
			case 4:{
				x = padding;
				y = height - heightAppend - padding;
				break;
			}
			case 5:{
				x = width - widthAppend - padding;
				y = height - heightAppend - padding;
				break;
			}
		}
		
		g.drawImage(appendImage, x, y, widthAppend, heightAppend, null); // 水印文件结束
		g.dispose();
		
		// 输出为文件
		File newImage = new File(newImagePath);
		String imgType = FilePathUtil.getSuffix(newImagePath);
		ImageIO.write(bufferedImage, imgType, newImage);
		bufferedImage.flush();
	}
	
	//匹配图片的正则表达式
	private static final String IMAGE_REGEX_EXP="[\"'(]([^\"'()]*\\.[jpg|gif|png|bmp]{3,4})[\"')]";
	/**
	 * 从一段代码中获取图片url
	 * @param codes 代码
	 * @autor yxl
	 */
	public static List getImageUrl(String codes){
		List imageList = new ArrayList();
		
		
		Pattern p = Pattern.compile(IMAGE_REGEX_EXP);
		 Matcher m = p.matcher(codes);
		 String url="";
		 while(m.find()) {
			 url = m.group(1);
			 if(!imageList.contains(url)){
				 imageList.add(url);
				 //System.out.println(url); 
			 }
		 }
		
		return imageList;
	}
	
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
		if(targetPath!=null && targetPath.trim().length()>0) targetImage = readImage(targetPath);
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
     * 图像压缩 
     * @param image 
     * @param quality 
     */  
    public static byte[] compressImage(BufferedImage image, float quality) {  
        // 如果图片空，返回空  
        if (image == null) {  
            return null;  
        }  
        // 开始开始，写入byte[]  
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(); // 取得内存输出流  
        // 设置压缩参数  
        JPEGEncodeParam param = JPEGCodec.getDefaultJPEGEncodeParam(image);  
        param.setQuality(quality, false);  
        // 设置编码器  
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(byteArrayOutputStream, param);  
        try {  
            encoder.encode(image);  
        } catch (Exception ef) {  
            ef.printStackTrace();  
        }  
        return byteArrayOutputStream.toByteArray();  
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
	public static void createWatermark(String text, int fontSize, int angle, float alpha, float position, String targetPath, String savePath, String sfont) throws Exception{
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
	public static void createWatermark(String text, int fontSize, int angle, float alpha, float position, BufferedImage targetImage, String savePath, String sfont) throws Exception{
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
	public static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float position, String savePath, String sfont) throws Exception{
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
	private static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float position, String targetPath, String savePath, String sfont) throws Exception{
		BufferedImage targetImage = null;
		if(targetPath!=null && targetPath.trim().length()>0) targetImage = readImage(targetPath);
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
	private static void createWatermark(String text, int width, int height, int fontSize, int angle, float alpha, float position, BufferedImage targetImage, String savePath, String sfont) throws Exception{
		
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
	
	public static void main(String[] args) throws Exception {

		/*
		 * ImageUtil imageUtil = new ImageUtil(); String srcpath = "F:/p/1.png";
		 * String subpath = "F:/p/2.png"; try { imageUtil.zoomImage(srcpath,
		 * 420, 480, true); //imageUtil.cut(150, 100, 120, 160, srcpath,
		 * subpath); } catch (IOException e) { e.printStackTrace(); }
		 * 
		 * String htmlCode = "<div align=\"center\">"+
		 * "<h2><spanclass=\"t\">404NotFound</span></h2>"+
		 * "<tableborder=\"0\"cellpadding=\"8\"cellspacing=\"0\"width=\"460\">"+
		 * "<tbody>"+ "<tr>"+
		 * "<tdclass=\"c\">你请求的页面不存在(TherequestedURLwasnotfoundonthisserver.)</td>"
		 * + "</tr>"+ "</tbody>"+ "</table>"+ "</div>";
		 * ImageUtil.generatePng(htmlCode, "d:/test.png");
		 */
		//ImageUtil.createPngFromUrl("http://www.baidu.com", "d:/test11.png");
		// ImageUtil.createPngByCutyCapt("http://www.taobao.com",
		// "d:/testcc.png");
		//ImageUtil.createSeal("IBM深圳分公司开发部", 150, "d:/ibm.png");
		//ImageUtil.merger("d:/test1.png", "d:/ibm.png", "d:/t2.jpg", 5, 10, 0.9f);
		
		
		/*String codes = "<div class =   \"rm_modules\" style=\"margin: 0 auto; padding: 0; width:750px; background:#FFF; background-image: url('http://f.topisv.com/m/mkae3e10bd7bda483e/image/bgjp.gijf');\">"+
		    	
						        "<div class=\"goods\" style=\"margin:5px 0 5px 5px; border:2px solid #000; padding:0; background-color:#FFF; text-align:center; float: left; width:240px; height:310px; position:relative;\">"+
						            "<span class=\"name changeBgColor\" style=\"display:block; width:240px; height:30px; line-height:30px; text-align:left; text-indent:10px; font-size:14px; font-weight:bold; background-color:#000; color:#FFF; overflow: hidden;\">齐B小短裙</span>"+
						            "<div style=\"width:240px; height:250px; overflow:hidden;\">        	"+
						            "<img class=\"mainImage\" src=\"http://f.topisv.com/m/mkae3e10bd7bda483e/image/epl_1.jpg\" style=\"position: absolute; top: -268px; left: -171px; max-width: none; width: 571px; height: 571px; overflow: hidden; display: inline;\">"+
						            "<img class=\"mainImage\" src=\"http://f.topisv.com/m/mkae3e10bd7bda483e/image/epl_1.jpg\" style=\"position: absolute; top: -20px; left: -300px; max-width: none; width: 571px; height: 571px; overflow: hidden; display: inline;\"></div>"+
						            "<div style=\"width:100%; height:30px; line-height:30px; background:#000; color:#FFF;\">"+
						                "<span class=\"price changeBgColor\" style=\"display:inline-block; float:left; width:80px; height:30px; text-align:center; color:#FFF; font-family:Arial, Helvetica, sans-serif; font-size:20px; line-height:30px; overflow: hidden;\">￥128</span>"+
						                "<span class=\"changeColor\" style=\"display:block; width:80px; height:30px; line-height:30px; background-color:#C00; color:#FFF; font-size:14px; font-weight:bold; text-align:center; float:right; cursor:pointer;\">购买</span>"+
						            "</div>"+
						        "</div>"+
						        "<div style=\" clear:both; height:0; font-size: 1px; line-height: 0px; overflow:hidden;\"></div>"+
						        "<div style=\" width:49px; float:left; height:65px;\"> <a href=\"http://item.taobao.com/item.htm?id=21459991529\" target=\"_blank\" class=\"goodsUrl\" style=\"background:url(http://f.topisv.com/Rm/500001.jpg) no-repeat; width:49px; height:40px; display:inline-block; float:left;\"></a></div>"+
						    "</div>";
		
		System.out.println(ImageUtil.getImageUrl(codes));*/
		
		//ImageUtil.zoomImage("d:/wlyx/block1 - 副本.png", 50, 50, true);
		//ImageUtil.createLoopWatermark("IBM深圳分公司开发部", 26, 45, 0.15f, 2f, "F:/shuiyin/12.png", "f:/shuiyin/11.png", "方正舒体");
		//ImageUtil.createLoopWatermark("IBM深圳分公司开发部", 26, 45, 0.15f, 2f, "F:/shuiyin/5.png", "f:/shuiyin/11.png", "黑体");
		//ImageUtil.createLoopWatermark("淘水印2", 400, 400, 26, 30, 1f, 2f, "f:/shuiyin/水印.png", "方正舒体");
		//ImageUtil.createLoopWatermark("淘水印3", 400, 400, 26, 30, 1f, 2f, "f:/shuiyin/水印.png", "f:/shuiyin/2.ttf");
		//ImageUtil.createWatermark("淘水印342哈", -400, 400, 80, 0, 0.3f, 0, "f:/shuiyin/水印.png", "华文行楷");
		ImageUtil.createWatermark("淘水印", 80, 30, 0.2f, 0, "f:/shuiyin/12.png", "f:/shuiyin/11.png", "华文行楷");
		//ImageUtil.zoomImage("F:/shuiyin/shuiyin1.png", 2300, 4100, true);//缩放图片
		//ImageUtil.merger("F:/shuiyin/1.jpg", "F:/shuiyin/shuiyin1.png", "F:/shuiyin/new.png", 3, 5, 1);
	}
}
