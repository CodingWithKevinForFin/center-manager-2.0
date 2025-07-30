package com.f1.ami.center.dialects;

import com.f1.base.Table;
import com.f1.utils.SH;

public class AmiDbDialect_Tableau implements AmiDbDialect {

	@Override
	public String prepareQuery(String sql) {
		if (sql != null) {
			sql = SH.replaceAll(sql, " = ", " == ");
			sql = SH.replaceAll(sql, " INNER JOIN ", " JOIN ");
			sql = SH.replaceAll(sql, "`AMI`.", "");
			sql = SH.replaceAll(sql, "` `", "` AS `");
		}
		return sql;
	}

	@Override
	public Table prepareResult(Table r) {
		return r;
	}

}
