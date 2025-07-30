package com.f1.ami.web.dm;

import com.f1.ami.web.filter.AmiWebFilterPortlet;
import com.f1.base.Table;

public interface AmiWebDmFilter {

	String getTargetDmAliasDotName();

	String getTargetTableName();

	Table filter(Table tbl);

	AmiWebFilterPortlet getSourcePanel();

}
