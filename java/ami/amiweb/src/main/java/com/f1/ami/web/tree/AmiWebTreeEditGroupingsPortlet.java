package com.f1.ami.web.tree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.web.AmiWebMenuUtils;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.Portlet;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.ColorPickerListener;
import com.f1.suite.web.portal.impl.ColorPickerPortlet;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.IndexedList;

public class AmiWebTreeEditGroupingsPortlet extends GridPortlet implements FormPortletListener, FormPortletContextMenuFactory, FormPortletContextMenuListener {

	private static final int MAX_FORMULA_DDD = 30;
	private static final Comparator<FormPortletButtonField> FORMULA_POSITION_COMPARATOR = new Comparator<FormPortletButtonField>() {

		@Override
		public int compare(FormPortletButtonField o1, FormPortletButtonField o2) {
			return OH.compare(getWrapper(o1).position, getWrapper(o2).position);
		}

	};

	private static FormulaWrapper getWrapper(FormPortletField o2) {
		return (FormulaWrapper) o2.getCorrelationData();
	}

	//	public class FormulaWrapper {
	//
	//		private AmiWebTreeGroupBy formula;
	//		public String description, color, bgColor, font, style, orderBy;
	//		public String rowBgColor, rowStyle, rowColor;
	//		public String groupby;
	//		public String amiId;
	//		public String parentGroup;
	//		public int position;
	//
	//		public FormulaWrapper(AmiWebTreeGroupBy formula, int position) {
	//			this.formula = formula;
	//			if (formula != null) {
	//				this.amiId = formula.getAmiId();
	//				this.parentGroup = formula.getParentGroup(false);
	//				this.description = formula.getFormatter().getDescription(false);
	//				this.orderBy = formula.getFormatter().getOrderBy(false);
	//				this.style = formula.getFormatter().getStyle(false);
	//				this.bgColor = formula.getFormatter().getBackgroundColor(false);
	//				this.color = formula.getFormatter().getColor(false);
	//				this.groupby = formula.getGroupby(false);
	//				this.rowStyle = formula.getRowStyle(false);
	//				this.rowBgColor = formula.getRowBackgroundColor(false);
	//				this.rowColor = formula.getRowColor(false);
	//			}
	//			this.position = position;
	//		}
	//
	//		public boolean setFormula(AmiWebTreeGroupBy f, StringBuilder sb) {
	//			return f.setFormula(position == -1, groupby, parentGroup, description, orderBy, style, color, bgColor, rowStyle, rowColor, rowBgColor);
	//		}
	//
	//	}

	private FormPortlet form;
	private AmiWebTreePortlet target;
	private FormPortletButtonField addGroupingButton;
	private FormPortletToggleButtonsField<Boolean> showLeafs;
	//	private FormPortletButton cancel;
	private FormPortletButton submit;
	private List<FormPortletButtonField> formulaFields = new ArrayList<FormPortletButtonField>();
	private FormPortletTextField titleField;
	private FormPortletButtonField leafFormulaButton;

	public AmiWebTreeEditGroupingsPortlet(PortletConfig config, AmiWebTreePortlet target) {
		super(config);
		this.target = target;
		this.form = new FormPortlet(generateConfig());
		this.form.addField(new FormPortletTitleField("Column Header (when displayed as table)"));
		this.form.addField(new FormPortletTitleField("")).setHeight(10);
		this.titleField = this.form.addField(new FormPortletTextField(""));
		this.titleField.setValue(target.getTree().getTreeColumn().getColumnName());
		this.form.addField(new FormPortletTitleField("Groupings"));
		addChild(this.form);
		addGroupingButton = this.form.addField(new FormPortletButtonField("").setValue(WebHelper.escapeHtml("<Add Grouping Formula>")));
		int cnt = target.getFormulasCount();
		AmiWebTreeGroupBy leaf = null;
		for (int i = 0; i < cnt; i++) {
			AmiWebTreeGroupBy formula = target.getFormula(i);
			if (formula.isLeaf())
				leaf = formula;
			else
				addFormula(formula);
		}
		this.form.addField(new FormPortletTitleField("Leaf Level"));
		this.showLeafs = new FormPortletToggleButtonsField<Boolean>(Boolean.class, "Leafs").addOption(true, "Show Leafs").addOption(false, "Hide Leafs");
		leafFormulaButton = new FormPortletButtonField("").setValue("Leaf Formatting...");
		leafFormulaButton.setCorrelationData(leaf == null ? null : new FormulaWrapper(leaf, -1));
		form.getFormPortletStyle().setLabelsWidth(100);
		this.showLeafs.setValue(leaf != null);
		this.form.addField(showLeafs);
		if (showLeafs.getValue())
			this.form.addFieldAfter(this.showLeafs, leafFormulaButton);
		submit = this.form.addButton(new FormPortletButton("Submit"));
		//		cancel = this.form.addButton(new FormPortletButton("Cancel"));
		form.addFormPortletListener(this);
		form.setMenuFactory(this);
		form.addMenuListener(this);
		setSuggestedSize(500, 500);

	}

