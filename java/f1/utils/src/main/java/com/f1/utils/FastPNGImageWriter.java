package com.f1.utils;

/*
 * Copyright (c) 2000, 2005, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.imageio.IIOException;
import javax.imageio.IIOImage;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.ImageOutputStreamImpl;

import com.sun.imageio.plugins.png.PNGMetadata;
import com.sun.imageio.plugins.png.RowFilter;

class CRC {

	private static int[] CRC_TABLE = new int[256];
	private int crc = 0xffffffff;

	static {
		// Initialize CRC table
		for (int n = 0; n < 256; n++) {
			int c = n;
			for (int k = 0; k < 8; k++) {
				if ((c & 1) == 1) {
					c = 0xedb88320 ^ (c >>> 1);
				} else {
					c >>>= 1;
				}

				CRC_TABLE[n] = c;
			}
		}
	}

	public CRC() {
	}

	public void reset() {
		crc = 0xffffffff;
	}

	public void update(byte[] data, int off, int len) {
		for (int n = 0; n < len; n++) {
			crc = CRC_TABLE[(crc ^ data[off + n]) & 0xff] ^ (crc >>> 8);
		}
	}

	public void update(int data) {
		crc = CRC_TABLE[(crc ^ data) & 0xff] ^ (crc >>> 8);
	}

	public int getValue() {
		return crc ^ 0xffffffff;
	}
}

final class ChunkStream extends ImageOutputStreamImpl {

	private ImageOutputStream stream;
	private long startPos;
	private CRC crc = new CRC();

	public ChunkStream(int type, ImageOutputStream stream) throws IOException {
		this.stream = stream;
		this.startPos = stream.getStreamPosition();

		stream.writeInt(-1); // length, will backpatch
		writeInt(type);
	}

	public int read() throws IOException {
		throw new RuntimeException("Method not available");
	}

	public int read(byte[] b, int off, int len) throws IOException {
		throw new RuntimeException("Method not available");
	}

	public void write(byte[] b, int off, int len) throws IOException {
		crc.update(b, off, len);
		stream.write(b, off, len);
	}

	public void write(int b) throws IOException {
		crc.update(b);
		stream.write(b);
	}

	public void finish() throws IOException {
		stream.writeInt(crc.getValue());

		long pos = stream.getStreamPosition();
		stream.seek(startPos);
		stream.writeInt((int) (pos - startPos) - 12);
		stream.seek(pos);
		stream.flushBefore(pos);
	}

}

// Compress output and write as a series of 'IDAT' chunks of
// fixed length.
final class IDATOutputStream extends ImageOutputStreamImpl {

	private static byte[] CHUNK_TYPE = { (byte) 'I', (byte) 'D', (byte) 'A', (byte) 'T' };
	private FastByteArrayOutputStream out = new FastByteArrayOutputStream(1000000);

	private ImageOutputStream stream;
	private long startPos;
	private CRC crc = new CRC();

	final Deflater def;

	public IDATOutputStream(ImageOutputStream stream, int compressionLevel) throws IOException {
		def = new Deflater(compressionLevel);
		this.stream = stream;
		startChunk();
	}

	private void startChunk() throws IOException {
		crc.reset();
		this.startPos = stream.getStreamPosition();
		stream.writeInt(-1); // length, will backpatch

		crc.update(CHUNK_TYPE, 0, 4);
		stream.write(CHUNK_TYPE, 0, 4);

	}

	private void finishChunk() throws IOException {
		// Write CRC
		stream.writeInt(crc.getValue());

		// Write length
		long pos = stream.getStreamPosition();
		stream.seek(startPos);
		stream.writeInt((int) (pos - startPos) - 12);

		// Return to end of chunk and flush to minimize buffering
		stream.seek(pos);
		stream.flushBefore(pos);
	}

	public int read() throws IOException {
		throw new RuntimeException("Method not available");
	}

	public int read(byte[] b, int off, int len) throws IOException {
		throw new RuntimeException("Method not available");
	}

	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}

	public void deflate() throws IOException {
		byte[] buf = new byte[Math.max(this.out.size(), 512)];
		int len = def.deflate(buf, 0, buf.length);
		if (len > 0) {
			crc.update(buf, 0, len);
			stream.write(buf, 0, len);
		}
	}

	public void write(int b) throws IOException {
		out.write(b);
	}

	public void finish() throws IOException {
		try {
			def.setInput(this.out.getBuffer(), 0, this.out.size());
			while (!def.needsInput())
				deflate();
			if (!def.finished()) {
				def.finish();
				while (!def.finished()) {
					deflate();
				}
			}
			finishChunk();
		} finally {
			def.end();
		}
	}

}

class PNGImageWriteParam extends ImageWriteParam {

	public PNGImageWriteParam(Locale locale) {
		super();
		this.canWriteCompressed = true;
		this.canWriteProgressive = true;
		this.locale = locale;
	}
}

/**
 */
