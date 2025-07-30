/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class ClassToByteArrayConverter_Legacy extends AbstractCircRefByteArrayConverter<Class> {
	private static final Logger log = Logger.getLogger(ClassToByteArrayConverter_Legacy.class.getName());

	public ClassToByteArrayConverter_Legacy() {
		super(Class.class, BasicTypes.CLASS);
	}

	@Override
	protected Class read(DataInput stream) throws IOException {
		String name = null;
		try {
			return OH.forName(name = stream.readUTF());
		} catch (ClassNotFoundException e) {
			LH.severe(log, "Class not found: ", name, e);
			return null;
		}
	}

	@Override
	protected void write(Class o, DataOutput stream) throws IOException {
		stream.writeUTF(o.getName());
	}
}
