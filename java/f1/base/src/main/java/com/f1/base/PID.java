/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * The {@link #value()} should uniquely identify the methoed annotated within the class/interface this method is declared in. When supplied, the params will be transmitted using
 * the byte value instead of the full bean name (which is the default behavior).
 * <P>
 * This improves compactness of the message and also reduces lookup time.
 * 
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PID {
	byte value();
}
