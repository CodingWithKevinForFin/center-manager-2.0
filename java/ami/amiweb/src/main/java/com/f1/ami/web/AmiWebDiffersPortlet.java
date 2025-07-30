package com.f1.ami.web;

import java.util.HashSet;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiWebDiffersPortlet extends GridPortlet {

	private TabPortlet tabs;
	private AmiWebService service;

	public AmiWebDiffersPortlet(AmiWebService service, PortletConfig config) {
		super(config);
		this.service = service;
		this.tabs = addChild(new TabPortlet(generateConfig()), 0, 0);
		this.tabs.setIsCustomizable(false);

	}

	public boolean addTabCompareToCurrent(AmiWebLayoutFile right, String leftTitle, String rightTitle) {
		if (right == null)
			return true;
		AmiWebLayoutFile left = right;
		String leftJson = this.service.getLayoutFilesManager().toJson(left.buildCurrentJson(this.service));
		String rightJson = "{}";
		if (right != null)
			rightJson = this.service.getLayoutFilesManager().toJson(right.getLastLoadedJson());
		AmiWebDifferPortlet differ = new AmiWebDifferPortlet(generateConfig());
		differ.setText(leftJson, rightJson);
		differ.setTitles(leftTitle, rightTitle);
		boolean same = OH.eq(leftJson, rightJson);

		// Get Tab Title
		String leftFullAlias = null;
		if (left != null) {
			leftFullAlias = AmiWebUtils.formatLayoutAlias(left.getAlias());
			if (SH.equals(AmiWebUtils.ROOT_ALIAS_FORMAT, leftFullAlias))
				leftFullAlias = left.getLocation();
		} else
			leftFullAlias = "None";

		String rightFullAlias = null;
		if (right != null) {
			rightFullAlias = AmiWebUtils.formatLayoutAlias(right.getAlias());
			if (SH.equals(AmiWebUtils.ROOT_ALIAS_FORMAT, rightFullAlias))
				rightFullAlias = right.getLocation();
		} else
			rightFullAlias = "None";

		String tabName = null;
		if (SH.equals(leftFullAlias, rightFullAlias))
			tabName = leftFullAlias;
		else
			tabName = leftFullAlias + " ~~ " + rightFullAlias;
		Tab tab = this.tabs.addChild(tabName + (same ? "" : " (*)"), differ);
		if (!same)
			this.tabs.setActiveTab(tab.getPortlet());

		HashSet<String> childrenAlias = new HashSet<String>();

		if (left != null)
			for (AmiWebLayoutFile child : left.getChildren())
				childrenAlias.add(child.getAlias());
		if (right != null)
			for (AmiWebLayoutFile child : right.getChildren())
				childrenAlias.add(child.getAlias());

		for (String alias : childrenAlias) {
			AmiWebLayoutFile leftChild = left.getChildByAlias(alias);
			AmiWebLayoutFile rightChild = right.getChildByAlias(alias);
			if (!addTab(leftChild, rightChild, leftTitle, rightTitle))
				same = false;
		}
		return same;

	}

	public boolean addTab(AmiWebLayoutFile left, AmiWebLayoutFile right, String leftTitle, String rightTitle) {
		String leftJson = "{}";
		if (left != null)
			leftJson = this.service.getLayoutFilesManager().toJson(left.getLastLoadedJson());
		String rightJson = "{}";
		if (right != null)
			rightJson = this.service.getLayoutFilesManager().toJson(right.getLastLoadedJson());
		AmiWebDifferPortlet differ = new AmiWebDifferPortlet(generateConfig());
		differ.setText(leftJson, rightJson);
		differ.setTitles(leftTitle, rightTitle);
		boolean same = OH.eq(leftJson, rightJson);

		// Get Tab Title
		String leftFullAlias = null;
		if (left != null) {
			leftFullAlias = AmiWebUtils.formatLayoutAlias(left.getAlias());
			if (SH.equals(AmiWebUtils.ROOT_ALIAS_FORMAT, leftFullAlias))
				leftFullAlias = left.getLocation();
		} else
			leftFullAlias = "None";

		String rightFullAlias = null;
		if (right != null) {
			rightFullAlias = AmiWebUtils.formatLayoutAlias(right.getAlias());
			if (SH.equals(AmiWebUtils.ROOT_ALIAS_FORMAT, rightFullAlias))
				rightFullAlias = right.getLocation();
		} else
			rightFullAlias = "None";

		String tabName = null;
		if (SH.equals(leftFullAlias, rightFullAlias))
			tabName = leftFullAlias;
		else
			tabName = leftFullAlias + " ~~ " + rightFullAlias;
		Tab tab = this.tabs.addChild(tabName + (same ? "" : " (*)"), differ);
		if (!same)
			this.tabs.setActiveTab(tab.getPortlet());

		HashSet<String> childrenAlias = new HashSet<String>();

		if (left != null)
			for (AmiWebLayoutFile child : left.getChildren())
				childrenAlias.add(child.getAlias());
		if (right != null)
			for (AmiWebLayoutFile child : right.getChildren())
				childrenAlias.add(child.getAlias());

		for (String alias : childrenAlias) {
			AmiWebLayoutFile leftChild = left.getChildByAlias(alias);
			AmiWebLayoutFile rightChild = right.getChildByAlias(alias);
			if (!addTab(leftChild, rightChild, leftTitle, rightTitle))
				same = false;
		}
		return same;
	}

	public void addTabCompareToSaved(AmiWebLayoutFile t) {
		String left = this.service.getLayoutFilesManager().toJson(t.buildCurrentJson(this.service));
		String right = Tuple2.getB(this.service.getLayoutFilesManager().loadLayoutData(t.getAbsoluteLocation(), t.getSource()));
		AmiWebDifferPortlet differ = new AmiWebDifferPortlet(generateConfig());
		differ.setText(left, right);
		differ.setTitles("current", "saved");
		boolean same = OH.eq(left, right);
		Tab tab = this.tabs.addChild(AmiWebUtils.formatLayoutAlias(t.getFullAlias()) + (same ? "" : " (*)"), differ);
		if (!same)
			this.tabs.setActiveTab(tab.getPortlet());

		for (AmiWebLayoutFile c : t.getChildren())
			addTabCompareToSaved(c);
	}

}
