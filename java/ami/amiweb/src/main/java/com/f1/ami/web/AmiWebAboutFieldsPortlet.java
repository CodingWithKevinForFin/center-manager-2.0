package com.f1.ami.web;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiStartup;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.style.PortletStyleManager_Form;
import com.f1.utils.AH;
import com.f1.utils.EH;
import com.f1.utils.EnvironmentDump;
import com.f1.utils.Property;
import com.f1.utils.PropertyController;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiWebAboutFieldsPortlet extends GridPortlet implements FormPortletListener, AmiWebLockedPermissiblePortlet {

	private FormPortletTextAreaField propertiesField;
	private FormPortletButton closeButton;
	private FormPortlet versionForm;
	private TabPortlet tabs;
	private static FormPortlet buttons;
	private FormPortlet envForm;
	private FormPortlet libsForm;

	public AmiWebAboutFieldsPortlet(PortletConfig config, String properties) {
		super(config);
		this.tabs = new TabPortlet(generateConfig());
		this.addChild(tabs);
		this.versionForm = new FormPortlet(generateConfig());
		this.envForm = new FormPortlet(generateConfig());
		AmiWebAboutFieldsPortlet.buttons = new FormPortlet(generateConfig());
		this.libsForm = new FormPortlet(generateConfig());
		this.tabs.addChild("Version", versionForm);
		this.tabs.addChild("Environment", envForm);
		this.tabs.addChild("Libraries", libsForm);
		FormPortletTextAreaField ta = new FormPortletTextAreaField("");
		ta.setCssStyle("_bg=white|style.borderWidth=0px|style.resize=none");
		this.propertiesField = this.versionForm.addField(ta);
		this.tabs.setIsCustomizable(false);

		FormPortletTextAreaField env = new FormPortletTextAreaField("");
		env.setCssStyle("_bg=white|style.borderWidth=0px|style.resize=none");
		int strength = EH.checkRc5Strength();
		this.envForm.addField(new FormPortletTitleField("Runtime Environment"));
		addField("Startup Time", new Date(ManagementFactory.getRuntimeMXBean().getStartTime()).toString());
		addField("Working Dir", EH.getPwd());
		addField("Local Host", EH.getLocalHost());
		addField("Pid", EH.getPid());
		addField("F1 ProcessUid", EH.getProcessUid());
		addField("CPU Count", SH.toString(Runtime.getRuntime().availableProcessors()));
		addField("Avail.Memory", SH.formatMemory(Runtime.getRuntime().maxMemory()));
		addField("Java Home", EH.getJavaHome());
		addField("Java Version", EH.getJavaVersion());
		addField("Java Vendor", EH.getJavaVendor());
		addField("Java Compiler", EH.getJavaCompiler());
		addField("RC5 Strength", strength == Integer.MAX_VALUE ? "Unlimited" : SH.s(EH.checkRc5Strength()));
		addField("OS - Version", EH.getOsName() + " - " + EH.getOsVersion());
		addField("OS Arch.", EH.getOsArchitecture());
		addField("Current Time", EH.now().toString());
		addField("Default TimeZone", EH.getTimeZone());
		addField("Default Charset", Charset.defaultCharset().name());

		propertiesField.setValue(properties);
		propertiesField.setDisabled(true);
		propertiesField.setWidth(FormPortletTextAreaField.WIDTH_STRETCH);
		propertiesField.setHeight(FormPortletTextAreaField.HEIGHT_STRETCH);
		this.versionForm.getFormPortletStyle().setLabelsWidth(0);
		this.versionForm.getFormPortletStyle().setFieldSpacing(0);
		this.versionForm.getFormPortletStyle().setWidthStretchPadding(0);
		this.versionForm.getFormPortletStyle().setLabelsStyle("");

		libsForm.addField(new FormPortletTitleField("Boot Libs, Runtime Libs & Additional classpath Directories"));
		FormPortletTextAreaField runtimeLibsField = libsForm
				.addField(new FormPortletTextAreaField("").setWidth(FormPortletField.WIDTH_STRETCH).setHeight(FormPortletField.HEIGHT_STRETCH)).setDisabled(true);
		runtimeLibsField.setCssStyle("_bg=white|style.borderWidth=0px|style.resize=none");
		this.libsForm.getFormPortletStyle().setLabelsWidth(0);
		this.libsForm.getFormPortletStyle().setFieldSpacing(0);
		this.libsForm.getFormPortletStyle().setWidthStretchPadding(0);
		this.libsForm.getFormPortletStyle().setLabelsStyle("");
		List<String> t2 = new ArrayList<String>();
		StringBuilder t = new StringBuilder();
		t.append("\n-- Boot Class Path --\n\n");
		SH.join('\n', EnvironmentDump.appendFileInfo(removeMissing(EH.getBootClassPath(), t2), false), t);
		t2.clear();
		t.append("\n\n-- Runtime Libraries --\n\n");
		String[] javaClassPathAbsolute = EH.getJavaClassPathAbsolute();
		SH.join('\n', EnvironmentDump.appendFileInfo(removeMissing(javaClassPathAbsolute, t2), false), t);
		t.append("\n\n-- Additional Classpath Directories --\n\n");
		SH.join('\n', t2, t);
		runtimeLibsField.setValue(t.toString());
		this.envForm.getFormPortletStyle().setFieldSpacing(1);
		this.envForm.getFormPortletStyle().setLabelsStyle("");
		AmiWebAboutFieldsPortlet.buttons.addFormPortletListener(this);
		this.closeButton = AmiWebAboutFieldsPortlet.buttons.addButton(new FormPortletButton("Close"));
		this.addChild(AmiWebAboutFieldsPortlet.buttons, 0, 1);
		this.setRowSize(1, 40);

	}
	private String[] removeMissing(String[] txt, List<String> directories) {
		ArrayList<String> r = new ArrayList<String>();
		for (String i : txt) {
			try {
				File f = new File(i);
				if (f.canRead())
					if (f.isFile())
						r.add(i);
					else
						directories.add(i);
			} catch (Exception e) {
			}
		}
		return AH.toArray(r, String.class);
	}
	private void addField(String name, String value) {
		this.envForm.addField(new FormPortletTextField(name + ":").setValue(value).setDisabled(true).setWidth(FormPortletTextField.WIDTH_STRETCH));
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		getParent().close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public static GridPortlet createAboutWindow(PortletManager pm, String properties, HtmlPortlet aboutPortlet) {
		GridPortlet gridPortlet = new GridPortlet(pm.generateConfig());
		gridPortlet.addChild(aboutPortlet, 0, 0);
		gridPortlet.addChild(new AmiWebAboutFieldsPortlet(pm.generateConfig(), properties), 0, 1);
		gridPortlet.setSuggestedSize(1000, 700);
		gridPortlet.setRowSize(0, 55);
		return gridPortlet;
	}

	public static HtmlPortlet createEmailHtmlPortlet(PortletManager pm, String properties, String propertiesEmail) {
		HtmlPortlet aboutPortlet = new HtmlPortlet(pm.generateConfig());
		aboutPortlet.setHtml(
				"<div style='width:100%;height:100%;background:white;border-width:0px 0px 0px 0px;border-style:solid;border-color:#CCCCCC;color:#000000;padding:18px;'>Contact us at <a href=\"mailto:support@3forge.com?Subject=Support%20Message &body="
						+ propertiesEmail + "\" target=\"none\">support@3forge.com</img></a></div>");
		return aboutPortlet;
	}
	public static Tuple2<String, String> createPropertyMessages(PortletManager pm) {
		String properties = "=== Ami Library Version ===\n\n";
		String propertiesEmail = "My%20Ami%20Library%20Version:%0A%0A";
		for (String key : AmiStartup.getBuildProperties()) {
			String value = AmiStartup.getBuildProperty(key);
			properties += key + " = " + value + "\n";
			propertiesEmail += key + "%20=%20" + value + "%0A";
		}
		//		properties += "\n\n=== Version Details from Properties ===\n\n";
		propertiesEmail += "My%20System%20Properties:%0A%0A";
		PropertyController f1Properties = pm.getTools().getSubPropertyController("f1.deployment");
		PropertyController buildProperties = pm.getTools().getSubPropertyController("build");
		for (String key : f1Properties.getKeys()) {
			Property property = f1Properties.getProperty(key);
			//			properties += "f1.deployment" + property.getKey() + " = " + property.getValue() + "\n";
			propertiesEmail += "fi.deployment" + property.getKey() + "%20=%20" + property.getValue() + "%0A";
		}
		for (String key : buildProperties.getKeys()) {
			Property property = buildProperties.getProperty(key);
			//			properties += "build" + property.getKey() + " = " + property.getValue() + "\n";
			propertiesEmail += "build" + property.getKey() + "%20=%20" + property.getValue() + "%0A";
		}
		propertiesEmail += "%0AMy%20Issue:%0A";
		Tuple2<String, String> propertyMessages = new Tuple2<String, String>();
		propertyMessages.setAB(properties, propertiesEmail);
		return propertyMessages;
	}
	public static void showAbout(PortletManager manager) {
		Tuple2<String, String> propertiesMessages = AmiWebAboutFieldsPortlet.createPropertyMessages(manager);
		String properties = propertiesMessages.getA();
		String propertiesEmail = propertiesMessages.getB();
		HtmlPortlet aboutPortlet = AmiWebAboutFieldsPortlet.createEmailHtmlPortlet(manager, properties, propertiesEmail);
		GridPortlet aboutWindow = AmiWebAboutFieldsPortlet.createAboutWindow(manager, properties, aboutPortlet);
		setFormStyle(AmiWebUtils.getService(manager).getUserFormStyleManager());
		manager.showDialog("About", aboutWindow).setStyle(AmiWebUtils.getService(manager).getUserDialogStyleManager());
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (KeyEvent.ENTER.equals(keyEvent.getKey())) {
			this.onButtonPressed(this.versionForm, this.closeButton);
			return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}
	private static void setFormStyle(PortletStyleManager_Form styleManager) {
		buttons.setStyle(styleManager);
	}
}
