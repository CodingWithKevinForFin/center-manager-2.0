package com.f1.suite.web.portal.impl.visual;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.f1.base.IterableAndSize;
import com.f1.suite.web.JsFunction;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.WebMenuLink;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.portal.PortletConfig;
import com.f1.suite.web.portal.PortletHelper;
import com.f1.suite.web.portal.PortletSchema;
import com.f1.suite.web.portal.impl.AbstractPortlet;
import com.f1.suite.web.portal.impl.BasicPortletSchema;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.ColorGradient;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.json.JsonBuilder;
import com.f1.utils.structs.IntKeyMap;

public class TreemapPortlet extends AbstractPortlet {

	private static final int DEFAULT_BORDER_SIZE = 1;
	private static final int DEFAULT_TEXT_SIZE = 12;
	private static final String DEFAULT_BORDER_COLOR = "#FFFFFF";
	private static final String DEFAULT_TEXT_COLOR = "#000000";
	private static final String DEFAULT_BG_COLOR = "#888888";
	private ColorGradient gradient;

	private int defaultBorderSize = DEFAULT_BORDER_SIZE;
	private int defaultTextSize = DEFAULT_TEXT_SIZE;
	private String defaultBorderColor = DEFAULT_BORDER_COLOR;
	private String defaultTextColor = DEFAULT_TEXT_COLOR;
	private String defaultBgColor = DEFAULT_BG_COLOR;
	private HashMap<TreemapNode, String> styleChangedNodes = new HashMap<TreemapNode, String>(); // could be storing id instead...

	public static final String OPTION_SELECT_BORDER_COLOR1 = "selectBorderColor1";
	public static final String OPTION_SELECT_BORDER_COLOR2 = "selectBorderColor2";
	public static final String OPTION_BACKGROUND_COLOR = "backgroundColor";
	public static final String OPTION_FONT_FAMILY = "fontFamily";

	public static final PortletSchema<TreemapPortlet> SCHEMA = new BasicPortletSchema<TreemapPortlet>("Treemap", "TreemapPortlet", TreemapPortlet.class, true, true);
	public static final String OPTION_STICKYNESS = "stickyness";
	public static final String OPTION_RATIO = "ratio";
	public static final String OPTION_TEXT_H_ALIGN = "textHAlign";
	public static final String OPTION_TEXT_V_ALIGN = "textVAlign";

	private IntKeyMap<TreemapNode> nodesById = new IntKeyMap<TreemapNode>();

	private int nextId = 0;

	private List<WebTreemapContextMenuListener> menuListeners = new ArrayList<WebTreemapContextMenuListener>();

	private IntKeyMap<TreemapNode> added = new IntKeyMap<TreemapNode>();
	private IntKeyMap<TreemapNode> updated = new IntKeyMap<TreemapNode>();
	private IntKeyMap<TreemapNode> removed = new IntKeyMap<TreemapNode>();
	private boolean sendInFull = true;

	final private List<TreemapDepthStyle> depthStyles = new ArrayList<TreemapDepthStyle>();

	private IntKeyMap<TreemapNode> selected = new IntKeyMap<TreemapNode>();

	public TreemapNode getNodeByGroupPath(List<String> names) {
		TreemapNode r = root;
		for (int i = 0; i < names.size() && r != null; i++)
			r = r.getChild(names.get(i));
		return r;
	}
	public TreemapNode getNodeByGroupPath(String... names) {
		TreemapNode r = root;
		for (int i = 0; i < names.length && r != null; i++)
			r = r.getChild(names[i]);
		return r;
	}

	public TreemapPortlet(PortletConfig portletConfig) {
		super(portletConfig);
		getStyle(0);
	}

	private Map<String, Object> options = new HashMap<String, Object>();
	private TreemapNode root = new TreemapNode(this, nextId++);

