package com.f1.ami.web.charts;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.TextAttribute;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.awt.geom.QuadCurve2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.MemoryCacheImageOutputStream;

import com.f1.utils.ColorHelper;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.FastPNGImageWriter;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.RectLite;
import com.f1.utils.SH;
import com.sun.imageio.plugins.png.PNGImageWriterSpi;

public class AmiWebChartGraphicsWrapper {

	private static final int MAX_CONTINUOUS_LINES = 100000;
	public final static byte LINE_TYPE_DIRECT = 0;
	public final static byte LINE_TYPE_HORZ = 1;
	public final static byte LINE_TYPE_VERT = 2;
	public final static byte LINE_TYPE_HORZ_QUAD_BEZIER = 3;
	public final static byte LINE_TYPE_VERT_QUAD_BEZIER = 4;
	public final static byte LINE_TYPE_HORZ_CUBIC_BEZIER = 5;
	public final static byte LINE_TYPE_VERT_CUBIC_BEZIER = 6;

	public static final byte ALIGN_TOP = -1;
	public static final byte ALIGN_LEFT = -1;
	public static final byte ALIGN_MIDDLE = 0;
	public static final byte ALIGN_BOTTOM = 1;
	public static final byte ALIGN_RIGHT = 1;
	private static final Logger log = LH.get();
	private Graphics2D g2d;
	private BufferedImage bufferedImage;
	private boolean antialias;
	private AmiWebChartZoomMetrics zoom;
	private int width, height;
	private Map<String, String> fontMappings;

	public void init(AmiWebChartZoomMetrics zoom, Map<String, String> fontMappings) {
		this.fontMappings = fontMappings;
		try {
			this.zoom = zoom;
			this.width = zoom.getWidth();
			this.height = zoom.getHeight();
			this.textRectLites.clear();
			this.setBufferedImage(newBufferedImage());
			this.g2d = initGraphics2D(this.getBufferedImage());
		} catch (Exception e) {
			LH.warning(log, "Error: ", e);
		}
	}
	private BufferedImage newBufferedImage() {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	private Graphics2D initGraphics2D(BufferedImage bufImage) {
		Graphics2D g = getBufferedImage().createGraphics();
		g.setBackground(new Color(255, 255, 255, 0));
		applyAntialias(antialias, g);
		return g;
	}

	private static void applyAntialias(boolean antialias, Graphics2D g) {
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, antialias ? RenderingHints.VALUE_ANTIALIAS_ON : RenderingHints.VALUE_ANTIALIAS_OFF);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
		g.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

	}
	public void renderTo(AmiWebChartGraphicsWrapper sink, int opacity) {
		applyOpacity(this.getBufferedImage(), sink.g2d, opacity);
	}

