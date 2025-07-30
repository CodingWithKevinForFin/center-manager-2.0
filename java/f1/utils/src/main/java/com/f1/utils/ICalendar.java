package com.f1.utils;

import java.util.ArrayList;
import java.util.TimeZone;

//See https://www.ietf.org/rfc/rfc2445.txt for format specifications
public class ICalendar {

	private static final String HEADER = "BEGIN:VCALENDAR";
	private static final String VERSION = "VERSION:2.0";
	private static final String PRODID = "PRODID:-//3Forge//AMI//EN";
	private static final String FOOTER = "END:VCALENDAR";
	private static final String LINE_END = "\r\n";
	private static final String CALSCALE = "CALSCALE:GREGORIAN";
	private static final String METHOD = "METHOD";
	private static final String METHOD_REQUEST = "REQUEST";

	private static class EventClass {
		private static final String CLASS_HEADER = "CLASS:";
		private static final String CLASS_PUBLIC = "PUBLIC";
		private static final String CLASS_CONFIDENTIAL = "CONFIDENTIAL";
		private static final String CLASS_PRIVATE = "PRIVATE";

		private String eventClass = CLASS_PRIVATE;

		public void setVisibility(String visibility) {
			visibility = SH.toUpperCase(visibility);
			if (SH.equals(CLASS_PUBLIC, visibility))
				this.eventClass = CLASS_PUBLIC;
			else if (SH.equals(CLASS_CONFIDENTIAL, visibility))
				this.eventClass = CLASS_CONFIDENTIAL;
			else if (SH.equals(CLASS_PRIVATE, visibility))
				this.eventClass = CLASS_PRIVATE;
		}

		public String build() {
			return CLASS_HEADER + this.eventClass + LINE_END;
		}
	}

	private static class TransparencyClass {
		private static final String CLASS_HEADER = "TRANSP:";
		private static final String CLASS_OPAQUE = "OPAQUE";
		private static final String CLASS_TRANSPARENT = "TRANSPARENT";

		private String transparency = CLASS_OPAQUE;

		public void setVisibility(String visibility) {
			visibility = SH.toUpperCase(visibility);
			if (SH.equals(CLASS_TRANSPARENT, visibility))
				this.transparency = CLASS_TRANSPARENT;
			else if (SH.equals(CLASS_OPAQUE, visibility))
				this.transparency = CLASS_OPAQUE;
		}

		public String build() {
			return SH.is(this.transparency) ? CLASS_HEADER + this.transparency : "";
		}
	}

	private static class ICalendarAttendee {

		private static final String HEADER = "ATTENDEE";
		private static final String ROLE = "ROLE";
		private static final String ROLE_REQ = "REQ-PARTICIPANT";
		private static final String ROLE_OPT = "OPT-PARTICIPANT";
		private static final String ROLE_NON = "NON-PARTICIPANT";
		private static final String ROLE_CHAIR = "CHAIR";
		private static final String CN = "CN";
		private static final String MAIL_TO = "mailto";
		private static final String PARTICIPANT_STATUS = "PARTSTAT";
		private static final String PARTICIPANT_STATUS_NEEDS_ACTION = "NEEDS-ACTION";
		private static final String RSVP = "RSVP";

		private String role = ROLE_REQ;
		public String cn = "";
		public String mailTo = "";
		public String status = PARTICIPANT_STATUS_NEEDS_ACTION;
		public boolean rsvp = true;

		public void setRole(String _role) {
			_role = SH.toUpperCase(_role);
			if (SH.equals(ROLE_CHAIR, _role))
				this.role = ROLE_CHAIR;
			else if (SH.equals(ROLE_NON, _role))
				this.role = ROLE_NON;
			else if (SH.equals(ROLE_OPT, _role))
				this.role = ROLE_OPT;
			else if (SH.equals(ROLE_REQ, _role))
				this.role = ROLE_REQ;
		}

		public String build() {
			if (SH.isnt(mailTo))
				return "";
			StringBuilder sb = new StringBuilder();
			sb.append(HEADER);
			sb.append(';').append(ROLE).append('=').append(role);
			sb.append(';').append(PARTICIPANT_STATUS).append('=').append(status);
			sb.append(';').append(RSVP).append('=').append(rsvp == true ? "TRUE" : "FALSE");
			if (SH.is(cn))
				sb.append(';').append(CN).append('=').append(cn);
			sb.append(':').append(MAIL_TO).append(':').append(mailTo).append(LINE_END);
			return sb.toString();
		}
	}

	private static class ICalendarOrganizer {

		private static final String HEADER = "ORGANIZER";
		private static final String CN_PARAM = "CN";
		private static final String DIR_PARAM = "DIR";
		private static final String SENTBY_PARAM = "SENT-BY";
		private static final String MAILTO_PARAM = "mailto";
		private static final String LANGUAGE_PARAM = "LANGUAGE";

		public String organizer = "";
		public String mailTo = "";
		public String sentBy = "";
		public String language = "";
		public String ldap = "";

		public String build() {
			if (SH.isnt(mailTo))
				return "";
			StringBuilder sb = new StringBuilder();
			sb.append(HEADER);
			if (SH.is(sentBy))
				sb.append(";").append(SENTBY_PARAM).append('=').append(sentBy);
			if (SH.is(organizer))
				sb.append(";").append(CN_PARAM).append('=').append(organizer);
			if (SH.is(ldap))
				sb.append(";").append(DIR_PARAM).append("=\"").append(ldap).append('\"');
			if (SH.is(language))
				sb.append(";").append(LANGUAGE_PARAM).append('=').append(language);

			sb.append(":").append(MAILTO_PARAM).append(':').append(mailTo).append(LINE_END);
			return sb.toString();
		}
	}

