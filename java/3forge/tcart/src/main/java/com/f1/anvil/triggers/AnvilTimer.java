package com.f1.anvil.triggers;

import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.f1.ami.center.table.AmiCenterProcess;
import com.f1.ami.center.table.AmiColumn;
import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.timers.AmiAbstractTimer;
import com.f1.ami.center.timers.AmiTimerBinding;
import com.f1.anvil.utils.AnvilMarketData;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.anvil.utils.AnvilTableCopier;
import com.f1.anvil.utils.AnvilTaqPeriodQueuesManager;
import com.f1.anvil.utils.AnvilTimedEvent;
import com.f1.base.Generator;
import com.f1.container.Container;
import com.f1.container.ContainerTools;
import com.f1.container.DispatchController;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.Scheduler;
import com.f1.utils.concurrent.ObjectPoolForClearable;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple3;

final public class AnvilTimer extends AmiAbstractTimer {
	public static final String ALERT_OVERFILL = "Overfill";
	public static final String ALERT_NBBO_REGULATION = "NBBO Regulation";
	public static final String ALERT_T1 = "T+1";
	public static final String ALERT_T5 = "T+5";

	public static final String ALERT_ADV = "ADV";
	public static final String ALERT_NOTIONAL_PER_CLIENT = "NOTIONAL_PER_CLIENT";
	public static final String ALERT_NOTIONAL_PER_CHILDORDER = "NOTIONAL_PER_CHILDORDER";
	public static final String ALERT_NOTIONAL_FARSIDE = "FARSIDE";
	public static final String ALERT_LAST_TRADED = "LAST_TRADED";
	public static final String ALERT_MESSAGES = "MESSAGES";

	public static final String ALERT_EX_ADV = "EX_ADV";
	public static final String ALERT_EX_NOTIONAL_PER_CLIENT = "EX_NOTIONAL_PER_CLIENT";
	public static final String ALERT_EX_NOTIONAL_PER_CHILDORDER = "EX_NOTIONAL_PER_CHILDORDER";
	public static final String ALERT_EX_NOTIONAL_FARSIDE = "EX_FARSIDE";
	public static final String ALERT_EX_LAST_TRADED = "EX_LAST_TRADED";
	public static final String ALERT_EX_MESSAGES = "EX_MESSAGES";

	public static final String ALERT_MARKET_MOVED = "MARKET_MOVED";
	public static final String ALERT_MISHIT_TRANS_PCT = "MISHIT_TRANS_PCT";
	public static final String ALERT_MISHIT_TRANS_ABS = "MISHIT_TRANS_ABS";
	public static final String ALERT_MISHIT_BBO_PCT = "MISHIT_BBO_PCT";
	public static final String ALERT_MISHIT_BBO_ABS = "MISHIT_BBO_ABS";
	public static final double THRESHOLD_ADV = 0.10;
	public static final double THRESHOLD_NOTIONAL_PER_CLIENT = 1e7;
	public static final double THRESHOLD_NOTIONAL_PER_CHILDORDER = 1e5;
	public static final double THRESHOLD_FARSIDE = 0.10;
	public static final double THRESHOLD_LAST_TRADED = 0.05;
	public static final double THRESHOLD_MESSAGES = 5;
	public static final double THRESHOLD_EX_MESSAGES = 20;
	public static final double THRESHOLD_MARKET_MOVED = 0.05;
	public static final double THRESHOLD_MISHIT_PCT = 0.0050;
	public static final double THRESHOLD_MISHIT_ABS = 0.20;
	public static final double ALERT_MESSAGE_TIMEFRAME = 60000d;

	private static final long NANOS_PER_ML = 1000000;
	private Scheduler<AnvilTimedEvent> timerQueue;
	private AnvilMarketDataMap marketData;
	private AnvilMarketData marketDataInstance;
	//	private List<AnvilTableCopier> copiers = new ArrayList<AnvilTableCopier>();

