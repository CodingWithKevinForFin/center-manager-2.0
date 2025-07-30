package com.f1.mktdatasim;

import java.util.Map;
import java.util.TimeZone;

import com.f1.base.Message;
import com.f1.container.RequestOutputPort;
import com.f1.container.ResultMessage;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicProcessor;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.RefDataRequestMessage;
import com.f1.pofo.refdata.Security;
import com.f1.utils.CH;
import com.f1.utils.CachedFile.Cache;
import com.f1.utils.LH;
import com.f1.utils.PropertiesBuilder;
import com.f1.utils.PropertyController;

public class MktDataSimReadConfigProcessor extends BasicProcessor<Message, MktDataSimState> {

	public final RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> refDataRequest = newRequestOutputPort(RefDataRequestMessage.class, RefDataInfoMessage.class);

	public MktDataSimReadConfigProcessor() {
		super(Message.class, MktDataSimState.class);
	}

	@Override
	public void processAction(Message action, MktDataSimState state, ThreadScope threadScope) throws Exception {
		TimeZone timeZone = getServices().getClock().getTimeZone();
		Cache data = state.data;
		try {
			if (data != null && !data.isOld())
				return;
			state.data = data = state.file.getData();
			PropertiesBuilder pb = new PropertiesBuilder();
			pb.readProperties(data.getText());
			PropertyController names = pb.resolveProperties(false).getSubPropertyController("name.");
			for (String k : names.getKeys()) {
				RefDataRequestMessage req = nw(RefDataRequestMessage.class);
				req.setSymbol(k);
				ResultMessage<RefDataInfoMessage> res = refDataRequest.requestWithFuture(req, threadScope).getResult(5000);
				Map<Integer, Security> securities = res.getAction().getSecurities();
				if (CH.isEmpty(securities)) {
					LH.info(log, "Security not found in config file: ", k);
					continue;
				}
				Security security = securities.values().iterator().next();
				int key = security.getSecurityId();
				String val = names.getRequired(k);
				MktDataSimNameSettings value = new MktDataSimNameSettings();
				value.parse(key, val, timeZone);
				state.configuration.put(key, value);
				if (state.subscribed.containsKey(key))
					state.subscribed.put(key, value);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error processing file: " + state.file, e);
		}

	}

}
