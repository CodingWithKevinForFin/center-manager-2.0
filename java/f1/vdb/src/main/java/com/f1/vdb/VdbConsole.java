
package com.f1.vdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import com.f1.base.Console;
import com.f1.base.Table;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.TextMatcher;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.TextMatcherFactory;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.BasicTable;

@Console(help = "provides access to the underlying database")
public class VdbConsole
{

  private final VdbDatabase<?> database;

  public VdbConsole(VdbDatabase<?> environment_)
  {
    database = environment_;
  }

  @Console(help = "list all tables in the database along with row counts and other intersting statistics")
  public Table showTables()
  {

    Table r = new BasicTable(String.class, "table_name", String.class, "data_type", int.class, "rows", String.class,
        "primary_index", String.class, "secondary_indexs");
    for (String tableName : database.getTableNames())
      {
        VdbTable<?, ?> db = database.getTable(tableName);
        r.getRows()
            .addRow(tableName, db.getSchema().askOriginalType().getName(), db.getRowSize(),
                db.getKeyFields().length == 0 ? "" : "[" + SH.join(",", db.getKeyFields()) + "]",
                db.getIndexNames().size());
      }
    return r;
  }

  @Console(help = "list all indexes in the database along with row counts and other intersting statistics")
  public Table showIndexes()
  {

    Table r = new BasicTable(String.class, "table_name", String.class, "index_type", String.class, "index_fields",
        Integer.class, "index_fields_count", Integer.class, "row_count");
    for (String tableName : database.getTableNames())
      {
        VdbTable<?, ?> db = database.getTable(tableName);
        if (db.getKeyFields().length > 0) r.getRows().addRow(tableName, "unique primary",
            "[" + SH.join(",", db.getKeyFields()) + "]", db.getKeyFields().length, db.getRowSize());
        for (String indexName : db.getIndexNames())
          {
            VdbIndex<?, ?> index = db.getIndex(indexName);
            r.getRows().addRow(tableName, !index.isUnique() ? "secondary" : "unique secondary",
                "[" + describeIdx(index) + "]", index.getKeyFields().length, index.getRowSize());
          }
      }
    return r;
  }

  private static String describeIdx(VdbIndex<?, ?> idx)
  {
    return SH.join(",", idx.getKeyFields());
  }

  private static TextMatcher toMatcher(String name)
  {
    return TextMatcherFactory.DEFAULT.toMatcher(name);
  }

  @Console(
      help = "query all tables that match supplied table expression for rows what matched supplied row expression",
      params =
        { "tableNameExpression", "columnNameExpression", "valueExpression" })
  public String query(String tableExpression, String columnsExpression, String valuesExpression)
  {
    StringBuilder sb = new StringBuilder();
    for (Table table : queryTables(tableExpression, columnsExpression, valuesExpression))
      {
        sb.append(TableHelper.toString(table)).append(SH.NEWLINE);
      }
    return sb.toString();
  }

  public List<Table> queryTables(String tableExpression, String columnsExpression, String valuesExpression)
  {
    final List<Table> r = new ArrayList<Table>();
    final TextMatcher tableMatcher = toMatcher(tableExpression);
    final TextMatcher columnsMatcher = toMatcher(columnsExpression);
    final TextMatcher valuesMatcher = toMatcher(valuesExpression);
    for (String tableName : database.getTableNames())
      {
        VdbTable<?, ?> db = database.getTable(tableName);
        if (!tableMatcher.matches(tableName)) continue;
        Table t = new BasicTable(OH.EMPTY_CLASS_ARRAY, OH.EMPTY_OBJECT_ARRAY);
        List<ValuedParam<Valued>> params = new ArrayList<ValuedParam<Valued>>(db.getSchema().askParamsCount());
        for (ValuedParam param : db.getSchema().askValuedParams())
          {
            if (columnsMatcher.matches(param.getName()) && !param.isTransient())
              {
                t.addColumn(param.getReturnType(), param.getName(), null);
                params.add(param);
              }
          }
        List<? extends Valued> results = db.queryAll(null);
        for (Valued result : results)
          {
            Object[] values = new Object[params.size()];
            boolean matches = false;
            for (int i = 0; i < params.size(); i++)
              {
                Object value = params.get(i).getValue(result);
                if (!matches && (valuesMatcher.matches(SH.toString(value)))) matches = true;
                values[i] = value;
              }
            if (matches) t.getRows().addRow(values);
          }
        t.setTitle(tableName);
        if (t.getSize() != 0) r.add(t);
      }
    return r;
  }

