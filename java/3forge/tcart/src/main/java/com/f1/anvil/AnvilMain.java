package com.f1.anvil;

import java.util.Set;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.ami.center.AmiCenterMain;
import com.f1.ami.center.AmiCenterSuite;
import com.f1.ami.relay.AmiRelayMain;
import com.f1.ami.web.AmiWebMain;
import com.f1.anvil.loader.AnvilFileLoaderManager;
import com.f1.anvil.utils.AnvilMarketData;
import com.f1.bootstrap.ContainerBootstrap;
import com.f1.container.impl.dispatching.RootPartitionActionRunner;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;

public class AnvilMain {

	public static void main(String a[]) throws Exception {

		ContainerBootstrap cb = new ContainerBootstrap(AnvilMain.class, a);
		cb.setProperty("f1.appname", "Anvil");
		cb.setProperty("f1.logfilename", "Anvil");
		cb.setProperty("f1.autocoded.disabled", "true");
		cb.setProperty("web.title", "3Forge Anvil");
		cb.setProperty("ami.web.index.html.file", "anvil_index.htm");
		cb.setProperty("sso.key.portlet.layout", "portletlayout_anvil");
		cb.setProperty("sso.namespace", "Anvil");
		cb.setProperty("ami.relay.fh.active", "ssocket");
		cb.setProperty("ami.relay.fh.ssocket.start", "true");
		cb.setProperty("ami.relay.fh.ssocket.class", "com.f1.ami.relay.fh.AmiServerSocketFH");
		cb.setProperty("ami.relay.fh.ssocket.props.amiId", "Server_Socket");
		cb.setProperty("ami.port", "3289");
		cb.setProperty("f1.console.prompt,", "Anvil");
		cb.setProperty("ami.relay.id", "relay_1");
		cb.setProperty("ami.log.messages", "false");
		AmiStartup.startupAmi(cb, "3forge_tcart");
		AnvilMarketData mdm = new AnvilMarketData(cb.getProperties());
		cb.addContainerService(AnvilMarketData.SERVICE_NAME, mdm);

		Set<String> modes = CH.s(SH.split(",", cb.getProperties().getOptional("ami.mode", "relay,center,web")));

		boolean runWeb = modes.remove("web");
		boolean runRelay = modes.remove("relay");
		boolean runCenter = modes.remove("center");
		if (!modes.isEmpty())
			throw new RuntimeException("Bad ami.mode option(s): " + modes);

		if (runWeb)
			AmiWebMain.main2(cb);

		RootPartitionActionRunner pr = runCenter ? AmiCenterMain.main2(cb).getRootPartitionRunner(AmiCenterSuite.PARTITIONID_AMI_CENTER) : null;

		//		Container c = null;
		//		for (Container container : cb.getContainers()) {
		//			if (SH.equals(container.getName(), "AmiCenter")) {
		//				c = container;
		//				break;
		//			}
		//		}
		//		if (c == null)
		//			throw new RuntimeException("Could not find AmiCenter Container");
		//
		//		AmiCenterState state = c.getPartitionController().getState(AmiCenterSuite.PARTITIONID_AMI_CENTER, AmiCenterState.class);
		//		AmiImdbFactoriesManager fm = state.getAmiImdb().getFactoriesManager();
		//		fm.addTriggerFactory(new AmiTriggerFactory_AmiScript());
		//
		if (runRelay || runCenter) {
			AmiRelayMain.main2(cb);
		}
		AmiStartup.startupComplete(cb);
		if (runRelay || runCenter) {
			OH.sleep(5000);
			AnvilFileLoaderManager manager = AnvilFileLoaderManager.main(cb, pr, mdm);
		}

	}
}
