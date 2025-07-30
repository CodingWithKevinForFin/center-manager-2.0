package com.f1.utils;

import com.f1.base.IdeableGenerator;

/*
 * Can convert (or serialize) a byte array to and from some high level object. Because the conversion of bytes to objects will result in the creation of objects, an object generator may be required (hence the generator's getter/setter)
 */
public interface OfflineConverter extends Cloneable {
	/**
	 * convert a stream of bytes into an object.
	 * 
	 * @param in
	 *            the stream of bytes to convert into an object
	 * @return the newly created object based on the supplied byte array
	 */
	public Object bytes2Object(byte[] in);

	/**
	 * convert the object into a stream of bytes.
	 * 
	 * @param in
	 *            the object to convert
	 * @return the newly created byte array representing the supplied object
	 */
	public byte[] object2Bytes(Object in);

	/**
	 * @param generator
	 *            the generator to use when creating objects in the {@link #bytes2Object(byte[])} method
	 */
	public void setIdeableGenerator(IdeableGenerator generator);

	/**
	 * @return generator the generator to use when creating objects in the {@link #bytes2Object(byte[])} method
	 */
	public IdeableGenerator getIdeableGenerator();

	public int getOptions();

	public void setOptions(int options);

	public OfflineConverter clone();

	int OPTION_IGNORE_UNCONVERTABLE = 1;
	int OPTION_SKIP_TRANSIENT_WIRE = 2;
	int OPTION_COMPACT_MODE = 4;
	int OPTION_TREAT_VALUED_AS_MAPS = 8;
	int OPTION_TREAT_VALUED_AS_MAP_BACKED_VALUED = 16;
	int OPTION_STRICT_VALIDATION = 32;
	int OPTION_SKIP_TRANSIENT_PERSIST = 64;
	int OPTION_INCLUDE_CLASSNAME = 128;
	int OPTION_TREAT_NAN_AS_NULL = 256;
	int OPTION_COMPACT_SEMI = 512;//If set, COMPACT_MODE bit must be clear
	int OPTION_SORT_MAPS = 1024;
}
