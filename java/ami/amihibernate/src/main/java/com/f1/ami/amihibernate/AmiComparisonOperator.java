package com.f1.ami.amihibernate;

import java.util.EnumMap;
import java.util.Map;

import org.hibernate.query.sqm.ComparisonOperator;

public class AmiComparisonOperator {
	public static final Map<ComparisonOperator, String> COMPARISON_OPERATOR_MAP;
	private static final String EQUAL = "==";
	private static final String GREATER_THAN = ">";
	private static final String GREATER_THAN_OR_EQUAL = ">=";
	private static final String LESS_THAN = "<";
	private static final String LESS_THAN_OR_EQUAL = "<=";
	private static final String NOT_EQUAL = "!=";
	private static final String DISTINCT_FROM = " is distinct from "; //TODO: not sure what this does
	private static final String NOT_DISTINCT_FROM = " is not distinct from "; //TODO: not sure what this does 
	static {
		COMPARISON_OPERATOR_MAP = new EnumMap<>(ComparisonOperator.class);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.EQUAL, AmiComparisonOperator.EQUAL);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.GREATER_THAN, AmiComparisonOperator.GREATER_THAN);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.GREATER_THAN_OR_EQUAL, AmiComparisonOperator.GREATER_THAN_OR_EQUAL);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.LESS_THAN, AmiComparisonOperator.LESS_THAN);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.LESS_THAN_OR_EQUAL, AmiComparisonOperator.LESS_THAN_OR_EQUAL);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.NOT_EQUAL, AmiComparisonOperator.NOT_EQUAL);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.DISTINCT_FROM, AmiComparisonOperator.DISTINCT_FROM);
		COMPARISON_OPERATOR_MAP.put(ComparisonOperator.NOT_DISTINCT_FROM, AmiComparisonOperator.NOT_DISTINCT_FROM);
	}

}
