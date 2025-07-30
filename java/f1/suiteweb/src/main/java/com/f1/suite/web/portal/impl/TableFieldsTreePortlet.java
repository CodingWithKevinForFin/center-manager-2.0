package com.f1.suite.web.portal.impl;

import com.f1.suite.web.portal.InterPortletMessage;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletSocket;
import com.f1.suite.web.portal.impl.ShowTableFieldsInterPortletMessage.TableRow;
import com.f1.suite.web.portal.impl.ShowTableFieldsInterPortletMessage.TableRowField;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.utils.CH;

public class TableFieldsTreePortlet extends FastTreePortlet {

	public final BasicPortletSocket hiddenFields;

	public TableFieldsTreePortlet(PortletConfig portletConfig) {
		super(portletConfig);

		WebTreeManager tm = getTree().getTreeManager();
		this.getTree().setRootLevelVisible(false);
		this.getTreeManager().setComparator(null);//no sorting
		this.hiddenFields = addSocket(false, "Show Hidden Fields", "Show Hidden Fields", true, null, CH.s(ShowTableFieldsInterPortletMessage.class));
	}

	@Override
	public void onMessage(PortletSocket localSocket, PortletSocket origin, InterPortletMessage message) {
		if (localSocket == hiddenFields) {
			ShowTableFieldsInterPortletMessage showMessage = (ShowTableFieldsInterPortletMessage) message;
			WebTreeManager tm = getTreeManager();
			tm.clear();

			for (TableRow row : showMessage.rows) {
				WebTreeNode node = tm.createNode(row.name, tm.getRoot(), true);
				for (TableRowField e : row.fields) {
					WebTreeNode field = tm.createNode(e.name + ": " + e.value, node, true).setIcon("portlet_icon_field");
					if (!e.visible)
						field.setCssClass("italic");
				}
			}
		}
	}

	public static class Builder extends AbstractPortletBuilder<TableFieldsTreePortlet> {

		public static final String ID = "TableFieldsTreePortlet";

		public Builder() {
			super(TableFieldsTreePortlet.class);
		}

		@Override
		public TableFieldsTreePortlet buildPortlet(PortletConfig portletConfig) {
			return new TableFieldsTreePortlet(portletConfig);
		}

		@Override
		public String getPortletBuilderName() {
			return "Table Fields Tree";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}
}
