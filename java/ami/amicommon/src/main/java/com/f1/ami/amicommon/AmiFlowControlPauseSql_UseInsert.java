package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.ami.amicommon.msg.AmiCenterUploadTable;
import com.f1.base.Action;
import com.f1.base.Table;
import com.f1.container.ContainerTools;
import com.f1.utils.CH;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiFlowControlPauseSql_UseInsert extends AmiFlowControlPauseSql {

	private AmiCenterQueryDsResponse currentResponse;
	final private String targetTableName;
	final private Table table;
	final private String[] targetColumnNames;
	private TableReturn tableReturn;

	public AmiFlowControlPauseSql_UseInsert(DerivedCellCalculator dcc, Map<String, DerivedCellCalculator> use, CalcFrameStack sf, String targetTableName,
			String[] targetColumnNames, Table table) {
		super(dcc, use, sf);
		this.targetColumnNames = targetColumnNames;
		this.targetTableName = targetTableName;
		this.table = table;
	}

	@Override
	public Action toRequest(ContainerTools tools) {
		AmiCenterQueryDsRequest r = super.createRequest(tools);
		r.setType(AmiCenterQueryDsRequest.TYPE_UPLOAD);
		AmiCenterUploadTable uv = tools.nw(AmiCenterUploadTable.class);
		uv.setData(this.table);
		uv.setTargetColumns(this.targetColumnNames == null ? null : CH.l(targetColumnNames));
		uv.setTargetTable(this.targetTableName);
		r.setUploadValues(CH.l(uv));
		return r;
	}

	@Override
	public void processResponse(Action response) {
		this.currentResponse = (AmiCenterQueryDsResponse) response;
		this.tableReturn = new TableReturn(this.currentResponse.getTables(), this.currentResponse.getRowsEffected(), this.currentResponse.getReturnType(),
				this.currentResponse.getReturnValue());
	}

	@Override
	public TableReturn getTableReturn() {
		return this.tableReturn;
	}

}
