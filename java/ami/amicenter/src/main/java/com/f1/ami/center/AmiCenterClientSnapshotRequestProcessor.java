package com.f1.ami.center;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterChanges;
import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetSnapshotResponse;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiRowImpl;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.base.Row;
import com.f1.container.RequestMessage;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.exceptions.ContainerException;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.mutable.Mutable;
import com.f1.utils.mutable.Mutable.Int;

public class AmiCenterClientSnapshotRequestProcessor extends AmiCenterRequestProcessor<AmiCenterGetSnapshotRequest, AmiCenterState, AmiCenterGetSnapshotResponse> {

	public AmiCenterClientSnapshotRequestProcessor() {
		super(AmiCenterGetSnapshotRequest.class, AmiCenterState.class, AmiCenterGetSnapshotResponse.class);
	}

	@Override
	protected AmiCenterGetSnapshotResponse processRequest(RequestMessage<AmiCenterGetSnapshotRequest> action, AmiCenterState state, ThreadScope threadScope) throws Exception {
		state.incrementAmiMessageStats(AmiCenterState.STATUS_TYPE_GET_SNAPSHOT);
		AmiCenterGetSnapshotRequest request = action.getAction();
		state.onProcessedEventsComplete();//makes sure we don't have any pending data to send(AKA snapshot needs to be directly after update)

		AmiImdbImpl db = state.getAmiImdb();
		String invokedBy = request.getInvokedBy();
		if (SH.isnt(invokedBy)) {
			throw new ContainerException("Received snapshotrequest from unknown user");
		}
		LH.info(log, "Received Snapshot Request From: ", invokedBy, " for: ", request.getAmiObjectTypesToSend());
		List<Row> rows = new ArrayList<Row>();
		for (String typeName : request.getAmiObjectTypesToSend()) {
			AmiTableImpl table = db.getAmiTable(typeName);
			if (table != null)
				table.getBroadcastableRows(rows);

		}
		Mutable.Int responseNum = new Mutable.Int(0);
		long seqNum = state.currentSequenceNumber();
		LH.info(log, "Returning Snapshot to: ", invokedBy, " at seqnum ", seqNum, ". AmiDb Counts: ", rows.size());
		final int totalObjects = rows.size();

		ResultMessage<AmiCenterGetSnapshotResponse> nextResponse = null;
		if (request.getIncludeStringPool())
			nextResponse = createStringPoolResponse(totalObjects, seqNum, state.getAmiValuesStringPoolAsBytes());

		int maxBatchSize = Math.max(10000, request.getMaxBatchSize());
		if (rows.size() > 0)
			for (List<Row> i : CH.batchSublists(rows, maxBatchSize, true))
				nextResponse = replyIfNotNull(action, nextResponse, createTableResponse(totalObjects, seqNum, i), responseNum);

		if (nextResponse == null) {
			final AmiCenterChanges changes = nw(AmiCenterChanges.class);
			nextResponse = createIntermediateResponse(changes, seqNum);
		}

		nextResponse.setIsIntermediateResult(false);
		nextResponse.getAction().getSnapshot().setResponseNum(responseNum.value++);
		reply(action, nextResponse, threadScope);
		return null;
	}

	private ResultMessage<AmiCenterGetSnapshotResponse> replyIfNotNull(RequestMessage<AmiCenterGetSnapshotRequest> action, ResultMessage<AmiCenterGetSnapshotResponse> response,
			ResultMessage<AmiCenterGetSnapshotResponse> response2, Int responseNum) {
		if (response == null)
			return response2;
		if (response2 == null)
			return response;
		response.getAction().getSnapshot().setResponseNum(responseNum.value++);
		reply(action, response, null);
		return response2;
	}

	private ResultMessage<AmiCenterGetSnapshotResponse> createTableResponse(int totalObject, long seqNum, List<Row> amiEntities) {
		final AmiCenterChanges changes = nw(AmiCenterChanges.class);
		if (amiEntities == null)
			return null;
		FastByteArrayDataOutputStream buf = new FastByteArrayDataOutputStream();
		for (Row i : amiEntities) {
			((AmiRowImpl) i).writeEntity(buf);
		}
		changes.setAmiEntitiesAdded(buf.toByteArray());
		return createIntermediateResponse(changes, seqNum);
	}
	private ResultMessage<AmiCenterGetSnapshotResponse> createStringPoolResponse(int totalObject, long seqNum, byte[] amiValuesMap) {
		final AmiCenterChanges changes = nw(AmiCenterChanges.class);
		if (amiValuesMap != null)
			changes.setAmiValuesStringPoolMap(amiValuesMap);
		return createIntermediateResponse(changes, seqNum);
	}
	private ResultMessage<AmiCenterGetSnapshotResponse> createIntermediateResponse(AmiCenterChanges changes, long seqNum) {
		changes.setEyeProcessUid(EH.getProcessUid());
		changes.setSeqNum(seqNum);
		AmiCenterGetSnapshotResponse tmpResponse = nw(AmiCenterGetSnapshotResponse.class);
		tmpResponse.setSnapshot(changes);
		ResultMessage<AmiCenterGetSnapshotResponse> tmp = nw(ResultMessage.class);
		tmp.setAction(tmpResponse);
		tmp.setIsIntermediateResult(true);
		tmpResponse.setOk(true);
		tmpResponse.setProcessUid(EH.getProcessUid());
		return tmp;
	}
}
