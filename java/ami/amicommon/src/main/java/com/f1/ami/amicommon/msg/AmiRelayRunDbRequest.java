package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VA.RRDBR")
public interface AmiRelayRunDbRequest extends AmiRelayRequest {

	@PID(1)
	void setDsAdapter(String adap);
	String getDsAdapter();

	@PID(2)
	void setDsPassword(String pass);
	String getDsPassword();

	@PID(3)
	void setDsUrl(String url);
	String getDsUrl();

	@PID(4)
	void setDsOptions(String options);
	String getDsOptions();

	@PID(5)
	void setDsUsername(String username);
	String getDsUsername();

	@PID(6)
	void setDsName(String name);
	String getDsName();

	@PID(7)
	void setClientRequest(AmiCenterQueryDsRequest request);
	AmiCenterQueryDsRequest getClientRequest();

	@PID(8)
	void setTimeoutMs(int timeout);
	int getTimeoutMs();

	@PID(9)
	void setDsRelayId(String relayId);
	String getDsRelayId();

	@PID(10)
	void setDsAmiId(long dsAmiId);
	long getDsAmiId();

}
