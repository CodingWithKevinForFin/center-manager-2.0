package app.fix42;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sjls.algos.eo.common.AmendOrderRequestMsg;
import com.sjls.algos.eo.common.CancelRejectedMsg;
import com.sjls.algos.eo.common.ExecType;
import com.sjls.algos.eo.common.ExecutionReportMsg;
import com.sjls.algos.eo.common.OrderStatus;
import com.sjls.algos.eo.common.OrderType;
import com.sjls.algos.eo.common.TimeInForce;
import com.sjls.algos.eo.orderpools.IOrderPool.Pool;
import com.sjls.algos.eo.utils.DateUtils;
import com.sjls.algos.eo.utils.DirectoryIterator;

/**
 * Simulate the execution of a child order
 * 
 * @author Olu Emuleomo
 * @date 04/26/2010
 * 
 */
public class OrderExecutor implements Runnable {
	private static Logger m_logger = Logger.getLogger(OrderExecutor.class);

	final IFIX42Client m_client;
	private static DOPVenuesFile m_venuesFile;

	private ScheduledFuture<?> m_scheduledFuture;
	private final ScheduledThreadPoolExecutor m_executor;

	boolean m_isOrderAccepted = false;
	final ExecutionReportMsg m_execRpt;

	private FIXEngine m_fixEngine;
	private AmendOrderRequestMsg m_amendReq;

	private final Random m_random = new Random();

	private final boolean m_isDarkOrder;

	public OrderExecutor(final IFIX42Client client, final ScheduledThreadPoolExecutor executor, final NewOrderSingleMsg msg) {
		m_client = client;
		m_execRpt = genExecRpt(msg);
		m_executor = executor;
		m_isDarkOrder = msg.getRequestingPool().equals(Pool.DOP.name());
		m_logger.info(String.format("BlockID=%s, clOrdID=%s:  Got OrderQty of [%d]", msg.getBlockID(), msg.getClOrdID(), msg.getOrderQty()));
	}

	private ExecutionReportMsg genExecRpt(final NewOrderSingleMsg msg) {
		final ExecutionReportMsg execRpt = new ExecutionReportMsg();
		execRpt.setBlockID(msg.getBlockID());
		execRpt.setBroker(msg.getBroker());
		execRpt.setVenue(msg.getVenue());
		execRpt.setClOrdID(msg.getClOrdID());
		// execRpt.setOrigClOrdID(msg.getOrigClOrdID()); Not always needed
		execRpt.setSide(msg.getSide());
		execRpt.setOrdType(msg.getAlgoPrice().isLimitOrder() ? OrderType.Limit : OrderType.Market);
		execRpt.setTicker(msg.getTicker());
		execRpt.setTimeInForce(msg.getTimeInForce());
		execRpt.setOrderQty(msg.getOrderQty());
		execRpt.setPrice(msg.getAlgoPrice().getLimitPrice());
		execRpt.setCumQty(0);
		execRpt.setAvgPrice(0);
		execRpt.setOrderStatus(OrderStatus.PdgNew);
		execRpt.setExecType(ExecType.PdgNew);

		return execRpt;
	}

	/**
	 * Enable the OrderExecutor to cancel itself
	 * 
	 * @param f
	 */
	public void setFuture(final ScheduledFuture<?> f) {
		m_scheduledFuture = f;
	}

	public int getLeaves() {
		return getOrderQty() - getCumQty();
	}

	public boolean isFilled() {
		return getLeaves() <= 0;
	}

	public int getOrderQty() {
		return m_execRpt.getOrderQty();
	}

	public int getCumQty() {
		return m_execRpt.getCumQty();
	}

	public double getAvgPrice() {
		return m_execRpt.getAvgPrice();
	}

	public boolean isOpen() {
		if (m_execRpt == null)
			return false;
		final OrderStatus status = m_execRpt.getOrderStatus();
		return status == OrderStatus.New || status == OrderStatus.PartiallyFilled || status == OrderStatus.PdgCancel || status == OrderStatus.PdgReplace;
	}

	public boolean isPdgNew() {
		return m_execRpt.getOrderStatus() == OrderStatus.PdgNew;
	}

	public boolean isPdgCancel() {
		return m_execRpt.getOrderStatus() == OrderStatus.PdgCancel;
	}

