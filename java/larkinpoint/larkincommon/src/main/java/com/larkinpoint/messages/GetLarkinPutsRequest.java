package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.BT.PUT")
public interface GetLarkinPutsRequest extends GetLarkinBackTestingDataRequest, PartialMessage, Lockable {

	byte PID_PUT_RATIO1 = 60;
	byte PID_PUT_RATIO2 = 61;
	byte PID_PUT_RATIO3 = 62;

	byte PID_PUT_RATIO1_UPPER_BOUND = 63;
	byte PID_PUT_RATIO2_UPPER_BOUND = 64;
	byte PID_PUT_RATIO3_UPPER_BOUND = 65;

	byte PID_PUT_RATIO1_LOWER_BOUND = 66;
	byte PID_PUT_RATIO2_LOWER_BOUND = 67;
	byte PID_PUT_RATIO3_LOWER_BOUND = 68;

	byte PID_TRADE_AMOUNT = 69;

	@PID(PID_PUT_RATIO1)
	public float getPutRatio1();
	public void setPutRatio1(float value);

	@PID(PID_PUT_RATIO2)
	public float getPutRatio2();
	public void setPutRatio2(float value);

	@PID(PID_PUT_RATIO3)
	public float getPutRatio3();
	public void setPutRatio3(float value);

	@PID(PID_PUT_RATIO1_UPPER_BOUND)
	public float getPutRatio1UpperBound();
	public void setPutRatio1UpperBound(float value);
	@PID(PID_PUT_RATIO1_LOWER_BOUND)
	public float getPutRatio1LowerBound();
	public void setPutRatio1LowerBound(float value);

	@PID(PID_PUT_RATIO2_UPPER_BOUND)
	public float getPutRatio2UpperBound();
	public void setPutRatio2UpperBound(float value);
	@PID(PID_PUT_RATIO2_LOWER_BOUND)
	public float getPutRatio2LowerBound();
	public void setPutRatio2LowerBound(float value);

	@PID(PID_PUT_RATIO3_UPPER_BOUND)
	public float getPutRatio3UpperBound();
	public void setPutRatio3UpperBound(float value);
	@PID(PID_PUT_RATIO3_LOWER_BOUND)
	public float getPutRatio3LowerBound();
	public void setPutRatio3LowerBound(float value);

	@PID(PID_TRADE_AMOUNT)
	public float getTradeAmount();
	public void setTradeAmount(float value);

}
