package com.f1.ami.web.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.web.AmiWebFormulasImpl;
import com.f1.ami.web.AmiWebLayoutFile;
import com.f1.ami.web.AmiWebPortlet;
import com.f1.ami.web.AmiWebService;
import com.f1.ami.web.AmiWebUtils;
import com.f1.ami.web.dm.AmiWebDm;
import com.f1.ami.web.dm.AmiWebDmTableSchema;
import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.CasterManager;
import com.f1.utils.ColorHelper;
import com.f1.utils.LH;
import com.f1.utils.MH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.casters.Caster_Integer;
import com.f1.utils.casters.Caster_Simple;
import com.f1.utils.casters.Caster_String;
import com.f1.utils.sql.aggs.AbstractAggCalculator;
import com.f1.utils.sql.aggs.AggCalculator;
import com.f1.utils.sql.aggs.AggregateFactory;
import com.f1.utils.structs.BasicIndexedList;
import com.f1.utils.structs.IndexedList;
import com.f1.utils.structs.IntKeyMap;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.BasicCalcTypes;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public abstract class AmiWebChartSeries extends AmiWebFormulasImpl {
	private static final Logger log = LH.get();
	public static final byte TYPE_NUMBER = AmiWebChartFormula.TYPE_NUMBER;
	public static final byte TYPE_STRING = AmiWebChartFormula.TYPE_STRING;
	public static final byte TYPE_COLOR = AmiWebChartFormula.TYPE_COLOR;
	public static final byte TYPE_SHAPE = AmiWebChartFormula.TYPE_SHAPE;
	public static final byte TYPE_CONST = AmiWebChartFormula.TYPE_CONST;
	public static final byte TYPE_BOOLEAN = AmiWebChartFormula.TYPE_BOOLEAN;
	public static final byte TYPE_POSITION = AmiWebChartFormula.TYPE_POSITION;
	public static final byte TYPE_FONT = AmiWebChartFormula.TYPE_FONT;
	public static final byte TYPE_LINE_TYPE = AmiWebChartFormula.TYPE_LINE_TYPE;
	public static final byte TYPE_SORTABLE = AmiWebChartFormula.TYPE_SORTABLE;
	public static final byte TYPE_AXIS = AmiWebChartFormula.TYPE_AXIS;

	final public static byte SERIES_TYPE_XY = 1;
	final public static byte SERIES_TYPE_RADIAL = 2;
	final public static byte SERIES_TYPE_SURFACE = 3;

	public final static String PARAM_DESC_FONT_FAMILY = "descFontFam";
	public final static String PARAM_LN_DIR = "lnDir";
	private static final Comparator<? super Tuple2<Comparable, Grouping>> COMPARATOR = new Comparator<Tuple2<Comparable, Grouping>>() {
		@Override
		public int compare(Tuple2<Comparable, Grouping> o1, Tuple2<Comparable, Grouping> o2) {
			return OH.compare(o1.getA(), o2.getA());
		}
	};

	public static class OrderbByClauseComparator implements Comparator<Row> {

		final private DerivedCellCalculator calc;
		final private ReusableCalcFrameStack rsf;

		public OrderbByClauseComparator(DerivedCellCalculator calc, ReusableCalcFrameStack rsf) {
			this.calc = calc;
			this.rsf = rsf;
		}

		@Override
		public int compare(Row l, Row r) {
			return OH.compare((Comparable) calc.get(rsf.reset(l)), (Comparable) calc.get(rsf.reset(r)));
		}

	}

	public static class OrderbByAggClauseComparator implements Comparator<Row> {

		final private AbstractAggCalculator calc;
		final private List<CalcFrame> tmp = new ArrayList<CalcFrame>();
		final private ReusableCalcFrameStack rsf;

		public OrderbByAggClauseComparator(AbstractAggCalculator calc, ReusableCalcFrameStack rsf) {
			this.calc = calc;
			this.rsf = rsf;
		}

		@Override
		public int compare(Row l, Row r) {
			tmp.clear();
			tmp.add(l);
			calc.visitRows(rsf, tmp);
			Object val1 = calc.get(null);
			tmp.clear();
			tmp.add(r);
			calc.visitRows(rsf, tmp);
			Object val2 = calc.get(null);
			return OH.compare((Comparable) val1, (Comparable) val2);
		}

	}

	final protected Wrapper tmpWrapper;
	final private AggregateFactory methodFactory;
	final private AmiWebService service;
	private final AmiWebLayoutFile layout;
	private final AmiWebPortlet portlet;

	final private List<AmiWebChartSeriesListener> listeners = new ArrayList<AmiWebChartSeriesListener>();
	final private IndexedList<String, AmiWebChartFormula> formulas = new BasicIndexedList<String, AmiWebChartFormula>();
	final private IndexedList<String, Grouping> groupings = new BasicIndexedList<String, Grouping>();
	final private IntKeyMap<Grouping> groupingsById = new IntKeyMap<Grouping>();

	final private AmiWebChartFormula_Simple nameFormula;
	final private AmiWebChartFormula_Simple whereFormula;
	final private AmiWebChartFormula_Simple orderByFormula;

	private int seriesId;
	private int position;
	private String name;
	private String dmTableName;
	private boolean formulasChanged;
	final private byte type;

	private boolean needsRefresh;
	private String currentGroup;
	private AmiWebChartSeriesContainer<?> layer;

	public AmiWebChartSeries(AmiWebService service, AmiWebPortlet portlet, AmiWebDmTableSchema model, byte type, AmiWebChartSeriesContainer<?> layer) {
		super(portlet);
		this.service = service;
		this.tmpWrapper = new Wrapper();
		this.portlet = portlet;
		this.type = type;
		if (layer != null)
			this.name = layer.getName();
		this.layer = layer;
		switch (this.type) {
			case SERIES_TYPE_RADIAL:
				this.editorTypeId = AmiWebChartEditSeriesPortlet.TYPE_RADIAL_ADVANCED;
				break;
			case SERIES_TYPE_XY:
				this.editorTypeId = AmiWebChartEditSeriesPortlet.TYPE_2D_ADVANCED;
				break;
			case SERIES_TYPE_SURFACE:
				this.editorTypeId = AmiWebChartEditSeriesPortlet.TYPE_3D_ADVANCED;
				break;
			default:
				throw new RuntimeException("Unknown type: " + this.type);
		}
		if (model == null)
			setDatamodel(null, null);
		else {
			setDatamodel(model.getDm().getAmiLayoutFullAliasDotId(), model.getName());
		}
		startGroup("Options");
		this.methodFactory = portlet == null ? null : portlet.getScriptManager().createAggregateFactory();
		super.setAggregateFactory(this.methodFactory);
		this.layout = portlet == null ? null : service.getLayoutFilesManager().getLayoutByFullAlias(portlet.getAmiLayoutFullAlias());
		this.whereFormula = addFormula("where", "Where:", TYPE_BOOLEAN).setHidden();
		this.nameFormula = addFormula("name", "Group By:", TYPE_STRING);
		this.orderByFormula = addFormula("orderBy", "Order By:", TYPE_SORTABLE).setHidden();
	}

	public AmiWebChartSeriesContainer<?> getLayer() {
		return this.layer;
	}
	protected void startGroup(String string) {
		this.currentGroup = string;
	}
	public void setDatamodel(String dmAliasDotName, String dmTableName) {
		this.dmAliasDotName = dmAliasDotName;
		this.dmTableName = dmTableName;
	}
	public boolean hasAggregates() {
		return !this.methodFactory.getAggregates().isEmpty();
	}
	public void addListener(AmiWebChartSeriesListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(AmiWebChartSeriesListener listener) {
		this.listeners.remove(listener);
	}

	public void fireOnDataChanged() {
		for (AmiWebChartSeriesListener i : this.listeners)
			i.onDataChanged(this);
	}

	public void fireOnRemoved() {
		for (AmiWebChartSeriesListener i : this.listeners)
			i.onRemoved(this);
	}

	public static class Grouping {
		final private List<Row> origRows;
		final private String groupName;
		final private Map<String, List> values = new HashMap<String, List>();
		final private int groupId;
		final private AmiWebChartSeries series;

		public Grouping(AmiWebChartSeries series, int groupId, String groupName, int size) {
			origRows = new ArrayList<Row>(size);
			this.groupName = groupName;
			this.groupId = groupId;
			this.series = series;
		}

		public AmiWebChartSeries getSeries() {
			return this.series;
		}

		public List<Row> getOrigRows() {
			return this.origRows;
		}
		public List<Object> getValuesForFormula(String name) {
			return OH.noNull(values.get(name), Collections.EMPTY_LIST);
		}

		public int getSize() {
			return origRows.size();
		}

		public String getName() {
			return groupName;
		}

		public int getId() {
			return groupId;
		}

		public void putValues(String name, List<? extends Object> vals) {
			this.values.put(name, vals);
		}

	}

	public String getLegendName(Grouping group) {
		if (this.groupings.getSize() == 1 && this.getNameFormula().isConst() && SH.isnt(this.getNameFormula().getConstValue())) {
			return SH.isnt(this.name) ? "Unnamed Series" : this.name;
		}
		return group.getName();
	}

	protected Object getFirst(Grouping group, AmiWebChartFormula t) {
		Object r;
		if (t.isConst()) {
			r = t.getConstValue();
		} else {
			List<Object> vals = group.getValuesForFormula(t.getName());
			r = CH.isntEmpty(vals) ? vals.get(0) : null;
		}
		if (r != null && t instanceof AmiWebChartFormula_Color) {
			Color c = layer.getColors((AmiWebChartFormula_Color) t, CH.l(r))[0];
			r = c == null ? null : ColorHelper.toString(c);
		}
		return r;
	}

	public void clearData() {
		groupings.clear();
		groupingsById.clear();
		this.selectedGroupings = null;
	}

	public void buildData(AmiWebDmTableSchema amiWebDmTableSchema, CalcFrameStack sf) {
		if (formulasChanged) {
			if (this.methodFactory.getAggregates().size() > 0) {
				this.methodFactory.getAggregates().clear();
				//				for (AmiWebChartFormula i : this.formulas.values())
				//					i.setValue(i.value.get());
				this.formulasChanged = false;
			}
		}
		Table table = amiWebDmTableSchema.getDm().getResponseTableset().getTableNoThrow(amiWebDmTableSchema.getName());
		buildData(table, sf);
	}
	public void buildData(Table table, CalcFrameStack sf) {
		// Check for bad formulas
		ReusableCalcFrameStack rsf = new ReusableCalcFrameStack(sf);
		StringBuilder sb = new StringBuilder();
		for (AmiWebChartFormula f : this.formulas.values()) {
			if (!f.testValue(f.getValue(), sb, table)) {
				return;
			}
		}
		clearData();
		int id = 0;
		boolean hasWhere;
		if (this.getWhereFormula().isConst()) {
			if (Boolean.FALSE.equals(getWhereFormula().getConstValue()))
				return;
			hasWhere = false;
		} else {
			hasWhere = true;
		}
		if (table != null)
			for (Row row : table.getRows()) {
				rsf.reset(row);
				if (hasWhere && !Boolean.TRUE.equals(getWhereFormula().getDataInner(rsf)))
					continue;
				String val = AmiUtils.snn(getNameFormula().getDataInner(rsf), "");
				Grouping list = groupings.getNoThrow(val);

				if (list == null) {
					groupings.add(val, list = new Grouping(this, id++, val, 10));
					groupingsById.put(list.getId(), list);
				}
				list.origRows.add(row);
			}
		if (!this.getOrderByFormula().isConst()) {
			DerivedCellCalculator calc = this.getOrderByFormula().getCalc();
			if (calc instanceof AbstractAggCalculator) {
				AbstractAggCalculator agg = (AbstractAggCalculator) calc;

				List<Tuple2<Comparable, Grouping>> l = new ArrayList<Tuple2<Comparable, Grouping>>();
				for (Grouping rows : groupings.values()) {
					agg.visitRows(rsf, rows.origRows);
					Comparable val = (Comparable) agg.get(null);
					l.add(new Tuple2<Comparable, Grouping>(val, rows));
				}
				Collections.sort(l, COMPARATOR);
				groupings.clear();
				groupingsById.clear();
				OrderbByAggClauseComparator orderByClauseComparator = new OrderbByAggClauseComparator(agg, rsf);
				for (int i = 0; i < l.size(); i++) {
					Grouping v = l.get(i).getB();
					Collections.sort(v.origRows, orderByClauseComparator);
					groupings.add(v.getName(), v);
					groupingsById.put(v.getId(), v);
				}
			} else {
				OrderbByClauseComparator orderByClauseComparator = new OrderbByClauseComparator(calc, rsf);
				for (Grouping rows : groupings.values()) {
					Collections.sort(rows.origRows, orderByClauseComparator);
				}
			}
		}
		for (AmiWebChartFormula formula : this.formulas.values())
			formula.clearCache();
		for (int sernum = 0; sernum < groupings.getSize(); sernum++) {
			tmpWrapper.setSerNum(sernum, groupings.getSize());
			final Grouping group = groupings.getAt(sernum);
			final List<Row> rows = group.origRows;
			resetAggregatesForRows(rows, rsf);
			final int size = rows.size();
			for (AmiWebChartFormula formula : this.formulas.values()) {
				if (formula.getIsHidden())
					continue;
				if (size == 0)
					group.values.put(formula.getName(), Collections.EMPTY_LIST);
				else if (formula.isConst()) {
					if (formula.getConstValue() != null)
						group.values.put(formula.getName(), Collections.singletonList(formula.getConstValue()));
				} else {
					tmpWrapper.reset(rows.get(0), 0);
					rsf.reset(tmpWrapper);
					Object first = formula.getDataInner(rsf);
					int j = 1;
					List<Object> values = null;
					for (; j < size; j++) {
						tmpWrapper.reset(rows.get(j), j);
						Object value = formula.getDataInner(rsf);
						if (OH.ne(first, value)) {
							values = new ArrayList<Object>(size);
							for (int k = 0; k < j; k++)
								values.add(first);
							values.add(value);
							j++;
							break;
						}
					}
					if (values == null) {
						group.values.put(formula.getName(), Collections.singletonList(first));
					} else {
						for (; j < size; j++) {
							tmpWrapper.reset(rows.get(j), j);
							values.add(formula.getDataInner(rsf));
						}
						group.values.put(formula.getName(), values);
					}
				}
			}
			postProcess(group);
		}
		for (AggCalculator i : this.methodFactory.getAggregates())
			i.reset();
		lastResetRows = null;
		fireOnDataChanged();
	}
	protected void postProcess(Grouping group) {
	}

	private List<Row> lastResetRows;
	private String editorTypeId;
	private String dmAliasDotName;
	private Set<Integer> selectedGroupingIds;
	private IndexedList<String, Grouping> selectedGroupings;

	public void resetAggregatesForRows(List<Row> rows, ReusableCalcFrameStack sf) {
		if (rows == lastResetRows)
			return;
		sf = new ReusableCalcFrameStack(sf);
		for (AggCalculator i : this.methodFactory.getAggregates()) {
			i.reset();
			i.visitRows(sf, rows);
		}
		lastResetRows = rows;
	}
	public IndexedList<String, Grouping> getAllGroupings() {
		return this.groupings;
	}
	public IndexedList<String, Grouping> getUserSelectedGroupings() {
		if (this.selectedGroupingIds != null) {
			if (this.selectedGroupings == null) {
				this.selectedGroupings = new BasicIndexedList<String, Grouping>();
				for (Entry<String, Grouping> i : this.groupings) {
					if (this.selectedGroupingIds.contains(i.getValue().getId()))
						selectedGroupings.add(i.getKey(), i.getValue());
				}
			}
			return this.selectedGroupings;
		}
		return this.groupings;
	}
	public AmiWebChartFormula_Color addColorFormula(String name, String label) {
		AmiWebChartFormula_Color formula = new AmiWebChartFormula_Color(this, this.currentGroup, name, label);
		this.formulas.add(name, formula);
		super.addFormula(formula);
		return formula;
	}

	public AmiWebChartFormula_Simple addFormula(String name, String label, byte type) {
		AmiWebChartFormula_Simple formula = new AmiWebChartFormula_Simple(this, this.currentGroup, name, label, type);
		this.formulas.add(name, formula);
		super.addFormula(formula);
		return formula;
	}
	//	public AmiWebChartFormula addFormula(String name, String label, byte type, int position) {
	//		AmiWebChartFormula formula = type == TYPE_COLOR ? new AmiWebChartFormula_Color(this, this.currentGroup, name, label)
	//				: new AmiWebChartFormula(this, this.currentGroup, name, label, type);
	//		this.formulas.add(name, formula, position);
	//		return formula;
	//	}

	@Override
	public AmiWebChartFormula getFormula(String name) {
		return formulas.getNoThrow(name);
	}

	public int getFormulasCount() {
		return this.formulas.getSize();
	}

	public void refresh() {
		if (needsRefresh)
			return;
		this.needsRefresh = true;
	}

	public int getId() {
		return seriesId;
	}

	public void setId(int seriesId) {
		this.seriesId = seriesId;
	}

	public static final String VARNAME_ROWNUM = "__row_num";
	public static final String VARNAME_SERIESNUM = "__series_num";
	public static final String VARNAME_SERIESCNT = "__series_cnt";
	public static final com.f1.utils.structs.table.stack.BasicCalcTypes VARTYPES = new BasicCalcTypes();
	static {
		VARTYPES.putType(VARNAME_ROWNUM, Integer.class);
		VARTYPES.putType(VARNAME_SERIESNUM, Integer.class);
		VARTYPES.putType(VARNAME_SERIESCNT, Integer.class);
		VARTYPES.putType("this", AmiWebChartRenderingLayer.class);
		VARTYPES.putType("layout", AmiWebLayoutFile.class);
	}

	protected void flagFormulasChanged() {
		this.formulasChanged = true;
	}

	public AggregateFactory getMethodFactory() {
		return this.methodFactory;
	}

	@Override
	public AmiWebChartSeriesContainer<?> getThis() {
		return layer;
	}
	public AmiWebPortlet getPortlet() {
		return this.portlet;
	}
	public AmiWebService getService() {
		return this.service;
	}
	public String getDescription() {
		if (SH.is(name))
			return name;
		else
			return "Series #" + (1 + getPosition());
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> r = new HashMap<String, Object>();
		r.put("id", seriesId);
		r.put("seriesName", name);
		r.put("editorType", this.editorTypeId);
		for (AmiWebChartFormula i : this.formulas.values())
			if (i.getValue() != null)
				r.put(i.getName(), i.getConfiguration());
		return r;
	}
	public void init(Map<String, Object> values) {
		this.seriesId = CH.getOrThrow(Caster_Integer.PRIMITIVE, values, "id");
		this.editorTypeId = CH.getOr(Caster_String.INSTANCE, values, "editorType", this.editorTypeId);
		for (AmiWebChartFormula i : this.formulas.values()) {
			if (values.containsKey(i.getName())) {
				Object object = values.get(i.getName());
				try {
					i.init(object);
				} catch (Exception e) {
					LH.info(log, "Error with formula " + i.getName(), e);
				}

			}
		}
	}

	public AmiWebChartFormula getFormulaAt(int i) {
		return this.formulas.getAt(i);
	}

	private static final Caster<AmiWebChartSeriesContainer> CASTER_AmiWebChartSeriesContainer = CasterManager.getCaster(AmiWebChartSeriesContainer.class);
	private static final Caster<AmiWebLayoutFile> CASTER_AmiWebLayoutFile = CasterManager.getCaster(AmiWebLayoutFile.class);

	protected class Wrapper implements CalcFrame, CalcTypes {

		private CalcFrame inner;
		private int rownum;
		private int sernum;
		private int sercnt;

		public void reset(CalcFrame inner, int rownum) {
			this.inner = inner;
			this.rownum = rownum;
		}
		public void setSerNum(int sernum, int sercnt) {
			this.sernum = sernum;
			this.sercnt = sercnt;
		}
		@Override
		public Object getValue(String key) {
			String s = (String) key;
			if (s == null)
				return null;
			if (s.startsWith("__")) {
				if (VARNAME_ROWNUM.equals(key))
					return Integer.valueOf(rownum);
				else if (VARNAME_SERIESNUM.equals(key))
					return Integer.valueOf(sernum);
				else if (VARNAME_SERIESCNT.equals(key))
					return Integer.valueOf(sercnt);
			} else if ("this".equals(key))
				return AmiWebChartSeries.this.layer;
			else if ("layout".equals(key))
				return AmiWebChartSeries.this.layout;
			return inner.getValue(key);
		}
		@Override
		public Class<?> getType(String key) {
			String s = (String) key;
			if (s == null)
				return null;
			if (s.startsWith("__")) {
				if (VARNAME_ROWNUM.equals(key))
					return Integer.class;
				else if (VARNAME_SERIESNUM.equals(key))
					return Integer.class;
				else if (VARNAME_SERIESCNT.equals(key))
					return Integer.class;
			} else if ("this".equals(key))
				return AmiWebChartSeriesContainer.class;
			else if ("layout".equals(key))
				return AmiWebLayoutFile.class;
			return inner.getType(key);
		}

		@Override
		public Object putValue(String key, Object value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isVarsEmpty() {
			return false;
		}

		@Override
		public Iterable<String> getVarKeys() {
			throw new UnsupportedOperationException();
		}
		@Override
		public int getVarsCount() {
			throw new UnsupportedOperationException();
		}

	}

	public Grouping getGroupById(int groupid) {
		return this.groupingsById.get(groupid);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public AmiWebDmTableSchema getDataModelSchema() {
		AmiWebDm t = service.getDmManager().getDmByAliasDotName(this.dmAliasDotName);
		return t == null ? null : t.getResponseOutSchema().getTable(this.dmTableName);
	}
	//	public AmiWebChartFormula addFormulaAfter(String existing, String name, String label, byte type) {
	//		return this.addFormula(name, label, type, formulas.getPosition(existing) + 1);
	//	}

	public byte getSeriesType() {
		return this.type;
	}

	abstract public String describe(Row row, CalcFrameStack sf);
	abstract public String getTooltip(Row row, CalcFrameStack sf);
	abstract public Integer getLegendLineDash(Grouping group);
	abstract public String getLegendLineColor(Grouping group);
	abstract public Integer getLegendLineSize(Grouping group);
	abstract public String getLegendShapeColor(Grouping group);
	abstract public String getLegendShapeBorderColor(Grouping group);
	abstract public String getLegendShape(Grouping group);

	public AmiWebChartFormula_Simple getWhereFormula() {
		return whereFormula;
	}

	public AmiWebChartFormula_Simple getNameFormula() {
		return nameFormula;
	}

	public AmiWebChartFormula_Simple getOrderByFormula() {
		return orderByFormula;
	}
	public void getUsedColors(Set<String> sink) {
		for (AmiWebChartFormula i : this.formulas.values()) {
			AmiWebUtils.getColors(i.getValue(), sink);
		}

	}
	public static void stack(AmiWebChartFormula stackOn, AmiWebChartFormula stackMin, AmiWebChartFormula stackMax, AmiWebChartFormula left, AmiWebChartFormula right,
			Grouping group) {
		List<Number> stackOnVals = (List) group.getValuesForFormula(stackOn.getName());
		if (CH.isEmpty(stackOnVals))
			return;
		List<Object> stackMinVals = group.getValuesForFormula(stackMin.getName());
		List<Object> stackMaxVals = group.getValuesForFormula(stackMax.getName());
		Number minNum = (Number) Caster_Simple.OBJECT.cast(CH.first(stackMinVals));
		Number maxNum = (Number) Caster_Simple.OBJECT.cast(CH.first(stackMaxVals));
		final List<Double> lVals = CH.isntEmpty(group.getValuesForFormula(left.getName())) ? null : new ArrayList<Double>(stackOnVals.size());
		final List<Double> rVals = CH.isntEmpty(group.getValuesForFormula(right.getName())) ? null : new ArrayList<Double>(stackOnVals.size());
		if (lVals == null && rVals == null)
			return;

		double[] values = AH.toArrayDouble(stackOnVals);
		if (values.length == 1 && group.getSize() > 1)
			values = AH.fill(new double[group.getSize()], values[0]);

		final double tot = MH.sumSkipNan(values);
		final double min = minNum == null ? 0 : minNum.doubleValue();
		final double max = maxNum == null ? (min + tot) : maxNum.doubleValue();
		double x = 0;
		if (lVals != null)
			lVals.add(min);

		if (tot == 0) {
			Double d = Double.valueOf(min);
			for (int i = 0; i < values.length - 1; i++) {
				if (rVals != null)
					rVals.add(d);
				if (lVals != null)
					lVals.add(d);
			}
			if (rVals != null)
				rVals.add(d);
		} else {
			final double diff = (max - min);
			for (int i = 0; i < values.length - 1; i++) {
				double d2 = values[i];
				if (d2 == d2)
					x += d2;
				Double d = Double.valueOf(min + x * diff / tot);
				if (rVals != null)
					rVals.add(d);
				if (lVals != null)
					lVals.add(d);
			}
			if (rVals != null)
				rVals.add(max);
		}
		if (lVals != null)
			group.putValues(left.getName(), lVals);
		if (rVals != null)
			group.putValues(right.getName(), rVals);
	}
	public String getEditorTypeId() {
		return this.editorTypeId;
	}
	public void setEditorTypeId(String editorTypeId) {
		this.editorTypeId = editorTypeId;
	}

	abstract public AmiWebChartFormula getSelectableFormula();

	public String getDmAliasDotName() {
		return this.dmAliasDotName;
	}
	public String getDmTableName() {
		return this.dmTableName;
	}
	public void onDmNameChanged(String oldAliasDotName, AmiWebDm dm) {
		if (OH.eq(oldAliasDotName, this.dmAliasDotName))
			this.dmAliasDotName = dm.getAmiLayoutFullAliasDotId();
	}
	public boolean clearOverrides() {
		boolean r = false;
		for (AmiWebChartFormula i : this.formulas.values())
			if (i.clearOverride())
				r = true;
		return r;
	}
	public void setSelectedGroupings(Set<Integer> set) {
		if (OH.eq(this.selectedGroupingIds, set))
			return;
		this.selectedGroupingIds = set == null ? null : new HashSet<Integer>(set);
		this.selectedGroupings = null;
		this.getLayer().flagNeedsRepaint();
	}

}
