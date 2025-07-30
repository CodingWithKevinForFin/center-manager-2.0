/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.ConvertedException;
import com.f1.utils.DetailedException;
import com.f1.utils.EH;
import com.f1.utils.FastDataOutput;

public class ThrowableToByteArrayConverter implements ByteArrayConverter<Throwable> {
	private static final Logger log = Logger.getLogger(ThrowableToByteArrayConverter.class.getName());

	@Override
	public void write(Throwable o, ToByteArrayConverterSession session) throws IOException {
		final FastDataOutput stream = session.getStream();
		final String localHost = EH.getLocalHost();
		final String pid = EH.getPid();
		while (o != null) {
			StringToByteArrayConverter.writeString(localHost, stream);
			StringToByteArrayConverter.writeString(pid, stream);
			StringToByteArrayConverter.writeString(o.getClass().getName(), stream);
			StringToByteArrayConverter.writeString(o.getMessage(), stream);
			if (o instanceof DetailedException) {
				final DetailedException ce = (DetailedException) o;
				final Set<String> keys = ce.getKeys();
				if (keys != null) {
					stream.writeInt(keys.size());
					for (String key : ce.getKeys()) {
						List<String> values = ce.getValues(key);
						if (values == null)
							values = Collections.EMPTY_LIST;
						if (key == null)
							key = "";
						StringToByteArrayConverter.writeString(key, stream);
						stream.writeInt(values.size());
						for (String value : values)
							StringToByteArrayConverter.writeString(value, stream);
					}
				} else
					stream.writeInt(0);
			} else
				stream.writeInt(0);

			StackTraceElement[] elements = o.getStackTrace();
			stream.writeInt(elements != null ? elements.length : 0);
			for (StackTraceElement ste : elements) {
				StringToByteArrayConverter.writeString(ste.getClassName(), stream);
				StringToByteArrayConverter.writeString(ste.getMethodName(), stream);
				StringToByteArrayConverter.writeString(ste.getFileName(), stream);

				stream.writeInt(ste.getLineNumber());
			}
			o = o.getCause();
			stream.writeBoolean(o != null);
		}
	}

	@Override
	public Throwable read(FromByteArrayConverterSession session) throws IOException {
		DataInput stream = session.getStream();
		Throwable r = null, last = null;
		ArrayList<String> values = null;
		do {
			String origHostName = StringToByteArrayConverter.readString(stream);
			String pid = StringToByteArrayConverter.readString(stream);
			String exceptionClass = StringToByteArrayConverter.readString(stream);
			String message = StringToByteArrayConverter.readString(stream);

			final int keysCount = stream.readInt();
			Exception t = new ConvertedException(origHostName, pid, exceptionClass, message);
			for (int i = 0; i < keysCount; i++) {
				String key = StringToByteArrayConverter.readString(stream);
				int valuesCount = stream.readInt();
				if (values == null)
					values = new ArrayList<String>(valuesCount);
				else {
					values.clear();
					values.ensureCapacity(valuesCount);
				}
				for (int j = 0; j < valuesCount; j++)
					values.add(StringToByteArrayConverter.readString(stream));
				((ConvertedException) t).addKeyValues(key, values);
			}
			if (last != null)
				last.initCause(t);
			else if (r == null)
				r = t;
			int elementsCount = stream.readInt();
			if (elementsCount >= 0) {
				StackTraceElement elements[] = new StackTraceElement[elementsCount];
				for (int i = 0; i < elementsCount; i++) {
					String className = StringToByteArrayConverter.readString(stream);
					String methodName = StringToByteArrayConverter.readString(stream);
					String fileName = StringToByteArrayConverter.readString(stream);
					int lineNumber = stream.readInt();
					elements[i] = new StackTraceElement(className, methodName, fileName, lineNumber);
				}
				t.setStackTrace(elements);
			}
			last = t;
		} while (stream.readBoolean());
		return r;
	}
	@Override
	public byte getBasicType() {
		return BasicTypes.THROWABLE;
	}

	@Override
	public boolean isCompatible(Class<?> o) {
		return Throwable.class.isAssignableFrom(o);
	}

}
