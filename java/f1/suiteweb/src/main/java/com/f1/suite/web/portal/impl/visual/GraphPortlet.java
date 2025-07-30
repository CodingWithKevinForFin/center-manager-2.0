package com.f1.suite.web.portal.impl.visual;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import com.f1.base.IterableAndSize;
import com.f1.base.ToStringable;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.BasicMultiMap;
import com.f1.utils.structs.IntKeyMap;

public class GraphPortlet extends AbstractPortlet {

	public static final byte DIRECTION_NONE = 0;
	public static final byte DIRECTION_FORWARD = 1;
	public static final byte DIRECTION_BACKWARD = 2;

	public static final PortletSchema<GraphPortlet> SCHEMA = new BasicPortletSchema<GraphPortlet>("Graph", "GraphPortlet", GraphPortlet.class, true, true);

	private List<GraphMovementListener> movementListeners = new ArrayList<GraphMovementListener>();

	public static final Comparator<? super Node> Y_POSITION_COMPARATOR = new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			return OH.compare(o1.y, o2.y);
		}
	};
	public static final Comparator<? super Node> X_POSITION_COMPARATOR = new Comparator<Node>() {
		@Override
		public int compare(Node o1, Node o2) {
			return OH.compare(o1.x, o2.x);
		}
	};

	private final IntKeyMap<Node> nodes = new IntKeyMap<Node>();
	private final IntKeyMap<Edge> edges = new IntKeyMap<Edge>();

	private IntKeyMap<Node> nodesAdded = new IntKeyMap<Node>();
	private IntKeyMap<Node> nodesUpdated = new IntKeyMap<Node>();
	private IntKeyMap<Node> nodesSelected = new IntKeyMap<Node>();

	private IntKeyMap<Node> nodesRemoved = new IntKeyMap<Node>();
	private IntKeyMap<Edge> edgesAdded = new IntKeyMap<Edge>();
	private IntKeyMap<Edge> edgesUpdated = new IntKeyMap<Edge>();
	private IntKeyMap<Edge> edgesRemoved = new IntKeyMap<Edge>();

	private int gridSize = 1;
	private int snapSize = 1;
	private int nextId = 0;
	private GraphContextMenuFactory contextMenuFactory = null;

	private boolean sendInFull;

	final private List<GraphListener> listeners = new ArrayList<GraphListener>();
	private IntKeyMap<Node> selectedNodes = new IntKeyMap<Node>();
	private boolean sendInFullEdges;

	private boolean pendingNodesAdded;
	private boolean pendingNodesSelected;
	private boolean pendingNodesRemoved;
	private boolean pendingEdgesAdded;
	private boolean pendingEdgesRemoved;
	private boolean pendingSetGridSnap;

	public GraphPortlet(PortletConfig portletConfig, boolean allowModifications) {
		super(portletConfig);
	}
	public GraphPortlet(PortletConfig portletConfig) {
		this(portletConfig, true);
	}

	public Node addNode(int x, int y, int width, int height, String html, String cssStyle) {
		Node r = new Node(this, ++nextId, x, y, width, height, html, cssStyle);
		nodes.put(r.getId(), r);
		onAdded(r);
		return r;
	}
	public Node removeNode(int id) {
		Node r = nodes.remove(id);
		if (r != null) {
			onRemoved(r);
			selectedNodes.remove(r.getId());
		}
		return r;
	}

	public Edge addEdge(int id1, int id2) {
		return addEdge(id1, id2, DIRECTION_NONE);
	}
	public Edge addEdge(int id1, int id2, byte direction) {
		Node n1 = getNodeOrThrow(id1);
		Node n2 = getNodeOrThrow(id2);
		int index = CH.size(n2.edgesByOtherNodeId.get(id1));
		if (index == -1)//TODO: is this right?
			index = 0;
		final Edge r = new Edge(this, ++nextId, id1, id2, index, direction, n1.getVisible() && n2.getVisible());
		this.edges.put(r.getId(), r);
		n1.edgesByOtherNodeId.putMulti(id2, r);
		n2.edgesByOtherNodeId.putMulti(id1, r);
		onAdded(r);
		return r;
	}
	@Override
	public PortletSchema<?> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("moveNode".equals(callback)) {
			int id = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "id");
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			Node node = nodes.get(id);
			if (node != null && node.getMovable())
				node.setXY(x, y);
		} else if ("select".equals(callback)) {
			int x = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "x");
			int y = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "y");
			int w = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "w");
			int h = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "h");
			boolean ctrl = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "ctrl");
			boolean shft = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "shift");
			if (w < 0) {
				x += w;
				w = -w;
			}
			if (h < 0) {
				y += h;
				h = -h;
			}
			int xx = x + w, yy = y + h;
			if (!shft)
				clearSelected();
			for (Node node : this.getNodes()) {
				if (node.getSelectable() && OH.isBetween(node.getX(), x, xx) && OH.isBetween(node.getY(), y, yy)) {
					node.setSelected(true);
				}

			}
			int button = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "button");
			if (listeners != null)
				for (GraphListener listener : listeners)
					listener.onSelectionChanged(this);
			if (button == 2) {
				if (contextMenuFactory != null) {
					WebMenu menu = contextMenuFactory.createMenu(this);
					if (menu != null) {
						Map<String, Object> menuModel = PortletHelper.menuToJson(getManager(), menu);
						callJsFunction("showContextMenu").addParamJson(menuModel).end();
					}
				}
			}
			if (listeners != null)
				for (GraphListener listener : listeners)
					listener.onUserClick(this, null, button, ctrl, shft);
		} else if ("click".equals(callback)) {
			Integer id = CH.getOr(Caster_Integer.INSTANCE, attributes, "id", null);
			boolean ctrl = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "ctrl");
			boolean shft = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "shift");
			int button = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "button");
			Node node = id == null ? null : nodes.get(id);
			if (node != null) {
				if (button == 2) {
					if (!node.getSelected()) {
						clearSelected();
						node.setSelected(true);
					}
					if (contextMenuFactory != null) {
						WebMenu menu = contextMenuFactory.createMenu(this);
						if (menu != null) {
							Map<String, Object> menuModel = PortletHelper.menuToJson(getManager(), menu);
							callJsFunction("showContextMenu").addParamJson(menuModel).end();
						}
					}
				}
				if (ctrl) {
					if (node.getSelectable())
						node.setSelected(!node.getSelected());
				} else if (shft) {
					if (!node.getSelected()) {
						if (node.getSelectable())
							node.setSelected(true);
					}
				} else if (button != 2) {
					clearSelected();
					if (node.getSelectable())
						node.setSelected(true);
				}
			}
			if (listeners != null)
				for (GraphListener listener : listeners)
					listener.onSelectionChanged(this);
			if (listeners != null)
				for (GraphListener listener : listeners)
					listener.onUserClick(this, node, button, ctrl, shft);
		} else if ("menuitem".equals(callback)) {
			final WebMenuLink action = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "action"));
			if (listeners != null)
				for (GraphListener listener : listeners)
					listener.onContextMenu(this, action.getAction());
		} else if ("dblClick".equals(callback)) {
			Integer id = CH.getOr(Caster_Integer.INSTANCE, attributes, "id", null);
			for (GraphListener listener : listeners) {
				listener.onUserDblClick(this, id);
			}
		} else if ("graphKeyDown".equals(callback)) {
			for (GraphListener listener : listeners) {
				listener.onKeyDown(attributes.get("key"), attributes.get("ctrl"));
			}
		} else
			super.handleCallback(callback, attributes);
	}
	public void clearSelected() {
		if (selectedNodes.size() > 0)
			for (int i : CH.l(selectedNodes.keys()))
				getNodeOrThrow(i).setSelected(false);

	}
	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			onCleared();
			flagPendingAjax();
		} else {
			onCleared();
			sendInFull = true;
		}
	}
	@Override
	public void drainJavascript() {
		if (getVisible()) {
			// Set Flags
			if (sendInFull) {
				this.pendingNodesAdded = true;
				this.pendingEdgesAdded = true;
			}
			if (sendInFullEdges)
				this.pendingEdgesAdded = true;
			if (nodesAdded.values().size() != 0 || nodesUpdated.size() != 0)
				this.pendingNodesAdded = true;
			if (nodesSelected.values().size() != 0)
				this.pendingNodesSelected = true;
			if (edgesAdded.values().size() != 0 || edgesUpdated.size() != 0)
				this.pendingEdgesAdded = true;
			if (nodesRemoved.values().size() != 0)
				this.pendingNodesRemoved = true;
			if (edgesRemoved.values().size() != 0)
				this.pendingEdgesRemoved = true;

			//Clear Data
			if (sendInFull) {
				callJsFunction("clearData").end();
			}
			if (sendInFullEdges)
				callJsFunction("clearEdges").end();
			//Nodes
			if (this.pendingNodesAdded) {
				JsFunction js = callJsFunction("addNodes");
				JsonBuilder json = js.startJson();
				json.startList();
				if (sendInFull) {
					for (Node cat : this.nodes.values())
						if (cat.getVisible())
							toJson(cat, json);
				} else {
					for (Node cat : this.nodesAdded.values())
						toJson(cat, json);
					for (Node cat : this.nodesUpdated.values())
						toJson(cat, json);
				}
				json.endList();
				json.close();
				js.end();
				this.pendingNodesAdded = false;
			}

			//Nodes Selected
			if (this.pendingNodesSelected) {
				if (!sendInFull) {
					JsFunction js = callJsFunction("selectNodes");
					JsonBuilder json = js.startJson();
					json.startList();
					for (Node cat : this.nodesSelected.values())
						toJsonNodesSelected(cat, json);
					json.endList();
					json.close();
					js.end();
					this.pendingNodesSelected = false;
				}
			}

			//Edges
			if (this.pendingEdgesAdded) {
				JsFunction js = callJsFunction("addEdges");
				JsonBuilder json = js.startJson();
				json.startList();
				if (sendInFull || sendInFullEdges) {
					for (Edge edge : this.edges.values())
						if (edge.getVisible())
							toJson(edge, json);
				} else {
					for (Edge edge : this.edgesAdded.values())
						if (edge.getVisible())
							toJson(edge, json);
					for (Edge edge : this.edgesUpdated.values())
						if (edge.getVisible())
							toJson(edge, json);
				}
				json.endList();
				json.close();
				js.end();
				this.pendingEdgesAdded = false;
			}
			if (pendingEdgesRemoved) {
				JsFunction js = callJsFunction("removeEdges");
				JsonBuilder json = js.startJson();
				json.add(this.edgesRemoved.keys());
				json.close();
				js.end();
				this.pendingEdgesRemoved = false;
			}
			if (pendingNodesRemoved) {
				JsFunction js = callJsFunction("removeNodes");
				JsonBuilder json = js.startJson();
				json.add(this.nodesRemoved.keys());
				json.close();
				js.end();
				this.pendingNodesRemoved = false;
			}
			if (pendingSetGridSnap || sendInFull)
				callJsFunction("setGridSnap").addParam(this.gridSize).addParam(this.snapSize).end();

			this.sendInFull = false;
			this.sendInFullEdges = false;
			this.pendingSetGridSnap = false;
			this.nodesAdded.clear();
			this.nodesSelected.clear();
			this.nodesUpdated.clear();
			this.nodesRemoved.clear();
			this.edgesAdded.clear();
			this.edgesUpdated.clear();
			this.edgesRemoved.clear();
			callJsFunction("repaint").end();
		}
		if (pendingEnsureVisibleNode) {
			callJsFunction("ensureVisibleNode").addParam(ensureVisibleNodeId).end();
			this.pendingEnsureVisibleNode = false;
		}
		if (pendingEnsureVisibleNodes) {
			callJsFunction("ensureVisibleNodes").addParamQuoted(ensureVisibleNodeIds).end();
			this.pendingEnsureVisibleNodes = false;
			this.ensureVisibleNodeIds = "";
		}
		super.drainJavascript();
	}
	private void toJson(Edge edge, JsonBuilder sink) {
		sink.startMap();
		sink.addKeyValue("id", edge.getId());
		sink.addKeyValue("n1", edge.getNodeId1());
		sink.addKeyValue("n2", edge.getNodeId2());
		sink.addKeyValue("d", edge.getDirection());
		sink.addKeyValue("idx", edge.getIndex());
		if (edge.getColor() != null)
			sink.addKeyValueQuoted("c", edge.getColor());
		sink.endMap();
	}
	private void toJson(Node node, JsonBuilder sink) {
		sink.startMap();
		sink.addKeyValue("id", node.getId());
		sink.addKeyValue("x", node.getX());
		sink.addKeyValue("y", node.getY());
		sink.addKeyValue("w", node.getWidth());
		sink.addKeyValue("h", node.getHeight());
		sink.addKeyValue("o", (node.getSelectable() ? 1 : 0) + (node.getMovable() ? 2 : 0));//both = 3, move=2,select=1,none=0
		sink.addKeyValueQuoted("n", node.getHtml());
		sink.addKeyValueQuoted("s", node.getCssStyle());
		if (node.getSelected())
			sink.addKeyValue("sel", true);
		sink.endMap();
	}
	private void toJsonNodesSelected(Node node, JsonBuilder sink) {
		sink.startMap();
		sink.addKeyValue("id", node.getId());
		if (node.getSelected())
			sink.addKeyValue("sel", true);
		sink.endMap();
	}

	private void onRemoved(Edge node) {
		fireEdgeRemoved(node);
		if (sendInFull)
			return;
		int id = node.getId();
		if (edgesAdded.remove(id) == null) {
			edgesUpdated.remove(id);
			edgesRemoved.put(id, node);
		}
		flagPendingAjax();
	}

	private void onRemoved(Node node) {
		fireNodeRemoved(node);
		int id = node.getId();
		if (node.hasEdgesIncoming() || node.hasEdgesOutgoing()) {
			List<Edge> sink = new ArrayList<Edge>();
			node.getEdgesIncoming(sink);
			node.getEdgesOutgoing(sink);
			for (Edge i : node.getEdges()) {
				removeEdge(i.getId());
			}
		}
		if (sendInFull)
			return;
		if (nodesAdded.remove(id) == null) {
			nodesSelected.remove(id);
			nodesUpdated.remove(id);
			nodesRemoved.put(id, node);
		}
		flagPendingAjax();
	}
	private void onUpdated(Node node) {
		fireNodeUpdated(node);
		if (sendInFull)
			return;
		if (!nodesAdded.containsKey(node.getId()))
			nodesUpdated.put(node.getId(), node);
		flagPendingAjax();
	}
	private void onSelected(Node node) {
		if (sendInFull)
			return;
		if (!nodesAdded.containsKey(node.getId()))
			nodesSelected.put(node.getId(), node);
		flagPendingAjax();
	}
	private void onUpdated(Edge edge) {
		fireEdgeUpdated(edge);
		if (sendInFull)
			return;
		if (!edgesAdded.containsKey(edge.getId()))
			edgesUpdated.put(edge.getId(), edge);
		flagPendingAjax();
	}
	private void onAdded(Node node) {
		fireNodeAdded(node);
		if (sendInFull)
			return;
		nodesAdded.put(node.getId(), node);
		flagPendingAjax();
	}
	private void onAdded(Edge edge) {
		fireEdgeAdded(edge);
		if (sendInFull)
			return;
		edgesAdded.put(edge.getId(), edge);
		flagPendingAjax();
	}
	private void onCleared() {
		this.nodesRemoved.clear();
		this.nodesAdded.clear();
		this.nodesUpdated.clear();
		this.nodesSelected.clear();
		this.edgesAdded.clear();
		this.edgesUpdated.clear();
		this.edgesRemoved.clear();
		this.sendInFull = true;
		flagPendingAjax();
	}
	private void onClearedEdges() {
		this.edgesAdded.clear();
		this.edgesUpdated.clear();
		this.edgesRemoved.clear();
		this.pendingEdgesAdded = false;
		this.pendingEdgesRemoved = false;
		this.sendInFullEdges = true;
		flagPendingAjax();

	}

	private boolean pendingEnsureVisibleNode = false;
	private boolean pendingEnsureVisibleNodes = false;
	private Integer ensureVisibleNodeId = null;
	private String ensureVisibleNodeIds = "";

	public void ensureVisibleNode(Node n) {
		if (!pendingEnsureVisibleNode) {
			ensureVisibleNodeId = n.getId();
			pendingEnsureVisibleNode = true;
			flagPendingAjax();
		} else {
			if (ensureVisibleNodeId != n.getId()) {
				ensureVisibleNodeId = n.getId();
			}
		}
	}
	public void ensureVisibleNodes(Collection<Node> nodes) {
		if (nodes.isEmpty())
			return;
		ArrayList<Integer> nodeids = new ArrayList<Integer>();
		for (Node n : nodes) {
			nodeids.add(n.getId());
		}

		String delimNodes = SH.join('|', nodeids);
		if (!pendingEnsureVisibleNodes) {
			ensureVisibleNodeIds = delimNodes;
			pendingEnsureVisibleNodes = true;
			flagPendingAjax();
		} else {
			if (!ensureVisibleNodeIds.equals(delimNodes))
				ensureVisibleNodeIds = delimNodes;
		}
	}

	public static class Node implements ToStringable {

		private BasicMultiMap.List<Integer, Edge> edgesByOtherNodeId = new BasicMultiMap.List<Integer, Edge>();
		final private GraphPortlet portlet;
		private int x, y, width, height;
		private String html;
		final private int id;
		private Object data;
		private String cssStyle;
		private boolean selected = false;
		private boolean selectable = true;
		private boolean movable = true;
		private boolean visible = true;

		public static Comparator<Node> compareLeft = new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				return OH.compare(o1.getLeft(), o2.getLeft());
			}
		};

		public static Comparator<Node> compareTop = new Comparator<Node>() {
			public int compare(Node o1, Node o2) {
				return OH.compare(o1.getTop(), o2.getTop());
			}
		};

		public Node(GraphPortlet portlet, int id, int x, int y, int width, int height, String html, String cssStyle) {
			this.portlet = portlet;
			this.id = id;
			if (x < 0)
				x = 0;
			if (y < 0)
				y = 0;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
			this.html = html;
			this.cssStyle = cssStyle;
		}

		public boolean getMovable() {
			return movable;
		}
		public boolean getSelectable() {
			return selectable;
		}

		public Iterable<Edge> getEdges() {
			return edgesByOtherNodeId.valuesMulti();

		}

		public void setSelected(boolean sel) {
			if (portlet.getNode(this.id) != this)
				throw new RuntimeException("Node has been removed: " + this);
			if (this.selected == sel)
				return;
			this.selected = sel;
			portlet.onSelected(this);
			if (selected) {
				portlet.selectedNodes.put(id, this);
			} else {
				portlet.selectedNodes.remove(id);
			}
		}
		public void setSelectedAndFire(boolean sel) {
			if (portlet.getNode(this.id) != this)
				throw new RuntimeException("Node has been removed: " + this);
			if (this.selected == sel)
				return;
			this.selected = sel;
			portlet.onSelected(this);
			if (selected) {
				portlet.selectedNodes.put(id, this);
			} else {
				portlet.selectedNodes.remove(id);
			}
			this.portlet.fireOnSelectedChanged();
		}

		public boolean getSelected() {
			return this.selected;
		}

		public void getEdgesOutgoing(List<Edge> sink) {
			for (Edge e : edgesByOtherNodeId.valuesMulti())
				if (e.getDirection() != DIRECTION_NONE && e.getDirection() == DIRECTION_FORWARD == (e.getNodeId1() == this.id))
					sink.add(e);
		}
		public void getEdgesIncoming(List<Edge> sink) {
			for (Edge e : edgesByOtherNodeId.valuesMulti()) {
				if (e.getDirection() != DIRECTION_NONE && e.getDirection() != DIRECTION_FORWARD == (e.getNodeId1() == this.id))
					sink.add(e);
			}
		}

		public boolean hasEdgesOutgoing() {
			for (Edge e : edgesByOtherNodeId.valuesMulti())
				if (e.getDirection() != DIRECTION_NONE && e.getDirection() == DIRECTION_FORWARD == (e.getNodeId1() == this.id))
					return true;
			return false;
		}
		public boolean hasEdgesIncoming() {
			for (Edge e : edgesByOtherNodeId.valuesMulti())
				if (e.getDirection() != DIRECTION_NONE && e.getDirection() != DIRECTION_FORWARD == (e.getNodeId1() == this.id))
					return true;
			return false;
		}
		public int getEdgesBetweenCount(Node other) {
			List<Edge> t = edgesByOtherNodeId.get(other.getId());
			return t == null ? 0 : t.size();
		}

		@Override
		public StringBuilder toString(StringBuilder sb) {
			return sb.append("Node: ").append(html);
		}
		@Override
		public String toString() {
			return "Node: " + html;
		}

		public int getX() {
			return x;
		}
		public void setX(int x) {
			if (x < this.width / 2)
				x = this.width / 2;
			if (x == this.x)
				return;
			this.x = x;
			this.portlet.onUpdated(this);
		}
		public void setY(int y) {
			if (y < this.height / 2)
				y = this.height / 2;
			if (y == this.y)
				return;
			this.y = y;
			this.portlet.onUpdated(this);
		}
		public int getY() {
			return y;
		}
		public void setXY(int x, int y) {
			if (x < this.width / 2)
				x = this.width / 2;
			if (y < this.height / 2)
				y = this.height / 2;
			if (x == this.x && y == this.y)
				return;
			int ox = x, oy = y;
			this.y = y;
			this.x = x;
			this.portlet.onUpdated(this);
			this.portlet.fireNodeMoved(this, ox, oy);
		}
		public int getWidth() {
			return width;
		}
		public void setWidth(int width) {
			if (width == this.width)
				return;
			this.width = width;
			this.portlet.onUpdated(this);
		}
		public int getHeight() {
			return height;
		}
		public void setHeight(int height) {
			if (height == this.height)
				return;
			this.height = height;
			this.portlet.onUpdated(this);
		}
		public String getHtml() {
			return html;
		}
		public void setHtml(String html) {
			if (OH.eq(html, this.html))
				return;
			this.html = html;
			this.portlet.onUpdated(this);
		}
		public GraphPortlet getPortlet() {
			return portlet;
		}

		public int getId() {
			return id;
		}

		public Object getData() {
			return data;
		}

		public Node setData(Object data) {
			this.data = data;
			this.portlet.fireNodeUpdated(this);
			return this;
		}

		public String getCssStyle() {
			return cssStyle;
		}

		public void setCssStyle(String cssStyle) {
			if (OH.eq(cssStyle, this.cssStyle))
				return;
			this.cssStyle = cssStyle;
			this.portlet.onUpdated(this);
		}

		public void removeEdge(Edge r) {
			final int otherId;
			if (r.getIsSelfLoop())
				otherId = r.getId();
			else if (id == r.getNodeId1())
				otherId = r.getNodeId2();
			else if (id == r.getNodeId2())
				otherId = r.getNodeId1();
			else
				throw new IllegalArgumentException("not an edge of this node: " + r);

			List<Edge> l = edgesByOtherNodeId.get(otherId);
			l.remove(r);
			for (int i = 0; i < l.size(); i++)
				l.get(i).setIndex(i);
		}

		public void setLeft(int x) {
			setXY(x + width / 2, y);
		}

		public void setTop(int y) {
			setXY(x, y + height / 2);
		}

		public int getTop() {
			return y - height / 2;
		}

		public int getBottom() {
			return y + height / 2;
		}
		public int getLeft() {
			return x - width / 2;
		}

		public int getRight() {
			return x + width / 2;
		}

		public void setMovable(boolean b) {
			if (this.movable == b)
				return;
			this.movable = b;
			this.portlet.onUpdated(this);
			this.portlet.fireNodeUpdated(this);
		}
		public void setSelectable(boolean b) {
			if (this.selectable == b)
				return;
			this.selectable = b;
			if (!selectable && selected) {
				setSelected(false);
			} else
				this.portlet.fireNodeUpdated(this);
		}

		public void setVisible(boolean visible) {
			if (this.visible == visible)
				return;
			this.visible = visible;
			this.portlet.fireNodeUpdated(this);
			if (!portlet.sendInFull) {
				if (!visible) {
					setSelected(false);
					if (portlet.nodesAdded.remove(id) == null) {
						portlet.nodesSelected.remove(id);
						portlet.nodesUpdated.remove(id);
						portlet.nodesRemoved.put(id, this);
					}
				} else {
					portlet.nodesRemoved.remove(id);
					portlet.nodesAdded.put(id, this);
				}
			}
			for (Edge i : this.getEdges())
				i.onEndpointVisibilityChanged();
		}
		public boolean getVisible() {
			return this.visible;
		}
	}

	public static class Edge {

		final private int id;
		final private int nodeId1;
		final private int nodeId2;
		private byte direction;
		private int index;
		private GraphPortlet portlet;
		private String color;
		private boolean visible = true;

		public Edge(GraphPortlet portlet, int id, int nodeId1, int nodeId2, int index, byte direction, boolean visible) {
			this.id = id;
			this.nodeId1 = nodeId1;
			this.nodeId2 = nodeId2;
			this.direction = direction;
			this.index = index;
			this.portlet = portlet;
			this.visible = visible;
		}

		public boolean getVisible() {
			return this.visible;
		}

		private void onEndpointVisibilityChanged() {
			boolean visible = portlet.getNode(nodeId1).getVisible() && portlet.getNode(nodeId2).getVisible();
			if (this.visible != visible) {
				this.visible = visible;
				if (!visible) {
					portlet.edgesUpdated.remove(id);
					portlet.edgesRemoved.put(id, this);
				} else {
					portlet.edgesAdded.put(id, this);
				}
			}
		}

		public boolean getIsSelfLoop() {
			return nodeId1 == nodeId2;
		}

		public int getId() {
			return id;
		}

		public int getNodeId1() {
			return nodeId1;
		}

		public int getNodeId2() {
			return nodeId2;
		}

		public byte getDirection() {
			return direction;
		}

		public void setDirection(byte direction) {
			if (direction == this.direction)
				return;
			this.direction = direction;
			portlet.onUpdated(this);

		}

		public void setIndex(int index) {
			if (index == this.index)
				return;
			this.index = index;
			portlet.onUpdated(this);
		}

		public int getIndex() {
			return this.index;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
			portlet.onUpdated(this);
		}

	}

	public Edge removeEdge(int eid) {
		Edge r = this.edges.remove(eid);
		if (r != null) {
			Node n1 = getNode(r.getNodeId1());
			Node n2 = getNode(r.getNodeId2());
			if (n1 != null)
				n1.removeEdge(r);
			if (n2 != null)
				n2.removeEdge(r);
			this.edgesRemoved.put(eid, r);
			onRemoved(r);
		}
		return r;
	}

	public void clearEdges() {
		this.edges.clear();
		for (Node n : this.nodes.values())
			n.edgesByOtherNodeId.clear();
		fireEdgesCleared();
		onClearedEdges();
	}
	public void addGraphListener(GraphListener listener) {
		this.listeners.add(listener);
	}

	public void removeGraphListener(GraphListener listener) {
		this.listeners.remove(listener);
	}

	public int getNodesCount() {
		return nodes.size();
	}
	public Node getNode(int node) {
		return nodes.get(node);
	}
	public Node getNodeOrThrow(int node) {
		return nodes.getOrThrow(node);
	}

	public Iterable<Node> getNodes() {
		return nodes.values();
	}

	public int getEdgesCount() {
		return edges.size();
	}

	public Iterable<Edge> getEdges() {
		return edges.values();
	}

	public void clear() {
		this.edges.clear();
		this.nodes.clear();
		this.selectedNodes.clear();
		fireNodesCleared();
		onCleared();
	}

	public static void layout(GraphPortlet gp) {
		int x = 10, y = 10;
		List<Node> remaining = CH.l(gp.getNodes());
		while (!remaining.isEmpty()) {
			List<Node> heads = new ArrayList<Node>();
			for (Node n : gp.getNodes()) {
				if (!n.hasEdgesIncoming())
					heads.add(n);
				else
					remaining.add(n);
			}
			for (Node head : heads) {
				head.setLeft(x);
				head.setTop(y);
				y += head.getHeight();

			}
		}

	}

	public IterableAndSize<Node> getSelectedNodes() {
		return selectedNodes.values();
	}
	public List<Node> getSelectedNodesList() {
		List<Node> nodes = new ArrayList<GraphPortlet.Node>();
		for (Node node : selectedNodes.values())
			nodes.add(node);
		return nodes;
	}

	public void setMenuFactory(GraphContextMenuFactory factory) {
		this.contextMenuFactory = factory;
	}

	public int getSelectedCount() {
		return this.selectedNodes.size();
	}

	public void addGraphMovementListener(GraphMovementListener listener) {
		this.movementListeners.add(listener);
	}
	public void removeGraphMovementListener(GraphMovementListener listener) {
		this.movementListeners.remove(listener);
	}

	private void fireNodeAdded(Node node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onNodeAdded(this, node);
	}
	private void fireNodeUpdated(Node node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onNodeUpdated(this, node);
	}
	private void fireNodeRemoved(Node node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onNodeRemoved(this, node);
	}
	private void fireEdgesCleared() {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onEdgesCleared();
	}
	private void fireNodesCleared() {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onNodesCleared();
	}

	private void fireEdgeAdded(Edge node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onEdgeAdded(this, node);
	}
	private void fireEdgeUpdated(Edge node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onEdgeUpdated(this, node);
	}
	private void fireEdgeRemoved(Edge node) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onEdgeRemoved(this, node);
	}
	private void fireNodeMoved(Node node, int oldx, int oldy) {
		for (int i = 0; i < movementListeners.size(); i++)
			movementListeners.get(i).onNodeMoved(this, oldx, oldy, node);
	}
	private void fireOnSelectedChanged() {
		for (GraphListener listener : listeners)
			listener.onSelectionChanged(this);

	}
	public int getGridSize() {
		return gridSize;
	}
	public void setGridSize(int gridSize) {
		if (this.gridSize == gridSize)
			return;
		this.gridSize = gridSize;
		this.pendingSetGridSnap = true;
		flagPendingAjax();
	}
	public int getSnapSize() {
		return snapSize;
	}
	public void setSnapSize(int snapSize) {
		if (this.snapSize == snapSize)
			return;
		this.snapSize = snapSize;
		this.pendingSetGridSnap = true;
		flagPendingAjax();
	}

}
