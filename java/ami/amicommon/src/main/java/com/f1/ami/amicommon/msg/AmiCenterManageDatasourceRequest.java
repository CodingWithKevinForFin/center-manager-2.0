package com.f1.ami.amicommon.msg;

import com.f1.base.PID;
import com.f1.base.VID;

@VID("F1.VE.MDSQ")
public interface AmiCenterManageDatasourceRequest extends AmiCenterRequest {

	@PID(1)
	public long getId();
	public void setId(long id);

	@PID(2)
	public String getName();
	public void setName(String name);

	@PID(3)
	public String getAdapter();
	public void setAdapter(String adapter);

	@PID(4)
	public String getUrl();
	public void setUrl(String url);

	@PID(5)
	public String getUsername();
	public void setUsername(String username);

	@PID(6)
	public String getPassword();
	public void setPassword(String password);

	@PID(7)
	public String getOptions();
	public void setOptions(String options);

	@PID(8)
	public boolean getDelete();
	public void setDelete(boolean del);

	@PID(9)
	public boolean getEdit();
	public void setEdit(boolean edit);

	@PID(10)
	public String getSelectedName();
	public void setSelectedName(String selName);

	@PID(11)
	public String getRelayId();
	public void setRelayId(String relayId);

	@PID(12)
	public String getPermittedOverrides();
	public void setPermittedOverrides(String permittedOverrides);

	@PID(13)
	public void setSkipTest(boolean booleanValue);
	public boolean getSkipTest();
}
