package com.f1.ami.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiCalcFrameStack;
import com.f1.ami.web.dm.portlets.AmiWebDmViewDataPortlet;
import com.f1.base.CalcFrame;
import com.f1.base.Table;
import com.f1.suite.web.fastwebcolumns.FastWebColumns;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.impl.DividerPortlet;
import com.f1.suite.web.portal.impl.FastTreePortlet;
import com.f1.suite.web.portal.impl.GridPortlet;
import com.f1.suite.web.portal.impl.TabPortlet;
import com.f1.suite.web.tree.WebTreeContextMenuListener;
import com.f1.suite.web.tree.WebTreeManager;
import com.f1.suite.web.tree.WebTreeNode;
import com.f1.suite.web.tree.WebTreeNodeFormatter;
import com.f1.suite.web.tree.WebTreeNodeListener;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.Tableset;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.BasicMethodFactory;
import com.f1.utils.structs.table.derived.ClassDebugInspector;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedCellCalculatorMethod;
import com.f1.utils.structs.table.derived.PauseStack;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.MutableCalcFrame;

public class AmiWebDebugPortlet extends GridPortlet implements WebTreeContextMenuListener, WebTreeNodeListener {

	public static final Integer COLID_TYPE = 1;
	public static final Integer COLID_VALUE = 2;
	private static final Object NULL = new Object();

	final private FastTreePortlet stackTree;
	final private FastTreePortlet debuggerTree;
	//	private FastWebTreeColumn typeColumn;
	private BasicMethodFactory methodFactory;
	private DividerPortlet divPanel;
	private DividerPortlet div2Panel;
	private FastTreePortlet valuesTree;
	private AmiWebDmViewDataPortlet valuesTable;
	private TabPortlet valuesTab;
	final private AmiWebService service;

	public AmiWebDebugPortlet(PortletConfig config) {
		super(config);

		this.stackTree = new FastTreePortlet(generateConfig());
		this.stackTree.getTreeManager().setComparator(null);
		this.debuggerTree = new FastTreePortlet(generateConfig());
		this.stackTree.getTree().setRootLevelVisible(true);
		this.debuggerTree.getTree().setRootLevelVisible(false);
		//		this.typeColumn = new FastWebTreeColumn(1, this, "Var-Type", "", false).setWidth(70);
		//		this.debuggerTree.getTree().addColumnAt(true, typeColumn, 0);
		this.debuggerTree.getTree().addColumnAt(true, new FastWebTreeColumn(COLID_TYPE, new TypeFormatter(), "Type", "", false).setWidth(70), 0);
		this.debuggerTree.getTree().addColumnAt(true, new FastWebTreeColumn(COLID_VALUE, new ValueFormatter(), "Value", "", false).setWidth(250), 1);
		this.divPanel = new DividerPortlet(generateConfig(), true);
		this.div2Panel = new DividerPortlet(generateConfig(), false);
		this.valuesTree = new FastTreePortlet(generateConfig());
		this.valuesTree.getTree().addColumnAt(true, new FastWebTreeColumn(COLID_TYPE, new TypeFormatter(), "Type", "", false).setWidth(70), 0);
		this.valuesTree.getTree().addColumnAt(true, new FastWebTreeColumn(COLID_VALUE, new ValueFormatter(), "Value", "", false).setWidth(250), 1);
		this.valuesTree.getTree().setRootLevelVisible(false);
		this.debuggerTree.getTreeManager().addListener(this);
		this.stackTree.getTree().addMenuContextListener(this);
		this.valuesTree.getTreeManager().addListener(this);
		this.valuesTree.getTree().setAutoExpandUntilMultipleNodes(false);
		this.debuggerTree.getTree().setAutoExpandUntilMultipleNodes(false);

		this.valuesTable = new AmiWebDmViewDataPortlet(generateConfig());
		this.valuesTable.hideButtons();
		this.div2Panel.addChild(this.stackTree);
		this.div2Panel.addChild(this.debuggerTree);
		this.divPanel.addChild(this.div2Panel);
		this.valuesTab = new TabPortlet(generateConfig());
		this.valuesTab.addChild(this.valuesTable);
		this.valuesTab.addChild(this.valuesTree);
		this.valuesTab.getTabPortletStyle().setIsHidden(true);
		this.divPanel.addChild(this.valuesTab);
		this.debuggerTree.getTree().addMenuContextListener(this);

		//add default style manager for all debuggerTree,stackTree,valuesTree
		this.service = AmiWebUtils.getService(getManager());
		this.debuggerTree.setFormStyle(service.getUserFormStyleManager());
		this.debuggerTree.setDialogStyle(service.getUserDialogStyleManager());
		this.stackTree.setFormStyle(service.getUserFormStyleManager());
		this.stackTree.setDialogStyle(service.getUserDialogStyleManager());
		this.valuesTree.setFormStyle(service.getUserFormStyleManager());
		this.valuesTree.setDialogStyle(service.getUserDialogStyleManager());
		addChild(this.divPanel);
	}

