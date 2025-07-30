package com.f1.ami.amiscript;

import java.util.Map;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.AbstractDerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.MethodExample;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellMemberMethod;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public abstract class AmiAbstractMemberMethod<T> extends AbstractDerivedCellMemberMethod<T> implements DerivedCellMemberMethod<T> {
	private static final Logger log = LH.get();

	final private String[] names;
	final private String[] descriptions;
	final private MethodExample[] examples;
	final private String help;

	private AmiScriptBaseMemberMethods memberMethods;

	private String fullMethodName;

	public AmiAbstractMemberMethod(Class<T> targetType, String methodName, Class<?> returnType, boolean isVarArg, Class... paramTypes) {
		super(targetType, methodName, returnType, isVarArg, paramTypes);
		this.names = buildParamNames();
		this.descriptions = buildParamDescriptions();
		if (this.names != null || this.descriptions != null) {
			OH.assertEq(this.names.length, this.descriptions.length);
			OH.assertEq(this.names.length, paramTypes.length);
		}
		this.help = getHelp();
		this.examples = getExamples();
	}
	public AmiAbstractMemberMethod(Class<T> targetType, String methodName, Class<?> returnType, Class... paramTypes) {
		this(targetType, methodName, returnType, false, paramTypes);
	}

	public AmiAbstractMemberMethod(Class<T> targetType, ParamsDefinition i) {
		super(targetType, i.getMethodName(), i.getReturnType(), i.isVarArg(), i.getParamTypes());
		this.names = i.getParamNames();
		this.descriptions = i.getParamDescriptions();
		this.help = getHelp();
		this.examples = getExamples();
	}
	@Override
	final public Object invokeMethod(CalcFrameStack sf, T targetObject, Object[] params, DerivedCellCalculator caller) {
		if (targetObject == null && getMethodName() != null)
			return null;
		try {
			Object r = invokeMethod2(sf, targetObject, params, caller);
			return r;
		} catch (FlowControlThrow fct) {
			if (fct.getTailFrame().getPosition() == null)
				fct.getTailFrame().setPosition(caller);
			throw fct;
		}
	};

	abstract public Object invokeMethod2(CalcFrameStack sf, T targetObject, Object[] params, DerivedCellCalculator caller);

	@Override
	public Object resumeMethod(CalcFrameStack sf, T target, Object[] params, PauseStack paused, FlowControlPause fp, DerivedCellCalculator caller) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isPausable() {
		return false;
	}

	protected String[] buildParamNames() {
		return OH.EMPTY_STRING_ARRAY;
	}
	protected String[] buildParamDescriptions() {
		return buildParamNames();
	}
	abstract protected String getHelp();

	public MethodExample[] getExamples() {
		return new MethodExample[] {};
	}

	@Override
	public String[] getParamNames() {
		return this.names;
	}
	public String[] getParamDescriptions() {
		return this.descriptions;
	}

	public String getDescription() {
		return this.help;
	}
	public Map<String, String> getAutocompleteOptions(AmiService service) {
		return null;
	}

	public void setMemberMethods(AmiScriptBaseMemberMethods mm) {
		this.memberMethods = mm;
		this.fullMethodName = this.memberMethods.getVarTypeName() + "::" + this.getMethodName();
	}

	private void log(CalcFrameStack sf, byte severity, String message, Map<Object, Object> details, Throwable ex) {
		AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
		if (shouldDebug(ei, severity)) {
			if (ei != null) {
				ei.getDebugManager().addMessage(new AmiDebugMessage(severity, ei.getSourceDebugType(), ei.getSourceAri(), ei.getCallbackName(),
						fullMethodName + " ==> " + SH.ddd(message, 255), details, ex));
			} else {
				this.memberMethods.getDebugManager()
						.addMessage(new AmiDebugMessage(severity, AmiDebugMessage.TYPE_METHOD, fullMethodName, null, SH.ddd(message, 255), details, ex));
			}
		}
	}

	protected boolean shouldDebug(CalcFrameStack sf, byte severity) {
		AmiCalcFrameStack ei = AmiUtils.getExecuteInstance2(sf);
		if (ei != null)
			return ei.getDebugManager().shouldDebug(severity);
		else
			return this.memberMethods.getDebugManager().shouldDebug(severity);
	}

	public void warning(CalcFrameStack sf, String message, Map<Object, Object> details) {
		log(sf, AmiDebugMessage.SEVERITY_WARNING, message, details, null);
	}
	public void warning(CalcFrameStack sf, String string, Map<Object, Object> details, Throwable ex) {
		log(sf, AmiDebugMessage.SEVERITY_WARNING, string, details, ex);
	}

	public void debug(CalcFrameStack sf, String message, Map<Object, Object> details) {
		log(sf, AmiDebugMessage.SEVERITY_INFO, message, details, null);
	}
	public void debug(CalcFrameStack sf, String message, Map<Object, Object> details, Throwable ex) {
		log(sf, AmiDebugMessage.SEVERITY_INFO, message, details, ex);
	}

}
