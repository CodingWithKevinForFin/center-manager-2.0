package app.fix42;

import java.util.Date;

import com.sjls.algos.eo.common.AlgoPrice;
import com.sjls.algos.eo.common.INewOrderRequestMsg;
import com.sjls.algos.eo.common.OrderType;
import com.sjls.algos.eo.common.Side;
import com.sjls.algos.eo.common.TimeInForce;

public class NewOrderSingleMsg {
	private String m_clOrdID;
	private String m_blockID;
	private Side m_side;
	private int m_orderQty;
	private String m_ticker;
	private AlgoPrice m_price;
	private TimeInForce m_timeInForce;
	private String m_broker;
	private String m_requestingPool;

	final private Date m_transactTime = new Date();
	private final String m_venue;

	public NewOrderSingleMsg(final String clOrdID, final INewOrderRequestMsg req) {
		m_clOrdID = clOrdID;
		m_blockID = req.getBlockID();
		m_side = req.getSide();
		m_orderQty = req.getOrderQty();
		m_ticker = req.getTicker();
		m_price = req.getAlgoPrice();
		m_timeInForce = req.getTimeInForce();
		m_broker = req.getBroker();
		m_venue = req.getVenue();
		m_requestingPool = req.getRequestingPool();
	}

	public String getClOrdID() {
		return m_clOrdID;
	}
	public void setClOrdID(String clOrdID) {
		m_clOrdID = clOrdID;
	}
	public String getBlockID() {
		return m_blockID;
	}
	public void setBlockID(String blockID) {
		m_blockID = blockID;
	}

	public Side getSide() {
		return m_side;
	}
	public void setSide(Side side) {
		m_side = side;
	}

	public int getOrderQty() {
		return m_orderQty;
	}
	public void setOrderQty(int orderQty) {
		m_orderQty = orderQty;
	}

	public String getTicker() {
		return m_ticker;
	}
	public void setTicker(String ticker) {
		m_ticker = ticker;
	}

	public AlgoPrice getAlgoPrice() {
		return m_price;
	}
	public void setAlgoPrice(AlgoPrice price) {
		m_price = price;
	}

	public TimeInForce getTimeInForce() {
		return m_timeInForce;
	}
	public void setTimeInForce(TimeInForce timeInForce) {
		m_timeInForce = timeInForce;
	}

	public String getBroker() {
		return m_broker;
	}

	public String getVenue() {
		return m_venue;
	}

	public String getRequestingPool() {
		return m_requestingPool;
	}
	public void setRequestingPool(String requestingPool) {
		m_requestingPool = requestingPool;
	}

	public Date getTransactTime() {
		return (Date) m_transactTime.clone();
	}
}
