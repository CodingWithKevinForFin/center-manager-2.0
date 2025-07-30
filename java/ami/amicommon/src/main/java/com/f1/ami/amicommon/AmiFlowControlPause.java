package com.f1.ami.amicommon;

import com.f1.base.Action;
import com.f1.container.ContainerTools;
import com.f1.utils.sql.FlowControlPauseTableReturn;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

abstract public class AmiFlowControlPause extends FlowControlPauseTableReturn {

	public AmiFlowControlPause(DerivedCellCalculator position) {
		super(position);
	}

	abstract public Action toRequest(ContainerTools tools);

	abstract public void processResponse(Action response);

}
