package com.f1.ami.web.dm.portlets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebDatasourceWrapper;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmLink;
import com.f1.ami.web.dm.AmiWebDmLinkImpl;
import com.f1.ami.web.dm.AmiWebDmLinkWhereClause;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletMetrics;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.utils.SH;

public class AmiWebDmWhereFieldsForm extends FormPortlet implements FormPortletListener {
	public static final String TYPE_VARNAME = "VARNAME";
	public static final String TYPE_WHERECLAUSE = "WHERECLAUSE";
	private static final String DEFAULT_VARNAME = "WHERE";
	private static final int DEFAULT_ROWHEIGHT = 25;
	private static final int DEFAULT_LEFTPOS = 26; //164
	private static final int DEFAULT_TITLELEFTPOS = 164; //164
	private static final int DEFAULT_SPACING = 10;
	private static final int DEFAULT_TITLEHEIGHT = 27;
	private static final int DEFAULT_TITLEWIDTH = 200;
	private static final int DEFAULT_TOPPOS = DEFAULT_SPACING + DEFAULT_TITLEHEIGHT;
	private static final int DEFAULT_VARNAMEWIDTH = 110; //60

	private final AmiWebDm targetDm;
	private final AmiWebDatasourceWrapper targetDs;
	private List<AmiWebDmLinkWhereClause> whereClauseObjectsList;
	private List<FormPortletTextField> varNameFields;
	private List<FormPortletTextField> whereClauseFields;
	private Set<String> usedWhereVarNames;
	private int size;
	private boolean enableAdditionalWhereClauses;
	private FormPortletTitleField relationshipTitleField;
	private AmiWebDmLinkWhereFieldListener listener;
	private AmiWebDmLink link;

	static int[] KEYS = new int[('Z' - 'A' + 1) + 3 + ('9' - '0' + 1)];
	static {
		int pos = 0;
		KEYS[pos++] = 8;
		KEYS[pos++] = '_';
		KEYS[pos++] = 34;
		for (int i = 'A'; i <= 'Z'; i++)
			KEYS[pos++] = i;
		for (int i = '0'; i <= '9'; i++)
			KEYS[pos++] = i;
	}

	public AmiWebDmWhereFieldsForm(AmiWebDmLinkWhereFieldListener listener, AmiWebDm targetDm, AmiWebDatasourceWrapper targetDs, PortletConfig config, AmiWebDmLink link) {
		super(config);
		this.link = link;

		this.setSize(0);
		this.targetDm = targetDm;
		this.targetDs = targetDs;

		this.setEnableAdditionalWhereClauses(true);

		this.usedWhereVarNames = new HashSet<String>();
		this.whereClauseObjectsList = new ArrayList<AmiWebDmLinkWhereClause>();
		this.varNameFields = new ArrayList<FormPortletTextField>();
		this.whereClauseFields = new ArrayList<FormPortletTextField>();
		this.relationshipTitleField = new FormPortletTitleField("Relationships");

		this.addField(relationshipTitleField);
		relationshipTitleField.setWidth(DEFAULT_TITLEWIDTH);
		relationshipTitleField.setHeight(DEFAULT_TITLEHEIGHT);
		relationshipTitleField.setLeftPosPx(DEFAULT_TITLELEFTPOS);
		relationshipTitleField.setTopPosPx(DEFAULT_SPACING);

		addListeners: {
			this.addFormPortletListener(this);
			this.setListener(listener);
		}

	}
	public void initWhereFieldsFromDmLink() {
		boolean addEmptyWhereField = false;
		if (link.getLinkUid() != null) {
			AmiWebDmLink dummy = new AmiWebDmLinkImpl(link.getService().getDmManager());
			Set<String> varnames = link.getWhereClauseVarNames();
			if (varnames.size() != 0) {
				for (String varname : varnames) {
					AmiWebDmLinkWhereClause wco = link.getWhereClauseO(varname);
					this.addWhereField(wco.copy(dummy));
				}
			} else {
				addEmptyWhereField = true;
			}
			dummy.close();
		} else {
			addEmptyWhereField = true;
		}
		if (addEmptyWhereField) {
			AmiWebDmLinkWhereClause whereClauseObject = getWhereClauseObject();
			this.addWhereField(whereClauseObject);
		}
	}
	public void addWhereFieldsToDmLink(AmiWebDmLink link, StringBuilder errorSink) {
		for (int i = 0; i < size; i++) {
			AmiWebDmLinkWhereClause w = this.getWhereClauseObjectAtPos(i);
			if (SH.equals(w.getWhereClause().getFormula(false), ""))
				continue;
			if (SH.equals(w.getVarName(), "")) {
				errorSink.append("Where Clause requires a name");
			}

			link.addWhereClause(w.copy(link));
		}
	}