	private AnvilSchema schema;

	private AnvilTaqPeriodQueuesManager queueManager = new AnvilTaqPeriodQueuesManager(60000);
	private Container container;
	private RootPartitionActionRunner partitionRunner;
	private AmiImdb imdb;
	private AnvilTableCopier statsCopier;
	private HashSet<String> darkVenues;
	public long childAlertId = 1000;
	private AnvilTriggerTimer timerTrigger;
	private AmiPreparedRow childAlertPreparedRow;
	private AmiPreparedRow preparedStatsRow;
	private String defaultCurrency;

	//private AmiPreparedQuery fxSpotQuery;
	//	private AmiPreparedQueryCompareClause fxSpotQuery_currency;
	//	private AmiPreparedQueryCompareClause fxSpotQuery_currency2;

	//	@Override
	//	public void init(ContainerTools tools, PropertyController props) {
	//	}

	public String getDefaultCurrency() {
		return this.defaultCurrency;
	}
	public boolean isVenueLit(String venue) {
		return !this.darkVenues.contains(venue);
	}

	public AnvilTaqPeriodQueuesManager getTaqQueueManager() {
		return this.queueManager;
	}

	@Override
	public void startup(AmiImdb imdb, AmiTimerBinding binding, AmiImdbSession session) {
		super.startup(imdb, binding, session);
		ContainerTools tools = imdb.getTools();
		this.timerQueue = new Scheduler<AnvilTimedEvent>();
		this.marketDataInstance = (AnvilMarketData) tools.getContainer().getServices().getService(AnvilMarketData.SERVICE_NAME);
		this.marketData = this.marketDataInstance.createLocalMarketDataMap();
		//this.marketDataInstance = AnvilMarketData.INSTANCE;
		this.container = tools.getContainer();
		this.captureStatsPeriodNanos = tools.getOptional("stats.period.ms", 1000) * NANOS_PER_ML;
		this.darkVenues = new HashSet<String>();
		this.darkVenues.addAll(SH.splitToSet(",", tools.getOptional("anvil.dark.exchanges", "")));
		this.defaultCurrency = tools.getOptional("anvil.defualt.currency", "USD");
		this.schema = new AnvilSchema(imdb);
		this.childAlertPreparedRow = schema.childAlertsTable.createAmiPreparedRow();
		this.preparedStatsRow = schema.statsTable.createAmiPreparedRow();
		//		this.timerTrigger = imdb.getAmiTimerOrThrow("timerTrigger", AnvilTriggerTimer.class);//TODO remove
		//this.fxSpotQuery = schema.fxSpotTable.createAmiPreparedQuery();
		//		this.fxSpotQuery_currency = this.fxSpotQuery.addCompare(schema.fxSpotTable_currency, AmiPreparedQueryCompareClause.EQ);
		//		this.fxSpotQuery_currency2 = this.fxSpotQuery.addCompare(schema.fxSpotTable_currency2, AmiPreparedQueryCompareClause.EQ);
		schema.statsTable.insertAmiRow(schema.statsTable.createAmiPreparedRow(), session);
		statsCopier = new AnvilTableCopier(imdb, "Stats", "StatsHistory", "time");
		DispatchController dc = container.getDispatchController();
		this.partitionRunner = dc.getRootPartitionRunner("AMI_CENTER");
		if (this.partitionRunner == null)
			throw new NoSuchElementException("partiiton not found: AMI_CENTER");
		imdb.registerTimerFromNow(this.getBinding().getTimerName(), -captureStatsPeriodNanos / NANOS_PER_ML, TimeUnit.MILLISECONDS);
		this.imdb = imdb;
		//LH.info(log, this.fxSpotQuery);
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersBySide", "OrdersBySideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersBySystemSide", "OrdersBySystemSideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersBySymSide", "OrdersBySymSideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersByAccountSide", "OrdersByAccountSideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersBySecIndSide", "OrdersBySecIndSideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "OrdersByStrategySide", "OrdersByStrategySideHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "Symbol", "SymbolHistory", "time"));
		//		copiers.add(new AnvilTableCopier(imdb, "ParentAlerts", "ParentAlertsHistory", "time"));
	}

