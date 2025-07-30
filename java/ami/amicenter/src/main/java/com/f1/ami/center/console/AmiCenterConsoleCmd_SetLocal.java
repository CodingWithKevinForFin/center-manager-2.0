package com.f1.ami.center.console;

import com.f1.utils.AH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AmiCenterConsoleCmd_SetLocal extends AmiCenterConsoleCmd {

	public AmiCenterConsoleCmd_SetLocal() {
		super("setlocal *", "Set a local variable");
	}

	@Override
	public void process(AmiCenterConsoleClient client, String cmd, String[] cmdParts) {
		String text = SH.join(' ', AH.subarray(cmdParts, 1, cmdParts.length - 1));
		StringBuilder sb = new StringBuilder();
		if (SH.isnt(text)) {
			for (String key : client.getLocalSettings()) {
				Class type = client.getLocalSettingType(key);
				Object val = client.getLocalSetting(key);
				sb.append(type.getSimpleName()).append(' ').append(key).append('=').append(val).append(SH.NEWLINE);
			}
		} else if (text.indexOf('=') == -1) {
			String key = SH.trim(text);
			Class type = client.getLocalSettingType(key);
			if (type == null) {
				sb.append("setting not found: ").append(key);
				sb.append(SH.NEWLINE);
			} else {
				Object val = client.getLocalSetting(key);
				sb.append(type.getSimpleName()).append(' ').append(key).append('=').append(val).append(SH.NEWLINE);
			}
		} else {

			String key = SH.trim(SH.beforeFirst(text, '='));
			String val = SH.trim(SH.afterFirst(text, '='));
			val = SH.stripSuffix(val, ";", false);
			if (SH.isnt(key)) {
				sb.append("expecting syntax setlocal key=value   (or just setlocal without additional arguments to display current local vars)");
				sb.append(SH.NEWLINE);
			} else {
				Object old = client.getLocalSetting(key);
				if (!client.setLocalSetting(key, val, sb)) {
					sb.append(SH.NEWLINE);
					sb.append("SETLOCAL FAILED").append(SH.NEWLINE);
				} else {
					Object nuw = client.getLocalSetting(key);
					sb.append(SH.NEWLINE);
					if (OH.eq(old, nuw))
						sb.append("OKAY. 0 SETTING(S) UPDATED").append(SH.NEWLINE);
					else
						sb.append("OKAY. 1 SETTING(S) UPDATED").append(SH.NEWLINE);
				}
			}
		}
		client.getOutputStream().append(sb);
	}
}
