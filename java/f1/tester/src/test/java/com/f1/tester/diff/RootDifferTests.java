package com.f1.tester.diff;

import static org.junit.Assert.*;
import java.util.regex.Pattern;
import org.junit.Test;
import com.f1.utils.CH;

public class RootDifferTests {
	RootDiffer rd = new RootDiffer();

	@Test
	public void test1() {

		// base line
		assertNull(rd.diff("", ""));

		// nulls
		assertNotNull(rd.diff(null, ""));
		assertNotNull(rd.diff("", null));
		assertNull(rd.diff(null, null));

		// numbers (whole)
		assertNotNull(rd.diff(-123, 123));
		assertNull(rd.diff(123, 123));
		assertNull(rd.diff(0, 0));

		// numbers (floats to floats)

		assertNull(rd.diff(123.0, 123.0));
		assertNull(rd.diff(123.1, 123.10000001));
		assertNull(rd.diff(-123.2, -123.20000001));
		assertNull(rd.diff(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
		assertNull(rd.diff(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
		assertNull(rd.diff(Double.NaN, Double.NaN));
		assertNull(rd.diff(Float.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
		assertNull(rd.diff(Float.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY));
		assertNull(rd.diff(Float.NaN, Double.NaN));

		// numbers (floats to whole)
		assertNull(rd.diff(123.0, 123));
		assertNull(rd.diff(123, 123.0));
		assertNotNull(rd.diff(123.1, 123));
		assertNotNull(rd.diff(123, 123.1));

		// numbers (floats to whole)
		assertNull(rd.diff(123.1, 123.1));
		assertNull(rd.diff(123.0f, 123.0d));
		assertNull(rd.diff(123.1d, 123.1f));
		assertNull(rd.diff(123.1f, 123.1d));
		assertNull(rd.diff(123.1, "123.1"));
		assertNull(rd.diff("123", 123));
		assertNull(rd.diff(true, true));
		assertNotNull(rd.diff(false, true));
		assertNotNull(rd.diff(true, false));
		assertNull(rd.diff(false, false));
		assertNull(rd.diff("false", false));
		assertNull(rd.diff(false, "false"));
		assertNotNull(rd.diff("false", true));
		assertNotNull(rd.diff(true, "false"));

		// regex
		assertNull(rd.diff(true, p("true")));
		assertNull(rd.diff(true, p("true|false")));
		assertNull(rd.diff(false, p("true|false")));
		assertNull(rd.diff("true", p("true|false")));
		assertNull(rd.diff("false", p("true|false")));
		assertNotNull(rd.diff("maybe", p("true|false")));
		assertNull(rd.diff(123, p("[1-4]+")));
		assertNotNull(rd.diff(127, p("[1-4]+")));

		// map
		assertNull(rd.diff(CH.m(1, "one", 2, "two"), CH.m("1", "one", "2", "two")));
		assertDiffs(1, CH.m(1, "one", 2, "two"), CH.m("1", "one", "2", "who"));
		assertDiffs(2, CH.m(1, "one", 2, "two"), CH.m("1", "done", "2", "who"));
		assertDiffs(2, CH.m(1, "one"), CH.m("1", "done", "2", "who"));
		assertDiffs(2, CH.m(1, "one"), CH.m("2", "done"));
		assertNull(rd.diff(CH.m(1, "asdf", 2, "asdf"), p(".*")));
		assertNull(rd.diff(CH.m(1, "asdf", 2, "asdf"), p("[^z]*")));
		assertNotNull(rd.diff(CH.m(1, "asdf", 2, "aszdf"), p("[^z]*")));

		// list
		assertDiffs(0, CH.l(1, 2, 3), CH.l(1, 2, 3));
		assertDiffs(1, CH.l(1, 2, 3), CH.l(1, 2, 3, 4));
		assertDiffs(2, CH.l(1, 2, 3), CH.l(1, 2, 3, 4, 5));
	}

	@Test
	public void testNumberPatternDiffer() {
		NumberedPatternDiffer npd = new NumberedPatternDiffer();
		assertNull(npd.diff(null, "123", "123", null));
		assertNull(npd.diff(null, "123:421", "123:421", null));
		assertNotNull(npd.diff(null, "123:421", "123:421:", null));
		assertNull(npd.diff(null, "321", "123", null));
		assertNull(npd.diff(null, "321:421", "123:421", null));
		assertNotNull(npd.diff(null, "321:421", "123:421:", null));
		assertNull(npd.diff(null, ":321", ":123", null));
		assertNull(npd.diff(null, "321:421", "123:421", null));
		assertNotNull(npd.diff(null, ":321:421", "123:421:", null));
		assertNull(npd.diff(null, "1a1:321", "3a3:123", null));
		assertNull(npd.diff(null, "2a2:321:421", "3a3:123:421", null));
		assertNotNull(npd.diff(null, "3a3:321:421", "4a5:123:421:", null));
		assertNull(npd.diff(null, "1:", "1111111111111:", null));
		assertNull(npd.diff(null, "1::", "1111111111111::", null));
		assertNull(npd.diff(null, ":1:", ":1111111111111:", null));
		assertNull(npd.diff(null, ":1::", ":1111111111111::", null));
		assertNull(npd.diff(null, "::1:", "::1111111111111:", null));
		assertNull(npd.diff(null, "123;321;313@333", "0;9;8@333", null));
	}

	private Pattern p(String s) {
		return Pattern.compile(s);
	}

	public void assertDiffs(int size, Object left, Object right) {
		DiffResult result = rd.diff(left, right);
		System.out.println("-------");
		System.out.println(left);
		System.out.println(right);
		System.out.println(new DiffResultReporter().report(result));
		assertEquals(size, result == null ? 0 : result.getDiffsCount());
	}

}
