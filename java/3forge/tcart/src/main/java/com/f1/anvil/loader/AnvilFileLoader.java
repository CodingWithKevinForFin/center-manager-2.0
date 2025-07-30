package com.f1.anvil.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.f1.anvil.AnvilMain;
import com.f1.anvil.utils.AnvilMarketData;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.anvil.utils.AnvilMarketDataSymbol;
import com.f1.bootstrap.Bootstrap;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.CharSubSequence;
import com.f1.utils.EH;
import com.f1.utils.FastBufferedInputStream;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.TimeOfDay;

public class AnvilFileLoader extends AnvilLoader {

	private static final Logger log = LH.get();

	public static void main(String[] a, RootPartitionActionRunner pr, AnvilMarketData mdm) throws IOException {
		Bootstrap cb = new Bootstrap(AnvilMain.class, a);
		cb.setConfigDirProperty("./src/main/config");
		AnvilFileLoaderManager.main(cb, pr, mdm);
	}

	final private String filename;
	final private long flushPeriod;
	final private long historySnapshotPeriod;
	final private List<AnvilMarketDataSymbol> buf = new ArrayList<AnvilMarketDataSymbol>();
	final private int maxQueueSize;
	final private AnvilMarketDataMap marketdata;
	final private File file;
	final private String fullPath;

	private long nextFlushTime = 0;
	private long forceFlushNbboTime = 0;
	private long forceFlushTradeTime = 0;
	private boolean hasTrades = false;
	private boolean hasNbbos = false;
	private CharSubSequence sym = new CharSubSequence();
	private CharSubSequence ex = new CharSubSequence();
	private CharSubSequence cur = new CharSubSequence();

	public AnvilFileLoader(AnvilFileLoaderManager manager, String filename) {
		super(manager);
		this.marketdata = manager.getMarketData().createLocalMarketDataMap();
		this.filename = filename;
		this.file = new File(filename);
		this.flushPeriod = manager.getFlushPeriod();
		this.maxQueueSize = manager.getMaxQueueSize();

		this.historySnapshotPeriod = manager.getHistorySnapshotPeriod();

		this.fullPath = IOH.getFullPath(file);
		LH.info(log, "Streaming '", fullPath, "' to anvil server: ", host, ":", port);
		super.startClient();
	}

