package app.controlpanel;

import java.util.Map;

import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.MapMessage;
import com.f1.utils.SH;

public class SjlsAlertsProcessor extends BasicProcessor<MapMessage, State> {

	private volatile ControlPanel controlPanel;

	public SjlsAlertsProcessor() {
		super(MapMessage.class, State.class);
	}

	@Override
	public void processAction(MapMessage action, State state, ThreadScope threadScope) throws Exception {
		final Map<Object, Object> map = action.getMap();
		final String blockId = (String) map.get("blockID");
		final String msg = (String) map.get("msg");
		this.controlPanel.onAlert(SH.isnt(blockId) ? msg : (blockId + ": " + msg));
	}

	public void setControlPanel(ControlPanel cp) {
		assertNotStarted();
		this.controlPanel = cp;
	}

}
