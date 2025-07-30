package com.larkinpoint.analytics;

import com.f1.container.RequestMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;
import com.f1.povo.standard.ObjectMessage;
import com.f1.povo.standard.TextMessage;
import com.f1.utils.structs.table.BasicTable;

public class LarkinPointGraphRequestProcessor extends BasicRequestProcessor<TextMessage, LarkinPointState, ObjectMessage> {

	public LarkinPointGraphRequestProcessor(Class<TextMessage> innerActionType, Class<LarkinPointState> stateType, Class<ObjectMessage> responseType) {
		super(TextMessage.class, LarkinPointState.class, ObjectMessage.class);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected ObjectMessage processRequest(RequestMessage<TextMessage> action, LarkinPointState state, ThreadScope threadScope) throws Exception {
		final String index = action.getAction().getText();

		BasicTable table = new BasicTable(new String[] { "qdate", "his", "hers" });

		//TODO: business logic
		table.getRows().addRow(201212, "asdf", "sadef");

		ObjectMessage r = nw(ObjectMessage.class);
		r.setObject(table);
		return r;
	}

}
