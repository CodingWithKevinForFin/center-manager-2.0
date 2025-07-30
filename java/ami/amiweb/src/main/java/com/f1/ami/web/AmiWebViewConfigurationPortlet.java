package com.f1.ami.web;

import java.util.LinkedHashSet;
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
import com.f1.utils.SH;
import com.f1.utils.StringFormatException;
import com.f1.utils.assist.RootAssister;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.table.BasicTable;

public class AmiWebViewConfigurationPortlet extends GridPortlet implements FormPortletListener, WebContextMenuListener {

	private static final String CONFIG_FULL = "";
	private static final String CONFIG_PARENT = "..";
	private Object configuration;
	private Object relativeConfiguration;
	private String absolutePath = null;
	protected FormPortlet formPortlet;
	private FastTablePortlet ftp;
	private FormPortletTextAreaField textAreaField;
	private FormPortletButton closeButton;
	private FormPortletToggleButtonsField<Byte> showCompactField;

	public AmiWebViewConfigurationPortlet(PortletConfig config) {
		super(config);
		BasicTable table = new BasicTable(String.class, "relativePath");
		this.ftp = new FastTablePortlet(generateConfig(), table, "upid");
		this.ftp.addOption(FastTablePortlet.OPTION_USE_GREY_BARS, false);
		this.ftp.addOption(FastTablePortlet.OPTION_CELL_BORDER_COLOR, "#ffffff");
		this.ftp.addOption(FastTablePortlet.OPTION_MENU_BAR_HIDDEN, "true");
		this.ftp.getTable().setGrayBarsSupported(false);
		this.ftp.getTable().addColumn(true, "Keys", "relativePath", AmiWebUtils.getService(getManager()).getFormatterManager().getHtmlWebCellFormatter());
		this.ftp.getTable().addMenuListener(this);
		this.formPortlet = new FormPortlet(generateConfig());
		FormPortletTitleField titleField = this.formPortlet.addField(new FormPortletTitleField(""));
		RootPortlet root = (RootPortlet) getManager().getRoot();
		int width = root.getWidth();
		if (width < 1440) {
			this.showCompactField = this.formPortlet.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Json Format:"));
		} else {
			this.showCompactField = this.formPortlet.addField(new FormPortletToggleButtonsField<Byte>(Byte.class, "Json whitespace Formatting:"));
		}
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_COMPACT, "Compact");
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_SEMI, "Legible");
		this.showCompactField.addOption(ObjectToJsonConverter.MODE_CLEAN, "Expanded");
		this.showCompactField.setValue(AmiWebUtils.getService(this.getManager()).getLayoutFilesManager().getUserExportMode());
		this.textAreaField = formPortlet.addField(new FormPortletTextAreaField(""));
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
		this.formPortlet.addFormPortletListener(this);
		this.closeButton = new FormPortletButton("Close");
		this.formPortlet.addButton(this.closeButton);
		this.enableBrowser(false); //For now disabled by default
	}

	public AmiWebViewConfigurationPortlet enableBrowser(boolean enabled) {
		if (this.getChildrenCount() > 0)
			this.removeAllChildren();

		if (enabled == false) {
			// Add only config panel
			this.addChild(this.formPortlet);
			this.updateBrowser(null);
		} else {
			// Add a divider that contains both the browser and config panel
			DividerPortlet div = new DividerPortlet(generateConfig(), true, ftp, this.formPortlet);
			div.setOffsetFromTopPx(0);
			this.addChild(div);
			this.updateBrowser(CONFIG_FULL);
		}
		return this;
	}
	private void updateBrowser(String fullPath) {
		this.ftp.clearRows();
		Object relativeConfiguration = this.configuration;
		if (fullPath != null) {
			LinkedHashSet<String> relativePaths = new LinkedHashSet<String>();
			if (!SH.equals(CONFIG_FULL, fullPath)) {
				relativeConfiguration = RootAssister.INSTANCE.getNestedValue(this.configuration, fullPath, true);
				this.ftp.addRow(CONFIG_PARENT);
			}
			if (relativeConfiguration instanceof List) {
				List l = (List) relativeConfiguration;
				for (int i = 0; i < l.size(); i++) {
					relativePaths.add(SH.toString(i));
				}
			} else if (relativeConfiguration instanceof Map) {
				Map m = (Map) relativeConfiguration;
				relativePaths.addAll(m.keySet());
			}
			for (String relativePath : relativePaths) {
				this.ftp.addRow(relativePath);
			}
			this.ftp.getTable().sortRows("relativePath", true, true, false);
		}
		this.absolutePath = fullPath;
	}
	public void setConfiguration(Object config) {
		this.configuration = config;
		this.relativeConfiguration = this.absolutePath == null || SH.equals(CONFIG_FULL, this.absolutePath) ? config
				: RootAssister.INSTANCE.getNestedValue(this.configuration, this.absolutePath, true);
		this.updateText();
		this.updateBrowser(this.absolutePath);
	}

	private void updateConfiguration() {
		ObjectToJsonConverter c = ObjectToJsonConverter.getInstance(showCompactField.getValue());
		try {
			if (absolutePath == null || SH.equals(CONFIG_FULL, absolutePath)) {
				Map<String, Object> newConfig = (Map<String, Object>) c.stringToObject(this.textAreaField.getValue());
				this.configuration = newConfig;
				this.relativeConfiguration = newConfig;
				this.updateBrowser(this.absolutePath);
				this.showCompactField.setDisabled(false);
			} else {
				String parentPath = SH.beforeLast(this.absolutePath, '.', CONFIG_FULL);
				String relativePath = SH.afterLast(this.absolutePath, '.', this.absolutePath);
				Map<String, Object> newFullConfig = (Map<String, Object>) c.stringToObject(AmiWebLayoutHelper.toJson(this.configuration, showCompactField.getValue()));
				Object parentConfig = SH.equals(CONFIG_FULL, parentPath) ? newFullConfig : RootAssister.INSTANCE.getNestedValue(newFullConfig, parentPath, true);
				Object newConfig = c.stringToObject(this.textAreaField.getValue());
				if (parentConfig instanceof List) {
					List l = (List) parentConfig;
					l.set(Caster_Integer.PRIMITIVE.cast(relativePath), newConfig);

				} else if (parentConfig instanceof Map) {
					Map m = (Map) parentConfig;
					m.put(relativePath, newConfig);

				}
				this.configuration = newFullConfig;
				this.relativeConfiguration = newConfig;
				this.updateBrowser(this.absolutePath);
				this.showCompactField.setDisabled(false);

			}
		} catch (StringFormatException e) {
			this.showCompactField.setDisabled(true);
		}
	}
	private void updateText() {
		byte mode = showCompactField.getValue();
		this.textAreaField.setValue(AmiWebLayoutHelper.toJson(this.relativeConfiguration, mode));
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.showCompactField) {
			this.updateText();
		}
		if (field == this.textAreaField) {
			this.updateConfiguration();
		}

	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
		List<Row> selected = this.ftp.getTable().getSelectedRows();
		if (selected.size() != 1)
			return;
		Row row = selected.get(0);
		String relativePath = row.get("relativePath", String.class);
		String newAbsolutePath;
		if (SH.equals(CONFIG_PARENT, relativePath))
			newAbsolutePath = SH.beforeLast(this.absolutePath, '.', CONFIG_FULL);
		else if (SH.equals(CONFIG_FULL, this.absolutePath))
			newAbsolutePath = relativePath;
		else
			newAbsolutePath = SH.join('.', this.absolutePath, relativePath);

		if (this.absolutePath == newAbsolutePath)
			return;
		this.absolutePath = newAbsolutePath;
		this.setConfiguration(this.configuration);
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
	}

	@Override
	public void onNoSelectedChanged(FastWebTable fastWebTable) {
	}

	@Override
	public void onScroll(int viewTop, int viewPortHeight, long contentWidth, long contentHeight) {
	}
}
