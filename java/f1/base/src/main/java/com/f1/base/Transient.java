package com.f1.base;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 
 * Indicates to the F1 persistence engine if, and when, a parameter should be persisted. The default is to always persist
 * 
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Transient {
	/**
	 * never persist
	 */
	byte NONE = 0;

	/**
	 * Only persist when sending across the wire
	 */
	byte WIRE = 1;

	/**
	 * Only persist when sending to disk
	 */
	byte PERSIST = 2;

	/**
	 * Always persist (this is the default)
	 */
	byte BOTH = WIRE | PERSIST;

	byte value() default BOTH;

}

