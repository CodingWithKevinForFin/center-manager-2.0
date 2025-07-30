package com.f1.ami.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.dm.AmiWebFormFieldVarLink;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTablePortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.tree.WebTreeColumnMenuFactory;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.tree.impl.WebTreeHelper;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Byte;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class AmiWebAmiObjectsVariablesPortlet extends GridPortlet implements AmiWebDomObjectDependency, WebTreeColumnMenuFactory, WebTreeNodeListener {
	private static final Logger log = LH.get();
	private AmiWebService service;
	private FastTreePortlet treePortlet;
	private WebTreeManager treeMgr;
	private String baseAlias;

	private static String CALLBACK_CUSTOM_FORMAT = "CUSTOM_FORMAT";
	private static String CALLBACK_TRIGGER_EVENT = "TRIGGER_EVENT";
	private static String CALLBACK_VARIABLE_NAME = "VARIABLE_NAME";
	public static byte TREEPANELS_ROOT_TYPE = 0;
	public static byte TREEPANELS_FORM_TYPE = 1;
	public static byte TREEPANELS_FIELDVAR_TYPE = 2;
	private HashMap<String, WebTreeNode> domObjectToNodes; // Ari To Nodes
	private HashMap<String, String> ariToVarName;
	private AmiWebAmiObjectsVariablesListener listener;
	private boolean isDatamodelColumnsEnabled = false;
	private WebTreeNode invalidRoot;
	private HashSet<String> invalidAris;

	protected static class AmiObjectVariable {
		public static String OPTION_FORMAT = "FORMAT";
		public static String OPTION_DOM_EVENT = "DOM_EVENT";
		private Map<String, Object> options;
		private String variableName;
		private String parentAri;
		private String fullAri;
		private boolean enabled;
		private boolean invalid;

		public AmiObjectVariable(String fullAri, String parentAri) {
			this.fullAri = fullAri;
			this.parentAri = parentAri;
			this.invalid = false;
		}

		public String getFullAri() {
			return this.fullAri;
		}
		public String getParentAri() {
			return this.parentAri;
		}
		public void setParentAri(String parentAri) {
			this.parentAri = parentAri;
		}
		public void setInvalid(boolean invalid) {
			this.invalid = invalid;
		}
		public boolean isInvalid() {
			return this.invalid;
		}
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enable) {
			this.enabled = enable;
		}

		public String getVariableName() {
			return this.variableName;
		}

		public void setVariableName(String variableName) {
			this.variableName = variableName;
		}

		public void clearOptions() {
			if (this.options != null)
				this.options.clear();
		}
		public void setOption(String key, Object value) {
			if (this.options == null)
				this.options = new HashMap<String, Object>();
			this.options.put(key, value);
		}
		public Object getOption(String key) {
			if (this.options == null)
				return null;
			else
				return this.options.get(key);
		}
		public Object removeOption(String key) {
			if (this.options.containsKey(key))
				return this.options.remove(key);
			else
				return null;
		}
		public Map<String, Object> getOptions() {
			return this.options;
		}
	}

	public AmiWebAmiObjectsVariablesPortlet(PortletConfig config, String baseAlias) {
		super(config);
		this.service = AmiWebUtils.getService(this.getManager());
		this.service.getDomObjectsManager().addGlobalListener(this);

		// Init the Tree Portlet and add it to the Grid.
		this.baseAlias = baseAlias;
		treePortlet = new FastTreePortlet(generateConfig());
		treePortlet.getTree().setColumnMenuFactory(this);
		treePortlet.getTree().setShowCheckUncheckOption(false);
		treeMgr = treePortlet.getTreeManager();
		treeMgr.addListener(this);
		//add default form and dialogue manager for treePortlet
		treePortlet.setFormStyle(AmiWebUtils.getService(getManager()).getUserFormStyleManager());
		treePortlet.setDialogStyle(AmiWebUtils.getService(getManager()).getUserDialogStyleManager());
		
		addChild(treePortlet, 0, 0);
		/*================================*/
		/* TreePortlet Options            */
		/*================================*/
		treePortlet.addOption(FastTablePortlet.OPTION_QUICK_COLUMN_FILTER_HIDDEN, false);
		treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		treePortlet.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=#ffffff");
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=#ffffff");
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, "#007608");
		/*================================*/
		//Init 
		/*================================*/
		//		WebTreeNode treeRoot = treeMgr.getRoot();
		FastWebTree fwt = treePortlet.getTree();

		/*================================*/
		//Add Columns
		/*================================*/
		FastWebTreeColumn col1;
		FastWebTreeColumn col2;
		fwt.setRootLevelVisible(false);
		fwt.getColumn(0).setColumnName("Enable");
		fwt.addColumnAt(true, col1 = new FastWebTreeColumn(1, new AmiObjectTypeFormatter(), "Category", "help", false), 0);
		fwt.addColumnAt(true, col2 = new FastWebTreeColumn(2, new AmiVariableNameFormatter(this.getPortletId()), "Variable Name", "help", false), 1);
		col2.setSelectable(false);

		/*================================*/
		//Add Nodes
		/*================================*/
		treeMgr.getRoot().setIsCascadeCheck(false);
		this.domObjectToNodes = new HashMap<String, WebTreeNode>();
		this.ariToVarName = new HashMap<String, String>();
		this.invalidAris = new HashSet<String>();
		//		this.invalidRoot = treeMgr.createNode("Invalid", this.treeMgr.getRoot(), true, null).setHasCheckbox(false).setIsCascadeCheck(false);
		this.invalidRoot = this.treeMgr.getRoot();
		buildTree(treeMgr.getRoot());
	}

	public void setEnableDatamodelColumns(boolean enabled) {
		if (this.isDatamodelColumnsEnabled == enabled)
			return;
		this.isDatamodelColumnsEnabled = enabled;
		FastWebTree fwt = this.treePortlet.getTree();
		if (this.isDatamodelColumnsEnabled == true) {
			FastWebTreeColumn col3;
			FastWebTreeColumn col4;
			fwt.addColumnAt(true, col3 = new FastWebTreeColumn(3, new AmiTriggerEventFormatter(this.getPortletId()), "Trigger Event", "help", false), 2);
			fwt.addColumnAt(true, col4 = new FastWebTreeColumn(4, new AmiCustomFormatFormatter(this.getPortletId()), "Custom Format", "help", false), 3);
			col3.setSelectable(false);
			col4.setSelectable(false);
		} else {
			fwt.removeColumn(3);
			fwt.removeColumn(4);
		}
	}
	@Override
	public void close() {
		this.service.getDomObjectsManager().removeGlobalListener(this);
		super.close();
	}

	public void initFromCallback(AmiWebAmiScriptCallback callback) {
		AmiWebAmiObjectsVariablesListener removedListener = this.listener;
		this.listener = null;
		// reset nodes
		for (WebTreeNode node : this.domObjectToNodes.values()) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			node.setChecked(false);
			r.clearOptions();
			r.variableName = null;
			r.enabled = false;
		}
		this.ariToVarName.clear();
		this.invalidAris.clear();
		// init the linked variables
		this.initLinkedVariables(callback.getVarNameToAriMap(), callback.getVariables());
		if (callback.hasDatamodel()) {
			Map<String, Integer> ariToCustomFormat = callback.getAriToCustomFormat();
			for (String ari : ariToCustomFormat.keySet()) {
				WebTreeNode node = this.domObjectToNodes.get(ari);
				if (!this.ariToVarName.containsKey(ari))
					continue;
				AmiObjectVariable r = (AmiObjectVariable) node.getData();
				r.setOption(AmiObjectVariable.OPTION_FORMAT, ariToCustomFormat.get(ari));
				this.treeMgr.onNodeDataChanged(node);
			}
			Map<String, Byte> ariToDomEvent = callback.getAriToDomEvent();
			for (String ari : ariToDomEvent.keySet()) {
				WebTreeNode node = this.domObjectToNodes.get(ari);
				if (!this.ariToVarName.containsKey(ari))
					continue;
				AmiObjectVariable r = (AmiObjectVariable) node.getData();
				r.setOption(AmiObjectVariable.OPTION_DOM_EVENT, ariToDomEvent.get(ari));
				this.treeMgr.onNodeDataChanged(node);
			}

		}
		this.listener = removedListener;
	}

	private void initLinkedVariables(Map<String, String> varNameToAri, Map<String, AmiWebDomObject> linkedVariables) {
		// Valid linked variables
		List<String> invalidVars = new ArrayList<String>();

		for (Map.Entry<String, String> entry : varNameToAri.entrySet()) {
			String variableName = entry.getKey();
			String ari = entry.getValue();

			this.ariToVarName.put(ari, variableName);
			WebTreeNode node = this.domObjectToNodes.get(ari);
			if (node == null) {
				this.invalidAris.add(ari);
				invalidVars.add(variableName);
				continue;
			}

			//Set the node to checked
			node.setCheckedNoFire(true);
			//Set the variable name
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			r.setEnabled(true);
			r.setVariableName(variableName);
			this.treeMgr.onNodeDataChanged(node);
		}
		//Invalid Variables
		if (invalidVars.size() > 0) {
			for (int i = 0; i < invalidVars.size(); i++) {
				String varName = invalidVars.get(i);
				String ari = varNameToAri.get(varName);

				WebTreeNode node = this.addNodeToTree(this.invalidRoot, ari, varName, false);

			}
			this.invalidAris.addAll(invalidVars);
		}

	}

	private void buildTree(WebTreeNode parent) {
		for (AmiWebDomObject o : this.service.getChildDomObjects())
			this.buildTree(parent, o);
		this.autoSizeTreeColumn();
	}
	private void autoSizeTreeColumn() {
		this.treePortlet.getTree().autoSizeColumn(0, this.getManager().getPortletMetrics());
	}
	private void buildTree(WebTreeNode parent, AmiWebDomObject parentDomObj) {
		if (parentDomObj.isTransient())
			return;
		String objLayoutAlias = parentDomObj.getAmiLayoutFullAlias();
		if (objLayoutAlias == null) {
			LH.warning(log, "AmiWebDomObject had null layout alias: " + parentDomObj.getAri());
			parentDomObj.getAmiLayoutFullAlias();
			return;
		}
		if (!AmiWebUtils.isParentAliasOrSame(this.baseAlias, objLayoutAlias)) {
			return;
		}

		WebTreeNode newNode = this.addNodeToTree(parent, parentDomObj.getAri(), parentDomObj.getDomLabel(), true);

		List<AmiWebDomObject> childDomObjects = parentDomObj.getChildDomObjects();
		if (childDomObjects == null)
			return;
		for (int i = 0; i < childDomObjects.size(); i++) {
			AmiWebDomObject childDomObj = childDomObjects.get(i);
			this.buildTree(newNode, childDomObj);
		}
	}
	private WebTreeNode addNodeToTree(WebTreeNode parent, String ari, String label, boolean isValid) {
		String parentAri = null;
		AmiObjectVariable parentVar = (AmiObjectVariable) parent.getData();
		if (parentVar != null)
			parentAri = parentVar.getFullAri();
		if (isValid == false)
			label = "(Invalid) " + label;
		WebTreeNode newNode = treeMgr.createNode(label, parent, true, new AmiObjectVariable(ari, parentAri)).setHasCheckbox(true).setIsCascadeCheck(false);
		newNode.setIconCssStyle(AmiWebAmiObjectsVariablesHelper.getAmiIconStyleForDomObjectType(ari));
		this.domObjectToNodes.put(ari, newNode);
		if (this.ariToVarName.containsKey(ari)) {
			AmiObjectVariable r = (AmiObjectVariable) newNode.getData();
			r.setEnabled(true);
			r.setVariableName(this.ariToVarName.get(ari));
			r.setInvalid(!isValid);
			newNode.setCheckedNoFire(true);
			if (isValid && this.invalidAris.contains(ari))
				this.invalidAris.remove(ari);
		}

		this.treeMgr.onNodeDataChanged(newNode);
		return newNode;
	}

	private AmiObjectVariable getObjectVariable(WebTreeNode node) {
		return (AmiObjectVariable) node.getData();
	}

	private AmiWebDomObject getDomObject(AmiObjectVariable v) {
		//		return this.service.getDomObjectsManager().getManagedDomObject(v.getFullAri());
		return AmiWebAmiObjectsVariablesHelper.getAmiWebDomObjectFromFullAri(v.getFullAri(), service);
	}

	public void setBaseAlias(String value) {
		if (OH.eq(this.baseAlias, value))
			return;
		this.baseAlias = value;
		WebTreeNode root = this.treeMgr.getRoot();
		List<WebTreeNode> sink = new ArrayList<WebTreeNode>();
		WebTreeHelper.getAllChildren(root, sink);
		for (WebTreeNode child : sink) {
			treeMgr.removeNode(child);
		}
		this.buildTree(this.treeMgr.getRoot());
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if (this.listener != null)
			if (SH.equals("treePanelsTgtVarName", callback)) { //Rename variable
				int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
				String varname = CH.getOrThrow(Caster_String.INSTANCE, attributes, "val");
				WebTreeNode node = treeMgr.getTreeNode(uid);
				AmiObjectVariable r = (AmiObjectVariable) node.getData();
				r.setVariableName(varname);
			} else if (CALLBACK_CUSTOM_FORMAT.contentEquals(callback)) {
				int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
				int format = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "val");
				WebTreeNode node = treeMgr.getTreeNode(uid);
				AmiObjectVariable r = (AmiObjectVariable) node.getData();

				r.setOption(AmiObjectVariable.OPTION_FORMAT, format);
			} else if (CALLBACK_TRIGGER_EVENT.contentEquals(callback)) {
				int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
				byte triggerEvent = CH.getOrThrow(Caster_Byte.PRIMITIVE, attributes, "val");
				WebTreeNode node = treeMgr.getTreeNode(uid);
				AmiObjectVariable r = (AmiObjectVariable) node.getData();

				r.setOption(AmiObjectVariable.OPTION_DOM_EVENT, triggerEvent);
			}
	}

	private void addVariable(WebTreeNode node) {
		AmiObjectVariable r = (AmiObjectVariable) node.getData();
		if (r == null)
			return;
		String varName = r.getVariableName();
		if (varName == null) {
			AmiWebDomObject domObject = getDomObject(r);
			if (domObject != null) {
				varName = AmiWebUtils.toSuggestedVarname(domObject.getDomLabel());
			} else
				varName = "value";
			varName = this.getNextVariableName(varName);
		} else if (!r.isInvalid())
			varName = this.getNextVariableName(varName);

		r.setEnabled(true);
		r.setVariableName(varName);
		this.ariToVarName.put(r.getFullAri(), r.getVariableName());

		Map<String, Object> options = r.getOptions();
		if (options != null)
			for (String key : options.keySet()) {
				r.setOption(key, options.get(key));
			}
	}
	private void removeVariable(WebTreeNode node) {
		AmiObjectVariable r = (AmiObjectVariable) node.getData();
		if (r == null)
			return;
		r.setEnabled(false);
		this.ariToVarName.remove(r.getFullAri());
	}

	@Override
	public void onCheckedChanged(WebTreeNode node) {
		if (this.listener != null) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			String fullAri = r.getFullAri();
			if (node.getChecked() == true) {
				if (!this.ariToVarName.containsKey(fullAri))
					this.addVariable(node);
			} else {
				if (this.ariToVarName.containsKey(fullAri))
					this.removeVariable(node);
			}
		}
	}

	private class AmiObjectTypeFormatter implements WebTreeNodeFormatter {
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (r.isInvalid()) {
				style.append("_bg=rgb(255 116 116 / 0.5)");
			}
			sink.append(AmiWebAmiObjectsVariablesHelper.getAriType(r.getFullAri()));
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			AmiObjectVariable r1 = (AmiObjectVariable) o1.getData();
			if (r1 == null)
				return -1;
			AmiObjectVariable r2 = (AmiObjectVariable) o2.getData();
			if (r2 == null)
				return 1;
			AmiWebDomObject dom1 = (AmiWebDomObject) getDomObject(r1);
			AmiWebDomObject dom2 = (AmiWebDomObject) getDomObject(r2);
			int b = OH.compare(dom1.getAriType(), dom2.getAriType());
			return b;
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			AmiWebDomObject dom = getDomObject(r);
			if (dom == null)
				return;
			sink.append(dom.getAriType());

		}
		@Override
		public Object getValue(WebTreeNode node) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return null;
			AmiWebDomObject dom = getDomObject(r);
			if (dom == null)
				return null;
			return dom.getAriType();
		}
		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}
		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	private class AmiObjectNameFormatter implements WebTreeNodeFormatter {
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (r.getFullAri() == null)
				return;
			sink.append(getDomObject(r).getDomLabel());
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			AmiObjectVariable r1 = (AmiObjectVariable) o1.getData();
			AmiObjectVariable r2 = (AmiObjectVariable) o2.getData();
			return OH.compare(getDomObject(r1).getDomLabel(), getDomObject(r2).getDomLabel());
		}
		@Override
		public Object getValue(WebTreeNode node) {
			return getDomObject((AmiObjectVariable) node.getData()).getDomLabel();
		}
		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValueDisplay(node);
		}
		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	private class AmiTriggerEventFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public AmiTriggerEventFormatter(String pid) {
			this.pid = pid;
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (r.isEnabled() == false)
				return;
			AmiWebDomObject obj = getDomObject(r);
			if (obj == null || !(obj instanceof AmiWebQueryFieldDomValue))
				return;
			if (!r.isInvalid())
				this.format(obj, node.getUid(), this.getTriggerEvent(r), sink);
			else {
				sink.append(AmiWebAmiObjectsVariablesHelper.getDomEventNameForType(this.getTriggerEvent(r)));
			}

		}
		private byte getTriggerEvent(AmiObjectVariable r) {
			AmiWebDomObject obj = getDomObject(r);
			if (obj == null || !(obj instanceof AmiWebQueryFieldDomValue))
				return AmiWebDomObject.DOM_EVENT_CODE_NONE;
			Byte trigger = (Byte) r.getOption(AmiObjectVariable.OPTION_DOM_EVENT);
			if (trigger == null)
				return AmiWebDomObject.DOM_EVENT_CODE_NONE;
			return trigger;
		}

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			AmiObjectVariable r1 = (AmiObjectVariable) o1.getData();
			if (r1 == null)
				return -1;
			AmiObjectVariable r2 = (AmiObjectVariable) o2.getData();
			if (r2 == null)
				return 1;
			byte fm1 = this.getTriggerEvent(r1);
			byte fm2 = this.getTriggerEvent(r2);
			return OH.compare(AmiWebAmiObjectsVariablesHelper.getDomEventNameForType(fm1), AmiWebAmiObjectsVariablesHelper.getDomEventNameForType(fm2));
		}

		private void format(AmiWebDomObject object, int nodeUid, int triggerEventCode, StringBuilder sink) {
			sink.append("<div class='ami_treepanels ami_treepanels_formatter'><select onchange='var that=this;new function(){TPCallback(that,");
			SH.doubleQuote(this.pid, sink).append(',');
			SH.doubleQuote(CALLBACK_TRIGGER_EVENT, sink).append(',');
			sink.append(nodeUid);
			sink.append(")}'> ");
			for (int i = 0; i < AmiWebDomObject.DOM_EVENTS.length; i++) {
				byte event = AmiWebDomObject.DOM_EVENTS[i];
				String eventName = AmiWebAmiObjectsVariablesHelper.getDomEventNameForType(event);
				sink.append("<option value=").append(event);
				if (event == triggerEventCode)
					sink.append(" selected");
				sink.append(">").append(eventName).append("</option>");

			}
			sink.append("</select></div>");
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiObjectVariable r1 = (AmiObjectVariable) node.getData();
			if (r1 == null)
				return -1;
			byte fm1 = this.getTriggerEvent(r1);
			return AmiWebAmiObjectsVariablesHelper.getDomEventNameForType(fm1);
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	private class AmiCustomFormatFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public AmiCustomFormatFormatter(String pid) {
			this.pid = pid;
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (r.isEnabled() == false)
				return;
			AmiWebDomObject obj = getDomObject(r);
			if (obj == null || !(obj instanceof AmiWebQueryFieldDomValue))
				return;
			if (!r.isInvalid())
				this.format(obj, node.getUid(), this.getFormat(r), sink);
			else {
				String formatters[] = AmiWebFormFieldVarLink.getFormattersForField(obj);
				int format = this.getFormat(r);
				sink.append(formatters[format]);
			}
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		private int getFormat(AmiObjectVariable r) {
			AmiWebDomObject obj = getDomObject(r);
			if (obj == null || !(obj instanceof AmiWebQueryFieldDomValue))
				return AmiWebFormFieldVarLink.INVALID;
			Integer format = (Integer) r.getOption(AmiObjectVariable.OPTION_FORMAT);
			if (format == null)
				return AmiWebFormFieldVarLink.getFormatterIdForFormatter(AmiWebFormFieldVarLink.NONE);
			return format;
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			AmiObjectVariable r1 = (AmiObjectVariable) o1.getData();
			if (r1 == null)
				return -1;
			AmiObjectVariable r2 = (AmiObjectVariable) o2.getData();
			if (r2 == null)
				return 1;
			int fm1 = this.getFormat(r1);
			int fm2 = this.getFormat(r2);
			if (fm1 == AmiWebFormFieldVarLink.INVALID || fm2 == AmiWebFormFieldVarLink.INVALID)
				return OH.compare(fm1, fm2);
			else
				return OH.compare(AmiWebFormFieldVarLink.formatterMapNameToId.getKey(fm1), AmiWebFormFieldVarLink.formatterMapNameToId.getKey(fm2));
		}

		private void format(AmiWebDomObject object, int nodeUid, int formatterId, StringBuilder sink) {
			if (formatterId == AmiWebFormFieldVarLink.INVALID)
				return;
			sink.append("<div class='ami_treepanels ami_treepanels_formatter'><select onchange='var that=this;new function(){TPCallback(that,");
			SH.doubleQuote(this.pid, sink).append(',');
			SH.doubleQuote(CALLBACK_CUSTOM_FORMAT, sink).append(',');
			sink.append(nodeUid);
			sink.append(")}'> ");
			String formatters[] = AmiWebFormFieldVarLink.getFormattersForField(object);
			for (int i = 0; i < formatters.length; i++) {
				int id = AmiWebFormFieldVarLink.getFormatterIdForFormatter(formatters[i]);
				sink.append("<option value=").append(id);
				if (id == formatterId)
					sink.append(" selected");
				sink.append(">").append(formatters[i]).append("</option>");
			}
			sink.append("</select></div>");
		}

		@Override
		public Object getValue(WebTreeNode node) {
			AmiObjectVariable o = (AmiObjectVariable) node.getData();
			int f = this.getFormat(o);
			return AmiWebFormFieldVarLink.formatterMapNameToId.getKey(f);
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	private class AmiVariableNameFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public AmiVariableNameFormatter(String pid) {
			this.pid = pid;
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (node.getChecked()) {
				if (!r.isInvalid()) {
					sink.append(
							"<div class='ami_treepanels ami_treepanels_varname'><div class='disable_glass_clear'></div><input type='text' oninput='var that=this;new function(){TPvarname(that,");
					SH.doubleQuote(this.pid, sink).append(',');
					sink.append(node.getUid());
					sink.append(")}' value='");
					sink.append(r.getVariableName());
					sink.append("' ");
					sink.append("></div>");
				} else {
					sink.append(r.getVariableName());
				}

			}
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			AmiObjectVariable r = (AmiObjectVariable) node.getData();
			if (r == null)
				return;
			if (node.getChecked()) {
				sink.append(r.getVariableName());
			}
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			AmiObjectVariable r1 = (AmiObjectVariable) o1.getData();
			if (r1 == null)
				return -1;
			AmiObjectVariable r2 = (AmiObjectVariable) o2.getData();
			if (r2 == null)
				return 1;
			return OH.compare(r1.getVariableName(), r2.getVariableName());
		}
		@Override
		public Object getValue(WebTreeNode node) {
			AmiObjectVariable r1 = (AmiObjectVariable) node.getData();
			if (r1 == null)
				return null;
			return r1.getVariableName();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	@Override
	public WebMenu createColumnMenu(FastWebTree tree, FastWebTreeColumn column, WebMenu defaultMenu) {
		return new BasicWebMenu();
	}

	@Override
	public void onNodeAdded(WebTreeNode node) {
	}

	@Override
	public void onNodeRemoved(WebTreeNode node) {
	}

	@Override
	public void onStyleChanged(WebTreeNode node) {
	}

	@Override
	public void onExpanded(WebTreeNode node) {
		this.autoSizeTreeColumn();
	}

	public AmiWebAmiObjectsVariablesListener getListener() {
		return listener;
	}

	public void setListener(AmiWebAmiObjectsVariablesListener listener) {
		this.listener = listener;
	}

	@Override
	public void onDomObjectAriChanged(AmiWebDomObject target, String oldAri) {
		String newAri = target.getAri();

		//Both the old and new are in the tree checked
		if (this.ariToVarName.containsKey(oldAri) && this.ariToVarName.containsKey(newAri)) {
			if (this.invalidAris.contains(newAri)) {
				String varName = this.ariToVarName.remove(oldAri);
				this.ariToVarName.remove(newAri);
				this.ariToVarName.put(newAri, varName);
			} else if (this.invalidAris.contains(oldAri)) {
				this.ariToVarName.remove(oldAri);
			}
			boolean isValid = this.isValidObjectForTree(target);
			if (this.domObjectToNodes.containsKey(oldAri))
				this.removeDomObjectHelper(target, oldAri, false);
			if (this.domObjectToNodes.containsKey(newAri))
				this.removeDomObjectHelper(target, newAri, false);
			this.addDomObjectHelper(target, isValid);

			this.autoSizeTreeColumn();
		} else if (this.ariToVarName.containsKey(oldAri)) {
			//Only the old key is in the tree checked
			String varName = this.ariToVarName.remove(oldAri);
			if (varName != null) {
				this.ariToVarName.put(newAri, varName);
			}
			boolean isValid = this.isValidObjectForTree(target);
			if (this.domObjectToNodes.containsKey(oldAri))
				this.removeDomObjectHelper(target, oldAri, false);
			if (this.domObjectToNodes.containsKey(newAri))
				this.removeDomObjectHelper(target, newAri, false);
			this.addDomObjectHelper(target, isValid);

			this.autoSizeTreeColumn();
		} else if (this.ariToVarName.containsKey(newAri)) {
			//The new key is in the tree checked
			boolean isValid = this.isValidObjectForTree(target);
			if (this.domObjectToNodes.containsKey(oldAri))
				this.removeDomObjectHelper(target, oldAri, false);
			if (this.domObjectToNodes.containsKey(newAri))
				this.removeDomObjectHelper(target, newAri, false);
			this.addDomObjectHelper(target, isValid);

			this.autoSizeTreeColumn();
		} else {
			if (this.domObjectToNodes.containsKey(oldAri))
				this.removeDomObjectHelper(target, oldAri, false);
			if (!this.domObjectToNodes.containsKey(target.getAri())) {
				boolean isValid = this.isValidObjectForTree(target);
				this.addDomObjectHelper(target, isValid);
			}
		}

	}

	@Override
	public void onDomObjectEvent(AmiWebDomObject object, byte eventType) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onDomObjectRemoved(AmiWebDomObject object) {
		String ari = object.getAri();
		if (this.domObjectToNodes.containsKey(ari)) {
			this.removeDomObjectHelper(object, ari, true);
			this.autoSizeTreeColumn();
		}
	}

	private void removeDomObjectHelper(String parentAri, String ari, boolean autoAddToInvalidRoot) {
		//Will remove object and child objects
		WebTreeNode node = this.domObjectToNodes.get(ari);

		//Remove ari from used aris
		if (node.getChildrenCount() > 0) {
			List<WebTreeNode> l = CH.l(node.getChildren());
			for (WebTreeNode child : l) {
				AmiObjectVariable var = (AmiObjectVariable) child.getData();
				this.removeDomObjectHelper(var.getParentAri(), var.getFullAri(), autoAddToInvalidRoot);
			}
		}

		WebTreeNode parent = null;
		// Get Parent Node
		if (((AmiObjectVariable) node.getData()).isInvalid()) {
			parent = this.invalidRoot;
		} else {
			parent = this.domObjectToNodes.get(parentAri);
		}
		if (parent != null)
			parent.removeChild(node);

		//Remove node
		if (this.ariToVarName.containsKey(ari)) {
			this.ariToVarName.put(ari, getObjectVariable(node).getVariableName()); // update the variable name with last filled value
			this.invalidAris.add(ari);
			getObjectVariable(node).setInvalid(true);
			node.setName("(Invalid) " + getObjectVariable(node).getFullAri());
			this.treeMgr.onNodeDataChanged(node);
			if (autoAddToInvalidRoot == true)
				this.invalidRoot.addChild(node);
			else
				this.domObjectToNodes.remove(ari);
		} else
			this.domObjectToNodes.remove(ari);

	}
	private void removeDomObjectHelper(AmiWebDomObject object, String ari, boolean autoAddToInvalidRoot) {
		WebTreeNode node = this.domObjectToNodes.get(ari);
		AmiObjectVariable var = (AmiObjectVariable) node.getData();

		this.removeDomObjectHelper(var.getParentAri(), ari, autoAddToInvalidRoot);
	}

	@Override
	public void onDomObjectAdded(AmiWebDomObject object) {
		if (object.isTransient())
			return;
		if (this.invalidAris.contains(object.getAri())) {
			boolean isValid = this.isValidObjectForTree(object);
			this.removeDomObjectHelper(object, object.getAri(), false);
			this.addDomObjectHelper(object, isValid);
			this.autoSizeTreeColumn();
		}
		if (!this.domObjectToNodes.containsKey(object.getAri())) {
			boolean isValid = this.isValidObjectForTree(object);
			this.addDomObjectHelper(object, isValid);
			this.autoSizeTreeColumn();
		}
	}
	//Recursively checks the tree to see if an object is valid
	private boolean isValidObjectForTree(AmiWebDomObject object) {
		AmiWebDomObject parent = object.getParentDomObject();
		// If parent doesn't exist it's invalid
		if (parent == null)
			return false;
		WebTreeNode node = this.domObjectToNodes.get(parent.getAri());

		if (node != null) {
			AmiObjectVariable var = (AmiObjectVariable) node.getData();
			// If the node is invalid, check the parent 
			if (var.isInvalid())
				return this.isValidObjectForTree(parent);
			else
				return true;
		} else
			// If the node doesn't exist have to check the parent object
			return this.isValidObjectForTree(parent);

	}

	//Recursively adds nodes parents if they aren't already added
	private WebTreeNode addDomObjectHelper(AmiWebDomObject object, boolean isValid) {
		AmiWebDomObject parentDomObject = object.getParentDomObject();
		String ari = parentDomObject.getAri();
		WebTreeNode parent = this.domObjectToNodes.get(ari);
		if (parent == null) {
			parent = addDomObjectHelper(parentDomObject, isValid);
		}

		WebTreeNode newNode = this.addNodeToTree(parent, object.getAri(), object.getDomLabel(), isValid);
		return newNode;
	}

	@Override
	public void initLinkedVariables() {
	}

	private String getNextVariableName(String varname) {
		HashSet<String> usedVariableNames = new HashSet<String>();
		this.listener.getUsedVariableNames(usedVariableNames);
		Map<String, String> ariToVariableNameMap = getAriToVariableNameMap();
		usedVariableNames.addAll(ariToVariableNameMap.values());

		return SH.getNextId(varname, usedVariableNames);
	}

	public Set<Entry<String, String>> getVariableNameToAriEntrySet() {
		return getVariableNameToAriEntrySet(this.treeMgr.getRoot(), new HashSet<Map.Entry<String, String>>());
	}
	private Set<Entry<String, String>> getVariableNameToAriEntrySet(WebTreeNode node, Set<Entry<String, String>> sink) {
		if (node.getChecked() == true) {
			AmiObjectVariable data = (AmiObjectVariable) node.getData();
			if (data != null) {
				Tuple2<String, String> entry = new Tuple2<String, String>(data.getVariableName(), data.getFullAri());
				sink.add(entry);
			}
		}
		for (WebTreeNode i : node.getChildren()) {
			getVariableNameToAriEntrySet(i, sink);
		}
		return sink;
	}

	public Map<String, String> getAriToVariableNameMap() {
		return getAriToVariableNameMap(this.treeMgr.getRoot(), new HashMap<String, String>());
	}
	private Map<String, String> getAriToVariableNameMap(WebTreeNode node, Map<String, String> sink) {
		if (node.getChecked() == true) {
			AmiObjectVariable data = (AmiObjectVariable) node.getData();
			if (data != null && data.isEnabled()) {
				sink.put(data.getFullAri(), data.getVariableName());
			}
		}
		for (WebTreeNode i : node.getChildren()) {
			getAriToVariableNameMap(i, sink);
		}
		return sink;

	}
	public boolean validateVariables() {
		Map<String, String> ariToVarName = getAriToVariableNameMap();
		HashSet<String> usedVariableNames = new HashSet<String>();
		HashSet<String> duplicateVariables = new HashSet<String>();
		HashSet<String> invalidVariableNames = new HashSet<String>();
		for (Entry<String, String> e : ariToVarName.entrySet()) {
			String varname = e.getValue();
			if (!usedVariableNames.add(varname))
				duplicateVariables.add(varname);
			//			String validVarname = AmiUtils.toValidVarName(varname);
			if (!AmiUtils.isValidVariableName(varname, false, false))
				invalidVariableNames.add(varname);

		}
		if (duplicateVariables.size() > 0) {
			getManager().showAlert("Callback contains duplicate variable names: " + duplicateVariables);
			return false;
		}
		if (invalidVariableNames.size() > 0) {
			getManager().showAlert("Callback contains invalid variable names: " + invalidVariableNames);
			return false;
		}

		if (this.listener != null) {
			//If failed hasError = true
			if (!this.applyVariables(this.treeMgr.getRoot()))
				return false;
		}
		return true;
	}
	private boolean applyVariables(WebTreeNode node) {
		boolean hasError = false;
		if (node.getChecked() == true) {
			AmiObjectVariable data = (AmiObjectVariable) node.getData();
			if (data != null) {
				String variableName = data.getVariableName();
				String fullAri = data.getFullAri();
				try {
					this.listener.onVariableAdded(variableName, fullAri);
					Map<String, Object> options = data.getOptions();
					if (options != null) {
						for (Entry<String, Object> e : options.entrySet()) {
							this.listener.onVariableUpdateOption(variableName, e.getKey(), e.getValue());
						}
					}
				} catch (Exception e) {
					getManager().showAlert("Callback failed to add variable: " + variableName + " with ARI: " + fullAri, e);
					//					LH.warning(log, "Callback failed to add variable: " + variableName + " with ARI: " + fullAri + e);
					return false;
				}
			}
		}

		for (WebTreeNode i : node.getChildren()) {
			if (!applyVariables(i))
				return false;
		}
		for (WebTreeNode i : node.getFilteredChildren()) {
			if (!applyVariables(i))
				return false;
		}
		return true;
	}

	@Override
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered) {
	}

}
