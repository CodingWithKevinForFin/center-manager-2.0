package com.f1.generator.handler;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.container.OutputPort;
import com.f1.container.ThreadScope;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.generator.StockInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class ClientEventGenerator extends AbstractEventGenerator {
	private static final Logger log = Logger.getLogger(ClientEventGenerator.class.getName());

	public OutputPort<OrderEvent> newOrderPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> cancelRequestPort = newOutputPort(OrderEvent.class);
	public OutputPort<OrderEvent> replaceRequestPort = newOutputPort(OrderEvent.class);

	private final int cancelProbility;
	private final int modProbility;

	public ClientEventGenerator(final PropertyController props) {
		super(props);

		cancelProbility = props.getOptional(AbstractHandler.ATTR_GENERATOR_CLIENT_CANCEL_PROBABILITY, AbstractHandler.DEFAULT_CLIENT_CANCEL_PROBABILITY);
		modProbility = props.getOptional(AbstractHandler.ATTR_GENERATOR_CLIENT_MOD_PROBABILITY, AbstractHandler.DEFAULT_CLIENT_MOD_PROBABILITY);
	}

	@Override
	public Void call() throws Exception {
		final StockInfo stockInfo = pickAStock(random);
		final String partitionId = createPartitionId(stockInfo.getSymbol());
		this.getInputPort().dispatch(createEvent(MsgType.NEW_ORDER_SINGLE, partitionId, stockInfo.getSymbol()));

		if (random.nextInt(100) <= cancelProbility) {
			this.getInputPort().dispatch(createEvent(MsgType.CANCEL_REQUEST, partitionId, stockInfo.getSymbol()));
		}

		if (random.nextInt(100) <= modProbility) {
			this.getInputPort().dispatch(createEvent(MsgType.REPLACE_REQUEST, partitionId, stockInfo.getSymbol()));
		}

		scheduler.schedule(this, random.nextInt(maxRandomSleepTimeInMillis), TimeUnit.MILLISECONDS);
		return null;
	}

	public void processAction(OrderEvent event, OrderCache state, ThreadScope threadScope) throws Exception {
		switch (event.getMsgType()) {
			case NEW_ORDER_SINGLE:
				newOrderPort.send(event, threadScope);
				break;
			case CANCEL_REQUEST:
				cancelRequestPort.send(event, threadScope);
				break;
			case REPLACE_REQUEST:
				replaceRequestPort.send(event, threadScope);
				break;
			default:
				LH.warning(log, "Unsupport MsgType: ", event.getMsgType());
		}
	}
}
