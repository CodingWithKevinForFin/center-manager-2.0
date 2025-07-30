package com.f1.ami.amicommon.customobjects;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Inherited
@Target({ ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface AmiScriptAccessible {

	String name() default "";
	String[] params() default {};
	String help() default "";
	boolean readonly() default false;
}
