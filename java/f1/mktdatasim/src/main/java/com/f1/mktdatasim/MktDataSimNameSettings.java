package com.f1.mktdatasim;

import java.util.Random;
import java.util.TimeZone;

import com.f1.base.DayTime;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.BasicDayTime;
import com.f1.utils.SH;

public class MktDataSimNameSettings {
	public static final byte SINE = 1;

	private int name;
	private String currency, venues[];
	private DayTime start, end;
	private double spread;
	private double low, high;
	private byte mode;
	private long period;
	private Random random;
	private boolean isOpen;

	private int[] sizes;

	private double lowPrice = Double.NaN, highPrice = Double.NaN;
	private long volume;

	public Random getRandom() {
		return random;
	}
	public int getName() {
		return name;
	}
	public void setName(int name) {
		this.name = name;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String[] getVenues() {
		return venues;
	}
	public void setVenues(String[] venues) {
		this.venues = venues;
	}
	public DayTime getStart() {
		return start;
	}
	public void setStart(DayTime start) {
		this.start = start;
	}
	public DayTime getEnd() {
		return end;
	}
	public void setEnd(DayTime end) {
		this.end = end;
	}
	public double getSpread() {
		return spread;
	}
	public void setSpread(double spread) {
		this.spread = spread;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public byte getMode() {
		return mode;
	}
	public void setMode(byte mode) {
		this.mode = mode;
	}
	public long getPeriod() {
		return period;
	}
	public void setPeriod(long period) {
		this.period = period;
	}

	public void parse(int key, String value, TimeZone timeZone) {
		this.name = key;
		String[] parts = SH.trimArray(SH.split(';', value));
		String mode = parts[0];
		if ("sine".equals(mode))
			this.mode = SINE;
		else
			throw new RuntimeException("invalid mode: " + mode + " (must be sine)");
		currency = parts[1];
		String priceRange = parts[2];
		low = Double.parseDouble(SH.beforeFirst(priceRange, '-').trim());
		high = Double.parseDouble(SH.afterFirst(priceRange, '-').trim());
		period = ContainerBootstrap.parseToNano(parts[3]) / 1000000;
		String timeRange = parts[4];
		start = parseToTime(SH.beforeFirst(timeRange, '-').trim(), timeZone);
		end = parseToTime(SH.afterFirst(timeRange, '-').trim(), timeZone);
		spread = Double.parseDouble(parts[5]);
		venues = SH.trimArray(SH.split(',', parts[6]));
		String[] s = SH.trimArray(SH.split(',', parts[7]));
		sizes = new int[s.length];
		for (int i = 0; i < s.length; i++)
			sizes[i] = Integer.parseInt(s[i]);
		random = new Random(value.hashCode());
	}
	// msft=sin,USD; 100-200; 2 minutes; ${open.us};. 01; ${venues}
	private DayTime parseToTime(String time, TimeZone timeZone) {
		int hours = Integer.parseInt(SH.beforeFirst(time, ':'));
		int minutes = Integer.parseInt(SH.afterFirst(time, ':'));
		BasicDayTime r = new BasicDayTime(timeZone);
		r.setTime(hours, minutes);
		return r;
	}
	public void setSizes(int[] sizes) {
		this.sizes = sizes;
	}
	public int[] getSizes() {
		return sizes;
	}
	public void onTrade(int lastSize, double lastPrice) {
		if (volume == 0) {
			lowPrice = highPrice = lastPrice;
		} else {
			if (lastPrice < lowPrice)
				lowPrice = lastPrice;
			if (lastPrice > highPrice)
				highPrice = lastPrice;
		}
		volume += lastSize;
	}
	public long getVolume() {
		return volume;
	}

	public double getLowPrice() {
		return lowPrice;
	}
	public double getHighPrice() {
		return highPrice;
	}
	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}
	public boolean isOpen() {
		return isOpen;
	}
	public void resetVolume() {
		lowPrice = Double.NaN;
		highPrice = Double.NaN;
		volume = 0;
	}

}
