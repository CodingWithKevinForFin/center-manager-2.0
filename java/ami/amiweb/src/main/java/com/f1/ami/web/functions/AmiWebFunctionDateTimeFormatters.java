package com.f1.ami.web.functions;

import java.util.Date;

import com.f1.ami.amicommon.functions.AmiWebFunctionFactory;
import com.f1.ami.web.AmiWebService;
import com.f1.base.Mapping;
import com.f1.utils.Formatter;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator1;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public abstract class AmiWebFunctionDateTimeFormatters extends AbstractMethodDerivedCellCalculator1 {

	private final Formatter formatter;

	public AmiWebFunctionDateTimeFormatters(int position, DerivedCellCalculator params, Formatter formatter) {
		super(position, params);
		this.formatter = formatter;
	}

	Date tmpDate = new Date();

	@Override
	public Object eval(Object o) {
		Number value = (Number) o;
		if (value == null)
			return null;

		return formatter.format(value);
	}

	public Formatter getFormatter() {
		return this.formatter;
	}

	public static class DateFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateFactory.VERIFIER;
		}
	}

	public static class DateFactory implements AmiWebFunctionFactory {

		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDate", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string date according to session preferences, returns the string.");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341724);
		}
		private AmiWebService service;

		public DateFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateFormatter(position, calcs[0], service.getFormatterManager().getDateFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class TimeFormatter extends AmiWebFunctionDateTimeFormatters {

		public TimeFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new TimeFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return TimeFactory.VERIFIER;
		}
	}

	public static class TimeFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatTime", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with minute accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699L);
		}

		public TimeFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new TimeFormatter(position, calcs[0], service.getFormatterManager().getTimeFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class TimeSecsFormatter extends AmiWebFunctionDateTimeFormatters {

		public TimeSecsFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new TimeSecsFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return TimeSecsFactory.VERIFIER;
		}
	}

	public static class TimeSecsFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatTimeWithSeconds", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with seconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699L);
		}

		public TimeSecsFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new TimeSecsFormatter(position, calcs[0], service.getFormatterManager().getTimeSecsFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class TimeMillisFormatter extends AmiWebFunctionDateTimeFormatters {

		public TimeMillisFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new TimeMillisFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return TimeMillisFactory.VERIFIER;
		}
	}

	public static class TimeMillisFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatTimeWithMillis", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with milliseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699L);
		}

		public TimeMillisFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new TimeMillisFormatter(position, calcs[0], service.getFormatterManager().getTimeMillisFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class TimeMicrosFormatter extends AmiWebFunctionDateTimeFormatters {

		public TimeMicrosFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new TimeMicrosFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return TimeMicrosFactory.VERIFIER;
		}
	}

	public static class TimeMicrosFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatTimeWithMicros", String.class, "Number unixEpochMicros");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with microseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of microseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(167234192869009L);
		}

		public TimeMicrosFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new TimeMicrosFormatter(position, calcs[0], service.getFormatterManager().getTimeMicrosFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class TimeNanosFormatter extends AmiWebFunctionDateTimeFormatters {

		public TimeNanosFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new TimeNanosFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return TimeNanosFactory.VERIFIER;
		}
	}

	public static class TimeNanosFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatTimeWithNanos", String.class, "Number unixEpochNanos");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with nanoseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of nanoseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699000L);
		}

		public TimeNanosFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new TimeNanosFormatter(position, calcs[0], service.getFormatterManager().getTimeNanosFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DateTimeFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateTimeFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateTimeFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateTimeFactory.VERIFIER;
		}
	}

	public static class DateTimeFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDateTime", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string time with minute accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341729);
		}

		public DateTimeFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateTimeFormatter(position, calcs[0], service.getFormatterManager().getDatetimeFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DateTimeSecsFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateTimeSecsFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateTimeSecsFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateTimeSecsFactory.VERIFIER;
		}
	}

	public static class DateTimeSecsFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDateTimeWithSeconds", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string date/time with seconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(50000);
		}

		public DateTimeSecsFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateTimeSecsFormatter(position, calcs[0], service.getFormatterManager().getDatetimeSecsFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DateTimeMillisFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateTimeMillisFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateTimeMillisFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateTimeMillisFactory.VERIFIER;
		}
	}

	public static class DateTimeMillisFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDateTimeWithMillis", String.class, "Number unixEpochMillis");
		static {
			VERIFIER.addDesc("Formats a number to a legible string date/time with milliseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of milliseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699L);
		}

		public DateTimeMillisFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateTimeMillisFormatter(position, calcs[0], service.getFormatterManager().getDatetimeMillisFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DateTimeMicrosFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateTimeMicrosFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateTimeMicrosFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateTimeMicrosFactory.VERIFIER;
		}
	}

	public static class DateTimeMicrosFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDateTimeWithMicros", String.class, "Number unixEpochMicros");
		static {
			VERIFIER.addDesc("Formats a number to a legible string date/time with microseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of microseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(1672341928699000L);
		}

		public DateTimeMicrosFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateTimeMicrosFormatter(position, calcs[0], service.getFormatterManager().getDatetimeMicrosFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

	public static class DateTimeNanosFormatter extends AmiWebFunctionDateTimeFormatters {

		public DateTimeNanosFormatter(int position, DerivedCellCalculator params, Formatter formatter) {
			super(position, params, formatter);
		}

		@Override
		public DerivedCellCalculator copy(DerivedCellCalculator p) {
			return new DateTimeNanosFormatter(getPosition(), p, getFormatter());
		}

		@Override
		public ParamsDefinition getDefinition() {
			return DateTimeNanosFactory.VERIFIER;
		}
	}

	public static class DateTimeNanosFactory implements AmiWebFunctionFactory {

		private AmiWebService service;
		private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDateTimeWithNanos", String.class, "Number unixEpochNanos");
		static {
			VERIFIER.addDesc("Formats a number to a legible string date/time with nanoseconds accuracy. Formatted according to session preferences");
			VERIFIER.addRetDesc("String legible date/time based on supplied time and format");
			VERIFIER.addParamDesc(0, "Number of nanoseconds since 1/1/1970 in UTC timezone or UTC object or UTCN object");
			VERIFIER.addExample(12345678900000L);
		}

		public DateTimeNanosFactory(AmiWebService service) {
			this.service = service;
		}
		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			AmiWebFunctionDateTimeFormatters r = new DateTimeNanosFormatter(position, calcs[0], service.getFormatterManager().getDatetimeNanosFormatter());
			return r;
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
