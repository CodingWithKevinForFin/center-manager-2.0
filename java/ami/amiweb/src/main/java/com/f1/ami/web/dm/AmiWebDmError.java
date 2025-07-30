package com.f1.ami.web.dm;

public class AmiWebDmError {

	private AmiWebDm datamodel;
	private Exception exception;

	public AmiWebDmError(AmiWebDm datamodel, Exception e) {
		this.datamodel = datamodel;
		this.exception = e;
	}

	public AmiWebDm getDatamodel() {
		return datamodel;
	}

	public void setDatamodel(AmiWebDm datamodel) {
		this.datamodel = datamodel;
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}

}
