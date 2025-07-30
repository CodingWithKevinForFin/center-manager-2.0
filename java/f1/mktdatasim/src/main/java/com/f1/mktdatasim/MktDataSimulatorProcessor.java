package com.f1.mktdatasim;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.f1.base.Message;
import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.mktdata.LevelOneData;
import com.f1.utils.MH;

public class MktDataSimulatorProcessor extends BasicProcessor<Message, MktDataSimState> {

	private final OutputPort<Message> loopback = newOutputPort(Message.class);
	public final OutputPort<Message> checkConfig = newOutputPort(Message.class);
	public final OutputPort<LevelOneData> output = newOutputPort(LevelOneData.class);
	private long messagesPerSecond;

	public MktDataSimulatorProcessor() {
		super(Message.class, MktDataSimState.class);
	}

	@Override
	public void processAction(Message action, MktDataSimState state, ThreadScope threadScope) throws Exception {
		if (!state.isRunning())
			return;
		checkConfig.send(nw(Message.class), threadScope);
		long next, now, start = state.getStartTime();
		for (;;) {
			now = getTools().getNow();
			next = start + state.getCount() * 1000L / messagesPerSecond;
			if (now < next)
				break;
			state.incCount();
			for (Map.Entry<Integer, MktDataSimNameSettings> e : state.getSubscribed().entrySet()) {

				MktDataSimNameSettings value = e.getValue();
				boolean afterOpen = value.getStart().getTodaysOccurenceMillis(now) < now;
				boolean beforeClose = value.getEnd().getTodaysOccurenceMillis(now) > now;
				boolean isOpen = afterOpen && beforeClose;
				if (isOpen != value.isOpen()) {
					if (isOpen) {
						value.setOpen(true);
						value.resetVolume();
						LevelOneData event = nw(LevelOneData.class);
						double last = calculateLast(value, now);
						event.setSecurityRefId(value.getName());
						event.setLastPrice(last);
						event.setLastStatus(LevelOneData.STATUS_OPENED);
						state.getOpenPrices().put(e.getKey(), last);
						output.send(event, threadScope);
					} else {
						LevelOneData event = nw(LevelOneData.class);
						event.setSecurityRefId(value.getName());
						value.setOpen(false);
						event.setLastStatus(LevelOneData.STATUS_HALTED);
						output.send(event, threadScope);
					}
				}
				if (isOpen) {
					double last = calculateLast(value, now);
					int size = getRandomSize(value);
					value.onTrade(size, last);
					LevelOneData event = nw(LevelOneData.class);
					event.setSecurityRefId(value.getName());

					event.setAskMarketMakerId(getRandomMarketMaker(value));
					event.setBidMarketMakerId(getRandomMarketMaker(value));
					event.setLastMarketMakerId(getRandomMarketMaker(value));

					event.setAskSize(getRandomSize(value));
					event.setBidSize(getRandomSize(value));
					event.setLastSize(size);

					event.setLastPrice(last);
					event.setAskPrice(last + getRandomSpread(value));
					event.setBidPrice(last - getRandomSpread(value));

					event.setLastTime(now);
					event.setAskTime(now);
					event.setBidTime(now);

					event.setCurrency(value.getCurrency());

					event.setVolume(value.getVolume());
					event.setHighPrice(value.getHighPrice());
					event.setLowPrice(value.getLowPrice());

					output.send(event, threadScope);
				}
			}
		}
		loopback.sendDelayed(action, threadScope, next - now, TimeUnit.MILLISECONDS);
	}
	private double calculateLast(MktDataSimNameSettings value, long now) {
		double m = Math.PI * 2 / value.getPeriod();
		double mid = (value.getHigh() + value.getLow()) / 2;
		double range = (value.getHigh() - value.getLow()) / 2;
		return MH.round(mid + range * Math.sin(now * m), MH.ROUND_HALF_EVEN, 2);
	}

	private double getRandomSpread(MktDataSimNameSettings value) {
		return MH.round(Math.abs(value.getRandom().nextGaussian()) * value.getSpread(), MH.ROUND_HALF_EVEN, 2);
	}

	private String getRandomMarketMaker(MktDataSimNameSettings value) {
		return value.getVenues()[value.getRandom().nextInt(value.getVenues().length)];
	}
	private int getRandomSize(MktDataSimNameSettings value) {
		return value.getSizes()[value.getRandom().nextInt(value.getSizes().length)];
	}

	public void init() {
		super.init();
		loopback.wire(getInputPort(), true);
	}

	public void start() {
		messagesPerSecond = getTools().getRequired("messages.per.sec", long.class);
		super.start();
	}

	public static void main(String a[]) {
		double min = 25, max = 30, period = 50;
		double m = Math.PI * 2 / period;
		double avg = (max + min) / 2;
		double range = (max - min) / 2;
		for (int i = 0; i < 1000; i++)
			System.out.println(i + ": " + (avg + range * Math.sin(i * m)));
	}
	public void checkConfig(MktDataSimState state) {
	}
}
