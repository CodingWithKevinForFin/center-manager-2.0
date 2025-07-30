package com.f1.codegen;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.ValuedListenable;
import com.f1.base.ValuedParam;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.utils.AH;
import com.f1.utils.CH;

public class PartialMessageTests {

	private BasicCodeGenerator g;

	public PartialMessageTests() throws IOException {
		g = new BasicCodeGenerator(new BasicCodeCompiler("./.autocoder"), true);

	}
	//	@Test
	public void test1() throws IOException {
		TestMessage msg = g.nw(TestMessage.class);
		test(msg);
	}

	private void test(TestMessage msg) {
		String[] params = msg.askSchema().askParams();
		assertArrayEquals(AH.sort(AH.a("boolean", "byte", "int", "short", "long", "double", "float", "char", "object", "abstract", "continue")), params);
		assertEquals(false, msg.getBoolean());
		assertEquals((byte) 0, msg.getByte());
		assertEquals((short) 0, msg.getShort());
		assertEquals(0, msg.getInt());
		assertEquals(0L, msg.getLong());
		assertEquals((char) 0, msg.getChar());
		assertEquals(0f, msg.getFloat(), .0000001);
		assertEquals(0d, msg.getDouble(), .0000001);
		assertEquals(null, msg.getObject());
		assertEquals(false, msg.ask("boolean"));
		assertEquals((byte) 0, msg.ask("byte"));
		assertEquals((short) 0, msg.ask("short"));
		assertEquals(0, msg.ask("int"));
		assertEquals(0L, msg.ask("long"));
		assertEquals((char) 0, msg.ask("char"));
		assertEquals(0f, (Float) msg.ask("float"), .0000001);
		assertEquals(0d, (Double) msg.ask("double"), .0000001);
		assertEquals(null, (Object) msg.ask("object"));
		assertEquals(null, msg.getObject());
		msg.put("boolean", true);
		msg.put("byte", (byte) 1);
		msg.put("short", (short) 2);
		msg.put("int", 3);
		msg.put("long", 4l);
		msg.put("char", '5');
		msg.put("float", (float) 6.0);
		msg.put("double", 7.0d);
		msg.put("object", "8");
		assertEquals(true, msg.getBoolean());
		assertEquals((byte) 1, msg.getByte());
		assertEquals((short) 2, msg.getShort());
		assertEquals(3, msg.getInt());
		assertEquals(4L, msg.getLong());
		assertEquals((char) '5', msg.getChar());
		assertEquals(6f, msg.getFloat(), .0000001);
		assertEquals(7d, msg.getDouble(), .0000001);
		assertEquals("8", msg.getObject());
		assertEquals(true, msg.ask("boolean"));
		assertEquals((byte) 1, msg.ask("byte"));
		assertEquals((short) 2, msg.ask("short"));
		assertEquals(3, msg.ask("int"));
		assertEquals(4L, msg.ask("long"));
		assertEquals((char) '5', msg.ask("char"));
		assertEquals(6f, (Float) msg.ask("float"), .0000001);
		assertEquals(7d, (Double) msg.ask("double"), .0000001);
		assertEquals("8", (Object) msg.ask("object"));
		msg.askSchema().askValuedParam("boolean").getValue(msg).equals(true);
		msg.askSchema().askValuedParam("byte").getValue(msg).equals(1);
		msg.askSchema().askValuedParam("short").getValue(msg).equals((byte) 2);
		msg.askSchema().askValuedParam("int").getValue(msg).equals((short) 3);
		msg.askSchema().askValuedParam("long").getValue(msg).equals(4l);
		msg.askSchema().askValuedParam("char").getValue(msg).equals('5');
		msg.askSchema().askValuedParam("float").getValue(msg).equals(6f);
		msg.askSchema().askValuedParam("double").getValue(msg).equals(7d);
		msg.askSchema().askValuedParam("object").getValue(msg).equals("8");
		TestMessage msg2 = msg.clone();
		for (ValuedParam p : msg.askSchema().askValuedParams())
			assertEquals(p.getValue(msg), p.getValue(msg2));
	}

	//	@Test
	public void test2() {
		PartialTestMessage msg = g.nw(PartialTestMessage.class);
		test(msg);
		test2(msg);
	}

	private void test2(PartialTestMessage msg) {
		msg = g.nw(PartialTestMessage.class);
		assertEquals(CH.l(), names(msg.askExistingValuedParams()));
		msg.setBoolean(true);
		msg.setShort((short) 32);
		msg.setObject("rob");
		assertEquals(CH.l("boolean", "object", "short"), names(msg.askExistingValuedParams()));
		assertEquals(CH.l((byte) 1, (byte) 11, (byte) 127), CH.l(msg.askExistingPids()));
		assertEquals(true, msg.getBoolean());
		assertEquals((short) 32, msg.getShort());
		assertEquals("rob", msg.getObject());
		msg.clear();

		assertEquals(CH.l(), CH.l(msg.askExistingValuedParams()));
		assertEquals(false, msg.getBoolean());
		assertEquals((short) 0, msg.getShort());
		assertEquals(null, msg.getObject());
	}

	//	@Test
	public void test3() {
		PartialValuedListenableTestMessage msg = g.nw(PartialValuedListenableTestMessage.class);
		test(msg);
		test2(msg);
	}

	private List<String> names(Iterable<ValuedParam> askExistingValuedParams) {
		ArrayList<String> r = new ArrayList<String>();
		for (ValuedParam vp : askExistingValuedParams)
			r.add(vp.getName());
		return r;
	}

	public static interface TestMessage extends Message {

		TestMessage clone();

		@PID(1)
		boolean getBoolean();
		void setBoolean(boolean bool);

		@PID(-10)
		byte getByte();
		void setByte(byte b);

		@PID(127)
		short getShort();
		void setShort(short s);

		@PID(-128)
		int getInt();
		void setInt(int i);

		@PID(3)
		long getLong();
		void setLong(long o);

		@PID(7)
		double getDouble();
		void setDouble(double d);

		@PID(2)
		float getFloat();
		void setFloat(float f);

		@PID(10)
		char getChar();
		void setChar(char c);

		@PID(11)
		Object getObject();
		void setObject(Object o);

		@PID(13)
		Object getAbstract();
		void setAbstract(Object o);

		@PID(14)
		void setContinue(Object o);
		Object getContinue();
	}

	public interface PartialTestMessage extends TestMessage, PartialMessage {

	}

	public interface PartialValuedListenableTestMessage extends PartialTestMessage, ValuedListenable {

	}
}
