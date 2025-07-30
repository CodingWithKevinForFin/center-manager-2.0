package com.f1.ami.web.dm.portlets.vizwiz;

import java.util.ArrayList;
import java.util.List;

import com.f1.ami.web.AmiWebAbstractPortlet;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.form.FormPortletButton;

public abstract class AmiWebVizwiz<T extends AmiWebPortlet> {

	private static final String ORANGE_BUTTON_STYLE = "_fs=16|_fm=bold|_fg=#FFFFFF|_w=160px|_h=29|_bg=#e27025|style.border=0px|style.borderRadius=5px";
	private T previewPortlet;
	final private AmiWebService service;
	final private String name;
	private FormPortletButton refreshButton;
	final private FormPortletButton underlyingDataButton;
	final private List<FormPortletButton> buttons = new ArrayList<FormPortletButton>();

	public AmiWebVizwiz(AmiWebService service, String name) {
		this.service = service;
		this.name = name;
		this.buttons.add(this.underlyingDataButton = new FormPortletButton("View Data").setCssStyle(ORANGE_BUTTON_STYLE));
	}

	abstract public Portlet getCreatorPortlet();
	abstract public boolean initDm(AmiWebDm dm, Portlet initForm, String tableName);

	public Portlet getInitForm(AmiWebDm dm, String tableName) {
		return null;
	}
	public String getName() {
		return this.name;
	}
	public String getHelp() {
		return "";
	}

	public AmiWebService getService() {
		return service;
	}
	public PortletConfig generateConfig() {
		return service.getPortletManager().generateConfig();
	}

	public void setPreviewPortlet(T previewPortlet) {
		this.previewPortlet = previewPortlet;
		this.previewPortlet.setShowConfigButtons(false);
	}
	public T getPreviewPortlet() {
		return this.previewPortlet;
	}
	public T removePreviewPortlet() {
		T r = this.previewPortlet;
		this.previewPortlet = null;
		r.setShowConfigButtons(true);
		return r;
	}
	public int getCreatorPortletWidth() {
		return 500;
	}

	public List<FormPortletButton> getButtons() {
		return this.buttons;
	}

	public void onButton(FormPortletButton button) {
		if (button == this.refreshButton) {
			preview();
		} else if (button == this.underlyingDataButton) {
			if (this.previewPortlet instanceof AmiWebAbstractPortlet) {
				((AmiWebAbstractPortlet) this.previewPortlet).showUnderlyingData();
			}
		}
	}

	public boolean preview() {
		return true; // Preview automatically succeeds if no preview method is implemented in the child Vizwiz
	}

	protected void addRefreshButton() {
		this.buttons.add(0, this.refreshButton = new FormPortletButton("Refresh").setCssStyle(ORANGE_BUTTON_STYLE));
	}
	protected void addButtons(List<FormPortletButton> buttons) {
		this.buttons.addAll(buttons);
	}

}
