package com.f1.ami.amicommon;

import java.util.Map;

import com.f1.ami.amicommon.msg.AmiCenterQueryDsRequest;
import com.f1.ami.amicommon.msg.AmiCenterQueryDsResponse;
import com.f1.base.Action;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.sqlnode.ExecuteNode;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.CalcFrameStack;

public class AmiFlowControlPauseSql_UseExecute extends AmiFlowControlPauseSql {

	final private ExecuteNode executeNode;
	private AmiCenterQueryDsResponse currentResponse;
	private TableReturn tableReturn;

	public AmiFlowControlPauseSql_UseExecute(DerivedCellCalculator dcc, ExecuteNode executeNode, Map<String, DerivedCellCalculator> use, CalcFrameStack sf) {
		super(dcc, use, sf);
		this.executeNode = executeNode;
	}

	@Override
	public Action toRequest(ContainerTools tools) {
		AmiCenterQueryDsRequest r = super.createRequest(tools);
		r.setOriginType(AmiCenterQueryDsRequest.ORIGIN_NESTED);
		r.setType(AmiCenterQueryDsRequest.TYPE_QUERY);
		r.setQuery(this.executeNode.getValue());
		return r;
	}

	@Override
	public void processResponse(Action response) {
		this.currentResponse = (AmiCenterQueryDsResponse) response;
		this.tableReturn = new TableReturn(this.currentResponse.getTables(), this.currentResponse.getRowsEffected(), this.currentResponse.getReturnType(),
				this.currentResponse.getReturnValue());
	}

	public ExecuteNode getExecuteNode() {
		return executeNode;
	}

	@Override
	public TableReturn getTableReturn() {
		return this.tableReturn;
	}

}
