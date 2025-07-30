package com.f1.ami.web.tree;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebPanelSettingsPortlet;
import com.f1.ami.web.AmiWebScriptManagerForLayout;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebWhereClause.WhereClause;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;

public class AmiWebTreeSettingsPortlet<T extends AmiWebTreePortlet> extends AmiWebPanelSettingsPortlet implements FormPortletContextMenuListener, FormPortletContextMenuFactory {

	protected final T treegrid;
	private FormPortletTextField whereField;
	private FormPortletTextField whereRuntimeField;
	private FormPortletTextField rowBackgroundColorField;
	private FormPortletTextField rowTextColorField;
	private FormPortletTextField visibleColumnsLimitField;

	public AmiWebTreeSettingsPortlet(PortletConfig config, T treegrid) {
		super(config, treegrid);
		this.treegrid = treegrid;

		FormPortlet settingsForm = getSettingsForm();

		// Row Styling
		settingsForm.addField(new FormPortletTitleField("Row Styling"));
		String rowTextColor = this.treegrid.getTreePortletFormatter().getRowTextColor();
		String rowBackgroundColor = this.treegrid.getTreePortletFormatter().getRowBackgroundColor();
		this.rowTextColorField = settingsForm.addField(new FormPortletTextField("Text").setValue(rowTextColor));
		this.rowTextColorField.setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH);
		this.rowBackgroundColorField = settingsForm.addField(new FormPortletTextField("Background").setValue(rowBackgroundColor));
		this.rowBackgroundColorField.setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH);

		this.whereField = new FormPortletTextField("Default (on login):").setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH).setMaxChars(4096);
		this.whereRuntimeField = new FormPortletTextField("Current (not saved):").setHasButton(true).setWidth(FormPortletField.WIDTH_STRETCH).setMaxChars(4096);

		this.whereField.setValue(this.treegrid.getDefaultWhereFilter());
		this.whereRuntimeField.setValue(this.treegrid.getCurrentRuntimeFilter());

		settingsForm.addField(new FormPortletTitleField("WHERE FILTER"));
		settingsForm.addField(this.whereField);
		settingsForm.addField(this.whereRuntimeField);

		settingsForm.addField(new FormPortletTitleField("Visible Columns Limit"));
		this.visibleColumnsLimitField = new FormPortletTextField("");
		settingsForm.addField(this.visibleColumnsLimitField).setWidthPx(100);
		if (treegrid.getTree().getVisibleColumnsLimit() != -1)
			this.visibleColumnsLimitField.setValue(Caster_String.INSTANCE.cast(treegrid.getTree().getVisibleColumnsLimit()));

		setSuggestedSize(400, 200);
		settingsForm.addMenuListener(this);
		settingsForm.setMenuFactory(this);

	}
	@Override
	protected boolean verifyChanges() {
		StringBuilder errorSink = new StringBuilder();
		this.treegrid.getTreePortletFormatter().validateFormula(this.rowBackgroundColorField.getValue(), errorSink);
		this.treegrid.getTreePortletFormatter().validateFormula(this.rowTextColorField.getValue(), errorSink);

		try {
			Caster_Integer.PRIMITIVE.cast(this.visibleColumnsLimitField.getValue());
		} catch (Exception e) {
			errorSink.append("Invalid value for Visible Columns Limit field.");
		}
		//TODO: WHERE
		WhereClause whereFm = this.treegrid.compileWhereFilter(this.whereField.getValue(), errorSink);
		if (whereFm != null && whereFm.getReturnType() != Boolean.class)
			errorSink.append("Where Filter must return type of Boolean, not: " + this.treegrid.getScriptManager().forType(whereFm.getReturnType()));
		WhereClause whereRuntimeFm = this.treegrid.compileWhereFilter(this.whereRuntimeField.getValue(), errorSink);
		if (whereRuntimeFm != null && whereRuntimeFm.getReturnType() != Boolean.class)
			errorSink.append("Where Runtime Filter must return type of Boolean, not: " + this.treegrid.getScriptManager().forType(whereFm.getReturnType()));
		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
			return false;
		}
		return super.verifyChanges();
	}
	@Override
	protected void submitChanges() {
		StringBuilder errorSink = new StringBuilder();

		String rowBgColor = this.rowBackgroundColorField.getValue();
		String rowColor = this.rowTextColorField.getValue();
		this.treegrid.getTreePortletFormatter().setFormula(rowColor, rowBgColor, errorSink);
		AmiWebScriptManagerForLayout sm = this.treegrid.getScriptManager();

		//TODO: WHERE
		WhereClause whereFm = this.treegrid.compileWhereFilter(this.whereField.getValue(), errorSink);
		if (whereFm != null && whereFm.getReturnType() != Boolean.class)
			errorSink.append("Where Filter must return type of Boolean, not: " + sm.forType(whereFm.getReturnType()));
		WhereClause whereRuntimeFm = this.treegrid.compileWhereFilter(this.whereRuntimeField.getValue(), errorSink);
		if (whereRuntimeFm != null && whereRuntimeFm.getReturnType() != Boolean.class) {
			errorSink.append("Where Runtime Filter must return type of Boolean, not: " + sm.forType(whereFm.getReturnType()));
		}
		if (errorSink.length() > 0) {
			getManager().showAlert(errorSink.toString());
			return;
		}
		this.treegrid.setCurrentRuntimeFilter(this.whereField.getValue(), false);
		this.treegrid.setCurrentRuntimeFilter(this.whereRuntimeField.getValue(), true);
		int lim = Caster_Integer.INSTANCE.cast(this.visibleColumnsLimitField.getValue()) == null ? -1 : Caster_Integer.PRIMITIVE.cast(this.visibleColumnsLimitField.getValue());
		this.treegrid.getTree().setVisibleColumnsLimit(lim);

		this.treegrid.rebuildCalcs();
		super.submitChanges();
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		AmiWebService service = this.getPortlet().getService();
		BasicWebMenu r = new BasicWebMenu();
		AmiWebMenuUtils.createVariablesMenu(r, false, this.getPortlet());
		AmiWebMenuUtils.createColorsMenu(r, this.treegrid.getStylePeer());
		return r;
	}

	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		AmiWebMenuUtils.processContextMenuAction(getPortlet().getService(), action, node);
	}

	@Override
	public int getSuggestedWidth(PortletMetrics pm) {
		return 600;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return 700;
	}
}