	public Object addOption(String key, Object value) {
		onOptionsChanged();
		return options.put(key, value);
	}
	public Object removeOption(String key) {
		onOptionsChanged();
		return options.remove(key);
	}
	public void clearOptions() {
		this.options.clear();
		onOptionsChanged();
	}
	public Object getOption(String option) {
		return options.get(option);
	}
	public Set<String> getOptions() {
		return options.keySet();
	}

	private int maxDepth = -1;
	private double minHeat = Double.NaN;
	private double maxHeat = Double.NaN;

	public TreemapNode addNode(TreemapNode category, String groupId, double value, double heat, String bgColor, String textColor, String label, String tooltip) {
		if (!Double.isNaN(heat)) {
			if (heat < minHeat)
				flagSendInFull();
			else if (heat > maxHeat)
				flagSendInFull();
		}
		final TreemapNode node = new TreemapNode(category, nextId++, groupId, value, heat, bgColor, textColor, label, tooltip);
		category.addNode(node);
		nodesById.put(node.getId(), node);

		if (node.getDepth() > maxDepth) {
			getStyle(node.getDepth());
			onOptionsChanged();
		}

		onAdded(node);
		return node;
	}
	public TreemapNode removeNode(int nodeId) {
		TreemapNode node = nodesById.remove(nodeId);
		if (node != null)
			removeNode(node);
		return null;
	}
	private void removeNode(TreemapNode node) {
		if (node.isSelected()) {
			this.selected.remove(node.getId());
			node.setSelectedNoFire(false);
		}
		node.getParent().removeChild(node.getGroupId());
		for (TreemapNode child : node.getChildren())
			removeNode(child);
		onRemoved(node);
		double heat = node.getHeat();
		if (!Double.isNaN(heat)) {
			if (heat == this.minHeat) {
				this.minHeat = Double.NaN;
				flagSendInFull();
			}
			if (heat == this.maxHeat) {
				this.maxHeat = Double.NaN;
				flagSendInFull();
			}
		}
	}

