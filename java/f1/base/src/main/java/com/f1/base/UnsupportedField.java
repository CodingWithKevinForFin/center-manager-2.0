/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * Indicates to the autocoder that the class should skip support for the annotated getter/setter.
 * <P>
 * This is used in extreme cases for performance gains, it will not declare the member variable (saving memory) and marshalling will be skipped. Attempts to call the unsupported
 * get/set methods will throw an exception
 * 
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UnsupportedField {

}
