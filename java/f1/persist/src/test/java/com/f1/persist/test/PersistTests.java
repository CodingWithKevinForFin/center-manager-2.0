package com.f1.persist.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.ObjectGenerator;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.persist.impl.BasicPersistValuedListener;
import com.f1.persist.reader.TransactionalPersistReader;
import com.f1.persist.sinks.InputStreamPersistSink;
import com.f1.persist.structs.PersistableArrayList;
import com.f1.persist.structs.PersistableHashMap;
import com.f1.persist.structs.PersistableHashSet;
import com.f1.persist.structs.PersistableSet;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.BasicIdeableGenerator;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;

public class PersistTests {

	public static void main(String a[]) throws IOException {
		try {
			new PersistTests().test();
		} catch (Exception e) {
			System.err.println(SH.printStackTrace(e));
		}
	}

	private BasicIdeableGenerator generator;
	private TransactionalPersistWriterFactory factory;
	private TransactionalPersistReader reader;
	private BasicPersistValuedListener listener;
	private DataInputStream input;

	public void test() throws Exception {

		final CodeCompiler compiler = new BasicCodeCompiler(".coder");
		final ObjectGenerator inner = new BasicCodeGenerator(compiler, true);
		this.generator = new BasicIdeableGenerator(inner);
		factory = new TransactionalPersistWriterFactory(generator);
		generator.register(TestObject.class);
		listener = factory.createListener();

		TestObject obj1 = newObj(1);

		reader = new TransactionalPersistReader(generator);
		InputStreamPersistSink sink = new InputStreamPersistSink(100000);
		factory.addSink(sink, false, true);
		input = new DataInputStream(sink);
		listener.onValuedAdded(obj1);

		obj1.setLeft(newObj(2));
		obj1.setRight(newObj(3));
		TestObject obj2 = newObj(123);
		obj1.getLeft().setRight(obj2);
		obj1.getRight().setRight(obj2);
		obj1.getRight().setLeft(obj2);
		obj1.getRight().setLeft(newObj(5));
		obj1.getRight().setRight(newObj(6));
		obj1.getLeft().setRight(newObj(7));
		obj1.setList(CH.l((List) new PersistableArrayList<String>(), "rob", "dave", "steve"));

		Map<Object, Object> map = new PersistableHashMap();
		map.put("rob", 33);
		obj1.setMap(map);

		testEquals();

		map.put("here", 40);

		obj1.setIntValue(123323);
		obj1.setDoubleValue(3232);
		obj1.setDoubleValue(112);
		testEquals();

		map.remove("here");
		obj1.getList().set(1, "eric");
		obj1.getList().add("jason");
		obj1.getList().addAll(CH.l("erin", "josh"));

		obj1.getLeft().setIntValue(421);
		obj1.getLeft().setIntValue(321);
		obj1.getLeft().setIntValue(321);
		testEquals();

		obj1.getList().remove(2);

		map.clear();
		obj1.getRight().setLeft(obj1.getRight().getRight());
		testEquals();
		obj1.getList().clear();
		obj1.getRight().getLeft().setLeft(newObj(2332));
		testEquals();
		obj1.setRight(null);
		testEquals();

		List<Map> l2 = new PersistableArrayList<Map>();
		PersistableHashMap<String, TestObject> m1 = new PersistableHashMap<String, TestObject>();
		m1.put("test", newObj(12332));
		m1.put("test2", newObj(12332));
		l2.add(m1);
		obj1.setList(l2);

		testEquals();
		m1.put("test3", newObj(3332));

		m1.put("test", newObj(-323));
		testEquals();
		m1.remove("test");

		testEquals();

		PersistableSet<String> set = new PersistableHashSet<String>();
		set.add("test");
		set.add("test1");
		set.add("test2");
		set.add("test3");
		obj1.setSet(set);
		testEquals();

		set.remove("test");

		testEquals();
		set.add("test5");
		testEquals();

	}

	public void testEquals() throws Exception {
		listener.commitTransaction();
		while (input.available() > 0) {
			reader.consumeTransaction(input);
			System.out.println("proceesed " + reader.pumpTransaction() + " event(s)");
		}
		Map<Long, Object> serverObjects = getObjects(factory);
		Map<Long, Object> clientObjects = getObjects(reader);
		System.out.println("Server / Client Object counts:" + serverObjects.size() + " / " + clientObjects.size());

		ObjectToJsonConverter convert = new ObjectToJsonConverter();

		// test the snapshot
		InputStreamPersistSink sink = new InputStreamPersistSink(100000);
		factory.addSink(sink, false, true);
		DataInputStream input2 = new DataInputStream(sink);
		TransactionalPersistReader reader2 = new TransactionalPersistReader(generator);
		reader2.consumeTransaction(input2);
		reader2.pumpTransaction();
		Map<Long, Object> clientObjects2 = getObjects(reader2);
	}

	private static Map<Long, Object> getObjects(TransactionalPersistWriterFactory factory) {
		final Map<Object, Long> r = new HashMap<Object, Long>();
		factory.getObjects(r);
		return CH.swapKeyValue(r.entrySet());
	}

	private static Map<Long, Object> getObjects(TransactionalPersistReader reader) {
		Map<Long, Object> r = new HashMap<Long, Object>();
		reader.getStore().getObjects(r);
		return r;
	}

	private TestObject newObj(long seed) {
		return populate(generator.nw(TestObject.class), seed);
	}

	private TestObject populate(TestObject obj1, long seed) {
		obj1.setBooleanValue(seed % 2 == 0);
		obj1.setByteValue((byte) ++seed);
		obj1.setShortValue((byte) ++seed);
		obj1.setIntValue((int) ++seed);
		obj1.setLongValue(++seed);
		obj1.setFloatValue((float) ++seed);
		obj1.setDoubleValue((double) ++seed);
		obj1.setStringValue(Long.toString(++seed));
		return obj1;
	}
}
