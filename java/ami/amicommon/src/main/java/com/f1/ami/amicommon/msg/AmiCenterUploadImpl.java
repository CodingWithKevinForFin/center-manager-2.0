package com.f1.ami.amicommon.msg;

import java.util.List;
import java.util.Map;

import com.f1.utils.structs.table.derived.TimeoutController;

public class AmiCenterUploadImpl implements AmiCenterUpload {

	final private Map<String, Object> directives;
	final private List<AmiCenterUploadTable> data;
	final private TimeoutController timeout;

	public AmiCenterUploadImpl(Map<String, Object> directives, List<AmiCenterUploadTable> data, TimeoutController timeout) {
		this.directives = directives;
		this.data = data;
		this.timeout = timeout;
	}

	@Override
	public List<AmiCenterUploadTable> getData() {
		return data;
	}

	@Override
	public Map<String, Object> getDirectives() {
		return directives;
	}

	@Override
	public TimeoutController getTimeout() {
		return timeout;
	}

}