	@Override
	public void run() {
		FastBufferedInputStream reader = null;
		try {
			reader = new FastBufferedInputStream(new FileInputStream(file), 100000);
			AnvilRecordReader recordReader = new AnvilRecordReader();
			long lastTime = System.currentTimeMillis();
			long lastCount = 0;
			StringBuilder line = new StringBuilder();
			for (int lineNumber = 1;; lineNumber++) {
				line.setLength(0);
				if (!readLine(reader, line))
					break;
				recordReader.reset(line);
				long time = AnvilFileLoaderHelper.handleNanos(recordReader.readLongOr('|', -1L));
				if (time == -1L) {
					logException(line, lineNumber, recordReader.getError());
					continue;
				}
				char messageType = recordReader.readCharOr('|', ' ');
				if (messageType == ' ') {
					logException(line, lineNumber, recordReader.getError());
					continue;
				}
				if (!shouldReplay(time, messageType))
					continue;

				parseLine(messageType, time, recordReader, lineNumber);
				checkFlushQueue();
				if (lineNumber % logRate == 0) {
					long time2 = System.currentTimeMillis();
					LH.info(log, "Read ", lineNumber, " lines from ", fullPath, ", current rate: ", 1000d * (lineNumber - lastCount) / (time2 - lastTime), " messages/second");
					lastTime = time2;
					lastCount = lineNumber;
				}
				if (limit > 0 && lineNumber >= limit)
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			forceFlush();
			IOH.close(reader);
			IOH.close(client);
		}
		LH.info(log, "Done with file: " + fullPath);
	}
	@Override
	protected void forceFlush() {
		try {
			if (hasTrades)
				flushTrades();
			if (hasNbbos)
				flushNbbos();
			this.client.flush();
		} catch (Exception e) {
			LH.warning(log, "Error on force flush: ", e);
		}
	}

	private void parseLine(char messageType, long time, AnvilRecordReader line, int lineNumber) {
		switch (messageType) {
			case TYPE_TRADE:
				onTrade(line, time, lineNumber);
				break;
			case TYPE_NBBO:
				onNbbo(line, time, lineNumber);
				break;
			case TYPE_PARENT_ORDER:
				onOrder(line, time, lineNumber);
				break;
			case TYPE_EXECUTION:
				onExecution(line, time, lineNumber);
				break;
			case TYPE_SECMASTER:
				onSecMaster(line, time, lineNumber);
				break;
			case TYPE_ALERT:
				onAlert(line, time, lineNumber);
				break;
			case TYPE_FX_RATE:
				onFxSpot(line, time, lineNumber);
				break;
			case TYPE_CHILD_ORDER:
				onChild(line, time, lineNumber);
				break;
			case TYPE_CHILD_ACK:
				onReport(line, time, lineNumber);
				break;
		}
	}

	private final StringBuilder fxBuf = new StringBuilder();

	private void onFxSpot(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_FXRATE, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			int start = line.getPosition();
			AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', true);
			int mid = line.getPosition();
			AnvilFileLoaderHelper.sendEnum(client, FIELD_COUNTER_CURRENCY, line, '|', true);
			int end = line.getPosition();
			fxBuf.setLength(0);
			fxBuf.append(line.getBuffer(), start, mid - 1);
			fxBuf.append('.');
			fxBuf.append(line.getBuffer(), mid, end - 1);
			client.addMessageParamEnum("I", fxBuf);
			//AnvilFileLoaderHelper.sendDouble(client, FIELD_RATE, line, '|', false);
			double value = line.readDoubleOr('|', -1d);
			if (line.wasEmpty()) {
				throw new RuntimeException(FIELD_RATE + " is required");
			} else if (line.wasError())
				throw new RuntimeException(FIELD_RATE + " not a double", line.getError());
			client.addMessageParamDouble(FIELD_RATE, value);
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			manager.getMarketData().addFxRate(line.getBuffer().substring(start, mid - 1), line.getBuffer().substring(mid, end - 1), value);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}

	private void onOrder(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_PARENT_ORDER, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			if (!shouldSendSymbol(line, '|')) {
				client.resetMessage();
				return;
			}
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', true);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SIDE, line, '|', true);
			AnvilFileLoaderHelper.sendDouble(client, FIELD_BASE_LIMIT_PX, line, '|', false);
			AnvilFileLoaderHelper.sendInt(client, FIELD_SIZE, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_ORDER_ID, line, '|', true);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_STATUS, line, '|', true);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYSTEM, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_STRATEGY, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_ACCOUNT, line, '|', false);
			AnvilFileLoaderHelper.sendLongTime(client, FIELD_START_TIME, line, '|', false);
			AnvilFileLoaderHelper.sendLongTime(client, FIELD_END_TIME, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', false);
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}

