package com.f1.anvil.utils;

import com.f1.ami.center.table.AmiColumn;
import com.f1.base.Clearable;

public class AnvilTimedEvent implements Clearable {
	public static final byte ORDER_ARRIVAL = 1;
	public static final byte ORDER_DEPARTURE = 2;
	public static final byte EXECUTION_TIMER = 3;
	public static final byte EXECUTION = 4;
	public static final byte BUST = 5;

	public static final byte T1 = 1;
	public static final byte NONE = 0;
	public static final byte T5 = 5;
	long amiID;
	//	AmiColumn exPxCol;
	//	AmiColumn orderValCol;
	//	AmiColumn orderVolCol;
	long time;
	byte type;
	private AnvilMarketDataSymbol marketData;
	private byte exType;

	public AnvilTimedEvent() {
	}

	public AnvilTimedEvent reset(long amiID, long time, byte type, AnvilMarketDataSymbol marketDataForSymbol, byte t1) {
		this.amiID = amiID;
		this.time = time;
		this.type = type;
		this.marketData = marketDataForSymbol;
		this.exType = t1;
		return this;
		//		this.exPxCol = exPxCol;
		//		this.orderVolCol = orderVolCol;
		//		this.orderValCol = orderValCol;
	}
	public AnvilTimedEvent reset(long amiID, long time, byte type, AmiColumn exPxCol, AmiColumn orderVolCol, AmiColumn orderValCol) {
		this.amiID = amiID;
		this.time = time;
		this.type = type;
		return this;
		//		this.exPxCol = exPxCol;
		//		this.orderVolCol = orderVolCol;
		//		this.orderValCol = orderValCol;
	}
	public long getAmiID() {
		return amiID;
	}
	//	public AmiColumn getExPxCol() {
	////		return exPxCol;
	//	}
	//	public AmiColumn getOrderValCol() {
	////		return orderValCol;
	//	}
	//	public AmiColumn getOrderVolCol() {
	////		return orderVolCol;
	//	}
	public long getTime() {
		return time;
	}
	public byte getType() {
		return type;
	}

	@Override
	public void clear() {
		this.amiID = 0;
		this.time = 0;
		this.type = 0;
	}

	public AnvilMarketDataSymbol getMarketData() {
		return this.marketData;
	}

	public byte getTargetCol() {
		return this.exType;
	}
}
