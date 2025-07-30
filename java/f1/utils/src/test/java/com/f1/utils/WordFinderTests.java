package com.f1.utils;

import static org.junit.Assert.*;
import org.junit.Test;

public class WordFinderTests {

	@Test
	public void test() {
		System.out.println("started");
		WordFinder wf = new WordFinder();
		wf.addWord("test");
		wf.addWord("what");
		wf.addWord("where");
		wf.addWord("when");
		wf.addWord("tester");
		wf.addWord("however");
		wf.addWord("how");
		assertEquals(0, wf.findWord("test").getLocation());
		assertEquals("test", wf.findWord("test").getWord());
		assertEquals(10, wf.findWord("this is a test").getLocation());
		assertEquals("when", wf.findWord("this is a tes of when things started").getWord());
		assertEquals("how", wf.findWord("this is a est of whein things how").getWord());
	}
}
