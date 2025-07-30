package com.f1.ami.web.auth;

/**
 * 
 * Key/value pair for this user's profile
 * <P>
 * see {@link BasicAmiAttribute} for basic implementation
 * 
 */
public interface AmiAuthAttribute {
	String getKey();
	Object getValue();
}
