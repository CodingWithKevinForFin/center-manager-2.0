package com.sjls.f1.start.ofradapter;

import org.apache.log4j.Logger;

import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.oms.OmsClientAction;
import com.f1.fixomsclient.OmsClientState;
import com.f1.pofo.oms.OmsAction;
import com.f1.pofo.oms.Order;
import com.f1.povo.standard.TextMessage;

public class OnOmsResponseProcessor extends BasicProcessor<ResultMessage<TextMessage>, OmsClientState> {
    public final static Logger m_logger = Logger.getLogger(OnOmsResponseProcessor.class);
    
    private final static int REJECTED_BY_OMS = 9;
    
    private OfrAdapter adapter;
    private String system;
    public OnOmsResponseProcessor(final OfrAdapter adapter) {
        super((Class) ResultMessage.class, OmsClientState.class);
        this.adapter = adapter;
    }

    @Override
    public void processAction(final ResultMessage<TextMessage> action, final OmsClientState state, final ThreadScope threadScope) throws Exception {
        final OmsClientAction clientAction = (OmsClientAction) action.getRequestMessage().getAction();
        final OmsAction reqAction = clientAction.getOrderAction();
        final String omsResponseText = action.getAction().getText();
        if (omsResponseText.equals("OK")){
            if(m_logger.isDebugEnabled()) {
                m_logger.debug(String.format("OnOmsResponseProcessor.processAction(%s) SUCCEEDED: Request==>%s<==", reqAction.name(), clientAction));
            }
        }
        else {
            m_logger.warn(String.format("OnOmsResponseProcessor.processAction(%s): FAILED! Request==>%s<==. Text=[%s]", reqAction.name(), clientAction, omsResponseText));
            final String sliceID=clientAction.getOrderID();
            final Order order = state.getOrder(system, sliceID);
            if(reqAction==OmsAction.NEW_CHILD_ORDER) {
                final ExecutionReport msg=new ExecutionReport(order, clientAction.getChildRequest(), REJECTED_BY_OMS, omsResponseText);
                adapter.getEO().onExecutionReport(msg);
            }
            else if(reqAction==OmsAction.CANCEL_CHILD_ORDER) {
                final CancelRejectWrapper.Builder bldr = new CancelRejectWrapper.Builder();
                final OfrSliceId ofrSliceId = adapter.getOfrIdFor(new OmsSliceId(sliceID));
                if(ofrSliceId==null) {
                    m_logger.warn(String.format("OnOmsResponseProcessor.processAction(%s): FAILED! Request==>%s<==. Cannot obtain OfrId for OmsId [%s]!!", 
                            reqAction.name(), clientAction, sliceID));
                }
                else {
                    bldr.origClOrdID = ofrSliceId.toString();
                    bldr.clOrdID = ofrSliceId.toString();
                    bldr.cxlRejReason="9";
                    bldr.text = "3forgeOMS Reject: "+omsResponseText;
                    final CancelRejectWrapper msg=new CancelRejectWrapper(order, bldr);
                    adapter.getEO().onCancelRejected(msg);
                }
            }
            else if(reqAction==OmsAction.REPLACE_CHILD_ORDER) {
                final CancelRejectWrapper.Builder bldr = new CancelRejectWrapper.Builder();
                bldr.origClOrdID = clientAction.getChildRequest().getOrigRequestId();
                bldr.clOrdID = clientAction.getChildRequest().getRequestId();
                bldr.cxlRejReason="9";
                bldr.text = "3forgeOMS Reject: "+omsResponseText;
                final CancelRejectWrapper msg=new CancelRejectWrapper(order, bldr);
                adapter.getEO().onCancelRejected(msg);
            }
        }
        // TODO:this.adapter.onReject(...);
    }

}
