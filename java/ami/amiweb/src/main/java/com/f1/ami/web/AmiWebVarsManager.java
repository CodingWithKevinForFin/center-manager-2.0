package com.f1.ami.web;

import java.text.NumberFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiCommonProperties;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.auth.AmiAuthUser;
import com.f1.ami.web.auth.AmiSsoSession;
import com.f1.ami.web.auth.AmiSsoSessionImpl;
import com.f1.base.CalcFrame;
import com.f1.container.ContainerTools;
import com.f1.suite.web.WebState;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManager;
import com.f1.utils.CH;
import com.f1.utils.EH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TextMatcher;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.concurrent.HasherMap;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebVarsManager {
	public static final String DEFAULT_TRANSIENT_ID_PREFIX = "TRANSIENT_";
	private static final TextMatcher[] EMPTY_TEXT_MATCHER = new TextMatcher[0];
	private static final Logger log = LH.get();

	public static final byte SOURCE_PROPERTY = 1;
	public static final byte SOURCE_USER_PROFILE = 2;
	public static final byte SOURCE_PREDEFINED = 3;
	public static final byte SOURCE_DASHBOARD = 4;
	public static final byte SOURCE_PLUGIN = 5;
	public static final String DEFAULT_NUMBER_NEGATIVE_FORMAT = "sign";
	public static final int DEFAULT_NUMBER_DECIMAL_PRECISION = 2;
	public static final String DEFAULT_NUMBER_SEPARATOR = "1,234,567,890.123";
	public static final String DEFAULT_DATE_TIME_FORMAT = "M/dd/yyyy h:mm a";
	public static final String DEFAULT_DATE_FORMAT = "M/dd/yyyy";
	public static final String DEFAULT_TIME_FORMAT = "H:mm";
	public static final String DEFAULT_LANGUAGE = "ENGLISH";
	public static final String DEFAULT_TIME_ZONE = "EST5EDT";
	public static final int DEFAULT_SCI_NOT_NUM_DIGITS_LEFT = 1;
	public static final int DEFAULT_SCI_NOT_NUM_DIGITS_RIGHT = 3;
	public static final String DEFAULT_CUSTOM_USER_MENU_TITLE = "User Menus";
	private final MutableCalcFrame globalVars = new MutableCalcFrame();
	private final HasherMap<String, Byte> globalVarSources = new HasherMap<String, Byte>();
	private final PortletManager portletManager;
	private final String sessionId;
	private final String pageId;
	private String timeZoneId;
	private TimeZone timezone;
	private Locale locale;
	private String language;
	private final AmiWebUserSettingsManager store;
	private String dateFormat;
	private String dateTimeFormat;
	private String dateTimeSecsFormat;
	private String dateTimeMillisFormat;
	private String dateTimeMicrosFormat;
	private String dateTimeNanosFormat;
	private String numberSeparator;
	private Locale numberSeparatorLocale;
	private NumberFormat numberSeparatorFormatter;
	private Integer numberDecimalPrecision;
	private String numberNegativeFormat;
	private Integer sciNotNumDigitsLeft;
	private Integer sciNotNumDigitsRight;
	private String spreadSheetFormatOption;
	private String autoApplyUserPrefs;
	final private long starttime;
	final private String sessionRemoteAddress;
	final private AmiWebService service;
	final private boolean supportLegacyAmiScriptVarnames;
	private AmiWebTimeFormats timeFormats = AmiWebTimeFormats.DEFAULT;
	private String userPrefsNamespace;
	private String browserTitle;
	private String customUserMenuTitle;

	final private String username;
	final private boolean userIsAdmin;
	final private boolean userIsDev;
	final private BasicMultiMap.List<String, TextMatcher> permittedLayoutPatternsBySource = new BasicMultiMap.List<String, TextMatcher>();
	final private String defaultLayoutSource;
	final private String defaultLayoutName;
	final private Map<String, Object> customPrefs = new HashMap<String, Object>();

	private ContainerTools tools;

	private final AmiSsoSession ssoSession;
	private int userMaxSessions;
	private String transientIdPrefix = DEFAULT_TRANSIENT_ID_PREFIX;

	public AmiWebVarsManager(PortletManager pm, AmiWebService service, CalcFrame amiscriptProperties, AmiWebUserSettingsManager userSettingsManager) {
		HashMap<String, Object> userProfileSettings = new HashMap<String, Object>();
		this.service = service;
		this.portletManager = pm;
		final WebState ws = this.portletManager.getState();
		this.tools = service.getPortletManager().getTools();
		this.store = userSettingsManager;
		this.sessionId = (String) ws.getSessionId();
		this.pageId = (String) ws.getPgId();
		this.sessionRemoteAddress = ws.getWebStatesManager() == null ? null : ws.getWebStatesManager().getRemoteAddress();
		this.starttime = tools.getNow();
		this.username = ws.getUserName();
		OH.assertNotNull(username, "username");
		OH.assertNotNull(pageId, "pageId");
		this.supportLegacyAmiScriptVarnames = tools.getOptional(AmiWebProperties.PROPERTY_AMI_SUPPORT_LEGACY_AMISCRIPT_VARNAMES, Boolean.FALSE);

		//+++Read user settings from cloud (see ami.users.file)+++
		//		AmiWebCloudManager cloudManager = service.getCloudManager();
		//		if (cloudManager.getCloudUsers().contains(this.username)) {
		//			userProfileSettings.putAll(cloudManager.loadCloudUser(this.username).getProperties());
		//		}

		//+++Process variables from local.properties files, with the amiscript.variable stripped off+++
		for (String i : amiscriptProperties.getVarKeys())
			addGlobalVar(i, amiscriptProperties.getValue(i), (Class) amiscriptProperties.getType(i), SOURCE_PROPERTY);

		//+++Process variables from The AmiAuthenticator plugin+++
		for (Entry<String, Object> attr : ws.getUserAttributes().entrySet()) {
			final String key = attr.getKey();
			final Object value = attr.getValue();
			if (key.startsWith("amivar_")) {//legacy
				final String name = "user." + SH.stripPrefix(key, "amivar_", true);
				addGlobalVar(name, String.class.cast(value), String.class, SOURCE_USER_PROFILE);
			} else if (key.startsWith("amiscript.variable.")) {
				final String name = SH.stripPrefix(key, "amiscript.variable.", true);
				Tuple2<Class<?>, Object> val = AmiUtils.toAmiscriptVariable(value, "User Attribute ", key);
				addGlobalVar(name, val.getB(), (Class) val.getA(), SOURCE_USER_PROFILE);
			} else if (isSetting(key))
				userProfileSettings.put(key, value);
			userProfileSettings.put(key, value);
		}

		addGlobalVar("session", service, AmiWebService.class, SOURCE_PREDEFINED);
		if (this.supportLegacyAmiScriptVarnames) {
			addGlobalVar("session.id", this.pageId, String.class, SOURCE_PREDEFINED);
			addGlobalVar("user.username", this.username, String.class, SOURCE_PREDEFINED);
			addGlobalVar("page.uid", ((BasicPortletManager) pm).getPageUid(), Long.class, SOURCE_PREDEFINED);
		}

		addGlobalVar("__LOGINID", this.sessionId, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__SESSIONID", this.pageId, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__LOADTIME", this.starttime, Long.class, SOURCE_PREDEFINED);
		addGlobalVar("__ADDRESS", this.sessionRemoteAddress, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__USERNAME", this.username, String.class, SOURCE_PREDEFINED);

		String s = getLayouts(userProfileSettings);
		if (SH.is(s)) {
			String parts[] = SH.split(',', s);
			for (int i = 0; i < parts.length; i++) {
				String trim = SH.trim(parts[i]);
				String source = SH.toUpperCase(SH.beforeFirst(trim, ':', AmiWebConsts.LAYOUT_SOURCE_CLOUD));
				String name = SH.afterFirst(trim, ':', trim);
				TextMatcher m = TextMatcherFactory.FULL_CASE_SENSETIVE.toMatcher(name);
				if ("*".equals(source)) {
					this.permittedLayoutPatternsBySource.putMulti(AmiWebConsts.LAYOUT_SOURCE_ABSOLUTE, m);
					this.permittedLayoutPatternsBySource.putMulti(AmiWebConsts.LAYOUT_SOURCE_CLOUD, m);
					this.permittedLayoutPatternsBySource.putMulti(AmiWebConsts.LAYOUT_SOURCE_LOCAL, m);
					this.permittedLayoutPatternsBySource.putMulti(AmiWebConsts.LAYOUT_SOURCE_SHARED, m);
				} else
					this.permittedLayoutPatternsBySource.putMulti(source, m);
			}
		}
		String defaultLayout = getDefaultLayout(userProfileSettings);//#1 PRIORITY: user's DEFAULT_LAYOUT
		if (SH.isnt(defaultLayout)) {
			defaultLayout = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_DEFAULT_LAYOUT); //#2 PRIORITY: local.properties ami.web.default.layout
			if (SH.isnt(defaultLayout)) {
				defaultLayout = getSetting(AmiWebConsts.USER_SETTING_AMI_LAYOUT_SHARED);//#3 LEGACY - user's ami_layout_shared
				if (SH.isnt(defaultLayout)) {
					defaultLayout = tools.getOptional(AmiWebProperties.PROPERTY_AMI_WEB_DEFAULT_LAYOUT_SHARED); //#4 LEGACY - local.properties.ami.web.default.layout.shared
				}
			}
		}
		if (SH.is(defaultLayout)) {
			String trim = SH.trim(defaultLayout);
			this.defaultLayoutSource = SH.toUpperCase(SH.beforeFirst(trim, ':', AmiWebConsts.LAYOUT_SOURCE_CLOUD));
			this.defaultLayoutName = SH.afterFirst(trim, ':', trim);
		} else {
			this.defaultLayoutSource = null;
			this.defaultLayoutName = null;
		}

		//		if (!cloudManager.hasAdmins() && tools.getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_TO_ADMIN, Boolean.FALSE)) {
		//			AmiUtils.logSecurityWarning("Converting user \"" + username + "\" to admin with developer access because the property " + AmiWebProperties.PROPERTY_AMI_DEFAULT_TO_ADMIN
		//					+ " has been set to true and no users have been set to admin. Please set this to false and add an admin user.");
		//			this.userIsAdmin = true;
		//			this.userIsDev = true;
		//		} else {
		this.userIsAdmin = getService().getWebState().isAdmin();
		this.userIsDev = getService().getWebState().isDev();
		this.userMaxSessions = getService().getWebState().getMaxSessions();
		//		}
		addGlobalVar("__ISDEV", this.isUserDev(), Boolean.class, SOURCE_PREDEFINED);
		addGlobalVar("__ISADMIN", this.isUserAdmin(), Boolean.class, SOURCE_PREDEFINED);
		addGlobalVar("__MAXSESSIONS", this.getMaxSessions(), Integer.class, SOURCE_PREDEFINED);
		AmiSsoSession t = getSsoSession(userProfileSettings);
		if (t == null)
			t = new AmiSsoSessionImpl(null, null, null, null, Collections.EMPTY_MAP);
		this.ssoSession = t;
	}
	public int getMaxSessions() {
		return this.userMaxSessions;
	}
	public boolean isUserAdmin() {
		return userIsAdmin;
	}
	public boolean isUserDev() {
		return userIsDev;
	}

	public String getSetting(String property) {
		return this.store.getSettingString(property);
	}

	public String getUsername() {
		return this.username;
	}
	public String getUserDefaultLayoutSource() {//DEFAULT_LAYOUT
		return this.defaultLayoutSource;
	}
	public String getUserDefaultLayoutName() {//DEFAULT_LAYOUT
		return this.defaultLayoutName;
	}

	public <T> void addGlobalVar(String key, T value, Class<T> clazz, byte source) {
		this.globalVars.putTypeValue(key, clazz, value);
		this.globalVarSources.put(key, source);
		if (this.service.getScriptManager() != null)
			this.service.getScriptManager().addConstValue(key, value, clazz);
	}
	protected void onPageRefreshed(BasicPortletManager bpm) {
		if (this.supportLegacyAmiScriptVarnames)
			this.globalVars.putValue("page.uid", bpm.getPageUid());
	}

	public String getSessionId() {
		return sessionId;
	}
	public long getSessionStarttime() {
		return starttime;
	}
	public String getTimeZoneId() {
		return this.timeZoneId;
	}
	public TimeZone getTimeZone() {
		return this.timezone;
	}
	public void setTimeZoneId(String timezoneId) {
		TimeZone tz = EH.getTimeZoneNoThrow(timezoneId);
		if (tz == null) {
			this.timezone = TimeZone.getDefault();
			this.timeZoneId = this.timezone.getID();
			LH.warning(log, this.portletManager.getUserName(), " Bad timezone: timeZone=", timezoneId, " defaulting to " + this.timezone);
		} else {
			this.timezone = tz;
			this.timeZoneId = timezoneId;
		}
	}
	public void setLanguage(String language) {
		this.locale = EH.getLocaleByLanguage(language);
		this.language = language == null ? language : language.toUpperCase();
	}
	public String getLanguage() {
		return this.language;
	}
	public Locale getLocale() { // language locale
		return this.locale;
	}
	public String getDateFormat() {
		return dateFormat;
	}
	public void setDateFormat(String dateFormat) {
		if (OH.eq(dateFormat, this.dateFormat))
			return;
		this.dateFormat = dateFormat;
		this.getService().getUserFormStyleManager().setDateDisplayFormat(dateFormat);
		buildDatetimeformats();
	}
	private void buildDatetimeformats() {
		this.dateTimeFormat = this.dateFormat + " " + this.timeFormats.timeFormat;
		this.dateTimeSecsFormat = this.dateFormat + " " + this.timeFormats.timeFormatSeconds;
		this.dateTimeMillisFormat = this.dateFormat + " " + this.timeFormats.timeFormatMillis;
		this.dateTimeMicrosFormat = this.dateFormat + " " + this.timeFormats.timeFormatMicros;
		this.dateTimeNanosFormat = this.dateFormat + " " + this.timeFormats.timeFormatNanos;

	}
	public AmiWebTimeFormats getTimeFormats() {
		return timeFormats;
	}
	public String getTimeFormat() {
		return timeFormats.timeFormat;
	}
	public String getTimeWithSecondsFormat() {
		return timeFormats.timeFormatSeconds;
	}
	public String getTimeWithMillisecondsFormat() {
		return timeFormats.timeFormatMillis;
	}
	public String getTimeWithMicrosecondsFormat() {
		return timeFormats.timeFormatMicros;
	}
	public String getTimeWithNanosecondsFormat() {
		return timeFormats.timeFormatNanos;
	}
	public void setTimeFormats(AmiWebTimeFormats timeFormats) {
		this.timeFormats = timeFormats;
		// sets it in user form style manager so query form can access it later
		getService().getUserFormStyleManager().setTimeDisplayFormat(timeFormats.timeFormat);
		buildDatetimeformats();
	}
	private void setTimeFormat(String string) {
		setTimeFormats(AmiWebTimeFormats.getByTimeFormat(string, AmiWebTimeFormats.DEFAULT));
	}
	public String getDateTimeFormat() {
		return dateTimeFormat;
	}
	public String getNumberSeparator() {
		return numberSeparator;
	}
	public void setNumberSeparator(String numberSeparator) {
		this.numberSeparator = numberSeparator;
		setNumberSeparatorLocale(AmiWebFormatterManager.NUMBER_FORMATS_2_LOCALES.get(numberSeparator));
	}
	public Locale getNumberSeparatorLocale() {
		return this.numberSeparatorLocale;
	}
	private void setNumberSeparatorLocale(Locale numberSeparatorLocale) {
		this.numberSeparatorLocale = numberSeparatorLocale;
		this.numberSeparatorFormatter = NumberFormat.getInstance(numberSeparatorLocale);
	}
	public NumberFormat getNumberSeparatorFormatter() {
		return numberSeparatorFormatter;
	}
	public Integer getNumberDecimalPrecision() {
		return numberDecimalPrecision;
	}
	public void setNumberDecimalPrecision(Integer numberDecimalPrecision) {
		this.numberDecimalPrecision = numberDecimalPrecision;
	}
	public String getNumberNegativeFormat() {
		return numberNegativeFormat;
	}
	public void setNumberNegativeFormat(String numberNegativeFormat) {
		this.numberNegativeFormat = numberNegativeFormat;
	}
	public void applyUserVariables() {
		setTimeZoneId(getValue(AmiWebConsts.USER_SETTING_TIME_ZONE, AmiCommonProperties.DEFAULT_USER_TIMEZONE));
		setLanguage(getValue(AmiWebConsts.USER_SETTING_LANGUAGE, DEFAULT_LANGUAGE));
		setDateFormat(getValue(AmiWebConsts.USER_SETTING_DATE_FORMAT, DEFAULT_DATE_FORMAT));
		setTimeFormat(getValue(AmiWebConsts.USER_SETTING_TIME_FORMAT, DEFAULT_TIME_FORMAT));
		setNumberSeparator(getValue(AmiWebConsts.USER_SETTING_NUMBER_SEPARATOR, DEFAULT_NUMBER_SEPARATOR));
		setNumberDecimalPrecision(getValue(AmiWebConsts.USER_SETTING_NUMBER_DECIMAL_PRECISION, DEFAULT_NUMBER_DECIMAL_PRECISION));
		setNumberNegativeFormat(getValue(AmiWebConsts.USER_SETTING_NUMBER_NEGATIVE_FORMAT, DEFAULT_NUMBER_NEGATIVE_FORMAT));
		setSciNotNumDigitsLeft(getValue(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_LEFT, DEFAULT_SCI_NOT_NUM_DIGITS_LEFT));
		setSciNotNumDigitsRight(getValue(AmiWebConsts.USER_SETTING_SCI_NOT_NUM_DIGITS_RIGHT, DEFAULT_SCI_NOT_NUM_DIGITS_RIGHT));
		setSpreadSheetFormatOption(getValue(AmiWebConsts.USER_SETTING_SPREAD_SHEET_FORMAT_OPTION, AmiWebConsts.ALWAYS));
		setAutoApplyuserPrefs(getValue(AmiWebConsts.USER_SETTING_AUTOAPPLY_USERPREFS, AmiWebConsts.ASK));
		addGlobalVar("__TIMEZONE", this.timeZoneId, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__FORMAT_DATE", this.dateFormat, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__FORMAT_TIME", this.timeFormats.timeFormat, String.class, SOURCE_PREDEFINED);
		addGlobalVar("__FORMAT_NUMBER_DECIMALS", this.numberDecimalPrecision, Integer.class, SOURCE_PREDEFINED);
	}
	private String getValue(String key, String dflt) {
		String val = this.store.getSettingString(key);
		return SH.is(val) ? val : tools.getOptional(AmiWebProperties.PREFIX_PROPERTY_AMI_DEFAULT_PREFIX + key, dflt);
	}
	private int getValue(String key, int dflt) {
		String storedLanguage = this.store.getSettingString(key);
		return SH.is(storedLanguage) ? Integer.parseInt(storedLanguage) : tools.getOptional(AmiWebProperties.PREFIX_PROPERTY_AMI_DEFAULT_PREFIX + key, dflt);
	}
	public String getDateTimeWithSecondsFormat() {
		return this.dateTimeSecsFormat;
	}
	public String getDateTimeWithMillisecondsFormat() {
		return this.dateTimeMillisFormat;
	}
	public String getDateTimeWithMicrosecondsFormat() {
		return this.dateTimeMicrosFormat;
	}
	public String getDateTimeWithNanosecondsFormat() {
		return this.dateTimeNanosFormat;
	}
	public Integer getSciNotNumDigitsLeft() {
		return sciNotNumDigitsLeft;
	}
	public void setSciNotNumDigitsLeft(Integer sciNotNumDigitsLeft) {
		this.sciNotNumDigitsLeft = sciNotNumDigitsLeft;
	}
	public Integer getSciNotNumDigitsRight() {
		return sciNotNumDigitsRight;
	}
	public void setSciNotNumDigitsRight(Integer sciNotNumDigitsRight) {
		this.sciNotNumDigitsRight = sciNotNumDigitsRight;
	}
	public void setSpreadSheetFormatOption(String formatOption) {
		this.spreadSheetFormatOption = formatOption;
	}
	public String getSpreadSheetFormatOption() {
		return this.spreadSheetFormatOption;
	}
	public Iterable<String> getGlobalVarNames() {
		return this.globalVars.getVarKeys();
	}
	public Class<?> getGlobalVarType(String i) {
		return this.globalVars.getType(i);
	}
	public Object getGlobalVarValue(String i) {
		return this.globalVars.getValue(i);
	}

	public CalcFrame getGlobalVars() {
		return this.globalVars;
	}
	public byte getGlobalVarSource(String i) {
		return this.globalVarSources.get(i);
	}
	public String getUserPrefNamespace() {
		return this.userPrefsNamespace;
	}
	public void setUserPrefNamespace(String upk) {
		this.userPrefsNamespace = upk;
		if (upk == null)
			this.customPrefs.clear();
	}
	public String getBrowserTitle() {
		return browserTitle;
	}
	public void setBrowserTitle(String browserTitle) {
		if (SH.isnt(browserTitle)) {
			this.browserTitle = null;
			portletManager.getRoot().setTitle(this.service.getPortletManager().getDefaultBrowserTitle());
		} else {
			this.browserTitle = browserTitle;
			portletManager.getRoot().setTitle(browserTitle);
		}
	}
	public String getCustomUserMenuTitle() {
		return this.customUserMenuTitle;
	}
	public void setCustomUserMenuTitle(String userMenutitle) {
		if (SH.isnt(userMenutitle))
			this.customUserMenuTitle = null;
		else
			this.customUserMenuTitle = userMenutitle;
	}
	public void clear() {
		this.browserTitle = null;
		this.customUserMenuTitle = null;
		this.userPrefsNamespace = null;
		this.customPrefs.clear();
	}
	public Object getCustomPreference(String id) {
		return this.customPrefs.get(id);
	}
	public void putCustomPreference(String id, Object preferences) {
		if (preferences == null)
			this.customPrefs.remove(id);
		else if (this.userPrefsNamespace == null)
			throw new IllegalStateException("Can't set custom preferences when user preferences namespace is null");
		else
			this.customPrefs.put(id, preferences);
	}

	public Set<String> getCustomPreferenceIds() {
		return this.customPrefs.keySet();
	}
	public void applyCustomPrefs(List<Map<String, Object>> list, boolean clearExisting) {
		if (clearExisting)
			this.customPrefs.clear();
		for (Map<String, Object> map : list) {
			String cpid = (String) map.get("cpid");
			if (cpid != null) {
				Object pref = map.get("pref");
				putCustomPreference(cpid, pref);
			}
		}
	}

	public AmiWebService getService() {
		return this.service;
	}

	public static final byte CUST_PREF_IMPORT_MODE_REJECT = 1;
	public static final byte CUST_PREF_IMPORT_MODE_IGNORE = 2;
	public static final byte CUST_PREF_IMPORT_MODE_ACCEPT = 3;
	private byte customPrefsImportMode = CUST_PREF_IMPORT_MODE_REJECT;

	public byte getCustomPrefsImportMode() {
		return this.customPrefsImportMode;
	}
	public byte setCustomPrefsImportMode(byte allowed) {
		return this.customPrefsImportMode = allowed;
	}
	public static String customPrefsImportModeToString(byte t) {
		switch (t) {
			case CUST_PREF_IMPORT_MODE_ACCEPT:
				return "accept";
			case CUST_PREF_IMPORT_MODE_REJECT:
				return "reject";
			case CUST_PREF_IMPORT_MODE_IGNORE:
				return "ignore";
		}
		return SH.toString(t);
	}
	public static byte parseCustomPresImportMode(String cpim) {
		if ("accept".equals(cpim))
			return CUST_PREF_IMPORT_MODE_ACCEPT;
		else if ("reject".equals(cpim))
			return CUST_PREF_IMPORT_MODE_REJECT;
		else if ("ignore".equals(cpim))
			return CUST_PREF_IMPORT_MODE_IGNORE;
		throw new RuntimeException("Unknown custom pref import mode: " + cpim);
	}
	public String getAutoApplyUserPrefs() {
		return this.autoApplyUserPrefs;
	}
	public void setAutoApplyuserPrefs(String s) {
		this.autoApplyUserPrefs = s;
	}
	public AmiSsoSession getSsoSession() {
		return ssoSession;
	}

	public boolean isReadonlySetting(String key) {
		return this.store.isReadonlySetting(key);
	}
	public void putSetting(String key, String value) {
		this.store.putSetting(key, value);
	}
	public void removeSetting(String key) {
		this.store.removeSetting(key);
	}
	public boolean isPermittedLayout(String source, String name) {
		if (!this.getService().getDesktop().getIsLocked())
			return true;
		List<TextMatcher> permitted = this.permittedLayoutPatternsBySource.get(source);
		if (permitted != null) {
			for (TextMatcher tm : permitted)
				if (tm.matches(name))
					return true;
		}
		if (SH.is(this.defaultLayoutName) && OH.eq(this.defaultLayoutName, name) && OH.eq(this.defaultLayoutSource, source))
			return true;

		return false;
	}

	static private AmiSsoSession getSsoSession(Map<String, Object> properties) {
		return CH.getOr(AmiSsoSession.class, properties, AmiAuthUser.PROPERTY_SSO_SESSION, null);
	}

	private String getDefaultLayout(Map<String, Object> properties) {
		String r = CH.getOr(Caster_String.INSTANCE, properties, AmiAuthUser.PROPERTY_DEFAULT_LAYOUT, null);
		if (r == null)
			r = service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_DEFAULT_LAYOUT);
		return r;
	}
	private String getLayouts(Map<String, Object> properties) {
		String r = CH.getOr(Caster_String.INSTANCE, properties, AmiAuthUser.PROPERTY_LAYOUTS, null);
		if (r == null)
			r = service.getPortletManager().getTools().getOptional(AmiWebProperties.PROPERTY_AMI_DEFAULT_LAYOUTS);
		return r;
	}
	static private boolean isSetting(String key) {
		return OH.eq(AmiAuthUser.PROPERTY_DEFAULT_LAYOUT, key) || OH.eq(AmiAuthUser.PROPERTY_LAYOUTS, key) || OH.eq(AmiAuthUser.PROPERTY_ISADMIN, key)
				|| OH.eq(AmiAuthUser.PROPERTY_ISDEV, key) || OH.eq(AmiAuthUser.PROPERTY_SSO_SESSION, key) || OH.eq(AmiAuthUser.PROPERTY_MAXSESSIONS, key);
	}

	public String fromTransientId(String id) {
		if (SH.startsWith(id, this.transientIdPrefix))
			return id.substring(this.transientIdPrefix.length());
		return id;
	}
	public String toTransientId(String id) {
		if (SH.startsWith(id, this.transientIdPrefix))
			return id;
		return this.transientIdPrefix + id;
	}

	public String getTransientIdPrefix() {
		return transientIdPrefix;
	}

	public void setTransientIdPrefix(String transientIdPrefix) {
		this.transientIdPrefix = transientIdPrefix;
	}
	public int getStackLimit() {
		return CalcFrameStack.DEFAULT_STACK_LIMIT;
	}

}