	public void clearNodes() {
		this.nodesById.clear();
		this.styleChangedNodes.clear();
		this.root.clearChildren();
		this.minHeat = this.maxHeat = Double.NaN;
		clearSelected();
		onCleared();
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			onCleared();
			sendInFull = true;
			flagPendingAjax();
		} else {
			onCleared();
			sendInFull = true;
		}
	}
	@Override
	public void drainJavascript() {
		super.drainJavascript();
		if (getVisible()) {
			if (sendInFull) {
				if (Double.isNaN(this.minHeat) || Double.isNaN(this.maxHeat)) {
					minHeat = maxHeat = Double.NaN;
					for (TreemapNode node : nodesById.values()) {
						double h = node.getHeat();
						if (Double.isNaN(h))
							continue;
						else if (Double.isNaN(minHeat))
							minHeat = maxHeat = h;
						else if (h < minHeat)
							minHeat = h;
						else if (h > maxHeat)
							maxHeat = h;
					}
				}
				callJsFunction("clearData").end();
			}

			//Children
			JsFunction js = callJsFunction("addNodes");
			JsonBuilder json = js.startJson();
			json.startList();
			if (sendInFull) {
				toJson(root, json);
				for (TreemapNode node : nodesById.values())
					if (node.getValue() > 0)
						toJson(node, json);
			} else {
				for (TreemapNode node : added.values())
					toJson(node, json);
				for (TreemapNode node : updated.values())
					toJson(node, json);

			}

			json.endList();
			json.close();
			js.end();

			if (!sendInFull && removed.size() > 0) {
				js = callJsFunction("removeNodes");
				json = js.startJson();
				json.startList();
				for (TreemapNode node : removed.values()) {
					json.addEntry(node.getId());
				}
				json.endList();
				json.close();
				js.end();
			}
			if (optionsChanged || sendInFull) {
				JsFunction func = callJsFunction("setOptions");
				json = func.startJson();
				json.addQuoted(options);
				json.close();
				func.end();

				js = callJsFunction("setDepthStyles");
				json = js.startJson();
				json.startList();
				for (TreemapDepthStyle node : this.depthStyles) {
					toJson(node, json);
				}
				json.endList();
				json.close();
				js.end();
			}

			if (styleChangedNodes.size() > 0) {
				// send node border color to the frontend
				js = callJsFunction("handleNodeStyleChange");
				json = js.startJson().startList();
				for (Entry<TreemapNode, String> node : styleChangedNodes.entrySet()) {
					json.startMap();
					TreemapNode key = node.getKey();
					json.addKeyValueQuoted("nid", key.getId());
					json.addKeyValueQuoted("borderColor", node.getValue());
					json.endMap();
				}
				json.endList().close();
				js.end();
			}

			callJsFunction("repaint").end();
			added.clear();
			updated.clear();
			removed.clear();
			sendInFull = false;
			optionsChanged = false;

		}

	}

	private void toJson(TreemapNode node, JsonBuilder sink) {
		sink.startMap();
		sink.addKeyValue("id", node.getId());
		TreemapNode parent = node.getParent();
		sink.addKeyValue("pid", parent == null ? -1 : parent.getId());
		sink.addKeyValueQuoted("n", node.getGroupId());
		sink.addKeyValue("v", node.getValue());

		if (node.getBgColor() != null)
			sink.addKeyValueQuoted("h", node.getBgColor());
		else if (!Double.isNaN(node.getHeat())) {
			sink.addKeyValueQuoted("h", this.gradient.toColorRgb(scaleHeat(node.getHeat())));
		}

		if (node.getTextColor() != null)
			sink.addKeyValueQuoted("t", node.getTextColor());

		if (node.isSelected())
			sink.addKeyValue("s", 1);
		sink.endMap();
	}
	private double scaleHeat(double heat) {
		if (maxHeat == minHeat)
			return minHeat;
		else
			return (heat - minHeat) / (maxHeat - minHeat);
	}
	private void toJson(TreemapDepthStyle node, JsonBuilder sink) {
		sink.startMap();
		sink.addKeyValue("depth", node.getDepth());
		sink.addKeyValue("borderSize", node.getBorderSize() == -1 ? defaultBorderSize : node.getBorderSize());
		sink.addKeyValue("textSize", node.getTextSize() == -1 ? defaultTextSize : node.getTextSize());
		sink.addKeyValueQuoted("borderColor", SH.isnt(node.getBorderColor()) ? defaultBorderColor : node.getBorderColor());
		sink.addKeyValueQuoted("textColor", SH.isnt(node.getDefaultTextColor()) ? defaultTextColor : node.getDefaultTextColor());
		sink.addKeyValueQuoted("bgColor", SH.isnt(node.getDefaultBgColor()) ? defaultBgColor : node.getDefaultBgColor());
		sink.endMap();
	}
	@Override
	public PortletSchema<TreemapPortlet> getPortletSchema() {
		return SCHEMA;
	}

	@Override
	public void handleCallback(String callback, Map<String, String> attributes) {
		if ("click".equals(callback)) {
			int btn = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "btn");
			int nodeId = CH.getOr(Caster_Integer.PRIMITIVE, attributes, "nid", -1);
			if (nodeId == -1)
				return;
			boolean shift = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "shift");
			boolean ctrl = CH.getOrThrow(Caster_Boolean.PRIMITIVE, attributes, "ctrl");
			TreemapNode node = getNodeById(nodeId);
			if (node == null)
				return;
			if (btn == 2) {
				if (ctrl || shift) {
					if (!node.isSelected()) {
						node.setSelectedNoFire(true);
						fireSelectionChanged(true);
					}
				} else {
					if (!getSelected().containsKey(node.getId())) {
						clearSelectedNoFire();
						node.setSelectedNoFire(true);
						fireSelectionChanged(true);
					}
				}
			} else {
				if (ctrl) {
					node.setSelectedNoFire(!node.isSelected());
					fireSelectionChanged(true);
				} else if (shift) {
					if (!node.isSelected()) {
						node.setSelectedNoFire(true);
						fireSelectionChanged(true);
					}
				} else {
					if (getSelected().size() != 1 || !getSelected().containsKey(node.getId())) {
						clearSelectedNoFire();
						node.setSelectedNoFire(true);
						fireSelectionChanged(true);
					}
				}
			}
			fireOnClick(btn, node);
		} else if ("hover".equals(callback)) {
			int nodeId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "nid");
			TreemapNode node = getNodeById(nodeId);
			if (node != null) {
				String value = produceTooltip(node);
				if (SH.is(value))
					callJsFunction("setHover").addParam(nodeId).addParamQuoted(value).end();
			}
		} else if ("menuitem".equals(callback)) {
			//MOBILE SCROLLING - cancel zoom menu call back for touch heatmap
			final WebMenuLink action = getManager().getMenuManager().fireLinkForId(CH.getOrThrow(attributes, "action"));
			if (action != null) {
				if (action.getAction() == "cancel_zoom") {
					callJsFunction("cancelZoom").end();
				} else {
					int nodeId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "nid");
					TreemapNode node = getNodeById(nodeId);
					for (WebTreemapContextMenuListener ml : menuListeners)
						ml.onContextMenu(this, action.getAction(), node);
				}
			}
		} else if ("onTouch".equals(callback)) {
			int nodeId = CH.getOrThrow(Caster_Integer.PRIMITIVE, attributes, "nid");
			TreemapNode node = getNodeById(nodeId);
			if (contextMenuFactory != null) {
				WebMenu menu = contextMenuFactory.createMenu(this, node);
				if (menu != null) {
					if (menu.getChildrenCount() > 0)
						menu.add(new BasicWebMenuDivider());
					menu.add(new BasicWebMenuLink("Cancel Zoom", true, "cancel_zoom"));
					Map<String, Object> menuModel = PortletHelper.menuToJson(getManager(), menu);
					System.out.println(menu);
					callJsFunction("showContextMenu").addParamJson(menuModel).end();
				}
			}
		} else
			super.handleCallback(callback, attributes);
	}
	protected String produceTooltip(TreemapNode node) {
		StringBuilder sb = new StringBuilder();
		String r = node.getTooltip();
		if (r != null)
			return r;
		WebHelper.escapeHtml(node.getParent().getGroupId(), sb).append("<BR>");
		return WebHelper.escapeHtml(node.getGroupId(), sb).toString();
	}

	public void clearSelected() {
		if (this.selected.size() == 0)
			return;
		clearSelectedNoFire();
		fireSelectionChanged(false);
	}

	private void clearSelectedNoFire() {
		for (TreemapNode s : this.selected.values())
			s.setSelectedNoFire(false);
		this.selected.clear();
	}

	private void fireSelectionChanged(boolean userDriven) {
		for (WebTreemapContextMenuListener listener : menuListeners)
			listener.onSelectionChanged(this, selected, userDriven);
	}

	private WebTreemapContextMenuFactory contextMenuFactory;

	private boolean optionsChanged;

	protected void fireOnClick(int btn, TreemapNode selected) {
		for (WebTreemapContextMenuListener listener : menuListeners)
			listener.onNodeClicked(this, selected, btn);
		if (btn == 2) {
			if (contextMenuFactory != null) {
				WebMenu menu = contextMenuFactory.createMenu(this, selected);
				if (menu != null) {
					Map<String, Object> menuModel = PortletHelper.menuToJson(getManager(), menu);
					callJsFunction("showContextMenu").addParamJson(menuModel).end();
				}
			}
		}
	}
	public TreemapNode getNodeById(int nodeId) {
		return nodesById.get(nodeId);
	}

	public void addMenuContextListener(WebTreemapContextMenuListener listener) {
		menuListeners.add(listener);
	}

	public boolean removeMenuContextListener(WebTreemapContextMenuListener listener) {
		return menuListeners.remove(listener);
	}

	public WebTreemapContextMenuFactory getMenuContextFactory() {
		return contextMenuFactory;
	}

	public void setMenuContextFactory(WebTreemapContextMenuFactory contextMenuFactory) {
		this.contextMenuFactory = contextMenuFactory;
	}

	protected void onHeatChanged(TreemapNode treemapNode, double old, double nuw) {
		if (Double.isNaN(this.minHeat) || Double.isNaN(this.minHeat)) {
			if (sendInFull)
				return;
			if (!Double.isNaN(nuw)) {
				flagSendInFull();
				return;
			}
		}
		if (!Double.isNaN(old)) {
			if (nuw > old) {
				if (old == this.minHeat) {
					this.minHeat = Double.NaN;
					flagSendInFull();
				}
				if (nuw > this.maxHeat) {
					this.maxHeat = nuw;
					flagSendInFull();
				}
			} else if (nuw < old) {
				if (old == this.maxHeat) {
					this.maxHeat = Double.NaN;
					flagSendInFull();
				}
				if (nuw < this.minHeat) {
					this.minHeat = nuw;
					flagSendInFull();
				}
				this.minHeat = Math.min(this.minHeat, nuw);
			}
		} else if (!Double.isNaN(nuw)) {
			if (nuw > this.maxHeat) {
				this.maxHeat = nuw;
				flagSendInFull();
			}
			if (nuw < this.minHeat) {
				this.minHeat = nuw;
				flagSendInFull();
			}
		}
		onUpdated(treemapNode);
	}
	private void flagSendInFull() {
		this.sendInFull = true;
		flagPendingAjax();
	}
	protected void onBgColorChanged(TreemapNode treemapNode, String old, String heat) {
		onUpdated(treemapNode);
	}
	protected void onTextColorChanged(TreemapNode treemapNode, String old, String heat) {
		onUpdated(treemapNode);
	}
	public void onTooltipChanged(TreemapNode treemapNode, String old, String tooltip) {
		onUpdated(treemapNode);
	}
	public void onLabelChanged(TreemapNode treemapNode, String old, String label) {
		onUpdated(treemapNode);
	}
	public void onValueChanged(TreemapNode treemapNode, String old, String label) {
		onUpdated(treemapNode);
	}

	protected void onSelectChanged(TreemapNode treemapNode, boolean fireSelectChanged) {
		onUpdated(treemapNode);
		if (treemapNode.isSelected())
			this.selected.put(treemapNode.getId(), treemapNode);
		else
			this.selected.remove(treemapNode.getId());
		if (fireSelectChanged)
			fireSelectionChanged(false);
	}

	protected void onValueChanged(TreemapNode node, double old, double value) {
		if (sendInFull)
			return;
		if (old > 0 && value > 0) {
			if (!added.containsKey(node.getId()))
				updated.put(node.getId(), node);
			flagPendingAjax();
		} else if (old > 0) {//remove
			int id = node.getId();
			if (added.remove(id) == null) {
				updated.remove(id);
				removed.put(id, node);
			}
			flagPendingAjax();
		} else if (value > 0) {//add
			if (removed.remove(node.getId()) == null) {
				updated.remove(node.getId());
				added.put(node.getId(), node);
			}
			flagPendingAjax();
		}
	}
	protected void onGroupIdChanged(TreemapNode treemapNode, String old, String groupId) {
		onUpdated(treemapNode);
	}

	private void onRemoved(TreemapNode node) {
		if (sendInFull)
			return;
		int id = node.getId();
		if (added.remove(id) == null) {
			updated.remove(id);
			if (node.getValue() > 0)
				removed.put(id, node);
		}
		double heat = node.getHeat();
		if (!Double.isNaN(heat)) {
			if (heat == this.minHeat || heat == this.maxHeat)
				flagSendInFull();
		}
		flagPendingAjax();
	}
	private void onUpdated(TreemapNode treemapNode) {
		if (sendInFull)
			return;
		if (!added.containsKey(treemapNode.getId()) && treemapNode.getValue() > 0)
			updated.put(treemapNode.getId(), treemapNode);
		flagPendingAjax();
	}

	private void onAdded(TreemapNode node) {
		if (sendInFull)
			return;
		if (removed.remove(node.getId()) == null) {
			if (node.getValue() > 0)
				added.put(node.getId(), node);
		}
		flagPendingAjax();
	}
	private void onCleared() {
		this.removed.clear();
		this.added.clear();
		this.updated.clear();
		flagSendInFull();
	}

	public IntKeyMap<TreemapNode> getSelected() {
		return selected;
	}
	private void onOptionsChanged() {
		if (sendInFull)
			return;
		this.optionsChanged = true;
		flagPendingAjax();
	}

	public IterableAndSize<TreemapNode> getNodes() {
		return this.nodesById.values();
	}

	public TreemapNode getRootNode() {
		return root;
	}

	public void setBorderSize(int depth, int borderSize) {
		getStyle(depth).setBorderSize(borderSize);
		onOptionsChanged();
	}
	public void setBorderColor(int depth, String borderColor) {
		getStyle(depth).setBorderColor(borderColor);
		onOptionsChanged();
	}
	public void setTextColor(int depth, String textColor) {
		getStyle(depth).setDefaultTextColor(textColor);
		onOptionsChanged();
	}
	public void setBackgroundColor(int depth, String bgColor) {
		getStyle(depth).setDefaultBgColor(bgColor);
		onOptionsChanged();
	}
	public void setTextSize(int depth, int textSize) {
		getStyle(depth).setTextSize(textSize);
		onOptionsChanged();
	}
	public int getBorderSize(int depth) {
		return getStyle(depth).getBorderSize();
	}
	public String getBorderColor(int depth) {
		return getStyle(depth).getBorderColor();
	}
	public String getTextColor(int depth) {
		return getStyle(depth).getDefaultTextColor();
	}
	public String getBackgroundColor(int depth) {
		return getStyle(depth).getDefaultBgColor();
	}
	public int getTextSize(int depth) {
		return getStyle(depth).getTextSize();
	}
	public TreemapDepthStyle getStyle(int depth) {
		while (depth >= maxDepth) {
			this.depthStyles.add(new TreemapDepthStyle(this.depthStyles.size()));
			maxDepth++;
		}
		return this.depthStyles.get(depth);
	}

	public int getDefaultBorderSize() {
		return defaultBorderSize;
	}
	public void setDefaultBorderSize(int defaultBorderSize) {
		this.defaultBorderSize = defaultBorderSize;
		onOptionsChanged();
	}
	public String getDefaultBorderColor() {
		return defaultBorderColor;
	}
	public void setDefaultBorderColor(String defaultBorderColor) {
		this.defaultBorderColor = defaultBorderColor;
		onOptionsChanged();
	}
	public String getDefaultTextColor() {
		return defaultTextColor;
	}
	public void setDefaultTextColor(String defaultTextColor) {
		this.defaultTextColor = defaultTextColor;
		onOptionsChanged();
	}
	public String getDefaultBgColor() {
		return defaultBgColor;
	}
	public void setDefaultBgColor(String defaultBgColor) {
		this.defaultBgColor = defaultBgColor;
		onOptionsChanged();
	}

	public int getDefaultTextSize() {
		return defaultTextSize;
	}
	public void setDefaultTextSize(int defaultTextSize) {
		this.defaultTextSize = defaultTextSize;
		onOptionsChanged();
	}
	public int getMaxDepth() {
		return this.maxDepth;
	}

	public ColorGradient getGradient() {
		return this.gradient;
	}

	public void setGradient(ColorGradient g) {
		sendInFull = true;
		flagPendingAjax();
		this.gradient = g;
	}

	public boolean setNodeBorderColor(TreemapNode node, String color) {
		if (node == null || OH.eq(node.getBorderColor(), color))
			return false;
		node.setBorderColor(color); // toPng uses borderColor too
		styleChangedNodes.put(node, color);
		flagPendingAjax();
		return true;
	}

	public boolean isNodeHighlighted(TreemapNode node) {
		return styleChangedNodes.get(node) != null;
	}
}
