package com.larkinpoint.analytics.state;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import com.f1.base.IdeableGenerator;
import com.f1.utils.BasicDay;
import com.f1.utils.OH;
import com.larkinpoint.messages.OptionMessage;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;

public class OptionUnderlying {

	public NavigableMap<BasicDay, OptionTradeDate> tradedate2tradedates = new TreeMap<BasicDay, OptionTradeDate>();
	public HashMap<Long, OptionTimeSeries> id2timeseries = new HashMap<Long, OptionTimeSeries>(10000, (float) .75);
	public NavigableMap<BasicDay, UnderlyingMessage> tradedate2quotes = new TreeMap<BasicDay, UnderlyingMessage>();
	public boolean needsRefresh;
	
	public HashMap<Long, OptionTimeSeries> getId2timeseries() {
		return id2timeseries;
	}
	public void setId2timeseries(HashMap<Long, OptionTimeSeries> id2timeseries) {
		this.id2timeseries = id2timeseries;
	}

	private String symbol;

	public OptionUnderlying(String symbol) {
		this.symbol = symbol;
		this.needsRefresh = true;
	}
	public boolean isNeedsRefresh() {
		return needsRefresh;
	}
	public void setNeedsRefresh(boolean needsRefresh) {
		this.needsRefresh = needsRefresh;
	}
	public String getSymbol() {
		return symbol;
	}
	public void addRecord(OptionMessage ov) {
		OptionTradeDate existing = getTradeDateList((BasicDay) ov.getTradeDate());
		if (existing == null)
			addTradeDate((BasicDay) ov.getTradeDate(), existing = new OptionTradeDate((BasicDay) ov.getTradeDate()));
		existing.addRecord(ov);

		OptionTimeSeries series = getTimeSeries(ov.getOptionId());
		if (series == null)
			addTimeSeries(ov.getOptionId(), series = new OptionTimeSeries(ov.getOptionId()));
		series.addRecord(ov);
	}
	public void addRecord(UnderlyingMessage record) {
		this.tradedate2quotes.put((BasicDay) record.getQuoteDate(), record);
	}
	public void addTradeDate(BasicDay TradeDate, OptionTradeDate optionValues) {
		this.tradedate2tradedates.put(TradeDate, optionValues);
	}
	public void addTimeSeries(long id, OptionTimeSeries series) {
		this.id2timeseries.put(id, series);
	}
	public OptionTradeDate getTradeDateList(BasicDay tradeDate) {
		return tradedate2tradedates.get(tradeDate);
	}
	public OptionTimeSeries getTimeSeries(long id) {
		return id2timeseries.get(id);
	}

	public OptionTradeDate getFirstTradeDateAfter(BasicDay TradeDate) {
		return tradedate2tradedates.get(tradedate2tradedates.tailMap(TradeDate).firstKey());
	}

