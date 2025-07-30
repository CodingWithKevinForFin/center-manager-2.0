package app.controlpanel;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.f1.bootstrap.ContainerBootstrap;
import com.f1.fixomsclient.OmsClientOrdersExecutions;
import com.f1.fixomsclient.OmsClientOrdersExecutionsListener;
import com.f1.fixomsclient.OmsClientOrdersExecutionsManager;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.povo.standard.MapMessage;
import com.f1.utils.CH;
import com.f1.utils.PropertyController;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.sjls.algos.eo.common.AlertMsg;
import com.sjls.algos.eo.common.AlgoParamsVWAP;
import com.sjls.algos.eo.common.AmendOrderRequestMsg;
import com.sjls.algos.eo.common.EOEngineCommand;
import com.sjls.algos.eo.common.EOException;
import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IBinStatistics;
import com.sjls.algos.eo.common.IBrokerInfoUpdateMsg;
import com.sjls.algos.eo.common.ICancelRejectedMsg;
import com.sjls.algos.eo.common.IEMSServices;
import com.sjls.algos.eo.common.IExecutionOptimizer;
import com.sjls.algos.eo.common.IExecutionReportMsg;
import com.sjls.algos.eo.common.INewOrderRequestMsg;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg.UpdateType;
import com.sjls.algos.eo.common.IQuoteData;
import com.sjls.algos.eo.common.IStrategyUpdateMsg;
import com.sjls.algos.eo.common.ITradeData;
import com.sjls.algos.eo.common.OrderModifyException;
import com.sjls.algos.eo.common.OrderRequestException;
import com.sjls.algos.eo.common.ParentOrderEventException;
import com.sjls.algos.eo.common.ParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.QueryKey;
import com.sjls.f1.sjlscommon.SjlsConverterHelper;

/*
 * -Dlog4j.configuratorClass=com.f1.speedloggerLog4j.Log4jSpeedLoggerManager
 * -Dlog4j.configuration=com/f1/speedloggerLog4j/Log4jSpeedLoggerManager.class
 */
public class F1Main implements IEMSServices, IExecutionOptimizer, OmsClientOrdersExecutionsListener {

	/**
	 * port of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_PORT = "oms.port";

	/**
	 * host of the oms exhaust where snapshots and deltas of orders / executions will be sent
	 */
	public static final String OMS_CONNECTION_HOST = "oms.host";

	public static void main(String a[]) throws InstantiationException, EOException, IOException {

		// read properties, etc...
		ContainerBootstrap bootstrap = new ContainerBootstrap(F1Main.class, a);
		bootstrap.setConfigDirProperty("./src/main/config");
		// bootstrap.setIsIdeModeProperty(false);
		// bootstrap.setLogLevel(Level.INFO, Level.INFO, "");
		bootstrap.startup();

		SjlsConverterHelper.registerConverters((ObjectToByteArrayConverter) bootstrap.getConverter());
		F1Main f1Main = new F1Main(bootstrap);
		ControlPanel controlPanel = new ControlPanel(f1Main);
		controlPanel.setSize(1000, 800);
		f1Main.setControlPanel(controlPanel);

		// startup (connect to oms server)
		f1Main.init();

	}

	private void init() {
		eventHandler.init();
	}

	private app.controlpanel.GuiEventHandler eventHandler;

	public F1Main(ContainerBootstrap bootstrap) throws IOException {
		final PropertyController props = bootstrap.getProperties();
		eventHandler = new GuiEventHandler(bootstrap, props.getOptional("oms.host", "localhost"), props.getOptional("oms.port", 4567));
		eventHandler.getManager().addGuiManagerListener(this);
	}

	private ControlPanel controlPanel;
	private boolean working;

	private void setControlPanel(ControlPanel controlPanel) {
		this.controlPanel = controlPanel;
		// eventHandler.getAlertsProcessor().setControlPanel(controlPanel);
	}

	@Override
	public String amendChildOrder(AmendOrderRequestMsg arg0) throws OrderModifyException {
		// System.out.println("amendChildorder: " + arg0);
		return null;
	}

	@Override
	public void cancelChildOrder(String arg0, String arg1) throws OrderModifyException {
		// System.out.println("cancel order: " + arg0 + "," + arg1);
	}

	@Override
	public List<IBinStatistics> getBinStatistics(QueryKey arg0) throws IOException {
		// System.out.println("getBinStatistics: " + arg0);
		return null;
	}

