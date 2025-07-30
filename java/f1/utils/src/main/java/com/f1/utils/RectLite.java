package com.f1.utils;

import java.awt.Rectangle;

public class RectLite {

	private static int getX(long rect) {
		return (int) (((rect & 0xffff000000000000L) >>> 48L) + Short.MIN_VALUE);
	}
	private static int getY(long rect) {
		return (int) (((rect & 0x0000ffff00000000L) >>> 32L) + Short.MIN_VALUE);
	}
	private static int getW(long rect) {
		return (int) (((rect & 0x00000000ffff0000L) >>> 16L));
	}
	private static int getH(long rect) {
		return (int) (((rect & 0x000000000000ffffL) >>> 0L));
	}

	public static long toRect(int x, int y, int w, int h) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}

		if (OH.isntBetween(x, Short.MIN_VALUE + 1, Short.MAX_VALUE) || OH.isntBetween(y, Short.MIN_VALUE + 1, Short.MAX_VALUE) || OH.isntBetween(w, 0, Short.MAX_VALUE)
				|| OH.isntBetween(h, 0, Short.MAX_VALUE))
			return 0;
		return (((long) x - Short.MIN_VALUE) << 48L) | (((long) y - Short.MIN_VALUE) << 32L) | (((long) w) << 16L) | (((long) h) << 0L);
	}

	public static boolean isRect(long rect) {
		return rect != 0;
	}
	public static boolean isntRect(long rect) {
		return rect == 0;
	}
	public static boolean intersects(long trect, int x, int y, int w, int h) {
		if (w == 0 || h == 0)
			return false;
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		if (isntRect(trect))
			return false;
		final int tw = getW(trect);
		if (tw == 0)
			return false;
		final int tx = getX(trect);
		if (tx + tw <= x || x + w <= tx)
			return false;
		final int th = getH(trect);
		if (th == 0)
			return false;
		final int ty = getY(trect);
		if (ty + th <= y || y + h <= ty)
			return false;
		return true;
	}

	public static boolean intersects(long trect, long orect) {
		if (isntRect(trect) || isntRect(orect))
			return false;
		final int tw = getW(trect);
		final int ow = getW(orect);
		if (tw == 0 || ow == 0)
			return false;
		final int tx = getX(trect);
		final int ox = getX(orect);
		if (tx + tw <= ox || ox + ow <= tx)
			return false;
		final int th = getH(trect);
		final int oh = getH(orect);
		if (th == 0 || oh == 0)
			return false;
		final int ty = getY(trect);
		final int oy = getY(orect);
		if (ty + th <= oy || oy + oh <= ty)
			return false;
		return true;
	}

	public static boolean contains(long outer, long inner) {
		if (isntRect(outer) || isntRect(inner))
			return false;
		final int ow = getW(outer);
		final int iw = getW(inner);
		if (iw == 0 || ow == 0)
			return false;
		final int ox = getX(outer);
		final int ix = getX(inner);
		if (ox > ix || ox + ow < ix + iw)
			return false;
		final int oh = getH(outer);
		final int ih = getH(inner);
		if (ih == 0 || oh == 0)
			return false;
		final int oy1 = getY(outer);
		final int iy1 = getY(inner);
		return !(oy1 > iy1 || oy1 + oh < iy1 + ih);
	}

	private static String rectToString(long rect) {
		return "x=" + getX(rect) + ", y=" + getY(rect) + ", w=" + getW(rect) + ", h=" + getH(rect);
	}

	static public void assertRect(int x, int y, int w, int h) {
		long rect = toRect(x, y, w, h);
		OH.assertEq(getX(rect), x);
		OH.assertEq(getY(rect), y);
		OH.assertEq(getW(rect), w);
		OH.assertEq(getH(rect), h);
	}

	public static void main(String a[]) {
		System.out.println(intersects(toRect(0, 0, 1, 1), toRect(0, 0, 0, 0)));
		for (int x1 = -5; x1 < 5; x1++) {
			for (int y1 = -5; y1 < 5; y1++) {
				for (int w1 = -5; w1 < 5; w1++) {
					for (int h1 = -5; h1 < 5; h1++) {
						for (int x = -5; x < 5; x++) {
							for (int y = -5; y < 5; y++) {
								for (int w = -5; w < 5; w++) {
									for (int h = -5; h < 5; h++) {
										boolean b1 = intersects(toRect(x1, y1, w1, h1), toRect(x, y, w, h));
										boolean b2 = newRectangle(x1, y1, w1, h1).intersects(newRectangle(x, y, w, h));
										if (b1 != b2)
											System.out.println(x1 + ", " + y1 + ", " + w1 + ", " + h1 + ", " + x + ", " + y + ", " + w + ", " + h);
										OH.assertEq(b1, b2);
										boolean b3 = contains(toRect(x1, y1, w1, h1), toRect(x, y, w, h));
										boolean b4 = newRectangle(x1, y1, w1, h1).contains(newRectangle(x, y, w, h));
										if (b3 != b4)
											System.out.println(x1 + ", " + y1 + ", " + w1 + ", " + h1 + ", " + x + ", " + y + ", " + w + ", " + h);
										OH.assertEq(b3, b4);
									}
								}
							}
						}
					}
				}
			}
		}

		System.out.println(rectToString(-1));
	}
	private static Rectangle newRectangle(int x, int y, int w, int h) {
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		return new Rectangle(x, y, w, h);

	}
	public static boolean intersects(int tx, int ty, int tw, int th, int x, int y, int w, int h) {
		if (w == 0 || h == 0)
			return false;
		if (w < 0) {
			x += w;
			w = -w;
		}
		if (h < 0) {
			y += h;
			h = -h;
		}
		if (tw == 0)
			return false;
		if (tx + tw <= x || x + w <= tx)
			return false;
		if (th == 0)
			return false;
		if (ty + th <= y || y + h <= ty)
			return false;
		return true;
	}
}
