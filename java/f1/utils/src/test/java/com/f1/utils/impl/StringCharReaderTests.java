package com.f1.utils.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.f1.utils.CharReader;

public class StringCharReaderTests {

	@Test
	public void testRead() {
		StringCharReader scr = new StringCharReader("test");
		assertEquals('t', scr.readChar());
		assertEquals('e', scr.readChar());
		assertEquals('s', scr.readChar());
		assertEquals('t', scr.readChar());

	}

	@Test
	public void testPush() {
		StringCharReader scr = new StringCharReader("test");
		assertEquals('t', scr.readChar());
		assertEquals('e', scr.readChar());
		assertEquals('s', scr.readChar());
		assertEquals('t', scr.readChar());
		scr.pushBack('t');
		scr.pushBack('s');
		scr.pushBack('e');
		scr.pushBack('t');
		assertEquals('t', scr.readChar());
		assertEquals('e', scr.readChar());
		assertEquals('s', scr.readChar());
		assertEquals('t', scr.readChar());
	}

	@Test
	public void testReadUntil() {
		StringCharReader scr = new StringCharReader("what now");
		StringBuilder sb = new StringBuilder();
		assertEquals(4, scr.readUntil(' ', '/', sb));
		assertEquals("what", sb.toString());
		assertEquals(' ', scr.readChar());
	}

	@Test
	public void testReadUntil2() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(9, scr.readUntil(' ', '/', sb));
		assertEquals("what/ now", sb.toString());
		sb.setLength(0);
		assertEquals(0, scr.readUntil(' ', '/', sb));
		assertEquals("", sb.toString());
		assertEquals(' ', scr.readChar());
	}

	@Test
	public void testReadUntil3() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(9, scr.readUntil(' ', '/', sb));
		assertEquals("what/ now", sb.toString());
		scr.expect(' ');
		sb.setLength(0);
		assertEquals(5, scr.readUntil(CharReader.EOF, '/', sb));
		assertEquals("folks", sb.toString());
	}
	@Test
	public void testReadUntilSeq() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(0, scr.readUntilSequence("what".toCharArray(), sb));
	}
	@Test
	public void testReadUntilSeq2() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(6, scr.readUntilSequence("now".toCharArray(), sb));
		assertEquals("what/ ", sb.toString());
	}
	@Test
	public void testReadUntilSeq3() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(10, scr.readUntilSequence("folks".toCharArray(), sb));
		assertEquals("what/ now ", sb.toString());
	}
	@Test
	public void testReadUntilSeq4() {
		StringCharReader scr = new StringCharReader("what/ now folks");
		StringBuilder sb = new StringBuilder();
		assertEquals(14, scr.readUntilSequence("s".toCharArray(), sb));
		assertEquals("what/ now folk", sb.toString());
	}
}

