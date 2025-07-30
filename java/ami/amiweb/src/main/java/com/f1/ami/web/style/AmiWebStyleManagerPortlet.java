package com.f1.ami.web.style;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.portlets.AmiWebHeaderPortlet;
import com.f1.ami.web.AmiWebDesktopPortlet;
import com.f1.ami.web.AmiWebEditStylePortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebSpecialPortlet;
import com.f1.ami.web.AmiWebStyleCssPortlet;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.FormExportPortlet;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.HtmlPortlet;
import com.f1.suite.web.portal.impl.RootPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuFactory;
import com.f1.suite.web.portal.impl.form.FormPortletContextMenuListener;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletSelectField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.portal.impl.form.FormPortletTextField;
import com.f1.suite.web.portal.impl.form.FormPortletTitleField;
import com.f1.suite.web.portal.impl.form.FormPortletToggleButtonsField;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;

public class AmiWebStyleManagerPortlet extends GridPortlet implements FormPortletListener, AmiWebStyleManagerListener, WebTreeContextMenuListener, WebTreeContextMenuFactory,
		ConfirmDialogListener, AmiWebSpecialPortlet, Comparator<WebTreeNode> {

	final private AmiWebService service;
	final private AmiWebStyleManager styleManager;
	private AmiWebEditStylePortlet editPortlet;
	final private FormPortletButton addButton;
	final private FastTreePortlet styleTreeGrid;
	final private FastTreePortlet substyleTreeGrid;
	final private AmiWebHeaderPortlet header;
	final private HtmlPortlet blankEditPortlet;
	final private InnerPortlet editPanel;
	final private AddStyleFormPortlet styleFormPortlet;
	final private Map<String, AmiWebEditStylePortlet> editors = new HashMap<String, AmiWebEditStylePortlet>();
	final private FormPortletButton importButton;
	//	final private WebTreeNode currentLayoutMode;
	final private Map<String, WebTreeNode> styleId2Nodes = new HashMap<String, WebTreeNode>();
	final private DividerPortlet vDivider;
	final private DividerPortlet hDivider;
	final private GridPortlet gridPortlet;
	final private AmiWebStyleCssPortlet cssPortlet;
	final private AmiWebStyleVarPortlet varPortlet;
	final private WebTreeNode substyleRootNode;
	private WebTreeNode substyleCssNode;
	private WebTreeNode substyleVarNode;
	private AmiWebStyle currentStyle;
	private WebTreeNode substyleStylesNode;

	public AmiWebStyleManagerPortlet(PortletConfig config) {
		super(config);
		this.vDivider = new DividerPortlet(generateConfig(), true);
		this.hDivider = new DividerPortlet(generateConfig(), false);
		this.vDivider.setOffsetFromTopPx(300);
		this.hDivider.setOffsetFromTopPx(300);
		this.service = AmiWebUtils.getService(getManager());
		this.cssPortlet = new AmiWebStyleCssPortlet(generateConfig(), service.getDesktop());
		getManager().onPortletAdded(cssPortlet);
		this.varPortlet = new AmiWebStyleVarPortlet(generateConfig(), service.getDesktop());
		getManager().onPortletAdded(varPortlet);
		this.styleManager = this.service.getStyleManager();
		this.styleManager.addListener(this);
		this.styleTreeGrid = new FastTreePortlet(generateConfig());
		this.styleTreeGrid.getTree().setRootLevelVisible(false);
		this.styleTreeGrid.getTree().setHeaderOptions(FastTreePortlet.OPTION_HEADER_BAR_HIDDEN);
		this.styleTreeGrid.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, true);
		this.styleTreeGrid.getTree().setSelectionMode(FastWebTree.SELECTION_MODE_TOGGLE);
		this.styleTreeGrid.getTree().setContextMenuFactory(this);
		this.styleTreeGrid.getTree().setAutoExpandUntilMultipleNodes(false);
		this.styleTreeGrid.getTreeManager().setComparator(this);
		this.styleTreeGrid.addOption(FastTreePortlet.OPTION_TREAT_NAME_CLICK_AS_SELECT, true);
		this.substyleTreeGrid = new FastTreePortlet(generateConfig());
		this.substyleTreeGrid.getTree().setRootLevelVisible(false);
		this.substyleTreeGrid.getTree().setHeaderOptions(FastTreePortlet.OPTION_HEADER_BAR_HIDDEN);
		this.substyleTreeGrid.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, true);
		this.substyleTreeGrid.getTree().setSelectionMode(FastWebTree.SELECTION_MODE_TOGGLE);
		this.substyleTreeGrid.getTree().setContextMenuFactory(this);
		this.substyleTreeGrid.getTree().setAutoExpandUntilMultipleNodes(false);
		this.substyleTreeGrid.getTreeManager().setComparator(this);
		this.substyleTreeGrid.addOption(FastTreePortlet.OPTION_TREAT_NAME_CLICK_AS_SELECT, true);
		//		this.currentLayoutMode = this.styleTreeGrid.createNode("Layout Styles", this.styleTreeGrid.getTreeManager().getRoot(), false).setIsSelectable(true)
		//				.setIconCssStyle("_bgi=url('rsc/ami/layout.png')").setCssClass("pointer").setIsExpandable(true).setIsExpanded(true);
		RootPortlet root = (RootPortlet) this.service.getPortletManager().getRoot();
		int height = root.getHeight() - 50;
		int scaledHeight = MH.min(AmiWebDesktopPortlet.MAX_HEIGHT, (int) (height * 0.8));
		AmiWebStyle top = this.styleManager.getStyleById(AmiWebStyleManager.FACTORY_DEFAULT_ID);
		addStyles(top, this.styleTreeGrid.getRoot());
		for (AmiWebStyleType i : this.styleManager.getTypes()) {
			AmiWebEditStylePortlet editor = new AmiWebEditStylePortlet(null, generateConfig(), i.getName());
			editor.hideButtonsForm(true);
			this.editors.put(editor.getStyleType(), editor);
			getManager().onPortletAdded(editor);
		}

		setCssStyle("_bg=#4c4c4c");
		this.addButton = new FormPortletButton("Add Style");
		this.importButton = new FormPortletButton("Import Style");
		this.gridPortlet = new GridPortlet(generateConfig());
		this.blankEditPortlet = new HtmlPortlet(generateConfig());
		this.blankEditPortlet.setCssStyle("_bg=#cccccc");
		getManager().onPortletAdded(this.blankEditPortlet);
		this.styleFormPortlet = new AddStyleFormPortlet(this.styleManager, generateConfig(), null, false);
		getManager().onPortletAdded(this.styleFormPortlet);

		this.header = new AmiWebHeaderPortlet(generateConfig());
		this.header.setInformationHeaderHeight(100);
		this.header.updateBlurbPortletLayout("AMI Styler",
				"Left click on the items in the tree to view and edit styles. Right click for additional options. <BR>Note: Predefined Styles cannot be edited. ");
		this.header.setShowSearch(false);
		this.header.setShowLegend(false);

		this.editPanel = this.gridPortlet.addChild(this.blankEditPortlet, 0, 0, 1, 1);
		this.addChild(this.header, 0, 0);
		this.addChild(this.vDivider, 0, 1);
		this.vDivider.addChild(this.hDivider);
		this.vDivider.addChild(this.gridPortlet);
		this.hDivider.addChild(this.styleTreeGrid);
		this.hDivider.addChild(this.substyleTreeGrid);
		//		this.addChild(this.styleTreeGrid, 0, 1, 1, 1);
		//		this.editPanel = addChild(blankEditPortlet, 1, 1, 1, 1);
		// 1000 comes from AmiWebDesktopPortlet 740, AMI styler

		FormPortlet bar = header.getBarFormPortlet();
		bar.addButton(this.addButton);
		bar.addButton(this.importButton);
		header.updateBarPortletLayout(addButton.getHtmlLayoutSignature() + importButton.getHtmlLayoutSignature());
		addButton.setCssStyle("_cn=ami_styler_addstyle");
		importButton.setCssStyle("_cn=ami_styler_importstyle");
		setSuggestedSize((int) (root.getWidth() * 0.5), scaledHeight);
		bar.addFormPortletListener(this);
		WebTreeNode target = getNodeForStyle(AmiWebStyleManager.LAYOUT_DEFAULT_ID);
		if (target != null) {
			target.setSelected(true);
			for (WebTreeNode i = target; i != null; i = i.getParent())
				i.setIsExpanded(true);
		}
		this.substyleTreeGrid.clear();
		this.substyleTreeGrid.getTree().isKeepSorting();
		this.currentStyle = top;
		this.substyleRootNode = this.substyleTreeGrid.getTreeManager().createNode(top.getLabel(), this.substyleTreeGrid.getRoot(), false).setIsSelectable(true)
				.setIconCssStyle("_bgi=url('rsc/ami/template.png')");
		this.substyleCssNode = this.substyleTreeGrid.getTreeManager().createNode("Css", this.substyleRootNode, false).setIsSelectable(true).setCssClass("pointer")
				.setIconCssStyle("_bgi=url('rsc/ami/cust-css.svg')");
		this.substyleVarNode = this.substyleTreeGrid.getTreeManager().createNode("Variables", this.substyleRootNode, false).setIsSelectable(true).setCssClass("pointer")
				.setIconCssStyle("_bgi=url('rsc/ami/cust-css.svg')");
		this.substyleStylesNode = this.substyleTreeGrid.getTreeManager().createNode("Styles", this.substyleRootNode, false).setIsSelectable(true).setCssClass("pointer")
				.setIconCssStyle("_bgi=url('rsc/ami/layout.png')");

		this.substyleRootNode.setIsExpanded(true);
		setCurrentStyle((AmiWebStyle) target.getData());
		this.editPanel.setPortlet(this.blankEditPortlet);
		this.substyleTreeGrid.getTree().addMenuContextListener(this);
		this.styleTreeGrid.getTree().addMenuContextListener(this);

	}
	private void addStyles(AmiWebStyle i, WebTreeNode parent) {
		WebTreeNode node = this.styleTreeGrid.getTreeManager().createNode(i.getLabel() + (i.getReadOnly() ? "(readonly)" : ""), parent, false, i)
				.setIconCssStyle("_bgi=url('rsc/ami/template.png')").setCssClass("pointer").setKey(i.getId());
		this.styleId2Nodes.put(i.getId(), node);
		//		WebTreeNode node2 = this.styleTreeGrid.getTreeManager().createNode("root", node, false).setIsSelectable(true).setIconCssStyle("_bgi=url('rsc/ami/style2.png')")
		//				.setCssClass("pointer").setIsExpandable(true);
		//addStyleTypes(i, node2, null);
		for (AmiWebStyle child : styleManager.getChildStyles(i.getId()))
			addStyles(child, node);
	}
	public void addStyleTypes(AmiWebStyle style, WebTreeNode child, String type) {
		List<AmiWebStyleType> t = this.styleManager.getChildTypes(type);
		for (AmiWebStyleType s : t) {
			WebTreeNode node = child.getChildByKey(s.getName());
			if (node == null) {
				node = this.substyleTreeGrid.getTreeManager().createNode(s.getUserLabel(), child, false, s.getName()).setIsSelectable(true)
						.setIconCssStyle("_bgi=url('rsc/ami/style2.png')").setCssClass("pointer");
				node.setKey(s.getName());
			}
			addStyleTypes(style, node, s.getName());
		}
	}
	private void removeStyle(AmiWebStyle i) {
		WebTreeNode child = getNodeForStyle(i.getId());
		this.styleTreeGrid.getTreeManager().removeNode(child);
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.addButton) {
			getManager().showDialog("Add New Style", new AddStyleFormPortlet(this.styleManager, generateConfig(), null, true));
		} else if (button == this.importButton) {
			getManager().showDialog("Import Style", new ImportStylePortlet(generateConfig()));
		} else if ("CLOSE".equals(button.getId()))
			portlet.close();
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public void onStyleAdded(AmiWebStyle style) {
		if (style.getId() != null)
			addStyles(style, getNodeForStyle(style.getParentStyle()));
	}

	@Override
	public void onStyleRemoved(AmiWebStyle style) {
		if (style.getId() != null)
			removeStyle(style);
	}

	private static class AddStyleFormPortlet extends GridPortlet
			implements FormPortletListener, ConfirmDialogListener, FormPortletContextMenuFactory, FormPortletContextMenuListener {

		final private FormPortlet formPortlet;
		final private FormPortletTextField idField;
		final private FormPortletTextField labelField;
		private AmiWebStyle existing;
		private boolean isNuw;
		private AmiWebStyleManager styleManager;
		final private FormPortletSelectField<String> inherit;
		private FormPortletButton button;
		private FormPortletTextField urlField;
		private AmiWebStyleManagerPortlet styleManagerPortlet;

		public AddStyleFormPortlet(AmiWebStyleManager manager, PortletConfig config, AmiWebStyle existing, boolean isNuw) {
			super(config);
			this.styleManager = manager;
			this.formPortlet = new FormPortlet(generateConfig());
			this.formPortlet.getFormPortletStyle().setLabelsWidth(100);
			this.formPortlet.addField(new FormPortletTitleField("Style Identification"));
			this.labelField = this.formPortlet.addField(new FormPortletTextField("Display Label:")).setWidth(200);
			this.idField = this.formPortlet.addField(new FormPortletTextField("ID:")).setWidth(200);
			this.urlField = new FormPortletTextField("Path:").setWidth(200);
			this.urlField.setWidth(FormPortletField.WIDTH_STRETCH).setDisabled(true);
			this.formPortlet.addField(new FormPortletTitleField("Inherit Defaults"));
			this.inherit = this.formPortlet.addField(new FormPortletSelectField<String>(String.class, ""));
			this.inherit.sortOptionsByName();
			this.formPortlet.setMenuFactory(this);
			this.formPortlet.addMenuListener(this);
			this.button = this.formPortlet.addButton(new FormPortletButton("Create Style"));
			setStyle(existing, isNuw, null);
			addChild(this.formPortlet);
			setSuggestedSize(400, this.formPortlet.getSuggestedHeight(null) + 10);
			this.formPortlet.addFormPortletListener(this);
		}
		private AddStyleFormPortlet setStyle(AmiWebStyle existing, boolean isNuw, AmiWebStyleManagerPortlet styleManagerPortlet) {
			this.styleManagerPortlet = styleManagerPortlet;
			if (isNuw && existing != null) {
				existing = new AmiWebStyleImpl(existing);
				existing.setId(existing.getId() + "_COPY");
				existing.setLabel(existing.getLabel() + " Copy");
			}
			this.isNuw = isNuw;
			this.existing = existing;
			this.formPortlet.removeFieldNoThrow(this.urlField);
			this.inherit.setHasButton(this.styleManagerPortlet != null);
			if (existing != null) {
				this.idField.setValue(existing.getId());
				this.labelField.setValue(existing.getLabel());
				if (existing.getUrl() != null) {
					this.formPortlet.addFieldAfter(this.idField, this.urlField);
					this.urlField.setValue(existing.getUrl());
				}
			} else {
				this.isNuw = true;
				this.labelField.setValue(this.styleManager.getNextLabel("My Style"));
				this.idField.setValue(this.styleManager.getNextId("MY_STYLE"));
			}
			this.formPortlet.clearButtons();
			if (isNuw)
				this.formPortlet.addButton(this.button);
			this.inherit.clearOptions();
			if (existing != null && AmiWebStyleManager.FACTORY_DEFAULT_ID.equals(existing.getId())) {
				this.idField.setDisabled(true);
				this.labelField.setDisabled(true);
				this.inherit.setDisabled(true);
				this.button.setEnabled(false);
				this.inherit.addOption(null, "<root>");
			} else {
				if (!isNuw)
					this.idField.setDisabled(true);
				else
					this.idField.setDisabled(false);
				this.labelField.setDisabled(false);
				this.inherit.setDisabled(false);
				this.button.setEnabled(true);
				for (AmiWebStyle i : this.styleManager.getAllStyles()) {
					this.inherit.addOption(i.getId(), i.getLabel());
				}
				this.inherit.setValue(existing != null ? existing.getParentStyle() : AmiWebStyleManager.FACTORY_DEFAULT_ID);
			}
			if (existing != null && existing.getReadOnly()) {
				this.labelField.setDisabled(true);
				this.inherit.setDisabled(true);
			}
			return this;
		}
		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			AmiWebStyleImpl style = new AmiWebStyleImpl(this.styleManager, SH.trim(this.idField.getValue()), SH.trim(this.labelField.getValue()));
			style.setParentStyle(this.inherit.getValue());
			if (SH.isnt(style.getId()) || SH.isnt(style.getId())) {
				getManager().showAlert("Id and Label required");
				return;
			}
			if (!AmiUtils.isValidVariableName(style.getId(), false, false)) {
				getManager().showAlert("ID must be a valid variable name: " + style.getId());
				return;
			}
			if (isNuw) {
				if (this.styleManager.getStyleById(style.getId()) != null) {
					getManager().showAlert("Duplicate Id: " + style.getId());
					style.close();
					return;
				}
				if (this.styleManager.getStylesByLabel(style.getLabel()) != null) {
					getManager().showAlert("Duplicate Label: " + style.getLabel());
					style.close();
					return;
				}
				if (existing != null) {
					this.existing.setId(style.getId());
					this.existing.setLabel(style.getLabel());
					this.existing.setParentStyle(this.inherit.getValue());
					this.styleManager.addStyle(existing);
					this.existing = null;
				} else {
					this.styleManager.addStyle(style);
				}
				this.close();
			} else {
				if (this.styleManager.getStyleById(style.getId()) != null && this.styleManager.getStyleById(style.getId()) != existing) {
					getManager().showAlert("Duplicate Id: " + style.getId());
					style.close();
					return;
				}
				if (this.styleManager.getStylesByLabel(style.getLabel()) != null && this.styleManager.getStylesByLabel(style.getLabel()) != existing) {
					getManager().showAlert("Duplicate Label: " + style.getLabel());
					style.close();
					return;
				}
				this.existing.setLabel(style.getLabel());
				this.existing.setParentStyle(this.inherit.getValue());
			}
		}
		@Override
		public void onClosed() {
			if (isNuw && existing != null)
				this.existing.close();
			super.onClosed();
		}
		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {

			if (isNuw) {
				if (field == this.labelField)
					this.idField.setValue(styleManager.getNextId(AmiUtils.toValidVarName(this.labelField.getValue()).toUpperCase()));
			} else {
				if (field == this.labelField) {
					String lbl = this.labelField.getValue();
					if (OH.ne(this.existing.getLabel(), lbl)) {
						if (this.styleManager.getStylesByLabel(lbl) != null)
							getManager().showAlert("Duplicate Label:" + lbl);
						else
							this.existing.setLabel(lbl);
					}
				} else if (field == this.inherit) {
					String styleId = this.inherit.getValue();
					if (this.styleManager.getStyleById(styleId).inheritsFrom(this.existing.getId()))
						getManager().showAlert("Can not inherit from <B>" + styleId + "</B> This Would Cause a Circular Reference.");
					else
						this.existing.setParentStyle(styleId);
				} else if (field instanceof FormPortletToggleButtonsField) {
				}

			}
		}
		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}
		@Override
		public boolean onButton(ConfirmDialog source, String id) {
			return true;
		}
		@Override
		public WebMenu createMenu(FormPortlet formPortlet, FormPortletField<?> field, int cursorPosition) {
			BasicWebMenu r = new BasicWebMenu();
			if (AmiWebEditStylePortlet.INHERIT_FROM_FIELD_NAME.equals(field.getName()))
				r.add(new BasicWebMenuLink("Jump to this style... ", true, "edit_style"));
			return r;
		}

		@Override
		public void onContextMenu(FormPortlet portlet, String action, FormPortletField node) {
			if ("edit_style".equals(action)) {
				final AmiWebStyle style = styleManager.getStyleById(this.inherit.getValue());
				this.styleManagerPortlet.focusStyle(style.getId(), null);

			}
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		if ("expimp_type".equals(action)) {
			if (this.editPortlet != null)
				getManager().showDialog("Export/Import", new FormExportPortlet(this.editPortlet.getStyleForm(), this.editPortlet, true));
		} else if ("reset".equals(action)) {

			List<WebTreeNode> sel = tree.getSelected();
			List<Tuple2> data = new ArrayList<Tuple2>(sel.size());
			for (WebTreeNode i : sel)
				data.add((Tuple2) i.getData());
			getManager().showDialog("Reset Style",
					new ConfirmDialogPortlet(generateConfig(), "Sure you want to clear out all style options for selected?", ConfirmDialog.TYPE_OK_CANCEL, this)
							.setCallback("CLEAR_STYLE").setCorrelationData(data));
		} else if (SH.startsWith(action, "copy_style_")) {
			AmiWebStyle existing = this.styleManager.getStyleById(SH.stripPrefix(action, "copy_style_", true));
			getManager().showDialog("Copy Style", new AddStyleFormPortlet(this.styleManager, generateConfig(), existing, true));
		} else if (OH.eq(action, "export_styles")) {
			List<Map> list = new ArrayList<Map>();
			for (WebTreeNode i : this.styleTreeGrid.getTree().getSelected()) {
				if (i.getData() instanceof AmiWebStyle) {
					list.add(((AmiWebStyle) i.getData()).getStyleConfiguration());
				}
			}
			String s = getManager().getJsonConverter().objectToString(list);
			FormPortlet fp = new FormPortlet(generateConfig()).getFormPortletStyle().setLabelsWidth(15);
			fp.addField(new FormPortletTitleField("Export Style:"));
			FormPortletTextAreaField field = fp.addField(new FormPortletTextAreaField(""));
			field.setWidth(FormPortletTextAreaField.WIDTH_STRETCH).setHeight(FormPortletTextAreaField.HEIGHT_STRETCH).setValue(s).setSelection(0, s.length());
			fp.focusField(field);
			fp.addButton(new FormPortletButton("Close").setId("CLOSE"));
			fp.addFormPortletListener(this);
			getManager().showDialog("Export Style", fp, 600, 600);
		} else if (SH.startsWith(action, "delete_style_")) {
			AmiWebStyle existing = this.styleManager.getStyleById(SH.stripPrefix(action, "delete_style_", true));
			List<String> dependents = new ArrayList<String>();
			for (AmiWebStyle i : this.styleManager.getAllStyles()) {
				if (i != existing && i.inheritsFrom(existing.getId()))
					dependents.add(i.getLabel());
			}
			int portlets = 0;
			for (AmiWebStyledPortlet i : PortletHelper.findPortletsByType(getManager().getRoot(), AmiWebStyledPortlet.class))
				if (i.getStylePeer().inheritsFrom(existing.getId()))
					portlets++;
			if (portlets > 0)
				dependents.add(portlets + " panel(s)");
			if (!dependents.isEmpty()) {
				getManager().showAlert("Can not delete <B>" + existing.getLabel() + "</B> because The following inherit from it: <B>" + SH.join(", ", dependents));
				return;
			} else {
				getManager().showDialog("Delete Style", new ConfirmDialogPortlet(generateConfig(), "Delete <B>" + existing.getLabel() + "</B>?", ConfirmDialog.TYPE_OK_CANCEL, this)
						.setCallback("DELETE_STYLE").setCorrelationData(existing.getId()));
			}
		}
	}
	public void focusStyle(String styleId, String type) {
		this.styleTreeGrid.getTree().clearSelected();
		getNodeForStyle(styleId).setSelected(true);
	}
	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}
	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
		if (fastWebTree == this.styleTreeGrid.getTree()) {
			if (!node.getSelected())
				return;
			if (!applyCurrentPanel())
				return;
			setCurrentStyle((AmiWebStyle) node.getData());
		} else if (fastWebTree == this.substyleTreeGrid.getTree()) {
			if (!node.getSelected())
				return;
			if (!applyCurrentPanel())
				return;
			Object data = node.getData();
			if (node == this.substyleCssNode) {
				this.editPanel.setPortlet(cssPortlet);
			} else if (node == this.substyleVarNode) {
				this.editPanel.setPortlet(varPortlet);
			} else if (node == this.substyleRootNode) {
				this.editPanel.setPortlet(this.styleFormPortlet);
			} else if (data instanceof String) {
				String type = (String) data;
				this.editPortlet = this.editors.get(type);
				this.editPortlet.setAmiWebStyle(this.currentStyle);
				this.editPanel.setPortlet(this.editPortlet);
			} else
				this.editPanel.setPortlet(this.blankEditPortlet);
		}
	}
	private void setCurrentStyle(AmiWebStyle style) {
		this.currentStyle = style;
		this.substyleRootNode.setName(this.currentStyle.getLabel());
		this.styleFormPortlet.setStyle(this.currentStyle, false, this);
		this.cssPortlet.setStyle(this.currentStyle);
		this.varPortlet.setStyle(this.currentStyle);
		if (this.editPortlet != null)
			this.editPortlet.setAmiWebStyle(this.currentStyle);
		addStyleTypes(this.currentStyle, this.substyleStylesNode, null);
	}

	private boolean applyCurrentPanel() {
		StringBuilder sink = new StringBuilder();
		if (!applyCurrentPanel(sink)) {
			getManager().showAlert(sink.toString());
			return false;
		}
		return true;
	}
	private boolean applyCurrentPanel(StringBuilder sink) {
		if (this.editPanel.getPortlet() == this.cssPortlet) {
			if (!this.cssPortlet.apply()) {
				sink.append("Error in css");
				return false;
			}
		} else if (this.editPanel.getPortlet() == this.varPortlet) {
			if (!this.varPortlet.apply(sink)) {
				return false;
			}
		}
		return true;
	}
	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onStyleLabelChanged(AmiWebStyle style, String old, String label) {
		WebTreeNode child = getNodeForStyle(style.getId());
		child.setName(label);
	}
	private WebTreeNode getNodeForStyle(String id) {
		return this.styleId2Nodes.get(id);

	}
	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		BasicWebMenu r = new BasicWebMenu();
		if (fastWebTree == this.styleTreeGrid.getTree()) {
			if (selected.size() >= 1) {
				if (selected.size() == 1) {
					Object data = selected.get(0).getData();
					AmiWebStyle style = (AmiWebStyle) data;
					r.addChild(new BasicWebMenuLink("Copy Style", true, "copy_style_" + style.getId()));
					r.addChild(new BasicWebMenuLink("Delete Style", !style.getReadOnly() && !style.getId().equals(AmiWebStyleManager.LAYOUT_DEFAULT_ID),
							"delete_style_" + style.getId()));
				}
				r.addChild(new BasicWebMenuLink("Export Style(s)", true, "export_styles"));
			}
		} else if (fastWebTree == this.substyleTreeGrid.getTree()) {
			if (selected.size() == 1 && selected.get(0).getData() instanceof String)
				r.addChild(new BasicWebMenuLink("Export / Import", true, "expimp_type"));
		}
		return r;
	}
	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}
	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("DELETE_STYLE".equals(source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				this.styleManager.removeStyleById((String) source.getCorrelationData());
			}
			return true;
		} else if ("CLEAR_STYLE".equals(source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id)) {
				List<Tuple2> datas = (List<Tuple2>) source.getCorrelationData();
				for (Tuple2 data : datas) {
					AmiWebStyle style = (AmiWebStyle) data.getA();
					String type = (String) data.getB();
					Set<Short> t = style.getDeclaredKeys(type);
					for (Short i : CH.l(t))
						style.putValue(type, i, null);
					this.editPortlet.setAmiWebStyle(style);
				}
			}
			return true;
		} else if ("CLOSE".equals(source.getCallback())) {
			if (ConfirmDialog.ID_YES.equals(id))
				close();
			return true;
		}
		return false;
	}

	public class ImportStylePortlet extends GridPortlet implements FormPortletListener {

		private FormPortletButton importButton;
		private FormPortletButton cancelButton;
		private FormPortletTextAreaField field;

		public ImportStylePortlet(PortletConfig config) {
			super(config);
			FormPortlet form = new FormPortlet(generateConfig());
			form.getFormPortletStyle().setLabelsWidth(15);
			form.addField(new FormPortletTitleField("Import Style:"));
			field = form.addField(new FormPortletTextAreaField(""));
			field.setWidth(FormPortletTextAreaField.WIDTH_STRETCH).setHeight(FormPortletTextAreaField.HEIGHT_STRETCH).setValue("");
			setSuggestedSize(500, 500);
			this.importButton = form.addButton(new FormPortletButton("Import"));
			this.cancelButton = form.addButton(new FormPortletButton("Cancel"));
			form.addFormPortletListener(this);
			addChild(form);
		}

		@Override
		public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
			if (button == cancelButton)
				close();
			else if (button == importButton) {
				String value = field.getValue();
				if (SH.isnt(value)) {
					getManager().showAlert("Copy and Paste a previously exported style into the dialog box");
					return;
				}
				List<AmiWebStyleImpl> styles = new ArrayList<AmiWebStyleImpl>();
				try {
					List l = (List) getManager().getJsonConverter().stringToObject(value);
					for (Object t : l)
						styles.add(new AmiWebStyleImpl(styleManager, (Map<String, Object>) t));
				} catch (Exception e) {
					getManager().showAlert("The Provided text is not a valid AMI Style json Export", e);
					return;
				}
				for (AmiWebStyle style : styles) {
					String label = styleManager.getNextLabel(style.getLabel());
					String id = styleManager.getNextId(style.getId());
					style.setLabel(label);
					style.setId(id);
					String s = style.getParentStyle();
					if (styleManager.getStyleById(s) == null)
						style.setParentStyle(AmiWebStyleManager.FACTORY_DEFAULT_ID);
					styleManager.addStyle(style);
				}
				close();
				if (styles.size() == 1)
					getManager().showAlert("Success, Added '<B>" + CH.first(styles).getLabel() + "</B>' with id='<B>" + CH.first(styles).getId() + "</B>'");
				else
					getManager().showAlert("Success, Added <B>" + styles.size() + "</B> styles");
			}
		}

		@Override
		public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		}

		@Override
		public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
		}

	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}
	@Override
	public int compare(WebTreeNode o1, WebTreeNode o2) {
		boolean a = o1.getData() instanceof AmiWebStyle;
		boolean b = o2.getData() instanceof AmiWebStyle;
		if (a != b) {
			return a ? 1 : -1;
		}
		return OH.compare(o1.getName(), o2.getName());
	}
	@Override
	public void onStyleParentChanged(AmiWebStyleImpl style, String oldParentStyleId, String parentStyleId) {
		WebTreeNode target = getNodeForStyle(style.getId());
		WebTreeNode nuw = getNodeForStyle(parentStyleId);
		if (target != null) {
			for (WebTreeNode p = target.getParent(); p != null; p = p.getParent())
				p.setIsExpanded(false);
			target.getParent().removeChild(target);
			nuw.addChild(target);
			target.setSelected(true);
			for (WebTreeNode p = target.getParent(); p != null; p = p.getParent())
				p.setIsExpanded(true);
		}

	}
	public void onCloseButton() {
		StringBuilder sink = new StringBuilder();
		if (!applyCurrentPanel(sink)) {
			ConfirmDialogPortlet cdp = new ConfirmDialogPortlet(generateConfig(), sink.toString(), ConfirmDialog.TYPE_OK_CANCEL, this).setCallback("CLOSE");
			cdp.updateButton(ConfirmDialog.ID_YES, "Drop changes and Close");
			getManager().showDialog("Close", cdp);
			return;
		} else
			close();
	}

}
