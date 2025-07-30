package com.f1.refdataclient;

import java.util.Map;

import com.f1.container.OutputPort;
import com.f1.container.RequestMessage;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.RefDataRequestMessage;
import com.f1.pofo.refdata.Security;
import com.f1.refdata.impl.BasicRefDataManager;
import com.f1.utils.CH;
import com.f1.utils.structs.IntSet;

public class RefDataClientRequestProcessor extends BasicRequestProcessor<RefDataRequestMessage, RefDataClientState, RefDataInfoMessage> {

	private BasicRefDataManager manager;
	public final OutputPort<ResultMessage<RefDataInfoMessage>> responsePort = (OutputPort) newOutputPort(ResultMessage.class);
	public final RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> serverrequestport = newRequestOutputPort(RefDataRequestMessage.class, RefDataInfoMessage.class);

	public RefDataClientRequestProcessor() {
		super(RefDataRequestMessage.class, RefDataClientState.class, RefDataInfoMessage.class);
		serverrequestport.setConnectionOptional(true);
	}

	@Override
	public void processAction(RequestMessage<RefDataRequestMessage> req, RefDataClientState state, ThreadScope threadScope) {
		RefDataRequestMessage action = req.getAction();
		this.manager = state.manager;
		IntSet result = manager.findSecurity(action.getSymbol(), action.getRic(), action.getCusip(), action.getSedol(), action.getIsin(), action.getAsOf());
		if (CH.isntEmpty(action.getSecurityIds())) {
			for (Integer i : action.getSecurityIds())
				if (manager.getSecurity(i) != null)
					result.add(i);
		}
		if (result.size() != 0) {
			RefDataInfoMessage r = nw(RefDataInfoMessage.class);
			Security security = manager.getSecurity(result.iterator().nextInt());
			r.setSecurities((Map) CH.m(security.getSecurityId(), security));
			reply(req, nwResultMessage(r), threadScope);
		} else if (serverrequestport.isConnected()) {
			RequestMessage<RefDataRequestMessage> req2 = nw(RequestMessage.class);
			req2.setCorrelationId(req);
			req2.setAction(req.getAction());
			req2.setResultPort(responsePort);
			serverrequestport.send(req2, threadScope);
		} else
			reply(req, nwResultMessage(nw(RefDataInfoMessage.class)), threadScope);
	}

	@Override
	protected RefDataInfoMessage processRequest(RequestMessage<RefDataRequestMessage> action, RefDataClientState state, ThreadScope threadScope) {
		return null;
	}
}
