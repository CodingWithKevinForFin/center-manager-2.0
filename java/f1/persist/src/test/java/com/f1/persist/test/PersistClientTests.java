package com.f1.persist.test;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;

import org.junit.Test;

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
import com.f1.persist.sinks.PersistClientSocket;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.AbstractValuedListener;
import com.f1.utils.BasicIdeableGenerator;

public class PersistClientTests extends AbstractValuedListener implements PersistStoreListener, ValuedListener {
	public TransactionalPersistReader reader;
	private DataInputStream in;
	private int count;

	public static void main(String a[]) throws IOException {
		new PersistClientTests().atest1(a[0]);
	}

	@Test
	public void nullTest() {

	}

	public void atest1(String host) throws IOException {
		final CodeCompiler compiler = new BasicCodeCompiler(".coder");
		final ObjectGenerator inner = new BasicCodeGenerator(compiler, true);
		final IdeableGenerator generator = new BasicIdeableGenerator(inner);

		generator.register(TestOrder.class, TestInstrument.class);

		TransactionalPersistWriterFactory factory = new TransactionalPersistWriterFactory(generator);
		BasicPersistValuedListener listener = factory.createListener();
		TestOrder ord = generator.nw(TestOrder.class);

		TransactionalPersistReader persist = new TransactionalPersistReader(generator);
		persist.addListener(this);
		new PersistClientSocket(host, 12345, persist, true);
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

	private static void log(String string) {
		System.out.println("Client " + new Date() + ":" + string);
	}

	@Override
	public void onValuedAdded(ValuedListenable target) {
		if (target instanceof ValuedListenable)
			((ValuedListenable) target).addListener(this);

	}

	@Override
	public void onValuedRemoved(ValuedListenable target) {
	}

	@Override
	public void onValued(ValuedListenable target, String name, byte pid, Object old, Object value) {
		count++;
		if (count % 1000000 == 0)
			log("" + count);
	}
}
