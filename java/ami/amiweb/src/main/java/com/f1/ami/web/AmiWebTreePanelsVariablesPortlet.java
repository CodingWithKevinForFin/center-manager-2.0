package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.f1.ami.web.dm.AmiWebFormFieldVarLink;
import com.f1.ami.web.form.AmiWebQueryFormPortlet;
import com.f1.ami.web.form.queryfield.QueryField;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.tree.WebTreeColumnMenuFactory;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.Tuple2;

public class AmiWebTreePanelsVariablesPortlet extends GridPortlet implements WebTreeColumnMenuFactory {
	private FastTreePortlet treePortlet;
	private WebTreeManager treeMgr;
	private String baseAlias;

	public static byte TREEPANELS_ROOT_TYPE = 0;
	public static byte TREEPANELS_FORM_TYPE = 1;
	public static byte TREEPANELS_FIELDVAR_TYPE = 2;

	public HashMap<String, String> pnlAliasDotNameToVariable = new HashMap<String, String>();
	public HashMap<String, String> dmAliasDotNameToVariable = new HashMap<String, String>();
	public HashMap<String, String> relAliasDotNameToVariable = new HashMap<String, String>();
	public HashMap<String, String> fieldsAliasDotNameToVariable = new HashMap<String, String>();
	public HashSet<String> usedVariableNames = new HashSet<String>();

	protected static class TreePanelsRow {
		byte type;
		Object object;
		Object secondaryObject;
		boolean rerunDmOnChange;
		String uniqueObjectId;
		String objectAlias;
		String variableName;
		int formatterId;
		Formatter formatter;

		public TreePanelsRow(byte type, Object object, Object secondaryObject, boolean rerunDmOnChange, String name, int formatterId) {
			this.type = type;
			this.object = object;
			this.secondaryObject = secondaryObject;
			this.rerunDmOnChange = rerunDmOnChange;
			this.formatterId = formatterId;
			this.objectAlias = name;
			this.variableName = name;
		}
		public static TreePanelsRow createRootRow() {
			return new TreePanelsRow(TREEPANELS_ROOT_TYPE, null, null, false, null, -1);
		}
		public static TreePanelsRow createFormRow(AmiWebQueryFormPortlet fp) {
			return new TreePanelsRow(TREEPANELS_FORM_TYPE, fp, null, false, null, -1);
		}
		public static TreePanelsRow createFieldVariableRow(QueryField<?> field, Integer varnameIndex) {
			return new TreePanelsRow(TREEPANELS_FIELDVAR_TYPE, field, varnameIndex, false, field.getVarNameAt(varnameIndex), 0);
		}
	}

