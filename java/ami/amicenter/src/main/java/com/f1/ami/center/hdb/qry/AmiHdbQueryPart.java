package com.f1.ami.center.hdb.qry;

import com.f1.ami.center.hdb.AmiHdbColumn;
import com.f1.base.ToStringable;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;

public interface AmiHdbQueryPart extends ToStringable {

	public AmiHdbColumn getColumn();
	public int getScore();

	int SCORE_EQ = 6;
	int SCORE_BETWEEN = 5;
	int SCORE_IN = 4;
	int SCORE_GTLT = 3;
	int SCORE_GELE = 2;
	int SCORE_NEPA = 1;
	int SCORE_ALL = 0;

	public boolean matches(Comparable val);
	public DerivedCellCalculator toDcc();

}
