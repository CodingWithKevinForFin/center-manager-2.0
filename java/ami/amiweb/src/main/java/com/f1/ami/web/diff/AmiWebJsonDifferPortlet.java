package com.f1.ami.web.diff;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.f1.ami.web.AmiWebConsts;
import com.f1.ami.web.AmiWebDesktopBar;
import com.f1.ami.web.AmiWebDifferPortlet;
import com.f1.ami.web.AmiWebFormPortletAmiScriptField;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebLayoutManager;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebServiceAction;
import com.f1.ami.web.AmiWebViewConfigurationPortlet;
import com.f1.suite.web.WebState;
import com.f1.suite.web.WebStatesManager;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.AbstractWebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.peripheral.KeyEvent;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletManager;
import com.f1.suite.web.portal.impl.ConfirmDialog;
import com.f1.suite.web.portal.impl.ConfirmDialogListener;
import com.f1.suite.web.portal.impl.ConfirmDialogPortlet;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.MultiDividerPortlet;
import com.f1.suite.web.portal.impl.form.FormPortlet;
import com.f1.suite.web.portal.impl.form.FormPortletButton;
import com.f1.suite.web.portal.impl.form.FormPortletButtonField;
import com.f1.suite.web.portal.impl.form.FormPortletCheckboxField;
import com.f1.suite.web.portal.impl.form.FormPortletDivField;
import com.f1.suite.web.portal.impl.form.FormPortletField;
import com.f1.suite.web.portal.impl.form.FormPortletListener;
import com.f1.suite.web.portal.impl.form.FormPortletNumericRangeField;
import com.f1.suite.web.portal.impl.form.FormPortletTextAreaField;
import com.f1.suite.web.tree.WebTreeContextMenuFactory;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.structs.Tuple2;

