/* The contents of this file are subject to the terms and conditions of the 3Forge LLC. End User License agreement Version 1.0 */

package com.f1.utils.converter.bytes;

import java.io.IOException;

import com.f1.base.BasicTypes;
import com.f1.base.Password;
import com.f1.utils.FastDataInput;
import com.f1.utils.FastDataOutput;

public class PasswordToByteArrayConverter extends SimpleByteArrayConverter<Password> {

	public PasswordToByteArrayConverter() {
		super(Password.class, BasicTypes.PASSWORD);
	}

	@Override
	protected Password read(FastDataInput stream) throws IOException {
		int length = stream.readInt();
		if (length != Password.UNKNOWN_LENGTH) {
			char[] r = new char[length];
			for (int i = 0; i < r.length; i++)
				r[i] = stream.readChar();
			return new Password(r, true);
		} else
			return new Password(null, true);
	}

	@Override
	protected void write(Password o, FastDataOutput stream) throws IOException {
		int length = o.getLength();
		stream.writeInt(length);
		if (length != Password.UNKNOWN_LENGTH)
			for (int i = 0; i < o.getLength(); i++)
				stream.writeChar(o.getObfuscatedCharAt(i));
	}
}
