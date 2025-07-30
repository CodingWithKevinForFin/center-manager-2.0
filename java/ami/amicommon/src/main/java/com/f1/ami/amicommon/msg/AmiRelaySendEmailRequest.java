package com.f1.ami.amicommon.msg;

import java.util.List;

import com.f1.base.PID;
import com.f1.base.Password;
import com.f1.base.VID;

@VID("F1.VE.SEMQ")
public interface AmiRelaySendEmailRequest extends AmiRelayRequest {

	@PID(1)
	public String getBody();
	public void setBody(String body);

	@PID(2)
	public String getSubject();
	public void setSubject(String subject);

	@PID(3)
	public List<String> getToList();
	public void setToList(List<String> toList);

	@PID(4)
	public String getFrom();
	public void setFrom(String from);

	@PID(5)
	public boolean getIsHtml();
	public void setIsHtml(boolean isHtml);

	@PID(6)
	public List<String> getAttachmentNames();
	public void setAttachmentNames(List<String> data);

	@PID(7)
	public List<byte[]> getAttachmentDatas();
	public void setAttachmentDatas(List<byte[]> data);

	@PID(8)
	public void setSendEmailUid(String guid);
	public String getSendEmailUid();

	@PID(9)
	public int getTimeoutMs();
	public void setTimeoutMs(int timeoutMs);

	@PID(10)
	public String getUsername();
	public void setUsername(String username);

	@PID(11)
	public Password getPassword();
	public void setPassword(Password username);
}
