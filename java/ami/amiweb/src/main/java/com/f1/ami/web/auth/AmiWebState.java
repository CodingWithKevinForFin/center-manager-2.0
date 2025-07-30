package com.f1.ami.web.auth;

import com.f1.suite.web.WebState;
import com.f1.utils.OH;

public class AmiWebState extends WebState {

	private String layout;
	final private String name;
	private String label;

	public AmiWebState(AmiWebStatesManager manager, String pgId, String name) {
		super(manager, pgId);
		this.name = name;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		if (OH.eq(this.layout, layout))
			return;
		this.layout = layout;
		if (getWebStatesManager() != null)
			getWebStatesManager().incrementModCount();
	}

	public String getName() {
		return this.name;
	}

	@Override
	public AmiWebStatesManager getWebStatesManager() {
		return (AmiWebStatesManager) super.getWebStatesManager();
	}

	public int getMaxSessions() {
		return getWebStatesManager().getMaxSessions();
	}

	public boolean isDev() {
		return this.getWebStatesManager().isDev();
	}

	public boolean isAdmin() {
		return this.getWebStatesManager().isAdmin();
	}

	public void setLabel(String label) {
		if (OH.eq(this.label, label))
			return;
		this.label = label;
		if (getWebStatesManager() != null)
			getWebStatesManager().incrementModCount();
	}
	public String getLabel() {
		return this.label;
	}

}
