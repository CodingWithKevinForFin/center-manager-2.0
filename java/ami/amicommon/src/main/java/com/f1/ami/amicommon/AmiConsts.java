package com.f1.ami.amicommon;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.msg.AmiRelayCommandDefMessage;
import com.f1.utils.CH;

public class AmiConsts {
	public static final String PLUGIN_TYPE_DATASOURCE = "DATASOURCE";
	public static final String PLUGIN_TYPE_PROCEDURE = "PROCEDURE";
	public static final String PLUGIN_TYPE_TRIGGER = "TRIGGER";
	public static final String PLUGIN_TYPE_PERSISTER = "PERSISTER";
	public static final String PLUGIN_TYPE_TIMER = "TIMER";
	public static final String PLUGIN_TYPE_DBO = "DBO";

	public static final int DEFAULT = -2;
	public static final int NO_LIMIT = -1; // we should use this
	public static int COMMAND_CALLBACK_USER_LOGIN = AmiRelayCommandDefMessage.CALLBACK_USER_LOGIN;
	public static int COMMAND_CALLBACK_USER_LOGOUT = AmiRelayCommandDefMessage.CALLBACK_USER_LOGOUT;
	public static int COMMAND_CALLBACK_USER_CLICK = AmiRelayCommandDefMessage.CALLBACK_USER_CLICK;
	public static int COMMAND_CALLBACK_NOW = AmiRelayCommandDefMessage.CALLBACK_NOW;
	public static final Set<String> PERMITTED_DS_OVERRIDE_OPTIONS = CH.s(new LinkedHashSet<String>(), "URL", "USERNAME", "PASSWORD", "URL", "OPTIONS", "RELAY");
	public static final String DATASOURCE_ADAPTER_NAME_AMI = "__AMI";
	public static final String DATASOURCE_NAME_AMI = "AMI";
	//	public static final String TABLE_PARAM_PARAMS = "!params";
	public static final String TABLE_PARAM_DATA = "!data";
	public static final String TABLE_PARAM_ID = "D";//AMI ID
	public static final String TABLE_PARAM_M = "M";//Modified on
	public static final String TABLE_PARAM_W = "W";//Now
	public static final String TABLE_PARAM_C = "C";//Created on
	public static final String TABLE_PARAM_V = "V";//Revision
	public static final String TABLE_PARAM_T = "T";//Table Name
	public static final String TABLE_PARAM_I = "I";//ID
	public static final String TABLE_PARAM_E = "E";//Expired Time
	public static final String TABLE_PARAM_P = "P";//Application 
	public static final String TABLE_PARAM_CENTER = "A";//Center name

	public static final String PARAM_COMMAND_NAME = "NA";
	public static final String PARAM_COMMAND_ID = "ID";
	public static final String PARAM_COMMAND_HELP = "HP";
	public static final String PARAM_COMMAND_LEVEL = "L";
	public static final String PARAM_COMMAND_AMISCRIPT = "AmiScript";
	public static final String PARAM_COMMAND_ARGUMENTS = "AR";
	public static final String PARAM_COMMAND_WHERE = "WH";
	public static final String PARAM_COMMAND_FILTER = "FL";
	public static final String PARAM_COMMAND_PRIORITY = "PR";
	public static final String PARAM_COMMAND_ENABLED = "N";
	public static final String PARAM_COMMAND_STYLE = "S";
	public static final String PARAM_COMMAND_SELECT_MODE = "SM";
	public static final String PARAM_COMMAND_FIELDS = "F";
	public static final String PARAM_COMMAND_CALLBACKS = "CB";

	public static final String PARAM_CONNECTION_PLUGINS = "PL";
	public static final String PARAM_CONNECTION_MACHINEID = "MA";
	public static final String PARAM_CONNECTION_APPID = "AI";
	public static final String PARAM_CONNECTION_REMOTE_PORT = "RP";
	public static final String PARAM_CONNECTION_REMOTE_HOST = "RH";
	public static final String PARAM_CONNECTION_OPTIONS = "O";
	public static final String PARAM_CONNECTION_ID = "CI";
	public static final String PARAM_CONNECTION_TIME = "CT";
	public static final String PARAM_CONNECTION_RELAY_ID = "RI";
	public static final String PARAM_CONNECTION_MESSAGESCOUNT = "MC";
	public static final String PARAM_CONNECTION_ERRORSCOUNT = "EC";

