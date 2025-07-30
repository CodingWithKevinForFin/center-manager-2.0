package com.f1.utils.structs.table.derived;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import com.f1.base.CalcFrame;
import com.f1.base.CalcTypes;
import com.f1.base.Caster;
import com.f1.base.NameSpaceCalcFrame;
import com.f1.base.Table;
import com.f1.base.ToStringable;
import com.f1.utils.CH;
import com.f1.utils.LH;
import com.f1.utils.OH;
import com.f1.utils.SH;
import com.f1.utils.concurrent.IdentityHashSet;
import com.f1.utils.sql.TableReturn;
import com.f1.utils.string.ExpressionParserException;
import com.f1.utils.string.Node;
import com.f1.utils.structs.table.stack.BasicCalcFrame;
import com.f1.utils.structs.table.stack.CalcFrameStack;
import com.f1.utils.structs.table.stack.CalcTypesStack;
import com.f1.utils.structs.table.stack.ConcurrentCalcFrameStack;
import com.f1.utils.structs.table.stack.EmptyCalcFrame;
import com.f1.utils.structs.table.stack.MutableCalcFrame;
import com.f1.utils.structs.table.stack.SqlResultset;

public class DerivedHelper {
	private static final Logger log = LH.get();

	public static final DerivedCellCalculator[] EMPTY_ARRAY = new DerivedCellCalculator[0];

	public static DerivedCellCalculator[] toConsts(Object[] o) {
		DerivedCellCalculator[] r = new DerivedCellCalculator[o.length];
		for (int i = 0; i < r.length; i++)
			r[i] = new DerivedCellCalculatorConst(0, o[i]);
		return r;
	}

