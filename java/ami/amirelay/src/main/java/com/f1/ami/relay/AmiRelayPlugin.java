package com.f1.ami.relay;

import com.f1.ami.relay.fh.AmiFH;
import com.f1.utils.ByteArray;
import com.f1.utils.PropertyController;

public interface AmiRelayPlugin {
	int ERROR = 1;
	int NA = 2;
	int OKAY = 3;
	int SKIP = 4;

	int processData(ByteArray mutableRawData, StringBuilder errorSink);

	boolean init(PropertyController properties, AmiFH fh, String switches, StringBuilder errorSink);

}