	public void addWhereField(AmiWebDmLinkWhereClause whereClauseObject) {
		addWhereFieldAtPos(whereClauseObject, getSize());
	}

	public void addWhereFieldAtPos(AmiWebDmLinkWhereClause whereClauseObject, int pos) {
		FormPortletTextField varNameField = new FormPortletTextField("${").setName(TYPE_VARNAME).setValue("");
		FormPortletTextField whereClauseField = new FormPortletTextField(" } =").setName(TYPE_WHERECLAUSE).setValue("").setMaxChars(2048);

		varNameFields.add(pos, varNameField);
		whereClauseFields.add(pos, whereClauseField);

		//TODO:
		//Order of fields being added changes if you can select a field.
		this.addField(whereClauseField);
		this.addField(varNameField);

		whereClauseField.setHasButton(true);

		this.whereClauseObjectsList.add(pos, whereClauseObject);
		this.addVarname(whereClauseObject, pos);
		this.addWhereClause(whereClauseObject, pos);
		this.setSize(this.getSize() + 1);
		this.repositionFromPosition(0);
		this.listener.onWhereFieldAdded(this, pos);
	}

	public int indexOfVarname(String varname) {
		for (int i = 0; i < whereClauseObjectsList.size(); i++) {
			if (whereClauseObjectsList.get(i).getVarName().equals(varname))
				return i;
		}
		return -1;
	}

	public AmiWebDmLinkWhereClause getWhereClauseObject() {
		if (this.targetDm != null) {
			return AmiWebDmUtils.getUnderlyingDatasourceWhereClauseObject(getManager(), this.targetDm, link);
		} else if (this.targetDs != null) {
			return AmiWebDmUtils.getDatasourceWhereClauseSyntax(getManager(), this.targetDs, link);
		} else
			return new AmiWebDmLinkWhereClause(link, (String) "WHERE", null);
	}

	public void createWhereFieldsContextMenu(FormPortletField field, WebMenu sink) {
		int pos = this.indexOfWhereField(((FormPortletTextField) field));
		boolean enable_del = this.getSize() > 1;
		if (pos >= 0 && this.enableAdditionalWhereClauses) {
			sink.add(new BasicWebMenuLink("Add Clause ...", true, "add_" + pos));
			sink.add(new BasicWebMenuLink("Remove Clause ...", enable_del, "remove_" + pos));
		}
		if (pos >= 0) {
			sink.add(new BasicWebMenuLink("Advanced ...", true, "adv_" + pos));
		}
	}