public class AmiWebJsonDifferPortlet extends GridPortlet
		implements Comparator<WebTreeNode>, WebTreeContextMenuListener, FormPortletListener, WebTreeContextMenuFactory, ConfirmDialogListener {

	private static final String TMP_LAYOUT_NAME = "DIFFRESULT";
	final private Map<String, AmiWebJsonDiffNode<?>> roots;
	final private DividerPortlet divPortlet;
	final private FastTreePortlet treePortlet;
	final private MultiDividerPortlet lowerDivPortlet;
	final private FormPortlet buttonsPortlet;
	final private AmiWebService service;
	final private FormPortlet leftTextPortlet;
	final private FormPortlet rightTextPortlet;
	final private FormPortletTextAreaField leftText;
	final private FormPortletTextAreaField rightText;
	final private FormPortletTextAreaField outText;
	final private FormPortlet outTextPortlet;
	final private FormPortlet origTextPortlet;
	final private FormPortletTextAreaField origText;
	final private FormPortletCheckboxField onlyChangesField;
	final private AmiWebFormPortletAmiScriptField origScriptText;
	final private AmiWebFormPortletAmiScriptField leftScriptText;
	final private AmiWebFormPortletAmiScriptField rightScriptText;
	final private AmiWebFormPortletAmiScriptField outScriptText;
	final private FormPortletButton saveButton;
	final private FormPortletButton testButton;
	final private FormPortletButton closeButton;
	final private FormPortlet formPortlet;
	private WebTreeNode activeTreeNode;
	private boolean showLeft;
	private String rootName;
	private FormPortletButtonField changeBackButtonField;
	private FormPortletButtonField changeNextButtonField;
	private FormPortletNumericRangeField changeNumberField;
	private FormPortletDivField changeCountField;
	private FormPortletButtonField conflictBackButtonField;
	private FormPortletButtonField conflictNextButtonField;
	private FormPortletNumericRangeField conflictNumberField;
	private FormPortletDivField conflictCountField;
	private List<AmiWebJsonDiffNode<?>> changes = new ArrayList<AmiWebJsonDiffNode<?>>();
	private List<AmiWebJsonDiffNode<?>> conflicts = new ArrayList<AmiWebJsonDiffNode<?>>();

	public AmiWebJsonDifferPortlet(AmiWebService service, PortletConfig config) {
		super(config);
		this.service = service;
		this.roots = new HashMap<String, AmiWebJsonDiffNode<?>>();
		this.divPortlet = new DividerPortlet(generateConfig(), false);
		this.formPortlet = new FormPortlet(generateConfig());
		this.buttonsPortlet = new FormPortlet(generateConfig());
		this.addChild(formPortlet, 0, 0);
		this.addChild(divPortlet, 0, 1);
		this.addChild(buttonsPortlet, 0, 2);
		this.setRowSize(0, 30);
		this.setRowSize(2, 40);
		this.treePortlet = new FastTreePortlet(generateConfig());
		this.treePortlet.getTree().getColumn(0).setWidth(350);
		this.treePortlet.getTree().addColumnAt(true, new FastWebTreeColumn(1, new AmiWebJsonDiffTreeFormatters.SideFormatter(0), "Base", "", false).setWidth(180), 0);
		this.treePortlet.getTree().addColumnAt(true, new FastWebTreeColumn(2, new AmiWebJsonDiffTreeFormatters.SideFormatter(1), "Theirs", "", false).setWidth(180), 1);
		this.treePortlet.getTree().addColumnAt(true, new FastWebTreeColumn(3, new AmiWebJsonDiffTreeFormatters.SideFormatter(2), "Yours", "", false).setWidth(180), 2);
		this.treePortlet.getTree().addColumnAt(true, new FastWebTreeColumn(4, new AmiWebJsonDiffTreeFormatters.ChoiceFormatter(), "Choice", "", false).setWidth(180), 3);
		this.treePortlet.getTree().addColumnAt(true, new FastWebTreeColumn(5, new AmiWebJsonDiffTreeFormatters.SideFormatter(3), "Result", "", false).setWidth(180), 4);
		this.treePortlet.addOption(FastTreePortlet.OPTION_COLUMN_HEADER_BG_COLOR, "_bg=#CCCCCC");
		this.treePortlet.addOption(FastTreePortlet.OPTION_COLUMN_HEADER_FONT_COLOR, "_fg=#000000");
		this.treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BAR_HIDDEN, "true");
		this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_FONT_SIZE, 14);
		this.treePortlet.addOption(FastTreePortlet.OPTION_HEADER_ROW_HEIGHT, 26);
		this.divPortlet.addChild(this.treePortlet);
		this.lowerDivPortlet = new MultiDividerPortlet(generateConfig(), true);
		this.divPortlet.addChild(this.lowerDivPortlet);
		this.treePortlet.getTree().addMenuContextListener(this);
		this.origTextPortlet = new FormPortlet(generateConfig());
		this.leftTextPortlet = new FormPortlet(generateConfig());
		this.rightTextPortlet = new FormPortlet(generateConfig());
		this.outTextPortlet = new FormPortlet(generateConfig());
		this.lowerDivPortlet.addChild(origTextPortlet);
		this.lowerDivPortlet.addChild(leftTextPortlet);
		this.lowerDivPortlet.addChild(rightTextPortlet);
		this.lowerDivPortlet.addChild(outTextPortlet);
		this.origText = this.origTextPortlet.addField(new FormPortletTextAreaField("Base"));
		this.leftText = this.leftTextPortlet.addField(new FormPortletTextAreaField("Theirs"));
		this.rightText = this.rightTextPortlet.addField(new FormPortletTextAreaField("Yours"));
		this.outText = this.outTextPortlet.addField(new FormPortletTextAreaField("Result"));
		this.divPortlet.setColor("#444444");
		this.divPortlet.setThickness(2);
		String la = "";
		this.origScriptText = this.origTextPortlet.addField(new AmiWebFormPortletAmiScriptField("Base", config.getPortletManager(), la));
		this.leftScriptText = this.leftTextPortlet.addField(new AmiWebFormPortletAmiScriptField("Theirs", config.getPortletManager(), la));
		this.rightScriptText = this.rightTextPortlet.addField(new AmiWebFormPortletAmiScriptField("Yours", config.getPortletManager(), la));
		this.outScriptText = this.outTextPortlet.addField(new AmiWebFormPortletAmiScriptField("Result", config.getPortletManager(), la));

		this.origScriptText.setVisible(false);
		this.leftScriptText.setVisible(false);
		this.rightScriptText.setVisible(false);
		this.outScriptText.setVisible(false);

		for (FormPortletField<?> i : AH.a(origText, leftText, rightText, outText, origScriptText, leftScriptText, rightScriptText, outScriptText)) {
			i.setLabelSide(FormPortletField.LABEL_SIDE_TOP);
			i.setLabelSideAlignment(FormPortletField.LABEL_SIDE_ALIGN_START);
			i.setLeftTopRightBottom(1, 20, 1, 1);
			i.setDisabled(true);
		}
		this.outTextPortlet.addFormPortletListener(this);
		this.treePortlet.getTree().setContextMenuFactory(this);
		this.treePortlet.getTree().setShowExpandMenuItems(false);
		this.treePortlet.getRoot().setIsExpanded(false);
		this.treePortlet.getTree().setAutoExpandUntilMultipleNodes(false);
		this.formPortlet.getFormPortletStyle().setCssStyle("_bg=#CCCCCC");
		this.onlyChangesField = this.formPortlet.addField(new FormPortletCheckboxField("Only Show Changes "));
		this.onlyChangesField.setLabelSide(FormPortletField.LABEL_SIDE_RIGHT);
		this.onlyChangesField.setLabelPaddingPx(4);
		this.onlyChangesField.setLeftTopWidthHeightPx(5, 5, 16, 16);
		this.onlyChangesField.setValue(true);

		int top = 2, height = 18;
		{
			int left = 200;
			this.changeNumberField = this.formPortlet.addField(new FormPortletNumericRangeField(""));
			this.changeBackButtonField = this.formPortlet.addField(new FormPortletButtonField(""));
			this.changeCountField = this.formPortlet.addField(new FormPortletDivField(""));
			this.changeNextButtonField = this.formPortlet.addField(new FormPortletButtonField(""));

			this.changeBackButtonField.setIgnoreDefaultStyle(true);
			this.changeBackButtonField.setBgColor("#BBDDBB");
			this.changeBackButtonField.setBorderColor("#559955");
			this.changeBackButtonField.setFontColor("#006600");

			this.changeNextButtonField.setIgnoreDefaultStyle(true);
			this.changeNextButtonField.setBgColor("#BBDDBB");
			this.changeNextButtonField.setBorderColor("#559955");
			this.changeNextButtonField.setFontColor("#006600");

			this.changeNumberField.setIgnoreDefaultStyle(true);
			this.changeNumberField.setBgColor("#BBDDBB");
			this.changeNumberField.setBorderColor("#559955");
			this.changeNumberField.setFontColor("#006600");

			this.changeCountField.setFontColor("#006600");

			this.changeNumberField.setValue(5);
			this.changeNumberField.setSliderHidden(true);
			this.changeNumberField.setTextHidden(false);
			this.changeNumberField.setDecimals(0);
			this.changeBackButtonField.setBorderRadius(2);
			this.changeNextButtonField.setBorderRadius(2);
			this.changeBackButtonField.setValue("<<");
			this.changeNextButtonField.setValue(">>");
			this.changeBackButtonField.setLeftTopWidthHeightPx(left + 0, top, 20, height);
			this.changeNumberField.setLeftTopWidthHeightPx(left + 20, top, 30, height);
			this.changeNextButtonField.setLeftTopWidthHeightPx(left + 50, top, 20, height);
			this.changeCountField.setLeftTopWidthHeightPx(left + 80, top, 120, height);
			this.changeCountField.setValue("of 10 changes");
		}
		{
			int left = 500;
			this.conflictNumberField = this.formPortlet.addField(new FormPortletNumericRangeField(""));
			this.conflictBackButtonField = this.formPortlet.addField(new FormPortletButtonField(""));
			this.conflictCountField = this.formPortlet.addField(new FormPortletDivField(""));
			this.conflictNextButtonField = this.formPortlet.addField(new FormPortletButtonField(""));

			this.conflictBackButtonField.setIgnoreDefaultStyle(true);
			this.conflictBackButtonField.setBgColor("#DDBBBB");
			this.conflictBackButtonField.setBorderColor("#995555");
			this.conflictBackButtonField.setFontColor("#AA0000");

			this.conflictNextButtonField.setIgnoreDefaultStyle(true);
			this.conflictNextButtonField.setBgColor("#DDBBBB");
			this.conflictNextButtonField.setBorderColor("#995555");
			this.conflictNextButtonField.setFontColor("#AA0000");

			this.conflictNumberField.setCssStyle("");
			this.conflictNumberField.setBgColor("#DDBBBB");
			this.conflictNumberField.setFontColor("#AA0000");
			this.conflictNumberField.setBorderColor("#995555");
			this.conflictCountField.setCssStyle("");
			this.conflictCountField.setFontColor("#AA0000");
			this.conflictNumberField.setValue(5);
			this.conflictNumberField.setSliderHidden(true);
			this.conflictNumberField.setTextHidden(false);
			this.conflictNumberField.setDecimals(0);
			this.conflictBackButtonField.setBorderRadius(2);
			this.conflictNextButtonField.setBorderRadius(2);
			this.conflictBackButtonField.setValue("<<");
			this.conflictNextButtonField.setValue(">>");
			this.conflictBackButtonField.setLeftTopWidthHeightPx(left + 0, top, 20, height);
			this.conflictNumberField.setLeftTopWidthHeightPx(left + 20, top, 30, height);
			this.conflictNextButtonField.setLeftTopWidthHeightPx(left + 50, top, 20, height);
			this.conflictCountField.setLeftTopWidthHeightPx(left + 80, top, 120, height);
			this.conflictCountField.setValue("of 10 conflicts");
		}

		this.saveButton = this.buttonsPortlet.addButton(new FormPortletButton("Save"));
		this.testButton = this.buttonsPortlet.addButton(new FormPortletButton("Test"));
		this.closeButton = this.buttonsPortlet.addButton(new FormPortletButton("Close"));
		this.buttonsPortlet.addFormPortletListener(this);
		this.formPortlet.addFormPortletListener(this);
		this.treePortlet.getTree().setRootLevelVisible(false);
	}

	private static final Comparator<AmiWebJsonDiffNode<?>> POSITION_COMPARATOR = new Comparator<AmiWebJsonDiffNode<?>>() {

		@Override
		public int compare(AmiWebJsonDiffNode<?> arg0, AmiWebJsonDiffNode<?> arg1) {
			System.out.println(arg0.getKey() + " " + arg0.getNode().getAbsolutePosition());
			System.out.println(arg1.getKey() + " " + arg1.getNode().getAbsolutePosition());
			System.out.println();
			int absolutePosition = arg0.getNode().getAbsolutePosition();
			return OH.compare(absolutePosition, arg1.getNode().getAbsolutePosition());
		}
	};

	public void buildTree() {
		this.changes.clear();
		this.conflicts.clear();
		this.treePortlet.clear();
		WebTreeNode webroot = this.treePortlet.getRoot();
		byte same = AmiWebJsonDiffNode.SAME_AL;
		for (Entry<String, AmiWebJsonDiffNode<?>> root : this.roots.entrySet()) {
			WebTreeNode root2 = AmiWebJsonDiffNode.createNode(OH.noNull(root.getKey(), AmiWebDesktopBar.UNSAVED_HTML), webroot, root.getValue(),
					this.onlyChangesField.getBooleanValue());
			same &= root.getValue().getSameness();
			findChanges(root.getValue());
		}
		this.showLeft = !MH.anyBits(same, AmiWebJsonDiffNode.SAME_BL);
		if (!showLeft) {
			this.treePortlet.getTree().hideColumn(2);
			if (this.lowerDivPortlet.getChildren().containsKey(leftTextPortlet.getPortletId()))
				this.lowerDivPortlet.removeChild(leftTextPortlet.getPortletId());
		} else {
			this.treePortlet.getTree().showColumn(2, 1);
			if (!this.lowerDivPortlet.getChildren().containsKey(leftTextPortlet.getPortletId()))
				this.lowerDivPortlet.addChild(leftTextPortlet);
		}
		this.treePortlet.getTreeManager().setComparator(this);
		Collections.sort(this.changes, POSITION_COMPARATOR);
		Collections.sort(this.conflicts, POSITION_COMPARATOR);
		this.treePortlet.getTreeManager().setActiveSelectedNode(null);
		if (this.changes.size() > 0) {
			this.changeCountField.setValue("of " + this.changes.size() + " changes");
			this.changeNumberField.setRange(1, changes.size());
			this.changeNumberField.setValue(1);
			this.changeNumberField.setVisible(true);
			this.changeNextButtonField.setVisible(true);
			this.changeBackButtonField.setVisible(true);
		} else {
			this.changeCountField.setValue("no changes");
			this.changeNumberField.setVisible(false);
			this.changeNextButtonField.setVisible(false);
			this.changeBackButtonField.setVisible(false);
		}
		if (this.conflicts.size() > 0) {
			this.conflictCountField.setValue("of " + this.conflicts.size() + " conflicts");
			this.conflictNumberField.setRange(1, conflicts.size());
			this.conflictNumberField.setValue(1);
			this.conflictNumberField.setVisible(true);
			this.conflictNextButtonField.setVisible(true);
			this.conflictBackButtonField.setVisible(true);
		} else {
			this.conflictCountField.setValue("no conflicts");
			this.conflictNumberField.setVisible(false);
			this.conflictNextButtonField.setVisible(false);
			this.conflictBackButtonField.setVisible(false);
		}
		if (changes.size() > 0)
			updateSelected();
	}

	private void updateUserChoice(AmiWebJsonDiffNode<?> dn, boolean alreadyAddedToChanges) {
		if (dn.getChildren().isEmpty()) {
			switch (dn.getSameness()) {
				case AmiWebJsonDiffNode.SAME_BR:
					dn.setUserChoice(AmiWebJsonDiffNode.STATE_LEFT);
					break;
				case AmiWebJsonDiffNode.SAME_AL:
					dn.setUserChoice(AmiWebJsonDiffNode.STATE_NOCHANGE);
					break;
				case AmiWebJsonDiffNode.SAME_LR:
					dn.setUserChoice(AmiWebJsonDiffNode.STATE_RIGHT);
					break;
				case AmiWebJsonDiffNode.SAME_BL:
					dn.setUserChoice(AmiWebJsonDiffNode.STATE_RIGHT);
					break;
				default:
					dn.setUserChoice(AmiWebJsonDiffNode.STATE_RIGHT);
					break;
			}
			return;
		}
		byte choices = 0;
		for (AmiWebJsonDiffNode<?> i : dn.getChildren()) {
			updateUserChoice(i, alreadyAddedToChanges);
			choices |= i.getUserChoice();
		}
		byte userChoice = getChoiceFromChildChoices(choices);
		dn.setUserChoice(userChoice);
	}
	private void findChanges(AmiWebJsonDiffNode<?> dn) {
		if (dn.getNode() != null && dn.getThisSameness() != AmiWebJsonDiffNode.SAME_AL) {
			this.changes.add(dn);
			if (dn.getSameness() == 0)
				this.conflicts.add(dn);
			return;
		}
		for (AmiWebJsonDiffNode<?> i : dn.getChildren())
			findChanges(i);
	}

	public AmiWebJsonDiffNode<?> build(String key, AmiWebJsonDiffNode<?> parent, Object org, Object lft, Object rgt) {
		String path = (parent == null || "".equals(parent.getKey())) ? key : (parent.getPath() + '.' + key);
		final byte ot = getType(org);
		final byte lt = getType(lft);
		final byte rt = getType(rgt);
		final byte type;
		switch ((lft == null ? 1 : 0) | (rgt == null ? 2 : 0) | (org == null ? 4 : 0)) {
			case 0://no nulls;
				type = (ot == rt && ot == lt) ? ot : AmiWebJsonDiffNode.TYPE_OBJ;
				break;
			case 1://lft 
				type = (ot == rt) ? ot : AmiWebJsonDiffNode.TYPE_OBJ;
				break;
			case 2://rgt
				type = (ot == lt) ? ot : AmiWebJsonDiffNode.TYPE_OBJ;
				break;
			case 3://lft & rght
				type = ot;
				break;
			case 4://org
				type = (rt == lt) ? rt : AmiWebJsonDiffNode.TYPE_OBJ;
				break;
			case 5://lft & org
				type = rt;
				break;
			case 6://rgt & org
				type = lt;
				break;
			case 7://all are null
			default:
				type = AmiWebJsonDiffNode.TYPE_OBJ;
		}
		AmiWebJsonDictionary dictionary = new AmiWebJsonDictionary();

		AmiWebJsonDiffNode<?> r;
		switch (type) {
			case AmiWebJsonDiffNode.TYPE_MAP:
				r = new AmiWebJsonDiffNodeMap(this, key, parent, (Map<String, Object>) org, (Map<String, Object>) lft, (Map<String, Object>) rgt);
				break;
			case AmiWebJsonDiffNode.TYPE_LST:
				if (dictionary.isAmiscriptKey(key))
					r = AmiWebJsonDiffNodeAmiscript.newDiffNodeAmiscript(this, key, parent, (List<Object>) org, (List<Object>) lft, (List<Object>) rgt);
				else {
					String sortingKey = dictionary.getListKey(path);
					r = new AmiWebJsonDiffNodeList(this, key, parent, (List<Object>) org, (List<Object>) lft, (List<Object>) rgt, sortingKey);
				}
				break;
			case AmiWebJsonDiffNode.TYPE_OBJ:
				r = new AmiWebJsonDiffNodeObj(this, key, parent, org, lft, rgt);
				break;
			default:
				throw new IllegalStateException();
		}

		String label = dictionary.getLabel(path);
		if (label != null)
			r.setLabel(label);
		else
			r.setLabel(key);
		if (dictionary.isHiddenKey(path))
			r.setHidden(true);
		return r;
	}

	static private byte getType(Object o) {
		if (o instanceof Map)
			return AmiWebJsonDiffNode.TYPE_MAP;
		else if (o instanceof List)
			return AmiWebJsonDiffNode.TYPE_LST;
		else
			return AmiWebJsonDiffNode.TYPE_OBJ;
	}

	@Override
	public int compare(WebTreeNode arg0, WebTreeNode arg1) {
		AmiWebJsonDiffNode dn0 = (AmiWebJsonDiffNode) arg0.getData();
		AmiWebJsonDiffNode dn1 = (AmiWebJsonDiffNode) arg1.getData();
		if (arg0.getParent() != null && arg0.getParent().getData() instanceof AmiWebJsonDiffNodeAmiscript) {
			return OH.compare(dn0.getKey(), dn1.getKey());
		}
		int r = OH.compare(dn0.getSameness(), dn1.getSameness());
		if (r != 0)
			return r;
		return OH.compare(dn0.getKey(), dn1.getKey());
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
		List<WebTreeNode> sel = tree.getSelected();
		if (sel.size() == 1) {
			WebTreeNode tn = sel.get(0);
			AmiWebJsonDiffNode dn = (AmiWebJsonDiffNode) tn.getData();
			if ("path".equals(action)) {
				getManager().showAlert(dn.getPath());
			}
			if ("export_b".equals(action)) {
				showText("Base", dn.getOrig());
			} else if ("export_y".equals(action)) {
				showText("Yours", dn.getRight());
			} else if ("export_t".equals(action)) {
				showText("Yours", dn.getLeft());
			} else if ("export_r".equals(action)) {
				showText("Result", dn.getOut());
			} else if ("diff_rb".equals(action)) {
				showDiff("Base vs Result", dn.getOrig(), dn.getOut());
			} else if ("diff_rt".equals(action)) {
				showDiff("Theirs vs Result", dn.getLeft(), dn.getOut());
			} else if ("diff_ry".equals(action)) {
				showDiff("Yours vs Result", dn.getRight(), dn.getOut());
			} else if ("diff_ty".equals(action)) {
				showDiff("Theirs vs Yours", dn.getLeft(), dn.getRight());
			} else if ("diff_yb".equals(action)) {
				showDiff("Base vs Result", dn.getOrig(), dn.getRight());
			} else if ("diff_tb".equals(action)) {
				showDiff("Base vs Thiers", dn.getOrig(), dn.getLeft());
			} else if ("orig".equals(action)) {
				setChoiceWithConfirm(dn, AmiWebJsonDiffNode.STATE_BASE);
			} else if ("nochange".equals(action)) {
				setChoiceWithConfirm(dn, AmiWebJsonDiffNode.STATE_NOCHANGE);
			} else if ("left".equals(action)) {
				setChoiceWithConfirm(dn, AmiWebJsonDiffNode.STATE_LEFT);
			} else if ("right".equals(action)) {
				setChoiceWithConfirm(dn, AmiWebJsonDiffNode.STATE_RIGHT);
			} else if ("manual".equals(action)) {
				Object out = dn.getOut();
				String s = toJson(out);
				dn.setManualText(s);
				setChoice(dn, AmiWebJsonDiffNode.STATE_MANUAL);
				outText.getForm().focusField(outText);
			}
		}
	}

	private void setChoiceWithConfirm(AmiWebJsonDiffNode dn, byte choice) {
		if (hasEdit(dn)) {
			ConfirmDialogPortlet t = new ConfirmDialogPortlet(generateConfig(), "Warning: Manually edited values will be lost. Continue?", ConfirmDialogPortlet.TYPE_OK_CANCEL,
					this).setCallback("SET_CHOICE").setCorrelationData(new Tuple2<AmiWebJsonDiffNode<?>, Byte>(dn, choice));
			getManager().showDialog("Confirm", t);
		} else
			setChoice(dn, choice);
	}

	private boolean hasEdit(AmiWebJsonDiffNode<?> dn) {
		if (dn.getUserChoice() == AmiWebJsonDiffNode.STATE_MANUAL)
			return true;
		for (AmiWebJsonDiffNode<?> i : dn.getChildren())
			if (hasEdit(i))
				return true;
		return false;
	}

	private void showText(String name, Object json) {
		AmiWebViewConfigurationPortlet viewConfigPortlet = new AmiWebViewConfigurationPortlet(generateConfig());
		viewConfigPortlet.enableBrowser(true);
		viewConfigPortlet.setConfiguration(json);
		AmiWebLayoutManager layoutManager = service.getLayoutManager();
		getManager().showDialog("Export " + name + " configuration", viewConfigPortlet, layoutManager.getDialogWidth(), layoutManager.getDialogHeight())
				.setStyle(service.getUserDialogStyleManager());
	}

	private void showDiff(String string, Object left, Object right) {
		AmiWebDifferPortlet dp = new AmiWebDifferPortlet(generateConfig());
		dp.setText(toJson(left), toJson(right));
		getManager().showDialog(string, dp);
	}

	private String toJson(Object out) {
		return ObjectToJsonConverter.INSTANCE_CLEAN_SORTING.objectToString(out);
	}
	private Object fromJson(String out) {
		return ObjectToJsonConverter.INSTANCE_CLEAN_SORTING.stringToObject(out);
	}

	private void setChoice(AmiWebJsonDiffNode<?> dn, byte choice) {
		if (!dn.setUserChoice(choice))
			return;
		if (choice == AmiWebJsonDiffNode.STATE_MANUAL)
			setChoiceChildren(dn, (byte) AmiWebJsonDiffNode.STATE_DISABLED);
		else
			setChoiceChildren(dn, choice);
		for (AmiWebJsonDiffNode<?> i = dn.getParent(); i != null; i = i.getParent()) {
			byte choices = 0;
			for (AmiWebJsonDiffNode<?> c : i.getChildren())
				choices |= c.getUserChoice();
			byte userChoice = getChoiceFromChildChoices(choices);
			if (!i.setUserChoice(userChoice))
				break;
		}
		this.treePortlet.getTreeManager().onNodeDataChanged(dn.getNode());
		outScriptText.setDisabled(choice != AmiWebJsonDiffNode.STATE_MANUAL);
		outText.setDisabled(choice != AmiWebJsonDiffNode.STATE_MANUAL);
		outText.setValue(toJson(dn.getOut()));
		if (dn.getParent() instanceof AmiWebJsonDiffNodeAmiscript)
			outScriptText.setValue((String) dn.getOut());
	}

	private byte getChoiceFromChildChoices(byte choices) {
		byte userChoice;
		switch (choices) {
			case AmiWebJsonDiffNode.STATE_NOCHANGE:
				userChoice = AmiWebJsonDiffNode.STATE_NOCHANGE;
				break;
			case AmiWebJsonDiffNode.STATE_NOCHANGE | AmiWebJsonDiffNode.STATE_BASE:
				userChoice = AmiWebJsonDiffNode.STATE_BASE;
				break;
			case AmiWebJsonDiffNode.STATE_NOCHANGE | AmiWebJsonDiffNode.STATE_LEFT:
				userChoice = AmiWebJsonDiffNode.STATE_LEFT;
				break;
			case AmiWebJsonDiffNode.STATE_NOCHANGE | AmiWebJsonDiffNode.STATE_RIGHT:
				userChoice = AmiWebJsonDiffNode.STATE_RIGHT;
				break;
			case AmiWebJsonDiffNode.STATE_BASE:
			case AmiWebJsonDiffNode.STATE_LEFT:
			case AmiWebJsonDiffNode.STATE_RIGHT:
				userChoice = choices;
				break;
			default:
				userChoice = AmiWebJsonDiffNode.STATE_MERGE;
				break;
		}
		return userChoice;
	}
	private void setChoiceChildren(AmiWebJsonDiffNode<?> dn, byte choice) {
		for (AmiWebJsonDiffNode<?> i : dn.getChildren()) {
			i.setUserChoice(choice);
			setChoiceChildren(i, choice);
		}
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode start) {
		List<WebTreeNode> sel = fastWebTree.getSelected();
		if (sel.size() != 1) {
			origText.setValue("");
			leftText.setValue("");
			rightText.setValue("");
			outText.setValue("");
			origScriptText.setValue("");
			leftScriptText.setValue("");
			rightScriptText.setValue("");
			outScriptText.setValue("");
		} else {
			AmiWebJsonDiffNode<?> node = (AmiWebJsonDiffNode<?>) sel.get(0).getData();
			if (node == null)
				return;
			this.activeTreeNode = sel.get(0);
			outText.setDisabled(node.getUserChoice() != AmiWebJsonDiffNode.STATE_MANUAL);
			outScriptText.setDisabled(node.getUserChoice() != AmiWebJsonDiffNode.STATE_MANUAL);
			boolean useAmiscript = node != null && (node.getParent() instanceof AmiWebJsonDiffNodeAmiscript || node instanceof AmiWebJsonDiffNodeAmiscript);
			if (useAmiscript) {
				if (node instanceof AmiWebJsonDiffNodeAmiscript) {
					String or = SH.join("", (List) node.getOrig());
					String lt = SH.join("", (List) node.getLeft());
					String rt = SH.join("", (List) node.getRight());
					String ot = SH.join("", (List) node.getOut());
					origScriptText.setValue(or);
					leftScriptText.setValue(lt);
					rightScriptText.setValue(rt);
					outScriptText.setValue(ot);
				} else {
					String or = (String) node.getOrig();
					String lt = (String) node.getLeft();
					String rt = (String) node.getRight();
					String ot = (String) node.getOut();
					origScriptText.setValue(or);
					leftScriptText.setValue(lt);
					rightScriptText.setValue(rt);
					outScriptText.setValue(ot);
				}
			} else {
				String or = toJson(node.getOrig());
				String lt = toJson(node.getLeft());
				String rt = toJson(node.getRight());
				String ot = toJson(node.getOut());
				origText.setValue(or);
				rightText.setValue(rt);
				leftText.setValue(lt);
				outText.setValue(ot);
			}
			this.origText.setVisible(!useAmiscript);
			this.leftText.setVisible(!useAmiscript);
			this.rightText.setVisible(!useAmiscript);
			this.outText.setVisible(!useAmiscript);
			this.origScriptText.setVisible(useAmiscript);
			this.leftScriptText.setVisible(useAmiscript);
			this.rightScriptText.setVisible(useAmiscript);
			this.outScriptText.setVisible(useAmiscript);
		}
	}

	@Override
	public void onButtonPressed(FormPortlet portlet, FormPortletButton button) {
		if (button == this.saveButton) {
			for (Entry<String, AmiWebJsonDiffNode<?>> i : this.roots.entrySet()) {
				if (i.getKey() == null) {
					getManager().showAlert("Can not save <i>new</i> layout");
					return;
				}
				String source = SH.beforeFirst(i.getKey(), ':');
				String path = SH.afterFirst(i.getKey(), ':');
				String jsonText = toJson(i.getValue().getOut());
				this.service.getLayoutFilesManager().saveFile(source, path, jsonText);
				this.service.getLayoutFilesManager().reloadLayout();
			}
			close();
		} else if (button == this.closeButton) {
			this.close();
		} else if (button == this.testButton) {
			WebStatesManager states = this.service.getPortletManager().getState().getWebStatesManager();

			for (Entry<String, AmiWebJsonDiffNode<?>> i : this.roots.entrySet()) {
				final String source;
				final String path;
				if (i.getKey() == this.rootName) {
					source = AmiWebConsts.LAYOUT_SOURCE_TMP;
					path = TMP_LAYOUT_NAME;
				} else {
					source = SH.beforeFirst(i.getKey(), ':');
					path = SH.afterFirst(i.getKey(), ':');
				}
				Map o = (Map) fromJson(toJson(i.getValue().getOut()));//poor mans copy
				List<Map> includedFiles = (List) o.get("includeFiles");
				if (includedFiles != null)
					for (Map map : includedFiles)
						map.put("type", AmiWebConsts.LAYOUT_SOURCE_TMP);
				this.service.getLayoutFilesManager().saveFile(AmiWebConsts.LAYOUT_SOURCE_TMP, path, toJson(o));
			}
			for (String pgid : states.getPgIds()) {
				WebState state = states.getState(pgid);
				if (state == null)
					continue;
				PortletManager pm = state.getPortletManager();
				if (pm == null || !pm.getIsOpen())
					continue;
				AmiWebService aws = (AmiWebService) pm.getService(AmiWebService.ID);
				String layoutName = aws.getLayoutFilesManager().getLayoutName();
				if (OH.eq(TMP_LAYOUT_NAME, layoutName)) {
					AmiWebServiceAction nw = service.getPortletManager().getTools().nw(AmiWebServiceAction.class);
					nw.setAction(AmiWebServiceAction.ACTION_RELOAD_LAYOUT);
					this.service.getPortletManager().getBackend().sendMessageToPortletManager((String) state.getPartitionId(), nw, 0);
					getManager().showAlert("Session '" + TMP_LAYOUT_NAME + "' updated");
					return;
				}
			}
			this.service.getDesktop().launchNewSession(AmiWebConsts.LAYOUT_SOURCE_TMP + "_" + TMP_LAYOUT_NAME);
		}
	}

	@Override
	public void onFieldValueChanged(FormPortlet portlet, FormPortletField<?> field, Map<String, String> attributes) {
		if (field == this.outText) {
			AmiWebJsonDiffNode node = (AmiWebJsonDiffNode) this.activeTreeNode.getData();
			if (node != null && node.getUserChoice() == AmiWebJsonDiffNode.STATE_MANUAL) {
				node.setManualText(this.outText.getValue());
				this.treePortlet.getTreeManager().onNodeDataChanged(this.activeTreeNode);
			}
		} else if (field == this.outScriptText) {
			AmiWebJsonDiffNode node = (AmiWebJsonDiffNode) this.activeTreeNode.getData();
			if (node != null && node.getUserChoice() == AmiWebJsonDiffNode.STATE_MANUAL) {
				node.setManualText(toJson(this.outScriptText.getValue()));
				this.treePortlet.getTreeManager().onNodeDataChanged(this.activeTreeNode);
			}
		} else if (field == this.onlyChangesField) {
			buildTree();
		} else if (field == this.changeBackButtonField) {
			backButton();
		} else if (field == this.changeNextButtonField) {
			nextButton();
		} else if (field == this.conflictBackButtonField) {
			int val = this.conflictNumberField.getIntValue();
			if (val > 1)
				this.conflictNumberField.setValue(--val);
			AmiWebJsonDiffNode<?> node = this.conflicts.get(val - 1);
			int i = CH.indexOfIdentity(this.changes, node);
			this.changeNumberField.setValue(i + 1);
			updateSelected();
		} else if (field == this.conflictNextButtonField) {
			int val = this.conflictNumberField.getIntValue();
			if (val < this.conflicts.size())
				this.conflictNumberField.setValue(++val);
			AmiWebJsonDiffNode<?> node = this.conflicts.get(val - 1);
			int i = CH.indexOfIdentity(this.changes, node);
			this.changeNumberField.setValue(i + 1);
			updateSelected();
		} else if (field == this.changeNumberField) {
			updateSelected();
		}
	}

	private void backButton() {
		if (changes.size() == 0)
			return;
		int val = this.changeNumberField.getIntValue();
		if (val > 1)
			this.changeNumberField.setValue(val - 1);
		else
			this.changeNumberField.setValue(this.changes.size());
		updateSelected();
	}

	private void nextButton() {
		if (changes.size() == 0)
			return;
		int val = this.changeNumberField.getIntValue();
		if (val < this.changes.size())
			this.changeNumberField.setValue(val + 1);
		else
			this.changeNumberField.setValue(1);

		updateSelected();
	}

	private void updateSelected() {
		int val = this.changeNumberField.getIntValue();
		this.treePortlet.getTree().clearSelected();
		AmiWebJsonDiffNode<?> amiWebJsonDiffNode = this.changes.get(val - 1);
		WebTreeNode node = amiWebJsonDiffNode.getNode();
		node.setSelected(true);
		this.treePortlet.getTree().ensureVisible(node);
		int i = CH.indexOfIdentity(conflicts, amiWebJsonDiffNode);
		if (i != -1)
			this.conflictNumberField.setValue(i + 1);
	}

	@Override
	public void onSpecialKeyPressed(FormPortlet formPortlet, FormPortletField<?> field, int keycode, int mask, int cursorPosition) {
	}

	@Override
	public WebMenu createMenu(FastWebTree fastWebTree, List<WebTreeNode> selected) {
		if (selected.size() == 1) {
			AmiWebJsonDiffNode<?> dn = (AmiWebJsonDiffNode<?>) selected.get(0).getData();
			//			boolean uc = dn.isSame() != AmiWebJsonDiffNode.SAME_AL;
			byte c = dn.getUserChoice();
			BasicWebMenu r = new BasicWebMenu();
			if (dn.getSameness() == AmiWebJsonDiffNode.SAME_AL) {
				if (c == AmiWebJsonDiffNode.STATE_MANUAL)
					r.addChild(new BasicWebMenuLink("Revert Edit", true, "nochange"));
				else
					r.addChild(new BasicWebMenuLink("Edit", c != AmiWebJsonDiffNode.STATE_DISABLED, "manual"));
			} else {
				AbstractWebMenuItem yours = new BasicWebMenuLink("Yours", true, "right");
				AbstractWebMenuItem theirs = new BasicWebMenuLink("Theirs", true, "left");
				AbstractWebMenuItem base = new BasicWebMenuLink("Base", true, "orig");
				AbstractWebMenuItem edit = new BasicWebMenuLink("Edit", true, "manual");
				AbstractWebMenuItem choice;
				switch (dn.getUserChoice()) {
					case AmiWebJsonDiffNode.STATE_LEFT:
						choice = theirs;
						break;
					case AmiWebJsonDiffNode.STATE_RIGHT:
						choice = yours;
						break;
					case AmiWebJsonDiffNode.STATE_BASE:
						choice = base;
						break;
					case AmiWebJsonDiffNode.STATE_MANUAL:
						choice = edit;
						break;
					default:
						choice = null;
						break;
				}
				if (choice != null)
					choice.setCssStyle("_fm=bold|className=ami_menu_checked");
				r.addChild(yours);
				if (showLeft)
					r.addChild(theirs);
				r.addChild(base);
				r.addChild(edit);
			}
			r.addChild(new BasicWebMenuDivider());
			BasicWebMenu diffs = new BasicWebMenu("Compare...", true);
			diffs.add(new BasicWebMenuLink("Base vs. Result", true, "diff_rb"));
			diffs.add(new BasicWebMenuLink("Base vs. Yours", true, "diff_yb"));
			if (this.showLeft)
				diffs.add(new BasicWebMenuLink("Base vs. Theirs", true, "diff_tb"));
			if (this.showLeft)
				diffs.add(new BasicWebMenuLink("Theirs vs. Result", true, "diff_rt"));
			diffs.add(new BasicWebMenuLink("Yours vs. Result", true, "diff_ry"));
			if (this.showLeft)
				diffs.add(new BasicWebMenuLink("Theirs vs. Yours", true, "diff_ty"));
			r.addChild(diffs);
			BasicWebMenu export = new BasicWebMenu("Export...", true);
			export.add(new BasicWebMenuLink("Base", true, "export_b"));
			if (this.showLeft)
				export.add(new BasicWebMenuLink("Theirs", true, "export_t"));
			export.add(new BasicWebMenuLink("Yours", true, "export_y"));
			export.add(new BasicWebMenuLink("Result", true, "export_r"));
			r.addChild(export);
			r.add(new BasicWebMenuLink("Show Path", true, "path"));
			return r;
		}
		return null;
	}

	@Override
	public boolean formatNode(WebTreeNode node, StringBuilder sink) {
		return false;
	}

	public void addComparison(AmiWebLayoutFile t) {
		addComparison(t, true);
	}
	private void addComparison(AmiWebLayoutFile t, boolean isRoot) {
		String right = this.service.getLayoutFilesManager().toJson(t.buildCurrentJson(this.service));
		String left = Tuple2.getB(this.service.getLayoutFilesManager().loadLayoutData(t.getAbsoluteLocation(), t.getSource()));
		Object origJson = left == null ? null : fromJson(t.getRawTextFromDisk());
		Object leftJson = left == null ? null : fromJson(left);
		Object rightJson = right == null ? null : fromJson(right);
		String fal = t.getAbsoluteLocation();
		addComparison(fal == null ? null : (t.getSource() + ":" + fal), origJson, leftJson, rightJson, isRoot);
		for (AmiWebLayoutFile amiWebLayoutFile : t.getChildren()) {
			addComparison(amiWebLayoutFile, false);
		}
	}

	private void addComparison(String name, Object origJson, Object leftJson, Object rightJson, boolean isRoot) {
		if (isRoot)
			this.rootName = name;
		AmiWebJsonDiffNode<?> build = build("", null, origJson, leftJson, rightJson);
		this.roots.put(name, build);
		updateUserChoice(build, false);
	}

	@Override
	public boolean onButton(ConfirmDialog source, String id) {
		if ("SET_CHOICE".equals(source.getCallback())) {
			if (ConfirmDialogPortlet.ID_YES.contentEquals(id)) {
				Tuple2<AmiWebJsonDiffNode<?>, Byte> cd = (Tuple2<AmiWebJsonDiffNode<?>, Byte>) source.getCorrelationData();
				setChoice(cd.getA(), cd.getB());
			}
		}
		return true;
	}

	@Override
	public boolean onUserKeyEvent(KeyEvent keyEvent) {
		if (" ".equals(keyEvent.getKey())) {
			if (keyEvent.isShiftKey())
				backButton();
			else
				nextButton();
			return true;
		}
		return super.onUserKeyEvent(keyEvent);
	}

	public void setHasSaveButton(boolean hasSave) {
		if (hasSave == this.buttonsPortlet.hasButton(this.saveButton.getId()))
			return;
		if (hasSave)
			this.buttonsPortlet.addButton(this.saveButton);
		else
			this.buttonsPortlet.removeButtonNoThrow(this.saveButton);
	}

	public boolean showLeft() {
		return this.showLeft;
	}
}
