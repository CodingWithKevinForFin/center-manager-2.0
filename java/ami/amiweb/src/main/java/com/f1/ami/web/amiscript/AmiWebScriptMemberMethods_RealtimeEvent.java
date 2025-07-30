package com.f1.ami.web.amiscript;

import java.util.HashMap;

import com.f1.ami.amiscript.AmiAbstractMemberMethod;
import com.f1.ami.web.dm.AmiWebDmRealtimeEvent;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiWebScriptMemberMethods_RealtimeEvent extends AmiWebScriptBaseMemberMethods<AmiWebDmRealtimeEvent> {

	private AmiWebScriptMemberMethods_RealtimeEvent() {
		super();
		addMethod(GET_ACTION, "action");
		addMethod(GET_FEED, "feed");
		addMethod(GET_NEXT, "next");
		addMethod(GET_VALUES, "values");
		addMethod(GET_VALUE);
		addMethod(IS_INSERT);
		addMethod(IS_UPDATE);
		addMethod(IS_DELETE);
		addMethod(IS_SNAPSHOT);
		addMethod(IS_ADD_COLUMN);
		addMethod(IS_DROP_COLUMN);
		addMethod(IS_MODIFY_COLUMN);
		addMethod(IS_ADD);
	}

	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_INSERT = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "isInsert",
			Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.INSERT;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is of type insert; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_UPDATE = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "isUpdate",
			Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.UPDATE;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is of type update; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_DELETE = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "isDelete",
			Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.DELETE;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is of type delete; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_SNAPSHOT = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "isSnapshot",
			Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.SNAPSHOT;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is of type snapshot; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_ADD_COLUMN = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class,
			"isAddColumn", Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.ADD_COLUMN;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is adding a column to the schema; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_MODIFY_COLUMN = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class,
			"isModifyColumn", Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.MODIFY_COLUMN;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is modifying a column within the schema; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_DROP_COLUMN = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class,
			"isDropColumn", Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.DROP_COLUMN;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is dropping a column from the schema; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> IS_ADD = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "isAdd",
			Boolean.class) {

		@Override
		public boolean isReadOnly() {
			return false;
		}

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.status == AmiWebDmRealtimeEvent.SNAPSHOT || targetObject.status == AmiWebDmRealtimeEvent.INSERT;
		}

		@Override
		protected String getHelp() {
			return "Returns true if the event is of type snapshot or insert; false otherwise.";
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> GET_ACTION = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "getAction",
			Character.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			switch (targetObject.status) {
				case AmiWebDmRealtimeEvent.INSERT:
					return 'I';
				case AmiWebDmRealtimeEvent.SNAPSHOT:
					return 'S';
				case AmiWebDmRealtimeEvent.UPDATE:
					return 'U';
				case AmiWebDmRealtimeEvent.DELETE:
					return 'D';
				case AmiWebDmRealtimeEvent.ADD_COLUMN:
					return 'A';
				case AmiWebDmRealtimeEvent.DROP_COLUMN:
					return 'R';
				case AmiWebDmRealtimeEvent.MODIFY_COLUMN:
					return 'M';
				case AmiWebDmRealtimeEvent.TRUNCATE:
					return 'T';
			}
			return null;
		}

		@Override
		protected String getHelp() {
			return "Returns this action: S=Snapshot, I=Insert, U=Update, D=Delete, A=AddColumn, R=DropColumn, M=ModifyColumn";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> GET_NEXT = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "getNext",
			AmiWebDmRealtimeEvent.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.next;
		}

		@Override
		protected String getHelp() {
			return "Returns the next event.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}

	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> GET_FEED = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "getFeed",
			String.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.getTypeName();
		}

		@Override
		protected String getHelp() {
			return "Returns the name of the feed.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> GET_VALUES = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "getValues",
			HashMap.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			HashMap<String, Object> m = new HashMap<String, Object>();
			targetObject.fill(m);
			return m;
		}

		@Override
		protected String getHelp() {
			return "Returns a map of the values associated with this event.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	private static final AmiAbstractMemberMethod<AmiWebDmRealtimeEvent> GET_VALUE = new AmiAbstractMemberMethod<AmiWebDmRealtimeEvent>(AmiWebDmRealtimeEvent.class, "getValue",
			String.class, Object.class) {

		@Override
		public Object invokeMethod2(CalcFrameStack sf, AmiWebDmRealtimeEvent targetObject, Object[] params, DerivedCellCalculator caller) {
			String key = (String) params[0];
			return targetObject.getValue(key);
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "key" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "corresponds to the column name" };
		}

		@Override
		protected String getHelp() {
			return "Returns the value associated with this event given the key.";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};

	@Override
	public String getVarTypeName() {
		return "RealtimeEvent";
	}

	@Override
	public String getVarTypeDescription() {
		return "AMI Script Class to represent a Realtime Event";
	}

	@Override
	public Class<AmiWebDmRealtimeEvent> getVarType() {
		return AmiWebDmRealtimeEvent.class;
	}

	@Override
	public Class<? extends AmiWebDmRealtimeEvent> getVarDefaultImpl() {
		return null;
	}

	public final static AmiWebScriptMemberMethods_RealtimeEvent INSTANCE = new AmiWebScriptMemberMethods_RealtimeEvent();
}
