package com.f1.ami.amiscript;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import com.f1.base.Bytes;
import com.f1.base.CalcFrame;
import com.f1.utils.AH;
import com.f1.utils.ColorHelper;
import com.f1.utils.FastByteArrayInputStream;
import com.f1.utils.FastByteArrayOutputStream;
import com.f1.utils.ImageHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.WebRectangle;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.IntKeyMap.Node;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_Image extends AmiScriptBaseMemberMethods<BufferedImage> {
	private Graphics g;

	private AmiScriptMemberMethods_Image() {
		super();

		addMethod(this.INIT);
		addMethod(this.INIT2);
		addMethod(this.getWidth, "width");
		addMethod(this.getHeight, "height");
		addMethod(this.toBinary);
		addMethod(this.getPixel);
		addMethod(this.getPixelRgb);
		addMethod(this.getPixelRgba);
		addMethod(this.getPixelsRgb);
		addMethod(this.drawString);
		addMethod(this.drawLine);
		addMethod(this.drawArc);
		addMethod(this.drawOval);
		addMethod(this.drawPolygon);
		addMethod(this.drawPolyline);
		addMethod(this.drawRect);
		addMethod(this.drawRoundRect);
		addMethod(this.fillArc);
		addMethod(this.fillOval);
		addMethod(this.fillPolygon);
		addMethod(this.fillRect);
		addMethod(this.fillRoundRect);
		addMethod(this.translate);
		addMethod(this.setFont);
		addMethod(this.setColor);
		addMethod(this.setClip);
		addMethod(this.getColor, "color");
		addMethod(this.getFont, "font");
		addMethod(this.getClip, "clip");
		addMethod(this.toImage);
		addMethod(this.drawImage);
		addMethod(this.dispose);
		addCustomDebugProperty("data", List.class);
	}

	@Override
	protected Object getCustomDebugProperty(String name, BufferedImage value) {
		if ("data".equals(name)) {
			int iw = value.getWidth();
			int ih = value.getHeight();
			int a[] = new int[iw * ih];
			value.getRGB(0, 0, iw, ih, a, 0, iw);
			List<Integer> r = new ArrayList<Integer>(a.length);
			for (int i : a)
				r.add(i);
			return r;
		}
		return super.getCustomDebugProperty(name, value);
	}

	public final AmiAbstractMemberMethod<BufferedImage> INIT = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, null, BufferedImage.class, Bytes.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Bytes data = (Bytes) params[0];
			try {
				BufferedImage bi = ImageIO.read(new FastByteArrayInputStream(data.getBytes()));
				g = bi.getGraphics();
				return bi;
			} catch (IOException e) {
				return null;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "imageData" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "imageData" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new image, using supplied data. Note that an image can only be declared in Custom Methods (Dashboard --> Custom Methods...).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> INIT2 = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, null, BufferedImage.class, Integer.class,
			Integer.class, Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer w = (Integer) params[0];
			if (w == null || w < 0)
				return null;
			Integer h = (Integer) params[1];
			if (h == null || h < 0)
				return null;
			Boolean hasAlpha = Boolean.TRUE.equals((Boolean) params[2]);
			BufferedImage bi = new BufferedImage(w, h, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			g = bi.getGraphics();
			return bi;
		}
		protected String[] buildParamNames() {
			return new String[] { "width", "height", "hasTransparency" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "width in px", "height in px", "has Transparency, or known as alpha" };
		}
		@Override
		protected String getHelp() {
			return "Creates a new image with the given width and height and transparency options. Note that an image can only be declared in Custom Methods (Dashboard --> Custom Methods...).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getWidth = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getWidth", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getWidth();
		}
		@Override
		protected String getHelp() {
			return "Returns an Integer that is the width in pixels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getHeight = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getHeight", Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getHeight();
		}
		@Override
		protected String getHelp() {
			return "Returns an Integer that is the height in pixels.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getPixelsRgb = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getPixelsRgb", String.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			int iw = targetObject.getWidth();
			int ih = targetObject.getHeight();

			Integer x = (Integer) params[0];
			if (x == null || x < 0 || x > iw)
				return null;
			Integer y = (Integer) params[1];
			if (y == null || y < 0 || y > ih)
				return null;
			Integer w = (Integer) params[2];
			if (w == null || w < 0 || x + w > iw)
				return null;
			Integer h = (Integer) params[3];
			if (h == null || h < 0 || y + h > ih)
				return null;

			int a[] = new int[w * h];
			targetObject.getRGB(x, y, w, h, a, 0, iw);
			IntKeyMap<String> cache = new IntKeyMap<String>();
			List<String> r = new ArrayList<String>();
			for (int i = 0; i < a.length; i++) {
				int c = a[i];
				Node<String> node = cache.getNodeOrCreate(c);
				String val = node.getValue();
				if (val == null)
					node.setValue(val = ColorHelper.toRgbString(c));
				r.add(val);
			}
			return r;

		}
		protected String[] buildParamNames() {
			return new String[] { "x", "y", "w", "h" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position where left most is 0", "y position where top most is 0", "widht in px", "height in px" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color value of a specific pixel in the form #RRGGBB or #RRGGBBAA if there is an alpha component.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getPixel = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getPixel", String.class, Integer.class,
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer x = (Integer) params[0];
			if (x == null || x < 0 || x > targetObject.getWidth())
				return null;
			Integer y = (Integer) params[1];
			if (y == null || y < 0 || y > targetObject.getHeight())
				return null;
			return ColorHelper.toString(targetObject.getRGB(x, y));
		}
		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position where left most is 0", "y position where top most is 0" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color value of a specific pixel in the form #RRGGBB or #RRGGBBAA if there is an alpha component.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getPixelRgb = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getPixelRgb", String.class, Integer.class,
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer x = (Integer) params[0];
			if (x == null || x < 0 || x > targetObject.getWidth())
				return null;
			Integer y = (Integer) params[1];
			if (y == null || y < 0 || y > targetObject.getHeight())
				return null;
			return ColorHelper.toRgbString(targetObject.getRGB(x, y));
		}
		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position where left most is 0", "y position where top most is 0" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color value of a specific pixel in the form #RRGGBB";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> toBinary = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "toBinary", Bytes.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			String format = (String) params[0];
			FastByteArrayOutputStream out = new FastByteArrayOutputStream();
			try {
				ImageIO.write(targetObject, format == null ? "PNG" : format, out);
			} catch (IOException e) {
				return null;
			}
			return Bytes.valueOf(out.toByteArray());
		}
		protected String[] buildParamNames() {
			return new String[] { "format" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "format: JPEG,PNG, GIF, BMP, WBMP" };
		}
		@Override
		protected String getHelp() {
			return "Returns a binary representation of the image.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getPixelRgba = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getPixelRgba", String.class, Integer.class,
			Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Integer x = (Integer) params[0];
			if (x == null || x < 0 || x > targetObject.getWidth())
				return null;
			Integer y = (Integer) params[1];
			if (y == null || y < 0 || y > targetObject.getHeight())
				return null;
			return ColorHelper.toRgbaString(targetObject.getRGB(x, y));
		}
		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x position where left most is 0", "y position where top most is 0" };
		}
		@Override
		protected String getHelp() {
			return "Returns the color value of a specific pixel in the form #RRGGBBAA.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> drawString = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawString", Boolean.class, String.class,
			Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				String str = Caster_String.INSTANCE.cast(params[0]);
				int x = Caster_Integer.PRIMITIVE.cast(params[1]);
				int y = Caster_Integer.PRIMITIVE.cast(params[2]);
				g.drawString(str, x, y);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws the text given by the specified string. The baseline of the left most character is at position (x, y).";
		}

		protected String[] buildParamNames() {
			return new String[] { "str", "x", "y" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "string to be drawn", "x coordinate of the baseline", "y coordinate of the baseline" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};

	public final AmiAbstractMemberMethod<BufferedImage> drawLine = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawLine", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x1 = Caster_Integer.INSTANCE.cast(params[0]);
				int y1 = Caster_Integer.INSTANCE.cast(params[1]);
				int x2 = Caster_Integer.INSTANCE.cast(params[2]);
				int y2 = Caster_Integer.INSTANCE.cast(params[3]);
				g.drawLine(x1, y1, x2, y2);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws a line between the points (x1, y1) and (x2, y2).";
		}

		protected String[] buildParamNames() {
			return new String[] { "x1", "y1", "x2", "y2" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x1 coordinate", "y1 coordinate", "x2 coordinate", "y2 coordinate" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	public final AmiAbstractMemberMethod<BufferedImage> drawArc = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawArc", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				int startAngle = Caster_Integer.INSTANCE.cast(params[4]);
				int arcAngle = Caster_Integer.INSTANCE.cast(params[5]);
				g.drawArc(x, y, width, height, startAngle, arcAngle);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws a circular or elliptical arc covering the rectangle created using x, y, width, height, start angle and arc angle.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height", "startAngle", "arcAngle" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate of the upper-left corner of the arc", "y coordinate of the upper-left corner of the arc", "wdith of the arc", "height of the arc",
					"the beginning angle", "the angular extent of the arc, relative to the start angle" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> drawOval = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawOval", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				g.drawOval(x, y, width, height);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws an oval bounded by the rectangle using (x, y, width, height).";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate of the upper-left corner of the oval", "y coordinate of the upper-left corner of the oval", "wdith of the oval",
					"height of the oval" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};

	};
	public final AmiAbstractMemberMethod<BufferedImage> drawPolygon = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawPolygon", Boolean.class, List.class,
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				List<Integer> listX = (List<Integer>) params[0];
				List<Integer> listY = (List<Integer>) params[1];
				OH.assertEq(listX.size(), listY.size());
				int[] xPoints = new int[listX.size()];
				int[] yPoints = new int[listY.size()];
				for (int i = 0; i < listX.size(); i++) {
					xPoints[i] = Caster_Integer.INSTANCE.castOr(listX.get(i), 0);
					yPoints[i] = Caster_Integer.INSTANCE.castOr(listY.get(i), 0);
				}
				g.drawPolygon(xPoints, yPoints, xPoints.length);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws a closed polygon defined by lists of x and y coordinates. Closes the polygon if the last coordinate does not match the first.";
		}

		protected String[] buildParamNames() {
			return new String[] { "xPoints", "yPoints" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of x coordinates", "list of y coordinates" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> drawPolyline = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawPolyline", Boolean.class, List.class,
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				List<Integer> listX = (List<Integer>) params[0];
				List<Integer> listY = (List<Integer>) params[1];
				OH.assertEq(listX.size(), listY.size());
				int[] xPoints = new int[listX.size()];
				int[] yPoints = new int[listY.size()];
				for (int i = 0; i < listX.size(); i++) {
					xPoints[i] = Caster_Integer.INSTANCE.castOr(listX.get(i), 0);
					yPoints[i] = Caster_Integer.INSTANCE.castOr(listY.get(i), 0);
				}
				g.drawPolyline(xPoints, yPoints, xPoints.length);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws an outline of a polygon defined by lists of x and y coordinates. Does not close the polygon if the the last coordinate does not match the first.";
		}

		protected String[] buildParamNames() {
			return new String[] { "xPoints", "yPoints" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of x coordinates", "list of y coordinates" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> drawRect = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawRect", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				g.drawRect(x, y, width, height);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws the outline of the specified rectangle. The left and right edges are at x and width. The top and bottom edges are at y and height.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate", "width of the rectangle", "height of the rectangle" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> drawRoundRect = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawRoundRect", Boolean.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				int arcWidth = Caster_Integer.INSTANCE.cast(params[4]);
				int arcHeight = Caster_Integer.INSTANCE.cast(params[5]);
				g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws the outline of a specified rounded rectangle. The left and right edges are at x and width. The top and bottom edges are at y and height.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height", "arcWidth", "arcHeight" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate", "width of the rectangle", "height of the rectangle", "the horizontal diameter of the arc at the four corners",
					"the vertical diameter of the arc at the four corners" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	public final AmiAbstractMemberMethod<BufferedImage> fillArc = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "fillArc", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				int startAngle = Caster_Integer.INSTANCE.cast(params[4]);
				int arcAngle = Caster_Integer.INSTANCE.cast(params[5]);
				g.fillArc(x, y, width, height, startAngle, arcAngle);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Fills circular or elliptical arc covering the rectangle created using x, y, width, height, start angle and arc angle.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height", "startAngle", "arcAngle" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate of the upper-left corner of the arc", "y coordinate of the upper-left corner of the arc", "wdith of the arc", "height of the arc",
					"the beginning angle", "the angular extent of the arc, relative to the start angle" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> fillOval = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "fillOval", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				g.fillOval(x, y, width, height);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Fills an oval bounded by the rectangle using x, y, width, height. Ex: " + "Image i = new Image(100, 100, true);\r\n" + "  i.setColor(\"#ff0000\");\r\n"
					+ "  i.drawOval(0,0, 55, 50);\r\n" + "  i.fillOval(0,0,55, 50);";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate of the upper-left corner of the oval", "y coordinate of the upper-left corner of the oval", "wdith of the oval",
					"height of the oval" };
		}

		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> fillPolygon = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "fillPolygon", Boolean.class, List.class,
			List.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				List<Integer> nonCastX = (List<Integer>) params[0];
				List<Integer> nonCastY = (List<Integer>) params[1];
				OH.assertEq(nonCastX.size(), nonCastY.size());
				int[] xPoints = new int[nonCastX.size()];
				int[] yPoints = new int[nonCastY.size()];
				for (int i = 0; i < nonCastX.size(); i++) {
					xPoints[i] = Caster_Integer.INSTANCE.castOr(nonCastX.get(i), 0);
					yPoints[i] = Caster_Integer.INSTANCE.castOr(nonCastY.get(i), 0);
				}
				g.fillPolygon(xPoints, yPoints, xPoints.length);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Fills the outline of a polygon defined by x and y coordinates.";
		}

		protected String[] buildParamNames() {
			return new String[] { "xPoints", "yPoints" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "list of x coordinates", "list of y coordinates" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> fillRect = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "fillRect", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				g.fillRect(x, y, width, height);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Fills the outline of a specified rectangle. The left and right edges are at x and width. The top and bottom edges are at y and height.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate", "width of the rectangle", "height of the rectangle" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> fillRoundRect = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "fillRoundRect", Boolean.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				int arcWidth = Caster_Integer.INSTANCE.cast(params[4]);
				int arcHeight = Caster_Integer.INSTANCE.cast(params[5]);
				g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Fills the outline of a specified rounded rectangle. The left and right edges are at x and width. The top and bottom edges are at y and height.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height", "arcWidth", "arcHeight" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate", "width of the rectangle", "height of the rectangle", "the horizontal diameter of the arc at the four corners",
					"the vertical diameter of the arc at the four corners" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	public final AmiAbstractMemberMethod<BufferedImage> translate = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "translate", Boolean.class, Number.class,
			Number.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				g.translate(x, y);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Translates the origin of the graphic context to the point (x,y) in the current coordinate system. Returns true if operation is successful, false otherwise.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> setFont = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "setFont", Boolean.class, String.class,
			String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				String fontName = Caster_String.INSTANCE.cast(params[0]);
				String[] fontStyles = SH.split('|', Caster_String.INSTANCE.cast(params[1]));
				int fontSize = Caster_Integer.PRIMITIVE.cast(params[2]);
				if (SH.is(fontName) && (AH.isntEmpty(fontStyles))) {
					int styleCode = 0;
					for (String style : fontStyles) {
						if ("plain".equalsIgnoreCase(style))
							styleCode += Font.PLAIN;
						else if ("italic".equalsIgnoreCase(style))
							styleCode += Font.ITALIC;
						else if ("bold".equalsIgnoreCase(style))
							styleCode += Font.BOLD;
						else
							return false;
					}
					Font f = new Font(fontName, styleCode, fontSize);
					g.setFont(f);
					return true;
				}
			} catch (Exception e) {
				LH.warning(log, e);
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Sets this graphic context's font to specified font. Returns true if successful, false otherwise.";
		}

		protected String[] buildParamNames() {
			return new String[] { "fontName", "fontStyle", "fontSize" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "name of the font (i.e. serif, monospaced, verdana)", "PLAIN, ITALIC, BOLD (can also be used in combination with a pipe delimiter)",
					"size of the font" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> setColor = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "setColor", Boolean.class, String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				String hexColor = Caster_String.INSTANCE.cast(params[0]);
				if (SH.is(hexColor)) {
					g.setColor(ColorHelper.parseColor(hexColor));
					return true;
				}
			} catch (Exception e) {
				LH.warning(log, e);
			}
			return false;
		}

		@Override
		protected String getHelp() {
			return "Sets this graphics context's color to specified color. NUll is ignored. Returns true if successful, false otherwise.";
		}

		protected String[] buildParamNames() {
			return new String[] { "color" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "hexadecimal color code (i.e. #ff00ff)" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> setClip = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "setClip", Boolean.class, Integer.class,
			Integer.class, Integer.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				int x = Caster_Integer.INSTANCE.cast(params[0]);
				int y = Caster_Integer.INSTANCE.cast(params[1]);
				int width = Caster_Integer.INSTANCE.cast(params[2]);
				int height = Caster_Integer.INSTANCE.cast(params[3]);
				g.setClip(x, y, width, height);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Sets the current clip to the rectangle specified by the given coordinates(x,y). Returns true if successful, false otherwise.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate", "y coordinate", "width of the clip", "height of the clip" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};
	public final AmiAbstractMemberMethod<BufferedImage> getColor = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getColor", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return ColorHelper.toString(g.getColor());
			} catch (Exception e) {
				LH.warning(log, e);
			}
			return null;
		}

		@Override
		protected String getHelp() {
			return "Get this graphics context's current color in hexadecimal format (i.e #ff0000).";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	public final AmiAbstractMemberMethod<BufferedImage> getFont = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getFont", String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			return g.getFont().toString();
		}

		@Override
		protected String getHelp() {
			return "Returns the font associated with the graphics.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	public final AmiAbstractMemberMethod<BufferedImage> getClip = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "getClip", WebRectangle.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			Shape clip = g.getClip();
			if (clip instanceof Rectangle)
				return new WebRectangle((Rectangle) clip);
			else
				return null;
		}

		@Override
		protected String getHelp() {
			return "Returns the current clipping area.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};

	};
	public final AmiAbstractMemberMethod<BufferedImage> drawImage = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "drawImage", Boolean.class, BufferedImage.class,
			Integer.class, Integer.class, Integer.class, Integer.class, String.class, Integer.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				BufferedImage img = (BufferedImage) params[0];
				if (img == null)
					return null;
				int x = Caster_Integer.PRIMITIVE.castOr(params[1], 0);
				int y = Caster_Integer.PRIMITIVE.castOr(params[2], 0);
				int width = Caster_Integer.PRIMITIVE.castOr(params[3], img.getWidth());
				int height = Caster_Integer.PRIMITIVE.castOr(params[4], img.getHeight());
				Color bgColorHex = ColorHelper.parseColor((String) params[5]);
				if (bgColorHex == null)
					bgColorHex = Color.BLACK;
				int mode = Caster_Integer.PRIMITIVE.cast(params[6]);
				if (img.getWidth() != width || img.getHeight() != height)
					img = ImageHelper.scaleImage(img, width, height, mode);
				g.drawImage(img, x, y, width, height, bgColorHex, null);
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Draws the specified image on top of the THIS image.";
		}

		protected String[] buildParamNames() {
			return new String[] { "sourceimage", "x", "y", "newWidth", "newHeight", "bgColor", "method" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Source Image to draw on this image", "x (default is 0)", "y (default is 0)", "width (default is soure image width)",
					"height (default is source image height)", "bgColor", "1 = STRETCH, 2 = CROP, 3 = PAD, 4 = SCALE_USING_HEIGHT or 5 = SCALE_USING_WIDTH" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	public final AmiAbstractMemberMethod<BufferedImage> toImage = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "toImage", BufferedImage.class, Integer.class,
			Integer.class, Integer.class, Integer.class, Integer.class, Integer.class, Integer.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				final int x = Caster_Integer.INSTANCE.castOr(params[0], 0);
				final int y = Caster_Integer.INSTANCE.castOr(params[1], 0);
				final int width = Caster_Integer.INSTANCE.castOr(params[2], targetObject.getWidth());
				final int height = Caster_Integer.INSTANCE.castOr(params[3], targetObject.getHeight());
				final int newHeight = Caster_Integer.INSTANCE.castOr(params[4], height);
				final int newWidth = Caster_Integer.INSTANCE.castOr(params[5], width);
				final int mode = Caster_Integer.PRIMITIVE.cast(params[6]);
				if (x < 0 || x + width > targetObject.getWidth() || y < 0 || y + height > targetObject.getHeight())
					return null;
				BufferedImage r = ImageHelper.scaleImage(targetObject.getSubimage(x, y, width, height), newWidth, newHeight, mode);
				return r;
			} catch (Exception e) {
				LH.warning(log, e);
				return null;
			}
		}

		@Override
		protected String getHelp() {
			return "Converts THIS image to another image with the specified properties.";
		}

		protected String[] buildParamNames() {
			return new String[] { "x", "y", "width", "height", "newWidth", "newHeight", "method" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "x coordinate of the upper-left corner of the new image", "y coordinate of the upper-left corner of the new image", "width", "height",
					"width of the new image", "height of the new image", "1 = STRETCH, 2 = CROP, 3 = PAD, 4 = SCALE_USING_HEIGHT or 5 = SCALE_USING_WIDTH" };
		}
		@Override
		public boolean isReadOnly() {
			return true;
		};
	};

	public final AmiAbstractMemberMethod<BufferedImage> dispose = new AmiAbstractMemberMethod<BufferedImage>(BufferedImage.class, "dispose", Boolean.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, BufferedImage targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				if (AH.indexOf(null, params) != -1)
					return false;
				g.dispose();
				return true;
			} catch (Exception e) {
				LH.warning(log, e);
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Disposes of this graphics and releases any system resources that it is using. This object cannot be used after dispose has been called.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		};
	};

	@Override
	public String getVarTypeName() {
		return "Image";
	}
	@Override
	public String getVarTypeDescription() {
		return null;
	}
	@Override
	public Class<BufferedImage> getVarType() {
		return BufferedImage.class;
	}
	@Override
	public Class<BufferedImage> getVarDefaultImpl() {
		return null;
	}

	public static AmiScriptMemberMethods_Image INSTANCE = new AmiScriptMemberMethods_Image();
}
