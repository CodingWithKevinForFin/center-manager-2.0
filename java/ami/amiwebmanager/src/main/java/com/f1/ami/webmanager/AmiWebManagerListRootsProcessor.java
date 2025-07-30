package com.f1.ami.webmanager;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerListRootsResponse;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;

public class AmiWebManagerListRootsProcessor extends BasicRequestProcessor<AmiWebManagerListRootsRequest, State, AmiWebManagerListRootsResponse> {

	final private AmiWebManagerController manager;
	public AmiWebManagerListRootsProcessor(AmiWebManagerController m) {
		super(AmiWebManagerListRootsRequest.class, State.class, AmiWebManagerListRootsResponse.class);
		this.manager = m;
	}

	@Override
	protected AmiWebManagerListRootsResponse processRequest(RequestMessage<AmiWebManagerListRootsRequest> action, State state, ThreadScope threadScope) throws Exception {
		AmiWebManagerListRootsRequest req = action.getAction();
		AmiWebManagerListRootsResponse res = nw(AmiWebManagerListRootsResponse.class);
		List<AmiFileMessage> files = new ArrayList<AmiFileMessage>();
		LH.info(log, "User ", req.getInvokedBy(), " getting list of roots");
		for (AmiWebManagerFile file : this.manager.listRoots()) {
			AmiFileMessage sink = nw(AmiFileMessage.class);
			this.manager.toFile(req.getInvokedBy(), sink, file, (short) 0);
			files.add(sink);
		}
		res.setFiles(files);
		res.setOk(true);
		return res;
	}

}
