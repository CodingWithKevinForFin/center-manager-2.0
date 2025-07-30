/* Generated SBE (Simple Binary Encoding) message codec. */
package com.f1.ami.relay.fh.areon.sbe.example;

@SuppressWarnings("all")
public enum Currency {

						/**
						 * US Dollar
						 */
						USD((short) 0),

						/**
						 * Euro
						 */
						EUR((short) 1),

						/**
						 * To be used to represent not present or null.
						 */
						NULL_VAL((short) 255);

	private final short value;

	Currency(final short value) {
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
	public static Currency get(final short value) {
		switch (value) {
			case 0:
				return USD;
			case 1:
				return EUR;
			case 255:
				return NULL_VAL;
		}

		throw new IllegalArgumentException("Unknown value: " + value);
	}
}
