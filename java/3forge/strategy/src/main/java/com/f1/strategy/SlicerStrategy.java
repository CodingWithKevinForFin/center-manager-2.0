package com.f1.strategy;

import java.util.Calendar;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import com.f1.container.ContainerTools;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.ChildNewOrderRequest;
import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.RepeatTimer;
import com.f1.utils.SH;
import com.f1.utils.Timer;
import com.f1.utils.TimerWrapper;

public class SlicerStrategy extends DefaultStrategy {

	public static final String ID = "SLICER";
	private static final int TAG_START_TIME = 7793;
	private static final int TAG_END_TIME = 7794;
	private static final int TAG_FREQUENCY = 7795;
	private Timer timer;
	private Order order;
	private long start;
	private long end;
	private long frequency;
	private int qtySent = 0;
	private int lotSize = 100;

	@Override
	public void init(OrderManager strategyState) {
		super.init(strategyState);
	}

	@Override
	public void onNewOrder(Order o) {
		this.order = o;
		Map<Integer, String> ptt = o.getPassThruTags();
		ContainerTools tools = getOrderManager().getTools();
		long now = tools.getNow();
		TimeZone tz = tools.getContainer().getServices().getClock().getTimeZone();
		this.start = parseTime(now, tz, CH.getOr(ptt, TAG_START_TIME, ""));
		this.end = parseTime(now, tz, CH.getOr(ptt, TAG_END_TIME, ""));
		this.frequency = Math.max(100L, (long) (60000 * Double.parseDouble(CH.getOr(ptt, TAG_FREQUENCY, "1"))));
		timer = new TimerWrapper(new RepeatTimer(frequency, TimeUnit.MILLISECONDS, new Random().nextInt((int) frequency)), start, end);
		getOrderManager().addTimer(timer);
	}
	public void onTimerDone(Timer timer) {

		if (timer == this.timer) {
			Order o = getOmsClientState().getOrder(order.getSourceSystem(), order.getId());
			int shares = o.getOrderQty() - qtySent;
			if (shares > 0) {
				ChildNewOrderRequest request = getOrderManager().newChildRequest();
				request.setOrderQty(shares);
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
				qtySent += shares;
			}
		} else
			super.onTimerDone(timer);
	}
	@Override
	public void onTimer(Timer timer, long scheduledTime, long now) {
		String id = getOmsClientState().getRootOrderId();
		Order o = getOmsClientState().getOrder(order.getSourceSystem(), order.getId());
		if (qtySent >= o.getOrderQty())
			return;
		if (OrdStatus.isCompleted(o.getOrderStatus())) {
			getOrderManager().cancelTimer(timer);
			return;
		}
		//Order o = CH.first(getOmsClientState().getOrders());
		if (timer == this.timer) {
			int remainingQty = order.getOrderQty() - qtySent;
			double percentTimePassed = end < start ? 1.0 : (double) (now - start - frequency) / (end - start);
			int qtyExpected = (int) (order.getOrderQty() * percentTimePassed);
			int shares = Math.min((int) MH.roundBy(qtyExpected - qtySent, lotSize, MH.ROUND_HALF_EVEN), remainingQty);
			if (shares > 0) {
				ChildNewOrderRequest request = getOrderManager().newChildRequest();
				request.setOrderQty(shares);
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
				qtySent += shares;
			}
		} else
			super.onTimer(timer, scheduledTime, now);
	}
	static public long parseTime(long now, TimeZone tz, String text) {
		if (SH.isnt(text))
			return now;
		boolean isOffset = false;
		if (text.startsWith("+")) {
			isOffset = true;
			text = text.substring(1);
		}
		String parts[] = SH.split(':', text);
		if (parts.length != 3)
			throw new RuntimeException("invalid format");
		int h = SH.parseInt(parts[0]);
		int m = SH.parseInt(parts[1]);
		int s = SH.parseInt(parts[2]);
		if (isOffset) {
			return now + TimeUnit.HOURS.toMillis(h) + TimeUnit.MINUTES.toMillis(m) + TimeUnit.SECONDS.toMillis(s);
		}
		Calendar cal = Calendar.getInstance();
		cal.setTimeZone(tz);
		cal.set(Calendar.HOUR_OF_DAY, h);
		cal.set(Calendar.MINUTE, m);
		cal.set(Calendar.SECOND, s);
		return cal.getTimeInMillis();
	}

}
