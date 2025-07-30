package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.web.realtimetree.AmiWebRealtimeTreePortlet;
import com.f1.ami.web.tree.AmiWebTreeGroupBy;
import com.f1.base.Row;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.visual.TileFormatter;
import com.f1.suite.web.portal.impl.visual.TilesListener;
import com.f1.suite.web.portal.impl.visual.TilesPortlet;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.OneToOne;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.table.BasicSmartTable;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.stack.BasicCalcTypes;

public class AmiWebChooseDataForm extends GridPortlet implements FormPortletListener, TilesListener, TileFormatter {

	private static final byte TYPE_SIMPLE = 1;
	private static final byte TYPE_AGG = 3;
	private static final byte TYPE_TREEMAP = 4;
	private static final byte TYPE_TREE = 2;

	private static final byte FIELD_GROUPBY = 0;
	private static final byte FIELD_NONE = 1;
	private static final byte FIELD_SUM = 2;
	private static final byte FIELD_AVG = 3;
	private static final byte FIELD_MIN = 4;
	private static final byte FIELD_MAX = 5;
	private static final byte FIELD_COUNT = 6;
	private static final String NAME_ID_SUFFIX = "!name";
	private static final String TYPE_ID_SUFFIX = "!type";
	private AmiWebService service;
	private FormPortlet columnsForm;
	private String targetPortletId;
	private AmiWebDesktopPortlet desktop;
	private OneToOne<String, String> columnNamesToIds = new OneToOne<String, String>();
	private TilesPortlet displayTypePanel;
	private FormPortletTextField titleField;
	private FormPortlet amiSqlForm;
	private HtmlPortlet amiSqlHints;
	private FormPortletSelectField<String> aliasField;
	private FormPortletTextField panelIdField;
	private List<String> typeNames = new ArrayList<String>();
	private FormPortletButton cancelButton;
	private FormPortletButton submitButton;
	private int width;
	private int height;

	public AmiWebChooseDataForm(PortletConfig config, AmiWebDesktopPortlet amiWebDesktopPortlet, String targetPortletId, boolean includeNavigationButtons) {
		super(config);
		this.desktop = amiWebDesktopPortlet;
		this.targetPortletId = targetPortletId;
		this.service = (AmiWebService) getManager().getService(AmiWebService.ID);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		width = MH.min(AmiWebDesktopPortlet.MAX_WIDTH, (int) (root.getWidth() * 0.8));
		height = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (root.getHeight() * 0.8));
		this.initPortlets();
		addChild(displayTypePanel, 0, 0);
		InnerPortlet t = addChild(amiSqlForm, 1, 0, 1, 1);
		InnerPortlet t2 = addChild(columnsForm, 2, 0, 1, 1);
		setCssStyle("_bg=#aaaaaa");
		t2.setPadding(20).setCssStyle("style.boxShadow=0px 0px 8px 2px #000000");
		t.setPadding(20).setCssStyle("style.boxShadow=0px 0px 8px 2px #000000");

		setColSize(0, 210);
		setColSize(1, 500);
		this.initPanel1();
		this.initPanel2();
		this.columnsForm.setLabelsWidth(400);
		if (includeNavigationButtons) {
			this.cancelButton = this.columnsForm.addButton(new FormPortletButton("Cancel"));
			this.submitButton = this.columnsForm.addButton(new FormPortletButton("Create"));
		}

