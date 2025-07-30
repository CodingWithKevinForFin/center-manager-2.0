package com.f1.ami.amicommon.encrypt;

import java.io.InputStream;
import java.io.OutputStream;

import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public abstract class AmiAbstractPasswordEncrypterPlugin implements AmiPasswordEncrypterPlugin {

	@Override
	public void init(ContainerTools tools, PropertyController props) {
	}

	@Override
	public String getKey64() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getBitDepth() {
		throw new UnsupportedOperationException();
	}

	@Override
	public InputStream createDecrypter(InputStream inner) {
		throw new UnsupportedOperationException();
	}

	@Override
	public OutputStream createEncrypter(OutputStream inner) {
		throw new UnsupportedOperationException();
	}

}
