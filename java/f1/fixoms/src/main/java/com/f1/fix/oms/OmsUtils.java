package com.f1.fix.oms;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.container.ContainerScope;
import com.f1.fix.oms.schema.ClientOrder;
import com.f1.fix.oms.schema.InternalSlice;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.Slice;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.pofo.fix.FixExecutionReport;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.FixReport;
import com.f1.pofo.fix.FixRequest;
import com.f1.pofo.fix.FixStatusReport;
import com.f1.pofo.fix.OrdStatus;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.pofo.oms.SliceType;
import com.f1.refdata.RefDataManager;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.db.Database;

public class OmsUtils {

	public static final String SERVICEID_REFDATA = "REFDATA";
	private static final String DATASOURCEID_OMSDB = "OMSDB";
	private static final Logger log = LH.get();

	// TODO: Maybe return boolean?
	/**
	 * depending on the order's status, transition the tragets status to that order accordingly
	 * 
	 * @param order
	 *            the order to transation
	 * @param target
	 *            the status to apply
	 */
	public static void transitionTo(ClientOrder order, OmsOrderStatus target) {
		int orderStatus = order.getOrderStatus();

		switch (OmsOrderStatus.get(MH.indexOfLastBitSet(orderStatus))) {
			case UNINITIALIZED:
				transitionUnitialized(order, target);
				break;
			case PENDING_ACK:
				transitionPendingAck(order, target);
				break;
			case ACKED:
				transitionAck(order, target);
				break;
			case PENDING_CXL:
				transitionPendingCxl(order, target);
				break;
			case PENDING_RPL:
				transitionPendingRpl(order, target);
				break;
			case REPLACED:
				transitionReplaced(order, target);
				break;
			case PARTIAL:
				transitionPartial(order, target);
				break;
			case FILLED:
				transitionFilled(order, target);
				break;
		}
	}

