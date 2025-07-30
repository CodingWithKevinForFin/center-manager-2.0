package app;

import java.util.Date;
import java.util.List;

import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.POEventType;
import com.sjls.algos.eo.common.ParentOrderMsg;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;
import com.sjls.algos.eo.utils.DateUtils;

/**
 * 
 * @author Olu Emuleomo
 * @date 2010-04-14
 * 
 */
public class CancelParentOrderEvent implements IParentOrderEvent {
	final ParentOrderMsg m_msg;

	public CancelParentOrderEvent(ParentOrderMsg msg) {
		m_msg = msg;
	}

	public POEventType getEventType() {
		return POEventType.CANCEL; // always, since this is a Cancel event!
	}

	public String getBlockID() {
		return m_msg.blockID;
	}
	public String getTicker() {
		return m_msg.ticker;
	}
	public long getBlockQty() {
		return m_msg.blockQty;
	}
	public double getLimitPrice() {
		return m_msg.limitPrice;
	}
	public String getSecurityID() {
		return m_msg.securityID;
	}
	public String getPM() {
		return m_msg.PM;
	}
	public String getPMStrategyCode() {
		return m_msg.PMStrategyCode;
	}
	public String getPMGroup() {
		return m_msg.PMGroup;
	}
	public String getPMProduct() {
		return m_msg.PMProduct;
	}
	public String getPMSubProduct() {
		return m_msg.PMSubProduct;
	}
	public Side getSide() {
		return m_msg.side;
	}
	public TimeInForce getTimeInForce() {
		return m_msg.timeInForce;
	}
	public Date getShortSettleDate() {
		return m_msg.shortSettleDate;
	}
	public String getDeskID() {
		return m_msg.deskID;
	}
	public Date getBlockGenTime() {
		return m_msg.blockGenTime;
	}
	public String getBorrowLocateString() {
		return m_msg.borrowLocateString;
	}
	public List<String> getRestrictedBrokers() {
		return m_msg.restrictedBrokers;
	}
	public String getPairLinkID() {
		return m_msg.pairLinkID;
	}
	public int getRiskTolerance() {
		return 0;
	}
	public Date getStartTime() {
		return new Date();
	}
	public Date getEndTime() {
		return DateUtils.getUSMarketCloseTime().toDate();
	}

	@Override
	public String toString() {
		return String.format("Event=[%s], Msg=[%s]", getEventType(), m_msg.toString());
	}

	@Override
	/** Returns null!! */
	public IAlgoParams getAlgoParams() {
		return null;
	}

	@Override
	public String getRouteToBrokerID() {
		// TODO Auto-generated method stub
		return null;
	}
}
