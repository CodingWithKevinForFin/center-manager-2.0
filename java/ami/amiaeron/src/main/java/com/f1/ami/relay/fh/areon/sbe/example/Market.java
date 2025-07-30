/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

@SuppressWarnings("all")
public enum Market {

					/**
					 * New York Stock Exchange
					 */
					NYSE((short) 0),

					/**
					 * National Association of Securities Dealers Automated Quotations
					 */
					NASDAQ((short) 1),

					/**
					 * To be used to represent not present or null.
					 */
					NULL_VAL((short) 255);

	private final short value;

	Market(final short value) {
		this.value = value;
	}

	/**
	 * The raw encoded value in the Java type representation.
	 *
	 * @return the raw value encoded.
	 */
	public short value() {
		return value;
	}

	/**
	 * Lookup the enum value representing the value.
	 *
	 * @param value
	 *            encoded to be looked up.
	 * @return the enum value representing the value.
	 */
	public static Market get(final short value) {
		switch (value) {
			case 0:
				return NYSE;
			case 1:
				return NASDAQ;
			case 255:
				return NULL_VAL;
		}

		throw new IllegalArgumentException("Unknown value: " + value);
	}
}