	public void onWhereFieldsFormContextMenu(String action) {
		if (action.startsWith("add_")) {
			int position = SH.parseInt(SH.stripPrefix(action, "add_", true));
			AmiWebDmLinkWhereClause whereClauseObject = this.getWhereClauseObject();
			this.addWhereFieldAtPos(whereClauseObject, position + 1);
		} else if (action.startsWith("remove_")) {
			int position = SH.parseInt(SH.stripPrefix(action, "remove_", true));
			this.removeWhereFieldAtPos(position);
		} else if (action.startsWith("adv_")) {
			int position = SH.parseInt(SH.stripPrefix(action, "adv_", true));
			AmiWebDmLinkWhereClause whereClauseObject = this.getWhereClauseObjectAtPos(position);
			getManager().showDialog("Advanced", new AmiWebDmWhereFieldAdvanced(whereClauseObject, generateConfig()), 500, 300);
		}

	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field.getName().equals(TYPE_VARNAME)) {
			int pos = varNameFields.indexOf(field);
			if (pos == -1)
				return;
			String oldVarname = this.whereClauseObjectsList.get(pos).getVarName();
			String newVarname = ((FormPortletTextField) field).getValue();

			//If the requested name differs from what has been set fire off to the field to update it's value
			this.setVarnameNoFire(newVarname, pos);
			if (!newVarname.equals(this.whereClauseObjectsList.get(pos).getVarName())) {
				this.varNameFields.get(pos).setValue(this.whereClauseObjectsList.get(pos).getVarName());
			}
		} else if (field.getName().equals(TYPE_WHERECLAUSE)) {
			int pos = whereClauseFields.indexOf(field);
			if (pos == -1)
				return;
			String oldWhereClause = this.whereClauseObjectsList.get(pos).getWhereClause().getFormula(false);
			this.setWhereClauseNoFire(((FormPortletTextField) field).getValue(), pos);
		}
	}

	public boolean isEnableAdditionalWhereClauses() {
		return enableAdditionalWhereClauses;
	}
	public void setEnableAdditionalWhereClauses(boolean enableAdditionalWhereClauses) {
		this.enableAdditionalWhereClauses = enableAdditionalWhereClauses;
	}
	@Override
	public int getSuggestedHeight(PortletMetrics pm) {
		return DEFAULT_SPACING * 1 + size * (DEFAULT_ROWHEIGHT + DEFAULT_SPACING) + DEFAULT_TITLEHEIGHT;
	}

	@Override
	public void close() {
		removeListeners: {
			this.removeFormPortletListener(this);
			this.removeListener(listener);
		}

		super.close();
	}

	private int getSize() {
		return size;
	}

	private void setSize(int size) {
		this.size = size;
	}

	private AmiWebDmLinkWhereClause getWhereClauseObjectAtPos(int position) {
		return whereClauseObjectsList.get(position);
	}

	private void removeWhereFieldAtPos(int position) {
		AmiWebDmLinkWhereClause whereClauseObject = this.whereClauseObjectsList.remove(position);
		String varname = whereClauseObject.getVarName();
		if (!SH.equals(varname, "")) {
			usedWhereVarNames.remove(varname);
		}

		FormPortletTextField varNameField = this.varNameFields.remove(position);
		FormPortletTextField whereClauseField = this.whereClauseFields.remove(position);

		this.setSize(getSize() - 1);
		this.repositionFromPosition(position);

		this.removeField(varNameField);
		this.removeField(whereClauseField);
		this.listener.onWhereFieldRemoved(this, varname);
	}

	private void repositionAtPosition(int position) {
		FormPortletTextField varNameField = this.varNameFields.get(position);
		FormPortletTextField whereClauseField = this.whereClauseFields.get(position);

		varNameField.setWidthPx(DEFAULT_VARNAMEWIDTH);
		varNameField.setHeightPx(DEFAULT_ROWHEIGHT);
		varNameField.setLeftPosPx(DEFAULT_LEFTPOS);
		varNameField.setTopPosPx(DEFAULT_TOPPOS + (DEFAULT_ROWHEIGHT + DEFAULT_SPACING) * position);

		whereClauseField.setLeftPosPx(DEFAULT_LEFTPOS + DEFAULT_VARNAMEWIDTH + 28);
		whereClauseField.setTopPosPx(DEFAULT_TOPPOS + (DEFAULT_ROWHEIGHT + DEFAULT_SPACING) * position);
		whereClauseField.setRightPosPx(20);
		whereClauseField.setHeightPx(DEFAULT_ROWHEIGHT);
	}

	private void repositionFromPosition(int position) {
		for (int i = position; i < getSize(); i++) {
			repositionAtPosition(i);
		}
	}

	private int indexOfWhereField(FormPortletTextField field) {
		return this.whereClauseFields.indexOf(field);
	}

	private void addVarname(AmiWebDmLinkWhereClause whereClauseObject, int pos) {
		String name = whereClauseObject.getVarName();
		whereClauseObject.setVarName("");
		this.addVarnameNoFire(name, pos);
		this.varNameFields.get(pos).setValue(this.whereClauseObjectsList.get(pos).getVarName());
	}

	private void addVarnameNoFire(String varname, int pos) {
		String prevVarName = this.whereClauseObjectsList.get(pos).getVarName();
		if (prevVarName != null) {
			if (prevVarName.equals(varname))
				return;
			if (usedWhereVarNames.contains(prevVarName)) {
				usedWhereVarNames.remove(prevVarName);
			}
		}
		String name;
		if (varname == null) {
			name = SH.getNextId(DEFAULT_VARNAME, usedWhereVarNames);
		} else {
			name = varname.equals("") ? "" : SH.getNextId(varname, usedWhereVarNames);
		}
		this.whereClauseObjectsList.get(pos).setVarName(name);
		if (!name.equals(""))
			usedWhereVarNames.add(name);
	}
	private void addWhereClause(AmiWebDmLinkWhereClause whereClauseObject, int pos) {
		String whereClause = whereClauseObject.getWhereClause().getFormula(false);
		whereClauseObject.getWhereClause().setFormula("", false);
		this.addWhereClauseNoFire(whereClause, pos);
		this.whereClauseFields.get(pos).setValue(this.whereClauseObjectsList.get(pos).getWhereClause().getFormula(false));
	}

	private void addWhereClauseNoFire(String whereClause, int pos) {
		String prevWhereClause = this.whereClauseObjectsList.get(pos).getVarName();
		if (prevWhereClause != null && prevWhereClause.equals(whereClause))
			return;
		if (whereClause == null) {
			whereClause = "";
		}
		this.whereClauseObjectsList.get(pos).getWhereClause().setFormula(whereClause, false);
	}

	private void setVarnameNoFire(String varname, int pos) {
		varname = SH.trim(varname);
		String prevVarName = whereClauseObjectsList.get(pos).getVarName();
		if (prevVarName != null) {
			if (prevVarName.equals(varname))
				return;
			if (usedWhereVarNames.contains(prevVarName)) {
				usedWhereVarNames.remove(prevVarName);
			}
		}
		String name;
		if (varname == null) {
			name = SH.getNextId(DEFAULT_VARNAME, usedWhereVarNames);
		} else {
			name = varname.equals("") ? "" : SH.getNextId(varname, usedWhereVarNames);
		}
		whereClauseObjectsList.get(pos).setVarName(name);
		if (!name.equals(""))
			usedWhereVarNames.add(name);
	}

	public void setWhereClauseNoFire(String whereClause, int pos) {
		String prevWhereClause = whereClauseObjectsList.get(pos).getWhereClause().getFormula(false);
		if (prevWhereClause != null && prevWhereClause.equals(whereClause))
			return;
		if (whereClause == null) {
			whereClause = "";
		}
		whereClauseObjectsList.get(pos).getWhereClause().setFormula(whereClause, false);
	}
	public void setWhereClause(String whereClause, int pos) {
		setWhereClauseNoFire(whereClause, pos);
		this.whereClauseFields.get(pos).setValue(whereClause);
	}

	//There is only one listener
	protected void setListener(AmiWebDmLinkWhereFieldListener listener) {
		if (this.listener != null)
			return;
		this.listener = listener;
	}

	//There is only one listener
	protected void removeListener(AmiWebDmLinkWhereFieldListener listener) {
		if (this.listener == null)
			return;
		this.listener = null;
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
	}
	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

}
