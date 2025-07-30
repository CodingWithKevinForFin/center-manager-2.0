package com.f1.persist.impl;

import com.f1.base.Message;
import com.f1.base.ValuedListenable;
import com.f1.persist.PersistEvent;
import com.f1.utils.SH;

public class BasicPersistEvent implements PersistEvent {

	private byte type;
	private ValuedListenable target;
	private byte pid = Message.NO_PID;
	private String pin;
	private Object key;
	private Object value;
	private long id;

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public ValuedListenable getTarget() {
		return target;
	}

	public void setTarget(ValuedListenable target) {
		this.target = target;
	}

	public byte getPid() {
		return pid;
	}

	public void setPid(byte pid) {
		this.pid = pid;
	}

	public String getPin() {
		return pin;
	}

	public void setPin(String pin) {
		this.pin = pin;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public void clear() {
		this.pid = Message.NO_PID;
		this.type = 0;
		this.key = this.value = this.target = null;
		this.id = 0;
		this.pin = null;
	}

	@Override
	public void setId(long id) {
		this.id = id;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("BasicPersistEvent [type=" + toString(type));
		if (target != null)
			sb.append(", target=").append(SH.toObjectString(target));
		if (pid != 0)
			sb.append(", pid=").append(pid);
		if (pin != null)
			sb.append(", pin=").append(pin);
		if (key != null)
			sb.append(", key=").append(key);
		if (value != null)
			sb.append(", value=").append(value);
		if (id != 0)
			sb.append(", id=").append(id);
		sb.append(']');
		return sb.toString();

	}
	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public void setKey(Object key) {
		this.key = key;
	}

	public static String toString(byte type) {
		switch (type) {
			case ADD :
				return "ADD(" + ADD + ")";
			case COMMIT :
				return "COMMIT(" + COMMIT + ")";
			case BEGIN :
				return "BEGIN(" + BEGIN + ")";

			case REMOVE :
				return "REMOVE(" + REMOVE + ") ";
			case UPDATE_BY_PID :
				return "UPDATE_BY_PID(" + UPDATE_BY_PID + ")";
			case UPDATE_BY_PIN :
				return "UPDATE_BY_PIN(" + UPDATE_BY_PIN + ")";
			case UPDATE_KEYED :
				return "UPDATE_KEYED(" + UPDATE_KEYED + ")";
			case CLEAR_KEYED :
				return "CLEAR_KEYED(" + CLEAR_KEYED + ")";
			case REMOVE_KEYED :
				return "REMOVE_KEYED(" + REMOVE_KEYED + ")";
			case ADD_KEYED :
				return "ADD_KEYED(" + ADD_KEYED + ")";

			case CURRENT_REMOVE :
				return "CURRENT_REMOVE(" + CURRENT_REMOVE + ") ";
			case CURRENT_UPDATE_BY_PID :
				return "CURRENT_UPDATE_BY_PID(" + CURRENT_UPDATE_BY_PID + ")";
			case CURRENT_UPDATE_BY_PIN :
				return "CURRENT_UPDATE_BY_PIN(" + CURRENT_UPDATE_BY_PIN + ")";
			case CURRENT_UPDATE_KEYED :
				return "CURRENT_UPDATE_KEYED(" + CURRENT_UPDATE_KEYED + ")";
			case CURRENT_CLEAR_KEYED :
				return "CURRENT_CLEAR_KEYED(" + CURRENT_CLEAR_KEYED + ")";
			case CURRENT_REMOVE_KEYED :
				return "CURRENT_REMOVE_KEYED(" + CURRENT_REMOVE_KEYED + ")";
			case CURRENT_ADD_KEYED :
				return "CURRENT_ADD_KEYED(" + CURRENT_ADD_KEYED + ")";
			case CHECKSUM1 :
				return "CHECKSUM1(" + CHECKSUM1 + ")";
			case CHECKSUM2 :
				return "CHECKSUM2(" + CHECKSUM2 + ")";
		}
		return null;
	}

}
