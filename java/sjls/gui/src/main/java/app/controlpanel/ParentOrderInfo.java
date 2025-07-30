package app.controlpanel;

import java.util.LinkedList;
import java.util.List;

import com.sjls.algos.eo.common.IBinTradeData;
import com.sjls.algos.eo.common.ITradingParams;

/**
 * ParentOrderInfo caches Order information about a parent order in local cache on Rules Engine Control Panel
 * 
 * @author hsingh
 * 
 */
public class ParentOrderInfo {
	private final String m_blockId;
	private final ITradingParams m_tradingParams;
	private final List<IBinTradeData> m_initialPlan;
	private final List<IBinTradeData> m_liveTrades;

	public ParentOrderInfo(final ITradingParams tradingParams, final String blockId, List<IBinTradeData> initialPlan) {
		m_blockId = blockId;
		m_tradingParams = tradingParams;
		m_initialPlan = initialPlan;
		m_liveTrades = new LinkedList<IBinTradeData>();
	}

	public String getBlockId() {
		return m_blockId;
	}

	public List<IBinTradeData> getInitialPlan() {
		return m_initialPlan;
	}

	public List<IBinTradeData> getLiveTrades() {
		return m_liveTrades;
	}
	public ITradingParams getTradingParams() {
		return m_tradingParams;
	}
}
