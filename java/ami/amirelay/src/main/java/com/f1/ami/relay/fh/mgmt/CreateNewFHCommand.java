package com.f1.ami.relay.fh.mgmt;

import java.util.Map;
import java.util.Properties;

import com.f1.ami.amicommon.msg.AmiRelayRunAmiCommandRequest;
import com.f1.ami.relay.AmiRelayServer;
import com.f1.ami.relay.fh.AmiFH;
import com.f1.utils.OH;
import com.f1.utils.PropertiesHelper;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.impl.BasicPropertyController;

public class CreateNewFHCommand extends AmiCommandBase {

	@Override
	public void exec(AmiRelayServer server, AmiCommandManager fhMgr, AmiRelayRunAmiCommandRequest action, StringBuilder msgSink) throws Exception {
		Map<String, Object> arguments = action.getArguments();
		String name = (String) arguments.get("name");
		if (SH.isnt(name))
			throw new NullPointerException("name");

		arguments.remove("name");

		String namespace = "ami.relay.fh." + name + ".";
		Properties props = new Properties();
		PropertiesHelper.addPropertiesWithoutNulls(props, namespace, arguments);
		PropertyController pc = new BasicPropertyController(props);

		PropertyController fhProps = pc.getSubPropertyController(namespace);

		String clazz = (String) fhProps.getRequired("class");
		AmiFH fh = (AmiFH) OH.forName(clazz).newInstance();
		server.initFH(fh, name, fhProps);

		server.appendNewProperties(pc);
	}
}
