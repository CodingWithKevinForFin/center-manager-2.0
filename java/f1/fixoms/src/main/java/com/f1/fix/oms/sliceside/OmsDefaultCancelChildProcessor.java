package com.f1.fix.oms.sliceside;

import java.util.logging.Level;

import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.ThreadScope;
import com.f1.fix.oms.OmsAbstractProcessor;
import com.f1.fix.oms.OmsUtils;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.plugin.CancelChildOrderPlugin;
import com.f1.fix.oms.schema.FixCopyUtil;
import com.f1.fix.oms.schema.OmsOrderStatus;
import com.f1.fix.oms.schema.OrderEventCtx;
import com.f1.fix.oms.schema.Slice;
import com.f1.pofo.fix.ChildOrderId;
import com.f1.pofo.fix.FixOrderInfo;
import com.f1.pofo.fix.MsgType;
import com.f1.pofo.fix.child.FixChildOrderCancelRequest;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.OmsNotification;
import com.f1.pofo.oms.Order;
import com.f1.utils.LH;

/**
 * process cancel requests for child orders
 */
public class OmsDefaultCancelChildProcessor extends OmsAbstractProcessor {
	ObjectGeneratorForClass<FixChildOrderCancelRequest> cg;
	ObjectGeneratorForClass<FixOrderInfo> infog;
	ObjectGeneratorForClass<ChildOrderId> coid;
	private CancelChildOrderPlugin plugin;

	public void start() {
		super.start();
		cg = getGenerator(FixChildOrderCancelRequest.class);
		infog = getGenerator(FixOrderInfo.class);
		coid = getGenerator(ChildOrderId.class);
		this.plugin = super.getOmsPlugin().getCancelChildOrderPlugin();
	}

	@Override
	public void processAction(OrderEventCtx action, OmsOrderState state, ThreadScope threadScope) throws Exception {
		final Slice orderToCancel = (Slice) action.getOrder();
		if (orderToCancel == null) {
			action.setRejectText("Cannot find child order : " + action.getClientMsg().getOrderID());
			return;
		}
		OmsUtils.transitionSliceTo(orderToCancel, OmsOrderStatus.PENDING_CXL);
		final Order fixorder = orderToCancel.getFixOrder();
		FixChildOrderCancelRequest request = cg.nw();
		request.setType(MsgType.CANCEL_REQUEST);
		request.setRootOrderId(state.getClientOrder().getFixOrder().getId());
		final FixOrderInfo info = infog.nw();
		request.setOrderInfo(info);
		FixCopyUtil.copy(request, fixorder);
		orderToCancel.setFixReqRevisionID(orderToCancel.getFixReqRevisionID() + 1);
		final ChildOrderId id = FixCopyUtil.createRequest(this, orderToCancel);
		request.setChildId(id);
		if (plugin != null) {
			try {
				plugin.onCancelChildOrder(orderToCancel.getParentOrder().getFixOrder(), orderToCancel.getFixOrder(), request);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Plugin generated error for cancel child: ", request, e);
			}
		}
		getOmsPlugin().mapCancelChildOrderTags(orderToCancel.getParentOrder().getFixOrder().getPassThruTags(), request.getOrderInfo());
		toFixSession.send(request, threadScope);
		request.setDestination(fixorder.getDestination());
		final OmsNotification notif = newNotification(state);
		notif.setType(OmsAction.CANCEL_CHILD_ORDER);
		toOMSClient.send(notif, threadScope);
	}

}
