package com.f1.ami.plugins.mongo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.AmiDatasourceException;
import com.f1.ami.amicommon.msg.AmiCenterQuery;
import com.f1.ami.center.ds.AmiDatasourceUtils;
import com.f1.utils.CharReader;
import com.f1.utils.SH;
import com.f1.utils.converter.json2.ObjectToJsonConverter;
import com.f1.utils.impl.StringCharReader;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoQueryParser {

	ObjectToJsonConverter jsonParser = new MongoJsonConverter();

	public static void main(String a[]) {
		new MongoQueryParser().parseEval("db.portfolio.aggregate([\n" + "{$group: {_id: {a:\"$instType\",b:\"$instSubType\"}, n: {$sum:1}}}\n" + "			 ]);\n" + "");
	}

	public MongoCmd parseSqlCmd(AmiCenterQuery q) throws AmiDatasourceException {
		// Directives
		final Map<String, Object> d = q.getDirectives();
		final String d_find = AmiDatasourceUtils.getOptional(d, AmiMongoDatasourceAdapter.USE_DIRECTIVE_MONGO_FIND);
		final String d_project = AmiDatasourceUtils.getOptional(d, AmiMongoDatasourceAdapter.USE_DIRECTIVE_MONGO_PROJECTION);
		final int d_skip = AmiDatasourceUtils.getOptionalInt(d, AmiMongoDatasourceAdapter.USE_DIRECTIVE_MONGO_SKIP);
		final String d_sort = AmiDatasourceUtils.getOptional(d, AmiMongoDatasourceAdapter.USE_DIRECTIVE_MONGO_SORT);

		String cmd = q.getQuery();
		boolean selectAll = false;
		StringBuilder sink = new StringBuilder();
		StringCharReader scr = new StringCharReader(cmd);

		//Read select
		scr.expectSequence("select");
		scr.skip(StringCharReader.WHITE_SPACE);

		List<String> columnList = new ArrayList<String>();
		LinkedHashMap<String, String> asMap = new LinkedHashMap<String, String>();
		LinkedHashMap<String, String> typesMap = new LinkedHashMap<String, String>();
		List<String> typeList = new ArrayList<String>();
		boolean isSelect = true;

		if (scr.peak() == '*') {
			StringBuilder save = new StringBuilder();
			scr.readUntil(StringCharReader.EOF, save);
			scr.reset(save.toString());

			scr.expect('*');
			scr.skip(StringCharReader.WHITE_SPACE);
			if (scr.peakSequence("FROM")) {
				selectAll = true;
				isSelect = false;
			} else {
				scr.reset(save.toString());
			}
		}

		while (isSelect) {
			String type = null;
			String columnName = null;
			String asString = null;
			if (scr.expectNoThrow('(')) {
				//Has Type

				//Read type
				scr.skip(StringCharReader.WHITE_SPACE);
				scr.readUntil(')', sink);
				SH.trim(sink);
				type = SH.toStringAndClear(sink);
				scr.expect(')');
			}

			//Read column name
			scr.skip(StringCharReader.WHITE_SPACE);
			scr.readUntilAny(StringCharReader.WHITE_SPACE_COMMA, true, sink);
			SH.trim(sink);
			columnName = SH.toStringAndClear(sink);

			//Read 'as' if available
			scr.skip(StringCharReader.WHITE_SPACE);
			if (scr.peakSequence("as")) {
				scr.expectSequence("as");
				scr.skip(StringCharReader.WHITE_SPACE);
				scr.readUntilAny(StringCharReader.WHITE_SPACE_COMMA, true, sink);
				SH.trim(sink);
				asString = SH.toStringAndClear(sink);
			} else {
				asString = columnName;
			}

			scr.skip(StringCharReader.WHITE_SPACE);
			if (scr.expectNoThrow(',')) {
				//If there is a comma isSelect stays on and we continue reading for columns 
				scr.skip(StringCharReader.WHITE_SPACE);
			} else {
				//Otherwise there are no more columns to select on
				isSelect = false;
			}
			typeList.add(type);
			columnList.add(columnName);
			asMap.put(columnName, asString);
			typesMap.put(columnName, type);
		}

		//Read unpack string
		String unpack = null;
		if (scr.expectSequenceNoThrow("unpack")) {
			scr.skip(StringCharReader.WHITE_SPACE);
			scr.readUntilSequence("from", sink);
			SH.trim(sink);
			unpack = SH.toStringAndClear(sink);
		}

		String from = null;
		String eval = null;
		//Read 'from' or eval string
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.expectSequence("from");
		scr.skip(StringCharReader.WHITE_SPACE);

		if (scr.expectSequenceNoThrow("eval")) {
			//Read eval string if there is an eval
			boolean isEval = true;
			eval = "";
			scr.skip(StringCharReader.WHITE_SPACE);
			while (isEval) {
				scr.readUntil(')', sink);
				scr.skip(StringCharReader.WHITE_SPACE);
				scr.skip(')');
				scr.skip(StringCharReader.WHITE_SPACE);
				sink.append(')');
				if (scr.expectNoThrow('.')) {
					//If there is a period keep reading
					sink.append('.');
				} else {
					//Otherwise finish reading eval
					//Expect a semicolon
					scr.expectNoThrow(';');
					isEval = false;
				}
			}

			SH.trim(sink);
			eval = SH.toStringAndClear(sink);
		} else {
			//Read 'from' string
			scr.readUntilAny(StringCharReader.WHITE_SPACE, true, sink);
			SH.trim(sink);
			from = SH.toStringAndClear(sink);
			scr.skip(StringCharReader.WHITE_SPACE);
		}

		String where = null;
		if (scr.expectSequenceNoThrow("where")) {
			scr.skip(StringCharReader.WHITE_SPACE);
			scr.readUntil(StringCharReader.EOF, sink);
			SH.trim(sink);
			where = SH.toStringAndClear(sink);
		}
		return new MongoCmd(columnList, asMap, typesMap, unpack, from, eval, where, selectAll, d_find, d_project, d_sort, d_skip, q.getLimit());
	}

	public MongoEvalCmd parseEval(String cmd) {
		StringBuilder sink = new StringBuilder();
		StringCharReader scr = new StringCharReader(cmd);//"db.portfolio.aggregate([\n" + "{$group: {_id: {a:\"$instType\",b:\"$instSubType\"}, n: {$sum:1}}}\n" + "			 ]);\n" + "");
		//Expect "db"
		scr.expectSequence("db");

		//Read collection name
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.expect('.');
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil('.', sink);
		scr.expect('.');
		SH.trim(sink);
		String collName = SH.toStringAndClear(sink);

		//Read method name
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil('(', sink);
		scr.expect('(');
		SH.trim(sink);
		String funcName = SH.toStringAndClear(sink);

		//Read argument string
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil(')', sink);
		scr.expect(')');
		SH.trim(sink);
		String argString = SH.toStringAndClear(sink);

		//Read post command string
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil(CharReader.EOF, sink);
		SH.trim(sink);
		String postCommand = SH.toStringAndClear(sink);
		return new MongoEvalCmd(collName, funcName, argString, postCommand);
	}

	public MongoEvalCmd parseNextCommand(String cmd) {
		StringBuilder sink = new StringBuilder();
		StringCharReader scr = new StringCharReader(cmd);//"db.portfolio.aggregate([\n" + "{$group: {_id: {a:\"$instType\",b:\"$instSubType\"}, n: {$sum:1}}}\n" + "			 ]);\n" + "");

		//Read method name
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.expect('.');
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil('(', sink);
		scr.expect('(');
		String funcName = SH.toStringAndClear(sink);

		//Read argument string
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil(')', sink);
		scr.expect(')');
		String argString = SH.toStringAndClear(sink);

		//Read post command string
		scr.skip(StringCharReader.WHITE_SPACE);
		scr.readUntil(CharReader.EOF, sink);
		String postCommand = SH.toStringAndClear(sink);
		return new MongoEvalCmd("", funcName, argString, postCommand);

	}
	static public Object toDbObject(Object o) {
		if (o == null)
			return null;
		else if (o instanceof DBObject)
			return (DBObject) o;
		else if (o instanceof List) {
			List l = (List) o;
			BasicDBList r = new BasicDBList();
			for (Object i : l)
				r.add(toDbObject(i));
			return r;
		} else if (o instanceof Map) {
			Map<String, Object> l = (Map) o;
			BasicDBObject r = new BasicDBObject();
			for (java.util.Map.Entry<String, Object> e : l.entrySet()) {
				r.put(e.getKey(), toDbObject(e.getValue()));
			}
			return r;
		} else
			return o;
	}
}
