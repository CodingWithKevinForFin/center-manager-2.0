package com.f1.ami.web;

import java.util.List;

import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.utils.concurrent.LinkedHasherSet;
import com.f1.utils.structs.table.derived.DeclaredMethodFactory;
import com.f1.utils.structs.table.derived.ParamsDefinition;

public class AmiWebViewMethodPortlet extends GridPortlet implements AmiWebSpecialPortlet {

	private FormPortlet form;
	private AmiWebFormPortletAmiScriptField editor;
	private LinkedHasherSet<ParamsDefinition> methods;
	private AmiWebService service;
	private String layoutAlias;
	private LinkedHasherSet<ParamsDefinition> paramsDefinitions;

	public AmiWebViewMethodPortlet(PortletConfig config, AmiWebService service, String layoutAlias, LinkedHasherSet<ParamsDefinition> defs) {
		super(config);
		this.paramsDefinitions = defs;
		this.layoutAlias = layoutAlias;
		this.service = service;
		this.form = new FormPortlet(generateConfig());
		this.addChild(form);
		this.editor = new AmiWebFormPortletAmiScriptField("", this.getManager(), layoutAlias);
		this.editor.setLeftTopRightBottom(0, 0, 0, 0);
		this.editor.setValue("asdf");
		this.editor.setDisabled(true);
		this.form.addField(editor);
		this.methods = defs;
		updateText();
	}

	public String getText() {
		return this.editor.getValue();
	}

	private void updateText() {
		AmiWebScriptManagerForLayout scriptManager = service.getScriptManager(this.layoutAlias);
		List<DeclaredMethodFactory> mf = scriptManager.getDeclaredMethodFactories();
		StringBuilder sb = new StringBuilder();
		for (DeclaredMethodFactory i : mf) {
			if (inList(i)) {
				sb.append(i.getText(scriptManager.getMethodFactory())).append("\n\n\n");
			}
		}
		this.editor.setValue(sb.toString());
	}

	private boolean inList(DeclaredMethodFactory i) {
		return this.methods.contains(i.getDefinition());
	}

	public LinkedHasherSet<ParamsDefinition> getParamDefinitions() {
		return this.paramsDefinitions;
	}

	public void recompileAmiScript() {
		updateText();
	}

}
