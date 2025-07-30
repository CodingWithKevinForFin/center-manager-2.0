package com.f1.ami.amicommon;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.crypto.SecretKey;

import com.f1.ami.amicommon.encrypt.AmiPasswordEncrypterPlugin;
import com.f1.ami.amicommon.encrypt.AmiSimplePasswordEncrypter;
import com.f1.ami.amicommon.functions.*;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsTrackerEvent;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiDataEntity;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiRelayRunDbRequest;
import com.f1.ami.amicommon.msg.AmiResponse;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.amiscript.AmiDebugManagerWrapper;
import com.f1.ami.amiscript.AmiScriptMemberMethods;
import com.f1.ami.web.auth.AmiAuthenticatorDisabled;
import com.f1.ami.web.auth.AmiAuthenticatorFileBacked;
import com.f1.ami.web.auth.AmiAuthenticatorPlugin;
import com.f1.ami.web.auth.CachingAuthenticator;
import com.f1.base.Bytes;
import com.f1.base.CalcTypes;
import com.f1.base.Complex;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.base.Encrypter;
import com.f1.base.Table;
import com.f1.base.UUID;
import com.f1.container.ContainerTools;
import com.f1.povo.standard.RunnableResponseMessage;
import com.f1.utils.AH;
import com.f1.utils.BasicServerSocketEntitlements;
import com.f1.utils.ByteHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorHelper;
import com.f1.utils.ConvertedException;
import com.f1.utils.CopyOnWriteHashMap;
import com.f1.utils.Formatter;
import com.f1.utils.IOH;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.ServerSocketEntitlements;
import com.f1.utils.TableHelper;
import com.f1.utils.TextMatcher;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.converter.bytes.ObjectToByteArrayConverter;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.encrypt.AesEncryptUtils;
import com.f1.utils.encrypt.EncoderUtils;
import com.f1.utils.impl.BasicClock;
import com.f1.utils.impl.CaseInsensitiveHasher;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.sql.SqlDerivedCellParser;
import com.f1.utils.sql.SqlProcessorUtils;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.ExpressionParserHelper;
import com.f1.utils.string.Node;
import com.f1.utils.string.SqlExpressionParser;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorConst;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.FlowControl;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;
import com.f1.utils.structs.table.derived.MethodFactory;
import com.f1.utils.structs.table.derived.MethodFactoryManager;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.derived.ThreadSafeMethodFactoryManager;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiUtils {

	public static final String DATE01 = "20150314";
	public static final String DATE02 = "3/14/2015";
	public static final String DATE03 = "03/14/2015";
	public static final String DATE04 = "3/14";
	public static final String DATE05 = "3/14/15";
	public static final String DATE06 = "03/14/15";
	public static final String DATE07 = "March 14, 2015";
	public static final String DATE08 = "Saturday, March 14, 2015";
	public static final String DATE09 = "14-Mar";
	public static final String DATE10 = "14-Mar-15";
	public static final String DATE11 = "Mar-15";
	public static final String DATE12 = "March-15";
	public static final String DATE13 = "2015/03/14";
	public static final String DATE14 = "14/03/2015";

	public static final Map<String, String> dateOptionToFormatMap = new HashMap<String, String>();
	static {
		dateOptionToFormatMap.put(DATE01, "yyyyMMdd");
		dateOptionToFormatMap.put(DATE02, "M/dd/yyyy");
		dateOptionToFormatMap.put(DATE03, "MM/dd/yyyy");
		dateOptionToFormatMap.put(DATE04, "M/dd");
		dateOptionToFormatMap.put(DATE05, "M/dd/yy");
		dateOptionToFormatMap.put(DATE06, "MM/dd/yy");
		dateOptionToFormatMap.put(DATE07, "MMMM dd, yyyy");
		dateOptionToFormatMap.put(DATE08, "EEEE, MMMM dd, yyyy");
		dateOptionToFormatMap.put(DATE09, "dd-MMM");
		dateOptionToFormatMap.put(DATE10, "dd-MMM-yy");
		dateOptionToFormatMap.put(DATE11, "MMM-yy");
		dateOptionToFormatMap.put(DATE12, "MMMM-yy");
		dateOptionToFormatMap.put(DATE13, "yyyy/MM/dd");
		dateOptionToFormatMap.put(DATE14, "dd/MM/yyyy");
	}

	static final private OneToOne<String, Byte> NAMES_TO_TYPES = new OneToOne<String, Byte>();
	static final private HasherMap<String, Byte> NAMES_TO_TYPES_CASE_INSENSITIVE = new HasherMap<String, Byte>(CaseInsensitiveHasher.INSTANCE);

	public static final byte[] TYPES = new byte[] { AmiDatasourceColumn.TYPE_BOOLEAN, AmiDatasourceColumn.TYPE_INT, AmiDatasourceColumn.TYPE_FLOAT, AmiDatasourceColumn.TYPE_DOUBLE,
			AmiDatasourceColumn.TYPE_LONG, AmiDatasourceColumn.TYPE_STRING, AmiDatasourceColumn.TYPE_UTC, AmiDatasourceColumn.TYPE_UTCN, AmiDatasourceColumn.TYPE_BINARY,
			AmiDatasourceColumn.TYPE_CHAR, AmiDatasourceColumn.TYPE_BYTE, AmiDatasourceColumn.TYPE_SHORT, AmiDatasourceColumn.TYPE_BIGINT, AmiDatasourceColumn.TYPE_BIGDEC,
			AmiDatasourceColumn.TYPE_COMPLEX, AmiDatasourceColumn.TYPE_UUID };
	public static final Map<Class, Byte> CLASS_TO_TYPES = new CopyOnWriteHashMap<Class, Byte>();
	private static final Logger log = LH.get();
	private static final List<AmiWebFunctionFactory> FUNCTIONS = new ArrayList<AmiWebFunctionFactory>();
	public static final MethodFactoryManager METHOD_FACTORY;
	public static final ObjectToJsonConverter AMI_JSON_CONVERTER = AmiObjectToJsonConverter.INSTANCE_AMI;
	public static Encrypter SIMPLE_ENCRYPTER = new AmiSimplePasswordEncrypter();

	static {
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_STRING, AmiDatasourceColumn.TYPE_STRING);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_BINARY, AmiDatasourceColumn.TYPE_BINARY);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_BOOLEAN, AmiDatasourceColumn.TYPE_BOOLEAN);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_DOUBLE, AmiDatasourceColumn.TYPE_DOUBLE);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_FLOAT, AmiDatasourceColumn.TYPE_FLOAT);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_UTC, AmiDatasourceColumn.TYPE_UTC);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_UTCN, AmiDatasourceColumn.TYPE_UTCN);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_LONG, AmiDatasourceColumn.TYPE_LONG);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_INTEGER, AmiDatasourceColumn.TYPE_INT);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_CHAR, AmiDatasourceColumn.TYPE_CHAR);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_SHORT, AmiDatasourceColumn.TYPE_SHORT);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_BYTE, AmiDatasourceColumn.TYPE_BYTE);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_BIGINT, AmiDatasourceColumn.TYPE_BIGINT);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_BIGDEC, AmiDatasourceColumn.TYPE_BIGDEC);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_COMPLEX, AmiDatasourceColumn.TYPE_COMPLEX);
		NAMES_TO_TYPES.put(AmiConsts.TYPE_NAME_UUID, AmiDatasourceColumn.TYPE_UUID);
		NAMES_TO_TYPES_CASE_INSENSITIVE.putAll(NAMES_TO_TYPES.toKeyValueMap());
		NAMES_TO_TYPES_CASE_INSENSITIVE.put("INT", AmiDatasourceColumn.TYPE_INT);
		for (byte type : TYPES)
			CLASS_TO_TYPES.put(getClassForValueType(type), type);
		CLASS_TO_TYPES.put(Integer.class, AmiDatasourceColumn.TYPE_INT);
		CLASS_TO_TYPES.put(Byte.class, AmiDatasourceColumn.TYPE_BYTE);
		CLASS_TO_TYPES.put(Short.class, AmiDatasourceColumn.TYPE_SHORT);
		CLASS_TO_TYPES.put(Character.class, AmiDatasourceColumn.TYPE_CHAR);
		CLASS_TO_TYPES.put(java.util.Date.class, AmiDatasourceColumn.TYPE_UTC);
		final BasicClock clock = new BasicClock();
		FUNCTIONS.add(new AmiWebFunctionNoNull.Factory());
		FUNCTIONS.add(new AmiWebFunctionAbs.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseJson.Factory());
		FUNCTIONS.add(new AmiWebFunctionToJson.Factory());
		FUNCTIONS.add(new AmiWebFunctionCycle.Factory());
		FUNCTIONS.add(new AmiWebFunctionColorCycle.Factory());
		FUNCTIONS.add(new AmiWebFunctionBrighten.Factory());
		FUNCTIONS.add(new AmiWebFunctionFormatDate.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseDate.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseDateNano.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseDateTz.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseDateTzNano.Factory());
		FUNCTIONS.add(new AmiWebFunctionSplitLines.Factory());
		FUNCTIONS.add(new AmiWebFunctionFormatNumber.Factory());
		FUNCTIONS.add(new AmiWebFunctionJsonExtract.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrLen.Factory());
		FUNCTIONS.add(new AmiWebFunctionPower.Factory());
		FUNCTIONS.add(new AmiWebFunctionLog.Factory());
		FUNCTIONS.add(new AmiWebFunctionLn.Factory());
		FUNCTIONS.add(new AmiWebFunctionQuote.Factory());
		FUNCTIONS.add(new AmiWebFunctionGradient.Factory());
		FUNCTIONS.add(new AmiWebFunctionScale.Factory());
		FUNCTIONS.add(new AmiWebFunctionRound.Factory());
		FUNCTIONS.add(new AmiWebFunctionRound2.Factory());
		FUNCTIONS.add(new AmiWebFunctionRoundUp.Factory());
		FUNCTIONS.add(new AmiWebFunctionRoundDown.Factory());
		FUNCTIONS.add(new AmiWebFunctionRand.Factory());
		FUNCTIONS.add(new AmiWebFunctionRand2.Factory());
		FUNCTIONS.add(new AmiWebFunctionRandGaussian.Factory());
		FUNCTIONS.add(new AmiWebFunctionRandSec.Factory());
		FUNCTIONS.add(new AmiWebFunctionRandSec2.Factory());
		FUNCTIONS.add(new AmiWebFunctionRandSecGaussian.Factory());
		FUNCTIONS.add(new AmiWebFunctionSin.Factory());
		FUNCTIONS.add(new AmiWebFunctionCos.Factory());
		FUNCTIONS.add(new AmiWebFunctionTan.Factory());
		FUNCTIONS.add(new AmiWebFunctionArcSin.Factory());
		FUNCTIONS.add(new AmiWebFunctionArcCos.Factory());
		FUNCTIONS.add(new AmiWebFunctionArcTan.Factory());
		FUNCTIONS.add(new AmiWebFunctionSwitch.Factory());
		FUNCTIONS.add(new AmiWebFunctionTrim.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrReplace.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrStrip.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrSplit.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrSplitToMap.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrSplice.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrSubstring.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrIndexOf.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrBefore.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrBeforeLast.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrCharAt.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrAfter.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrAfterLast.Factory());
		FUNCTIONS.add(new AmiWebFunctionDatepart.Factory());
		FUNCTIONS.add(new AmiWebFunctionDatepartNum.Factory());
		FUNCTIONS.add(new AmiWebFunctionDigamma.Factory());
		FUNCTIONS.add(new AmiWebFunctionExponential.Factory());
		FUNCTIONS.add(new AmiWebFunctionLnGamma.Factory());
		FUNCTIONS.add(new AmiWebFunctionTimestamp.Factory(clock));
		FUNCTIONS.add(new AmiWebFunctionTimestampNano.Factory(clock));
		FUNCTIONS.add(new AmiWebFunctionTimezoneOffset.Factory());
		FUNCTIONS.add(new AmiWebFunctionDiffSequence.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrLastIndexOf.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrStartsWith.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrEndsWith.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrIs.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrIsnt.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrSplitLines.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrRepeat.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrIndexOf2.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrLastIndexOf2.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrJoin.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrJoin2.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrEqualsIgnoreCase.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrDistanceLev.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrToBinary.Factory());
		FUNCTIONS.add(new AmiWebFunctionBinaryToStr.Factory());
		FUNCTIONS.add(new AmiWebFunctionBinaryToStr16.Factory());
		FUNCTIONS.add(new AmiWebFunctionBinaryToStr64.Factory());
		FUNCTIONS.add(new AmiWebFunctionBinaryToStr64Safe.Factory());
		FUNCTIONS.add(new AmiWebFunctionWriteBinaryFile.Factory());
		FUNCTIONS.add(new AmiWebFunctionReadBinaryFile.Factory());
		FUNCTIONS.add(new AmiWebFunctionBinaryLen.Factory());
		FUNCTIONS.add(new AmiWebFunctionCksum.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrParseLong.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrCut.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrUpper.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrLower.Factory());
		FUNCTIONS.add(new AmiWebFunctionPercentDiff.Factory());
		FUNCTIONS.add(new AmiWebFunctionPercentChange.Factory());
		FUNCTIONS.add(new AmiWebFunctionRoundNearestDown.Factory());
		FUNCTIONS.add(new AmiWebFunctionRoundNearestUp.Factory());
		FUNCTIONS.add(new AmiWebFunctionRoundNearest.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetRed.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetGreen.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetBlue.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetAlpha.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetHue.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetSat.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrGetLum.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrSetRgb.Factory());
		FUNCTIONS.add(new AmiWebFunctionClrSetHsl.Factory());
		FUNCTIONS.add(new AmiWebFunctionMinimum.Factory());
		FUNCTIONS.add(new AmiWebFunctionMaximum.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitAnd.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitOr.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitXor.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitShiftLeft.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitShiftRight.Factory());
		FUNCTIONS.add(new AmiWebFunctionBitShiftRightUnsigned.Factory());
		FUNCTIONS.add(new AmiWebFunctionLogInfo.Factory());
		FUNCTIONS.add(new AmiWebFunctionLogWarn.Factory());
		FUNCTIONS.add(new AmiWebFunctionSleepMillis.Factory());
		FUNCTIONS.add(new AmiWebFunctionGetRawJavaStackTrace.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseCsv.Factory());
		FUNCTIONS.add(new AmiWebFunctionVersion.Factory());
		FUNCTIONS.add(new AmiWebFunctionVersionDetails.Factory());
		FUNCTIONS.add(new AmiWebFunctionVersionMap.Factory());
		FUNCTIONS.add(new AmiWebFunctionVerifyDataMac.Factory());
		FUNCTIONS.add(new AmiWebFunctionSignDataMac.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrDecodeBytes.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrDecodeBytes2.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrEncodeBytes.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrEncodeBytes2.Factory());
		FUNCTIONS.add(new AmiWebFunctionUrlDecode.Factory());
		FUNCTIONS.add(new AmiWebFunctionUrlDecode2.Factory());
		FUNCTIONS.add(new AmiWebFunctionUrlEncode.Factory());
		FUNCTIONS.add(new AmiWebFunctionUrlEncode2.Factory());
		FUNCTIONS.add(new AmiWebFunctionStrMatchesFilter.Factory());
		FUNCTIONS.add(new AmiWebFunctionUrlPath.Factory());
		FUNCTIONS.add(new AmiWebFunctionFormatNumberCompact.Factory());
		FUNCTIONS.add(new AmiWebFunctionGetBestImageType.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseXlsx.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseXlsx2.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseXlsx3.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseXlsx4.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseRawXlsx.Factory());
		FUNCTIONS.add(new AmiWebFunctionParseInt.Factory());
		FUNCTIONS.add(new AmiWebFunctionToHex.Factory());
		FUNCTIONS.add(new AmiWebFunctionEncodeDouble64.Factory());
		FUNCTIONS.add(new AmiWebFunctionEncodeFloat64.Factory());
		FUNCTIONS.add(new AmiWebFunctionDecodeDouble64.Factory());
		FUNCTIONS.add(new AmiWebFunctionDecodeFloat64.Factory());
		FUNCTIONS.add(new AmiWebFunctionConstructEml.Factory());

		BasicMethodFactory mf = new ThreadSafeMethodFactoryManager();
		addTypes(mf);
		for (MethodFactory i : FUNCTIONS) {
			try {
				mf.addFactory(i);
			} catch (Error e) {
				throw new RuntimeException("Error with " + i, e);
			}
		}
		AmiScriptMemberMethods.registerMethods(new AmiDebugManagerWrapper(null), mf);
		mf.lock();
		METHOD_FACTORY = mf;
	}

	static public <T extends AmiPlugin> T loadPlugin(String pluginClassName, String pluginDescription, ContainerTools tools, PropertyController props, Class<T> returnType) {
		StringBuilder sb = new StringBuilder();
		T r = loadPlugin(pluginClassName, pluginDescription, tools, props, returnType, sb);
		if (r == null)
			throw new RuntimeException(sb.toString());
		return r;
	}
	static public <T extends AmiPlugin> T loadPlugin(String pluginClassName, String pluginDescription, ContainerTools tools, PropertyController props, Class<T> returnType,
			StringBuilder errorSink) {
		try {
			if (SH.isnt(pluginClassName)) {
				errorSink.append("Invalid ").append(pluginDescription).append(", class name can not be empty: ");
				return null;
			}
			final Class<T> clazz;
			try {
				clazz = (Class<T>) Class.forName(pluginClassName);
			} catch (ClassNotFoundException e) {
				errorSink.append("Invalid ").append(pluginDescription).append(", class not found: ").append(pluginClassName);
				return null;
			}
			if (!returnType.isAssignableFrom(clazz)) {
				errorSink.append("Invalid ").append(pluginDescription).append(", must implement ").append(returnType.getName()).append(": ").append(pluginClassName);
				return null;
			}
			final T r;
			try {
				r = clazz.newInstance();
				LH.info(log, "PLUGIN MANAGER: Loaded ", pluginDescription, " class: ", OH.getClassName(r), " PluginID:", r.getPluginId());
			} catch (Exception e) {
				LH.warning(log, e);
				errorSink.append("Invalid ").append(pluginDescription).append(", Could not access default constructor: ").append(pluginClassName).append(" ERROR ==> " + e);
				return null;
			}
			try {
				r.init(tools, props);
				LH.info(log, "PLUGIN MANAGER: Initialized ", pluginDescription, " class: ", OH.getClassName(r));
			} catch (Exception e) {
				errorSink.append("Invalid ").append(pluginDescription).append(", ").append(pluginClassName)
						.append("::init(ContainerTools tools, PropertyController props) threw exception: ");
				errorSink.append(SH.printStackTrace(e));
				return null;
			}
			return r;
		} finally {
			if (errorSink.length() > 0)
				System.err.println(errorSink);
		}
	}
	public static AmiPasswordEncrypterPlugin loadEncrypter(ContainerTools tools, String propertyName, String description) {
		String name = SH.stripSuffix(propertyName, ".class", true);
		String className = tools.getOptional(propertyName);
		final AmiPasswordEncrypterPlugin r;
		PropertyController props = tools.getSubPropertyController(name);
		StringBuilder errorSink = new StringBuilder();
		try {
			if (SH.isnt(className)) {
				r = (AmiPasswordEncrypterPlugin) AmiUtils.SIMPLE_ENCRYPTER;
				LH.info(log, "PLUGIN MANAGER: Disabled " + description + " using default encrypter: " + AmiUtils.SIMPLE_ENCRYPTER.getClass().getCanonicalName());
			} else {
				if (SH.isnt(className)) {
					errorSink.append("Invalid ").append(description).append(", class name can not be empty: ");
					LH.warning(log, errorSink.toString());
					return null;
				}
				final Class<AmiPasswordEncrypterPlugin> clazz;
				try {
					clazz = (Class<AmiPasswordEncrypterPlugin>) Class.forName(className);
				} catch (ClassNotFoundException e) {
					LH.warning(log, e);
					errorSink.append("Invalid ").append(description).append(", class not found: ").append(className);
					return null;
				}
				try {
					r = clazz.newInstance();
					LH.info(log, "ENCRYPTER MANAGER: Loaded ", description, " class: ", OH.getClassName(r));
				} catch (Exception e) {
					LH.warning(log, e);
					errorSink.append("Invalid ").append(description).append(", Could not access default constructor: ").append(className).append(" ERROR ==> " + e);
					return null;
				}
				LH.info(log, "PLUGIN MANAGER: " + description + " using encrypter: " + r.getClass().getCanonicalName());
			}
			try {
				r.init(tools, props);
				LH.info(log, "PLUGIN MANAGER: Initialized ", description, " class: ", OH.getClassName(r));
			} catch (Exception e) {
				LH.warning(log, e);
				errorSink.append("Invalid ").append(description).append(", ").append(OH.getClassName(r))
						.append("::init(ContainerTools tools, PropertyController props) threw exception: ");
				errorSink.append(SH.printStackTrace(e));
				return null;
			}
		} finally {
			if (errorSink.length() > 0)
				System.err.println(errorSink);
		}
		return r;
	}

	@Deprecated
	public static byte getNarrowestType(byte type, byte type2) {
		if (type == type2)
			return type;
		switch (type << 8 | type2) {
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
				return AmiDatasourceColumn.TYPE_BOOLEAN;
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_STRING << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_CHAR:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_BOOLEAN << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_BOOLEAN:
				return AmiDatasourceColumn.TYPE_STRING;
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_DOUBLE << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_FLOAT:
				return AmiDatasourceColumn.TYPE_DOUBLE;
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_FLOAT << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_FLOAT:
				return AmiDatasourceColumn.TYPE_FLOAT;
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_UTC:
				return AmiDatasourceColumn.TYPE_UTC;
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_UTCN:
				return AmiDatasourceColumn.TYPE_UTCN;
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_LONG << 8 | AmiDatasourceColumn.TYPE_UTCN:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_LONG:
			case AmiDatasourceColumn.TYPE_UTC << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_UTCN << 8 | AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_UTCN:
				return AmiDatasourceColumn.TYPE_LONG;
			case AmiDatasourceColumn.TYPE_INT << 8 | AmiDatasourceColumn.TYPE_INT:
				return AmiDatasourceColumn.TYPE_INT;
			case AmiDatasourceColumn.TYPE_CHAR << 8 | AmiDatasourceColumn.TYPE_CHAR:
				return AmiDatasourceColumn.TYPE_CHAR;
			default:
				throw new IllegalArgumentException("Type(s) not valid: " + type + ", " + type2);
		}
	}
	public static byte toColumnType(byte type) {
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
				return AmiDatasourceColumn.TYPE_BOOLEAN;
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return AmiDatasourceColumn.TYPE_FLOAT;
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
				return AmiDatasourceColumn.TYPE_DOUBLE;
			case AmiDataEntity.PARAM_TYPE_STRING:
			case AmiDataEntity.PARAM_TYPE_ASCII:
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL:
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
			case AmiDataEntity.PARAM_TYPE_ENUM1:
			case AmiDataEntity.PARAM_TYPE_ENUM2:
			case AmiDataEntity.PARAM_TYPE_ENUM3:
				return AmiDatasourceColumn.TYPE_STRING;
			case AmiDataEntity.PARAM_TYPE_INT1:
			case AmiDataEntity.PARAM_TYPE_INT2:
			case AmiDataEntity.PARAM_TYPE_INT3:
			case AmiDataEntity.PARAM_TYPE_INT4:
				return AmiDatasourceColumn.TYPE_INT;
			case AmiDataEntity.PARAM_TYPE_LONG1:
			case AmiDataEntity.PARAM_TYPE_LONG2:
			case AmiDataEntity.PARAM_TYPE_LONG3:
			case AmiDataEntity.PARAM_TYPE_LONG4:
			case AmiDataEntity.PARAM_TYPE_LONG5:
			case AmiDataEntity.PARAM_TYPE_LONG6:
			case AmiDataEntity.PARAM_TYPE_LONG7:
			case AmiDataEntity.PARAM_TYPE_LONG8:
				return AmiDatasourceColumn.TYPE_LONG;
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return AmiDatasourceColumn.TYPE_UTC;
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return AmiDatasourceColumn.TYPE_UTCN;
			case AmiDataEntity.PARAM_TYPE_BINARY:
				return AmiDatasourceColumn.TYPE_BINARY;
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return AmiDatasourceColumn.TYPE_CHAR;
			case AmiDataEntity.PARAM_TYPE_BIGINT:
				return AmiDatasourceColumn.TYPE_BIGINT;
			case AmiDataEntity.PARAM_TYPE_BIGDEC:
				return AmiDatasourceColumn.TYPE_BIGDEC;
			case AmiDataEntity.PARAM_TYPE_COMPLEX:
				return AmiDatasourceColumn.TYPE_COMPLEX;
			case AmiDataEntity.PARAM_TYPE_UUID:
				return AmiDatasourceColumn.TYPE_UUID;
			case -1:
				return AmiDatasourceColumn.TYPE_UNKNOWN;
			case AmiDataEntity.PARAM_TYPE_NULL:
				throw new IllegalArgumentException("null type not valid: " + type);
			default:
				throw new IllegalArgumentException("Type not valid: " + type);
		}
	}
	public static Class<?> getClassForValueType(byte valueType) {
		switch (valueType) {
			case AmiDataEntity.PARAM_TYPE_NULL:
				return Object.class;
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
				return Boolean.class;
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return Float.class;
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
				return Double.class;
			case AmiDataEntity.PARAM_TYPE_STRING:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_ASCII:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
				return String.class;

			case AmiDataEntity.PARAM_TYPE_INT1:
				return Integer.class;
			case AmiDataEntity.PARAM_TYPE_INT2:
				return Integer.class;
			case AmiDataEntity.PARAM_TYPE_INT3:
				return Integer.class;
			case AmiDataEntity.PARAM_TYPE_INT4:
				return Integer.class;

			case AmiDataEntity.PARAM_TYPE_LONG1:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG2:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG3:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG4:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG5:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG6:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG7:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_LONG8:
				return Long.class;
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return DateMillis.class;
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return DateNanos.class;

			case AmiDataEntity.PARAM_TYPE_ENUM1:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_ENUM2:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_ENUM3:
				return String.class;
			case AmiDataEntity.PARAM_TYPE_BINARY:
				return Bytes.class;
			case AmiDataEntity.PARAM_TYPE_BIGINT:
				return BigInteger.class;
			case AmiDataEntity.PARAM_TYPE_BIGDEC:
				return BigDecimal.class;
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return Character.class;
			case AmiDataEntity.PARAM_TYPE_COMPLEX:
				return Complex.class;
			case AmiDataEntity.PARAM_TYPE_UUID:
				return UUID.class;
			case AmiDatasourceColumn.TYPE_UNKNOWN:
				return Object.class;
			default:
				throw new RuntimeException("unknown type: " + valueType);
		}
	}

	public static byte getTypeForClass(Class clazz, byte dflt) {
		if (clazz == null)
			throw new NullPointerException();
		clazz = OH.getBoxed(clazz);
		Byte r = CLASS_TO_TYPES.get(clazz);
		if (r != null)
			return r.byteValue();
		for (Entry<Class, Byte> e : CLASS_TO_TYPES.entrySet()) {
			if (e.getKey().isAssignableFrom(clazz)) {
				CLASS_TO_TYPES.put(clazz, e.getValue().byteValue());
				return e.getValue().byteValue();
			}
		}
		return dflt;
	}
	public static byte getTypeForClass(Class clazz) {
		if (clazz == null)
			throw new NullPointerException();
		clazz = OH.getBoxed(clazz);
		Byte r = CLASS_TO_TYPES.get(clazz);
		if (r != null)
			return r.byteValue();
		for (Entry<Class, Byte> e : CLASS_TO_TYPES.entrySet()) {
			if (e.getKey().isAssignableFrom(clazz)) {
				CLASS_TO_TYPES.put(clazz, e.getValue().byteValue());
				return e.getValue().byteValue();
			}
		}
		throw new RuntimeException("Unknown type: " + clazz);
	}

	public static Class getWidest(Class a, Class b) {
		return SqlProcessorUtils.getWidest(a, b);
	}
	public static Iterable<AmiWebFunctionFactory> getFunctions() {
		return FUNCTIONS;
	}
	public static List<AmiWebFunctionFactory> getFunctions(String methodName) {
		List<AmiWebFunctionFactory> results = new ArrayList<AmiWebFunctionFactory>();
		for (int i = 0; i < FUNCTIONS.size(); i++) {
			if (FUNCTIONS.get(i).getDefinition().getMethodName() == methodName) {
				results.add(FUNCTIONS.get(i));
			}
		}
		return results;
	}

	public static byte parseTypeName(String type) {
		Byte r = NAMES_TO_TYPES_CASE_INSENSITIVE.get(type);
		if (r == null) {
			return AmiDatasourceColumn.TYPE_UNKNOWN;
		}
		return r.byteValue();
	}
	public static Set<String> getTypeNames() {
		return NAMES_TO_TYPES.getKeys();
	}
	public static String toTypeName(byte type) {
		String r = NAMES_TO_TYPES.getKey(toColumnType(type));
		return r;
	}
	@Deprecated
	public static String toTypeName(Class type) {
		return NAMES_TO_TYPES.getKey(getTypeForClass(type, AmiDatasourceColumn.TYPE_UNKNOWN));
	}

	public static Iterable<Entry<String, Byte>> getNamesToTypes() {
		return NAMES_TO_TYPES.getEntries();
	}

	static public StringBuilder appendObject(StringBuilder sink, Object v) {
		if (v == null)
			sink.append("null");
		else if (v instanceof CharSequence)
			escape((CharSequence) v, sink.append('\"')).append('"');
		else if (v instanceof Double)
			sink.append(((Double) v).doubleValue()).append('D');
		else if (v instanceof Long)
			sink.append(((Long) v).longValue()).append('L');
		else if (v instanceof Integer)
			sink.append(((Integer) v).intValue());
		else if (v instanceof Float)
			sink.append(((Float) v).floatValue());
		else if (v instanceof Boolean)
			sink.append(((Boolean) v).booleanValue());
		else if (v instanceof byte[]) {
			sink.append('"');
			EncoderUtils.encode64UrlSafe((byte[]) v, sink);
			sink.append('"');
			sink.append('U');
		} else if (v instanceof Bytes) {
			sink.append('"');
			EncoderUtils.encode64UrlSafe(((Bytes) v).getBytes(), sink);
			sink.append('"');
			sink.append('U');
		} else
			escape(v.toString(), sink.append('"')).append('"');
		return sink;
	}
	static public StringBuilder escape(CharSequence text, StringBuilder sink) {
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (c > 127) {
				sink.append("\\u");
				SH.repeat('0', 4 - MH.getDigitsCount((int) c, 16), sink);
				SH.toString((int) c, 16, sink);
				continue;
			}
			switch (c) {
				case '\\':
					sink.append("\\\\");
					continue;
				case '\t':
					sink.append("\\t");
					continue;
				case '\r':
					sink.append("\\r");
					continue;
				case '\n':
					sink.append("\\n");
					continue;
				case '\f':
					sink.append("\\f");
					continue;
				case '\b':
					sink.append("\\b");
					continue;
				case '"':
					sink.append("\\\"");
					continue;
				default:
					sink.append(c);
			}
		}
		return sink;
	}

	/*
	 * Prepares the given given variable name
	 */
	public static String toValidVarName(String name) {
		if (SH.isnt(name))
			return "_";
		StringBuilder r = null;
		int i = 0;
		if (OH.isBetween(name.charAt(0), '0', '9')) {
			r = new StringBuilder(name.length() + 1);
			r.append('_');
		} else {
			for (;; i++) {
				if (i == name.length())
					return name;
				char c = name.charAt(i);
				if (OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || OH.isBetween(c, '0', '9'))
					continue;
				r = new StringBuilder(name.length() + 1);
				r.append(name, 0, i);
				break;
			}
			if (r == null)
				return name;
		}
		for (; i < name.length(); i++) {
			char c = name.charAt(i);
			if (OH.isBetween(c, 'a', 'z') || OH.isBetween(c, 'A', 'Z') || OH.isBetween(c, '0', '9')) {
				r.append(c);
			} else if (r.length() == 0 || r.charAt(r.length() - 1) != '_') {
				r.append('_');
			}
		}
		return r.toString();
	}
	static public boolean isValidVariableName(CharSequence value, boolean allowBackTicks, boolean allowPeriods) {
		return isValidVariableName(value, allowBackTicks, allowPeriods, false);
	}
	static public boolean isValidVariableName(CharSequence value, boolean allowBackTicks, boolean allowPeriods, boolean allowReservedWords) {
		if (value == null)
			return false;
		if (!allowReservedWords && SqlExpressionParser.isReserved(value))
			return false;
		int l = value.length();
		if (l == 0)
			return false;
		else if (allowBackTicks && l > 2 && value.charAt(0) == '`' && value.charAt(l - 1) == '`') {
			return true;//TODO:check for bad escape sequence 
		} else {
			char c = value.charAt(0);
			if (OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && c != '_')
				return false;
			for (int i = 1; i < l; i++) {
				c = value.charAt(i);
				if (OH.isntBetween(c, 'a', 'z') && OH.isntBetween(c, 'A', 'Z') && OH.isntBetween(c, '0', '9') && c != '_' && (c != '.' || !allowPeriods))
					return false;
			}
		}
		return true;
	}

	static public byte getReservedParamType(String type) {
		if (type.length() == 1) {
			switch (type.charAt(0)) {
				case AmiConsts.RESERVED_PARAM_ID:
				case AmiConsts.RESERVED_PARAM_TYPE:
				case AmiConsts.RESERVED_PARAM_APPLICATION:
					return AmiDatasourceColumn.TYPE_STRING;
				case AmiConsts.RESERVED_PARAM_CREATED_ON:
				case AmiConsts.RESERVED_PARAM_EXPIRED:
				case AmiConsts.RESERVED_PARAM_MODIFIED_ON:
				case AmiConsts.RESERVED_PARAM_NOW:
				case AmiConsts.RESERVED_PARAM_AMIID:
					return AmiDatasourceColumn.TYPE_LONG;
				case AmiConsts.RESERVED_PARAM_REVISION:
					return AmiDatasourceColumn.TYPE_INT;
			}
		}
		return AmiDatasourceColumn.TYPE_UNKNOWN;
	}
	public static String toMessage(Throwable t) {
		if (t == null)
			return "";
		StringBuilder sb = new StringBuilder();
		String lastMessage = null;
		for (; t != null; t = t.getCause() == t ? null : t.getCause()) {
			if (sb.length() > 0 && !SH.endsWith(sb, '\n'))
				sb.append('\n');
			if (t instanceof ExpressionParserException && ((ExpressionParserException) t).getExpression() != null) {
				ExpressionParserException epe = (ExpressionParserException) t;
				return epe.toLegibleString();
			} else if (t instanceof FlowControlThrow) {
				return "Uncaught: " + t;
			} else {
				String clazzName = t instanceof ConvertedException ? ((ConvertedException) t).getExceptionClassName() : t.getClass().getName();
				String message = SH.trim(t.getMessage());
				boolean hasMessage = SH.is(message);
				if (!hasMessage)
					message = clazzName;
				if (SH.indexOf(sb, message, 0) != -1 && message.indexOf(' ') != -1)
					continue;
				if (OH.ne(lastMessage, message)) {
					lastMessage = message;
					sb.append(message);
				}

			}
		}
		return sb.toString();
	}
	public static boolean isNumericType(Class<?> type) {
		return Number.class.isAssignableFrom(type) && !isDateType(type);
	}
	public static boolean isDateType(Class<?> type) {
		return type == DateMillis.class || type == DateNanos.class;
	}

	static public List<File> findFiles(String names, boolean includeDirs, boolean includeMissingExplicitFiles) {
		List<File> r = new ArrayList<File>();
		if (SH.isnt(names))
			return r;
		String parts[] = SH.splitWithEscape(',', '\\', names);
		for (String part : parts) {
			if (part.contains("*")) {
				part = SH.trim(part);
				if (SH.isnt(part))
					continue;
				String suffix = SH.afterLast(part, '/', null);
				String prefix = SH.beforeLast(part, '/', null);
				boolean found = false;
				if (suffix != null) {
					File file = new File(prefix);
					if (file.isDirectory()) {
						File[] files = file.listFiles();
						if (AH.isntEmpty(files)) {
							TextMatcher matcher = SH.m("^" + suffix + "$");
							for (File f : files)
								if (f.exists() && matcher.matches(f.getName()) && (includeDirs || f.isFile())) {
									r.add(f);
									found = true;
								}
						}
					}
				}
				if (!found) {
					File f = new File(part);
					if (f.exists() && (includeDirs || f.isFile()))
						r.add(f);
				}
			} else {
				File file = new File(part);
				if (includeMissingExplicitFiles || file.isFile())
					r.add(file);
			}
		}
		return r;
	}

	public static boolean isResevedTableName(String name) {
		return name.startsWith("__");
	}
	public static <T extends AmiPlugin> Map<String, T> loadPlugins(ContainerTools tools, String propertyName, String description, Class<T> pluginType) {
		Map<String, T> r = new HashMap<String, T>();
		loadPlugins(tools, propertyName, description, pluginType, r);
		return r;
	}
	public static AmiAuthenticatorPlugin loadAuthenticatorPlugin(ContainerTools tools, String propertyName, String description) {
		String name = SH.stripSuffix(propertyName, ".class", true);
		String classname = tools.getOptional(propertyName);
		if (SH.isnt(classname)) {
			LH.info(log, "PLUGIN MANAGER: Disabled " + description);
			return AmiAuthenticatorDisabled.INSTANCE;
		}
		try {
			PropertyController props = tools.getSubPropertyController(name);
			AmiAuthenticatorPlugin r = loadPlugin(classname, description, tools, props, AmiAuthenticatorPlugin.class);
			AmiAuthenticatorPlugin r2;
			if (r instanceof AmiAuthenticatorDisabled || r instanceof AmiAuthenticatorFileBacked)
				r2 = r;
			else
				r2 = new AmiAuthenticatorApplyEntitlements(tools, props, r);
			String cacheDuration = props.getOptional(AmiCommonProperties.PROPERTY_AUTH_SUFFIX_CACHE_DURATION, String.class);
			if (SH.is(cacheDuration)) {
				long ms = SH.parseDurationTo(cacheDuration, TimeUnit.MILLISECONDS);
				LH.info(log, "PLUGIN MANAGER: CACHING SUCCESSFUL LOGINS TO " + description + " FOR " + ms + " MILLIS");
				r2 = new CachingAuthenticator(r2, ms);
			}
			return r2;
		} catch (Exception e) {
			throw new RuntimeException("Failed to process property: " + propertyName + "=" + classname, e);
		}
	}
	public static <T extends AmiPlugin> T loadPlugin(ContainerTools tools, String propertyName, String description, Class<T> pluginType) {
		String classname = tools.getOptional(propertyName);
		if (SH.isnt(classname))
			return null;
		try {
			return loadPlugin(classname, description, tools, tools, pluginType);
		} catch (Exception e) {
			throw new RuntimeException("Failed to process property: " + propertyName + "=" + classname, e);
		}
	}
	public static <T extends AmiPlugin> void loadPlugins(ContainerTools tools, String propertyName, String description, Class<T> pluginType, Map<String, T> sink) {
		String values = tools.getOptional(propertyName);
		if (SH.is(values)) {
			String[] parts = SH.split(',', values);
			try {
				for (String datasourceClass : parts) {
					StringBuilder errorSink = new StringBuilder();
					T plugin = AmiUtils.loadPlugin(datasourceClass, description, tools, tools.getSubPropertyController(datasourceClass + '.'), pluginType, errorSink);
					if (plugin == null)
						throw new RuntimeException("Error loading " + datasourceClass + ": " + errorSink.toString());
					CH.putOrThrow(sink, plugin.getPluginId(), plugin, "Duplicate Ami Datasource Plugin identifier");
				}
			} catch (Exception e) {
				throw new RuntimeException("Error Processing property: " + propertyName + "=" + values, e);
			}
		}
	}
	/*
	 * Strict castType
	 */
	@SuppressWarnings("unchecked")
	static public <V> V castOptionStrict(Class<V> cast, Object o, int position, String optionName) {
		if (o == null)
			return null;
		if (!cast.isAssignableFrom(o.getClass()))
			throw new ExpressionParserException(position - optionName.length(), "Option `" + optionName + "` isn't of type " + cast.getSimpleName());
		return (V) o;
	}
	static public Map<String, Object> processOptions(int pos, Map<String, Node> userOptions, AmiFactoryPlugin plugin, SqlDerivedCellParser dcp, CalcFrameStack sf,
			boolean require) {
		if (CH.isEmpty(userOptions)) {
			if (require)
				for (AmiFactoryOption defOption : plugin.getAllowedOptions())
					if (defOption.getRequired())
						throw new ExpressionParserException(pos, "Required USE option missing: " + defOption.getName());
			return Collections.EMPTY_MAP;
		}
		Map<String, Object> optionsSink = new HashMap<String, Object>(userOptions.size());
		HashMap<String, Node> suppliedOptions = new HashMap<String, Node>(userOptions);
		for (AmiFactoryOption defOption : plugin.getAllowedOptions()) {
			String key = defOption.getName();
			Node userOption = suppliedOptions.remove(key);
			if (userOption == null) {
				if (defOption.getRequired() && require)
					throw new ExpressionParserException(pos, "Required USE option missing: " + key);
			} else {
				final Object value = AmiUtils.getNodeValue(userOption, dcp, sf);
				if (value == null)
					throw new ExpressionParserException(userOption.getPosition(), "Option '" + key + "' must be of type: " + defOption.getType().getSimpleName());
				optionsSink.put(key, value);
			}
		}
		if (suppliedOptions.size() > 0) {
			Entry<String, Node> first = CH.first(suppliedOptions.entrySet());
			List<String> allowedOptions = new ArrayList<String>();
			for (AmiFactoryOption i : plugin.getAllowedOptions())
				allowedOptions.add(i.getName());
			Collections.sort(allowedOptions);
			throw new ExpressionParserException(first.getValue().getPosition(), "Unknown option: '" + first.getKey() + "'. Valid options are: " + allowedOptions);
		}
		return optionsSink;
	}
	static public String descriptFactoryOptions(Iterable<AmiFactoryOption> options, MethodFactoryManager methodFactory, boolean full) {
		if (CH.isEmpty(options))
			return "";
		StringBuilder sb = new StringBuilder();
		for (AmiFactoryOption i : options) {
			if (sb.length() > 0)
				sb.append(",");
			sb.append(i.getName());
			sb.append(' ');
			sb.append(methodFactory.forType(i.getType()));
			if (i.getRequired())
				sb.append(" nonull");
			if (full && SH.is(i.getHelp()))
				sb.append("  /*").append(i.getHelp()).append("*/");
		}
		return sb.toString();
	}
	public static void fillResponse(RunnableResponseMessage msg, AmiResponse r) {
		if (msg.getText() != null) {
			r.setMessage(msg.getText());
			r.setOk(false);
		}
		if (msg.getThrowable() != null) {
			if (msg.getThrowable() instanceof Exception)
				r.setException(((Exception) msg.getThrowable()));
			else
				r.setException(new Exception(msg.getThrowable()));
			r.setOk(false);
		}
	}

	public static void fillResponse(AmiResponse in, AmiResponse sink) {
		sink.setOk(in.getOk());
		sink.setException(in.getException());
		if (in.getException() instanceof ExpressionParserException) {
			sink.setMessage(((ExpressionParserException) in.getException()).toLegibleString());
		} else
			sink.setMessage(in.getMessage());
	}
	public static AmiServiceLocator newServiceLocator(AmiRelayRunDbRequest request) {
		final String pass = request.getDsPassword();
		return new AmiServiceLocator(AmiServiceLocator.TARGET_TYPE_DATASOURCE, request.getDsAdapter(), request.getDsName(), request.getDsUrl(), request.getDsUsername(),
				pass == null ? null : pass.toCharArray(), request.getDsOptions(), request.getInvokedBy());
	}
	public static List<Table> processQuery(ContainerTools t, AmiDatasourceAdapter adapter, AmiCenterQuery q, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		AmiCenterQueryResult r = t.nw(AmiCenterQueryResult.class);
		adapter.processQuery(q, r, debugSink, tc);
		return r.getTables();
	}

	static public String toStringForTrackedEventType(byte b) {
		switch (b) {
			case AmiCenterQueryDsTrackerEvent.TYPE_ERROR:
				return "ERROR";
			case AmiCenterQueryDsTrackerEvent.TYPE_QUERY:
				return "QUERY";
			case AmiCenterQueryDsTrackerEvent.TYPE_QUERY_END:
				return "END_QUERY";
			case AmiCenterQueryDsTrackerEvent.TYPE_QUERY_END_ERROR:
				return "END_IN_ERROR";
			case AmiCenterQueryDsTrackerEvent.TYPE_QUERY_START:
				return "START_QUERY";
			case AmiCenterQueryDsTrackerEvent.TYPE_QUERY_STEP:
				return "QUERY_STEP";
			default:
				return SH.toString(b);
		}
	}
	public static String escapeVarName(String name) {
		if (isValidVariableName(name, false, false))
			return name;
		else if (name.indexOf('`') != -1)
			return SH.escape(name, '`', '\\', new StringBuilder(name.length() + 4).append('`')).append('`').toString();
		return new StringBuilder(name.length() + 2).append('`').append(name).append('`').toString();
	}
	public static StringBuilder escapeVarName(String name, StringBuilder sink) {
		if (isValidVariableName(name, false, false))
			return sink.append(name);
		else if (name.indexOf('`') != -1) {
			return SH.escape(name, '`', '\\', sink.append('`')).append('`');
		}
		return sink.append('`').append(name).append('`');
	}
	public static long getMillis(Number when) {
		if (when instanceof DateNanos) {
			return ((DateNanos) when).getTimeMillis();
		} else if (when instanceof DateMillis) {
			return ((DateMillis) when).getDate();
		}
		return when == null ? 0 : when.longValue();
	}

	@Deprecated
	public static OneToOne<String, Class> COLUMN_TYPES = new OneToOne<String, Class>();
	static {
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_INTEGER, Integer.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_LONG, Long.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_STRING, String.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_CHAR, Character.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_FLOAT, Float.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_DOUBLE, Double.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_UTC, DateMillis.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_UTCN, DateNanos.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_BOOLEAN, Boolean.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_BINARY, Bytes.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_SHORT, Short.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_BYTE, Byte.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_BIGINT, BigInteger.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_BIGDEC, BigDecimal.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_UUID, UUID.class);
		COLUMN_TYPES.put(AmiConsts.TYPE_NAME_COMPLEX, Complex.class);
	}

	public static void addTypes(BasicMethodFactory methodFactory) {
		//		methodFactory.addCaster(AmiCaster_String.INSTANCE);
		//		methodFactory.addCaster(AmiCaster_StringBuilder.INSTANCE);
		//		methodFactory.addCaster(AmiCaster_List.INSTANCE);
		//		methodFactory.addCaster(AmiCaster_Map.INSTANCE);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_INTEGER, Integer.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_LONG, Long.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_STRING, String.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_CHAR, Character.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_FLOAT, Float.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_DOUBLE, Double.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_UTC, DateMillis.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_UTCN, DateNanos.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_BOOLEAN, Boolean.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_BINARY, Bytes.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_SHORT, Short.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_BYTE, Byte.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_BIGINT, BigInteger.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_BIGDEC, BigDecimal.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_UUID, UUID.class);
		methodFactory.addVarType(AmiConsts.TYPE_NAME_COMPLEX, Complex.class);
		methodFactory.addVarType("Int", Integer.class);
		methodFactory.addVarType("Char", Character.class);
		methodFactory.addVarType("Enum", String.class);
	}
	synchronized public static AmiEncrypter initCertificate(ContainerTools tools) {
		final String key = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_TEXT);
		if (key != null) {
			LH.info(log, "Building key for encrypting/decrypting using property ", AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_TEXT);
			return new AmiEncrypter(key);
		}
		final File amiPersistAesKeyFile = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_FILE, new File("persist/amikey.aes"));
		final int amiPersistAesKeyStrength = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_STRENGTH, 128);
		if (amiPersistAesKeyStrength < 256)
			AmiUtils.logSecurityWarning("AES-STRENGTH BELOW 256  (Set " + AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_STRENGTH + "=256 to resolve this)");
		if (IOH.isntFile(amiPersistAesKeyFile)) {
			try {
				LH.info(log, "Creating key file for encrypting passwords: ", IOH.getFullPath(amiPersistAesKeyFile), " with ", amiPersistAesKeyStrength, "-bit strength.");
				LH.info(log, "We suggest moving this key to a safe location and updating the property ", AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_FILE, " accordingly.");
				IOH.ensureDir(amiPersistAesKeyFile.getParentFile());
				SecretKey tmpKey = AesEncryptUtils.generateKeyRandom(amiPersistAesKeyStrength);
				String key64 = AesEncryptUtils.encode64UrlSafe(tmpKey);
				IOH.writeText(amiPersistAesKeyFile, key64);
			} catch (Exception e) {
				throw new RuntimeException(
						"Error Creating key (see property " + AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_FILE + ")  " + IOH.getFullPath(amiPersistAesKeyFile), e);
			}
		}
		try {
			LH.info(log, "Reading key file for encrypting/decrypting: ", IOH.getFullPath(amiPersistAesKeyFile));
			AmiEncrypter r = new AmiEncrypter(amiPersistAesKeyFile);
			int bitDepth = r.getBitDetph();
			if (bitDepth != amiPersistAesKeyStrength)
				AmiUtils.logSecurityWarning("AES-STRENGTH MISMATCH: Property " + AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_FILE + " is set to " + amiPersistAesKeyStrength
						+ " but existing file " + IOH.getFullPath(amiPersistAesKeyFile) + " has a bit depth of: " + bitDepth);
			return r;
		} catch (Exception e) {
			throw new RuntimeException("Error reading key (see property " + AmiCommonProperties.PROPERTY_AMI_PERSIST_AES_KEY_FILE + ")  " + IOH.getFullPath(amiPersistAesKeyFile),
					e);
		}
	}
	public static ServerSocketEntitlements parseWhiteList(ContainerTools tools, PropertyController props, String property) {
		String value = props.getOptional(property);
		if (SH.isnt(value))
			return null;
		if (value.startsWith("file:")) {
			File file = new File(SH.stripPrefix(value, "file:", true));
			try {
				return new BasicServerSocketEntitlements(file);
			} catch (Exception e) {
				throw new RuntimeException("Error processing property " + property + ", Could not read file " + IOH.getFullPath(file), e);
			}
		} else if (value.startsWith("text:")) {
			String t = SH.stripPrefix(value, "text:", true);
			String[] text = t.indexOf('\n') != -1 ? SH.splitLines(t) : SH.split(',', t);
			return new BasicServerSocketEntitlements(text);
		} else if (value.startsWith("plugin:")) {
			String plugin = SH.stripPrefix(value, "plugin:", true);
			try {
				AmiServerSocketEntitlementsPlugin t = loadPlugin(plugin, "ServerSocketEntitlements", tools, props, AmiServerSocketEntitlementsPlugin.class);
				//t.init(tools, props);
				return t.createEntitlements();
			} catch (Exception e) {
				throw new RuntimeException("Error processing property " + property + ", Could not use plugin " + plugin, e);
			}
		} else
			throw new RuntimeException("Error processing property " + property
					+ ", Value should be either 'text:<comma_delimited_list_of_hosts' or 'file:<file_name>' or 'plugin:<class.name.of.entitlements.plugin>'");
	}
	public static int getDataLength(byte[] data, int pos) {
		byte type = ByteHelper.readByte(data, pos);
		switch (type) {
			case AmiDataEntity.PARAM_TYPE_NULL:
				return 1;
			case AmiDataEntity.PARAM_TYPE_BOOLEAN:
			case AmiDataEntity.PARAM_TYPE_LONG1:
			case AmiDataEntity.PARAM_TYPE_INT1:
			case AmiDataEntity.PARAM_TYPE_ENUM1:
				return 2;
			case AmiDataEntity.PARAM_TYPE_LONG2:
			case AmiDataEntity.PARAM_TYPE_INT2:
			case AmiDataEntity.PARAM_TYPE_ENUM2:
			case AmiDataEntity.PARAM_TYPE_CHAR:
				return 3;
			case AmiDataEntity.PARAM_TYPE_LONG3:
			case AmiDataEntity.PARAM_TYPE_INT3:
			case AmiDataEntity.PARAM_TYPE_ENUM3:
				return 4;
			case AmiDataEntity.PARAM_TYPE_LONG4:
			case AmiDataEntity.PARAM_TYPE_INT4:
			case AmiDataEntity.PARAM_TYPE_FLOAT:
				return 5;
			case AmiDataEntity.PARAM_TYPE_LONG5:
				return 6;
			case AmiDataEntity.PARAM_TYPE_LONG6:
			case AmiDataEntity.PARAM_TYPE_UTC6:
				return 7;
			case AmiDataEntity.PARAM_TYPE_LONG7:
				return 8;
			case AmiDataEntity.PARAM_TYPE_DOUBLE:
			case AmiDataEntity.PARAM_TYPE_LONG8:
			case AmiDataEntity.PARAM_TYPE_UTCN:
				return 9;
			case AmiDataEntity.PARAM_TYPE_COMPLEX:
			case AmiDataEntity.PARAM_TYPE_UUID:
				return 17;
			case AmiDataEntity.PARAM_TYPE_STRING:
				return ByteHelper.readInt(data, pos + 1) * 2 + 5;
			case AmiDataEntity.PARAM_TYPE_BINARY:
				return ByteHelper.readInt(data, pos + 1) + 5;
			case AmiDataEntity.PARAM_TYPE_ASCII_SMALL:
			case AmiDataEntity.PARAM_TYPE_ASCII_ENUM:
				return ByteHelper.readByte(data, pos + 1) + 2;
			case AmiDataEntity.PARAM_TYPE_ASCII:
				return ByteHelper.readInt(data, pos + 1) + 5;
			default:
				throw new RuntimeException("bad type: " + type);
		}
	}
	public static void mergeTo(BasicCalcTypes sink, CalcTypes other) {
		for (String i : other.getVarKeys()) {
			Class<?> type = sink.getType(i);
			sink.putType(i, OH.getWidestIgnoreNull(type, other.getType(i)));
		}
	}
	//to string no null
	public static String snn(Object r, String onnull) {
		return r == null ? onnull : s(r);
	}

	//to string
	public static String s(Object r) {
		if (r instanceof CharSequence)
			return r.toString();
		if (r instanceof Table) {
			return join((Table) r, new StringBuilder()).toString();
		} else if (r instanceof Map) {
			return join((Map) r, new StringBuilder()).toString();
		} else if (r instanceof Collection) {
			return join((Collection) r, new StringBuilder()).toString();
		}
		return DerivedHelper.toString(r);
	}
	public static StringBuilder s(Object r, StringBuilder sb) {
		if (r instanceof CharSequence)
			return sb.append((CharSequence) r);
		if (r instanceof Table) {
			return join((Table) r, sb);
		} else if (r instanceof Map) {
			return join((Map) r, sb);
		} else if (r instanceof Collection) {
			return join((Collection) r, sb);
		}
		return DerivedHelper.toString(r, sb);
	}
	//to string
	public static String sJson(Object r) {
		if (r == null)
			return null;
		if (r instanceof CharSequence)
			return SH.s(r);
		return AMI_JSON_CONVERTER.objectToString(r);
	}

	public static StringBuilder sJson(Object r, StringBuilder sb) {
		if (r == null)
			return sb.append("null");
		if (r instanceof CharSequence)
			return sb.append(SH.s(r));
		return sb.append(AMI_JSON_CONVERTER.objectToString(r));
	}

	public static StringBuilder toConstString(Object value, StringBuilder sink) {
		DerivedCellCalculatorConst.toString(value, sink);
		return sink;
	}

	public static final Formatter FORMATTER = new Formatter() {

		@Override
		public String get(Object key) {
			return AmiUtils.s(key);
		}

		@Override
		public void format(Object value, StringBuilder sb) {
			AmiUtils.s(value, sb);
		}

		@Override
		public String format(Object value) {
			return AmiUtils.s(value);
		}

		@Override
		public void format(Object value, Writer out) throws IOException {
			out.append(AmiUtils.s(value));
		}

		@Override
		public Formatter clone() {
			return this;
		}

		@Override
		public boolean canFormat(Object obj) {
			return false;
		}

		@Override
		public boolean canParse(String text) {
			return false;
		}

		@Override
		public Object parse(String text) {
			return null;
		}

		@Override
		public String getPattern() {
			return null;
		}

	};
	private static final Formatter[] FORMATS_ARRAY = new Formatter[1024];
	static {
		AH.fill(FORMATS_ARRAY, FORMATTER);
	}

	public static StringBuilder join(Map<?, ?> map, StringBuilder sb) {
		if (map.isEmpty())
			return sb.append("{}");
		sb.append("{");

		boolean first = true;
		int start, end;

		Object o;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else
				sb.append(',').append(' ');

			o = e.getKey();
			if (o instanceof CharSequence) {
				start = sb.length();
				AmiUtils.s(o, sb);
				end = sb.length();
				SH.doubleQuoteInPlace(sb, start, end);
			} else
				AmiUtils.s(o, sb);

			sb.append(':');

			o = e.getValue();

			if (o instanceof CharSequence) {
				start = sb.length();
				AmiUtils.s(o, sb);
				end = sb.length();
				SH.doubleQuoteInPlace(sb, start, end);
			} else
				AmiUtils.s(o, sb);
		}
		sb.append("}");
		return sb;
	}
	public static StringBuilder join(Collection<?> tokens, StringBuilder r) {
		if (tokens.isEmpty())
			return r.append("[]");
		Iterator<?> i = tokens.iterator();
		r.append("[");
		int start, end;
		Object o;
		if (i.hasNext()) {
			o = i.next();
			if (o instanceof CharSequence) {
				start = r.length();
				AmiUtils.s(o, r);
				end = r.length();
				SH.doubleQuoteInPlace(r, start, end);
			} else
				AmiUtils.s(o, r);

		}

		while (i.hasNext()) {
			o = i.next();
			r.append(',').append(' ');
			if (o instanceof CharSequence) {
				start = r.length();
				AmiUtils.s(o, r);
				end = r.length();
				SH.doubleQuoteInPlace(r, start, end);
			} else
				AmiUtils.s(o, r);

		}
		r.append("]");
		return r;
	}

	private static StringBuilder join(Table r, StringBuilder sb) {
		return TableHelper.toString(r, "", TableHelper.SHOW_ALL, sb, SH.NEWLINE, TableHelper.DEFAULT_VERTICLE, TableHelper.DEFAULT_HORIZONTAL, TableHelper.DEFAULT_CROSS,
				TableHelper.DEFAULT_MAX_LENGTH, null, FORMATS_ARRAY);
	}
	public static int toTimeout(long timeout, int defaultTimeout) {
		if (timeout == AmiCenterQueryDsRequest.NO_TIMEOUT)
			return AmiCenterQueryDsRequest.MAX_TIMEOUT;
		else if (timeout == AmiCenterQueryDsRequest.USE_DEFAULT_TIMEOUT)
			return defaultTimeout;
		else
			return (int) MH.clip(timeout, 0, Integer.MAX_VALUE);
	}
	public static int getDefaultTimeout(ContainerTools tools) {
		long r = SH.parseDurationTo(tools.getOptional(AmiCommonProperties.PROPERTY_AMI_AMISCRIPT_DEFAULT_TIMEOUT, AmiCommonProperties.DEFAULT_AMISCRIPT_DEFAULT_TIMEOUT),
				TimeUnit.MILLISECONDS);
		if (r <= 0)
			throw new RuntimeException(AmiCommonProperties.PROPERTY_AMI_AMISCRIPT_DEFAULT_TIMEOUT + " must be >=0, not: " + r + " millis");
		return (int) MH.clip(r, 0, Integer.MAX_VALUE);
	}

	public static int getDefaultLimit(ContainerTools tools) {
		int r = tools.getOptional(AmiCommonProperties.PROPERTY_AMI_AMISCRIPT_DEFAULT_LIMIT, AmiCommonProperties.DEFAULT_AMISCRIPT_LIMIT);
		if (r < 0)
			throw new RuntimeException(AmiCommonProperties.PROPERTY_AMI_AMISCRIPT_DEFAULT_LIMIT + " must be a positive integer, not: " + r);
		return (int) MH.clip(r, 0, Integer.MAX_VALUE);
	}

	public static AmiCalcFrameStack getExecuteInstance(FlowControlPause fcp) {
		PauseStack fp = fcp.getStack();
		AmiCalcFrameStack r = null;
		while (fp != null) {
			AmiCalcFrameStack t = getExecuteInstance2(fp.getLcvs());
			if (t != null)
				r = t;
			fp = fp.getNext();
		}
		return r;
	}
	public static AmiCalcFrameStack getExecuteInstance2(CalcFrameStack sf) {
		for (; sf != null; sf = sf.getParent())
			if (sf instanceof AmiCalcFrameStack)
				return (AmiCalcFrameStack) sf;
		return null;
	}
	public static byte parseDbPermissions(String value) {
		byte r = 0;
		for (String s : SH.split(',', value)) {
			s = SH.trim(s);
			if ("READ".equalsIgnoreCase(s))
				r |= AmiCenterQueryDsRequest.PERMISSIONS_READ;
			else if ("WRITE".equalsIgnoreCase(s))
				r |= AmiCenterQueryDsRequest.PERMISSIONS_WRITE;
			else if ("ALTER".equalsIgnoreCase(s))
				r |= AmiCenterQueryDsRequest.PERMISSIONS_ALTER;
			else if ("EXECUTE".equalsIgnoreCase(s))
				r |= AmiCenterQueryDsRequest.PERMISSIONS_EXECUTE;
		}
		return r;
	}

	//If value is a string, then parse it like a const, ex: "test" -> test    123L -> (long)123
	//Type will be amiscript safe type
	public static Tuple2<Class<?>, Object> toAmiscriptVariable(Object value, String descriptionPrefix, String description) {
		Object r;
		if (value instanceof CharSequence) {
			String str = value.toString();
			try {
				r = SH.parseConstant(str);
			} catch (Exception e) {
				LH.warning(log, descriptionPrefix, description, "=", value,
						" is malformatted so assuming a string.  You should explicitly define a string by surrounding in double quotes");
				r = str;
			}
		} else
			r = value;
		if (r == null)
			return new Tuple2<Class<?>, Object>(String.class, r);
		Class<?> type = AmiUtils.METHOD_FACTORY.findType(r.getClass());
		return new Tuple2<Class<?>, Object>(type, r);
	}
	public static void logSecurityWarning(String msg) {
		final String msg2 = "SECURITY-WARNING: " + msg;
		System.err.println(msg2);
		LH.warning(log, msg2);
	}
	public static void toMessage(AmiCenterQueryDsResponse action, Formatter f, StringBuilder sb) {
		Class<?> returnType = action.getReturnType();
		boolean hasReturnValue = returnType != null && returnType != Object.class && returnType != Void.class;
		if (action.getOk()) {
			if (action.getMessage() != null)
				sb.append("(").append(action.getMessage()).append(")").append(SH.NEWLINE);
			sb.append("(");
			boolean needsComma = false;
			if (CH.isntEmpty(action.getTables())) {
				if (needsComma)
					sb.append(", ");
				sb.append("RETURNED ");
				int rows = 0;
				for (Table i : action.getTables()) {
					rows += i.getSize();
				}
				SH.quantify(rows, "ROW", "ROWS", sb);
				sb.append(", ");
				SH.quantify(action.getTables().size(), "TABLE", "TABLES", sb);
				needsComma = true;
			}
			if (CH.isntEmpty(action.getGeneratedKeys())) {
				if (needsComma)
					sb.append(", ");
				sb.append("RETURNED ");
				SH.quantify(action.getGeneratedKeys(), "GENERATED KEY", "GENERATED KEYS", sb);
				needsComma = true;
			}
			if (action.getRowsEffected() != -1L) {
				if (needsComma)
					sb.append(", ");
				sb.append("AFFECTED ");
				SH.quantify(action.getRowsEffected(), "ROW", "ROWS", sb);
				needsComma = true;
			}
			if (hasReturnValue) {
				if (needsComma)
					sb.append(", ");
				sb.append("1 VALUE");
				needsComma = true;
			}
			if (action.getDurrationNanos() > 0) {
				if (needsComma)
					sb.append(", ");
				sb.append("EXECUTED IN ").append((action.getDurrationNanos() / 1000) / 1000d).append(" MILLISECONDS");
				needsComma = true;
			}
			if (needsComma) {
				sb.append(")");
				sb.append(SH.NEWLINE);
			} else
				sb.setLength(sb.length() - 1);
		}
		if (action.getTrackedEvents() != null) {
			sb.append("QUERY PLAN:");
			sb.append(SH.NEWLINE);
			for (AmiCenterQueryDsTrackerEvent s : action.getTrackedEvents()) {
				int start = sb.length();
				sb.append("  [");
				sb.append(f.format(new Date(s.getTimestamp())));
				sb.append("] ");
				int len = sb.length() - start;
				sb.append(AmiUtils.toStringForTrackedEventType(s.getType()));
				sb.append(' ');
				String str = s.getString();
				if (SH.indexOf(str, '\n', 0) != -1) {
					String prefix = '\n' + SH.repeat(' ', len);
					sb.append(SH.replaceAll(s.getString(), '\n', prefix));
				} else
					sb.append(s.getString());
				sb.append(SH.NEWLINE);
			}
		}
	}
	public static Object getReturnValue(AmiCenterQueryDsResponse action) {
		int n = action.getReturnValueTablePos();
		return n == -1 ? action.getReturnValue() : action.getTables().get(n);
	}
	public static void setReturnValues(ContainerTools ct, AmiCenterQueryDsResponse sink, Class<?> returnType, Object returnValue, List<Table> tables) {
		sink.setTables(tables);
		if (returnType == null || !FlowControl.class.isAssignableFrom(returnType)) {
			final int n = CH.indexOfIdentity(tables, returnValue);
			sink.setReturnType(returnType);
			sink.setReturnValueTablePos(n);
			if (n == -1 && returnValue != null) {
				final ObjectToByteArrayConverter oc = (ObjectToByteArrayConverter) ct.getServices().getConverter();
				if (oc.getConverterNoThrow(returnType) != null)
					sink.setReturnValue(returnValue);
				else if (oc.getConverterNoThrow(returnValue.getClass()) != null)
					sink.setReturnValue(returnValue);
				else {
					sink.setReturnType(returnType);
					String type = AmiUtils.METHOD_FACTORY.forType(returnValue.getClass());
					if (sink.getMessage() == null) {
						if (type == null)
							type = returnValue.getClass().getSimpleName();
						sink.setMessage("RETURNED NULL BECAUSE '" + type + "' OBJECTS CAN NOT BE SENT TO CLIENT");
					}
				}
			}
		}
		sink.setReturnValueTablePos(-1);

	}

	public static boolean checkHexColor(String s) {
		if (s == null || s.length() != 7 || s.charAt(0) != '#')
			return false;
		return true;
	}

	/*
	 * Given a hex color code, returns which type of logo should be used (white, black or color)
	 */
	public static final byte WHITE = 1;
	public static final byte BLACK = 2;
	public static final byte COLOR1 = 3;
	public static final byte COLOR2 = 4;

	public static Byte getBestImageType(String hexColor) {
		int alpha = ColorHelper.getAFromHex(hexColor);
		int color = blend(ColorHelper.getRFromHex(hexColor), alpha) + blend(ColorHelper.getGFromHex(hexColor), alpha) + blend(ColorHelper.getBFromHex(hexColor), alpha) * 2;
		int colorCutoff = color / 4;
		if (colorCutoff > 210)
			return COLOR1;
		else if (colorCutoff > 150)
			return BLACK;
		else if (colorCutoff > 80)
			return WHITE;
		else
			return COLOR2;
	}

	private static int blend(int c, int alpha) {
		return ColorHelper.blend(c, 255, alpha);
	}

	public static Object getNodeValue(Node node, SqlDerivedCellParser parser, CalcFrameStack sf) {
		DerivedCellCalculator calc = parser.toCalc(node, sf);
		Object object = calc.get(sf);
		return object;
	}
	//	public static void findAndFixDsName(String o, String key, Map<String, Object> sink) {
	//		OH.assertNotNull(key);
	//		sink.put(key, fixUseDsLine(o));
	//	}

	public static boolean fixUseDsLines(List<String> lines) {
		boolean changed = false;
		for (int i = 0; i < lines.size(); i++) {
			String before = lines.get(i);
			String after = AmiUtils.fixUseDsLine(before);
			lines.set(i, after);
			if (!SH.equals(before, after))
				changed = true;
		}
		return changed;
	}

	/**
	 * Takes a line, adds double quote to `use ds =` pattern, return a new line. Returns the original if there is no change or line has no content
	 * 
	 **/
	public static String fixUseDsLine(String line) {
		// rules:
		// 1. Disregards syntax validity
		// 2. Disregards ds name validity
		if (!SH.is(line))
			return line;
		StringCharReader scr = new StringCharReader();
		scr.setCaseInsensitive(true);
		StringBuilder sb = new StringBuilder();
		//		boolean isStarComment = false;
		//		boolean changed = false;
		// each line can contain multiple statements delimited by ;
		List<String> statements = SH.splitToList(";", line);
		boolean statementChanged = false;
		for (int s = 0; s < statements.size(); s++) {
			String statement = statements.get(s);
			if (!SH.is(statement))
				continue;
			scr.reset(statement);
			sb.setLength(0);
			//				// check comments
			//				if (isStarComment) {
			//					if (scr.readUntilSequence("*/", sb) != -1) {
			//						isStarComment = false;
			//					}
			//					// continues until */ is found
			//					break;
			//				}
			//				if (scr.readUntilSequence("/*", sb) != -1) {
			//					isStarComment = true;
			//					break;
			//				}
			//				if (scr.readUntilSequence("//", sb) != -1) {
			//					break;
			//				}
			//				// end check comments
			// begin sequence check
			// use ds
			//    ^
			if (scr.readUntilSequenceAndSkip("use ", sb) == -1) {
				continue;
			}
			scr.skip(ExpressionParserHelper.WS);
			if (scr.readUntilSequenceAndSkip("ds", sb) == -1 && scr.readUntilSequenceAndSkip("datasource", sb) == -1) {
				continue;
			}
			scr.skip(ExpressionParserHelper.WS);
			if (!scr.expectNoThrow('=')) {
				continue;
			}
			scr.skip(ExpressionParserHelper.WS);
			if (scr.peakOrEof() == -1) {
				continue;
			}
			// try to get ds name
			if (scr.peak() == '"') {
				scr.readChar();
				// expect ending double quote
				if (scr.readUntilAny("\"", true, sb) == -1) {
					// EOF and couldn't find ending dq
					continue;
				} else {
					// already contains double quotes
					continue;
				}
			} else {
				// read ds name into sb
				scr.mark();
				sb.setLength(0);
				scr.readUntilAny(ExpressionParserHelper.WS, true, sb);
			}
			//				if (!AmiUtils.isValidVariableName(sb, false, false, false)) {
			//					continue; // ignore invalid var name
			//				}
			scr.returnToMark();
			// insert quotes
			statement = SH.splice(statement, scr.getCountRead(), sb.length(), SH.doubleQuote(sb.toString()));
			statements.set(s, statement);
			statementChanged = true;
		}
		if (statementChanged) {
			// rejoin the statements
			String newLine = SH.j(';', statements);
			return newLine;
		}
		// no change
		return line;
	}

}
