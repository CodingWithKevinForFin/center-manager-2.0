/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

public interface WebResourceManager {

	public byte[] getResource(String url);

	public boolean isResourcePattern(String target);
}
