package com.f1.ami.plugins.rproject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import javax.script.ScriptEngine;

import org.apache.commons.math.complex.Complex;
import org.renjin.primitives.io.serialization.RDataReader;
import org.renjin.script.RenjinScriptEngineFactory;
import org.renjin.sexp.AtomicVector;
import org.renjin.sexp.AttributeMap;
import org.renjin.sexp.Closure;
import org.renjin.sexp.ComplexVector;
import org.renjin.sexp.DoubleVector;
import org.renjin.sexp.IntVector;
import org.renjin.sexp.ListVector;
import org.renjin.sexp.LogicalVector;
import org.renjin.sexp.PairList;
import org.renjin.sexp.RawVector;
import org.renjin.sexp.SEXP;
import org.renjin.sexp.StringVector;
import org.renjin.sexp.Vector;

import com.f1.ami.amicommon.AmiDatasourceAdapter;
import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiDatasourceTracker;
import com.f1.ami.amicommon.AmiServiceLocator;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.amicommon.msg.AmiCenterQueryResult;
import com.f1.ami.amicommon.msg.AmiCenterUpload;
import com.f1.ami.amicommon.msg.AmiDatasourceColumn;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.sql.SelectClause;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.string.sqlnode.AsNode;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiRDataDatasourceAdapter implements AmiDatasourceAdapter {
	protected static final Logger log = LH.get();
	private String name;
	private ContainerTools tools;
	private AmiServiceLocator serviceName;
	private Map<String, String> options = new HashMap<String, String>();
	private String url;
	private String password;
	private RenjinScriptEngineFactory factory;
	private ScriptEngine engine;
	private String collectionName;
	private SqlProcessor processor;

	public static Map<String, String> buildOptions() {
		return new HashMap<String, String>();
	}

	@Override
	public void init(ContainerTools tools, AmiServiceLocator locator) throws AmiDatasourceException {
		this.tools = tools;
		this.serviceName = locator;
		if (locator.getOptions() != null)
			this.options = SH.splitToMap(options, ',', '=', '\\', locator.getOptions());
		this.name = locator.getTargetName();
		this.url = locator.getUrl();
		this.password = locator.getPassword() == null ? null : new String(locator.getPassword());
		if (log.isLoggable(Level.FINE)) {
			LH.fine(log, getClass().getSimpleName(), " ", name);
		}
		this.processor = new SqlProcessor();
		this.factory = new RenjinScriptEngineFactory();
		this.engine = factory.getScriptEngine();
		if (this.url == null || SH.equals(this.url, "")) {
			this.collectionName = this.name;
		} else {
			File f = new File(this.url);
			this.collectionName = f.getName();
		}
	}

	public SEXP getSEXP(String url) throws AmiDatasourceException {
		try {
			if (url == null || SH.equals(url, ""))
				return null;
			else {
				if (SH.endsWithIgnoreCase(url, ".r")) {
					return (SEXP) engine.eval(new FileReader(url));
				} else if (SH.endsWithIgnoreCase(url, ".rdata")) {
					RDataReader reader = new RDataReader(new GZIPInputStream(new FileInputStream(url)));
					return reader.readFile();
				}
			}
			return null;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.INITIALIZATION_FAILED, "For Url: " + url, e);
		}
	}
	private Object getElementAs(SEXP sexp, Class<?> type, int pos) {
		Object ret;

		if (sexp instanceof Vector) {
			Vector v = (Vector) sexp;
			if (sexp instanceof ComplexVector) {
				Complex c = v.getElementAsComplex(pos);
				double real = c.getReal();
				double imag = c.getImaginary();
				ret = SH.toString(real) + (imag >= 0.0 ? '+' : "") + SH.toString(imag) + 'i';
			} else if (sexp instanceof ListVector) {
				ret = SH.toString(v.getElementAsObject(pos));
			} else if (sexp instanceof RawVector) {
				ret = SH.toString(v.getElementAsObject(pos));
			} else
				ret = v.getElementAsObject(pos);
		} else if (sexp instanceof Closure) {
			Closure c = (Closure) sexp;
			ret = c.toString() + c.getBody().toString();
		} else {
			ret = sexp.asString();
		}
		return ret;
	}
	public AmiDatasourceTable getAmiDatasourceTable(SEXP sexp, String name) {
		AmiDatasourceTable table = tools.nw(AmiDatasourceTable.class);
		table.setCollectionName(this.collectionName);
		table.setName(name);

		table.setCustomQuery("SELECT * FROM " + SH.doubleQuote(name) + " where true ");
		table.setColumns(new ArrayList<AmiDatasourceColumn>());
		return table;
	}

	@Override
	public List<AmiDatasourceTable> getTables(AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		SEXP sexp = getSEXP(url);

		List<AmiDatasourceTable> tables = new ArrayList<AmiDatasourceTable>();
		if (!sexp.hasNames() || SH.endsWithIgnoreCase(url, ".r")) {
			tables.add(getAmiDatasourceTable(sexp, this.name));
		} else {
			AtomicVector names = sexp.getNames();
			int len = names.length();
			for (int i = 0; i < len; i++) {
				String ename = names.getElementAsString(i);
				SEXP s = sexp.getElementAsSEXP(i);
				tables.add(getAmiDatasourceTable(s, ename));
			}
		}

		return tables;
	}
	@Override
	public List<AmiDatasourceTable> getPreviewData(List<AmiDatasourceTable> tables, int previewCount, AmiDatasourceTracker debugSink, TimeoutController tc)
			throws AmiDatasourceException {
		for (int i = 0; i < tables.size(); i++) {
			AmiCenterQuery query = tools.nw(AmiCenterQuery.class);

			AmiDatasourceTable table = tables.get(i);
			query.setQuery(table.getCustomQuery());
			query.setLimit(previewCount);

			List<Table> cols = this.processQuery(query, debugSink, tc);

			if (cols.size() > 1)
				throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Unable to handle multiple tables");

			table.setPreviewData(cols.get(0));
		}
		return tables;
	}
	public String getName() {
		return name;
	}

	private void addColumn(Table table, SEXP sexp, String id) {
		Class<?> type;
		if (sexp instanceof IntVector) {
			type = Integer.class;
		} else if (sexp instanceof DoubleVector) {
			type = Double.class;
		} else if (sexp instanceof LogicalVector) {
			type = Boolean.class;
		} else if (sexp instanceof StringVector) {
			type = String.class;
		} else if (sexp instanceof ComplexVector) {
			type = String.class;
		} else if (sexp instanceof RawVector) {
			type = String.class;
		} else if (sexp instanceof ListVector) {
			type = String.class;
		} else if (sexp instanceof Closure) {
			type = String.class;
		} else
			type = Object.class;
		table.addColumn(type, id);
	}
	private Table toTable(SEXP sexp, String name, String as) throws AmiDatasourceException {
		Table table = new BasicTable();

		String s3 = sexp.getS3Class().toString();

		addColumn(table, sexp, as);
		if (sexp.hasAttributes()) {
			AttributeMap att = sexp.getAttributes();
			if ("data.frame".equals(s3)) {
				if (att.hasNames()) {
					StringVector names = att.getNames();
					for (int i = 0; i < names.length(); i++) {
						addColumn(table, sexp.getElementAsSEXP(i), names.getElementAsString(i));
					}
				}
			} else {
				if ("factor".equals(s3)) {
					SEXP levels = att.get("levels");
					addColumn(table, levels, "levels");
				}
				ListVector vec = att.toVector();
				PairList pl = att.asPairList();
				if (att.hasNames()) {
					addColumn(table, att.getNames(), "names");
				}
				int dimLen = att.getDim().length();
				Vector dimNames = att.getDimNames();
				AtomicVector dimNamesNames = null;
				if (dimNames.hasAttributes())
					dimNamesNames = dimNames.getAttributes().getNamesOrNull();
				for (int i = 0; i < dimLen; i++) {
					String dimNameName = "dim" + i;

					if (dimNamesNames != null)
						dimNameName = (String) dimNamesNames.getElementAsObject(i);
					SEXP dim = att.getDim().getElementAsSEXP(i);
					if (dimNames != null && dimNames.length() > 0) {
						dim = dimNames.getElementAsSEXP(i);
					}
					addColumn(table, dim, dimNameName);
				}
			}
		}

		return table;
	}
	private void getData(SEXP sexp, Table table, String n, String as) {
		com.f1.base.CalcTypes coltotype = table.getColumnTypesMapping();

		String s3 = sexp.getS3Class().toString();
		AttributeMap att = null;
		AtomicVector names = null;
		Vector dim = null;
		Vector dimNames = null;
		AtomicVector dimNamesNames = null;
		AtomicVector levels = null;
		if (sexp.hasAttributes()) {
			att = sexp.getAttributes();
			if (att.hasNames()) {
				names = att.getNamesOrNull();
			}
			dim = att.getDim();
			dimNames = att.getDimNames();
			if (dimNames.hasAttributes())
				dimNamesNames = dimNames.getAttributes().getNamesOrNull();
			if ("factor".equals(s3)) {
				levels = (AtomicVector) att.get("levels");
			}
		}
		if ("data.frame".equals(s3)) {
			SEXP rownames = att.get("row.names");
			int len = rownames.length();
			for (int i = 0; i < len; i++) {
				Row r = table.newEmptyRow();
				Class<?> type = coltotype.getType(as);
				Object value = getElementAs(rownames, type, i);
				r.put(as, value);

				for (int ni = 0; ni < names.length(); ni++) {
					SEXP nameElement = sexp.getElementAsSEXP(ni);
					String nameName = names.getElementAsString(ni);
					Class<?> nameType = coltotype.getType(nameName);
					Object nameValue = getElementAs(nameElement, nameType, i);
					r.put(nameName, nameValue);
				}
				table.getRows().add(r);
			}
		} else {
			int len = sexp.length();
			for (int i = 0; i < len; i++) {
				Row r = table.newEmptyRow();
				// Put value column data
				Class<?> type = coltotype.getType(as);
				Object value = getElementAs(sexp, type, i);

				r.put(as, value);
				// Put name column
				if (names != null) {
					r.put("names", names.getElementAsObject(i));
				}
				// Put dimensions columns
				if (dim != null) {
					int totDimSize = 1;
					for (int di = 0; di < dim.length(); di++) {
						int dimSize = dim.getElementAsInt(di);
						String dimNameName = "dim" + di;
						if (dimNamesNames != null)
							dimNameName = (String) dimNamesNames.getElementAsObject(di);
						int dimPos = ((int) (i / totDimSize)) % dimSize;
						if (dimNames != null && dimNames.length() > 0) {
							AtomicVector dimName = (AtomicVector) dimNames.getElementAsSEXP(di);
							Object dimVal = dimName.getElementAsObject(dimPos);
							r.put(dimNameName, dimVal);
						} else {
							r.put(dimNameName, dimPos + 1);
						}
						totDimSize *= dimSize;
					}
				}
				if (levels != null && value instanceof Integer) {
					r.put("levels", levels.getElementAsObject((Integer) value - 1));
				}
				table.getRows().add(r);
			}
		}

	}
	@Override
	public void processQuery(AmiCenterQuery query, AmiCenterQueryResult resultSink, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> t = processQuery(query, debugSink, tc);
		resultSink.setTables(t);
	}
	protected List<Table> processQuery(AmiCenterQuery query, AmiDatasourceTracker debugSink, TimeoutController tc) throws AmiDatasourceException {
		List<Table> r = new ArrayList<Table>();

		SelectClause selectC = processor.toSelectClause(query.getQuery());

		//		int limit = query.getLimit();
		//		Limits limits = selectC.getLimits();
		//		if (limit != NO_LIMIT && limits == null) {
		//			selectC = processor.toSelectClause(createLimitClause(query.getQuery(), limit));
		//		}
		String select = selectC.getSelect().toString();
		AsNode[] tables = selectC.getTables();

		debugSink.onQuery(select);
		//1 Get sexp from url
		SEXP sexp = getSEXP(this.url);
		//2 loop through each table and get the data
		Tableset tableset = new TablesetImpl();
		for (int i = 0; i < tables.length; i++) {
			String n = tables[i].getValue().toString();
			String as = tables[i].getAs().toString();

			//2.1 Get sexp node to obtain data from
			SEXP element;
			if (!sexp.hasNames() || SH.endsWithIgnoreCase(url, ".r")) {
				element = sexp;
			} else {
				int indx = -1;
				AtomicVector names = sexp.getNames();
				for (int z = 0; z < names.length(); z++) {
					if (SH.equals(n, SH.doubleQuote(names.getElementAsString(z)))) {
						indx = z;
						break;
					}
					if (SH.equals(n, names.getElementAsString(z))) {
						indx = z;
						break;
					}
				}
				if (indx == -1) {
					throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Could not parse column " + name);
				}
				element = sexp.getElementAsSEXP(indx);
			}
			Table table = toTable(element, n, as);
			//			if (limit != 0)
			getData(element, table, n, as);
			tableset.putTable(n, table);
		}
		//3 Process and return the result
		Table rs = processor.process(select, new TopCalcFrameStack(tableset, query.getLimit(), tc, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		debugSink.onQueryResult(select, rs);
		r.add(rs);
		return r;
	}

	@Override
	public boolean cancelQuery() {
		return false;
	}

	@Override
	public void processUpload(AmiCenterUpload upload, AmiCenterQueryResult results, AmiDatasourceTracker tracker) throws AmiDatasourceException {
		throw new AmiDatasourceException(AmiDatasourceException.UNSUPPORTED_OPERATION_ERROR, "Upload to datasource");
	}
	@Override
	public AmiServiceLocator getServiceLocator() {
		return this.serviceName;
	}

}