public class FastPNGImageWriter extends ImageWriter {

	// Critical chunks
	static final int IHDR_TYPE = 0x49484452;
	static final int PLTE_TYPE = 0x504c5445;
	static final int IDAT_TYPE = 0x49444154;
	static final int IEND_TYPE = 0x49454e44;

	// Ancillary chunks
	static final int bKGD_TYPE = 0x624b4744;
	static final int cHRM_TYPE = 0x6348524d;
	static final int gAMA_TYPE = 0x67414d41;
	static final int hIST_TYPE = 0x68495354;
	static final int iCCP_TYPE = 0x69434350;
	static final int iTXt_TYPE = 0x69545874;
	static final int pHYs_TYPE = 0x70485973;
	static final int sBIT_TYPE = 0x73424954;
	static final int sPLT_TYPE = 0x73504c54;
	static final int sRGB_TYPE = 0x73524742;
	static final int tEXt_TYPE = 0x74455874;
	static final int tIME_TYPE = 0x74494d45;
	static final int tRNS_TYPE = 0x74524e53;
	static final int zTXt_TYPE = 0x7a545874;

	static final int PNG_COLOR_GRAY = 0;
	static final int PNG_COLOR_RGB = 2;
	static final int PNG_COLOR_PALETTE = 3;
	static final int PNG_COLOR_GRAY_ALPHA = 4;
	static final int PNG_COLOR_RGB_ALPHA = 6;
	static final int PNG_FILTER_NONE = 0;
	static final int PNG_FILTER_SUB = 1;
	static final int PNG_FILTER_UP = 2;
	static final int PNG_FILTER_AVERAGE = 3;
	static final int PNG_FILTER_PAETH = 4;

	static final int[] adam7XOffset = { 0, 4, 0, 2, 0, 1, 0 };
	static final int[] adam7YOffset = { 0, 0, 4, 0, 2, 0, 1 };
	static final int[] adam7XSubsampling = { 8, 8, 4, 4, 2, 2, 1, 1 };
	static final int[] adam7YSubsampling = { 8, 8, 8, 4, 4, 2, 2, 1 };
	private ImageOutputStream stream = null;
	private PNGMetadata metadata = null;

	private int sourceXOffset = 0;
	private int sourceYOffset = 0;
	private int sourceWidth = 0;
	private int sourceHeight = 0;
	private int[] sourceBands = null;
	private int periodX = 1;
	private int periodY = 1;

	private int numBands;
	private int bpp;
	private volatile boolean aborted;

	private RowFilter rowFilter = new RowFilter();

	// Per-band scaling tables
	//
	// After the first call to initializeScaleTables, either scale and scale0
	// will be valid, or scaleh and scalel will be valid, but not both.
	//
	// The tables will be designed for use with a set of input but depths
	// given by sampleSize, and an output bit depth given by scalingBitDepth.
	//
	private int[] sampleSize = null; // Sample size per band, in bits
	private int scalingBitDepth = -1; // Output bit depth of the scaling tables

	// Tables for 1, 2, 4, or 8 bit output
	private byte[][] scale = null; // 8 bit table