	private void applyOpacity(BufferedImage src, Graphics2D sink, int opacity) {
		if (opacity < 100) {
			sink = (Graphics2D) sink.create();
			AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity / 100f);
			sink.setComposite(ac);
			if (!sink.drawImage(src, 0, 0, null))
				LH.warning(log, "Alpha layer still has pending pixels");
			sink.dispose();
		} else {
			if (!sink.drawImage(src, 0, 0, null))
				LH.warning(log, "Alpha layer still has pending pixels");
		}
	}

	private int scaleY(double y) {
		return AmiWebChartUtils.rd(zoom.scaleY(y));
	}
	private int scaleX(double x) {
		return AmiWebChartUtils.rd(zoom.scaleX(x));
	}
	private int scaleH(double h) {
		return AmiWebChartUtils.rd(zoom.scaleH(h));
	}
	private int scaleW(double w) {
		return AmiWebChartUtils.rd(zoom.scaleW(w));
	}

	final FastPNGImageWriter writer = new FastPNGImageWriter(new PNGImageWriterSpi());
	final FastByteArrayOutputStream buf = new FastByteArrayOutputStream();

	private int xPadding = 0, yPadding = 0;

	private static BufferedImage toImage(AmiWebImages img) {
		if (img != null) {
			return toImage(img.getImage());
		}
		return null;
	}

	private static BufferedImage toImage(byte[] img) {
		try {
			BufferedImage image = null;
			if (img != null)
				try (InputStream in = new ByteArrayInputStream(img)) {
					image = ImageIO.read(in);
				} catch (IOException e) {
					LH.warning(log, "Encountered error while converting image", e);
				}
			return image;
		} catch (NullPointerException e) {
			LH.warning(log, "Encountered NPE while converting image", e);
			return null;
		}
	}

	public static byte[] emptyImage(int width, int height) {
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

		AmiWebChartGraphicsWrapper wrapper = new AmiWebChartGraphicsWrapper();
		AmiWebChartZoomMetrics z = new AmiWebChartZoomMetrics(width, 1, 0, height, 1, 0);
		Map<String, String> fMap = new HashMap<>();
		wrapper.init(z, fMap);
		wrapper.setBufferedImage(bufferedImage);

		return wrapper.renderPNG(1);
	}

	public static byte[] toBytes(BufferedImage image) {
		AmiWebChartGraphicsWrapper wrapper = new AmiWebChartGraphicsWrapper();
		AmiWebChartZoomMetrics z = new AmiWebChartZoomMetrics(image.getWidth(), 1, 0, image.getHeight(), 1, 0);
		Map<String, String> fMap = new HashMap<>();
		wrapper.init(z, fMap);
		wrapper.setBufferedImage(image);

		return wrapper.renderPNG(1);
	}

	private static Graphics2D generateGraphics(BufferedImage bufferedImage, boolean printBg, Color bgColor) {
		Graphics2D g = bufferedImage.createGraphics();
		if (printBg) {
			g.setBackground(bgColor);
			g.clearRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());
		}
		applyAntialias(false, g);
		return g;
	}

	public static byte[] addBgToImage(AmiWebImages img, String bgColor) {
		BufferedImage bufferedImg = toImage(img);
		BufferedImage result = new BufferedImage(bufferedImg.getWidth(), bufferedImg.getHeight(), BufferedImage.TYPE_INT_ARGB);

		Color bg = ColorHelper.parseColor(bgColor);
		Graphics2D g = generateGraphics(result, true, bg);

		g.drawImage(bufferedImg, 0, 0, null);

		g.dispose();

		return toBytes(result);
	}

	public static byte[] combineAxesWithPlot(AmiWebImages[][] plots, int pw, int ph, int plotWidth, int plotHeight, int[][][] offsets, AmiWebImages xBottomAxis,
			AmiWebImages yLeftAxis, AmiWebImages xTopAxis, AmiWebImages yRightAxis, String dividerColor, String backgroundColor, boolean printBg) {
		Color bgColor = ColorHelper.parseColor(backgroundColor);

		BufferedImage[][] plotImages = generatePlotImages(plots, pw, ph);

		BufferedImage xb = toImage(xBottomAxis);
		BufferedImage yl = toImage(yLeftAxis);

		BufferedImage xt = toImage(xTopAxis);
		BufferedImage yr = toImage(yRightAxis);

		int xbHeight = xb != null ? xb.getHeight() : 0;
		int xtHeight = xt != null ? xt.getHeight() : 0;

		int ylWidth = yl != null ? yl.getWidth() : 0;
		int yrWidth = yr != null ? yr.getWidth() : 0;

		int pWidth = plotWidth;
		int pHeight = plotHeight;

		int width = ylWidth + yrWidth + pWidth;
		int height = xbHeight + xtHeight + pHeight;

		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = generateGraphics(bufferedImage, printBg, bgColor);

		if (xbHeight > 0)
			g.drawImage(xb, ylWidth, pHeight + xtHeight, null);

		if (ylWidth > 0)
			g.drawImage(yl, 0, xtHeight, null);

		if (xtHeight > 0)
			g.drawImage(xt, ylWidth, 0, null);

		if (yrWidth > 0)
			g.drawImage(yr, ylWidth + pWidth, xtHeight, null);

		drawMultiPlot(g, ylWidth, xtHeight, plotImages, pw, ph, offsets);

		drawPlotDividers(g, plotImages, pw, ph, offsets, xtHeight, ylWidth, width, height, dividerColor);

		g.dispose();

		return toBytes(bufferedImage);

	}

	private static BufferedImage[][] generatePlotImages(AmiWebImages[][] plots, int width, int height) {
		BufferedImage[][] plotImages = new BufferedImage[width][height];
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				plotImages[i][j] = toImage(plots[i][j]);
			}
		}
		return plotImages;
	}

	private static void drawPlotDividers(Graphics2D g, BufferedImage[][] plots, int width, int height, int[][][] offsets, int xtHeight, int ylWidth, int plotWidth, int plotHeight,
			String dividerColor) {
		g.setStroke(new BasicStroke());
		Color div = ColorHelper.parseColor(dividerColor);
		g.setColor(div);
		int sx = ylWidth, sy = xtHeight;

		g.drawLine(sx, 0, sx, plotHeight);
		g.drawLine(0, sy, plotWidth, sy);

		for (int i = 0; i < width; i++) {
			sx = ylWidth;
			for (int j = 0; j < height; j++) {
				BufferedImage p = plots[i][j];
				if (p != null) {
					sx += offsets[i][j][0];

					sy += offsets[i][j][1];
					g.drawLine(0, sy + p.getHeight(), plotWidth, sy + p.getHeight());
					g.drawLine(sx + p.getWidth(), 0, sx + p.getWidth(), plotHeight);
					sy -= offsets[i][j][1];
					sx += p.getWidth();
					if (j == plots[0].length - 1)
						sy += p.getHeight() + offsets[i][j][1];
				}
			}
		}
	}

	private static void drawMultiPlot(Graphics2D g, int xOffset, int yOffset, BufferedImage[][] plots, int width, int height, int[][][] offsets) {

		int sx = xOffset, sy = yOffset;
		for (int i = 0; i < width; i++) {
			sx = xOffset;
			for (int j = 0; j < height; j++) {
				BufferedImage imageP = plots[i][j];
				if (imageP != null) {
					sx += offsets[i][j][0];
					g.drawImage(imageP, sx, sy, null);
					sy += offsets[i][j][1];
					sy -= offsets[i][j][1];
					sx += imageP.getWidth();
					if (j == plots[0].length - 1)
						sy += imageP.getHeight() + offsets[i][j][1];
				}
			}
		}
	}

	public byte[] renderPNG(float compression) {
		try {
			buf.reset();
			final MemoryCacheImageOutputStream out = new MemoryCacheImageOutputStream(buf);
			final ImageWriteParam param = writer.getDefaultWriteParam();
			writer.setOutput(out);
			param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			param.setCompressionQuality(compression);
			if (isAborted())
				return null;
			writer.write(getBufferedImage(), param);
			out.close();
			if (isAborted())
				return null;
			return buf.toByteArray();
		} catch (Exception e) {
			LH.warning(log, "Error producing image", e);
		}
		return null;
	}
	public void drawText(String str, double xp, double yp, int rotate, byte alignX, byte alignY, Color color, int fontSize, String fontFamily, String fontStyle,
			boolean clipToWindow, int padding) {
		int x = scaleX(xp);
		int y = scaleY(yp);
		drawTextDirect(str, x, y, rotate, alignX, alignY, color, fontSize, fontFamily, fontStyle, false, clipToWindow, padding);
	}
	public boolean drawTextIfSpace(String str, double xp, double yp, int rotate, byte alignX, byte alignY, Color color, int fontSize, String fontFamily, String fontStyle,
			boolean clipToWindow, int padding) {
		int x = scaleX(xp);
		int y = scaleY(yp);
		return drawTextDirect(str, x, y, rotate, alignX, alignY, color, fontSize, fontFamily, fontStyle, true, clipToWindow, padding);
	}
	public boolean drawTextIfSpaceDirect(String str, int x, int y, int rotate, byte alignX, byte alignY, Color color, int fontSize, String fontFamily, String fontStyle,
			boolean clipToWindow, int padding) {
		return drawTextDirect(str, x, y, rotate, alignX, alignY, color, fontSize, fontFamily, fontStyle, true, clipToWindow, padding);
	}

	private String fontName;
	private int fontSize;

	private boolean setFont(String fontName, String fontStyle, int fontSize) {
		if (OH.eq(this.fontName, fontName) && OH.eq(this.fontSize, fontSize))
			return false;
		this.fontName = fontName;
		this.fontSize = fontSize;
		final String f2 = this.fontMappings.get(fontName);
		if (SH.isnt(fontStyle) || SH.indexOf(fontStyle, "normal", 0) != -1) {
			g2d.setFont(new Font(f2 != null ? f2 : fontName, 0, fontSize));
			return true;
		}

		if (SH.indexOf(fontStyle, "underline", 0) != -1) {
			Map<TextAttribute, Integer> fontAttributes = new HashMap<TextAttribute, Integer>();
			fontAttributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
			if (SH.indexOf(fontStyle, "bold", 0) != -1 && SH.indexOf(fontStyle, "italic", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.BOLD | Font.ITALIC, fontSize).deriveFont(fontAttributes));
			else if (SH.indexOf(fontStyle, "bold", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.BOLD, fontSize).deriveFont(fontAttributes));
			else if (SH.indexOf(fontStyle, "italic", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.ITALIC, fontSize).deriveFont(fontAttributes));
			else
				g2d.setFont(new Font(f2 != null ? f2 : fontName, 0, fontSize).deriveFont(fontAttributes));
		} else {
			if (SH.indexOf(fontStyle, "bold", 0) != -1 && SH.indexOf(fontStyle, "italic", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.BOLD | Font.ITALIC, fontSize));
			else if (SH.indexOf(fontStyle, "bold", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.BOLD, fontSize));
			else if (SH.indexOf(fontStyle, "italic", 0) != -1)
				g2d.setFont(new Font(f2 != null ? f2 : fontName, Font.ITALIC, fontSize));
			else
				g2d.setFont(new Font(f2 != null ? f2 : fontName, 0, fontSize));
		}
		return true;
	}
	private boolean drawTextDirect(String str, int x, int y, int rotate, byte alignX, byte alignY, Color color, int fontSize, String fontFamily, String fontStyle,
			boolean onlyIfSpace, boolean clipToWindow, int padding) {
		if (fontSize <= 0)
			return false;
		if (x < -100 || x > width + 100 || y < -100 | y > height + 100)
			return false;
		boolean debug = false;
		if (debug)
			str += ": " + alignX + "," + alignY;
		setFont(fontFamily, fontStyle, fontSize);
		g2d.setColor((color));
		FontMetrics fontMetrics = g2d.getFontMetrics();
		if (debug)
			g2d.drawOval(x - 1, y - 1, 2, 2);
		int w = fontMetrics.stringWidth(str);
		int h = fontMetrics.getAscent() + fontMetrics.getDescent();
		if (rotate < 0)
			rotate = 360 - ((-rotate) % 360);
		else
			rotate %= 360;
		if (rotate > 90 && rotate < 270) {
			rotate = (rotate + 180) % 360;
			alignX *= -1;
			alignY *= -1;
		}

		int xPos, yPos;
		if (alignX == ALIGN_RIGHT)
			xPos = x - w - padding;
		else if (alignX == ALIGN_MIDDLE)
			xPos = x - w / 2;
		else
			xPos = x + padding;

		if (alignY == ALIGN_TOP)
			yPos = y + h + padding;
		else if (alignY == ALIGN_MIDDLE)
			yPos = y + h / 2;
		else
			yPos = y - padding;

		if (xPos + w < 0 || xPos > width)
			return false;
		if (yPos + y < 0 || yPos > height + h)
			return false;
		if (clipToWindow) {
			if (width - w <= 0 || height < h)
				return false;
			xPos = MH.clip(xPos, 0, width - w);
			yPos = MH.clip(yPos, h, height);
		}
		if (onlyIfSpace) {
			for (int i = textRectLites.size() - 1; i >= 0; i--)
				if (RectLite.intersects(textRectLites.getLong(i), xPos, yPos, w, h))
					return false;
			textRectLites.add(RectLite.toRect(xPos, yPos, w, h));
		}

		if (!antialias)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		if (debug)
			g2d.drawRect(xPos, yPos - h, w, h);
		if (rotate == 0) {
			g2d.drawString(str, xPos, yPos - fontMetrics.getDescent());
		} else {
			double rotate2 = (rotate * Math.PI / 180);
			g2d.rotate(rotate2, x, y);
			g2d.drawString(str, xPos, yPos - fontMetrics.getDescent());
			g2d.rotate(-rotate2, x, y);
		}
		if (!antialias)
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
		return true;
	}

	private final LongArrayList textRectLites = new LongArrayList();

	public void drawTriangleDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));
		int xPoints[] = new int[] { x, x + w / 2, x + w };
		int yPoints[] = new int[] { y + h, y, y + h };
		g2d.drawPolygon(xPoints, yPoints, 3);

	}
	public void fillTriangleDirect(int x, int y, int w, int h, Color fillColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((fillColor));
		int xPoints[] = new int[] { x, x + w / 2, x + w };
		int yPoints[] = new int[] { y + h, y, y + h };
		g2d.fillPolygon(xPoints, yPoints, 3);
	}
	public void drawSquareDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));
		g2d.drawRect(x, y, w, h);
	}
	public void fillSquareDirect(int x, int y, int w, int h, Color fillColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((fillColor));
		g2d.fillRect(x, y, w, h);
	}

	public void drawOvalDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor(strokeColor);
		g2d.drawOval(x, y, w, h);
	}
	public void fillOvalDirect(int x, int y, int w, int h, Color fillColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor(fillColor);
		g2d.fillOval(x, y, w, h);
	}
	
	public void drawDiamondDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0) {
			return;
		}
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));
		int xPoints[] = new int[] { x, x + w / 2, x + w, x + w / 2 };
		int yPoints[] = new int[] { y + h / 2, y + h, y + h / 2, y };
		g2d.drawPolygon(xPoints, yPoints, 4);
	};

	public void fillDiamondDirect(int x, int y, int w, int h, Color strokeColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((strokeColor));
		int xPoints[] = new int[] { x, x + w / 2, x + w, x + w / 2 };
		int yPoints[] = new int[] { y + h / 2, y + h, y + h / 2, y };
		g2d.fillPolygon(xPoints, yPoints, 4);
	};

	public void drawCrossDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0) {
			return;
		}
		int x1 = x;
		int y1 = y;
		int x2 = x + w;
		int y2 = y + h;
		int x3 = x;
		int y3 = y + h;
		int x4 = x + w;
		int y4 = y;
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));
		g2d.drawLine(x1, y1, x2, y2);
		g2d.drawLine(x3, y3, x4, y4);
	}

	public void fillCrossDirect(int x, int y, int w, int h, Color strokeColor) {
		g2d.setColor((strokeColor));
		int x1 = x;
		int y1 = y;
		int x2 = x + w;
		int y2 = y + h;
		int x3 = x;
		int y3 = y + h;
		int x4 = x + w;
		int y4 = y;
		g2d.drawLine(x1, y1, x2, y2);
		g2d.drawLine(x3, y3, x4, y4);
	}

	public void drawHexagonDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0) {
			return;
		}
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));

		int xPoints[] = new int[] { x, x + w / 4, x + w * 3 / 4, x + w, x + w * 3 / 4, x + w / 4 };
		int yPoints[] = new int[] { y + h / 2, h + y, h + y, y + h / 2, y, y };

		g2d.drawPolygon(xPoints, yPoints, 6);
	}

	public void fillHexagonDirect(int x, int y, int w, int h, Color strokeColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((strokeColor));

		int xPoints[] = new int[] { x, x + w / 4, x + w * 3 / 4, x + w, x + w * 3 / 4, x + w / 4 };
		int yPoints[] = new int[] { y + h / 2, h + y, h + y, y + h / 2, y, y };

		g2d.fillPolygon(xPoints, yPoints, 6);
	};

	public void drawPentagonDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (borderSize <= 0) {
			return;
		}
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));

		int xPoints[] = new int[] { x, x + w / 2, x + w, x + w * 3 / 4, x + w / 4 };
		int yPoints[] = new int[] { y + h * 2 / 5, y, y + h * 2 / 5, y + h, y + h };

		g2d.drawPolygon(xPoints, yPoints, 5);
	}

	public void fillPentagonDirect(int x, int y, int w, int h, Color strokeColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((strokeColor));

		int xPoints[] = new int[] { x, x + w / 2, x + w, x + w * 3 / 4, x + w / 4 };
		int yPoints[] = new int[] { y + h * 2 / 5, y, y + h * 2 / 5, y + h, y + h };

		g2d.fillPolygon(xPoints, yPoints, 5);
	};

	public void drawTickDirect(int x, int y, int w, int h, int borderSize, Color strokeColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((strokeColor));
		g2d.drawLine(x, y + h / 2, x + w / 2, y + h);
		g2d.drawLine(x + w / 2, y + h, x + w, y);
	}

	public void fillTickDirect(int x, int y, int w, int h, Color strokeColor) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.setColor((strokeColor));
		g2d.drawLine(x, y + h / 2, x + w / 2, y + h);
		g2d.drawLine(x + w / 2, y + h, x + w, y);
	}

	public void drawTriangle(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawTriangleDirect(_x, _y, _w, _h, borderSize, strokeColor);

	}
	public void fillTriangle(double x, double y, double w, double h, Color fillColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillTriangleDirect(_x, _y, _w, _h, fillColor);
	}
	public void drawSquare(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawSquareDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	public void fillSquare(double x, double y, double w, double h, Color fillColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillSquareDirect(_x, _y, _w, _h, fillColor);
	}

	public void drawOval(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		if (borderSize <= 0)
			return;
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawOvalDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}

	public void fillOval(double x, double y, double w, double h, Color fillColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillOvalDirect(_x, _y, _w, _h, fillColor);
	}
	
	public void drawDiamond(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawDiamondDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	
	public void fillDiamond(double x, double y, double w, double h, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillDiamondDirect(_x, _y, _w, _h, strokeColor);
	}
	
	public void drawCross(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawCrossDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	
	public void fillCross(double x, double y, double w, double h, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillCrossDirect(_x, _y, _w, _h, strokeColor);
	}
	
	public void drawHexagon(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawHexagonDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	
	public void fillHexagon(double x, double y, double w, double h, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillHexagonDirect(_x, _y, _w, _h, strokeColor);
	}
	
	public void drawPentagon(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawPentagonDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	
	public void fillPentagon(double x, double y, double w, double h, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillPentagonDirect(_x, _y, _w, _h, strokeColor);
	}
	
	public void drawTick(double x, double y, double w, double h, int borderSize, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		drawTickDirect(_x, _y, _w, _h, borderSize, strokeColor);
	}
	
	public void fillTick(double x, double y, double w, double h, Color strokeColor) {
		int _x = scaleX(x);
		int _y = scaleY(y);
		int _w = scaleW(w);
		int _h = scaleH(h);
		fillTickDirect(_x, _y, _w, _h, strokeColor);
	}

	private static final BasicStroke[] STROKES = new BasicStroke[400];
	static {
		for (int dashSize = 0; dashSize < 20; dashSize++)
			for (int strokeSize = 0; strokeSize < 20; strokeSize++)
				STROKES[dashSize * 20 + strokeSize] = createStroke(strokeSize, dashSize);
	}

	public Stroke toBasicStroke(int strokeSize) {
		return toBasicStroke(strokeSize, 0);
	}
	public Stroke toBasicStroke(int strokeSize, int dashSize) {
		//		return new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
		if (strokeSize < 0)
			return STROKES[0];
		if (dashSize < 0)
			dashSize = 0;
		if (strokeSize >= 20 || dashSize >= 20)
			return createStroke(strokeSize, dashSize);
		return STROKES[dashSize * 20 + strokeSize];
	}
	private static BasicStroke createStroke(int strokeSize, int dashSize) {
		return new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, dashSize == 0 || strokeSize == 0 ? null : new float[] { (dashSize + 1) * strokeSize },
				0);
	}

	public void drawPolygon(AmiWebChartLine line) {
		drawPolygon(line.getX(), line.getY(), 0, line.getLength(), line.getLineSize(), line.getColor(), line.getDashSize(), line.getLineType());
	}
	public void drawPolygon(double dx[], double dy[], int start, int end, int[] lineSize, Color[] color, int[] dashSize, byte lineType) {
		int len = end - start;
		if (len < 2)
			return;
		int x[] = null;
		if (lineType == LINE_TYPE_DIRECT || lineType == LINE_TYPE_HORZ_QUAD_BEZIER || lineType == LINE_TYPE_VERT_QUAD_BEZIER || lineType == LINE_TYPE_HORZ_CUBIC_BEZIER
				|| lineType == LINE_TYPE_VERT_CUBIC_BEZIER)
			x = new int[len];
		else
			x = new int[len * 2];
		int y[] = new int[x.length];
		int lastLineSize = lineSize[0];
		Color lastColor = color[0];
		int lastDashSize = dashSize[0];
		int dashedLinesCount = 0;
		x[0] = scaleX(dx[0]);
		y[0] = scaleY(dy[0]);
		int lines = 0;
		Path2D.Double path = new Path2D.Double();
		for (int n = start + 1; n < end; n++) {
			int xp = scaleX(dx[n]);
			int yp = scaleY(dy[n]);

			boolean outside = isOutside(x[lines], y[lines], xp, yp, lastLineSize);
			if (outside || dashedLinesCount > 1000 || lineSize[n] != lastLineSize || dashSize[n] != lastDashSize || OH.ne(color[n], lastColor)) {
				if (!outside) {
					lines++;
					switch (lineType) {
						case LINE_TYPE_DIRECT:
							x[lines] = xp;
							y[lines] = yp;
							break;
						case LINE_TYPE_VERT:
							x[lines] = x[lines - 1];
							y[lines] = yp;
							lines++;
							x[lines] = xp;
							y[lines] = yp;
							break;
						case LINE_TYPE_HORZ:
							y[lines] = y[lines - 1];
							x[lines] = xp;
							lines++;
							x[lines] = xp;
							y[lines] = yp;
							break;
						case LINE_TYPE_HORZ_QUAD_BEZIER: {
							x[lines] = xp;
							y[lines] = yp;
							int quad_xlast = x[lines - 1];
							int quad_ylast = y[lines - 1];
							double quad_ctrly = yp < quad_ylast ? (xp > quad_xlast ? yp : quad_ylast) : (xp > quad_xlast ? quad_ylast : yp);
							double quad_ctrlx = (quad_xlast + xp) / 2.0;
							path.moveTo(quad_xlast, quad_ylast);
							path.curveTo(quad_ctrlx, quad_ctrly, quad_ctrlx, quad_ctrly, xp, yp);
							break;
						}
						case LINE_TYPE_VERT_QUAD_BEZIER: {
							x[lines] = xp;
							y[lines] = yp;
							int quad_xlast = x[lines - 1];
							int quad_ylast = y[lines - 1];
							double quad_ctrly = (quad_ylast + yp) / 2.0;
							double quad_ctrlx = xp < quad_xlast ? (yp > quad_ylast ? xp : quad_xlast) : (yp > quad_ylast ? quad_xlast : xp);
							path.moveTo(quad_xlast, quad_ylast);
							path.curveTo(quad_ctrlx, quad_ctrly, quad_ctrlx, quad_ctrly, xp, yp);
							break;
						}
						case LINE_TYPE_HORZ_CUBIC_BEZIER: {
							x[lines] = xp;
							y[lines] = yp;
							int xlast = x[lines - 1];
							int ylast = y[lines - 1];
							double _ctrlx = (xlast + xp) / 2.0;
							path.moveTo(xlast, ylast);
							path.curveTo(_ctrlx, ylast, _ctrlx, yp, xp, yp);
							break;
						}
						case LINE_TYPE_VERT_CUBIC_BEZIER: {
							x[lines] = xp;
							y[lines] = yp;
							int xlast = x[lines - 1];
							int ylast = y[lines - 1];
							double _ctrly = (ylast + yp) / 2.0;
							path.moveTo(xlast, ylast);
							path.curveTo(xlast, _ctrly, xp, _ctrly, xp, yp);
							break;
						}
					}
				}
				if (lines > 0 && lastLineSize > 0) {
					g2d.setStroke(toBasicStroke(lastLineSize, lastDashSize));
					g2d.setColor(lastColor);

					if (lineType != LINE_TYPE_HORZ_CUBIC_BEZIER && lineType != LINE_TYPE_VERT_CUBIC_BEZIER && lineType != LINE_TYPE_HORZ_QUAD_BEZIER
							&& lineType != LINE_TYPE_VERT_QUAD_BEZIER) {
						g2d.drawPolyline(x, y, lines + 1);
					} else {
						g2d.draw(path);
						path.reset();
					}
				}
				lastLineSize = lineSize[n];
				lastDashSize = dashSize[n];
				lastColor = color[n];
				dashedLinesCount = 0;
				x[0] = xp;
				y[0] = yp;
				lines = 0;
			} else {
				if (lastDashSize == 0)
					dashedLinesCount++;
				else
					dashedLinesCount += Math.max(1, distance(xp, yp, x[lines], y[lines], lineType) / (3 * lastLineSize * lastDashSize));
				lines++;
				switch (lineType) {
					case LINE_TYPE_DIRECT:
						x[lines] = xp;
						y[lines] = yp;
						break;
					case LINE_TYPE_VERT:
						x[lines] = x[lines - 1];
						y[lines] = yp;
						lines++;
						x[lines] = xp;
						y[lines] = yp;
						break;
					case LINE_TYPE_HORZ:
						y[lines] = y[lines - 1];
						x[lines] = xp;
						lines++;
						x[lines] = xp;
						y[lines] = yp;
						break;
					case LINE_TYPE_HORZ_QUAD_BEZIER: {
						x[lines] = xp;
						y[lines] = yp;
						int xlast = x[lines - 1];
						int ylast = y[lines - 1];
						double ctrly = yp < ylast ? (xp > xlast ? yp : ylast) : (xp > xlast ? ylast : yp);
						double ctrlx = (xlast + xp) / 2.0;
						path.moveTo(xlast, ylast);
						path.curveTo(ctrlx, ctrly, ctrlx, ctrly, xp, yp);
						break;
					}
					case LINE_TYPE_VERT_QUAD_BEZIER: {
						x[lines] = xp;
						y[lines] = yp;
						int xlast = x[lines - 1];
						int ylast = y[lines - 1];
						double ctrly = (ylast + yp) / 2.0;
						double ctrlx = xp < xlast ? (yp > ylast ? xp : xlast) : (yp > ylast ? xlast : xp);
						path.moveTo(xlast, ylast);
						path.curveTo(ctrlx, ctrly, ctrlx, ctrly, xp, yp);
						break;
					}
					case LINE_TYPE_HORZ_CUBIC_BEZIER: {
						x[lines] = xp;
						y[lines] = yp;
						int xlast = x[lines - 1];
						int ylast = y[lines - 1];
						double ctrlx = (xlast + xp) / 2.0;
						path.moveTo(xlast, ylast);
						path.curveTo(ctrlx, ylast, ctrlx, yp, xp, yp);
						break;
					}
					case LINE_TYPE_VERT_CUBIC_BEZIER: {
						x[lines] = xp;
						y[lines] = yp;
						int xlast = x[lines - 1];
						int ylast = y[lines - 1];
						double ctrly = (ylast + yp) / 2.0;
						path.moveTo(xlast, ylast);
						path.curveTo(xlast, ctrly, xp, ctrly, xp, yp);
						break;
					}
				}
			}
		}
		if (lines > 0 && lastLineSize > 0)

		{
			g2d.setStroke(toBasicStroke(lastLineSize, lastDashSize));
			g2d.setColor(lastColor);

			if (lineType != LINE_TYPE_HORZ_CUBIC_BEZIER && lineType != LINE_TYPE_VERT_CUBIC_BEZIER && lineType != LINE_TYPE_HORZ_QUAD_BEZIER
					&& lineType != LINE_TYPE_VERT_QUAD_BEZIER) {
				g2d.drawPolyline(x, y, lines + 1);
			} else {
				g2d.draw(path);
				path.reset();
			}
		}
	}

	private boolean isOutside(int x1, int y1, int x2, int y2, int padding) {
		if (x1 < -padding && x2 < -padding)
			return true;
		if (x1 > width + padding && x2 > width + padding)
			return true;
		if (y1 < -padding && y2 < -padding)
			return true;
		if (y1 > height + padding && y2 > height + padding)
			return true;
		return false;
	}
	private double distance(int x1, int y1, int x2, double y2, byte type) {
		double xd = (x1) - (x2);
		double yd = (y1) - (y2);
		if (xd == 0)
			return Math.abs(yd);
		if (yd == 0)
			return Math.abs(xd);
		if (type != LINE_TYPE_DIRECT)
			return Math.abs(xd) + Math.abs(yd);
		//TODO if type is LINE_TYPE_BEZIER ... to calculate distance is harder
		return Math.sqrt(xd * xd + yd * yd);
	}
	private void drawPolygon(int x[], int y[], int start, int end, int lineSize, Color strokeColor, int dashSize, byte lineType) {
		if (lineSize <= 0)
			return;
		int len = end - start;
		if (len < 2)
			return;
		final int[] ix;
		final int[] iy;
		switch (lineType) {
			case LINE_TYPE_DIRECT:
				ix = new int[len];
				iy = new int[len];
				for (int src = start, dst = 0; src < end; src++, dst++) {
					ix[dst] = (x[src]);
					iy[dst] = (y[src]);
				}
				break;
			case LINE_TYPE_VERT:
				ix = new int[len * 2 - 1];
				iy = new int[len * 2 - 1];
				for (int src = start, dst = 0; src < end; src++, dst += 2) {
					int xpos = (x[src]);
					int ypos = (y[src]);
					ix[dst] = xpos;
					iy[dst] = ypos;
					if (dst < ix.length - 1)
						ix[dst + 1] = xpos;
					if (dst > 0)
						iy[dst - 1] = ypos;
				}
				break;
			case LINE_TYPE_HORZ:
				ix = new int[len * 2 - 1];
				iy = new int[len * 2 - 1];
				for (int src = start, dst = 0; src < end; src++, dst += 2) {
					int xpos = (x[src]);
					int ypos = (y[src]);
					ix[dst] = xpos;
					iy[dst] = ypos;
					if (dst < iy.length - 1)
						iy[dst + 1] = ypos;
					if (dst > 0)
						ix[dst - 1] = xpos;
				}
				break;
			//TODO: if this function will be used, need to add support for LINE_TYPE_BEZIER
			default:
				throw new RuntimeException("bad line type: " + lineType);
		}

		g2d.setStroke(toBasicStroke(lineSize, dashSize));
		g2d.setColor(strokeColor);
		g2d.drawPolyline(ix, iy, ix.length);
	}

	public void drawLine(double x1, double y1, double x2, double y2, int lineSize, Color strokeColor, byte lineType) {
		if (lineSize <= 0)
			return;
		Stroke stroke = toBasicStroke(lineSize);
		drawLineHelper(x1, y1, x2, y2, stroke, strokeColor, lineType);
	}

	public void drawLineDashed(double x1, double y1, double x2, double y2, int lineSize, int dashSize, Color strokeColor, byte lineType) {
		if (lineSize <= 0)
			return;
		Stroke stroke = toBasicStroke(lineSize, dashSize);
		drawLineHelper(x1, y1, x2, y2, stroke, strokeColor, lineType);
	}

	private void drawLineHelper(double x1, double y1, double x2, double y2, Stroke lineStroke, Color strokeColor, byte lineType) {

		g2d.setStroke(lineStroke);
		g2d.setColor(strokeColor);
		int _x1 = scaleX(x1);
		int _y1 = scaleY(y1);
		int _x2 = scaleX(x2);
		int _y2 = scaleY(y2);
		switch (lineType) {
			case LINE_TYPE_DIRECT:
				g2d.drawLine(_x1, _y1, _x2, _y2);
				break;
			case LINE_TYPE_VERT:
				g2d.drawLine(_x1, _y1, _x1, _y2);
				g2d.drawLine(_x1, _y2, _x2, _y2);
				break;
			case LINE_TYPE_HORZ:
				g2d.drawLine(_x1, _y1, _x2, _y1);
				g2d.drawLine(_x2, _y1, _x2, _y2);
				break;
			case LINE_TYPE_HORZ_QUAD_BEZIER: {
				QuadCurve2D quad = new QuadCurve2D.Double();
				double ctrlx = (_x1 + _x2) / 2.0;
				double ctrly = _y2 < _y1 ? (_x2 > _x1 ? _y2 : _y1) : (_x2 > _x1 ? _y1 : _y2);
				quad.setCurve(_x1, _y1, ctrlx, ctrly, _x2, _y2);
				g2d.draw(quad);
				break;
			}
			case LINE_TYPE_VERT_QUAD_BEZIER: {
				QuadCurve2D quad = new QuadCurve2D.Double();
				double ctrlx = _x2 < _x1 ? (_y2 > _y1 ? _x2 : _x1) : (_y2 > _y1 ? _x1 : _x2);
				double ctrly = (_y1 + _y2) / 2.0;
				quad.setCurve(_x1, _y1, ctrlx, ctrly, _x2, _y2);
				g2d.draw(quad);
				break;
			}
			case LINE_TYPE_HORZ_CUBIC_BEZIER: {
				CubicCurve2D c = new CubicCurve2D.Double();
				double ctrlx = (_x1 + _x2) / 2.0;
				c.setCurve(_x1, _y1, ctrlx, _y1, ctrlx, _y2, _x2, _y2);
				g2d.draw(c);
				break;
			}
			case LINE_TYPE_VERT_CUBIC_BEZIER: {
				CubicCurve2D c = new CubicCurve2D.Double();
				double ctrly = (_y1 + _y2) / 2.0;
				c.setCurve(_x1, _y1, _x1, ctrly, _x2, ctrly, _x2, _y2);
				g2d.draw(c);
				break;
			}
		}
	}

	public boolean isAntialias() {
		return antialias;
	}

	public void setAntialias(boolean antialias) {
		if (this.antialias == antialias)
			return;
		this.antialias = antialias;
		if (this.g2d != null) {
			applyAntialias(antialias, g2d);
		}
	}

	private int[] tmpX = new int[10];
	private int[] tmpY = new int[10];

	public void fillPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, double x5, double y5, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpX[4] = scaleX(x4);
		tmpX[5] = scaleX(x5);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		tmpY[4] = scaleY(y4);
		tmpY[5] = scaleY(y5);
		g2d.fillPolygon(tmpX, tmpY, 6);
	}
	public void fillPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpX[4] = scaleX(x4);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		tmpY[4] = scaleY(y4);
		g2d.fillPolygon(tmpX, tmpY, 5);
	}

	public void fillCurvedHorzCubicPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		Path2D.Double path = new Path2D.Double();
		double ctrlxa = (tmpX[0] + tmpX[1]) / 2.0;
		path.moveTo(tmpX[0], tmpY[0]);
		path.curveTo(ctrlxa, tmpY[0], ctrlxa, tmpY[1], tmpX[1], tmpY[1]);
		path.lineTo(tmpX[2], tmpY[2]);
		double ctrlxb = (tmpX[2] + tmpX[3]) / 2.0;
		path.curveTo(ctrlxb, tmpY[2], ctrlxb, tmpY[3], tmpX[3], tmpY[3]);
		path.lineTo(tmpX[0], tmpY[0]);
		path.closePath();
		g2d.fill(path);
	}
	/*
	 * 
	 * 			case LINE_TYPE_VERT_CUBIC_BEZIER: {
				CubicCurve2D c = new CubicCurve2D.Double();
				double ctrly = (_y1 + _y2) / 2.0;
				c.setCurve(_x1, _y1, _x1, ctrly, _x2, ctrly, _x2, _y2);
				g2d.draw(c);
				break;
			}
	 */
	public void fillCurvedVertCubicPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		Path2D.Double path = new Path2D.Double();
		double ctrlya = (tmpY[0] + tmpY[1]) / 2.0;
		path.moveTo(tmpX[0], tmpY[0]);
		path.curveTo(tmpX[0], ctrlya, tmpX[1], ctrlya, tmpX[1], tmpY[1]);
		path.lineTo(tmpX[2], tmpY[2]);
		double ctrlyb = (tmpY[2] + tmpY[3]) / 2.0;
		path.curveTo(tmpX[2], ctrlyb, tmpX[3], ctrlyb, tmpX[3], tmpY[3]);
		path.lineTo(tmpX[0], tmpY[0]);
		path.closePath();
		g2d.fill(path);
	}
	public void fillCurvedHorzQuadPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		Path2D.Double path = new Path2D.Double();
		double ctrlxa = (tmpX[0] + tmpX[1]) / 2.0;
		double ctrlya = tmpY[1] < tmpY[0] ? (tmpX[1] > tmpX[0] ? tmpY[1] : tmpY[0]) : (tmpX[1] > tmpX[0] ? tmpY[0] : tmpY[1]);
		path.moveTo(tmpX[0], tmpY[0]);
		path.curveTo(ctrlxa, ctrlya, ctrlxa, ctrlya, tmpX[1], tmpY[1]);
		path.lineTo(tmpX[2], tmpY[2]);
		double ctrlxb = (tmpX[2] + tmpX[3]) / 2.0;
		double ctrlyb = tmpY[3] < tmpY[2] ? (tmpX[3] > tmpX[2] ? tmpY[2] : tmpY[3]) : (tmpX[3] > tmpX[2] ? tmpY[3] : tmpY[2]);
		path.curveTo(ctrlxb, ctrlyb, ctrlxb, ctrlyb, tmpX[3], tmpY[3]);
		path.lineTo(tmpX[0], tmpY[0]);
		path.closePath();
		g2d.fill(path);
	}

	public void fillCurvedVertQuadPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		Path2D.Double path = new Path2D.Double();
		double ctrlxa = tmpX[1] < tmpX[0] ? (tmpY[1] > tmpY[0] ? tmpX[1] : tmpX[0]) : (tmpY[1] > tmpY[0] ? tmpX[0] : tmpX[1]);
		double ctrlya = (tmpY[0] + tmpY[1]) / 2.0;
		path.moveTo(tmpX[0], tmpY[0]);
		path.curveTo(ctrlxa, ctrlya, ctrlxa, ctrlya, tmpX[1], tmpY[1]);
		path.lineTo(tmpX[2], tmpY[2]);
		double ctrlxb = tmpX[3] < tmpX[2] ? (tmpY[3] > tmpY[2] ? tmpX[2] : tmpX[3]) : (tmpY[3] > tmpY[2] ? tmpX[3] : tmpX[2]);
		double ctrlyb = (tmpY[2] + tmpY[3]) / 2.0;
		path.curveTo(ctrlxb, ctrlyb, ctrlxb, ctrlyb, tmpX[3], tmpY[3]);
		path.lineTo(tmpX[0], tmpY[0]);
		path.closePath();
		g2d.fill(path);
	}

	public void fillPolygon(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpX[3] = scaleX(x3);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		tmpY[3] = scaleY(y3);
		g2d.fillPolygon(tmpX, tmpY, 4);
	}
	public void fillPolygon(double x0, double y0, double x1, double y1, double x2, double y2, Color color) {
		g2d.setColor((color));
		tmpX[0] = scaleX(x0);
		tmpX[1] = scaleX(x1);
		tmpX[2] = scaleX(x2);
		tmpY[0] = scaleY(y0);
		tmpY[1] = scaleY(y1);
		tmpY[2] = scaleY(y2);
		g2d.fillPolygon(tmpX, tmpY, 4);
	}

	public void fillPolygon(double[] xPoints, double[] yPoints, Color t) {
		g2d.setColor((t));
		int[] _x, _y;
		int length = xPoints.length;
		if (length <= tmpX.length) {
			_x = tmpX;
			_y = tmpY;
		} else {
			_x = new int[length];
			_y = new int[length];
		}
		for (int i = 0; i < length; i++) {
			_x[i] = scaleX(xPoints[i]);
			_y[i] = scaleX(yPoints[i]);
		}
		g2d.fillPolygon(_x, _y, length);
	}

	public void drawWedge(double centerX, double centerY, double r1, double a1, double r2, double a2, double borderSize, Color borderColor) {
		if (borderSize <= 0)
			return;
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((borderColor));
		double a1r = a1 * Math.PI / 180;
		double a2r = a2 * Math.PI / 180;
		int _x1 = scaleX(centerX + radiansToX(r1, a1r));
		int _y1 = scaleY(centerY + radiansToY(r1, a1r));
		int _x2 = scaleX(centerX + radiansToX(r2, a1r));
		int _y2 = scaleY(centerY + radiansToY(r2, a1r));
		int _x3 = scaleX(centerX + radiansToX(r2, a2r));
		int _y3 = scaleY(centerY + radiansToY(r2, a2r));
		int _x4 = scaleX(centerX + radiansToX(r1, a2r));
		int _y4 = scaleY(centerY + radiansToY(r1, a2r));

		g2d.drawLine(_x1, _y1, _x2, _y2);
		drawArc(scaleX(centerX - r2), scaleY(centerY - r2), scaleW(r2 + r2), scaleH(r2 + r2), (int) (a2), (int) (a1 - a2));
		g2d.drawLine(_x3, _y3, _x4, _y4);
		drawArc(scaleX(centerX - r1), scaleY(centerY - r1), scaleW(r1 + r1), scaleH(r1 + r1), (int) (a2), (int) (a1 - a2));
	}
	public void fillWedge(double centerX, double centerY, double r1, double a1, double r2, double a2, Color fillColor) {
		g2d.setColor((fillColor));
		double a1r = a1 * Math.PI / 180;
		double a2r = a2 * Math.PI / 180;
		int _x1 = scaleX(centerX + radiansToX(r1, a1r));
		int _y1 = scaleY(centerY + radiansToY(r1, a1r));
		int _x2 = scaleX(centerX + radiansToX(r2, a1r));
		int _y2 = scaleY(centerY + radiansToY(r2, a1r));
		int _x4 = scaleX(centerX + radiansToX(r1, a2r));
		int _y4 = scaleY(centerY + radiansToY(r1, a2r));
		Path2D.Double path = new Path2D.Double();
		path.moveTo(_x1, _y1);
		path.lineTo(_x2, _y2);
		radiate(path, centerX, centerY, r2, a1r, r2, a2r);
		path.lineTo(_x4, _y4);
		radiate(path, centerX, centerY, r1, a2r, r1, a1r);
		g2d.fill(path);
		g2d.draw(path);
	}

	public void drawArc(double centerX, double centerY, double r1, double a1, double r2, double a2, double borderSize, Color borderColor) {
		if (borderSize <= 0)
			return;
		g2d.setStroke(toBasicStroke((int) borderSize));
		g2d.setColor((borderColor));
		double a = a1 * Math.PI / 180;
		double x1 = radiansToX(r1, a);
		double y1 = radiansToY(r1, a);
		if (a1 == a2) {
			double x2 = radiansToX(r2, a);
			double y2 = radiansToY(r2, a);
			g2d.drawLine(scaleX(x1 + centerX), scaleY(y1 + centerY), scaleX(x2 + centerX), scaleY(y2 + centerY));
		} else if (r1 == r2) {
			drawArc(scaleX(centerX - r1), scaleY(centerY - r1), scaleW(r1 + r1), scaleH(r1 + r1), (int) (a1), (int) (a2 - a1));
		} else {
			Path2D.Double path = new Path2D.Double();
			path.moveTo(scaleX(x1 + centerX), scaleY(y1 + centerY));
			radiate(path, centerX, centerY, r1, a, r2, a2 * Math.PI / 180);
			g2d.draw(path);
		}

	}
	private void drawArc(int x, int y, int w, int h, int startAngle, int arcAngle) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		g2d.drawArc(x, y, w, h, startAngle, arcAngle);

	}

	private void radiate(Path2D.Double path, double centerX, double centerY, double r1, double a1, double r2, double a2) {
		long parts = (long) Math.ceil(Math.abs(a2 - a1) / (Math.PI / 8));
		if (parts == 0) {
			double x2 = radiansToX(r2, a2);
			double y2 = radiansToY(r2, a2);
			path.lineTo(scaleX(centerX + x2), scaleY(centerY + y2));
		} else if (parts == 1) {
			radiate2(path, centerX, centerY, r1, a1, r2, a2);
		} else {
			double diffA = (a2 - a1) / parts;
			double diffX = (r2 - r1) / parts;
			for (int i = 0; i < parts; i++) {
				radiate2(path, centerX, centerY, r1 + diffX * i, a1 + diffA * i, r1 + diffX * (i + 1), a1 + diffA * (i + 1));
			}
		}
	}
	private void radiate2(Path2D.Double path, double centerX, double centerY, double r1, double a1, double r2, double a2) {
		double x0 = radiansToX(r1, a1);
		double y0 = radiansToY(r1, a1);
		double x2 = radiansToX(r2, a2);
		double y2 = radiansToY(r2, a2);
		double angle = (a1 + a2) / 2;
		double ax = (r1 + r2) / 2;
		double x1 = radiansToX(ax, angle);
		double y1 = radiansToY(ax, angle);
		double cpX = 2 * x1 - (x0 + x2) / 2;
		double cpY = 2 * y1 - (y0 + y2) / 2;
		path.quadTo(scaleX(centerX + cpX), scaleY(centerY + cpY), scaleX(centerX + x2), scaleY(centerY + y2));
	}
	public void fillArc(double centerX, double centerY, double r1, double a1, double r2, double a2, double r3, double a3, double r4, double a4, Color color) {
		Path2D.Double path = new Path2D.Double();
		a1 *= AmiWebChartUtils.PI_180;
		a2 *= AmiWebChartUtils.PI_180;
		a3 *= AmiWebChartUtils.PI_180;
		a4 *= AmiWebChartUtils.PI_180;
		double x1 = scaleX(centerX + radiansToX(r1, a1));
		double y1 = scaleY(centerY + radiansToY(r1, a1));
		double x3 = scaleX(centerX + radiansToX(r3, a3));
		double y3 = scaleY(centerY + radiansToY(r3, a3));

		g2d.setColor((color));
		path.moveTo(x1, y1);
		radiate(path, centerX, centerY, r1, a1, r2, a2);
		path.lineTo(x3, y3);
		radiate(path, centerX, centerY, r3, a3, r4, a4);
		path.lineTo(x1, y1);

		g2d.fill(path);
	}

	static public double radiansToX(double r, double a) {
		return r * Math.cos(a);
	}

	static public double radiansToY(double r, double a) {
		return -r * Math.sin(a);
	}

	public void abort() {
		this.writer.setAborted();
	}
	public int getWidth() {
		return this.width;
	}
	public int getHeight() {
		return this.height;
	}

	public boolean isAborted() {
		return this.writer.isAborted();
	}

	public AmiWebChartZoomMetrics getZoom() {
		return this.zoom;
	}

	public static double toRadians(double x, double y) {
		return normalizeAngle(-Math.atan2(y, x));
	}

	public static double toRadius(double x, double y) {
		return Math.sqrt(MH.sq(x) + MH.sq(y));
	}

	public static void main(String[] arg) {
		for (double a = -10; a < 10; a += .1) {
			double r = 17;
			System.out.println(normalizeAngle(a) + " ==> " + normalizeAngle(toRadians(radiansToX(r, a), radiansToY(r, a))));
		}

	}

	public static double normalizeAngle(double radians) {
		while (radians < 0)
			radians += Math.PI * 2;
		while (radians > Math.PI * 2)
			radians -= Math.PI * 2;
		return radians;
	}
	public BufferedImage getBufferedImage() {
		return bufferedImage;
	}
	public void setBufferedImage(BufferedImage bufferedImage) {
		this.bufferedImage = bufferedImage;
	}

}
