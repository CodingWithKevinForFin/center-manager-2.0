package com.f1.anvil.triggers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.ami.amicommon.AmiFactoryOption;
import com.f1.ami.center.procs.AmiAbstractStoredProc;
import com.f1.ami.center.procs.AmiStoredProcRequest;
import com.f1.ami.center.procs.AmiStoredProcResult;
import com.f1.ami.center.table.AmiImdbSession;
import com.f1.anvil.utils.AnvilMarketDataMap;
import com.f1.anvil.utils.AnvilMarketDataSymbol;
import com.f1.base.Table;
import com.f1.utils.AH;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.SH;
import com.f1.utils.structs.Tuple2;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.f1.utils.structs.table.derived.StackFrame;

public class AnvilStoredProcQueryTrades extends AmiAbstractStoredProc {

	private static final Logger log = LH.get(AnvilStoredProcQueryTrades.class);
	private AnvilMarketDataMap marketData;

	private List<AmiFactoryOption> arguments = new ArrayList<AmiFactoryOption>();

	public AnvilStoredProcQueryTrades() {
		arguments.add(new AmiFactoryOption("symbol", String.class, true));
		arguments.add(new AmiFactoryOption("startTime", Long.class, false));
		arguments.add(new AmiFactoryOption("endTime", Long.class, false));
		arguments.add(new AmiFactoryOption("columns", String.class, false));
	}

	@Override
	public List<AmiFactoryOption> getArguments() {
		return arguments;
	}

	@Override
	protected void onStartup(AmiImdbSession session) {
		this.marketData = getImdb().getAmiServiceOrThrow(AnvilServices.SERVICE_NAME, AnvilServices.class).getMarketData();

	}

