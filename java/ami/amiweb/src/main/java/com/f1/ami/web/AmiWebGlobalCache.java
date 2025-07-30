package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCenterDefinition;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectMessage;
import com.f1.ami.amicommon.centerclient.AmiCenterClientObjectPool;
import com.f1.ami.amicommon.centerclient.AmiCenterClientState;
import com.f1.ami.amicommon.centerclient.AmiCenterClientWithCacheListener;
import com.f1.utils.LH;
import com.f1.utils.LongArrayList;
import com.f1.utils.structs.LongKeyMap;

public class AmiWebGlobalCache implements AmiCenterClientWithCacheListener {
	private static final Logger log = LH.get();
	private int cachedObjects;
	private Map<String, ByType> types = new HashMap<String, ByType>();

	@Override
	public int getObjectsCount() {
		return this.cachedObjects;
	}

	private class ByType {

		final private String type;
		final private LongKeyMap<AmiWebObject_Feed> objects = new LongKeyMap<AmiWebObject_Feed>();
		final private AmiWebObject_FeedPositions positions;
		private AmiCenterClientObjectPool pool;
		final private List<AmiCenterClientObjectMessage> queue = new ArrayList<AmiCenterClientObjectMessage>();
		final private LongArrayList queueSeqnum = new LongArrayList();
		public long snapshotSeqnum = Long.MAX_VALUE;

		private ByType(String type) {
			this.type = type;
			positions = new AmiWebObject_FeedPositions(type);
			this.pool = new AmiCenterClientObjectPool(type);
		}

		public boolean process(long seqnum, AmiCenterClientObjectMessage m) {
			if (seqnum < this.snapshotSeqnum) {
				if (!hasSnapshot()) {//we're waiting for snapshot
					this.queue.add(m);
					this.queueSeqnum.add(seqnum);
				}
				return false;
			}
			process(m);
			return true;

		}

		public void process(AmiCenterClientObjectMessage m) {
			switch (m.getAction()) {
				case AmiCenterClientObjectMessage.ACTION_ADD:
					if (objects.put(m.getId(), new AmiWebObject_Feed(positions, m)) == null)
						cachedObjects++;
					break;
				case AmiCenterClientObjectMessage.ACTION_UPD: {
					AmiWebObject_Feed existing = objects.get(m.getId());
					if (existing != null)
						existing.update(m, null);
					break;
				}
				case AmiCenterClientObjectMessage.ACTION_DEL:
					if (objects.remove(m.getId()) != null)
						cachedObjects--;

					break;
			}
		}

		public boolean hasSnapshot() {
			return this.snapshotSeqnum != Long.MAX_VALUE;
		}

		public void clear() {
			this.objects.clear();
			this.queue.clear();
			this.queueSeqnum.clear();
			this.snapshotSeqnum = Long.MAX_VALUE;
		}

		public void setSnapshotSeqnum(long seqNum) {
			boolean fine = log.isLoggable(Level.FINE);
			if (hasSnapshot()) {
				LH.warning(log, logMe(), " Invalid state, received snapshot seqnum: ", seqNum);
				return;
			}

			this.snapshotSeqnum = seqNum;
			int applied = 0, skipped = 0;
			for (int i = 0; i < queue.size(); i++) {
				long n = queueSeqnum.get(i);
				AmiCenterClientObjectMessage m = queue.get(i);
				if (n > seqNum) {
					applied++;
					if (fine)
						LH.fine(log, logMe(), " Applying queued delta at seqnum ", n, ": ", m.describe());
					process(m);
				} else {
					skipped++;
					if (fine)
						LH.fine(log, logMe(), " Skipping queued delta at seqnum ", n, ": ", m.describe());
				}
			}
			queue.clear();
			this.queueSeqnum.clear();
			LH.info(log, logMe(), " Finished snapshot: ", this.objects.size(), " entries, deltas skipped=", skipped, ", deltas applied=", applied);
		}

		public void toWebCached(List<Object> r) {
			AmiWebObject_FeedPositions newPositions = new AmiWebObject_FeedPositions(this.positions);
			for (AmiWebObject_Feed i : objects.values())
				r.add(i.clone(newPositions));
		}

		public String logMe() {
			return state.getCenterName() + " (seqnum=" + state.getCurrentSeqNum() + ") [" + this.type + "]";
		}

	}

	@Override
	public void onSubscribe(AmiCenterDefinition def, String s) {
		this.types.put(s, new ByType(s));
	}

	@Override
	public void toWebCached(String type, List<Object> sink) {
		this.types.get(type).toWebCached(sink);
	}

	private AmiCenterClientState state;

	@Override
	public void init(AmiCenterClientState state) {
		this.state = state;
	}

	@Override
	public void onCenterMessage(AmiCenterDefinition def, AmiCenterClientObjectMessage m) {
		types.get(m.getTypeName()).process(m);

	}

	@Override
	public void onCenterDisconnect(AmiCenterDefinition center) {
		for (ByType i : this.types.values())
			i.clear();
		this.cachedObjects = 0;
	}

	@Override
	public void onCenterConnect(AmiCenterDefinition center) {
		for (ByType i : this.types.values())
			i.clear();
		this.cachedObjects = 0;
	}

	@Override
	public void onCenterMessageBatchDone(AmiCenterDefinition center) {
	}

	@Override
	public void onUnsubscribe(AmiCenterDefinition def, String s) {
	}
}
