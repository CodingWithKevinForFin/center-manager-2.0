package com.f1.ami.web.centermanager.utils;

import java.util.HashSet;
import java.util.Set;

import com.f1.utils.CH;

public interface AmiCenterEntityConsts {
	//entities, already in AmiCenterGraphNode

	//editor option names:
	//trigger
	public static final String OPTION_NAME_TRIGGER_NAME = "Trigger Name";
	public static final String OPTION_NAME_TRIGGER_TYPE = "Trigger Type";
	public static final String OPTION_NAME_TRIGGER_ON = "ON";
	public static final String OPTION_NAME_TRIGGER_PRIORITY = "PRIORITY";
	//index
	public static final String OPTION_NAME_INDEX_NAME = "Index Name";
	public static final String OPTION_NAME_INDEX_ON = "ON";
	public static final String OPTION_NAME_INDEX_CONFIG = "Index Configuration";
	public static final String OPTION_NAME_INDEX_CONSTRAINT = "CONSTRAINT";
	public static final String OPTION_NAME_INDEX_AUTOGEN = "AUTOGEN";
	//trigger type specific option names://TODO:

	public static final Set<String> TRIGGER_CONFIG_OPTIONS = CH.s(OPTION_NAME_TRIGGER_NAME, OPTION_NAME_TRIGGER_TYPE, OPTION_NAME_TRIGGER_ON, OPTION_NAME_TRIGGER_PRIORITY);
	public static final Set<String> INDEX_CONFIG_OPTIONS = CH.s(OPTION_NAME_INDEX_NAME, OPTION_NAME_INDEX_ON, OPTION_NAME_INDEX_CONFIG, OPTION_NAME_INDEX_CONSTRAINT);

	//general
	public static final String GROUP_NAME_REQUIRED_FIELD = "GROUP_REQUIRED";
	public static final String GROUP_NAME_SKIP_ONFIELDCHANGED = "GROUP_SKIP_ONFIELDCHANGED";
	//Null
	public static final String ENTITY_TYPE_NULL = "<NULL>";
	public static final short ENTITY_TYPE_CODE_NULL = -1;

	//********TRIGGERS***************************************************
	public static final String TRIGGER_TYPE_AMISCRIPT = "AMISCRIPT";
	public static final String TRIGGER_TYPE_AGGREGATE = "AGGREGATE";
	public static final String TRIGGER_TYPE_PROJECTION = "PROJECTION";
	public static final String TRIGGER_TYPE_JOIN = "JOIN";
	public static final String TRIGGER_TYPE_DECORATE = "DECORATE";
	public static final String TRIGGER_TYPE_RELAY = "RELAY";
	//CODES
	public static final short TRIGGER_TYPE_CODE_NULL = 0;
	public static final short TRIGGER_TYPE_CODE_AMISCRIPT = 1;
	public static final short TRIGGER_TYPE_CODE_AGGREGATE = 2;
	public static final short TRIGGER_TYPE_CODE_PROJECTION = 3;
	public static final short TRIGGER_TYPE_CODE_JOIN = 4;
	public static final short TRIGGER_TYPE_CODE_DECORATE = 5;
	public static final short TRIGGER_TYPE_CODE_RELAY = 6;

	//JOIN TRIGGERS
	public static final short TRIGGER_JOIN_TYPE_CODE_LEFT = 0;
	public static final short TRIGGER_JOIN_TYPE_CODE_RIGHT = 1;
	public static final short TRIGGER_JOIN_TYPE_CODE_INNER = 2;
	public static final short TRIGGER_JOIN_TYPE_CODE_OUTER = 3;
	public static final short TRIGGER_JOIN_TYPE_CODE_LEFT_ONLY = 4;
	public static final short TRIGGER_JOIN_TYPE_CODE_RIGHT_ONLY = 5;
	public static final short TRIGGER_JOIN_TYPE_CODE_OUTER_ONLY = 6;

	public static final String TRIGGER_JOIN_TYPE_LEFT = "LEFT";
	public static final String TRIGGER_JOIN_TYPE_RIGHT = "RIGHT";
	public static final String TRIGGER_JOIN_TYPE_INNER = "INNER";
	public static final String TRIGGER_JOIN_TYPE_OUTER = "OUTER";
	public static final String TRIGGER_JOIN_TYPE_LEFT_ONLY = "LEFT ONLY";
	public static final String TRIGGER_JOIN_TYPE_RIGHT_ONLY = "RIGHT ONLY";
	public static final String TRIGGER_JOIN_TYPE_OUTER_ONLY = "OUTER ONLY";
	//*******************************************************************

	//********TIMERS*****************************************************
	public static final String TIMER_TYPE_AMISCRIPT = "AMISCRIPT";
	//CODES
	public static final short TIMER_TYPE_CODE_AMISCRIPT = 1;
	//*******************************************************************

	//********PROCEDURES*****************************************************
	public static final String PROCEDURE_TYPE_AMISCRIPT = "AMISCRIPT";
	//CODES
	public static final short PROCEDURE_TYPE_CODE_AMISCRIPT = 1;
	//*******************************************************************