	public void clear() {
		this.debuggerTree.clear();
		this.valuesTable.clear();
		this.valuesTree.clear();
		this.stackTree.clear();
	}

	public void setStack(PauseStack stack, BasicMethodFactory mf, String codeBlock) {
		DerivedCellCalculator dcc = stack.getFlowControlPause().getPosition();
		Tuple2<Integer, Integer> pos = SH.getLinePosition(codeBlock, dcc.getPosition());
		this.stackTree.clear();
		this.methodFactory = mf;
		while (stack.getNext() != null)
			stack = stack.getNext();
		CalcFrameStack map = stack.getLcvs();

		List<Tuple2<String, CalcFrameStack>> debugStack = new ArrayList<Tuple2<String, CalcFrameStack>>();

		CalcFrameStack lcvs = map;
		while (map != null) {//!(map instanceof AmiWebScriptExecuteInstance)) {
			if (map instanceof AmiCalcFrameStack) {
				break;
			} else {
				CalcFrameStack s = map;
				if (s.getCalc() instanceof DerivedCellCalculatorMethod) {
					DerivedCellCalculatorMethod m = (DerivedCellCalculatorMethod) s.getCalc();
					debugStack.add(new Tuple2<String, CalcFrameStack>(m.getDefinition().toString(), lcvs));
					map = s.getParent();
					lcvs = map;
				} else
					map = s.getParent();
			}
		}

		AmiCalcFrameStack ei = (AmiCalcFrameStack) map;
		AmiWebDomObject object = AmiWebUtils.getService(getManager()).getDomObjectsManager().getManagedDomObject(ei.getSourceAri());
		String type = object == null ? "Object" : mf.forType(object.getClass());//TODO: object should never be null
		debugStack.add(new Tuple2<String, CalcFrameStack>(type + "::" + ei.getCallbackName() + "(...) callback (Line " + pos.getA() + ":" + pos.getB() + ")", lcvs));
		this.stackTree.getTreeManager().getRoot().setName(ei.getSourceAri());
		for (int i = 0; i < debugStack.size(); i++) {
			Tuple2<String, CalcFrameStack> s = debugStack.get(i);
			WebTreeNode node = this.stackTree.getTreeManager().createNode(s.getA(), this.stackTree.getTreeManager().getRoot(), false).setIsExpandable(false).setData(s.getB());
			if (i == 0) {
				node.setSelected(true);
				onStackSelected(s.getB());
			}
		}

	}

