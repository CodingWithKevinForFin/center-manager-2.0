package com.f1.ami.relay.fh.mgmt;

import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayIn;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.ami.relay.fh.AmiFHBase;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiCommandManager extends AmiFHBase {
	private static final Logger log = LH.get();
	private HashMap<String, AmiCommand> cmdMap = new HashMap<String, AmiCommand>();

	@Override
	public void init(int id, String name, PropertyController sysProps, PropertyController props, AmiRelayIn amiServer) {
		super.init(id, name, sysProps, props, amiServer);

		PropertyController cmdsProps = props.getSubPropertyController("commands.");
		String[] cmds = SH.trimArray(SH.split(',', cmdsProps.getRequired("active")));

		//load each command
		for (String cmd : cmds) {
			PropertyController cmdDefProps = cmdsProps.getSubPropertyController(cmd + ".");
			String clazz = cmdDefProps.getRequired("class");

			PropertyController cmdProps = cmdDefProps.getSubPropertyController("props.");

			//create class
			try {
				AmiCommand c = (AmiCommand) OH.forName(clazz).newInstance();
				c.init(cmdProps);

				//simplyl sanity check for dup command ids...will save tons of time down the road
				if (cmdMap.containsKey(c.getId()))
					LH.log(log, Level.SEVERE, "Commands with dup id ", c.getId());

				cmdMap.put(c.getId(), c);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Failed to instantiate command with class - ", clazz, e);
			}

		}
	}

	@Override
	public void start() {
		super.start();

		getAmiRelayIn().onConnection(EMPTY_PARAMS);
		login();

		publishDefinitions();

		onStartFinish(true);
	}

	private void publishDefinitions() {
		for (AmiCommand c : cmdMap.values()) {
			getAmiRelayIn().onCommandDef(c.getId(), c.getTitle(), c.getPermissionLevel(), c.getWhereClause(), c.getFilterClause(), c.getHelp(), c.getArgs(), c.getScript(),
					c.getPriority(), c.getEnabledClause(), c.getStyle(), c.getSelectMode(), c.getFields(), EMPTY_PARAMS, c.getCallbacksMask());
		}
	}

	@Override
	public void stop() {
		super.stop();
		logout();
		onStopFinish(true);
	}

	@Override
	public void call(AmiRelayServer server, AmiRelayRunAmiCommandRequest action, StringBuilder errorSink) {
		//find command
		AmiCommand c = cmdMap.get(action.getCommandDefinitionId());

		final String cmd = action.getCommandDefinitionId();
		final String id = action.getCommandUid();
		LH.fine(log, "Call to execute command ", cmd);

		if (c == null) {
			LH.fine(log, "command ", cmd, " not found");
			getAmiRelayIn().onResponse(id, AmiRelayIn.RESPONSE_STATUS_NOT_FOUND, SH.join(' ', "Command", cmd, "not found"), null, Collections.EMPTY_MAP);
		} else {
			StringBuilder sb = new StringBuilder(); //create new one since we don't expect many calls + calls maybe multi-threaded

			try {
				LH.fine(log, "command ", cmd, " found...executing with params..[", action, "]");
				c.exec(server, this, action, sb);
				getAmiRelayIn().onResponse(id, AmiRelayIn.RESPONSE_STATUS_OK, sb.toString(), null, Collections.EMPTY_MAP);
			} catch (Exception e) {
				LH.log(log, Level.SEVERE, "Failed to executed command ", cmd, e);
				getAmiRelayIn().onResponse(id, AmiRelayIn.RESPONSE_STATUS_ERROR, sb.toString(), null, Collections.EMPTY_MAP);
			}
		}
	}
}
