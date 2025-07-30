package com.f1.anvil.triggers;

import com.f1.ami.center.table.AmiImdb;
import com.f1.ami.center.triggers.AmiCommandRequest;
import com.f1.ami.center.triggers.AmiCommandResponse;
import com.f1.ami.center.triggers.AmiCommandTriggerPlugin;
import com.f1.container.ContainerTools;
import com.f1.utils.PropertyController;

public class TestCommandTrigger implements AmiCommandTriggerPlugin {

	@Override
	public AmiCommandResponse onCommand(AmiCommandRequest cr) {
		if ("TestCt".equals(cr.getCommandDefinitionId())) {
			System.out.println("got here!");
			return new AmiCommandResponse(AmiCommandResponse.STATUS_OKAY, "nicely done", "session.alert(\"bang!\")");
		}
		return null;
	}
	@Override
	public void init(ContainerTools tools, PropertyController props) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startup(AmiImdb imdb) {
		//		imdb.getSystemSchema().__COMMAND.addCommand(-1, "Test", "TestCt", "__SYSTEM", "", 1, "Test Me", null, null, "This is some help", "0-2",
		//				AmiConsts.COMMAND_CALLBACK_USER_CLICK);
	}

	@Override
	public String getPluginId() {
		return "TestTrigger";
	}
}
