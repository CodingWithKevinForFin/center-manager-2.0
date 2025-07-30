package com.f1.tester.diff;

import static org.junit.Assert.*;
import java.io.IOException;
import org.junit.Test;
import com.f1.utils.IOH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.TextMatcherFactory;

public class DifferTests {

	@Test
	public void test2() throws IOException {
		ObjectToJsonConverter c = new ObjectToJsonConverter();
		Object left = c.stringToObject(IOH.readText(DifferTests.class, ".left"));
		Object right = c.stringToObject(IOH.readText(DifferTests.class, ".right"));
		RootDiffer rd = new RootDiffer();
		DiffSession ds = new DiffSession(rd);
		for (String s : new String[]{".uid", ".gid", ".dateTime", ".lastModified", ".firmOrderUid", ".clientBlockUid", ".clientTradeGroupUid", ".firmBlockUid",
				".sequenceNumber", ".taskDueTime", ".taskCreationTime", ".parentUid", ".description"})
			ds.addDiffOverride(TextMatcherFactory.DEFAULT.toMatcher(s), new NumberedPatternDiffer());
		DiffResult r = rd.diff(left, right);
		DiffResultReporter reporter = new DiffResultReporter();
		System.out.println(reporter.report(r));
	}

	@Test
	public void testNumberedPatternDiffer() {
		NumberedPatternDiffer d = new NumberedPatternDiffer();
		assertNull(d.diff(null, "123", "456", null));
		assertNull(d.diff(null, "123a", "456a", null));
		assertNull(d.diff(null, "123a", "456b", null));
		assertNull(d.diff(null, "123a.test", "456b.test", null));
		assertNotNull(d.diff(null, "123a.tesk", "456b.test", null));
	}
}
