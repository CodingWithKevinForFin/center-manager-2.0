/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

import org.agrona.MutableDirectBuffer;

/**
 * A quote represents the price of a stock in a market
 */
@SuppressWarnings("all")
public final class QuoteEncoder {
	public static final int SCHEMA_ID = 1;
	public static final int SCHEMA_VERSION = 0;
	public static final int ENCODED_LENGTH = 15;
	public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

	private int offset;
	private MutableDirectBuffer buffer;

	public QuoteEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
		if (buffer != this.buffer) {
			this.buffer = buffer;
		}
		this.offset = offset;

		return this;
	}

	public MutableDirectBuffer buffer() {
		return buffer;
	}

	public int offset() {
		return offset;
	}

	public int encodedLength() {
		return ENCODED_LENGTH;
	}

	public int sbeSchemaId() {
		return SCHEMA_ID;
	}

	public int sbeSchemaVersion() {
		return SCHEMA_VERSION;
	}

	public static int marketEncodingOffset() {
		return 0;
	}

	public static int marketEncodingLength() {
		return 1;
	}

	public QuoteEncoder market(final Market value) {
		buffer.putByte(offset + 0, (byte) value.value());
		return this;
	}

	public static int symbolEncodingOffset() {
		return 1;
	}

	public static int symbolEncodingLength() {
		return 4;
	}

	public static byte symbolNullValue() {
		return (byte) 0;
	}

	public static byte symbolMinValue() {
		return (byte) 32;
	}

	public static byte symbolMaxValue() {
		return (byte) 126;
	}

	public static int symbolLength() {
		return 4;
	}

	public QuoteEncoder symbol(final int index, final byte value) {
		if (index < 0 || index >= 4) {
			throw new IndexOutOfBoundsException("index out of range: index=" + index);
		}

		final int pos = offset + 1 + (index * 1);
		buffer.putByte(pos, value);

		return this;
	}
	public QuoteEncoder putSymbol(final byte value0, final byte value1, final byte value2, final byte value3) {
		buffer.putByte(offset + 1, value0);
		buffer.putByte(offset + 2, value1);
		buffer.putByte(offset + 3, value2);
		buffer.putByte(offset + 4, value3);

		return this;
	}

	public static String symbolCharacterEncoding() {
		return java.nio.charset.StandardCharsets.US_ASCII.name();
	}

	public QuoteEncoder putSymbol(final byte[] src, final int srcOffset) {
		final int length = 4;
		if (srcOffset < 0 || srcOffset > (src.length - length)) {
			throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
		}

		buffer.putBytes(offset + 1, src, srcOffset, length);

		return this;
	}

	public QuoteEncoder symbol(final String src) {
		final int length = 4;
		final int srcLength = null == src ? 0 : src.length();
		if (srcLength > length) {
			throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
		}

		buffer.putStringWithoutLengthAscii(offset + 1, src);

		for (int start = srcLength; start < length; ++start) {
			buffer.putByte(offset + 1 + start, (byte) 0);
		}

		return this;
	}

	public QuoteEncoder symbol(final CharSequence src) {
		final int length = 4;
		final int srcLength = null == src ? 0 : src.length();
		if (srcLength > length) {
			throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
		}

		buffer.putStringWithoutLengthAscii(offset + 1, src);

		for (int start = srcLength; start < length; ++start) {
			buffer.putByte(offset + 1 + start, (byte) 0);
		}

		return this;
	}

	public static int priceEncodingOffset() {
		return 5;
	}

	public static int priceEncodingLength() {
		return 9;
	}

	private final DecimalEncoder price = new DecimalEncoder();

	public DecimalEncoder price() {
		price.wrap(buffer, offset + 5);
		return price;
	}

	public static int currencyEncodingOffset() {
		return 14;
	}

	public static int currencyEncodingLength() {
		return 1;
	}

	public QuoteEncoder currency(final Currency value) {
		buffer.putByte(offset + 14, (byte) value.value());
		return this;
	}

	public String toString() {
		if (null == buffer) {
			return "";
		}

		return appendTo(new StringBuilder()).toString();
	}

	public StringBuilder appendTo(final StringBuilder builder) {
		if (null == buffer) {
			return builder;
		}

		final QuoteDecoder decoder = new QuoteDecoder();
		decoder.wrap(buffer, offset);

		return decoder.appendTo(builder);
	}
}
