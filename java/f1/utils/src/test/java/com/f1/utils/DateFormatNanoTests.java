package com.f1.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import org.junit.Test;

import junit.framework.Assert;

public class DateFormatNanoTests {
	@Test(expected = IllegalArgumentException.class)
	public void testBadQuotes() throws ParseException {
		Assert.assertEquals("---", test("hh'", 0));
	}
	@Test(expected = IllegalArgumentException.class)
	public void testBadQuotes2() throws ParseException {
		Assert.assertEquals("---", test("hh'''", 0));
	}

	@Test
	public void testParse() throws ParseException {
		String s = "2011/03/05 003:005:006";
		SimpleDateFormat f1 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		DateFormatNano f2 = new DateFormatNano("yyyy/MM/dd HH:mm:ss");
		DateFormatNano f3 = new DateFormatNano("yyyy/MM/dd HH:mm:ss.rrr.RRR");
		Assert.assertEquals(f2.parse(s).getTime(), f2.parseToNanos(s).longValue() / 1000000L);
		Assert.assertEquals(f1.parse(s), f2.parse(s));
		Assert.assertEquals(f1.parse(s).getTime(), f2.parse(s).getTime());
		Assert.assertEquals(f1.parse(s).getTime(), f2.parseToNanos(s).longValue() / 1000000L);
		Assert.assertEquals(f1.parse(s).getTime() * 1000000L, f3.parseToNanos(s + ".000.000").longValue());
		Assert.assertEquals(f1.parse(s).getTime() * 1000000L + 123456L, f3.parseToNanos(s + ".123.456").longValue());
	}
	@Test
	public void testNeg() throws ParseException {
		Assert.assertEquals("1970/01/01 00:00:00.000.000,000", test("yyyy/MM/dd HH:mm:ss.SSS.rrr,RRR", 0L, "UTC"));
		Assert.assertEquals("1969/12/31 23:59:59.999", test("yyyy/MM/dd HH:mm:ss.SSS", -1L, "UTC"));
		Assert.assertEquals("1969/12/31 23:59:59.999.999", test("yyyy/MM/dd HH:mm:ss.SSS.rrr", -1L, "UTC"));
		Assert.assertEquals("1969/12/31 23:59:59.999.999,999", test("yyyy/MM/dd HH:mm:ss.SSS.rrr,RRR", -1L, "UTC"));
		Assert.assertEquals("1969/12/31 23:59:59.999.000", test("yyyy/MM/dd HH:mm:ss.SSS.rrr", -1000L, "UTC"));
	}