	public boolean isPdgReplace() {
		return m_execRpt.getOrderStatus() == OrderStatus.PdgReplace;
	}

	public void schedule(final long numSecs) {
		m_scheduledFuture = m_executor.schedule(this, numSecs, TimeUnit.SECONDS);
	}

	public boolean isMktOrder() {
		return m_execRpt.getOrdType() == OrderType.Market;
	}

	public boolean isIOC() {
		return m_execRpt.getOrdType() == OrderType.Limit && m_execRpt.getTimeInForce() == TimeInForce.IOC;
	}

	public boolean isTIFOnOpen() {
		return m_execRpt.getTimeInForce() == TimeInForce.ON_OPEN;
	}

	public boolean isTIFOnClose() {
		return m_execRpt.getTimeInForce() == TimeInForce.ON_CLOSE;
	}

	public boolean isDarkPoolOrder() {
		return m_isDarkOrder;
	}

	private boolean isDebug() {
		return m_logger.isDebugEnabled();
	}

	public int getRandom1to10() {
		return 1 + m_random.nextInt(10);
	}

	public int getRandom1to100() {
		return 1 + m_random.nextInt(100);
	}

	private void doExecute(final ExecutionReportMsg execRpt, final int lastShares) {
		if (lastShares <= 0)
			throw new IllegalArgumentException(String.format("Cannot execute %d shares!!", lastShares));
		//
		final double lastPx = m_execRpt.getPrice();
		double avgPx = getAvgPrice();
		int cumQty = getCumQty();
		avgPx = (cumQty + lastShares) <= 0 ? 0 : ((avgPx * cumQty) + (lastPx * lastShares)) / (cumQty + lastShares);
		cumQty += lastShares;
		execRpt.setAvgPrice(avgPx);
		execRpt.setCumQty(cumQty);
		determineOrderStatus(execRpt, null); // no default!
		execRpt.setExecType(isFilled() ? ExecType.Fill : ExecType.PartialFill);
		execRpt.setLastShares(lastShares);
		execRpt.setLastPrice(lastPx);
		execRpt.setLeavesQty(getLeaves());
	}

