package app;

import org.apache.log4j.Logger;

import app.fix42.IFIX42Client;

import com.sjls.algos.eo.common.CancelRejectedMsg;
import com.sjls.algos.eo.common.ExecutionReportMsg;
import com.sjls.algos.eo.common.IExecutionOptimizer;

public class FIX42Client implements IFIX42Client {
	private static Logger m_logger = Logger.getLogger(FIX42Client.class);

	private final IExecutionOptimizer m_exectnOptimizer;

	public FIX42Client(final IExecutionOptimizer exectnOptimizer) {
		m_exectnOptimizer = exectnOptimizer;
	}

	@Override
	public void onCancelRejected(CancelRejectedMsg msg) {
		m_logger.info(String.format("BlockID %s:, clOrdID=%s. Got CancelRejectedMsg", msg.getBlockID(), msg.getClOrdID()));
		try {
			m_exectnOptimizer.onCancelRejected(msg); // Forward the FIX java object to execution optimizer
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
		}
	}

	@Override
	public void onExecutionReport(final ExecutionReportMsg msg) {
		m_logger.info(String.format("BlockID %s:, clOrdID=%s. Got Execution Report. Status=%s, last_shares=%d, lastPrice=%.4f, cumqty=%d, orderQty=%d",
				msg.getBlockID(), msg.getClOrdID(), msg.getOrderStatus(), msg.getLastShares(), msg.getLastPrice(), msg.getCumQty(), msg.getOrderQty()));
		try {
			m_exectnOptimizer.onExecutionReport(msg); // Forward the FIX java object to execution optimizer
		} catch (Exception e) {
			m_logger.error(e.getMessage(), e);
		}
	}
}
