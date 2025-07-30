package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.ExecutionFormat;
import com.f1.utils.SH;

public class ExecutionRecord extends OrderRecord {
	private double px;
	private char side;
	private String childOrderID;
	private String executionID;
	private String execIndicator;
	private String exchange;

	public ExecutionRecord(String[] data) {
		super(data);
		assert (getFormat() == 'E');
		setSymbol(getSymbol(data));
		setPx(getPx(data));
		setSize(getSize(data));
		setSide(getSide(data));
		setExchange(getExchange(data));
		setCurrency(getCurrency(data));
		setParentOrderID(getParentOrderID(data));
		setChildOrderID(getChildOrderID(data));
		setExecutionID(getExecutionID(data));
		setExecIndicator(getExecIndicator(data));
		setVariants(getVariants(data));
	}

	protected static String getSymbol(String[] data) {
		return data[ExecutionFormat.POSITION_SYMBOL];
	}
	protected static long getSize(String[] data) {
		return SH.parseLong(data[ExecutionFormat.POSITION_SIZE]);
	}
	protected static String getParentOrderID(String[] data) {
		return data[ExecutionFormat.POSITION_PARENTORDERID];
	}
	protected static String getCurrency(String[] data) {
		return data[ExecutionFormat.POSITION_CURRENCY];
	}
	protected static double getPx(String[] data) {
		return SH.parseDouble(data[ExecutionFormat.POSITION_PX]);
	}
	protected static char getSide(String[] data) {
		return SH.parseChar(data[ExecutionFormat.POSITION_SIDE]);
	}
	protected static String getChildOrderID(String[] data) {
		return data[ExecutionFormat.POSITION_CHILDORDERID];
	}
	protected static String getExecutionID(String[] data) {
		return data[ExecutionFormat.POSITION_EXECUTIONID];
	}
	protected static String getExecIndicator(String[] data) {
		return data[ExecutionFormat.POSITION_EXECINDICATOR];
	}
	protected static String getExchange(String[] data) {
		return data[ExecutionFormat.POSITION_EXCHANGE];
	}
	protected static String getVariants(String[] data) {
		if (ExecutionFormat.POSITION_VARIANTS < data.length)
			return data[ExecutionFormat.POSITION_VARIANTS];
		else
			return "";
	}

	public double getPx() {
		return px;
	}

	public void setPx(double px) {
		this.px = px;
	}

	public char getSide() {
		return side;
	}

	public void setSide(char side) {
		this.side = side;
	}

	public String getChildOrderID() {
		return childOrderID;
	}

	public void setChildOrderID(String childOrderID) {
		this.childOrderID = childOrderID;
	}

	public String getExecutionID() {
		return executionID;
	}

	public void setExecutionID(String executionID) {
		this.executionID = executionID;
	}

	public String getExecIndicator() {
		return execIndicator;
	}

	public void setExecIndicator(String execIndicator) {
		this.execIndicator = execIndicator;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

}
