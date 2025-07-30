/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.suite.web;

import com.f1.base.Message;
import com.f1.utils.ByteArray;

public interface HttpFile extends Message {

	public String getName();

	public void setName(String name);

	public ByteArray getData();

	public void setData(ByteArray data);
}
