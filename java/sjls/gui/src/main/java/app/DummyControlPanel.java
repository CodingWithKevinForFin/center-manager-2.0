package app;

import org.apache.log4j.Logger;

import app.controlpanel.IControlPanel;

import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;

public class DummyControlPanel implements IControlPanel {
	private static Logger m_logger = Logger.getLogger(DummyControlPanel.class);

	public void onParentOrderStatsUpdate(final IParentOrderStatusUpdateMsg msg) {
		m_logger.info("Got IParentOrderStatusUpdateMsg() " + msg.toString());
	}

	@Override
	public void onAlert(final String msg) {
		m_logger.info("onAlert(): " + msg);
	}

}
