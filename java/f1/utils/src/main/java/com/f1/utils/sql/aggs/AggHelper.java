package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.DoubleArrayList;
import com.f1.utils.MH;
import com.f1.utils.casters.Caster_Double;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class AggHelper {

	public static DoubleArrayList getDoubles(ReusableCalcFrameStack sf, List<? extends CalcFrame> values, DerivedCellCalculator inner) {
		for (int i = 0, origSize = values.size(); i < origSize; i++) {
			Double d = Caster_Double.INSTANCE.cast(inner.get(sf.reset(values.get(i))));
			if (d != null && MH.isNumber(d)) {
				DoubleArrayList list = new DoubleArrayList(origSize - i);
				list.add(d.doubleValue());
				for (i++; i < origSize; i++) {
					d = Caster_Double.INSTANCE.cast(inner.get(sf.reset(values.get(i))));
					if (d != null && MH.isNumber(d))
						list.add(d.doubleValue());
				}
				return list;
			}
		}
		return null;
	}
}
