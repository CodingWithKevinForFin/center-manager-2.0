/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

import org.agrona.MutableDirectBuffer;

/**
 * Represents a quote and amount of trade
 */
@SuppressWarnings("all")
public final class TradeDataEncoder {
	public static final int BLOCK_LENGTH = 17;
	public static final int TEMPLATE_ID = 1;
	public static final int SCHEMA_ID = 1;
	public static final int SCHEMA_VERSION = 0;
	public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

	private final TradeDataEncoder parentMessage = this;
	private MutableDirectBuffer buffer;
	private int initialOffset;
	private int offset;
	private int limit;

	public int sbeBlockLength() {
		return BLOCK_LENGTH;
	}

	public int sbeTemplateId() {
		return TEMPLATE_ID;
	}

	public int sbeSchemaId() {
		return SCHEMA_ID;
	}

	public int sbeSchemaVersion() {
		return SCHEMA_VERSION;
	}

	public String sbeSemanticType() {
		return "";
	}

	public MutableDirectBuffer buffer() {
		return buffer;
	}

	public int initialOffset() {
		return initialOffset;
	}

	public int offset() {
		return offset;
	}

	public TradeDataEncoder wrap(final MutableDirectBuffer buffer, final int offset) {
		if (buffer != this.buffer) {
			this.buffer = buffer;
		}
		this.initialOffset = offset;
		this.offset = offset;
		limit(offset + BLOCK_LENGTH);

		return this;
	}

	public TradeDataEncoder wrapAndApplyHeader(final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder) {
		headerEncoder.wrap(buffer, offset).blockLength(BLOCK_LENGTH).templateId(TEMPLATE_ID).schemaId(SCHEMA_ID).version(SCHEMA_VERSION);

		return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
	}

	public int encodedLength() {
		return limit - offset;
	}

	public int limit() {
		return limit;
	}

	public void limit(final int limit) {
		this.limit = limit;
	}

	public static int quoteId() {
		return 1;
	}

	public static int quoteSinceVersion() {
		return 0;
	}

	public static int quoteEncodingOffset() {
		return 0;
	}

	public static int quoteEncodingLength() {
		return 15;
	}

	public static String quoteMetaAttribute(final MetaAttribute metaAttribute) {
		if (MetaAttribute.PRESENCE == metaAttribute) {
			return "required";
		}

		return "";
	}

	private final QuoteEncoder quote = new QuoteEncoder();

	public QuoteEncoder quote() {
		quote.wrap(buffer, offset + 0);
		return quote;
	}

	public static int amountId() {
		return 2;
	}

	public static int amountSinceVersion() {
		return 0;
	}

	public static int amountEncodingOffset() {
		return 15;
	}

	public static int amountEncodingLength() {
		return 2;
	}

	public static String amountMetaAttribute(final MetaAttribute metaAttribute) {
		if (MetaAttribute.PRESENCE == metaAttribute) {
			return "required";
		}

		return "";
	}

	public static int amountNullValue() {
		return 65535;
	}

	public static int amountMinValue() {
		return 0;
	}

	public static int amountMaxValue() {
		return 65534;
	}

	public TradeDataEncoder amount(final int value) {
		buffer.putShort(offset + 15, (short) value, java.nio.ByteOrder.LITTLE_ENDIAN);
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

		final TradeDataDecoder decoder = new TradeDataDecoder();
		decoder.wrap(buffer, initialOffset, BLOCK_LENGTH, SCHEMA_VERSION);

		return decoder.appendTo(builder);
	}
}
