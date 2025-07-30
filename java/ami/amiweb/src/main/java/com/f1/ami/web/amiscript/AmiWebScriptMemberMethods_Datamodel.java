package com.f1.ami.web.amiscript;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiWebChildCalcFrameStack;
import com.f1.ami.web.AmiWebAmiScriptCallback;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmListener;
import com.f1.ami.web.dm.AmiWebDmManager;
import com.f1.ami.web.dm.AmiWebDmRequest;
import com.f1.ami.web.dm.AmiWebDmsImpl;
import com.f1.base.CalcFrame;
import com.f1.base.DateMillis;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_Datamodel extends AmiWebScriptBaseMemberMethods<AmiWebDm> {

	private AmiWebScriptMemberMethods_Datamodel() {
		super();

		addMethod(REPROCESS);
		addMethod(PROCESS);
		addMethod(PROCESS_SYNC);
		addMethod(GET_DATA);
		addMethod(SET_VALUE);
		addMethod(GET_VALUE);
		addMethod(GET_PARAMETERS);
		addMethod(EXPORT_CONFIG);
		addMethod(GET_ID, "id");
		addMethod(GET_UID, "uid");
		addMethod(GET_LAYOUT, "layout");
		addMethod(GET_UPSTREAM_DATAMODELS, "upstreamDatamodels");
		addMethod(GET_DOWNSTREAM_DATAMODELS, "downstreamDatamodels");
		addMethod(GET_CONSECUTIVE_REQUERIES_COUNT, "consecutiveRequeriesCount");
		addMethod(GET_ERRORS_COUNT, "errorsCount");
		addMethod(GET_EXECUTED_COUNT, "executedCount");
		addMethod(GET_EXECUTED_TOTAL_MILLIS, "executedTotalMillis");
		addMethod(GET_NEXT_EXECUTION_TIME, "nextExecutionTime");
		addMethod(GET_LAST_EXECUTION_TIME, "lastExecutionTime");

		addCustomDebugProperty("values", Map.class);
		addCustomDebugProperty("data", Tableset.class);
		registerCallbackDefinition(AmiWebDmsImpl.CALLBACK_DEF_ONCOMPLETE);
		registerCallbackDefinition(AmiWebDmsImpl.CALLBACK_DEF_ONPROCESS);
	}

	@Override
	protected Object getCustomDebugProperty(String name, AmiWebDm value) {
		if ("values".equals(name))
			return value.getVars();
		if ("data".equals(name))
			return value.getResponseTableset();
		return super.getCustomDebugProperty(name, value);
	}

	private static final AmiAbstractMemberMethod<AmiWebDm> PROCESS_SYNC = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "processSync", Object.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack stackFrame, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebDmsImpl dm = (AmiWebDmsImpl) targetObject;

			AmiWebDmRequest request = new AmiWebDmRequest();
			Map<String, String> m = (Map<String, String>) params[0];
			if (m != null) {
				for (Entry<String, String> e : m.entrySet())
					request.putVariable(e.getKey(), e.getValue());
			}

			targetObject.getService().putLastRealtimeRequest(targetObject.getDmUid(), request.getVariables());
			AmiWebAmiScriptCallback cb = dm.getAmiScriptCallbacks().getCallback(AmiWebDmsImpl.CALLBACK_ONPROCESS);
			if (cb.hasError(true)) {
				return new FlowControlThrow(caller, "COMPILE_ERROR");
			}
			dm.setRequestTableset(request);
			Map<String, Object> args = request.getVariablesForOnProcess(dm.getDefaultTrue());
			args.put(AmiWebDmsImpl.RTEVENTS, null);
			CalcFrame frame = targetObject.getCallback_OnProcess().mapInputs(args);
			dm.setLastQueryStartTimeNanos(System.nanoTime());
			AmiWebChildCalcFrameStack ei2 = cb.prepareExecuteInstance(stackFrame, frame);
			try {
				Object r = cb.executeInstance(ei2, frame);
				if (r instanceof FlowControlPause)
					return r;

				if (ei2 != null) { // null because editor open but there is an error
					dm.setResponseTableset(ei2.getTableset());
					for (final AmiWebDmListener l : dm.getDmListeners())
						l.onDmDataChanged(dm);
				}
				dm.incStatsEvalsCount();
				dm.incStatsEvalTimeNanos(System.nanoTime() - dm.getLastQueryStartTimeNanos());
				dm.setLastQueryEndTimeMillis(System.currentTimeMillis());

				return r;
			} catch (FlowControlThrow e) {
				dm.incStatsErrorsCount();
				dm.setLastQueryEndTimeMillis(System.currentTimeMillis());
				e.getTailFrame().setOriginalSourceCode(cb.getAri(), cb.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			}
		}

		@Override
		protected String getHelp() {
			return "Executes the datamodel immediately with the given WHERE arguments and is a blocking call. It returns the datamodel's return value.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key/value params to pass in" };
		}

		@Override
		public Object resumeMethod(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
			AmiWebDmsImpl dm = (AmiWebDmsImpl) targetObject;
			try {
				Object r = paused.getNext().resume();
				if (r instanceof FlowControlPause)
					return r;
				AmiCalcFrameStack ei = AmiUtils.getExecuteInstance(fp);
				targetObject.setResponseTableset(ei.getTableset());
				dm.incStatsEvalsCount();
				dm.incStatsEvalTimeNanos(System.nanoTime() - dm.getLastQueryStartTimeNanos());
				dm.setLastQueryEndTimeMillis(System.currentTimeMillis());
				return r;
			} catch (FlowControlThrow e) {
				dm.incStatsErrorsCount();
				dm.setLastQueryEndTimeMillis(System.currentTimeMillis());
				AmiWebAmiScriptCallback cb = targetObject.getAmiScriptCallbacks().getCallback(AmiWebDmsImpl.CALLBACK_ONPROCESS);
				e.getTailFrame().setOriginalSourceCode(cb.getAri(), cb.getAmiscript(true));
				e.addFrame(caller);
				throw e;
			}
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return true;
		};
	};

	private static final AmiAbstractMemberMethod<AmiWebDm> PROCESS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "process", Boolean.class, Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebDmRequest request = new AmiWebDmRequest();
			Map<String, String> m = (Map<String, String>) params[0];
			if (m != null) {
				for (Entry<String, String> e : m.entrySet()) {
					request.putVariable(e.getKey(), e.getValue());
				}
			}
			try {
				targetObject.getService().putLastRealtimeRequest(targetObject.getDmUid(), request.getVariables());
				((AmiWebDmsImpl) targetObject).processRequest(request, targetObject.getDmManager().getService().getDebugManager(), false, true);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Schedules a datamodel to run with provided WHERE arguments, returns true on success. This will conflate executions if the arguments are the same, and is not a blocking call so the next amiscript will execute.";
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "values" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key/value params to pass in" };
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
		@Override
		public boolean isPausable() {
			return false;
		};
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_PARAMETERS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getParameters", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				AmiWebDmRequest defaults = targetObject.getInputDefaults();
				return new LinkedHashMap(defaults.getVariables());
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "DEPRECATED... returns the input parameters with the associated defaults.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> REPROCESS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "reprocess", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				((AmiWebDmsImpl) targetObject).processRequest(targetObject.getRequestTableset(), targetObject.getDmManager().getService().getDebugManager(), true, true);
				return true;
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Reruns the datamodel with the last given WHERE arguments, returns true on success. This call is not blocking and will conflate repeated executions like `process`. ";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_DATA = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getData", Tableset.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getResponseTableset();
			} catch (Exception e) {
				return false;
			}
		}

		@Override
		protected String getHelp() {
			return "Returns a TableSet object that stores the column names of the underlying table and their respective types.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebDm> SET_VALUE = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "setValue", Boolean.class, String.class,
			Object.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getVars().put((String) params[0], params[1]);
			} catch (Exception e) {
				return null;
			}
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key", "value" };
		}
		@Override
		protected String getHelp() {
			return "Adds the key value pair to this portlet's attributes and return true if the operation is successful.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_VALUE = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getValue", Object.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			try {
				return targetObject.getVars().get((String) params[0]);
			} catch (Exception e) {
				return null;
			}
		}
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "key" };
		}
		@Override
		protected String getHelp() {
			return "Returns the value associated with the given key from this portlet's attributes. Returns null if key does not exist.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> EXPORT_CONFIG = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "exportConfig", Map.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getConfiguration();
		}
		@Override
		protected String getHelp() {
			return "Exports this datamodel's configuration to a map.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_UID = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getUid", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDmUid();
		}
		@Override
		protected String getHelp() {
			return "Returns this datamodel's id.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_ID = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getId", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getDmName();
		}
		@Override
		protected String getHelp() {
			return "Returns this datamodel's name.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_LAYOUT = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getLayout", AmiWebLayoutFile.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getService().getLayoutFilesManager().getLayoutByFullAlias(targetObject.getAmiLayoutFullAlias());
		}
		@Override
		protected String getHelp() {
			return "Returns the layout that owns this datamodel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_UPSTREAM_DATAMODELS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getSourceDatamodels", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebDmManager manager = targetObject.getDmManager();
			Set<String> adn = targetObject.getLowerDmAliasDotNames();
			List<AmiWebDm> r = new ArrayList<AmiWebDm>(adn.size());
			for (String s : adn) {
				AmiWebDmsImpl dm = manager.getDmByAliasDotName(s);
				if (dm != null)
					r.add(dm);
			}
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a list of datamodels that this datamodel gets data from.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_DOWNSTREAM_DATAMODELS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getTargetDatamodels", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			AmiWebDmManager manager = targetObject.getDmManager();
			Set<String> adn = targetObject.getUpperDmAliasDotNames();
			List<AmiWebDm> r = new ArrayList<AmiWebDm>(adn.size());
			for (String s : adn) {
				AmiWebDmsImpl dm = manager.getDmByAliasDotName(s);
				if (dm != null)
					r.add(dm);
			}
			return r;
		}
		@Override
		protected String getHelp() {
			return "Returns a list of datamodels that this datamodel sends data to.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebDm> GET_CONSECUTIVE_REQUERIES_COUNT = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getConsecutiveRequeriesCount",
			Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStatisticConsecutiveRequeriesCount();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of times this datamodel has been auto-requeried. This number resets to zero once it is run by means other than a requery.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_ERRORS_COUNT = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getErrorsCount", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStatisticErrors();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of times this datamodel has resulted in an error.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_EXECUTED_COUNT = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getExecutedCount", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStatisticEvals();
		}
		@Override
		protected String getHelp() {
			return "Returns the number of times this datamodel has been executed.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_EXECUTED_TOTAL_MILLIS = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getExecutedTotalMillis", Long.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getStatisticEvalsMinTimeMillis();
		}
		@Override
		protected String getHelp() {
			return "Returns the total execution time for this datamodel.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_LAST_EXECUTION_TIME = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getLastExecutionTime",
			DateMillis.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			long n = targetObject.getStatisticLastEvalTimeMillis();
			return n == 0 ? null : new DateMillis(n);
		}
		@Override
		protected String getHelp() {
			return "Returns the last execution time for this datamodel or null if the datamodel has not been run yet.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDm> GET_NEXT_EXECUTION_TIME = new AmiAbstractMemberMethod<AmiWebDm>(AmiWebDm.class, "getNextExecutionTime",
			DateMillis.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDm targetObject, Object[] params, DerivedCellCalculator caller) {
			long n = targetObject.getStatisticNextEvalTimeMillis();
			return n == 0 ? null : new DateMillis(n);
		}
		@Override
		protected String getHelp() {
			return "Returns the next scheduled execution time for this datamodel or null if the datamodel does not have a next scheduled run time.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "Datamodel";
	}
	@Override
	public String getVarTypeDescription() {
		return "The datamodel objects on which visualizations are built. Datamodels can be viewed and edited by double-clicking on the green nodes in the datamodel graph (Dashboard -> Data Modeler...).";
	}
	@Override
	public Class<AmiWebDm> getVarType() {
		return AmiWebDm.class;
	}
	@Override
	public Class<AmiWebDm> getVarDefaultImpl() {
		return null;
	}

	public static final AmiWebScriptMemberMethods_Datamodel INSTANCE = new AmiWebScriptMemberMethods_Datamodel();
}
