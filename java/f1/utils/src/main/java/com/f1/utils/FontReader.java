package com.f1.utils;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.f1.utils.structs.Tuple2;

/**
 * The Font.createFont(...) does NOT properly set style (bold/italic)
 * 
 * @author RobertCooke
 *
 */
public class FontReader {

	private static final long MAGIC = 0x5F0F3CF5;

	public static Font createFont(byte[] bytes) throws FontFormatException, IOException {
		FastByteArrayDataInputStream in = new FastByteArrayDataInputStream(bytes);
		long scalarType = in.readUnsignedInt();
		int numTables = in.readUnsignedShort();
		int searchRange = in.readUnsignedShort();
		int entrySelector = in.readUnsignedShort();
		int rangeShift = in.readUnsignedShort();
		Map<String, Tuple2<Integer, Integer>> tables = new HashMap<String, Tuple2<Integer, Integer>>();
		for (int i = 0; i < numTables; i++) {
			String tag = new StringBuilder().append((char) in.readByte()).append((char) in.readByte()).append((char) in.readByte()).append((char) in.readByte()).toString();
			long cksum = in.readUnsignedInt();
			long offset = in.readUnsignedInt();
			long len = in.readUnsignedInt();
			tables.put(tag, new Tuple2<Integer, Integer>((int) offset, (int) len));
		}
		Tuple2<Integer, Integer> t = tables.get("head");
		if (t == null)
			throw new RuntimeException("Font Missing 'head'");
		in.reset(bytes, t.getA(), t.getB());
		int version = in.readInt();
		int fontVersion = in.readInt();
		int cksum = in.readInt();
		long magicNum = in.readUnsignedInt();
		if (magicNum != MAGIC)
			throw new RuntimeException("Font header has bad magic number: 0x" + SH.toString(magicNum, 16) + " (expecting 0x" + SH.toString(MAGIC, 16) + ")");
		int flags = in.readUnsignedShort();
		int unitsPerEm = in.readUnsignedShort();
		long createdMacTime = in.readLong();
		long modifiedMacTime = in.readLong();
		int xMin = in.readShort();
		int yMin = in.readShort();
		int xMax = in.readShort();
		int yMax = in.readShort();
		int macStyle = in.readUnsignedShort();
		int lowestRecPPEM = in.readUnsignedShort();
		int fontDirectionHint = in.readShort();
		int indexToLocFormat = in.readShort();
		int glyphDataFormat = in.readShort();
		//		OH.assertEq(in.available(), 0);
		return new StyledFont(Font.createFont(Font.TRUETYPE_FONT, new FastByteArrayDataInputStream(bytes)), macStyle);
	}

	public static class StyledFont extends Font {

		protected StyledFont(Font font, int flags) {
			super(font);
			this.style = flags;
		}
	}

	public static void main(String a[]) throws FontFormatException, IOException {
		byte[] data = IOH.readData(new File("C:/sanjay_laptop/dev/java/ami/amione/data/fonts/Roboto-Italic.ttf"));
		Font f = createFont(data);
		System.out.println("name=" + f.getFontName());
		System.out.println("family=" + f.getFamily());
		System.out.println("bold=" + f.isBold());
		System.out.println("italic=" + f.isItalic());

	}

}
