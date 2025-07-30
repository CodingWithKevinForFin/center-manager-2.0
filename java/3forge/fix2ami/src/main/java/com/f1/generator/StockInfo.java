package com.f1.generator;

import java.util.HashMap;
import java.util.Map;

public class StockInfo {
	final String symbol;
	final double previousClose;
	final double marketPrice;
	final double high;
	final double low;
	final double ask;
	final double bid;
	final long askSize;
	final long bidSize;

	final int priceSwingInPercent;
	final long avgVolume;
	final String exchange;
	final int maxQty;

	private final static Map<String, StockInfo> STOCK_INFO = new HashMap<>();

	public static Map<String, StockInfo> getStockInfo() {
		return STOCK_INFO;
	}

	public static StockInfo getStockInfo(final String symbol) {
		return STOCK_INFO.get(symbol);
	}

	public StockInfo(final String symbol, double previousClose, double marketPrice, double high, double low, double ask, double bid, long askSize, long bidSize,
			int priceSwingInPercent, long avgVolume, final String exchange, int maxQty) {
		this.symbol = symbol;
		this.previousClose = previousClose;
		this.marketPrice = marketPrice;
		this.high = high;
		this.low = low;
		this.ask = ask;
		this.bid = bid;
		this.askSize = askSize;
		this.bidSize = bidSize;
		this.priceSwingInPercent = priceSwingInPercent;
		if (avgVolume < 1) {
			this.avgVolume = 10000;
		} else {
			this.avgVolume = avgVolume;
		}

		this.exchange = exchange;
		this.maxQty = maxQty;
	}

	public String getExchange() {
		return exchange;
	}

	public String getSecurityExchangeMnemonic() {
		switch (exchange) {
			case "NYSE":
				return "N";
			case "NasdaqGS":
				return "O";
			case "American Stock Exchange":
				return "A";
			case "Montreal Exchange":
				return "M";
			case "Chicago Stock Exchange":
				return "MW";
			case "Pink Sheets":
				return "PNK";
			case "Toronto Stock Exchange":
				return "TO";
			case "New York Mercantile Exchange":
				return "12";
			case "NYFIX Millennium":
				return "13";
			case "NYSE BBSS":
				return "10";
			default:
				return null;
		}
	}

	public long getAvgVolume() {
		return avgVolume;
	}

	public int getPriceSwing() {
		return priceSwingInPercent;
	}

	public double getAsk() {
		return ask;
	}

	public double getBid() {
		return bid;
	}

	public long getAskSize() {
		return askSize;
	}

	public long getBidSize() {
		return bidSize;
	}

	public String getSymbol() {
		return symbol;
	}

	public double getPreviousClose() {
		return previousClose;
	}

	public double getMarketPrice() {
		return marketPrice;
	}

	public double getHigh() {
		return high;
	}

	public double getLow() {
		return low;
	}

	public int getMaxQty() {
		return maxQty;
	}
}
