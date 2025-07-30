package com.f1.tcartsim.preparer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class TcartSimManager {

	public static final String DEFAULT_DATE = "20160503 EST5EDT";
	//	private static final Logger log = LH.get();
	//	public static final String SRC_MAIN_CONFIG = "./src/main/config";
	//	private static boolean lockTimeOffset;
	//	static {
	//		setSimluatedDate("20160503 EST5EDT");
	//	}

	final private long timeOffset;
	final private String simulatedDateAndTz;
	final private Random random;
	final private Random guidRandom;

	private String defaultCurrency;
	private Map<String, Double> fxRates;
	private List<String> currenciesList;

	public TcartSimManager() {
		this(new Random(123), new Random(123), "USD", (Map) Collections.emptyMap(), DEFAULT_DATE);
	}
	public TcartSimManager(Random random, Random guidRandom, String defaultCurrency, Map<String, Double> rates, String simulatedDateAndTz) {
		this.defaultCurrency = defaultCurrency;
		this.random = random;
		this.fxRates = rates;
		this.guidRandom = guidRandom;
		this.simulatedDateAndTz = simulatedDateAndTz;
		this.currenciesList = CH.l(rates.keySet());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
		sdf.setTimeZone(TimeZone.getTimeZone(SH.afterFirst(simulatedDateAndTz, ' ')));
		try {
			timeOffset = sdf.parse(SH.beforeFirst(simulatedDateAndTz, ' ') + " 00:00:00.000").getTime();
		} catch (ParseException e) {
			throw OH.toRuntime(e);
		}
	}

	public Random getRandom() {
		return this.random;
	}

	public long parseTimeFromLine(CharSequence line) {
		int hours = SH.parseInt(line, 0, 2, 10);
		int minutes = SH.parseInt(line, 2, 4, 10);
		int seconds = SH.parseInt(line, 4, 6, 10);
		int milliseconds = SH.parseInt(line, 6, 9, 10);
		return this.timeOffset + hours * 3600000 + minutes * 60000 + seconds * 1000 + milliseconds;
	}
	public int nextInt(int i) {
		return random.nextInt(i);
	}
	public double nextDouble() {
		return random.nextDouble();
	}
	public boolean nextBoolean() {
		return random.nextBoolean();
	}
	public <T> T getRandom(List<T> list) {
		return CH.getRandom(list, this.random);
	}
	public long nextInt() {
		return this.random.nextInt();
	}
	StringBuilder tmp = new StringBuilder();

	public CharSequence getGuid(int i) {
		tmp.setLength(0);
		while (tmp.length() < i)
			SH.toString(MH.abs(this.guidRandom.nextInt()), 62, tmp);
		return tmp.substring(0, i);
	}

	public String getDefaultCurrency() {
		return this.defaultCurrency;
	}
	public String getCurrency() {
		if (currenciesList.isEmpty() || random.nextDouble() > .02)
			return defaultCurrency;
		return CH.getRandom(currenciesList, random);
	}
	public double getPrice(String currency, double pxInDflt) {
		if (currency.equals(this.defaultCurrency))
			return pxInDflt;
		return pxInDflt * CH.getOrThrow(this.fxRates, currency).doubleValue();
	}
}