		setSuggestedSize(width, height);
	}

	private void initPortlets() {
		displayTypePanel = new TilesPortlet(generateConfig());
		columnsForm = new FormPortlet(generateConfig());
		amiSqlForm = new FormPortlet(generateConfig());
		amiSqlHints = new HtmlPortlet(generateConfig());
	}
	private void initPanel1() {

		displayTypePanel.setTable(new BasicSmartTable(new BasicTable(new String[] { "name", "id", "img", "cn" })));
		displayTypePanel.setTileFormatter(this);
		displayTypePanel.addRow("Table", TYPE_SIMPLE, "icon_table_st", "ami_display_realtimeTable");
		displayTypePanel.addRow("Aggregate Table", TYPE_AGG, "icon_table_ag", "ami_display_aggregateTable");
		displayTypePanel.addRow("Heat Map", TYPE_TREEMAP, "icon_table_tm", "ami_display_heatmap");
		displayTypePanel.addRow("Tree", TYPE_TREE, "icon_tree", "ami_display_tree");
		displayTypePanel.addOption(TilesPortlet.OPTION_TILE_WIDTH, 180);
		displayTypePanel.addOption(TilesPortlet.OPTION_TILE_HEIGHT, 130);
		displayTypePanel.addOption(TilesPortlet.OPTION_TILE_PADDING, 30);
		displayTypePanel.addOption(TilesPortlet.OPTION_ALIGN, TilesPortlet.VALUE_ALIGN_CENTER);
		displayTypePanel.addOption(TilesPortlet.OPTION_CSS_STYLE, "_bg=#4c4c4c");
		displayTypePanel.setMultiselectEnabled(false);
		displayTypePanel.setActiveTileByPosition(0);
		displayTypePanel.addTilesListener(this);
	}
	private void initPanel2() {
		titleField = new FormPortletTextField("Title:");
		aliasField = new FormPortletSelectField<String>(String.class, "Owning Layout:");
		panelIdField = new FormPortletTextField("Panel Id:");
		amiSqlForm.addField(new FormPortletTitleField(""));//padding
		amiSqlForm.addField(new FormPortletTitleField("Panel Details"));
		amiSqlForm.getFormPortletStyle().setLabelsWidth(150);
		amiSqlForm.addField(titleField).setWidth(250);
		amiSqlForm.addField(panelIdField).setWidth(250);
		amiSqlForm.addField(aliasField).setWidth(250);
		amiSqlForm.addFormPortletListener(this);
		columnsForm.getFormPortletStyle().setLabelsWidth(200);
		columnsForm.addFormPortletListener(this);
		AmiWebLayoutFilesManager layoutFilesManager = this.service.getLayoutFilesManager();
		AmiWebAliasPortlet removed = (AmiWebAliasPortlet) getManager().getPortletNoThrow(this.targetPortletId);
		if (removed != null) {
			for (String s : layoutFilesManager.getAvailableAliasesDown(removed.getAmiParent().getAmiLayoutFullAlias())) {
				this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
			}
			this.aliasField.setValue(removed.getAmiLayoutFullAlias());
		} else {
			for (String s : layoutFilesManager.getAvailableAliasesDown("")) {
				this.aliasField.addOption(s, AmiWebUtils.formatLayoutAlias(s));
			}
			this.aliasField.setValue("");
		}

		panelIdField.setValue(service.getNextPanelId(aliasField.getValue(), "PNL1"));
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.aliasField) {
			String fullAlias = this.aliasField.getValue();
			String panelId = SH.trim(this.panelIdField.getValue());
			if (SH.is(panelId)) {
				if (service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(fullAlias, panelId)) != null) {
					panelIdField.setValue(service.getNextPanelId(fullAlias, panelId));
				}
			}
		} else if (portlet == this.columnsForm) {

			boolean enabled;
			if (this.getDisplayType() == TYPE_AGG || this.getDisplayType() == TYPE_TREE) {
				if (field.getId().endsWith("name")) {

				} else if (field.getId().endsWith("type")) {

				} else {
					Byte aggtype = (Byte) field.getValue();
					if (aggtype == FIELD_NONE)
						enabled = false;
					else
						enabled = true;
					if (aggtype == FIELD_GROUPBY) {
						field.setCssStyle("_fm=bold");
					} else {
						field.setCssStyle("_fm=");
					}
					String name = this.columnNamesToIds.getKey(SH.beforeFirst(field.getId(), '!'));
					String displayName;
					if (name.length() <= 2 && AmiWebUtils.RESERVED_PARAMS.containsKey(name)) {
						displayName = AmiWebUtils.RESERVED_PARAMS.get(name);
					} else
						displayName = name;
					updateAggregateField(name, displayName);
					((FormPortletTextField) columnsForm.getField(field.getId() + NAME_ID_SUFFIX)).setDisabled(!enabled);
					((FormPortletSelectField) columnsForm.getField(field.getId() + TYPE_ID_SUFFIX)).setDisabled(!enabled);
				}
			} else if (this.getDisplayType() == TYPE_TREEMAP) {
				if (field.getId().endsWith("Label")) {
					Byte aggtype = (Byte) field.getValue();
					if (aggtype == FIELD_NONE)
						enabled = false;
					else
						enabled = true;
					String id = SH.stripSuffix(field.getId(), "Label", true);
					columnsForm.getField(id).setDisabled(!enabled);
				}
			} else if (this.getDisplayType() == TYPE_SIMPLE) {
				if (field instanceof FormPortletCheckboxField) {
					enabled = (Boolean) field.getValue();
					((FormPortletTextField) columnsForm.getField(field.getId() + NAME_ID_SUFFIX)).setDisabled(!enabled);
					((FormPortletSelectField) columnsForm.getField(field.getId() + TYPE_ID_SUFFIX)).setDisabled(!enabled);
				}
			}
		}

	}
	private void updateAggregateField(String name, String displayName) {
		byte aggtype = getColumnAggType(name);
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		FormPortletSelectField<Byte> select = (FormPortletSelectField<Byte>) this.columnsForm.getField(id + TYPE_ID_SUFFIX);
		boolean allowProgress = OH.eq("KEEP_PROGRESS", select.getCorrelationData());
		switch (aggtype) {
			case FIELD_COUNT:
				setColumnName(name, "Count of " + displayName);
				allowProgress = false;
				break;
			case FIELD_AVG:
				setColumnName(name, "Avg " + displayName);
				allowProgress = true;
				break;
			case FIELD_NONE:
				setColumnName(name, "");
				break;
			case FIELD_GROUPBY:
				setColumnName(name, displayName);
				break;
			case FIELD_MAX:
				setColumnName(name, "Max " + displayName);
				break;
			case FIELD_MIN:
				setColumnName(name, "Min " + displayName);
				break;
			case FIELD_SUM:
				setColumnName(name, "Total " + displayName);
				break;
		}
		if (allowProgress)
			select.addOptionNoThrow(AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS, "Progress Bar");
		else
			select.removeOptionNoThrow(AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS);
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public void setTypes(List<String> names) {
		this.typeNames.clear();
		this.typeNames.addAll(names);
		refreshWizard();
	}
	public void refreshWizard() {
		if (typeNames.size() == 0)
			return;
		if (typeNames.size() == 1)
			this.titleField.setValue(SH.afterFirst(typeNames.get(0), ':'));
		else {
			List<String> t = new ArrayList<String>(typeNames.size());
			for (String s : typeNames)
				t.add(SH.afterFirst(s, ':'));
			Collections.sort(t);
			this.titleField.setValue(SH.join(", ", t));
		}
		this.amiSqlHints.setHtml("");
		com.f1.utils.structs.table.stack.BasicCalcTypes paramToSchema = new com.f1.utils.structs.table.stack.BasicCalcTypes();
		for (String typeName : typeNames) {
			com.f1.base.CalcTypes schema = service.getWebManagers().getAmiObjectsByType(typeName).getRealtimeObjectsOutputSchema();
			if (schema.isVarsEmpty())
				return; // no schema
			for (String varname : schema.getVarKeys()) {
				if ("A".equals(varname))
					continue;
				Class<?> widestIgnoreNull = OH.getWidestIgnoreNull(paramToSchema.getType(varname), schema.getType(varname));
				paramToSchema.putType(varname, widestIgnoreNull);
			}
		}

		columnsForm.clearFields();
		//		FormPortletTitleField title = columnsForm.addField(new FormPortletTitleField("Display Configuration"));
		//		title.setRightPosPct(.5);
		//		title.setRightPosPx(1);
		//		title.setTopPosPx(50);
		int y = 40;
		//		title.setLeftPosPct(.5).setLeftPosPx(-100).setTopPosPx(y).setWidthPx(200).setHeightPx(25);
		//		y += 40;
		byte displayType = getDisplayType();
		if (displayType == TYPE_TREEMAP) {
			columnsForm.addField(new FormPortletTitleField("Mode")).setRightPosPct(.5).setRightPosPx(2).setTopPosPx(y).setWidthPx(100).setHeightPx(25);
			columnsForm.addField(new FormPortletTitleField("Value")).setLeftPosPct(.5).setLeftPosPx(2).setTopPosPx(y).setWidthPx(200).setHeightPx(25);
			FormPortletSelectField<Byte> f = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "Top Level Grouping:").setId("groupingLabel"));
			f.setRightPosPct(.5).setRightPosPx(2).setTopPosPx(y += 30).setWidthPx(100).setHeightPx(25);
			f.addOption(FIELD_GROUPBY, "Group By");
			f.addOption(FIELD_NONE, "None");
			f = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "Grouping:").setId("labelLabel"));
			f.setRightPosPct(.5).setRightPosPx(2).setTopPosPx(y += 30).setWidthPx(100).setHeightPx(25);
			f.addOption(FIELD_GROUPBY, "Group By");
			f.addOption(FIELD_NONE, "None");
			f = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "Size:").setId("sizeLabel"));
			f.setRightPosPct(.5).setRightPosPx(2).setTopPosPx(y += 30).setWidthPx(100).setHeightPx(25);
			f.addOption(FIELD_NONE, "None");
			f.addOption(FIELD_COUNT, "Count");
			f.addOption(FIELD_SUM, "Sum");
			f.addOption(FIELD_MAX, "Max");
			f.addOption(FIELD_MIN, "Min");
			f.addOption(FIELD_AVG, "Average");
			f.setValue(FIELD_SUM);
			f = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "Color:").setId("heatLabel"));
			f.setRightPosPct(.5).setRightPosPx(2).setTopPosPx(y += 30).setWidthPx(100).setHeightPx(25);
			f.addOption(FIELD_NONE, "None");
			f.addOption(FIELD_COUNT, "Count");
			f.addOption(FIELD_SUM, "Sum");
			f.addOption(FIELD_MAX, "Max");
			f.addOption(FIELD_MIN, "Min");
			f.addOption(FIELD_AVG, "Average");
			f.setValue(FIELD_SUM);
			FormPortletSelectField<String> g = columnsForm.addField(new FormPortletSelectField<String>(String.class, "").setId("grouping"));
			FormPortletSelectField<String> l = columnsForm.addField(new FormPortletSelectField<String>(String.class, "").setId("label"));
			FormPortletSelectField<String> s = columnsForm.addField(new FormPortletSelectField<String>(String.class, "").setId("size"));
			FormPortletSelectField<String> h = columnsForm.addField(new FormPortletSelectField<String>(String.class, "").setId("heat"));
			g.setLabelHidden(true);
			l.setLabelHidden(true);
			s.setLabelHidden(true);
			h.setLabelHidden(true);
			y -= 30 * 4;
			g.setLeftPosPct(.5).setLeftPosPx(2).setTopPosPx(y += 30).setWidthPx(200).setHeightPx(25);
			l.setLeftPosPct(.5).setLeftPosPx(2).setTopPosPx(y += 30).setWidthPx(200).setHeightPx(25);
			s.setLeftPosPct(.5).setLeftPosPx(2).setTopPosPx(y += 30).setWidthPx(200).setHeightPx(25);
			h.setLeftPosPct(.5).setLeftPosPx(2).setTopPosPx(y += 30).setWidthPx(200).setHeightPx(25);
			int idd = 10;
			for (String name : paramToSchema.getVarKeys()) {
				Class<?> ctype = paramToSchema.getType(name);
				byte type = AmiUtils.getTypeForClass(ctype, AmiDatasourceColumn.TYPE_STRING);
				String displayName;
				if (name.length() <= 2 && AmiWebUtils.RESERVED_PARAMS.containsKey(name)) {
					displayName = AmiWebUtils.RESERVED_PARAMS.get(name);
				} else
					displayName = name;
				switch (type) {
					case AmiDatasourceColumn.TYPE_STRING:
					case AmiDatasourceColumn.TYPE_UUID:
					case AmiDatasourceColumn.TYPE_CHAR:
						g.addOption(name, displayName);
						l.addOption(name, displayName);
						break;
					case AmiDatasourceColumn.TYPE_DOUBLE:
					case AmiDatasourceColumn.TYPE_FLOAT:
					case AmiDatasourceColumn.TYPE_INT:
					case AmiDatasourceColumn.TYPE_LONG:
					case AmiDatasourceColumn.TYPE_BIGINT:
					case AmiDatasourceColumn.TYPE_BIGDEC:
					case AmiDatasourceColumn.TYPE_COMPLEX:
					case AmiDatasourceColumn.TYPE_UTC:
					case AmiDatasourceColumn.TYPE_UTCN:
						g.addOption(name, displayName);
						l.addOption(name, displayName);
						s.addOption(name, displayName);
						h.addOption(name, displayName);
						break;
				}
			}
			g.sortOptionsByName();
			l.sortOptionsByName();
			s.sortOptionsByName();
			g.addOption(null, "<None>");
			l.addOption(null, "<None>");
		} else {
			if (!paramToSchema.isVarsEmpty()) {
				//					columnsForm.addField(new FormPortletTitleField("Column Names")).setLeftTopWidthHeightPx(200, 10, 200, 25);
				//					columnsForm.addField(new FormPortletTitleField("Formatting")).setLeftTopWidthHeightPx(410, 10, 200, 25);
				columnsForm.addField(new FormPortletTitleField("Column Names")).setLeftPosPct(.3).setTopPosPx(y).setRightPosPct(.3).setHeightPx(25);
				columnsForm.addField(new FormPortletTitleField("Formatting")).setLeftPosPct(.7).setLeftPosPx(5).setTopPosPx(y).setWidthPx(140).setHeightPx(25);
			}
			this.columnNamesToIds.clear();
			int idd = 100;
			for (String name : paramToSchema.getVarKeys()) {
				Class<?> ctype = paramToSchema.getType(name);
				if (AmiUtils.getReservedParamType(name) != AmiDatasourceColumn.TYPE_UNKNOWN)
					continue;
				y += 30;
				String id = SH.toString(idd);
				byte type = AmiUtils.getTypeForClass(ctype, AmiDatasourceColumn.TYPE_STRING);
				columnNamesToIds.put(name, id);

				String displayName;
				if (name.length() <= 2 && AmiWebUtils.RESERVED_PARAMS.containsKey(name)) {
					displayName = AmiWebUtils.RESERVED_PARAMS.get(name);
				} else
					displayName = toDisplayName(name);
				FormPortletTextField nameField = columnsForm.addField(new FormPortletTextField("").setId(id + NAME_ID_SUFFIX)).setValue(displayName).setDisabled(false);
				nameField.setLabelHidden(true);
				nameField.setLeftPosPct(.3).setTopPosPx(y).setRightPosPct(.3).setHeightPx(25);
				FormPortletSelectField<Byte> formatField = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, "").setId(id + TYPE_ID_SUFFIX));
				formatField.setLabelHidden(true);
				formatField.setLeftPosPct(.7).setLeftPosPx(5).setTopPosPx(y).setWidthPx(140).setHeightPx(25);
				addToFormatField(type, name, formatField, AmiDatasourceColumn.HINT_NONE);
				if (displayType != TYPE_AGG && displayType != TYPE_TREE) {
					FormPortletField<Boolean> field = columnsForm.addField(new FormPortletCheckboxField(name + ": ").setId(id)).setValue(true).setCorrelationData(ctype);
					field.setHelp(name);
					field.setRightPosPct(.70).setRightPosPx(6).setTopPosPx(y - 2).setWidthPx(25).setHeightPx(25);//.setHorizontalOffsetFromCenterPct(.2);
					field.setLabelWidthPx(600);
				} else {
					FormPortletSelectField<Byte> f = columnsForm.addField(new FormPortletSelectField<Byte>(Byte.class, name + ": ").setId(id));
					f.setRightPosPct(.70).setRightPosPx(6).setTopPosPx(y - 2).setWidthPx(75).setHeightPx(25);//.setHorizontalOffsetFromCenterPct(.2);
					f.setLabelWidthPx(600);
					f.setHelp(name);
					//						f.setLeftTopWidthHeightPx(115, y, 75, 25);
					f.addOption(FIELD_NONE, "Hidden");
					f.addOption(FIELD_GROUPBY, "Group By");
					f.addOption(FIELD_COUNT, "Count");
					switch (type) {
						case AmiDatasourceColumn.TYPE_LONG:
						case AmiDatasourceColumn.TYPE_FLOAT:
						case AmiDatasourceColumn.TYPE_DOUBLE:
						case AmiDatasourceColumn.TYPE_INT:
						case AmiDatasourceColumn.TYPE_BIGINT:
						case AmiDatasourceColumn.TYPE_BIGDEC:
						case AmiDatasourceColumn.TYPE_COMPLEX:
							f.addOption(FIELD_SUM, "Sum");
							f.addOption(FIELD_MAX, "Max");
							f.addOption(FIELD_MIN, "Min");
							f.addOption(FIELD_AVG, "Average");
							f.setValue(FIELD_SUM);
							break;
						case AmiDatasourceColumn.TYPE_STRING:
						case AmiDatasourceColumn.TYPE_UUID:
							f.setValue(FIELD_NONE);
							nameField.setDisabled(true);
							formatField.setDisabled(true);
							break;
					}
					updateAggregateField(name, displayName);
				}
				idd++;
			}
		}

	}

	private String toDisplayName(String name) {
		return AmiWebUtils.toPrettyName(name);
	}
	private void addToFormatField(byte type, String name, FormPortletSelectField<Byte> formatField, byte hint) {
		String upperName = name.toUpperCase();
		switch (type) {
			case AmiDatasourceColumn.TYPE_UTC:
			case AmiDatasourceColumn.TYPE_UTCN:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE, "Date");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME, "Time");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC, "Time w/ seconds");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS, "Time w/ millis");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS, "Time w/ micros");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS, "Time w/ nanos");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC, "Date & Time w/ seconds");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS, "Date & Time w/ millis");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS, "Date & Time w/ micros");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS, "Date & Time w/ nanos");
				formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC);
				break;
			case AmiDatasourceColumn.TYPE_LONG:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, "Numeric");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_PRICE, "Price");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE, "Date");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME, "Time");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC, "Time w/ seconds");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS, "Time w/ millis");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS, "Time w/ micros");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS, "Time w/ nanos");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC, "Date & Time w/ seconds");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MILLIS, "Date & Time w/ millis");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_MICROS, "Date & Time w/ micros");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_NANOS, "Date & Time w/ nanos");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				if (upperName.indexOf("TIME") != -1 || upperName.indexOf("DATE") != -1)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_DATE_TIME_SEC);
				else if (upperName.indexOf("PRICE") != -1 || name.indexOf("Px") != -1)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_PRICE);
				break;
			case AmiDatasourceColumn.TYPE_FLOAT:
			case AmiDatasourceColumn.TYPE_DOUBLE:
			case AmiDatasourceColumn.TYPE_BIGDEC:
			case AmiDatasourceColumn.TYPE_COMPLEX:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, "Numeric");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_PRICE, "Price");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_PERCENT, "Percent");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_PROGRESS, "Progress Bar");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				formatField.setCorrelationData("KEEP_PROGRESS");
				if (upperName.indexOf("PRICE") != -1 || name.indexOf("Px") != -1)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_PRICE);
				break;
			case AmiDatasourceColumn.TYPE_INT:
			case AmiDatasourceColumn.TYPE_BIGINT:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, "Numeric");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_PRICE, "Price");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC, "Time w/ seconds");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MILLIS, "Time w/ millis");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_MICROS, "Time w/ micros");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TIME_NANOS, "Time w/ nanos");
				if (upperName.indexOf("TIME") != -1)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_TIME_SEC);
				if (upperName.indexOf("PRICE") != -1 || name.indexOf("Px") != -1)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_PRICE);
				break;
			case AmiDatasourceColumn.TYPE_SHORT:
			case AmiDatasourceColumn.TYPE_BYTE:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC, "Numeric");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				break;
			case AmiDatasourceColumn.TYPE_BOOLEAN:
			case AmiDatasourceColumn.TYPE_STRING:
			case AmiDatasourceColumn.TYPE_UUID:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_HTML, "Html");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_JSON, "Json");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_CHECKBOX, "Checkbox");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_MASKED, "Mask");
				if (hint == AmiDatasourceColumn.HINT_JSON)
					formatField.setValue(AmiWebUtils.CUSTOM_COL_TYPE_JSON);
				break;
			default:
			case AmiDatasourceColumn.TYPE_CHAR:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
				break;
			case AmiDatasourceColumn.TYPE_BINARY:
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_IMAGE, "Image");
				formatField.addOption(AmiWebUtils.CUSTOM_COL_TYPE_TEXT, "Text");
		}

	}
	private byte getDisplayType() {
		Set<Row> selected = this.displayTypePanel.getSelectedTiles();
		if (selected.size() != 1)
			return -1;
		return CH.first(selected).get("id", Caster_Byte.INSTANCE);
	}
	private boolean getColumnSelected(String name) {
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		Object o = this.columnsForm.getField(id, Object.class).getValue();
		return OH.ne(false, o) && OH.ne(FIELD_NONE, o);
	}
	private byte getColumnAggType(String name) {
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		return this.columnsForm.getField(id, Byte.class).getValue();
	}
	private String getColumnName(String name) {
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		return this.columnsForm.getField(id + NAME_ID_SUFFIX, String.class).getValue();
	}
	private void setColumnName(String name, String value) {
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		this.columnsForm.getField(id + NAME_ID_SUFFIX, String.class).setValue(value);
	}
	private Byte getColumnType(String name) {
		final String id = CH.getOrThrow(columnNamesToIds.getInnerKeyValueMap(), name);
		return this.columnsForm.getField(id + TYPE_ID_SUFFIX, Byte.class).getValue();
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == cancelButton) {
			close();
			return;
		}
		if (button == submitButton) {
			if (submit())
				close();
		}
	}
	public boolean submit() {
		BasicCalcTypes paramToSchema = new BasicCalcTypes();
		for (String typeName : typeNames) {
			com.f1.base.CalcTypes schema = service.getWebManagers().getAmiObjectsByType(typeName).getRealtimeObjectsOutputSchema();
			if (schema.isVarsEmpty()) {// no schema
				getManager().showAlert("No schema detected, please check data source");
				return false;
			}
			for (String varname : schema.getVarKeys()) {
				if ("A".equals(varname))
					continue;
				Class<?> widestIgnoreNull = OH.getWidestIgnoreNull(paramToSchema.getType(varname), schema.getType(varname));
				paramToSchema.putType(varname, widestIgnoreNull);
			}
		}
		if (SH.isnt(this.titleField.getValue())) {
			getManager().showAlert("Datasource name required");
			return false;
		}
		Set<String> types = new HashSet<String>();
		String fullAlias = this.aliasField.getValue();
		for (String i : typeNames) {
			if (!i.startsWith(AmiWebManagers.FEED) && !AmiWebUtils.isParentAliasOrSame(fullAlias, AmiWebUtils.getAliasFromAdn(i))) {
				getManager().showAlert("You can not create a panel in the '" + fullAlias + "' layout that references " + i
						+ ". Please change to an owning layout with visiblity to all references.");
				return false;
			}
			types.add(i);
		}
		Portlet removed = getManager().getPortletNoThrow(this.targetPortletId);

		AmiWebPortlet newPortlet;
		if (service.getLayoutFilesManager().getLayoutByFullAlias(fullAlias).isReadonly()) {
			getManager().showAlert("Readonly layout: " + WebHelper.escapeHtml(AmiWebUtils.formatLayoutAlias(fullAlias)));
			return false;
		}
		String panelId = SH.trim(this.panelIdField.getValue());
		if (!AmiWebUtils.isValidPanelId(panelId)) {
			getManager().showAlert("Panel ID is not valid (Must be alpha numeric): " + panelId);
			return false;
		}
		if (SH.is(panelId)) {
			if (service.getPortletByAliasDotPanelId(AmiWebUtils.getFullAlias(fullAlias, panelId)) != null) {
				getManager().showAlert("Panel Id already exists in owning layout");
				return false;
			}
		}

		Byte dtype = getDisplayType();
		switch (dtype) {
			case TYPE_SIMPLE: {
				AmiWebObjectTablePortlet tablePortlet = (AmiWebObjectTablePortlet) desktop.newPortlet(AmiWebObjectTablePortlet.Builder.ID, fullAlias);
				newPortlet = tablePortlet;
				tablePortlet.setDataTypes(types);
				newPortlet.setAmiTitle(titleField.getValue(), false);
				StringBuilder errorSink = new StringBuilder();
				int col = tablePortlet.getTable().getVisibleColumnsCount();
				com.f1.base.CalcTypes varTypes = AmiWebUtils.getAvailableVariables(this.service, tablePortlet);
				Set<String> ids = new HashSet<String>();
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnSelected(var)) {
						Class<?> type = paramToSchema.getType(var);
						String formula = AmiWebUtils.toValidVarname(var);
						String id = AmiUtils.toValidVarName(var);
						ids.add(id = SH.getNextId(id, ids));
						Byte columnType = getColumnType(var);
						int precision = AmiConsts.DEFAULT;
						if (columnType == AmiWebUtils.CUSTOM_COL_TYPE_NUMERIC && !OH.isFloat(type))
							precision = 0;
						AmiWebCustomColumn cc = new AmiWebCustomColumn(tablePortlet, id, getColumnName(var), columnType, precision, formula, null, null, null, null, null, false,
								null, false);
						if (!tablePortlet.addCustomColumn(cc, errorSink, col++, null, varTypes, false)) {
							getManager().showAlert(errorSink.toString());
							tablePortlet.close(); // Need to close after error
							return false;
						}
					}
				}
				break;
			}
			case TYPE_AGG: {
				AmiWebAggregateObjectTablePortlet tablePortlet = (AmiWebAggregateObjectTablePortlet) desktop.newPortlet(AmiWebAggregateObjectTablePortlet.Builder.ID, fullAlias);
				newPortlet = tablePortlet;
				tablePortlet.setDataTypes(types);
				newPortlet.setAmiTitle(titleField.getValue(), false);
				StringBuilder errorSink = new StringBuilder();
				int col = 0;
				com.f1.utils.structs.table.stack.BasicCalcTypes varTypes = new com.f1.utils.structs.table.stack.BasicCalcTypes();
				for (String i : types)
					varTypes.putAll(this.service.getWebManagers().getAmiObjectsByType(i).getRealtimeObjectsOutputSchema());
				Set<String> ids = new HashSet<String>();
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnAggType(var) == FIELD_GROUPBY) {
						String formula = AmiWebUtils.toValidVarname(var);
						String id = AmiUtils.toValidVarName(var);
						ids.add(id = SH.getNextId(id, ids));
						AmiWebCustomColumn cc = new AmiWebCustomColumn(tablePortlet, id, getColumnName(var), getColumnType(var), AmiConsts.DEFAULT, formula, null, null, "\"bold\"",
								null, null, false, null, false);
						tablePortlet.addCustomColumnGroupBy(cc, errorSink, col++, null, varTypes, false);
					}
				}
				col = tablePortlet.getTable().getVisibleColumnsCount();
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnSelected(var)) {
						String formula = toFormula(getColumnAggType(var), AmiWebUtils.toValidVarname(var));
						if (formula == null)
							continue;
						String id = AmiUtils.toValidVarName(var);
						ids.add(id = SH.getNextId(id, ids));
						AmiWebCustomColumn cc = new AmiWebCustomColumn(tablePortlet, id, getColumnName(var), getColumnType(var), AmiConsts.DEFAULT, formula, null, null, null, null,
								null, true, null, false);
						tablePortlet.addCustomColumn(cc, errorSink, col++, null, varTypes, false);
					}
				}
				newPortlet = tablePortlet;
				break;
			}
			case TYPE_TREE: {
				int groupBys = 0;
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnAggType(var) == FIELD_GROUPBY) {
						groupBys++;
					}
				}
				if (groupBys == 0) {
					getManager().showAlert("Trees must have at least one group by");
					return false;
				}

				AmiWebRealtimeTreePortlet treePortlet = (AmiWebRealtimeTreePortlet) desktop.newPortlet(AmiWebRealtimeTreePortlet.Builder.ID, fullAlias);
				treePortlet.setDataTypes(types);
				StringBuilder errorSink = new StringBuilder();
				treePortlet.setAmiTitle(titleField.getValue(), false);
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnAggType(var) != FIELD_GROUPBY) {
						String formula = toFormula(getColumnAggType(var), AmiWebUtils.toValidVarname(var));
						treePortlet.addColumnFormula(-1, null, getColumnName(var), formula, null, null, null, null, AmiWebUtils.CUSTOM_COL_TYPE_TEXT, 0, 0, errorSink, null, "",
								false);
					}
				}

				List<AmiWebTreeGroupBy> formulas = new ArrayList<AmiWebTreeGroupBy>();
				for (String var : CH.sort(columnNamesToIds.getKeys())) {
					if (getColumnAggType(var) == FIELD_GROUPBY) {
						String formula = AmiWebUtils.toValidVarname(var);
						String id = AmiWebUtils.toPrettyVarName(var, "grouping");
						AmiWebTreeGroupBy f = new AmiWebTreeGroupBy(id, treePortlet);
						f.setFormula(false, formula, null, var, null, null, null, null, null, null, null);
						//							getManager().showAlert("Group by is invalid: " + formula + ": " + errorSink);
						//							treePortlet.close();
						//							return false;
						//						}
						formulas.add(f);
					}
				}
				treePortlet.setFormulas(formulas);
				treePortlet.flagRebuildCalcs();
				//				treePortlet.rebuildAmiData();
				newPortlet = treePortlet;
				break;
			}
			case TYPE_TREEMAP: {
				AmiWebTreemapRealtimePortlet treePortlet = (AmiWebTreemapRealtimePortlet) desktop.newPortlet(AmiWebTreemapRealtimePortlet.Builder.ID, fullAlias);
				treePortlet.setDataTypes(types);
				//				com.f1.base.Types varTypes = AmiWebUtils.getAvailableVariables(this.service, treePortlet);
				treePortlet.setAmiTitle(titleField.getValue(), false);
				String grouping = columnsForm.getField("groupingLabel", Byte.class).getValue() == FIELD_NONE ? null
						: AmiWebUtils.toValidVarname(columnsForm.getField("grouping", String.class).getValue());
				String label = columnsForm.getField("labelLabel", Byte.class).getValue() == FIELD_NONE ? null
						: AmiWebUtils.toValidVarname(columnsForm.getField("label", String.class).getValue());
				String size = toFormula(columnsForm.getField("sizeLabel", Byte.class).getValue(),
						AmiWebUtils.toValidVarname(columnsForm.getField("size", String.class).getValue()));
				String heat = toFormula(columnsForm.getField("heatLabel", Byte.class).getValue(),
						AmiWebUtils.toValidVarname(columnsForm.getField("heat", String.class).getValue()));
				StringBuilder errorSink = new StringBuilder();
				if (!treePortlet.setFormulas(grouping, label, size, heat, "", errorSink)) {
					getManager().showAlert(errorSink.toString());
					treePortlet.close();
					return false;
				}
				newPortlet = treePortlet;
				break;
			}
			default:
				throw new RuntimeException("Unknown type: " + dtype);
		}

		if (SH.is(panelId))
			newPortlet.setAdn(AmiWebUtils.getFullAlias(fullAlias, panelId));
		newPortlet.setDefaultPref(newPortlet.getUserPref());
		if (removed == null)
			this.desktop.createNewWindow(newPortlet);
		else
			this.desktop.replacePortlet(removed.getPortletId(), newPortlet);

		return true;
	}

	static private String toFormula(byte type, String var) {
		switch (type) {
			case FIELD_AVG:
				return "sum(" + var + ") / count(" + var + ")";
			case FIELD_COUNT:
				return "count(" + var + ")";
			case FIELD_MIN:
				return "min(" + var + ")";
			case FIELD_MAX:
				return "max(" + var + ")";
			case FIELD_SUM:
				return "sum(" + var + ")";
			case FIELD_NONE:
			case FIELD_GROUPBY:
				return null;
			default:
				throw new RuntimeException("Unknown type");
		}

	}
	@Override
	public void onContextMenu(TilesPortlet tiles, String action) {

	}
	@Override
	public void onTileClicked(TilesPortlet table, Row row) {

	}
	@Override
	public void onSelectedChanged(TilesPortlet tiles) {
		refreshWizard();
	}
	@Override
	public void onVisibleRowsChanged(TilesPortlet tiles) {

	}
	@Override
	public void onDoubleclick(TilesPortlet tilesPortlet, Row tile) {

	}
	@Override
	public void formatTileDescription(TilesPortlet tilesPortlet, Row tile, StringBuilder sink) {
	}
	@Override
	public void formatTile(TilesPortlet tilesPortlet, Row tile, boolean selected, boolean activeTile, StringBuilder sink, StringBuilder styleSink) {
		String cn = tile.get("cn", Caster_String.INSTANCE);
		sink.append("<div class=\"ami_tile_display " + cn + "\">");
		sink.append("</div>");
		sink.append("<div class=\"ami_tile_footer\">");
		sink.append(tile.get("name"));
		sink.append("</div>");
		if (selected)
			styleSink.append("_cna=ami_tile|_cna=ami_tile_selected");
		else
			styleSink.append("_cna=ami_tile");
	}

}
