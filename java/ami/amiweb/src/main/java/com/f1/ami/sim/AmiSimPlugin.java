package com.f1.ami.sim;

import com.f1.ami.client.AmiClient;

public interface AmiSimPlugin {

	void visit(AmiClient client, AmiSimSession session, long periodMs);

}
