package com.f1.ami.amicommon.functions;

import java.util.TimeZone;

import com.f1.base.Mapping;
import com.f1.utils.DateFormatNano;
import com.f1.utils.EH;
import com.f1.utils.structs.table.derived.AbstractMethodDerivedCellCalculator3;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebFunctionFormatDate extends AbstractMethodDerivedCellCalculator3 {

	public static final String DATE_FORMAT_HELP = ""
			+ "<P><U><B>Pattern</B></U></P>Words for including number components. (Note: Including fewer repeating characters will result in variable-width formatting while extra repeating chars will pad with unncessary extra zeros):<UL>"//
			+ "<li><B>yy</B> - 2 Digit Year"//
			+ "<li><B>yyyy</B> - 4 Digit Year"//
			+ "<li><B>MM</B> - Month in year"//
			+ "<li><B>ww</B> - Week in year"//
			+ "<li><B>WW</B> - Week in month"//
			+ "<li><B>DDD</B> - Day in year"//
			+ "<li><B>dd</B> - Day in month"//
			+ "<li><B>FF</B> - Day of week in month"//
			+ "<li><B>HH</B> - Hour in day ranging from 0-23 "//
			+ "<li><B>kk</B> - Hour in day ranging from 1-24"//
			+ "<li><B>KK</B> - Hour in AM/PM ranging from 0-11"//
			+ "<li><B>hh</B> - Hour in AM/PM ranging from 1-12"//
			+ "<li><B>mm</B> - Minute in hour"//
			+ "<li><B>ss</B> - Second in minute"//
			+ "<li><B>SSS</B> - Millisecond"//
			+ "<li><B>rrr</B> - Microsecond (Note: See below section on supplying unix epoch time) "//
			+ "<li><B>RRR</B> - Nanosecond (Note: See below section on supplying unix epoch time) "//
			+ "</UL><P>Words for including text components:<UL>"//
			+ "<li><B>MMM</B> - Month in year as 3 letter code. Ex: <I>Jan</I>"//
			+ "<li><B>MMMM</B> - Month in year as full name. Ex: <I>January</I>"//
			+ "<li><B>E</B> - Day in week as 3 letter code. Ex: <I>Mon</I>"//
			+ "<li><B>EEEE</B> - Day in week as full name. Ex: <I>Monday</I>"//
			+ "<li><B>z</B> - Time zone code Ex: <I>EST</I>"//
			+ "<li><B>zzzz</B> - Time zone full name. Ex: <I>Eastern Standard Time</I>"//
			+ "<li><B>Z</B> - Timezone offset from UTC in hours and minutes, of the form [+-]hhmm. Ex: <I>-0500</I> "//
			+ "<li><B>a</B> - AM/PM marker. Ex: <I>AM</I>"//
			+ "<li><B>G</B> - Era designator. Ex: <I>AD</I>"//
			+ "</ul>Syntax for including literal text:<UL>"//
			+ "<li><B>''</B> - Single quote. Ex: <i>'</i>"//
			+ "<li><B>'Some Text'</B> - Literal text with quotes removed. Ex: <i>Some Text</i>"//
			+ "<li><B><I>non-letters</I></B> - Interpreted as a literal. Ex: <i>:</i>"//
			+ "</UL><P><U><B>Supplying unix epoch time</B></U></P>When passing in the unixEpoch as a UTC or UTCN, the formatter will automatically understand the supplied precision.  When Supplying as a number it is normally interpreted in milliseconds, unless the pattern calls for greater precision by using the letters <B>r</B> or <B>R</B>:<UL>" //
			+ "<LI><B>Nanoseconds</B> -If the pattern contains the letter <B>R</B> the unix epoch time should be in <B>nanoseconds</B> since 1/1/1970"//
			+ "<LI><B>Microseconds</B> - If the pattern has <B>r</B> letter but not the letter <B>R</B>, the unix epoch time should be in <B>microseconds</B> since 1/1/1970"//
			+ "<LI><B>Milliseconds</B> - If the pattern does not have the letter <B>r</B> nor the letter <B>R</B> the unix epoch time should be in <B>milliseconds</B> since 1/1/1970</UL>";

	private static final ParamsDefinition VERIFIER = new ParamsDefinition("formatDate", String.class, "Number unixEpoch,String pattern,String timezone");

	static {
		VERIFIER.addDesc(
				"Formats the supplied unix epoch time and returns a legible string date/time based on the supplied pattern and timezone. The pattern is constructed by combining words listed below "
						+ DATE_FORMAT_HELP);
		VERIFIER.addParamDesc(0,
				"Number of milli, micro or nanoseconds since 1/1/1970 in UTC timezone, depending on pattern. If the pattern contains n then nanoseconds, If the pattern contains u then microseconds, otherwise milliseconds.");
		VERIFIER.addParamDesc(1, "Pattern string. See Letters above for details.");
		VERIFIER.addParamDesc(2, "TimeZone to format time in");
		VERIFIER.addExample(1464215270786L, "yyyyMMdd-HH:mm:ss.SSS", "EST5EDT");
		VERIFIER.addExample(1464215270786L, "MMMM/dd/yyyy-HH:mm:ss zzz", "EST5EDT");
		VERIFIER.addExample(1540407446123456L, "MMMM/dd/yyyy-HH:mm:ss.SSS.rrr zzz", "EST5EDT");
		VERIFIER.addExample(1540407446123456789L, "MMMM/dd/yyyy-HH:mm:ss.SSS.rrr.RRR zzz", "EST5EDT");
		VERIFIER.addExample(113232L, "HH:mm:ss", "UTC");
	}

	@Override
	public ParamsDefinition getDefinition() {
		return VERIFIER;
	}
	public AmiWebFunctionFormatDate(int position, DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		super(position, p0, p1, p2);
		evalConsts();
	}

	@Override
	protected DateFormatNano get1(Object o1) {
		try {
			return new DateFormatNano((String) o1);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	protected TimeZone get2(Object o2) {
		return EH.getTimeZoneOrGMT((String) o2);
	}

	@Override
	public Object eval(Object o0, Object o1, Object o2) {
		Number value = (Number) o0;
		DateFormatNano sdf = (DateFormatNano) o1;
		if (sdf == null)
			return "<BAD_PATTERN>";
		TimeZone tz = (TimeZone) o2;
		if (tz == null)
			return null;
		sdf.setTimeZone(tz);
		return sdf.format(value);
	}

	@Override
	public DerivedCellCalculator copy(DerivedCellCalculator p0, DerivedCellCalculator p1, DerivedCellCalculator p2) {
		return new AmiWebFunctionFormatDate(getPosition(), p0, p1, p2);
	}

	public static class Factory implements AmiWebFunctionFactory {

		@Override
		public DerivedCellCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new AmiWebFunctionFormatDate(position, calcs[0], calcs[1], calcs[2]);
		}

		@Override
		public ParamsDefinition getDefinition() {
			return VERIFIER;
		}

	}

}
