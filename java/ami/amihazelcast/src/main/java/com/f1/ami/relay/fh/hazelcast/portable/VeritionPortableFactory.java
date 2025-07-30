package com.f1.ami.relay.fh.hazelcast.portable;

import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableFactory;

public class VeritionPortableFactory implements PortableFactory {

	@Override
	public Portable create(int classId) {
		if (RefPricePortable.ID == classId)
			return new RefPricePortable();
		else if (FXRatePortable.ID == classId)
			return new FXRatePortable();
		else if (FXForwardPortable.ID == classId)
			return new FXForwardPortable();
		return null;
	}
}