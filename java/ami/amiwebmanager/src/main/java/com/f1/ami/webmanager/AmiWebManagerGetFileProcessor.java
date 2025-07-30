package com.f1.ami.webmanager;

import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerGetFileResponse;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.LH;

public class AmiWebManagerGetFileProcessor extends BasicRequestProcessor<AmiWebManagerGetFileRequest, State, AmiWebManagerGetFileResponse> {
	private static final Logger log = LH.get();
	private Object anag;
	final private AmiWebManagerController manager;

	public AmiWebManagerGetFileProcessor(AmiWebManagerController manager) {
		super(AmiWebManagerGetFileRequest.class, State.class, AmiWebManagerGetFileResponse.class);
		this.manager = manager;
	}

	@Override
	protected AmiWebManagerGetFileResponse processRequest(RequestMessage<AmiWebManagerGetFileRequest> action, State state, ThreadScope threadScope) throws Exception {
		AmiWebManagerGetFileRequest req = action.getAction();
		AmiWebManagerGetFileResponse res = nw(AmiWebManagerGetFileResponse.class);
		String fileName = req.getFileName();
		String parentFileName = req.getParentFileName();
		try {
			AmiWebManagerFile file = manager.newFile(parentFileName, fileName);
			final AmiFileMessage r = nw(AmiFileMessage.class);
			if (req.getAsOfModifiedTime() > 0L && req.getAsOfModifiedTime() == file.getFile().lastModified()) {
				LH.info(log, "User ", req.getInvokedBy(), " requested file without change (based on timestamp): ", file);
				res.setStatus(AmiWebManagerGetFileResponse.STATUS_NO_CHANGE);
			} else {
				this.manager.toFile(req.getInvokedBy(), r, file, req.getOptions());
				res.setFile(r);
				res.setStatus(AmiWebManagerGetFileResponse.STATUS_OKAY);
			}
			res.setOk(true);
		} catch (Exception e) {
			LH.warning(log, "Error Processing request for ", req.getInvokedBy(), ": ", e);
			res.setOk(false);
			res.setException(e);
		}
		return res;
	}
}
