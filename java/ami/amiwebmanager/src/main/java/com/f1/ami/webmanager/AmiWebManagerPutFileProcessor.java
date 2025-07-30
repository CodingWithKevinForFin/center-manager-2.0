package com.f1.ami.webmanager;

import java.io.File;

import com.f1.ami.amicommon.msg.AmiFileMessage;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileRequest;
import com.f1.ami.amicommon.msg.AmiWebManagerPutFileResponse;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.utils.AH;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class AmiWebManagerPutFileProcessor extends BasicRequestProcessor<AmiWebManagerPutFileRequest, State, AmiWebManagerPutFileResponse> {

	final private AmiWebManagerController manager;
	public AmiWebManagerPutFileProcessor(AmiWebManagerController m) {
		super(AmiWebManagerPutFileRequest.class, State.class, AmiWebManagerPutFileResponse.class);
		this.manager = m;
	}

	@Override
	protected AmiWebManagerPutFileResponse processRequest(RequestMessage<AmiWebManagerPutFileRequest> action, State state, ThreadScope threadScope) throws Exception {
		AmiWebManagerPutFileRequest req = action.getAction();
		AmiWebManagerPutFileResponse res = nw(AmiWebManagerPutFileResponse.class);
		AmiWebManagerFile awFile = manager.newFile(req.getFileName());
		File file = awFile.getFile();
		try {
			switch (req.getAction()) {
				case AmiWebManagerPutFileRequest.ACTION_WRITE_DATA: {
					LH.info(log, "User ", req.getInvokedBy(), " write: ", awFile, " (", AH.length(req.getData()), " chars)");
					IOH.writeData(file, req.getData());
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_WRITE_DATA_SAFE: {
					LH.info(log, "User ", req.getInvokedBy(), " safe-write: ", awFile, " (", AH.length(req.getData()), " chars)");
					if (!file.exists())
						IOH.writeData(file, OH.EMPTY_BYTE_ARRAY);
					File tmpFile = new File(file.getAbsolutePath() + req.getSafeFileExtension());
					IOH.writeData(tmpFile, req.getData());
					file.delete();
					if (!tmpFile.renameTo(file))
						throw new RuntimeException("Could not complete safe file write: move " + IOH.getFullPath(tmpFile) + " ==> " + IOH.getFullPath(file));
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_APPEND_DATA: {
					LH.info(log, "User ", req.getInvokedBy(), " append: ", awFile, " (", AH.length(req.getData()), " chars)");
					IOH.appendData(file, req.getData());
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_MKDIR: {
					LH.info(log, "User ", req.getInvokedBy(), " mkdir: ", awFile);
					boolean b = file.mkdir();
					res.setReturnFlag(b);
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_MKDIR_FORCE: {
					LH.info(log, "User ", req.getInvokedBy(), " force-mkdir: ", awFile);
					IOH.ensureDir(file);
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_MOVE: {
					AmiWebManagerFile target = manager.newFile(req.getTargetFileName());
					LH.info(log, "User ", req.getInvokedBy(), " mv: ", awFile, " to ", target);
					boolean b = file.renameTo(target.getFile());
					res.setReturnFlag(b);
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_MOVE_FORCE:
					AmiWebManagerFile target = manager.newFile(req.getTargetFileName());
					LH.info(log, "User ", req.getInvokedBy(), " force-mv: ", awFile, " to ", target);
					IOH.moveForce(file, target.getFile());
					break;

				case AmiWebManagerPutFileRequest.ACTION_DELETE: {
					LH.info(log, "User ", req.getInvokedBy(), " rm: ", awFile);
					boolean b = file.delete();
					res.setReturnFlag(b);
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_DELETE_SAFE: {
					LH.info(log, "User ", req.getInvokedBy(), " safe-rm: ", awFile);
					File tmpFile = new File(file.getAbsolutePath() + req.getSafeFileExtension());
					IOH.delete(file);
					IOH.delete(tmpFile);
					break;
				}
				case AmiWebManagerPutFileRequest.ACTION_DELETE_FORCE_RECURSIVE: {
					LH.info(log, "User ", req.getInvokedBy(), " force-rm: ", awFile);
					boolean b = IOH.deleteForce(file);
					res.setReturnFlag(b);
					break;
				}
			}
			if (req.getExecutable() != null || req.getReadable() != null || req.getWritable() != null) {
				LH.info(log, "User ", req.getInvokedBy(), " chmod: ", awFile, " r=", req.getReadable(), ", w=", req.getWritable(), ", x=", req.getExecutable());
				if (req.getExecutable() != null)
					file.setExecutable(req.getExecutable().booleanValue());
				if (req.getReadable() != null)
					file.setReadable(req.getReadable().booleanValue());
				if (req.getWritable() != null)
					file.setWritable(req.getWritable().booleanValue());
			}
		} catch (Exception e) {
			LH.warning(log, "Error Processing request for ", req.getInvokedBy(), ": ", e);
			res.setException(e);
			res.setOk(false);
		}
		AmiFileMessage sink = nw(AmiFileMessage.class);
		this.manager.toFile(req.getInvokedBy(), sink, awFile, req.getOptions());
		res.setFile(sink);
		res.setOk(true);
		return res;
	}

}