	public AmiWebTreeEditGroupingsPortlet hideCloseButtons(boolean hide) {
		this.form.clearButtons();
		this.form.addButton(this.submit);
		//		if (!hide)
		//			this.form.addButton(this.cancel);
		return this;
	}
	public AmiWebTreeEditGroupingsPortlet hideButtonsForm(boolean hide) {
		this.form.clearButtons();
		if (!hide) {
			this.form.addButton(this.submit);
			//			this.form.addButton(this.cancel);
		}
		return this;
	}

	private FormPortletButtonField addFormula(AmiWebTreeGroupBy formula) {
		FormPortletButtonField field = this.form.addFieldBefore(this.addGroupingButton, new FormPortletButtonField("Grouping " + (formulaFields.size() + 1) + ": ")
				.setValue(WebHelper.escapeHtml(SH.ddd(formula.getGroupby(false), MAX_FORMULA_DDD))).setCorrelationData(new FormulaWrapper(formula, formulaFields.size())));
		formulaFields.add(field);
		return field;
	}
	private FormPortletButtonField addFormula() {
		FormPortletButtonField field = this.form.addFieldBefore(this.addGroupingButton, new FormPortletButtonField("Grouping " + (formulaFields.size() + 1) + ": ").setValue("")
				.setCorrelationData(new FormulaWrapper(new AmiWebTreeGroupBy(null, this.target), formulaFields.size())));
		formulaFields.add(field);
		return field;
	}
	@Override
	public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
		showOptions(node);
	}

	private OptionsPortlet showOptions(FormPortletField node) {
		if (node == this.addGroupingButton) {
			FormPortletButtonField t = addFormula();
			OptionsPortlet p = new OptionsPortlet(generateConfig(), t, true);
			getManager().showDialog("Grouping Options", p, 600, 500);
			return p;
		} else if (node == this.leafFormulaButton) {
			FormPortletButtonField t = this.leafFormulaButton;
			if (t.getCorrelationData() == null)
				t.setCorrelationData(new FormulaWrapper(new AmiWebTreeGroupBy(null, this.target), -1));

			OptionsPortlet p = new OptionsPortlet(generateConfig(), t, true);
			getManager().showDialog("Leaf Options", p, 600, 500);
			return p;
		} else {
			OptionsPortlet p = new OptionsPortlet(generateConfig(), (FormPortletButtonField) node, false);
			getManager().showDialog("Grouping Options", p, 600, 500);
			return p;
		}
	}
	private void updateFormulaLabels() {
		for (int i = 0; i < this.formulaFields.size(); i++) {
			this.form.removeField(this.formulaFields.get(i));
		}
		Collections.sort(this.formulaFields, FORMULA_POSITION_COMPARATOR);
		for (int i = 0; i < this.formulaFields.size(); i++) {
			FormPortletButtonField field = this.formulaFields.get(i);
			field.setTitle("Grouping " + (i + 1) + ": ");
			getWrapper(field).position = i;
		}
		for (int i = 0; i < this.formulaFields.size(); i++) {
			FormPortletButtonField field = this.form.addFieldBefore(this.addGroupingButton, this.formulaFields.get(i));
		}
	}

	@Override
	public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
		return null;
	}
	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		//		if (button == this.cancel) {
		//			close();
		//		} else 
		if (button == this.submit) {
			boolean okay = applySettings();
			if (okay && getParent() instanceof RootPortlet)
				close();
		}
	}

	public boolean applySettings() {
		if (this.formulaFields.isEmpty()) {
			getManager().showAlert("At least one grouping formula required.");
			return false;
		}
		StringBuilder sb = new StringBuilder();
		List<AmiWebTreeGroupBy> formulas = new ArrayList<AmiWebTreeGroupBy>(this.formulaFields.size());
		for (int i = 0; i < this.formulaFields.size(); i++) {
			FormulaWrapper wrapper = getWrapper(this.formulaFields.get(i));
			AmiWebTreeGroupBy f = new AmiWebTreeGroupBy(wrapper.amiId, target);
			if (!wrapper.setFormula(f, sb)) {
				getManager().showAlert("Error with Grouping " + (i + 1) + ": " + sb);
				return false;
			}
			formulas.add(f);
		}
		if (this.showLeafs.getValue()) {
			FormulaWrapper leafFormatter = getWrapper(this.leafFormulaButton);
			AmiWebTreeGroupBy leaf = null;
			if (leafFormatter == null) {
				leaf = new AmiWebTreeGroupBy(null, target);
				leafFormatter = new FormulaWrapper(leaf, -1);
			} else
				leaf = new AmiWebTreeGroupBy(leafFormatter.amiId, target);
			if (!leafFormatter.setFormula(leaf, sb)) {
				getManager().showAlert("Error with Leaf Formatter: " + sb);
				return false;
			}
			formulas.add(leaf);
		}
		target.setFormulas(formulas);
		target.flagRebuildCalcs();
		target.getTree().getTreeColumn().setColumnName(this.titleField.getValue());
		target.disableFilteringInRecursiveTrees();
		return true;
	}
	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.addGroupingButton) {

		}

		if (field == this.showLeafs) {
			this.form.removeFieldNoThrow(this.leafFormulaButton);
			if (showLeafs.getValue())
				this.form.addFieldAfter(this.showLeafs, leafFormulaButton);
			else
				this.form.removeFieldNoThrow(leafFormulaButton);
			applySettings();
		}

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	public class OptionsPortlet extends FormPortlet implements FormPortletContextMenuFactory, FormPortletContextMenuListener, ConfirmDialogListener, ColorPickerListener {

		private FormPortletButton okayButton;
		private FormPortletButton cancelButton;
		private FormPortletButtonField groupingField;
		private FormPortletTextField descriptionField;
		private FormPortletTextField idField;
		private FormPortletTextField orderByField;
		private FormPortletTextField colorField;
		private FormPortletTextField bgField;
		private FormPortletTextField styleField;
		private FormPortletTextField rowColorField;
		private FormPortletTextField rowBgField;
		private FormPortletTextField rowStyleField;
		private FormulaWrapper formulaWrapper;
		private FormPortletTextField groupbyField;
		private FormPortletTextField parentGroupField;
		private FormPortletSelectField<Integer> positionField;
		private FormPortletButton deleteButton;
		private boolean isAdd;
		private boolean isLeaf;

		public OptionsPortlet(PortletConfig config, FormPortletButtonField node, boolean isAdd) {
			super(config);
			this.isAdd = isAdd;
			this.formulaWrapper = getWrapper(node);
			this.groupingField = node;
			this.isLeaf = formulaWrapper.position == -1;
			if (!isLeaf) {
				addField(new FormPortletTitleField("Grouping"));
				groupbyField = addField(new FormPortletTextField("").setValue(formulaWrapper.groupby).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
				this.positionField = addField(new FormPortletSelectField<Integer>(Integer.class, ""));
				addField(new FormPortletTitleField("Recursive Groupings (optional)"));
				this.parentGroupField = addField(
						new FormPortletTextField("Parent Formula:").setValue(formulaWrapper.parentGroup).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
				for (int i = 0; i < formulaFields.size(); i++) {
					if (i == 0)
						this.positionField.addOption(i, " Position #1 (top level)");
					else if (i == formulaFields.size() - 1)
						this.positionField.addOption(i, " Position #" + (i + 1) + " (inner-most level)");
					else
						this.positionField.addOption(i, "Position #" + (i + 1));
				}
				this.positionField.setValue(formulaWrapper.position);
			}
			addField(new FormPortletTitleField("Text (optional)"));
			descriptionField = addField(new FormPortletTextField("Display:").setValue(formulaWrapper.description).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			idField = addField(new FormPortletTextField("Grouping Id:").setValue(formulaWrapper.amiId).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			orderByField = addField(new FormPortletTextField("Sorting:").setValue(formulaWrapper.orderBy).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			addField(new FormPortletTitleField("Cell Styling (optional)"));
			styleField = addField(new FormPortletTextField("Style:").setValue(formulaWrapper.style).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			colorField = addField(new FormPortletTextField("Foreground Color:").setValue(formulaWrapper.color).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			bgField = addField(new FormPortletTextField("Background Color:").setValue(formulaWrapper.bgColor).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));

			addField(new FormPortletTitleField("Row Styling (optional)"));
			rowStyleField = addField(new FormPortletTextField("Style:").setValue(formulaWrapper.rowStyle).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			rowColorField = addField(
					new FormPortletTextField("Foreground Color:").setValue(formulaWrapper.rowColor).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));
			rowBgField = addField(
					new FormPortletTextField("Background Color:").setValue(formulaWrapper.rowBgColor).setWidth(FormPortletTextField.WIDTH_STRETCH).setHasButton(true));

			this.setMenuFactory(this);
			this.addMenuListener(this);
			okayButton = addButton(new FormPortletButton("OK"));
			cancelButton = addButton(new FormPortletButton("Cancel"));
			deleteButton = addButton(new FormPortletButton("Delete This Groupby"));
			deleteButton.setEnabled(formulaFields.size() > 1);

		}
		@Override
		protected void onUserPressedButton(FormPortletButton formPortletButton) {
			super.onUserPressedButton(formPortletButton);
			if (formPortletButton == okayButton) {
				StringBuilder errorSink = new StringBuilder();
				String amiId = idField.getValue();
				String description = this.descriptionField.getValue();
				String groupby = isLeaf ? description : this.groupbyField.getValue();
				if (SH.isnt(amiId)) {
					amiId = AmiWebUtils.toPrettyVarName(groupby, "grouping");
					Set<String> existing = new HashSet<String>();
					for (int i = 0; i < formulaFields.size(); i++) {
						FormulaWrapper wrapper = getWrapper(formulaFields.get(i));
						if (wrapper == this.formulaWrapper)
							continue;
						existing.add(wrapper.amiId);

					}
					amiId = SH.getNextId(amiId, existing);
				}
				if (OH.ne(amiId, formulaWrapper.amiId) && target.getGroupBy(amiId) != null) {
					getManager().showAlert("Group Id already exists.");
					return;
				}
				if (!isLeaf) {
					IndexedList<String, AmiWebTreeGroupBy> gbf = target.getGroupbyFormulas();
					int oldPosition = this.formulaWrapper.position;
					int size = gbf.getSize();
					if (size > 0) {
						if (gbf.getAt(size - 1).isLeaf())
							size--;
						if (this.positionField.getValue() < size - 1 && SH.is(this.parentGroupField.getValue())) {
							getManager().showAlert("Only inner-most level can have recursion.  You must either remove the Parent Formula or Move the Position to Innter Most");
							return;
						} else if (this.positionField.getValue() == (isAdd ? size : size - 1)) {
							for (int i = 0; i < gbf.getSize(); i++) {
								if (i != oldPosition && gbf.getAt(i).getIsRecursive()) {
									getManager().showAlert("Only inner-most level can have recursion and the '" + gbf.getAt(i).getGroupby(false)
											+ "' group-by has recursion so it must remain as the inner-most grouping");
									return;
								}
							}
						}

					}
				}
				if (!this.formulaWrapper.getFormula().setFormula(isLeaf, groupby, isLeaf ? null : this.parentGroupField.getValue(), description, this.orderByField.getValue(),
						this.styleField.getValue(), this.colorField.getValue(), this.bgField.getValue(), this.rowStyleField.getValue(), this.rowColorField.getValue(),
						this.rowBgField.getValue())) {
					getManager().showAlert(errorSink.toString());
					return;
				}
				this.formulaWrapper.getFormula().flagRebuildCalcs();
				this.formulaWrapper.getFormula().onRebuildCalcs();
				formulaWrapper.amiId = amiId;
				if (!isLeaf) {
					int nuwPosition = this.positionField.getValue();
					int oldPosition = this.formulaWrapper.position;
					this.formulaWrapper.position = nuwPosition;
					for (FormPortletButtonField field : formulaFields) {
						FormulaWrapper wrapper = getWrapper(field);
						if (wrapper == this.formulaWrapper)
							continue;
						if (OH.isBetween(wrapper.position, oldPosition, nuwPosition))
							wrapper.position--;
						else if (OH.isBetween(wrapper.position, nuwPosition, oldPosition))
							wrapper.position++;
					}
					this.formulaWrapper.groupby = groupby;
					this.formulaWrapper.parentGroup = this.parentGroupField.getValue();
					this.groupingField.setValue(WebHelper.escapeHtml(SH.ddd(groupby, MAX_FORMULA_DDD)));
				}
				this.formulaWrapper.description = description;
				this.formulaWrapper.style = this.styleField.getValue();
				this.formulaWrapper.color = this.colorField.getValue();
				this.formulaWrapper.bgColor = this.bgField.getValue();
				this.formulaWrapper.orderBy = this.orderByField.getValue();
				this.formulaWrapper.rowStyle = this.rowStyleField.getValue();
				this.formulaWrapper.rowColor = this.rowColorField.getValue();
				this.formulaWrapper.rowBgColor = this.rowBgField.getValue();
				close();
			} else if (formPortletButton == cancelButton) {
				if (isAdd) {
					form.removeField(groupingField);
					formulaFields.remove(groupingField);
				}
				close();
			} else if (formPortletButton == deleteButton) {
				getManager().showDialog(this.groupingField.getTitle() + " Grouping",
						new ConfirmDialogPortlet(generateConfig(), "Remove Grouping?", ConfirmDialogPortlet.TYPE_OK_CANCEL, this).setCallback("DELETE")
								.setCorrelationData(this.groupingField));
			}
			updateFormulaLabels();
			applySettings();
		}
		@Override
		public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
			BasicWebMenu r = new BasicWebMenu();
			WebMenu vars = (BasicWebMenu) AmiWebMenuUtils.createVariablesMenu(r, false, target);
			if (field != this.groupbyField) {
				vars.add(new BasicWebMenuDivider());
				addSpecialVars(vars);
			}
			r.add(vars);

			AmiWebMenuUtils.createOperatorsMenu(r, target.getService(), target.getAmiLayoutFullAlias());
			//			if (field != this.groupbyField)
			//				AmiWebMenuUtils.createAggOperatorsMenu(r, false);
			FormulaWrapper formula = getWrapper(field);
			if (field == this.bgField || field == this.colorField || field == this.rowBgField || field == this.rowColorField) {
				AmiWebMenuUtils.createColorsMenu(r, target.getStylePeer());
			}
			if (field == this.styleField || field == rowStyleField) {
				AmiWebMenuUtils.createFormatsMenu(r, AmiWebUtils.getService(this.getManager()));
			}
			return r;
		}
		@Override
		public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
			AmiWebMenuUtils.processContextMenuAction(target.getService(), action, node);
		}
		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			if ("DELETE".equals(source.getCallback()) && ConfirmDialogPortlet.ID_YES.equals(id)) {
				FormPortletField<?> tf = (FormPortletField<?>) source.getCorrelationData();
				form.removeField(tf);
				formulaFields.remove(tf);
				updateFormulaLabels();
				close();
			}
			return true;
		}
		@Override
		public void onColorChanged(ColorPickerPortlet target, String oldColor, String nuwColor) {

		}
		@Override
		public void onOkayPressed(ColorPickerPortlet target) {
			FormPortletTextField field = (FormPortletTextField) target.getCorrelationData();
			String color = "\"" + target.getColor() + "\"";
			field.insertAtCursor(color);
			target.close();
		}
		@Override
		public void onCancelPressed(ColorPickerPortlet target) {
		}

	}

	public static void addSpecialVars(WebMenu vars) {
		vars.add(new BasicWebMenuLink("__CHECKED (0=not checked, 1=partially,2=checked)", true, "var___CHECKED"));
		vars.add(new BasicWebMenuLink("__EXPANDED (true=expanded,false=contracted)", true, "var___EXPANDED"));
		vars.add(new BasicWebMenuLink("__SIZE (# of direct children)", true, "var___SIZE"));
		vars.add(new BasicWebMenuLink("__RSIZE (# of children, recursively)", true, "var___RSIZE"));
		vars.add(new BasicWebMenuLink("__LEAVES (# of leaves)", true, "var___LEAVES"));
		vars.add(new BasicWebMenuLink("__DEPTH", true, "var___DEPTH"));

	}

	public Portlet showEditor(AmiWebTreeGroupBy groupBy) {
		for (FormPortletButtonField i : this.formulaFields) {
			FormulaWrapper fw = (FormulaWrapper) i.getCorrelationData();
			if (fw.getFormula() == groupBy) {
				return showOptions(i);
			}
		}
		FormulaWrapper fw = (FormulaWrapper) leafFormulaButton.getCorrelationData();
		if (fw != null && fw.getFormula() == groupBy) {
			return showOptions(leafFormulaButton);
		}
		return null;
	}
}