	private static void transitionFilled(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
		}
	}
	private static void transitionPartial(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
			case PENDING_PAUSE:
			case PAUSED:
				break;
		}
	}

	private static void transitionReplaced(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
			case PENDING_PAUSE:
			case PAUSED:
				break;
		}
	}

	private static void transitionPendingCxl(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.CANCELLED, OrdStatus.PENDING_CXL);
				break;
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.FILLED, OrdStatus.PENDING_CXL);
				break;
		}
	}

	private static void transitionPendingRpl(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case REPLACED:
				addOrderStatus(order, OmsOrderStatus.REPLACED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.REPLACED, OrdStatus.PENDING_RPL);
				if (MH.areAnyBitsSet(order.getOrderStatus(), OmsOrderStatus.FILLED.getMask())) {
					order.setOrderStatus(MH.clearBits(order.getOrderStatus(), OmsOrderStatus.FILLED.getMask()));
					final Order fo = order.getFixOrder();
					fo.setOrderStatus(MH.clearBits(fo.getOrderStatus(), OrdStatus.FILLED.getIntMask()));
				}
				break;
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.FILLED, OrdStatus.PENDING_RPL);
				break;
		}
	}

	// Uninitialized can transition to:
	// PendingAck
	// Rejected
	// Cancelled for DFD
	private static void transitionUnitialized(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case PENDING_ACK:
				order.setOrderStatus(OmsOrderStatus.PENDING_ACK.getMask()); // implicitly
																			// clear
																			// uninitialized
																			// bit
				order.getFixOrder().setOrderStatus(OrdStatus.PENDING_ACK.getIntMask());
				break;
			case REJECTED:
				order.setOrderStatus(OmsOrderStatus.REJECTED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.REJECTED.getIntMask()); // implicitly
																						// clear
																						// uninitialized
																						// bit
				break;
			case CANCELLED:
				order.setOrderStatus(OmsOrderStatus.CANCELLED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.CANCELLED.getIntMask()); // implicitly
																						// clear
																						// uninitialized
																						// bit
				break;
		}
	}

	private static void transitionPendingAck(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case ACKED:
				order.setOrderStatus(OmsOrderStatus.ACKED.getMask()); // implicitly
																		// clear
																		// uninitialized
																		// bit
				order.getFixOrder().setOrderStatus(OrdStatus.ACKED.getIntMask());
				break;
			case REJECTED:
				order.setOrderStatus(OmsOrderStatus.REJECTED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.REJECTED.getIntMask()); // implicitly
																						// clear
																						// uninitialized
																						// bit
				break;
			case PENDING_CXL:
				order.setOrderStatus(OmsOrderStatus.PENDING_CXL.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.PENDING_CXL.getIntMask());
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
		}
	}

	private static void transitionAck(ClientOrder order, OmsOrderStatus target) {
		switch (target) {
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
			case PENDING_PAUSE:
			case PAUSED:
				break;
		}
	}

	private static void addOrderStatus(OmsOrder order, OmsOrderStatus status, OmsOrderStatus... p) {
		int currstatus = order.getOrderStatus();
		int mask = 0;
		for (OmsOrderStatus removeStatus : p) {
			mask = mask | removeStatus.getMask();
		}
		currstatus = currstatus & ~mask;// drop all the bits that are set;
		order.setOrderStatus(currstatus | status.getMask());
	}

	private static void addOrderStatus(OmsOrder order, OrdStatus status, OrdStatus... p) {
		int currstatus = order.getFixOrder().getOrderStatus();
		int mask = 0;
		for (OrdStatus removeStatus : p) {
			mask = mask | removeStatus.getIntMask();
		}
		currstatus = currstatus & ~mask;// drop all the bits that are set;
		order.getFixOrder().setOrderStatus((currstatus | status.getIntMask()));
	}

	public static void transitionSliceTo(Slice order, OmsOrderStatus target) {
		int orderStatus = order.getOrderStatus();
		switch (OmsOrderStatus.get(MH.indexOfLastBitSet(orderStatus))) {
			case PENDING_ACK:
				transitionPendingAck(order, target);
				break;
			case ACKED:
				transitionAck(order, target);
				break;
			case PARTIAL:
				transitionPartial(order, target);
				break;
			case PENDING_CXL:
				transitionPendingCxl(order, target);
				break;
			case PENDING_RPL:
				transitionPendingRpl(order, target);
				break;
			case REPLACED:
				transitionReplaced(order, target);
				break;
		}
	}

	private static void transitionReplaced(Slice order, OmsOrderStatus target) {
		switch (target) {
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
		}
	}

	public static void transitionPendingRpl(Slice order, OmsOrderStatus target) {
		switch (target) {
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.CANCELLED, OrdStatus.PENDING_RPL);
				break;
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.FILLED, OrdStatus.PENDING_RPL);
				break;
			case ACKED:
				addOrderStatus(order, OmsOrderStatus.ACKED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.ACKED, OrdStatus.PENDING_RPL);
				break;
			case REPLACED:
				addOrderStatus(order, OmsOrderStatus.REPLACED, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.REPLACED, OrdStatus.PENDING_RPL);
				break;
		}
	}

	private static void transitionPartial(Slice order, OmsOrderStatus target) {
		switch (target) {
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
		}
	}

	private static void transitionPendingCxl(Slice order, OmsOrderStatus target) {
		switch (target) {
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.CANCELLED, OrdStatus.PENDING_CXL);
				break;
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.FILLED, OrdStatus.PENDING_CXL);
				break;
			case ACKED:
				addOrderStatus(order, OmsOrderStatus.ACKED);
				addOrderStatus(order, OrdStatus.ACKED);
				break;
		}
	}

	private static void transitionAck(Slice order, OmsOrderStatus target) {
		switch (target) {
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
			case PARTIAL:
				addOrderStatus(order, OmsOrderStatus.PARTIAL);
				addOrderStatus(order, OrdStatus.PARTIAL);
				break;
			case FILLED:
				addOrderStatus(order, OmsOrderStatus.FILLED);
				addOrderStatus(order, OrdStatus.FILLED);
				break;
			case PENDING_CXL:
				addOrderStatus(order, OmsOrderStatus.PENDING_CXL);
				addOrderStatus(order, OrdStatus.PENDING_CXL);
				break;
			case PENDING_RPL:
				addOrderStatus(order, OmsOrderStatus.PENDING_RPL);
				addOrderStatus(order, OrdStatus.PENDING_RPL);
				break;
			case REJECTED:
				order.setOrderStatus(OmsOrderStatus.REJECTED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.REJECTED.getIntMask()); // implicitly
				break;
		}
	}

	private static void transitionPendingAck(Slice order, OmsOrderStatus target) {
		switch (target) {
			case ACKED:
				order.setOrderStatus(OmsOrderStatus.ACKED.getMask()); // implicitly
																		// clear
																		// uninitialized
																		// bit
				order.getFixOrder().setOrderStatus(OrdStatus.ACKED.getIntMask());
				break;
			case REJECTED:
				order.setOrderStatus(OmsOrderStatus.REJECTED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.REJECTED.getIntMask()); // implicitly
																						// clear
																						// uninitialized
																						// bit
				break;
			case PARTIAL:
				order.setOrderStatus(OmsOrderStatus.PARTIAL.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.PARTIAL.getIntMask());
				break;
			case FILLED:
				order.setOrderStatus(OmsOrderStatus.FILLED.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.FILLED.getIntMask());
				break;
			case PENDING_CXL:
				order.setOrderStatus(OmsOrderStatus.PENDING_CXL.getMask());
				order.getFixOrder().setOrderStatus(OrdStatus.PENDING_CXL.getIntMask());
				break;
			case CANCELLED:
				addOrderStatus(order, OmsOrderStatus.CANCELLED);
				addOrderStatus(order, OrdStatus.CANCELLED);
				break;
		}
	}

	public static void processFill(OmsOrder slice, FixExecutionReport report) {
		if (slice.getExecutions() == null) {
			slice.setExecutions(new PersistableHashMap<String, Execution>());
		}
		Execution exec = report.getExecution();
		if (exec.getExecGroupID() != null)
			slice.getExecutions().put(exec.getExecGroupID(), exec);
		else
			slice.getExecutions().put(exec.getId(), exec);
		Order state = slice.getFixOrder();
		state.setTotalExecQty(state.getTotalExecQty() + exec.getExecQty());
		state.setTotalExecValue(state.getTotalExecValue() + exec.getExecPx() * exec.getExecQty());
	}

	public static Execution processBust(OmsOrder slice, FixExecutionReport report) {
		if (slice.getExecutions() == null) {
			slice.setExecutions(new PersistableHashMap<String, Execution>());
		}
		Execution exec = report.getExecution();
		String execRefId = exec.getExecRefID();
		Execution orig = slice.getExecutions().get(execRefId);
		Execution old = slice.getExecutions().put(exec.getId(), exec);
		Order state = slice.getFixOrder();
		if (orig != null && orig.getExecStatus() != 1) {
			orig.setExecStatus(1);//TODO: USE A DAMN EXECSTATUS ENUM
			state.setTotalExecQty(state.getTotalExecQty() - orig.getExecQty());
			state.setTotalExecValue(state.getTotalExecValue() - orig.getExecPx() * orig.getExecQty());
		}
		return orig;
	}
	public static Execution processCorrect(OmsOrder slice, FixExecutionReport report, String type) {
		if (slice.getExecutions() == null) {
			slice.setExecutions(new PersistableHashMap<String, Execution>());
		}
		Execution exec = report.getExecution();
		String execRefId = exec.getExecRefID();
		Execution orig = slice.getExecutions().get(execRefId);
		slice.getExecutions().put(exec.getId(), exec);
		Order fixOrder = slice.getFixOrder();
		long origTotQty = fixOrder.getTotalExecQty();
		double origTotVal = fixOrder.getTotalExecValue();
		if (orig != null && orig.getExecStatus() != 1) {
			orig.setExecStatus(2);//TODO: USE A DAMN EXECSTATUS ENUM
			fixOrder.setTotalExecQty(fixOrder.getTotalExecQty() - orig.getExecQty() + exec.getExecQty());
			fixOrder.setTotalExecValue(fixOrder.getTotalExecValue() - orig.getExecPx() * orig.getExecQty() + exec.getExecPx() * exec.getExecQty());
		}
		{//TODO: remove debug
			long origQty = orig == null ? -1 : orig.getExecQty();
			double origPx = orig == null ? -1 : orig.getExecPx();
			long newQty = exec.getExecQty();
			double newPx = exec.getExecPx();
			long newTotQty = fixOrder.getTotalExecQty();
			double newTotVal = fixOrder.getTotalExecValue();
			LH.info(log, "correct for ", type, ", refId=", execRefId, ", origTotQty=", origTotQty, ", origTotVal=", origTotVal, ", origQty=", origQty, ", origPx=", origPx,
					", newQty=", newQty, ", newPx=", newPx, ", newTotQty=", newTotQty, ", newTotVal=", newTotVal);
		}
		return orig;
	}

	public static boolean isPending(int status) {
		int mask = OrdStatus.PENDING_CXL.getIntMask() | OrdStatus.PENDING_RPL.getIntMask();
		return ((status & mask) > 0);
	}

	// This is the only logical thing that can happen to cancels and replaces
	public static void removePendingCxlRpl(OmsOrder order) {
		int fixmask = OrdStatus.PENDING_CXL.getIntMask() | OrdStatus.PENDING_RPL.getIntMask();
		int mask = OmsOrderStatus.PENDING_CXL.getMask() | OmsOrderStatus.PENDING_RPL.getMask();
		int status = order.getOrderStatus();
		int fixorderStatus = order.getFixOrder().getOrderStatus();
		fixorderStatus &= ~fixmask;
		status &= ~mask;
		order.setOrderStatus(status);
		order.getFixOrder().setOrderStatus(fixorderStatus);
	}

	public static RefDataManager getRefData(ContainerScope cs) {
		return (RefDataManager) cs.getServices().getService(SERVICEID_REFDATA);
	}

	public static Database getOmsDb(ContainerScope cs) {
		return cs.getServices().getDatabase(DATASOURCEID_OMSDB);
	}

	public static void setOmsDb(ContainerScope mycontainer, Database dbsource) {
		mycontainer.getServices().addDatabase(DATASOURCEID_OMSDB, dbsource);
	}

	public static void setRefData(ContainerScope mycontainer, RefDataManager refData) {
		mycontainer.getServices().putService(SERVICEID_REFDATA, refData);
	}

	public static SliceType getSliceType(OmsOrder order) {
		if (order instanceof ClientOrder)
			return SliceType.CLIENT_ORDER;
		else if (order instanceof InternalSlice)
			return SliceType.INTERNAL_SLICE;
		else if (order instanceof Slice)
			return SliceType.SLICE;
		else if (order == null)
			return null;
		else
			throw new RuntimeException("unkown slice type: " + order.getClass().getName());
	}

	public static boolean isTerminal(int orderStatus) {
		int mask = OmsOrderStatus.CANCELLED.getMask() | OmsOrderStatus.FILLED.getMask() | OmsOrderStatus.REJECTED.getMask();
		return MH.anyBits(orderStatus, mask);
	}

	public static boolean isInState(int ordStatus, OmsOrderStatus status) {
		return MH.anyBits(ordStatus, status.getMask());
	}

	/**
	 * 
	 * @param order
	 *            order to extract tag from
	 * @param tag
	 *            tag number
	 * @param dfault
	 *            default value if tag does not exist on order
	 * @return value or default
	 */
	public static String getPassThroughValue(Order order, int tag, String dfault) {
		return CH.getOr(order.getPassThruTags(), tag, dfault);
	}

	/**
	 * 
	 * @param request
	 *            sink to have tag applied to
	 * @param tag
	 *            fix tag value
	 * @param value
	 *            value
	 * @return overwritten value or null
	 */
	public static String setPassThroughValue(FixRequest request, int tag, String value) {
		FixOrderInfo oi = request.getOrderInfo();
		Map<Integer, String> ptt = oi.getPassThruTags();
		if (ptt == null)
			oi.setPassThruTags(ptt = new HashMap<Integer, String>());
		return ptt.put(tag, value);
	}

	/**
	 * 
	 * @param request
	 *            sink to have tag applied to
	 * @param tag
	 *            fix tag value
	 * @param value
	 *            value
	 * @return overwritten value or null
	 */
	public static String setPassThroughValue(FixStatusReport request, int tag, String value) {
		Map<Integer, String> ptt = request.getPassThruTags();
		if (ptt == null)
			request.setPassThruTags(ptt = new HashMap<Integer, String>());
		return ptt.put(tag, value);
	}
	/**
	 * 
	 * @param order
	 *            order to extract tag from
	 * @param tag
	 *            tag number
	 * @param dfault
	 *            default value if tag does not exist on order
	 * @return value or default
	 */
	public static String getPassThroughValue(FixStatusReport request, int tag, String dfault) {
		return CH.getOr(request.getPassThruTags(), tag, dfault);
	}

	/**
	 * 
	 * @param order
	 *            order to extract tag from
	 * @param tag
	 *            tag number
	 * @param dfault
	 *            default value if tag does not exist on order
	 * @return value or default
	 */
	public static String getPassThroughValue(FixExecutionReport order, int tag, String dfault) {
		return CH.getOr(order.getPassThruTags(), tag, dfault);
	}

	/**
	 * 
	 * @param request
	 *            sink to have tag applied to
	 * @param tag
	 *            fix tag value
	 * @param value
	 *            value
	 * @return overwritten value or null
	 */
	public static String setPassThroughValue(FixExecutionReport request, int tag, String value) {
		Map<Integer, String> ptt = request.getPassThruTags();
		if (ptt == null)
			request.setPassThruTags(ptt = new HashMap<Integer, String>());
		return ptt.put(tag, value);
	}

	public static String setPassThroughValue(FixReport request, int tag, String value) {
		Map<Integer, String> ptt = request.getPassThruTags();
		if (ptt == null)
			request.setPassThruTags(ptt = new HashMap<Integer, String>());
		return ptt.put(tag, value);
	}

	public static String getPassThroughValue(FixReport order, int tag, String dfault) {
		return CH.getOr(order.getPassThruTags(), tag, dfault);
	}

	public static void inherit(Execution parentExec, Execution execution) {
		parentExec.setExecGroupID(execution.getId());
		parentExec.setExecTime(execution.getExecTime());
		parentExec.setContraBroker(execution.getContraBroker());
		parentExec.setExecBroker(execution.getExecBroker());
		parentExec.setExecPx(execution.getExecPx());
		parentExec.setExecQty(execution.getExecQty());
		parentExec.setExecStatus(execution.getExecStatus());
		parentExec.setLastMkt(execution.getLastMkt());
		parentExec.setPassThruTags(execution.getPassThruTags());
		parentExec.setExecTransType(execution.getExecTransType());
	}
}