	public static final String PARAM_RELAY_MACHINE_UID = "MachineUid";
	public static final String PARAM_RELAY_PROCESS_UID = "ProcessUid";
	public static final String PARAM_RELAY_START_TIME = "StartTime";
	public static final String PARAM_RELAY_SERVER_PORT = "ServerPort";
	public static final String PARAM_RELAY_RELAY_ID = "RelayId";
	public static final String PARAM_RELAY_HOSTNAME = "Hostname";
	public static final String PARAM_RELAY_CONNECTION_TIME = "ConnectTime";

	public static final String PARAM_DATASOURCE_URL = "UR";
	public static final String PARAM_DATASOURCE_PW = "PW";
	public static final String PARAM_DATASOURCE_PASSWORD = "Password";
	public static final String PARAM_DATASOURCE_PERMITTED_OVERRIDES = "PermittedOverrides";
	public static final String PARAM_DATASOURCE_ADAPTER = "AD";
	public static final String PARAM_DATASOURCE_NAME = "NM";
	public static final String PARAM_DATASOURCE_OPTIONS = "OP";
	public static final String PARAM_DATASOURCE_USER = "US";
	public static final String PARAM_DATASOURCE_RELAY_ID = "RelayId";
	public static final String PARAM_PROPERTY_VALUE = "VL";

	public static final String PARAM_STATS_TIME = "Time";
	public static final String PARAM_STATS_USED_MEMORY = "UsedMemory";
	public static final String PARAM_STATS_MAX_MEMORY = "MaxMemory";
	public static final String PARAM_STATS_POST_GC_USED_MEMORY = "PostGcUsedMemory";
	public static final String PARAM_STATS_RUNNING_THREADS = "RunningThreads";
	public static final String PARAM_STATS_ROWS = "Rows";
	public static final String PARAM_STATS_EVENTS = "Events";
	public static final String PARAM_STATS_QUERIES = "Queries";
	public static final String PARAM_STATS_UNIQUE_USERS = "UniqueUsers";
	public static final String PARAM_STATS_MAX_USERS = "MaxUsers";

	public static final String PARAM_OBJECT_USER = "U";

	public static final String TYPE_COMMAND = "__COMMAND";
	public static final String TYPE_CONNECTION = "__CONNECTION";
	public static final String TYPE_DATASOURCE = "__DATASOURCE";
	public static final String TYPE_DATASOURCE_TYPE = "__DATASOURCE_TYPE";
	public static final String TYPE_RESOURCE = "__RESOURCE";
	public static final String TYPE_TABLE = "__TABLE";
	public static final String TYPE_RELAY = "__RELAY";
	public static final String TYPE_COLUMN = "__COLUMN";
	public static final String TYPE_STATS = "__STATS";
	public static final String TYPE_INDEX = "__INDEX";
	public static final String TYPE_TRIGGER = "__TRIGGER";
	public static final String TYPE_DBO = "__DBO";
	public static final String TYPE_TIMER = "__TIMER";
	public static final String TYPE_PLUGIN = "__PLUGIN";
	public static final String TYPE_PROCEDURE = "__PROCEDURE";
	public static final String TYPE_PROPERTY = "__PROPERTY";
	public static final String TYPE_CENTER = "__CENTER";
	public static final String TYPE_REPLICATION = "__REPLICATION";
	public static final Map<String, String> RESERVED_TYPES = new HashMap<String, String>();
	public static final Map<String, String> RESERVED_PARAMS = new HashMap<String, String>();

	public static final String SERVICE_SERVICE_RESOLVER = "service.resolver";
	public static final String SERVICE_ENCRYPTER = "AMI_ENCRYPTER";
	public static final String SERVICE_AUTH = "AUTH";
	public static final String SERVICE_AUTH_JDBC = "AUTH_JDBC";