	//********INDEXES*****************************************************
	public static final String INDEX_CONSTRAINT_TYPE_NONE = "NONE";
	public static final String INDEX_CONSTRAINT_TYPE_UNIQUE = "UNIQUE";
	public static final String INDEX_CONSTRAINT_TYPE_PRIMARY = "PRIMARY";
	//CODES
	public static final short INDEX_CONSTRAINT_TYPE_CODE_NONE = 0;
	public static final short INDEX_CONSTRAINT_TYPE_CODE_UNIQUE = 1;
	public static final short INDEX_CONSTRAINT_TYPE_CODE_PRIMARY = 2;

	//AUTOGEN
	public static final String AUTOGEN_TYPE_NONE = "NONE";
	public static final String AUTOGEN_TYPE_RAND = "RAND";
	public static final String AUTOGEN_TYPE_INC = "INC";

	public static final short AUTOGEN_TYPE_CODE_NONE = 0;
	public static final short AUTOGEN_TYPE_CODE_RAND = 1;
	public static final short AUTOGEN_TYPE_CODE_INC = 2;

	public static final short INDEX_TYPE_CODE_SORT = 0;
	public static final short INDEX_TYPE_CODE_HASH = 1;
	public static final short INDEX_TYPE_CODE_SERIES = 2;
	public static final String INDEX_TYPE_SORT = "SORT";
	public static final String INDEX_TYPE_HASH = "HASH";
	public static final String INDEX_TYPE_SERIES = "SERIES";
	//*******************************************************************

	//********TABLES*****************************************************
	public static final String PERSIST_ENGINE_TYPE_NONE = "<NONE>";
	public static final String PERSIST_ENGINE_TYPE_FAST = "FAST";
	public static final String PERSIST_ENGINE_TYPE_HISTORICAL = "HISTORICAL";
	public static final String PERSIST_ENGINE_TYPE_TEXT = "TEXT";
	//CODES
	public static final short PERSIST_ENGINE_TYPE_CODE_NONE = 0;
	public static final short PERSIST_ENGINE_TYPE_CODE_FAST = 1;
	public static final short PERSIST_ENGINE_TYPE_CODE_HISTORICAL = 2;
	public static final short PERSIST_ENGINE_TYPE_CODE_TEXT = 3;

	public static final String ON_UNDEF_COLUMN_OPTION_REJECT = "REJECT";
	public static final String ON_UNDEF_COLUMN_OPTION_IGNORE = "IGNORE";
	public static final String ON_UNDEF_COLUMN_OPTION_ADD = "ADD";

	public static final short ON_UNDEF_COLUMN_OPTION_CODE_REJECT = 1;
	public static final short ON_UNDEF_COLUMN_OPTION_CODE_IGNORE = 2;
	public static final short ON_UNDEF_COLUMN_OPTION_CODE_ADD = 3;

	//*******************************************************************

	//mode: ADD/EDIT
	public static final byte ADD = 0;
	public static final byte EDIT = 1;

	//consts string
	public static final String REQUIRED_FEILD_WARNING = "[MISSING REQUIRED FIELD]";

	//masks
	public static final int HAS_OFTYPE = 1 << 0;
	public static final int HAS_ON = 1 << 1;
	public static final int HAS_PRIORITY = 1 << 2;
	public static final int HAS_ALL_SCHEMA_NODE = HAS_OFTYPE | HAS_ON | HAS_PRIORITY;

	//HTML
	public static final String REQUIRED_FIELD_ANNOTATION_HTML = "<span style=\"color: red;\"> *</span>";
	public static final String REQUIRED_FEILD_WARNING_HTML = "<span style=\"color: red;\">[MISSING REQUIRED FIELD]&nbsp;</span>";
	public static final String CHANGED_FIELD_ANNOTATION_HTML = "<span style=\"color: blue; font-style: italic;\">";

	public static final String SQL_KEYWORD_HTML = "<span class=\"ace_keyword ace_sql\">";

	public static final Set<String> SQL_KEYWORDS = new HashSet<String>(CH.l("CREATE", "TRIGGER", "OFTYPE", "ON", "USE", "PRIORITY"));

	//backend config
	public static final int DEFAULT_PORTLET_WIDTH = 550;
	public static final int DEFAULT_PORTLET_HEIGHT = 520;
	public static final boolean DEFAULT_ALLOW_SQL_INJECTION = Boolean.FALSE;
	public static final String DEFAULT_DS_NAME = "AMI";
	public static final byte DEFAULT_PERMISSION = (byte) 15;
	public static final int DEFAULT_LIMIT = 10000;
	public static final int DEFAULT_TIMEOUT = 60000;

	//LOGGING
	public static final short LOGGING_LEVEL_CODE_ON = 1;
	public static final short LOGGING_LEVEL_CODE_OFF = 2;
	public static final short LOGGING_LEVEL_CODE_VERBOSE = 3;
	public static final String LOGGING_LEVEL_ON = "on";
	public static final String LOGGING_LEVEL_OFF = "off";
	public static final String LOGGING_LEVEL_VERBOSE = "verbose";
	//edit policy
	public static final byte EDIT_POLICY_STRICT = 1;//When using fuzzy, every edit op is treated as drop and recreate
	public static final byte EDIT_POLICY_FUZZY = 2;
}
