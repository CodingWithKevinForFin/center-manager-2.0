package com.f1.persist.test;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.f1.base.IdeableGenerator;
import com.f1.base.ObjectGenerator;
import com.f1.codegen.CodeCompiler;
import com.f1.codegen.impl.BasicCodeCompiler;
import com.f1.codegen.impl.BasicCodeGenerator;
import com.f1.persist.impl.BasicPersistValuedListener;
import com.f1.persist.sinks.FilePersister;
import com.f1.persist.writer.TransactionalPersistWriterFactory;
import com.f1.utils.BasicIdeableGenerator;

public class PersistFileTests {

	public static final void main(String a[]) throws Exception {
		final CodeCompiler compiler = new BasicCodeCompiler(".coder");
		final ObjectGenerator inner = new BasicCodeGenerator(compiler, true);
		final IdeableGenerator generator = new BasicIdeableGenerator(inner);
		generator.register(TestOrder.class, TestInstrument.class);
		TransactionalPersistWriterFactory factory = new TransactionalPersistWriterFactory(generator);
		new FilePersister(new File("c:/test/persist"), factory, generator, false);
		BasicPersistValuedListener listener = factory.createListener();
		Date now = new Date();
		System.out.println("now:" + now);

		for (int i = 0; i < 100000; i++) {
			TestOrder order = generator.nw(TestOrder.class);
			order.setTicker(now.toString());
			listener.onValuedAdded(order);
			listener.commitTransaction();
		}
		Map<Object, Long> sink = new HashMap<Object, Long>();
		factory.getObjects(sink);
		System.out.println("sinks: " + sink.size());
		System.exit(0);
	}
}
