package com.f1.ami.sample;

import java.util.LinkedHashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.client.AmiClient;
import com.f1.ami.client.AmiClientListener;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;

public class AmiSampleMain implements AmiClientListener {

	public static void main(String a[]) {
		ContainerBootstrap bs = new ContainerBootstrap(AmiSampleMain.class, a);
		bs.setConfigDirProperty("./src/main/config");
		bs.setPluginsDir("./src/main/data/plugins");
		AmiSampleMain asm = new AmiSampleMain();
		bs.startup();
		PropertyController props = bs.getProperties();
		String host = props.getOptional("ami.host", AmiClient.DEFAULT_HOST);
		String plugins = props.getOptional("command.plugins", "");
		int port = props.getOptional("ami.port", AmiClient.DEFAULT_PORT);
		String loginId = props.getOptional("ami.loginId", "Simulator");

		for (String plugin : SH.split(',', plugins)) {
			StringBuilder errorSink = new StringBuilder();
			SampleCommandPlugin instance = AmiUtils.loadPlugin(plugin, "Command Plugin", null, props.getSubPropertyController("ami.plugins." + plugin + "."),
					SampleCommandPlugin.class, errorSink);
			asm.addPlugin(instance);
		}

		AmiClient ac = new AmiClient();
		asm.setClient(ac);
		ac.addListener(asm);
		ac.start(host, port, loginId, 0);
		bs.startupComplete();
		bs.getPluginsDir();
		bs.keepAlive();
	}

	private AmiClient client;

	private void setClient(AmiClient ac) {
		this.client = ac;
	}

	private LinkedHashMap<String, SampleCommandPlugin> plugins = new LinkedHashMap<String, SampleCommandPlugin>();

	private void addPlugin(SampleCommandPlugin instance) {
		String def = instance.getCommandDef();
		String name = SH.afterFirst(def, "I=\"", null);
		if (name == null) {
			name = SH.afterFirst(def, "I='", null);
			name = SH.beforeFirst(name, "'");
		} else {
			name = SH.beforeFirst(name, "\"");
		}
		System.out.println("ADDING PLUGIN: " + name + " ==> " + instance.getClass().getName());
		this.plugins.put(name, instance);
	}

	@Override
	public void onMessageReceived(AmiClient source, long now, long seqnum, int status, CharSequence message) {
		System.out.println("Message received:" + message);

	}

	@Override
	public void onMessageSent(AmiClient source, CharSequence message) {
	}

	@Override
	public void onConnect(AmiClient source) {
		System.out.println("Connected");

	}

	@Override
	public void onDisconnect(AmiClient source) {
		System.out.println("Disconnected");
	}

	@Override
	public void onCommand(AmiClient source, String requestId, String cmd, String userName, String objectType, String objectId, Map<String, Object> params) {
		System.out.println("Command Received: " + cmd + " from " + userName + ", objectType=" + objectType + ", objectId=" + objectId + ", params=" + params);
		SampleCommandPlugin plugin = this.plugins.get(cmd);
		if (plugin == null) {
			client.startResponseMessage(requestId, 2, "Command not found: " + cmd);
			client.sendMessageAndFlush();
		} else
			plugin.onCommand(client, requestId, cmd, userName, objectType, objectId, params);
	}
	@Override
	public void onLoggedIn(AmiClient source) {
		System.out.println("Logged In");
		for (SampleCommandPlugin plugin : plugins.values())
			source.sendMessageAndFlush(plugin.getCommandDef());
	}

}
