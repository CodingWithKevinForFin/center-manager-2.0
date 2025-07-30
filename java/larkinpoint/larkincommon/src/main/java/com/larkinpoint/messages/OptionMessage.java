package com.larkinpoint.messages;

import com.f1.base.Day;
import com.f1.base.Lockable;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;

@VID("LP.OP.MSG")
public interface OptionMessage extends PartialMessage, Lockable {

	byte PID_DELTA = 91;
	byte PID_GAMMA = 92;
	byte PID_THETA = 93;
	byte PID_VEGA = 94;
	byte PID_IMPLIED_VOL = 95;
	byte PID_STRIKE = 96;
	byte PID_LAST = 97;
	byte PID_ASK = 98;
	byte PID_EXPIRY = 99;
	byte PID_OPEN_INTEREST = 100;
	byte PID_VOLUME = 101;
	byte PID_BID = 102;
	byte PID_C_P = 103;
	byte PID_UNDERLYING = 104;
	byte PID_TRADE_DATE = 105;
	byte PID_UNDERLYING_CLOSE = 106;
	byte PID_DAYS_TO_EXPIRY = 107;
	byte PID_OPTION_ID = 108;

	byte PID_PAIRED_OPTION = 110;
	byte PID_PAIRED_OPTION_TYPE = 111;
	byte PID_PAIRED_RATIO = 112;
	byte PID_PAIRED_VALUE = 113;
	byte PID_INTRINSIC_VALUE = 114;
	byte PID_PAIRED_OM = 115;

	byte STRADDLE_TYPE = 1;
	byte STRANGLE_TYPE = 2;

	@PID(PID_C_P)
	public boolean getCP();
	public void setCP(boolean cp);

	@PID(PID_UNDERLYING)
	public String getUnderlying();
	public void setUnderlying(String underlying);
	@PID(PID_STRIKE)
	public float getStrike();
	public void setStrike(float strike);
	@PID(PID_EXPIRY)
	public Day getExpiry();
	public void setExpiry(Day expiry);
	@PID(PID_TRADE_DATE)
	public Day getTradeDate();
	public void setTradeDate(Day tradeDate);
	@PID(PID_LAST)
	public float getLast();
	public void setLast(float last);
	@PID(PID_BID)
	public float getBid();
	public void setBid(float bid);
	@PID(PID_ASK)
	public float getAsk();
	public void setAsk(float ask);
	@PID(PID_DELTA)
	public float getDelta();
	public void setDelta(float delta);
	@PID(PID_GAMMA)
	public float getGamma();
	public void setGamma(float gamma);
	@PID(PID_THETA)
	public float getTheta();
	public void setTheta(float theta);
	@PID(PID_VEGA)
	public float getVega();
	public void setVega(float vega);
	@PID(PID_IMPLIED_VOL)
	public float getImpliedVol();
	public void setImpliedVol(float impliedVol);
	@PID(PID_VOLUME)
	public int getVolume();
	public void setVolume(int volume);
	@PID(PID_OPEN_INTEREST)
	public int getOpenInterest();
	public void setOpenInterest(int openInterest);
	@PID(PID_UNDERLYING_CLOSE)
	public float getUnderlyingClose();
	public void setUnderlyingClose(float underCLose);
	@PID(PID_DAYS_TO_EXPIRY)
	public int getDaysToExpiry();
	public void setDaysToExpiry(int days);

	@PID(PID_OPTION_ID)
	public int getOptionId();
	public void setOptionId(int ID);

	@PID(PID_PAIRED_OPTION)
	public int getPairedOption();
	public void setPairedOption(int optionID);
	@PID(PID_PAIRED_OPTION_TYPE)
	public byte getPairedOptionType();
	public void setPairedOptionType(byte optionID);
	@PID(PID_PAIRED_RATIO)
	public float getPairedRatio();
	public void setPairedRatio(float ratio);
	@PID(PID_INTRINSIC_VALUE)
	public float getIntrinsicValue();
	public void setIntrinsicValue(float value);
	@PID(PID_PAIRED_VALUE)
	public float getPairedValue();
	public void setPairedValue(float value);
	@PID(PID_PAIRED_OM)
	public OptionMessage getPairedOm();
	public void setPairedOm(OptionMessage value);

}