package com.f1.omsweb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.base.Action;
import com.f1.base.Message;
import com.f1.container.ResultMessage;
import com.f1.fixomsclient.OmsClientNotification;
import com.f1.pofo.fix.FixCancelRequest;
import com.f1.pofo.fix.FixOrderReplaceRequest;
import com.f1.pofo.fix.FixOrderRequest;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;
import com.f1.suite.web.HttpRequestAction;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.PortletService;
import com.f1.suite.web.table.WebCellFormatter;
import com.f1.suite.web.table.impl.BasicWebCellFormatter;
import com.f1.suite.web.table.impl.MapWebCellFormatter;
import com.f1.suite.web.table.impl.NumberWebCellFormatter;
import com.f1.suite.web.table.impl.PassthruWebCellFormatter;
import com.f1.suite.web.table.impl.PercentWebCellFormatter;
import com.f1.suite.web.table.impl.ToggleButtonCellFormatter;
import com.f1.utils.BundledTextFormatter;
import com.f1.utils.CH;
import com.f1.utils.Formatter;
import com.f1.utils.GuidHelper;
import com.f1.utils.LocaleFormatter;
import com.f1.utils.structs.Tuple2;

public class OrdersService implements PortletService {

	static final private Set<Class<? extends Action>> INTERESTED = (Set) CH.s(OmsClientNotification.class);
	public static final String ID = "orders";

	final private WebCellFormatter sideFormatter;
	final private WebCellFormatter priceWebCellFormatter;
	final private WebCellFormatter timeWebCellFormatter;
	final private WebCellFormatter percentFormatter;
	final private WebCellFormatter quantityFormatter;
	final private WebCellFormatter symbolWebCellFormatter;
	final private BasicWebCellFormatter orderTypeFormatter;
	final private WebCellFormatter timeInForceFormatter;
	final private WebCellFormatter showButtonWebCellFormatter;
	final private WebCellFormatter passThruWebCellFormatter;
	final private WebCellFormatter orderStatusWebCellFormatter;

	private boolean listening = false;
	final private PortletManager manager;
	final private WebOmsManager omsManager = new WebOmsManager();
	final private List<OmsPortlet> omsPortlets = new ArrayList<OmsPortlet>();

	public OrdersService(PortletManager manager, Map<String, String> sectorMap) {
		this.manager = manager;
		this.omsManager.setSectorMap(sectorMap);
		final LocaleFormatter localeFormatter = manager.getLocaleFormatter();
		BundledTextFormatter textFormatter = manager.getTextFormatter();
		Formatter timeFormatter = localeFormatter.getDateFormatter(LocaleFormatter.TIME);
		Formatter n = localeFormatter.getNumberFormatter(0);
		Formatter priceFormatter = localeFormatter.getPriceFormatter(3);
		Formatter pn = localeFormatter.getPercentFormatter(2);

		final MapWebCellFormatter sideFormatter = new MapWebCellFormatter(textFormatter);
		sideFormatter.addEntry(Side.BUY, "Buy", "style.color=blue");
		sideFormatter.addEntry(Side.SELL, "Sell", "style.color=red");
		sideFormatter.addEntry(Side.SHORT_SELL, "Short Sell", "style.color=red");
		sideFormatter.addEntry(Side.SHORT_SELL_EXEMPT, "Exempt Short Sell", "style.color=red");
		sideFormatter.setDefaultWidth(60).lockFormatter();
		this.sideFormatter = sideFormatter;

		percentFormatter = new PercentWebCellFormatter(pn).lockFormatter();
		quantityFormatter = new NumberWebCellFormatter(n).lockFormatter();

		priceWebCellFormatter = new NumberWebCellFormatter(priceFormatter).lockFormatter();
		symbolWebCellFormatter = new BasicWebCellFormatter().setCssClass("bold").setDefaultWidth(60).lockFormatter();
		timeWebCellFormatter = new NumberWebCellFormatter(timeFormatter).setDefaultWidth(60).lockFormatter();

		final MapWebCellFormatter orderTypeFormatter = new MapWebCellFormatter(textFormatter);
		orderTypeFormatter.addEntry(OrderType.LIMIT, "Lmt");
		orderTypeFormatter.setNullValue("");
		orderTypeFormatter.addEntry(OrderType.MARKET, "Mkt").setDefaultWidth(35).lock();
		this.orderTypeFormatter = orderTypeFormatter;

		this.timeInForceFormatter = new MapWebCellFormatter(textFormatter).addEntry(TimeInForce.ON_OPEN, "Open").addEntry(TimeInForce.ON_CLOSE, "Close").setDefaultWidth(60)
				.setNullValue("").lockFormatter();
		this.showButtonWebCellFormatter = new ToggleButtonCellFormatter("image_show_dn", "image_show_up", "shown", "hidden").setDefaultWidth(30).lockFormatter();
		this.passThruWebCellFormatter = new PassthruWebCellFormatter().lockFormatter();
		this.orderStatusWebCellFormatter = new OrderStatusWebCellFormatter(textFormatter);
	}
	@Override
	public String getServiceId() {
		return ID;
	}

