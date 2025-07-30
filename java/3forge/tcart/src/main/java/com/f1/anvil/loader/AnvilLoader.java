package com.f1.anvil.loader;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientListener;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.TimeOfDay;

public abstract class AnvilLoader implements Runnable, AmiClientListener {
	private static final Logger log = LH.get();

	public static final String MSGTYPE_PARENT_ORDER = "ParentOrder";
	public static final String MSGTYPE_EXECUTION = "Execution";
	public static final String MSGTYPE_CHILD_ACK = "ChildAck";
	public static final String MSGTYPE_CHILD_ORDER = "ChildOrder";
	public static final String MSGTYPE_CHILD_ALERT = "ChildAlert";
	public static final String MSGTYPE_TRADE = "Trade";
	public static final String MSGTYPE_NBBO = "NBBO";
	public static final String MSGTYPE_SEC_MASTER = "SecMaster";
	public static final String MSGTYPE_FXRATE = "FxRate";

	public static final String FIELD_ACCOUNT = "account";
	public static final String FIELD_ALERT_ID = "alertId";
	public static final String FIELD_ASK = "ask";
	public static final String FIELD_ASSIGNED_TO = "assignedTo";
	public static final String FIELD_BID = "bid";
	public static final String FIELD_CL_ORD_ID = "clOrdId";
	public static final String FIELD_COMMENT = "comment";
	public static final String FIELD_COUNTER_CURRENCY = "counterCurrency";
	public static final String FIELD_BASE_CURRENCY = "baseCurrency";
	public static final String FIELD_OPEN_TIME = "openTime";
	public static final String FIELD_DETAILS = "details";
	public static final String FIELD_END_TIME = "endTime";
	public static final String FIELD_BID_EX = "bidEx";
	public static final String FIELD_ASK_EX = "askEx";
	public static final String FIELD_EX = "ex";
	public static final String FIELD_EXEC_INDICATOR = "execIndicator";
	public static final String FIELD_EXEC_ID = "execId";
	public static final String FIELD_HIGH = "high";
	public static final String FIELD_INDUSTRY = "industry";
	public static final String FIELD_BASE_LIMIT_PX = "baseLimitPx";
	public static final String FIELD_LOW = "low";
	public static final String FIELD_MKT_CAP = "mktCap";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_OPEN = "open";
	public static final String FIELD_ORDER_ID = "orderID";
	public static final String FIELD_ORIG_CL_ORD_ID = "origClOrdId";
	public static final String FIELD_O_ID = "oID";
	public static final String FIELD_PARENT_ID = "parentId";
	public static final String FIELD_PREV_CLOSE = "prevClose";
	public static final String FIELD_RATE = "rate";
	public static final String FIELD_SECTOR = "sector";
	public static final String FIELD_SEVERITY = "severity";
	public static final String FIELD_SIDE = "side";
	public static final String FIELD_SIZE = "size";
	public static final String FIELD_START_TIME = "startTime";
	public static final String FIELD_STATUS = "status";
	public static final String FIELD_STRATEGY = "strategy";
	public static final String FIELD_SYMBOL = "symbol";
	public static final String FIELD_SYSTEM = "system";
	public static final String FIELD_TIME = "time";
	public static final String FIELD_PX = "px";
	public static final String FIELD_BASE_PX = "basePx";
	public static final String FIELD_TYPE = "type";
	public static final String FIELD_VALUE = "value";
	public static final String FIELD_VOLUME = "volume";

	public static final char TYPE_PARENT_ORDER = 'O';
	public static final char TYPE_CHILD_ORDER = 'C';
	public static final char TYPE_EXECUTION = 'E';
	public static final char TYPE_CHILD_ACK = 'R';
	public static final char TYPE_NBBO = 'N';
	public static final char TYPE_TRADE = 'T';
	public static final char TYPE_SECMASTER = 'S';
	public static final char TYPE_ALERT = 'A';
	public static final char TYPE_FX_RATE = 'X';

	private static final int DELAY_WHEN_NO_BYTES_TO_READ_MS = 10;

