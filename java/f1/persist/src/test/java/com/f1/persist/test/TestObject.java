package com.f1.persist.test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.ValuedListenable;

public interface TestObject extends Message, ValuedListenable {

	@PID(1)
	public boolean getBooleanValue();

	public void setBooleanValue(boolean value);

	@PID(2)
	public byte getByteValue();

	public void setByteValue(byte value);

	@PID(3)
	public char getCharValue();

	public void setCharValue(char value);

	@PID(4)
	public short getShortValue();

	public void setShortValue(short value);

	@PID(5)
	public int getIntValue();

	public void setIntValue(int value);

	@PID(6)
	public long getLongValue();

	public void setLongValue(long value);

	@PID(7)
	public float getFloatValue();

	public void setFloatValue(float value);

	@PID(8)
	public double getDoubleValue();

	public void setDoubleValue(double value);

	@PID(9)
	public String getStringValue();

	public void setStringValue(String value);

	@PID(10)
	public TestObject getLeft();

	public void setLeft(TestObject value);

	@PID(11)
	public TestObject getRight();

	public void setRight(TestObject value);

	@PID(12)
	public Class getClassValue();

	public void setClassValue(Class value);

	@PID(13)
	public Map<String, TestObject> getMapValue();

	public void setMapValue(Map<String, TestObject> mapValue);

	@PID(14)
	public List getList();

	public void setList(List list);

	@PID(15)
	public Map getMap();

	public void setMap(Map map);

	@PID(16)
	public Set getSet();

	public void setSet(Set map);
}
