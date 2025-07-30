package com.f1.ami.web;

import java.util.Collection;
import java.util.Map;

import com.f1.ami.web.style.AmiWebStyledPortletPeer;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletContainer;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public abstract class AmiWebAbstractContainerPortlet extends AmiWebAbstractPortlet {

	public AmiWebAbstractContainerPortlet(PortletConfig config) {
		super(config);
	}

	abstract PortletContainer getInnerContainer();

	@Override
	public Portlet getInnerPortlet() {
		return this.getInnerContainer();
	}

	@Override
	public void init(Map<String, Object> configuration, Map<String, String> origToNewIdMapping, StringBuilder sb) {
		super.init(configuration, origToNewIdMapping, sb);
	}

	@Override
	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = super.getConfiguration();
		return r;
	}
	@Override
	public Collection<AmiWebAliasPortlet> getAmiChildren() {
		return (Collection) this.getInnerContainer().getChildren().values();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public void setTransient(boolean isTransient) {
		if (isTransient == this.isTransient())
			return;
		super.setTransient(isTransient);
		for (AmiWebAliasPortlet i : this.getAmiChildren())
			i.setTransient(isTransient);
	}

	public void setTransientNoRecurse() {
		super.setTransient(true);
	}

	@Override
	public AmiWebAliasPortlet getNonTransientPanel() {
		AmiWebAliasPortlet r = super.getNonTransientPanel();
		if (r != null)
			return r;
		for (AmiWebAliasPortlet child : getAmiChildren()) {
			r = child.getNonTransientPanel();
			if (r != null)
				return r;
		}
		return null;
	}
	@Override
	public void onParentStyleChanged(AmiWebStyledPortletPeer peer) {
	}
}
