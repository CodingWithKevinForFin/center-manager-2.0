package app.fix42;

import com.sjls.algos.eo.common.CancelRejectedMsg;
import com.sjls.algos.eo.common.ExecutionReportMsg;

public interface IFIX42Client {
	public void onExecutionReport(ExecutionReportMsg msg);
	public void onCancelRejected(CancelRejectedMsg msg);
}
