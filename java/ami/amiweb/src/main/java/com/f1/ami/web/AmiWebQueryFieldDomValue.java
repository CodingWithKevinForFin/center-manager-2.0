package com.f1.ami.web;

import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebQueryFieldDomValue extends AmiWebDomValue {

	private String ari;

	public AmiWebQueryFieldDomValue(QueryField<?> parent, String key) {
		super(parent, key, parent.getVarTypeAt(parent.getPositionFromSuffixName(key)));
	}

	@Override
	public String getAri() {
		return ari;
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_FIELD_VALUE;
	}

	@Override
	public String getDomLabel() {
		if (SH.equals("", this.getDomValueKey()))
			return "value";//this.getParentDomObject().getName();
		return SH.stripPrefix(this.getDomValueKey(), "_", false);
	}

	@Override
	public void updateAri() {
		String oldAri = ari;
		String dl = this.getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_FIELD_VALUE + ":" + ((QueryField<?>) this.getParentDomObject()).getForm().getAmiLayoutFullAliasDotId() + "?"
				+ this.getParentDomObject().getDomLabel() + (dl == "" ? "" : ("?" + this.getDomValueKey()));
		if (this.getParentDomObject().getField().getForm() != null)
			getParentDomObject().getForm().getService().getDomObjectsManager().fireAriChanged(this, oldAri);
	}

	@Override
	public QueryField getParentDomObject() {
		return (QueryField) super.getParentDomObject();
	}

	@Override
	public Object getDomValue() {
		return getParentDomObject().getValue(this.getDomValueKey());
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}
	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	@Override
	public boolean isTransient() {
		return super.getParentDomObject().isTransient();
	}

	private boolean isManagedByDomManager = false;

	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.getParentDomObject().getForm().getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}

	public void removeFromDomManager() {
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.getParentDomObject().getForm().getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.getParentDomObject().getAmiLayoutFullAlias();
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		String dl = this.getDomLabel();
		return this.getParentDomObject().getAmiLayoutFullAliasDotId() + (dl == "" ? "" : ("?" + this.getDomValueKey()));
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return null;
	}

	@Override
	public AmiWebService getService() {
		return getParentDomObject().getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
