package com.f1.ami.web;

import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.web.charts.AmiWebChartOptionsPortlet;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.BasicPortletManagerSecurityModel;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.TableFilterPortlet;
import com.f1.suite.web.portal.impl.text.SimpleFastTextPortlet;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.suite.web.table.impl.CopyPortlet;
import com.f1.suite.web.tree.impl.ArrangeColumnsPortlet;
import com.f1.suite.web.tree.impl.FastWebTreeFilterColumnPortlet2;
import com.f1.utils.CH;
import com.f1.utils.LH;

public class AmiWebPortletManagerSecurityModel extends BasicPortletManagerSecurityModel {

	final private AmiWebService service;
	private static final Logger log = LH.get();
	private static final Set<Class> CLASSES_PERMITTED = (Set) CH.s(ConfirmDialogPortlet.class, ArrangeColumnsPortlet.class, TableFilterPortlet.class,
			FastWebTreeFilterColumnPortlet2.class, CopyPortlet.class, AmiWebChartOptionsPortlet.OptionsPortlet.class, AmiWebDesktopBar.class,
			AmiWebUserExportUserPrefsPortlet.class, AmiWebUserImportUserPrefsPortlet.class, TilesPortlet.class, AmiWebUploadUserPrefsPortlet.class, SimpleFastTextPortlet.class);

	public AmiWebPortletManagerSecurityModel(AmiWebService amiWebService) {
		super(amiWebService.getPortletManager());
		this.service = amiWebService;
	}

	@Override
	public void assertPermitted(Portlet source, String action, String permissableActions) {
		if (!this.service.getDesktop().getIsLocked())
			return;
		super.assertPermitted(source, action, permissableActions);
	}
	@Override
	public boolean hasPermissions(PortletManager manager, Portlet portlet, String type, Map<String, String> attributes) {
		if (portlet == null)
			return NON_PORTLET_PERMITTED_ACTIONS.contains(type);
		if (portlet.getClass() == RootPortlet.class || portlet.getClass() == AmiWebDesktopPortlet.class)
			return true;
		for (Portlet p = portlet; p != null; p = p.getParent()) {
			Class<? extends Portlet> clazz = p.getClass();
			if (CLASSES_PERMITTED.contains(clazz))
				return true;
			if (clazz == AmiWebDesktopPortlet.class)
				if (((AmiWebDesktopPortlet) p).managesSecurityFor(portlet))
					return true;
			if (p instanceof AmiWebLockedPermissiblePortlet)
				return true;
		}
		assertPermitted(portlet, type, null);
		return true;
	}

	@Deprecated
	@Override
	public boolean hasPermissions(PortletManager manager, String type, Map<String, String> attributes) {
		return NON_PORTLET_PERMITTED_ACTIONS.contains(type);
	}

}
