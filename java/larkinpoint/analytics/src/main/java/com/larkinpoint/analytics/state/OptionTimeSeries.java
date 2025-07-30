package com.larkinpoint.analytics.state;

import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import com.f1.base.Day;
import com.f1.base.IdeableGenerator;
import com.f1.utils.BasicDay;
import com.f1.utils.OH;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;

public class OptionTimeSeries {

	private final long OptionID;
	private long PairedOptionID;
	public NavigableMap<BasicDay, OptionMessage> optionID2options = new TreeMap<BasicDay, OptionMessage>();
	

	public long getPairedOptionID() {
		return PairedOptionID;
	}
	public void setPairedOptionID(long id) {
		this.PairedOptionID = id;
	}
	public long getOptionID() {
		return OptionID;
	}

	
	
	public BasicDay getLastTradeDate(){
		BasicDay last = null;
		last =optionID2options.lastKey();
		return last;
		
	}

	public OptionTimeSeries(long id) {
		this.OptionID = id;
	}
	public void addRecord(OptionMessage ov) {
		optionID2options.put((BasicDay) ov.getTradeDate(), ov);

	}
	public Collection<OptionMessage> getAllRecords() {
		return optionID2options.values();
	}
	public NavigableMap<BasicDay, OptionMessage> getAllRecordsAfterTradeDate(BasicDay TradeDate) {
		return optionID2options.tailMap(TradeDate, true);
	}
	public NavigableMap<BasicDay, OptionMessage> getAllRecordsBeforeTradeDate(BasicDay TradeDate) {
		return optionID2options.headMap(TradeDate, true);
	}
	public NavigableMap<BasicDay, OptionMessage> getAllRecordsBetween(BasicDay TradeDate1, BasicDay TradeDate2) {
		return optionID2options.tailMap(TradeDate1, true).headMap(TradeDate2, true);
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {

		for (OptionMessage option : optionID2options.values()) {
			SpreadMessage spread = ideableGenerator.nw(SpreadMessage.class);
			spread.setLeg1(option);
			spread.setLeg2(option.getPairedOm());
			sink.add(spread);
		}
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, Day qdate1, Day qdate2) {
		// TODO Auto-generated method stub
		for (OptionMessage option : optionID2options.values())
			if (OH.isBetween(option.getTradeDate(), qdate1, qdate2)) {
				SpreadMessage spread = ideableGenerator.nw(SpreadMessage.class);
				spread.setLeg1(option);
				spread.setLeg2(option.getPairedOm());
				sink.add(spread);
			}

	}
	public OptionMessage findRecord(Day qdate) {
		// TODO Auto-generated method stub
		OptionMessage ov = null;
		for (OptionMessage ov1 : optionID2options.values())
			if (OH.eq(ov1.getTradeDate(), qdate))
				return ov1;

		return ov;
	}
}
