package com.f1.ami.amicommon;

import java.util.Arrays;

import com.f1.base.Lockable;
import com.f1.base.LockedException;

/**
 * Represents the varrious attributes for connecting to an external resource (database or feed handler provider) referred to as a target
 * <P>
 * 
 * 
 * <B>targetType</B> - 1 = DATASOURCE , 2=FEEDHANDLER<BR>
 * <B>targetPluginId</B> - The plugin type, example MYSQL, SYBASE, etc. See {@link AmiDatasourcePlugin#getPluginId()}<BR>
 * <B>targetName</B> - The name of the plugin, example 'My account database'. <BR>
 * <B>url</B> - the url, as passed in from the user configuration on the frontend datasource management panel<BR>
 * <B>username</B> - the username, as passed in from the user configuration on the frontend datasource management panel<BR>
 * <B>password</B> - password, as passed in from the user configuration on the frontend datasource management panel<BR>
 * <B>options</B> - options, as passed in from the user configuration on the frontend datasource management panel<BR>
 */
public class AmiServiceLocator implements Lockable {
	public static final byte TARGET_TYPE_DATASOURCE = 1;
	public static final byte TARGET_TYPE_FEEDHANDLER = 2;
	public static final byte TARGET_TYPE_SCM = 3;

	private String url;
	private String username;
	private char[] password;
	private String targetPluginId;
	private byte targetType;
	private String targetName;
	private String options;
	private boolean locked = false;
	private String invokedBy;

	public AmiServiceLocator() {
	}

	public AmiServiceLocator(byte targetType, String targetPluginId, String targetName, String url, String username, char[] password, String options, String invokedBy) {
		super();
		this.targetName = targetName;
		this.targetType = targetType;
		this.url = url;
		this.username = username;
		this.password = password;
		this.options = options;
		this.targetPluginId = targetPluginId;
		this.invokedBy = invokedBy;
	}
	public AmiServiceLocator(AmiServiceLocator locator) {
		this.targetName = locator.targetName;
		this.targetType = locator.targetType;
		this.url = locator.url;
		this.username = locator.username;
		this.password = locator.password;
		this.options = locator.options;
		this.targetPluginId = locator.targetPluginId;
	}

	@Override
	public String toString() {
		return "AmiServiceName [url=" + url + ", username=" + username + ", password=" + Arrays.toString(password) + ", targetPluginId=" + targetPluginId + ", targetType="
				+ targetType + ", targetName=" + targetName + ", invokedBy=" + invokedBy + ", locked=" + locked + "]";
	}

	public void setTargetType(byte targetType) {
		LockedException.assertNotLocked(this);
		this.targetType = targetType;
	}
	public void setTargetPluginId(String targetPluginId) {
		LockedException.assertNotLocked(this);
		this.targetPluginId = targetPluginId;
	}
	public void setOptions(String options) {
		LockedException.assertNotLocked(this);
		this.options = options;
	}
	public void setTargetName(String targetName) {
		LockedException.assertNotLocked(this);
		this.targetName = targetName;
	}
	public void setUsername(String username) {
		LockedException.assertNotLocked(this);
		this.username = username;
	}
	public void setPassword(char[] password) {
		LockedException.assertNotLocked(this);
		this.password = password;
	}
	public void setUrl(String url) {
		LockedException.assertNotLocked(this);
		this.url = url;
	}

	public String getUrl() {
		return url;
	}
	public String getUsername() {
		return username;
	}
	public char[] getPassword() {
		return password;
	}
	public String getTargetName() {
		return this.targetName;
	}
	public byte getTargetType() {
		return targetType;
	}
	public String getInvokedBy() {
		return invokedBy;
	}

	@Override
	public void lock() {
		this.locked = true;
	}

	@Override
	public boolean isLocked() {
		return this.locked;
	}

	public String getTargetPluginId() {
		return targetPluginId;
	}

	public String getOptions() {
		return options;
	}

}
