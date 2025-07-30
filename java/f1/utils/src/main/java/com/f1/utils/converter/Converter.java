/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter;

public interface Converter<TO, FROM> {
	public TO to(FROM in) throws ConverterException;

	public FROM from(TO in) throws ConverterException;

	public Class<FROM> getToType();

	public Class<TO> getFromType();

}
