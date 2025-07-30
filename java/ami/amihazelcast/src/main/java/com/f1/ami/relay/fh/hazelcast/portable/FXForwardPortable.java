package com.f1.ami.relay.fh.hazelcast.portable;

import java.io.IOException;

import com.f1.ami.relay.fh.hazelcast.AmiHazelcastPortableIDSetter;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;

public class FXForwardPortable implements Portable, AmiHazelcastPortableIDSetter {

	static int ID = 0;
	static int FACTORY_ID = 0;
	
	private String symbol;
	private String source;
	private Double bbg_rate;
	private Double bbg_factor;
	private Double bid;
	private Double ask;
	private Double last_trade;
	private Double fx_rate;
	private Long event_timestamp;
	private Integer state;
	
	public FXForwardPortable() {
		symbol = null;
		source = null;
		bbg_rate = null;
		bbg_factor = null;
		bid = null;
		ask = null;
		last_trade = null;
		fx_rate = null;
		event_timestamp = null;
		state = null;
	}
	
	public FXForwardPortable (String symbol, String source, Double bbg_rate, Double bbg_factor, Double bid, Double ask, Double last_trade, 
			Double fx_rate, Long event_timestamp, Integer state) {
		this.symbol = symbol;
		this.source = source;
		this.bbg_rate = bbg_rate;
		this.bbg_factor = bbg_factor;
		this.bid = bid;
		this.ask = ask;
		this.last_trade = last_trade;
		this.fx_rate = fx_rate;
		this.event_timestamp = event_timestamp;
		this.state = state;
	}
	
	@Override
	public void setPortableClassID(int id) {
		FXForwardPortable.ID = id;
	}

	@Override
	public void setPortableFactoryID(int id) {
		FXForwardPortable.FACTORY_ID = id;
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
		writer.writeString("symbol", this.symbol);
		writer.writeString("source", this.source);
		writer.writeDouble("bbg_rate", this.bbg_rate);
		writer.writeDouble("bbg_factor", this.bbg_factor);
		writer.writeDouble("bid", this.bid);
		writer.writeDouble("ask", this.ask);
		writer.writeDouble("last_trade", this.last_trade);
		writer.writeDouble("fx_rate", this.fx_rate);
		writer.writeLong("event_timestamp", this.event_timestamp);
		writer.writeInt("state", this.state);
	}

	@Override
	public void readPortable(PortableReader reader) throws IOException {
		this.symbol = reader.readString("symbol");
		this.source = reader.readString("source");
		this.bbg_rate = reader.readDouble("bbg_rate");
		this.bbg_factor = reader.readDouble("bbg_factor");
		this.bid = reader.readDouble("bid");
		this.ask = reader.readDouble("ask");
		this.last_trade = reader.readDouble("last_trade");
		this.fx_rate = reader.readDouble("fx_rate");
		this.event_timestamp = reader.readLong("event_timestamp");
		this.state = reader.readInt("state");
	}
}