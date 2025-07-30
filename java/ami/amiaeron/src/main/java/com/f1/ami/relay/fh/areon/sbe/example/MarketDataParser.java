package com.f1.ami.relay.fh.areon.sbe.example;

import java.math.BigDecimal;
import java.util.Random;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.ami.relay.fh.aeron.AmiAeronMessageParser;
import com.f1.utils.PropertyController;

import io.aeron.logbuffer.Header;

public class MarketDataParser implements AmiAeronMessageParser {

	@Override
	public AmiRelayMapToBytesConverter parseMessage(DirectBuffer buffer, int offset, int length, Header header) {

		MessageHeaderDecoder headerDecoder = new MessageHeaderDecoder();
		TradeDataDecoder dataDecoder = new TradeDataDecoder();

		//        int bufferOffset = 0;
		headerDecoder.wrap(buffer, offset);

		// Lookup the applicable flyweight to decode this type of message based on templateId and version.
		final int templateId = headerDecoder.templateId();
		if (templateId != TradeDataDecoder.TEMPLATE_ID) {
			throw new IllegalStateException("Template ids do not match");
		}

		final int actingBlockLength = headerDecoder.blockLength();
		final int actingVersion = headerDecoder.version();

		offset += headerDecoder.encodedLength();
		dataDecoder.wrap(buffer, offset, actingBlockLength, actingVersion);

		AmiRelayMapToBytesConverter converter = new AmiRelayMapToBytesConverter();
		double price = BigDecimal.valueOf(dataDecoder.quote().price().mantissa()).scaleByPowerOfTen(dataDecoder.quote().price().exponent()).doubleValue();

		converter.append("amount", dataDecoder.amount());
		converter.append("symbol", dataDecoder.quote().symbol());
		converter.append("market", dataDecoder.quote().market().name());
		converter.append("currency", dataDecoder.quote().currency().name());
		converter.append("price", price);

		return converter;
	}

	@Override
	public void init(PropertyController props) {
		// to use any properties if needed
	}

	private int amount;
	private double price;
	private Market market;
	private Currency currency;
	private String symbol;
	private int encodedLength;
	private Random rand;

	public MarketDataParser(int _amount, double _price, Market _market, Currency _currency, String _symbol) {
		this.rand = new Random();
		amount = _amount;
		price = _price;
		market = _market;
		currency = _currency;
		symbol = _symbol;
		encodedLength = 0;
	}

	public MarketDataParser() {
		this.rand = new Random();
		this.setRandomValues();
	}

	public double getPrice() {
		return this.price;
	}

	public int getAmount() {
		return this.amount;
	}

	public Market getMarket() {
		return this.market;
	}

	public Currency getCurrency() {
		return this.currency;
	}

	public String getSymbol() {
		return this.symbol;
	}

	public int getEncodedLength() {
		return this.encodedLength;
	}

	public void setRandomValues() {
		this.price = rand.nextDouble() * 1000.0;
		this.amount = rand.nextInt(100);
		this.symbol = "AAPL";
		this.market = Market.NYSE;
		this.currency = Currency.USD;
		this.encodedLength = 0;
	}

	public void encodeAndSend(UnsafeBuffer buffer) {
		MessageHeaderEncoder headerEncoder = new MessageHeaderEncoder();
		TradeDataEncoder dataEncoder = new TradeDataEncoder();

		BigDecimal priceDecimal = BigDecimal.valueOf(this.getPrice());
		int priceMantissa = priceDecimal.scaleByPowerOfTen(priceDecimal.scale()).intValue();
		int priceExponent = priceDecimal.scale() * -1;

		TradeDataEncoder encoder = dataEncoder.wrapAndApplyHeader(buffer, 0, headerEncoder);
		encoder.amount(this.getAmount());
		System.out.println("Amount is " + this.getAmount());
		encoder.quote().market(this.getMarket()).currency(this.getCurrency()).symbol(this.getSymbol()).price().mantissa(priceMantissa).exponent((byte) priceExponent);
		this.encodedLength = encoder.encodedLength() + MessageHeaderEncoder.ENCODED_LENGTH;
	}
}