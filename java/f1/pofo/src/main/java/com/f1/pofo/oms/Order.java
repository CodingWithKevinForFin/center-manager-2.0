package com.f1.pofo.oms;

import java.util.Map;

import com.f1.base.DateNanos;
import com.f1.base.PID;
import com.f1.base.PartialMessage;
import com.f1.base.Transient;
import com.f1.base.VID;
import com.f1.base.ValuedListenable;
import com.f1.pofo.fix.OrdStatus;

/**
 * FIXOrder is intended to serve as the state of an order as desired in FIX. Though it contains TWO FixOrderInfo fields, the ideal case is that the Request would contain initial
 * data and the OrderInfo would contain data that can be changed per request Hence .getFixOrderRequest().getOrderInfo() should typically return null in good application code. TODO:
 * This needs to be made simpler
 * 
 * @author adityad
 * 
 */
@VID("F1.FX.OD")
public interface Order extends Revisioned, ValuedListenable, PartialMessage {

	// IDENTIFICATION
	@PID(1)
	public String getRequestId();
	public void setRequestId(String RequestId);

	/**
	 * @return the id which relates parent / slice / child orders
	 */
	@PID(2)
	public String getOrderGroupId();
	public void setOrderGroupId(String id);

	/**
	 * @return true if this is the latest version (no modifications have been made to this revision)
	 */
	@PID(3)
	public boolean getIsLatest();
	public void setIsLatest(boolean isLatest);

	/**
	 * @return the order id known externally by the client (typically tag 37)
	 */
	@PID(4)
	public String getExternalOrderId();
	public void setExternalOrderId(String externalOrderId);

	// ---NON MODIFIABLE FIELDS

	/**
	 * @return the id, as known by the security master for the security this order is for
	 */
	@PID(5)
	public int getSecurityRefId();
	public void setSecurityRefId(int securityRefId);

	/**
	 * @return you user-specific legible security representation
	 */
	@PID(7)
	public String getSecurityID();
	public void setSecurityID(String securityID);

	/**
	 * @return the id source (typically tag 22)
	 */
	@PID(8)
	public int getIDType();
	public void setIDType(int IDType);

	/**
	 * @return the exhange symbol this order is for
	 */
	@PID(9)
	public String getSymbol();
	public void setSymbol(String Symbol);

	/**
	 * @return symbol suffix
	 */
	@PID(10)
	public String getSymbolSfx();
	public void setSymbolSfx(String SymbolSfx);

	/**
	 * 
	 * @return the capacity of the order. (typically tag 528)
	 */
	@PID(11)
	public String getOrderCapacity();
	public void setOrderCapacity(String orderCapacity);

	/**
	 * @return is locate broker required true=Y,false=N, null=unspecified (typically tag 114)
	 */
	@PID(12)
	public Boolean getLocateBrokerRequired();
	public void setLocateBrokerRequired(Boolean locateBroker);

	/**
	 * @return the optional locate broker (typically tag 5700)
	 */
	@PID(13)
	public String getLocateBroker();
	public void setLocateBroker(String locateBroker);

	/**
	 * @return the optional locate id (typically tag 5701)
	 */
	@PID(14)
	public String getLocateId();
	public void setLocateId(String locateId);

	/**
	 * @return flag for regulator requirement rule 80 A
	 */
	@PID(15)
	public String getRule80A();
	public void setRule80A(String orderCapacity);

	/**
	 * @return destination for this order
	 */
	@PID(16)
	public String getDestination();
	public void setDestination(String destination);

	// ---STATE---
	/**
	 * 
	 * @return custom user data associated with this order.
	 */
	@Transient(Transient.PERSIST)
	@PID(17)
	public Object getUserData();
	public void setUserData(Object data);

	/**
	 * @return total number of shares executed
	 */
	@PID(18)
	public int getTotalExecQty();
	public void setTotalExecQty(int totalExecQty);

	/**
	 * @return total value of all shares executed
	 */
	@PID(19)
	public double getTotalExecValue();
	public void setTotalExecValue(double totalExecValue);

	/**
	 * @return status of the order. See {@link OrdStatus#getIntMask()}
	 */
	@PID(20)
	public int getOrderStatus();
	public void setOrderStatus(int status);

	/**
	 * @return last time the order was updated
	 */
	@PID(21)
	public DateNanos getUpdatedTime();
	public void setUpdatedTime(DateNanos time);

	/**
	 * @return time the order was created
	 */
	@PID(22)
	public DateNanos getCreatedTime();
	public void setCreatedTime(DateNanos createdTime);

	/**
	 * @return sessionName the name of the fix session related to this order
	 */
	@PID(23)
	public String getSessionName();
	public void setSessionName(String sessionName);

	/**
	 * @return the original requestId (tag 41)
	 */

	@PID(24)
	public String getOrigRequestId();
	public void setOrigRequestId(String requestId);

	// order Info

	/**
	 * @return total quantity for this order
	 */
	@PID(25)
	public int getOrderQty();
	public void setOrderQty(int OrderQty);

	/**
	 * @return the limit price for this order
	 */
	@PID(26)
	public double getLimitPx();
	public void setLimitPx(double Px);

	/**
	 * @return the side for this order
	 */
	@PID(27)
	public Side getSide();
	public void setSide(Side Side);

	/**
	 * @return the account for this order (typically fix tag 1)
	 */
	@PID(28)
	public String getAccount();
	public void setAccount(String account);

	/**
	 * @return the order type
	 */
	@PID(29)
	public OrderType getOrderType();
	public void setOrderType(OrderType OrdType);

	/**
	 * @return the time in force for this order
	 */
	@PID(30)
	public TimeInForce getTimeInForce();
	public void setTimeInForce(TimeInForce TimeInForce);

	/**
	 * @return custom pass through tags, typically the keys are fix tag string Representations
	 */
	@PID(31)
	public Map<Integer, String> getPassThruTags();
	public void setPassThruTags(Map<Integer, String> passThruTags);

	/**
	 * @return custom text (typically tag 58)
	 */
	@PID(32)
	public String getText();
	public void setText(String string);

	/**
	 * @return execution instructions
	 */
	@PID(33)
	public String getExecInstructions();
	public void setExecInstructions(String execInstructions);

	/**
	 * @return type of order
	 */
	@PID(34)
	public SliceType getSliceType();
	public void setSliceType(SliceType type);

	/**
	 * @return identify the originator of this specific message (typically fix tag 50)
	 */
	@PID(35)
	public String getSenderSubId();
	public void setSenderSubId(String senderSubId);

	/**
	 * @return the currency on price and value fields(typically fix tag 15)
	 */
	@PID(36)
	public String getCurrency();
	public void setCurrency(String currency);

	@PID(37)
	public String getOnBehalfOfCompId();
	public void setOnBehalfOfCompId(String onBehalfofCompId);

	public Order clone();

	@PID(38)
	public void setCxlRejReason(int reason);
	public int getCxlRejReason();

	@PID(39)
	public void setCxlRejResponseTo(int responseTo);
	public int getCxlRejResponseTo();
}
