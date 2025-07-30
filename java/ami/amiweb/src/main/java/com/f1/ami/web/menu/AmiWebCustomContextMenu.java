package com.f1.ami.web.menu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amiscript.AmiDebugMessage;
import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.base.IterableAndSize;
import com.f1.suite.web.menu.WebMenu;
import com.f1.suite.web.menu.impl.AbstractWebMenuItem;
import com.f1.suite.web.menu.impl.BasicWebMenu;
import com.f1.suite.web.menu.impl.BasicWebMenuDivider;
import com.f1.suite.web.menu.impl.BasicWebMenuLink;
import com.f1.suite.web.util.WebHelper;
import com.f1.utils.CH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Boolean;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.DerivedHelper;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.derived.ToDerivedString;
import com.f1.utils.structs.table.stack.EmptyCalcTypes;

public class AmiWebCustomContextMenu implements ToDerivedString, AmiWebDomObject {
	public static final String ON_SELECTED = "onSelected";
	public static final ParamsDefinition PARAMDEF_ON_SELECTED = new ParamsDefinition(ON_SELECTED, Object.class, "");
	static {
		PARAMDEF_ON_SELECTED.addDesc("called when the menu is selected by the user");
	}
	public final static String STATUS_ENABLED = "enabled";
	public final static String STATUS_DISABLED = "disabled";
	public final static String STATUS_INVISIBLE = "invisible";
	public final static String STATUS_DIVIDER = "divider";

	final public static String PARAM_ID = "id";
	final public static String PARAM_DISPLAY = "display";
	final public static String PARAM_PATH = "path";
	final public static String PARAM_AMISCRIPT = "amiscript";
	final public static String PARAM_CALLBACKS = "callbacks";
	final public static String PARAM_BOLD = "bold";
	final public static String PARAM_ITALIC = "italic";
	final public static String PARAM_UNDERLINE = "underline";
	final public static String PARAM_STATUS = "status";
	final public static String PARAM_ICON = "icon";
	final public static String PARAM_PARENT_ID = "parentId";
	final public static String PARAM_POSITION = "position";
	public static final String FORMULA_DISPLAY = "display";
	public static final String FORMULA_STATUS = "status";
	public static final String FORMULA_ICON = "icon";

	private String id;
	//	private String display;
	final private AmiWebAmiScriptCallbacks amiscript;
	private boolean bold;
	private boolean italic;
	private boolean underline;
	//	private String status;
	//	private String icon;
	private int position;
	private AmiWebCustomContextMenuManager owner;
	private String parentId;
	private boolean isAdded = false;
	final private AmiWebFormulasImpl formulas;
	final private AmiWebFormula displayFormula;
	final private AmiWebFormula statusFormula;
	final private AmiWebFormula iconFormula;
	private List<AmiWebCustomContextMenuListener> listeners = null;

	public void addListener(AmiWebCustomContextMenuListener listener) {
		if (listeners == null)
			listeners = new ArrayList<AmiWebCustomContextMenuListener>();
		listeners.add(listener);
	}

	public void removeListener(AmiWebCustomContextMenuListener listener) {
		if (listeners != null)
			listeners.remove(listener);
	}
	public AmiWebCustomContextMenu(AmiWebCustomContextMenuManager owner, boolean isRoot) {
		this.formulas = new AmiWebFormulasImpl(this);
		if (!isRoot) {
			this.displayFormula = this.formulas.addFormula("display", String.class);
			this.statusFormula = this.formulas.addFormula("status", String.class);
			this.iconFormula = this.formulas.addFormula("icon", String.class);
			OH.assertNotNull(owner);
			this.owner = owner;
			this.amiscript = new AmiWebAmiScriptCallbacks(owner.getService(), this);
			this.amiscript.setAmiLayoutAlias(owner.getAmiLayoutFullAlias());
			for (ParamsDefinition i : owner.getService().getScriptManager().getCallbackDefinitions(AmiWebCustomContextMenu.class))
				this.amiscript.registerCallbackDefinition(i);
		} else {
			OH.assertNotNull(owner);
			this.owner = owner;
			this.displayFormula = null;
			this.statusFormula = null;
			this.iconFormula = null;
			this.amiscript = null;
		}
	}

	public void onAdded() {
		if (this.isAdded)
			throw new IllegalStateException();
		this.isAdded = true;
		updateAri();
		if (!isRoot())
			this.addToDomManager();
	}

