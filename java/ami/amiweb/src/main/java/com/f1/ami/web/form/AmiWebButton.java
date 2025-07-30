package com.f1.ami.web.form;

import java.util.Collections;
import java.util.List;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebService;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.ToDerivedString;

public class AmiWebButton implements ToDerivedString, AmiWebDomObject {

	public static String ON_PRESSED = "onPressed";
	public static final ParamsDefinition PARAM_DEF = new ParamsDefinition(ON_PRESSED, Object.class, "");
	static {
		PARAM_DEF.addDesc("Called when the button is clicked");
	}

	final private String id;
	String name;
	private AmiWebAmiScriptCallbacks script;
	private FormPortletButton button;
	final private AmiWebQueryFormPortlet formPortlet;

	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public AmiWebButton(String id, String name, AmiWebService service, AmiWebQueryFormPortlet formPortlet) {
		this.script = new AmiWebAmiScriptCallbacks(service, this);
		for (ParamsDefinition s : formPortlet.getService().getScriptManager().getCallbackDefinitions(AmiWebButton.class))
			this.script.registerCallbackDefinition(s);
		this.formPortlet = formPortlet;
		this.id = id;
		this.name = name;
		this.button = new FormPortletButton(name);
		button.setCorrelationData(this);
	}
	//	public void setScript(String script) {
	//		this.script.setAmiScriptCallbackNoCompile(ON_PRESSED, script);
	//	}
	public AmiWebAmiScriptCallbacks getScript() {
		return this.script;
	}
	public FormPortletButton getButton() {
		return button;
	}
	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}

	private String ari;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.formPortlet.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.formPortlet.getAmiLayoutFullAliasDotId() + "?" + getId();
		this.ari = AmiWebDomObject.ARI_TYPE_FORMBUTTON + ":" + this.amiLayoutFullAliasDotId;
		this.getAmiScriptCallbacks().setAmiLayoutAlias(this.getTargetPortlet().getAmiLayoutFullAlias());
		this.getTargetPortlet().getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}
	private AmiWebQueryFormPortlet getTargetPortlet() {
		return formPortlet;
	}
	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_FORMBUTTON;
	}

	@Override
	public String getDomLabel() {
		return this.id;
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.getTargetPortlet();
	}
	@Override
	public Class<?> getDomClassType() {
		return AmiWebButton.class;
	}
	public void onInitDone() {
		this.script.initCallbacksLinkedVariables();
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	public void execute() {
		getScript().execute(AmiWebButton.ON_PRESSED);
		getScript().getService().getDomObjectsManager().fireEvent(this, DOM_EVENT_CODE_ONCHANGE);
	}
	public void close() {
		this.script.close();
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.script;
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.formPortlet.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		AmiWebService service = this.formPortlet.getService();
		// Is it missing the fire removed?
		//		service.getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}
	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}
	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}
	@Override
	public AmiWebService getService() {
		return this.formPortlet.getService();
	}
	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.formPortlet.getFormulaVarTypes(f);
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}