package com.f1.generator.handler;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class ExchangeEventGenerator extends AbstractEventGenerator implements Callable<Void> {
	public OutputPort<OrderEvent> fillPort = newOutputPort(OrderEvent.class);
	private final int fillProbability;

	public ExchangeEventGenerator(final PropertyController props) {
		super(props);
		fillProbability = props.getOptional(AbstractHandler.ATTR_GENERATOR_EXCHANGE_FILL_PROBABILITY, AbstractHandler.DEFAULT_FILL_PROBABILITY);
	}

	@Override
	public Void call() throws Exception {
		final StockInfo stockInfo = pickAStock(random);
		if (null == stockInfo || null == stockInfo.getSymbol()) {
			LH.warning(log, "internal error - stock info: ", stockInfo, " or symbol is null");
		} else {
			if (random.nextInt(100) <= fillProbability) {
				final String partitionId = createPartitionId(stockInfo.getSymbol());
				this.getInputPort().dispatch(createEvent(MsgType.EXECUTION_REPORT, partitionId, stockInfo.getSymbol()));
			}
		}

		scheduler.schedule(this, random.nextInt(maxRandomSleepTimeInMillis), TimeUnit.MILLISECONDS);
		return null;
	}

	public void processAction(OrderEvent event, OrderCache state, ThreadScope threadScope) throws Exception {
		switch (event.getMsgType()) {
			case EXECUTION_REPORT:
				fillPort.send(event, threadScope);
				break;
			default:
				LH.warning(log, "Unsupport MsgType: ", event.getMsgType());
		}
	}

}
