/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

import org.agrona.DirectBuffer;

/**
 * A quote represents the price of a stock in a market
 */
@SuppressWarnings("all")
public final class QuoteDecoder {
	public static final int SCHEMA_ID = 1;
	public static final int SCHEMA_VERSION = 0;
	public static final int ENCODED_LENGTH = 15;
	public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

	private int offset;
	private DirectBuffer buffer;

	public QuoteDecoder wrap(final DirectBuffer buffer, final int offset) {
		if (buffer != this.buffer) {
			this.buffer = buffer;
		}
		this.offset = offset;

		return this;
	}

	public DirectBuffer buffer() {
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

	public static int marketSinceVersion() {
		return 0;
	}

	public short marketRaw() {
		return ((short) (buffer.getByte(offset + 0) & 0xFF));
	}

	public Market market() {
		return Market.get(((short) (buffer.getByte(offset + 0) & 0xFF)));
	}

	public static int symbolEncodingOffset() {
		return 1;
	}

	public static int symbolEncodingLength() {
		return 4;
	}

	public static int symbolSinceVersion() {
		return 0;
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

	public byte symbol(final int index) {
		if (index < 0 || index >= 4) {
			throw new IndexOutOfBoundsException("index out of range: index=" + index);
		}

		final int pos = offset + 1 + (index * 1);

		return buffer.getByte(pos);
	}

	public static String symbolCharacterEncoding() {
		return java.nio.charset.StandardCharsets.US_ASCII.name();
	}

	public int getSymbol(final byte[] dst, final int dstOffset) {
		final int length = 4;
		if (dstOffset < 0 || dstOffset > (dst.length - length)) {
			throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
		}

		buffer.getBytes(offset + 1, dst, dstOffset, length);

		return length;
	}

	public String symbol() {
		final byte[] dst = new byte[4];
		buffer.getBytes(offset + 1, dst, 0, 4);

		int end = 0;
		for (; end < 4 && dst[end] != 0; ++end)
			;

		return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
	}

	public int getSymbol(final Appendable value) {
		for (int i = 0; i < 4; ++i) {
			final int c = buffer.getByte(offset + 1 + i) & 0xFF;
			if (c == 0) {
				return i;
			}

			try {
				value.append(c > 127 ? '?' : (char) c);
			} catch (final java.io.IOException ex) {
				throw new java.io.UncheckedIOException(ex);
			}
		}

		return 4;
	}

	public static int priceEncodingOffset() {
		return 5;
	}

	public static int priceEncodingLength() {
		return 9;
	}

	public static int priceSinceVersion() {
		return 0;
	}

	private final DecimalDecoder price = new DecimalDecoder();

	public DecimalDecoder price() {
		price.wrap(buffer, offset + 5);
		return price;
	}

	public static int currencyEncodingOffset() {
		return 14;
	}

	public static int currencyEncodingLength() {
		return 1;
	}

	public static int currencySinceVersion() {
		return 0;
	}

	public short currencyRaw() {
		return ((short) (buffer.getByte(offset + 14) & 0xFF));
	}

	public Currency currency() {
		return Currency.get(((short) (buffer.getByte(offset + 14) & 0xFF)));
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

		builder.append('(');
		builder.append("market=");
		builder.append(this.market());
		builder.append('|');
		builder.append("symbol=");
		for (int i = 0; i < symbolLength() && this.symbol(i) > 0; i++) {
			builder.append((char) this.symbol(i));
		}
		builder.append('|');
		builder.append("price=");
		final DecimalDecoder price = this.price();
		if (price != null) {
			price.appendTo(builder);
		} else {
			builder.append("null");
		}
		builder.append('|');
		builder.append("currency=");
		builder.append(this.currency());
		builder.append(')');

		return builder;
	}
}