	static {
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_PLUGINS, "Plugins");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_MACHINEID, "Machine Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_APPID, "App Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_REMOTE_PORT, "Remote Port");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_REMOTE_HOST, "Remote Host");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_OPTIONS, "Options");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_ID, "Connection Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_TIME, "Time");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_RELAY_ID, "Relay Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_MESSAGESCOUNT, "Messages Count");
		RESERVED_PARAMS.put(AmiConsts.PARAM_CONNECTION_ERRORSCOUNT, "Errors Count");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_NAME, "Title");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_ID, "Command Id");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_HELP, "Help");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_LEVEL, "Level");
		RESERVED_PARAMS.put(AmiConsts.PARAM_OBJECT_USER, "User");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_AMISCRIPT, "AmiScript");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_ARGUMENTS, "Arguments");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_WHERE, "Where");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_FILTER, "Filter");
		RESERVED_PARAMS.put(AmiConsts.PARAM_COMMAND_CALLBACKS, "Callbacks");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_ADAPTER, "Adapter");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_PW, "Pw");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_PASSWORD, "Password");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_PERMITTED_OVERRIDES, "Permitted Overrides");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_URL, "URL");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_NAME, "Name");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_OPTIONS, "Options");
		RESERVED_PARAMS.put(AmiConsts.PARAM_DATASOURCE_USER, "Username");
		RESERVED_PARAMS.put(AmiConsts.PARAM_PROPERTY_VALUE, "Value");

		RESERVED_TYPES.put(AmiConsts.TYPE_COMMAND, "Command");
		RESERVED_TYPES.put(AmiConsts.TYPE_CONNECTION, "Connection");
		RESERVED_TYPES.put(AmiConsts.TYPE_DATASOURCE, "Database");
		RESERVED_TYPES.put(AmiConsts.TYPE_PROPERTY, "Property");
	}

	public static final String TYPE_NAME_STRING = "String";
	public static final String TYPE_NAME_LONG = "Long";
	public static final String TYPE_NAME_INTEGER = "Integer";
	public static final String TYPE_NAME_BYTE = "Byte";
	public static final String TYPE_NAME_SHORT = "Short";
	public static final String TYPE_NAME_DOUBLE = "Double";
	public static final String TYPE_NAME_FLOAT = "Float";
	public static final String TYPE_NAME_BOOLEAN = "Boolean";
	public static final String TYPE_NAME_UTC = "UTC";
	public static final String TYPE_NAME_UTCN = "UTCN";
	public static final String TYPE_NAME_BINARY = "Binary";
	public static final String TYPE_NAME_ENUM = "Enum";
	public static final String TYPE_NAME_CHAR = "Character";
	public static final String TYPE_NAME_BIGINT = "BigInteger";
	public static final String TYPE_NAME_BIGDEC = "BigDecimal";
	public static final String TYPE_NAME_COMPLEX = "Complex";
	public static final String TYPE_NAME_UUID = "UUID";

	public static final char RESERVED_PARAM_AMIID = 'D';//AMI ID
	public static final char RESERVED_PARAM_MODIFIED_ON = 'M';//Modified on
	public static final char RESERVED_PARAM_NOW = 'W';//Now
	public static final char RESERVED_PARAM_CREATED_ON = 'C';//Created on
	public static final char RESERVED_PARAM_REVISION = 'V';//Revision
	public static final char RESERVED_PARAM_TYPE = 'T';//Type
	public static final char RESERVED_PARAM_ID = 'I';//ID
	public static final char RESERVED_PARAM_EXPIRED = 'E';//Expired Time
	public static final char RESERVED_PARAM_APPLICATION = 'P';//Application 

	// Style abbreviations 
	public static final String STYLE_BG = "bg"; // background
	public static final String STYLE_CL = "cl"; // color
	public static final String STYLE_BDR = "bdr"; // border
	public static final String STYLE_HT = "ht"; // height
	public static final String STYLE_WD = "wd"; // width
	public static final String STYLE_SZ = "sz"; // size
	public static final String STYLE_FLD = "fld"; // field
	public static final String STYLE_BTN = "btn"; // button
	public static final String STYLE_VT = "vt"; // vertical
	public static final String STYLE_HZ = "hz"; // horizontal
	public static final String STYLE_LF = "lf"; // left
	public static final String STYLE_RT = "rt"; // right
	public static final String STYLE_TP = "tp"; // top
	public static final String STYLE_BTM = "btm"; // bottom
	public static final String STYLE_PD = "pd"; // padding
	public static final String STYLE_PX = "px"; // pixels
	public static final String STYLE_LBL = "lbl"; // label
	public static final String STYLE_GR = "gr"; // graph
	public static final String STYLE_GRD = "grd"; // grid
	public static final String STYLE_RD = "rd"; // radial
	public static final String STYLE_LYR = "lyr"; // layer
	public static final String STYLE_LGD = "lgd"; // legend
	public static final String STYLE_POS = "pos"; // position
	public static final String STYLE_NM = "nm"; // name
	public static final String STYLE_SCR = "scr"; // scroll
	public static final String STYLE_WIN = "win"; // window
	public static final String STYLE_USR = "usr"; // user
	public static final String STYLE_FML = "fml"; // formula

	public static final String NONULL = "NoNull";
	public static final String COMPACT = "Compact";
	public static final String ASCII = "Ascii";
	public static final String BITMAP = "BITMAP";
	public static final String ONDISK = "OnDisk";
	public static final String CACHE = "Cache";
	public static final String NOBROADCAST = "NoBroadcast";

	public static final String PASSWORD_KEYWORD = "PASSWORD";

	public static final Map<String, String> NONULL_OPTIONS = CH.m(NONULL, "true");

}
