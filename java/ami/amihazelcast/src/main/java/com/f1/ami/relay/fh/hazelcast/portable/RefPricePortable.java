package com.f1.ami.relay.fh.hazelcast.portable;

import java.io.IOException;

import com.f1.ami.relay.fh.hazelcast.AmiHazelcastPortableIDSetter;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class RefPricePortable implements Portable, AmiHazelcastPortableIDSetter {
	
	static int ID = 0;
	static int FACTORY_ID = 0;
	
	private String vert_investment_code;
	private String pricing_source;
	private String symbol;
	private Long event_timestamp;
	private Long last_trade_time;
	private Double bid;
	private Double ask;
	private Double last_trade;
	private Double price;
	private String ccy;
	private String eid;
	private Integer state;
	
	public RefPricePortable () {
		vert_investment_code = null;
		pricing_source = null;
		symbol = null;
		event_timestamp = null;
		last_trade_time = null;
		bid = null;
		ask = null;
		last_trade = null;
		price = null;
		ccy = null;
		eid = null;
		state = null;
	}
	
	public RefPricePortable (String vert_investment_code, String pricing_source, String symbol, Long event_timestamp, Long last_trade_time,
			Double bid, Double ask, Double last_trade, Double price, String ccy, String eid, Integer state) {
		this.vert_investment_code = vert_investment_code;
		this.pricing_source = pricing_source;
		this.symbol = symbol;
		this.event_timestamp = event_timestamp;
		this.last_trade_time = last_trade_time;
		this.bid = bid;
		this.ask = ask;
		this.last_trade = last_trade;
		this.price = price;
		this.ccy = ccy;
		this.eid = eid;
		this.state = state;
	}
		
	@Override
	public int getFactoryId() {
		return FACTORY_ID;
	}

	@Override
	public int getClassId() {
		return ID;
	}

	@Override
	public void writePortable(PortableWriter writer) throws IOException {
		writer.writeString("vert_investment_code", this.vert_investment_code);
		writer.writeString("pricing_source", this.pricing_source);
		writer.writeString("symbol", this.symbol);
		writer.writeLong("event_timestamp", this.event_timestamp);
		writer.writeLong("last_trade_time", this.last_trade_time);
		writer.writeDouble("bid", this.bid);
		writer.writeDouble("ask", this.ask);
		writer.writeDouble("last_trade", this.last_trade);
		writer.writeDouble("price", this.price);
		writer.writeString("ccy", this.ccy);
		writer.writeString("eid", this.eid);
		writer.writeInt("state", this.state);
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		vert_investment_code = reader.readString("vert_investment_code");
		pricing_source = reader.readString("pricing_source");
		symbol = reader.readString("symbol");
		event_timestamp = reader.readLong("event_timestamp");
		last_trade_time = reader.readLong("last_trade_time");
		bid = reader.readDouble("bid");
		ask = reader.readDouble("ask");
		last_trade = reader.readDouble("last_trade");
		price = reader.readDouble("price");
		ccy = reader.readString("ccy");
		eid = reader.readString("eid");
		state = reader.readInt("state");
	}

	@Override
	public void setPortableClassID(int id) {
		RefPricePortable.ID = id;
	}

	@Override
	public void setPortableFactoryID(int id) {
		RefPricePortable.FACTORY_ID = id;
	}
}