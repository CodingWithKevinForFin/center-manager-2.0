package com.f1.sample.index;

import com.f1.base.Message;

public interface SampleOrder extends Message {

	public int getAccountId();

	public void setAccountId(int accountId);

	public int getOrderId();

	public void setOrderId(int orderId);

	public String getItemName();

	public void setItemName(String firstName);

	public double getPrice();

	public void setPrice(double price);
}

