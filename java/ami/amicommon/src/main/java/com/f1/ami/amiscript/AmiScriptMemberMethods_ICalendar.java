package com.f1.ami.amiscript;

import com.f1.utils.ICalendar;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Long;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiScriptMemberMethods_ICalendar extends AmiScriptBaseMemberMethods<ICalendar> {

	public AmiScriptMemberMethods_ICalendar() {
		super();
		addMethod(this.NEW);
		addMethod(this.ADD_ATTENDEE);
		addMethod(this.ADD_EVENT);
		addMethod(this.ADD_ORGANIZER);
		addMethod(this.BUILD);
	}

	public final AmiAbstractMemberMethod<ICalendar> NEW = new AmiAbstractMemberMethod<ICalendar>(ICalendar.class, null, ICalendar.class, false) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ICalendar targetObject, Object[] params, DerivedCellCalculator caller) {
			return new ICalendar();
		}

		@Override
		protected String getHelp() {
			return "Creates an ICalendar";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	
	public final AmiAbstractMemberMethod<ICalendar> ADD_EVENT = new AmiAbstractMemberMethod<ICalendar>(ICalendar.class, "addEvent", 
			Integer.class, String.class, String.class, Long.class, Long.class, String.class, String.class, String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ICalendar targetObject, Object[] params, DerivedCellCalculator caller) {
			
			String summary = SH.toString(params[0]);
			String timezone = SH.toString(params[1]);
			Long start = Caster_Long.INSTANCE.cast(params[2]);
			Long end = Caster_Long.INSTANCE.cast(params[3]);
			String location = SH.toString(params[4]);
			String description = SH.toString(params[5]);
			String language = SH.toString(params[6]);
			String transparency = SH.toString(params[7]);
			String visibility = SH.toString(params[8]);

			targetObject.addEvent(summary, timezone, start, end, location, description, language, transparency, visibility);
			return targetObject.getEventCount();
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "summary", "timezone", "start", "end", "location", "description", "language", "transparency", "visibility" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { 
					"Summary of the event",
					"Timezone of the event", 
					"Start Timestamp of the event",
					"End timestamp of the event",
					"Location of the event",
					"Description of the event",
					"Language of the event",
					"Transparency of the event. Valid values are: OPAQUE,TRANSPARENT", 
					"Visibility of the event. Valid values are: PUBLIC,PRIVATE,CONFIDENTIAL"};
		}
		@Override
		protected String getHelp() {
			return "Adds an event to the calendar and returns the event's index";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};


	public final AmiAbstractMemberMethod<ICalendar> ADD_ATTENDEE = new AmiAbstractMemberMethod<ICalendar>(ICalendar.class, "addAttendee", ICalendar.class,
			Integer.class, String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ICalendar targetObject, Object[] params, DerivedCellCalculator caller) {
			
			final Integer eventIndex = Caster_Integer.INSTANCE.cast(params[0]);
			if (eventIndex == null || eventIndex < 0 || eventIndex >= targetObject.getEventCount())
				throw new RuntimeException("Invalid event index: " + eventIndex);
			
			final String mailTo = SH.toString(params[1]);
			final String role = SH.toString(params[2]); 
			final String cn = SH.toString(params[3]);
			targetObject.addAttendeeToEvent(eventIndex, mailTo, role, cn);
			return targetObject;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "eventIndex", "mailTo", "role", "cn" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Index of the event to be used", 
					"Email address of the attendee", 
					"Role of the attendee, default is REQ-PARTICIPANT, valid options are: REQ-PARTICIPANT, OPT-PARTICIPANT, NON-PARTICIPANT, CHAIR",
					"Common or displayable name of the attendee"};
		}

		@Override
		protected String getHelp() {
			return "Adds an attendee to the specified event ";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	
	public final AmiAbstractMemberMethod<ICalendar> ADD_ORGANIZER = new AmiAbstractMemberMethod<ICalendar>(ICalendar.class, "addOrganizer", ICalendar.class,
			Integer.class, String.class, String.class, String.class, String.class, String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ICalendar targetObject, Object[] params, DerivedCellCalculator caller) {
			final Integer eventIndex = Caster_Integer.INSTANCE.cast(params[0]);
			if (eventIndex == null || eventIndex < 0 || eventIndex >= targetObject.getEventCount())
				throw new RuntimeException("Invalid event index: " + eventIndex);
			
			final String mailTo = SH.toString(params[1]);
			final String organizer = SH.toString(params[2]);
			final String sentBy = SH.toString(params[3]);
			final String language = SH.toString(params[4]);
			final String ldap = SH.toString(params[5]);
			
			targetObject.addOrganizer(eventIndex, organizer, mailTo, sentBy, language, ldap);
			return targetObject;
		}
		@Override
		protected String[] buildParamNames() {
			return new String[] { "eventIndex", "mailTo", "organizer", "sentBy", "language", "ldap" };
		}
		@Override
		protected String[] buildParamDescriptions() {
			return new String[] { "Index of the event to be used", 
					"Required mail to address of the organizer",
					"Optional - Name of the organizer", 
					"Optional - Sent by email aliase", 
					"Optional - Language",
					"Optional - LDAP directory of the organizer"};
		}

		@Override
		protected String getHelp() {
			return "Sets the organizer information for the calendar invite";
		}
		@Override
		public boolean isReadOnly() {
			return false;
		}
	};
	
	public final AmiAbstractMemberMethod<ICalendar> BUILD = new AmiAbstractMemberMethod<ICalendar>(ICalendar.class, "build", String.class) {
		@Override
		public Object invokeMethod2(CalcFrameStack sf, ICalendar targetObject, Object[] params, DerivedCellCalculator caller) {
			return targetObject.build();
		}
		@Override
		protected String getHelp() {
			return "Creates the .ics file content as a string";
		}
		@Override
		public boolean isReadOnly() {
			return true;
		}
	};
	

	@Override
	public String getVarTypeName() {
		return "ICalendar";
	}

	@Override
	public String getVarTypeDescription() {
		return "The ICalendar file format (.ics) for creating calendar invites";
	}

	@Override
	public Class<ICalendar> getVarType() {
		return ICalendar.class;
	}

	@Override
	public Class<? extends ICalendar> getVarDefaultImpl() {
		return ICalendar.class;
	}

	public static AmiScriptMemberMethods_ICalendar INSTANCE = new AmiScriptMemberMethods_ICalendar();
}
