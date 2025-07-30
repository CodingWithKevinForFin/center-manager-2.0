package com.f1.ami.plugins.onetick;

import java.util.HashMap;

//================================================================
//
//class InfoCallback
//User defined callback that just prints out the input data.
//
//================================================================

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.f1.base.Column;
import com.f1.base.DateMillis;
import com.f1.base.DateNanos;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.structs.table.columnar.ColumnarTable;
import com.omd.jomd.DataQualityType;
import com.omd.jomd.DataType;
import com.omd.jomd.DataType.data_type_t;
import com.omd.jomd.JavaOutputCallback;
import com.omd.jomd.JavaTimeval;
import com.omd.jomd.Tick;
import com.omd.jomd.TickDescriptor;
import com.omd.jomd.TickField;
import com.omd.jomd.tick_type_t;

class AmiOneTickInfoCallback extends JavaOutputCallback {
	private static final Logger log = LH.get();
	private String symbol;
	private String label;
	final private String timezone;
	final private List<ColumnarTable> sink;
	final private long startTime;

	public AmiOneTickInfoCallback(String tz, List<ColumnarTable> sink) {
		this(tz, sink, System.currentTimeMillis());
	}
	private AmiOneTickInfoCallback(String tz, List<ColumnarTable> sink, long startTime) {
		this.startTime = startTime;
		timezone = tz;
		this.sink = sink;
	}
	@Override
	public JavaOutputCallback replicate() {
		AmiOneTickInfoCallback obj = new AmiOneTickInfoCallback(timezone, sink, this.startTime);
		obj.symbol = symbol;
		obj.label = label;
		return obj;
	}
	@Override
	public void process_callback_label(String callback_label) {
		label = callback_label;
	}

	@Override
	public void process_symbol_name(String symbol_name) {
		symbol = symbol_name;
		if (this.currentRow != null)
			this.currentRow[0] = this.symbol;
	}

	@Override
	public void process_symbol_group_name(String symbol_group_name) {
	}
	@Override
	public void process_tick_type(tick_type_t tick_type) {
	}

	private ColumnarTable currentTable;
	private int[] fieldMappings;
	private Object[] currentRow;

	@Override
	public void process_tick_descriptor(TickDescriptor tick_descriptor) {
		if (this.sink.size() == 0)
			this.sink.add(new ColumnarTable(String.class, "Symbol", DateMillis.class, "Time"));
		currentTable = this.sink.get(0);
		int colsCount = tick_descriptor.get_num_of_fields();
		fieldMappings = new int[colsCount];
		for (int i = 0; i < colsCount; ++i) {
			TickField field = tick_descriptor.get_field(i);
			String name = field.get_name();
			data_type_t type = field.get_type();
			Column col = currentTable.getColumnsMap().get(name);
			if (col == null)
				col = currentTable.addColumn(parseType(type.toString()), name);
			fieldMappings[i] = col.getLocation();
		}
		this.currentRow = new Object[currentTable.getColumnsCount()];
		this.currentRow[0] = this.symbol;
	}

	private static final Map<String, Class> TYPES = new HashMap<String, Class>();
	static {
		TYPES.put("TYPE_STRING", String.class);
		TYPES.put("TYPE_TIME32", DateMillis.class);
		TYPES.put("TYPE_TIME_MSEC64", DateMillis.class);
		TYPES.put("TYPE_TIME_NSEC64", DateNanos.class);
		TYPES.put("TYPE_DOUBLE", double.class);
		TYPES.put("TYPE_FLOAT", float.class);
		TYPES.put("TYPE_INT8", byte.class);
		TYPES.put("TYPE_INT16", short.class);
		TYPES.put("TYPE_INT32", int.class);
		TYPES.put("TYPE_INT64", long.class);
		TYPES.put("TYPE_UINT32", long.class);
	}

	private static Class<?> parseType(String type) {
		return CH.getOr(TYPES, type, String.class);
	}

	@Override
	public void process_event(Tick tick, java.util.Date time) {
		Object values[] = this.currentRow;
		values[1] = new DateMillis(time.getTime());
		for (int i = 0; i < tick.get_num_of_fields(); ++i) {
			DataType.data_type_t type = tick.get_type(i);
			int fm = fieldMappings[i];
			if (type == DataType.data_type_t.TYPE_INT8) {
				values[fm] = (byte) tick.get_int(i);
			} else if (type == DataType.data_type_t.TYPE_INT16) {
				values[fm] = (short) tick.get_int(i);
			} else if (type == DataType.data_type_t.TYPE_INT32) {
				values[fm] = tick.get_int(i);
			} else if (type == DataType.data_type_t.TYPE_INT64 || type == DataType.data_type_t.TYPE_UINT32) {
				values[fm] = tick.get_int64(i);
			} else if (type == DataType.data_type_t.TYPE_STRING) {
				values[fm] = tick.get_string(i);
			} else if ((type == DataType.data_type_t.TYPE_FLOAT)) {
				values[fm] = (float) tick.get_double(i);
			} else if ((type == DataType.data_type_t.TYPE_DOUBLE)) {
				values[fm] = tick.get_double(i);
			} else if (type == DataType.data_type_t.TYPE_TIME32) {
				values[fm] = new DateMillis(tick.get_int(i) * 1000);
			} else if (type == DataType.data_type_t.TYPE_TIME_MSEC64) {
				values[fm] = new DateMillis(tick.get_int64(i));
			} else if (type == DataType.data_type_t.TYPE_TIME_NSEC64) {
				values[fm] = new DateNanos(JavaTimeval.timeval_to_datetime_nsec(tick.get_timeval(i)));
			} else {
				values[fm] = tick.get_string(i);
			}
		}
		try {
			currentTable.getRows().addRow(values);
		} catch (Exception e) {
			throw new RuntimeException("Casting error, try adding USE _safe=true", e);
		}
	}
	@Override
	public void process_sorting_order(boolean sorted_by_time_flag) {
	}

	@Override
	public void process_data_quality_change(String symbol_name, DataQualityType data_quality, java.util.Date time) {
	}

	@Override
	public void done() {
	}

	@Override
	public void process_error(int error_code, String error_msg) {
	}
}
