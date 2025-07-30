package com.larkinpoint.messages;

import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.SP.MSG")
public interface SpreadMessage extends PartialMessage, Lockable {

	byte PID_LEG1 = 10;
	byte PID_LEG2 = 11;
	byte PID_LEG3 = 12;
	byte PID_LEG_COUNTS = 15;
	byte PID_DAILY_P_AND_L = 16;

	byte PID_CASH_FLOW = 17;
	byte PID_STARTING_VALUE = 18;
	byte PID_ENDING_VALUE = 19;
	byte PID_DAILY_RETURN = 20;
	byte PID_NUMBER_OF_SPREADS = 21;

	@PID(PID_LEG1)
	public OptionMessage getLeg1();
	public void setLeg1(OptionMessage ov);

	@PID(PID_LEG2)
	public OptionMessage getLeg2();
	public void setLeg2(OptionMessage ov);

	@PID(PID_LEG3)
	public OptionMessage getLeg3();
	public void setLeg3(OptionMessage ov);

	@PID(PID_LEG_COUNTS)
	public int getLegCounts();
	public void setLegCounts(int count);

	@PID(PID_DAILY_P_AND_L)
	public double getDailyPAndL();
	public void setDailyPAndL(double value);

	@PID(PID_CASH_FLOW)
	public double getCashFlow();
	public void setCashFlow(double value);

	@PID(PID_STARTING_VALUE)
	public double getStartingValue();
	public void setStartingValue(double value);

	@PID(PID_ENDING_VALUE)
	public double getEndingValue();
	public void setEndingValue(double value);
	@PID(PID_DAILY_RETURN)
	public double getDailyReturn();
	public void setDailyReturn(double value);
	@PID(PID_NUMBER_OF_SPREADS)
	public double getNumberOfSpreads();
	public void setNumberOfSpreads(double value);

}