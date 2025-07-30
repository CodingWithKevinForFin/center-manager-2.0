/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * 
 * supports the ability to be converted to a string by adding itself to a string builder.
 * <P>
 * Calling obj.oString(sink) should result in the same outcome as calling sink.append(obj) unless obj is null of course. The advantage is that many implementations of toString()
 * internally need to create temporary string builders, this eliminates that need for the temporary object creation.
 * 
 */
public interface ToStringable {

	/**
	 * dump the toString() into the supplied string builder
	 * 
	 * @param sink
	 *            the sink to have the toString appended to
	 * @return the supplied sink
	 */
	public StringBuilder toString(StringBuilder sink);
}
