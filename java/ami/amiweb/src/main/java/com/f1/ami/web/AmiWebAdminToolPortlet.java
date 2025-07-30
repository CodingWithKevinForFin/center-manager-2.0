package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.cloud.AmiWebCloudManager;
import com.f1.container.ContainerTools;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabListener;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.TabPortlet.Tab;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;

public class AmiWebAdminToolPortlet extends GridPortlet implements AmiWebSpecialPortlet, FormPortletListener, ConfirmDialogListener, TabListener{
	
	final static public ArrayList<String> validPermissions = new ArrayList<String>(Arrays.asList("READ", "WRITE", "EXECUTE", "ALTER"));
	
	public static final String USERS_TAB_TITLE = "Users";
	public static final String SESSIONS_TAB_TITLE = "Sessions";
	public static final String HEADLESS_SESSIONS_TAB_TITLE = "Headless Sessions";
	
	private TabPortlet tabsContainer;
	
	private AmiWebManageUsersPortlet manageUsersPortlet;
	private AmiWebManageSessionsPortlet manageSessionsPortlet;
	private AmiWebManageHeadlessSessionsPortlet manageHeadlessPortlet;
	private FormPortlet form;
	private FormPortletButton closeButton;

	public AmiWebAdminToolPortlet(PortletConfig config, ContainerTools properties, AmiWebCloudManager amiWebCloudManager) {
		super(config);
			
		tabsContainer = new TabPortlet(generateConfig());
		tabsContainer.setIsCustomizable(false);
		
		this.manageUsersPortlet = new AmiWebManageUsersPortlet(generateConfig(), properties, amiWebCloudManager, this);
		this.manageSessionsPortlet = new AmiWebManageSessionsPortlet(generateConfig(), this);
		this.manageHeadlessPortlet = new AmiWebManageHeadlessSessionsPortlet(generateConfig(), properties, amiWebCloudManager);
		
		this.tabsContainer.addTabListener(this);
		this.tabsContainer.addChild(USERS_TAB_TITLE, manageUsersPortlet);
		this.tabsContainer.addChild(SESSIONS_TAB_TITLE, manageSessionsPortlet);
		this.tabsContainer.addChild(HEADLESS_SESSIONS_TAB_TITLE, manageHeadlessPortlet);
		this.addChild(tabsContainer, 0, 0);
		
		this.form = new FormPortlet(generateConfig());
		this.closeButton = new FormPortletButton("Close");
		this.addChild(form, 0, 1);
		this.form.addButton(closeButton);
		this.form.addFormPortletListener(this);
		this.setRowSize(1, 40);
	}
	
	public void changeSessionTab(String username) {
		this.manageSessionsPortlet.populateTable(username);
		this.tabsContainer.selectTab(1);
	}
	
	public static Set<String> verifyPermissions (Set<String> permissions) {
		Set<String> newPermissions = new HashSet<String>();
		for (String p : permissions) {
			String P = p.toUpperCase();
			if (AmiWebAdminToolPortlet.validPermissions.contains(P)) 
				newPermissions.add(P);
		}
		return newPermissions;
	}
	
	public static String permissionStringBuilder(LinkedHashSet<String> permissions) {
		String str = "";
		for (String s : permissions)
			str += s + ",";
		return str.length() == 0 ? str : str.substring(0, str.lastIndexOf(","));
	}
	
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		return false;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.closeButton)
			close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask,
			int cursorPosition) {
		
	}

	@Override
	public void onTabSelected(TabPortlet tabPortlet, Tab tab) {
	}

	@Override
	public void onTabClicked(TabPortlet tabPortlet, Tab curTab, Tab prevTab, boolean onArrow) {
		
	}

	@Override
	public void onTabRemoved(TabPortlet tabPortlet, Tab tab) {
		
	}

	@Override
	public void onTabAdded(TabPortlet tabPortlet, Tab tab) {
		
	}

	@Override
	public void onTabMoved(TabPortlet tabPortlet, int origPosition, Tab tab) {
		
	}
	
}
