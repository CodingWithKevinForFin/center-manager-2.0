package com.larkinpoint.salestool;

import java.util.List;

import com.f1.base.Day;
import com.f1.base.Message;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.utils.BasicDay;
import com.f1.utils.structs.BasicMultiMap;
import com.larkinpoint.messages.SpreadMessage;
import com.larkinpoint.messages.UnderlyingMessage;

public class LarkinScenarioContext {
	
	private List<UnderlyingMessage> underlyingsList;
	private List<SpreadMessage> backMonthPutsList1;
	private List<SpreadMessage> backMonthPutsList2;
	private LarkinParameters Params;

	final private String Symbol;
	private long requestTime;
	
	BasicMultiMap.List<Day, Message> tradeDateMasher;
	BasicMultiMap.List<Day, UnderlyingMessage> underlyingMasher;
	BasicMultiMap.List<Day, SpreadMessage> straddleMasher;
	BasicMultiMap.List<Day, SpreadMessage> puts1Masher;
	BasicMultiMap.List<Day, SpreadMessage> puts2Masher;

	private double strdCount;
	private double put1Count;
	private double put2Count;
	
	
	private double totalPL;
	private double totalReturn;
	private double indexPL;
	private double indexReturn;
	private double totalFees;
	
	BasicDay creationTime;
	
	
	
	public Day getCreationTime() {
		return creationTime;
	}
	public double getTotalReturn() {
		return totalReturn;
	}
	public void setTotalReturn(double totalReturn) {
		this.totalReturn = totalReturn;
	}
	public double getIndexPL() {
		return indexPL;
	}
	public void setIndexPL(double indexPL) {
		this.indexPL = indexPL;
	}
	public double getIndexReturn() {
		return indexReturn;
	}
	public void setIndexReturn(double indexReturn) {
		this.indexReturn = indexReturn;
	}
	
	
	
	public double getTotalPL() {
		return totalPL;
	}
	public LarkinScenarioContext(List<SpreadMessage> nearMonthStraddlesList, List<UnderlyingMessage> underlyingsList, List<SpreadMessage> backMonthPutsList1,
			List<SpreadMessage> backMonthPutsList2, String symbol, BasicDay now, LarkinParameters params) {
		super();
		this.nearMonthStraddlesList = nearMonthStraddlesList;
		this.underlyingsList = underlyingsList;
		this.backMonthPutsList1 = backMonthPutsList1;
		this.backMonthPutsList2 = backMonthPutsList2;
		Symbol = params.getSymbolName();
	   this.creationTime =now;
		
		//this.requestTime = requestTime;
		
		//this.strdCount = strdCount;
		//this.put1Count = put1Count;
		//this.put2Count = put2Count;
		this.Params = params;
	}
	private List<SpreadMessage> nearMonthStraddlesList;
	public List<SpreadMessage> getNearMonthStraddlesList() {
		return nearMonthStraddlesList;
	}
	public void setNearMonthStraddlesList(List<SpreadMessage> nearMonthStraddlesList) {
		this.nearMonthStraddlesList = nearMonthStraddlesList;
	}
	public List<UnderlyingMessage> getUnderlyingsList() {
		return underlyingsList;
	}
	public void setUnderlyingsList(List<UnderlyingMessage> underlyingsList) {
		this.underlyingsList = underlyingsList;
	}
	public List<SpreadMessage> getBackMonthPutsList1() {
		return backMonthPutsList1;
	}
	public void setBackMonthPutsList1(List<SpreadMessage> backMonthPutsList1) {
		this.backMonthPutsList1 = backMonthPutsList1;
	}
	public List<SpreadMessage> getBackMonthPutsList2() {
		return backMonthPutsList2;
	}
	public void setBackMonthPutsList2(List<SpreadMessage> backMonthPutsList2) {
		this.backMonthPutsList2 = backMonthPutsList2;
	}
	public LarkinParameters getParams() {
		return Params;
	}
	public void setParams(LarkinParameters params) {
		Params = params;
	}
	public long getRequestTime() {
		return requestTime;
	}
	public void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}
	public BasicMultiMap.List<Day, Message> getTradeDateMasher() {
		return tradeDateMasher;
	}
	public void setTradeDateMasher(BasicMultiMap.List<Day, Message> tradeDateMasher) {
		this.tradeDateMasher = tradeDateMasher;
	}
	public BasicMultiMap.List<Day, UnderlyingMessage> getUnderlyingMasher() {
		return underlyingMasher;
	}
	public void setUnderlyingMasher(BasicMultiMap.List<Day, UnderlyingMessage> underlyingMasher) {
		this.underlyingMasher = underlyingMasher;
	}
	public BasicMultiMap.List<Day, SpreadMessage> getStraddleMasher() {
		return straddleMasher;
	}
	public void setStraddleMasher(BasicMultiMap.List<Day, SpreadMessage> straddleMasher) {
		this.straddleMasher = straddleMasher;
	}
	public BasicMultiMap.List<Day, SpreadMessage> getPuts1Masher() {
		return puts1Masher;
	}
	public void setPuts1Masher(BasicMultiMap.List<Day, SpreadMessage> puts1Masher) {
		this.puts1Masher = puts1Masher;
	}
	public BasicMultiMap.List<Day, SpreadMessage> getPuts2Masher() {
		return puts2Masher;
	}
	public void setPuts2Masher(BasicMultiMap.List<Day, SpreadMessage> puts2Masher) {
		this.puts2Masher = puts2Masher;
	}
	public double getStrdCount() {
		return strdCount;
	}
	public void setStrdCount(double strdCount) {
		this.strdCount = strdCount;
	}
	public double getPut1Count() {
		return put1Count;
	}
	public void setPut1Count(double put1Count) {
		this.put1Count = put1Count;
	}
	public double getPut2Count() {
		return put2Count;
	}
	public void setPut2Count(double put2Count) {
		this.put2Count = put2Count;
	}
	public String getSymbol() {
		return Symbol;
	}
	public void setTotalPL(double totalPL2) {
		// TODO Auto-generated method stub
		this.totalPL=totalPL2;
	}


}