	public void cancelChildOrder(WebOmsOrder childOrder) {
		String parentOrderID = childOrder.getParent().getOrder().getId();
		OmsClientAction action = this.manager.getGenerator().nw(OmsClientAction.class);
		action.setOrderAction(OmsAction.CANCEL_CHILD_ORDER);
		action.setOrderID(childOrder.getOrder().getId());
		action.setRootOrderID(parentOrderID);
		this.manager.sendRequestToBackend(Oms3fWebMain.OMS_REQUEST, action);
	}

	public void cancelParentOrder(String orderId) {
		FixCancelRequest req = this.manager.getGenerator().nw(FixCancelRequest.class);
		req.setRequestId(GuidHelper.getGuid());
		req.setType(MsgType.CANCEL_REQUEST);
		req.setRootOrderId(orderId);
		this.manager.sendMessageToBackend(Oms3fWebMain.OMS_FIXCLIENT, req);
	}
	public void bustExecution(OmsClientAction bustMessage) {
		this.manager.sendRequestToBackend(Oms3fWebMain.OMS_REQUEST, bustMessage);

	}

	public void modifyOrder(FixOrderReplaceRequest msg) {
		this.manager.sendMessageToBackend(Oms3fWebMain.OMS_FIXCLIENT, msg);
	}

	public void createOrder(FixOrderRequest msg) {
		this.manager.sendMessageToBackend(Oms3fWebMain.OMS_FIXCLIENT, msg);
	}

	private void requestSnapshot() {
		manager.sendRequestToBackend(Oms3fWebMain.OMS, manager.getGenerator().nw(Message.class));
		listening = true;
	}

	public void onOmsNotification(OmsClientNotification notification) {
		if (!listening)
			return;

		if (CH.isntEmpty(notification.getAddedOrders()))
			for (Order o : notification.getAddedOrders()) {
				fireOnOrder(omsManager.onOrder(o));
			}

		if (notification.getUpdatedOrders() != null && CH.isntEmpty(notification.getUpdatedOrders()))
			for (Tuple2<Order, Order> tuple : notification.getUpdatedOrders()) {
				fireOnOrder(omsManager.onOrder(tuple.getB()));
			}
		if (CH.isntEmpty(notification.getAddedExecutions()))
			for (Execution e : notification.getAddedExecutions()) {
				fireOnExecution(omsManager.onExecution(e));
			}

	}

	@Override
	public void onBackendAction(Action action) {
		onOmsNotification((OmsClientNotification) action);
	}

	@Override
	public void onBackendResponse(ResultMessage<Action> action) {
		onOmsNotification((OmsClientNotification) action.getAction());
	}

	@Override
	public Set<Class<? extends Action>> getInterestedBackendMessages() {
		return INTERESTED;
	}
	public WebCellFormatter getSideFormatter() {
		return sideFormatter;
	}

	public WebCellFormatter getPriceWebCellFormatter() {
		return priceWebCellFormatter;
	}

	public WebCellFormatter getTimeWebCellFormatter() {
		return timeWebCellFormatter;
	}

	public WebCellFormatter getPercentFormatter() {
		return percentFormatter;
	}

	public WebCellFormatter getQuantityFormatter() {
		return quantityFormatter;
	}

	public WebCellFormatter getSymbolWebCellFormatter() {
		return symbolWebCellFormatter;
	}

	public WebCellFormatter getTextWebCellFormatter(int width) {
		return new BasicWebCellFormatter().setDefaultWidth(width);
	}

	public WebCellFormatter getOrderTypeWebCellFormatter() {
		return orderTypeFormatter;
	}

	public WebCellFormatter getTimeInForceFormatter() {
		return timeInForceFormatter;
	}

	public WebCellFormatter getShowButtonWebCellFormatter() {
		return showButtonWebCellFormatter;
	}

	public WebCellFormatter getPassThruWebCellFormatter() {
		return passThruWebCellFormatter;
	}

	public WebCellFormatter getOrderStatusWebCellFormatter() {
		return orderStatusWebCellFormatter;
	}

	public void addOmsPortlet(OmsPortlet portlet) {
		omsPortlets.add(portlet);
		if (listening) {
			for (WebOmsOrder order : omsManager.getParentOrders().values())
				portlet.onOrder(order);
			for (WebOmsOrder order : omsManager.getChildOrders().values())
				portlet.onOrder(order);
			for (WebOmsExecution execution : omsManager.getExecutions().values())
				portlet.onExecution(execution);
		} else {
			requestSnapshot();
		}
	}

	public void removeOmsPortlet(OmsPortlet portlet) {
		omsPortlets.remove(portlet);
	}
	private void fireOnOrder(WebOmsOrder order) {
		for (OmsPortlet p : omsPortlets)
			p.onOrder(order);
	}

	private void fireOnExecution(WebOmsExecution execution) {
		for (OmsPortlet p : omsPortlets)
			p.onExecution(execution);
	}
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}
	@Override
	public void handleCallback(Map<String, String> attributes, HttpRequestAction action) {
		// TODO Auto-generated method stub

	}

}
