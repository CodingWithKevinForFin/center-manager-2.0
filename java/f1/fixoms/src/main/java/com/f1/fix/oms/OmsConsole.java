package com.f1.fix.oms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.f1.base.Column;
import com.f1.base.Table;
import com.f1.base.Valued;
import com.f1.base.ValuedParam;
import com.f1.base.ValuedSchema;
import com.f1.container.Container;
import com.f1.container.Partition;
import com.f1.container.PartitionController;
import com.f1.container.State;
import com.f1.fix.oms.adapter.OmsOrderState;
import com.f1.fix.oms.schema.OmsOrder;
import com.f1.pofo.oms.Execution;
import com.f1.pofo.oms.Order;
import com.f1.utils.CH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.TableHelper;
import com.f1.utils.VH;
import com.f1.utils.sql.SqlProcessor;
import com.f1.utils.sql.Tableset;
import com.f1.utils.sql.TablesetImpl;
import com.f1.utils.structs.table.BasicColumn;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.TopCalcFrameStack;

public class OmsConsole {

	private Container container;
	private Column[] orderColumns;
	private Column[] execColumns;
	private ValuedSchema<Valued> execSchema;
	private ValuedSchema<Valued> orderSchema;

	public OmsConsole(Container container) {
		this.container = container;
		orderSchema = container.nw(Order.class).askSchema();
		execSchema = container.nw(Execution.class).askSchema();
		{
			List<Column> orderColumns = new ArrayList<Column>(orderSchema.askParamsCount());
			for (ValuedParam vp : orderSchema.askValuedParams())
				orderColumns.add(new BasicColumn(OH.getBoxed(vp.getReturnType()), vp.getName()));
			this.orderColumns = orderColumns.toArray(new Column[orderColumns.size()]);
		}
		{
			List<Column> execColumns = new ArrayList<Column>(execSchema.askParamsCount());
			for (ValuedParam vp : execSchema.askValuedParams())
				execColumns.add(new BasicColumn(OH.getBoxed(vp.getReturnType()), vp.getName()));
			this.execColumns = execColumns.toArray(new Column[execColumns.size()]);
		}
	}

	public Table query(String sqlExpression) {

		List<Order> ordersTable = new ArrayList<Order>();
		List<Execution> executionsTable = new ArrayList<Execution>();
		final PartitionController pc = container.getPartitionController();
		for (Object pid : pc.getPartitions()) {
			final OmsOrderState state = (OmsOrderState) pc.getState(pid, OmsOrderState.class);
			if (state != null) {
				Partition p = state.getPartition();
				if (p.lockForRead(100, TimeUnit.MILLISECONDS)) {
					try {
						for (String id : state.getRequestIds()) {
							OmsOrder omsOrder = state.getOrder(id);
							Order order = omsOrder.getFixOrder();
							if (order != null) {
								order = order.clone();// clone so we can inspect in another thread
								order.setPassThruTags(null);
								ordersTable.add(order.clone());
							}
							if (omsOrder.getExecutions() != null) {
								for (Execution execution : omsOrder.getExecutions().values()) {
									execution = execution.clone();// clone so we can inspect in another thread
									execution.setPassThruTags(null);
									executionsTable.add(execution);
								}
							}
						}
					} finally {
						p.unlockForRead();
					}
				} else {

				}
			}
		}
		SqlProcessor sp = new SqlProcessor();
		Tableset tablesMap = new TablesetImpl();
		tablesMap.putTable("orders", TableHelper.toTable(ordersTable));
		tablesMap.putTable("executions", TableHelper.toTable(executionsTable));
		return sp.process(sqlExpression, new TopCalcFrameStack(tablesMap, EmptyCalcFrame.INSTANCE));
	}

	public String verify(String id) {
		StringBuilder sb = new StringBuilder();
		final PartitionController pc = container.getPartitionController();
		for (Object pid : CH.sort(pc.getPartitions(), SH.COMPARATOR)) {
			if (!SH.m(id).matches(pid.toString()))
				continue;
			State state = pc.getState(pid, OmsOrderState.class);
			if (state == null)
				continue;
			Partition p = state.getPartition();
			if (p.lockForRead(100, TimeUnit.MILLISECONDS)) {
				try {
					sb.append(pid).append(": ");
					if (state != null) {
						String s = state.getPersistedRoot().toString();
						int i = 0;
						for (char c : s.toCharArray())
							i += c;
						sb.append(i + "-" + s.length());
					} else
						sb.append("null");
					sb.append(SH.NEWLINE);
				} finally {
					p.unlockForRead();
				}
			}
		}
		return sb.toString();
	}

	public String verify2(String id) {
		StringBuilder sb = new StringBuilder();
		final PartitionController pc = container.getPartitionController();
		for (Object pid : CH.sort(pc.getPartitions(), SH.COMPARATOR)) {
			if (!SH.m(id).matches(pid.toString()))
				continue;
			State state = pc.getState(pid, OmsOrderState.class);
			if (state == null)
				continue;
			Partition p = state.getPartition();
			if (p.lockForRead(100, TimeUnit.MILLISECONDS)) {
				try {
					sb.append(pid).append(": ");
					if (state != null) {
						sb.append(state.getPersistedRoot());
					} else
						sb.append("null");
					sb.append(SH.NEWLINE);
				} finally {
					p.unlockForRead();
				}
			}
		}
		return sb.toString();
	}

	public String verify3(String id) {
		StringBuilder sb = new StringBuilder();
		final PartitionController pc = container.getPartitionController();
		for (Object pid : CH.sort(pc.getPartitions(), SH.COMPARATOR)) {
			if (!SH.m(id).matches(pid.toString()))
				continue;
			State state = pc.getState(pid, OmsOrderState.class);
			if (state == null)
				continue;
			Partition p = state.getPartition();
			if (p.lockForRead(100, TimeUnit.MILLISECONDS)) {
				try {
					sb.append(pid).append(": " + SH.NEWLINE);
					if (state != null) {
						sb.append(VH.toLegibleString(state.getPersistedRoot(), 1024 * 1024));
					} else
						sb.append("null");
					sb.append(SH.NEWLINE);
				} finally {
					p.unlockForRead();
				}
			}
		}
		return sb.toString();
	}
}
