package app.controlpanel;

import com.f1.container.ResultMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.povo.standard.MapMessage;

public class SjlsBinDataProcessor extends BasicProcessor<ResultMessage<MapMessage>, State> {

	public SjlsBinDataProcessor() {
		super((Class) ResultMessage.class, State.class);
	}

	@Override
	public void processAction(ResultMessage<MapMessage> action, State state, ThreadScope threadScope) throws Exception {
		// TODO Auto-generated method stub

	}

}
