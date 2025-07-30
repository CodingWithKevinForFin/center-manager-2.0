/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.base.Ideable;
import com.f1.base.PartialMessage;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.FastDataOutput;
import com.f1.utils.LH;
import com.f1.utils.concurrent.ConcurrentHashSet;
import com.f1.utils.structs.Tuple2;

public class ValuedToByteArrayConverter implements ByteArrayConverter<Valued> {
	private static final Logger log = Logger.getLogger(ValuedToByteArrayConverter.class.getName());
	private static final ConcurrentHashSet<Tuple2<Class<?>, Object>> alreadyReadLogged = new ConcurrentHashSet<Tuple2<Class<?>, Object>>();

	@Override
	public byte getBasicType() {
		return BasicTypes.MESSAGE;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Valued.class.isAssignableFrom(o);
	}

	@Override
	public Valued read(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Valued) session.get(id);
		DataInput stream = session.getStream();
		ObjectToByteArrayConverter converter = session.getConverter();
		long ideableId = stream.readLong();
		Valued r;
		if (ideableId == Ideable.NO_IDEABLEID) {
			String ideableName = StringToByteArrayConverter.readString(stream);
			r = (Valued) converter.getGenerator().nw(ideableName);
		} else
			r = (Valued) converter.getGenerator().nw(ideableId);
		session.store(id, r);
		if (r instanceof ByteArraySelfConverter && shouldSelfConvert((ByteArraySelfConverter) r)) {
			((ByteArraySelfConverter) r).read(session);
		} else {
			if (stream.readBoolean()) {
				for (;;) {
					byte pid = stream.readByte();
					if (pid == Valued.NO_PID)
						break;
					writeToValuedByPid(r, pid, session);//TODO: test
				}
			} else {
				for (;;) {
					String name = StringToByteArrayConverter.readString(stream);
					if (name.length() == 0)
						break;
					writeToValuedByName(r, name, session);
				}
			}
		}
		return r;
	}

	@Override
	public void write(Valued msg, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(msg))
			return;
		byte skipTransience = session.getConverter().getSkipTransience();
		FastDataOutput stream = session.getStream();
		if (msg instanceof Ideable) {
			Ideable ideable = (Ideable) msg;
			long id = ideable.askVid();
			stream.writeLong(id);
			if (id == Ideable.NO_IDEABLEID)
				StringToByteArrayConverter.writeString(ideable.askIdeableName(), stream);
		} else {
			stream.writeLong(Ideable.NO_IDEABLEID);
			StringToByteArrayConverter.writeString(msg.askSchema().askOriginalType().getName(), stream);
		}
		if (msg instanceof ByteArraySelfConverter && shouldSelfConvert((ByteArraySelfConverter) msg)) {
			((ByteArraySelfConverter) msg).write(session);
		} else if (msg instanceof PartialMessage) {
			Iterator<ValuedParam> params = ((PartialMessage) msg).askExistingValuedParams().iterator();
			if (msg.askSchema().askSupportsPids()) {
				stream.writeBoolean(true);
				while (params.hasNext())
					writeToStreamByPid(msg, params.next(), session, skipTransience);//TODO: test
				stream.write(Valued.NO_PID);
			} else {
				stream.writeBoolean(false);
				while (params.hasNext())
					writeToStreamByName(msg, params.next(), session, skipTransience);//TODO: test
				StringToByteArrayConverter.writeString("", stream);
			}
		} else {
			ValuedParam<Valued>[] params = msg.askSchema().askValuedParams();
			if (msg.askSchema().askSupportsPids()) {
				stream.writeBoolean(true);
				for (int i = 0; i < params.length; i++)
					writeToStreamByPid(msg, params[i], session, skipTransience);//TODO: test
				stream.write(Valued.NO_PID);
			} else {
				stream.writeBoolean(false);
				for (int i = 0; i < params.length; i++)
					writeToStreamByName(msg, params[i], session, skipTransience);
				StringToByteArrayConverter.writeString("", stream);
			}
		}
	}

	protected void writeToStreamByName(Valued msg, ValuedParam<Valued> param, ToByteArrayConverterSession session, byte skipTransience) {
		if ((param.getTransience() & skipTransience) != 0)
			return;
		FastDataOutput stream = session.getStream();
		String name = param.getName();
		try {
			StringToByteArrayConverter.writeString(name, stream);
			if (param.isPrimitive()) {
				stream.write(param.getBasicType());
				param.write(msg, stream);
			} else {
				session.getConverter().write(msg.ask(name), session);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not write param '" + name + "' for " + msg.getClass().getName(), e);
		}

	}

	protected void writeToStreamByPid(Valued msg, ValuedParam<Valued> param, ToByteArrayConverterSession session, byte skipTransience) {
		if ((param.getTransience() & skipTransience) != 0)
			return;
		FastDataOutput stream = session.getStream();
		byte pid = param.getPid();
		try {
			stream.writeByte(pid);
			if (param.isPrimitive()) {
				stream.write(param.getBasicType());
				param.write(msg, stream);
			} else {
				session.getConverter().write(msg.ask(pid), session);
			}
		} catch (Exception e) {
			throw new RuntimeException("Could not write pid '" + pid + "' for " + msg.getClass().getName(), e);
		}

	}

	protected boolean shouldSelfConvert(ByteArraySelfConverter msg) {
		return true;
	}
	protected void writeToValuedByName(Valued r, String name, FromByteArrayConverterSession session) throws IOException {
		Object o = session.getConverter().read(session);
		try {
			r.put(name, o);
		} catch (RuntimeException e) {
			if (alreadyReadLogged.add(new Tuple2<Class<?>, Object>(r.getClass(), name))) {
				if ("no such param name".equals(e.getMessage())) {
					LH.warning(log, "(Only logging this once) Unknown param: ", r.askSchema().askOriginalType().getName(), "::", name);
				} else
					LH.warning(log, "(Only logging this once) Error for param: ", r.askSchema().askOriginalType().getName(), "::", name, e);
			}
		}
	}

	protected void writeToValuedByPid(Valued r, byte pid, FromByteArrayConverterSession session) throws IOException {
		Object o = session.getConverter().read(session);
		try {
			r.put(pid, o);
		} catch (RuntimeException e) {
			if (alreadyReadLogged.add(new Tuple2<Class<?>, Object>(r.getClass(), pid))) {
				if ("no such param name".equals(e.getMessage())) {
					LH.warning(log, "(Only logging this once) Unknown param: ", r.askSchema().askOriginalType().getName(), "::", pid);
				} else
					LH.warning(log, "(Only logging this once) Error for param: ", r.askSchema().askOriginalType().getName(), "::", pid, e);
			}
		}
	}

}
