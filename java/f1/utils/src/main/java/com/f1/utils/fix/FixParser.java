package com.f1.utils.fix;

/**
 * Converts a string into a {@link FixMap}
 */
public interface FixParser {

	/**
	 * converts a string into a {@link FixMap}
	 * 
	 * @param text
	 *            fix string
	 * @return FixMap representation.
	 */
	FixMap parse(char[] text);

}
