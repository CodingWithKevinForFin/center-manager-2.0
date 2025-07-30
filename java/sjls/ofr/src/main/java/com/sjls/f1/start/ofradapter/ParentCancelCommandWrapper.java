package com.sjls.f1.start.ofradapter;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.sjls.algos.eo.common.IAlgoParams;
import com.sjls.algos.eo.common.IParentOrderEvent;
import com.sjls.algos.eo.common.LimitPrice;
import com.sjls.algos.eo.common.POEventType;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;

public class ParentCancelCommandWrapper implements IParentOrderEvent {

	String blockId;
	public ParentCancelCommandWrapper(String blockId) {
		this.blockId = blockId;
	}
	@Override
	public POEventType getEventType() {
		return POEventType.CANCEL;
	}

	@Override
	public String getBlockID() {
		return blockId;
	}

	@Override
	public String getTicker() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getBlockQty() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public LimitPrice getLimitPriceFromAlgoParams() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSecurityID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPM() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPMStrategyCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPMGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPMProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPMSubProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Side getSide() {
		return Side.Buy;
	}

	@Override
	public TimeInForce getTimeInForce() {
		return TimeInForce.DAY;
	}

	@Override
	public Date getShortSettleDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDeskID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getBlockGenTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBorrowLocateString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getRestrictedBrokers() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public String getPairLinkID() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public IAlgoParams getAlgoParams() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRouteToBrokerID() {
        // TODO Auto-generated method stub
        return null;
    }

}
