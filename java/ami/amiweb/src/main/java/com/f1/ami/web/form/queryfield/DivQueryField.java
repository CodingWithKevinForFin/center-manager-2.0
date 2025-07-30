package com.f1.ami.web.form.queryfield;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebOverrideValue;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.factory.AmiWebFormDivFieldFactory;
import com.f1.base.CalcFrame;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class DivQueryField extends QueryField<FormPortletDivField> {
	public static final String FORMULA_DIV_HTML = "div_html";

	final private AmiWebOverrideValue<String> rawValue = new AmiWebOverrideValue<String>(null);
	private DerivedCellCalculator calc;
	private Set<String> referencedVarsInTemplate = new HashSet<String>();
	private boolean needsTemplateUpdate;

	public DivQueryField(AmiWebFormDivFieldFactory factory, AmiWebQueryFormPortlet form) {
		super(factory, form, new FormPortletDivField(""));
	}
	@Override
	public void init(Map<String, Object> initArgs) {
		super.init(initArgs);
		if (initArgs.containsKey("template"))
			this.rawValue.set((CH.getOr(Caster_String.INSTANCE, initArgs, "template", null)), true);
		else {
			String val = CH.getOr(Caster_String.INSTANCE, initArgs, "innerHtml", null);
			if (val != null)
				val = SH.replaceAll(val, "${", "\\${");
			this.rawValue.set(val, true);
		}
		setHtmlValue(this.rawValue.get(), false);
	}
	@Override
	public Map<String, Object> getJson(Map<String, Object> sink) {
		CH.m(sink, "template", this.rawValue.getValue());
		return super.getJson(sink);
	}

	public void testHtml(String value, StringBuilder errorSink) {
		AmiWebQueryFormPortlet form = this.getForm();
		AmiWebService service = form.getService();
		form.getScriptManager().parseAmiScriptTemplate(value, form.getTemplateVarTypes(), errorSink, service.getDebugManager(), AmiDebugMessage.TYPE_TEST, this, FORMULA_DIV_HTML,
				null);
	}
	@Override
	public boolean setValue(Object value) {
		return true;
	}

	public boolean setHtmlValue(Object value, boolean currentValue) {
		this.rawValue.setValue(AmiUtils.s(value), currentValue);
		AmiWebQueryFormPortlet form = this.getForm();
		StringBuilder errorSink = new StringBuilder();
		AmiWebService service = form.getService();
		this.usedConstVars.clear();
		if (this.rawValue.get() == null)
			this.calc = null;
		else
			this.calc = form.getScriptManager().parseAmiScriptTemplate(this.rawValue.get(), form.getTemplateVarTypes(), errorSink, service.getDebugManager(),
					AmiDebugMessage.TYPE_FORMULA, this, FORMULA_DIV_HTML, this.usedConstVars);
		this.referencedVarsInTemplate.clear();
		DerivedHelper.getDependencyIds(this.calc, (Set) this.referencedVarsInTemplate);
		CalcFrame vars = form.getTemplateVars();
		processHtmlTemplate(vars);
		return true;
	}
	public String getHtmlValue(boolean currentValue) {
		return this.rawValue.getValue(currentValue);
	}

	public void processHtmlTemplate(CalcFrame vars) {
		AmiWebQueryFormPortlet form = this.getForm();
		AmiWebService service = form.getService();
		final String text;
		if (this.calc != null) {
			text = AmiUtils.s(form.getScriptManager().executeAmiScript(this.rawValue.get(), null, this.calc, vars, service.getDebugManager(), AmiDebugMessage.TYPE_FORMULA, this,
					FORMULA_DIV_HTML));
		} else
			text = "";
		getField().setValue(service.cleanHtml(text));
		this.needsTemplateUpdate = false;
	}
	public Object getValue(int i) {
		if (i == 0)
			return rawValue.get();
		return super.getValue();
	}
	public boolean onVarChanged(String varName) {
		if (!needsTemplateUpdate && this.referencedVarsInTemplate.contains(varName))
			return needsTemplateUpdate = true;
		return false;
	}
}