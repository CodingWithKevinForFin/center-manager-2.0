package com.f1.ami.web.tree;

import java.util.Collections;
import java.util.List;

import com.f1.ami.web.AmiWebAmiScriptCallbacks;
import com.f1.ami.web.AmiWebDomObject;
import com.f1.ami.web.AmiWebDomObjectsManager;
import com.f1.ami.web.AmiWebFormula;
import com.f1.ami.web.AmiWebFormulas;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.suite.web.tree.impl.FastWebTree;
import com.f1.suite.web.tree.impl.FastWebTreeColumn;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.structs.table.derived.DerivedHelper;

public class AmiWebTreeColumn implements AmiWebDomObject {

	private String amiId;
	final private int columnId;
	final private AmiWebTreePortlet tree;
	final private AmiWebTreeGroupByFormatter formatter;
	private boolean isTransient;
	private String ari;
	private FastWebTreeColumn column;
	private String amiLayoutFullAlias;
	private String amiLayoutFullAliasDotId;
	final private AmiWebFormulasImpl formulas;
	private byte formatterType;
	private int decimals;
	private String headerStyleExpression = "";
	private String headerStyle = "";

	public AmiWebTreeColumn(int columnId, String amiId, AmiWebTreePortlet tree, byte formatterType, int decimals) {
		super();
		OH.assertNe(formatterType, 0);
		this.formatterType = formatterType;
		this.decimals = decimals;
		this.formulas = new AmiWebFormulasImpl(this);
		this.formulas.setAggregateFactory(tree.aggregateFactory);
		this.amiId = amiId;
		this.columnId = columnId;
		this.tree = tree;
		this.formatter = new AmiWebTreeGroupByFormatter(tree, tree.getService().getFormatterManager().getBasicFormatter(), this.formulas);
		updateAri();
	}

	public String getHeaderStyleExpression() {
		return this.headerStyleExpression;
	}
	public String setHeaderStyleExpression(String expression) {
		return this.headerStyleExpression = AmiWebUtils.toCssExpression(expression);
	}

	public String getHeaderStyle() {
		return this.headerStyle;
	}

	public void setHeaderStyle(String headerStyle) {
		this.headerStyle = headerStyle;
		setHeaderStyleExpression(headerStyle);
	}

	public FastWebTreeColumn getColumn() {
		return this.column;
	}

	public void setColumn(FastWebTreeColumn column) {
		this.column = column;
	}

	public String getAmiId() {
		return amiId;
	}
	public int getColumnId() {
		return columnId;
	}

	public AmiWebTreePortlet getTree() {
		return tree;
	}

	public AmiWebTreeGroupByFormatter getFormatter() {
		return formatter;
	}

	public void setAmiId(String amiId) {
		if (OH.eq(this.amiId, amiId))
			return;
		this.amiId = amiId;
		updateAri();
	}

	@Override
	public String toString() {
		return toDerivedString();
	}
	@Override
	public String toDerivedString() {
		return getAri();
	}

	@Override
	public StringBuilder toDerivedString(StringBuilder sb) {
		return sb.append(getAri());
	}
	@Override
	public String getAri() {
		return this.ari;
	}
	@Override
	public void updateAri() {
		String oldAri = this.ari;
		this.amiLayoutFullAlias = this.tree.getAmiLayoutFullAlias();
		this.amiLayoutFullAliasDotId = this.tree.getAmiLayoutFullAliasDotId() + "?" + getDomLabel();
		this.ari = AmiWebDomObject.ARI_TYPE_COLUMN + ":" + this.amiLayoutFullAliasDotId;
		if (OH.ne(this.ari, oldAri)) {
			this.tree.getService().getDomObjectsManager().fireAriChanged(this, oldAri);
		}
		FastWebTree fwt = this.tree.getTree();
		fwt.setHtmlIdSelectorForColumn(SH.toString(this.getColumnId()), AmiWebUtils.toHtmlIdSelector(this));
	}
	@Override
	public String getAriType() {
		return ARI_TYPE_COLUMN;
	}
	@Override
	public String getDomLabel() {
		return getAmiId();
	}
	@Override
	public List<AmiWebDomObject> getChildDomObjects() {
		return Collections.EMPTY_LIST;
	}
	@Override
	public AmiWebDomObject getParentDomObject() {
		return this.tree;
	}
	@Override
	public Class<?> getDomClassType() {
		return this.getClass();
	}
	@Override
	public Object getDomValue() {
		return this;
	}
	@Override
	public boolean isTransient() {
		return isTransient;
	}
	@Override
	public void setTransient(boolean isTransient) {
		this.isTransient = isTransient;
	}
	@Override
	public AmiWebAmiScriptCallbacks getAmiScriptCallbacks() {
		return null;
	}

	private boolean isManagedByDomManager = false;

	@Override
	public void addToDomManager() {
		if (this.isManagedByDomManager == false) {
			AmiWebService service = this.tree.getService();
			service.getDomObjectsManager().addManagedDomObject(this);
			service.getDomObjectsManager().fireAdded(this);
			this.isManagedByDomManager = true;

		}
	}

	@Override
	public void removeFromDomManager() {
		AmiWebDomObjectsManager domObjectsManager = this.tree.getService().getDomObjectsManager();
		for (AmiWebDomObject i : this.getChildDomObjects())
			domObjectsManager.fireRemoved(i);
		domObjectsManager.fireRemoved(this);

		if (this.isManagedByDomManager == true) {
			//Remove DomValues First

			//Remove Self
			AmiWebService service = this.tree.getService();
			service.getDomObjectsManager().removeManagedDomObject(this);
			this.isManagedByDomManager = false;
		}
	}

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
		return this.tree.getService();
	}

	@Override
	public com.f1.base.CalcTypes getFormulaVarTypes(AmiWebFormula f) {
		return this.tree.getFormulaVarTypes(f);
	}

	public byte getFormatterType() {
		return formatterType;
	}
	public int getDecimals() {
		return this.decimals;
	}

	public void setFormatterType(byte formatterType) {
		this.formatterType = formatterType;
	}

	public void setDecimals(int decimals) {
		this.decimals = decimals;
	}
	@Override
	public String objectToJson() {
		return DerivedHelper.toString(this);
	}

}
