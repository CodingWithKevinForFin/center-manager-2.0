package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.rt.AmiWebRealtimeProcessor_BPIPE;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_BloombergPipeProcessor extends AmiWebScriptBaseMemberMethods<AmiWebRealtimeProcessor_BPIPE> {

	private AmiWebScriptMemberMethods_BloombergPipeProcessor() {
		super();
		addMethod(SUBSCRIBE);
		addMethod(SUBSCRIBE_LEVEL2);
		addMethod(UNSUBSCRIBE);
		addMethod(GET_SUBSCRIPTIONS);
		addMethod(UNSUBSCRIBE_ALL);
	}

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE> GET_SUBSCRIPTIONS = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE>(
			AmiWebRealtimeProcessor_BPIPE.class, "getSubscriptions", List.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor_BPIPE targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getSubscriptions();
		}
		@Override
		protected String getHelp() {
			return "Returns the list of active subscriptions";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE> SUBSCRIBE = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE>(
			AmiWebRealtimeProcessor_BPIPE.class, "subscribe", Boolean.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor_BPIPE targetObject, Object[] params, DerivedCellCalculator caller) {
			String ticker = (String) params[0];
			String fields = (String) params[1];

			if (ticker == null || fields == null) {
				return false;
			}
			targetObject.startSubscription(ticker, fields);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "ticker", "fields" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ticker", "fields" };
		}
		@Override
		protected String getHelp() {
			return "For BPIPE processors, it subscribes to the given input. Example) //blp/mktdata/ticker/BBHBEAT Index?fields=TIME,LAST_PRICE";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE> UNSUBSCRIBE = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE>(
			AmiWebRealtimeProcessor_BPIPE.class, "unsubscribe", Boolean.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor_BPIPE targetObject, Object[] params, DerivedCellCalculator caller) {
			String subscription = (String) params[0];
			if (subscription == null) {
				return false;
			}
			if (targetObject.getType() != "BPIPE") {
				return false;
			}
			targetObject.unsubscribe(subscription);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "subscription" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "subscription" };
		}
		@Override
		protected String getHelp() {
			return "For BPIPE processors, it unsubscribes for the given input. Example) //blp/mktdata/ticker/BBHBEAT Index?fields=TIME,LAST_PRICE";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE> UNSUBSCRIBE_ALL = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE>(
			AmiWebRealtimeProcessor_BPIPE.class, "unsubscribeAll", Boolean.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor_BPIPE targetObject, Object[] params, DerivedCellCalculator caller) {
			targetObject.unsubscribeAll();
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] {};
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] {};
		}
		@Override
		protected String getHelp() {
			return "For BPIPE processors, it unsubscribes from all active subscriptions.";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	private static final AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE> SUBSCRIBE_LEVEL2 = new AmiAbstractMemberMethod<AmiWebRealtimeProcessor_BPIPE>(
			AmiWebRealtimeProcessor_BPIPE.class, "subscribeLevel2", Boolean.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebRealtimeProcessor_BPIPE targetObject, Object[] params, DerivedCellCalculator caller) {
			String ticker = (String) params[0];
			String type = (String) params[1];

			if (ticker == null || type == null) {
				return false;
			}
			targetObject.startSubscriptionLevel2(ticker, type);
			return true;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "ticker", "type" };
		}

		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "ticker", "type can be MBO or MBL" };
		}
		@Override
		protected String getHelp() {
			return "For BPIPE processors, it subscribes to the given input for level 2 streaming. Example) ticker=BBHBEAT Index type=MBO";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};

	@Override
	public String getVarTypeName() {
		return "BloombergPipeProcessor";
	}
	@Override
	public String getVarTypeDescription() {
		return "A Realtime Event Processor.";
	}
	@Override
	public Class<AmiWebRealtimeProcessor_BPIPE> getVarType() {
		return AmiWebRealtimeProcessor_BPIPE.class;
	}
	@Override
	public Class<AmiWebRealtimeProcessor_BPIPE> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_BloombergPipeProcessor INSTANCE = new AmiWebScriptMemberMethods_BloombergPipeProcessor();
}
