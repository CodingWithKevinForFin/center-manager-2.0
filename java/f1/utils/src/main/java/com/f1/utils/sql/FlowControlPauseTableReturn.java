package com.f1.utils.sql;

import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.FlowControlPause;
import com.f1.utils.structs.table.derived.FlowControlThrow;

public abstract class FlowControlPauseTableReturn extends FlowControlPause {

	public FlowControlPauseTableReturn(DerivedCellCalculator position) {
		super(position);
	}

	@Override
	public Object resume() {
		if (this.getStack() == null)
			return getTableReturn();
		return super.resume();
	}

	abstract public TableReturn getTableReturn();

	private Throwable responseErrorException;
	private String responseErrorMessage;

	public void processErrorResponse(Throwable exception) {
		this.responseErrorException = exception;
	}

	public void processErrorResponse(String exception) {
		this.responseErrorMessage = exception;

	}
	public void throwIfError(DerivedCellCalculator position) {
		if (responseErrorException != null) {
			if (responseErrorException instanceof ExpressionParserException)
				throw new FlowControlThrow(responseErrorException);
			throw new FlowControlThrow(position, this.responseErrorException.getMessage(), this.responseErrorException);
		}
		if (responseErrorMessage != null)
			throw new FlowControlThrow(position, this.responseErrorMessage, new ExpressionParserException(position.getPosition(), this.responseErrorMessage));
	}
	//	public void throwIfError() {
	//		if (responseErrorException != null)
	//			throw new FlowControlThrow(this.responseErrorException);
	//		if (responseErrorMessage != null)
	//			throw new FlowControlThrow(this.responseErrorMessage);
	//	}

}