	public OptionTradeDate getLastTradeDateBefore(BasicDay TradeDate) {
		return tradedate2tradedates.get(tradedate2tradedates.headMap(TradeDate).lastKey());
	}
	public NavigableMap<BasicDay, OptionTradeDate> getAllTradeDatesAfter(BasicDay TradeDate) {
		return tradedate2tradedates.tailMap(TradeDate, true);
	}
	public NavigableMap<BasicDay, OptionTradeDate> getAllTradeDatesBefore(BasicDay TradeDate) {
		return tradedate2tradedates.headMap(TradeDate, true);
	}
	public NavigableMap<BasicDay, OptionTradeDate> getTradeDatesBetween(BasicDay TradeDate1, BasicDay TradeDate2) {

		return tradedate2tradedates.tailMap(TradeDate1, true).headMap(TradeDate2, true);
	}
	public NavigableMap<BasicDay, UnderlyingMessage> getUnderlyingDataBetween(BasicDay TradeDate1, BasicDay TradeDate2) {

		return tradedate2quotes.tailMap(TradeDate1, true).headMap(TradeDate2, true);
	}
	public Collection<OptionTradeDate> getAllTradeDates() {
		return tradedate2tradedates.values();
	}
	public Set<BasicDay> getAllTradeDateKeys() {
		return tradedate2tradedates.keySet();
	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink) {
		for (OptionTradeDate td : getAllTradeDates())
			td.buildOptionList(ideableGenerator, sink);

	}

	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, String symbol, BasicDay qdate1, BasicDay qdate2, int top) {
		// TODO Auto-generated method stub
		if (symbol.equals(getSymbol()))
			for (OptionTradeDate td : getAllTradeDates())
				td.buildOptionList(ideableGenerator, sink, qdate1, qdate2, top);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> list, String symbol, BasicDay qdate1, BasicDay qdate2, int top, int daysMax, int daysMin) {
		// TODO Auto-generated method stub
		if (symbol.equals(getSymbol()))
			for (OptionTradeDate td : getAllTradeDates())
				td.buildOptionList(ideableGenerator, list, qdate1, qdate2, top, daysMax, daysMin);
	}

	public void buildStraddleTradeList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, float cpRatio, BasicDay firstTradeDate, BasicDay lastTradeDate, int step, int daysLB,
			int daysUB, float ratioLB, float ratioUB, float tradeAmount) {

		//Step #1: find first TradeDate
		//find farthest expiry for that dates that satisfy the the bounds of daysLB and daysUB days to expiry
		//find best spread for that expiry that fits the cpRatio
		//find all records for those options that lie between firstTradeDate and lastTradeDate and where daystoExpiry fall between fewest and most days
		//recurse that list of records until the cpRatio breaks it's bounds.
		//if it doesn't then the lastTrade record is the last day with this series of options and end of loop
		///recurse through Step#1-N until all tradedates between the arguments have data....then return

		NavigableMap<BasicDay, OptionTradeDate> allDates = getTradeDatesBetween(firstTradeDate, lastTradeDate);
		if (allDates.isEmpty())
			return;
		BasicDay currentDate = allDates.firstKey();
		BasicDay periodEnd = allDates.lastKey();
		int count = allDates.size();
		Entry<BasicDay, OptionTradeDate> tdEntry;
		tdEntry = allDates.floorEntry(currentDate);
		do {
			//Grab the next valid set of tradedates....start off with the input values
			OptionTradeDate td = tdEntry.getValue();
			currentDate = tdEntry.getKey();
			
			long[] ids = td.findRecords(cpRatio, daysLB, daysUB, step);
			if (ids==null){
				System.out.println("can't find anything that matches for " +this.getSymbol()+ " "+ currentDate.toStringNoTimeZone() );
				continue;
			}
			OptionTimeSeries ts1 = getTimeSeries(ids[0]);
			OptionTimeSeries ts2 = getTimeSeries(ids[1]);

			NavigableMap<BasicDay, OptionMessage> tradeList1 = ts1.getAllRecordsBetween(currentDate, periodEnd);
			NavigableMap<BasicDay, OptionMessage> tradeList2 = ts2.getAllRecordsBetween(currentDate, periodEnd);

			int count1 = tradeList1.size();
			int count2 = tradeList2.size();
			if (count1 != count2) {
				System.out.println("Problem in TimeSeries for " +this.getSymbol()+ " ....Option1: " +ts1.getOptionID() +":"+ count1 +":"+ ts1.getLastTradeDate().toStringNoTimeZone()+ 
						" Option2: " + ts2.getOptionID()+":"+ count2+":"+  ts1.getLastTradeDate().toStringNoTimeZone()+ " records for for query " + currentDate.toStringNoTimeZone() +" and " +periodEnd.toStringNoTimeZone());
				//		break;
			}
			SpreadMessage lastSpreadRecord = null;
			//For every tradedate....evaluate the spread with regards to their TTL and cpRatio
			for (BasicDay entry : tradeList1.keySet()) {
				OptionMessage trade1 = tradeList1.get(entry);
				OptionMessage trade2 = tradeList2.get(entry);
				double endingValue = 0.0;
				double costBasis = 0.0;
				double startingValue = 0.0;

				if (trade1 == null || trade2 == null) {
					System.out.println("Missing data on date: " + entry + ":" + trade1.getOptionId());

				} else {
					double ratio;
					if (trade1.getBid() < 0.0 || trade1.getAsk() < 0.0 || trade2.getBid() < 0.0 || trade2.getAsk() < 0.0)
						ratio = trade1.getLast() / (trade1.getLast() + trade2.getLast());
					else
						ratio = (trade1.getBid() + trade1.getAsk()) / (trade1.getBid() + trade1.getAsk() + trade2.getBid() + trade2.getAsk());
					//		double ratio = (trade1.getBid() + trade1.getAsk()) / (trade1.getBid() + trade1.getAsk() + trade2.getBid() + trade2.getAsk());
					//if we hit a tradedate that violates the conditions then we're done with this spread...
					// we then reenter the outer loop to choose new options
					if (trade1.getDaysToExpiry() < daysLB || ratio < ratioLB || ratio > ratioUB || OH.eq(entry, tradeList1.lastKey())) {
						//We're officially done with this spread so wrap it up... since we've run past the last record for this spread...generate an extra trade to recalculate the cost basis and p&l
						if (lastSpreadRecord != null) {
							//unroll the last spread record and recalulate the summary #'s to effectively buyback the spread
							SpreadMessage spread2 = ideableGenerator.nw(SpreadMessage.class);
							spread2.setLeg1((OptionMessage) trade1.clone());
							spread2.setLeg2((OptionMessage) trade2.clone());
							spread2.getLeg1().setPairedRatio((float) ratio);
							spread2.getLeg2().setPairedRatio((float) ratio);
							spread2.setLegCounts(2);

							//	OptionMessage t1 = lastSpreadRecord.getLeg1();
							//OptionMessage t2 = lastSpreadRecord.getLeg2();

							endingValue = 0.0;
							costBasis = tradeAmount * -0.5 * (trade1.getAsk() + trade2.getAsk() + trade1.getBid() + trade2.getBid());
							startingValue = lastSpreadRecord.getEndingValue();
							if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0 || trade2.getAsk() < 0.0 || trade2.getBid() < 0.0)
								costBasis = tradeAmount * (trade1.getLast() + trade2.getLast()) * -1.0;
							spread2.setStartingValue(startingValue);
							spread2.setEndingValue(endingValue);
							spread2.setCashFlow(costBasis);
							spread2.setDailyPAndL(endingValue - startingValue + costBasis);

							spread2.setDailyReturn((startingValue == 0.0 ? 0.0 : -1.0 * (endingValue - startingValue + costBasis) / startingValue));
							spread2.setNumberOfSpreads(tradeAmount);
							sink.add(spread2);
							lastSpreadRecord = spread2;

						}
						break;
					}

					//Still a valid set of spreads so add them to the pile and increment the periodStart continue on.
					SpreadMessage spread = ideableGenerator.nw(SpreadMessage.class);
					spread.setLeg1((OptionMessage) trade1.clone());
					spread.setLeg2((OptionMessage) trade2.clone());
					spread.getLeg1().setPairedRatio((float) ratio);
					spread.getLeg2().setPairedRatio((float) ratio);
					spread.setLegCounts(2);

					if (lastSpreadRecord == null) {
						//This is the first record for this spread

						costBasis = tradeAmount * (trade1.getAsk() + trade1.getBid() + trade2.getBid() + trade2.getAsk()) * 0.5;
						startingValue = 0.0;
						if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0 || trade2.getAsk() < 0.0 || trade2.getBid() < 0.0)
							costBasis = tradeAmount * trade1.getLast() + trade2.getLast();
						endingValue = -1.0 * costBasis;

					} else {
						endingValue = tradeAmount * (trade1.getAsk() + trade1.getBid() + trade2.getBid() + trade2.getAsk()) * -0.5;
						costBasis = 0.0;
						startingValue = lastSpreadRecord.getEndingValue();

						if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0 || trade2.getAsk() < 0.0 || trade2.getBid() < 0.0)
							endingValue = tradeAmount * (trade1.getLast() + trade2.getLast()) * -1.0;

					}
					spread.setStartingValue(startingValue);
					spread.setEndingValue(endingValue);
					spread.setCashFlow(costBasis);
					spread.setDailyPAndL(endingValue - startingValue + costBasis);
					spread.setDailyReturn((endingValue - startingValue + costBasis) / startingValue);
					spread.setDailyReturn((startingValue == 0.0 ? 0.0 : -1.0 * (endingValue - startingValue + costBasis) / startingValue));
					spread.setNumberOfSpreads(tradeAmount);
					sink.add(spread);
					lastSpreadRecord = spread;
					currentDate = entry;
				}
			}
			//for whatever reason we're done processing these options...periodStart has been incremented to the last valid date processed.
			//and we're ready to continue
		} while ((tdEntry = allDates.higherEntry(currentDate)) != null);
	}
	public void buildPutTradeList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, float cpRatio, BasicDay firstTradeDate, BasicDay lastTradeDate, int daysLB,
			int daysUB, float ratioLB, float ratioUB, float tradeAmount) {

		//Step #1: find first TradeDate
		//find farthest expiry for that dates that satisfy the the bounds of daysLB and daysUB days to expiry
		//find best spread for that expiry that fits the cpRatio
		//find all records for those options that lie between firstTradeDate and lastTradeDate and where daystoExpiry fall between fewest and most days
		//recurse that list of records until the cpRatio breaks it's bounds.
		//if it doesn't then the lastTrade record is the last day with this series of options and end of loop
		///recurse through Step#1-N until all tradedates between the arguments have data....then return

		NavigableMap<BasicDay, OptionTradeDate> allDates = getTradeDatesBetween(firstTradeDate, lastTradeDate);
		if (allDates.isEmpty())
			return;
		BasicDay currentDate = allDates.firstKey();
		BasicDay periodEnd = allDates.lastKey();
		int count = allDates.size();
		Entry<BasicDay, OptionTradeDate> tdEntry;
		tdEntry = allDates.floorEntry(currentDate);
		SpreadMessage lastSpreadRecord = null;
		do {
			//Grab the next valid set of tradedates....start off with the input values
			OptionTradeDate td = tdEntry.getValue();
			currentDate = tdEntry.getKey();
			long[] ids = td.findPutRecords(cpRatio, daysLB, daysUB);
			if (ids != null) {

				OptionTimeSeries ts1 = getTimeSeries(ids[0]);
				OptionTimeSeries ts2 = getTimeSeries(ids[1]);

				if (ts1 == null || ts2 == null)
					return;

				NavigableMap<BasicDay, OptionMessage> tradeList1 = ts1.getAllRecordsBetween(currentDate, periodEnd);
				NavigableMap<BasicDay, OptionMessage> tradeList2 = ts2.getAllRecordsBetween(currentDate, periodEnd);

				int count1 = tradeList1.size();
				int count2 = tradeList2.size();
				if (count1 != count2) {
					System.out.println("Problem in TimeSeries for " +this.getSymbol()+ " ....Both options should have the same count: " + count1 + ":" + count2+ " for dates " + currentDate.toStringNoTimeZone() +" and " +periodEnd.toStringNoTimeZone());
					//		break;
				}
				lastSpreadRecord = null;
				//For every tradedate....evaluate the spread with regards to their TTL and cpRatio
				for (BasicDay entry : tradeList1.keySet()) {
					OptionMessage trade1 = tradeList1.get(entry);
					OptionMessage trade2 = tradeList2.get(entry);
					double endingValue = 0.0;
					double costBasis = 0.0;
					double startingValue = 0.0;

					if (trade1 == null || trade2 == null) {
						System.out.println("Missing data on date: " + entry + ":" + trade1.getOptionId());

					} else {
						double ratio;
						if (trade1.getBid() < 0.0 || trade1.getAsk() < 0.0 || trade2.getBid() < 0.0 || trade2.getAsk() < 0.0)
							ratio = 1.0 - (trade1.getLast() / (trade1.getLast() + trade2.getLast()));
						else
							ratio = 1.0 - (trade1.getBid() + trade1.getAsk()) / (trade1.getBid() + trade1.getAsk() + trade2.getBid() + trade2.getAsk());
						//if we hit a tradedate that violates the conditions then we're done with this spread...
						// we then reenter the outer loop to choose new options
						if (trade1.getDaysToExpiry() < daysLB || ratio < ratioLB || ratio > ratioUB || OH.eq(entry, tradeList1.lastKey())) {
							//We're officially done with this spread so wrap it up... since we've run past the last record for this spread...generate an extra trade to recalculate the cost basis and p&l
							if (lastSpreadRecord != null) {
								//unroll the last spread record and recalulate the summary #'s to effectively buyback the spread
								SpreadMessage spread2 = ideableGenerator.nw(SpreadMessage.class);
								spread2.setLeg1((OptionMessage) trade1.clone());
								spread2.setLegCounts(1);
								endingValue = 0.0;
								costBasis = tradeAmount * 0.5 * (trade1.getAsk() + trade1.getBid());
								startingValue = lastSpreadRecord.getEndingValue();
								if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0)
									costBasis = tradeAmount * trade1.getLast();
								spread2.setStartingValue(startingValue);
								spread2.setEndingValue(endingValue);
								spread2.setCashFlow(costBasis);
								spread2.setDailyPAndL((endingValue - startingValue + costBasis));

								spread2.setDailyReturn((startingValue == 0.0 ? 0.0 : (endingValue - startingValue + costBasis) / startingValue));
								spread2.setNumberOfSpreads(tradeAmount);
								sink.add(spread2);
								lastSpreadRecord = spread2;

							}
							break;
						}

						//Still a valid set of spreads so add them to the pile and increment the periodStart continue on.
						SpreadMessage spread = ideableGenerator.nw(SpreadMessage.class);
						spread.setLeg1((OptionMessage) trade1.clone());
						spread.setLegCounts(1);

						if (lastSpreadRecord == null) {
							//This is the first record for this spread
							startingValue = 0.0;
							costBasis = tradeAmount * (trade1.getAsk() + trade1.getBid()) * -0.5;
							if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0)
								costBasis = -1.0 * tradeAmount * trade1.getLast();
							endingValue = -1.0 * costBasis;

						} else {
							startingValue = lastSpreadRecord.getEndingValue();
							costBasis = 0.0;
							endingValue = tradeAmount * (trade1.getAsk() + trade1.getBid()) * 0.5;
							if (trade1.getAsk() < 0.0 || trade1.getBid() < 0.0)
								endingValue = tradeAmount * trade1.getLast();

						}
						spread.setStartingValue(startingValue);
						spread.setEndingValue(endingValue);
						spread.setCashFlow(costBasis);
						spread.setDailyPAndL((endingValue - startingValue + costBasis));

						spread.setDailyReturn((startingValue == 0.0 ? 0.0 : (endingValue - startingValue + costBasis) / startingValue));
						spread.setNumberOfSpreads(tradeAmount);
						sink.add(spread);
						lastSpreadRecord = spread;
						currentDate = entry;
					}
				}
			}
			//for whatever reason we're done processing these options...periodStart has been incremented to the last valid date processed.
			//and we're ready to continue
		} while ((tdEntry = allDates.higherEntry(currentDate)) != null);
	}
	public void buildOptionList(IdeableGenerator ideableGenerator, List<SpreadMessage> sink, String symbol, long leg1, long leg2, BasicDay day1, BasicDay day2) {
		// TODO Auto-generated method stub

		OptionTimeSeries ots = id2timeseries.get(leg1);
		OptionTimeSeries ots1 = id2timeseries.get(leg2);
		if (ots != null && ots1 != null) {
			List<SpreadMessage> sm = new ArrayList<SpreadMessage>();
			ots.buildOptionList(ideableGenerator, sm, day1, day2);
			for (SpreadMessage temp : sm) {
				OptionMessage ov = ots1.findRecord(temp.getLeg1().getTradeDate());
				if (ov != null) {
					temp.setLeg2(ov);
					sink.add(temp);
				}
			}

		}

	}
	public boolean haveUnderRecords() {
		if (this.tradedate2quotes.isEmpty())
			return false;
		return true;
	}

	public void buildUnderlyingList(IdeableGenerator ideableGenerator, List<UnderlyingMessage> list, String symbol2, BasicDay qdate1, BasicDay qdate2) {
		// TODO Auto-generated method stub
		NavigableMap<BasicDay, UnderlyingMessage> allDates;
		if (qdate1 == null || qdate2 == null)
			allDates = tradedate2quotes;
		else
			allDates = getUnderlyingDataBetween(qdate1, qdate2);
		if (allDates.isEmpty())
			return;

		UnderlyingMessage lastMessage = null;
		double startingValue;
		double cashFlow;
		double endingValue;

		for (BasicDay entry : allDates.keySet()) {
			UnderlyingMessage message = (UnderlyingMessage) tradedate2quotes.get(entry).clone();

			if (lastMessage == null) {
				startingValue = 0.0;
				cashFlow = -1.0 * message.getClose();
				endingValue = -1.0 * cashFlow;

			} else if (OH.eq(entry, allDates.lastKey())) {
				startingValue = lastMessage.getEndingValue();
				cashFlow = message.getClose();
				endingValue = 0.0;
			} else {
				startingValue = lastMessage.getEndingValue();
				cashFlow = 0.0;
				endingValue = message.getClose();
			}
			message.setStartingValue(startingValue);
			message.setEndingValue(endingValue);
			message.setCashFlow(cashFlow);
			message.setDailyPAndL(endingValue - startingValue + cashFlow);
			message.setDailyReturn((startingValue == 0.0 ? 0.0 : (endingValue - startingValue + cashFlow) / startingValue));
			list.add(message);
			lastMessage = message;

		}
	}
	public UnderlyingMessage getUnderlyingData(BasicDay tradedate) {
		return tradedate2quotes.get(tradedate);
	}

	public void checkForOptionPairs(){
		//For every trade Date
		
		//for every expiry
		
		//for every strike
		
		//check options
		
		//build json string 
		
	}
}