  public String queryJson(String tableExpression, String valuesExpression)
  {
    Map<String, List<Valued>> r = new TreeMap<String, List<Valued>>();
    ObjectToJsonConverter converter = new ObjectToJsonConverter();
    final TextMatcher tableMatcher = toMatcher(tableExpression);
    final TextMatcher valuesMatcher = toMatcher(valuesExpression);
    for (String tableName : database.getTableNames())
      {
        VdbTable<?, ?> db = database.getTable(tableName);
        if (!tableMatcher.matches(tableName)) continue;
        List<ValuedParam<Valued>> params = new ArrayList<ValuedParam<Valued>>(db.getSchema().askParamsCount());
        for (ValuedParam param : db.getSchema().askValuedParams())
          params.add(param);
        List<? extends Valued> results = db.queryAll(null);
        List<Valued> l = new ArrayList<Valued>(results.size());
        for (Valued result : results)
          for (int i = 0; i < params.size(); i++)
            if (valuesMatcher.matches(SH.toString(params.get(i).getValue(result))))
              {
                l.add(result);
                break;
              }
        r.put(tableName, l);
      }
    return converter.objectToString(r);
  }

  public List<Table> querySecondaryIndex(String tableExpression, String indexExpression)
  {
    final TextMatcher tableMatcher = toMatcher(tableExpression);
    final TextMatcher indexMatcher = toMatcher(indexExpression);
    List<Table> r = new ArrayList<Table>();

    for (String tableName : database.getTableNames())
      {
        VdbTable<?, ?> db = database.getTable(tableName);
        if (!tableMatcher.matches(tableName)) continue;
        for (String indexName : db.getIndexNames())
          {
            VdbIndex<?, ?> index = db.getIndex(indexName);
            if (!indexMatcher.matches(describeIdx(index))) continue;

            Table t = new BasicTable(OH.EMPTY_CLASS_ARRAY, OH.EMPTY_OBJECT_ARRAY);
            List<ValuedParam<Valued>> params = new ArrayList<ValuedParam<Valued>>(db.getSchema().askParamsCount());
            String[] keyFields = index.getKeyFields();
            for (String keyField : keyFields)
              {
                ValuedParam param = db.getSchema().askValuedParam(keyField);
                t.addColumn(param.getReturnType(), param.getName(), null);
                params.add(param);
              }
            t.addColumn(Object.class, "<primary key>", null);
            List<Tuple2<Object, Valued>> results = (List)index.queryKeyValues(null);
            for (Tuple2<Object, Valued> kv : results)
              {
                Object[] values = new Object[t.getColumnsCount()];
                if (keyFields.length == 1) values[0] = kv.getA();
                else
                  {
                    List l = (List)kv.getA();
                    for (int i = 0; i < t.getColumnsCount() - 1; i++)
                      values[i] = l.get(i);
                  }
                values[t.getColumnsCount() - 1] = VdbHelper.extractKey(db.getKeyFields(), kv.getB());

                boolean matches = false;
                t.getRows().addRow(values);
              }
            t.setTitle(tableName + "::[" + describeIdx(index) + "]");
            r.add(t);
          }
      }
    return r;
  }

  @Console(help = "truncate supplied tables ", params =
    { "tables" })
  public String truncateTables(String... tables)
  {
    StringBuilder sb = new StringBuilder();
    for (String table : tables)
      {
        sb.append(table).append(" truncated.").append(SH.NEWLINE);
        database.getTable(table).truncate(null);
      }
    if (sb.length() == 0) return "table(s) not found";
    return sb.toString();
  }
}

