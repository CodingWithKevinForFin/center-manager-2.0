package com.f1.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class ColorHelper {

	public static final int OPAQUE = 0xff000000;
	public static final int BRIGHTNESS_THRESHOLD_DODGE = 0x007000;
	public static final int BRIGHTNESS_THRESHOLD_GRADIENT = 0x8000;
	public static final long NO_COLOR = Long.MIN_VALUE;

	public static int toRgb(int r, int g, int b) {
		return OPAQUE | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}
	public static int toRgba(int r, int g, int b, int a) {
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	static public int getA(int color) {
		return (color >> 24) & 0xFF;
	}
	static public int getR(int color) {
		return (color >> 16) & 0xFF;
	}
	static public int getG(int color) {
		return (color >> 8) & 0xFF;
	}
	static public int getB(int color) {
		return (color >> 0) & 0xFF;
	}
	static public int setA(int color, int a) {
		return ((a & 0xFF) << 24) | (color & 0x00FFFFFF);
	}
	static public int setR(int color, int r) {
		return ((r & 0xFF) << 16) | (color & 0xFF00FFFF);
	}
	static public int setG(int color, int g) {
		return ((g & 0xFF) << 8) | (color & 0xFFFF00FF);
	}
	static public int setB(int color, int b) {
		return ((b & 0xFF) << 0) | (color & 0xFFFFFF00);
	}

	static public int getRFromHex(String hexColor) {
		if (SH.startsWith(hexColor, '#') && isColorLength(hexColor))
			return parseInt(hexColor, 1, 3);
		return -1;
	}
	static public int getGFromHex(String hexColor) {
		if (SH.startsWith(hexColor, '#') && isColorLength(hexColor))
			return parseInt(hexColor, 3, 5);
		return -1;
	}
	static public int getBFromHex(String hexColor) {
		if (SH.startsWith(hexColor, '#') && isColorLength(hexColor))
			return parseInt(hexColor, 5, 7);
		return -1;
	}
	static public int getAFromHex(String hexColor) {
		if (SH.startsWith(hexColor, '#') && isColorLength(hexColor))
			return hexColor.length() == 7 ? 255 : parseInt(hexColor, 7, 9);
		return -1;
	}

	//hue
	static public int getH(long hsl) {
		return (int) ((hsl >> 32) & 0xFFFFL);
	}
	//saturation
	static public int getS(long hsl) {
		return (int) ((hsl >> 16) & 0xFFFFL);
	}
	//luminense
	static public int getL(long hsl) {
		return (int) ((hsl >> 0) & 0xFFFFL);
	}
	public static long toHsl(int h, int s, int l) {
		return ((h & 0xFFFFL) << 32) | ((s & 0xFFFFL) << 16) | ((l & 0xFFFFL) << 0);
	}

	public static int getRgbHue(int rgb) {
		final int r = getR(rgb);
		final int g = getG(rgb);
		final int b = getB(rgb);
		final int max = Math.max(Math.max(r, g), b);
		final int min = Math.min(Math.min(r, g), b);
		final int dif = max - min;
		final int h;
		if (max == min)
			h = 0;
		else if (max == g)
			h = (int) (T * (b - r) / dif + 2 * T);
		else if (max == b)
			h = (int) (T * (r - g) / dif + 4 * T);
		else
			h = (int) (T * (g - b) / dif + 6 * T) % 0xffff;
		return h;
	}
	public static int getRgbSat(int rgb) {
		final int r = getR(rgb);
		final int g = getG(rgb);
		final int b = getB(rgb);
		final int max = Math.max(Math.max(r, g), b);
		final int min = Math.min(Math.min(r, g), b);
		final int dif = max - min;
		final int l = (max + min);
		final int s = (l < 256) ? (l == 0 ? 0 : dif * 65535 / l) : (l == 510 ? 0 : 65535 * dif / (510 - l));
		return s;
	}
	public static int getRgbLum(int rgb) {
		final int r = getR(rgb);
		final int g = getG(rgb);
		final int b = getB(rgb);
		final int max = Math.max(Math.max(r, g), b);
		final int min = Math.min(Math.min(r, g), b);
		final int l = (max + min);
		return (l << 7) + (l >> 1);
	}
	public static long rgb2Hsl(int rgb) {
		final int r = getR(rgb);
		final int g = getG(rgb);
		final int b = getB(rgb);
		final int max = Math.max(Math.max(r, g), b);
		final int min = Math.min(Math.min(r, g), b);
		final int dif = max - min;
		final int h;
		if (max == min)
			h = 0;
		else if (max == g)
			h = (int) (T * (b - r) / dif + 2 * T);
		else if (max == b)
			h = (int) (T * (r - g) / dif + 4 * T);
		else
			h = (int) (T * (g - b) / dif + 6 * T) % 65536;

		final int l = (max + min);
		final int s = (l < 256) ? (l == 0 ? 0 : dif * 65535 / l) : (l == 510 ? 0 : 65535 * dif / (510 - l));
		return toHsl(h, s, (l << 7) + (l >> 1));
	}

	public static int hsl2Rgb(long hsl) {
		final int h = getH(hsl);
		final int s = getS(hsl);
		final int l = getL(hsl);
		return hsl2Rgb(h, s, l);
	}
	public static int hsl2Rgb(int h, int s, int l) {
		final int c = (int) ((0xffffL - Math.abs((l << 1) - 0xffff)) * s / 0xffff);
		final double d = h / (0xffff / 6d);
		final int x = (int) (c * (1 - Math.abs(d % 2 - 1)));
		final int m = l - (c >> 1);
		switch ((int) d) {
			case 6:
			case 0:
				return toRgb((c + m) >> 8, (x + m) >> 8, (0 + m) >> 8);
			case 1:
				return toRgb((x + m) >> 8, (c + m) >> 8, (0 + m) >> 8);
			case 2:
				return toRgb((0 + m) >> 8, (c + m) >> 8, (x + m) >> 8);
			case 3:
				return toRgb((0 + m) >> 8, (x + m) >> 8, (c + m) >> 8);
			case 4:
				return toRgb((x + m) >> 8, (0 + m) >> 8, (c + m) >> 8);
			case 5:
				return toRgb((c + m) >> 8, (0 + m) >> 8, (x + m) >> 8);
			default:
				throw new RuntimeException();
		}
	}
	public static void main(String[] s) {
		//		hsl2Rgb(60, .50*65536, 70);
		System.out.println(getA(0x00000000));
		System.out.println(getA(0x00000001));
		System.out.println(getA(0xFF000000));
		System.out.println(toRgbString(parseRgb("#ffffffff")));
		System.out.println(toRgbString(parseRgb("#ffffffaa")));
		System.out.println(toRgbString(parseRgb("#ffffff")));
		System.out.println(toRgbString(parseRgb("#ffffff00")));
		System.out.println(toRgbString(parseRgb("#aabbccff")));
		System.out.println(toRgbString(parseRgb("#000000ff")));
		System.out.println(toRgbString(parseRgb("#000000fa")));
		System.out.println(toRgbString(parseRgb("#00000000")));
		System.out.println();
		System.out.println(toRgbString(parseRgb("#000000")));
		System.out.println(toRgbString(parseRgb("#100000")));
		System.out.println(toRgbString(parseRgb("#FFFFFF")));
		System.out.println();
		System.out.println(toRgbString(parseRgb("#000")));
		System.out.println(toRgbString(parseRgb("#111")));
		System.out.println(toRgbString(parseRgb("#fff")));
		System.out.println();
		System.out.println(toRgbString(parseRgb("#000a")));
		System.out.println(toRgbString(parseRgb("#111a")));
		System.out.println(toRgbString(parseRgb("#fffa")));
		System.out.println();
		System.out.println(toRgbString(parseRgb("#000f")));
		System.out.println(toRgbString(parseRgb("#111f")));
		System.out.println(toRgbString(parseRgb("#ffff")));
		System.out.println(toRgbString(parseRgb("#aabbcc")));
		System.out.println(toRgbString(parseRgb("#aabbcc11")));
		System.out.println(toRgbString(parseRgb("#aabbcc00")));

		int n = hsl2Rgb((int) (20d / 360 * 65535), (int) (100D / 100 * 65535), (int) (62d / 100 * 65535));
		System.out.println(getR(n));
		System.out.println(getG(n));
		System.out.println(getB(n));
	}

	public int adjustRgb(int rgb, double hue, double sat, double lum) {
		long hsl = rgb2Hsl(rgb);
		int h = getH(hsl);
		int s = getS(hsl);
		int l = getL(hsl);
		if (hue != 0d)
			h = (int) (h + (hue * 65535 / 2)) & 0xffff;
		if (sat != 0d)
			s = adjust(s, sat);
		if (lum != 0d)
			l = adjust(l, lum);
		return hsl2Rgb(toHsl(h, s, l));
	}

	public static int brighten(int rgb, double factor) {
		int r = getR(rgb);
		int g = getG(rgb);
		int b = getB(rgb);
		int a = getA(rgb);
		r = f(r, factor);
		g = f(g, factor);
		b = f(b, factor);
		return toRgba(MH.clip(r, 0, 255), MH.clip(g, 0, 255), MH.clip(b, 0, 255), a);
	}

	public static int brighten2(int rgb, double factor) {
		int r = getR(rgb);
		int g = getG(rgb);
		int b = getB(rgb);
		int a = getA(rgb);
		double tot = (r + g + b) * (1 + factor);
		if (factor > 0 && r == 0 && g == 0 && b == 0)
			r = g = b = 1;
		r = (int) adj(r, factor);
		g = (int) adj(g, factor);
		b = (int) adj(b, factor);
		int tot2 = r + g + b;
		double sat = tot - tot2;
		if (Math.abs(sat) > 2) {//luminance at 100%, lets increase hue now
			sat = sat * 3 / 2;
			r += sat;
			g += sat;
			b += sat;
		}
		return toRgba(MH.clip(r, 0, 255), MH.clip(g, 0, 255), MH.clip(b, 0, 255), a);
	}

	public static int colorDodgeRgb(int rgb) {
		return ((getR(rgb) + getG(rgb) + getB(rgb)) / 3 < 120) ? 0xFFFFFFFF : 0xFF000000;
	}
	public static String colorDodgeRgbToString(int rgb, int bgcolor) {
		int a = getA(rgb);
		return ((blend(getR(rgb), getR(bgcolor), a) + blend(getG(rgb), getG(bgcolor), a) + blend(getB(rgb), getB(bgcolor), a)) / 3 < 120) ? "#FFFFFF" : "#000000";
	}

	//blends a single channel
	public static int blend(int c, int bg, int a) {
		if (a <= 0)
			return bg;
		else if (a >= 255)
			return c;
		else
			return (c * a + bg * (255 - a)) / 255;
	}
	public static long colorDodgeHsl(long hsl) {
		return getL(hsl) > BRIGHTNESS_THRESHOLD_DODGE ? 0xff000000L : 0xFFFFFFFFL;
	}

	public static int generateGradientLimitRgb(int rgb, double factor) {
		return getL(rgb2Hsl(rgb)) < BRIGHTNESS_THRESHOLD_GRADIENT ? brighten(rgb, factor) : brighten(rgb, -factor);
	}
	public static long generateGradientLimitHsl(long hsl, double factor) {
		return getL(hsl) < BRIGHTNESS_THRESHOLD_GRADIENT ? brighten(hsl2Rgb(hsl), factor) : brighten(hsl2Rgb(hsl), -factor);
	}
	public static String generateGradientLimitPairRgb(int rgb, double factor, boolean lightToDark) {
		boolean condition = ColorHelper.getL(ColorHelper.rgb2Hsl(rgb)) < BRIGHTNESS_THRESHOLD_GRADIENT;
		if (!lightToDark)
			condition = !condition;
		return condition ? toRgbString(generateGradientLimitRgb(rgb, factor)) + "," + toRgbString(rgb)
				: toRgbString(rgb) + "," + toRgbString(generateGradientLimitRgb(rgb, factor));
	}
	public static String generateGradientLimitPairHsl(long hsl, double factor, boolean lightToDark) {
		return generateGradientLimitPairRgb(hsl2Rgb(hsl), factor, lightToDark);
	}

	//return NO_COLOR       on error
	public static long parseRgbNoThrow(CharSequence rrggbb) {
		if (rrggbb == null)
			return NO_COLOR;
		return parseRgbNoThrow(rrggbb, 0, rrggbb.length());
	}
	public static long parseRgbNoThrow(CharSequence rrggbb, int start, int end) {
		if (rrggbb == null)
			return NO_COLOR;
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);

		try {
			switch (end - start) {
				case 9://#rrggbbaa
					if (rrggbb.charAt(start + 0) != '#')
						return NO_COLOR;
					return parseIntFast(rrggbb, start + 1, start + 7) + (parseIntFast(rrggbb, start + 7, start + 9) << 24);
				case 8://rrggbbaa
					return parseIntFast(rrggbb, start + 0, start + 6) + (parseIntFast(rrggbb, start + 6, start + 8) << 24);
				case 7://#rrggbb
					if (rrggbb.charAt(start + 0) != '#')
						return NO_COLOR;
					return parseIntFast(rrggbb, start + 1, start + 7) | OPAQUE;
				case 6://#rrggbb
					return parseIntFast(rrggbb, start + 0, start + 6) | OPAQUE;
				case 5://#rgba
					if (rrggbb.charAt(start + 0) != '#')
						return NO_COLOR;
					return toRgba(parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2), parseSingleFast(rrggbb, start + 3), parseSingleFast(rrggbb, start + 4));
				case 4://#rgb or rgba
					if (rrggbb.charAt(start + 0) != '#')
						return toRgba(parseSingleFast(rrggbb, start + 0), parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2),
								parseSingleFast(rrggbb, start + 3));
					return toRgb(parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2), parseSingleFast(rrggbb, start + 3));
				case 3://rgb
					return toRgb(parseSingleFast(rrggbb, start + 0), parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2));
				default:
					return NO_COLOR;
			}
		} catch (Exception e) {
			return NO_COLOR;
		}
	}
	public static int parseRgb(CharSequence rrggbb) {
		return parseRgb(rrggbb, 0, rrggbb.length());
	}
	public static int parseRgb(CharSequence rrggbb, int start, int end) {
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);
		switch (end - start) {
			case 9://#rrggbbaa
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return parseInt(rrggbb, start + 1, start + 7) + (parseInt(rrggbb, start + 7, start + 9) << 24);
			case 8://rrggbbaa
				return parseInt(rrggbb, start + 0, start + 6) + (parseInt(rrggbb, start + 6, start + 8) << 24);
			case 7://#rrggbb
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return parseInt(rrggbb, start + 1, start + 7) | OPAQUE;
			case 6://#rrggbb
				return parseInt(rrggbb, start + 0, start + 6) | OPAQUE;
			case 5://#rgba
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return toRgba(parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3), parseSingle(rrggbb, start + 4));
			case 4://#rgb or rgba
				if (rrggbb.charAt(start + 0) != '#')
					return toRgba(parseSingle(rrggbb, start + 0), parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3));
				return toRgb(parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3));
			case 3://rgb
				return toRgb(parseSingle(rrggbb, start + 0), parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2));
			default:
				throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
		}
	}
	public static Color parseColorNoThrow(CharSequence rrggbb) {
		if (rrggbb == null)
			return null;
		return parseColorNoThrow(rrggbb, 0, rrggbb.length());
	}
	public static Color parseColorNoThrow(CharSequence rrggbb, int start, int end) {
		if (rrggbb == null)
			return null;
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);
		try {
			switch (end - start) {
				case 9://#rrggbbaa
					if (rrggbb.charAt(start + 0) != '#')
						return null;
					return newColor(parseIntFast(rrggbb, start + 1, start + 7) + (parseIntFast(rrggbb, start + 7, start + 9) << 24));
				case 8://rrggbbaa
					return newColor(parseIntFast(rrggbb, start + 0, start + 6) + (parseIntFast(rrggbb, start + 6, start + 8) << 24));
				case 7://#rrggbb
					if (rrggbb.charAt(start + 0) != '#')
						return null;
					return newColor(parseIntFast(rrggbb, start + 1, start + 7) | OPAQUE);
				case 6://#rrggbb
					return newColor(parseIntFast(rrggbb, start + 0, start + 6) | OPAQUE);
				case 5://#rgba
					if (rrggbb.charAt(start + 0) != '#')
						return null;
					return newColor(parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2), parseSingleFast(rrggbb, start + 3), parseSingleFast(rrggbb, start + 4));
				case 4://#rgb or rgba
					if (rrggbb.charAt(start + 0) != '#')
						return newColor(parseSingleFast(rrggbb, start + 0), parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2),
								parseSingleFast(rrggbb, start + 3));
					return newColor(parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2), parseSingleFast(rrggbb, start + 3));
				case 3://rgb
					return newColor(parseSingleFast(rrggbb, start + 0), parseSingleFast(rrggbb, start + 1), parseSingleFast(rrggbb, start + 2));
				default:
					return null;
			}
		} catch (Exception e) {
			return null;
		}
	}
	public static Color parseColor(CharSequence rrggbb) {
		return parseColor(rrggbb, 0, rrggbb.length());
	}

	public static boolean checkColor(String color) {
		if (color != null && !color.startsWith("#") || !isColorLength(color))
			return false;
		return true;
	}

	private static boolean isColorLength(String s) {
		final int length = s.length();
		return length == 7 || length == 9;
	}
	public static Color parseColor(CharSequence rrggbb, int start, int end) {
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);
		switch (end - start) {
			case 9://#rrggbbaa
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return newColor(parseInt(rrggbb, start + 1, start + 7) + (parseInt(rrggbb, start + 7, start + 9) << 24));
			case 8://rrggbbaa
				return newColor(parseInt(rrggbb, start + 0, start + 6) + (parseInt(rrggbb, start + 6, start + 8) << 24));
			case 7://#rrggbb
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return newColor(parseInt(rrggbb, start + 1, start + 7) | OPAQUE);
			case 6://#rrggbb
				return newColor(parseInt(rrggbb, start + 0, start + 6) | OPAQUE);
			case 5://#rgba
				if (rrggbb.charAt(start + 0) != '#')
					throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
				return newColor(parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3), parseSingle(rrggbb, start + 4));
			case 4://#rgb or rgba
				if (rrggbb.charAt(start + 0) != '#')
					return newColor(parseSingle(rrggbb, start + 0), parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3));
				return newColor(parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2), parseSingle(rrggbb, start + 3));
			case 3://rgb
				return newColor(parseSingle(rrggbb, start + 0), parseSingle(rrggbb, start + 1), parseSingle(rrggbb, start + 2));
			default:
				throw new RuntimeException("Invalid color format: '" + rrggbb.subSequence(start, end) + "'");
		}
	}

	private static final Color _0x00000000 = new Color(0x00000000, true);
	private static final Color _0xff000000 = new Color(0xff000000, true);
	private static final Color _0xff0000ff = new Color(0xff0000ff, true);
	private static final Color _0xff00ff00 = new Color(0xff00ff00, true);
	private static final Color _0xff00ffff = new Color(0xff00ffff, true);
	private static final Color _0xffff0000 = new Color(0xffff0000, true);
	private static final Color _0xffff00ff = new Color(0xffff00ff, true);
	private static final Color _0xffffff00 = new Color(0xffffff00, true);
	private static final Color _0xffffffff = new Color(0xffffffff, true);
	private static final Color _0xff404040 = new Color(0xff404040, true);
	private static final Color _0xff808080 = new Color(0xff808080, true);
	private static final Color _0xffc0c0c0 = new Color(0xffc0c0c0, true);

	public static Color newColor(int i) {
		switch (i) {
			case 0x00000000:
				return _0x00000000;
			case 0xff000000:
				return _0xff000000;
			case 0xff0000ff:
				return _0xff0000ff;
			case 0xff00ff00:
				return _0xff00ff00;
			case 0xff00ffff:
				return _0xff00ffff;
			case 0xffff0000:
				return _0xffff0000;
			case 0xffff00ff:
				return _0xffff00ff;
			case 0xffffff00:
				return _0xffffff00;
			case 0xffffffff:
				return _0xffffffff;
			case 0xff404040:
				return _0xff404040;
			case 0xff808080:
				return _0xff808080;
			case 0xffc0c0c0:
				return _0xffc0c0c0;
			default:
				return new Color(i, true);
		}
	}
	public static Color newColor(int r, int g, int b, int a) {
		return newColor(toRgba(r, g, b, a));
	}
	public static Color newColor(int r, int g, int b) {
		return newColor(toRgb(r, g, b));
	}
	public static String toRgbaString(int color) {
		switch (color) {
			case 0xff000000:
				return "#000000ff";
			case 0xff0000ff:
				return "#0000ffff";
			case 0xff00ff00:
				return "#00ff00ff";
			case 0xff00ffff:
				return "#00ffffff";
			case 0xffff0000:
				return "#ff0000ff";
			case 0xffff00ff:
				return "#ff00ffff";
			case 0xffffff00:
				return "#ffff00ff";
			case 0xffffffff:
				return "#ffffffff";
			case 0xff040404:
				return "#040404ff";
			case 0xff080808:
				return "#080808ff";
			case 0xffc0c0c0:
				return "#c0c0c0ff";
		}
		return toRgbaString(color, new StringBuilder(9)).toString();
	}
	public static String toRgbString(int color) {
		switch (color & 0xffffff) {
			case 0x000000:
				return "#000000";
			case 0x0000ff:
				return "#0000ff";
			case 0x00ff00:
				return "#00ff00";
			case 0x00ffff:
				return "#00ffff";
			case 0xff0000:
				return "#ff0000";
			case 0xff00ff:
				return "#ff00ff";
			case 0xffff00:
				return "#ffff00";
			case 0xffffff:
				return "#ffffff";
			case 0x040404:
				return "#040404";
			case 0x080808:
				return "#080808";
			case 0xc0c0c0:
				return "#c0c0c0";
		}
		return toRgbString(color, new StringBuilder(7)).toString();
	}
	public static String toString(int color) {
		if (hasAlpha(color))
			return toRgbaString(color);
		else
			return toRgbString(color);
	}
	public static StringBuilder toString(int color, StringBuilder sb) {
		if (hasAlpha(color))
			return toRgbaString(color, sb);
		else
			return toRgbString(color, sb);
	}

	public static StringBuilder toRgbaString(int color, StringBuilder sb) {
		sb.append('#');
		toHex(getR(color), sb);
		toHex(getG(color), sb);
		toHex(getB(color), sb);
		toHex(getA(color), sb);
		return sb;
	}
	public static StringBuilder toRgbString(int color, StringBuilder sb) {
		sb.append('#');
		toHex(getR(color), sb);
		toHex(getG(color), sb);
		toHex(getB(color), sb);
		return sb;
	}

	public static String toString(Color color) {
		return toString(color.getRGB());
	}
	public static StringBuilder toString(Color color, StringBuilder sb) {
		return toString(color.getRGB(), sb);
	}

	public static boolean hasAlpha(int color) {
		return getA(color) != 0xff;
	}
	public static boolean hasAplha(Color color) {
		return color.getAlpha() != 0xff;
	}
	public static boolean isColor(CharSequence rrggbb) {
		if (rrggbb == null)
			return false;
		return isColor(rrggbb, 0, rrggbb.length());
	}
	public static boolean isColor(CharSequence rrggbb, int start, int end) {
		if (rrggbb == null)
			return false;
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);
		try {
			switch (end - start) {
				case 9://#rrggbbaa
					if (rrggbb.charAt(start) != '#')
						return false;
					parseIntFast(rrggbb, start + 1, start + 9);
					return true;
				case 8://rrggbbaa
					parseIntFast(rrggbb, start + 0, start + 8);
					return true;
				case 7://#rrggbb
					if (rrggbb.charAt(start + 0) != '#')
						return false;
					parseIntFast(rrggbb, start + 1, start + 7);
					return true;
				case 6://#rrggbb
					parseIntFast(rrggbb, start + 0, start + 6);
					return true;
				case 5://#rgba
					if (rrggbb.charAt(start + 0) != '#')
						return false;
					parseSingleFast(rrggbb, start + 1);
					parseSingleFast(rrggbb, start + 2);
					parseSingleFast(rrggbb, start + 3);
					parseSingleFast(rrggbb, start + 4);
					return true;
				case 4://#rgb or rgba
					if (rrggbb.charAt(start + 0) != '#') {
						parseSingleFast(rrggbb, start + 0);
						parseSingleFast(rrggbb, start + 1);
						parseSingleFast(rrggbb, start + 2);
						parseSingleFast(rrggbb, start + 3);
						return true;
					} else {
						parseSingleFast(rrggbb, start + 1);
						parseSingleFast(rrggbb, start + 2);
						parseSingleFast(rrggbb, start + 3);
						return true;
					}
				case 3://rgb
					parseSingleFast(rrggbb, start + 0);
					parseSingleFast(rrggbb, start + 1);
					parseSingleFast(rrggbb, start + 2);
					return true;
				default:
					return false;
			}
		} catch (Exception e) {
			return false;
		}
	}
	public static boolean hasAlpha(CharSequence rrggbb) {
		if (rrggbb == null)
			return false;
		return hasAlpha(rrggbb, 0, rrggbb.length());
	}
	public static boolean hasAlpha(CharSequence rrggbb, int start, int end) {
		if (rrggbb == null)
			return false;
		start = SH.findTrimStart(rrggbb, start, end);
		end = SH.findTrimEnd(rrggbb, start, end);
		try {
			switch (end - start) {
				case 9://#rrggbbaa
					if (rrggbb.charAt(start) != '#')
						return false;
					parseIntFast(rrggbb, start + 1, start + 9);
					return true;
				case 8://rrggbbaa
					parseIntFast(rrggbb, start, start + 8);
					return true;
				case 5://#rgba
					if (rrggbb.charAt(start) != '#')
						return false;
					parseSingleFast(rrggbb, start + 1);
					parseSingleFast(rrggbb, start + 2);
					parseSingleFast(rrggbb, start + 3);
					parseSingleFast(rrggbb, start + 4);
					return true;
				case 4://#rgb or rgba
					if (rrggbb.charAt(start) == '#')
						return false;
					parseSingleFast(rrggbb, start);
					parseSingleFast(rrggbb, start + 1);
					parseSingleFast(rrggbb, start + 2);
					parseSingleFast(rrggbb, start + 3);
					return true;
				default:
					return false;
			}
		} catch (Exception e) {
			return false;
		}
	}

	private static final RuntimeException EXCEPTION = new RuntimeException("DUMMY");

	private static int parseFast(CharSequence rrggbb, int i) {
		switch (rrggbb.charAt(i)) {
			case '0':
				return 0;
			case '1':
				return 0x1;
			case '2':
				return 0x2;
			case '3':
				return 0x3;
			case '4':
				return 0x4;
			case '5':
				return 0x5;
			case '6':
				return 0x6;
			case '7':
				return 0x7;
			case '8':
				return 0x8;
			case '9':
				return 0x9;
			case 'a':
			case 'A':
				return 0xa;
			case 'b':
			case 'B':
				return 0xb;
			case 'c':
			case 'C':
				return 0xc;
			case 'd':
			case 'D':
				return 0xd;
			case 'e':
			case 'E':
				return 0xe;
			case 'f':
			case 'F':
				return 0xf;
			default:
				throw EXCEPTION;
		}
	}
	private static int parse(CharSequence rrggbb, int i) {
		switch (rrggbb.charAt(i)) {
			case '0':
				return 0;
			case '1':
				return 0x1;
			case '2':
				return 0x2;
			case '3':
				return 0x3;
			case '4':
				return 0x4;
			case '5':
				return 0x5;
			case '6':
				return 0x6;
			case '7':
				return 0x7;
			case '8':
				return 0x8;
			case '9':
				return 0x9;
			case 'a':
			case 'A':
				return 0xa;
			case 'b':
			case 'B':
				return 0xb;
			case 'c':
			case 'C':
				return 0xc;
			case 'd':
			case 'D':
				return 0xd;
			case 'e':
			case 'E':
				return 0xe;
			case 'f':
			case 'F':
				return 0xf;
			default:
				throw new RuntimeException("invalid char: " + rrggbb.charAt(i));
		}
	}
	private static int parseSingleFast(CharSequence rrggbb, int i) {
		switch (rrggbb.charAt(i)) {
			case '0':
				return 0;
			case '1':
				return 0x11;
			case '2':
				return 0x22;
			case '3':
				return 0x33;
			case '4':
				return 0x44;
			case '5':
				return 0x55;
			case '6':
				return 0x66;
			case '7':
				return 0x77;
			case '8':
				return 0x88;
			case '9':
				return 0x99;
			case 'a':
			case 'A':
				return 0xaa;
			case 'b':
			case 'B':
				return 0xbb;
			case 'c':
			case 'C':
				return 0xcc;
			case 'd':
			case 'D':
				return 0xdd;
			case 'e':
			case 'E':
				return 0xee;
			case 'f':
			case 'F':
				return 0xff;
			default:
				throw EXCEPTION;
		}
	}
	private static int parseSingle(CharSequence rrggbb, int i) {
		switch (rrggbb.charAt(i)) {
			case '0':
				return 0;
			case '1':
				return 0x11;
			case '2':
				return 0x22;
			case '3':
				return 0x33;
			case '4':
				return 0x44;
			case '5':
				return 0x55;
			case '6':
				return 0x66;
			case '7':
				return 0x77;
			case '8':
				return 0x88;
			case '9':
				return 0x99;
			case 'a':
			case 'A':
				return 0xaa;
			case 'b':
			case 'B':
				return 0xbb;
			case 'c':
			case 'C':
				return 0xcc;
			case 'd':
			case 'D':
				return 0xdd;
			case 'e':
			case 'E':
				return 0xee;
			case 'f':
			case 'F':
				return 0xff;
			default:
				throw new RuntimeException("invalid char: " + rrggbb.charAt(i));
		}
	}
	private static int parseIntFast(CharSequence chars, int start, int end) {
		int s = start;
		int r = 0;
		while (start < end)
			r = (r << 4) + parseFast(chars, start++);
		return r;
	}
	private static int parseInt(CharSequence chars, int start, int end) {
		int s = start;
		int r = 0;
		while (start < end)
			r = (r << 4) + parse(chars, start++);
		return r;
	}
	private static final StringBuilder toHex(int n, StringBuilder sink) {
		int a = n >> 4;
		int b = n & 0x0f;
		sink.append(a < 10 ? (char) (a + '0') : (char) (a - 10 + 'a'));
		sink.append(b < 10 ? (char) (b + '0') : (char) (b - 10 + 'a'));
		return sink;
	}

	private static final double T = 65536d / 6d;

	private int adjust(int n, double multiple) {
		if (multiple == 0)
			return n;
		if (n == 0)
			n = 256;//let's get out of the zero trap
		int mul = (int) (multiple * n / 256);
		if (mul == 0)
			mul = multiple < 0 ? -1 : 1;
		int n2 = MH.clip(n + mul * 256, 0, 0xffff);
		return n2;
	}
	private static int f(int x, double f) {
		return (int) (f > 0 ? x + f * (255 - x) : (x + f * x));
	}
	static private double adj(int c, double d) {
		return MH.clip((c + .5d + c * d), 0, 255);
	}
	public static List<Color> parseColorsNoThrow(List<String> colors) {
		if (colors == null)
			return null;
		List<Color> r = new ArrayList<Color>(colors.size());
		for (int n = 0; n < colors.size(); n++)
			r.add(parseColorNoThrow(colors.get(n)));
		return r;
	}
}
