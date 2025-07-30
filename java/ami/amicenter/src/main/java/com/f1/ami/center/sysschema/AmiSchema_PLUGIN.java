package com.f1.ami.center.sysschema;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.f1.ami.amicommon.AmiConsts;
import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.amicommon.AmiFactoryPlugin;
import com.f1.ami.amicommon.AmiPlugin;
import com.f1.ami.amicommon.AmiUtils;
import com.f1.ami.center.table.AmiColumnWrapper;
import com.f1.ami.center.table.AmiImdbImpl;
import com.f1.ami.center.table.AmiIndex;
import com.f1.ami.center.table.AmiIndexImpl;
import com.f1.ami.center.table.AmiPreparedRow;
import com.f1.ami.center.table.AmiRow;
import com.f1.ami.center.table.AmiTable;
import com.f1.ami.center.table.AmiTableDef;
import com.f1.ami.center.table.AmiTableImpl;
import com.f1.ami.center.table.AmiTableUtils;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiSchema_PLUGIN {

	final public AmiTableImpl table;
	final public AmiPreparedRow preparedRow;
	final public AmiColumnWrapper pluginName;
	final public AmiColumnWrapper pluginType;
	final public AmiColumnWrapper classType;
	final public AmiColumnWrapper arguments;
	private AmiImdbImpl db;

	public AmiSchema_PLUGIN(AmiImdbImpl imdb, CalcFrameStack sf) {
		this.db = imdb;
		AmiTableDef def = new AmiTableDef(AmiTableUtils.DEFTYPE_SYSTEM, AmiConsts.TYPE_PLUGIN);

		this.pluginName = def.addColumn("PluginName", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.pluginType = def.addColumn("PluginType", AmiTable.TYPE_ENUM, AmiConsts.NONULL_OPTIONS);
		this.classType = def.addColumn("ClassType", AmiTable.TYPE_STRING, AmiConsts.NONULL_OPTIONS);
		this.arguments = def.addColumn("Arguments", AmiTable.TYPE_STRING);

		this.table = (AmiTableImpl) imdb.createTable(def, sf);
		this.preparedRow = this.table.createAmiPreparedRow();
		this.table.addIndex(AmiTableUtils.DEFTYPE_SYSTEM, "pk", CH.l(pluginType.getName(), pluginName.getName()), CH.l(AmiIndexImpl.TYPE_SORT, AmiIndexImpl.TYPE_SORT),
				AmiIndex.CONSTRAINT_TYPE_PRIMARY, null, sf);
	}
	public void addRow(AmiRow existing, String pluginType, String pluginName, String className, String arguments, CalcFrameStack sf) {
		this.preparedRow.reset();
		this.preparedRow.setString(this.pluginName, pluginName);
		this.preparedRow.setString(this.pluginType, pluginType);
		this.preparedRow.setString(this.arguments, arguments);
		this.preparedRow.setString(this.classType, className);
		this.table.insertAmiRow(this.preparedRow, sf);
	}

	public Map<Tuple2<String, String>, AmiRow> getRowsByTypeName() {
		Map<Tuple2<String, String>, AmiRow> r = new HashMap<Tuple2<String, String>, AmiRow>();
		for (int i = 0; i < this.table.getRowsCount(); i++) {
			AmiRow row = this.table.getAmiRowAt(i);
			r.put(new Tuple2<String, String>(row.getString(pluginType), row.getString(pluginName)), row);
		}
		return r;
	}
	public void addRow(String pluginType, AmiPlugin s, CalcFrameStack sf) {
		String optionsString;
		if (s instanceof AmiFactoryPlugin) {
			Collection<AmiFactoryOption> options = ((AmiFactoryPlugin) s).getAllowedOptions();
			optionsString = AmiUtils.descriptFactoryOptions(options, db.getScriptManager().getMethodFactory(), false);
		} else
			optionsString = null;
		addRow(null, pluginType, s.getPluginId(), OH.getClassName(s), optionsString, sf);

	}
}
