package com.f1.ami.amicommon.msg;

import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.base.PID;
import com.f1.base.Password;
import com.f1.base.Transient;
import com.f1.base.VID;
import com.f1.utils.string.Node;

@VID("F1.VE.QDOQ")
public interface AmiCenterQueryDsRequest extends AmiCenterRequest {
	public static byte PERMISSIONS_READ = 1;
	public static byte PERMISSIONS_WRITE = 2;
	public static byte PERMISSIONS_ALTER = 4;
	public static byte PERMISSIONS_EXECUTE = 8;
	public static byte PERMISSIONS_FULL = (byte) (PERMISSIONS_READ | PERMISSIONS_WRITE | PERMISSIONS_ALTER | PERMISSIONS_EXECUTE);
	public static byte PERMISSIONS_READ_WRITE = (byte) (PERMISSIONS_READ | PERMISSIONS_WRITE);

	public static byte TYPE_QUERY = 0;
	public static byte TYPE_SHOW_TABLES = 1;
	public static byte TYPE_UPLOAD = 2;
	public static byte TYPE_PREVIEW = 3;

	public static byte ORIGIN_CMDLINE = 1;
	public static byte ORIGIN_FRONTEND = 2;
	public static byte ORIGIN_JDBC = 3;
	public static byte ORIGIN_NESTED = 4;
	public static byte ORIGIN_SYSTEM = 5;
	public static byte ORIGIN_FRONTEND_SHELL = 6;
	public static byte ORIGIN_RTFEED = 7;
	public static byte ORIGIN_REST = 8;

	public static int USE_DEFAULT_TIMEOUT = AmiConsts.DEFAULT;

	public final static int NO_LIMIT = -1;
	public final static int NO_TIMEOUT = -1;
	public final static int MAX_TIMEOUT = Integer.MAX_VALUE;
	public final static int NO_MINREQUERY = 0;
	public final static int NO_MAXREQUERY = 0;

	@PID(14)
	public byte getType();
	public void setType(byte type);

	@PID(7)
	public int getTimeoutMs();
	public void setTimeoutMs(int timeout);

	@PID(8)
	public boolean getIsTest();
	public void setIsTest(boolean isTest);

	@PID(10)
	public String getQuery();
	public void setQuery(String query);

	@PID(11)
	public void setDatasourceName(String string);
	public String getDatasourceName();

	@PID(12)
	public void setQuerySessionId(long durrationNanos);
	public long getQuerySessionId();

	@PID(13)
	public void setQuerySessionKeepAlive(boolean querySessionKeepAlive);
	public boolean getQuerySessionKeepAlive();

	@PID(3)
	public void setLimit(int limit);
	public int getLimit();

	@PID(4)
	public void setDirectives(Map<String, Object> directives);
	public Map<String, Object> getDirectives();

	@PID(15)
	public void setUploadValues(List<AmiCenterUploadTable> values);
	public List<AmiCenterUploadTable> getUploadValues();

	@PID(16)
	@Transient
	public void setParsedNode(Node node);
	public Node getParsedNode();

	@PID(17)
	@Transient
	public void setUseConcurrency(boolean useConcurrency);
	public boolean getUseConcurrency();

	@PID(18)
	public List<AmiDatasourceTable> getTablesForPreview();
	public void setTablesForPreview(List<AmiDatasourceTable> records);

	@PID(19)
	public int getPreviewCount();
	public void setPreviewCount(int previewCount);

	@PID(20)
	public byte getOriginType();
	public void setOriginType(byte originType);

	@PID(21)
	public void setDatasourceOverrideUrl(String string);
	public String getDatasourceOverrideUrl();

	@PID(22)
	public void setDatasourceOverrideUsername(String string);
	public String getDatasourceOverrideUsername();

	@PID(23)
	public void setDatasourceOverridePassword(Password password);
	public Password getDatasourceOverridePassword();

	@PID(24)
	public void setDatasourceOverridePasswordEnc(String string);
	public String getDatasourceOverridePasswordEnc();

	@PID(25)
	public void setDatasourceOverrideOptions(String string);
	public String getDatasourceOverrideOptions();

	@PID(26)
	public void setDatasourceOverrideRelay(String string);
	public String getDatasourceOverrideRelay();

	@PID(27)
	public void setDatasourceOverrideAdapter(String string);
	public String getDatasourceOverrideAdapter();

	@PID(28)
	public void setPermissions(byte string);
	public byte getPermissions();

	@PID(29)
	public boolean getAllowSqlInjection();
	public void setAllowSqlInjection(boolean allowSqlInjection);

	@PID(30)
	public long getParentProcessId();
	public void setParentProcessId(long parentProcessId);

	@PID(31)
	public Map<String, Object> getSessionVariables();
	public void setSessionVariables(Map<String, Object> sessionVariables);

	@PID(32)
	public Map<String, Class> getSessionVariableTypes();
	public void setSessionVariableTypes(Map<String, Class> sessionVariables);

	@PID(33)
	public boolean getDisableLogging();
	public void setDisableLogging(boolean disableLogging);
}
