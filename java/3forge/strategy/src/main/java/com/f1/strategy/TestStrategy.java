package com.f1.strategy;

import java.util.Map;

import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.Order;

public class TestStrategy extends DefaultStrategy {

	public static final String ID = "TEST";

	@Override
	public void onNewOrder(Order o) {
		try {
			Map<Integer, String> tags = o.getPassThruTags();
			int times = 1;
			if (tags.containsKey(7792)) {
				times = Integer.parseInt(tags.get(7792));
				if (times == 0)
					times = 1;
			}

			if (tags.containsKey(7791)) {
				int ratio = Integer.parseInt(tags.get(7791));
				for (int i = 0; i < times; i++) {
					ChildNewOrderRequest request = getOrderManager().newChildRequest();
					request.setOrderQty((int) Math.round(o.getOrderQty() * ratio * 1.0 / 100));
					request.setOrderType(o.getOrderType());
					request.setLimitPx(o.getLimitPx());
					request.setDestination(o.getDestination());
					request.setSessionName("TEST");
					request.setSide(o.getSide());
					request.setTimeInForce(o.getTimeInForce());
					try {
						String requestId = getOrderManager().createChildOrder(request, o.getId());
					} catch (RequestException e) {
						throw new StrategyException("Cannot create child order");
					}
				}
			}
		} catch (RuntimeException e) {
			throw new StrategyException(e);
		}

	}
}
