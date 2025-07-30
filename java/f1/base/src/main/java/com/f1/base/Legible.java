package com.f1.base;

/**
 * represents classes with a more enhanced version of string representation then just toString(). It should be expected that the returned string may be multilined
 */
public interface Legible {
	/**
	 * @return an in-depth string representation which is (a) human readable and (b) multi-lined
	 */
	String toLegibleString();
}