	@Test
	public void testEsc() throws ParseException {
		Assert.assertEquals("'", test("''", 0));
		Assert.assertEquals("-rob-", test("-'rob'-", 0));
		Assert.assertEquals("---", test("---", 0));
		Assert.assertEquals("--'-", test("--''-", 0));
		Assert.assertEquals("-rob'-", test("-'rob'''-", 0));
		Assert.assertEquals("-rob''-'", test("-'rob'''''-''", 0));
		Assert.assertEquals("yyyy/MM/dd HH:mm:ss.S.r,R", test("'yyyy/MM/dd HH:mm:ss.S.r,R'", 0));
		Assert.assertEquals("", test("", 0));
		Assert.assertEquals("'", test("''", 0));
		Assert.assertEquals("0", test("r", 0));
		Assert.assertEquals("0", test("R", 0));
		Assert.assertEquals("10", test("r", 10));
		Assert.assertEquals("10", test("R", 10));
		Assert.assertEquals("100", test("r", 100));
		Assert.assertEquals("100", test("R", 100));
		Assert.assertEquals("0", test("r", 1000));
		Assert.assertEquals("0", test("R", 1000));
		Assert.assertEquals("'0'", test("''r''", 0));
		long d = parse("2011/03/05 03:05:06", "EST5EDT");
		long d2 = parse("2011/03/05 11:15:16", "EST5EDT");
		Assert.assertEquals("2011/03/05 03:05:06", test("yyyy/MM/dd HH:mm:ss", d));
		Assert.assertEquals("2011/03/05 3:5:6", test("yyyy/MM/dd H:m:s", d));
		Assert.assertEquals("2011/03/05 11:15:16", test("yyyy/MM/dd H:m:s", d2));
		Assert.assertEquals("2011/03/05 003:005:006", test("yyyy/MM/dd HHH:mmm:sss", d));
		Assert.assertEquals("2011/03/05 003:005:006", test("y/MM/dd HHH:mmm:sss", d));
		Assert.assertEquals("02011/03/05 003:005:006", test("yyyyy/MM/dd HHH:mmm:sss", d));
		Assert.assertEquals("2011/03/05 03:05:06 AM", test("y/MM/dd HH:mm:ss a", d));
		Assert.assertEquals("2011/03/05 03:05:06 AM", test("y/MM/dd HH:mm:ss aa", d));
		Assert.assertEquals("2011/03/05 03:05:06 AM", test("y/MM/dd HH:mm:ss aaaa", d));
		Assert.assertEquals("2011/3/05 03:05:06", test("yyyy/M/dd HH:mm:ss", d));
		Assert.assertEquals("2011/03/05 03:05:06", test("yyyy/MM/dd HH:mm:ss", d));
		Assert.assertEquals("2011/Mar/05 03:05:06", test("yyyy/MMM/dd HH:mm:ss", d));
		Assert.assertEquals("2011/March/05 03:05:06", test("yyyy/MMMM/dd HH:mm:ss", d));
		Assert.assertEquals("2011/March/05 03:05:06", test("yyyy/MMMMM/dd HH:mm:ss", d));
		Assert.assertEquals("2011/March/05 03:05:06", test("yyyy/MMMMMMMM/dd HH:mm:ss", d));
		Assert.assertEquals("Sat", test("E", d));
		Assert.assertEquals("Sat", test("EE", d));
		Assert.assertEquals("Sat", test("EEE", d));
		Assert.assertEquals("Saturday", test("EEEE", d));
		Assert.assertEquals("EST", test("z", d));
		Assert.assertEquals("EST", test("zz", d));
		Assert.assertEquals("EST", test("zzz", d));
		Assert.assertEquals("Eastern Standard Time", test("zzzz", d));
		Assert.assertEquals("-0500", test("Z", d));
		Assert.assertEquals("-0500", test("ZZ", d));
		Assert.assertEquals("-0500", test("ZZZ", d));
		Assert.assertEquals("-0500", test("ZZZZZZZZZZ", d));
	}
	@Test
	public void test() throws ParseException {
		Assert.assertEquals("123", test("RRR", 123L));
		Assert.assertEquals("123", test("rrr", 123L));
		Assert.assertEquals("123.456", test("rrr.RRR", 123456L));
		Assert.assertEquals("123.456.789", test("SSS.rrr.RRR", 123456789L));
		Assert.assertEquals("789", test("SSS", 123456789L));
		Assert.assertEquals("56.789", test("ss.SSS", 56789L));
		Assert.assertEquals("56.789.123", test("ss.SSS.rrr", 56789123L));
		Assert.assertEquals("56.789.123.543", test("ss.SSS.rrr.RRR", 56789123543L));
		Assert.assertEquals("56,789,123,543", test("ss,SSS,rrr,RRR", 56789123543L));
		Assert.assertEquals("56,789,123,543", test("ss,S,r,R", 56789123543L));
		Assert.assertEquals("56,0789,0123,0543", test("ss,SSSS,rrrr,RRRR", 56789123543L));

		for (String date : new String[] { "2011/03/05 03:05:56", "2011/09/05 03:05:56", "2018/10/24 18:57:26", "1900/01/01 14:40:55", "1970/01/01 00:00:00",
				"1800/05/05 12:30:59" }) {
			for (String s : new String[] { "EST5EDT", "UTC", "GMT" }) {
				long d = parse(date, s);
				System.out.println(d);
				Assert.assertEquals(date + "", test("yyyy/MM/dd HH:mm:ss", d, s));
				Assert.assertEquals(date + ".5", test("yyyy/MM/dd HH:mm:ss.S", d + 5, s));
				Assert.assertEquals(date + ".55", test("yyyy/MM/dd HH:mm:ss.S", d + 55, s));
				Assert.assertEquals(date + ".555", test("yyyy/MM/dd HH:mm:ss.S", d + 555, s));
				Assert.assertEquals(date + ".0", test("yyyy/MM/dd HH:mm:ss.S", d, s));
				Assert.assertEquals(date + ".0.0", test("yyyy/MM/dd HH:mm:ss.S.r", d * 1000, s));
				Assert.assertEquals(date + ".0.0,0", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L, s));
				Assert.assertEquals(date + ".00", test("yyyy/MM/dd HH:mm:ss.SS", d, s));
				Assert.assertEquals(date + ".00.00", test("yyyy/MM/dd HH:mm:ss.SS.rr", d * 1000, s));
				Assert.assertEquals(date + ".00.00,00", test("yyyy/MM/dd HH:mm:ss.SS.rr,RR", d * 1000000L, s));
				Assert.assertEquals(date + ".000", test("yyyy/MM/dd HH:mm:ss.SSS", d, s));
				Assert.assertEquals(date + ".000.000", test("yyyy/MM/dd HH:mm:ss.SSS.rrr", d * 1000, s));
				Assert.assertEquals(date + ".000.000,000", test("yyyy/MM/dd HH:mm:ss.SSS.rrr,RRR", d * 1000000L, s));
				Assert.assertEquals(date + ".20", test("yyyy/MM/dd HH:mm:ss.S", d + 20, s));
				Assert.assertEquals(date + ".20.30", test("yyyy/MM/dd HH:mm:ss.S.r", d * 1000 + 20030, s));
				Assert.assertEquals(date + ".20.30,40", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 20030040, s));
				Assert.assertEquals(date + ".200", test("yyyy/MM/dd HH:mm:ss.S", d + 200, s));
				Assert.assertEquals(date + ".200.300", test("yyyy/MM/dd HH:mm:ss.S.r", d * 1000 + 200300, s));
				Assert.assertEquals(date + ".200.300,400", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 200300400L, s));
				Assert.assertEquals(date + ".123", test("yyyy/MM/dd HH:mm:ss.S", d + 123, s));
				Assert.assertEquals(date + ".123.456", test("yyyy/MM/dd HH:mm:ss.S.r", d * 1000 + 123456, s));
				Assert.assertEquals(date + ".123.456,789", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 123456789, s));
				Assert.assertEquals(date + ".999.999,999", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 999999999, s));
				Assert.assertEquals(date + ".0.0,1", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 1, s));
				Assert.assertEquals(date + ".1.1,1", test("yyyy/MM/dd HH:mm:ss.S.r,R", d * 1000000L + 1001001, s));
			}
		}
	}
	private long parse(String time, String tz) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone(tz));
		return sdf.parse(time).getTime();
	}
	private String test(String pattern, long l) {
		return test(pattern, l, "EST5EDT");
	}
	private String test(String pattern, long l, String tz) {
		DateFormatNano df = new DateFormatNano(pattern);
		df.setTimeZone(TimeZone.getTimeZone(tz));
		return df.format(l);
	}
}
