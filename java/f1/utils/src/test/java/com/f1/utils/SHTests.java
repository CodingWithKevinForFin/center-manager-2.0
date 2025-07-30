package com.f1.utils;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import org.junit.Ignore;
import org.junit.Test;

public class SHTests {
	@Test
	public void testAfterFirst1() {

		System.out.println(SH.join(",", EH.getJavaClassPathAbsolute()));
		assertEquals("test", SH.afterFirst("test", ""));
	}
	@Test
	public void testAfterFirst2() {
		assertEquals("test", SH.afterFirst("test", "."));
	}
	@Test
	public void testAfterFirst3() {
		assertEquals(null, SH.afterFirst("test", ".", null));
	}
	@Test
	public void testAfterFirst4() {
		assertEquals("this.what", SH.afterFirst("test.this.what", "."));
	}
	@Test
	public void testAfterFirst5() {
		assertEquals("test.this.what", SH.afterFirst(".test.this.what", "."));
	}

	@Test
	public void testBeforeFirst1() {
		assertEquals("", SH.beforeFirst("test", ""));
	}
	@Test
	public void testBeforeFirst2() {
		assertEquals("test", SH.beforeFirst("test", "."));
	}
	@Test
	public void testBeforeFirst3() {
		assertEquals(null, SH.beforeFirst("test", ".", null));
	}
	@Test
	public void testBeforeFirst4() {
		assertEquals("test", SH.beforeFirst("test.this.what", "."));
	}
	@Test
	public void testBeforeFirst5() {
		assertEquals("", SH.beforeFirst(".test.this.what.", "."));
	}

	@Test
	public void testAfterLast1() {
		assertEquals("", SH.afterLast("test", ""));
	}
	@Test
	public void testAfterLast2() {
		assertEquals("test", SH.afterLast("test", "."));
	}
	@Test
	public void testAfterLast3() {
		assertEquals(null, SH.afterLast("test", ".", null));
	}
	@Test
	public void testAfterLast4() {
		assertEquals("what", SH.afterLast("test.this.what", "."));
	}
	@Test
	public void testAfterLast5() {
		assertEquals("", SH.afterLast(".test.this.what.", "."));
	}

	@Test
	public void testBeforeLast1() {
		assertEquals("test", SH.beforeLast("test", ""));
	}
	@Test
	public void testBeforeLast2() {
		assertEquals("test", SH.beforeLast("test", "."));
	}
	@Test
	public void testBeforeLast3() {
		assertEquals(null, SH.beforeLast("test", ".", null));
	}
	@Test
	public void testBeforeLast4() {
		assertEquals("test.this", SH.beforeLast("test.this.what", "."));
	}
	@Test
	public void testBeforeLast5() {
		assertEquals(".test.this.what", SH.beforeLast(".test.this.what.", "."));
	}

	@Test
	public void testDdd1() {
		assertEquals("test this", SH.ddd("test this", 10));
	}
	@Test
	public void testDdd2() {
		assertEquals("test this", SH.ddd("test this", 9));
	}
	@Test
	public void testDdd3() {
		assertEquals("test ...", SH.ddd("test this", 8));
	}
	@Test
	public void testDdd4() {
		assertEquals("test...", SH.ddd("test this", 7));
	}
	@Test
	public void testDdd5() {
		assertEquals("t...", SH.ddd("test this", 4));
	}
	@Test
	public void testDdd6() {
		assertEquals("...", SH.ddd("test this", 3));
	}
	@Test
	public void testDdd7() {
		assertEquals("..", SH.ddd("test this", 2));
	}
	@Test
	public void testDdd8() {
		assertEquals("tt", SH.ddd("tt", 2));
	}
	@Test
	public void testDdd9() {
		assertEquals(".", SH.ddd("test this", 1));
	}

	@Test
	public void testEscape1() {
		assertEquals("", SH.escape("", '.', '/'));
	}
	@Test
	public void testEscape2() {
		assertEquals("/.", SH.escape(".", '.', '/'));
	}
	@Test
	public void testEscape3() {
		assertEquals("//", SH.escape("/", '.', '/'));
	}
	@Test
	public void testEscape4() {
		assertEquals("this/.is/.a/.test", SH.escape("this.is.a.test", '.', '/'));
	}
	@Test
	public void testEscape5() {
		assertEquals("/.this/.is/.a/.test/.", SH.escape(".this.is.a.test.", '.', '/'));
	}
	@Test
	public void testEscape6() {
		assertEquals("/.this///.is/.a/.test/.", SH.escape(".this/.is.a.test.", '.', '/'));
	}
	@Test
	public void testEscape7() {
		assertEquals("/.this/////.is/.a/./.test/.", SH.escape(".this//.is.a..test.", '.', '/'));
	}
	@Test
	public void testEscape8() {
		assertEquals("//this/////.is/.a/./.test/.", SH.escape("/this//.is.a..test.", '.', '/'));
	}
	@Test
	public void testEscape9() {
		assertEquals("asdf//this/////.is/.a/./.test/.", SH.escape("asdf/this//.is.a..test.", '.', '/'));
	}

	@Test
	public void testFormatMemory1() {
		assertEquals("0 B", SH.formatMemory(0L));
	}
	@Test
	public void testFormatMemory2() {
		assertEquals("5 B", SH.formatMemory(5L));
	}
	@Test
	public void testFormatMemory3() {
		assertEquals("100 B", SH.formatMemory(100L));
	}
	@Test
	public void testFormatMemory4() {
		assertEquals("1,000 B", SH.formatMemory(1000L));
	}
	@Test
	public void testFormatMemory5() {
		assertEquals("2,000 B", SH.formatMemory(2000L));
	}
	@Test
	public void testFormatMemory6() {
		assertEquals("2.93 KB", SH.formatMemory(3000L));
	}
	@Test
	public void testFormatMemory7() {
		assertEquals("8.50 KB", SH.formatMemory(8704L));
	}
	@Test
	public void testFormatMemory8() {
		assertEquals("976.56 KB", SH.formatMemory(1000000L));
	}
	@Test
	public void testFormatMemory9() {
		assertEquals("1,953.12 KB", SH.formatMemory(2000000L));
	}
	@Test
	public void testFormatMemory10() {
		assertEquals("2.86 MB", SH.formatMemory(3000000L));
	}
	@Test
	public void testFormatMemory11() {
		assertEquals("953.67 MB", SH.formatMemory(1000000000L));
	}
	@Test
	public void testFormatMemory12() {
		assertEquals("1,907.35 MB", SH.formatMemory(2000000000L));
	}
	@Test
	public void testFormatMemory13() {
		assertEquals("2.79 GB", SH.formatMemory(3000000000L));
	}
	@Test
	public void testFormatMemory14() {
		assertEquals("931.32 GB", SH.formatMemory(1000000000000L));
	}
	@Test
	public void testFormatMemory15() {
		assertEquals("1,862.65 GB", SH.formatMemory(2000000000000L));
	}
	@Test
	public void testFormatMemory16() {
		assertEquals("2.73 TB", SH.formatMemory(3000000000000L));
	}
	@Test
	public void testFormatMemory17() {
		assertEquals("909.49 TB", SH.formatMemory(1000000000000000L));
	}
	@Test
	public void testFormatMemory18() {
		assertEquals("1,818.99 TB", SH.formatMemory(2000000000000000L));
	}
	@Test
	public void testFormatMemory19() {
		assertEquals("2,728.48 TB", SH.formatMemory(3000000000000000L));
	}

