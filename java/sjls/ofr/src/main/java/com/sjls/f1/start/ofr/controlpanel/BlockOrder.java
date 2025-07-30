package com.sjls.f1.start.ofr.controlpanel;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.f1.pofo.oms.Order;
import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IAlgoParamsIS;
import com.sjls.algos.eo.common.IBinTradeData;
import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;
import com.sjls.algos.eo.common.ITCMEstimate;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.AlgoParamsMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.BinTradeData;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.BlockOrderMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.BrokerRouteMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPAlertMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPAlgoParams;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPStatus;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.CPStatusMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.LimitPriceMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.SJLSTime;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.SharesExecdMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.TCMEstimateMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.TradingCurveMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.TradingPlanMsg;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.UserData;
import com.sjls.controlpanel.protobuf.messages.ControlPanelMsgs.UserDataMsg;
import com.sjls.controlpanel.protobuf.utils.ProtobufUtils;

public class BlockOrder {
	public static String CVS_ID = "$Id: BlockOrder.java,v 1.7 2014/12/03 17:45:40 olu Exp $";
	public final static Logger m_logger = Logger.getLogger(BlockOrder.class);

	private final String m_blockID;

	private final BlockOrderMsg.Builder m_bldr = BlockOrderMsg.newBuilder();
	private volatile boolean m_blockOrderIsUpdated = false;
	//
	private final CPAlgoParams.Builder m_algoParamsBldr = CPAlgoParams.newBuilder();
	private volatile boolean m_algoParamsIsUpdated = false;
	//
	private final LimitPriceMsg.Builder m_limitPxBldr = LimitPriceMsg.newBuilder();
	private volatile boolean m_LimitPriceIsUpdated = false;

	private final TradingCurveMsg.Builder m_totalCurve = TradingCurveMsg.newBuilder(); //since start of trading day
	private volatile TradingCurveMsg.Builder m_deltaCurve; //since the last time we asked for a delta

	private final SharesExecdMsg.Builder m_sharesExecdBldr = SharesExecdMsg.newBuilder();
	private volatile boolean m_SharesExecdIsUpdated = false;

	private final CPStatusMsg.Builder m_cpStatusBldr = CPStatusMsg.newBuilder();
	private volatile boolean m_cpStatusIsUpdated = false;

	private final HashMap<String, Object> m_cumulativeUserData = new HashMap<String, Object>();
	private volatile HashMap<String, Object> m_userDataDelta;

	private BrokerRouteMsg m_brokerRoute;
	private volatile boolean m_BrokerRouteIsUpdated = false;

	private TradingPlanMsg m_tradingPlan; //trading plan is not "volatile" not change. So, no need to store the builder
	private volatile boolean m_tradingPlanIsUpdated = false;

	private TCMEstimateMsg m_tcmEstimate;
	private volatile boolean m_TCMEstimateIsUpdated = false;

	//private final ErrorHistoryMsg.Builder m_errorHistoryBldr = ErrorHistoryMsg.newBuilder();

	/**
	 * ctor
	 * 
	 * @param order
	 */
	public BlockOrder(final com.f1.pofo.oms.Order order) {
		m_bldr.setBlockId(order.getId());
		m_bldr.setSymbol(order.getSymbol());
		if (!PofoUtils.isEmpty(order.getSymbolSfx()))
			m_bldr.setSymbolSfx(order.getSymbolSfx());
		m_bldr.setSide(PofoUtils.toCPSide(order.getSide()));
		//
		m_blockID = m_bldr.getBlockId();
		m_limitPxBldr.setBlockId(m_blockID);
		m_totalCurve.setBlockId(m_blockID);
		m_sharesExecdBldr.setBlockId(m_blockID);
		m_cpStatusBldr.setBlockId(m_blockID);
		//m_errorHistoryBldr.setBlockId(m_blockID);
		//
		populateBldrsFrom(order, CPStatus.Pending);
	}

	public String getBlockId() {
		return m_blockID;
	}

	/**
	 * Populate the builders using the data in Orders
	 * 
	 * @param bldr
	 * @param limitPxBldr
	 * @param order
	 */
	private void populateBldrsFrom(final com.f1.pofo.oms.Order order, final CPStatus status) {
		m_bldr.setOrderType(PofoUtils.toCPOrderType(order.getOrderType()));
		m_bldr.setQty(order.getOrderQty());
		m_limitPxBldr.setLimitPrice(order.getLimitPx());
		update(status);
		updateSharesExecd(this, order);
		m_bldr.addAllCustomTags(PofoUtils.toCustomTags(order.getPassThruTags()));
		if (order.getSenderSubId() != null)
			m_bldr.setSenderSubId(order.getSenderSubId());
		m_blockOrderIsUpdated = true;
		m_LimitPriceIsUpdated = true;
	}

