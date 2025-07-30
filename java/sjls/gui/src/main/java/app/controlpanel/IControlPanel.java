package app.controlpanel;

import com.sjls.algos.eo.common.IParentOrderStatusUpdateMsg;

public interface IControlPanel {
	public void onParentOrderStatsUpdate(final IParentOrderStatusUpdateMsg msg);

	void onAlert(String msg);
}
