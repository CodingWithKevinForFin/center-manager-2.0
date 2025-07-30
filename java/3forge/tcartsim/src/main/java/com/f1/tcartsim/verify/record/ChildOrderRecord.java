package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.ChildOrderFormat;
import com.f1.utils.SH;

public class ChildOrderRecord extends OrderRecord {
	private double limitPx;
	private char status;
	private String childOrderID;
	private String origChildOrderID;
	private boolean hasLimitPx;

	public ChildOrderRecord(String[] data) {
		super(data);
		assert (getFormat() == 'C');
		hasLimitPx = true;
		setSymbol(getSymbol(data));
		setSize(getSize(data));
		setParentOrderID(getParentOrderID(data));
		setCurrency(getCurrency(data));
		setLimitPx(getLimitPx(data));
		setStatus(getStatus(data));
		setChildOrderID(getChildOrderID(data));
		setOrigChildOrderID(getOrigChildOrderID(data));
		setVariants(getVariants(data));
	}

	protected static String getVariants(String[] data) {
		if (ChildOrderFormat.POSITION_VARIANTS < data.length)
			return data[ChildOrderFormat.POSITION_VARIANTS];
		else
			return "";
	}
	protected static String getSymbol(String[] data) {
		return data[ChildOrderFormat.POSITION_SYMBOL];
	}
	protected static long getSize(String[] data) {
		return SH.parseLong(data[ChildOrderFormat.POSITION_SIZE]);
	}
	protected static String getParentOrderID(String[] data) {
		return data[ChildOrderFormat.POSITION_PARENTORDERID];
	}
	protected static String getCurrency(String[] data) {
		return data[ChildOrderFormat.POSITION_CURRENCY];
	}
	protected static double getLimitPx(String[] data) {
		if (SH.equals(data[ChildOrderFormat.POSITION_LIMITPX], "")) {
			return -1;
		}
		return SH.parseDouble(data[ChildOrderFormat.POSITION_LIMITPX]);
	}
	protected static char getStatus(String[] data) {
		return SH.parseChar(data[ChildOrderFormat.POSITION_STATUS]);
	}
	protected static String getChildOrderID(String[] data) {
		return data[ChildOrderFormat.POSITION_CHILDORDERID];
	}
	protected static String getOrigChildOrderID(String[] data) {
		return data[ChildOrderFormat.POSITION_ORIGCHILDORDERID];
	}

	public double getLimitPx() {
		return limitPx;
	}

	public void setLimitPx(double limitPx) {
		if (limitPx == -1) {
			this.limitPx = 0;
			hasLimitPx = false;
		} else {
			this.limitPx = limitPx;
		}
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public String getChildOrderID() {
		return childOrderID;
	}

	public void setChildOrderID(String childOrderID) {
		this.childOrderID = childOrderID;
	}

	public String getOrigChildOrderID() {
		return origChildOrderID;
	}

	public void setOrigChildOrderID(String origChildOrderID) {
		this.origChildOrderID = origChildOrderID;
	}

	public boolean hasLimitPx() {
		return hasLimitPx;
	}

}