package com.f1.persist.writer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.IdeableGenerator;
import com.f1.base.Transient;
import com.f1.persist.PersistEvent;
import com.f1.persist.PersistSink;
import com.f1.persist.PersistWriteStore;
import com.f1.persist.impl.BasicPersistValuedListener;
import com.f1.persist.impl.converter.PersistToByteArrayConverterSession;
import com.f1.persist.impl.converter.PersistValuedToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableListToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableMapToByteArrayConverter;
import com.f1.persist.impl.converter.PersistableSetToByteArrayConverter;
import com.f1.persist.sinks.BasicPersistSink;
import com.f1.persist.sinks.DeltaBufferingPersistSink;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class TransactionalPersistWriterFactory {
	private final AtomicInteger nextId = new AtomicInteger();
	private final ObjectToByteArrayConverter converter;
	private final BasicPersistSink sink;
	private final ConcurrentMap<Integer, TransactionalPersistWriter> writers = new ConcurrentHashMap<Integer, TransactionalPersistWriter>();

	public TransactionalPersistWriterFactory(IdeableGenerator generator) {
		this.converter = new ObjectToByteArrayConverter();
		this.converter.setSkipTransience(Transient.PERSIST);
		this.converter.setIdeableGenerator(generator);
		this.converter.replaceConverter(new PersistValuedToByteArrayConverter());
		this.converter.registerConverter(new PersistableMapToByteArrayConverter());
		this.converter.registerConverter(new PersistableSetToByteArrayConverter());
		this.converter.registerConverter(new PersistableListToByteArrayConverter());
		this.sink = new BasicPersistSink();
	}

	public BasicPersistValuedListener createListener() {
		int id = nextId.getAndIncrement();
		TransactionalPersistWriter r = new TransactionalPersistWriter(id, converter, sink);
		writers.put(id, r);
		return new BasicPersistValuedListener(r);
	}

	public void getObjects(Map<Object, Long> objectsSink) {
		for (TransactionalPersistWriter writer : writers.values())
			writer.getStore().getObjects(objectsSink);
	}

	public Long getId(Object o) {
		for (TransactionalPersistWriter writer : writers.values()) {
			Long id = writer.getStore().getIdByObject(o);
			if (id != null)
				return id;
		}
		return null;
	}

	public void addSink(PersistSink sink, boolean async, boolean writeSnapshot) {
		final DeltaBufferingPersistSink buffer = new DeltaBufferingPersistSink(sink);
		if (async)
			this.sink.addAsyncSink(buffer);
		else
			this.sink.addSink(buffer);
		if (writeSnapshot) {
			Map<Object, Long> snapshot = new HashMap<Object, Long>();
			getObjects(snapshot);
			FastByteArrayDataOutputStream snapshotBuffer = new FastByteArrayDataOutputStream();
			PersistWriteStore store = new SnapshotPersistStore(snapshot, this);
			ToByteArrayConverterSession session = new PersistToByteArrayConverterSession(converter, snapshotBuffer, store, false);
			try {
				for (Map.Entry<Object, Long> e : snapshot.entrySet()) {
					Long id = snapshot.get(e.getKey());
					if (id == null || id.equals(0L))
						continue;
					snapshotBuffer.writeByte(PersistEvent.ADD);

					converter.write(e.getKey(), session);
				}

			} catch (IOException ex) {
				throw OH.toRuntime(ex);
			}
			buffer.writeSnapshotDelta(TransactionalPersistWriter.toTransaction(snapshotBuffer));
		} else
			buffer.writeSnapshotDelta(OH.EMPTY_BYTE_ARRAY);
	}

	public ObjectToByteArrayConverter getConverter() {
		return converter;
	}

}