	private void onStackSelected(CalcFrameStack map) {

		MutableCalcFrame lcvs = new MutableCalcFrame();
		WebTreeManager tm = this.debuggerTree.getTree().getTreeManager();
		tm.clear();
		WebTreeNode lcvNode = tm.createNode("Local Variables", tm.getRoot(), true).setIsExpandable(true);
		WebTreeNode extVarsNode = tm.createNode("External Variables (readonly)", tm.getRoot(), false).setIsExpandable(true);
		WebTreeNode paramNode = tm.createNode("Params", tm.getRoot(), false).setIsExpandable(true);
		WebTreeNode tablesNode = tm.createNode("Temp Database", tm.getRoot(), true).setIsExpandable(true);
		Set<Object> st = new IdentityHashSet<Object>();

		boolean skip = false;
		while (!(map instanceof AmiCalcFrameStack)) {
			CalcFrameStack s = map;
			if (!skip) {
				if (s.getCalc() instanceof DerivedCellCalculatorMethod)
					skip = true;
				if (!s.getFrame().isVarsEmpty()) {
					CalcFrame types = s.getFrame();
					for (String i : types.getVarKeys())
						addNode(i, types.getValue(i), lcvNode);
				}
				lcvs.putAllTypeValues(s.getFrame());
			}
			map = s.getParent();
			if (map == null)
				break;
		}

		AmiCalcFrameStack ei = (AmiCalcFrameStack) map;

		for (String i : ei.getGlobalConsts().getVarKeys()) {
			Object val = ei.getGlobalConsts().getValue(i);
			addNode(i, val, extVarsNode);
		}
		for (String i : ei.getGlobal().getVarKeys()) {
			Object val = ei.getGlobal().getValue(i);
			addNode(i, val, paramNode);
		}
		if (skip == false)

			addNode("this", ei.getThis(), tm.getRoot());
		if (this.getParent().getVisible())
			PortletHelper.ensureVisible(this.debuggerTree);
		Tableset ts = ei.getTableset();
		for (String s : ts.getTableNames()) {
			Table t = ts.getTable(s);
			tm.createNode(s, tablesNode, false).setData(t);
		}
	}

	public String toValueString(Object o) {
		try {
			if (o == NULL) {
				return "<i>null</i>";
			} else if (o instanceof Deferred) {
				return "<i>expand to see " + this.methodFactory.forType(((Deferred) o).type);
			} else if (o instanceof Collection) {
				Collection l = (Collection) o;
				String type = getCommonType(l);
				return WebHelper.escapeHtml("<" + type + ">[" + l.size() + "]");
			} else if (o instanceof Map) {
				Map m = (Map) o;
				String ktype = getCommonType(m.keySet());
				String vtype = getCommonType(m.values());
				return WebHelper.escapeHtml("<" + ktype + "," + vtype + ">[" + m.size() + "]");
			} else if (o instanceof Table) {
				Table t = (Table) o;
				return " [" + t.getRows().size() + "][" + t.getColumnsCount() + "]";
			} else {
				return AmiUtils.s(o);
			}
		} catch (Throwable t) {
			return "<circular-reference>";
		}
	}

	private String getCommonType(Collection l) {
		Class type = null;
		for (Object i : l) {
			if (i == null)
				continue;
			if (type == null)
				type = i.getClass();
			else if (type != i.getClass())
				return "?";
		}
		if (type == null)
			return "?";
		else
			return this.methodFactory.forType(type);
	}

	public class TypeFormatter implements WebTreeNodeFormatter {
		private StringBuilder tmp1 = new StringBuilder();
		private StringBuilder tmp2 = new StringBuilder();

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			tmp1.setLength(0);
			tmp2.setLength(0);
			formatToText(o1, tmp1);
			formatToText(o2, tmp2);
			return SH.COMPARATOR.compare(tmp1, tmp2);
		}
		@Override
		public Object getValue(WebTreeNode node) {
			tmp1.setLength(0);
			formatToText(node, tmp1);
			return tmp1.toString();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			Object o = node.getData();
			if (o != null && o != NULL)
				sink.append(methodFactory.forType(o.getClass()));
		}
		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	public class ValueFormatter implements WebTreeNodeFormatter {
		private StringBuilder tmp1 = new StringBuilder();
		private StringBuilder tmp2 = new StringBuilder();

		@Override
		public int compare(WebTreeNode o1, WebTreeNode o2) {
			tmp1.setLength(0);
			tmp2.setLength(0);
			formatToText(o1, tmp1);
			formatToText(o2, tmp2);
			return SH.COMPARATOR.compare(tmp1, tmp2);
		}
		@Override
		public Object getValue(WebTreeNode node) {
			tmp1.setLength(0);
			formatToText(node, tmp1);
			return tmp1.toString();
		}

		@Override
		public Object getValueDisplay(WebTreeNode node) {
			return this.getValue(node);
		}

		@Override
		public void formatToHtml(WebTreeNode node, StringBuilder sink, StringBuilder style) {
			formatToText(node, sink);
		}

		@Override
		public void formatToText(WebTreeNode node, StringBuilder sink) {
			Object o = node.getData();
			if (o != null)
				sink.append(toValueString(o));
		}

		@Override
		public String formatToText(Object data) {
			return OH.toString(data);
		}
	}