	@Override
	public void onControlPanelAlert(AlertMsg arg0) {
		// System.out.println("onControlPanelAlert: " + arg0);
	}

	@Override
	public void onParentOrderStatusUpdate(IParentOrderStatusUpdateMsg arg0) {
		// System.out.println("onParentStatusUpdate: " + arg0);
	}

	@Override
	public String sendNewChildOrder(INewOrderRequestMsg arg0) throws OrderRequestException {
		// System.out.println("onSendNewChildOrder: " + arg0);
		return null;
	}

	@Override
	public void storeBinStatistics(List<IBinStatistics> arg0) throws IOException {

	}

	@Override
	public boolean isWorking() {
		return working;
	}

	@Override
	public void startWork() throws EOException {
		this.working = true;
	}

	@Override
	public void stopWork() throws EOException {
		this.working = false;
	}

	@Override
	public void onCancelRejected(ICancelRejectedMsg arg0) {
		// System.out.println("onCancelRejected: " + arg0);
	}

	@Override
	public void onExecutionReport(IExecutionReportMsg arg0) {
		// System.out.println("onExecutionReport: " + arg0);
	}

	@Override
	public void onParentOrderEvent(IParentOrderEvent arg0) throws ParentOrderEventException {
		// System.out.println("onParentOrderEvent: " + arg0);
	}

	@Override
	public void onQuote(IQuoteData arg0) {
		// System.out.println("onQuote: " + arg0);
	}

	@Override
	public void onTrade(ITradeData arg0) {
		// System.out.println("onTrade: " + arg0);
	}

	@Override
	public void updateBrokerInfo(IBrokerInfoUpdateMsg arg0) throws EOException {
		// System.out.println("onUpdateBrokerInfo: " + arg0);
	}

	@Override
	public void updateOrderStrategy(IStrategyUpdateMsg arg0) throws EOException {
		MapMessage command = eventHandler.getContainer().nw(MapMessage.class);
		Map toSend = CH.m("TYPE", "COMMAND");
		toSend.put("msg", arg0);
		command.setMap(toSend);
		eventHandler.sendBroadCast(command);
	}

	public void pause(IStrategyUpdateMsg arg0, boolean pause) throws EOException {
		MapMessage command = eventHandler.getContainer().nw(MapMessage.class);
		Map toSend = CH.m("TYPE", pause ? "PAUSED" : "UNPAUSED");
		toSend.put("msg", arg0);
		command.setMap(toSend);
		eventHandler.sendBroadCast(command);
	}

	@Override
	public void onNewOrder(OmsClientOrdersExecutions manager, Order order, OmsAction action) {
		if (order.getSliceType() == SliceType.CLIENT_ORDER) {
			ParentOrderStatusUpdateMsg msg = new ParentOrderStatusUpdateMsg(UpdateType.Resumed, order.getId(), new AlgoParamsVWAP());
			msg.setTradingPlan(Collections.EMPTY_LIST);
			controlPanel.onParentOrderStatsUpdate(msg);
		}

	}

	@Override
	public void onUpdateOrder(OmsClientOrdersExecutions manager, Order old, Order nuw, OmsAction action) {
		// System.out.println("update: " + nuw);
		nuw.getUserData();

	}

	@Override
	public void onNewExecution(OmsClientOrdersExecutions manager, Execution order, OmsAction action) {

	}

	@Override
	public void onConnected(OmsClientOrdersExecutionsManager manager) {
		controlPanel.onAlert("Connected to: " + eventHandler.getHost() + ":" + eventHandler.getPort());
	}

	@Override
	public void onDisconnected(OmsClientOrdersExecutionsManager manager) {
		controlPanel.onAlert("Connection lost from: " + eventHandler.getHost() + ":" + eventHandler.getPort());
	}

	@Override
	public void onCommand(EOEngineCommand paramEOEngineCommand) throws EOException {

	}

	@Override
	public void onStarted(OmsClientOrdersExecutionsManager manager) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRcvBroadcast(OmsClientOrdersExecutionsManager manager, MapMessage clientBroadcast) {
		Map m = clientBroadcast.getMap();
		if (((String) m.get("TYPE")).equalsIgnoreCase("ALERT")) {
			AlertMsg msg = new AlertMsg((String) m.get("blockID"), (String) m.get("msg"));
			controlPanel.onAlert(msg.toString());
		}
	}

	@Override
	public void updateOrderStrategy(String arg0, IAlgoParams arg1) throws EOException {
		// TODO Auto-generated method stub

	}

}
