package com.f1.ami.web.amiscript;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandResponse;
import com.f1.ami.web.AmiWebService;
import com.f1.utils.CH;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class AmiWebCommandResponse implements ToDerivedString {

	final private Map<String, Object> values;
	final private AmiRelayRunAmiCommandResponse response;
	final private AmiRelayRunAmiCommandRequest request;

	public AmiWebCommandResponse(AmiWebService service, AmiRelayRunAmiCommandRequest request, AmiRelayRunAmiCommandResponse response) {
		this.response = response;
		this.request = request;
		if (CH.isntEmpty(response.getParams())) {
			this.values = new LinkedHashMap<String, Object>(response.getParams());
		} else
			this.values = new LinkedHashMap<String, Object>(1);
	}

	public AmiRelayRunAmiCommandResponse getResponse() {
		return this.response;
	}
	public AmiRelayRunAmiCommandRequest getRequest() {
		return this.request;
	}

	public Map<String, Object> getValues() {
		return this.values;
	}

	@Override
	public String toDerivedString() {
		return toDerivedString(new StringBuilder()).toString();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append("CommandResponse-").append(this.request.getCommandUid());
	}

}
