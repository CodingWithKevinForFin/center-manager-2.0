package com.f1.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.LockedException;

/**
 * The f1 framework tries to minimize global statics, and as such this class contains the few variables which are JVM wide. For consistency, a particular value can only be modified
 * (via it's setter) prior to reading it (it's getter is invoked). Any subsequent writes after a read will result in an exception.
 */
public class F1GlobalProperties {
	static private List<Prop<?>> props = new ArrayList<Prop<?>>();

	/**
	 * the file name (relative to pwd) that files will be written to
	 */
	public static final String PROPERTY_PROCINFOSINK_FILE = "f1.global.procinfosink.file";

	/**
	 * When properties are listed, any keys which match the mask will have the value converted to p******d
	 */
	public static final String PROPERTY_PASSWORD_MASK = "f1.global.password.mask";

	/**
	 * What to display in logs instead of showing the plain text password
	 */
	public static final String PROPERTY_PASSWORD_SUBSTITUTE = "f1.global.password.substitute";

	/**
	 * If this is set to true then calls to EH.systemExit(...) will not shut down the JVM (useful for diagnostics)
	 */
	public static final String PROPERTY_DISABLE_SYSTEM_EXIT = "f1.global.disable.systemexit";

	/** determines startup disclaimer messages */

	public static final String PROPERTY_TITLE = "f1.global.title";

	/**
	 * the prefix used to identify environment properties that should be included as properties by the {@link PropertiesBuilder}
	 */
	public static final String PROPERTY_ENV_PROPERTY_PREFIX = "f1.global.env.property.prefix";

	/**
	 * the prefix used to identify system properties that should be included as properties by the {@link PropertiesBuilder}
	 */
	public static final String PROPERTY_SYS_PROPERTY_PREFIX = "f1.global.sys.property.prefix";

	/**
	 * determines how checked exceptions are converter to runtime exceptions. see {@link CheckedRuntimeException}
	 */
	public static final String PROPERTY_REPLACE_CHECKED_THROWN = "f1.global.replace.checked.thrown";

	/**
	 * determines the maximum number of items to delineate in warning / error /exception messages. For example, when generating a debug message for all valid options, what is the
	 * max number of options to print. See {@link CH#getOrThrow(java.util.Map, Object)}
	 */
	public static final String PROPERTY_MAX_COLLECTION_TO_DELINEATE = "f1.global.max.collection.to.delineate";

	public static final String PROPERTY_MAX_DEBUG_STRING_LENGTH = "f1.global.max.debug.string.length";

	private static final Prop<String> title = new Prop<String>(PROPERTY_TITLE, "3forge.com");
	private static final Prop<String> envPropertyPrefix = new Prop<String>(PROPERTY_ENV_PROPERTY_PREFIX, "property_");
	private static final Prop<String> sysPropertyPrefix = new Prop<String>(PROPERTY_SYS_PROPERTY_PREFIX, "property.");
	private static final Prop<Boolean> disableSystemExit = new Prop<Boolean>(PROPERTY_DISABLE_SYSTEM_EXIT, Boolean.FALSE);
	private static final Prop<Integer> maxCollectionToDelineate = new Prop<Integer>(PROPERTY_MAX_COLLECTION_TO_DELINEATE, 30);
	private static final Prop<Integer> maxDebugStringLength = new Prop<Integer>(PROPERTY_MAX_DEBUG_STRING_LENGTH, 10240);
	private static final Prop<Boolean> replaceCheckedThrown = new Prop<Boolean>(PROPERTY_REPLACE_CHECKED_THROWN, true);

	private static final Prop<String> passwordMask = new Prop<String>(PROPERTY_PASSWORD_MASK, ".*password.*");
	private static final Prop<String> passwordSubstitute = new Prop<String>(PROPERTY_PASSWORD_SUBSTITUTE, "******");
	private static final Prop<String> procInfoSinkFile = new Prop<String>(PROPERTY_PROCINFOSINK_FILE, "./.f1proc.txt");

	public static String getTitle() {
		return title.get();
	}

	public static Boolean getDisableSytemExit() {
		return disableSystemExit.get();
	}

	public static void setTitle(String title) {
		F1GlobalProperties.title.put(title);
	}

	public static String getPasswordMask() {
		return passwordMask.get();
	}

	public static void setPasswordMask(String passwordMask) {
		F1GlobalProperties.passwordMask.put(passwordMask);
	}
	public static String getPasswordSubstitute() {
		return passwordSubstitute.get();
	}

	public static void setPasswordSubstitute(String passwordSubstitute) {
		F1GlobalProperties.passwordSubstitute.put(passwordSubstitute);
	}

	public static boolean getReplaceCheckedExceptions() {
		return replaceCheckedThrown.get();
	}

	public static void setReplaceCheckedExceptions(boolean value) {
		replaceCheckedThrown.put(value);
	}

	public static void setEnvPropertyPrefix(String value) {
		envPropertyPrefix.put(value);
	}

	public static String getEnvPropertyPrefix() {
		return envPropertyPrefix.get();
	}

	public static void setSysPropertyPrefix(String value) {
		sysPropertyPrefix.put(value);
	}

	public static String getSysPropertyPrefix() {
		return sysPropertyPrefix.get();
	}

	public static int getMaxCollectionToDelineate() {
		return maxCollectionToDelineate.get();
	}

	public static void setMaxCollectionToDelineate(Integer value) {
		maxCollectionToDelineate.put(value);
	}

	public static Integer getMaxDebugStringLength() {
		return maxDebugStringLength.get();
	}

	public static void setMaxDebugStringLength(Integer value) {
		maxDebugStringLength.put(value);
	}

	public static void setProcInfoSinkFile(String value) {
		procInfoSinkFile.put(value);
	}

	public static String getProcInfoSinkFile() {
		return procInfoSinkFile.get();
	}

	static public Map<String, Object> getProps() {
		Map<String, Object> r = new HashMap<String, Object>(props.size());
		for (Prop<?> i : props)
			r.put(i.propname, i.localVal);
		return r;
	}

	private static final class Prop<T> extends AutolockingPointer<T> {

		private String propname;
		private T localVal;

		public Prop(String propname, T defaultValue) {
			this.propname = propname;
			props.add(this);
			String v = System.getProperty(propname, null);
			if (v == null)
				put(defaultValue);
			else if (defaultValue.getClass() == Integer.class)
				put((T) (Integer) Integer.parseInt(v));
			else if (defaultValue.getClass() == String.class)
				put((T) v);
			else if (defaultValue.getClass() == Boolean.class)
				put((T) (Boolean) Boolean.parseBoolean(v));
			else
				throw new RuntimeException("type not supported " + defaultValue.getClass());
		}

		@Override
		public T put(T value) {
			if (isLocked())
				throw new LockedException("global property '" + propname + "' has already been used, for consistency can not be overridden at this point");
			this.localVal = value;
			return super.put(value);
		}

	}

}
