package com.f1.refdataclient;

import com.f1.container.RequestInputPort;
import com.f1.container.RequestOutputPort;
import com.f1.container.impl.BasicSuite;
import com.f1.pofo.refdata.Exchange;
import com.f1.pofo.refdata.ExchangeMarketSession;
import com.f1.pofo.refdata.ExchangeSegment;
import com.f1.pofo.refdata.Fundamentals;
import com.f1.pofo.refdata.RefDataInfoMessage;
import com.f1.pofo.refdata.RefDataRequestMessage;
import com.f1.pofo.refdata.Security;
import com.f1.refdata.impl.BasicRefDataManager;

public class RefDataClientSuite extends BasicSuite {

	private RefDataClientDataProcessor dataProcessor;
	private RefDataClientRequestProcessor requestProcessor;
	private RefDataClientResponseProcessor responseProcessor;
	public RequestInputPort<RefDataRequestMessage, RefDataInfoMessage> requestPort;
	public RequestOutputPort<RefDataRequestMessage, RefDataInfoMessage> toServerPort;
	private String stripeId;
	private BasicRefDataManager manager;

	public RefDataClientSuite(String stripeId) {
		this.stripeId = stripeId;
	}
	public RefDataClientSuite(String stripeId, BasicRefDataManager manager) {
		this.stripeId = stripeId;
		this.manager = manager;
	}
	public void init() {
		super.init();
		getContainer().getServices().getGenerator().register(RefDataInfoMessage.class);
		getContainer().getServices().getGenerator().register(RefDataRequestMessage.class);
		getContainer().getServices().getGenerator().register(Fundamentals.class);
		getContainer().getServices().getGenerator().register(Security.class);
		getContainer().getServices().getGenerator().register(Exchange.class);
		getContainer().getServices().getGenerator().register(ExchangeMarketSession.class);
		getContainer().getServices().getGenerator().register(ExchangeSegment.class);
		if (manager != null)
			getContainer().getPartitionController().putState(stripeId, new RefDataClientState(manager));
		dataProcessor = new RefDataClientDataProcessor();
		dataProcessor.bindToPartition(stripeId);
		requestProcessor = new RefDataClientRequestProcessor();
		requestProcessor.bindToPartition(stripeId);
		responseProcessor = new RefDataClientResponseProcessor();
		responseProcessor.bindToPartition(stripeId);

		addChildren(dataProcessor, requestProcessor, responseProcessor);
		wire(requestProcessor.responsePort, responseProcessor, false);
		wire(responseProcessor.refdataOutput, dataProcessor, false);
		requestPort = exposeInputPort(requestProcessor);
		requestPort.setName("requestPort");
		toServerPort = exposeOutputPort(requestProcessor.serverrequestport);
		toServerPort.setConnectionOptional(true);
		toServerPort.setName("toServerPort");
	}
}
