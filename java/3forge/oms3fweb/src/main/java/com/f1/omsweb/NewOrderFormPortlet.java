package com.f1.omsweb;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;
import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.BasicPortletSocket;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.CH;
import com.f1.utils.GuidHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.HasherMap;

public class NewOrderFormPortlet extends FormPortlet {

	private static final String STRATEGY_CODE_SLICER = "SLICER";
	private static final String STRATEGY_CODE_TEST = "TEST";

	private OrdersService service;
	private Order selectedOrder;

	private FormPortletTextField stock;
	private FormPortletTextField price;
	private FormPortletTextField qty;
	private FormPortletSelectField<Side> side;
	private FormPortletSelectField<TimeInForce> timeInForce;
	private FormPortletSelectField<String> strategy;
	private FormPortletSelectField<String> session;
	private Map<Integer, FormPortletField<String>> sParams;
	private FormPortletButton submitButton;
	private FormPortletButton resetButton;

	private Map<String, StrategyField> strategies;
	private Map<Integer, FormPortletField<String>> strategyParamFields;
	private FormPortletTextField account;
	private FormPortletTextField locateIdField;
	private FormPortletTextField locateBrokerField;
	private BasicPortletSocket fromParentOrdersSocket;

	public NewOrderFormPortlet(PortletConfig config, String sessionNames) {
		super(config);
		this.sParams = new HasherMap<Integer, FormPortletField<String>>();
		addField(this.stock = new FormPortletTextField("Symbol Name"));
		addField(this.price = new FormPortletTextField("Limit Px"));
		addField(this.qty = new FormPortletTextField("Quantity"));
		addField(this.account = new FormPortletTextField("account"));
		locateIdField = new FormPortletTextField("Locate Id");
		locateBrokerField = new FormPortletTextField("Locate Broker");
		addField(this.side = new FormPortletSelectField<Side>(Side.class, "Side"));
		side.addOption(Side.BUY, "Buy");
		side.addOption(Side.SELL, "Sell");
		side.addOption(Side.SHORT_SELL, "Sell Short");

		addField(this.timeInForce = new FormPortletSelectField<TimeInForce>(TimeInForce.class, "Time in Force"));
		timeInForce.addOption(TimeInForce.DAY, "Day");
		timeInForce.addOption(TimeInForce.ON_CLOSE, "On Close");
		timeInForce.addOption(TimeInForce.GTC, "GTC");

		String[] sessions = SH.split(",", sessionNames);
		addField(this.session = new FormPortletSelectField<String>(String.class, "Session"));
		for (String s : sessions) {
			this.session.addOption(s, s);
		}
		this.session.setValue(sessions[0]);
		this.stock.setWidth(50).setMaxChars(20);
		this.price.setWidth(60).setMaxChars(20);
		this.qty.setWidth(100).setMaxChars(20);
		addField(this.strategy = new FormPortletSelectField<String>(String.class, "Strategy").addOption("t", "Test").addOption("s", "Slicer"));
		addButton(this.submitButton = new FormPortletButton("Create Order"));
		addButton(this.resetButton = new FormPortletButton("Reset"));
		initializeStrategies();
		this.strategy.setValue("s");

		service = (OrdersService) getManager().getService(OrdersService.ID);
		this.fromParentOrdersSocket = addSocket(false, "modifyOrder", "modify selected order", true, null, CH.s(ModifyOrderInterPortletMessage.class));
	}

	private void initializeStrategies() {
		this.strategies = new HashMap<String, StrategyField>();
		this.strategies.put("t", new TestStrategyField());
		this.strategies.put("s", new SlicerStrategyField());

		this.strategyParamFields = new HashMap<Integer, FormPortletField<String>>();
		this.strategyParamFields.put(7791, new FormPortletTextField("% of Total Qty"));
		this.strategyParamFields.put(7792, new FormPortletTextField("No. of Slices"));
		this.strategyParamFields.put(7793, new FormPortletTextField("Start Time").setValue("+00:00:00"));
		this.strategyParamFields.put(7794, new FormPortletTextField("End Time").setValue("+00:00:30"));
		this.strategyParamFields.put(7795, new FormPortletTextField("Frequency").setValue(".0001"));
	}

	private void initializeStrategyParamters(String selectedStrategy) {
		if (selectedStrategy == null || selectedStrategy.length() == 0) {
			return;
		}

		StrategyField sf = this.strategies.get(selectedStrategy);
		if (sf == null) {
			return;
		}

		for (FormPortletField<String> field : this.sParams.values()) {
			removeField(field);
		}
		this.sParams.clear();

		int i = 1;
		for (int tag : sf.getTags()) {
			this.sParams.put(tag, addField(this.strategyParamFields.get(tag), getFieldLocation(strategy) + i++));
		}
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		if (localSocket == this.fromParentOrdersSocket) {
			ModifyOrderInterPortletMessage mMessage = (ModifyOrderInterPortletMessage) message;
			this.selectedOrder = mMessage.getOrder();

			this.stock.setValue(this.selectedOrder.getSymbol());
			this.price.setValue(SH.toString(this.selectedOrder.getLimitPx()));
			this.qty.setValue(SH.toString(this.selectedOrder.getOrderQty()));
			this.side.setValue(this.selectedOrder.getSide());

			Map<Integer, String> map = this.selectedOrder.getPassThruTags();
			if (map != null && map.size() > 0) {

				String strategy = map.get(7790);
				if (strategy != null) {
					if (strategy.equals(STRATEGY_CODE_TEST))
						this.strategy.setValue("t");
					else if (strategy.equals(STRATEGY_CODE_SLICER))
						this.strategy.setValue("s");
				}

				Set<Integer> keys = map.keySet();
				for (Integer key : keys) {
					if (key == 7790)
						continue;

					FormPortletField<String> field = sParams.get(key);
					if (field != null)
						field.setValue(map.get(key));
				}
			}
			this.submitButton.setName("Modify Order");
		}
	}

