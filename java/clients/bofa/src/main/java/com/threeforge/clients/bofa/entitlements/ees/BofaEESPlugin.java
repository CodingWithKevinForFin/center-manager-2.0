package com.threeforge.clients.bofa.entitlements.ees;

import java.util.logging.Logger;

import com.f1.ami.web.datafilter.AmiWebDataFilter;
import com.f1.ami.web.datafilter.AmiWebDataFilterPlugin;
import com.f1.ami.web.datafilter.AmiWebDataSession;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.PropertyController;

public class BofaEESPlugin implements AmiWebDataFilterPlugin {
	private static Logger log = LH.get();
	private PropertyController props;

	public static void main(String[] args) {
	}
	public void init(ContainerTools tools, PropertyController props) {
		this.props = props;
	}

	public String getPluginId() {
		return "BofaEESEntitlementsPlugin";
	}

	public AmiWebDataFilter createDataFilter(AmiWebDataSession session) {
		return new BofaEESDataFilter(session, props);
	}

}
