package com.larkinpoint.analytics.state;

import java.util.List;

import com.f1.base.IdeableGenerator;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;

public class OptionStrike {

	private OptionMessage call, put;
	private double strikePrice;

	public OptionStrike(double strikePrice) {
		this.strikePrice = strikePrice;
		this.call = null;
		this.put = null;
	}
	public OptionStrike(double strikePrice, OptionMessage call, OptionMessage put) {
		this.strikePrice = strikePrice;
		this.call = call;
		this.put = put;
	}

	public double getStrikePrice() {
		return strikePrice;
	}

	public OptionMessage getCall() {
		return call;
	}

	public void setCall(OptionMessage option) {
		if (this.call != null) {
			//	System.out.println("Bad Record for " + option.getOptionId() + " and " + this.call.getOptionId() + " on quote date " + option.getTradeDate());
			System.out.println("Bad Record for Calls on " + option.getTradeDate().toStringNoTimeZone() + " for " + this.call.getOptionId() + ":" + option.getOptionId());
			return;
		}

		this.call = option;
		if (this.put != null) {
			linkOptions();
		}

	}

	public OptionMessage getPut() {
		return put;
	}

	public void setPut(OptionMessage option) {
		if (this.put != null) {

			System.out.println("Bad Record for Puts on " + option.getTradeDate().toStringNoTimeZone() + " for " + this.put.getOptionId() + ":" + option.getOptionId());
			return;
		}
		this.put = option;
		if (this.call != null) {
			linkOptions();
		}
	}
	public void buildOptionlist(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {
		SpreadMessage newSpread = ideableGenerator.nw(SpreadMessage.class);
		if (getCall() != null && getPut() != null) {
			newSpread.setLeg1(getCall());
			newSpread.setLeg2(getPut());
			newSpread.setLegCounts(2);
			sink.add(newSpread);
		}

	}
	public void linkOptions() {

		if (call.isLocked() == false && put.isLocked() == false) {
			double straddle, intrinsic, cpratio;
			call.setPairedOption(put.getOptionId());
			put.setPairedOption(call.getOptionId());
			call.setPairedOptionType(OptionMessage.STRADDLE_TYPE);
			put.setPairedOptionType(OptionMessage.STRADDLE_TYPE);
			if (put.getBid() < 0.0 || call.getBid() < 0.0 || call.getAsk() < 0.0 || put.getAsk() < 0.0) {
				straddle = (put.getLast() + call.getLast());
				intrinsic = Math.abs(call.getStrike() - call.getUnderlyingClose());
				cpratio = call.getLast() / straddle;

			} else {

				straddle = (put.getBid() + call.getBid() + call.getAsk() + put.getAsk());
				intrinsic = Math.abs(call.getStrike() - call.getUnderlyingClose());
				cpratio = (call.getBid() + call.getAsk()) / straddle;
				straddle *= .5;
			}
			put.setPairedValue((float) straddle);
			call.setPairedValue((float) straddle);
			put.setPairedOm(call);
			call.setPairedOm(put);

			put.setPairedRatio((float) cpratio);
			call.setPairedRatio((float) cpratio);

			call.lock();
			put.lock();
		}
	}
}
