/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

/**
 * Identifies a class which can be identified by a unique name and optionally a unique id. A simple, default implementation might use the fully qualified name as the identifiable
 * name. The intent is that the id (both int and string) should be unique throughout, not only the JVM, but all JVMs in order to support communication across instances.
 * <P>
 * Additionally, a checksum which describes the class can be aquired by calling {@link #askCheckSum()}.
 * <P>
 * Note: this DOES NOT identify an object instance... it identifies a class.
 */
public interface Ideable {
	/**
	 * returned by {@link #askVid()} to indicate this class doesn't support int ids (and one should fall back to {@link #askIdeableName()} instead)
	 */
	public long NO_IDEABLEID = -1;

	/**
	 * A unique string which identifies this <B>class</B> (not instance).
	 * 
	 * @return non-blank string, never null.
	 */
	public String askIdeableName();

	/**
	 * A unique int which identifies this <B>class</B> (not instance).
	 * 
	 * @return a unique int, -1 indicates int ids are not supported
	 */
	public long askVid();

	/**
	 * returns a 'relatively unique' check sum which can be used to verify the definition of a class. Implementation is non-specific
	 * 
	 * @return string, never null
	 */
	public String askCheckSum();

}
