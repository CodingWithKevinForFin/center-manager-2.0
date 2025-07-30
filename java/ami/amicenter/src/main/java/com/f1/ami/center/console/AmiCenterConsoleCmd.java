package com.f1.ami.center.console;

import com.f1.utils.SH;

public abstract class AmiCenterConsoleCmd {

	private static final byte TYPE_TEXT = 1;
	private static final byte TYPE_ANY = 2;
	final private String[] parts;
	final private byte[] partTypes;
	final private String command;
	final private String help;
	final private boolean unbounded;

	public AmiCenterConsoleCmd(String cmd, String help) {
		if (cmd.endsWith(" *")) {
			this.unbounded = true;
			cmd = SH.stripSuffix(cmd, " *", true);
		} else
			this.unbounded = false;

		this.parts = SH.split(' ', cmd);
		this.partTypes = new byte[parts.length];
		for (int i = 0; i < this.partTypes.length; i++)
			this.partTypes[i] = getType(this.parts[i]);

		this.command = cmd;
		this.help = help;
	}

	private byte getType(String string) {
		if (string.startsWith("<"))
			return TYPE_ANY;
		return TYPE_TEXT;
	}

	public String[] getParts() {
		return this.parts;
	}

	public boolean matches(String[] parts) {
		if (parts.length < this.parts.length)
			return false;
		for (int i = 0; i < parts.length; i++)
			if (!matches(i, parts[i]))
				return false;
		return true;
	}

	private boolean matches(int i, String string) {
		if (i >= this.partTypes.length)
			return unbounded;
		switch (this.partTypes[i]) {
			case TYPE_TEXT:
				return SH.equalsIgnoreCase(this.parts[i], string);
			case TYPE_ANY:
				return true;
		}
		return false;
	}
	private boolean startsWith(int i, String string) {
		if (unbounded && i >= this.partTypes.length)
			return true;
		switch (this.partTypes[i]) {
			case TYPE_TEXT:
				return SH.startsWithIgnoreCase(this.parts[i], string);
			case TYPE_ANY:
				return true;
		}
		return false;
	}

	public String getAutocomplete(String[] parts) {
		if (parts.length == 0)
			parts = new String[] { "" };
		if (parts.length > this.parts.length)
			return null;
		int last = parts.length - 1;
		for (int i = 0; i < last; i++)
			if (!matches(i, parts[i]))
				return null;
		if (!startsWith(last, parts[last]))
			return null;
		StringBuilder sb = new StringBuilder();
		String lastPart = this.parts[last];
		String lastPart2 = parts[last];
		if (this.partTypes[last] == TYPE_TEXT || lastPart2.length() == 0)
			sb.append(lastPart, lastPart2.length(), lastPart.length());
		for (int i = last + 1; i < this.parts.length; i++) {
			sb.append(' ');
			sb.append(this.parts[i]);
		}
		return sb.length() > 0 ? sb.toString() : null;
	}

	abstract public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts);

	public String getCommand() {
		return this.command;
	}

	public String getHelp() {
		return this.help;
	}

	public boolean verifyLocalSetting(String key, Object value, StringBuilder sink) {
		return true;
	}

	public void init(AmiCenterConsoleClient amiCenterConsoleClient) {
	}

	public void onLocalSettingChanged(String key, Object val2) {
	}
}