	public static class Builder extends AbstractPortletBuilder<NewOrderFormPortlet> {

		private static final String NEW_ORDER_FORM_ID = "newOrderForm";
		private static final String NEW_ORDER_TICKET_WINDOW = "New Order Ticket";
		private String sessionNames;

		public Builder(String sessionNames) {
			super(NewOrderFormPortlet.class);
			this.sessionNames = sessionNames;
		}

		@Override
		public NewOrderFormPortlet buildPortlet(PortletConfig portletConfig) {
			NewOrderFormPortlet portlet = new NewOrderFormPortlet(portletConfig, sessionNames);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return NEW_ORDER_TICKET_WINDOW;
		}

		@Override
		public String getPortletBuilderId() {
			return NEW_ORDER_FORM_ID;
		}

	}

	protected void onUserPressedButton(FormPortletButton button) {
		if (button == submitButton) {
			if (button.getName().equals("Create Order"))
				this.service.createOrder(createNewOrderMsg());
			else
				this.service.modifyOrder(modifyOrderMsg());
		} else if (button == resetButton) {
			reset();
			this.submitButton.setName("Create Order");
			this.strategy.setValue("t");
			this.side.setValue(Side.BUY);
			this.selectedOrder = null;
		}

		super.onUserPressedButton(button);
	}

	public FixOrderRequest createNewOrderMsg() {
		String guid = GuidHelper.getGuid();
		String symbol = this.stock.getValue();
		int quantity = OH.cast(this.qty.getValue(), Integer.class);
		double limitPx = OH.cast(this.price.getValue(), Double.class);
		String session = this.session.getValue();

		FixOrderRequest msg = createFixOrderRequest();
		msg.setType(MsgType.NEW_ORDER_SINGLE);
		msg.setSymbol(symbol);
		msg.setRequestId(guid);
		msg.setSessionName(session);

		FixOrderInfo info = msg.getOrderInfo();
		info.setOrderQty(quantity);

		if (limitPx > 0) {
			info.setOrderType(OrderType.LIMIT);
			info.setLimitPx(limitPx);
		} else
			info.setOrderType(OrderType.MARKET);

		info.setSide(side.getValue());

		info.setTimeInForce(timeInForce.getValue());

		String account = this.account.getValue();
		info.setAccount(account);
		setPassThruTags(info);
		msg.setOrderInfo(info);
		msg.setRootOrderId(guid);

		return msg;
	}

	private void setPassThruTags(FixOrderInfo info) {
		Map<Integer, String> ptTags = new HashMap<Integer, String>();

		for (Entry<Integer, FormPortletField<String>> e : this.sParams.entrySet()) {
			String value = e.getValue().getValue();
			if (value == null || value.length() == 0)
				continue;
			ptTags.put(e.getKey(), value);
		}

		ptTags.put(new Integer(7790), this.strategies.get(this.strategy.getValue()).getName());
		info.setPassThruTags(ptTags);
	}

	public FixOrderReplaceRequest modifyOrderMsg() {
		String guid = GuidHelper.getGuid();
		String symbol = this.stock.getValue();
		int quantity = OH.cast(this.qty.getValue(), Integer.class);
		String account = this.account.getValue();
		double limitPx = OH.cast(this.price.getValue(), Double.class);
		TimeInForce timeInForce = this.timeInForce.getValue();
		Side side = this.side.getValue();
		String session = this.session.getValue();

		FixOrderReplaceRequest msg = createFixOrderReplaceRequest();
		msg.setType(MsgType.REPLACE_REQUEST);
		msg.setRequestId(guid);
		msg.setSessionName(session);
		msg.setSymbol(symbol);

		FixOrderInfo info = msg.getOrderInfo();
		info.setOrderQty(quantity);

		if (limitPx > 0) {
			info.setOrderType(OrderType.LIMIT);
			info.setLimitPx(limitPx);
		} else
			info.setOrderType(OrderType.MARKET);

		info.setTimeInForce(timeInForce);

		info.setSide(side);
		setPassThruTags(info);
		msg.setOrderInfo(info);
		info.setAccount(account);
		msg.setRootOrderId(this.selectedOrder.getId());
		msg.setRefId(this.selectedOrder.getRequestId());

		return msg;
	}

	private FixOrderRequest createFixOrderRequest() {
		FixOrderRequest msg = getManager().getGenerator().nw(FixOrderRequest.class);
		FixOrderInfo info = getManager().getGenerator().nw(FixOrderInfo.class);
		msg.setOrderInfo(info);
		return msg;
	}

	private FixOrderReplaceRequest createFixOrderReplaceRequest() {
		FixOrderReplaceRequest msg = getManager().getGenerator().nw(FixOrderReplaceRequest.class);
		FixOrderInfo info = getManager().getGenerator().nw(FixOrderInfo.class);
		msg.setOrderInfo(info);
		return msg;
	}

	@Override
	public void onFieldChanged(FormPortletField<?> field) {
		super.onFieldChanged(field);
		if (field == side) {
			boolean isShortSell = side.getValue() == Side.SHORT_SELL;
			if (isShortSell) {
				if (!hasField(locateIdField)) {
					addFieldAfter(side, locateIdField);
					addFieldAfter(locateIdField, locateBrokerField);
				}
			} else {
				removeFieldNoThrow(locateIdField);
				removeFieldNoThrow(locateBrokerField);
			}
		} else if (field == strategy) {
			initializeStrategyParamters(strategy.getValue());
		}
	}

}
