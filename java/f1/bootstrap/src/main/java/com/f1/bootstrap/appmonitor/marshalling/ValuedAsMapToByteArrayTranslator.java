package com.f1.bootstrap.appmonitor.marshalling;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.base.BasicTypes;
import com.f1.base.Ideable;
import com.f1.base.PartialMessage;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.VidParser;
import com.f1.utils.converter.bytes.ByteArrayConverter;
import com.f1.utils.converter.bytes.ByteArrayConverterTranslator;
import com.f1.utils.converter.bytes.FromByteArrayConverterSession;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.bytes.StringToByteArrayConverter;
import com.f1.utils.converter.bytes.ToByteArrayConverterSession;

public class ValuedAsMapToByteArrayTranslator extends ByteArrayConverterTranslator<Map, Valued> {

	public ValuedAsMapToByteArrayTranslator() {
		super(Map.class, BasicTypes.MAP, Valued.class, BasicTypes.MESSAGE);
	}

	@Override
	public Map readObject(FromByteArrayConverterSession session) throws IOException {
		int id = session.handleIfAlreadyConverted();
		if (id < 0)
			return (Map) session.get(id);
		DataInput stream = session.getStream();
		ObjectToByteArrayConverter converter = session.getConverter();
		int ideableId = stream.readInt();
		Map<Object, Object> r = new LinkedHashMap<Object, Object>();
		if (ideableId == Ideable.NO_IDEABLEID) {
			String ideableName = StringToByteArrayConverter.readString(stream);
			r.put("_", ideableName);
		} else {
			r.put("_", VidParser.fromLong(ideableId));
		}
		session.store(id, r);
		boolean pids = stream.readBoolean();
		if (pids) {
			for (;;) {
				byte pid = stream.readByte();
				if (pid == Valued.NO_PID)
					break;
				r.put(pid, converter.read(session));
			}
		} else {
			for (;;) {
				String name = StringToByteArrayConverter.readString(stream);
				if (name.length() == 0)
					break;
				r.put(name, converter.read(session));
			}
		}
		return r;
	}

	@Override
	public void writeObject(Valued msg, ToByteArrayConverterSession session) throws IOException {
		if (session.handleIfAlreadyConverted(msg))
			return;
		DataOutput stream = session.getStream();
		ObjectToByteArrayConverter converter = session.getConverter();
		ByteArrayConverter keyConverter = converter.getConverter(String.class);
		ByteArrayConverter valConverter = session.getConverter();
		byte skipTransience = session.getConverter().getSkipTransience();
		if (msg instanceof PartialMessage) {
			Iterable<ValuedParam> params = ((PartialMessage) msg).askExistingValuedParams();
			int cnt = 0;
			for (ValuedParam<Valued> param : params) {
				if ((param.getTransience() & skipTransience) != 0)
					continue;
				Object value = param.getValue(msg);
				if (value == null)
					continue;
				cnt++;
			}
			stream.writeInt(cnt + 1);//add one because of class name
			stream.writeByte(keyConverter.getBasicType());
			stream.writeByte(valConverter.getBasicType());
			keyConverter.write("_", session);
			valConverter.write(msg.askSchema().askOriginalType().getName(), session);
			for (ValuedParam<Valued> param : params) {
				if ((param.getTransience() & skipTransience) != 0)
					continue;
				Object value = param.getValue(msg);
				if (value == null)
					continue;
				keyConverter.write(param.getName(), session);
				valConverter.write(value, session);
			}
		} else {
			ValuedParam<Valued>[] params = msg.askSchema().askValuedParams();
			int cnt = 0;
			for (ValuedParam<Valued> param : params) {
				if ((param.getTransience() & skipTransience) != 0)
					continue;
				Object value = param.getValue(msg);
				if (value == null)
					continue;
				cnt++;
			}
			stream.writeInt(cnt + 1);//add one because of class name
			stream.writeByte(keyConverter.getBasicType());
			stream.writeByte(valConverter.getBasicType());
			keyConverter.write("_", session);
			valConverter.write(msg.askSchema().askOriginalType().getName(), session);
			for (ValuedParam<Valued> param : params) {
				if ((param.getTransience() & skipTransience) != 0)
					continue;
				Object value = param.getValue(msg);
				if (value == null)
					continue;
				keyConverter.write(param.getName(), session);
				valConverter.write(value, session);
			}
		}
	}
}
