package com.f1.utils.fix.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.f1.utils.Duration;
import com.f1.utils.fix.FixMap;
import com.f1.utils.fix.FixParseException;

public class FixMapBuilderTests {

	private static BasicFixParser parser;
	private static BasicFix44Builder builder;

	static {
		BasicFixDictionary dictionary = new BasicFixDictionary();
		dictionary.putFixTag(new BasicFixTag(1, "ACCOUNT"));
		dictionary.putFixTag(new BasicFixTag(6, "AVG_PX"));
		dictionary.putFixTag(new BasicFixTag(8, "BEGINSTRING"));
		dictionary.putFixTag(new BasicFixTag(11, "CLIENT_ORDER_ID"));
		dictionary.putFixTag(new BasicFixTag(38, "ORDER_QYT"));
		dictionary.putFixTag(new BasicFixTag(40, "ORDTYPE"));
		dictionary.putFixTag(new BasicFixTag(35, "MSGTYPE"));
		dictionary.putFixTag(new BasicFixTag(49, "SENDERCOMPID"));
		dictionary.putFixTag(new BasicFixTag(56, "TARGETCOMPID"));
		dictionary.putFixTag(new BasicFixTag(54, "SIDE"));
		dictionary.putFixTag(new BasicFixTag(59, "TIF"));
		dictionary.putFixTag(new BasicFixTag(97, "POSSRESEND"));
		dictionary.putFixTag(new BasicFixTag(52, "SENDINGTIME"));
		dictionary.putFixTag(new BasicFixTag(50, "SENDERSUBID"));
		dictionary.putFixTag(new BasicFixTag(55, "SYMBOL"));
		dictionary.putFixTag(new BasicFixTag(21, "HANDINST"));
		dictionary.putFixTag(new BasicFixTag(9, "BODYLENGTH"));
		dictionary.putFixTag(new BasicFixTag(34, "MSGSEQNUM"));
		dictionary.putFixTag(new BasicFixTag(44, "PRICE"));
		dictionary.putFixTag(new BasicFixTag(47, "CAPACITY"));
		dictionary.putFixTag(new BasicFixTag(60, "TIMESTAMP"));
		dictionary.putFixTag(new BasicFixTag(78, "NO_ALLOCS", 79, new int[]{80}));
		dictionary.putFixTag(new BasicFixTag(79, "ALLOC_ACCOUNT"));
		dictionary.putFixTag(new BasicFixTag(80, "ALLOC_QTY"));
		dictionary.putFixTag(new BasicFixTag(12396, "NO_BLOCKS", 12397, new int[]{12406, 12403}));
		dictionary.putFixTag(new BasicFixTag(12397, "B1"));
		dictionary.putFixTag(new BasicFixTag(12403, "B2"));
		dictionary.putFixTag(new BasicFixTag(10, "CHECKSUM"));
		dictionary.putFixTag(new BasicFixTag(12406, "NO_SPLIT", 38, new int[]{}));
		parser = new BasicFixParser(dictionary, '|', '=');
		builder = new BasicFix44Builder(dictionary, '|');
	}

