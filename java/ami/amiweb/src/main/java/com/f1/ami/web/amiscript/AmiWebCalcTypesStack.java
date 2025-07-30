package com.f1.ami.web.amiscript;

import java.util.Set;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebCalcTypesStack implements CalcTypesStack {
	final private FrameCacher consts;
	final private CalcTypes frameTypes;
	final private MethodFactoryManager factoryManager;
	final private CalcFrame frameConsts;

	public AmiWebCalcTypesStack(Set<String> constVarsSink, CalcTypes frameTypes, CalcFrame frameConsts, MethodFactoryManager methodFactory, CalcFrame constsMap) {
		this.consts = new FrameCacher(constsMap, constVarsSink);
		this.frameConsts = frameConsts;
		this.frameTypes = frameTypes;
		this.factoryManager = methodFactory;
	}

	@Override
	public CalcFrame getGlobalConsts() {
		return this.consts;
	}

	private static class FrameCacher implements CalcFrame {

		private CalcFrame inner;
		final private Set<String> constVarSink;

		public FrameCacher(CalcFrame inner, Set<String> constsSink) {
			this.inner = inner;
			this.constVarSink = constsSink;
		}

		@Override
		public Object getValue(String key) {
			Object r = inner.getValue(key);
			if (r != null && key.startsWith("$"))
				this.constVarSink.add(key);
			return r;
		}

		@Override
		public Object putValue(String key, Object value) {
			return inner.putValue(key, value);
		}

		@Override
		public Class<?> getType(String key) {
			return inner.getType(key);
		}

		@Override
		public boolean isVarsEmpty() {
			return inner.isVarsEmpty();
		}

		@Override
		public Iterable<String> getVarKeys() {
			return inner.getVarKeys();
		}

		@Override
		public int getVarsCount() {
			return inner.getVarsCount();
		}

	}

	@Override
	public MethodFactoryManager getFactory() {
		return this.factoryManager;
	}

	@Override
	public CalcTypes getFrame() {
		return this.frameTypes;
	}

	@Override
	public boolean isParentVisible() {
		return false;
	}

	@Override
	public CalcTypesStack getParent() {
		return null;
	}

	@Override
	public CalcFrame getFrameConsts() {
		return this.frameConsts;
	}

	@Override
	public CalcTypes getGlobal() {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public CalcTypesStack getTop() {
		return this;
	}
}