	public AmiWebTreePanelsVariablesPortlet(PortletConfig config, String baseAlias) {
		super(config);

		// Init the Tree Portlet and add it to the Grid.
		this.baseAlias = baseAlias;
		treePortlet = new FastTreePortlet(generateConfig());
		treePortlet.getTree().setColumnMenuFactory(this);
		treeMgr = treePortlet.getTreeManager();
		addChild(treePortlet, 0, 0);
		/*================================*/
		/* TreePortlet Options            */
		/*================================*/
		treePortlet.addOption(FastTreePortlet.OPTION_SEARCH_BUTTONS_COLOR, "#007608");
		treePortlet.addOption(FastTreePortlet.OPTION_GRIP_COLOR, "_bg=#ffffff");
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_BUTTON_COLOR, "_bg=#ffffff");
		treePortlet.addOption(FastTreePortlet.OPTION_SCROLL_ICONS_COLOR, "#007608");
		this.getAmiWebQueryFormPortlets();
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		String action = CH.getOrThrow(Caster_String.INSTANCE, attributes, "type");
		if (action.equals("treePanelsRerunDmOnChange")) {
			int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
			boolean value = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "val");
			WebTreeNode node = treeMgr.getTreeNode(uid);
			if (node.getChecked() == false)
				return;
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r.rerunDmOnChange != value) {
				r.rerunDmOnChange = value;
			}
		} else if (action.equals("treePanelsTgtVarName")) {
			int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
			String value = CH.getOrThrow(Caster_String.INSTANCE, attributes, "val");
			WebTreeNode node = treeMgr.getTreeNode(uid);
			if (node.getChecked() == false)
				return;
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r.variableName != value) {
				r.variableName = value;
			}
		} else if (action.equals("treePanelsFormatter")) {
			int uid = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "uid");
			int value = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "val");
			WebTreeNode node = treeMgr.getTreeNode(uid);
			if (node.getChecked() == false)
				return;
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r.formatterId != value) {
				r.formatterId = value;
			}
		} else
			super.handleCallback(callback, attributes);
	}

	public void initVariables(List<AmiWebFormFieldVarLink> configuration) {
		if (configuration == null)
			return;

		WebTreeNode root = treeMgr.getRoot();
		Iterable<WebTreeNode> panelsNodes = root.getChildren();
		for (WebTreeNode panelNode : panelsNodes) {
			Iterable<WebTreeNode> fieldVarNodes = panelNode.getChildren();
			for (WebTreeNode fieldVarNode : fieldVarNodes) {
				fieldVarNode.setChecked(false);
			}
		}

		for (AmiWebFormFieldVarLink link : configuration) {
			WebTreeNode panelNode = root.getChildByKey(link.getFormFullAliasDotName());
			if (panelNode == null)
				continue;
			WebTreeNode fieldVarNode = panelNode.getChildByKey(link.getSourceVarname());
			if (fieldVarNode == null)
				continue;
			else {
				TreePanelsRow row = (TreePanelsRow) fieldVarNode.getData();
				row.rerunDmOnChange = link.isRerunDmOnChange();
				row.variableName = link.getTargetVarname();
				row.formatterId = link.getFormatterId();
				fieldVarNode.setChecked(true);
			}
		}
	}
	public List<AmiWebFormFieldVarLink> getVariablesConfiguration() {
		List<AmiWebFormFieldVarLink> r = new ArrayList<AmiWebFormFieldVarLink>();
		List<WebTreeNode> checkedNodes = treePortlet.getTree().getChecked(false);
		for (int i = 0; i < checkedNodes.size(); i++) {
			WebTreeNode node = checkedNodes.get(i);
			if (node.getChecked() == false)
				continue;
			TreePanelsRow row = (TreePanelsRow) node.getData();
			if (row.type != TREEPANELS_FIELDVAR_TYPE)
				continue;

			//Data for fieldvar
			QueryField<?> field = (QueryField<?>) row.object;
			String sourceVarname = field.getVarNameAt((Integer) row.secondaryObject);
			AmiWebQueryFormPortlet form = (AmiWebQueryFormPortlet) ((TreePanelsRow) node.getParent().getData()).object;
			r.add(new AmiWebFormFieldVarLink(sourceVarname, row.rerunDmOnChange, row.variableName, row.formatterId, form.getPortletId(), form.getAmiLayoutFullAliasDotId()));
		}
		return r;
	}
	public List<Tuple2<String, Class>> getFieldVarsNameTypeForAutoComplete() {
		List<Tuple2<String, Class>> r = new ArrayList<Tuple2<String, Class>>();

		AmiWebService service = AmiWebUtils.getService(getManager());
		List<AmiWebFormFieldVarLink> links = this.getVariablesConfiguration();
		for (int i = 0; i < links.size(); i++) {
			AmiWebFormFieldVarLink link = links.get(i);

			//Get QueryFormPortlet
			AmiWebAliasPortlet pnl = service.getPortletByAliasDotPanelId(link.getFormFullAliasDotName());
			if (!(pnl instanceof AmiWebQueryFormPortlet))
				continue;
			AmiWebQueryFormPortlet form = (AmiWebQueryFormPortlet) pnl;

			//Get Field
			QueryField<?> field = form.getFieldByVarName(link.getSourceVarname());
			if (field == null)
				continue;
			int pos = field.getVarPosition(link.getSourceVarname());
			if (pos != -1) {
				Class clazz = field.getVarTypeAt(pos);
				switch (link.getFormatterId()) {
					case 0: {
						break;
					}
					case 1: {
						clazz = String.class;
						break;
					}
					case 2: {
						clazz = String.class;
						break;
					}
					case 3: {
						clazz = String.class;
						break;
					}
					case 4: {
						clazz = String.class;
						break;
					}
					default: {
						break;
					}
				}
				r.add(new Tuple2<String, Class>(link.getTargetVarname(), clazz));
			}

		}
		return r;
	}
	public void getAmiWebQueryFormPortlets() {
		/*================================*/
		//Init 
		/*================================*/
		WebTreeNode treeRoot = treeMgr.getRoot();
		FastWebTree fwt = treePortlet.getTree();
		String pid = this.getPortletId();

		/*================================*/
		//Add Columns
		/*================================*/
		FastWebTreeColumn col1;
		FastWebTreeColumn col2;
		FastWebTreeColumn col3;
		fwt.getColumn(0).setColumnName("Available Variables");
		fwt.addColumnAt(true, col1 = new FastWebTreeColumn(1, new RerunDmOnChangeFormatter(this), "Trigger Rerun", "help", false), 0);
		fwt.addColumnAt(true, col2 = new FastWebTreeColumn(2, new VariableNameFormatter(this), "Var Name", "help", false), 1);
		fwt.addColumnAt(true, col3 = new FastWebTreeColumn(3, new VariableFormatterFormatter(this), "Formatter", "help", false), 2);
		col1.setSelectable(false);
		col2.setSelectable(false);
		col3.setSelectable(false);

		/*================================*/
		//Add Nodes
		/*================================*/

		buildTree();
	}

	private void buildTree() {
		treeMgr.clear();
		WebTreeNode treeRoot = treeMgr.getRoot();
		String pid = this.getPortletId();
		TreePanelsRow r = TreePanelsRow.createRootRow();
		treeRoot.setName("HTML Panels");
		treeRoot.setData(r);
		StringBuilder sb = new StringBuilder();
		Collection<AmiWebQueryFormPortlet> formPortlets = AmiWebUtils.findPortletsByTypeFollowUndocked(getManager().getRoot(), AmiWebQueryFormPortlet.class);
		for (AmiWebQueryFormPortlet queryFormPortlet : formPortlets) {
			if (!AmiWebUtils.isParentAliasOrSame(this.baseAlias, queryFormPortlet.getAmiLayoutFullAlias()))
				continue;
			r = TreePanelsRow.createFormRow(queryFormPortlet);
			String formFullAliasDotName = queryFormPortlet.getAmiLayoutFullAliasDotId();
			WebTreeNode queryFormNode = treeMgr.createNode(formFullAliasDotName, treeRoot, true, r);
			queryFormNode.setKey(formFullAliasDotName);

			//Loop Through Query Fields
			Map<String, QueryField<?>> fields = queryFormPortlet.getFieldsById();
			for (QueryField<?> field : fields.values()) {
				String fieldName = field.getField().getTitle();
				String editorTypeId = field.getFactory().getEditorTypeId();
				//Loop Through Variables
				for (int i = 0; i < field.getVarsCount(); i++) {
					r = TreePanelsRow.createFieldVariableRow(field, i);
					String sourceVarname = field.getVarNameAt(i);
					SH.clear(sb);
					sb.append("<span class='ami_treepanels_name'>").append(fieldName).append(" (").append(sourceVarname).append(") ").append("<span style='color:#b3b3b3'>")
							.append(editorTypeId).append("</span>").append("</span>");
					WebTreeNode variableNode = treeMgr.createNode(sb.toString(), queryFormNode, false, r);
					variableNode.setKey(sourceVarname);
					variableNode.setHasCheckbox(true);
				}
			}
		}
	}

	private class RerunDmOnChangeFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public RerunDmOnChangeFormatter(AmiWebTreePanelsVariablesPortlet amiWebTreePanelsVariablesPortlet) {
			this.pid = amiWebTreePanelsVariablesPortlet.getPortletId();
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			byte type = r.type;
			if (type != 2)
				return;
			boolean b = r.rerunDmOnChange;
			sink.append("<div class='ami_treepanels ami_treepanels_checkbox'><input type='checkbox' onchange='var that=this;new function(){TPcheck(that,");
			SH.doubleQuote(this.pid, sink).append(',');
			sink.append(node.getUid());
			sink.append(")}' ");
			if (b == true)
				sink.append("checked ");
			if (!node.getChecked())
				sink.append("disabled");
			sink.append("></div>");
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			byte type = r.type;
			if (type != 2)
				return;
			boolean b = r.rerunDmOnChange;
			if (b == true)
				sink.append(b);
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(o1.getName(), o2.getName());
		}
		@Override
		public Object getValue(WebTreeNode node) {
			return node.getName();
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

	private class VariableNameFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public VariableNameFormatter(AmiWebTreePanelsVariablesPortlet amiWebTreePanelsVariablesPortlet) {
			this.pid = amiWebTreePanelsVariablesPortlet.getPortletId();
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			byte type = r.type;
			if (type != 2)
				return;
			String b = r.variableName;

			sink.append(
					"<div class='ami_treepanels ami_treepanels_varname'><div class='disable_glass_clear'></div><input type='text' oninput='var that=this;new function(){TPvarname(that,");
			SH.doubleQuote(this.pid, sink).append(',');
			sink.append(node.getUid());
			sink.append(")}' value='");
			sink.append(b);
			sink.append("' ");
			if (!node.getChecked())
				sink.append("disabled");
			sink.append("></div>");
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			byte type = r.type;
			if (type != 2)
				return;
			String b = r.variableName;

			sink.append(b);
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(o1.getName(), o2.getName());
		}
		@Override
		public Object getValue(WebTreeNode node) {
			return node.getName();
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

	private class VariableFormatterFormatter implements WebTreeNodeFormatter {
		final private String pid;

		public VariableFormatterFormatter(AmiWebTreePanelsVariablesPortlet amiWebTreePanelsVariablesPortlet) {
			this.pid = amiWebTreePanelsVariablesPortlet.getPortletId();
		}
		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			byte type = r.type;
			if (type != 2)
				return;
			Formatter b = r.formatter;
			sink.append("<div class='ami_treepanels ami_treepanels_formatter'><select onchange='var that=this;new function(){TPformatter(that,");
			SH.doubleQuote(this.pid, sink).append(',');
			sink.append(node.getUid());
			sink.append(")}' ");
			if (!node.getChecked())
				sink.append("disabled");
			sink.append(">");
			String formatters[] = AmiWebFormFieldVarLink.getFormattersForField(r.object);
			for (int i = 0; i < formatters.length; i++) {
				int id = AmiWebFormFieldVarLink.getFormatterIdForFormatter(formatters[i]);
				sink.append("<option value=").append(id);
				if (id == r.formatterId)
					sink.append(" selected");
				sink.append(">").append(formatters[i]).append("</option>");
			}
			sink.append("</select></div>");
		}
		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			TreePanelsRow r = (TreePanelsRow) node.getData();
			if (r == null)
				return;
			String formatters[] = AmiWebFormFieldVarLink.getFormattersForField(r.object);
			for (int i = 0; i < formatters.length; i++) {
				int id = AmiWebFormFieldVarLink.getFormatterIdForFormatter(formatters[i]);
				if (id == r.formatterId) {
					sink.append(id);
					return;
				}
			}
		}
		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			return OH.compare(o1.getName(), o2.getName());
		}
		@Override
		public Object getValue(WebTreeNode node) {
			return node.getName();
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

	public void setBaseAlias(String value) {
		if (OH.eq(this.baseAlias, value))
			return;
		this.baseAlias = value;
		List<AmiWebFormFieldVarLink> vars = getVariablesConfiguration();
		this.buildTree();
		initVariables(vars);
	}

}
