package com.f1.generator.handler;

import java.util.logging.Logger;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.generator.Order;
import com.f1.generator.OrderCache;
import com.f1.generator.OrderEvent;
import com.f1.pofo.fix.OrdStatus;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

import quickfix.ConfigError;

public class CancelRejectHandler extends BasicProcessor<OrderEvent, OrderCache> {
	private static Logger log = Logger.getLogger(CancelRejectHandler.class.getName());

	public CancelRejectHandler(final PropertyController props) throws ConfigError {
		super(OrderEvent.class, OrderCache.class);
	}

	@Override
	public void processAction(OrderEvent event, OrderCache orderCache, ThreadScope threadScope) throws Exception {
		LH.info(log, "received a CANCEL REJECT message ->   ", event);
		final String clOrdID = event.getClOrdID();
		Order order = orderCache.getOrder(clOrdID);

		if (null == order) {
			return;
		}

		order.setLastOrdStatus(OrdStatus.REJECTED);
	}

}
