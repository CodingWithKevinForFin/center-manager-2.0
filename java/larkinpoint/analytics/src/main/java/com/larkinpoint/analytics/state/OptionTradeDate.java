package com.larkinpoint.analytics.state;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import com.f1.base.Day;
import com.f1.base.IdeableGenerator;
import com.f1.utils.BasicDay;
import com.f1.utils.OH;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;

public class OptionTradeDate {

	private final BasicDay tradeDate;
	public SortedMap<BasicDay, OptionExpiry> expiryDay2expirys = new TreeMap<BasicDay, OptionExpiry>();

	public OptionTradeDate(BasicDay tradeDate) {
		this.tradeDate = tradeDate;
	}

	public BasicDay getTradeDate() {
		return tradeDate;
	}
	public void addRecord(OptionMessage ov) {
		OptionExpiry existing = getExpiryList((BasicDay) ov.getExpiry());
		if (existing == null)
			addExpiry((BasicDay) ov.getExpiry(), existing = new OptionExpiry((BasicDay) ov.getExpiry(), ov.getDaysToExpiry()));
		existing.addRecord(ov);
	}

	public void addExpiry(BasicDay expiry, OptionExpiry optionValues) {
		this.expiryDay2expirys.put(expiry, optionValues);
	}

	public OptionExpiry getExpiryList(BasicDay expiry) {
		return expiryDay2expirys.get(expiry);
	}

	public OptionExpiry getExpiryGreaterThan(BasicDay expiry) {
		return expiryDay2expirys.get(expiryDay2expirys.tailMap(expiry).firstKey());
	}

	public OptionExpiry getExpiryLessThan(BasicDay expiry) {
		return expiryDay2expirys.get(expiryDay2expirys.headMap(expiry).lastKey());
	}

	public Collection<OptionExpiry> getAllExpirys() {
		return expiryDay2expirys.values();
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {
		for (OptionExpiry expiry : getAllExpirys())
			expiry.buildOptionList(ideableGenerator, sink);

	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, Day qdate1, Day qdate2) {
		// TODO Auto-generated method stub
		if (OH.isBetween(tradeDate, qdate1, qdate2))
			for (OptionExpiry expiry : getAllExpirys())
				expiry.buildOptionList(ideableGenerator, sink);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, Day qdate1, Day qdate2, int top) {
		// TODO Auto-generated method stub
		if (OH.isBetween(tradeDate, qdate1, qdate2))
			for (OptionExpiry expiry : getAllExpirys())
				expiry.buildOptionList(ideableGenerator, sink, top);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, Day qdate1, Day qdate2, int top, int daysMax, int daysMin) {
		// TODO Auto-generated method stub
		if (OH.isBetween(tradeDate, qdate1, qdate2))
			for (OptionExpiry expiry : getAllExpirys())
				if (expiry.getDaysToExpiry() <= daysMax && expiry.getDaysToExpiry() >= daysMin)
					expiry.buildOptionList(ideableGenerator, sink, top);
	}

	public long[] findRecords(float cpRatio, int daysLB, int daysUB, int strikeStep) {
		// TODO Auto-generated method stub
		long diff = 0;
		long[] s = null;

		OptionExpiry selected = null;
		//Find the expiry with the longest time to expiry
		for (OptionExpiry expiry : getAllExpirys()) {
			long days = expiry.getDaysToExpiry();
			if (days < daysLB || days > daysUB)
				continue;
			if (selected == null || diff < days) {
				selected = expiry;
				diff = days;
			}
		}
		//find the spread now
		if (selected != null) {
			return selected.findRecords(cpRatio, strikeStep);
		}
		System.out.println("Can't find an expiry that matches input\n");
		return s;
	}

	public long[] findPutRecords(float cpRatio, int daysLB, int daysUB) {
		// TODO Auto-generated method stub
		long diff = 0;
		long[] s = null;

		OptionExpiry selected = null;
		//Find the expiry with the longest time to expiry
		for (OptionExpiry expiry : getAllExpirys()) {
			long days = expiry.getDaysToExpiry();
			if (days < daysLB || days > daysUB)
				continue;
			if (selected == null || diff < days) {
				selected = expiry;
				diff = days;
			}
		}
		//find the spread now
		if (selected != null) {
			return selected.findPutRecords(cpRatio);
		}
		return s;
	}
}
