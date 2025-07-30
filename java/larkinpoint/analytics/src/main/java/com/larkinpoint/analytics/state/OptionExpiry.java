package com.larkinpoint.analytics.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.f1.base.IdeableGenerator;
import com.f1.utils.BasicDay;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.TupleComparator;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;

public class OptionExpiry {

	final public SortedMap<Float, OptionStrike> strikePx2strikes = new TreeMap<Float, OptionStrike>();
	final public List<Tuple2<Float, OptionStrike>> nearestStrikes = new ArrayList<Tuple2<Float, OptionStrike>>();
	final public BasicDay expiryDay;
	final public long daysToExpiry;

	public long getDaysToExpiry() {
		return daysToExpiry;
	}

	public OptionExpiry(BasicDay expiryDay, long days) {
		this.expiryDay = expiryDay;
		this.daysToExpiry = days;

	}
	public void sort() {
		TupleComparator.sort(nearestStrikes, 0);
	}
	public void addRecord(OptionMessage ov) {
		OptionStrike existing = this.strikePx2strikes.get(ov.getStrike());
		if (existing == null) {

			existing = new OptionStrike(ov.getStrike(), null, null);
			this.strikePx2strikes.put(ov.getStrike(), existing);
			float dist = Math.abs(ov.getStrike() - ov.getUnderlyingClose());
			this.nearestStrikes.add(new Tuple2<Float, OptionStrike>(dist, existing));
			sort();
		}
		if (ov.getCP())
			existing.setPut(ov);
		else
			existing.setCall(ov);
	}
	public void addStrike(float px, OptionStrike optionValues) {
		this.strikePx2strikes.put(px, optionValues);
	}

	public OptionStrike getStrike(float px) {
		return strikePx2strikes.get(px);
	}

	public OptionStrike getStrikeGreaterThan(float px) {
		return strikePx2strikes.get(strikePx2strikes.tailMap(px).firstKey());
	}

	public OptionStrike getStrikeLessThan(float px) {
		return strikePx2strikes.get(strikePx2strikes.headMap(px).lastKey());
	}
	public Collection<OptionStrike> getAllStrikes() {
		return strikePx2strikes.values();
	}

	public BasicDay getExpiryDay() {
		return expiryDay;
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {
		for (OptionStrike cp : getAllStrikes())
			cp.buildOptionlist(ideableGenerator, sink);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, int topX) {
		int i = 0;

		for (Tuple2<Float, OptionStrike> cp : nearestStrikes) {
			if (i++ < topX)
				cp.getB().buildOptionlist(ideableGenerator, sink);
			else
				break;
		}
	}
	public long[] findRecords(float cpRatio, int strikeStep) {
		long[] s = null;
		if (strikeStep == 0)
			return findStraddle(cpRatio);
		else
			return findStrangle(cpRatio, strikeStep);
	}
	//public void findStraddle(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, float cpRatio) {
	public long[] findStraddle(float cpRatio) {
		long[] s = new long[2];
		float diff = (float) 0.0;
		OptionMessage temp = null;
		for (Tuple2<Float, OptionStrike> cp : nearestStrikes) {
			float temp1 = (float) 0.0;
			if (cp.getB().getCall() == null || cp.getB().getPut() == null)
				continue;
			OptionMessage call = cp.getB().getCall();

			temp1 = Math.abs(call.getPairedRatio() - cpRatio);
			if (temp == null || temp1 < diff) {
				temp = call;
				diff = temp1;
			}
		}
		if (temp != null) {
			s[0] = (temp.getOptionId());
			s[1] = (temp.getPairedOm().getOptionId());
		}
		return s;
	}
	public long[] findStrangle(float cpRatio, int strikeStep) {
		long[] s = new long[2];
		float diff = (float) 0.0;
		SortedMap<Float, Integer> strangles = new TreeMap<Float, Integer>();

		int strikeCount = strikePx2strikes.size();
		Collection<OptionStrike> vals = strikePx2strikes.values();
		OptionStrike[] values = new OptionStrike[strikeCount];
		strikePx2strikes.values().toArray(values);
		for (int i = 0; i < strikeCount - strikeStep; i++) {
			if (values[i].getPut() == null || values[i + strikeStep].getCall() == null)
				continue;
			if (values[i].getPut().getBid() > 0.0) {

				float put = values[i].getPut().getBid() + values[i].getPut().getAsk();
				float call = values[i + strikeStep].getCall().getBid() + values[i + strikeStep].getCall().getAsk();
				strangles.put(Math.abs(cpRatio - (put / (call + put))), i);
			} else {
				float put = values[i].getPut().getLast();
				float call = values[i + strikeStep].getCall().getLast();
				strangles.put(Math.abs(cpRatio - (put / (call + put))), i);
			}
		}
		if (strangles.size() > 0) {
			s[1] = values[strangles.get(strangles.firstKey())].getPut().getOptionId();
			s[0] = values[strangles.get(strangles.firstKey()) + strikeStep].getCall().getOptionId();
		}
		return s;
	}
	public long[] findPutRecords(float cpRatio) {
		// TODO Auto-generated method stub
		long[] s = new long[2];
		float diff = (float) 0.0;
		OptionMessage temp = null;
		for (Tuple2<Float, OptionStrike> cp : nearestStrikes) {
			float temp1 = (float) 0.0;
			if (cp.getB().getCall() == null || cp.getB().getPut() == null)
				continue;
			OptionMessage option = cp.getB().getPut();

			temp1 = Math.abs(option.getPairedRatio() - cpRatio);
			if (temp == null || temp1 < diff) {
				temp = option;
				diff = temp1;
			}
		}
		if (temp != null) {
			s[0] = (temp.getOptionId());
			s[1] = (temp.getPairedOm().getOptionId());
		}
		return s;

	}
}
