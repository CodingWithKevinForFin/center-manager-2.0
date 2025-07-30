package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.TradeFormat;
import com.f1.utils.SH;

/**
 * @author george
 * 
 */
public class TradeRecord extends Record {
	private String symbol;
	private String exchange;
	private double px;
	private long size;
	private String currency;

	public TradeRecord(String[] data) {
		super(data);
		assert (getFormat() == 'T');
		setSymbol(getSymbol(data));
		setExchange(getExchange(data));
		setPx(getPx(data));
		setSize(getSize(data));
		setCurrency(getCurrency(data));
		setVariants(getVariants(data));
	}

	protected static String getSymbol(String[] data) {
		return data[TradeFormat.POSITION_SYMBOL];
	}
	protected static String getExchange(String[] data) {
		return data[TradeFormat.POSITION_EXCHANGE];
	}
	protected static double getPx(String[] data) {
		return SH.parseDouble(data[TradeFormat.POSITION_PX]);
	}
	protected static long getSize(String[] data) {
		return SH.parseLong(data[TradeFormat.POSITION_SIZE]);
	}
	protected static String getCurrency(String[] data) {
		return data[TradeFormat.POSITION_CURRENCY];
	}
	protected static String getVariants(String[] data) {
		if (TradeFormat.POSITION_VARIANTS < data.length)
			return data[TradeFormat.POSITION_VARIANTS];
		else
			return "";
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getExchange() {
		return exchange;
	}

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}

	public double getPx() {
		return px;
	}

	public void setPx(double px) {
		this.px = px;
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
}