	@Override
	public void execute(AmiStoredProcRequest arguments, AmiStoredProcResult resultSink, AmiImdbSession session, StackFrame sf) throws Exception {
		//TODO: limit
		int limit = 100000;
		List<Object> args = arguments.getArguments();
		String symbolArg = (String) args.get(0);
		Long startTimeArg = (Long) args.get(1);
		Long endTimeArg = (Long) args.get(2);
		String columnsArg = (String) args.get(3);
		Set<String> columns = SH.splitToSet(",", columnsArg);
		Map<String, Tuple2<Long, Long>> symbols = new HashMap<String, Tuple2<Long, Long>>();
		symbols.put(symbolArg, new Tuple2<Long, Long>(startTimeArg, endTimeArg));

		//		Set<String> columns = CH.s(SH.split(",", arguments));
		//		FastColumnarTable queryFields = request.get("FIELDS");
		//		Map<String, Tuple2<Long, Long>> symbols = new HashMap<String, Tuple2<Long, Long>>();
		//		Map<Object, Column> cols = queryFields.getColumnsMap();
		//		Column minTimeCol = cols.get("minTime");
		//		Column maxTimeCol = cols.get("maxTime");
		//		for (int i = 0; i < queryFields.getSize(); i++) {
		//			String sym = queryFields.get(i, "symbol", Caster_String.INSTANCE);
		//			if (SH.is(sym)) {
		//				Tuple2<Long, Long> range = symbols.get(sym);
		//				if (range == null)
		//					range = new Tuple2<Long, Long>();
		//				symbols.put(sym, range);
		//				if (minTimeCol != null) {
		//					Long minTime = queryFields.getAt(i, minTimeCol.getLocation(), Caster_Long.INSTANCE);
		//					if (minTime != null && (range.getA() == null || range.getA().longValue() > minTime.longValue()))
		//						range.setA(minTime);
		//				}
		//				if (maxTimeCol != null) {
		//					Long maxTime = queryFields.getAt(i, maxTimeCol.getLocation(), Caster_Long.INSTANCE);
		//					if (maxTime != null && (range.getB() == null || range.getB().longValue() < maxTime.longValue()))
		//						range.setB(maxTime);
		//				}
		//			}
		//		}
		int starts[] = new int[symbols.size()];
		int ends[] = new int[symbols.size()];
		AnvilMarketDataSymbol[] mds = new AnvilMarketDataSymbol[symbols.size()];
		int pos = 0;
		int totSize = 0;
		LH.info(log, "Executing trades query across: " + symbols);
		long startTime = System.currentTimeMillis();
		for (Entry<String, Tuple2<Long, Long>> entry : symbols.entrySet()) {
			String sym = entry.getKey();
			Long min = entry.getValue().getA();
			Long max = entry.getValue().getB();
			AnvilMarketDataSymbol md = this.marketData.getMarketData(sym);
			int start;
			if (min != null) {
				start = md.getTradePositionLe(min.longValue());
				if (start == -1)
					start = 0;
			} else
				start = 0;
			int end;
			if (max != null) {
				end = md.getTradePositionGe(max.longValue());
				if (end == -1)
					end = md.getCurrentTradeCount();
			} else {
				end = md.getCurrentTradeCount();
			}
			if (end == -1 || start == -1) {
				end = 0;
				start = 0;
			}

			if (end - start + totSize > limit)
				end = limit - totSize - start;

			starts[pos] = start;
			ends[pos] = end;
			mds[pos] = md;
			pos++;
			totSize += end - start;
		}
		double[] vals = columns.isEmpty() || columns.contains("val") ? new double[totSize] : null;
		long[] vols = columns.isEmpty() || columns.contains("vol") ? new long[totSize] : null;
		float[] pxs = columns.isEmpty() || columns.contains("px") ? new float[totSize] : null;
		int[] sizes = columns.isEmpty() || columns.contains("size") ? new int[totSize] : null;
		long[] times = columns.isEmpty() || columns.contains("time") ? new long[totSize] : null;
		String[] syms = columns.isEmpty() || columns.contains("symbol") ? new String[totSize] : null;
		for (int i = 0, offset = 0; i < starts.length; i++) {
			AnvilMarketDataSymbol mdsym = mds[i];
			int end = ends[i];
			int str = starts[i];
			int size = end - str;
			if (syms != null)
				AH.fill(syms, offset, size, mdsym.getSymbol());
			if (vals != null)
				mdsym.getTradeAggVals(str, end, vals, offset);
			if (vols != null)
				mdsym.getTradeAggVols(str, end, vols, offset);
			if (pxs != null)
				mdsym.getTradePxs(str, end, pxs, offset);
			if (sizes != null)
				mdsym.getTradeSizes(str, end, sizes, offset);
			if (times != null)
				mdsym.getTradeTimes(str, end, times, offset);
			offset += size;
		}

		ColumnarTable table = new ColumnarTable(Collections.EMPTY_LIST, totSize);
		if (pxs != null)
			table.addColumnWithValues(Float.class, "px", pxs, null, false); //			table.addColumnWithFloats("px", pxs);
		if (vols != null)
			table.addColumnWithValues(Long.class, "vol", vols, null, false); //			table.addColumnWithLongs("vol", vols);
		if (vals != null)
			table.addColumnWithValues(Double.class, "val", vals, null, false); //			table.addColumnWithDoubles("val", vals);
		if (sizes != null)
			table.addColumnWithValues(Integer.class, "size", sizes, null, false); //			table.addColumnWithInts("size", sizes);
		if (times != null)
			table.addColumnWithValues(Long.class, "time", times, null, false); //			table.addColumnWithLongs("time", times);
		if (syms != null)
			table.addColumnWithValues(String.class, "symbol", syms, null, false); //			table.addColumnWithObjects("symbol", String.class, syms);
		//		AmiStoredProcedureResponse r = new AmiStoredProcedureResponse((Map) CH.m("trades", table));
		table.setTitle("Trades");
		resultSink.setTables(CH.l((Table) table));
		long end = System.currentTimeMillis();
		LH.info(log, "Query returned: " + totSize, " row(s) in ", (end - startTime), "ms");
		//		return r;

	}
}
