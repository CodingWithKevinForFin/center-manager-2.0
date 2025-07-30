package com.f1.ami.center;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterGetResourceRequest;
import com.f1.ami.amicommon.msg.AmiCenterGetResourceResponse;
import com.f1.ami.amicommon.msg.AmiCenterResource;
import com.f1.container.RequestMessage;
import com.f1.container.State;
import com.f1.container.ThreadScope;
import com.f1.container.impl.BasicRequestProcessor;

public class AmiCenterResourceProcessor extends BasicRequestProcessor<AmiCenterGetResourceRequest, State, AmiCenterGetResourceResponse> {

	private AmiCenterResourcesManager manager;

	public AmiCenterResourceProcessor(AmiCenterResourcesManager watcher) {
		super(AmiCenterGetResourceRequest.class, State.class, AmiCenterGetResourceResponse.class);
		this.manager = watcher;
	}

	@Override
	protected AmiCenterGetResourceResponse processRequest(RequestMessage<AmiCenterGetResourceRequest> action, State state, ThreadScope threadScope) throws Exception {
		final AmiCenterGetResourceResponse r = nw(AmiCenterGetResourceResponse.class);
		final List<String> paths = action.getAction().getPaths();
		final List<AmiCenterResource> list = new ArrayList<AmiCenterResource>(paths.size());
		for (int i = 0, l = paths.size(); i < l; i++) {
			String path = paths.get(i);
			AmiCenterResource resource = nw(AmiCenterResource.class);
			resource = manager.getResource(path);
			list.add(resource);
		}
		r.setResources(list);
		return r;
	}

}