	/**
	 * Get list of flds that have been updated and reset the update flag Returns null if no updates!
	 * 
	 * @return
	 */
	public synchronized List<Message> getAndResetUpdates() {
		LinkedList<Message> list = null;
		if (m_blockOrderIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_bldr.build());
			m_blockOrderIsUpdated = false;
		}
		if (m_LimitPriceIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_limitPxBldr.build());
			m_LimitPriceIsUpdated = false;
		}
		if (m_deltaCurve != null) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_deltaCurve.build());
			m_deltaCurve = null;
		}
		if (m_tradingPlanIsUpdated && m_tradingPlan != null) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_tradingPlan);
			m_tradingPlanIsUpdated = false;
		}
		if (m_TCMEstimateIsUpdated && m_tcmEstimate != null) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_tcmEstimate);
			m_TCMEstimateIsUpdated = false;
		}
		if (m_userDataDelta != null) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(toUserDataMsg(m_userDataDelta));
			m_userDataDelta = null;
		}
		if (m_BrokerRouteIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_brokerRoute);
			m_BrokerRouteIsUpdated = false;
		}
		if (m_algoParamsIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(genAlgoParamsMsg());
			m_algoParamsIsUpdated = false;
		}
		if (m_SharesExecdIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			final SharesExecdMsg msg = m_sharesExecdBldr.build();
			list.add(msg);
			if (m_logger.isDebugEnabled())
				m_logger.debug("getAndResetUpdates(): Publishing SharesExecdMsg==>" + msg);
			m_SharesExecdIsUpdated = false;
		}
		if (m_cpStatusIsUpdated) {
			if (list == null)
				list = new LinkedList<Message>();
			list.add(m_cpStatusBldr.build());
			m_cpStatusIsUpdated = false;
		}
		return list;
	}

	/**
	 * Return the current state of the block order serialized into a bunch of msgs
	 * 
	 * @return
	 */
	public synchronized List<Message> snapshot() {
		final LinkedList<Message> list = new LinkedList<Message>();
		list.add(m_bldr.build());
		list.add(m_limitPxBldr.build());
		list.add(m_totalCurve.build());
		if (m_tradingPlan != null)
			list.add(m_tradingPlan);
		if (m_tcmEstimate != null)
			list.add(m_tcmEstimate);
		if (m_cumulativeUserData.size() > 0)
			list.add(toUserDataMsg(m_cumulativeUserData));
		if (m_brokerRoute != null)
			list.add(m_brokerRoute);
		list.add(genAlgoParamsMsg());
		list.add(m_sharesExecdBldr.build());
		list.add(m_cpStatusBldr.build());
		//list.add(m_errorHistoryBldr.build());
		return list;
	}

	/**
	 * Synchronized. Update the block order status
	 * 
	 * @param newStatus
	 */
	public synchronized void update(final CPStatus newStatus) {
		final CPStatus prevStatus = m_cpStatusBldr.hasStatus() ? m_cpStatusBldr.getStatus() : null;
		if (prevStatus == null || prevStatus != newStatus) {
			if (m_logger.isDebugEnabled()) {
				m_logger.debug(String.format("update(): BlockID=[%s]: From [%s] --> [%s]", m_blockID, prevStatus, newStatus));
			}
			m_cpStatusBldr.setStatus(newStatus);
			m_cpStatusIsUpdated = true;
		}
	}

	/**
	 * Synchronized. Update the block order its self with a new copy
	 * */
	public synchronized void update(final Order order) {
		final CPStatus prevStatus = m_cpStatusBldr.hasStatus() ? m_cpStatusBldr.getStatus() : CPStatus.Pending;
		populateBldrsFrom(order, prevStatus);
	}

	public synchronized void updateSharesExecd(final Order order) {
		if (m_logger.isDebugEnabled())
			m_logger.debug(String.format("updateSharesExecd(): BlockID=[%s]: Got getTotalExecQty=[%s]", m_blockID, order.getTotalExecQty()));
		updateSharesExecd(this, order);
	}

	private static void updateSharesExecd(final BlockOrder thisObj, final Order order) {
		final int cumQty = order.getTotalExecQty();
		thisObj.m_sharesExecdBldr.setCumQty(cumQty);
		thisObj.m_sharesExecdBldr.setPctFilled(order.getOrderQty() == 0 ? 0 : 100.0 * (cumQty / (double) order.getOrderQty()));
		thisObj.m_sharesExecdBldr.setAvgPx(cumQty == 0 ? 0 : order.getTotalExecValue() / cumQty);
		thisObj.m_SharesExecdIsUpdated = true;
	}

	public synchronized void update(final IParentOrderStatusUpdateMsg msg) {
		if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.TradingPlan) {
			doTradingPlanUpdate(msg.getTradingPlan());
			doAlgoParamsUpdate(msg.getAlgoParams());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.TCMEstimate) {
			doTCMEstimateUpdate(msg.getTCMEstimate());
			doAlgoParamsUpdate(msg.getAlgoParams());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.LimitPrice) {
			doOFRLimitPxUpdate(msg.getLimitPrice());
			doAlgoParamsUpdate(msg.getAlgoParams());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.BrokerRoute) {
			doBrokerRouteUpdate(msg.getRouteToBrokerID());
			doAlgoParamsUpdate(msg.getAlgoParams());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.BinTradeInfo) {
			doBinTradeInfoUpdate(msg.getBinTradeInfo());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.UserData) {
			doUserDataUpdate(msg.getUserdata());
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.Resumed) {
			update(CPStatus.Active);
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.Paused) {
			update(CPStatus.Idle);
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.Filled) {
			update(CPStatus.Filled);
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.PdgCancel) {
			update(CPStatus.PendingCancel);
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.Rejected) {
			update(CPStatus.Rejected);
		} else if (msg.getUpdateType() == IParentOrderStatusUpdateMsg.UpdateType.Activating) {
			//update(CPStatus.Activating);
		} else {
			m_logger.warn(String.format("update(): BlockID=[%s]: Got UpdateType [%s] from OFR but nothing to do...", m_blockID, msg.getUpdateType()));
		}
	}

	public synchronized void initToIdleIfNecessary() {
		final CPStatus cpStatus = getCPStatus();
		if (cpStatus == null || cpStatus == CPStatus.Pending) {
			update(CPStatus.Idle);
		}
	}

	public synchronized void storeErrorAlertMsg(final CPAlertMsg msg) {
		//final ErrorText errorText = ErrorText.newBuilder().setText(msg.getText()).setTimeStamp(msg.getTimeStamp()).build();
		//m_errorHistoryBldr.addErrorHistory(errorText);  
	}

	private void doBrokerRouteUpdate(final String brokerID) {
		m_brokerRoute = BrokerRouteMsg.newBuilder().setBlockId(m_blockID).setBrokerId(brokerID).build();
		m_BrokerRouteIsUpdated = true;
	}

	private void doOFRLimitPxUpdate(final double ofrPrice) {
		m_limitPxBldr.setCalculatedOfrPrice(ofrPrice);
		m_LimitPriceIsUpdated = true;
	}

	private void doUserDataUpdate(final Map<String, Object> userdata) {
		if (userdata == null)
			return; //Nothing to do
		m_cumulativeUserData.putAll(userdata);
		if (m_userDataDelta == null) {
			m_userDataDelta = new HashMap<String, Object>();
		}
		m_userDataDelta.putAll(userdata);
	}

	private void doBinTradeInfoUpdate(final IBinTradeData ibt) {
		final BinTradeData data = toProtoBufBinTradeData(ibt);
		m_totalCurve.addBinTrade(data);
		if (m_deltaCurve == null)
			m_deltaCurve = TradingCurveMsg.newBuilder();
		m_deltaCurve.setBlockId(getBlockId());
		m_deltaCurve.addBinTrade(data);

		//also BlockStatus needs to go to Active if not currently terminal
		if (!isTerminal())
			update(CPStatus.Active);
	}

	private void doTCMEstimateUpdate(final ITCMEstimate est) {
		m_tcmEstimate = TCMEstimateMsg.newBuilder().setBlockId(m_blockID).setTotalCost(est.getTotalCost()).setAlphaCost(est.getAlhpaCost())
				.setMktImpactCost(est.getMktImpactCost()).setSpreadCost(est.getSpreadCost()).setStddevCost(est.getStdDevOfTotalCost()).build();
		m_TCMEstimateIsUpdated = true;
	}

	private void doTradingPlanUpdate(final List<IBinTradeData> tradingPlan) {
		final TradingPlanMsg.Builder tpBldr = TradingPlanMsg.newBuilder();
		tpBldr.setBlockId(getBlockId());
		for (IBinTradeData trade : tradingPlan) {
			tpBldr.addBinTrade(toProtoBufBinTradeData(trade));
		}
		m_tradingPlan = tpBldr.build();
		m_tradingPlanIsUpdated = true;
	}

	private void doAlgoParamsUpdate(final IAlgoParams ap) {
		if (ap == null)
			return;
		if (ap.getStartTime() != null) {
			m_algoParamsBldr.setStartTime(toSJLSTime(ap.getStartTime()));
			m_algoParamsIsUpdated = true;
		}
		if (ap.getEndTime() != null) {
			m_algoParamsBldr.setEndTime(toSJLSTime(ap.getEndTime()));
			m_algoParamsIsUpdated = true;
		}
		if (ap.getParticipateOnOpen() != null) {
			m_algoParamsBldr.setParticipateOnOpen(ap.getParticipateOnOpen());
			m_algoParamsIsUpdated = true;
		}
		if (ap.getParticipateOnClose() != null) {
			m_algoParamsBldr.setParticipateOnClose(ap.getParticipateOnClose());
			m_algoParamsIsUpdated = true;
		}
		if (ap.getOnOpenAmt() != null) {
			m_algoParamsBldr.setOnOpenAmount(ProtobufUtils.toOnOpenAmount(ap.getOnOpenAmt()));
			m_algoParamsIsUpdated = true;
		}
		if (ap.getLowerPct() != null) {
			m_algoParamsBldr.setLowerPr(ap.getLowerPct());
			m_algoParamsIsUpdated = true;
		}
		if (ap.getUpperPct() != null) {
			m_algoParamsBldr.setUpperPr(ap.getUpperPct());
			m_algoParamsIsUpdated = true;
		}
		if (ap.getPaused() != null) {
			m_algoParamsBldr.setIsPaused(ap.getPaused());
			m_algoParamsIsUpdated = true;
		}
		if (ap.getHardUpperPRIndicator() != null) {
			m_algoParamsBldr.setIsHardUpperPr(ap.getHardUpperPRIndicator());
			m_algoParamsIsUpdated = true;
		}
		if (ap instanceof IAlgoParamsIS) {
			final IAlgoParamsIS apis = (IAlgoParamsIS) ap;
			m_algoParamsBldr.setRiskTolerance(apis.getRiskTolerance());
			if (apis.getMaxdeviationFromProfile() != null)
				m_algoParamsBldr.setMaxDeviation(apis.getMaxdeviationFromProfile());
			m_algoParamsIsUpdated = true;
		}
	}

	/**
	 * TODO: Should this be in ProtoUtils?
	 * 
	 * @param ibt
	 * @return
	 */
	private BinTradeData toProtoBufBinTradeData(final IBinTradeData ibt) {
		return BinTradeData.newBuilder().setShares(ibt.getShares()).setLengthInSecs(ibt.getBinLengthInSecs()).setStartTime(toSJLSTime(ibt.getStartTime())).build();
	}

	private static SJLSTime toSJLSTime(final DateTime dt) {
		return toSJLSTime(dt.toDate());
	}

	private static SJLSTime toSJLSTime(final Date dt) {
		return ProtobufUtils.toSJLSDateTime(dt).getTimeFld();
	}

	private UserDataMsg toUserDataMsg(final HashMap<String, Object> map) {
		final UserDataMsg.Builder bldr = UserDataMsg.newBuilder();
		bldr.setBlockId(getBlockId());
		for (Map.Entry<String, Object> e : map.entrySet()) {
			bldr.addUserData(UserData.newBuilder().setKey(e.getKey()).setValue(ByteString.copyFrom(e.getValue().toString().getBytes())).build()); //TODO: Need to fix this. cant pass Object to CP as userData value!!
		}
		return bldr.build();
	}

	/**
	 * generate an AlgoParamsmsg using the current CPAlgoParams
	 * 
	 * @return
	 */
	private AlgoParamsMsg genAlgoParamsMsg() {
		return AlgoParamsMsg.newBuilder().setBlockId(getBlockId()).setParams(m_algoParamsBldr.build()).build();
	}

	public boolean isTerminal() {
		final CPStatus status = getCPStatus();
		if (status == null)
			return false;
		return false;//return status == CPStatus.Rejected || status == CPStatus.Canceled || status == CPStatus.Filled || status == CPStatus.DoneForDay;
	}

	public CPStatus getCPStatus() {
		return m_cpStatusBldr.hasStatus() ? m_cpStatusBldr.getStatus() : null;
	}

	public void resetStatus() {
		if (!isTerminal()) {
			final int orderQty = m_bldr.getQty();
			final int cumQty = m_sharesExecdBldr.getCumQty();
			update(cumQty >= orderQty ? CPStatus.Filled : CPStatus.Idle);
		}
	}
}
