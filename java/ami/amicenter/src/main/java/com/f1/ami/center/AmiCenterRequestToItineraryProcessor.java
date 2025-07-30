package com.f1.ami.center;

import com.f1.ami.amicommon.msg.AmiCenterRequest;
import com.f1.base.Message;
import com.f1.base.ObjectGeneratorForClass;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;

public class AmiCenterRequestToItineraryProcessor<T extends AmiCenterRequest, T2 extends AmiCenterItinerary<T>> extends AmiCenterRequestProcessor<T, State, Message> {

	final private Class<T2> itineraryType;
	private ObjectGeneratorForClass<T2> itineraryGenerator;

	public AmiCenterRequestToItineraryProcessor(Class<T> type, Class<T2> itineraryType) {
		super(type, State.class, Message.class);
		this.itineraryType = itineraryType;
	}
	@Override
	public void init() {
		super.init();
		this.itineraryGenerator = getGenerator(this.itineraryType);
	}
	@Override
	protected Message processRequest(RequestMessage<T> action, State state, ThreadScope threadScope) throws Exception {
		startItinerary(itineraryGenerator.nw(), action);
		return null;
	}

}