	@Test
	public void testIndexOf1() {
		assertEquals(4, SH.indexOf("ABCDEFG", 0, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOf2() {
		assertEquals(4, SH.indexOf("ABCDEFG", 4, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOf3() {
		assertEquals(5, SH.indexOf("ABCDEFG", 5, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOf4() {
		assertEquals(6, SH.indexOf("ABCDEFG", 6, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOf5() {
		assertEquals(-1, SH.indexOf("ABCDEFG", 7, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOf6() {
		assertEquals(-1, SH.indexOf("ABCDEFG", 0, CH.s(AH.box("".toCharArray()))));
	}
	@Test
	public void testIndexOf7() {
		assertEquals(-1, SH.indexOf("ABCDEFG", 0, CH.s(AH.box("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOf8() {
		assertEquals(2, SH.indexOf("ZZZZZZZ", 2, CH.s(AH.box("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOf9() {
		assertEquals(2, SH.indexOf("ABRQAA", 0, CH.s(AH.box("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOf10() {
		assertEquals(-1, SH.indexOf("", 0, CH.s(AH.box("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOf11() {
		assertEquals(-1, SH.indexOf("", 0, CH.s(AH.box("".toCharArray()))));
	}

	@Test
	public void testIndexOfNot1() {
		assertEquals(0, SH.indexOfNot("ABCDEFG", 0, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfNot2() {
		assertEquals(-1, SH.indexOfNot("ABCDEFG", 4, CH.s(AH.box("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfNot3() {
		assertEquals(4, SH.indexOfNot("ABCDEFG", 4, CH.s(AH.box("".toCharArray()))));
	}
	@Test
	public void testIndexOfNot4() {
		assertEquals(-1, SH.indexOfNot("", 0, CH.s(AH.box("".toCharArray()))));
	}

	private static String[] toStrings(char[] charArray_) {
		String[] r = new String[charArray_.length];
		for (int i = 0; i < charArray_.length; i++)
			r[i] = "" + charArray_[i];
		return r;
	}

	@Test
	public void testIndexOfFirst1() {
		assertEquals(4, SH.indexOfFirst("ABCDEFG", 0, (toStrings("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst2() {
		assertEquals(4, SH.indexOfFirst("ABCDEFG", 4, (toStrings("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst3() {
		assertEquals(5, SH.indexOfFirst("ABCDEFG", 5, (toStrings("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst4() {
		assertEquals(6, SH.indexOfFirst("ABCDEFG", 6, (toStrings("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst5() {
		assertEquals(-1, SH.indexOfFirst("ABCDEFG", 7, (toStrings("EFG".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst6() {
		assertEquals(-1, SH.indexOfFirst("ABCDEFG", 0, (toStrings("".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst7() {
		assertEquals(-1, SH.indexOfFirst("ABCDEFG", 0, (toStrings("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst8() {
		assertEquals(2, SH.indexOfFirst("ZZZZZZZ", 2, (toStrings("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst9() {
		assertEquals(2, SH.indexOfFirst("ABRQAA", 0, (toStrings("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst10() {
		assertEquals(-1, SH.indexOfFirst("", 0, (toStrings("ZRQ".toCharArray()))));
	}
	@Test
	public void testIndexOfFirst11() {
		assertEquals(-1, SH.indexOfFirst("", 0, (toStrings("".toCharArray()))));
	}

	@Test
	public void testIs1() {
		assertEquals(false, SH.is(""));
	}
	@Test
	public void testIs2() {
		assertEquals(false, SH.is(" "));
	}
	@Test
	public void testIs3() {
		assertEquals(false, SH.is("  "));
	}
	@Test
	public void testIs4() {
		assertEquals(false, SH.is(null));
	}
	@Test
	public void testIs5() {
		assertEquals(false, SH.is(new StringBuilder()));
	}
	@Test
	public void testIs6() {
		assertEquals(true, SH.is(new StringBuilder(" asdf ")));
	}
	@Test
	public void testIs7() {
		assertEquals(true, SH.is(new StringBuilder(" asdf ")));
	}
	@Test
	public void testIs8() {
		assertEquals(true, SH.is("a"));
	}
	@Test
	public void testIs9() {
		assertEquals(true, SH.is(" a"));
	}

	@Test
	public void testIsnt1() {
		assertEquals(true, SH.isnt(""));
	}
	@Test
	public void testIsnt2() {
		assertEquals(true, SH.isnt(" "));
	}
	@Test
	public void testIsnt3() {
		assertEquals(true, SH.isnt("  "));
	}
	@Test
	public void testIsnt4() {
		assertEquals(true, SH.isnt(null));
	}
	@Test
	public void testIsnt5() {
		assertEquals(true, SH.isnt(new StringBuilder()));
	}
	@Test
	public void testIsnt6() {
		assertEquals(false, SH.isnt(new StringBuilder(" asdf ")));
	}
	@Test
	public void testIsnt7() {
		assertEquals(false, SH.isnt(new StringBuilder(" asdf ")));
	}
	@Test
	public void testIsnt8() {
		assertEquals(false, SH.isnt("a"));
	}
	@Test
	public void testIsnt9() {
		assertEquals(false, SH.isnt(" a"));
	}

	@Test
	public void testJoin1() {
		assertEquals("", SH.join(',', new String[0]));
	}
	@Test
	public void testJoin2() {
		assertEquals("a", SH.join(',', "a"));
	}
	@Test
	public void testJoin3() {
		assertEquals("a,b", SH.join(',', "a", "b"));
	}
	@Test
	public void testJoin4() {
		assertEquals("a,b,c", SH.join(',', "a", "b", "c"));
	}

	@Test
	public void testJoinList1() {
		assertEquals("", SH.join(',', CH.l()));
	}
	@Test
	public void testJoinList2() {
		assertEquals("1", SH.join(',', CH.l("1")));
	}
	@Test
	public void testJoinList3() {
		assertEquals("1,a", SH.join(',', CH.l("1", "a")));
	}
	@Test
	public void testJoinList4() {
		assertEquals("1,a,2,b", SH.join(',', CH.l("1", "a", "2", "b")));
	}
	@Test
	public void testJoinList5() {
		assertEquals("1,a,2,b,3,c", SH.join(',', CH.l("1", "a", "2", "b", "3", "c")));
	}

	@Test
	public void testJoinMap1() {
		assertEquals("", SH.joinMap(',', '=', CH.m(new TreeMap())));
	}
	@Test
	public void testJoinMap2() {
		assertEquals("1=a", SH.joinMap(',', '=', CH.m(new TreeMap(), "1", "a")));
	}
	@Test
	public void testJoinMap3() {
		assertEquals("1=a,2=b", SH.joinMap(',', '=', CH.m(new TreeMap(), "1", "a", "2", "b")));
	}
	@Test
	public void testJoinMap4() {
		assertEquals("1=a,2=b,3=c", SH.joinMap(',', '=', CH.m(new TreeMap(), "1", "a", "2", "b", "3", "c")));
	}

	@Test
	public void testS1() {
		assertEquals("test", SH.s("test"));
	}
	@Test
	public void testS2() {
		assertEquals("1", SH.s(1));
	}
	@Test
	public void testS3() {
		assertEquals("[1,2]", SH.s(CH.l(1, 2)));
	}

	@Test
	public void testLeftAlign1() {
		assertEquals("abc  ", SH.leftAlign(' ', "abc", 5, false));
	}
	@Test
	public void testLeftAlign2() {
		assertEquals("abc ", SH.leftAlign(' ', "abc", 4, false));
	}
	@Test
	public void testLeftAlign3() {
		assertEquals("abc", SH.leftAlign(' ', "abc", 3, false));
	}
	@Test
	public void testLeftAlign4() {
		assertEquals("abc", SH.leftAlign(' ', "abc", 2, false));
	}
	@Test
	public void testLeftAlign5() {
		assertEquals("abc", SH.leftAlign(' ', "abc", 1, false));
	}
	@Test
	public void testLeftAlign6() {
		assertEquals("abc", SH.leftAlign(' ', "abc", 0, false));
	}
	@Test
	public void testLeftAlign7() {
		assertEquals("abc  ", SH.leftAlign(' ', "abc", 5, true));
	}
	@Test
	public void testLeftAlign8() {
		assertEquals("abc ", SH.leftAlign(' ', "abc", 4, true));
	}
	@Test
	public void testLeftAlign9() {
		assertEquals("abc", SH.leftAlign(' ', "abc", 3, true));
	}
	@Test
	public void testLeftAlign10() {
		assertEquals("ab", SH.leftAlign(' ', "abc", 2, true));
	}
	@Test
	public void testLeftAlign11() {
		assertEquals("a", SH.leftAlign(' ', "abc", 1, true));
	}
	@Test
	public void testLeftAlign12() {
		assertEquals("", SH.leftAlign(' ', "abc", 0, true));
	}
	@Test
	public void testLeftAlign13() {
		assertEquals("", SH.leftAlign(' ', "", 0, true));
	}
	@Test
	public void testLeftAlign14() {
		assertEquals(" ", SH.leftAlign(' ', "", 1, true));
	}

	@Test
	public void testRightAlign1() {
		assertEquals("  abc", SH.rightAlign(' ', "abc", 5, false));
	}
	@Test
	public void testRightAlign2() {
		assertEquals(" abc", SH.rightAlign(' ', "abc", 4, false));
	}
	@Test
	public void testRightAlign3() {
		assertEquals("abc", SH.rightAlign(' ', "abc", 3, false));
	}
	@Test
	public void testRightAlign4() {
		assertEquals("abc", SH.rightAlign(' ', "abc", 2, false));
	}
	@Test
	public void testRightAlign5() {
		assertEquals("abc", SH.rightAlign(' ', "abc", 1, false));
	}
	@Test
	public void testRightAlign6() {
		assertEquals("abc", SH.rightAlign(' ', "abc", 0, false));
	}
	@Test
	public void testRightAlignj() {
		assertEquals("  abc", SH.rightAlign(' ', "abc", 5, true));
	}
	@Test
	public void testRightAlign8() {
		assertEquals(" abc", SH.rightAlign(' ', "abc", 4, true));
	}
	@Test
	public void testRightAlign9() {
		assertEquals("abc", SH.rightAlign(' ', "abc", 3, true));
	}
	@Test
	public void testRightAlign10() {
		assertEquals("bc", SH.rightAlign(' ', "abc", 2, true));
	}
	@Test
	public void testRightAlign11() {
		assertEquals("c", SH.rightAlign(' ', "abc", 1, true));
	}
	@Test
	public void testRightAlign12() {
		assertEquals("", SH.rightAlign(' ', "abc", 0, true));
	}
	@Test
	public void testRightAlign13() {
		assertEquals("", SH.rightAlign(' ', "", 0, true));
	}
	@Test
	public void testRightAlign14() {
		assertEquals(" ", SH.rightAlign(' ', "", 1, true));
	}

	@Test
	public void testCenterAlign1() {
		assertEquals("b", SH.centerAlign(' ', "abc", 1, true));
	}
	@Test
	public void testCenterAlign2() {
		assertEquals("ab", SH.centerAlign(' ', "abc", 2, true));
	}
	@Test
	public void testCenterAlign3() {
		assertEquals("abc", SH.centerAlign(' ', "abc", 3, true));
	}
	@Test
	public void testCenterAlign4() {
		assertEquals("abc ", SH.centerAlign(' ', "abc", 4, true));
	}
	@Test
	public void testCenterAlign5() {
		assertEquals("abc", SH.centerAlign(' ', "abc", 1, false));
	}
	@Test
	public void testCenterAlign6() {
		assertEquals("abc", SH.centerAlign(' ', "abc", 2, false));
	}
	@Test
	public void testCenterAlign7() {
		assertEquals("abc", SH.centerAlign(' ', "abc", 3, false));
	}
	@Test
	public void testCenterAlign8() {
		assertEquals("abc ", SH.centerAlign(' ', "abc", 4, false));
	}
	@Test
	public void testCenterAlign9() {
		assertEquals(" abc ", SH.centerAlign(' ', "abc", 5, false));
	}
	@Test
	public void testCenterAlign10() {
		assertEquals(" abc  ", SH.centerAlign(' ', "abc", 6, false));
	}
	@Test
	public void testCenterAlign11() {
		assertEquals("  abc  ", SH.centerAlign(' ', "abc", 7, false));
	}

	@Test
	public void testLowercaseFirstChar1() {
		assertEquals("test", SH.lowercaseFirstChar("test"));
	}
	@Test
	public void testLowercaseFirstChar2() {
		assertEquals("test", SH.lowercaseFirstChar("Test"));
	}
	@Test
	public void testLowercaseFirstChar3() {
		assertEquals("tEst", SH.lowercaseFirstChar("TEst"));
	}
	@Test
	public void testLowercaseFirstChar4() {
		assertEquals("t", SH.lowercaseFirstChar("T"));
	}
	@Test
	public void testLowercaseFirstChar5() {
		assertEquals("t", SH.lowercaseFirstChar("t"));
	}
	@Test
	public void testLowercaseFirstChar6() {
		assertEquals("", SH.lowercaseFirstChar(""));
	}

	@Test
	public void testUppercaseFirstChar1() {
		assertEquals("Test", SH.uppercaseFirstChar("test"));
	}
	@Test
	public void testUppercaseFirstChar2() {
		assertEquals("Test", SH.uppercaseFirstChar("Test"));
	}
	@Test
	public void testUppercaseFirstChar3() {
		assertEquals("TEst", SH.uppercaseFirstChar("TEst"));
	}
	@Test
	public void testUppercaseFirstChar4() {
		assertEquals("T", SH.uppercaseFirstChar("T"));
	}
	@Test
	public void testUppercaseFirstChar5() {
		assertEquals("T", SH.uppercaseFirstChar("t"));
	}
	@Test
	public void testUppercaseFirstChar6() {
		assertEquals("", SH.uppercaseFirstChar(""));
	}

	@Test
	public void testNoNull1() {
		assertEquals("", SH.noNull(null));
	}
	@Test
	public void testNoNull2() {
		assertEquals("", SH.noNull(""));
	}
	@Test
	public void testNoNull3() {
		assertEquals("a", SH.noNull("a"));
	}

	@Test
	public void testParseChar() {
		assertEquals('a', SH.parseChar("a"));
	}

	@Test(expected = RuntimeException.class)
	public void testParseCharFail1() {
		assertEquals('a', SH.parseChar("ab"));
	}

	@Test(expected = RuntimeException.class)
	public void testParseCharFail2() {
		assertEquals('a', SH.parseChar(""));
	}

	@Test
	public void testPath1() {
		assertEquals("root.file", SH.path('.', "root", "file"));
	}
	@Test
	public void testPath2() {
		assertEquals("file", SH.path('.', null, "file"));
	}
	@Test
	public void testPath3() {
		assertEquals("root", SH.path('.', "root", null));
	}
	@Test
	public void testPath4() {
		assertEquals("file", SH.path('.', "", "file"));
	}
	@Test
	public void testPath5() {
		assertEquals("root", SH.path('.', "root", ""));
	}
	@Test
	public void testPath6() {
		assertEquals("", SH.path('.', ""));
	}
	@Test
	public void testPath7() {
		assertEquals("test", SH.path('.', "test"));
	}
	@Test
	public void testPath8() {
		assertEquals("", SH.path('.'));
	}
	@Test
	public void testPath9() {
		assertEquals("root.asdf.blah", SH.path('.', "root", "", "asdf", "", "", "", "blah"));
	}

	@Test
	public void testQuote1() {
		assertEquals("'test'", SH.quote("test"));
	}
	@Test
	public void testQuote2() {
		assertEquals("''", SH.quote(""));
	}
	@Test
	public void testQuote3() {
		assertEquals("''", SH.quote(null));
	}
	@Test
	public void testQuote4() {
		assertEquals("'\\'test\\''", SH.quote("'test'"));
	}
	@Test
	public void testQuote5() {
		assertEquals("'\\\\\\'test\\\\\\''", SH.quote("\\'test\\'"));
	}

	@Test
	public void testRepeat1() {
		assertEquals("", SH.repeat(' ', 0));
	}
	@Test
	public void testRepeat2() {
		assertEquals(" ", SH.repeat(' ', 1));
	}
	@Test
	public void testRepeat3() {
		assertEquals("  ", SH.repeat(' ', 2));
	}
	@Test
	public void testRepeat4() {
		assertEquals(10000, SH.repeat(' ', 10000).length());
	}
	@Test
	public void testRepeat5() {
		assertEquals("", SH.repeat('_', 0));
	}
	@Test
	public void testRepeat6() {
		assertEquals("_", SH.repeat('_', 1));
	}
	@Test
	public void testRepeat7() {
		assertEquals("__", SH.repeat('_', 2));
	}
	@Test
	public void testRepeat8() {
		assertEquals("___", SH.repeat('_', 3));
	}
	@Test
	public void testRepeat9() {
		assertEquals(10000, SH.repeat('_', 10000).length());
	}

	@Test
	public void testRepeatStrings1() {
		assertEquals("", SH.repeat("ab", 0));
	}
	@Test
	public void testRepeatStrings2() {
		assertEquals("ab", SH.repeat("ab", 1));
	}
	@Test
	public void testRepeatStrings3() {
		assertEquals("abab", SH.repeat("ab", 2));
	}
	@Test
	public void testRepeatStrings4() {
		assertEquals(20000, SH.repeat("ab", 10000).length());
	}

	@Test
	public void testReplaceAll1() {
		assertEquals("", SH.replaceAll("", 'z', "T"));
	}
	@Test
	public void testReplaceAll2() {
		assertEquals("t", SH.replaceAll("t", 'z', "T"));
	}
	@Test
	public void testReplaceAll3() {
		assertEquals("T", SH.replaceAll("t", 't', "T"));
	}
	@Test
	public void testReplaceAll4() {
		assertEquals("this is a test", SH.replaceAll("this is a test", 'z', "T"));
	}
	@Test
	public void testReplaceAll5() {
		assertEquals("This is a TesT", SH.replaceAll("this is a test", 't', "T"));
	}
	@Test
	public void testReplaceAll6() {
		assertEquals("TThis is a TTesTT", SH.replaceAll("this is a test", 't', "TT"));
	}
	@Test
	public void testReplaceAll7() {
		assertEquals("TTTThis is a TTTTesTTTT", SH.replaceAll("tthis is a ttestt", 't', "TT"));
	}
	@Test
	public void testReplaceAll8() {
		assertEquals("thisTTisTTaTTtest", SH.replaceAll("this is a test", ' ', "TT"));
	}
	@Test
	public void testReplaceAll9() {
		assertEquals("thisTTTTisTTTTaTTTTtest", SH.replaceAll("this  is  a  test", ' ', "TT"));
	}
	@Test
	public void testReplaceAll10() {
		assertEquals("thisisatest", SH.replaceAll("  this  is a  test ", ' ', ""));
	}
	@Test
	public void testReplaceAll11() {
		assertEquals("", SH.replaceAll("   ", ' ', ""));
	}
	@Test
	public void testReplaceAll12() {
		assertEquals("!", SH.replaceAll("_!__", '_', ""));
	}
	@Test
	public void testReplaceAll13() {
		assertEquals("!", SH.replaceAll("_!__", "_", ""));
	}
	@Test
	public void testReplaceAll14() {
		assertEquals("_!", SH.replaceAll("_!__", "__", ""));
	}
	@Test
	public void testReplaceAll15() {
		assertEquals("__", SH.replaceAll("_!__", "_!", ""));
	}
	@Test
	public void testReplaceAll16() {
		assertEquals("__", SH.replaceAll("_!___!", "_!", ""));
	}
	@Test
	public void testReplaceAll17() {
		assertEquals("what__what", SH.replaceAll("_!___!", "_!", "what"));
	}
	@Test
	public void testReplaceAll18() {
		assertEquals("_!___!", SH.replaceAll("_!___!", "", "what"));
	}
	@Test
	public void testReplaceAll19() {
		assertEquals("______", SH.replaceAll("_________", "___", "__"));
	}
	@Test
	public void testReplaceAll20() {
		assertEquals("!__!__!__!___", SH.replaceAll("_________", "__", "!__"));
	}
	@Test
	public void testReplaceAll21() {
		assertEquals(" !__!__!__!___ ", SH.replaceAll(" _________ ", "__", "!__"));
	}

	@Test
	public void testSort() {
		assertArrayEquals(new Object[] { 1, 10, 15, 20, 5 }, SH.sort(new Object[] { 1, 5, 10, 15, 20 }));
	}

	@Test
	public void testSplitString1() {
		assertArrayEquals(new String[] { "test" }, SH.split(",", "test"));
	}
	@Test
	public void testSplitString2() {
		assertArrayEquals(new String[] { "test", "this" }, SH.split(",", "test,this"));
	}
	@Test
	public void testSplitString3() {
		assertArrayEquals(new String[] { "test", "this", "that" }, SH.split(",", "test,this,that"));
	}
	@Test
	public void testSplitString4() {
		assertArrayEquals(new String[] { "test", "this", "that", "" }, SH.split(",", "test,this,that,"));
	}
	@Test
	public void testSplitString5() {
		assertArrayEquals(new String[] { "", "test", "", "this", "that", "" }, SH.split(",", ",test,,this,that,"));
	}
	@Test
	public void testSplitString6() {
		assertArrayEquals(new String[] { "", "test", "", "this", "that", "" }, SH.split(",", ",test,,this,that,"));
	}
	@Test
	public void testSplitString7() {
		assertArrayEquals(new String[] { "", "" }, SH.split(",", ","));
	}
	@Test
	public void testSplitString8() {
		assertArrayEquals(new String[] { "test" }, SH.split(",,", "test"));
	}
	@Test
	public void testSplitString9() {
		assertArrayEquals(new String[] { "test,this" }, SH.split(",,", "test,this"));
	}
	@Test
	public void testSplitString10() {
		assertArrayEquals(new String[] { "test", "this" }, SH.split(",,", "test,,this"));
	}
	@Test
	public void testSplitString11() {
		assertArrayEquals(new String[] { "test", "this", "that" }, SH.split(",,", "test,,this,,that"));
	}
	@Test
	public void testSplitString12() {
		assertArrayEquals(new String[] { "test,this,that," }, SH.split(",,", "test,this,that,"));
	}
	@Test
	public void testSplitString13() {
		assertArrayEquals(new String[] { ",test,this,that," }, SH.split(",,", ",test,this,that,"));
	}
	@Test
	public void testSplitString14() {
		assertArrayEquals(new String[] { "", "" }, SH.split(",,", ",,"));
	}
	@Test
	public void testSplitString15() {
		assertArrayEquals(new String[] { "", "", "" }, SH.split(",,", ",,,,"));
	}

	@Test
	public void testSplitContinousString1() {
		assertArrayEquals(new String[] { "test" }, SH.splitContinous(',', "test"));
	}
	@Test
	public void testSplitContinousString2() {
		assertArrayEquals(new String[] { "test", "this" }, SH.splitContinous(',', "test,this"));
	}
	@Test
	public void testSplitContinousString3() {
		assertArrayEquals(new String[] { "test", "this", "that" }, SH.splitContinous(',', "test,this,,that"));
	}
	@Test
	public void testSplitContinousString4() {
		assertArrayEquals(new String[] { "test", "this", "that", "" }, SH.splitContinous(',', "test,this,,that,,,"));
	}
	@Test
	public void testSplitContinousString5() {
		assertArrayEquals(new String[] { "", "test", "this", "that", "" }, SH.splitContinous(',', ",,,test,this,,that,,,"));
	}
	@Test
	public void testSplitContinousString6() {
		assertArrayEquals(new String[] { "", "" }, SH.splitContinous(',', ",,,"));
	}
	@Test
	public void testSplitContinousString7() {
		assertArrayEquals(new String[] { "what" }, SH.splitContinous(',', "what"));
	}
	@Test
	public void testSplitContinousString8() {
		assertArrayEquals(new String[] {}, SH.splitContinous(',', ""));
	}
	@Test
	public void testSplitContinousString9() {
		assertArrayEquals(new String[] { "", "" }, SH.splitContinous(',', ","));
	}
	@Test
	public void testSplitContinousString10() {
		assertArrayEquals(new String[] { "", "a", "b", "c", "" }, SH.splitContinous(',', ",,,a,b,,c,,,"));
	}

	@Test
	public void testSplitChar1() {
		assertArrayEquals(new String[] { "test" }, SH.split(',', "test"));
	}
	@Test
	public void testSplitChar2() {
		assertArrayEquals(new String[] { "test", "this" }, SH.split(',', "test,this"));
	}
	@Test
	public void testSplitChar3() {
		assertArrayEquals(new String[] { "test", "this", "that" }, SH.split(',', "test,this,that"));
	}
	@Test
	public void testSplitChar4() {
		assertArrayEquals(new String[] { "test", "", "this", "that" }, SH.split(',', "test,,this,that"));
	}
	@Test
	public void testSplitChar5() {
		assertArrayEquals(new String[] { "test", "this", "that", "" }, SH.split(',', "test,this,that,"));
	}
	@Test
	public void testSplitChar6() {
		assertArrayEquals(new String[] { "", "test", "this", "that", "" }, SH.split(',', ",test,this,that,"));
	}
	@Test
	public void testSplitChar7() {
		assertArrayEquals(new String[] { "", "" }, SH.split(',', ","));
	}

	@Test
	public void testSplitToListString_1() {
		assertEquals(CH.l(new String[] { "test" }), SH.splitToList(",", "test"));
	}
	@Test
	public void testSplitToListString_2() {
		assertEquals(CH.l(new String[] { "test", "this" }), SH.splitToList(",", "test,this"));
	}
	@Test
	public void testSplitToListString_3() {
		assertEquals(CH.l(new String[] { "test", "this", "that" }), SH.splitToList(",", "test,this,that"));
	}
	@Test
	public void testSplitToListString_4() {
		assertEquals(CH.l(new String[] { "test", "this", "that", "" }), SH.splitToList(",", "test,this,that,"));
	}
	@Test
	public void testSplitToListString_5() {
		assertEquals(CH.l(new String[] { "", "test", "", "this", "that", "" }), SH.splitToList(",", ",test,,this,that,"));
	}
	@Test
	public void testSplitToListString_7() {
		assertEquals(CH.l(new String[] { "", "test", "", "this", "that", "" }), SH.splitToList(",", ",test,,this,that,"));
	}
	@Test
	public void testSplitToListString_8() {
		assertEquals(CH.l(new String[] { "", "" }), SH.splitToList(",", ","));
	}
	@Test
	public void testSplitToListString_9() {
		assertEquals(CH.l(new String[] { "test" }), SH.splitToList(",,", "test"));
	}
	@Test
	public void testSplitToListString_10() {
		assertEquals(CH.l(new String[] { "test,this" }), SH.splitToList(",,", "test,this"));
	}
	@Test
	public void testSplitToListString_11() {
		assertEquals(CH.l(new String[] { "test", "this" }), SH.splitToList(",,", "test,,this"));
	}
	@Test
	public void testSplitToListString_12() {
		assertEquals(CH.l(new String[] { "test", "this", "that" }), SH.splitToList(",,", "test,,this,,that"));
	}
	@Test
	public void testSplitToListString_13() {
		assertEquals(CH.l(new String[] { "test,this,that," }), SH.splitToList(",,", "test,this,that,"));
	}
	@Test
	public void testSplitToListString_14() {
		assertEquals(CH.l(new String[] { ",test,this,that," }), SH.splitToList(",,", ",test,this,that,"));
	}
	@Test
	public void testSplitToListString_15() {
		assertEquals(CH.l(new String[] { "", "" }), SH.splitToList(",,", ",,"));
	}
	@Test
	public void testSplitToListString_16() {
		assertEquals(CH.l(new String[] { "", "", "" }), SH.splitToList(",,", ",,,,"));
	}

	@Test
	public void testSplitToListString2_1() {
		assertEquals(CH.l(new String[] { "test" }), SH.splitToList(new ArrayList<String>(), ",", "test"));
	}
	@Test
	public void testSplitToListString2_2() {
		assertEquals(CH.l(new String[] { "test", "this" }), SH.splitToList(new ArrayList<String>(), ",", "test,this"));
	}
	@Test
	public void testSplitToListString2_3() {
		assertEquals(CH.l(new String[] { "test", "this", "that" }), SH.splitToList(new ArrayList<String>(), ",", "test,this,that"));
	}
	@Test
	public void testSplitToListString2_4() {
		assertEquals(CH.l(new String[] { "test", "this", "that", "" }), SH.splitToList(new ArrayList<String>(), ",", "test,this,that,"));
	}
	@Test
	public void testSplitToListString2_5() {
		assertEquals(CH.l(new String[] { "", "test", "", "this", "that", "" }), SH.splitToList(new ArrayList<String>(), ",", ",test,,this,that,"));
	}
	@Test
	public void testSplitToListString2_6() {
		assertEquals(CH.l(new String[] { "", "test", "", "this", "that", "" }), SH.splitToList(new ArrayList<String>(), ",", ",test,,this,that,"));
	}
	@Test
	public void testSplitToListString2_7() {
		assertEquals(CH.l(new String[] { "", "" }), SH.splitToList(new ArrayList<String>(), ",", ","));
	}
	@Test
	public void testSplitToListString2_8() {
		assertEquals(CH.l(new String[] { "test" }), SH.splitToList(new ArrayList<String>(), ",,", "test"));
	}
	@Test
	public void testSplitToListString2_9() {
		assertEquals(CH.l(new String[] { "test,this" }), SH.splitToList(new ArrayList<String>(), ",,", "test,this"));
	}
	@Test
	public void testSplitToListString2_10() {
		assertEquals(CH.l(new String[] { "test", "this" }), SH.splitToList(new ArrayList<String>(), ",,", "test,,this"));
	}
	@Test
	public void testSplitToListString2_11() {
		assertEquals(CH.l(new String[] { "test", "this", "that" }), SH.splitToList(new ArrayList<String>(), ",,", "test,,this,,that"));
	}
	@Test
	public void testSplitToListString2_12() {
		assertEquals(CH.l(new String[] { "test,this,that," }), SH.splitToList(new ArrayList<String>(), ",,", "test,this,that,"));
	}
	@Test
	public void testSplitToListString2_13() {
		assertEquals(CH.l(new String[] { ",test,this,that," }), SH.splitToList(new ArrayList<String>(), ",,", ",test,this,that,"));
	}
	@Test
	public void testSplitToListString2_14() {
		assertEquals(CH.l(new String[] { "", "" }), SH.splitToList(new ArrayList<String>(), ",,", ",,"));
	}
	@Test
	public void testSplitToListString2_15() {
		assertEquals(CH.l(new String[] { "", "", "" }), SH.splitToList(new ArrayList<String>(), ",,", ",,,,"));
	}

	@Test
	public void testStripPrefix1() {
		assertEquals("cooke", SH.stripPrefix("rob cooke", "rob ", false));
	}
	@Test
	public void testStripPrefix2() {
		assertEquals("rob cooke", SH.stripPrefix("rob cooke", "", false));
	}
	@Test
	public void testStripPrefix3() {
		assertEquals("rob cooke", SH.stripPrefix("rob cooke", "dave", false));
	}

	@Test
	public void testStripSuffix1() {
		assertEquals("rob", SH.stripSuffix("rob cooke", " cooke", false));
	}
	@Test
	public void testStripSuffix2() {
		assertEquals("rob cooke", SH.stripSuffix("rob cooke", "", false));
	}
	@Test
	public void testStripSuffix3() {
		assertEquals("rob cooke", SH.stripSuffix("rob cooke", "dave", false));
	}

	@Test
	public void tesTrim1() {
		assertEquals("h", SH.trim(' ', "h"));
	}
	@Test
	public void tesTrim2() {
		assertEquals("h", SH.trim(' ', " h"));
	}
	@Test
	public void tesTrim3() {
		assertEquals("h", SH.trim(' ', "h "));
	}
	@Test
	public void tesTrim4() {
		assertEquals("hello", SH.trim(' ', "hello"));
	}
	@Test
	public void tesTrim5() {
		assertEquals("hello", SH.trim(' ', " hello"));
	}
	@Test
	public void tesTrim6() {
		assertEquals("hello", SH.trim(' ', " hello "));
	}
	@Test
	public void tesTrim7() {
		assertEquals("hello", SH.trim(' ', "  hello "));
	}
	@Test
	public void tesTrim8() {
		assertEquals("hello", SH.trim(' ', "  hello  "));
	}
	@Test
	public void tesTrim9() {
		assertEquals("", SH.trim(' ', "  "));
	}

	@Test
	public void testTrim_1() {
		assertEquals(0, SH.trimArray().length);
	}
	@Test
	public void testTrim_2() {
		assertEquals(0, SH.trimArray(new String[] { null }).length);
	}
	@Test
	public void testTrim_3() {
		assertEquals(0, SH.trimArray(null, null).length);
	}
	@Test
	public void testTrim_4() {
		assertEquals(0, SH.trimArray(null, null, null).length);
	}
	@Test
	public void testTrim_5() {
		assertEquals(0, SH.trimArray(null, null, null, null).length);
	}
	@Test
	public void testTrim_6() {
		assertEquals(5, SH.trimArray("a", "a", "a", "a", "a").length);
	}
	@Test
	public void testTrim_7() {
		assertEquals(4, SH.trimArray(null, "a", "a", "a", "a").length);
	}
	@Test
	public void testTrim_8() {
		assertEquals(4, SH.trimArray("a", null, "a", "a", "a").length);
	}
	@Test
	public void testTrim_9() {
		assertEquals(3, SH.trimArray(null, null, "a", "a", "a").length);
	}
	@Test
	public void testTrim_10() {
		assertEquals(4, SH.trimArray("a", "a", null, "a", "a").length);
	}
	@Test
	public void testTrim_11() {
		assertEquals(3, SH.trimArray(null, "a", null, "a", "a").length);
	}
	@Test
	public void testTrim_12() {
		assertEquals(3, SH.trimArray("a", null, null, "a", "a").length);
	}
	@Test
	public void testTrim_13() {
		assertEquals(2, SH.trimArray(null, null, null, "a", "a").length);
	}
	@Test
	public void testTrim_14() {
		assertEquals(4, SH.trimArray("a", "a", null, "a", "a").length);
	}
	@Test
	public void testTrim_15() {
		assertEquals(3, SH.trimArray(null, "a", null, "a", "a").length);
	}
	@Test
	public void testTrim_16() {
		assertEquals(3, SH.trimArray("a", null, null, "a", "a").length);
	}
	@Test
	public void testTrim_17() {
		assertEquals(2, SH.trimArray(null, null, null, "a", "a").length);
	}
	@Test
	public void testTrim_18() {
		assertEquals(3, SH.trimArray("a", "a", null, null, "a").length);
	}
	@Test
	public void testTrim_19() {
		assertEquals(2, SH.trimArray(null, "a", null, null, "a").length);
	}
	@Test
	public void testTrim_20() {
		assertEquals(2, SH.trimArray("a", null, null, null, "a").length);
	}
	@Test
	public void testTrim_21() {
		assertEquals(1, SH.trimArray(null, null, null, null, "a").length);
	}
	@Test
	public void testTrim_22() {
		assertEquals(4, SH.trimArray("a", "a", "a", "a", null).length);
	}
	@Test
	public void testTrim_23() {
		assertEquals(3, SH.trimArray(null, "a", "a", "a", null).length);
	}
	@Test
	public void testTrim_24() {
		assertEquals(3, SH.trimArray("a", null, "a", "a", null).length);
	}
	@Test
	public void testTrim_25() {
		assertEquals(2, SH.trimArray(null, null, "a", "a", null).length);
	}
	@Test
	public void testTrim_26() {
		assertEquals(3, SH.trimArray("a", "a", null, "a", null).length);
	}
	@Test
	public void testTrim_27() {
		assertEquals(2, SH.trimArray(null, "a", null, "a", null).length);
	}
	@Test
	public void testTrim_28() {
		assertEquals(2, SH.trimArray("a", null, null, "a", null).length);
	}
	@Test
	public void testTrim_29() {
		assertEquals(1, SH.trimArray(null, null, null, "a", null).length);
	}
	@Test
	public void testTrim_30() {
		assertEquals(3, SH.trimArray("a", "a", null, "a", null).length);
	}
	@Test
	public void testTrim_31() {
		assertEquals(2, SH.trimArray(null, "a", null, "a", null).length);
	}
	@Test
	public void testTrim_32() {
		assertEquals(2, SH.trimArray("a", null, null, "a", null).length);
	}
	@Test
	public void testTrim_33() {
		assertEquals(1, SH.trimArray(null, null, null, "a", null).length);
	}
	@Test
	public void testTrim_34() {
		assertEquals(2, SH.trimArray("a", "a", null, null, null).length);
	}
	@Test
	public void testTrim_35() {
		assertEquals(1, SH.trimArray(null, "a", null, null, null).length);
	}
	@Test
	public void testTrim_36() {
		assertEquals(1, SH.trimArray("a", null, null, null, null).length);
	}
	@Test
	public void testTrim_37() {
		assertEquals(0, SH.trimArray(null, null, null, null, null).length);
	}
	@Test
	public void testTrim_38() {
		assertArrayEquals(new String[] { "abc", "ab" }, SH.trimArray(null, "abc", null, " ab ", null));
	}

	@Test
	public void testIndexOfNotEscaped_1() {
		assertEquals(-1, SH.indexOfNotEscaped("", '.', 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_2() {
		assertEquals(-1, SH.indexOfNotEscaped("test", '.', 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_3() {
		assertEquals(4, SH.indexOfNotEscaped("test.", '.', 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_4() {
		assertEquals(4, SH.indexOfNotEscaped("test.", '.', 4, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_5() {
		assertEquals(-1, SH.indexOfNotEscaped("test.", '.', 5, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_6() {
		assertEquals(-1, SH.indexOfNotEscaped("test/.", '.', 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_7() {
		assertEquals(-1, SH.indexOfNotEscaped("test/.", '.', 4, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_8() {
		assertEquals(5, SH.indexOfNotEscaped("test/.", '.', 5, '/'));
	}
	@Test
	public void testIndexOfNotEscaped_9() {
		assertEquals(10, SH.indexOfNotEscaped("///.test/..", '.', 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_1() {
		assertEquals(-1, SH.indexOfNotEscaped("", ".", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_2() {
		assertEquals(-1, SH.indexOfNotEscaped("test", ".", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_3() {
		assertEquals(4, SH.indexOfNotEscaped("test.", ".", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_4() {
		assertEquals(4, SH.indexOfNotEscaped("test.", ".", 4, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_5() {
		assertEquals(-1, SH.indexOfNotEscaped("test.", ".", 5, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_6() {
		assertEquals(-1, SH.indexOfNotEscaped("test/.", ".", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_7() {
		assertEquals(-1, SH.indexOfNotEscaped("test/.", ".", 4, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_8() {
		assertEquals(5, SH.indexOfNotEscaped("test/.", ".", 5, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_9() {
		assertEquals(10, SH.indexOfNotEscaped("///.test/..", ".", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_10() {
		assertEquals(5, SH.indexOfNotEscaped("test this out", "this", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_11() {
		assertEquals(-1, SH.indexOfNotEscaped("test /this out", "this", 0, '/'));
	}
	@Test
	public void testIndexOfNotEscaped2_12() {
		assertEquals(-1, SH.indexOfNotEscaped("test /this out", "this", 0, '/'));
	}

	@Test
	public void testEndsWith1() {
		assertTrue(SH.endsWith("what", 't'));
	}
	@Test
	public void testEndsWith2() {
		assertTrue(SH.endsWith("t", 't'));
	}
	@Test
	public void testEndsWith3() {
		assertFalse(SH.endsWith(null, 't'));
	}
	@Test
	public void testEndsWith4() {
		assertFalse(SH.endsWith("", 't'));
	}
	@Test
	public void testEndsWith5() {
		assertFalse(SH.endsWith("where", 't'));
	}

	@Test
	public void testStartsWith1() {
		assertTrue(SH.endsWith("that", 't'));
	}
	@Test
	public void testStartsWith2() {
		assertTrue(SH.endsWith("t", 't'));
	}
	@Test
	public void testStartsWith3() {
		assertFalse(SH.endsWith(null, 't'));
	}
	@Test
	public void testStartsWith4() {
		assertFalse(SH.endsWith("", 't'));
	}
	@Test
	public void testStartsWith5() {
		assertFalse(SH.endsWith("where", 't'));
	}

	@Test
	public void testParseCharHandleSpecial1() {
		assertEquals('a', SH.parseCharHandleSpecial("a"));
	}
	@Test
	public void testParseCharHandleSpecial2() {
		assertEquals('e', SH.parseCharHandleSpecial("e"));
	}
	@Test
	public void testParseCharHandleSpecial3() {
		assertEquals('\t', SH.parseCharHandleSpecial("\t"));
	}
	@Test
	public void testParseCharHandleSpecial4() {
		assertEquals('\f', SH.parseCharHandleSpecial("\f"));
	}
	@Test
	public void testParseCharHandleSpecial5() {
		assertEquals('\\', SH.parseCharHandleSpecial("\\\\"));
	}
	@Test
	public void testParseCharHandleSpecial6() {
		assertEquals('\u1234', SH.parseCharHandleSpecial("\u1234"));
	}
	@Test
	public void testParseCharHandleSpecial7() {
		assertEquals('\n', SH.parseCharHandleSpecial("\n"));
	}

	@Test(expected = Exception.class)
	public void testParseCharHandleSpecialF1() {
		SH.parseCharHandleSpecial("\\");
	}

	@Test(expected = Exception.class)
	public void testParseCharHandleSpecialF2() {
		SH.parseCharHandleSpecial("aa");
	}

	@Test(expected = Exception.class)
	public void testParseCharHandleSpecialF3() {
		SH.parseCharHandleSpecial("\\u");
	}

	@Test(expected = Exception.class)
	public void testParseCharHandleSpecialF4() {
		SH.parseCharHandleSpecial("\\u11111");
	}

	@Test(expected = Exception.class)
	public void testParseCharHandleSpecialF5() {
		SH.parseCharHandleSpecial("\\u111");
	}

	@Test
	public void testParseInt1() {
		assertEquals(0, SH.parseInt("0"));
	}
	@Test
	public void testParseInt2() {
		assertEquals(-0, SH.parseInt("-0"));
	}
	@Test
	public void testParseInt3() {
		assertEquals(+0, SH.parseInt("+0"));
	}
	@Test
	public void testParseInt4() {
		assertEquals(1, SH.parseInt("1"));
	}
	@Test
	public void testParseInt5() {
		assertEquals(-1, SH.parseInt("-1"));
	}
	@Test
	public void testParseInt6() {
		assertEquals(1, SH.parseInt("+1"));
	}
	@Test
	public void testParseInt7() {
		assertEquals(01, SH.parseInt("01"));
	}
	@Test
	public void testParseInt8() {
		assertEquals(-01, SH.parseInt("-01"));
	}
	@Test
	public void testParseInt9() {
		assertEquals(+01, SH.parseInt("+01"));
	}
	@Test
	public void testParseInt10() {
		assertEquals(0x134, SH.parseInt("0x134"));
	}
	@Test
	public void testParseInt11() {
		assertEquals(-0x134, SH.parseInt("-0x134"));
	}
	@Test
	public void testParseInt12() {
		assertEquals(+0x134, SH.parseInt("+0x134"));
	}
	@Test
	public void testParseInt13() {
		assertEquals(Integer.MAX_VALUE, SH.parseInt("" + Integer.MAX_VALUE));
	}
	@Test
	public void testParseInt14() {
		assertEquals(Integer.MIN_VALUE, SH.parseInt("" + Integer.MIN_VALUE));
	}
	@Test
	public void testParseInt15() {
		Random r = new Random(12);
		for (long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i += r.nextInt(10000)) {
			testParseInt((int) i);
		}
	}
	@Test
	public void testParseInt16() {
		Random r = new Random(12);
		for (long i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i += r.nextInt(10000)) {
			testParseInt((int) i);
		}

	}
	@Test
	public void testParseInt17() {
		Random r = new Random(12);
		for (int i = 0; i < 1000; i++) {
			testParseInt(Integer.MAX_VALUE - i);
		}
	}
	@Test
	public void testParseInt18() {
		Random r = new Random(12);
		for (int i = 0; i < 1000; i++) {
			testParseInt(Integer.MIN_VALUE + i);
		}
	}
	@Test
	public void testParseIntOutside1() throws Throwable {
		for (int i = 1; i < 1000; i++) {
			String s = Long.toString(Integer.MAX_VALUE + 0L + i);
			try {
				SH.parseInt((CharSequence) s);
				throw new Throwable(s);
			} catch (Exception e) {
			}
		}
	}
	@Test
	public void testParseIntOutside2() throws Throwable {
		for (int i = 1; i < 1000; i++) {
			String s = Long.toString(Integer.MIN_VALUE + 0L - i);
			try {
				SH.parseInt((CharSequence) s);
				throw new Throwable(s);
			} catch (Exception e) {
			}
		}
	}
	@Test
	public void testParseLong1() {
		assertEquals(0L, SH.parseLong("0L"));
	}
	@Test
	public void testParseLong2() {
		assertEquals(-0L, SH.parseLong("-0L"));
	}
	@Test
	public void testParseLong3() {
		assertEquals(+0L, SH.parseLong("+0L"));
	}
	@Test
	public void testParseLong4() {
		assertEquals(1L, SH.parseLong("1L"));
	}
	@Test
	public void testParseLong5() {
		assertEquals(-1L, SH.parseLong("-1L"));
	}
	@Test
	public void testParseLong6() {
		assertEquals(1L, SH.parseLong("+1L"));
	}
	@Test
	public void testParseLong7() {
		assertEquals(0x134L, SH.parseLong("0x134"));
	}
	@Test
	public void testParseLong8() {
		assertEquals(-0x134L, SH.parseLong("-0x134"));
	}
	@Test
	public void testParseLong9() {
		assertEquals(1L, SH.parseLong("1L"));
	}
	@Test
	public void testParseLong10() {
		assertEquals(-1L, SH.parseLong("-1L"));
	}
	@Test
	public void testParseLong11() {
		assertEquals(0x134L, SH.parseLong("0x134L"));
	}
	@Test
	public void testParseLong12() {
		assertEquals(-0x134L, SH.parseLong("-0x134L"));
	}
	@Test
	public void testParseLong13() {
		assertEquals(1L, SH.parseLong("1L"));
	}
	@Test
	public void testParseLong14() {
		assertEquals(-1L, SH.parseLong("-1L"));
	}
	@Test
	public void testParseLong15() {
		assertEquals(0x134L, SH.parseLong("0x134l"));
	}
	@Test
	public void testParseLong16() {
		assertEquals(-0x134L, SH.parseLong("-0x134l"));
	}
	@Test
	public void testParseLong17() {
		assertEquals(+0x134L, SH.parseLong("+0x134l"));
	}
	@Test
	public void testParseLong18() {
		assertEquals(1L, SH.parseLong("1"));
	}
	@Test
	public void testParseLong19() {
		assertEquals(-1L, SH.parseLong("-1"));
	}
	@Test
	public void testParseLong20() {
		assertEquals(0x134L, SH.parseLong("0X134"));
	}
	@Test
	public void testParseLong21() {
		assertEquals(-0x134L, SH.parseLong("-0X134"));
	}
	@Test
	public void testParseLong22() {
		assertEquals(1L, SH.parseLong("1L"));
	}
	@Test
	public void testParseLong23() {
		assertEquals(-1L, SH.parseLong("-1L"));
	}
	@Test
	public void testParseLong24() {
		assertEquals(0x134L, SH.parseLong("0X134L"));
	}
	@Test
	public void testParseLong25() {
		assertEquals(-0x134L, SH.parseLong("-0X134L"));
	}
	@Test
	public void testParseLong26() {
		assertEquals(1L, SH.parseLong("1l"));
	}
	@Test
	public void testParseLong27() {
		assertEquals(-1L, SH.parseLong("-1l"));
	}
	@Test
	public void testParseLong28() {
		assertEquals(+1L, SH.parseLong("+1l"));
	}
	@Test
	public void testParseLong29() {
		assertEquals(0x134L, SH.parseLong("0X134l"));
	}
	@Test
	public void testParseLong30() {
		assertEquals(-0x134L, SH.parseLong("-0X134l"));
	}
	@Test
	public void testParseLong31() {
		for (int i = 2; i < 36; i++) {
			assertEquals(Long.MAX_VALUE, SH.parseLong(Long.toString(Long.MAX_VALUE, i), i));
		}
	}
	@Test
	public void testParseLong32() {
		for (int i = 2; i < 36; i++) {
			assertEquals(Long.MIN_VALUE, SH.parseLong(Long.toString(Long.MIN_VALUE, i), i));
		}
	}
	@Test
	public void testParseLong33() {
		for (int i = 2; i < 36; i++) {
			assertEquals((long) Integer.MAX_VALUE, SH.parseLong(Long.toString(Integer.MAX_VALUE, i), i));
		}
	}
	@Test
	public void testParseLong34() {
		for (int i = 2; i < 36; i++) {
			assertEquals((long) Integer.MIN_VALUE, SH.parseLong(Long.toString(Integer.MIN_VALUE, i), i));
		}
	}
	@Test
	public void testParseLong35() {
		for (int i = 2; i < 36; i++) {
			assertEquals((int) Integer.MAX_VALUE, SH.parseInt(Integer.toString(Integer.MAX_VALUE, i), i));
		}
	}
	@Test
	public void testParseLong36() {
		for (int i = 2; i < 36; i++) {
			assertEquals((int) Integer.MIN_VALUE, SH.parseInt(Integer.toString(Integer.MIN_VALUE, i), i));
		}
	}

	@Test
	public void testParseFloat1() {
		assertEquals(Float.MIN_VALUE, SH.parseFloat(Float.toString(Float.MIN_VALUE)), .0001);
	}
	@Test
	public void testParseFloat2() {
		assertEquals(Float.MAX_VALUE, SH.parseFloat(Float.toString(Float.MAX_VALUE)), .0001);
	}
	@Test
	public void testParseFloat3() {
		assertEquals(1F, SH.parseFloat("1"), .0001);
	}
	@Test
	public void testParseFloat4() {
		assertEquals(-1F, SH.parseFloat("-1"), .0001);
	}
	@Test
	public void testParseFloat5() {
		assertEquals(1F, SH.parseFloat("1F"), .0001);
	}
	@Test
	public void testParseFloat6() {
		assertEquals(-1F, SH.parseFloat("-1F"), .0001);
	}
	@Test
	public void testParseFloat7() {
		assertEquals(1F, SH.parseFloat("1f"), .0001);
	}
	@Test
	public void testParseFloat8() {
		assertEquals(-1F, SH.parseFloat("-1f"), .0001);
	}
	@Test
	public void testParseFloat9() {
		assertEquals(0F, SH.parseFloat("0F"), .0001);
	}
	@Test
	public void testParseFloat10() {
		assertEquals(0F, SH.parseFloat("0f"), .0001);
	}
	@Test
	public void testParseFloat11() {
		assertEquals(1F, SH.parseFloat("+1f"), .0001);
	}
	@Test
	public void testParseFloat12() {
		assertEquals(0.1232F, SH.parseFloat("0.1232F"), .0001);
	}
	@Test
	public void testParseFloat13() {
		assertEquals(-0.343F, SH.parseFloat("-0.343f"), .0001);
	}

	@Test
	public void testParseDouble1() {
		assertEquals(Double.MIN_VALUE, SH.parseDouble(Double.toString(Double.MIN_VALUE)), .0001);
	}
	@Test
	public void testParseDouble2() {
		assertEquals(Double.MAX_VALUE, SH.parseDouble(Double.toString(Double.MAX_VALUE)), .0001);
	}
	@Test
	public void testParseDouble3() {
		assertEquals(Double.MIN_VALUE / 2, SH.parseDouble(Double.toString(Double.MIN_VALUE / 2)), .0001);
	}
	@Test
	public void testParseDouble4() {
		assertEquals(Double.MAX_VALUE / 2, SH.parseDouble(Double.toString(Double.MAX_VALUE / 2)), .0001);
	}
	@Test
	public void testParseDouble5() {
		assertEquals(1D, SH.parseDouble("1"), 0);
	}
	@Test
	public void testParseDouble6() {
		assertEquals(-1D, SH.parseDouble("-1"), 0);
	}
	@Test
	public void testParseDouble7() {
		assertEquals(1D, SH.parseDouble("1D"), 0);
	}
	@Test
	public void testParseDouble8() {
		assertEquals(-1D, SH.parseDouble("-1D"), 0);
	}
	@Test
	public void testParseDouble9() {
		assertEquals(1D, SH.parseDouble("1d"), 0);
	}
	@Test
	public void testParseDouble10() {
		assertEquals(-1D, SH.parseDouble("-1d"), 0);
	}
	@Test
	public void testParseDouble11() {
		assertEquals(0D, SH.parseDouble("0D"), 0);
	}
	@Test
	public void testParseDouble12() {
		assertEquals(0D, SH.parseDouble("0d"), 0);
	}
	@Test
	public void testParseDouble13() {
		assertEquals(1D, SH.parseDouble("+1d"), 0);
	}
	@Test
	public void testParseDouble14() {
		assertEquals(0.1232d, SH.parseDouble("0.1232d"), 0);
	}
	@Test
	public void testParseDouble15() {
		assertEquals(-0.343D, SH.parseDouble("-0.343D"), 0);
	}
	@Test
	public void testParseDouble16() {
		assertEquals(Double.NaN, SH.parseDouble(Double.toString(Double.NaN)), 0);
	}
	@Test
	public void testParseDouble17() {
		assertEquals(Double.NEGATIVE_INFINITY, SH.parseDouble(Double.toString(Double.NEGATIVE_INFINITY)), 0);
	}
	@Test
	public void testParseDouble18() {
		assertEquals(Double.POSITIVE_INFINITY, SH.parseDouble(Double.toString(Double.POSITIVE_INFINITY)), 0);
	}

	@Test
	public void testDoubleToString() {
		Random r = new Random(1442);
		long max = 1000L * 1000 * 1000 * 1000 * 1000;
		int cnt = 10000;
		int n = 0;
		for (long j = 1; j < max; j *= 10) {
			for (int i = 0; i < cnt; i++) {
				double d = r.nextDouble() * (double) j * j;
				testToString(d);
			}
		}
		testToString(0);
		testToString(-1);
		testToString(1);
		testToString(Long.MAX_VALUE);
		testToString(Long.MIN_VALUE);
		testToString(Double.MAX_VALUE);
		testToString(Double.MIN_VALUE);
		testToString(Double.NaN);
		testToString(Double.NEGATIVE_INFINITY);
		testToString(Double.POSITIVE_INFINITY);
	}
	@Test
	public void testDoubleToString2() {
		Random r = new Random(1442);
		int cnt = 100000;
		int n = 0;
		for (int i = 0; i < cnt; i++) {
			double d = r.nextLong();
			testToString(d);
		}
	}
	private void testToString(double d) {
		assertEquals("" + d, Double.toString(d), SH.toString(d));
		assertEquals("" + d, Double.toString(d), FastFloatingDecimal.toJavaFormatString(d, new StringBuilder()).toString());
		float f = (float) d;
		assertEquals("" + f, Float.toString(f), SH.toString(f));
		assertEquals("" + f, Float.toString(f), FastFloatingDecimal.toJavaFormatString(f, new StringBuilder()).toString());
	}
	@Test
	public void testParseDouble19() {
		Random r = new Random(1442);
		long max = 1000L * 1000 * 1000 * 1000 * 1000;
		int cnt = 10000;
		double accuracy = 0;
		int n = 0, miss = 0;
		for (long j = 1; j < max; j *= 10) {
			for (int i = 0; i < cnt; i++) {
				double d = r.nextDouble() * (double) j * j;
				String s = "abc" + Double.toString(d) + "abc";
				assertEquals("" + d, Double.parseDouble(s.substring(3, s.length() - 3)), SH.parseDouble(s, 3, s.length() - 3), accuracy);
			}
			accuracy *= 100;
			n += cnt;
		}
	}
	@Test
	public void testParseDouble20() {
		Random r = new Random(1442);
		long max = 1000L * 1000 * 1000 * 1000 * 1000;
		int cnt = 10000;
		double accuracy = 0;
		int n = 0;
		for (long j = 1; j < max; j *= 10) {
			for (int i = 0; i < cnt; i++) {
				double d = r.nextDouble() * (double) j * j;
				String s = Double.toString(d);
				assertEquals(i + ": " + s, Double.parseDouble(s), SH.parseDouble(s), accuracy);
			}
			accuracy *= 100;
			n += cnt;
		}
	}
	@Test
	public void testParseDouble21() {
		Random r = new Random(1442);
		long max = 1000L * 1000 * 1000 * 1000 * 1000;
		int cnt = 10000;
		double accuracy = 0;
		int n = 0;
		for (long j = 1; j < max; j *= 10) {
			for (int i = 0; i < cnt; i++) {
				double d = r.nextDouble() * 1 / (double) j / j;
				String s = Double.toString(d);
				assertEquals(i + ": " + s, Double.parseDouble(s), SH.parseDouble(s), .000000000001);
			}
			accuracy *= 100;
			n += cnt;
		}
	}
	@Test
	public void testParseDouble22() {
		Random r = new Random(1442);
		long max = 1000L * 1000 * 1000 * 1000 * 1000;
		int cnt = 10000;
		double accuracy = 0;
		int n = 0;
		for (long j = 1; j < max; j *= 10) {
			for (int i = 0; i < cnt; i++) {
				double d = r.nextDouble() * 1 / (double) j / j;
				String s = Double.toString(d);
				assertEquals(i + ": " + s, Double.parseDouble(s), SH.parseDouble(s), .000000000001);
			}
			accuracy *= 100;
			n += cnt;
		}
	}

	@Test
	public void testParseConst1() {
		assertEquals(-5, SH.parseConstant("-5"));
	}
	@Test
	public void testParseConst2() {
		assertEquals(+5, SH.parseConstant("+5"));
	}
	@Test
	public void testParseConst3() {
		assertEquals('\t', SH.parseConstant("'\t'"));
	}
	@Test
	public void testParseConst4() {
		assertEquals("what", SH.parseConstant("\"what\""));
	}
	@Test
	public void testParseConst5() {
		assertEquals("wh\tat", SH.parseConstant("\"wh\\tat\""));
	}
	@Test
	public void testParseConst6() {
		assertEquals("wh\t\u1234at", SH.parseConstant("\"wh\\t\\u1234at\""));
	}
	@Test
	public void testParseConst7() {
		assertEquals("wh\t\u1234at\n", SH.parseConstant("\"wh\\t\\u1234at\n\""));
	}
	@Test
	public void testParseConst8() {
		assertEquals('\u5555', SH.parseConstant("'\\u5555'"));
	}
	@Test
	public void testParseConst9() {
		assertEquals(5555, SH.parseConstant("5555"));
	}
	@Test
	public void testParseConst10() {
		assertEquals(5555f, SH.parseConstant("5555f"));
	}
	@Test
	public void testParseConst11() {
		assertEquals(5555d, SH.parseConstant("5555d"));
	}
	@Test
	public void testParseConst12() {
		assertEquals(-5555l, SH.parseConstant("-5555l"));
	}
	@Test
	public void testParseConst13() {
		assertEquals(-5555f, SH.parseConstant("-5555f"));
	}
	@Test
	public void testParseConst14() {
		assertEquals(-5555d, SH.parseConstant("-5555d"));
	}
	@Test
	public void testParseConst15() {
		assertEquals(-5555l, SH.parseConstant("-5555l"));
	}
	@Test
	public void testParseConst16a() {
		assertEquals(0x5555l, SH.parseConstant("0x5555l"));
	}
	@Test
	public void testParseConst16() {
		assertEquals(-0x5555l, SH.parseConstant("-0x5555l"));
	}
	@Test
	public void testParseConst17() {
		assertEquals(-0x5555f, SH.parseConstant("-0x5555f"));
	}
	@Test
	public void testParseConst18() {
		assertEquals(-0x5555d, SH.parseConstant("-0x5555d"));
	}
	@Test
	public void testParseConst19() {
		assertEquals(-0x5555L, SH.parseConstant("-0x5555L"));
	}
	@Test
	public void testParseConst20() {
		assertEquals(-5555E4, SH.parseConstant("-5555E4"));
	}
	@Test
	public void testParseConst21() {
		assertEquals(5555E4, SH.parseConstant("5555E4"));
	}
	@Test
	public void testParseConst22() {
		assertEquals(5555E4f, SH.parseConstant("5555E4f"));
	}
	@Test
	public void testParseConst23() {
		assertEquals(5555E4d, SH.parseConstant("5555E4d"));
	}
	@Test
	public void testParseConst24() {
		assertEquals(55.55, SH.parseConstant("55.55"));
	}
	@Test
	public void testParseConst25() {
		assertEquals(55.55d, SH.parseConstant("55.55d"));
	}
	@Test
	public void testParseConst26() {
		assertEquals(55.55f, SH.parseConstant("55.55f"));
	}
	@Test
	public void testParseConst27() {
		assertEquals(0x123e, SH.parseConstant("0x123e"));
	}
	@Test
	public void testParseConst28() {
		assertEquals(null, SH.parseConstant("null"));
	}
	@Test
	public void testParseConst29() {
		assertEquals(true, SH.parseConstant("true"));
	}
	@Test
	public void testParseConst30() {
		assertEquals(false, SH.parseConstant("false"));
	}
	// dec
	@Test
	public void testParseConst31() {
		assertEquals(Integer.MAX_VALUE, SH.parseConstant(Long.toString(Integer.MAX_VALUE)));
	}
	@Test
	public void testParseConst32() {
		assertEquals(Integer.MIN_VALUE, SH.parseConstant(Long.toString(Integer.MIN_VALUE)));
	}
	@Test
	public void testParseConst33() {
		long maxv = Integer.MAX_VALUE;
		assertEquals(maxv + 1, SH.parseConstant(Long.toString(maxv + 1L) + "L"));
	}
	@Test
	public void testParseConst34() {
		long minv = Integer.MIN_VALUE;
		assertEquals(minv - 1, SH.parseConstant(Long.toString(minv - 1L)));
	}
	@Test
	public void testParseConst35() {
		assertEquals(Long.MAX_VALUE, SH.parseConstant(Long.toString(Long.MAX_VALUE)));
	}
	@Test
	public void testParseConst36() {
		assertEquals(Long.MIN_VALUE, SH.parseConstant(Long.toString(Long.MIN_VALUE)));
	}
	// hex
	@Test
	public void testParseConst37() {

		long maxv = Integer.MAX_VALUE;
		assertEquals(Integer.MAX_VALUE, SH.parseConstant("0x" + Long.toString(maxv, 16)));
	}
	@Test
	public void testParseConst38() {
		long minv = Integer.MIN_VALUE;
		assertEquals(Integer.MIN_VALUE, SH.parseConstant("-0x" + Long.toString(-minv, 16)));
	}
	@Test
	public void testParseConst39() {
		long maxv = Integer.MAX_VALUE;
		assertEquals(maxv + 1, SH.parseConstant("0x" + Long.toString(maxv + 1L, 16)));
	}
	@Test
	public void testParseConst40() {
		long minv = Integer.MIN_VALUE;
		assertEquals(minv - 1, SH.parseConstant("-0x" + Long.toString(-minv + 1L, 16)));
	}
	@Test
	public void testParseConst41() {
		assertEquals(Long.MAX_VALUE, SH.parseConstant("0x" + Long.toString(Long.MAX_VALUE, 16)));

	}

	@Test
	public void whatInTheWorld1() {
		BigDecimal left = new BigDecimal("123.00000");
		BigDecimal right = new BigDecimal("123.99999");

		// Lets make sure these aren't equal!
		assertFalse(left.equals(right));

		// Why does this pass????
		// assertNotEquals(left, right);

		// Answer is in this unbelievable junit code:
		// private static boolean isEquals(Object expected, Object actual) {
		// if (expected instanceof Number && actual instanceof Number)
		// return ((Number) expected).longValue() == ((Number)
		// actual).longValue();
		// return expected.equals(actual);
		// }
	}
	@Test
	public void whatInTheWorld2() {
		BigDecimal left = new BigDecimal("123.00000");
		BigDecimal right = new BigDecimal("123.99999");

		// Lets make sure these aren't equal!
		assertFalse(right.equals(left));

		// Why does this pass????
		// assertNotEquals(left, right);

		// Answer is in this unbelievable junit code:
		// private static boolean isEquals(Object expected, Object actual) {
		// if (expected instanceof Number && actual instanceof Number)
		// return ((Number) expected).longValue() == ((Number)
		// actual).longValue();
		// return expected.equals(actual);
		// }
	}

	@Test
	public void testOccurrences1() {
		assertEquals(0, SH.getCount("a", ""));
	}
	@Test
	public void testOccurrences2() {
		assertEquals(0, SH.getCount("a", "when"));
	}
	@Test
	public void testOccurrences3() {
		assertEquals(1, SH.getCount("a", "what"));
	}
	@Test
	public void testOccurrences4() {
		assertEquals(2, SH.getCount("a", "whaat"));
	}
	@Test
	public void testOccurrences5() {
		assertEquals(3, SH.getCount("a", "whaata"));
	}
	@Test
	public void testOccurrences6() {
		assertEquals(2, SH.getCount("aa", "whaaaa"));
	}

	@Test
	public void testPrefixLines1() {
		assertEquals("B:what", SH.prefixLines("what", "B:"));
	}
	@Test
	public void testPrefixLines2() {
		assertEquals("B:", SH.prefixLines("", "B:"));
	}
	@Test
	public void testPrefixLines3() {
		assertEquals("B:what\n", SH.prefixLines("what\n", "B:"));
	}
	@Test
	public void testPrefixLines4() {
		assertEquals("B:what\nB:where", SH.prefixLines("what\nwhere", "B:"));
	}
	@Test
	public void testPrefixLines5() {
		assertEquals("B:what\nB:\nB:\nB:where", SH.prefixLines("what\n\n\nwhere", "B:"));
	}
	@Test
	public void testPrefixLines6() {
		assertEquals("B:what\nB:\nB:\n", SH.prefixLines("what\n\n\n", "B:"));
	}
	@Test
	public void testPrefixLines7() {
		assertEquals("B:what\r\nB:\r\nB:\r\n", SH.prefixLines("what\r\n\r\n\r\n", "B:"));
	}
	@Test
	public void testPrefixLines8() {
		assertEquals("B:what\r\nB:\r\nB:\r\n", SH.prefixLines("what\r\n\r\n\r\n", "B:"));
	}
	@Test
	public void testPrefixLines9() {
		assertEquals("B:\nB:\n", SH.prefixLines("\n\n", "B:"));
	}

	@Test
	public void testEncode1() {
		encodeDecode("this\n\ris\u0025\\\"");
	}
	@Test
	public void testEncode2() {
		encodeDecode("this is a test");
	}
	@Test
	public void testEncode3() {
		encodeDecode("this is a \tes\t\n");
	}
	@Test
	public void testEncode4() {
		encodeDecode("this\\ is a \tes\t\n");
	}
	@Test
	public void testEncode5() {
		String base = "😊";
		encodeDecode(base);
	}

	@Test
	public void testLinePosition1() {
		assertEquals("[0,4]", SH.getLinePosition("this test", 4).toString());
	}
	@Test
	public void testLinePosition2() {
		assertEquals("[0,3]", SH.getLinePosition("this\ntest", 3).toString());
	}
	@Test
	public void testLinePosition3() {
		assertEquals("[0,4]", SH.getLinePosition("this\ntest", 4).toString());
	}
	@Test
	public void testLinePosition4() {
		assertEquals("[1,0]", SH.getLinePosition("this\ntest", 5).toString());
	}
	@Test
	public void testLinePosition5() {
		assertEquals("[1,1]", SH.getLinePosition("this\ntest", 6).toString());
	}
	@Test
	public void testLinePosition6() {
		assertEquals("[2,0]", SH.getLinePosition("this\n\n\ntest", 6).toString());
	}
	@Test
	public void testLinePosition7() {
		assertEquals("[3,0]", SH.getLinePosition("this\n\n\ntest", 7).toString());
	}
	@Test
	public void testLinePosition8() {
		assertEquals("[3,1]", SH.getLinePosition("this\n\n\ntest", 8).toString());
	}
	@Test
	public void testLinePosition9() {
		assertEquals("[0,0]", SH.getLinePosition("\nhis test", 0).toString());
	}
	@Test
	public void testLinePosition10() {
		assertEquals("[1,0]", SH.getLinePosition("\nhis test", 1).toString());
	}
	@Test
	public void testLinePosition11() {
		assertEquals("[1,1]", SH.getLinePosition("\nhis test", 2).toString());
	}
	@Test
	public void testLinePosition12() {
		assertEquals("[1,3]", SH.getLinePosition("\nhis test", 4).toString());
	}
	@Test
	public void testLinePosition13() {
		assertEquals("[1,2]", SH.getLinePosition("\nhis\ntest", 3).toString());
	}
	@Test
	public void testLinePosition14() {
		assertEquals("[1,3]", SH.getLinePosition("\nhis\ntest", 4).toString());
	}
	@Test
	public void testLinePosition15() {
		assertEquals("[2,0]", SH.getLinePosition("\nhis\ntest", 5).toString());
	}
	@Test
	public void testLinePosition16() {
		assertEquals("[2,1]", SH.getLinePosition("\nhis\ntest", 6).toString());
	}
	@Test
	public void testLinePosition17() {
		assertEquals("[3,0]", SH.getLinePosition("\nhis\n\n\ntest", 6).toString());
	}
	@Test
	public void testLinePosition18() {
		assertEquals("[4,0]", SH.getLinePosition("\nhis\n\n\ntest", 7).toString());
	}
	@Test
	public void testLinePosition19() {
		assertEquals("[4,1]", SH.getLinePosition("\nhis\n\n\ntest", 8).toString());
	}

	public void encodeDecode(String unencoded) {
		String encoded = SH.toStringEncode(unencoded, '"');
		System.out.println(encoded);
		assertEquals(unencoded, SH.toStringDecode(encoded));
	}

	@Test
	public void testCaseInsensitiveComparator1() {
		assertEquals(0, SH.COMPARATOR_CASEINSENSITIVE.compare("what", "what"));
	}
	@Test
	public void testCaseInsensitiveComparator2() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("WHAT", "what") < 0);
	}
	@Test
	public void testCaseInsensitiveComparator3() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("what", "WHAT") > 0);
	}
	@Test
	public void testCaseInsensitiveComparator4() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("a", "b") < 0);
	}
	@Test
	public void testCaseInsensitiveComparator5() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("c", "b") > 0);
	}
	@Test
	public void testCaseInsensitiveComparator6() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("a", "B") < 0);
	}
	@Test
	public void testCaseInsensitiveComparator7() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("c", "B") > 0);
	}
	@Test
	public void testCaseInsensitiveComparator8() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("A", "b") < 0);
	}
	@Test
	public void testCaseInsensitiveComparator9() {
		assertTrue(SH.COMPARATOR_CASEINSENSITIVE.compare("C", "b") > 0);
	}

	@Test
	public void testToString_1() {
		assertEquals("1", SH.toString(1));
	}
	@Test
	public void testToString_2() {
		assertEquals("-1", SH.toString(-1));
	}
	@Test
	public void testToString_3() {
		assertEquals("0", SH.toString(0));
	}
	@Test
	public void testToString_4() {
		for (long i = 1; i < Integer.MAX_VALUE * 4L; i = i * 2) {
			assertEquals(Long.toString(i), SH.toString(i));
		}
	}
	@Test
	public void testToString_5() {
		for (long i = 1; i < Integer.MAX_VALUE * 4L; i = i * 2) {
			assertEquals(Long.toString(-i), SH.toString(-i));
		}
	}
	@Test
	public void testToString_6() {
		for (int k = 0; k < 10; k++) {
			Duration d = new Duration("Long.toString()");
			for (long j = 1; j < 100; j++)
				for (long i = -1000; i < 1000; i++) {
					Long.toString(i);
				}
			d.stampStdout();
		}
	}
	@Test
	public void testToString_7() {
		for (int k = 0; k < 10; k++) {
			Duration d = new Duration("SH.toString()");
			for (long j = 1; j < 100; j++)
				for (long i = -1000; i < 1000; i++) {
					SH.toString(i);
				}
			d.stampStdout();
		}
	}

	@Test
	public void testToStringWithCommans1() {
		assertEquals("" + Long.MIN_VALUE, SH.toString(Long.MIN_VALUE, 10));
	}
	@Test
	public void testToStringWithCommans2() {
		assertEquals("0", SH.toStringWithCommas(',', 0L));
	}
	@Test
	public void testToStringWithCommans3() {
		assertEquals("1", SH.toStringWithCommas(',', 1L));
	}
	@Test
	public void testToStringWithCommans4() {
		assertEquals("10", SH.toStringWithCommas(',', 10L));
	}
	@Test
	public void testToStringWithCommans5() {
		assertEquals("100", SH.toStringWithCommas(',', 100L));
	}
	@Test
	public void testToStringWithCommans6() {
		assertEquals("1,000", SH.toStringWithCommas(',', 1000L));
	}
	@Test
	public void testToStringWithCommans7() {
		assertEquals("10,000", SH.toStringWithCommas(',', 10000L));
	}
	@Test
	public void testToStringWithCommans8() {
		assertEquals("100,000", SH.toStringWithCommas(',', 100000L));
	}
	@Test
	public void testToStringWithCommans9() {
		assertEquals("1,000,000", SH.toStringWithCommas(',', 1000000L));
	}
	@Test
	public void testToStringWithCommans10() {
		assertEquals("10,000,000", SH.toStringWithCommas(',', 10000000L));
	}
	@Test
	public void testToStringWithCommans11() {
		assertEquals("100,000,000", SH.toStringWithCommas(',', 100000000L));
	}
	@Test
	public void testToStringWithCommans12() {
		assertEquals("1,000,000,000", SH.toStringWithCommas(',', 1000000000L));
	}
	@Test
	public void testToStringWithCommans13() {
		assertEquals("10,000,000,000", SH.toStringWithCommas(',', 10000000000L));
	}
	@Test
	public void testToStringWithCommans14() {
		assertEquals("100,000,000,000", SH.toStringWithCommas(',', 100000000000L));
	}
	@Test
	public void testToStringWithCommans15() {
		assertEquals("1,000,000,000,000", SH.toStringWithCommas(',', 1000000000000L));
	}
	@Test
	public void testToStringWithCommans16() {
		assertEquals("10,000,000,000,000", SH.toStringWithCommas(',', 10000000000000L));
	}
	@Test
	public void testToStringWithCommans17() {
		assertEquals("100,000,000,000,000", SH.toStringWithCommas(',', 100000000000000L));
	}
	@Test
	public void testToStringWithCommans18() {
		assertEquals("1,000,000,000,000,000", SH.toStringWithCommas(',', 1000000000000000L));
	}
	@Test
	public void testToStringWithCommans19() {
		assertEquals("10,000,000,000,000,000", SH.toStringWithCommas(',', 10000000000000000L));
	}
	@Test
	public void testToStringWithCommans20() {
		assertEquals("100,000,000,000,000,000", SH.toStringWithCommas(',', 100000000000000000L));
	}
	@Test
	public void testToStringWithCommans21() {
		assertEquals("1,000,000,000,000,000,000", SH.toStringWithCommas(',', 1000000000000000000L));
	}
	@Test
	public void testToStringWithCommans22() {
		assertEquals("-1", SH.toStringWithCommas(',', -1L));
	}
	@Test
	public void testToStringWithCommans23() {
		assertEquals("-10", SH.toStringWithCommas(',', -10L));
	}
	@Test
	public void testToStringWithCommans24() {
		assertEquals("-100", SH.toStringWithCommas(',', -100L));
	}
	@Test
	public void testToStringWithCommans25() {
		assertEquals("-1,000", SH.toStringWithCommas(',', -1000L));
	}
	@Test
	public void testToStringWithCommans26() {
		assertEquals("-10,000", SH.toStringWithCommas(',', -10000L));
	}
	@Test
	public void testToStringWithCommans27() {
		assertEquals("-100,000", SH.toStringWithCommas(',', -100000L));
	}
	@Test
	public void testToStringWithCommans28() {
		assertEquals("-1,000,000", SH.toStringWithCommas(',', -1000000L));
	}
	@Test
	public void testToStringWithCommans29() {
		assertEquals("-10,000,000", SH.toStringWithCommas(',', -10000000L));
	}
	@Test
	public void testToStringWithCommans30() {
		assertEquals("-100,000,000", SH.toStringWithCommas(',', -100000000L));
	}
	@Test
	public void testToStringWithCommans31() {
		assertEquals("-1,000,000,000", SH.toStringWithCommas(',', -1000000000L));
	}
	@Test
	public void testToStringWithCommans32() {
		assertEquals("-10,000,000,000", SH.toStringWithCommas(',', -10000000000L));
	}
	@Test
	public void testToStringWithCommans33() {
		assertEquals("-100,000,000,000", SH.toStringWithCommas(',', -100000000000L));
	}
	@Test
	public void testToStringWithCommans34() {
		assertEquals("-1,000,000,000,000", SH.toStringWithCommas(',', -1000000000000L));
	}
	@Test
	public void testToStringWithCommans35() {
		assertEquals("-10,000,000,000,000", SH.toStringWithCommas(',', -10000000000000L));
	}
	@Test
	public void testToStringWithCommans36() {
		assertEquals("-100,000,000,000,000", SH.toStringWithCommas(',', -100000000000000L));
	}
	@Test
	public void testToStringWithCommans37() {
		assertEquals("-1,000,000,000,000,000", SH.toStringWithCommas(',', -1000000000000000L));
	}
	@Test
	public void testToStringWithCommans38() {
		assertEquals("-10,000,000,000,000,000", SH.toStringWithCommas(',', -10000000000000000L));
	}
	@Test
	public void testToStringWithCommans39() {
		assertEquals("-100,000,000,000,000,000", SH.toStringWithCommas(',', -100000000000000000L));
	}
	@Test
	public void testToStringWithCommans40() {
		assertEquals("-1,000,000,000,000,000,000", SH.toStringWithCommas(',', -1000000000000000000L));
	}
	@Test
	public void testToStringWithCommans41() {
		assertEquals("9,223,372,036,854,775,807", SH.toStringWithCommas(',', Long.MAX_VALUE));
	}
	@Test
	public void testToStringWithCommans42() {
		assertEquals("-9,223,372,036,854,775,807", SH.toStringWithCommas(',', Long.MIN_VALUE + 1));
	}
	@Test
	public void testToStringWithCommans43() {
		assertEquals("-9,223,372,036,854,775,808", SH.toStringWithCommas(',', Long.MIN_VALUE));
	}
	@Test
	public void testToStringWithCommans44() {
		assertEquals("9,876,543", SH.toStringWithCommas(',', 9876543L));
	}
	@Test
	public void testToStringWithCommans45() {
		assertEquals("8,765,432", SH.toStringWithCommas(',', 8765432L));
	}
	@Test
	public void testToStringWithCommans46() {
		assertEquals("2,999,999,999,999,999,999", SH.toStringWithCommas(',', 2999999999999999999L));
	}

	@Test
	public void assertEqualsIgnoreCase1() {
		assertTrue(SH.equalsIgnoreCase('c', 'c'));
	}
	@Test
	public void assertEqualsIgnoreCase2() {
		assertTrue(SH.equalsIgnoreCase('a', 'A'));
	}
	@Test
	public void assertEqualsIgnoreCase3() {
		assertTrue(SH.equalsIgnoreCase('9', '9'));
	}
	@Test
	public void assertEqualsIgnoreCase4() {
		assertFalse(SH.equalsIgnoreCase('a', 'b'));
	}
	@Test
	public void assertEqualsIgnoreCase5() {
		assertTrue(SH.equalsIgnoreCase("this", new StringBuilder("this")));
	}
	@Test
	public void assertEqualsIgnoreCase6() {
		assertFalse(SH.equalsIgnoreCase("hhis", new StringBuilder("this")));
	}
	@Test
	public void assertEqualsIgnoreCase7() {
		assertFalse(SH.equalsIgnoreCase("thih", new StringBuilder("this")));
	}
	@Test
	public void assertEqualsIgnoreCase8() {
		assertFalse(SH.equalsIgnoreCase("this1", new StringBuilder("this")));
	}
	@Test
	public void assertEqualsIgnoreCase9() {
		assertFalse(SH.equalsIgnoreCase("this", new StringBuilder("this1")));
	}
	@Test
	public void assertEqualsIgnoreCase10() {
		assertTrue(SH.equalsIgnoreCase("", new StringBuilder("")));
	}

	@Test
	public void testToString2_1() {
		assertEquals(Integer.toString(12, 12), SH.toString(12, 12));
	}
	@Test
	public void testToString2_2() {
		assertEquals(Integer.toString(876068352, 12), SH.toString(876068352, 12));
	}
	@Test
	public void testToString2_3() {
		assertEquals(Integer.toString(Integer.MAX_VALUE, 12), SH.toString(Integer.MAX_VALUE, 12));
	}
	@Test
	public void testToString2_4() {
		assertEquals(Integer.toString(Integer.MIN_VALUE, 12), SH.toString(Integer.MIN_VALUE, 12));
	}
	@Test
	public void testToString2_5() {
		Random r = new Random(321);
		for (int j = 2; j < 36; j++)
			for (int i = 0; i < 1000; i++) {
				int v = r.nextInt(1000 * 1000 * 1000);
				assertEquals("" + v + "," + j + "," + i, Integer.toString(v, j), SH.toString(v, j));
			}
	}

	@Test
	public void testToString3_1() {
		assertEquals(Integer.toString(12, 12), SH.toString(12L, 12));
	}
	@Test
	public void testToString3_2() {
		assertEquals(Integer.toString(876068352, 12), SH.toString(876068352L, 12));
	}
	@Test
	public void testToString3_3() {
		assertEquals(Long.toString(Long.MAX_VALUE, 12), SH.toString(Long.MAX_VALUE, 12));
	}
	@Test
	public void testToString3_4() {
		assertEquals(Long.toString(Long.MIN_VALUE, 12), SH.toString(Long.MIN_VALUE, 12));
	}
	@Test
	public void testToString3_5() {
		Random r = new Random(321);
		for (int j = 2; j < 36; j++)
			for (int i = 0; i < 1000; i++) {
				int v = r.nextInt(1000 * 1000 * 1000);
				assertEquals("" + v + "," + j + "," + i, Integer.toString(v, j), SH.toString(v, j));
			}
	}

	@Test
	public void testCamelHump1() {
		assertEquals("this test", SH.fromCamelHumps(" ", "thisTest"));
	}
	@Test
	public void testCamelHump2() {
		assertEquals("this is a test", SH.fromCamelHumps(" ", "thisIsATest"));
	}
	@Test
	public void testCamelHump3() {
		assertEquals("this is a test", SH.fromCamelHumps(" ", "thisIsATest"));
	}
	@Test
	public void testCamelHump4() {
		assertEquals("this is a test", SH.fromCamelHumps(" ", "ThisIsATest"));
	}
	@Test
	public void testCamelHump5() {
		assertEquals("thisTest", SH.toCamelHumps(" ", "this test", false));
	}
	@Test
	public void testCamelHump6() {
		assertEquals("thisIsTest", SH.toCamelHumps(" ", "this is test", false));
	}
	@Test
	public void testCamelHump7() {
		assertEquals("thisIsATest", SH.toCamelHumps(" ", "this is a test", false));
	}
	@Test
	public void testCamelHump8() {
		assertEquals("ThisIsATest", SH.toCamelHumps(" ", "this is a test", true));
	}
	@Test
	public void testCamelHump9() {
		testToFromCamel("this");
	}
	@Test
	public void testCamelHump10() {
		testToFromCamel("");
	}
	@Test
	public void testCamelHump11() {
		testToFromCamel("this is a test");
	}

	public void testToFromCamel(String text) {
		assertEquals(text, SH.fromCamelHumps(" ", SH.toCamelHumps(" ", text, false)));
		assertEquals(text, SH.fromCamelHumps(" ", SH.toCamelHumps(" ", text, true)));
	}

	@Test
	public void testTrimSb1() {
		assertTrimed("  ");
	}
	@Test
	public void testTrimSb2() {
		assertTrimed("test");
	}
	@Test
	public void testTrimSb3() {
		assertTrimed(" test ");
	}
	@Test
	public void testTrimSb4() {
		assertTrimed("  test  ");
	}
	@Test
	public void testTrimSb5() {
		assertTrimed("  t  ");
	}
	@Test
	public void testTrimSb6() {
		assertTrimed("t  ");
	}
	@Test
	public void testTrimSb7() {
		assertTrimed("  t");
	}

	private void assertTrimed(String string) {
		assertEquals(string.trim(), SH.trim(new StringBuilder(string)));
	}

	@Test
	public void testIndexOfIgnoreCase1() {
		testIndexOfIgnoreCase("test", "test");
	}
	@Test
	public void testIndexOfIgnoreCase2() {
		testIndexOfIgnoreCase("test this", "test");
	}
	@Test
	public void testIndexOfIgnoreCase3() {
		testIndexOfIgnoreCase("test this test", "test");
	}
	@Test
	public void testIndexOfIgnoreCase4() {
		testIndexOfIgnoreCase("test this test this tes", "test");
	}
	@Test
	public void testIndexOfIgnoreCase5() {
		testIndexOfIgnoreCase("test this test this tes", "t t");
	}
	@Test
	public void testIndexOfIgnoreCase6() {
		testIndexOfIgnoreCase("te", "t t");
	}
	@Test
	public void testIndexOfIgnoreCase7() {
		testIndexOfIgnoreCase("est fhis est his test2", "test2");
	}
	@Test
	public void testIndexOfIgnoreCase8() {
		testIndexOfIgnoreCase("est fhis est his test2", "tEsT2");
	}

	public static void testIndexOfIgnoreCase(String text, String find) {
		for (int i = 0; i < text.length(); i++) {
			int j = text.toUpperCase().indexOf(find.toUpperCase(), i);
			int k = text.toUpperCase().lastIndexOf(find.toUpperCase(), i);
			assertEquals(text + ":" + i, j, SH.indexOfIgnoreCase(text, find, i));
			assertEquals(text + ":" + i, j, SH.indexOfIgnoreCase(text, find.toUpperCase(), i));
			assertEquals(text + ":" + i, j, SH.indexOfIgnoreCase(text.toUpperCase(), find, i));
			assertEquals(text + ":" + i, k, SH.lastIndexOfIgnoreCase(text, find, i));
			assertEquals(text + ":" + i, k, SH.lastIndexOfIgnoreCase(text, find.toUpperCase(), i));
			assertEquals(text + ":" + i, k, SH.lastIndexOfIgnoreCase(text.toUpperCase(), find, i));
		}
	}

	@Test
	public void testStartsWithIgnoreCase1() {
		testStartsWithIgnoreCase("test", "test");
	}
	@Test
	public void testStartsWithIgnoreCase2() {
		testStartsWithIgnoreCase(" test", "test");
	}
	@Test
	public void testStartsWithIgnoreCase3() {
		testStartsWithIgnoreCase(" test", "tEsT");
	}
	@Test
	public void testStartsWithIgnoreCase4() {
		testStartsWithIgnoreCase(" here", "tEsT");
	}
	@Test
	public void testStartsWithIgnoreCase5() {
		testStartsWithIgnoreCase(" he", "tEsThe");
	}
	@Test
	public void testStartsWithIgnoreCase6() {
		testStartsWithIgnoreCase("he", "tEsThe");
	}
	@Test
	public void testStartsWithIgnoreCase7() {
		testStartsWithIgnoreCase("he", "a");
	}
	@Test
	public void testStartsWithIgnoreCase8() {
		testStartsWithIgnoreCase("abcd", "a");
	}
	@Test
	public void testStartsWithIgnoreCase9() {
		testStartsWithIgnoreCase("abcd", "b");
	}
	@Test
	public void testStartsWithIgnoreCase10() {
		testStartsWithIgnoreCase("abcd", "c");
	}
	@Test
	public void testStartsWithIgnoreCase11() {
		testStartsWithIgnoreCase("abcd", "d");
	}
	@Test
	public void testStartsWithIgnoreCase12() {
		testStartsWithIgnoreCase("abcd", "e");
	}
	@Test
	public void testStartsWithIgnoreCase13() {
		testStartsWithIgnoreCase("abcd", "abcde");
	}

	public static void testStartsWithIgnoreCase(String text, String find) {
		for (int i = 0; i < text.length(); i++) {
			boolean j = text.toUpperCase().startsWith(find.toUpperCase(), i);
			assertEquals(text + "," + find + ":" + i, j, SH.startsWithIgnoreCase(text, find, i));
			assertEquals(text + "," + find + ":" + i, j, SH.startsWithIgnoreCase(text, find.toUpperCase(), i));
			assertEquals(text + "," + find + ":" + i, j, SH.startsWithIgnoreCase(text.toUpperCase(), find, i));
		}
	}

	@Test
	public void testSplitToMap1() {
		testMap((Map) CH.m("this", "is", "test", "here"));
	}
	@Test
	public void testSplitToMap2() {
		testMap((Map) CH.m("this", "is", "test", "here", "asdf", "fasd"));
	}
	@Test
	public void testSplitToMap3() {
		testMap((Map) CH.m("this", "is", "test", "here", "asdf", "fasd", "", ""));
	}
	@Test
	public void testSplitToMap4() {
		testMap((Map) CH.m("what", "when"));
	}
	@Test
	public void testSplitToMap5() {
		testMap((Map) CH.m("", "when"));
	}
	@Test
	public void testSplitToMap6() {
		testMap((Map) CH.m("when", ""));
	}
	@Test
	public void testSplitToMap7() {
		testMap((Map) CH.m("", ""));
	}
	@Test
	public void testSplitToMap8() {
		testMap((Map) CH.m("", "b"));
	}
	@Test
	public void testSplitToMap9() {
		testMap((Map) CH.m("a", ""));
	}
	@Test
	public void testSplitToMap10() {
		testMap((Map) CH.m("a", "b"));
	}
	@Test
	public void testSplitToMap11() {
		testMap((Map) CH.m());
	}

	private static void testMap(Map<String, String> m) {
		m = new TreeMap<String, String>(m);
		String text = SH.joinMap(',', '=', '\\', m);
		System.out.println(text);
		Map<String, String> m2 = SH.splitToMap(new TreeMap<String, String>(), ',', '=', '\\', text);
		assertEquals(m, m2);
		text = SH.joinMap("<comma>", "<equals>", m);
		System.out.println(text);
		m2 = SH.splitToMap(new TreeMap<String, String>(), "<comma>", "<equals>", text);
		assertEquals(m, m2);
	}

	@Test
	public void testSplitToMap() {
		assertEquals(CH.m("this", "that"), SH.splitToMap(',', '=', '\\', "this=that"));
		assertEquals(CH.m("th=is", "that"), SH.splitToMap(',', '=', '\\', "th\\=is=that"));
		assertEquals(CH.m("th=is", "that,this=those"), SH.splitToMap(',', '=', '\\', "th\\=is=that\\,this\\=those"));
		assertEquals("this\\,\\=that=these\\,\\=those", SH.joinMap(',', '=', '\\', CH.m("this,=that", "these,=those")));
		Map m = CH.m("this,that=those", "these,", ",what", "=", ",", ",", ",", "=", "=", ",=,=,");
		testMap(m);
	}

	@Test
	public void testHex() {
		Random r = new Random(123);
		for (int i = 0; i < 32; i++) {
			byte b[] = new byte[i];
			for (int j = 0; j < i; j++)
				b[j] = (byte) r.nextInt();
			testHex(b);
		}
	}

	public static void testHex(byte[] data) {
		String text = SH.toHex(data);
		assertArrayEquals(text, data, SH.fromHex(text));
	}

	@Test
	public void testParseIntCharSequence1() {
		testPartInt(0);
	}
	@Test
	public void testParseIntCharSequence2() {
		testPartInt(-1);
	}
	@Test
	public void testParseIntCharSequence3() {
		testPartInt(1);
	}
	@Test
	public void testParseIntCharSequence4() {
		testPartInt(Integer.MIN_VALUE);
	}
	@Test
	public void testParseIntCharSequence5() {
		testPartInt(Integer.MAX_VALUE);
	}
	@Test
	public void testParseIntCharSequence6() {
		Random r = new Random(123);
		for (int i = 0; i < 10000; i++) {
			testPartInt(r.nextInt());
		}
	}

	@Test(expected = NumberFormatException.class)
	public void testParseIntOOB() {
		SH.parseInt(new StringBuilder(Long.toString(Integer.MAX_VALUE + 1L)));
	}
	@Test(expected = NumberFormatException.class)
	public void testParseIntOOB2() {
		SH.parseInt(new StringBuilder(Long.toString(Integer.MIN_VALUE - 1L)));
	}

	public static void testPartInt(int i) {
		assertEquals(i, SH.parseInt(new StringBuilder(Integer.toString(i))));
	}

	@Test
	@Ignore("This signature of URLEncode is deprecated, returns different values on different platforms, testing non AMI code")
	public void testEncodeUrl1() {
		int i = 'è';//232 195 + 168
		assertEquals("%3Bwhat", URLEncoder.encode(";what"));
	}
	@Test
	@Ignore("This signature of URLEncode is deprecated, returns different values on different platforms, testing non AMI code")
	public void testEncodeUrl2() {
		assertEquals("%C3%A8", URLEncoder.encode("è"));
	}
	@Test
	public void testEncodeUrl3() {
		assertEquals("%3Bwhat", SH.encodeUrl(";what"));
	}
	@Test
	public void testEncodeUrl4() {
		assertEquals("%C3%A8", SH.encodeUrl("è"));
	}
	@Test
	public void testEncodeUrl5() {
		assertEquals("%C3%A8%C3%A8%C3%A8", SH.encodeUrl("èèè"));
	}
	@Test
	public void testEncodeUrl6() {
		assertEquals("t%C3%A8st", SH.encodeUrl("tèst"));
	}
	@Test
	public void testEncodeUrl7() {
		assertEquals("+what", SH.encodeUrl(" what"));
	}
	@Test
	public void testEncodeUrl8() {
		assertEquals("testthisout", SH.encodeUrl("testthisout"));
	}
	@Test
	public void testEncodeUrl9() {
		assertEquals("test+what", SH.encodeUrl("test what"));
	}
	@Test
	public void testEncodeUrl10() {
		assertEquals("test%25what", SH.encodeUrl("test%what"));
	}
	@Test
	public void testEncodeUrl11() {
		assertEquals("%5Bwhat%5Dand%5Bwhere%5D", SH.encodeUrl("[what]and[where]"));
	}
	@Test
	public void testEncodeUrl12() {
		assertEquals("%21%2A%27%28%29%3B%3A%40%26%3D%2B%24%2C%2F%3F%23%5B%5D", SH.encodeUrl("!*'();:@&=+$,/?#[]"));
	}
	@Test
	public void testDecodeUrl1() {
		assertEquals("   ", SH.decodeUrl("%20%20%20"));
	}
	@Test
	public void testDecodeUrl2() {
		assertEquals("è", SH.decodeUrl("%c3%a8"));
	}
	@Test
	public void testDecodeUrl3() {
		assertEquals("test", SH.decodeUrl("test"));
	}
	@Test
	public void testDecodeUrl4() {
		assertEquals("test this", SH.decodeUrl("test+this"));
	}
	@Test
	public void testDecodeUrl5() {
		assertEquals("test this", SH.decodeUrl("test%20this"));
	}
	@Test
	public void testDecodeUrl6() {
		assertEquals("test this there", SH.decodeUrl("test%20this%20there"));
	}
	@Test
	public void testDecodeUrl7() {
		assertEquals("a   b", SH.decodeUrl("a%20%20%20b"));
	}
	@Test
	public void testDecodeUrl8() {
		assertEquals("a    b", SH.decodeUrl("a%20+%20%20b"));
	}
	@Test
	public void testDecodeUrl9() {
		assertEquals("a    b", SH.decodeUrl("a%20+%20%20b"));
	}
	@Test
	public void testDecodeUrl10() {
		assertEquals("ë", SH.decodeUrl("%c3%AB"));
		assertEquals("ë", SH.decodeUrl("%c3%AB", 0, 6, new StringBuilder()).toString());
		String s = "%F0%9F%98%8A";
		String base = "😊";
		assertEquals(base, SH.decodeUrl(s));
		assertEquals(s, SH.encodeUrl(base));
	}
	@Test
	public void testDecodeUrl11() {
		assertEquals("[{\"x\":10,\"y\":18,\"v\":\"België/Belgique\"}]", SH.decodeUrl(
				"cells=%5B%7B%22x%22%3A10%2C%22y%22%3A18%2C%22v%22%3A%22Belgi%C3%AB%2FBelgique%22%7D%5D&submit=true&type=rows_edit&portletId=QTgTg6Mm1T&pageUid=2013992575&seqnum=90",
				6, 86, new StringBuilder()).toString());
	}

	//TODO: why is this failing??? @Test
	@Test
	public void testEncodeDecodeUrl() {
		testEncodeDecodeUrl("asdfasdf");
		testEncodeDecodeUrl("asdR%f[as]df");
		Random r = new Random(4371);
		for (int j = 0; j < 10; j++) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < 1000; i++)
				sb.append((char) r.nextInt(256));
			testEncodeDecodeUrl(sb.toString());
		}
	}

	public void testEncodeDecodeUrl(String text) {
		String encode = SH.encodeUrl(text);
		String text2 = SH.decodeUrl(encode);
		System.out.println(encode);
		assertEquals(encode, text, text2);
	}

	@Test
	public void testTrim2_1() {
		assertEquals("blah", SH.trim("blah", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_2() {
		assertEquals("blah", SH.trim("  blah  ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_3() {
		assertEquals("b", SH.trim("  b ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_4() {
		assertEquals("", SH.trim("   ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_5() {
		assertEquals("", SH.trim(null, new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_6() {
		assertEquals("a", SH.trim(" a", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_7() {
		assertEquals("a", SH.trim("a ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_8() {
		assertEquals("a", SH.trim(" a ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_9() {
		assertEquals("ab", SH.trim(" ab", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_10() {
		assertEquals("ab", SH.trim("ab ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_11() {
		assertEquals("ab", SH.trim(" ab ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_12() {
		assertEquals("a b", SH.trim(" a b ", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_13() {
		assertEquals("a b", SH.trim("   a b", new StringBuilder()).toString());
	}
	@Test
	public void testTrim2_14() {
		assertEquals("a b", SH.trim("a b    ", new StringBuilder()).toString());
	}

	@Test
	public void testUnescapeUntil1() {
		StringBuilder out = new StringBuilder();
		assertEquals(-1, SH.unescapeUntil("test", 0, '!', '$', SH.clear(out)));
		assertEquals("test", out.toString());
	}
	@Test
	public void testUnescapeUntil2() {
		StringBuilder out = new StringBuilder();
		assertEquals(4, SH.unescapeUntil("test$this", 0, '!', '$', SH.clear(out)));
		assertEquals("test", out.toString());
	}
	@Test
	public void testUnescapeUntil3() {
		StringBuilder out = new StringBuilder();

		assertEquals(0, SH.unescapeUntil("$test$this", 0, '!', '$', SH.clear(out)));
		assertEquals("", out.toString());
	}
	@Test
	public void testUnescapeUntil4() {
		StringBuilder out = new StringBuilder();

		assertEquals(5, SH.unescapeUntil("$test$this$", 5, '!', '$', SH.clear(out)));
		assertEquals("", out.toString());
	}
	@Test
	public void testUnescapeUntil5() {
		StringBuilder out = new StringBuilder();

		assertEquals(10, SH.unescapeUntil("$test$this$", 6, '!', '$', SH.clear(out)));
		assertEquals("this", out.toString());
	}
	@Test
	public void testUnescapeUntil6() {
		StringBuilder out = new StringBuilder();

		assertEquals(6, SH.unescapeUntil("$test$$", 6, '!', '$', SH.clear(out)));
		assertEquals("", out.toString());
	}
	@Test
	public void testUnescapeUntil7() {
		StringBuilder out = new StringBuilder();

		assertEquals(7, SH.unescapeUntil("$test$t$", 6, '!', '$', SH.clear(out)));
		assertEquals("t", out.toString());
	}
	@Test
	public void testUnescapeUntil8() {
		StringBuilder out = new StringBuilder();

		assertEquals(7, SH.unescapeUntil("$test$t$", 7, '!', '$', SH.clear(out)));
		assertEquals("", out.toString());
	}
	@Test
	public void testUnescapeUntil9() {
		StringBuilder out = new StringBuilder();

		assertEquals(-1, SH.unescapeUntil("test!$this", 0, '!', '$', SH.clear(out)));
		assertEquals("test$this", out.toString());
	}
	@Test
	public void testUnescapeUntil10() {
		StringBuilder out = new StringBuilder();

		assertEquals(-1, SH.unescapeUntil("test!!!$this", 0, '!', '$', SH.clear(out)));
		assertEquals("test!$this", out.toString());
	}
	@Test
	public void testUnescapeUntil11() {
		StringBuilder out = new StringBuilder();

		assertEquals(-1, SH.unescapeUntil("!!!!test!!!$this!!!!", 0, '!', '$', SH.clear(out)));
		assertEquals("!!test!$this!!", out.toString());
	}
	@Test
	public void testUnescapeUntil12() {
		StringBuilder out = new StringBuilder();

		assertEquals(20, SH.unescapeUntil("!!!!test!!!$this!!!!$", 0, '!', '$', SH.clear(out)));
		assertEquals("!!test!$this!!", out.toString());
	}

	@Test
	public void testPropertiesHelperSplitVars1() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("", CH.clear(sink));
		assertEquals(CH.l(""), sink);
	}
	@Test
	public void testPropertiesHelperSplitVars2() {
		List<String> sink = new ArrayList<String>();
		PropertiesHelper.splitVariables("this", CH.clear(sink));
		assertEquals(CH.l("this"), sink);
	}
	@Test
	public void testParseConstants() {
		assertEquals(1, SH.parseConstant("1"));
		assertEquals(10, SH.parseConstant("10"));
		assertEquals(100, SH.parseConstant("100"));
		assertEquals(1000, SH.parseConstant("1000"));
		assertEquals(10000, SH.parseConstant("10000"));
		assertEquals(100000, SH.parseConstant("100000"));
		assertEquals(1000000, SH.parseConstant("1000000"));
		assertEquals(10000000, SH.parseConstant("10000000"));
		assertEquals(100000000, SH.parseConstant("100000000"));
		assertEquals(1000000000, SH.parseConstant("1000000000"));
		assertEquals(10000000000L, SH.parseConstant("10000000000"));
		assertEquals(100000000000L, SH.parseConstant("100000000000"));
		assertEquals(1000000000000L, SH.parseConstant("1000000000000"));
		assertEquals(10000000000000L, SH.parseConstant("10000000000000"));
		assertEquals(100000000000000L, SH.parseConstant("100000000000000"));
		assertEquals(1000000000000000L, SH.parseConstant("1000000000000000"));
		assertEquals(10000000000000000L, SH.parseConstant("10000000000000000"));
		assertEquals(100000000000000000L, SH.parseConstant("100000000000000000"));
		assertEquals(1000000000000000000L, SH.parseConstant("1000000000000000000"));
		assertEquals(5, SH.parseConstant("5"));
		assertEquals(50, SH.parseConstant("50"));
		assertEquals(500, SH.parseConstant("500"));
		assertEquals(5000, SH.parseConstant("5000"));
		assertEquals(50000, SH.parseConstant("50000"));
		assertEquals(500000, SH.parseConstant("500000"));
		assertEquals(5000000, SH.parseConstant("5000000"));
		assertEquals(50000000, SH.parseConstant("50000000"));
		assertEquals(500000000, SH.parseConstant("500000000"));
		assertEquals(5000000000L, SH.parseConstant("5000000000"));
		assertEquals(50000000000L, SH.parseConstant("50000000000"));
		assertEquals(500000000000L, SH.parseConstant("500000000000"));
		assertEquals(5000000000000L, SH.parseConstant("5000000000000"));
		assertEquals(50000000000000L, SH.parseConstant("50000000000000"));
		assertEquals(500000000000000L, SH.parseConstant("500000000000000"));
		assertEquals(5000000000000000L, SH.parseConstant("5000000000000000"));
		assertEquals(50000000000000000L, SH.parseConstant("50000000000000000"));
		assertEquals(500000000000000000L, SH.parseConstant("500000000000000000"));
		assertEquals(5000000000000000000L, SH.parseConstant("5000000000000000000"));
		assertEquals(-1, SH.parseConstant("-1"));
		assertEquals(-10, SH.parseConstant("-10"));
		assertEquals(-100, SH.parseConstant("-100"));
		assertEquals(-1000, SH.parseConstant("-1000"));
		assertEquals(-10000, SH.parseConstant("-10000"));
		assertEquals(-100000, SH.parseConstant("-100000"));
		assertEquals(-1000000, SH.parseConstant("-1000000"));
		assertEquals(-10000000, SH.parseConstant("-10000000"));
		assertEquals(-100000000, SH.parseConstant("-100000000"));
		assertEquals(-1000000000, SH.parseConstant("-1000000000"));
		assertEquals(-10000000000L, SH.parseConstant("-10000000000"));
		assertEquals(-100000000000L, SH.parseConstant("-100000000000"));
		assertEquals(-1000000000000L, SH.parseConstant("-1000000000000"));
		assertEquals(-10000000000000L, SH.parseConstant("-10000000000000"));
		assertEquals(-100000000000000L, SH.parseConstant("-100000000000000"));
		assertEquals(-1000000000000000L, SH.parseConstant("-1000000000000000"));
		assertEquals(-10000000000000000L, SH.parseConstant("-10000000000000000"));
		assertEquals(-100000000000000000L, SH.parseConstant("-100000000000000000"));
		assertEquals(-1000000000000000000L, SH.parseConstant("-1000000000000000000"));
		assertEquals(-5, SH.parseConstant("-5"));
		assertEquals(-50, SH.parseConstant("-50"));
		assertEquals(-500, SH.parseConstant("-500"));
		assertEquals(-5000, SH.parseConstant("-5000"));
		assertEquals(-50000, SH.parseConstant("-50000"));
		assertEquals(-500000, SH.parseConstant("-500000"));
		assertEquals(-5000000, SH.parseConstant("-5000000"));
		assertEquals(-50000000, SH.parseConstant("-50000000"));
		assertEquals(-500000000, SH.parseConstant("-500000000"));
		assertEquals(-5000000000L, SH.parseConstant("-5000000000"));
		assertEquals(-50000000000L, SH.parseConstant("-50000000000"));
		assertEquals(-500000000000L, SH.parseConstant("-500000000000"));
		assertEquals(-5000000000000L, SH.parseConstant("-5000000000000"));
		assertEquals(-50000000000000L, SH.parseConstant("-50000000000000"));
		assertEquals(-500000000000000L, SH.parseConstant("-500000000000000"));
		assertEquals(-5000000000000000L, SH.parseConstant("-5000000000000000"));
		assertEquals(-50000000000000000L, SH.parseConstant("-50000000000000000"));
		assertEquals(-500000000000000000L, SH.parseConstant("-500000000000000000"));
		assertEquals(-5000000000000000000L, SH.parseConstant("-5000000000000000000"));
		assertEquals(0, SH.parseConstant("-0"));
		assertEquals(0, SH.parseConstant("0"));
		assertEquals(Integer.MAX_VALUE, SH.parseConstant(new Integer(Integer.MAX_VALUE).toString()));
		assertEquals(Integer.MIN_VALUE, SH.parseConstant(new Integer(Integer.MIN_VALUE).toString()));
		assertEquals(Long.MAX_VALUE, SH.parseConstant(new Long(Long.MAX_VALUE).toString()));
		assertEquals(Long.MIN_VALUE, SH.parseConstant(new Long(Long.MIN_VALUE).toString()));
	}
	@Test
	public void testPropertiesHelperSplitVars3() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("this${that}", CH.clear(sink));
		assertEquals(CH.l("this", "that", ""), sink);
	}
	@Test
	public void testPropertiesHelperSplitVars4() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("this${that}these", CH.clear(sink));
		assertEquals(CH.l("this", "that", "these"), sink);
	}
	@Test
	public void testPropertiesHelperSplitVars5() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("this\\${that}these", CH.clear(sink));
		assertEquals(CH.l("this${that}these"), sink);
	}
	@Test
	public void testPropertiesHelperSplitVars6() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("this$thatthese", CH.clear(sink));
		assertEquals(CH.l("this$thatthese"), sink);
	}
	@Test
	public void testPropertiesHelperSplitVars7() {
		List<String> sink = new ArrayList<String>();

		PropertiesHelper.splitVariables("t${a}${b},${c},${d}", CH.clear(sink));
		assertEquals(CH.l("t", "a", "", "b", ",", "c", ",", "d", ""), sink);
	}

	@Test
	public void testParseIntWithRadix() {
		Random r = new Random(123);
		for (int i = 0; i < 1000000; i++) {
			int j = i == 0 ? Integer.MAX_VALUE : i == 1 ? Integer.MIN_VALUE : r.nextInt();
			int k = 2 + r.nextInt(80);
			String text = SH.toString(j, k);
			if (k < 16) {
				assertEquals(j, SH.parseInt(text.toLowerCase(), k));
				assertEquals(j, SH.parseInt(text.toUpperCase(), k));
			} else
				assertEquals(j, SH.parseInt(text, k));
		}
	}
	@Test
	public void testParseLongWithRadix() {
		Random r = new Random(123);
		for (int i = 0; i < 1000000; i++) {
			long j = i == 0 ? Long.MAX_VALUE : i == 1 ? Long.MIN_VALUE : r.nextLong();
			int k = 2 + r.nextInt(80);
			String text = SH.toString(j, k);
			assertEquals("for " + j + ", " + k + ": " + text, j, SH.parseLong(text, k));
		}
	}

	@Test
	public void testSplitLines1() {
		splitLineTest();
	}
	@Test
	public void testSplitLines2() {
		splitLineTest("test");
	}
	@Test
	public void testSplitLines3() {
		splitLineTest("test", "", "this");
	}
	@Test
	public void testSplitLines4() {
		splitLineTest("test", "", "", "this");
	}
	@Test
	public void testSplitLines5() {
		splitLineTest("test", "", "booo", "", "this");
	}
	@Test
	public void testSplitLines6() {
		splitLineTest("", "test", "", "booo", "", "this");
	}

	@Test
	public void testEscapeInplace1() {
		testEscapeInspace("hello");
	}
	@Test
	public void testEscapeInplace2() {
		testEscapeInspace("he!l===lo");
	}
	@Test
	public void testEscapeInplace3() {
		testEscapeInspace("!!");
	}
	@Test
	public void testEscapeInplace4() {
		testEscapeInspace("==");
	}
	@Test
	public void testEscapeInplace5() {
		testEscapeInspace("!");
	}
	@Test
	public void testEscapeInplace6() {
		testEscapeInspace("=");
	}
	@Test
	public void testEscapeInplace7() {
		testEscapeInspace("");
	}
	@Test
	public void testEscapeInplace8() {
		testEscapeInspace("hello=everyone!");
	}

	private void testEscapeInspace(String string) {
		StringBuilder sb = new StringBuilder(string);
		SH.escapeInplace(sb, 0, sb.length(), '=', '!');
		assertEquals(SH.escape(string, '=', '!'), sb.toString());
		System.out.println(string + " : " + sb.toString());
	}

	@Test
	public void testSplitWithEscapes1() {
		assertArrayEquals(AH.a("this", "is", "test"), SH.splitWithEscape(',', '|', "this,is,test"));
	}
	@Test
	public void testSplitWithEscapes2() {
		assertArrayEquals(AH.a("this,is", "test"), SH.splitWithEscape(',', '|', "this|,is,test"));
	}
	@Test
	public void testSplitWithEscapes3() {
		assertArrayEquals(AH.a("this,i|s", "test"), SH.splitWithEscape(',', '|', "this|,i||s,test"));
	}
	@Test
	public void testSplitWithEscapes4() {
		assertArrayEquals(AH.a(","), SH.splitWithEscape(',', '|', "|,"));
	}

	private void splitLineTest(String... parts) {
		String text = SH.join('\n', parts);
		String text2 = SH.join("\r\n", parts);
		String text3 = SH.join("\r", parts);
		assertArrayEquals(parts, SH.splitLines(text));
		assertArrayEquals(parts, SH.splitLines(text2));
		assertArrayEquals(parts, SH.splitLines(text + "\n"));
		assertArrayEquals(parts, SH.splitLines(text2 + "\r\n"));
		assertArrayEquals(parts, SH.splitLines(text3));
	}
	@Test
	public void testJoinMapEscape1() {
		assertEquals("a=rob,b=dave", SH.joinMap(',', '=', '\\', CH.m(new LinkedHashMap(), "a", "rob", "b", "dave")));
	}
	@Test
	public void testJoinMapEscape2() {
		assertEquals("a=rob,b=dave\\,steve", SH.joinMap(',', '=', '\\', CH.m(new LinkedHashMap(), "a", "rob", "b", "dave,steve")));
	}
	@Test
	public void testJoinMapEscape3() {
		testJoinSplitEscape("a", "rob", "b", "steve=dave\\and rob");
	}
	@Test
	public void testJoinMapEscape4() {
		testJoinSplitEscape("a", "rob", "b", "steve=dave\\\\and rob");
	}
	@Test
	public void testJoinMapEscape5() {
		testJoinSplitEscape("a", "rob", "b", "steve=dave\\\\and rob=asdf\\=blasdf===dfs\\,,,==", "c", "=", "d", "", "elk", "\\", "foo", "=");
	}

	public void testJoinSplitEscape(String... kv) {
		Map<Object, Object> m = CH.m(kv);
		String txt = SH.joinMap(',', '=', '\\', m);
		Map<String, String> m2 = SH.splitToMap(',', '=', '\\', txt);
		assertEquals(m, m2);
	}

	@Test
	public void testValidEmail1() {
		assertFalse(SH.isValidEmail("robert@3forge"));
	}
	@Test
	public void testValidEmail2() {
		assertFalse(SH.isValidEmail("robert#3forge.com"));
	}
	@Test
	public void testValidEmail3() {
		assertFalse(SH.isValidEmail("robert@3forge.!!"));
	}
	@Test
	public void testValidEmail4() {
		assertFalse(SH.isValidEmail("robert@3forge..com"));
	}
	@Test
	public void testValidEmail5() {
		assertFalse(SH.isValidEmail("robert.__test+1@3forge.-blah.garbage.t.com"));
	}
	@Test
	public void testValidEmail6() {
		assertFalse(SH.isValidEmail("robert.__test+1@3forge.b-lah.garbage-.t.com"));
	}
	@Test
	public void testValidEmail7() {
		assertFalse(SH.isValidEmail("\"some crazier\" email\"@3forge.com"));
	}
	@Test
	public void testValidEmail8() {
		assertTrue(SH.isValidEmail("robert@3forge.com"));
	}
	@Test
	public void testValidEmail9() {
		assertTrue(SH.isValidEmail("robert.test@3forge.com"));
	}
	@Test
	public void testValidEmail10() {
		assertTrue(SH.isValidEmail("robert.test+1@3forge.com"));
	}
	@Test
	public void testValidEmail11() {
		assertTrue(SH.isValidEmail("robert.__test+1@3forge.com"));
	}
	@Test
	public void testValidEmail12() {
		assertTrue(SH.isValidEmail("robert.__test+1@3forge.b-lah.garbage.t.com"));
	}
	@Test
	public void testValidEmail13() {
		assertTrue(SH.isValidEmail("\"some crazy email\"@3forge.com"));
	}
	@Test
	public void testValidEmail14() {
		assertTrue(SH.isValidEmail("Blah Support <support@test.com>"));
	}
	@Test
	public void testCount1() {
		assertEquals(0, SH.count('a', ""));
	}
	@Test
	public void testCount2() {
		assertEquals(1, SH.count('a', "what"));
	}
	@Test
	public void testCount3() {
		assertEquals(2, SH.count('a', "aonea"));
	}
	@Test
	public void testCount4() {
		assertEquals(5, SH.count('a', "aaaaa"));
	}
	@Test
	public void testCount5() {
		assertEquals(5, SH.count('a', "abacadaea"));
	}
	@Test
	public void testCount6() {
		assertEquals(0, SH.count("abc", "ab", false));
	}
	@Test
	public void testCount7() {
		assertEquals(1, SH.count("abc", "abc", false));
	}
	@Test
	public void testCount8() {
		assertEquals(2, SH.count("abc", "abcabc", false));
	}
	@Test
	public void testCount9() {
		assertEquals(2, SH.count("abc", "bcabcabcab", false));
	}
	@Test
	public void testCount10() {
		assertEquals(3, SH.count("abc", "bcabcabcabc", false));
	}
	@Test
	public void testCount11() {
		assertEquals(1, SH.count("abab", "ababab", false));
	}
	@Test
	public void testCount12() {
		assertEquals(2, SH.count("abab", "ababab", true));
	}
	@Test
	public void testCount13() {
		assertEquals(3, SH.count("abab", "abababab", true));
	}
	@Test
	public void testCount14() {
		assertEquals(5, SH.count("aa", "aaaaaa", true));
	}
	@Test
	public void testCount15() {
		assertEquals(3, SH.count("what", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testCount16() {
		assertEquals(2, SH.count("is", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testCount17() {
		assertEquals(1, SH.count("isnt", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testCount18() {
		assertEquals(0, SH.count("isn't", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testCount19() {
		assertEquals(4, SH.count("i", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testCount20() {
		assertEquals(0, SH.count("", "what is going on with what I thought was what isnt", true));
	}
	@Test
	public void testRemoveChar() {
		StringBuilder sb = new StringBuilder("test");
		SH.removeChar(sb, 1);
		assertEquals("tst", sb.toString());
		SH.removeChar(sb, 1);
		assertEquals("tt", sb.toString());
		SH.removeChar(sb, 1);
		assertEquals("t", sb.toString());
		SH.removeChar(sb, 0);
		assertEquals("", sb.toString());
	}

	@Test
	public void testGetNextId1() {
		assertEquals("test", SH.getNextId("test", (Set) CH.s()));
	}
	@Test
	public void testGetNextId2() {
		assertEquals("test1", SH.getNextId("test", (Set) CH.s("test")));
	}
	@Test
	public void testGetNextId3() {
		assertEquals("test2", SH.getNextId("test", (Set) CH.s("test", "test1", "test4")));
	}
	@Test
	public void testGetNextId4() {
		assertEquals("4", SH.getNextId("", (Set) CH.s("", "1", "2", "3")));
	}
	@Test
	public void testGetNextId5() {
		assertEquals("t431", SH.getNextId("t425", (Set) CH.s("t425", "t426", "t427", "t428", "t429", "t430")));
	}

	@Test
	public void testDuration1() {
		assertEquals(36000L, SH.parseDurationTo("10 HOURS", TimeUnit.SECONDS));
	}
	@Test
	public void testDuration2() {
		assertEquals(60L, SH.parseDurationTo("1 HOUR", TimeUnit.MINUTES));
	}
	@Test
	public void testDuration3() {
		assertEquals(-36000L, SH.parseDurationTo("-10 HOURS", TimeUnit.SECONDS));
	}
	@Test
	public void testDuration4() {
		assertEquals(-60L, SH.parseDurationTo("-1 HOUR", TimeUnit.MINUTES));
	}
	@Test
	public void testDuration5() {
		assertEquals(150L, SH.parseDurationTo("2:30", TimeUnit.MINUTES));
	}
	@Test
	public void testDuration6() {
		assertEquals(9010000L, SH.parseDurationTo("2:30:10", TimeUnit.MILLISECONDS));
	}
	@Test
	public void testDuration7() {
		assertEquals(9010111L, SH.parseDurationTo("2:30:10.111", TimeUnit.MILLISECONDS));
	}
	@Test
	public void testDuration8() {
		assertEquals(-150L, SH.parseDurationTo("-2:30", TimeUnit.MINUTES));
	}
	@Test
	public void testDuration9() {
		assertEquals(-9010000L, SH.parseDurationTo("-2:30:10", TimeUnit.MILLISECONDS));
	}
	@Test
	public void testDuration10() {
		assertEquals(-9010111L, SH.parseDurationTo("-2:30:10.111", TimeUnit.MILLISECONDS));
	}

	@Test
	public void testReverse1() {
		assertEquals("", SH.reverse(new StringBuilder(""), 0, 0).toString());
	}
	@Test
	public void testReverse2() {
		assertEquals("t", SH.reverse(new StringBuilder("t"), 0, 1).toString());
	}
	@Test
	public void testReverse3() {
		assertEquals("aset", SH.reverse(new StringBuilder("tesa"), 0, 4).toString());
	}
	@Test
	public void testReverse4() {
		assertEquals("hctal", SH.reverse(new StringBuilder("latch"), 0, 5).toString());
	}
	@Test
	public void testReverse5() {
		assertEquals("lctah", SH.reverse(new StringBuilder("latch"), 1, 4).toString());
	}
	@Test
	public void testToJavaConst1() {

		assertEquals("this and that", SH.toStringEncode("this and that", '"'));
	}
	@Test
	public void testToJavaConst2() {
		assertEquals("this and that\\n", SH.toStringEncode("this and that\n", '"'));
	}
	@Test
	public void testToJavaConst3() {
		assertEquals("\\tthis and that\\n", SH.toStringEncode("\tthis and that\n", '"'));
	}
	@Test
	public void testToJavaConst4() {
		assertEquals("\\tthis and\\u0001 that\\n", SH.toStringEncode("\tthis and\u0001 that\n", '"'));
	}
	@Test
	public void testToJavaConst5() {
		assertEquals("\\tthis and\\u0001\\ufffa that\\n", SH.toStringEncode("\tthis and\u0001\ufffa that\n", '"'));
	}
	@Test
	public void testToJavaConst6() {
		assertEquals("\\u0123", SH.toStringEncode("\u0123", '"'));
	}
	@Test
	public void testToJavaConst7() {
		assertEquals("\\u00ff", SH.toStringEncode("\u00ff", '"'));
	}

	private static final long[] EDGE = new long[] { 1, 15, 153, 1538, 15389, 153899, 1538999, -1, -15, -153, -1538, -15389, -153899, -1538999, Long.MAX_VALUE, 0, -1, 1, 1234,
			-1234, Long.MIN_VALUE, Long.MAX_VALUE - 10L, Long.MIN_VALUE + 10 };
	private static final double[] EDGE_DOUBLE = new double[] { Double.MIN_VALUE, 0, -1, 1, 1234, -1234, .000123, -.000123, Double.MAX_VALUE };

	public void testParseInt(int number) {
		String n = Integer.toString(number);
		assertEquals(n, SH.toString(number));
		assertEquals(SH.parseInt((CharSequence) n), number);
	}

	public static void main(String[] a) {
		String[] t = new String[100000];
		Random r = new Random();
		for (int i = 0; i < t.length; i++) {
			t[i] = Integer.toString(r.nextInt(10000000));
		}
		for (int i = 0; i < 10; i++) {
			Duration d = new Duration();
			for (int j = 0; j < t.length * 100; j++) {
				SH.parseInt((CharSequence) t[j % t.length]);
			}
			d.stampStdout();
		}
		System.out.println();
		for (int i = 0; i < 10; i++) {
			Duration d = new Duration();
			for (int j = 0; j < t.length * 100; j++) {
				Integer.parseInt((String) t[j % t.length]);
			}
			d.stampStdout();
		}
		Duration d = new Duration();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < t.length * 100; j++) {
				SH.parseInt((CharSequence) t[j % t.length]);
			}
			d.stampStdout();
		}
		d = new Duration();
		System.out.println();
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < t.length * 100; j++) {
				Integer.parseInt((String) t[j % t.length]);
			}
			d.stampStdout();
		}
	}

	@Test
	public void testParseTime1() {
		assertEquals(0L, SH.parseTime("00:00:00.000"));
	}
	@Test
	public void testParseTime2() {
		assertEquals(0L, SH.parseTime("00:00:00"));
	}
	@Test
	public void testParseTime3() {
		assertEquals(0L, SH.parseTime("00:00"));
	}
	@Test
	public void testParseTime4() {
		assertEquals(1L, SH.parseTime("00:00:00.001"));
	}
	@Test
	public void testParseTime5() {
		assertEquals(61200000L, SH.parseTime("17:00:00"));
	}
	@Test
	public void testParseTime6() {
		assertEquals(1000L, SH.parseTime("00:00:01"));
	}
	@Test
	public void testParseTime7() {
		assertEquals(60000L, SH.parseTime("00:01:00"));
	}
	@Test
	public void testParseTime8() {
		assertEquals(3600000L, SH.parseTime("01:00"));
	}
	@Test
	public void testParseTime9() {
		assertEquals(123 + 14 * 1000 + 25 * 60000 + 12 * 3600000L, SH.parseTime("12:25:14.123"));
	}
	@Test
	public void testParseTime10() {
		assertEquals(59 * 60000 + 17 * 3600000L, SH.parseTime("17:59"));
	}
	@Test
	public void testReplaceAllForAny1() {
		assertEquals("test", SH.replaceAllForAny("test", "!$^".toCharArray(), "**"));
	}
	@Test
	public void testReplaceAllForAny2() {
		assertEquals("test****", SH.replaceAllForAny("test!!", "!$^".toCharArray(), "**"));
	}
	@Test
	public void testReplaceAllForAny3() {
		assertEquals("**test****", SH.replaceAllForAny("$test!!", "!$^".toCharArray(), "**"));
	}
	@Test
	public void testReplaceAllForAny4() {
		assertEquals("**te**st****", SH.replaceAllForAny("$te^st!!", "!$^".toCharArray(), "**"));
	}
	@Test
	public void testCommonPrefix1() {
		assertEquals("t", SH.commonPrefix("test", "this"));
	}
	@Test
	public void testCommonPrefix2() {
		assertEquals("test", SH.commonPrefix("test", "test"));
	}
	@Test
	public void testCommonPrefix3() {
		assertEquals("test", SH.commonPrefix("tester", "test"));
	}
	@Test
	public void testCommonPrefix4() {
		assertEquals("test", SH.commonPrefix("test", "tester"));
	}
	@Test
	public void testCommonPrefix5() {
		assertEquals("teste", SH.commonPrefix("tester", "testen"));
	}
	@Test
	public void testCommonPrefix6() {
		assertEquals("", SH.commonPrefix("diff", "testen"));
	}
	@Test
	public void testCommonPrefix7() {
		assertEquals(null, SH.commonPrefix(null, "test"));
	}
	@Test
	public void testCommonPrefix8() {
		assertEquals(null, SH.commonPrefix("", null));
	}
	@Test
	public void testCommonPrefix9() {
		assertEquals("", SH.commonPrefix("", ""));
	}
	@Test
	public void testCommonPrefix10() {
		assertEquals("wh", SH.commonPrefix(CH.l("what", "when", "where")));
	}
	@Test
	public void testCommonSuffix1() {
		assertEquals("hen", SH.commonSuffix("when", "then"));
	}
	@Test
	public void testCommonSuffix2() {
		assertEquals("test", SH.commonSuffix("test", "test"));
	}
	@Test
	public void testCommonSuffix3() {
		assertEquals("when", SH.commonSuffix("rewhen", "when"));
	}
	@Test
	public void testCommonSuffix4() {
		assertEquals("when", SH.commonSuffix("when", "rewhen"));
	}
	@Test
	public void testCommonSuffix5() {
		assertEquals("etest", SH.commonSuffix("retest", "netest"));
	}
	@Test
	public void testCommonSuffix6() {
		assertEquals("etest", SH.commonSuffix("retest", "enetest"));
	}
	@Test
	public void testCommonSuffix7() {
		assertEquals("etest", SH.commonSuffix("eretest", "netest"));
	}
	@Test
	public void testCommonSuffix8() {
		assertEquals("", SH.commonSuffix("diff", "testen"));
	}
	@Test
	public void testCommonSuffix9() {
		assertEquals(null, SH.commonSuffix(null, "test"));
	}
	@Test
	public void testCommonSuffix10() {
		assertEquals(null, SH.commonSuffix("", null));
	}
	@Test
	public void testCommonSuffix11() {
		assertEquals("", SH.commonSuffix("", ""));
	}
	@Test
	public void testCommonSuffix12() {
		assertEquals("en", SH.commonSuffix(CH.l("when", "then", "seen")));
	}

	@Test
	public void testSplitLongLines1() {
		System.out.println(SH.join("|\n", SH.splitLongLines(new String[] { "THIS IS A TEST OF SOMETHING LONG" }, 5, false)) + "|\n");
	}
	@Test
	public void testSplitLongLines2() {
		System.out.println(SH.join("|\n", SH.splitLongLines(new String[] { "THIS IS A TEST OF SOMETHING     LONG" }, 5, true)) + "|\n");
	}

	@Test
	public void testEqualsAt1() {
		assertTrue(SH.equalsAt("this", 0, "this"));
	}
	@Test
	public void testEqualsAt2() {
		assertFalse(SH.equalsAt(" this", 0, "this"));
	}
	@Test
	public void testEqualsAt3() {
		assertTrue(SH.equalsAt(" this", 1, "this"));
	}
	@Test
	public void testEqualsAt4() {
		assertFalse(SH.equalsAt(" this", 2, "this"));
	}

	@Test
	public void testReplaceInline() {
		testReplaceInline("testme", "blah");
		testReplaceInline("testme", "blahasdfasdf");
		testReplaceInline("", "");
		testReplaceInline("a", "");
		testReplaceInline("", "a");
		testReplaceInline("a", "a");
	}
	public void testReplaceInline(String target, CharSequence source) {
		for (int targetStartChar = 0; targetStartChar < target.length(); targetStartChar++)
			for (int targetEndChar = targetStartChar; targetEndChar < target.length(); targetEndChar++)
				for (int sourceStartChar = 0; sourceStartChar < source.length(); sourceStartChar++)
					for (int sourceEndChar = sourceStartChar; sourceEndChar < source.length(); sourceEndChar++)
						testReplaceInline(target, targetStartChar, targetEndChar, source, sourceStartChar, sourceEndChar);
	}
	public void testReplaceInline(String target, int targetStartChar, int targetEndChar, CharSequence source, int sourceStartChar, int sourceEndChar) {
		String ex = new StringBuilder(target).replace(targetStartChar, targetEndChar, source.subSequence(sourceStartChar, sourceEndChar).toString()).toString();
		String ac = SH.replaceInline(new StringBuilder(target), targetStartChar, targetEndChar, source, sourceStartChar, sourceEndChar).toString();
		assertEquals(ex, ac);

	}

	@Test
	public void testCut() {
		assertEquals("012", SH.cut("0123456789", "", 0, 0, 1, 1, 2, 2));
		assertEquals("012789", SH.cut("0123456789", "", 0, 2, 7, 80));
		assertEquals("", SH.cut(" 0 1 2 3 4 5 6 7 8 9", " ", 0, 0));
		assertEquals("this", SH.cut("this is rob", " ", 0, 0));
		assertEquals("is", SH.cut("this is rob", " ", 1, 1));
		assertEquals("rob", SH.cut("this is rob", " ", 2, 2));
		assertEquals("this is rob", SH.cut("this is rob", " ", 0, 2));
		assertEquals("0 1 2", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 0, 2));
		assertEquals("0 1 2 7 8", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 0, 2, 7, 8));
		assertEquals("0 1 2 7 8 9", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 0, 2, 7, 1000));
		assertEquals("", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 1000, 1010));
		assertEquals("0 1 2", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 0, 0, 1, 1, 2, 2));
		assertEquals(" 0 1", SH.cut(" 0 1 2 3 4 5 6 7 8 9", " ", 0, 0, 1, 1, 2, 2));
		assertEquals("  0  1", SH.cut("  0  1  2  3  4  5  6   7   8   9", "  ", 0, 0, 1, 1, 2, 2));
		assertEquals("2 1 0", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 2, 0));
		assertEquals("2 1 0 3 2 1 0", SH.cut("0 1 2 3 4 5 6 7 8 9", " ", 2, 0, 3, 0));
	}

	@Test
	public void testEscape() {
		assertArrayEquals(new String[] { "test" }, SH.splitWithEscape('|', '\\', "test"));
		assertArrayEquals(new String[] { "test", "me" }, SH.splitWithEscape('|', '\\', "test|me"));
		assertArrayEquals(new String[] { "test|me" }, SH.splitWithEscape('|', '\\', "test\\|me"));
		assertArrayEquals(new String[] { "test|me" }, SH.splitWithEscape('|', '\\', "te\\st\\|me"));
	}
	@Test
	public void testIndexOfAll() {
		assertArrayEquals(new int[] { 0, 1, 2, 3 }, SH.indexOfAll("1111", '1'));
		assertArrayEquals(new int[] {}, SH.indexOfAll("", '1'));
		assertArrayEquals(new int[] { 2 }, SH.indexOfAll("00100", '1'));
		assertArrayEquals(new int[] { 2, 4, 6 }, SH.indexOfAll("0010101", '1'));
		assertArrayEquals(new int[] { 0, 2, 4, 6 }, SH.indexOfAll("1010101", '1'));
		assertArrayEquals(new int[] { 0, 1, 2, 4, 6 }, SH.indexOfAll("1110101", '1'));
		assertArrayEquals(new int[] { 0 }, SH.indexOfAll("1", '1'));
	}

	@Test
	public void testFormatNumberCompact() {
		assertEquals(".1", SH.formatNumberCompact(.1));
		assertEquals(".01", SH.formatNumberCompact(.01));
		assertEquals(".001", SH.formatNumberCompact(.001));
		assertEquals("1E-3", SH.formatNumberCompact(.0001));
		assertEquals("1E-4", SH.formatNumberCompact(.00001));
		assertEquals("1E-5", SH.formatNumberCompact(.000001));
		assertEquals("1E-6", SH.formatNumberCompact(.0000001));
		assertEquals("1E-7", SH.formatNumberCompact(.00000001));
		assertEquals("1E-8", SH.formatNumberCompact(.000000001));
		assertEquals("9E-8", SH.formatNumberCompact(.000000009));
		assertEquals("1E-9", SH.formatNumberCompact(.0000000001));
		assertEquals("0", SH.formatNumberCompact(.00000000001));
		assertEquals("0", SH.formatNumberCompact(.000000000001));
		assertEquals("1", SH.formatNumberCompact(1));
		assertEquals("12", SH.formatNumberCompact(12));
		assertEquals("123", SH.formatNumberCompact(123));
		assertEquals("1.2k", SH.formatNumberCompact(1234));
		assertEquals("12k", SH.formatNumberCompact(12345));
		assertEquals("123k", SH.formatNumberCompact(123456));
		assertEquals("1.2m", SH.formatNumberCompact(1234567));
		assertEquals("12m", SH.formatNumberCompact(12345678));
		assertEquals("123m", SH.formatNumberCompact(123456789));
		assertEquals("1.2b", SH.formatNumberCompact(1234567890));
		assertEquals("12t", SH.formatNumberCompact(12345678901234L));
		assertEquals("-1", SH.formatNumberCompact(-1));
		assertEquals("-12", SH.formatNumberCompact(-12));
		assertEquals("-123", SH.formatNumberCompact(-123));
		assertEquals("-1.2k", SH.formatNumberCompact(-1234));
		assertEquals("-12k", SH.formatNumberCompact(-12345));
		assertEquals("-123k", SH.formatNumberCompact(-123456));
		assertEquals("-1.2m", SH.formatNumberCompact(-1234567));
		assertEquals("-12m", SH.formatNumberCompact(-12345678));
		assertEquals("-123m", SH.formatNumberCompact(-123456789));
		assertEquals("-1.2b", SH.formatNumberCompact(-1234567890));
		assertEquals("-12t", SH.formatNumberCompact(-12345678901234L));
		assertEquals("-123t", SH.formatNumberCompact(-123456789012345L));
		assertEquals("-1234t", SH.formatNumberCompact(-1234567890123456L));
		assertEquals("1k", SH.formatNumberCompact(1034));
		assertEquals("1m", SH.formatNumberCompact(1034567));
	}
}
