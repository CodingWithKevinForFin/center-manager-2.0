package com.f1.ami.relay.fh.tibcoems;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.PropertyController;
import javax.jms.Message;

public interface AmiTibcoEMSMessageParser {
	public void parseMessage(AmiRelayMapToBytesConverter converter, Message message);
	public void init(PropertyController props);
}