package com.f1.tcartsim.verify.chain;

import java.util.ArrayList;

import com.f1.tcartsim.verify.record.NBBORecord;

public class NBBOChain {
	private ArrayList<Double> bidPx;
	private ArrayList<Double> offerPx;
	private ArrayList<Long> bidTime;
	private ArrayList<Long> offerTime;
	private String symbol;

	public NBBOChain(String symbol) {
		this.symbol = symbol;
		bidPx = new ArrayList<Double>();
		offerPx = new ArrayList<Double>();
		bidTime = new ArrayList<Long>();
		offerTime = new ArrayList<Long>();
	}

	public void addNBBO(NBBORecord r) {
		if (r.getBidSize() != 0 && r.getOfferSize() != 0) {
			if (r.getBidSize() > 0) {
				addBid(r);
			}
			if (r.getOfferSize() > 0) {
				addOffer(r);
			}
		}
	}

	public void addBid(NBBORecord r) {
		bidPx.add(r.getBidPx());
		bidTime.add(r.getTime());
	}
	public void addOffer(NBBORecord r) {
		offerPx.add(r.getOfferPx());
		offerTime.add(r.getTime());
	}

	public int getBidLength() {
		return bidPx.size();
	}

	public int getOfferLength() {
		return offerPx.size();
	}

	public Double getBid(int index) {
		return bidPx.get(index);
	}

	public Double getOffer(int index) {
		return offerPx.get(index);
	}

	public Long getBidTime(int index) {
		return bidTime.get(index);
	}

	public Long getOfferTime(int index) {
		return offerTime.get(index);
	}

	public String getSymbol() {
		return symbol;
	}
}
