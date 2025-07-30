package com.f1.ami.webmanager;

import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerProcessSpecialFileResponse;
import com.f1.ami.amicommon.webfilespecial.AmiSpecialFileProcessor;
import com.f1.base.Table;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.SH;

public class AmiWebManagerProcessSpecialFileProcessor extends BasicRequestProcessor<AmiWebManagerProcessSpecialFileRequest, State, AmiWebManagerProcessSpecialFileResponse> {
	private static final Logger log = LH.get();
	final private AmiWebManagerController manager;

	public AmiWebManagerProcessSpecialFileProcessor(AmiWebManagerController m) {
		super(AmiWebManagerProcessSpecialFileRequest.class, State.class, AmiWebManagerProcessSpecialFileResponse.class);
		this.manager = m;
	}

	@Override
	protected AmiWebManagerProcessSpecialFileResponse processRequest(RequestMessage<AmiWebManagerProcessSpecialFileRequest> action, State state, ThreadScope threadScope)
			throws Exception {
		AmiWebManagerProcessSpecialFileRequest req = action.getAction();
		AmiWebManagerProcessSpecialFileResponse res = nw(AmiWebManagerProcessSpecialFileResponse.class);
		String fileName = req.getFileName();
		try {
			AmiWebManagerFile file = manager.newFile(fileName);
			LH.info(log, "User ", req.getInvokedBy(), " running special process ", req.getInstruction(),
					req.getOptions() == null ? "()" : "(" + SH.joinMap(',', '=', req.getOptions()) + ")", " for ", IOH.getFullPath(file.getFile()));
			Table table = AmiSpecialFileProcessor.processSpecial(file.getFile(), req.getInstruction(), req.getOptions());
			res.setResults(table);
		} catch (Exception e) {
			res.setOk(false);
			res.setException(e);
		}
		return res;
	}

}
