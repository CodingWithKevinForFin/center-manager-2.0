/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen;

import java.util.Map;

public interface CodeableParam {

	boolean isAbstract();

	boolean isValuedType();

	String getName();

	String getVarname();

	String getUpperCasedName();

	Class getType();

	Class getBoxedType();

	byte getBasicType();

	String getDefaultValue();

	Map<String, String> getAnnotations();

	String getStreamName();

	boolean getIsBoxedType();

	boolean getIsImmutable();

	boolean getIsString();

	boolean getIsFixPoint();

	int getPosition();

	void setPosition(int i);

	boolean getIsBoolean();

	boolean getIsByte();

	boolean getIsChar();

	boolean getIsShort();

	boolean getIsInt();

	boolean getIsLong();

	boolean getIsDouble();

	boolean getIsFloat();

	boolean getIsValuedListenable();

	byte getTransience();

	boolean getSupported();

}
