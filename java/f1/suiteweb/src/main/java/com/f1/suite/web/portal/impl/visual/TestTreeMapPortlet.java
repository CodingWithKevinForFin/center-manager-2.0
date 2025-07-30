package com.f1.suite.web.portal.impl.visual;

import java.util.Map;

import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.AbstractPortletBuilder;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.utils.structs.IntKeyMap;

public class TestTreeMapPortlet extends GridPortlet implements WebTreemapContextMenuFactory, WebTreemapContextMenuListener, FormPortletListener {

	private FormPortletNumericRangeField heatField;
	private FormPortletNumericRangeField valueField;
	private FormPortletButton addButton;
	private FormPortletButton updateButton;
	private FormPortletButton removeButton;
	private FormPortletTextField nameField;
	private FormPortletTextField catField;
	private FormPortlet form;
	private TreemapPortlet treePortlet;
	private TreemapNode selectedNode;

	public TestTreeMapPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		this.treePortlet = new TreemapPortlet(generateConfig());
		treePortlet.setMenuContextFactory(this);
		treePortlet.addMenuContextListener(this);
		this.form = new FormPortlet(generateConfig());
		this.catField = form.addField(new FormPortletTextField("Category"));
		this.nameField = form.addField(new FormPortletTextField("Name"));
		this.valueField = form.addField(new FormPortletNumericRangeField("size").setRange(.001, 100).setValue(50));
		this.heatField = form.addField(new FormPortletNumericRangeField("heat").setRange(-100, 100).setValue(30));
		this.addButton = form.addButton(new FormPortletButton("Add"));
		this.removeButton = form.addButton(new FormPortletButton("Remove"));
		this.updateButton = form.addButton(new FormPortletButton("Update"));
		DividerPortlet div = new DividerPortlet(generateConfig(), true);
		div.addChild(form);
		div.setOffset(.2);
		div.addChild(treePortlet);
		this.addChild(div, 0, 0);
		//		this.treePortlet.addOption(TreemapPortlet.OPTION_CATEGORY_BORDER_SIZE, "5");
		//		this.treePortlet.addOption(TreemapPortlet.OPTION_CATEGORY_BORDER_COLOR, "#AAFFAA");
		form.addFormPortletListener(this);
	}
	@Override
	public void onContextMenu(TreemapPortlet treemap, String action, TreemapNode selectedNode) {
		if ("remove".equals(action)) {
			treemap.removeNode(selectedNode.getId());
			this.selectedNode = null;
		}
		if ("details".equals(action)) {
			updateButton.setName("hello world");
		}
	}

	@Override
	public void onNodeClicked(TreemapPortlet portlet, TreemapNode node, int btn) {
		if (node != null) {
			this.selectedNode = node;
			valueField.setValue(node.getValue());
			nameField.setValue(node.getGroupId());
			catField.setValue(node.getParent().getGroupId());
		}
	}
	@Override
	public WebMenu createMenu(TreemapPortlet treemap, TreemapNode selected) {
		return new BasicWebMenu(new BasicWebMenuLink("remove", true, "remove"), new BasicWebMenuLink("show details", true, "details"));
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == addButton) {
			nameField.setValue(nameField.getValue() + "_");
		} else if (button == removeButton) {
			treePortlet.removeNode(this.selectedNode.getId());
		} else if (button == updateButton) {
			selectedNode.setValue(valueField.getValue());
		}
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (selectedNode != null) {
			selectedNode.setValue(valueField.getValue());
		}
	}

	public static class Builder extends AbstractPortletBuilder<TestTreeMapPortlet> {

		public static final String ID = "testTreemap";

		public Builder() {
			super(TestTreeMapPortlet.class);
		}

		@Override
		public TestTreeMapPortlet buildPortlet(PortletConfig portletConfig) {
			TestTreeMapPortlet portlet = new TestTreeMapPortlet(portletConfig);
			return portlet;
		}

		@Override
		public String getPortletBuilderName() {
			return "Test Treemap";
		}

		@Override
		public String getPortletBuilderId() {
			return ID;
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void onSelectionChanged(TreemapPortlet treemapPortlet, IntKeyMap<TreemapNode> selected, boolean userDriven) {
	}

}
