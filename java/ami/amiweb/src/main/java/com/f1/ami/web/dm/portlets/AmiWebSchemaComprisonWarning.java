package com.f1.ami.web.dm.portlets;

import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;

public class AmiWebSchemaComprisonWarning {
	public static final byte ACTION_DO_NOTHING = 0;
	public static final byte ACTION_APPLY = 1;
	public static final byte ACTION_STOP_WARNING = 2;
	public static final byte ACTION_APPLY_DONT_ASK = 3;
	public static final byte TABLE_ONLY_IN_NUW = 0;
	public static final byte TABLE_ONLY_IN_OLD = 1;
	public static final byte MISMATCH = 2;
	final private byte warningType;
	final private AmiWebDmTableSchema tableSchemaNuw;
	final private AmiWebDmTableSchema tableSchemaExisting;
	final private FormPortletSelectField<Byte> field;

	public AmiWebSchemaComprisonWarning(AmiWebDmTableSchema nuw, AmiWebDmTableSchema existing) {
		this.tableSchemaNuw = nuw;
		this.tableSchemaExisting = existing;
		if (nuw == null)
			warningType = TABLE_ONLY_IN_OLD;
		else if (existing == null)
			warningType = TABLE_ONLY_IN_NUW;
		else
			warningType = MISMATCH;
		this.field = new FormPortletSelectField<Byte>(Byte.class, getDescription()).setTitleIsClickable(true);
		this.field.setCorrelationData(this);
		switch (warningType) {
			case TABLE_ONLY_IN_NUW: {
				field.addOption(ACTION_APPLY, "Add to schema");
				field.addOption(ACTION_DO_NOTHING, "Do nothing");
				break;
			}
			case TABLE_ONLY_IN_OLD: {
				field.addOption(ACTION_APPLY, "Remove from schema");
				field.addOption(ACTION_STOP_WARNING, "Stop warning about changes to this table");
				field.addOption(ACTION_DO_NOTHING, "Do nothing");
				break;
			}
			case MISMATCH: {
				field.addOption(ACTION_APPLY_DONT_ASK, "Update schema, don't ask again");
				field.addOption(ACTION_APPLY, "Update schema");
				field.addOption(ACTION_STOP_WARNING, "Stop warning about changes to this table");
				field.addOption(ACTION_DO_NOTHING, "Do nothing");
				break;
			}
		}
	}

	public byte getWarningType() {
		return warningType;
	}

	public AmiWebDmTableSchema getTableSchemaNuw() {
		return tableSchemaNuw;
	}
	public AmiWebDmTableSchema getTableSchemaExisting() {
		return tableSchemaExisting;
	}

	public String getDescription() {
		switch (warningType) {
			case TABLE_ONLY_IN_NUW:
				return "<img src='rsc/portlet_icon_warning.gif' style='position:relative;top:-1px;'><span style='position:relative;top:-4px;'> &nbsp;<b>" + tableSchemaNuw.getName()
						+ "</b> is not in the schema: </span>";
			case TABLE_ONLY_IN_OLD:
				return "<img src='rsc/portlet_icon_warning.gif' style='position:relative;top:-1px;'><span style='position:relative;top:-4px;'> &nbsp;<b>"
						+ tableSchemaExisting.getName() + "</b> is not in the test response: </span>";
			case MISMATCH:
				return "<img src='rsc/portlet_icon_warning.gif' style='position:relative;top:-1px;'><span style='position:relative;top:-4px;'> &nbsp;<b>" + tableSchemaNuw.getName()
						+ "</b> schema does not match: </span>";
			default:
				return null;
		}
	}

	public byte getType() {
		return warningType;
	}

	protected FormPortletSelectField<Byte> getField() {
		return this.field;
	}

	public byte getActionSelected() {
		return this.field.getValue();
	}

	public String getTableSchemaName() {
		return this.tableSchemaExisting != null ? this.tableSchemaExisting.getName() : this.tableSchemaNuw.getName();
	}

	public void setActionSelected(byte val) {
		this.field.setValue(val);
	}

}
