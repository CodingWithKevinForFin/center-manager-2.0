package com.f1.tcartsim.verify.record;

public abstract class OrderRecord extends Record {
	private String parentOrderID;
	private String symbol;
	private long size;
	private String currency;

	public OrderRecord(String[] data) {
		super(data);
	}

	public String getParentOrderID() {
		return parentOrderID;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public void setParentOrderID(String parentOrderID) {
		this.parentOrderID = parentOrderID;
	}
}