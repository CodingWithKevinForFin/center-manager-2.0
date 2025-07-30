package com.f1.ami.center.ds;

import java.sql.Statement;

public interface AmiStatement extends Statement {

	long getQueryTimeoutMillis();
	void setQueryTimeoutMillis(long millis);

}
