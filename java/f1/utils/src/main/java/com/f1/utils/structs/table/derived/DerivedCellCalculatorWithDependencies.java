package com.f1.utils.structs.table.derived;

import java.util.Set;

public interface DerivedCellCalculatorWithDependencies extends DerivedCellCalculator {

	public Set<Object> getDependencyIds(Set<Object> sink);
}
