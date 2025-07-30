package com.f1.pofo.fix;

import java.util.Map;

import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.VID;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;

@VID("F1.FX.OI")
public interface FixOrderInfo extends PartialMessage {

	/**
	 * @return the total order quantity (typically fix tag 38)
	 */
	@PID(1)
	public int getOrderQty();
	public void setOrderQty(int OrderQty);

	/**
	 * @return the limit price (typically fix tag 44)
	 */
	@PID(2)
	public double getLimitPx();
	public void setLimitPx(double Px);

	/**
	 * @return side of the order(typically fix tag 54)
	 */
	@PID(3)
	public Side getSide();
	public void setSide(Side Side);

	/**
	 * @return account of the order(typically fix tag 1)
	 */
	@PID(4)
	public String getAccount();
	public void setAccount(String account);

	/**
	 * @return order type of the order(typically fix tag 40)
	 */
	@PID(5)
	public OrderType getOrderType();
	public void setOrderType(OrderType OrdType);

	/**
	 * @return time in force for the order(typically fix tag 59)
	 */
	@PID(6)
	public TimeInForce getTimeInForce();
	public void setTimeInForce(TimeInForce TimeInForce);

	/**
	 * @return user-defined custom fields on the order, typically the keys are fix tags.
	 */
	@PID(7)
	public Map<Integer, String> getPassThruTags();
	public void setPassThruTags(Map<Integer, String> passThruTags);

	/**
	 * @return text for the order(typically fix tag 58)
	 */
	@PID(8)
	public String getText();
	public void setText(String string);

	/**
	 * @return execution instructions on the order(typically fix tag 18)
	 */
	@PID(9)
	public String getExecInstructions();
	public void setExecInstructions(String execInstructions);

	/**
	 * @return the currency on price and value fields(typically fix tag 15)
	 */
	@PID(10)
	public String getCurrency();
	public void setCurrency(String string);

}