	public void resetTimers() {
		imdb.getAmiTimerOrThrow("timerTrigger", AnvilTriggerTimer.class).resetTimers();
	}

	public Scheduler<AnvilTimedEvent> getTimerQueue() {
		return timerQueue;
	}

	private ObjectPoolForClearable<AnvilTimedEvent> timedEventPool = new ObjectPoolForClearable<AnvilTimedEvent>(new Generator<AnvilTimedEvent>() {
		@Override
		public AnvilTimedEvent nw() {
			return new AnvilTimedEvent();
		}
	}, 10000);

	public ObjectPoolForClearable<AnvilTimedEvent> getTimedEventPool() {
		return this.timedEventPool;
	}
	public AnvilMarketDataMap getMarketData() {
		return this.marketData;
	}

	public AnvilMarketData getMarketDataInstance() {
		return marketDataInstance;
	}

	private long statCount_OrdersProcessed;
	private long statNanos_OrdersProcessed;
	private long statCount_OrdersUpdated;
	private long statCount_ExecsProcessed;
	private long statNanos_ExecsProcessed;
	private long statCount_TaqsProcessed;
	private long statNanos_TaqsProcessed;
	private long statCount_TimersProcessed;
	private long statNanos_TimersProcessed;
	private long statCount_HistoriesProcessed;
	private long statNanos_HistoriesProcessed;
	private long statCount_TradesReceived;
	private long statCount_NbbosReceived;
	private long statCount_OrdersOpened;
	private long statCount_OrdersClosed;
	private long statCount_ChildOrdersProcessed;
	private long statNanos_ChildOrdersProcessed;
	private long statCount_ChildModifiesProcessed;
	private long statNanos_ChildModifiesProcessed;

	public void incrementStatsForChildMessageProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_ChildOrdersProcessed += count;
		this.statNanos_ChildOrdersProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForChildModifyProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_ChildModifiesProcessed += count;
		this.statNanos_ChildModifiesProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForOrderProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_OrdersProcessed += count;
		this.statNanos_OrdersProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForExecProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_ExecsProcessed += count;
		this.statNanos_ExecsProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForTaqsProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_TaqsProcessed += count;
		this.statNanos_TaqsProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForTimersProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_TimersProcessed += count;
		this.statNanos_TimersProcessed += end - start;
		captureStatsIfReady(end, session);
	}
	public void incrementStatsForHistoryProcessed(int count, long start, long end, AmiImdbSession session) {
		this.statCount_HistoriesProcessed += count;
		this.statNanos_HistoriesProcessed += end - start;
		captureStatsIfReady(end, session);
	}

	public void incrementStatsForTradeReceived(int count, AmiImdbSession session) {
		this.statCount_TradesReceived += count;
		captureStatsIfReady(session);
	}

	public void incrementStatsForNbbosReceived(int count, AmiImdbSession session) {
		this.statCount_NbbosReceived += count;
		captureStatsIfReady(session);
	}
	public void incrementStatsForOrdersUpdated(int count, AmiImdbSession session) {
		this.statCount_OrdersUpdated += count;
		captureStatsIfReady(session);
	}
	public void incrementStatsForOrderOpened(int count) {
		this.statCount_OrdersOpened += count;
	}
	public void incrementStatsForOrderClosed(int count) {
		this.statCount_OrdersClosed += count;
	}

	private long capturedStatsNextTimeNanos = 0;
	private long captureStatsPeriodNanos = 1000 * NANOS_PER_ML;
	private boolean statsChangedSinceCapture = false;

	private void captureStatsIfReady(AmiImdbSession session) {
		captureStatsIfReady(System.nanoTime(), session);
	}

	private void captureStatsIfReady(long nowNannos, AmiImdbSession session) {
		statsChangedSinceCapture = true;
		if (nowNannos >= capturedStatsNextTimeNanos)
			captureStatsNow(nowNannos, session);
	}

	private final StringBuilder buf = new StringBuilder();
	private static final Logger log = LH.get(AnvilTimer.class);
	public static final String SERVICE_NAME = "anvilService";

	private void captureStatsNow(long nowNannos, AmiImdbSession session) {
		this.capturedStatsNextTimeNanos = (nowNannos / captureStatsPeriodNanos) * captureStatsPeriodNanos + captureStatsPeriodNanos;
		this.statsChangedSinceCapture = false;
		LH.info(log, "Stats: ", getStats(SH.clear(buf), nowNannos, session));
	}
	public void captureStatsIfChanged(long nowNannos, AmiImdbSession session) {
		if (statsChangedSinceCapture)
			if (nowNannos >= capturedStatsNextTimeNanos)
				captureStatsNow(nowNannos, session);
	}
	@Override
	public boolean onTimer(long timerId, AmiImdbSession session, AmiCenterProcess process) {
		try {
			session.lock(process, null);
			long nowNanos = System.nanoTime();
			captureStatsIfChanged(nowNanos, session);
			imdb.registerTimerFromNow(this.getBinding().getTimerName(), -Math.max(50L, (capturedStatsNextTimeNanos - nowNanos) / NANOS_PER_ML), TimeUnit.MILLISECONDS);
			return true;
		} finally {
			session.unlock();
		}
		//		imdb.registerTimer(this, -Math.max(50L, (capturedStatsNextTimeNanos - nowNanos) / NANOS_PER_ML), null);
	}

	private StringBuilder getStats(StringBuilder sb, long nowNannos, AmiImdbSession session) {
		AmiRow row = schema.statsTable.getAmiRowAt(0);
		final long cNbbo = marketDataInstance.getCurrentNbbosTime();
		final long cTrade = marketDataInstance.getCurrentTradesTime();
		final long nbboCount = marketDataInstance.getNbboCount();
		final long tradeCount = marketDataInstance.getTradeCount();
		final long oNbbo = queueManager.getOldestNbboTime();
		final long oTrade = queueManager.getOldestTradeTime();
		final long fm = EH.getFreeMemory();
		final long mm = EH.getMaxMemory();
		final long tm = EH.getTotalMemory();

		this.preparedStatsRow.reset();
		preparedStatsRow.setLong(schema.statsTable_currentTime, nowNannos / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_ordersCnt, statCount_OrdersProcessed);
		preparedStatsRow.setLong(schema.statsTable_ordersOpenedCnt, statCount_OrdersOpened);
		preparedStatsRow.setLong(schema.statsTable_ordersClosedCnt, statCount_OrdersClosed);
		preparedStatsRow.setLong(schema.statsTable_ordersMs, statNanos_OrdersProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_execsCnt, statCount_ExecsProcessed);
		preparedStatsRow.setLong(schema.statsTable_execsMs, statNanos_ExecsProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_timersCnt, statCount_TimersProcessed);
		preparedStatsRow.setLong(schema.statsTable_timersOnOrdersCnt, statCount_OrdersUpdated);
		preparedStatsRow.setLong(schema.statsTable_timersMs, statNanos_TimersProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_taqsCnt, statCount_TaqsProcessed);
		preparedStatsRow.setLong(schema.statsTable_taqsMs, statNanos_TaqsProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_historiesCnt, statCount_HistoriesProcessed);
		preparedStatsRow.setLong(schema.statsTable_historiesMs, statNanos_HistoriesProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_rawNbbosCnt, nbboCount);
		preparedStatsRow.setLong(schema.statsTable_rawNbbosMaxMs, cNbbo);
		preparedStatsRow.setLong(schema.statsTable_rawTradesCnt, tradeCount);
		preparedStatsRow.setLong(schema.statsTable_rawTradesMaxMs, cTrade);
		preparedStatsRow.setLong(schema.statsTable_nbbosCnt, statCount_NbbosReceived);
		preparedStatsRow.setLong(schema.statsTable_nbbosMaxMs, queueManager.getNewestNbboTime());
		preparedStatsRow.setLong(schema.statsTable_nbbosMinMs, queueManager.getOldestNbboTime());
		preparedStatsRow.setLong(schema.statsTable_tradesCnt, statCount_TradesReceived);
		preparedStatsRow.setLong(schema.statsTable_tradesMaxMs, queueManager.getNewestTradeTime());
		preparedStatsRow.setLong(schema.statsTable_tradesMinMs, queueManager.getOldestTradeTime());
		preparedStatsRow.setLong(schema.statsTable_queueSize, getQueueSize());
		preparedStatsRow.setLong(schema.statsTable_queueHpSize, getHighPriorityQueueSize());
		preparedStatsRow.setLong(schema.statsTable_memFree, fm);
		preparedStatsRow.setLong(schema.statsTable_memTot, tm);
		preparedStatsRow.setLong(schema.statsTable_memMax, mm);
		preparedStatsRow.setLong(schema.statsTable_childOrdersMs, statNanos_ChildOrdersProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_childModifiesMs, statNanos_ChildModifiesProcessed / NANOS_PER_ML);
		preparedStatsRow.setLong(schema.statsTable_childOrdersCnt, statCount_ChildOrdersProcessed);
		preparedStatsRow.setLong(schema.statsTable_childModifiesCnt, statCount_ChildModifiesProcessed);
		schema.statsTable.updateAmiRow(row.getAmiId(), preparedStatsRow, session);
		statsCopier.copy(EH.currentTimeMillis(), session);
		//((AmiTableImpl) schema.statsTable).broadcastPendingChanges();
		//((AmiTableImpl) this.ordersBySideTable).broadcastPendingChanges();
		//((AmiImdbImpl) this.imdb).getState().sendPendingChangesToClients();

		sb.append("Or=").append(statCount_OrdersProcessed).append('/').append(statNanos_OrdersProcessed / NANOS_PER_ML);
		sb.append(",Co=").append(statCount_ChildOrdersProcessed).append('/').append(statNanos_ChildOrdersProcessed / NANOS_PER_ML);
		sb.append(",Cm=").append(statCount_ChildModifiesProcessed).append('/').append(statNanos_ChildModifiesProcessed / NANOS_PER_ML);
		sb.append(",Ex=").append(statCount_ExecsProcessed).append('/').append(statNanos_ExecsProcessed / NANOS_PER_ML);
		sb.append(",Tm=").append(statCount_TimersProcessed).append('+').append(statCount_OrdersUpdated).append('/').append(statNanos_TimersProcessed / NANOS_PER_ML);
		sb.append(",Tq=").append(statCount_TaqsProcessed).append('/').append(statNanos_TaqsProcessed / NANOS_PER_ML);
		sb.append(",Hs=").append(statCount_HistoriesProcessed).append('/').append(statNanos_HistoriesProcessed / NANOS_PER_ML);
		sb.append(",Nb=").append(nbboCount).append('@').append(cNbbo);
		sb.append(",Tr=").append(tradeCount).append('@').append(cTrade);
		sb.append(",CfNb=").append(statCount_NbbosReceived).append('@').append(oNbbo).append('-').append(queueManager.getOldestNbboTime());
		sb.append(",CfTr=").append(statCount_TradesReceived).append('@').append(oTrade).append('-').append(queueManager.getOldestTradeTime());
		sb.append(",OrOp=").append(statCount_OrdersOpened - statCount_OrdersClosed);
		sb.append(",Qu=").append(getQueueSize()).append('+').append(getHighPriorityQueueSize());
		sb.append(",Dl=").append(Math.min(cNbbo, cTrade) - Math.min(oNbbo, oTrade));
		return sb;
	}
	public long getQueueSize() {
		return this.partitionRunner.getQueueSize();
	}

	public long getHighPriorityQueueSize() {
		return this.partitionRunner.getHighPriorityQueueSize();
	}
	public AnvilSchema getSchema() {
		return this.schema;
	}

	//	public int copyTablesToHistory(long horizonTime, AmiImdbSession session) {
	//		int r = 0;
	//		for (int i = 0; i < copiers.size(); i++)
	//			r += copiers.get(i).copy(horizonTime, session);
	//		return r;
	//	}

	private Set<Tuple3> existing = new HashSet();

	public void addAlert(String type, String parentOrderId, String sector, String industry, String account, String symbol, String system, long exTime, long severity,
			String comment, String details, String exchange, String assignedTo, Map<String, Object> mapParams, AmiImdbSession session) {
		String params = mapParams == null ? "" : ObjectToJsonConverter.INSTANCE_COMPACT.objectToString(mapParams);
		if (!existing.add(new Tuple3(type, parentOrderId, params)))
			return;
		childAlertPreparedRow.reset();
		childAlertPreparedRow.setString(schema.childAlertsTable_type, type);
		childAlertPreparedRow.setLong(schema.childAlertsTable_id, childAlertId++);
		childAlertPreparedRow.setString(schema.childAlertsTable_orderId, parentOrderId);
		//if (sector != null && industry != null) {
		childAlertPreparedRow.setString(schema.childAlertsTable_sector, sector);
		childAlertPreparedRow.setString(schema.childAlertsTable_industry, industry);
		//}
		childAlertPreparedRow.setString(schema.childAlertsTable_account, account);
		childAlertPreparedRow.setString(schema.childAlertsTable_sym, symbol);
		childAlertPreparedRow.setString(schema.childAlertsTable_system, system);
		childAlertPreparedRow.setLong(schema.childAlertsTable_time, exTime);
		childAlertPreparedRow.setLong(schema.childAlertsTable_severity, severity);
		childAlertPreparedRow.setString(schema.childAlertsTable_comment, comment);
		childAlertPreparedRow.setString(schema.childAlertsTable_details, details);
		childAlertPreparedRow.setString(schema.childAlertsTable_exchange, exchange);
		childAlertPreparedRow.setString(schema.childAlertsTable_assignedTo, assignedTo);

		childAlertPreparedRow.setString(schema.childAlertsTable_params, params);
		//		schema.childAlertsTable.fireTriggerInsert(childAlertPreparedRow);
		schema.childAlertsTable.insertAmiRow(childAlertPreparedRow, session);

	}

	public double getFxRate(CharSequence exCurrency) {
		return this.marketDataInstance.getFxSpotRate(exCurrency);
		//		if (this.defaultCurrency.equals(exCurrency))
		//			return 1d;
		//		fxSpotQuery_currency.setValue(exCurrency);
		//		fxSpotQuery_currency2.setValue(exCurrency);
		//		AmiRow row = schema.fxSpotTable.query(fxSpotQuery);
		//		if (row == null)
		//			return 1d;
		//		return row.getDouble(schema.fxSpotTable_rate);

	}

	public void resolveCurrency(AmiRow row, AmiPreparedRow preparedRow, AmiColumn baseCurrency, AmiColumn basePx, AmiColumn px) {
		final double exPx = row.getDouble(basePx);
		if (row.getIsNull(baseCurrency)) {
			preparedRow.setString(baseCurrency, getDefaultCurrency());
			preparedRow.setDouble(px, exPx);
		} else {
			String exCurrency = row.getString(baseCurrency);
			double rate = getFxRate(exCurrency);
			preparedRow.setDouble(px, exPx * rate);
		}

	}
	@Override
	protected void onStartup(AmiImdbSession session) {
		// TODO Auto-generated method stub

	}

}