	@Override
	public void onContextMenu(FastWebTree tree, String action) {
	}

	@Override
	public void onNodeClicked(FastWebTree tree, WebTreeNode node) {
		if (node == null)
			return;
		if (tree == this.stackTree.getTree()) {
			CalcFrameStack map = (CalcFrameStack) node.getData();
			if (map != null)
				this.onStackSelected(map);
			return;
		}
		if (node == null)
			return;
		Object o = node.getData();
		if (o instanceof Table) {
			Table t = (Table) o;
			this.valuesTable.clear();
			this.valuesTable.addTable(t);
			PortletHelper.ensureVisible(this.valuesTable);
		} else if (o != null) {
			this.valuesTree.getTreeManager().clear();
			addNode(node.getName(), o, this.valuesTree.getTreeManager().getRoot()).setIsExpanded(true);
			PortletHelper.ensureVisible(this.valuesTree);
		}
	}

	private StringBuilder buf = new StringBuilder();

	private WebTreeNode addNode(String name, Object o, WebTreeNode root) {
		return root.getTreeManager().createNode(name, root, false).setIsExpandable(true).setData(toData(o)).setIsExpandable(true);
	}
	private void addNodes(Object o, WebTreeNode child) {
		if (o instanceof Collection) {
			Collection l = (Collection) o;
			int n = 0;
			int padding = SH.toString(Math.max(l.size() - 1, 0)).length();

			for (Object o2 : l) {
				SH.clear(buf);
				buf.append('[');
				String s = SH.toString(n);
				SH.rightAlign('0', s, padding, false, buf);
				buf.append(']');
				addNode(buf.toString(), o2, child);
				n++;
			}
		} else if (o instanceof Map) {
			Map<?, ?> m = (Map) o;
			for (Map.Entry<?, ?> o2 : m.entrySet()) {
				String s = toValueString(o2.getKey());
				addNode(s, o2.getValue(), child);
			}
		} else {
			CalcFrameStack sf = this.service.createStackFrame(this.service);
			if (o instanceof Deferred) {
				Deferred deferred = ((Deferred) o);
				Object value = deferred.cdi.getDebugProperty(deferred.name, deferred.o, sf);
				child.setData(value);
				addNodes(value, child);
			} else {
				if (o != null) {
					List<ClassDebugInspector> dss = (List) this.methodFactory.getClassDebugInepectors(o.getClass());
					if (!dss.isEmpty()) {
						for (ClassDebugInspector<Object> ds : dss) {
							for (Map.Entry<String, Class<?>> e : ds.getDebugProperties().entrySet()) {
								Class<?> type = e.getValue();
								if (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type) || Table.class.isAssignableFrom(type))
									addNode(e.getKey(), new Deferred(o, ds, e.getKey(), type), child);
								else
									addNode(e.getKey(), ds.getDebugProperty(e.getKey(), o, sf), child);
							}
						}
					}
				}
			}
		}
	}

	private class Deferred {
		private final Object o;
		private final String name;
		private final ClassDebugInspector cdi;
		private final Class type;

		private Deferred(Object o, ClassDebugInspector cdi, String name, Class type) {
			this.o = o;
			this.cdi = cdi;
			this.name = name;
			this.type = type;
		}
	}

	private Object toData(Object o) {
		return o == null ? NULL : o;
	}

	@Override
	public void onNodeSelectionChanged(FastWebTree fastWebTree, WebTreeNode node) {
	}

	@Override
	public void onCellMousedown(FastWebTree tree, WebTreeNode start, FastWebTreeColumn col) {
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
		if (node.getIsExpanded() && node.getChildrenCount() == 0) {
			addNodes(node.getData(), node);
		}
	}

	@Override
	public void onCheckedChanged(WebTreeNode node) {
	}

	@Override
	public void onFilteredChanged(WebTreeNode child, boolean isFiltered) {
	}

	@Override
	public void onUserDblclick(FastWebColumns columns, String action, Map<String, String> properties) {
	}

}
