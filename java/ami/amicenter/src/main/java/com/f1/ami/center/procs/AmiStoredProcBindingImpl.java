package com.f1.ami.center.procs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.base.Caster;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableStackFramePool;
import com.f1.utils.structs.table.stack.SqlResultset;

public class AmiStoredProcBindingImpl implements AmiStoredProcBinding {

	private static final Logger log = LH.get();
	private String storedProcName;
	final private AmiStoredProc storedProc;
	final private byte defType;
	final private String storedProcType;
	final private Map<String, String> optionsStrings;
	private Class returnType;
	private List<AmiFactoryOption> arguments;
	private String returnTypeString;
	private String argumentsString;
	final private Map<String, Object> options;
	final private AmiImdbImpl db;
	private Class<?>[] argumentTypes;
	private Caster<?>[] argumentCasters;
	private boolean[] argumentRequired;
	private ReusableStackFramePool pool;

	public AmiStoredProcBindingImpl(AmiImdbImpl db, String storedProcName, AmiStoredProc storedProc, String storedProcType, Map<String, Object> options,
			Map<String, String> optionsStrings, byte definedBy) {
		this.storedProcName = storedProcName;
		this.storedProc = storedProc;
		this.storedProcType = storedProcType;
		this.defType = definedBy;
		this.optionsStrings = optionsStrings;
		this.options = options;

		this.db = db;
	}
	@Override
	public AmiStoredProc getStoredProc() {
		return this.storedProc;
	}

	@Override
	public String getStoredProcName() {
		return storedProcName;
	}

	public void startup(AmiImdbImpl amiImdb, CalcFrameStack sf) {
		this.pool = amiImdb.getState().getStackFramePool();
		this.storedProc.startup(amiImdb, this, sf);
		this.returnType = storedProc.getReturnType();
		this.arguments = storedProc.getArguments();
		int numArgs = this.arguments.size();
		this.argumentTypes = new Class[numArgs];
		this.argumentCasters = new Caster[numArgs];
		this.argumentRequired = new boolean[numArgs];
		for (int i = 0; i < this.argumentTypes.length; i++) {
			AmiFactoryOption amiFactoryOption = this.arguments.get(i);
			this.argumentTypes[i] = amiFactoryOption.getType();
			this.argumentCasters[i] = OH.getCaster(this.argumentTypes[i]);
			this.argumentRequired[i] = amiFactoryOption.getRequired();
		}
		MethodFactoryManager methodFactory = db.getScriptManager().getMethodFactory();
		this.returnTypeString = methodFactory.forType(this.returnType);
		this.argumentsString = AmiUtils.descriptFactoryOptions(this.arguments, methodFactory, false);
	}

	@Override
	public String getStoredProcType() {
		return this.storedProcType;
	}
	@Override
	public byte getDefType() {
		return this.defType;
	}

	@Override
	public Map<String, Object> getOptions() {
		return options;
	}
	public Map<String, String> getOptionsStrings() {
		return optionsStrings;
	}

	public void stop() {
	}
	@Override
	public String getReturnTypeString() {
		return this.returnTypeString;
	}
	@Override
	public String getArgumentsString() {
		return this.argumentsString;
	}
	@Override
	public <T> T getOption(Class<T> castType, String key) {
		return CH.getOrThrow(castType, this.options, key);
	}
	@Override
	public <T> T getOption(Class<T> castType, String key, T defaultValue) {
		return CH.getOr(castType, this.options, key, defaultValue);
	}
	@Override
	public <T> T getOption(Caster<T> caster, String key, T defaultValue) {
		return CH.getOr(caster, this.options, key, defaultValue);
	}
	public void onSchemaChanged(CalcFrameStack sf) {
		try {
			this.storedProc.onSchemaChanged(this.db, sf);
		} catch (Exception e) {
			LH.warning(log, "AmiStoredProc '", this.storedProcName, "' generated error: ", e);
		}
	}

	private long statsCount = 0;
	private long statsNanos = 0;
	private long statsError = 0;

	public FlowControl execute(Object[] arguments, int limitOffset, int limit, String username, AmiImdbSession session, CalcFrameStack sf) throws Exception {
		if (this.argumentTypes == null)
			throw new IllegalStateException("Procedure not initialized: " + this.getStoredProcName());
		if (arguments.length < this.argumentTypes.length) {
			boolean areRequired = false;
			for (int i = arguments.length; i < this.argumentTypes.length; i++)
				if (this.argumentRequired[i])
					areRequired = true;
			if (!areRequired)
				arguments = Arrays.copyOf(arguments, this.argumentTypes.length);
		}
		if (arguments.length != this.argumentTypes.length)
			throw new RuntimeException("Expecting " + argumentTypes.length + " argument(s) not " + arguments.length);
		List<Object> arguments2 = argumentTypes.length == 0 ? Collections.EMPTY_LIST : new ArrayList<Object>(arguments.length);
		for (int i = 0; i < argumentTypes.length; i++) {
			Object v = arguments[i];
			if (v != null) {
				v = this.argumentCasters[i].cast(v, false, false);
				if (v == null && !"null".equals(v))
					throw new RuntimeException((i + 1) + SH.getOrdinalIndicator(i + 1) + " Argument '" + this.arguments.get(i).getName() + " should be of type "
							+ db.getAmiScriptMethodFactory().forType(argumentTypes[i]) + ": " + arguments[i]);
			}
			if (v == null && this.arguments.get(i).getRequired())
				throw new RuntimeException((i + 1) + SH.getOrdinalIndicator(i + 1) + " Argument '" + this.arguments.get(i).getName() + "' can not be null");
			arguments2.add(v);
		}

		long start = System.nanoTime();
		ReusableCalcFrameStack rsf = push(AmiTrigger.CALL, sf);
		SqlResultset resultset = new SqlResultset();
		rsf.setSqlResultSet(resultset);
		final AmiStoredProcRequest request = this.db.getTools().nw(AmiStoredProcRequest.class);
		request.setArguments(arguments2);
		request.setInvokedBy(username);
		request.setLimitOffset(limitOffset);
		request.setLimit(limit);
		FlowControl r;
		try {
			r = this.storedProc.execute(request, rsf);
			statsNanos += System.nanoTime() - start;
			statsCount++;
		} catch (Exception e) {
			statsNanos += System.nanoTime() - start;
			statsCount++;
			statsError++;
			throw e;
		}
		if (r instanceof TableReturn) {
			resultset.appendTable((TableReturn) r);
		} else if (r instanceof FlowControl)
			return r;
		TableReturn tr = new TableReturn(resultset.getTables(), resultset.getGenerateKeys(), resultset.getRowsEffected(), null, null);
		pop(rsf);
		return tr;
	}

	public long getStatsCount() {
		return this.statsCount;
	}
	public long getStatsNanos() {
		return this.statsNanos;
	}
	public long getStatsErrors() {
		return this.statsError;
	}
	public void rename(String newName) {
		this.storedProcName = newName;
	}
	private void pop(ReusableCalcFrameStack sf) {
		pool.release(sf);
	}

	private ReusableCalcFrameStack push(byte inserting, CalcFrameStack sf) {
		return pool.borrow(sf, EmptyCalcFrame.INSTANCE);
	}
}