	public int getPosition() {
		return this.position;
	}
	public void setPosition(int position) {
		this.position = position;
	}

	//simple getters
	public AmiWebDomObject getTargetPortlet() {
		return this.owner.getTargetPortlet();
	}
	protected AmiWebCustomContextMenuManager getOwner() {
		return this.owner;
	}
	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
		this.updateAri();
	}
	public AmiWebFormula getDisplayFormula() {
		return displayFormula;
	}
	//	public void setDisplay(String display) {
	//		this.display = display;
	//	}
	public boolean isBold() {
		return bold;
	}
	public void setBold(boolean bold) {
		this.bold = bold;
	}
	public boolean isItalic() {
		return italic;
	}
	public void setItalic(boolean italic) {
		this.italic = italic;
	}
	public boolean isUnderline() {
		return underline;
	}
	public void setUnderline(boolean underline) {
		this.underline = underline;
	}
	public AmiWebFormula getIconFormula() {
		return iconFormula;
	}
	//	public void setIcon(String icon) {
	//		this.icon = icon;
	//	}
	//	public void setStatus(String status) {
	//		this.status = status;
	//	}
	public AmiWebFormula getStatusFormula() {
		return this.statusFormula;
	}
	public boolean isRoot() {
		return "".equals(this.id);
	}
	public AmiWebAmiScriptCallbacks getAmiScript() {
		return amiscript;
	}

	public List<AmiWebCustomContextMenu> getChildrenNested(boolean includeMe, List<AmiWebCustomContextMenu> sink) {
		if (includeMe)
			sink.add(this);
		for (AmiWebCustomContextMenu i : this.children.values()) {
			i.getChildrenNested(true, sink);
		}
		return sink;
	}

	public void reorderChildren(List<String> ids) {
		if (ids.size() != this.children.getSize())
			throw new RuntimeException("Number of ids must match number of child nodes.");
		List<AmiWebCustomContextMenu> reordered = new ArrayList<AmiWebCustomContextMenu>();
		for (String id : ids)
			reordered.add(this.children.get(id));
		this.children.clear();
		int pos = 0;
		for (AmiWebCustomContextMenu i : reordered) {
			this.children.add(i.getId(), i);
			i.setPosition(pos++);
		}
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put(PARAM_ID, this.id);
		r.put(PARAM_POSITION, this.getPosition());
		r.put(PARAM_CALLBACKS, this.amiscript.getConfiguration());
		CH.putExcept(r, PARAM_STATUS, this.statusFormula.getFormulaConfig(), null);
		CH.putExcept(r, PARAM_PARENT_ID, this.parentId, "");
		CH.putExcept(r, PARAM_DISPLAY, this.displayFormula.getFormulaConfig(), null);
		CH.putExcept(r, PARAM_BOLD, this.bold, false);
		CH.putExcept(r, PARAM_ITALIC, this.italic, false);
		CH.putExcept(r, PARAM_UNDERLINE, this.underline, false);
		CH.putExcept(r, PARAM_ICON, this.iconFormula.getFormulaConfig(), null);
		return r;
	}

	public void init(Map<String, Object> config) {
		this.parentId = CH.getOr(Caster_String.INSTANCE, config, PARAM_PARENT_ID, "");
		this.id = CH.getOr(Caster_String.INSTANCE, config, PARAM_ID, null);
		this.displayFormula.initFormula(CH.getOr(Caster_String.INSTANCE, config, PARAM_DISPLAY, null));
		this.bold = CH.getOr(Caster_Boolean.PRIMITIVE, config, PARAM_BOLD, false);
		this.italic = CH.getOr(Caster_Boolean.PRIMITIVE, config, PARAM_ITALIC, false);
		this.underline = CH.getOr(Caster_Boolean.PRIMITIVE, config, PARAM_UNDERLINE, false);
		this.statusFormula.initFormula(CH.getOr(Caster_String.INSTANCE, config, PARAM_STATUS, null));
		this.iconFormula.initFormula(CH.getOr(Caster_String.INSTANCE, config, PARAM_ICON, null));
		this.position = CH.getOrThrow(Caster_Integer.INSTANCE, config, PARAM_POSITION);
		this.amiscript.init(null, this.getTargetPortlet().getAmiLayoutFullAlias(), (Map<String, Object>) config.get(PARAM_CALLBACKS), null);
	}

	@Override
	public String toString() {
		try {
			return this.id + " : " + getEvaluatedDisplay();
		} catch (Exception e) {
			return this.id;
		}
	}
	@Override
	public String toDerivedString() {
		if (this.ari == null)
			return "ROOT_MENUITEM";
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(toDerivedString());
	}
	public StringBuilder getPathDescription(StringBuilder sink) {
		if (getParent() != null)
			return getParent().getPathDescription(sink).append(" --> ").append(id);
		else
			return sink.append("(ROOT)");
	}

	public void processMenuAction() {
		if (STATUS_ENABLED.equals(getEvaluatedStatus()))
			getAmiScript().execute(ON_SELECTED);
	}
	public String getEvaluatedDisplay() {
		return getEvaluatedParam(this.displayFormula);
	}
	public String getEvaluatedDisplayEscaped() {
		return WebHelper.escapeHtml(getEvaluatedParam(this.displayFormula));
	}
	public String getEvaluatedStatus() {
		return getEvaluatedParam(this.statusFormula);
	}
	public String getEvaluatedIcon() {
		return getEvaluatedParam(this.iconFormula);
	}
	private String getEvaluatedParam(AmiWebFormula formula) {
		DerivedCellCalculator formulaCalc = formula.getFormulaCalc();
		if (formulaCalc == null)
			return null;
		return AmiUtils.s(formulaCalc.get(this.owner.getService().createStackFrame(this)));
	}

	public Exception testParam(String param, String formulaId) {
		return this.getFormulas().getFormula(formulaId).testFormula(param);
	}

	public WebMenu generateMenu(String prefix) {
		BasicWebMenu sink = new BasicWebMenu();
		for (AmiWebCustomContextMenu child : this.children.values()) {
			child.generateMenu(sink, prefix);
		}
		return sink;
	}
	public void generateMenu(WebMenu sink, String prefix) {
		String evaluatedStatus = this.getEvaluatedStatus();
		if (STATUS_DIVIDER.equals(evaluatedStatus)) {
			sink.add(new BasicWebMenuDivider());
			return;
		} else if (STATUS_INVISIBLE.equals(evaluatedStatus)) {
		} else if (hasChildren()) {
			WebMenu r = new BasicWebMenu(this.getEvaluatedDisplayEscaped(), AmiWebCustomContextMenu.STATUS_ENABLED.equals(evaluatedStatus));
			r.setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
			for (AmiWebCustomContextMenu child : this.children.values())
				child.generateMenu(r, prefix);
			if (!r.getChildren().isEmpty())
				sink.add(r);
		} else {
			final AbstractWebMenuItem item;
			if (STATUS_ENABLED.equals(evaluatedStatus)) {
				item = new BasicWebMenuLink(getEvaluatedDisplayEscaped(), true, prefix + this.id);
			} else if (STATUS_DISABLED.equals(evaluatedStatus)) {
				item = new BasicWebMenuLink(getEvaluatedDisplayEscaped(), false, prefix + this.id);
			} else {
				if (getTargetPortlet().getService().getDebugManager().shouldDebug(AmiDebugMessage.SEVERITY_WARNING))
					getTargetPortlet().getService().getDebugManager().addMessage(
							new AmiDebugMessage(AmiDebugMessage.SEVERITY_WARNING, AmiDebugMessage.TYPE_FORMULA, getAri(), null, "Invalid status: " + evaluatedStatus, null, null));
				item = new BasicWebMenuLink(getEvaluatedDisplayEscaped(), false, prefix + this.id);
			}
			String evaluatedIcon = getEvaluatedIcon();
			if (SH.is(evaluatedIcon))
				item.setBackgroundImage(evaluatedIcon);
			item.setCssStyle("style.fontWeight=" + (this.bold ? "bold" : "") + "|style.fontStyle=" + (this.italic ? "italic" : "") + "|style.textDecoration="
					+ (this.underline ? "underline" : ""));
			item.setHtmlIdSelector(AmiWebUtils.toHtmlIdSelector(this));
			sink.add(item);
		}
	}

	private AmiWebCustomContextMenu parent;
	private final BasicIndexedList<String, AmiWebCustomContextMenu> children = new BasicIndexedList<String, AmiWebCustomContextMenu>();

	public IterableAndSize<AmiWebCustomContextMenu> getChildren() {
		return this.children.values();
	}
	public AmiWebCustomContextMenu getChildItemAt(int pos) {
		return this.children.getAt(pos);
	}
	public int getChildrenCount() {
		return this.children.getSize();
	}
	public boolean hasChildren() {
		return this.children.getSize() > 0;
	}

	protected void setParent(AmiWebCustomContextMenu parent) {
		this.parent = parent;
	}
	public AmiWebCustomContextMenu getParent() {
		return this.parent;
	}

	public String getParentId() {
		return this.parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	protected void addChild(AmiWebCustomContextMenu child) {
		OH.assertEq(this.id, child.getParentId());
		this.children.add(child.getId(), child, MH.clip(child.getPosition(), 0, this.children.getSize()));
		updateChildPositions(child.getPosition());
		child.setParent(this);
		if (CH.isntEmpty(this.listeners)) {
			for (AmiWebCustomContextMenuListener i : this.listeners)
				i.onChildAdded(this, child);
		}
	}
	private void updateChildPositions(int i) {
		while (i < this.getChildrenCount())
			this.children.getAt(i).setPosition(i++);
	}

	protected void removeChild(String child) {
		int pos = this.children.getPosition(child);
		AmiWebCustomContextMenu c = this.children.remove(child);
		updateChildPositions(pos);
		if (c != null && CH.isntEmpty(this.listeners)) {
			for (AmiWebCustomContextMenuListener i : this.listeners)
				i.onChildRemoved(this, c);
		}
	}

	public boolean isNestedChild(AmiWebCustomContextMenu i) {
		if (this.children.containsKey(i.getId()))
			return true;
		for (AmiWebCustomContextMenu child : this.children.values())
			if (child.isNestedChild(i))
				return true;
		return false;
	}

	private String ari;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;

	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.getTargetPortlet().getAmiLayoutFullAlias();
		if (isAdded && !"".equals(getId())) {
			this.amiLayoutFullAliasDotId = this.getTargetPortlet().getAmiLayoutFullAliasDotId() + "?" + getId();
			this.ari = AmiWebDomObject.ARI_TYPE_MENUITEM + ":" + this.amiLayoutFullAliasDotId;
			this.getOwner().getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		}
	}

	@Override
	public String getAriType() {
		return AmiWebDomObject.ARI_TYPE_MENUITEM;
	}

	@Override
	public String getDomLabel() {
		return getId();
	}

	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}

	@Override
	public AmiWebDomObject getParentDomObject() {
		if (this.owner.getTargetPortlet() instanceof AmiWebPortlet) {
			return (AmiWebDomObject) this.owner.getTargetPortlet();
		}
		return this.getTargetPortlet().getService().getLayoutFilesManager().getLayoutByFullAlias(this.getTargetPortlet().getAmiLayoutFullAlias()); // when owner is a desktop
	}

	@Override
	public Class<?> getDomClassType() {
		return AmiWebCustomContextMenu.class;
	}

	@Override
	public Object getDomValue() {
		return this;
	}

	public void onInitDone() {
		if (this.amiscript != null)
			this.amiscript.initCallbacksLinkedVariables();
	}

	public void close() {
		if (!isRoot()) {
			this.amiscript.close();
			this.removeFromDomManager();
		}
	}

	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return this.amiscript;
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	@Override
	public void setTransient(boolean isTransient) {
		throw new UnsupportedOperationException("Invalid operation");
	}

	private boolean isManagedByDomManager = false;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.owner.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;
		}
	}
	@Override
	public void removeFromDomManager() {
		this.getOwner().getService().getDomObjectsManager().fireRemoved(this);
		if (this.isManagedByDomManager == true) {
			AmiWebService service = this.owner.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}
	public String getAlias() {
		return getTargetPortlet().getAmiLayoutFullAlias();
	}

	//	@Override
	//	public void recompileAmiscript() {
	//		if (this.amiscript != null)
	//			this.amiscript.recompileAmiscript();
	//		getFormulas().recompileAmiscript();
	//		for (AmiWebCustomContextMenu i : this.children.values())
	//			i.recompileAmiscript();
	//
	//	}

	@Override
	public String getAmiLayoutFullAlias() {
		return this.amiLayoutFullAlias;
	}

	@Override
	public String getAmiLayoutFullAliasDotId() {
		return this.amiLayoutFullAliasDotId;
	}

	@Override
	public AmiWebFormulas getFormulas() {
		return this.formulas;
	}

	@Override
	public AmiWebService getService() {
		return this.amiscript.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return EmptyCalcTypes.INSTANCE;
	}

	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
