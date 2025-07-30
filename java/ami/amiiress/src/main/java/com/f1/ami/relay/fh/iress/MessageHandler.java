package com.f1.ami.relay.fh.iress;

public interface MessageHandler {
	void sendMessage(String topic, String key, Object value, long timestamp);
}
