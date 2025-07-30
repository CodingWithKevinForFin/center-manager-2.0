/**
 * 
 */
package com.f1.tcartsim.verify.chain;

import java.util.ArrayList;

import com.f1.tcartsim.verify.record.TradeRecord;

/**
 * @author george
 * 
 */
public class TradeChain {
	private ArrayList<Double> px;
	private ArrayList<Long> time;
	private String symbol;

	public TradeChain(String symbol) {
		this.symbol = symbol;
		px = new ArrayList<Double>();
		time = new ArrayList<Long>();
	}

	public void addTrade(TradeRecord r) {
		px.add(r.getPx());
		time.add(r.getTime());
	}

	public int getLength() {
		return px.size();
	}

	public Double getPx(int index) {
		return px.get(index);
	}

	public Long getTime(int index) {
		return time.get(index);
	}

	public String getSymbol() {
		return symbol;
	}
}
