package com.f1.persist.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.Date;

import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGenerator;
import com.f1.base.ValuedListenable;
import com.f1.base.ValuedListener;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.persist.PersistStoreListener;
import com.f1.persist.impl.BasicPersistValuedListener;
import com.f1.persist.reader.TransactionalPersistReader;
import com.f1.persist.sinks.PersistServerSocket;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.AbstractValuedListener;
import com.f1.utils.BasicIdeableGenerator;

public class PersistServerTests extends AbstractValuedListener implements PersistStoreListener, Runnable, ValuedListener {
	public TransactionalPersistReader reader;
	private DataInputStream in;

	public static void main(String a[]) throws IOException {
		new PersistServerTests().test1();
	}

	public void test1() throws IOException {
		final CodeCompiler compiler = new BasicCodeCompiler(".coder");
		final ObjectGenerator inner = new BasicCodeGenerator(compiler, true);
		final IdeableGenerator generator = new BasicIdeableGenerator(inner);

		generator.register(TestOrder.class, TestInstrument.class);

		TransactionalPersistWriterFactory factory = new TransactionalPersistWriterFactory(generator);
		BasicPersistValuedListener listener = factory.createListener();
		TestOrder ord = generator.nw(TestOrder.class);
		TestInstrument instrument = generator.nw(TestInstrument.class);
		listener.onValuedAdded(ord);
		instrument.setExchange("LSE");
		instrument.setName("IBM");
		ord.setQuantity(100);
		ord.setTicker("test");
		ord.setTicker("test this");
		instrument.setExchange("NASD");
		PipedInputStream inputPipe = new PipedInputStream();
		PersistServerSocket pss = new PersistServerSocket(12345, false, factory);
		pss.start();
		listener.commitTransaction();
		this.in = new DataInputStream(inputPipe);
		reader = new TransactionalPersistReader(generator);
		reader.addListener(this);
		new Thread(this).start();

		for (int i = 0; i < 1000 * 1000 * 1000; i++) {
			ord.setQuantity(i);
			ord.setFilledQuantity(i * 10);
			ord.setPrice(i * 10d);
			ord.setIsOpen(i % 2 == 0);
			ord.setAvgPrice(i);
			ord.setState((char) i);
			listener.commitTransaction();
			if (i % 1000000 == 0)
				log("sent:" + i);
		}
	}

	@Override
	public void onObjectAdded(Long id, Object object) {
		if (object instanceof ValuedListenable)
			((ValuedListenable) object).addListener(this);
	}

	@Override
	public void onObjectRemoved(Long id, Object object) {
		if (object instanceof ValuedListenable)
			((ValuedListenable) object).removeListener(this);

	}

	@Override
	public void run() {
		int count = 0;
		while (true) {
			reader.consumeTransaction(in);
			try {
				count += reader.pumpTransaction();
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (count % 10000 == 0)
				log("read:" + Integer.toString(count));
		}
	}

	private static void log(String string) {
		System.out.println(new Date() + ":" + string);
	}

	@Override
	public void onValuedAdded(ValuedListenable target) {
	}

	@Override
	public void onValuedRemoved(ValuedListenable target) {
	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
	}

}
