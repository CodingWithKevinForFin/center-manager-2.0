/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.ack;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.f1.base.Ackable;
import com.f1.base.Acker;
import com.f1.utils.FastByteArrayDataInputStream;
import com.f1.utils.IOH;
import com.f1.utils.concurrent.FastAsyncMemMap;

public class FileAcker implements Acker, Closeable {
	final private AtomicInteger size = new AtomicInteger();
	final private AtomicInteger lastContinousId = new AtomicInteger();
	final private ConcurrentMap<Integer, Integer> ids2loc = new ConcurrentHashMap<Integer, Integer>();
	final private ConcurrentMap<Integer, Integer> freeLoc = new ConcurrentHashMap<Integer, Integer>();
	private FastAsyncMemMap file;
	final private int totalSize;

	@Override
	public void ack(Ackable ackable, Object optionalResult) {
		int ackId = ackable.askAckId();
		if (ackId == Ackable.NO_ACK_ID)
			return;
		ack(ackId);
		ackable.registerAcker(null);
	}

	public void ack(int... ackId) {
		for (int a : ackId)
			ack(a);
	}

	public void ack(int ackId) {
		if (ackId < 1)
			throw new RuntimeException("Can not ack: " + ackId);
		if (lastContinousId.compareAndSet(ackId - 1, ackId)) {
			Integer loc;
			while ((loc = ids2loc.remove(ackId + 1)) != null) {
				freeLoc.put(loc, loc);
				ackId++;
			}
			while (lastContinousId.get() < ackId)
				lastContinousId.set(ackId);
			writeIntAt(0, ackId);
		} else {
			if (ackId < lastContinousId.get() || ids2loc.containsKey(ackId))
				return;
			Integer position = null;
			for (Integer loc : freeLoc.keySet()) {
				if (freeLoc.remove(loc) != null) {
					position = loc;
					break;
				}
			}
			if (position == null)
				position = size.getAndIncrement();
			ids2loc.put(ackId, position);
			writeIntAt(position + 1, ackId);
		}
	}

	public FileAcker(File location, int totalSize) throws IOException {
		IOH.ensureDir(location.getParentFile());
		this.totalSize = totalSize;
		boolean exists = location.exists();

		file = new FastAsyncMemMap();
		file.map(location, 0, totalSize * 4, false);
		if (exists) {
			byte[] buf = new byte[totalSize * 4];
			file.read(0, buf, 0, buf.length);
			FastByteArrayDataInputStream dis = new FastByteArrayDataInputStream(buf);
			int i = dis.readInt();
			if (i != -1)
				lastContinousId.set(i);
			int loc = 0;
			while ((i = dis.readInt()) != Ackable.NO_ACK_ID) {
				size.incrementAndGet();
				if (i <= lastContinousId.get() || ids2loc.containsKey(i))
					freeLoc.put(loc, loc);
				else
					ids2loc.put(i, loc);
				loc++;
			}
			dis.close();

		} else {
			lastContinousId.set(-1);
			for (int i = 0; i < totalSize; i++)
				writeIntAt(i, Ackable.NO_ACK_ID);
		}
	}

	public List<Integer> getMissing() {
		List<Integer> r = new ArrayList<Integer>();
		NavigableSet<Integer> acked = new TreeSet<Integer>();
		Integer start = lastContinousId.get();
		acked.add(start);
		for (Integer i : ids2loc.keySet()) {
			if (i == Ackable.NO_ACK_ID)
				break;
			if (i > start)
				acked.add(i);
		}
		Integer last = acked.first();
		for (Integer id : acked) {
			for (int i = last + 1; i < id; i++)
				r.add(i);
			last = id;
		}
		r.add(acked.last() + 1);
		return r;
	}

	private void writeIntAt(int position, int ackId) {
		file.writeInt(position * 4, ackId);
	}

	@Override
	public void close() throws IOException {
		file.unmap();
		file = null;
	}

}
