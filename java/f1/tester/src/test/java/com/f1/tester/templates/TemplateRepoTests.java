/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.tester.templates;

import static org.junit.Assert.assertEquals;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import org.junit.Test;
import com.f1.tester.TestH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.SH;
import com.f1.utils.assist.RootAssister;

public class TemplateRepoTests {
	@Test
	public void test1() {
		TemplateRepo tr = new TemplateRepo();
		tr.setMapAlphabetical(true);
		tr.put("test", "this");
		assertEquals("this", tr.get("test"));
		tr.put("m1.this.now", "what");
		tr.put("m1.this.later", "what2");
		System.out.println(tr.get(""));
		assertEquals("what", tr.get("m1.this.now"));
		assertEquals("{later=what2, now=what}", tr.get("m1.this").toString());
		tr.put("m1.this.yep", CH.m(new TreeMap(), "1", "one", "2", "two"));
		assertEquals("{later=what2, now=what, yep={1=one, 2=two}}", tr.get("m1.this").toString());
		tr.put("m1.this.yep.2", "too", true);
		assertEquals("{later=what2, now=what, yep={1=one, 2=too}}", tr.get("m1.this").toString());
		tr.put("m1.this.yep.3", "three");
		assertEquals("{later=what2, now=what, yep={1=one, 2=too, 3=three}}", tr.get("m1.this").toString());
		assertEquals("three", tr.get("m1.this.yep.3").toString());

		tr.mergeMap("m1.this.yep", CH.m(new TreeMap(), "4", "four", "1", "uno"), false, true);
		assertEquals("{later=what2, now=what, yep={1=uno, 2=too, 3=three, 4=four}}", tr.get("m1.this").toString());
		System.out.println(RootAssister.INSTANCE.toLegibleString(tr.get("")));
		tr.mergeMap("m1", CH.m(new TreeMap(), "x", "end", "this", CH.m("x2", "end2")), true, true);
		System.out.println(RootAssister.INSTANCE.toLegibleString(tr.get("")));
	}

	@Test
	public void test2() throws IOException {
		TemplateRepo tr = new TemplateRepo();
		IOH.writeText(new File(System.getProperty("java.io.tmpdir"), "f1.txt"), "{'1':'one','2':'2'}".replaceAll("'", "\""));
		IOH.writeText(new File(System.getProperty("java.io.tmpdir"), "f2.txt"), "{'3':'three','4':'four'}".replaceAll("'", "\""));
		IOH.writeText(new File(System.getProperty("java.io.tmpdir"), "f3.txt"), "{'5':'five','6':'six'}".replaceAll("'", "\""));
		tr.putFile("f1", new File(System.getProperty("java.io.tmpdir"), "f1.txt"));
		tr.putFile("f2", new File(System.getProperty("java.io.tmpdir"), "f2.txt"));
		tr.putFile("f3", new File(System.getProperty("java.io.tmpdir"), "f3.txt"));
		System.out.println(RootAssister.INSTANCE.toLegibleString(tr.get("")));
	}

	@Test
	public void test3() throws IOException {
		TemplateRepo tr = new TemplateRepo();
		IOH.writeText(new File(System.getProperty("java.io.tmpdir"), "f1.txt"), "{'1':'one','2':'2','3': ;a++; }".replaceAll("'", "\""));
		tr.putFile("f1", new File(System.getProperty("java.io.tmpdir"), "f1.txt"));
		TemplateSession session = new TemplateSession(tr);
		session.putVariable("a", 5);
		assertEquals(5, session.get("f1.3"));
		assertEquals(6, session.get("f1.3"));
		assertEquals(7, session.get("f1.3"));
		System.out.println(tr.toJson());
	}

	@Test
	public void test4() throws IOException {
		TemplateRepo tr = new TemplateRepo();
		tr.putJson("login", "{'fname':'Robert','lname':'Cooke','Age':'33'}");
		tr.putJson("basket", "[]");
		tr.putJson("basket.0", "{'id':'123','qty':'100','ticker':'MSFT'}");
		tr.putJson("basket.1", "{'id':'124','qty':'200','ticker':'IBM' }");
		tr.putJson("basket.2", "{'id':'125','qty':'300','ticker':'F'   }");
		System.out.println(tr.toJson());
		System.out.println(tr.get("basket.1.ticker"));
		System.out.println(tr.get("basket.1"));
	}

	@Test
	public void test5() throws IOException {
		TemplateRepo tr = new TemplateRepo();
		tr.putJson("login", "{'fname':'Robert','lname':'Cooke','Age':'33'}");
		tr.putJson("basket", "[]");
		tr.putJson("basket.0", "{'id':;++i;,'qty':;Math.random();,'ticker':'MSFT'}");
		tr.putJson("basket.1", "{'id':;i++;,'qty':;Math.random();,'ticker':'IBM' }");
		tr.putJson("basket.2", "{'id':;\"orderId-\"+ (++i);,'qty':;Math.random();,'ticker':'F'   }");
		tr.putVariableInitialValue("i", 1000);
		tr.addImport("java.lang.Math");

		TemplateSession session = new TemplateSession(tr);
		System.out.println(session.get("basket.0"));
		System.out.println(session.get("basket.0"));
		System.out.println(session.get("basket.0"));
		System.out.println(session.get("basket"));
		System.out.println(session.get("basket"));
		System.out.println(session.get("basket"));

		System.out.println(session.get("basket.1"));
		System.out.println(session.get("basket.2"));
		System.out.println(session.get("basket.2"));
		System.out.println();
		System.out.println();
		System.out.println(session.get("basket"));
		System.out.println(session.get("basket"));
		System.out.println(session.get("basket"));
	}

	@Test
	public void test6() {
		TemplateRepo tr = new TemplateRepo();
		tr.putJson("t", "[{'xray':;j++;,'apple':;j++;},;j++;,;j++;]");
		tr.putVariableInitialValue("j", 000);
		TemplateSession session = new TemplateSession(tr);
		System.out.println(tr.toJson());
		System.out.println(session.get("t"));
	}

	@Test
	public void test7() {
		TemplateRepo tr = new TemplateRepo();
		tr.setMapAlphabetical(false);
		tr.putJson("t", ";TestH.cycle(i++, \"MSFT\",\"IBM\",\"F\");");
		tr.putVariableInitialValue("i", 0);
		TemplateSession session = new TemplateSession(tr);
		System.out.println(tr.toJson());
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
		System.out.println(session.get("t"));
	}

	private String r(String s) {
		return SH.replaceAll(s, '\'', '\"');
	}

	@Test
	public void testCycle() {
		assertEquals(0, (int) TestH.cycle(-6, 0, 1, 2));
		assertEquals(1, (int) TestH.cycle(-5, 0, 1, 2));
		assertEquals(2, (int) TestH.cycle(-4, 0, 1, 2));
		assertEquals(0, (int) TestH.cycle(-3, 0, 1, 2));
		assertEquals(1, (int) TestH.cycle(-2, 0, 1, 2));
		assertEquals(2, (int) TestH.cycle(-1, 0, 1, 2));
		assertEquals(0, (int) TestH.cycle(0, 0, 1, 2));
		assertEquals(1, (int) TestH.cycle(1, 0, 1, 2));
		assertEquals(2, (int) TestH.cycle(2, 0, 1, 2));
		assertEquals(0, (int) TestH.cycle(3, 0, 1, 2));
		assertEquals(1, (int) TestH.cycle(4, 0, 1, 2));
	}
}
