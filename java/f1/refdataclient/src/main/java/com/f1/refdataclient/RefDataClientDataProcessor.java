package com.f1.refdataclient;

import java.util.Map;

import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.refdata.Exchange;
import com.f1.pofo.refdata.Fundamentals;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.Security;
import com.f1.refdata.impl.BasicRefDataManager;

public class RefDataClientDataProcessor extends BasicProcessor<RefDataInfoMessage, RefDataClientState> {

	public RefDataClientDataProcessor() {
		super(RefDataInfoMessage.class, RefDataClientState.class);
	}

	@Override
	public void processAction(RefDataInfoMessage action, RefDataClientState state, ThreadScope threadScope) throws Exception {
		BasicRefDataManager manager = state.manager;
		{
			final Map<Integer, Exchange> m = action.getExchanges();
			if (m != null)
				for (Map.Entry<Integer, Exchange> e : m.entrySet())
					if (e.getValue() == null)
						manager.removeExchange(e.getKey());
					else
						manager.addExchange(e.getValue());
		}
		{
			final Map<Integer, Fundamentals> m = action.getFundamentals();
			if (m != null)
				for (Map.Entry<Integer, Fundamentals> e : m.entrySet())
					if (e.getValue() == null)
						manager.removeFundamentals(e.getKey());
					else
						manager.addFundamentals(e.getValue());
		}
		{
			final Map<Integer, Security> m = action.getSecurities();
			if (m != null)
				for (Map.Entry<Integer, Security> e : m.entrySet())
					if (e.getValue() == null)
						manager.removeSecurity(e.getKey());
					else
						manager.addSecurity(e.getValue());
		}
	}
}
