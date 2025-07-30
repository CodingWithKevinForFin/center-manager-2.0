package com.f1.ami.web.amiscript;

import java.util.List;

import com.f1.ami.amicommon.msg.AmiCenterPassToRelayRequest;
import com.f1.ami.amicommon.msg.AmiCenterPassToRelayResponse;
import com.f1.base.Password;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;

public class AmiSendEmailFlowControlPause extends FlowControlPause {

	final private int timeout;
	private AmiCenterPassToRelayResponse response;
	private AmiCenterPassToRelayRequest request;
	final private String body;
	final private String subject;
	final private List<String> toList;
	final private String from;
	final private boolean isHtml;
	final private List<String> attachmentNames;
	final private List<byte[]> attachmentData;
	final private Password password;
	final private String username;
	final private String relayIds;

	public AmiSendEmailFlowControlPause(String body, String subject, List<String> toList, String from, boolean isHtml, List<String> attachmentNames, List<byte[]> attachmentData,
			String username, Password password, String relayIds, int timeout, DerivedCellCalculator position) {
		super(position);
		this.body = body;
		this.subject = subject;
		this.toList = toList;
		this.from = from;
		this.isHtml = isHtml;
		this.attachmentNames = attachmentNames;
		this.attachmentData = attachmentData;
		this.timeout = timeout;
		this.password = password;
		this.username = username;
		this.relayIds = relayIds;
	}

	public int getTimeout() {
		return this.timeout;
	}

	public void processResponse(AmiCenterPassToRelayResponse response, Tableset ts) {
		this.response = response;
	}

	@Override
	public Object resume() {
		return super.resume();
	}

	public AmiCenterPassToRelayResponse getCommandResponse() {
		return this.response;
	}
	public AmiCenterPassToRelayRequest getCommandRequest() {
		return this.request;
	}

	public void setRequest(AmiCenterPassToRelayRequest request) {
		this.request = request;
	}

	public String getBody() {
		return body;
	}

	public String getSubject() {
		return subject;
	}

	public List<String> getToList() {
		return toList;
	}

	public String getFrom() {
		return from;
	}

	public boolean getIsHtml() {
		return isHtml;
	}

	public List<String> getAttachmentNames() {
		return attachmentNames;
	}

	public List<byte[]> getAttachmentData() {
		return attachmentData;
	}
	public String getUsername() {
		return username;
	}
	public Password getPassword() {
		return password;
	}

	public String getRelayIds() {
		return this.relayIds;
	}

}