	final protected AnvilFileLoaderManager manager;
	final protected int port;
	final protected String host;
	final protected AmiClient client;
	final private double replaySpeed;
	final private TimeOfDay replaySkipTime;
	final private TimeOfDay replayEndTime;
	final private TimeOfDay replayStartTime;
	final private boolean replayEnabled;
	final private long startTime;
	final protected long logRate;
	final protected int limit;

	public AnvilLoader(AnvilFileLoaderManager manager) {
		this.manager = manager;
		this.port = manager.getPort();
		this.host = "localhost";
		this.client = new AmiClient();
		this.client.addListener(this);
		this.replayEnabled = manager.getReplayEnabled();
		this.replaySpeed = manager.getReplaySpeed();
		this.replaySkipTime = manager.getReplaySkipTime();
		this.replayStartTime = manager.getReplayStartTime();
		this.replayEndTime = manager.getReplayEndTime();
		this.startTime = manager.getStartTime();
		this.logRate = manager.getLogRate();
		this.limit = manager.getLimit();
	}

	@Override
	public void onMessageReceived(AmiClient source, long now, long seqnum, int status, CharSequence message) {
	}

	@Override
	public void onMessageSent(AmiClient source, CharSequence message) {
	}

	@Override
	public void onConnect(AmiClient source) {
	}

	@Override
	public void onDisconnect(AmiClient source) {
	}

	@Override
	public void onCommand(AmiClient source, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params) {
	}
	@Override
	public void onLoggedIn(AmiClient rawAmiClient) {
		LH.info(log, "Connected to AMI server port: localhost:", port);
		Thread t = new Thread(this);
		t.setDaemon(false);
		t.start();
	}

	private boolean firstException = true;

	protected void logException(CharSequence line, int lineNumber, Exception e) {
		LH.warning(log, "Bad record at ", this.getSourceDescription(), ":", lineNumber, " '", line, "' ERROR: ", (e == null ? null : e.getMessage()));
		if (firstException) {
			LH.warning(log, e);
			this.firstException = false;
		}

	}
	public abstract String getSourceDescription();

	protected boolean shouldReplay(long time, char messageType) {
		if (!replayEnabled)
			return true;
		if (messageType == TYPE_SECMASTER)
			return true;
		if (time == -1L)
			return false;
		if (replaySkipTime.isLt(time))
			return messageType != TYPE_NBBO && messageType != TYPE_TRADE;
		if (replayEndTime.isGe(time))
			return false;
		if (replayStartTime.isGe(time)) {
			long ctm = System.currentTimeMillis();
			double delay = (time - replayStartTime.getTimeForToday(time)) / this.replaySpeed - (ctm - startTime);
			if (delay > 20) {
				forceFlush();
			}
			if (delay >= 1d)
				OH.sleep((long) delay);
		}
		return true;
	}
	protected boolean readLine(FastBufferedInputStream reader, StringBuilder line) throws IOException {
		int a = reader.availableBuffer();
		for (;;) {
			int cnt = 0;
			while (a == 0) {
				a = reader.available();
				if (a == 0) {
					client.flush();
					if (!this.hasReadedEof && cnt * DELAY_WHEN_NO_BYTES_TO_READ_MS > 1000 && cnt > 2) {
						this.hasReadedEof = true;//after one second go rounds of no data, let's assume EOF
					} else
						cnt++;
					OH.sleep(DELAY_WHEN_NO_BYTES_TO_READ_MS);
					checkFlushQueue();
				}
			}
			char t = (char) reader.readByte();
			a--;
			if (t == '\r')
				continue;
			if (t == '\n')
				break;
			line.append(t);
		}
		return true;
	}

	protected abstract void checkFlushQueue();
	protected abstract void forceFlush();

	public void startClient() {
		this.client.start(host, port, "test", AmiClient.ENABLE_QUIET);
	}

	volatile private boolean hasReadedEof = false;

	public boolean hasReachedEof() {
		return this.hasReadedEof;
	}

}
