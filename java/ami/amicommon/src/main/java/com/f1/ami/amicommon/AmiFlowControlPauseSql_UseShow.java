package com.f1.ami.amicommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.f1.ami.amicommon.ds.AmiDatasourceRunner;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiDatasourceTable;
import com.f1.base.Action;
import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.sqlnode.SqlShowNode;
import com.f1.utils.structs.table.BasicTable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiFlowControlPauseSql_UseShow extends AmiFlowControlPauseSql {

	private SqlShowNode show;
	private TableReturn tableReturn;

	public AmiFlowControlPauseSql_UseShow(DerivedCellCalculator position, SqlShowNode node, Map<String, DerivedCellCalculator> use, CalcFrameStack sf) {
		super(position, use, sf);
		this.show = node;
	}

	@Override
	public Action toRequest(ContainerTools tools) {
		AmiCenterQueryDsRequest r = super.createRequest(tools);
		if (show.getName() != null) {
			r.setType(AmiCenterQueryDsRequest.TYPE_PREVIEW);
			String tableName = show.getName().getVarname();
			List<AmiDatasourceTable> records = new ArrayList<AmiDatasourceTable>();
			AmiDatasourceTable record = tools.nw(AmiDatasourceTable.class);
			record.setName(tableName);
			records.add(record);
			r.setTablesForPreview(records);
			r.setPreviewCount(0);
		} else
			r.setType(AmiCenterQueryDsRequest.TYPE_SHOW_TABLES);
		return r;
	}

	@Override
	public void processResponse(Action response) {
		AmiCenterQueryDsResponse currentResponse = (AmiCenterQueryDsResponse) response;
		Table r;
		if (show.getName() != null) {
			List<AmiDatasourceTable> pt = currentResponse.getPreviewTables();
			if (pt.size() != 1)
				throw new ExpressionParserException(this.getPosition().getPosition(), "SHOW TABLE Expecting exactly 1 table, not: " + pt.size());
			Table table = (Table) pt.get(0).getPreviewData();
			r = new BasicTable(String.class, "ColumnName", String.class, "Type", Integer.class, "Position");
			r.setTitle("COLUMNS");
			int n = 0;
			for (Column c : table.getColumns()) {
				String type = c.getType().getSimpleName(); //TODO: methodFactory.forType(c.getType());
				r.getRows().addRow((String) c.getId(), type, n++);
			}
		} else {
			//			final List<Table> resultTables = currentResponse.getTables();
			//			if (resultTables == null || resultTables.size() != 1)
			//				throw new ExpressionParserException(this.getPosition().getPosition(), "SHOW TABLES Expecting exactly 1 table");
			r = AmiDatasourceRunner.toTable(currentResponse.getPreviewTables());
		}
		this.tableReturn = new TableReturn(r);
	}

	@Override
	public TableReturn getTableReturn() {
		return this.tableReturn;
	}

}
