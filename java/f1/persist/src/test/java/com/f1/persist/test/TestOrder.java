package com.f1.persist.test;

import com.f1.base.Message;
import com.f1.base.PID;
import com.f1.base.ValuedListenable;

public interface TestOrder extends Message, ValuedListenable {

	@PID(1)
	public TestInstrument getInstrument();

	public void setInstrument(TestInstrument instrument);

	@PID(2)
	public String getTicker();

	public void setTicker(String ticker);

	@PID(3)
	public int getQuantity();

	public void setQuantity(int quantity);

	@PID(4)
	public long getFilledQuantity();

	public void setFilledQuantity(long quantity);

	@PID(5)
	public double getPrice();

	public void setPrice(double price);

	@PID(6)
	public float getAvgPrice();

	public void setAvgPrice(float price);

	@PID(7)
	public char getState();

	public void setState(char state);

	@PID(8)
	public void setIsOpen(boolean isOpen);

	public boolean getIsOpen();
}
