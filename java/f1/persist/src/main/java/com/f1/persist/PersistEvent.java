package com.f1.persist;

import com.f1.base.Clearable;
import com.f1.base.ValuedListenable;

public interface PersistEvent extends Clearable {
	public static final boolean WRITE_CHECKSUM = true;

	static final public byte ADD = 1;
	static final public byte COMMIT = 2;
	static final public byte BEGIN = 3;

	static final public byte REMOVE = 11;
	static final public byte UPDATE_BY_PID = 12;
	static final public byte UPDATE_BY_PIN = 13;
	static final public byte UPDATE_KEYED = 14;
	static final public byte CLEAR_KEYED = 15;
	static final public byte REMOVE_KEYED = 16;
	static final public byte ADD_KEYED = 17;

	static final public byte CURRENT_REMOVE = 21;
	static final public byte CURRENT_UPDATE_BY_PID = 22;
	static final public byte CURRENT_UPDATE_BY_PIN = 23;
	static final public byte CURRENT_UPDATE_KEYED = 24;
	static final public byte CURRENT_CLEAR_KEYED = 25;
	static final public byte CURRENT_REMOVE_KEYED = 26;
	static final public byte CURRENT_ADD_KEYED = 27;

	static final public byte CHECKSUM1 = 100;
	static final public byte CHECKSUM2 = 101;

	public byte getType();

	public void setType(byte type);

	public ValuedListenable getTarget();

	public void setTarget(ValuedListenable target);

	public byte getPid();

	public void setPid(byte pid);

	public String getPin();

	public void setPin(String pin);

	public Object getKey();

	public void setKey(Object key);

	public Object getValue();

	public void setValue(Object value);

	public void setId(long id);

	public long getId();

}
