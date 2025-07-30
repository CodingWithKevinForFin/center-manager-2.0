/**
 * 
 */
package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.NBBOFormat;
import com.f1.utils.SH;

/**
 * @author george
 * 
 */
public class NBBORecord extends Record {
	private String symbol;
	private String exchange;
	private long bidSize;
	private long offerSize;
	private double bidPx;
	private double offerPx;
	private String currency;

	public NBBORecord(String[] data) {
		super(data);
		assert (getFormat() == 'N');
		setSymbol(getSymbol(data));
		setExchange(getExchange(data));
		setBidSize(getBidSize(data));
		setOfferSize(getOfferSize(data));
		setBidPx(getBidPx(data));
		setOfferPx(getOfferPx(data));
		setCurrency(getCurrency(data));
		setVariants(getVariants(data));
	}

	protected static String getSymbol(String[] data) {
		return data[NBBOFormat.POSITION_SYMBOL];
	}

	protected static String getExchange(String[] data) {
		return data[NBBOFormat.POSITION_EXCHANGE];
	}

	protected static long getBidSize(String[] data) {
		return SH.parseLong(data[NBBOFormat.POSITION_BIDSIZE]);
	}

	protected static long getOfferSize(String[] data) {
		return SH.parseLong(data[NBBOFormat.POSITION_OFFERSIZE]);
	}

	protected static double getBidPx(String[] data) {
		return SH.parseDouble(data[NBBOFormat.POSITION_BIDPX]);
	}

	protected static double getOfferPx(String[] data) {
		return SH.parseDouble(data[NBBOFormat.POSITION_OFFERPX]);
	}

	protected static String getCurrency(String[] data) {
		return data[NBBOFormat.POSITION_CURRENCY];
	}

	protected static String getVariants(String[] data) {
		if (NBBOFormat.POSITION_VARIANTS < data.length)
			return data[NBBOFormat.POSITION_VARIANTS];
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

	public long getBidSize() {
		return bidSize;
	}

	public void setBidSize(long bidSize) {
		this.bidSize = bidSize;
	}

	public long getOfferSize() {
		return offerSize;
	}

	public void setOfferSize(long offerSize) {
		this.offerSize = offerSize;
	}

	public double getBidPx() {
		return bidPx;
	}

	public void setBidPx(double bidPx) {
		this.bidPx = bidPx;
	}

	public double getOfferPx() {
		return offerPx;
	}

	public void setOfferPx(double offerPx) {
		this.offerPx = offerPx;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}
}
