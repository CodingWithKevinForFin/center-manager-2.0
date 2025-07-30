package com.f1.utils;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageHelper {

	public static WebImage readImage(String name, byte[] data) throws IOException {
		BufferedImage src = ImageIO.read(new FastByteArrayInputStream(data));
		if (src == null)
			return null;
		WebImage r = new WebImage(name, src.getWidth(), src.getHeight(), data, ContentType.getTypeByFileExtension(SH.afterLast(name, ".")).getMimeType());
		return r;
	}
	public static WebImage readImage(File file) throws IOException {
		byte[] data = IOH.readData(file);
		return readImage(file.getName(), data);
	}
	private static void writeImage(File file, WebImage image) throws IOException {
		IOH.writeData(file, image.getData());
	}

	public static final int MODE_STRETCH = 1;
	public static final int MODE_CROP = 2;
	public static final int MODE_PAD = 3;
	public static final int MODE_SCALE_USING_HEIGHT = 4;
	public static final int MODE_SCALE_USING_WIDTH = 5;

	public static WebImage scaleImage(WebImage image, int width, int height, int mode) {
		try {
			//			if (width == image.getWidth() && height == image.getHeight())
			//				return image;
			BufferedImage src = toImage(image.getData());
			int h = height, w = width;
			if (mode == MODE_CROP || mode == MODE_PAD) {
				if ((double) image.getWidth() / image.getHeight() < (double) width / height == (mode == MODE_CROP))
					h = (int) ((double) image.getHeight() * width / image.getWidth());
				else
					w = (int) ((double) image.getWidth() * height / image.getHeight());
			} else if (mode == MODE_SCALE_USING_HEIGHT) {
				width = w = (int) ((double) image.getWidth() * h / image.getHeight());
			} else if (mode == MODE_SCALE_USING_WIDTH) {
				height = h = (int) ((double) image.getHeight() * w / image.getWidth());
			} else if (mode != MODE_STRETCH)
				throw new IllegalArgumentException("unknown mode: " + mode);

			final int y = (height - h) / 2;
			final int x = (width - w) / 2;
			int type = src.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
			BufferedImage dest = new BufferedImage(width, height, type);
			Graphics2D g = dest.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g.drawImage(src, x, y, x + w, y + h, 0, 0, image.getWidth(), image.getHeight(), null);
			return new WebImage(image.getName(), width, height, fromImage(dest, image.getEncoding()), image.getEncoding());
		} catch (Exception e) {
			throw new RuntimeException("error scaling image: " + image.getName(), e);
		}
	}
	public static BufferedImage scaleImage(BufferedImage image, int width, int height, int mode) {
		try {
			//			if (width == image.getWidth() && height == image.getHeight())
			//				return image;
			//			BufferedImage src;
			int h = height, w = width;
			if (mode == MODE_CROP || mode == MODE_PAD) {
				if ((double) image.getWidth() / image.getHeight() < (double) width / height == (mode == MODE_CROP))
					h = (int) ((double) image.getHeight() * width / image.getWidth());
				else
					w = (int) ((double) image.getWidth() * height / image.getHeight());
			} else if (mode == MODE_SCALE_USING_HEIGHT) {
				width = w = (int) ((double) image.getWidth() * h / image.getHeight());
			} else if (mode == MODE_SCALE_USING_WIDTH) {
				height = h = (int) ((double) image.getHeight() * w / image.getWidth());
			} else if (mode != MODE_STRETCH)
				throw new IllegalArgumentException("unknown mode: " + mode);

			final int y = (height - h) / 2;
			final int x = (width - w) / 2;
			int type = image.getTransparency() == Transparency.OPAQUE ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
			BufferedImage dest = new BufferedImage(width, height, type);
			Graphics2D g = dest.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			g.drawImage(image, x, y, x + w, y + h, 0, 0, image.getWidth(), image.getHeight(), null);
			return dest;
		} catch (RuntimeException e) {
			throw e;
		}
	}
	public static BufferedImage toImage(byte data[]) {
		try {
			return ImageIO.read(new FastByteArrayInputStream(data));
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}
	public static byte[] fromImage(BufferedImage image, String encoding) {
		try {
			final FastByteArrayOutputStream out = new FastByteArrayOutputStream();
			encoding = SH.stripPrefix(encoding, "image/", false);
			//TODO:validate encoding type
			ImageIO.write(image, encoding, out);
			return out.toByteArray();
		} catch (IOException e) {
			throw OH.toRuntime(e);
		}
	}

	public static int parseMode(String m, int deflt) {
		if ("STRETCH".equalsIgnoreCase(m))
			return ImageHelper.MODE_STRETCH;
		else if ("CROP".equalsIgnoreCase(m))
			return ImageHelper.MODE_CROP;
		else if ("PAD".equalsIgnoreCase(m))
			return ImageHelper.MODE_PAD;
		else if ("SCALE_USING_HEIGHT".equalsIgnoreCase(m))
			return ImageHelper.MODE_SCALE_USING_HEIGHT;
		else if ("SCALE_USING_WIDTH".equalsIgnoreCase(m))
			return ImageHelper.MODE_SCALE_USING_WIDTH;
		else
			return deflt;
	}
}