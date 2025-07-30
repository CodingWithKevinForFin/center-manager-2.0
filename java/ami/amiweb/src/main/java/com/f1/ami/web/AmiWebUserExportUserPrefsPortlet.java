package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.base.Row;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.table.WebColumn;
import com.f1.suite.web.table.WebContextMenuListener;
import com.f1.suite.web.table.WebTable;
import com.f1.suite.web.table.fast.FastWebTable;
import com.f1.suite.web.table.impl.BasicWebColumn;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebUserExportUserPrefsPortlet extends GridPortlet implements WebContextMenuListener, FormPortletListener {

	private AmiWebService service;
	private FormPortlet formPortlet;
	private FastTablePortlet ftp;
	private List<Map<String, Object>> userPreferences;
	private Map<Tuple2<String, String>, Map<String, Object>> userPreferencesById;
	private Map<String, Map<String, Object>> customPreferencesById;
	private FormPortletTextAreaField textAreaField;
	private FormPortletButton closeButton;
	private FormPortletToggleButtonsField<Byte> showCompactField;

	public AmiWebUserExportUserPrefsPortlet(PortletConfig config, AmiWebService service) {
		super(config);
		this.service = service;
		this.formPortlet = new FormPortlet(generateConfig());
		FormPortletTitleField titleField = this.formPortlet.addField(new FormPortletTitleField(""));
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int width = root.getWidth();

		this.showCompactField = this.formPortlet.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Json whitespace Formatting:"));
		if (width < 1440) {
			this.showCompactField.setTitle("Json Format:");
		}
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_COMPACT, "Compact");
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_SEMI, "Legible");
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_CLEAN, "Expanded");
		this.showCompactField.setValue(ObjectToJsonConverter.MODE_SEMI);
		this.closeButton = new FormPortletButton("Close");
		this.formPortlet.addButton(this.closeButton);
		this.textAreaField = formPortlet.addField(new FormPortletTextAreaField(""));
		BasicTable table = new BasicTable(String.class, "layout", String.class, "label", String.class, "upid", String.class, "type");
		this.userPreferences = this.service.getUserPrefs();
		this.userPreferencesById = new HashMap<Tuple2<String, String>, Map<String, Object>>();
		this.customPreferencesById = new HashMap<String, Map<String, Object>>();
		boolean hasLayout = false;
		for (Map<String, Object> i : userPreferences) {
			String upid = (String) i.get("upid");
			String layout = (String) i.get("layout");
			if (SH.isnt(layout))
				layout = "";
			else
				hasLayout = true;
			if (upid != null) {
				table.getRows().addRow(WebHelper.escapeHtml(layout), WebHelper.escapeHtml(upid), upid, "Panel");
				this.userPreferencesById.put(new Tuple2<String, String>(layout, upid), i);
			} else {
				if (!service.getDesktop().getIsLocked()) {
					String cpid = (String) i.get("cpid");
					if (cpid != null) {
						table.getRows().addRow("", "<i>" + WebHelper.escapeHtml(cpid) + "&nbsp;(custom)</i>", cpid, "Custom");
						this.customPreferencesById.put(cpid, i);
					}
				}
			}
		}
		this.ftp = new FastTablePortlet(generateConfig(), table, "upid");
		this.ftp.addOption(FastTablePortlet.OPTION_USE_GREY_BARS, false);
		this.ftp.addOption(FastTablePortlet.OPTION_CELL_BORDER_COLOR, "#ffffff");
		this.ftp.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, "true");
		this.ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, false);
		this.ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HEIGHT, 22);
		this.ftp.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_FONT_SZ, 12);
		this.ftp.addOption(FastTablePortlet.OPTION_TITLE_DIVIDER_HIDDEN, true);
		this.ftp.getTable().setGrayBarsSupported(false);
		BasicWebColumn layout = this.ftp.getTable().addColumn(true, "Layout", "layout", service.getFormatterManager().getHtmlWebCellFormatter()).setWidth(98);
		this.ftp.getTable().addColumn(true, "Preference ID", "label", service.getFormatterManager().getHtmlWebCellFormatter()).setWidth(148);
		if (!hasLayout)
			this.ftp.getTable().hideColumn(layout.getColumnId());

		this.ftp.getTable().addMenuListener(this);
		this.formPortlet.addFormPortletListener(this);
		textAreaField.setLeftPosPx(5);
		textAreaField.setTopPosPx(30);
		textAreaField.setBottomPosPx(50);
		textAreaField.setRightPosPx(5);
		showCompactField.setTopPosPx(5);
		showCompactField.setHeightPx(20);
		showCompactField.setRightPosPx(5);
		showCompactField.setWidthPx(260);
		titleField.setTopPosPx(5);
		titleField.setHeightPx(25);
		titleField.setLeftPosPx(5);
		titleField.setWidthPx(600);
		titleField.setValue("Select and copy the text below into you clip board using Ctrl+C");
		DividerPortlet div = new DividerPortlet(generateConfig(), true, ftp, this.formPortlet);
		div.setOffsetFromTopPx(hasLayout ? 250 : 151);
		addChild(div);
		updateSelection();
	}
	@Override
	public void onContextMenu(WebTable table, String action) {
	}

	@Override
	public void onCellClicked(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onCellMousedown(WebTable table, Row row, WebColumn col) {
	}

	@Override
	public void onSelectedChanged(FastWebTable fastWebTable) {
		updateSelection();
	}

	private void updateSelection() {
		List<Row> selectedRows = ftp.getTable().getSelectedRows();
		final List<Map<String, Object>> userPreferences;
		if (selectedRows.isEmpty()) {
			userPreferences = this.userPreferences;
		} else {
			userPreferences = new ArrayList<Map<String, Object>>();
			for (Row row : selectedRows) {
				String layout = CH.getOr(Caster_String.INSTANCE, row, "layout", "");
				String upid = (String) row.get("upid");
				String type = (String) row.get("type");
				if ("Panel".equals(type))
					userPreferences.add(this.userPreferencesById.get(new Tuple2<String, String>(layout, upid)));
				else
					userPreferences.add(this.customPreferencesById.get(upid));
			}
		}
		ObjectToJsonConverter json = ObjectToJsonConverter.getInstance((byte) this.showCompactField.getValue());
		this.textAreaField.setValue(json.objectToString(userPreferences));
		this.textAreaField.setSelection(0, this.textAreaField.getValue().length());
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.showCompactField)
			updateSelection();
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}
	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}
	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
