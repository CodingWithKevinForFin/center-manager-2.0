package com.f1.generator.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.generator.OrderGeneratorMain;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.PropertyController;

public abstract class AbstractEventGenerator extends BasicProcessor<OrderEvent, OrderCache> implements Callable<Void> {
	private final int partitionIdLength;
	final int maxRandomSleepTimeInMillis;

	final static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
	final Random random = new Random();

	public AbstractEventGenerator(final PropertyController props) {
		super(OrderEvent.class, OrderCache.class);

		partitionIdLength = props.getOptional(AbstractHandler.ATTR_GENERATOR_PARTITION_ID_LENGTH, -1);
		maxRandomSleepTimeInMillis = props.getOptional(AbstractHandler.ATTR_GENERATOR_MAX_SLEEP_RANDOM, AbstractHandler.DEFAULT_MAX_SLEEP);
	}

	String createPartitionId(final String symbol) {
		String partitionId = symbol;
		if (partitionIdLength != -1 && partitionId.length() >= partitionIdLength) {
			partitionId = partitionId.substring(0, partitionIdLength);
		}
		return partitionId;
	}

	static OrderEvent createEvent(final MsgType msgType, final String partitionId, final String symbol) {
		OrderEvent orderEvent = OrderGeneratorMain.getContainer().nw(OrderEvent.class);
		orderEvent.setClOrdID(null);
		orderEvent.setMsgType(msgType);
		orderEvent.setPartitionId(partitionId);
		orderEvent.setFIXMessage(null);
		orderEvent.setSymbol(symbol);
		return orderEvent;
	}

	static StockInfo pickAStock(final Random random) {
		Map<String, StockInfo> stockInfoMap = StockInfo.getStockInfo();
		List<String> symbols = new ArrayList<>(stockInfoMap.keySet());

		return stockInfoMap.get(symbols.get(random.nextInt(symbols.size())));
	}

	public void startScheduleEvent() {
		scheduler.schedule(this, random.nextInt(maxRandomSleepTimeInMillis), TimeUnit.MILLISECONDS);
	}

	abstract public void processAction(OrderEvent event, OrderCache state, ThreadScope threadScope) throws Exception;
}
