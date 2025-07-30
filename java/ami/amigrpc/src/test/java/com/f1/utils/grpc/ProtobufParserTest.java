package com.f1.utils.grpc;

import org.junit.Test;

import junit.framework.Assert;

public class ProtobufParserTest {

	@Test(expected = RuntimeException.class)
	public void TestParse1() {
		new ProtobufParser("", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse2() {
		new ProtobufParser("function(asd", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse3() {
		new ProtobufParser("function(asd)", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse4() {
		new ProtobufParser("(123)", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse5() {
		new ProtobufParser("fn(123,)", "");
	}

	@Test()
	public void TestParse6() {
		final ProtobufParser p = new ProtobufParser("fn(123,234)", "");
		Assert.assertEquals(p.getInstructionSize(), 5);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), 123);
		Assert.assertEquals(p.pop(), 234);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test(expected = RuntimeException.class)
	public void TestParse7() {
		new ProtobufParser("fn(123,new abc)", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse8() {
		new ProtobufParser("fn(123,new abc(1)", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse9() {
		new ProtobufParser("fn(123,new abc(1,))", "");
	}

	@Test
	public void TestParse10() {
		final ProtobufParser p = new ProtobufParser("fn(123,new  abc(1,\"abcd\"))", "");
		Assert.assertEquals(p.getInstructionSize(), 9);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), 123);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "abc");
		Assert.assertEquals(p.pop(), 1);
		Assert.assertEquals(p.pop(), "abcd");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test
	public void TestParse11() {
		final ProtobufParser p = new ProtobufParser("fn(\"new 123\",new  abc(\"1\",\"abcd\"))", "");
		Assert.assertEquals(p.getInstructionSize(), 9);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), "new 123");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "abc");
		Assert.assertEquals(p.pop(), "1");
		Assert.assertEquals(p.pop(), "abcd");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test(expected = RuntimeException.class)
	public void TestParse12() {
		new ProtobufParser("fn(\"new 123\",new  abc(\"1\",\"abcd\"1))", "");
	}

	@Test(expected = RuntimeException.class)
	public void TestParse13() {
		new ProtobufParser("fn(\"new 123\",new  abc(\"1,\"abcd\"1))", "");
	}

	@Test
	public void TestParse14() {
		final ProtobufParser p = new ProtobufParser("fn(true,new  abc(1,\"abcd\"), fALSE)", "");
		Assert.assertEquals(p.getInstructionSize(), 10);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), true);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "abc");
		Assert.assertEquals(p.pop(), 1);
		Assert.assertEquals(p.pop(), "abcd");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), false);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test
	public void TestParse15() {
		final ProtobufParser p = new ProtobufParser("fn   (   new apple(true,0.1  , 2L,   3.0f) ,new  abc2  (1,  \"abcd\"), fALSE)", "");
		Assert.assertEquals(p.getInstructionSize(), 16);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "apple");
		Assert.assertEquals(p.pop(), true);
		Assert.assertEquals(p.pop(), 0.1);
		Assert.assertEquals(p.pop(), 2L);
		Assert.assertEquals(p.pop(), 3.0f);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "abc2");
		Assert.assertEquals(p.pop(), 1);
		Assert.assertEquals(p.pop(), "abcd");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), false);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test()
	public void TestParse16() {
		final ProtobufParser p = new ProtobufParser("fn(new  abc(1))", "com.example.group");
		Assert.assertEquals(p.getInstructionSize(), 7);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "com.example.group.abc");
		Assert.assertEquals(p.pop(), 1);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}

	@Test()
	public void TestParse17() {
		final ProtobufParser p = new ProtobufParser("fn(new a(new b(1), new c(true)))", "");
		Assert.assertEquals(p.getInstructionSize(), 14);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD);
		Assert.assertEquals(p.pop(), "fn");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "a");
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "b");
		Assert.assertEquals(p.pop(), 1);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT);
		Assert.assertEquals(p.pop(), "c");
		Assert.assertEquals(p.pop(), true);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CONSTRUCT_OBJECT_END);
		Assert.assertEquals(p.pop(), ProtobufParser.INSTRUCTION_CALL_METHOD_END);
		Assert.assertEquals(p.pop(), null);
	}
}