	private static class ICalendarEvent {

		private static final String HEADER = "BEGIN:VEVENT";
		private static final String FOOTER = "END:VEVENT";
		private static final String CREATED = "CREATED";
		private static final String DTSTAMP = "DTSTAMP";
		private static final String DTSTART = "DTSTART";
		private static final String DTEND = "DTEND";
		private static final String UID = "UID:3FORGE-";
		private static final String SUMMARY = "SUMMARY:";
		private static final String LOCATION = "LOCATION:";
		private static final String DESCRIPTION = "DESCRIPTION:";
		private static final String LANGUAGE = "LANGUAGE:";

		public String timezone;
		public Long start;
		public Long end;
		public String summary;
		public String location;
		public String description;
		public String language;

		public EventClass event = new EventClass();
		public TransparencyClass transparency = new TransparencyClass();
		private ICalendarOrganizer organizer = new ICalendarOrganizer();

		private ArrayList<ICalendarAttendee> attendees = new ArrayList<ICalendarAttendee>();

		public ICalendarEvent() {
		}

		public String build() {
			Long now = EH.currentTimeMillis();
			StringBuilder sb = new StringBuilder();
			sb.append(HEADER).append(LINE_END);
			sb.append(CREATED).append(toICalendarDateString(timezone, now)).append(LINE_END);
			if (start != null)
				sb.append(DTSTART).append(toICalendarDateString(timezone, start)).append(LINE_END);
			if (end != null)
				sb.append(DTEND).append(toICalendarDateString(timezone, end)).append(LINE_END);
			sb.append(DTSTAMP).append(toICalendarDateString(timezone, now)).append(LINE_END);
			sb.append(organizer.build());
			sb.append(UID).append(now).append(LINE_END);
			if (SH.is(this.summary))
				sb.append(SUMMARY).append(this.summary).append(LINE_END);
			sb.append(this.transparency.build());
			if (SH.is(this.location))
				sb.append(LOCATION).append(this.location).append(LINE_END);
			if (SH.is(this.description))
				sb.append(DESCRIPTION).append(this.description).append(LINE_END);
			if (SH.is(this.language))
				sb.append(LANGUAGE).append(this.language).append(LINE_END);
			sb.append(this.event.build());
			for (final ICalendarAttendee a : attendees)
				sb.append(a.build());
			sb.append(FOOTER).append(LINE_END);
			return sb.toString();
		}
	}

	private ArrayList<ICalendarEvent> events = new ArrayList<ICalendarEvent>();
	private String method = METHOD_REQUEST;

	public static final String toICalendarDateString(final String timezone, final Long timestamp) {
		TimeZone tz = EH.getTimeZoneOrGMT(timezone);
		Long unixTimestamp = tz == null ? timestamp : timestamp + tz.getOffset(timestamp);
		Boolean onlyDate = unixTimestamp % 86400000L == 0;
		DateFormatNano sdf = onlyDate ? new DateFormatNano("yyyyMMdd") : new DateFormatNano("yyyyMMdd'T'HHmmss'Z'");
		if (tz == null) {
			sdf.setTimeZone(EH.getTimeZoneOrGMT("UTC"));
			return (onlyDate ? ";VALUE=DATE:" : ":") + sdf.format(timestamp);
		} else {
			sdf.setTimeZone(tz);
			return ";TZID=" + tz.getID() + (onlyDate ? ";VALUE=DATE:" : ":") + sdf.format(timestamp);
		}
	}

	public ICalendar() {
	}

	public int getEventCount() {
		return this.events.size();
	}

	public void addOrganizer(final int eventIndex, final String organizer, final String mailTo, final String sentBy, final String language, final String ldap) {
		final ICalendarEvent e = this.events.get(eventIndex);
		if (e == null)
			throw new RuntimeException("Event does not exist at this index");
		e.organizer.organizer = organizer;
		e.organizer.mailTo = mailTo;
		if (SH.is(sentBy))
			e.organizer.sentBy = SH.indexOf(sentBy, ICalendarOrganizer.MAILTO_PARAM, 0) == -1 ? "\"" + ICalendarOrganizer.MAILTO_PARAM + ":" + sentBy + "\"" : sentBy;
		e.organizer.language = language;
		if (SH.is(ldap))
			e.organizer.ldap = "\"" + ldap + "\"";
	}

	public void addEvent(final String summary, final String timezone, final Long start, final Long end, final String location, final String description, final String language,
			final String transparency, final String visibility) {
		final ICalendarEvent e = new ICalendarEvent();
		e.start = start;
		e.end = end;
		e.location = location;
		e.description = description;
		e.language = language;
		e.event.setVisibility(visibility);
		e.transparency.setVisibility(transparency);
		e.timezone = timezone;
		e.summary = summary;
		this.events.add(e);
	}

	public void addAttendeeToEvent(final int eventIndex, final String mailTo, final String role, final String cn) {
		final ICalendarEvent e = this.events.get(eventIndex);
		if (e == null)
			throw new RuntimeException("Event does not exist at this index");
		final ICalendarAttendee attendee = new ICalendarAttendee();
		attendee.setRole(role);
		attendee.cn = cn;
		attendee.mailTo = mailTo;
		e.attendees.add(attendee);
	}

	public String build() {
		StringBuilder sb = new StringBuilder();
		sb.append(HEADER).append(LINE_END);
		sb.append(PRODID).append(LINE_END);
		sb.append(VERSION).append(LINE_END);
		sb.append(CALSCALE).append(LINE_END);
		sb.append(METHOD).append(':').append(this.method).append(LINE_END);
		for (final ICalendarEvent event : events)
			sb.append(event.build());
		sb.append(FOOTER).append(LINE_END);
		return sb.toString();
	}

}