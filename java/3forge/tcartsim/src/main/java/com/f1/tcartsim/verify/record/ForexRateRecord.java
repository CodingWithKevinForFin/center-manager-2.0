/**
 * 
 */
package com.f1.tcartsim.verify.record;

import com.f1.tcartsim.verify.format.ForexRateFormat;
import com.f1.utils.SH;

/**
 * @author george
 */
public class ForexRateRecord extends Record {
	private String currency1;
	private String currency2;
	private double fxRate;

	public ForexRateRecord(String[] data) {
		super(data);
		assert (getFormat(data) == 'X');
		setCurrency1(getCurrency1(data));
		setCurrency2(getCurrency2(data));
		setFxRate(getFxRate(data));
		setVariants(getVariants(data));
	}

	protected static String getCurrency1(String[] data) {
		return data[ForexRateFormat.POSITION_CURRENCY1];
	}
	protected static String getCurrency2(String[] data) {
		return data[ForexRateFormat.POSITION_CURRENCY2];
	}
	protected static double getFxRate(String[] data) {
		return SH.parseDouble(data[ForexRateFormat.POSITION_FXRATE]);
	}
	protected static String getVariants(String[] data) {
		if (ForexRateFormat.POSITION_VARIANTS < data.length) {
			return data[ForexRateFormat.POSITION_VARIANTS];
		} else
			return "";
	}

	public String getCurrency1() {
		return currency1;
	}

	public String getCurrency2() {
		return currency2;
	}

	public void setCurrency2(String currency2) {
		this.currency2 = currency2;
	}

	public double getFxRate() {
		return fxRate;
	}

	public void setFxRate(double fxRate) {
		this.fxRate = fxRate;
	}

	public void setCurrency1(String currency1) {
		this.currency1 = currency1;
	}
}
