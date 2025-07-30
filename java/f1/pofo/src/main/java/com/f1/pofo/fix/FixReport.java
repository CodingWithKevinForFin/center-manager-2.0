package com.f1.pofo.fix;

import java.util.Map;

import com.f1.base.DateNanos;
import com.f1.base.PID;
import com.f1.base.VID;
import com.f1.pofo.oms.OrderType;
import com.f1.pofo.oms.Side;
import com.f1.pofo.oms.TimeInForce;

/**
 * a fix report message
 */
@VID("F1.FX.RE")
public interface FixReport extends FixMsg {
	// ---NON MODIFIABLE FIELDS

	/**
	 * @return the security id
	 */
	@PID(10)
	public String getSecurityID();
	public void setSecurityID(String SecurityID);

	/**
	 * @return the id source (typically tag 22)
	 */
	@PID(11)
	public int getIDType();
	public void setIDType(int IDType);

	/**
	 * @return the symbol (typically tag 55)
	 */
	@PID(12)
	public String getSymbol();
	public void setSymbol(String Symbol);

	/**
	 * @return the exchange destination
	 */
	@PID(13)
	public String getDestination();
	public void setDestination(String destination);

	/**
	 * @return the total order quantity
	 */
	@PID(14)
	public int getOrderQty();
	public void setOrderQty(int OrderQty);

	/**
	 * @return the limit price
	 */
	@PID(15)
	public Double getLimitPx();
	public void setLimitPx(Double Px);

	/**
	 * @return the side(typically tag 54)
	 */
	@PID(16)
	public Side getSide();
	public void setSide(Side Side);

	/**
	 * @return the side(typically tag 40)
	 */
	@PID(17)
	public OrderType getOrderType();
	public void setOrderType(OrderType OrdType);

	/**
	 * @return the time in force(typically tag 59)
	 */
	@PID(18)
	public TimeInForce getTimeInForce();
	public void setTimeInForce(TimeInForce TimeInForce);

	/**
	 * @return custom pass through fields. The key is usually a fix tag
	 */
	@PID(19)
	public Map<Integer, String> getPassThruTags();
	public void setPassThruTags(Map<Integer, String> passThruTags);

	// ---STATE---

	/**
	 * @return the total executed quantity
	 */
	@PID(20)
	public int getCumQty();
	public void setCumQty(int qty);

	/**
	 * @return the total executed value
	 */
	@PID(21)
	public Double getExecValue();
	public void setExecValue(Double val);

	/**
	 * @return status of the order. See {@link OrdStatus#getIntMask()}
	 */
	@PID(22)
	public int getOrdStatus();
	public void setOrdStatus(int status);

	/**
	 * @return time this order was last updated
	 */
	@PID(23)
	public DateNanos getUpdatedTime();
	public void setUpdatedTime(DateNanos time);

	/**
	 * @return the execution type (typically fix tag )
	 */
	@PID(24)
	public ExecType getExecType();
	public void setExecType(ExecType execType);

	/**
	 * @return client order id (typically fix tag 11)
	 */
	@PID(25)
	public String getRequestId();
	public void setRequestId(String requestId);

	/**
	 * @return the symbol suffix (typically tag 65)
	 */
	@PID(26)
	public String getSymbolSfx();
	public void setSymbolSfx(String symbol);

	/**
	 * @return the currency on price and value fields(typically fix tag 15)
	 */
	@PID(28)
	public String getCurrency();
	public void setCurrency(String currency);

	/**
	 * @return the text (typically fix tag 58)
	 */
	@PID(29)
	public String getText();
	public void setText(String text);

	@PID(95)
	public int getRestatementReason();
	public void setRestatementReason(int restatementReason);

}
