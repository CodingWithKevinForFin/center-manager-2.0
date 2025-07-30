/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.codegen.impl;

import com.f1.base.Message;
import com.f1.base.UnsupportedField;

public interface ValueTest extends Message {

	public double getPrice();
	public void setPrice(double price);

	public int getQuantity();
	public void setQuantity(int price);

	@UnsupportedField
	public long getNow();
	public void setNow(long now);

}
