package com.f1.ami.center.ds;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.base.Caster;
import com.f1.base.Row;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.IOH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.impl.StringCharReader;
import com.f1.utils.sql.SqlPlanListener;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.TimeoutController;
import com.f1.utils.structs.table.online.OnlineTable;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class AmiDatasourceUtils {

	public static Table parseCsv(String csv) throws IOException {
		StringCharReader scr = new StringCharReader(csv);
		StringBuilder sink = new StringBuilder();
		Table r = new BasicTable();
		for (;;) {
			if (scr.expectNoThrow('"')) {
				SH.clear(sink);
				for (;;) {
					scr.readUntil('"', sink);
					scr.expect('"');
					if (scr.expectNoThrow('"')) {
						sink.append('"');
					} else
						break;
				}

				String id = AmiUtils.toValidVarName(SH.toStringAndClear(sink));
				id = SH.getNextId(id, (Set) r.getColumnsMap().keySet());
				r.addColumn(String.class, id);
			} else {
				scr.readUntilAny(",\n", false, SH.clear(sink));
				String id = AmiUtils.toValidVarName(SH.toStringAndClear(sink));
				id = SH.getNextId(id, (Set) r.getColumnsMap().keySet());
				r.addColumn(String.class, id);
			}
			if (scr.expectNoThrow(','))
				continue;
			scr.expect('\n');
			break;
		}
		for (;;) {
			if (scr.isEof())
				break;
			Row row = r.newEmptyRow();
			for (int pos = 0;; pos++) {
				if (scr.expectNoThrow('"')) {
					SH.clear(sink);
					for (;;) {
						scr.readUntil('"', sink);
						scr.expect('"');
						if (scr.expectNoThrow('"')) {
							sink.append('"');
						} else
							break;
					}

					row.putAt(pos, SH.toStringAndClear(sink));
				} else {
					scr.readUntilAny(",\n", false, SH.clear(sink));
					row.putAt(pos, SH.toStringAndClear(sink));
				}
				if (scr.expectNoThrow(','))
					continue;
				scr.expect('\n');
				break;
			}
			r.getRows().add(row);
		}
		return r;
	}

	/*
	 * Generic getOptional, use getOptionalStrict instead
	 */
	static public <V> V getOptional(Class<V> cast, Map<String, ?> d, String key) {
		if (d == null)
			return null;
		Object value = d.remove(key);
		return OH.cast(value, cast);
	}

	/*
	 * Strict Generic getOptional 
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getOptionalStrict(Class<V> cast, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object value = d.remove(key);
		if (value == null)
			return null;
		if (!cast.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type " + cast.getSimpleName());
		return (V) value;
	}

	/*
	 * Generic getOptional returns default value if null, use getOptionalStrict instead
	 */
	static public <V> V getOptional(Class<V> cast, Map<String, ?> d, String key, V dflt) {
		if (d == null)
			return dflt;
		boolean ne = !d.containsKey(key);
		Object value = d.remove(key);
		if (value == null && ne)
			return dflt;

		return OH.cast(value, cast);
	}
	/*
	 * Strict Generic getOptional returns default value if null 
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getOptionalStrict(Class<V> cast, Map<String, ?> d, String key, V dflt) throws AmiDatasourceException {
		if (d == null)
			return dflt;
		Object value = d.remove(key);
		if (value == null)
			return dflt;
		if (!cast.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type " + cast.getSimpleName());
		return (V) value;
	}

	/* 
	 * Generic getOptional using caster, use getOptionalStrict instead
	 */
	static public <V> V getOptional(Caster<V> caster, Map<String, ?> d, String key) {
		if (d == null)
			return null;
		Object value = d.remove(key);
		return caster.cast(value);
	}

	/*
	 * Strict Generic getOptional using caster
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getOptionalStrict(Caster<V> caster, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object value = d.remove(key);
		if (value == null)
			return null;
		Class<V> cast = caster.getCastToClass();
		if (!cast.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type " + cast.getSimpleName());
		return (V) value;
	}

	/*
	 * Generic getOptional using caster with default value, use getOptionalStrict instead
	 */
	static public <V> V getOptional(Caster<V> caster, Map<String, ?> d, String key, V dflt) {
		if (d == null)
			return dflt;
		boolean ne = !d.containsKey(key);
		Object value = d.remove(key);
		if (value == null && ne)
			return dflt;
		return caster.cast(value);
	}

	/*
	 * Strict Generic getOptional using caster with default value
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getOptionalStrict(Caster<V> caster, Map<String, ?> d, String key, V dflt) throws AmiDatasourceException {
		if (d == null)
			return dflt;
		boolean ne = !d.containsKey(key);
		Object value = d.remove(key);
		if (value == null && ne)
			return dflt;
		Class<V> cast = caster.getCastToClass();
		if (!cast.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type " + cast.getSimpleName());
		return (V) value;
	}

	/*
	 * (String) getOptional, use getOptionalStrict instead
	 */
	static public String getOptional(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object value = d.remove(key);
		if (value == null)
			return null;
		return value.toString();
	}

	/*
	 * Strict (String) getOptional 
	 */
	static public String getOptionalStrict(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object value = d.remove(key);
		if (value == null)
			return null;
		if (!String.class.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		return (String) value;
	}

	/*
	 * (String) getOptional returns default value if null, use getOptionalStrict instead
	 */
	static public String getOptional(Map<String, Object> d, String key, String dflt) throws AmiDatasourceException {
		if (d == null)
			return dflt;
		if (!d.containsKey(key))
			return dflt;
		Object value = d.remove(key);
		if (value == null)
			return dflt;
		return value.toString();
	}
	/*
	 * Strict (String) getOptional returns default value if null
	 */
	static public String getOptionalStrict(Map<String, Object> d, String key, String dflt) throws AmiDatasourceException {
		if (d == null)
			return dflt;
		if (!d.containsKey(key))
			return dflt;
		Object value = d.remove(key);
		if (value == null)
			return dflt;
		if (!String.class.isAssignableFrom(value.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		return (String) value;
	}

	/*
	 * (String) getOptional(Map d, String key, String... (lowercase) enums), throws exception if value is not in set of enums. Use getOptionalStrict instead
	 */
	static public String getOptional(Map<String, Object> d, String key, String... enums) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		String value = obj.toString();
		if (value == null)
			return null;
		if (value != null)
			value = value.toLowerCase();
		if (value != null && AH.indexOf(value, enums) == -1)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be one of: " + SH.join(',', enums));
		return value;
	}

	/*
	 * Strict (String) getOptional(Map d, String key, String... (lowercase) enums), throws exception if value is not in set of enums
	 */
	static public String getOptionalStrict(Map<String, Object> d, String key, String... enums) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		if (!String.class.isAssignableFrom(obj.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		String value = (String) obj;
		if (value != null)
			value = value.toLowerCase();
		if (value != null && AH.indexOf(value, enums) == -1)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be one of: " + SH.join(',', enums));
		return value;
	}

	/*
	 * (String) getRequired(Map d, String key, String... (lowercase) enums), throws exception if value is null or not in set of enums. Use getRequiredStrict instead
	 */
	static public String getRequired(Map<String, Object> d, String key, String... enums) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directives is null");
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		String value = obj.toString();
		if (value != null)
			value = value.toLowerCase();
		if (value == null || AH.indexOf(value, enums) == -1)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be one of: " + SH.join(',', enums));
		return value;
	}

	/*
	 * Strict (String) getRequired(Map d, String key, String... (lowercase) enums), throws exception if value is null or not in set of enums
	 */
	static public String getRequiredStrict(Map<String, Object> d, String key, String... enums) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directives is null");
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		if (!String.class.isAssignableFrom(obj.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		String value = (String) obj;
		if (value != null)
			value = value.toLowerCase();
		if (value == null || AH.indexOf(value, enums) == -1)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be one of: " + SH.join(',', enums));
		return value;
	}

	public static final Pattern DATE_PATTERN = Pattern.compile("\\d\\d\\d\\d\\-\\d\\d\\-\\d\\d");
	public static final Pattern NUMBER_PATTERN = Pattern.compile("\\d+\\.?\\d*");

	/*
	 * (String) getOptionalDate(Map d, String key), use getOptionalDateStrict instead
	 */
	static public String getOptionalDate(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		String value = obj.toString();
		if (value != null && !DATE_PATTERN.matcher(value).matches())
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be in foramt yyyy-mm-dd");
		return value;
	}

	/*
	 * Strict (String) getOptionalDate(Map d, String key)
	 */
	static public String getOptionalDateStrict(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return null;
		Object obj = d.remove(key);
		if (obj == null)
			return null;
		if (!String.class.isAssignableFrom(obj.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		String value = (String) obj;
		if (value != null && !DATE_PATTERN.matcher(value).matches())
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be in foramt yyyy-mm-dd");
		return value;
	}

	/*
	 * (int) getOptionalInt(Map d, String key), use getOptionalIntSemiStrict instead
	 */
	static public int getOptionalInt(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return -1;
		Object obj = d.remove(key);
		if (obj == null)
			return -1;
		String value = obj.toString();
		if (value == null)
			return -1;
		try {
			return SH.parseIntSafe(value, false, true);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be number");
		}
	}

	/*
	 * (int) getOptionalInt(Map d, String key), expects an Integer or a String
	 */
	static public int getOptionalIntSemiStrict(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			return -1;
		Object obj = d.remove(key);
		if (obj == null)
			return -1;
		// Throw if not Integer, or String
		boolean isInt = Integer.class.isAssignableFrom(obj.getClass());
		if (!isInt && !String.class.isAssignableFrom(obj.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type Integer or String");
		if (isInt)
			return (int) obj;
		String value = obj.toString();
		if (value == null)
			return -1;
		try {
			return SH.parseIntSafe(value, false, true);
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " shoud be number");
		}
	}
	/*
	 * Generic getRequired, use getRequiredStrict instead
	 */
	static public <V> V getRequired(Class<V> cast, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		return OH.cast(r, cast);
	}

	/*
	 * Strict Generic getRequired
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getRequiredStrict(Class<V> cast, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		if (!cast.isAssignableFrom(r.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: _" + key + " isn't of type " + cast.getSimpleName());
		return (V) r;
	}

	/*
	 * Generic getRequired, use getRequiredStrict instead
	 */
	static public <V> V getRequired(Caster<V> caster, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		return caster.cast(r);
	}
	/*
	 * Strict Generic getRequired
	 */
	@SuppressWarnings("unchecked")
	static public <V> V getRequiredStrict(Caster<V> caster, Map<String, ?> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		Class<V> cast = caster.getCastToClass();
		if (!cast.isAssignableFrom(r.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type " + cast.getSimpleName());
		return (V) r;
	}

	/*
	 * (String) getRequired, use getRequiredStrict instead
	 */
	static public String getRequired(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		return r.toString();
	}

	/*
	 * Strict (String) getRequired
	 */
	static public String getRequiredStrict(Map<String, Object> d, String key) throws AmiDatasourceException {
		if (d == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "directives is null");
		Object r = d.remove(key);
		if (r == null)
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "_" + key + " required ");
		if (!String.class.isAssignableFrom(r.getClass()))
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Directive: " + key + " isn't of type String");
		return (String) r;
	}

	public static void checkForExtraDirectives(Map<String, Object> d) throws AmiDatasourceException {
		if (!d.isEmpty())
			throw new AmiDatasourceException(AmiDatasourceException.DIRECTIVE_ERROR, "Unknown Directive: _" + CH.first(d.keySet()));
	}

	public static Table processOnlineTable(String query, int limit, String sourceTableName, OnlineTable table, SqlPlanListener planListener, TimeoutController timeout) {
		try {
			return AmiDatasourceUtils.processTable(query, limit, sourceTableName, table, planListener, timeout);
		} finally {
			IOH.close(table);
		}
	}

	public static Table processTable(String query, int limit, String sourceTableName, Table table, SqlPlanListener planListener, TimeoutController timeout) {
		SqlProcessor processor = new SqlProcessor();
		Tableset tablesMap = new TablesetImpl();
		tablesMap.putTable(sourceTableName, table);
		Object r = processor.process(query, new TopCalcFrameStack(tablesMap, limit, timeout, planListener, AmiUtils.METHOD_FACTORY, EmptyCalcFrame.INSTANCE));
		return (Table) r;
	}
	public static BasicTable readResource(Package pakage, String name) throws AmiDatasourceException {
		try {
			String raw = new String(IOH.readData(pakage, name));
			String[] lines = SH.splitLines(raw);
			BasicTable r = new BasicTable();
			for (String s : SH.split('|', lines[0]))
				r.addColumn(String.class, s);
			for (int i = 1; i < lines.length; i++) {
				String[] parts = SH.split('|', lines[i]);
				Row row = r.newEmptyRow();
				for (int j = 0; j < parts.length; j++)
					row.putAt(j, parts[j]);
				r.getRows().add(row);
			}
			return r;
		} catch (Exception e) {
			throw new AmiDatasourceException(AmiDatasourceException.UNKNOWN_ERROR, "Failed to load: " + name, e);
		}
	}
}
