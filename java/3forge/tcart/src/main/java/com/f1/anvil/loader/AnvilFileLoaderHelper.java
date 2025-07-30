package com.f1.anvil.loader;

import com.f1.ami.client.AmiClient;
import com.f1.utils.CharSubSequence;

public class AnvilFileLoaderHelper {

	public static boolean sendString(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		int start = line.getPosition();
		int end = line.moveToAndSkipDelim(delim);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		}
		client.addMessageParamString(key, line.getBuffer(), start, end);
		return true;
	}
	public static boolean sendEnum(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		int start = line.getPosition();
		int end = line.moveToAndSkipDelim(delim);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		}
		client.addMessageParamEnum(key, line.getBuffer(), start, end);
		return true;
	}
	public static boolean sendDouble(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		double value = line.readDoubleOr('|', -1d);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		} else if (line.wasError())
			throw new RuntimeException(key + " not a double", line.getError());
		client.addMessageParamDouble(key, value);
		return true;
	}
	public static boolean sendInt(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		int value = line.readIntOr('|', -1);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		} else if (line.wasError())
			throw new RuntimeException(key + " not an int", line.getError());
		client.addMessageParamInt(key, value);
		return true;
	}
	public static boolean sendLong(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		long value = line.readLongOr('|', -1);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		} else if (line.wasError())
			throw new RuntimeException(key + " not an int", line.getError());
		client.addMessageParamLong(key, value);
		return true;
	}
	public static boolean sendLongTime(AmiClient client, String key, AnvilRecordReader line, char delim, boolean required) {
		long value = handleNanos(line.readLongOr('|', -1));
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			return false;
		} else if (line.wasError())
			throw new RuntimeException(key + " not an int", line.getError());
		client.addMessageParamLong(key, value);
		return true;
	}
	public static boolean readString(CharSubSequence sink, String key, AnvilRecordReader line, char delim, boolean required) {
		int start = line.getPosition();
		int end = line.moveToAndSkipDelim(delim);
		if (line.wasEmpty()) {
			if (required)
				throw new RuntimeException(key + " is required");
			sink.reset("", 0, 0);
			return false;
		}
		sink.reset(line.getBuffer(), start, end);
		return true;
	}
	public static void sendRemaining(AmiClient client, AnvilRecordReader line, char c, boolean b) {
		while (line.getLength() > line.getPosition()) {
			if (line.getBuffer().charAt(line.getPosition()) == '|') {
				line.setPosition(line.getPosition() + 1);
				continue;
			}
			client.addRawText("|", 0, 1).addRawText(line.getBuffer(), line.getPosition(), line.getLength());
			break;
		}
	}
	public static long handleNanos(long time) {
		return time > 1000000000000000L ? (time / 1000000L) : time;
	}
}
