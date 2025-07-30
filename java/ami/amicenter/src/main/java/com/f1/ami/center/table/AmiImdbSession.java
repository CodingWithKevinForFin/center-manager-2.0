package com.f1.ami.center.table;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amiscript.AmiDebugManager;
import com.f1.ami.amiscript.AmiService;
import com.f1.ami.center.AmiCenterState;
import com.f1.ami.center.AmiCenterUtils;
import com.f1.ami.center.hdb.AmiHdb;
import com.f1.ami.center.hdb.AmiHdbTableRep;
import com.f1.base.CalcFrame;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Table;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellTimeoutController;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.SqlResultset;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiImdbSession implements Tableset, AmiService {

	public static final long TEMP_SESSION = -1L;

	private static final Logger log = LH.get();

	public static byte PERMISSIONS_READ = AmiCenterQueryDsRequest.PERMISSIONS_READ;
	public static byte PERMISSIONS_WRITE = AmiCenterQueryDsRequest.PERMISSIONS_WRITE;
	public static byte PERMISSIONS_ALTER = AmiCenterQueryDsRequest.PERMISSIONS_ALTER;
	public static byte PERMISSIONS_EXECUTE = AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE;
	public static final byte PERMISSIONS_FULL = AmiCenterQueryDsRequest.PERMISSIONS_FULL;

	final private AmiImdbImpl imdb;
	private Tableset local = null;
	final private long sessionId;
	private long lastUsedTime;
	//	private List<Table> returnTables = new ArrayList<Table>();
	//	private List<Object> generatedKeys = new ArrayList<Object>();
	//	private long rowsEffected = -1L;
	final private byte definedBy;
	final private String username;
	final private long createdTime;
	private MutableCalcFrame globalConsts = new MutableCalcFrame();
	private MutableCalcFrame writeableValues = new MutableCalcFrame();
	private final TopCalcFrameStack topStackFrame;
	private boolean isTemporary;

	private SqlPlanListener planListener;

	private Thread lockedThread;

	private String description;

	private AmiImdbWarningsManager warningsManager;

	private String cachedDescribe;

	final private byte permissions;

	private int defaultTimeoutMillis;

	private int defaultLimit;

	private boolean ignoreReturnTables;

	private byte originType;

	private AmiCenterState state;

	private AmiHdb hdb;

	final private BasicMethodFactory methodFactory;
	final private SqlProcessor processor;
	final private AmiDebugManager debugManager = new AmiCenterDebugManager(); // Debugmanager doesn't seem to be used

	public AmiImdbSession(long sessionId, boolean isTemp, AmiCenterState state, byte definedBy, byte originType, String username, String description, byte permissions,
			int defaultTimeoutMillis, int defaultLimit, CalcFrame variables, TimeZone timezone) {
		if (defaultLimit < 0 && defaultLimit != AmiCenterQueryDsRequest.NO_LIMIT)
			throw new RuntimeException("Invalid default limit: " + defaultLimit);
		this.state = state;
		this.imdb = state.getAmiImdb();
		this.hdb = state.getHdb();
		int maxStackSize = getImdb().getMaxStackSize();
		this.warningsManager = new AmiImdbWarningsManager(maxStackSize);
		this.isTemporary = isTemp;
		this.sessionId = sessionId;
		this.definedBy = definedBy;
		this.originType = originType;
		this.username = username;
		this.createdTime = imdb.getNow();
		this.description = description;
		this.permissions = permissions;
		this.defaultTimeoutMillis = AmiUtils.toTimeout(defaultTimeoutMillis, imdb.getDefaultQueryTimeoutMs());
		this.defaultLimit = defaultLimit;
		this.globalConsts.putAllTypeValues(variables);
		if (imdb.getGlobalSession() != null) //skip the global session
			this.globalConsts.putAllTypeValues(imdb.getGlobalSession().globalConsts);
		this.globalConsts.putTypeValue("__SESSIONID", Long.class, sessionId);
		this.globalConsts.putTypeValue("__USERNAME", String.class, username);
		this.timezone = timezone;
		this.methodFactory = new BasicMethodFactory(this.imdb.getAmiScriptMethodFactory());
		this.processor = this.imdb.getScriptManager().getSqlProcessor();

		this.topStackFrame = new AmiCenterTopCalcFrameStack(this, state.getStackLimit(), this, SqlProcessor.NO_LIMIT, null, null, null, this.writeableValues,
				EmptyCalcFrame.INSTANCE, this.methodFactory, this.globalConsts, new SqlResultset());
	}

	@Override
	public Table getTable(String name) {
		assertLocked();
		Table r = getTableNoThrow(name);
		if (r != null)
			return r;
		throw new RuntimeException("Table not found: " + name);

	}
	private void assertLocked() {
		if (this.lockedThread != Thread.currentThread())
			throw new IllegalStateException(this.lockedThread == null ? "Not locked" : "not locked by this thread");
	}

	@Override
	public Table getTableNoThrow(String name) {
		assertLocked();
		if (local != null) {
			Table r = local.getTableNoThrow(name);
			if (r != null)
				return r;
		}
		AmiTableImpl r = imdb.getAmiTable(name);
		if (r != null)
			return r.getTable();
		AmiHdbTableRep hdb = imdb.getState().getHdb().getTableRepNoThrow(name);
		if (hdb != null)
			return hdb;
		return null;
	}
	public Table getLocalTableNoThrow(String name) {
		assertLocked();
		return local == null ? null : local.getTableNoThrow(name);
	}
	public Table getVariableTableNoThrow(String name, CalcFrame vars) {
		if (vars == null)
			return null;
		Object o = vars.getValue(name);
		return (Table) ((o instanceof Table) ? o : null);
	}
	public Table getPublicTableNoThrow(String name) {
		assertLocked();
		AmiTableImpl r = imdb.getAmiTable(name);
		return r == null ? null : r.getTable();
	}

	@Override
	public Table removeTable(String name) {
		assertLocked();
		if (this.local == null)
			return null;
		return this.local.removeTable(name);
	}

	@Override
	public void putTable(String name, Table table) {
		assertLocked();
		if (local == null)
			this.local = new TablesetImpl();
		this.local.putTable(name, table);

	}

	@Override
	public void putTable(Table table) {
		assertLocked();
		putTable(table.getTitle(), table);
	}

	@Override
	public Collection<String> getTableNamesSorted() {
		return getTableNames();
	}

	@Override
	public Set<String> getTableNames() {
		assertLocked();
		if (this.local == null)
			return imdb.getAmiTableNamesSorted();
		//TODO: don't create every time, look at a [not yet created] schema modify counter on the inner schema 
		TreeSet<String> r = new TreeSet<String>(imdb.getAmiTableNamesSorted());
		r.addAll(local.getTableNames());
		return r;
	}

	public Tableset getLocalTableset() {
		assertLocked();
		return this.local;
	}

	@Override
	public void clearTables() {
		assertLocked();
		if (this.local != null) {
			this.local.clearTables();
			this.local = null;
		}
	}

	public AmiImdbImpl getImdb() {
		return this.imdb;
	}
	public long getSessionId() {
		return sessionId;
	}
	public void touch(long now) {
		this.lastUsedTime = now;
	}

	public long getLastUsedTime() {
		return this.lastUsedTime;
	}

	public void setIgnoreReturnTables(boolean ignoreReturnTables) {
		this.ignoreReturnTables = ignoreReturnTables;
	}

	//	public void addReturnTable(Table r) {
	//		if (ignoreReturnTables)
	//			return;
	//		assertLocked();
	//		this.returnTables.add(r);
	//	}
	//
	//	public List<Table> getReturnTables() {
	//		if (this.returnTables.isEmpty())
	//			return Collections.EMPTY_LIST;
	//		List<Table> r = new ArrayList<Table>(this.returnTables);
	//		this.returnTables.clear();
	//		return r;
	//	}
	//	public List<Object> getGeneratedKeys() {
	//		if (this.generatedKeys.isEmpty())
	//			return null;
	//		List<Object> r = new ArrayList<Object>(this.generatedKeys);
	//		this.generatedKeys.clear();
	//		return r;
	//	}
	//	public void addRowsEffected(long rowsEffected) {
	//		assertLocked();
	//		if (rowsEffected == -1)
	//			return;
	//		else if (this.rowsEffected == -1)
	//			this.rowsEffected = rowsEffected;
	//		else
	//			this.rowsEffected += rowsEffected;
	//	}

	//	public long getRowsEffected() {
	//		long r = this.rowsEffected;
	//		this.rowsEffected = -1L;
	//		return r;
	//	}

	public boolean isTemporaryTable(String tableName) {
		if (this.local != null && local.getTableNoThrow(tableName) != null)
			return true;
		if (this.writeableValues.getValue(tableName) instanceof Table)
			return true;
		return false;
	}

	public byte getDefinedBy() {
		return this.definedBy;
	}

	public String getUsername() {
		return this.username;
	}

	public long getCreatedTime() {
		return this.createdTime;
	}

	public boolean getIsTemporary() {
		return this.isTemporary;

	}

	public void close() {
		imdb.getSessionManager().removeSession(this.sessionId);
		String uncaughtWarnings = this.warningsManager.drainWarnings();
		if (SH.is(uncaughtWarnings)) {
			LH.info(log, describe(), " has Uncaught Warnings: ", uncaughtWarnings);
		}
	}

	private String describe() {
		if (this.cachedDescribe == null)
			this.cachedDescribe = "SESSION-" + this.sessionId + ":" + AmiTableUtils.toStringForOriginType(this.originType) + ":" + this.username + " (" + this.description + ")";
		return this.cachedDescribe;
	}

	public int getTempTablesCount() {
		return this.local == null ? 0 : this.local.getTableNames().size();
	}

	public SqlPlanListener getPlanListener() {
		return planListener;
	}

	private ReentrantLock lock = new ReentrantLock();

	private AmiCenterProcess process;

	private StackTraceElement[] st;

	public void lock(AmiCenterProcess process, SqlPlanListener listener) {
		if (this.lockedThread == Thread.currentThread())
			throw new RuntimeException("Already has lock");
		this.st = Thread.currentThread().getStackTrace();
		this.lock.lock();
		this.lockedThread = Thread.currentThread();
		this.planListener = listener;
		this.process = process;
	}

	public void unlock() {
		this.planListener = null;
		assertLocked();
		this.lockedThread = null;
		this.process = null;
		this.lock.unlock();
	}

	public AmiCenterProcess getProcess() {
		return this.process;
	}

	public String getDescription() {
		return this.description;
	}

	//	public boolean isInStack(byte type) {
	//		return this.warningsManager.isInStack(type);
	//	}
	//	public boolean pushStack(byte type, String name) {
	//		return this.warningsManager.pushStack(type, name);
	//	}

	//	public void popStack(byte type, String name) {
	//		this.warningsManager.popStack(type, name);
	//	}

	public void onWarning(String errorCode, AmiTableImpl target, String objectName, String actionType, String description, AmiRow row, Exception e) {
		this.warningsManager.onWarning(errorCode, target, objectName, actionType, description, row);
		if (e != null)
			LH.info(log, "errorCode: ", errorCode, ",target: ", (target == null ? null : target.getName()), ",objectName=", objectName, ",actionType=", actionType, ",description=",
					description, ",row=", row, ",exception: ", e);
	}
	public String drainWarnings() {
		return this.warningsManager.drainWarnings();
	}

	public byte getPermissions() {
		return this.permissions;
	}

	public boolean hasAlterPermissions() {
		return MH.anyBits(this.permissions, PERMISSIONS_ALTER);
	}
	public boolean hasReadPermissions() {
		return MH.anyBits(this.permissions, PERMISSIONS_READ);
	}
	public boolean hasWritePermissions() {
		return MH.anyBits(this.permissions, PERMISSIONS_WRITE);
	}
	public boolean hasExecutePermissions() {
		return MH.anyBits(this.permissions, PERMISSIONS_EXECUTE);
	}

	public void assertCanRead() {
		if (!hasReadPermissions())
			throw new ExpressionParserException(0, "Permission denied: READ required");
	}
	public void assertCanWrite() {
		if (!hasWritePermissions())
			throw new ExpressionParserException(0, "Permission denied: WRITE required");
	}
	public void assertCanAlter() {
		if (!hasAlterPermissions())
			throw new ExpressionParserException(0, "Permission denied: ALTER required");
	}
	public void assertCanExecute() {
		if (!hasExecutePermissions())
			throw new ExpressionParserException(0, "Permission denied: EXECUTE required");
	}

	public MutableCalcFrame getConsts() {
		return this.globalConsts;
	}
	public CalcFrame getVars() {
		return this.writeableValues;
	}
	public boolean getIsLocked() {
		return this.lock.isLocked();
	}

	public DerivedCellTimeoutController createTimeoutController() {
		return new DerivedCellTimeoutController(this.defaultTimeoutMillis);
	}

	public int getDefaultTimeoutMs() {
		return this.defaultTimeoutMillis;
	}
	public int getDefaultLimit() {
		return this.defaultLimit;
	}

	//	public void onGeneratedKey(Object amiId) {
	//		this.generatedKeys.add(amiId);
	//	}

	public AmiHdbTableRep getHistoricalTableNoThrow(String tableName) {
		return this.imdb.getState().getHdb().getTableRepNoThrow(tableName);
	}

	public TopCalcFrameStack getReusableTopStackFrame() {
		return this.topStackFrame;
	}

	public AmiImdbObjectsManager getObjectsManager() {
		return this.imdb.getObjectsManager();
	}

	public AmiHdb getHdb() {
		return this.hdb;
	}

	private TimeZone timezone;

	public TimeZone getTimezone() {
		return this.timezone;
	}
	public String getTimezoneId() {
		return this.timezone.getID();
	}
	public void setTimezone(String timezoneId) {
		this.timezone = EH.getTimeZone(timezoneId);
	}

	@Override
	public MethodFactoryManager getMethodFactory() {
		return this.methodFactory;
	}

	@Override
	public SqlProcessor getSqlProcessor() {
		return this.processor;
	}

	@Override
	public AmiDebugManager getDebugManager() {
		return this.debugManager;
	}

	private DateFormatNano ERROR_FORMATTER = new DateFormatNano("");
	private static final int MAX_DATEFORMATS_CACHE = 100;
	private Date tmp = new Date();

	private String lastTimezone;
	private String lastFormat;
	private DateFormatNano lastSimpleDateFormat;
	private Map<String, DateFormatNano> cachedFormatters = new HashMap<String, DateFormatNano>();

	//TODO: look at AmiWebFormatterManager
	@Override
	public String getformatDate(String format, long time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			tmp.setTime(time);
			return lastSimpleDateFormat.format(tmp);
		}
		DateFormatNano cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		tmp.setTime(time);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(tmp);
	}

	@Override
	public String getformatDate(String format, DateMillis time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			return lastSimpleDateFormat.format(time);
		}
		DateFormatNano cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(time);
	}
	@Override
	public String getformatDate(String format, DateNanos time, String timezone) {
		if (OH.eq(lastFormat, format) && OH.eq(lastTimezone, timezone) && lastTimezone != null) {
			return lastSimpleDateFormat.format(time);
		}
		DateFormatNano cached = cachedFormatters.get(format);
		if (cached == null)
			try {
				cached = new DateFormatNano(format);
				if (cachedFormatters.size() < MAX_DATEFORMATS_CACHE)
					cachedFormatters.put(format, cached);
			} catch (Exception e) {
				cachedFormatters.put(format, ERROR_FORMATTER);
				return null;
			}
		else if (cached == ERROR_FORMATTER)
			return null;
		TimeZone tz = timezone == null ? getTimezone() : EH.getTimeZoneNoThrow(timezone);
		if (tz == null)
			return null;
		cached.setTimeZone(tz);
		lastFormat = format;
		lastTimezone = timezone;
		lastSimpleDateFormat = cached;
		return cached.format(time);
	}
	@Override
	public String getformatDate(String format, long time, CalcFrameStack sf) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		return getformatDate(format, time, session.getTimezoneId());
	}

	@Override
	public String getformatDate(String format, DateMillis time, CalcFrameStack sf) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		return getformatDate(format, time, session.getTimezoneId());
	}

	@Override
	public String getformatDate(String format, DateNanos time, CalcFrameStack sf) {
		AmiImdbSession session = AmiCenterUtils.getSession(sf);
		return getformatDate(format, time, session.getTimezoneId());
	}
}
