package com.f1.ami.center.table;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.center.AmiCenterProperties;
import com.f1.ami.center.dbo.AmiDboBindingImpl;
import com.f1.ami.center.table.index.AmiIndexMap;
import com.f1.ami.center.table.persist.AmiTablePersister;
import com.f1.ami.center.table.persist.AmiTablePersisterBinding;
import com.f1.ami.center.table.persist.AmiTablePersisterBindingImpl;
import com.f1.ami.center.table.persist.AmiTablePersisterFactory_Text;
import com.f1.ami.center.table.persist.AmiTablePersister_Text;
import com.f1.ami.center.timers.AmiTimerBindingImpl;
import com.f1.ami.center.triggers.AmiServicePlugin;
import com.f1.ami.center.triggers.AmiTrigger;
import com.f1.ami.center.triggers.AmiTriggerBindingImpl;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiTableUtils {

	public static final Comparator<AmiTriggerBindingImpl> TRIGGER_PRIORITY_COMPARATOR = new Comparator<AmiTriggerBindingImpl>() {

		@Override
		public int compare(AmiTriggerBindingImpl o1, AmiTriggerBindingImpl o2) {
			final int r = OH.compare(o1.getPriority(), o2.getPriority());
			return r != 0 ? r : SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getTriggerName(), o2.getTriggerName());
		}
	};
	public static final Comparator<AmiTimerBindingImpl> TIMER_PRIORITY_COMPARATOR = new Comparator<AmiTimerBindingImpl>() {
		@Override
		public int compare(AmiTimerBindingImpl o1, AmiTimerBindingImpl o2) {
			int r = OH.compare(o1.getPriority(), o2.getPriority());
			return r != 0 ? r : SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getTimerName(), o2.getTimerName());
		}

	};
	public static final Comparator<AmiDboBindingImpl> DBO_PRIORITY_COMPARATOR = new Comparator<AmiDboBindingImpl>() {
		@Override
		public int compare(AmiDboBindingImpl o1, AmiDboBindingImpl o2) {
			int r = OH.compare(o1.getPriority(), o2.getPriority());
			return r != 0 ? r : SH.COMPARATOR_CASEINSENSITIVE_STRING.compare(o1.getDboName(), o2.getDboName());
		}

	};
	public static final byte DEFTYPE_SYSTEM = 1;
	public static final byte DEFTYPE_AMI = 2;

	public static final byte DEFTYPE_CONFIG = 3;
	public static final byte DEFTYPE_USER = 4;
	public static final byte DEFTYPE_LEGACY = 5;
	public static final byte[] TRIGGER_TYPES = { AmiTrigger.INSERTING, AmiTrigger.INSERTED, AmiTrigger.UPDATING, AmiTrigger.UPDATED, AmiTrigger.DELETING };

	public static String toStringForDefType(byte type) {
		switch (type) {
			case DEFTYPE_CONFIG:
				return "CONFIG";
			case DEFTYPE_SYSTEM:
				return "SYSTEM";
			case DEFTYPE_USER:
				return "USER";
			case DEFTYPE_AMI:
				return "AMI";
			case DEFTYPE_LEGACY:
				return "LEGACY";
			default:
				throw new NoSuchElementException("deftype: " + type);
		}
	}
	public static String toStringForIndexType(byte b) {
		switch (b) {
			case AmiIndexMap.TYPE_HASH:
				return "HASH";
			case AmiIndexMap.TYPE_SORT:
				return "SORT";
			case AmiIndexMap.TYPE_SERIES:
				return "SERIES";
			default:
				return SH.toString(b);
		}
	}
	public static byte parseIndexType(String sort) {
		if ("HASH".equalsIgnoreCase(sort))
			return (AmiIndexMap.TYPE_HASH);
		else if ("SORT".equalsIgnoreCase(sort))
			return (AmiIndexMap.TYPE_SORT);
		else if ("SERIES".equalsIgnoreCase(sort))
			return (AmiIndexMap.TYPE_SERIES);
		else
			return -1;
	}
	public static boolean isUserDefined(byte defType) {
		return defType >= DEFTYPE_CONFIG;
	}
	public static String toStringForDataType(byte paramType) {
		switch (paramType) {
			case AmiDataEntity.PARAM_TYPE_ENUM3:
				return AmiConsts.TYPE_NAME_ENUM;
			case AmiTable.TYPE_LONG:
				return AmiConsts.TYPE_NAME_LONG;
			case AmiTable.TYPE_INT:
				return AmiConsts.TYPE_NAME_INTEGER;
			case AmiTable.TYPE_SHORT:
				return AmiConsts.TYPE_NAME_SHORT;
			case AmiTable.TYPE_BYTE:
				return AmiConsts.TYPE_NAME_BYTE;
			case AmiTable.TYPE_DOUBLE:
				return AmiConsts.TYPE_NAME_DOUBLE;
			case AmiTable.TYPE_FLOAT:
				return AmiConsts.TYPE_NAME_FLOAT;
			case AmiTable.TYPE_STRING:
				return AmiConsts.TYPE_NAME_STRING;
			case AmiTable.TYPE_BOOLEAN:
				return AmiConsts.TYPE_NAME_BOOLEAN;
			case AmiTable.TYPE_UTC:
				return AmiConsts.TYPE_NAME_UTC;
			case AmiTable.TYPE_UTCN:
				return AmiConsts.TYPE_NAME_UTCN;
			case AmiTable.TYPE_CHAR:
				return AmiConsts.TYPE_NAME_CHAR;
			case AmiTable.TYPE_BINARY:
				return AmiConsts.TYPE_NAME_BINARY;
			case AmiTable.TYPE_COMPLEX:
				return AmiConsts.TYPE_NAME_COMPLEX;
			case AmiTable.TYPE_UUID:
				return AmiConsts.TYPE_NAME_UUID;
			case AmiTable.TYPE_BIGINT:
				return AmiConsts.TYPE_NAME_BIGINT;
			case AmiTable.TYPE_BIGDEC:
				return AmiConsts.TYPE_NAME_BIGDEC;
			default:
				return SH.toString(paramType);
		}
	}
	public static void setSystemPersister(AmiImdb imdb, AmiTableImpl table) {
		Map<String, String> options = new HashMap<String, String>();
		String fileName = imdb.getTools().getOptional(AmiCenterProperties.PREFIX_AMI_DB_PERSIST_DIR_SYSTEM_TABLE + table.getName());
		if (fileName == null)
			fileName = imdb.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_DB_PERSIST_DIR_SYSTEM_TABLES);
		if (fileName != null)
			options.put(AmiTablePersisterFactory_Text.OPTION_PERSIST_DIR, fileName);
		String encrypter = imdb.getTools().getOptional(AmiCenterProperties.PREFIX_AMI_DB_PERSIST_ENCRYPTER_SYSTEM_TABLE + table.getName());
		if (encrypter == null)
			encrypter = imdb.getTools().getOptional(AmiCenterProperties.PROPERTY_AMI_DB_PERSIST_ENCRYPTER_SYSTEM_TABLES);
		if (encrypter != null) {
			options.put(AmiTablePersisterFactory_Text.OPTION_PERSIST_ENCRYPTER, encrypter);
		}
		Map<String, Object> options2 = new HashMap<String, Object>(options);
		AmiTablePersister p = imdb.getTablePersisterFactory(AmiTablePersisterFactory_Text.NAME).newPersister(options2);
		AmiTablePersisterBindingImpl binding = new AmiTablePersisterBindingImpl(p, AmiTablePersisterFactory_Text.NAME, options2, options, table.getDefType());
		table.setPersister(binding);
	}
	public static File getSystemPersisterFile(AmiTableImpl table) {
		AmiTablePersisterBinding p = table.getPersister();
		return p == null ? null : ((AmiTablePersister_Text) p.getPersister()).getFile();
	}
	public static String toStringForIndexConstraintType(byte type) {
		switch (type) {
			case AmiIndex.CONSTRAINT_TYPE_NONE:
				return "NONE";
			case AmiIndex.CONSTRAINT_TYPE_UNIQUE:
				return "UNIQUE";
			case AmiIndex.CONSTRAINT_TYPE_PRIMARY:
				return "PRIMARY";
			default:
				return SH.toString(type);

		}
	}
	public static String toStringForIndexAutoGenType(byte type) {
		switch (type) {
			case AmiIndex.AUTOGEN_INC:
				return "INC";
			case AmiIndex.AUTOGEN_NONE:
				return "NONE";
			case AmiIndex.AUTOGEN_RAND:
				return "RAND";
			default:
				return SH.toString(type);

		}
	}
	public static byte parseIndexConstraintType(String sort) {
		if ("NONE".equalsIgnoreCase(sort))
			return (AmiIndex.CONSTRAINT_TYPE_NONE);
		else if ("UNIQUE".equalsIgnoreCase(sort))
			return (AmiIndex.CONSTRAINT_TYPE_UNIQUE);
		else if ("PRIMARY".equalsIgnoreCase(sort))
			return (AmiIndex.CONSTRAINT_TYPE_PRIMARY);
		else
			return -1;
	}
	public static byte parseIndexAutoGenType(String type) {
		if ("RAND".equalsIgnoreCase(type))
			return (AmiIndex.AUTOGEN_RAND);
		else if ("INC".equalsIgnoreCase(type))
			return (AmiIndex.AUTOGEN_INC);
		else if ("NONE".equalsIgnoreCase(type))
			return (AmiIndex.AUTOGEN_NONE);
		else
			return -1;
	}
	public static String toStringForTriggerType(byte type) {
		switch (type) {
			case AmiTrigger.INSERTING:
				return "INSERTING";
			case AmiTrigger.INSERTED:
				return "INSERTED";
			case AmiTrigger.UPDATING:
				return "UPDATING";
			case AmiTrigger.UPDATED:
				return "UPDATED";
			case AmiTrigger.DELETING:
				return "DELETING";
			case AmiTrigger.TIMER:
				return "TIMER";
			case AmiTrigger.CALL:
				return "CALL";
			default:
				return SH.toString(type);
		}
	}
	public static byte parseOnUndefColType(String type) {
		if ("ADD".equalsIgnoreCase(type))
			return (AmiTableDef.ON_UNDEFINED_COLUMN_ADD);
		else if ("REJECT".equalsIgnoreCase(type))
			return (AmiTableDef.ON_UNDEFINED_COLUMN_REJECT);
		else if ("IGNORE".equalsIgnoreCase(type))
			return (AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE);
		else
			return -1;
	}
	public static String toStringForOnUndefColType(byte type) {
		switch (type) {
			case AmiTableDef.ON_UNDEFINED_COLUMN_ADD:
				return "ADD";
			case AmiTableDef.ON_UNDEFINED_COLUMN_REJECT:
				return "REJECT";
			case AmiTableDef.ON_UNDEFINED_COLUMN_IGNORE:
				return "IGNORE";
			default:
				return SH.toString(type);
		}
	}
	public static byte toAmiTableColumnType(byte type) {
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
				return AmiTable.TYPE_BOOLEAN;
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return AmiTable.TYPE_FLOAT;
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
				return AmiTable.TYPE_DOUBLE;
			case AmiDataEntity.PARAM_TYPE_STRING:
			case AmiDataEntity.PARAM_TYPE_ASCII:
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL:
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
				return AmiTable.TYPE_STRING;
			case AmiDataEntity.PARAM_TYPE_ENUM1:
			case AmiDataEntity.PARAM_TYPE_ENUM2:
			case AmiDataEntity.PARAM_TYPE_ENUM3:
				return AmiTable.TYPE_ENUM;
			case AmiDataEntity.PARAM_TYPE_INT1:
				return AmiTable.TYPE_BYTE;
			case AmiDataEntity.PARAM_TYPE_INT2:
				return AmiTable.TYPE_SHORT;
			case AmiDataEntity.PARAM_TYPE_INT3:
			case AmiDataEntity.PARAM_TYPE_INT4:
				return AmiTable.TYPE_INT;
			case AmiDataEntity.PARAM_TYPE_LONG1:
			case AmiDataEntity.PARAM_TYPE_LONG2:
			case AmiDataEntity.PARAM_TYPE_LONG3:
			case AmiDataEntity.PARAM_TYPE_LONG4:
			case AmiDataEntity.PARAM_TYPE_LONG5:
			case AmiDataEntity.PARAM_TYPE_LONG6:
			case AmiDataEntity.PARAM_TYPE_LONG7:
			case AmiDataEntity.PARAM_TYPE_LONG8:
				return AmiTable.TYPE_LONG;
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return AmiTable.TYPE_UTC;
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return AmiTable.TYPE_UTCN;
			case AmiDataEntity.PARAM_TYPE_BINARY:
				return AmiTable.TYPE_BINARY;
			case AmiDataEntity.PARAM_TYPE_COMPLEX:
				return AmiTable.TYPE_COMPLEX;
			case AmiDataEntity.PARAM_TYPE_UUID:
				return AmiTable.TYPE_UUID;
			case AmiDataEntity.PARAM_TYPE_BIGINT:
				return AmiTable.TYPE_BIGINT;
			case AmiDataEntity.PARAM_TYPE_BIGDEC:
				return AmiTable.TYPE_BIGDEC;
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return AmiTable.TYPE_CHAR;
			case AmiDataEntity.PARAM_TYPE_NULL:
				throw new IllegalArgumentException("null type not valid: " + type);
			default:
				throw new IllegalArgumentException("Type not valid: " + type);
		}
	}
	public static Class getClassForValueType(byte type) {
		switch (type) {
			case AmiTable.TYPE_UTC:
				return DateMillis.class;
			case AmiTable.TYPE_UTCN:
				return DateNanos.class;
			case AmiTable.TYPE_ENUM:
				return int.class;
			case AmiTable.TYPE_SHORT:
				return short.class;
			case AmiTable.TYPE_BYTE:
				return byte.class;
			default:
				return AmiUtils.getClassForValueType(type);
		}
	}
	public static String toOptionsString(Map<String, String> options) {
		if (CH.isEmpty(options))
			return "";
		StringBuilder sb = new StringBuilder();
		for (String s : CH.sort(options.keySet())) {
			if (sb.length() > 0)
				sb.append(' ');
			sb.append(s);
			String value = options.get(s);
			if (!"true".equals(value)) {
				sb.append('=');
				SH.quote('"', value, sb);
			}
		}
		return sb.toString();
	}
	public static String toStringForOriginType(byte originType) {
		switch (originType) {
			case AmiCenterQueryDsRequest.ORIGIN_CMDLINE:
				return "CMDLINE";
			case AmiCenterQueryDsRequest.ORIGIN_FRONTEND:
				return "FRONTEND";
			case AmiCenterQueryDsRequest.ORIGIN_FRONTEND_SHELL:
				return "FRONTEND_SHELL";
			case AmiCenterQueryDsRequest.ORIGIN_JDBC:
				return "JDBC";
			case AmiCenterQueryDsRequest.ORIGIN_NESTED:
				return "NESTED";
			case AmiCenterQueryDsRequest.ORIGIN_SYSTEM:
				return "SYSTEM";
			case AmiCenterQueryDsRequest.ORIGIN_RTFEED:
				return "RTFEED";
			case AmiCenterQueryDsRequest.ORIGIN_REST:
				return "REST";
			default:
				return "ORIGIN-" + originType;
		}
	}
	public static String toStringForServiceStep(byte step) {
		switch (step) {
			case AmiServicePlugin.STATE_STEP1_IMDB_INITIALIZED:
				return "STEP1_IMDB_INITIALIZED";
			case AmiServicePlugin.STATE_STEP2_TABLES_INITIALIZED:
				return "STEP2_TABLES_INITIALIZED";
			case AmiServicePlugin.STATE_STEP3_TRIGGERS_INITIALIZED:
				return "STEP3_TRIGGERS_INITIALIZED";
			case AmiServicePlugin.STATE_STEP4_PROCEDURES_INITIALIZED:
				return "STEP4_PROCEDURES_INITIALIZED";
			case AmiServicePlugin.STATE_STEP5_DBOS_INITIALIZED:
				return "STEP5_DBOS_INITIALIZED";
			case AmiServicePlugin.STATE_STEP6_TRIGGERS_FIRED:
				return "STEP6_TRIGGERS_FIRED";
			case AmiServicePlugin.STATE_STEP7_TIMERS_INITIALIZED:
				return "STEP7_TIMERS_INITIALIZED";
			default:
				return "STEP" + step;
		}
	}
}
