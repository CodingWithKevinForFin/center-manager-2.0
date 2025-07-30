package com.f1.ami.relay.fh.aeron;

import org.agrona.DirectBuffer;

import com.f1.ami.relay.fh.AmiRelayMapToBytesConverter;
import com.f1.utils.PropertyController;

import io.aeron.logbuffer.Header;

public interface AmiAeronMessageParser {
	public AmiRelayMapToBytesConverter parseMessage(DirectBuffer buffer, int offset, int length, Header header);
	public void init(PropertyController props);
}