package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.ParentOrderFormat;
import com.f1.utils.SH;

public class ParentOrderRecord extends OrderRecord {
	private double limitPx;
	private char side;
	private char status;
	private String system;
	private String strategy;
	private String account;
	private long startTime;
	private long endTime;
	private boolean hasLimitPx;

	public ParentOrderRecord(String[] data) {
		super(data);
		assert (getFormat() == 'O');
		hasLimitPx = true;
		setSymbol(getSymbol(data));
		setSize(getSize(data));
		setLimitPx(getLimitPx(data));
		setSide(getSide(data));
		setCurrency(getCurrency(data));
		setParentOrderID(getParentOrderID(data));
		setStatus(getStatus(data));
		setSystem(getSystem(data));
		setStrategy(getStrategy(data));
		setAccount(getAccount(data));
		setStartTime(getStartTime(data));
		setEndTime(getEndTime(data));
		setVariants(getVariants(data));
		//		System.out.println(SH.join(',', data));
	}
	protected static String getSymbol(String[] data) {
		return data[ParentOrderFormat.POSITION_SYMBOL];
	}

	protected static long getSize(String[] data) {
		return SH.parseLong(data[ParentOrderFormat.POSITION_SIZE]);
	}

	protected static String getParentOrderID(String[] data) {
		return data[ParentOrderFormat.POSITION_PARENTORDERID];
	}
	protected static String getCurrency(String[] data) {
		return data[ParentOrderFormat.POSITION_CURRENCY];
	}
	protected static String getVariants(String[] data) {
		if (ParentOrderFormat.POSITION_VARIANTS < data.length)
			return data[ParentOrderFormat.POSITION_VARIANTS];
		else
			return "";
	}
	protected static double getLimitPx(String[] data) {
		if (SH.equals(data[ParentOrderFormat.POSITION_LIMITPX], "")) {
			return -1;
		}
		return SH.parseDouble(data[ParentOrderFormat.POSITION_LIMITPX]);
	}
	protected static char getSide(String[] data) {
		return SH.parseChar(data[ParentOrderFormat.POSITION_SIDE]);
	}
	protected static char getStatus(String[] data) {
		return SH.parseChar(data[ParentOrderFormat.POSITION_STATUS]);
	}
	protected static String getSystem(String[] data) {
		return data[ParentOrderFormat.POSITION_SYSTEM];
	}
	protected static String getStrategy(String[] data) {
		return data[ParentOrderFormat.POSITION_STRATEGY];
	}
	protected static String getAccount(String[] data) {
		return data[ParentOrderFormat.POSITION_ACCOUNT];
	}
	protected static long getStartTime(String[] data) {
		if (SH.equals(data[ParentOrderFormat.POSITION_STARTTIME], "")) {
			return -1;
		}
		return SH.parseLong(data[ParentOrderFormat.POSITION_STARTTIME]);
	}
	protected static long getEndTime(String[] data) {
		if (SH.equals(data[ParentOrderFormat.POSITION_ENDTIME], "")) {
			return -1;
		}
		return SH.parseLong(data[ParentOrderFormat.POSITION_ENDTIME]);
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

	public char getSide() {
		return side;
	}

	public void setSide(char side) {
		this.side = side;
	}

	public char getStatus() {
		return status;
	}

	public void setStatus(char status) {
		this.status = status;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public String getStrategy() {
		return strategy;
	}

	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public boolean hasLimitPx() {
		return hasLimitPx;
	}

}