	/**
	 * If isFilled, status is set to FILLED. If cumQty > 0, status is set to PartilaFill, otherwise status is unchanged
	 */
	private void determineOrderStatus(final ExecutionReportMsg execRpt, final OrderStatus defaultStatus) {
		if (isFilled()) {
			m_logger.info(String.format("BlockID %s: clOrdID=%s. Order FILLED!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
			m_scheduledFuture.cancel(false); // since I am canceling myself,
												// don't generate
												// InterruptException
			execRpt.setOrderStatus(OrderStatus.Filled);
		} else if (getCumQty() > 0) {
			execRpt.setOrderStatus(OrderStatus.PartiallyFilled);
			m_logger.info(String.format("BlockID %s: clOrdID=%s. PARTIAL_FILL", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
		} else if (defaultStatus != null) {
			execRpt.setOrderStatus(defaultStatus);
		} else {
			m_logger.warn(String.format("BlockID %s: clOrdID=%s. OrderStaus UNCHANGED from %s", m_execRpt.getBlockID(), m_execRpt.getClOrdID(), execRpt.getOrderStatus().name()));
		}
	}

	private void reSchedule() {
		final int delaySecs;
		if (isIOC())
			delaySecs = 1;
		else if (isDarkPoolOrder())
			delaySecs = 1 + getRandom1to10();
		else if (isTIFOnOpen()) {
			delaySecs = getRandom1to100() + Math.max(0, DateUtils.getUSMarketOpenTime().getSecondOfDay() - DateUtils.getCurrentTime().getSecondOfDay());
			m_logger.info(String.format("BlockID %s: clOrdID=%s. ON_OPEN: Scheduled to execute in %d seconds", m_execRpt.getBlockID(), m_execRpt.getClOrdID(), delaySecs));
		} else if (isTIFOnClose()) {
			delaySecs = 60 + Math.max(0, DateUtils.getUSMarketCloseTime().getSecondOfDay() - DateUtils.getCurrentTime().getSecondOfDay());
			m_logger.info(String.format("BlockID %s: clOrdID=%s. ON_CLOSE: Scheduled to execute in %d seconds", m_execRpt.getBlockID(), m_execRpt.getClOrdID(), delaySecs));
		} else
			delaySecs = getRandom1to10();
		//
		schedule(Math.max(delaySecs, 1));
	}

	/** Determine new status */
	private void setStatus() {
		m_execRpt.setOrderStatus(isFilled() ? OrderStatus.Filled : getCumQty() > 0 ? OrderStatus.PartiallyFilled : OrderStatus.New);
	}

	@Override
	public synchronized void run() {
		m_logger.info(String.format("BlockID=%s: ClordID=%s: OrderExecutor.run() called...", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));

		if (isPdgNew()) {
			if (isIOC()) {
				if (getRandom1to10() < 4) { // Randomly reject the order
					m_execRpt.setOrderStatus(OrderStatus.Rejected);
					m_execRpt.setExecType(ExecType.Rejected);
					m_execRpt.setOrderRejectReason("Random reject");
					m_logger.info(String.format("BlockID=%s: ClordID=%s: Order REJECTED!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
				}
			}
			//
			if (m_execRpt.getOrderStatus() == OrderStatus.PdgNew) {
				m_execRpt.setOrderStatus(OrderStatus.New);
				m_execRpt.setExecType(ExecType.New);
				m_logger.info(String.format("BlockID %s: clOrdID=%s. ORDER_ACCEPTED", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
				reSchedule();
			}
			m_client.onExecutionReport(m_execRpt);
		} else if (isPdgCancel()) {
			m_execRpt.setOrderStatus(OrderStatus.Canceled);
			m_execRpt.setExecType(ExecType.Canceled);
			m_logger.info(String.format("BlockID=%s: ClordID=%s: Order CANCELED!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
			m_client.onExecutionReport(m_execRpt);
		} else if (isPdgReplace()) {
			if (m_amendReq.getNewQty() != null) {// new qty already validated in
													// cxlRpl() below
				m_logger.info(String.format("BlockID=%s: OrrigClOrdID=%s, ClordID=%s: Qty Changed from %s to %s !!", m_execRpt.getBlockID(), m_execRpt.getOrigClOrdID(),
						m_execRpt.getClOrdID(), m_execRpt.getOrderQty(), m_amendReq.getNewQty()));
				m_execRpt.setOrderQty(m_amendReq.getNewQty());
			}
			if (m_amendReq.getNewPrice() != null) {
				m_logger.info(String.format("BlockID=%s: OrrigClOrdID=%s, ClordID=%s: Price Changed from %s to %s !!", m_execRpt.getBlockID(), m_execRpt.getOrigClOrdID(),
						m_execRpt.getClOrdID(), m_execRpt.getPrice(), m_amendReq.getNewPrice().getLimitPrice()));
				m_execRpt.setPrice(m_amendReq.getNewPrice().getLimitPrice());
			}
			//
			m_execRpt.setLeavesQty(getLeaves()); // This may cause the order to
													// be FILLED!!
			m_execRpt.setLastShares(0);
			m_execRpt.setLastPrice(m_execRpt.getPrice());
			determineOrderStatus(m_execRpt, OrderStatus.Replaced); // May be set
																	// to
																	// filled!!
			m_execRpt.setExecType(ExecType.Replace);
			m_logger.info(String.format("BlockID=%s: OrigClOrdID=%s, ClordID=%s: Order REPLACED!!", m_execRpt.getBlockID(), m_execRpt.getOrigClOrdID(), m_execRpt.getClOrdID()));
			//
			// Swap out OrigClOrdID from the orders map and insert theClOrdID
			m_fixEngine.replaceOrdID(m_amendReq, m_execRpt.getClOrdID());

			m_client.onExecutionReport(m_execRpt);
			//
			// Morph into the new order!
			m_execRpt.setOrigClOrdID(null);
			determineOrderStatus(m_execRpt, OrderStatus.New); // Set the order
																// staus for the
																// NEW order

			if (isOpen() && !isFilled())
				reSchedule();
		} else if (isOpen()) {
			// do a partial fill
			if (isIOC() && getRandom1to10() > 5) {
				m_execRpt.setOrderStatus(OrderStatus.Canceled);
				m_execRpt.setExecType(ExecType.Canceled);
				m_logger.info(String.format("BlockID=%s: ClordID=%s: Order CANCELED!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
				m_client.onExecutionReport(m_execRpt);
			} else {
				final int lastShares;
				if (isTIFOnClose() || isTIFOnOpen() || isMktOrder()) {
					lastShares = getLeaves();
				} else if (isDarkPoolOrder()) {
					lastShares = getRandom1to100() < 75 ? Math.min(500, getLeaves()) : 0;
				} else if (isIOC()) {
					lastShares = getRandom1to100() < 90 ? Math.min(10000, getLeaves()) : 0;
				} else {
					lastShares = getRandom1to100() < 80 ? Math.min(300, getLeaves()) : 0;
				}
				if (lastShares > 0) {
					doExecute(m_execRpt, lastShares);
					m_client.onExecutionReport(m_execRpt);
				}
				if (isOpen() && !isFilled())
					reSchedule();
			}
		} else {
			m_logger.warn(String.format("BlockID %s: clOrdID=%s. Order is no longer OPEN!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
		}
	}

	public synchronized void cancel() {
		if (isPdgCancel()) {
			doCxlReject("Order is PdgCancel");
		} else if (isPdgReplace()) {
			doCxlReject("Order is PdgReplace");
		} else if (!isOpen()) {
			doCxlReject("Too late to cancel. Order NO LONGER open");
		} else {
			m_execRpt.setOrderStatus(OrderStatus.PdgCancel);
			m_execRpt.setExecType(ExecType.PdgCancel);
			m_logger.info(String.format("BlockID=%s: ClordID=%s: Order is PendingCancel!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
			m_client.onExecutionReport(m_execRpt);
			schedule(2); // Try to cancel it in 2 secs
		}
	}

	private void doCxlReject(final String rejReason) {
		doCxlReject(m_execRpt.getClOrdID(), rejReason);
	}

	private void doCxlReject(final String clOrdID, final String rejReason) {
		final CancelRejectedMsg cxlRej = new CancelRejectedMsg();
		cxlRej.setBroker(m_execRpt.getBroker());
		cxlRej.setBlockID(m_execRpt.getBlockID());
		cxlRej.setClOrdID(clOrdID);
		cxlRej.setOrigClOrdID(m_execRpt.getClOrdID());
		cxlRej.setOrderStatus(m_execRpt.getOrderStatus());
		cxlRej.setCxlRejectReason(rejReason);
		cxlRej.setText("");
		cxlRej.setOrderQty(m_execRpt.getOrderQty());
		cxlRej.setOrdType(m_execRpt.getOrdType());
		cxlRej.setPrice(m_execRpt.getPrice());
		cxlRej.setSide(m_execRpt.getSide());
		cxlRej.setTicker(m_execRpt.getTicker());
		cxlRej.setTimeInForce(m_execRpt.getTimeInForce());
		m_client.onCancelRejected(cxlRej);
	}

	public synchronized void cxlRpl(final AmendOrderRequestMsg req, final String clOrdID, final FIXEngine fixEngine) {
		if (isPdgCancel()) {
			doCxlReject(clOrdID, "Order is PdgCancel");
		} else if (isPdgReplace()) {
			doCxlReject(clOrdID, "Order is PdgReplace");
		} else if (req.getNewQty() != null && req.getNewQty() < getCumQty()) {
			doCxlReject(clOrdID, String.format("Too late to cancel. NewQty(%s) < CumQty(%s)", req.getNewQty(), getCumQty()));
		} else if (!isOpen()) {
			doCxlReject(clOrdID, "Too late to cancel");
		} else {
			m_fixEngine = fixEngine;
			m_execRpt.setOrderStatus(OrderStatus.PdgReplace);
			m_execRpt.setExecType(ExecType.PdgReplace);
			m_logger.info(String.format("BlockID=%s: ClordID=%s: Order is PendingReplace!!", m_execRpt.getBlockID(), m_execRpt.getClOrdID()));
			m_execRpt.setOrigClOrdID(m_execRpt.getClOrdID());
			m_execRpt.setClOrdID(clOrdID);
			m_client.onExecutionReport(m_execRpt);
			m_amendReq = req;
			final int delaySecs = (int) (getRandom1to10() / 2.0);
			schedule(delaySecs); // Try to replace it in 0 to 5 secs
		}
	}

	public static boolean isDarkPool(final String venueID) {
		return m_venuesFile.getVenue(venueID) != null;
	}

	public static void init(final DirectoryIterator dataDirIter) throws IOException {
		m_venuesFile = new DOPVenuesFile(dataDirIter);
	}

}
