package com.f1.ami.center.dbo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import com.f1.ami.center.AmiDboFactoryWrapper;
import com.f1.ami.center.sysschema.AmiSchema;
import com.f1.ami.center.sysschema.AmiSchema_DBO;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbScriptManager;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.table.AmiRow;
import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorExpression;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiDboBindingImpl implements AmiDboBinding {

	private static final Logger log = LH.get();
	final private Map<String, Object> options;
	final private Map<String, String> optionsStrings;
	private String name;
	final private HashMap<String, Callback> callbacks;
	final private AmiDbo dbo;
	final private AmiDboFactoryWrapper factory;
	final private byte defType;
	final private int priority;
	final private Method methods[];
	private AmiImdbImpl db;
	private boolean enabled = true;
	private Throwable startupError;
	private long startupErrorTime;

	public AmiDboBindingImpl(String name, AmiDbo dbo, int priority, AmiDboFactoryWrapper factory, Map<String, Object> options, Map<String, String> optionsStrings, byte definedBy) {
		this.name = name;
		this.factory = factory;
		this.options = options;
		this.optionsStrings = optionsStrings;
		this.dbo = dbo;
		this.defType = definedBy;
		this.callbacks = new HashMap<String, Callback>();
		List<AmiDboMethodWrapper> mthds = factory.getMethods();
		this.methods = new Method[mthds.size()];
		for (int i = 0; i < this.methods.length; i++)
			this.methods[i] = new Method(i, mthds.get(i));
		for (ParamsDefinition i : this.factory.getCallbackDefinitions()) {
			final String amiscript = this.getOption(Caster_String.INSTANCE, "callback_" + i.getMethodName(), null);
			this.callbacks.put(i.getMethodName(), new Callback(i, amiscript));
		}
		this.priority = priority;
	}

	@Override
	public Object executeCallbackNoThrow(String name, Map<String, Object> args) {
		try {
			return executeCallback(name, args);
		} catch (Throwable t) {
			return null;
		}
	}
	@Override
	public Object executeCallback(String callback, Map<String, Object> args) {
		if (!isRunning())
			return null;
		Callback cb = CH.getOrThrow(this.callbacks, callback);
		if (cb.calc != null) {
			this.db.getState().getPartition().lockForWrite(60000, TimeUnit.MILLISECONDS);
			try {
				this.db.getGlobalSession().lock(this.db.getGlobalProcess(), null);
				try {
					return cb.execute(args);
				} finally {
					this.db.getGlobalSession().unlock();
				}
			} finally {
				this.db.getState().getPartition().unlockForWrite();
			}
		} else if (cb.compileException != null)
			cb.statErrors++;
		return null;
	}
	@Override
	public boolean isCallbackImplemented(String callback) {
		return CH.getOrThrow(this.callbacks, callback).calc != null;
	}

	@Override
	public Map<String, Object> getOptions() {
		return this.options;
	}

	@Override
	public <T> T getOption(Caster<T> caster, String key, T defaultValue) {
		return CH.getOr(caster, this.options, key, defaultValue);
	}
	@Override
	public <T> T getOption(Caster<T> caster, String key) {
		return CH.getOrThrow(caster, this.options, key);
	}

	@Override
	public List<ParamsDefinition> getCallbackDefinitions() {
		return this.factory.getCallbackDefinitions();
	}

	@Override
	public String getDboName() {
		return this.name;
	}
	public void startup(AmiImdbImpl amiImdb, CalcFrameStack sf) {
		this.db = amiImdb;
		for (Callback i : this.callbacks.values())
			i.compile();
		try {
			this.dbo.startAmiDbo(this);
		} catch (Throwable e) {
			LH.warning(log, "Dbo '", this.getDboName(), "' failed to start.", e);
			this.startupError = e;
			this.startupErrorTime = System.currentTimeMillis();
		}
	}

	public void setCallback(String name, String amiscript, CalcFrameStack sf) {
		if (CH.getOrThrow(this.callbacks, name).setAmiscript(amiscript))
			this.db.onSchemaChanged(sf);
	}

	public void stop(CalcFrameStack sf) {
		this.dbo.closeAmiDbo();
	}

	public AmiDbo getDbo() {
		return this.dbo;
	}

	@Override
	public String getDboType() {
		return this.factory.getDboClassName();
	}

	@Override
	public byte getDefType() {
		return this.defType;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	public Map<String, String> getOptionsStrings() {
		return this.optionsStrings;
	}

	@Override
	public boolean getIsEnabled() {
		return enabled;
	}
	public void setIsEnabled(boolean enable, CalcFrameStack sf) {
		if (this.enabled == enable)
			return;
		this.enabled = enable;
		if (this.db != null) {
			AmiSchema_DBO __DBO = this.db.getSystemSchema().__DBO;
			__DBO.getRowsByDboName().get(this.getDboName()).setLong(__DBO.enabled, enabled ? 1 : 0, sf);
			this.db.getSystemSchema().writeManagedSchemaFile(sf);
		}
	}

	public void rename(String newName) {
		this.name = newName;
	}

	public class Method {

		public Method(int i, AmiDboMethodWrapper method) {
			this.methodIndex = i;
			this.method = method;
		}

		public int methodIndex;
		public int statCount;
		public long statNanos;
		public int statErrors;
		public AmiDboMethodWrapper method;
		public Throwable lastError;
		public long lastErrorTime;
	}

	public class Callback {

		private BasicCalcTypes types = new BasicCalcTypes();
		private Throwable compileException;
		public String amiscript;
		public DerivedCellCalculatorExpression calc;
		public ParamsDefinition def;
		public int statErrors;
		public int statCount;
		public long statNanos;
		private Throwable lastError;
		private Throwable runtimeException;
		private long compileExceptionTime;
		private long lastErrorTime;

		public Throwable getLastRuntimeError() {
			return lastError;
		}
		public long getLastRuntimeErrorTime() {
			return lastErrorTime;
		}

		public Throwable getCompileError() {
			return this.compileException;
		}
		public long getCompileErrorTime() {
			return this.compileExceptionTime;
		}

		public Callback(ParamsDefinition def, String amiscript) {
			this.def = def;
			this.amiscript = amiscript;
			types.putType("this", getClassType());
			for (int i = 0; i < def.getParamsCount(); i++) {
				String n = def.getParamName(i);
				Class<?> t = def.getParamType(i);
				types.putType(n, t);
			}
		}

		public Throwable getLastException() {
			if (this.lastError != null)
				return this.lastError;
			return this.compileException;
		}

		public Object execute(Map<String, Object> args) {
			final BasicCalcFrame frame = new BasicCalcFrame(types);
			long start = System.nanoTime();
			try {
				for (Entry<String, Object> entry : args.entrySet()) {
					final Object value = OH.cast(entry.getValue(), frame.getType(entry.getKey()));
					frame.putValue(entry.getKey(), value);
				}
				frame.putValue("this", dbo);
				AmiImdbSession gs = db.getGlobalSession();
				Object r = db.getScriptManager().executeSql(calc, frame, AmiImdbScriptManager.ON_EXECUTE_AUTO_HANDLE, gs.createTimeoutController(), gs.getDefaultLimit(), null,
						gs.getReusableTopStackFrame());
				this.statCount++;
				this.statNanos += (System.nanoTime() - start);
				return r;
			} catch (Throwable e) {
				this.statErrors++;
				LH.warning(log, e);
				this.lastError = e;
				this.lastErrorTime = System.currentTimeMillis();
				throw e;
			}

		}

		public boolean setAmiscript(String amiscript) {
			if (SH.isnt(amiscript))
				amiscript = null;
			if (OH.eq(this.amiscript, amiscript))
				return false;
			this.amiscript = amiscript;
			compile();
			return true;
		}

		public void compile() {
			try {
				this.compileException = null;
				this.compileExceptionTime = -1L;
				this.calc = null;
				if (this.amiscript != null)
					this.calc = db.getScriptManager().prepareSql(this.amiscript, this.types, true, false, db.getGlobalSession().getReusableTopStackFrame());
			} catch (Throwable e) {
				LH.warning(log, "Error with dbo '", name, "' callback '" + def.getMethodName(), "': ", e);
				this.compileException = e;
				this.compileExceptionTime = System.currentTimeMillis();
			}
		}

	}

	public Class<?> getClassType() {
		return this.factory.getDboClassType();
	}

	private ReentrantLock lock = new ReentrantLock();

	public void lock() {
		lock.lock();
	}

	public void onMethodStat(AmiDboMethodWrapper method, long l) {
		Method m = this.methods[method.getMethodIndex()];
		m.statCount++;
		m.statNanos += l;
	}
	public void onMethodErrorStat(AmiDboMethodWrapper method, Throwable t) {
		Method m = this.methods[method.getMethodIndex()];
		m.statErrors++;
		LH.warning(log, t);
		m.lastError = t;
		m.lastErrorTime = System.currentTimeMillis();
	}

	public void unlock() {
		lock.unlock();
	}

	public HashMap<String, Callback> getCallbacks() {
		return this.callbacks;
	}
	public int getMethodsCount() {
		return this.methods.length;
	}
	public Method getMethodAt(int i) {
		return this.methods[i];
	}

	public long getStatsNanosTotal() {
		long r = 0;
		for (Callback i : this.callbacks.values())
			r += i.statNanos;
		for (Method i : this.methods)
			r += i.statNanos;
		return r;
	}

	public long getStatsCountTotal() {
		long r = 0;
		for (Callback i : this.callbacks.values())
			r += i.statCount;
		for (Method i : this.methods)
			r += i.statCount;
		return r;
	}

	public long getStatsErrorsTotal() {
		long r = 0;
		if (startupError != null)
			r++;
		for (Callback i : this.callbacks.values())
			r += i.statErrors;
		for (Method i : this.methods)
			r += i.statErrors;
		return r;
	}

	public Object getStatsCompiledErrors() {
		long r = 0;
		if (startupError != null)
			r++;
		for (Callback i : this.callbacks.values())
			if (i.compileException != null)
				r++;
		return r;
	}

	public AmiDboFactoryWrapper getFactory() {
		return this.factory;
	}

	public void updateOption(String key, Object value, CalcFrameStack sf) {
		if (key.startsWith("callback_")) {
			String amiscript = Caster_String.INSTANCE.cast(value);
			setCallback(SH.stripPrefix(key, "callback_", true), amiscript, sf);
			this.options.put(key, value);
			this.optionsStrings.put(key, amiscript);
			StringBuilder buf = new StringBuilder();
			AmiSchema.useOptionsToString(this.optionsStrings, buf);
			AmiRow ri = db.getSystemSchema().__DBO.getRowsByDboName().get(this.name);
			ri.setString("Options", buf.toString(), sf);
		}
	}

	@Override
	public String getOption(String key, String defaultValue) {
		return getOption(Caster_String.INSTANCE, key, defaultValue);
	}

	@Override
	public String getOption(String key) {
		return getOption(Caster_String.INSTANCE, key);
	}

	public boolean isRunning() {
		return this.enabled && this.startupError == null;
	}

	public Throwable getStartupError() {
		return this.startupError;
	}

	public void onSchemaChanged(CalcFrameStack sf) {
		for (Callback i : this.callbacks.values())
			i.compile();
	}

	public long getStartupErrorTime() {
		return this.startupErrorTime;
	}

}
