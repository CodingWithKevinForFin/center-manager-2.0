/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

import org.agrona.DirectBuffer;

/**
 * Represents a quote and amount of trade
 */
@SuppressWarnings("all")
public final class TradeDataDecoder {
	public static final int BLOCK_LENGTH = 17;
	public static final int TEMPLATE_ID = 1;
	public static final int SCHEMA_ID = 1;
	public static final int SCHEMA_VERSION = 0;
	public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

	private final TradeDataDecoder parentMessage = this;
	private DirectBuffer buffer;
	private int initialOffset;
	private int offset;
	private int limit;
	int actingBlockLength;
	int actingVersion;

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

	public DirectBuffer buffer() {
		return buffer;
	}

	public int initialOffset() {
		return initialOffset;
	}

	public int offset() {
		return offset;
	}

	public TradeDataDecoder wrap(final DirectBuffer buffer, final int offset, final int actingBlockLength, final int actingVersion) {
		if (buffer != this.buffer) {
			this.buffer = buffer;
		}
		this.initialOffset = offset;
		this.offset = offset;
		this.actingBlockLength = actingBlockLength;
		this.actingVersion = actingVersion;
		limit(offset + actingBlockLength);

		return this;
	}

	public TradeDataDecoder wrapAndApplyHeader(final DirectBuffer buffer, final int offset, final MessageHeaderDecoder headerDecoder) {
		headerDecoder.wrap(buffer, offset);

		final int templateId = headerDecoder.templateId();
		if (TEMPLATE_ID != templateId) {
			throw new IllegalStateException("Invalid TEMPLATE_ID: " + templateId);
		}

		return wrap(buffer, offset + MessageHeaderDecoder.ENCODED_LENGTH, headerDecoder.blockLength(), headerDecoder.version());
	}

	public TradeDataDecoder sbeRewind() {
		return wrap(buffer, initialOffset, actingBlockLength, actingVersion);
	}

	public int sbeDecodedLength() {
		final int currentLimit = limit();
		sbeSkip();
		final int decodedLength = encodedLength();
		limit(currentLimit);

		return decodedLength;
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

	private final QuoteDecoder quote = new QuoteDecoder();

	public QuoteDecoder quote() {
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

	public int amount() {
		return (buffer.getShort(offset + 15, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
	}

	public String toString() {
		if (null == buffer) {
			return "";
		}

		final TradeDataDecoder decoder = new TradeDataDecoder();
		decoder.wrap(buffer, initialOffset, actingBlockLength, actingVersion);

		return decoder.appendTo(new StringBuilder()).toString();
	}

	public StringBuilder appendTo(final StringBuilder builder) {
		if (null == buffer) {
			return builder;
		}

		final int originalLimit = limit();
		limit(initialOffset + actingBlockLength);
		builder.append("[TradeData](sbeTemplateId=");
		builder.append(TEMPLATE_ID);
		builder.append("|sbeSchemaId=");
		builder.append(SCHEMA_ID);
		builder.append("|sbeSchemaVersion=");
		if (parentMessage.actingVersion != SCHEMA_VERSION) {
			builder.append(parentMessage.actingVersion);
			builder.append('/');
		}
		builder.append(SCHEMA_VERSION);
		builder.append("|sbeBlockLength=");
		if (actingBlockLength != BLOCK_LENGTH) {
			builder.append(actingBlockLength);
			builder.append('/');
		}
		builder.append(BLOCK_LENGTH);
		builder.append("):");
		builder.append("quote=");
		final QuoteDecoder quote = this.quote();
		if (quote != null) {
			quote.appendTo(builder);
		} else {
			builder.append("null");
		}
		builder.append('|');
		builder.append("amount=");
		builder.append(this.amount());

		limit(originalLimit);

		return builder;
	}

	public TradeDataDecoder sbeSkip() {
		sbeRewind();

		return this;
	}
}