	@Test
	public void testSimple() {
		FixMap m = parser.parse("1=1234|6=32.4|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
	}

	@Test
	public void testGroup() {
		FixMap m = parser.parse("1=1234|6=32.4|78=1|79=555|80=1000|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(1, m.getGroups(78).size());
		assertEquals("555", m.getGroups(78).get(0).get(79));
		assertEquals("1000", m.getGroups(78).get(0).get(80));
	}

	@Test
	public void testGroup2() {
		FixMap m = parser.parse("1=1234|6=32.4|78=2|79=555|80=1000|79=666|80=2000|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(2, m.getGroups(78).size());
		assertEquals("555", m.getGroups(78).get(0).get(79));
		assertEquals("1000", m.getGroups(78).get(0).get(80));
		assertEquals("666", m.getGroups(78).get(1).get(79));
		assertEquals("2000", m.getGroups(78).get(1).get(80));
	}

	@Test
	public void testGroup3() {
		FixMap m = parser.parse("1=1234|6=32.4|78=3|79=555|80=1000|79=666|80=2000|79=1|80=2|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(3, m.getGroups(78).size());
		assertEquals("555", m.getGroups(78).get(0).get(79));
		assertEquals("1000", m.getGroups(78).get(0).get(80));
		assertEquals("666", m.getGroups(78).get(1).get(79));
		assertEquals("2000", m.getGroups(78).get(1).get(80));
		assertEquals("1", m.getGroups(78).get(2).get(79));
		assertEquals("2", m.getGroups(78).get(2).get(80));
	}

	@Test(expected = FixParseException.class)
	public void testGroupCountTooHigh() {
		try {
			FixMap m = parser.parse("1=1234|6=32.4|78=1|79=555|80=1000|79=666|80=2000|10=123|", '|');
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test(expected = FixParseException.class)
	public void testGroupCountTooLow() {
		try {
			FixMap m = parser.parse("1=1234|6=32.4|78=3|79=555|80=1000|79=666|80=2000|10=123|", '|');
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test(expected = FixParseException.class)
	public void testGroupCountNoStart() {
		try {
			FixMap m = parser.parse("1=1234|6=32.4|78=2|80=555|79=1000|79=666|80=2000|10=123|", '|');
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test(expected = FixParseException.class)
	public void testGroupCountNoEndTag() {
		try {
			FixMap m = parser.parse("1=1234|6=32.4|78=3|79=555|81=1000|79=666|80=2000|10=123|", '|');
		} catch (RuntimeException e) {
			e.printStackTrace();
			throw e;
		}
	}

	@Test
	public void testOneItemGroup() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=1|38=1000|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(1, m.getGroups(12406).size());
		assertEquals("1000", m.getGroups(12406).get(0).get(38));
	}

	@Test
	public void testOneItemGroupEnd() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=1|38=1000|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals(1, m.getGroups(12406).size());
		assertEquals("1000", m.getGroups(12406).get(0).get(38));
	}

	@Test
	public void testOneItemGroup2() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=2|38=1000|38=2000|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(2, m.getGroups(12406).size());
		assertEquals("1000", m.getGroups(12406).get(0).get(38));
		assertEquals("2000", m.getGroups(12406).get(1).get(38));
	}

	@Test
	public void testOneItemGroup2End() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=2|38=1000|38=2000|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals(2, m.getGroups(12406).size());
		assertEquals("1000", m.getGroups(12406).get(0).get(38));
		assertEquals("2000", m.getGroups(12406).get(1).get(38));
	}

	@Test
	public void testOneItemGroupEmptyEnd() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=0|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals(0, m.getGroups(12406).size());
	}

	@Test
	public void testOneItemGroupEmpty() {
		FixMap m = parser.parse("1=1234|6=32.4|12406=0|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals(0, m.getGroups(12406).size());
		assertEquals("321", m.get(11));
	}

	@Test
	public void testNestedEmptyGroup() {
		FixMap m = parser.parse("1=1234|6=32.4|12396=1|12397=2|12406=0|12403=P|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(null, m.getGroupsNoThrow(12406));
		assertEquals(1, m.getGroupsCount(12396));
		assertEquals("2", m.getGroupAt(12396, 0).get(12397));
		assertEquals(0, m.getGroupAt(12396, 0).getGroupsCount(12406));
		assertEquals("P", m.getGroupAt(12396, 0).get(12403));
	}

	@Test
	public void testNestedEmptyGroupEnd() {
		FixMap m = parser.parse("1=1234|6=32.4|12396=1|12397=2|12406=0|12403=P|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals(null, m.getGroupsNoThrow(12406));
		assertEquals(1, m.getGroupsCount(12396));
		assertEquals("2", m.getGroupAt(12396, 0).get(12397));
		assertEquals(0, m.getGroupAt(12396, 0).getGroupsCount(12406));
		assertEquals("P", m.getGroupAt(12396, 0).get(12403));
	}

	@Test
	public void testNestedGroup() {
		FixMap m = parser.parse("1=1234|6=32.4|12396=1|12397=2|12406=1|38=1000|12403=P|11=321|10=123|", '|');
		System.out.println(m);
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(null, m.getGroupsNoThrow(12406));
		assertEquals(1, m.getGroupsCount(12396));
		assertEquals("2", m.getGroupAt(12396, 0).get(12397));
		assertEquals(1, m.getGroupAt(12396, 0).getGroupsCount(12406));
		assertEquals("1000", m.getGroupAt(12396, 0).getGroupAt(12406, 0).get(38));
		assertEquals("P", m.getGroupAt(12396, 0).get(12403));
	}

	@Test
	public void testMultiNestedGroup() {

		Duration d = new Duration();
		FixMap m = null;
		for (int j = 0; j < 100; j++) {
			for (int i = 0; i < 1000; i++) {
				m = parser
						.parse("8=FIX.4.2|9=153|35=D|49=BLP|56=SCHB|34=1|50=30737|97=Y|52=20000809-20:20:50|11=90001008|1=10030003|21=2|55=TESTA|54=1|38=4000|40=2|59=0|44=30|47=I|60=20000809-18:20:32|10=061|",
								'|');
			}
			d.stampMicrosStdout(1000);
		}
		System.out.println(m);
		m = parser.parse("1=1234|6=32.4|12396=2|12397=2|12406=2|38=1000|38=2000|12403=P|12397=3|12406=3|38=3000|38=4000|38=5000|12403=Q|11=321|10=123|", '|');
		assertEquals("1234", m.get(1));
		assertEquals("32.4", m.get(6));
		assertEquals("321", m.get(11));
		assertEquals(null, m.getGroupsNoThrow(12406));
		assertEquals(2, m.getGroupsCount(12396));
		assertEquals("2", m.getGroupAt(12396, 0).get(12397));
		assertEquals(2, m.getGroupAt(12396, 0).getGroupsCount(12406));
		assertEquals("1000", m.getGroupAt(12396, 0).getGroupAt(12406, 0).get(38));
		assertEquals("2000", m.getGroupAt(12396, 0).getGroupAt(12406, 1).get(38));
		assertEquals("3000", m.getGroupAt(12396, 1).getGroupAt(12406, 0).get(38));
		assertEquals("4000", m.getGroupAt(12396, 1).getGroupAt(12406, 1).get(38));
		assertEquals("5000", m.getGroupAt(12396, 1).getGroupAt(12406, 2).get(38));
		assertEquals("P", m.getGroupAt(12396, 0).get(12403));
		assertEquals("Q", m.getGroupAt(12396, 1).get(12403));
	}
	@Test
	public void test44Builder() {
		BasicFixMap m = parser
				.parse("8=FIX.4.2|9=153|35=D|49=BLP|56=SCHB|34=1|50=30737|97=Y|52=20000809-20:20:50|11=90001008|1=10030003|21=2|55=TESTA|54=1|38=4000|40=2|59=0|44=30|47=I|60=20000809-18:20:32|10=061|",
						'|');
		System.out.println(new String(builder.buildFix(m)));
	}
}