	// Tables for 16 bit output

	public FastPNGImageWriter(ImageWriterSpi originatingProvider) {
		super(originatingProvider);
	}

	public void setOutput(Object output) {
		super.setOutput(output);
		if (output != null) {
			if (!(output instanceof ImageOutputStream)) {
				throw new IllegalArgumentException("output not an ImageOutputStream!");
			}
			this.stream = (ImageOutputStream) output;
		} else {
			this.stream = null;
		}
	}

	private static int[] allowedProgressivePasses = { 1, 7 };

	public ImageWriteParam getDefaultWriteParam() {
		return new PNGImageWriteParam(getLocale());
	}

	public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
		return null;
	}

	public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
		PNGMetadata m = new PNGMetadata();
		m.initialize(imageType, imageType.getSampleModel().getNumBands());
		return m;
	}

	public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
		return null;
	}

	public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
		// TODO - deal with imageType
		if (inData instanceof PNGMetadata) {
			return (PNGMetadata) ((PNGMetadata) inData).clone();
		} else {
			return new PNGMetadata(inData);
		}
	}

	private void write_magic() throws IOException {
		// Write signature
		byte[] magic = { (byte) 137, 80, 78, 71, 13, 10, 26, 10 };
		stream.write(magic);
	}

	private void write_IHDR() throws IOException {
		// Write IHDR chunk
		ChunkStream cs = new ChunkStream(IHDR_TYPE, stream);
		cs.writeInt(metadata.IHDR_width);
		cs.writeInt(metadata.IHDR_height);
		cs.writeByte(metadata.IHDR_bitDepth);
		cs.writeByte(metadata.IHDR_colorType);
		if (metadata.IHDR_compressionMethod != 0) {
			throw new IIOException("Only compression method 0 is defined in PNG 1.1");
		}
		cs.writeByte(metadata.IHDR_compressionMethod);
		if (metadata.IHDR_filterMethod != 0) {
			throw new IIOException("Only filter method 0 is defined in PNG 1.1");
		}
		cs.writeByte(metadata.IHDR_filterMethod);
		if (metadata.IHDR_interlaceMethod < 0 || metadata.IHDR_interlaceMethod > 1) {
			throw new IIOException("Only interlace methods 0 (node) and 1 (adam7) are defined in PNG 1.1");
		}
		cs.writeByte(metadata.IHDR_interlaceMethod);
		cs.finish();
	}

	private void write_cHRM() throws IOException {
		if (metadata.cHRM_present) {
			ChunkStream cs = new ChunkStream(cHRM_TYPE, stream);
			cs.writeInt(metadata.cHRM_whitePointX);
			cs.writeInt(metadata.cHRM_whitePointY);
			cs.writeInt(metadata.cHRM_redX);
			cs.writeInt(metadata.cHRM_redY);
			cs.writeInt(metadata.cHRM_greenX);
			cs.writeInt(metadata.cHRM_greenY);
			cs.writeInt(metadata.cHRM_blueX);
			cs.writeInt(metadata.cHRM_blueY);
			cs.finish();
		}
	}

	private void write_gAMA() throws IOException {
		if (metadata.gAMA_present) {
			ChunkStream cs = new ChunkStream(gAMA_TYPE, stream);
			cs.writeInt(metadata.gAMA_gamma);
			cs.finish();
		}
	}

	private void write_iCCP() throws IOException {
		if (metadata.iCCP_present) {
			ChunkStream cs = new ChunkStream(iCCP_TYPE, stream);
			cs.writeBytes(metadata.iCCP_profileName);
			cs.writeByte(0); // null terminator

			cs.writeByte(metadata.iCCP_compressionMethod);
			cs.write(metadata.iCCP_compressedProfile);
			cs.finish();
		}
	}

	private void write_sBIT() throws IOException {
		if (metadata.sBIT_present) {
			ChunkStream cs = new ChunkStream(sBIT_TYPE, stream);
			int colorType = metadata.IHDR_colorType;
			if (metadata.sBIT_colorType != colorType) {
				processWarningOccurred(0, "sBIT metadata has wrong color type.\n" + "The chunk will not be written.");
				return;
			}

			if (colorType == PNG_COLOR_GRAY || colorType == PNG_COLOR_GRAY_ALPHA) {
				cs.writeByte(metadata.sBIT_grayBits);
			} else if (colorType == PNG_COLOR_RGB || colorType == PNG_COLOR_PALETTE || colorType == PNG_COLOR_RGB_ALPHA) {
				cs.writeByte(metadata.sBIT_redBits);
				cs.writeByte(metadata.sBIT_greenBits);
				cs.writeByte(metadata.sBIT_blueBits);
			}

			if (colorType == PNG_COLOR_GRAY_ALPHA || colorType == PNG_COLOR_RGB_ALPHA) {
				cs.writeByte(metadata.sBIT_alphaBits);
			}
			cs.finish();
		}
	}

	private void write_sRGB() throws IOException {
		if (metadata.sRGB_present) {
			ChunkStream cs = new ChunkStream(sRGB_TYPE, stream);
			cs.writeByte(metadata.sRGB_renderingIntent);
			cs.finish();
		}
	}

	private void write_PLTE() throws IOException {
		if (metadata.PLTE_present) {
			if (metadata.IHDR_colorType == PNG_COLOR_GRAY || metadata.IHDR_colorType == PNG_COLOR_GRAY_ALPHA) {
				// PLTE cannot occur in a gray image

				processWarningOccurred(0, "A PLTE chunk may not appear in a gray or gray alpha image.\n" + "The chunk will not be written");
				return;
			}

			ChunkStream cs = new ChunkStream(PLTE_TYPE, stream);

			int numEntries = metadata.PLTE_red.length;
			byte[] palette = new byte[numEntries * 3];
			int index = 0;
			for (int i = 0; i < numEntries; i++) {
				palette[index++] = metadata.PLTE_red[i];
				palette[index++] = metadata.PLTE_green[i];
				palette[index++] = metadata.PLTE_blue[i];
			}

			cs.write(palette);
			cs.finish();
		}
	}

	private void write_hIST() throws IOException, IIOException {
		if (metadata.hIST_present) {
			ChunkStream cs = new ChunkStream(hIST_TYPE, stream);

			if (!metadata.PLTE_present) {
				throw new IIOException("hIST chunk without PLTE chunk!");
			}

			cs.writeChars(metadata.hIST_histogram, 0, metadata.hIST_histogram.length);
			cs.finish();
		}
	}

	private void write_tRNS() throws IOException, IIOException {
		if (metadata.tRNS_present) {
			ChunkStream cs = new ChunkStream(tRNS_TYPE, stream);
			int colorType = metadata.IHDR_colorType;
			int chunkType = metadata.tRNS_colorType;

			// Special case: image is RGB and chunk is Gray
			// Promote chunk contents to RGB
			int chunkRed = metadata.tRNS_red;
			int chunkGreen = metadata.tRNS_green;
			int chunkBlue = metadata.tRNS_blue;
			if (colorType == PNG_COLOR_RGB && chunkType == PNG_COLOR_GRAY) {
				chunkType = colorType;
				chunkRed = chunkGreen = chunkBlue = metadata.tRNS_gray;
			}

			if (chunkType != colorType) {
				processWarningOccurred(0, "tRNS metadata has incompatible color type.\n" + "The chunk will not be written.");
				return;
			}

			if (colorType == PNG_COLOR_PALETTE) {
				if (!metadata.PLTE_present) {
					throw new IIOException("tRNS chunk without PLTE chunk!");
				}
				cs.write(metadata.tRNS_alpha);
			} else if (colorType == PNG_COLOR_GRAY) {
				cs.writeShort(metadata.tRNS_gray);
			} else if (colorType == PNG_COLOR_RGB) {
				cs.writeShort(chunkRed);
				cs.writeShort(chunkGreen);
				cs.writeShort(chunkBlue);
			} else {
				throw new IIOException("tRNS chunk for color type 4 or 6!");
			}
			cs.finish();
		}
	}

	private void write_bKGD() throws IOException {
		if (metadata.bKGD_present) {
			ChunkStream cs = new ChunkStream(bKGD_TYPE, stream);
			int colorType = metadata.IHDR_colorType & 0x3;
			int chunkType = metadata.bKGD_colorType;

			// Special case: image is RGB(A) and chunk is Gray
			// Promote chunk contents to RGB
			int chunkRed = metadata.bKGD_red;
			int chunkGreen = metadata.bKGD_red;
			int chunkBlue = metadata.bKGD_red;
			if (colorType == PNG_COLOR_RGB && chunkType == PNG_COLOR_GRAY) {
				// Make a gray bKGD chunk look like RGB
				chunkType = colorType;
				chunkRed = chunkGreen = chunkBlue = metadata.bKGD_gray;
			}

			// Ignore status of alpha in colorType
			if (chunkType != colorType) {
				processWarningOccurred(0, "bKGD metadata has incompatible color type.\n" + "The chunk will not be written.");
				return;
			}

			if (colorType == PNG_COLOR_PALETTE) {
				cs.writeByte(metadata.bKGD_index);
			} else if (colorType == PNG_COLOR_GRAY || colorType == PNG_COLOR_GRAY_ALPHA) {
				cs.writeShort(metadata.bKGD_gray);
			} else {
				cs.writeShort(chunkRed);
				cs.writeShort(chunkGreen);
				cs.writeShort(chunkBlue);
			}
			cs.finish();
		}
	}

	private void write_pHYs() throws IOException {
		if (metadata.pHYs_present) {
			ChunkStream cs = new ChunkStream(pHYs_TYPE, stream);
			cs.writeInt(metadata.pHYs_pixelsPerUnitXAxis);
			cs.writeInt(metadata.pHYs_pixelsPerUnitYAxis);
			cs.writeByte(metadata.pHYs_unitSpecifier);
			cs.finish();
		}
	}

	private void write_sPLT() throws IOException {
		if (metadata.sPLT_present) {
			ChunkStream cs = new ChunkStream(sPLT_TYPE, stream);

			cs.writeBytes(metadata.sPLT_paletteName);
			cs.writeByte(0); // null terminator

			cs.writeByte(metadata.sPLT_sampleDepth);
			int numEntries = metadata.sPLT_red.length;

			if (metadata.sPLT_sampleDepth == 8) {
				for (int i = 0; i < numEntries; i++) {
					cs.writeByte(metadata.sPLT_red[i]);
					cs.writeByte(metadata.sPLT_green[i]);
					cs.writeByte(metadata.sPLT_blue[i]);
					cs.writeByte(metadata.sPLT_alpha[i]);
					cs.writeShort(metadata.sPLT_frequency[i]);
				}
			} else { // sampleDepth == 16
				for (int i = 0; i < numEntries; i++) {
					cs.writeShort(metadata.sPLT_red[i]);
					cs.writeShort(metadata.sPLT_green[i]);
					cs.writeShort(metadata.sPLT_blue[i]);
					cs.writeShort(metadata.sPLT_alpha[i]);
					cs.writeShort(metadata.sPLT_frequency[i]);
				}
			}
			cs.finish();
		}
	}

	private void write_tIME() throws IOException {
		if (metadata.tIME_present) {
			ChunkStream cs = new ChunkStream(tIME_TYPE, stream);
			cs.writeShort(metadata.tIME_year);
			cs.writeByte(metadata.tIME_month);
			cs.writeByte(metadata.tIME_day);
			cs.writeByte(metadata.tIME_hour);
			cs.writeByte(metadata.tIME_minute);
			cs.writeByte(metadata.tIME_second);
			cs.finish();
		}
	}

	private void write_tEXt() throws IOException {
		Iterator keywordIter = metadata.tEXt_keyword.iterator();
		Iterator textIter = metadata.tEXt_text.iterator();

		while (keywordIter.hasNext()) {
			ChunkStream cs = new ChunkStream(tEXt_TYPE, stream);
			String keyword = (String) keywordIter.next();
			cs.writeBytes(keyword);
			cs.writeByte(0);

			String text = (String) textIter.next();
			cs.writeBytes(text);
			cs.finish();
		}
	}

	private byte[] deflate(byte[] b) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DeflaterOutputStream dos = new DeflaterOutputStream(baos);
		dos.write(b);
		dos.close();
		return baos.toByteArray();
	}

	private void write_iTXt() throws IOException {
		Iterator<String> keywordIter = metadata.iTXt_keyword.iterator();
		Iterator<Boolean> flagIter = metadata.iTXt_compressionFlag.iterator();
		Iterator<Integer> methodIter = metadata.iTXt_compressionMethod.iterator();
		Iterator<String> languageIter = metadata.iTXt_languageTag.iterator();
		Iterator<String> translatedKeywordIter = metadata.iTXt_translatedKeyword.iterator();
		Iterator<String> textIter = metadata.iTXt_text.iterator();

		while (keywordIter.hasNext()) {
			ChunkStream cs = new ChunkStream(iTXt_TYPE, stream);

			cs.writeBytes(keywordIter.next());
			cs.writeByte(0);

			Boolean compressed = flagIter.next();
			cs.writeByte(compressed ? 1 : 0);

			cs.writeByte(methodIter.next().intValue());

			cs.writeBytes(languageIter.next());
			cs.writeByte(0);

			cs.write(translatedKeywordIter.next().getBytes("UTF8"));
			cs.writeByte(0);

			String text = textIter.next();
			if (compressed) {
				cs.write(deflate(text.getBytes("UTF8")));
			} else {
				cs.write(text.getBytes("UTF8"));
			}
			cs.finish();
		}
	}

	private void write_zTXt() throws IOException {
		Iterator keywordIter = metadata.zTXt_keyword.iterator();
		Iterator methodIter = metadata.zTXt_compressionMethod.iterator();
		Iterator textIter = metadata.zTXt_text.iterator();

		while (keywordIter.hasNext()) {
			ChunkStream cs = new ChunkStream(zTXt_TYPE, stream);
			String keyword = (String) keywordIter.next();
			cs.writeBytes(keyword);
			cs.writeByte(0);

			int compressionMethod = ((Integer) methodIter.next()).intValue();
			cs.writeByte(compressionMethod);

			String text = (String) textIter.next();
			cs.write(deflate(text.getBytes("ISO-8859-1")));
			cs.finish();
		}
	}

	private void writeUnknownChunks() throws IOException {
		Iterator typeIter = metadata.unknownChunkType.iterator();
		Iterator dataIter = metadata.unknownChunkData.iterator();

		while (typeIter.hasNext() && dataIter.hasNext()) {
			String type = (String) typeIter.next();
			ChunkStream cs = new ChunkStream(chunkType(type), stream);
			byte[] data = (byte[]) dataIter.next();
			cs.write(data);
			cs.finish();
		}
	}

	private static int chunkType(String typeString) {
		char c0 = typeString.charAt(0);
		char c1 = typeString.charAt(1);
		char c2 = typeString.charAt(2);
		char c3 = typeString.charAt(3);

		int type = (c0 << 24) | (c1 << 16) | (c2 << 8) | c3;
		return type;
	}

	private void encodePass(ImageOutputStream os, RenderedImage image) throws IOException {
		final int bitDepth = metadata.IHDR_bitDepth;
		final int numBands = this.numBands;
		final int minX = sourceXOffset;
		final int minY = sourceYOffset;
		final int width = sourceWidth;
		final int height = sourceHeight;
		final int bpp = this.bpp;
		if (sourceBands != null)
			throw new UnsupportedOperationException();
		if (image.getColorModel().isAlphaPremultiplied())
			throw new UnsupportedOperationException();
		if (metadata.PLTE_order != null)
			throw new UnsupportedOperationException();
		if (bitDepth != 8)
			throw new UnsupportedOperationException();
		if (numBands != 4)
			throw new UnsupportedOperationException();
		if (width == 0 || height == 0)
			return;

		// Create row buffers
		int numSamples = width * numBands;
		int[] samples = new int[numSamples];

		int bytesPerRow = width * numBands;

		if (metadata.IHDR_colorType == PNG_COLOR_GRAY_ALPHA && image.getColorModel() instanceof IndexColorModel) {
			// reserve space for alpha samples
			bytesPerRow *= 2;

			// will be used to calculate alpha value for the pixel
		}

		byte[] prevRow = new byte[bytesPerRow + bpp];
		byte[] currRow = new byte[bytesPerRow + bpp];
		final byte[][] filteredRows = new byte[5][bytesPerRow + bpp];

		Raster ras = ((BufferedImage) image).getRaster();
		for (int row = minY; row < minY + height; row++) {
			if (isAborted())
				return;
			ras.getPixels(minX, row, width, 1, samples);
			int count = bpp;

			for (int s = 0; s < numSamples;)
				currRow[count++] = (byte) samples[s++];
			int filterType = rowFilter.filterRow(metadata.IHDR_colorType, currRow, prevRow, filteredRows, bytesPerRow, bpp);
			os.write(filterType);
			os.write(filteredRows[filterType], bpp, bytesPerRow);
			byte[] swap = currRow;
			currRow = prevRow;
			prevRow = swap;
		}
	}
	// Use sourceXOffset, etc.
	private void write_IDAT(RenderedImage image, int compressionLevel) throws IOException {
		if (metadata.IHDR_interlaceMethod == 1)
			throw new UnsupportedOperationException();
		IDATOutputStream ios = new IDATOutputStream(stream, compressionLevel);
		try {
			encodePass(ios, image);
		} finally {
			ios.finish();
		}
	}

	private void writeIEND() throws IOException {
		ChunkStream cs = new ChunkStream(IEND_TYPE, stream);
		cs.finish();
	}

	// Initialize the scale/scale0 or scaleh/scalel arrays to
	// hold the results of scaling an input value to the desired
	// output bit depth
	private void initializeScaleTables(int[] sampleSize) {
		int bitDepth = metadata.IHDR_bitDepth;
		if (bitDepth != 8)
			throw new UnsupportedOperationException();

		// If the existing tables are still valid, just return
		if (bitDepth == scalingBitDepth && AH.eq(sampleSize, this.sampleSize)) {
			return;
		}

		// Compute new tables
		this.sampleSize = sampleSize;
		this.scalingBitDepth = bitDepth;
		int maxOutSample = (1 << bitDepth) - 1;
		scale = new byte[numBands][];
		for (int b = 0; b < numBands; b++) {
			int maxInSample = (1 << sampleSize[b]) - 1;
			int halfMaxInSample = maxInSample / 2;
			scale[b] = new byte[maxInSample + 1];
			for (int s = 0; s <= maxInSample; s++) {
				scale[b][s] = (byte) ((s * maxOutSample + halfMaxInSample) / maxInSample);
			}
		}
	}

	public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IIOException {
		if (image.hasRaster())
			throw new UnsupportedOperationException("image has a Raster!");
		IIOMetadata imd = image.getMetadata();
		if (imd != null)
			throw new UnsupportedOperationException("image has a Raster!");
		if (streamMetadata != null)
			throw new UnsupportedOperationException("image has a Raster!");
		write(image.getRenderedImage(), param);
	}
	public void write(RenderedImage image, ImageWriteParam param) throws IIOException {
		if (stream == null) {
			throw new IllegalStateException("output == null!");
		}
		if (image == null) {
			throw new IllegalArgumentException("image == null!");
		}

		RenderedImage im = image;
		SampleModel sampleModel = im.getSampleModel();
		this.numBands = sampleModel.getNumBands();

		this.sourceXOffset = im.getMinX();
		this.sourceYOffset = im.getMinY();
		this.sourceWidth = im.getWidth();
		this.sourceHeight = im.getHeight();
		this.sourceBands = null;
		this.periodX = 1;
		this.periodY = 1;

		if (param != null) {
			Rectangle sourceRegion = param.getSourceRegion();
			if (sourceRegion != null) {
				Rectangle imageBounds = new Rectangle(im.getMinX(), im.getMinY(), im.getWidth(), im.getHeight());
				sourceRegion = sourceRegion.intersection(imageBounds);
				sourceXOffset = sourceRegion.x;
				sourceYOffset = sourceRegion.y;
				sourceWidth = sourceRegion.width;
				sourceHeight = sourceRegion.height;
			}

			// Adjust for subsampling offsets
			int gridX = param.getSubsamplingXOffset();
			int gridY = param.getSubsamplingYOffset();
			sourceXOffset += gridX;
			sourceYOffset += gridY;
			sourceWidth -= gridX;
			sourceHeight -= gridY;

			periodX = param.getSourceXSubsampling();
			periodY = param.getSourceYSubsampling();

			int[] sBands = param.getSourceBands();
			if (sBands != null) {
				sourceBands = sBands;
				numBands = sourceBands.length;
			}
		}

		int destWidth = (sourceWidth + periodX - 1) / periodX;
		int destHeight = (sourceHeight + periodY - 1) / periodY;
		if (destWidth <= 0 || destHeight <= 0) {
			throw new IllegalArgumentException("Empty source region!");
		}

		metadata = new PNGMetadata();

		if (param != null) {
			switch (param.getProgressiveMode()) {
				/*
				 * MODE_DISABLED
				 * MODE_EXPLICIT
				 * MODE_COPY_FROM_METADATA
				 * MODE_DEFAULT
				 * */
				case ImageWriteParam.MODE_DEFAULT:
					metadata.IHDR_interlaceMethod = 1;
					break;
				case ImageWriteParam.MODE_DISABLED:
					metadata.IHDR_interlaceMethod = 0;
					break;
			}
		}

		metadata.initialize(new ImageTypeSpecifier(im), numBands);

		metadata.IHDR_width = destWidth;
		metadata.IHDR_height = destHeight;

		this.bpp = numBands * ((metadata.IHDR_bitDepth == 16) ? 2 : 1);

		initializeScaleTables(sampleModel.getSampleSize());

		clearAbortRequest();

		processImageStarted(0);

		try {
			write_magic();
			write_IHDR();

			write_cHRM();
			write_gAMA();
			write_iCCP();
			write_sBIT();
			write_sRGB();

			write_PLTE();

			write_hIST();
			write_tRNS();
			write_bKGD();

			write_pHYs();
			write_sPLT();
			write_tIME();
			write_tEXt();
			write_iTXt();
			write_zTXt();

			writeUnknownChunks();

			final int compressionQuality;
			switch (param.getCompressionMode()) {
				case ImageWriteParam.MODE_DISABLED:
					compressionQuality = Deflater.NO_COMPRESSION;
					break;
				case ImageWriteParam.MODE_EXPLICIT:
					compressionQuality = MH.clip((int) (param.getCompressionQuality() * 10), Deflater.NO_COMPRESSION, Deflater.BEST_COMPRESSION);
					break;
				case ImageWriteParam.MODE_DEFAULT:
				default:
					compressionQuality = Deflater.DEFAULT_COMPRESSION;
					break;
				case ImageWriteParam.MODE_COPY_FROM_METADATA:
					throw new UnsupportedOperationException("Compression mode of COPY_FROM_METADATA not supported");
			}
			write_IDAT(im, compressionQuality);

			if (abortRequested()) {
				processWriteAborted();
			} else {
				writeIEND();
				processImageComplete();
			}
		} catch (IOException e) {
			throw new IIOException("I/O error writing PNG file!", e);
		}
	}

	public boolean isAborted() {
		return aborted;
	}

	public void setAborted() {
		this.aborted = true;
	}
}
