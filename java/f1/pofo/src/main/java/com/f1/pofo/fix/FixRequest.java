package com.f1.pofo.fix;

import com.f1.base.PID;
import com.f1.base.VID;

/**
 * fix order request, such as a new order, replace or cancel request.
 */
@VID("F1.FX.RQ")
public interface FixRequest extends FixMsg {

	@PID(10)
	public String getRequestId();
	public void setRequestId(String RequestId);

	/**
	 * @return the symbol (typically tag 55)
	 */
	@PID(11)
	public String getSymbol();
	public void setSymbol(String symbol);

	/**
	 * 
	 * @return the symbol suffix (typically tag 65)
	 */
	@PID(12)
	public String getSymbolSfx();
	public void setSymbolSfx(String symbolSfx);

	/**
	 * @return information about the order such as quantity and limit price
	 */
	@PID(13)
	public FixOrderInfo getOrderInfo();
	public void setOrderInfo(FixOrderInfo info);

	/**
	 * 
	 * @return order capacity (typically fix tag 528)
	 */
	@PID(14)
	public String getOrderCapacity();
	public void setOrderCapacity(String orderCapacity);

	/**
	 * @return is locate broker required true=Y,false=N, null=unspecified (typically tag 114)
	 */
	@PID(15)
	public Boolean getLocateBrokerRequired();
	public void setLocateBrokerRequired(Boolean locateBroker);

	/**
	 * @return the optional locate broker (typically tag 5700)
	 */
	@PID(16)
	public String getLocateBroker();
	public void setLocateBroker(String locateBroker);

	/**
	 * @return the optional locate id (typically tag 5701)
	 */
	@PID(17)
	public String getLocateId();
	public void setLocateId(String locateId);

	/**
	 * @return flag for regulator requirement rule 80 A (typically tag 47)
	 */
	@PID(18)
	public String getRule80A();
	public void setRule80A(String orderCapacity);

	/**
	 * @return the exchange destination (typically tag 100)
	 */
	@PID(19)
	public String getDestination();
	public void setDestination(String destination);

	/**
	 * @return identify the originator of this specific message (typically fix tag 50)
	 */
	@PID(20)
	public void setSenderSubId(String string);
	public String getSenderSubId();

	@PID(21)
	public void setOnBehalfOfCompId(String string);
	public String getOnBehalfOfCompId();

}