	private boolean shouldSendSymbol(AnvilRecordReader line, char c) {
		if (!this.manager.hasSymbolFilterList())
			return true;
		int start = line.getPosition();
		int end = line.findNext(c);
		sym.reset(line.getBuffer(), start, end);
		if (sym.length() == 0)
			return true;
		return this.manager.shouldProcessSymbol(sym);
	}
	private void onExecution(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_EXECUTION, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			if (!shouldSendSymbol(line, '|')) {
				client.resetMessage();
				return;
			}
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_EX, line, '|', false);
			AnvilFileLoaderHelper.sendDouble(client, FIELD_BASE_PX, line, '|', true);
			AnvilFileLoaderHelper.sendInt(client, FIELD_SIZE, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_O_ID, line, '|', false);
			AnvilFileLoaderHelper.sendString(client, FIELD_PARENT_ID, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SIDE, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_EXEC_INDICATOR, line, '|', false);
			//AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', false);
			//AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			//			line.moveToAndSkipDelim('|');//skip the execId
			//			AnvilFileLoaderHelper.sendEnum(client, FIELD_EXEC_CURRENCY, line, '|', false);
			//			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_EXEC_ID, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', false);
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}
	private void onReport(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_CHILD_ACK, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			if (!shouldSendSymbol(line, '|')) {
				client.resetMessage();
				return;
			}
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_STATUS, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_CL_ORD_ID, line, '|', true);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}
	private void onChild(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_CHILD_ORDER, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			if (!shouldSendSymbol(line, '|')) {
				client.resetMessage();
				return;
			}
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', false);
			AnvilFileLoaderHelper.sendDouble(client, FIELD_BASE_LIMIT_PX, line, '|', false);
			AnvilFileLoaderHelper.sendInt(client, FIELD_SIZE, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_PARENT_ID, line, '|', true);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_STATUS, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_CL_ORD_ID, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_ORIG_CL_ORD_ID, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', false);
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}
	private void onSecMaster(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_SEC_MASTER, null, 0);
			client.addMessageParamLong(FIELD_TIME, (long) time);
			if (!shouldSendSymbol(line, '|')) {
				client.resetMessage();
				return;
			}
			int symStart = line.getPosition();
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', true);
			int symEnd = line.getPosition();
			AnvilFileLoaderHelper.sendEnum(client, FIELD_NAME, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SECTOR, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_INDUSTRY, line, '|', false);
			AnvilFileLoaderHelper.sendDouble(client, FIELD_MKT_CAP, line, '|', false);
			AnvilFileLoaderHelper.sendDouble(client, FIELD_PREV_CLOSE, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_BASE_CURRENCY, line, '|', false);
			int start = line.getPosition();
			boolean hasTime = AnvilFileLoaderHelper.sendString(client, FIELD_OPEN_TIME, line, '|', false);
			int end = line.getPosition();
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			if (hasTime) {
				AnvilMarketDataSymbol mkd = this.marketdata.getMarketData(line.getBuffer().substring(symStart, symEnd - 1));
				mkd.setOpenTime(new TimeOfDay(line.getBuffer().substring(start, end)));
			}
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}
	private void onAlert(AnvilRecordReader line, long time, int lineNumber) {
		try {
			client.startObjectMessage(MSGTYPE_CHILD_ALERT, null, 0);
			client.addMessageParamLong(FIELD_TIME, time);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_TYPE, line, '|', true);
			AnvilFileLoaderHelper.sendInt(client, FIELD_SEVERITY, line, '|', true);
			AnvilFileLoaderHelper.sendString(client, FIELD_DETAILS, line, '|', true);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_ALERT_ID, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_ASSIGNED_TO, line, '|', false);
			AnvilFileLoaderHelper.sendString(client, FIELD_PARENT_ID, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYMBOL, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SYSTEM, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_STRATEGY, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_ACCOUNT, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_SECTOR, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_INDUSTRY, line, '|', false);
			AnvilFileLoaderHelper.sendEnum(client, FIELD_EX, line, '|', false);
			AnvilFileLoaderHelper.sendString(client, FIELD_COMMENT, line, '|', false);
			AnvilFileLoaderHelper.sendRemaining(client, line, '|', false);
			client.sendMessage();
		} catch (Exception e) {
			client.resetMessage();
			logException(line.getBuffer(), lineNumber, e);
		}
	}

	private void onNbbo(AnvilRecordReader line, long time, int lineNumber) {
		try {
			if (!hasNbbos) {
				if (manager.setNbboFileName(this.filename))
					hasNbbos = true;
				else {
					LH.warning(log, "CAN NOT HAVE MULTIPLE FILES WITH NBBOS: " + this.filename + " and ", manager.getNbboFileName());
					return;
				}
			}
			AnvilFileLoaderHelper.readString(this.sym, FIELD_SYMBOL, line, '|', true);
			if (!this.manager.shouldProcessSymbol(sym))
				return;
			AnvilFileLoaderHelper.readString(this.ex, FIELD_EX, line, '|', false);
			float bidPx = line.readFloatOr('|', Float.NaN);
			float bidSize = line.readFloatOr('|', Float.NaN);
			float askPx = line.readFloatOr('|', Float.NaN);
			float askSize = line.readFloatOr('|', Float.NaN);
			AnvilFileLoaderHelper.readString(this.cur, FIELD_BASE_CURRENCY, line, '|', false);
			if (bidPx != bidPx || askPx != askPx) {
				LH.warning(log, "Bad record at ", this.filename, ":", lineNumber, " '", line, "' ERROR: bidPx and askPx required");
				return;
			}
			if (time >= forceFlushNbboTime) {
				flushNbbos();
				forceFlushNbboTime = (time / historySnapshotPeriod) * historySnapshotPeriod + historySnapshotPeriod;
			}
			marketdata.getMarketData(sym).addNbbo(time, ex, bidPx, askPx, cur);
		} catch (Exception e) {
			logException(line.getBuffer(), lineNumber, e);
		}
	}

	private void onTrade(AnvilRecordReader line, long time, int lineNumber) {
		try {
			if (!hasTrades) {
				if (manager.setTradesFileName(this.filename))
					hasTrades = true;
				else {
					LH.warning(log, "CAN NOT HAVE MULTIPLE FILES WITH TRADES: " + this.filename + " and ", manager.getTradesFileName());
					return;
				}
			}
			AnvilFileLoaderHelper.readString(this.sym, FIELD_SYMBOL, line, '|', true);
			if (!this.manager.shouldProcessSymbol(sym))
				return;
			AnvilFileLoaderHelper.readString(this.ex, FIELD_EX, line, '|', false);
			float px = line.readFloatOr('|', Float.NaN);
			int size = line.readIntOr('|', Integer.MIN_VALUE);
			AnvilFileLoaderHelper.readString(this.cur, FIELD_BASE_CURRENCY, line, '|', false);
			if (px != px || size == Integer.MIN_VALUE) {
				LH.warning(log, "Bad record at ", this.filename, ":", lineNumber, " '", line, "' ERROR: px or size is bad");
				return;
			}

			if (time >= forceFlushTradeTime) {
				flushTrades();
				forceFlushTradeTime = (time / historySnapshotPeriod) * historySnapshotPeriod + historySnapshotPeriod;
			}
			this.marketdata.getMarketData(sym).addTrade(time, ex, size, px, cur);
		} catch (Exception e) {
			logException(line.getBuffer(), lineNumber, e);
		}
	}

	@Override
	protected void checkFlushQueue() {
		if (!hasNbbos && !hasTrades)
			return;
		long now = EH.currentTimeMillis();
		if (now < nextFlushTime)
			return;
		if (manager.getAnvilMainQueueSize() < maxQueueSize) {
			if (hasTrades)
				flushTrades();
			if (hasNbbos)
				flushNbbos();
		}
		nextFlushTime = now + flushPeriod;
	}
	private void flushNbbos() {
		this.manager.getMarketData().getNbbos(buf);
		for (int i = 0; i < buf.size(); i++) {
			AnvilMarketDataSymbol md = buf.get(i);
			client.startObjectMessage(MSGTYPE_NBBO, null, 0);
			client.addMessageParamLong(FIELD_TIME, md.getCurrentNbboTime());
			client.addMessageParamEnum(FIELD_SYMBOL, md.getSymbol());
			client.addMessageParamEnum(FIELD_BID_EX, md.getCurrentNbboBidEx());
			client.addMessageParamEnum(FIELD_ASK_EX, md.getCurrentNbboAskEx());
			client.addMessageParamFloat(FIELD_ASK, md.getCurrentNbboAskPx());
			client.addMessageParamFloat(FIELD_BID, md.getCurrentNbboBidPx());
			client.sendMessage();
		}
		if (buf.isEmpty())
			client.flush();
		buf.clear();
	}

	private void flushTrades() {
		this.manager.getMarketData().getTrades(buf);
		for (int i = 0; i < buf.size(); i++) {
			AnvilMarketDataSymbol md = buf.get(i);
			client.startObjectMessage(MSGTYPE_TRADE, null, 0);
			client.addMessageParamLong(FIELD_TIME, md.getCurrentTradeTime());
			client.addMessageParamEnum(FIELD_SYMBOL, md.getSymbol());
			client.addMessageParamEnum(FIELD_EX, md.getCurrentTradeEx());
			client.addMessageParamFloat(FIELD_PX, md.getCurrentTradePx());
			client.addMessageParamInt(FIELD_SIZE, md.getCurrentTradeSize());
			client.addMessageParamFloat(FIELD_HIGH, md.getCurrentTradeHigh());
			client.addMessageParamFloat(FIELD_LOW, md.getCurrentTradeLow());
			client.addMessageParamLong(FIELD_VOLUME, md.getCurrentTradeVolume());
			client.addMessageParamDouble(FIELD_VALUE, md.getCurrentTradeValue());
			float open = md.getCurrentTradeOpen();
			if (open == open)
				client.addMessageParamFloat(FIELD_OPEN, open);
			client.sendMessage();
			md.getCurrentTradeTime();
		}
		if (buf.isEmpty())
			client.flush();
		buf.clear();

	}

	@Override
	public String getSourceDescription() {
		return this.filename;
	}
}
