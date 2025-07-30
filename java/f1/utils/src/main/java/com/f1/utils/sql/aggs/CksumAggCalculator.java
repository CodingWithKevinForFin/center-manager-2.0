package com.f1.utils.sql.aggs;

import java.util.List;

import com.f1.base.CalcFrame;
import com.f1.utils.Cksum;
import com.f1.utils.FastByteArrayDataOutputStream;
import com.f1.utils.OH;
import com.f1.utils.structs.table.derived.DerivedCellCalculator;
import com.f1.utils.structs.table.derived.ParamsDefinition;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.ReusableCalcFrameStack;

public class CksumAggCalculator extends AbstractAggCalculator {
	final public static String METHOD_NAME = "cksumAgg";
	public final static ParamsDefinition paramsDefinition;
	static {
		paramsDefinition = new ParamsDefinition(METHOD_NAME, Long.class, "Object value");
		paramsDefinition.addDesc("Returns a checksum of the column, using the unix cksum method.");
		paramsDefinition.addParamDesc(0, "");
	}
	public final static AggMethodFactory FACTORY = new AggMethodFactory() {

		@Override
		public ParamsDefinition getDefinition() {
			return paramsDefinition;
		}

		@Override
		public AbstractAggCalculator toMethod(int position, String methodName, DerivedCellCalculator[] calcs, com.f1.utils.structs.table.stack.CalcTypesStack context) {
			return new CksumAggCalculator(position, calcs[0]);
		}
	};
	private long longValue;
	private FastByteArrayDataOutputStream out;

	public CksumAggCalculator(int position, DerivedCellCalculator inner) {
		super(position, inner);
		this.out = new FastByteArrayDataOutputStream();
	}
	@Override
	public Object get(CalcFrameStack lcvs) {
		return this.longValue;
	}
	@Override
	public void visit(ReusableCalcFrameStack sf, List<? extends CalcFrame> values) {
		for (CalcFrame row : values) {
			Object value = inner.get(sf.reset(row));
			out.write(OH.toBytes(value));
		}
		this.longValue = Cksum.cksum(out.getBuffer(), 0, out.getCount());
		out.reset();
	}
	@Override
	public DerivedCellCalculator copy() {
		return new CksumAggCalculator(getPosition(), inner.copy());
	}
	@Override
	public Class<?> getReturnType() {
		return Long.class;
	}
	@Override
	public String getMethodName() {
		return METHOD_NAME;
	}

	@Override
	public void setValue(Object value) {
		this.longValue = (Long) value;
	}

	@Override
	public void visitRows(CalcFrameStack values, long count) {
		Object value = inner.get(values);
		while (count-- > 0)
			out.write(OH.toBytes(value));
		this.longValue = Cksum.cksum(out.getBuffer(), 0, out.getCount());
		out.reset();

	}
}
