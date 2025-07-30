/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * A short, yet unique, string that will be converted to a long for identifying a class, usually used for transmission: passed in the header before the body of a given object. Note
 * that VINS and VIDS are one to one. Technically, VINS get translated to 64 bit (long) numbers that are VIDS. This is what gets moved across the wire. Using strings makes it much
 * easier to namespace for teams.
 * <P>
 * <B> VINS may contain only 2 to 12 upper case letters, numbers and periods.
 * <P>
 * </B>For Example: MY.MSG.ORD1<B>
 * 
 * <P>
 * 
 * @see VID
 * 
 */
@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VIN {

	/**
	 * @return truly-unique (across VMs) string representing this class.
	 */
	String value();
}
