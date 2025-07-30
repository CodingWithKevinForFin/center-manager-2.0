/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.logging.Logger;

import com.f1.base.BasicTypes;
import com.f1.utils.BasicTypeHelper;
import com.f1.utils.LH;
import com.f1.utils.OH;

public class ClassToByteArrayConverter extends AbstractCircRefByteArrayConverter<Class> {
	private static final Logger log = Logger.getLogger(ClassToByteArrayConverter.class.getName());

	public ClassToByteArrayConverter() {
		super(Class.class, BasicTypes.CLASS);
	}

	@Override
	protected Class read(DataInput stream) throws IOException {
		byte type = stream.readByte();
		if (type != BasicTypes.UNDEFINED)
			return BasicTypeHelper.toClass(type);
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
		byte type = BasicTypeHelper.toTypeNoInheritance(o);
		stream.writeByte(type);
		if (type == BasicTypes.UNDEFINED)
			stream.writeUTF(o.getName());
	}

}