	static public boolean isNull(DerivedCellCalculator t) {
		return t.isConst() && t.get(null) == null;
	}
	public static <T extends DerivedCellCalculator> T[] copy(T[] params) {
		T[] r = params.clone();
		for (int i = 0; i < r.length; i++)
			r[i] = (T) r[i].copy();
		return r;
	}
	public static <T extends DerivedCellCalculator> T copy(T param) {
		return (T) (param == null ? null : param.copy());
	}
	public static Set<Object> getDependencyIds(DerivedCellCalculator calc, Set<Object> sink) {
		if (calc != null) {
			if (calc instanceof DerivedCellCalculatorWithDependencies)
				((DerivedCellCalculatorWithDependencies) calc).getDependencyIds(sink);
			for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++)
				getDependencyIds(calc.getInnerCalcAt(i), sink);
		}
		return sink;
	}
	public static Set<Object> getDependencyIds(DerivedCellCalculator dcc) {
		Set<Object> r = getDependencyIds2(dcc, null);
		return r == null ? Collections.EMPTY_SET : r;
	}
	public static Set<Object> getDependencyIds2(DerivedCellCalculator calc, Set<Object> sink) {
		if (calc != null) {
			if (calc instanceof DerivedCellCalculatorWithDependencies) {
				if (sink == null)
					sink = new HashSet<Object>();
				((DerivedCellCalculatorWithDependencies) calc).getDependencyIds(sink);
			}
			for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++) {
				sink = getDependencyIds2(calc.getInnerCalcAt(i), sink);
			}
		}
		return sink;
	}

	public static Set<Object> getDependencyIdsInSet(DerivedCellCalculator dcc, Set<? extends Object> set) {
		if (set.isEmpty())
			return Collections.EMPTY_SET;
		Set<Object> dep = getDependencyIds(dcc);
		if (dep.isEmpty())
			return Collections.EMPTY_SET;
		return CH.comm(set, dep, false, false, true);
	}
	public static <T extends DerivedCellCalculator> T findFirst(DerivedCellCalculator calc, Class<T> whatToLookfor) {
		if (whatToLookfor.isInstance(calc))
			return whatToLookfor.cast(calc);
		for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++) {
			T r = findFirst(calc.getInnerCalcAt(i), whatToLookfor);
			if (r != null)
				return r;
		}
		return null;
	}
	public static DerivedCellCalculator findFirstPausable(DerivedCellCalculator calc) {
		return findFirstPausable(calc, null);
	}
	private static DerivedCellCalculator findFirstPausable(DerivedCellCalculator calc, IdentityHashSet<DerivedCellCalculatorMethod> visited) {
		if (calc.isPausable())
			return calc;
		for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++) {
			final DerivedCellCalculator r = findFirstPausable(calc.getInnerCalcAt(i), visited);
			if (r != null)
				return r;
		}
		if (calc instanceof DerivedCellCalculatorMethod) {
			DerivedCellCalculatorMethod dcm = (DerivedCellCalculatorMethod) calc;
			if (visited == null)
				visited = new IdentityHashSet<DerivedCellCalculatorMethod>();
			if (!visited.add(dcm))
				return null;
			DerivedCellCalculator r = findFirstPausable(dcm.getInnerBlock(), visited);
			if (r != null)
				return r;
		}
		return null;
	}
	public static <T extends Node> List<? super T> find(Node node, Class<T> whatToLookfor, List<? super T> sink) {
		if (node == null)
			return sink;
		if (whatToLookfor.isInstance(node)) {
			if (sink == null)
				sink = new ArrayList<T>();
			sink.add(whatToLookfor.cast(node));
		}
		for (int i = 0, l = node.getInnerNodesCount(); i < l; i++)
			sink = find(node.getInnerNode(i), whatToLookfor, sink);
		return sink;
	}
	public static <T extends DerivedCellCalculator> List<T> find(DerivedCellCalculator calc, Class<T> whatToLookfor) {
		return (List<T>) find(calc, whatToLookfor, null);
	}

	public static <T extends DerivedCellCalculator> List<? super T> find(DerivedCellCalculator calc, Class<T> whatToLookfor, List<? super T> sink) {
		if (calc == null)
			return sink;
		if (whatToLookfor.isInstance(calc)) {
			if (sink == null)
				sink = new ArrayList<T>();
			sink.add(whatToLookfor.cast(calc));
		}
		for (int i = 0, l = calc.getInnerCalcsCount(); i < l; i++)
			sink = find(calc.getInnerCalcAt(i), whatToLookfor, sink);
		return sink;
	}
	public static String toString(Object o) {
		if (o instanceof ToDerivedString)
			return ((ToDerivedString) o).toDerivedString();
		if (o == null)
			return null;
		else if (o instanceof CharSequence)
			return o.toString();
		else if (o instanceof ToStringable || o instanceof Map || o instanceof Collection)
			return toString(o, new StringBuilder()).toString();
		if (o instanceof Number) {
			if (o instanceof Integer)
				return SH.toString((int) (Integer) o);
			else if (o instanceof Long)
				return SH.toString((long) (Long) o);
			else if (o instanceof Short)
				return SH.toString((short) (Short) o);
			else if (o instanceof Byte)
				return SH.toString((byte) (Byte) o);
		} else if (o instanceof Character)
			return SH.toString((char) (Character) o);
		else if (o.getClass().isArray())
			return o.getClass().getComponentType().getSimpleName() + "[" + Array.getLength(o) + "]";
		return o.toString();
	}
	public static StringBuilder toString(Object o, StringBuilder sb) {
		if (o == null)
			return sb.append("null");
		if (o instanceof ToDerivedString)
			return ((ToDerivedString) o).toDerivedString(sb);
		if (o instanceof ToStringable)
			return ((ToStringable) o).toString(sb);
		if (o instanceof CharSequence)
			return sb.append((CharSequence) o);
		Class clazz = o.getClass();
		if (o instanceof Number)// these avoid object creation also
		{
			if (clazz == Integer.class) {
				int i = ((Integer) o).intValue();
				if (OH.isBetween(i, -SH.MAX_CACHED_INTS_SIZE, SH.MAX_CACHED_INTS_SIZE))
					sb.append(SH.toString(i));
				else
					sb.append(i);
			} else if (clazz == Long.class) {
				long i = ((Long) o).longValue();
				if (OH.isBetween(i, -SH.MAX_CACHED_INTS_SIZE, SH.MAX_CACHED_INTS_SIZE))
					sb.append(SH.toString((int) i));
				else
					sb.append(i);
			} else if (clazz == Byte.class) {
				byte i = ((Byte) o).byteValue();
				sb.append(SH.toString((int) i));
			} else if (clazz == Short.class) {
				short i = ((Short) o).shortValue();
				if (OH.isBetween(i, -SH.MAX_CACHED_INTS_SIZE, SH.MAX_CACHED_INTS_SIZE))
					sb.append(SH.toString((int) i));
				else
					sb.append(i);
			} else
				sb.append(o);
		} else if (o instanceof Collection) {
			sb.append('[');
			join(',', (Collection<?>) o, sb);
			sb.append(']');
		} else if (o instanceof Map) {
			sb.append('{');
			joinMap(',', '=', (Map<?, ?>) o, sb);
			sb.append('}');
		} else if (clazz == Character.class) {
			return sb.append(((Character) o).charValue());
		} else if (clazz.isArray()) {
			sb.append(clazz.getComponentType().getSimpleName()).append('[').append(Array.getLength(o)).append(']');
		} else
			sb.append(o);
		return sb;
	}
	private static StringBuilder join(char delim, Iterable<?> tokens, StringBuilder r) {
		Iterator<?> i = tokens.iterator();
		if (i.hasNext()) {
			toString(i.next(), r);
			while (i.hasNext())
				toString(i.next(), r.append(delim));
		}
		return r;
	}
	public static StringBuilder joinMap(char delim, char associator, Map<?, ?> map, StringBuilder sb) {
		boolean first = true;
		for (Map.Entry<?, ?> e : map.entrySet()) {
			if (first) {
				first = false;
			} else
				sb.append(delim);
			toString(e.getValue(), toString(e.getKey(), sb).append(associator));
		}
		return sb;
	}
	public static FlowControlPause onFlowControl(FlowControlPause l, DerivedCellCalculator thiz, CalcFrameStack sf, int i, Object attachment) {
		for (CalcFrameStack t = sf; t != null && t.isParentVisible(); t = t.getParent())
			if (t instanceof ConcurrentCalcFrameStack) {
				((ConcurrentCalcFrameStack) t).addPause(l);
				return null;
			}
		l.push(thiz, sf, i, attachment);
		return l;
	}
	public static DerivedCellCalculator reduceConst(DerivedCellCalculator param) {
		if (param.isConst() && !(param instanceof DerivedCellCalculatorConst) && !(param instanceof DerivedCellCalculatorFlowStatement)) {
			try {
				return new DerivedCellCalculatorConst(param.getPosition(), param.get(null), param.getReturnType());
			} catch (FlowControlThrow e) {
				LH.warning(log, "Should not have gotten here: ", param, " threw: " + e);
			}
		}
		return param;
	}
	public static <T> Caster<T> getReturnType(DerivedCellCalculator[] params, Class<T> mustExtend) {
		return getReturnType(params, 0, params.length, mustExtend);
	}
	public static <T> Caster<T> getReturnType(DerivedCellCalculator[] params, int start, int end, Class<T> mustExtend) {
		Class r = null;
		while (start < end) {
			DerivedCellCalculator p = params[start++];
			if (p.getReturnType() == Object.class && isNull(p))
				continue;
			if (!mustExtend.isAssignableFrom(p.getReturnType()))
				throw new ExpressionParserException(p.getPosition(), "Invalid Type, Expecting: " + mustExtend.getSimpleName());
			r = OH.getWidestIgnoreNull(r, p.getReturnType());
		}
		return OH.getCaster(r == null ? mustExtend : r);
	}
	//	public static DerivedCellTimeoutController toDerivedTimeoutController(TimeoutController timeout) {
	//		return timeout instanceof DerivedCellTimeoutController ? (DerivedCellTimeoutController) timeout
	//				: new DerivedCellTimeoutController(timeout.getTimeoutMillisRemainingOrZero());
	//	}
	public static FlowControlThrow onThrowable(DerivedCellCalculator dcc, Throwable e) {
		if (e instanceof FlowControlThrow) {
			return (FlowControlThrow) e;
		} else if (e instanceof ExpressionParserException) {
			return new FlowControlThrow(dcc, "Parse Error: " + e.getMessage(), e);
		} else if (SH.is(e.getMessage()))
			return new FlowControlThrow(dcc, "Internal Error: " + e.getMessage(), e);
		else
			return new FlowControlThrow(dcc, "Internal Error: " + e.getClass().getSimpleName(), e);
	}
	public static Object getForFlowControl(Object r) {
		return r == DerivedCellCalculatorFlowControl.NULL || r == DerivedCellCalculatorFlowControl.VOID ? null : r;
	}
	public static Object getFlowControl(DerivedCellCalculator dcc, CalcFrameStack sf, PauseStack paused) {
		try {
			if (paused != null)
				return paused.resumeFlowControl();
			else if (dcc instanceof DerivedCellCalculatorFlowControl)
				return ((DerivedCellCalculatorFlowControl) dcc).getFlowControl(sf);
			Object r = dcc.get(sf);
			if (r instanceof TableReturn) {
				SqlResultset rs = sf.getSqlResultset();
				if (rs != null)
					rs.appendTable((TableReturn) r);
				return null;
			}
			return r instanceof FlowControl ? r : null;
		} catch (Throwable e) {
			throw DerivedHelper.onThrowable(dcc, e);
		}
	}
	public static boolean childrenAreSame(DerivedCellCalculator l, DerivedCellCalculator r) {
		if (l == null || r == null)
			return l == r;
		int n = l.getInnerCalcsCount();
		if (n != r.getInnerCalcsCount())
			return false;
		for (int i = 0; i < n; i++) {
			DerivedCellCalculator t1 = l.getInnerCalcAt(i);
			DerivedCellCalculator t2 = r.getInnerCalcAt(i);
			if (t1 != t2)
				if (t1 == null || t2 == null || !t1.isSame(t2))
					return false;
		}
		return true;

	}
	static public boolean areSame(Map<String, DerivedCellCalculator> l, Map<String, DerivedCellCalculator> r) {
		if (l.size() != r.size())
			return false;
		for (String s : l.keySet())
			if (!areSame(l.get(s), r.get(s)))
				return false;
		return true;
	}
	static public boolean areSame(DerivedCellCalculator[][] l, DerivedCellCalculator[][] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		int n = l.length;
		if (r.length != n)
			return false;
		for (int i = 0; i < n; i++)
			return areSame(l[i], r[i]);
		return false;
	}
	static public boolean areSame(DerivedCellCalculator[] l, DerivedCellCalculator[] r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		int n = l.length;
		if (r.length != n)
			return false;
		for (int i = 0; i < n; i++)
			return areSame(l[i], r[i]);
		return false;
	}

	static public boolean areSame(DerivedCellCalculator l, DerivedCellCalculator r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		return l.isSame(r);
	}
	public static boolean areSame(Node l, Node r) {
		if (l == r)
			return true;
		if (l == null || r == null)
			return false;
		return OH.eq(l.toString(), r.toString());//not ideal!
	}

	public static MutableCalcFrame toFrame(Map<?, ?> m) {
		final MutableCalcFrame r = new MutableCalcFrame();
		for (Map.Entry<?, ?> i : m.entrySet()) {
			String name = i.getKey().toString();
			r.putTypeValue(name, i.getValue() == null ? Object.class : i.getValue().getClass(), i.getValue());
		}
		return r;
	}

	public static CalcFrame toFrame(Map<?, ?> m, CalcTypes types) {
		final BasicCalcFrame r = new BasicCalcFrame(types);
		for (Map.Entry<?, ?> i : m.entrySet()) {
			String name = i.getKey().toString();
			if (types.getType(name) != null)
				r.putValue(name, i.getValue());
		}
		return r;
	}

	public static CalcFrame toFrame(Map<String, Object> vals, Map<String, Class> types) {
		if (types == null || types.isEmpty())
			return EmptyCalcFrame.INSTANCE;
		final MutableCalcFrame r = new MutableCalcFrame();
		for (Map.Entry<String, Class> i : types.entrySet()) {
			String name = i.getKey().toString();
			r.putTypeValue(i.getKey(), i.getValue(), vals.get(name));
		}
		return r;
	}

	public static boolean areSame(CalcTypes l, CalcTypes r) {
		if (l == r)
			return true;
		if (l == null || r == null || l.getVarsCount() != r.getVarsCount())
			return false;
		for (String s : l.getVarKeys())
			if (l.getType(s) != r.getType(s))
				return false;
		return true;

	}

	public static Class<?> getType(CalcTypesStack c, String varname) {
		for (;;) {
			Class<?> r = c.getFrame().getType(varname);
			if (r != null)
				return r;
			r = c.getFrameConsts().getType(varname);
			if (r != null)
				return r;
			if (!c.isParentVisible())
				break;
			CalcTypesStack c2 = c.getParent();
			if (c2 == null)
				break;
			c = c2;
		}
		Class<?> r = c.getGlobal().getType(varname);
		if (r != null)
			return r;
		return c.getGlobalConsts().getType(varname);
	}

	public static Object getValue(CalcFrameStack c, String varname) {
		CalcFrameStack n = c;
		for (;;) {
			if (c.getFrame().getType(varname) != null)
				return c.getFrame().getValue(varname);
			if (c.getFrameConsts().getType(varname) != null)
				return c.getFrameConsts().getValue(varname);
			if (!c.isParentVisible())
				break;
			CalcFrameStack c2 = c.getParent();
			if (c2 == null)
				break;
			c = c2;
		}
		if (c.getGlobal().getType(varname) != null)
			return c.getGlobal().getValue(varname);
		if (c.getGlobalConsts().getType(varname) != null)
			return c.getGlobalConsts().getValue(varname);
		if (varname.startsWith("$"))
			return null;
		throw new RuntimeException("Unknown variable: " + varname);
	}

	public static Object putValue(CalcFrameStack c, String varname, Object value) {
		for (;;) {
			if (c.getFrame().getType(varname) != null)
				return c.getFrame().putValue(varname, value);
			if (c.getFrameConsts().getType(varname) != null)
				throw new IllegalStateException("Can not assign value to const: " + varname);
			if (!c.isParentVisible())
				break;
			CalcFrameStack c2 = c.getParent();
			if (c2 == null)
				break;
			c = c2;
		}
		if (c.getGlobal().getType(varname) != null)
			return c.getGlobal().putValue(varname, value);
		if (c.getGlobalConsts().getType(varname) != null)
			throw new IllegalStateException("Can not assign value to const: " + varname);
		throw new RuntimeException("Unknown variable: " + varname);
	}

	public static void toStringNoRecurse(CalcFrameStack stackFrame, StringBuilder sink) {
		if (stackFrame.getStackSize() < 10)
			sink.append('0');
		sink.append(stackFrame.getStackSize()).append(": ");
		sink.append(stackFrame.getClass().getSimpleName());
		if (!stackFrame.getFrame().isVarsEmpty())
			sink.append(" ").append(stackFrame.getFrame());
		if (!stackFrame.getFrameConsts().isVarsEmpty())
			sink.append(" cns=").append(stackFrame.getFrameConsts());
		CalcFrameStack parent = stackFrame.getParent();
		if (parent == null || parent.getGlobal() != stackFrame.getGlobal())
			sink.append(" glv=").append(stackFrame.getGlobal());
		if (parent == null || parent.getGlobalConsts() != stackFrame.getGlobalConsts())
			sink.append(" glc=").append(stackFrame.getGlobalConsts());
		if (parent == null || parent.getBreakPointManager() != stackFrame.getBreakPointManager())
			sink.append(" bpm=").append(OH.getSimpleClassName(stackFrame.getBreakPointManager()));
		if (parent == null || parent.getFactory() != stackFrame.getFactory())
			sink.append(" mtf=").append(OH.getSimpleClassName(stackFrame.getFactory()));
		if (parent == null || parent.getLimit() != stackFrame.getLimit())
			sink.append(" lmt=").append(stackFrame.getLimit());
		if (parent == null || parent.getSqlPlanListener() != stackFrame.getSqlPlanListener())
			sink.append(" qpl=").append(OH.getSimpleClassName(stackFrame.getSqlPlanListener()));
		if (parent == null || parent.getTableset() != stackFrame.getTableset())
			sink.append(" tbs=").append(SH.ddd(stackFrame.getTableset().getTableNames().toString(), 30));
		if (parent == null || parent.getTimeoutController() != stackFrame.getTimeoutController())
			sink.append(" tc=").append(stackFrame.getTimeoutController() == null ? -1 : stackFrame.getTimeoutController().getTimeoutMillis());
		if (!stackFrame.isParentVisible() && parent != null)
			sink.append(SH.NEWLINE + "----");
	}

	public static CalcFrame toFrame(CalcFrameStack sf) {
		MutableCalcFrame r = new MutableCalcFrame();
		for (;;) {
			putAllIfAbsent(sf.getFrame(), r);
			putAllIfAbsent(sf.getFrameConsts(), r);
			if (sf.getParent() == null) {
				putAllIfAbsent(sf.getGlobal(), r);
				putAllIfAbsent(sf.getGlobalConsts(), r);
				return r;
			} else
				sf = sf.getParent();
		}
	}
	public static void putAllIfAbsent(CalcFrame source, MutableCalcFrame sink) {
		for (String i : source.getVarKeys())
			if (sink.getType(i) == null)
				sink.putTypeValue(i, source.getType(i), source.getValue(i));
	}

	public static Table toTableOrThrow(int position, FlowControl o) {
		if (o instanceof TableReturn) {
			List<Table> tables = ((TableReturn) o).getTables();
			if (tables.size() == 1)
				return tables.get(0);
			throw new ExpressionParserException(position, "Expecting 1 table");
		} else if (o == null)
			throw new ExpressionParserException(position, "null");
		else
			throw new ExpressionParserException(position, "Unexpected type: " + o.getClass().getSimpleName());
	}

	public static DerivedCellCalculator[] replaceVarsWithConsts(DerivedCellCalculator[] calcs, CalcFrameStack vars, CalcTypes toSkip) {
		if (calcs != null)
			for (int i = 0; i < calcs.length; i++)
				replaceVarsWithConsts(calcs[i], vars, toSkip);
		return calcs;
	}
	public static DerivedCellCalculator replaceVarsWithConsts(DerivedCellCalculator calc, CalcFrameStack vars, CalcTypes toSkip) {
		if (calc != null && vars != null) {
			List<DerivedCellCalculatorRefGlobal> refs = DerivedHelper.find(calc, DerivedCellCalculatorRefGlobal.class);
			if (CH.isntEmpty(refs))
				for (DerivedCellCalculatorRefGlobal i : refs) {
					if (i.getId() instanceof String) {
						String id = (String) i.getId();
						if (toSkip.getType(id) == null && getType(vars, id) != null)
							i.setConst(OH.cast(getValue(vars, id), i.getReturnType(), false, false));
					}
				}
			List<DerivedCellCalculatorRef> refs2 = DerivedHelper.find(calc, DerivedCellCalculatorRef.class);
			if (CH.isntEmpty(refs2))
				for (DerivedCellCalculatorRef i : refs2) {
					if (i.getId() instanceof String) {
						String id = (String) i.getId();
						if (toSkip.getType(id) == null && getType(vars, id) != null)
							i.setConst(OH.cast(getValue(vars, id), i.getReturnType(), false, false));
					}
				}
		}
		return calc;
	}

	public static NameSpaceCalcFrame getNameSpaceCalcFrame(CalcFrameStack key) {
		for (; key != null; key = key.getParent())
			if (key.getFrame() instanceof NameSpaceCalcFrame)
				return (NameSpaceCalcFrame) key.getFrame();
		return null;
	}
}
