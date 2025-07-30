package com.f1.ami.relay.plugins;

import com.f1.ami.relay.AmiRelayPlugin;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.utils.ByteArray;
import com.f1.utils.PropertyController;

public class DummyAmiPlugin implements AmiRelayPlugin {

	@Override
	public int processData(ByteArray mutableRawData, StringBuilder errorSink) {
		return 0;
	}

	@Override
	public boolean init(PropertyController properties, AmiFH fh, String switches, StringBuilder errorSink) {
		return false;
	}

}
